package redempt.crunch;

import redempt.crunch.data.CharTree;
import redempt.crunch.data.FastNumberParsing;
import redempt.crunch.data.Pair;
import redempt.crunch.data.TokenList;
import redempt.crunch.data.TokenList.Node;
import redempt.crunch.exceptions.ExpressionCompilationException;
import redempt.crunch.functional.ArgumentList;
import redempt.crunch.functional.ExpressionEnv;
import redempt.crunch.functional.Function;
import redempt.crunch.functional.FunctionCall;
import redempt.crunch.token.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

class ExpressionCompiler {
	
	private static final char VAR_CHAR = '$';
	
	static CompiledExpression compile(String expression, ExpressionEnv env) {
		if (expression == null || env == null) {
			throw new ExpressionCompilationException(null, "Expression and environment may not be null");
		}
		CompiledExpression exp = new CompiledExpression();
		Value val = compileValue(expression, exp, env, 0, false).getFirst();
		exp.setValue(val);
		return exp;
	}
	
	private static Value parseExpression(Parser parser, ExpressionEnv env) {
		List<Token> tokens = new ArrayList<>();
		
		return null;
	}

	private static Value parseOptionalNestedExpression(Parser parser, ExpressionEnv env) {
		if (parser.peek() != '(') {
			return null;
		}
		parser.advanceCursor();
		Value expression = parseExpression(parser, env);
		parser.expectChar(')');
		return expression;
	}

	private static Value parseTerm(Parser parser, ExpressionEnv env) {
		Value nested = parseOptionalNestedExpression(parser, env);
		if (nested != null) {
			return nested;
		}
		Token token = env.getNamedTokens().getWith(parser);
		if (token instanceof Value) {
			return (Value) token;
		}
		return parseLeadingOperation(parser, env, token);
	}

	private static Value parseLeadingOperation(Parser parser, ExpressionEnv env, Token operation) {
		switch (operation.getType()) {
			case UNARY_OPERATOR:
				return new UnaryOperation((UnaryOperator) operation, parseTerm(parser, env));
			case FUNCTION:
				Function function = (Function) operation;
				ArgumentList args = parseArgumentList(parser, env, function.getArgCount());
				return new FunctionCall(function, args.getArguments());
		}
		throw new ExpressionCompilationException(parser, "Expected leading operation");
	}

	private static ArgumentList parseArgumentList(Parser parser, ExpressionEnv env, int args) {
        parser.expectChar('(');
        parser.whitespace();
        Value[] values = new Value[args];
        if (args == 0) {
            parser.expectChar(')');
            return new ArgumentList(new Value[0]);
        }
        values[0] = parseExpression(parser, env);
        parser.whitespace();
        for (int i = 1; i < args; i++) {
			parser.expectChar(',');
			values[i] = parseExpression(parser, env);
			parser.whitespace();
		}

        parser.expectChar(')');
        return new ArgumentList(values);
    }

	private static Pair<Value, Integer> compileValue(String expression, CompiledExpression exp, ExpressionEnv env, int begin, boolean parenthetical) {
		CharTree<Token> namedTokens = env.getNamedTokens();
		TokenList tokens = new TokenList();
		Pair<Token, Integer> firstOp = namedTokens.getFrom(expression, begin);
		boolean op = firstOp.getFirst() != null && firstOp.getFirst().getType() == TokenType.BINARY_OPERATOR;
		boolean closed = false;
		int tokenStart = begin;
		char[] chars = expression.toCharArray();
		int i;
		loop:
		for (i = begin; i < expression.length(); i++) {
			char c = chars[i];
			switch (c) {
				case '(':
					if (tokens.size() > 0 && tokens.tail().token.getType() == TokenType.FUNCTION) {
						Pair<ArgumentList, Integer> args = compileArgumentList(expression, exp, env, i + 1);
						tokens.add(args.getFirst());
						i += args.getSecond();
						tokenStart = i;
						op = true;
						continue;
					}
					if (!op && tokenStart != i) {
						tokens.add(compileToken(expression, tokenStart, i, exp));
					}
					if (tokens.tail() != null && tokens.tail().token instanceof Value) {
						tokens.add(BinaryOperator.MULTIPLY);
					}
					Pair<Value, Integer> inner = compileValue(expression, exp, env, i + 1, true);
					i += inner.getSecond() + 1;
					tokens.add(inner.getFirst());
					tokenStart = i;
					op = true;
					continue;
				case ' ':
					if (!op && tokenStart != i) {
						tokens.add(compileToken(expression, tokenStart, i, exp));
						tokenStart = i + 1;
					} else {
						tokenStart++;
					}
					continue;
				case ')':
				case ',':
					if (!parenthetical) {
						throw new ExpressionCompilationException("Unbalanced parenthesis");
					}
					closed = true;
					break loop;
			}
			Pair<Token, Integer> namedToken = namedTokens.getFrom(expression, i);
			if (namedToken.getFirst() != null) {
				Token token = namedToken.getFirst();
				if (token.getType() == TokenType.VARIABLE) {
					Variable var = ((Variable) token).getClone();
					var.expression = exp;
					token = var;
				}
				if (!op && tokenStart != i) {
					tokens.add(compileToken(expression, tokenStart, i, exp));
				}
				if (!(token.getType() == TokenType.BINARY_OPERATOR && !((BinaryOperator) token).isUnary())
						&& tokens.tail() != null && tokens.tail().token instanceof Value) {
					tokens.add(BinaryOperator.MULTIPLY);
				}
				if (token == BinaryOperator.SUBTRACT && (tokens.size() == 0 || !(tokens.tail().token instanceof Value))) {
					token = BinaryOperator.NEGATE;
				}
				op = token.getType() == TokenType.BINARY_OPERATOR;
				i += namedToken.getSecond() - 1;
				tokenStart = i + 1;
				tokens.add(token);
				continue;
			}
			op = false;
		}
		if (parenthetical && !closed) {
			throw new ExpressionCompilationException("Unbalanced parenthesis");
		}
		if (tokenStart < i && i <= expression.length() && !op) {
			tokens.add(compileToken(expression, tokenStart, i, exp));
		}
		return new Pair<>(reduceTokens(tokens), i - begin);
	}
	
	private static Pair<ArgumentList, Integer> compileArgumentList(String expression, CompiledExpression exp, ExpressionEnv env, int start) {
		List<Value> values = new ArrayList<>();
		int i = start;
		loop:
		while (i < expression.length() && expression.charAt(i) != ')') {
			Pair<Value, Integer> result = compileValue(expression, exp, env, i, true);
			i += result.getSecond() + 1;
			values.add(result.getFirst());
			switch (expression.charAt(i - 1)) {
				case ')':
					break loop;
				case ',':
					break;
				default:
					throw new ExpressionCompilationException("Function argument lists must be separated by commas");
			}
		}
		if (values.size() == 0) {
			i++;
		}
		if (expression.charAt(i - 1) != ')') {
			throw new ExpressionCompilationException("Unbalanced parenthesis");
		}
		Value[] valueArray = values.toArray(new Value[values.size()]);
		return new Pair<>(new ArgumentList(valueArray), i - start);
	}
	
	private static class OperatorList extends ArrayList<Node> {}
	
	private static Value reduceTokens(TokenList tokens) {
		OperatorList[] priorities = new OperatorList[11];
		for (Node node = tokens.head(); node != null; node = node.next) {
			Token token = node.token;
			if (token.getType() == TokenType.FUNCTION) {
				createFunctionCall(node);
				continue;
			}
			if (token.getType() == TokenType.BINARY_OPERATOR) {
				BinaryOperator op = (BinaryOperator) token;
				OperatorList ops = priorities[op.getPriority()];
				if (ops == null) {
					ops = new OperatorList();
					priorities[op.getPriority()] = ops;
				}
				ops.add(node);
			}
		}
		for (int i = priorities.length - 1; i >= 0; i--) {
			OperatorList list = priorities[i];
			if (list == null) {
				continue;
			}
			list.forEach(ExpressionCompiler::createOperation);
		}
		Token token = tokens.head().token;
		if (!(token instanceof Value)) {
			throw new ExpressionCompilationException("Token is not a value: " + token.toString());
		}
		if (tokens.size() > 1) {
			StringJoiner joiner = new StringJoiner(", ");
			tokens.forEach(t -> joiner.add(t.toString()));
			throw new ExpressionCompilationException("Adjacent values have no operators between them: " + joiner.toString());
		}
		return (Value) tokens.head().token;
	}
	
	private static void createFunctionCall(Node node) {
		if (node.next == null) {
			throw new ExpressionCompilationException("Function must be followed by argument list");
		}
		Token next = node.next.token;
		if (next.getType() != TokenType.ARGUMENT_LIST) {
			throw new ExpressionCompilationException("Function must be followed by argument list");
		}
		Function func = (Function) node.token;
		ArgumentList list = (ArgumentList) next;
		if (list.getArguments().length != func.getArgCount()) {
			throw new ExpressionCompilationException("Function '" + func.getName() + "' takes " + func.getArgCount() + " args, but got " + list.getArguments().length);
		}
		node.removeAfter();
		node.token = new FunctionCall(func, list.getArguments());
	}
	
	private static void createOperation(Node node) {
		BinaryOperator op = (BinaryOperator) node.token;
		if (node.next == null) {
			throw new ExpressionCompilationException("Operator " + op + " has no following operand");
		}
		if (op.isUnary()) {
			Token next = node.next.token;
			node.removeAfter();
			if (next.getType() == TokenType.BINARY_OPERATOR) {
				throw new ExpressionCompilationException("Adjacent operators have no values to operate on");
			}
			if (next.getType() == TokenType.LITERAL_VALUE && op.canInline()) {
				Value literal = (Value) next;
				node.token = new LiteralValue(op.operate(literal.getValue()));
				return;
			}
			node.token = new BinaryOperation(op, (Value) next);
			return;
		}
		if (node.prev == null) {
			throw new ExpressionCompilationException("Operator " + op + " has no leading operand");
		}
		Token next = node.next.token;
		node.removeAfter();
		Token prev = node.prev.token;
		node.removeBefore();
		if (prev.getType() == TokenType.BINARY_OPERATOR || next.getType() == TokenType.BINARY_OPERATOR) {
			throw new ExpressionCompilationException("Adjacent operators have no values to operate on");
		}
		if (prev.getType() == TokenType.LITERAL_VALUE && next.getType() == TokenType.LITERAL_VALUE && op.canInline()) {
			Value lit1 = (Value) prev;
			Value lit2 = (Value) next;
			node.token = new LiteralValue(op.operate(lit1.getValue(), lit2.getValue()));
			return;
		}
		node.token = new BinaryOperation(op, (Value) prev, (Value) next);
	}
	
	private static Token compileToken(String str, int start, int end, CompiledExpression exp) {
		if (str.charAt(start) == VAR_CHAR) {
			return new Variable(exp, FastNumberParsing.parseInt(str, start + 1, end) - 1);
		}
		return new LiteralValue(FastNumberParsing.parseDouble(str, start, end));
	}
	
}
