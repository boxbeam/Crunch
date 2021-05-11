package redempt.crunch;

import redempt.crunch.TokenList.Node;
import redempt.crunch.exceptions.ExpressionCompilationException;
import redempt.crunch.functional.ArgumentList;
import redempt.crunch.functional.EvaluationEnvironment;
import redempt.crunch.functional.Function;
import redempt.crunch.functional.FunctionCall;
import redempt.crunch.token.LiteralValue;
import redempt.crunch.token.Operation;
import redempt.crunch.token.Operator;
import redempt.crunch.token.Token;
import redempt.crunch.token.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

class ExpressionCompiler {
	
	private static final char VAR_CHAR = '$';
	
	static CompiledExpression compile(String expression, EvaluationEnvironment env) {
		if (expression == null || env == null) {
			throw new ExpressionCompilationException("Expression and environment may not be null");
		}
		CompiledExpression exp = new CompiledExpression();
		Value val = compileValue(expression, exp, env, 0, false).getFirst();
		exp.setValue(val);
		return exp;
	}
	
	private static Pair<Value, Integer> compileValue(String expression, CompiledExpression exp, EvaluationEnvironment env, int begin, boolean parenthetical) {
		CharTree<Token> namedTokens = env.getNamedTokens();
		TokenList tokens = new TokenList();
		Pair<Token, Integer> firstOp = namedTokens.getFrom(expression, begin);
		boolean op = firstOp.getFirst() != null && firstOp.getFirst().getType() == TokenType.OPERATOR;
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
				if (token == Operator.SUBTRACT && (tokens.size() == 0 || !(tokens.tail().token instanceof Value))) {
					token = Operator.NEGATE;
				}
				op = token.getType() == TokenType.OPERATOR;
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
	
	private static Pair<ArgumentList, Integer> compileArgumentList(String expression, CompiledExpression exp, EvaluationEnvironment env, int start) {
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
				if (node.next == null) {
					throw new ExpressionCompilationException("Function must be followed by argument list");
				}
				Token next = node.next.token;
				if (next.getType() != TokenType.ARGUMENT_LIST) {
					throw new ExpressionCompilationException("Function must be followed by argument list");
				}
				Function func = (Function) token;
				ArgumentList list = (ArgumentList) next;
				if (list.getArguments().length != func.getArgCount()) {
					throw new ExpressionCompilationException("Function '" + func.getName() + "' takes " + func.getArgCount() + " args, but got " + list.getArguments().length);
				}
				node.removeAfter();
				node.token = new FunctionCall(func, list.getArguments());
				continue;
			}
			if (token.getType() == TokenType.OPERATOR) {
				Operator op = (Operator) token;
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
			throw new ExpressionCompilationException("Adjacent values have no operators between them");
		}
		return (Value) tokens.head().token;
	}
	
	private static void createOperation(Node node) {
		Operator op = (Operator) node.token;
		if (node.next == null) {
			throw new ExpressionCompilationException("Operator " + op + " has no following operand");
		}
		if (op.isUnary()) {
			Token next = node.next.token;
			node.removeAfter();
			if (next.getType() == TokenType.OPERATOR) {
				throw new ExpressionCompilationException("Adjacent operators have no values to operate on");
			}
			if (next.getType() == TokenType.LITERAL_VALUE) {
				Value literal = (Value) next;
				node.token = new LiteralValue(op.operate(literal.getValue()));
				return;
			}
			node.token = new Operation(op, (Value) next);
			return;
		}
		if (node.prev == null) {
			throw new ExpressionCompilationException("Operator " + op + " has no leading operand");
		}
		Token next = node.next.token;
		node.removeAfter();
		Token prev = node.prev.token;
		node.removeBefore();
		if (prev.getType() == TokenType.OPERATOR || next.getType() == TokenType.OPERATOR) {
			throw new ExpressionCompilationException("Adjacent operators have no values to operate on");
		}
		if (prev.getType() == TokenType.LITERAL_VALUE && next.getType() == TokenType.LITERAL_VALUE) {
			Value lit1 = (Value) prev;
			Value lit2 = (Value) next;
			node.token = new LiteralValue(op.operate(lit1.getValue(), lit2.getValue()));
			return;
		}
		node.token = new Operation(op, (Value) prev, (Value) next);
	}
	
	private static Token compileToken(String str, int start, int end, CompiledExpression exp) {
		if (str.charAt(start) == VAR_CHAR) {
			return new Variable(exp, parseInt(str, start + 1, end) - 1);
		}
		return new LiteralValue(parseDouble(str, start, end));
	}
	
	private static int parseInt(String input, int start, int end) {
		int i = start;
		boolean negative = false;
		if (input.charAt(i) == '-') {
			negative = true;
			i++;
		}
		int output = 0;
		for (; i < end; i++) {
			char c = input.charAt(i);
			if (c > '9' || c < '0') {
				throw new NumberFormatException("Non-numeric character in input '" + input + "'");
			}
			output *= 10;
			output += c - '0';
		}
		return negative ? -output: output;
	}
	
	private static double parseDouble(String input, int start, int end) {
		int i = start;
		boolean negative = false;
		if (input.charAt(start) == '-') {
			negative = true;
			i++;
		}
		double output = 0;
		double after = 0;
		int decimal = -1;
		for (; i < end; i++) {
			char c = input.charAt(i);
			if (c == '.') {
				if (decimal != -1) {
					throw new NumberFormatException("Second period in double for input '" + input + "'");
				}
				decimal = i;
				continue;
			}
			if (c > '9' || c < '0') {
				throw new NumberFormatException("Non-numeric character in input '" + input + "'");
			}
			if (decimal != -1) {
				after *= 10;
				after += c - '0';
			} else {
				output *= 10;
				output += c - '0';
			}
		}
		after /= Math.pow(10, end - decimal - 1);
		return negative ? -output - after: output + after;
	}
	
}
