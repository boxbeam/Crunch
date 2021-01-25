package redempt.crunch;

import redempt.crunch.exceptions.ExpressionCompilationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpressionCompiler {
	
	private static CharTree opMap = new CharTree();
	private static final char VAR_CHAR = '$';
	
	static {
		for (Operator operator : Operator.values()) {
			opMap.set(operator.getSymbol(), operator);
		}
	}
	
	static CompiledExpression compile(String expression) {
		CompiledExpression exp = new CompiledExpression();
		expression = expression.replace(" ", "");
		Value val = compileValue(expression, exp, 0, expression.length());
		exp.setValue(val);
		return exp;
	}
	
	private static Value compileValue(String expression, CompiledExpression exp, int begin, int end) {
		List<Token> tokens = new ArrayList<>();
		int depth = 0;
		boolean op = opMap.containsFirstChar(expression.charAt(begin));
		int tokenStart = begin;
		char[] chars = expression.toCharArray();
		for (int i = begin; i < end; i++) {
			char c = chars[i];
			switch (c) {
				case '(':
					depth++;
					if (depth != 1) {
						continue;
					}
					if (!op && tokenStart != i) {
						tokens.add(compileToken(expression.substring(tokenStart, i), exp));
					}
					tokenStart = i;
					continue;
				case ')':
					depth--;
					if (depth == 0) {
						tokens.add(compileValue(expression, exp, tokenStart + 1, i));
						tokenStart = i + 1;
						op = true;
					}
					continue;
			}
			if (depth != 0) {
				continue;
			}
			Operator operator = opMap.getFrom(expression, i);
			if (operator != null) {
				if (!op) {
					tokens.add(compileToken(expression.substring(tokenStart, i), exp));
				}
				op = true;
				tokens.add(operator);
				i += operator.getSymbol().length() - 1;
				tokenStart = i + 1;
				continue;
			}
			op = false;
		}
		if (depth != 0) {
			throw new ExpressionCompilationException("Unbalanced parenthesis");
		}
		if (tokenStart != end) {
			tokens.add(compileToken(expression.substring(tokenStart, end), exp));
		}
		return reduceTokens(tokens);
	}
	
	private static Value reduceTokens(List<Token> tokens) {
		while (tokens.size() > 1) {
			int maxInd = -1;
			int priority = -1;
			for (int i = 0; i < tokens.size(); i++) {
				Token token = tokens.get(i);
				if (token.getType() != TokenType.OPERATOR) {
					continue;
				}
				Operator op = (Operator) token;
				if (priority == -1 || op.getPriority() >= priority) {
					priority = op.getPriority();
					maxInd = i;
				}
			}
			if (priority == -1) {
				throw new ExpressionCompilationException("Expression has multiple values without operator between them");
			}
			createOperation(tokens, maxInd);
		}
		Token token = tokens.get(0);
		if (!(token instanceof Value)) {
			throw new ExpressionCompilationException("Token is not a value: " + token.toString());
		}
		return (Value) tokens.get(0);
	}
	
	private static void createOperation(List<Token> tokens, int index) {
		Operator op = (Operator) tokens.get(index);
		if (index + 1 >= tokens.size()) {
			throw new ExpressionCompilationException("Operator " + op + " has no following operand");
		}
		if (op.isUnary()) {
			Token next = tokens.remove(index + 1);
			if (next.getType() == TokenType.OPERATOR) {
				throw new ExpressionCompilationException("Adjacent operators have no values to operate on");
			}
			if (next.getType() == TokenType.LITERAL_VALUE) {
				LiteralValue literal = (LiteralValue) next;
				tokens.set(index, new LiteralValue(op.operate(literal.getValue())));
				return;
			}
			tokens.set(index, new Operation(op, (Value) next));
			return;
		}
		if (index < 1) {
			throw new ExpressionCompilationException("Operator " + op + " has no leading operand");
		}
		Token prev = tokens.get(index - 1);
		Token next = tokens.get(index + 1);
		if (prev.getType() == TokenType.OPERATOR || next.getType() == TokenType.OPERATOR) {
			throw new ExpressionCompilationException("Adjacent operators have no values to operate on");
		}
		tokens.subList(index - 1, index + 1).clear();
		if (prev.getType() == TokenType.LITERAL_VALUE && next.getType() == TokenType.LITERAL_VALUE) {
			LiteralValue lit1 = (LiteralValue) prev;
			LiteralValue lit2 = (LiteralValue) next;
			tokens.set(index - 1, new LiteralValue(op.operate(lit1.getValue(), lit2.getValue())));
			return;
		}
		tokens.set(index - 1, new Operation(op, (Value) prev, (Value) next));
	}
	
	private static Token compileToken(String str, CompiledExpression exp) {
		if (str.charAt(0) == VAR_CHAR) {
			return new Variable(exp, Integer.parseInt(str.substring(1)) - 1);
		}
		return new LiteralValue(Double.parseDouble(str));
	}
	
}
