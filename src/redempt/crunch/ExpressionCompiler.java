package redempt.crunch;

import redempt.crunch.exceptions.ExpressionCompilationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExpressionCompiler {

	private static Set<Character> opChars = new HashSet<>();
	private static Map<String, Operator> opMap = new HashMap<>();
	private static final char VAR_CHAR = '$';
	
	static {
		for (Operator operator : Operator.values()) {
			for (char c : operator.getSymbol().toCharArray()) {
				opChars.add(c);
			}
			opMap.put(operator.getSymbol(), operator);
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
		boolean op = opChars.contains(expression.charAt(begin));
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
					if (op && tokenStart != i) {
						tokens.add(compileToken(expression.substring(tokenStart, i), op, exp));
					}
					tokenStart = i;
					continue;
				case ')':
					depth--;
					if (depth == 0) {
						tokens.add(compileValue(expression, exp, tokenStart + 1, i));
					}
					tokenStart = i + 1;
					op = true;
					continue;
			}
			if (depth != 0) {
				continue;
			}
			boolean charOp = opChars.contains(c);
			if (charOp ^ op) {
				tokens.add(compileToken(expression.substring(tokenStart, i), op, exp));
				op = charOp;
				tokenStart = i;
			}
		}
		if (depth != 0) {
			throw new ExpressionCompilationException("Unbalanced parenthesis");
		}
		if (tokenStart != end) {
			tokens.add(compileToken(expression.substring(tokenStart, end), op, exp));
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
		tokens.set(index - 1, new Operation(op, (Value) prev, (Value) next));
	}
	
	private static Token compileToken(String str, boolean op, CompiledExpression exp) {
		if (op) {
			Operator operator = opMap.get(str);
			if (operator == null) {
				throw new ExpressionCompilationException("Invalid operator '" + str + "'");
			}
			return operator;
		}
		if (str.charAt(0) == VAR_CHAR) {
			return new Variable(exp, Integer.parseInt(str.substring(1)) - 1);
		}
		return new LiteralValue(Double.parseDouble(str));
	}

}
