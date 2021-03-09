package redempt.crunch;

import redempt.crunch.exceptions.ExpressionCompilationException;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

public class ExpressionCompiler {
	
	private static CharTree<Operator> opMap = new CharTree<>();
	private static CharTree<Constant> constMap = new CharTree<>();
	private static final char VAR_CHAR = '$';
	
	static {
		for (Operator operator : Operator.values()) {
			if (operator.isInternal()) {
				continue;
			}
			opMap.set(operator.getSymbol(), operator);
		}
		for (Constant constant : Constant.values()) {
			constMap.set(constant.toString(), constant);
		}
	}
	
	static CompiledExpression compile(String expression) {
		CompiledExpression exp = new CompiledExpression();
		expression = expression.replace(" ", "");
		Value val = compileValue(expression, exp, 0).getFirst();
		exp.setValue(val);
		return exp;
	}
	
	private static Pair<Value, Integer> compileValue(String expression, CompiledExpression exp, int begin) {
		LinkedList<Token> tokens = new LinkedList<>();
		boolean op = opMap.containsFirstChar(expression.charAt(begin));
		boolean closed = false;
		int tokenStart = begin;
		char[] chars = expression.toCharArray();
		int i;
		loop:
		for (i = begin; i < expression.length(); i++) {
			char c = chars[i];
			switch (c) {
				case '(':
					if (!op && tokenStart != i) {
						tokens.add(compileToken(expression.substring(tokenStart, i), exp));
					}
					Pair<Value, Integer> inner = compileValue(expression, exp, i + 1);
					i += inner.getSecond() + 1;
					tokens.add(inner.getFirst());
					tokenStart = i;
					op = true;
					continue;
				case ')':
					if (begin == 0) {
						throw new ExpressionCompilationException("Unbalanced parenthesis");
					}
//					tokens.add(compileValue(expression, exp, tokenStart + 1, i));
//					tokenStart = i + 1;
					closed = true;
					break loop;
			}
			Operator operator = opMap.getFrom(expression, i);
			if (operator != null) {
				if (!op) {
					tokens.add(compileToken(expression.substring(tokenStart, i), exp));
				} else if (operator == Operator.SUBTRACT) {
					operator = Operator.NEGATE;
				}
				op = true;
				tokens.add(operator);
				i += operator.getSymbol().length() - 1;
				tokenStart = i + 1;
				continue;
			}
			op = false;
		}
		if (begin != 0 && !closed) {
			throw new ExpressionCompilationException("Unbalanced parenthesis");
		}
		if (tokenStart < i && i <= expression.length() && !op) {
			tokens.add(compileToken(expression.substring(tokenStart, i), exp));
		}
		return new Pair<>(reduceTokens(tokens), i - begin);
	}
	
	private static Value reduceTokens(LinkedList<Token> tokens) {
		TreeSet<Integer> set = new TreeSet<>();
		int max = -1;
		for (Token token : tokens) {
			if (token.getType() == TokenType.OPERATOR) {
				Operator op = (Operator) token;
				set.add(op.getPriority());
				if (op.getPriority() > max) {
					max = op.getPriority();
				}
			}
		}
		while (set.size() > 0) {
			int priority = set.floor(max);
			ListIterator<Token> iter = tokens.listIterator();
			while (iter.hasNext()) {
				Token token = iter.next();
				if (token.getType() != TokenType.OPERATOR) {
					continue;
				}
				Operator op = (Operator) token;
				if (op.getPriority() != priority) {
					continue;
				}
				createOperation(iter, op);
			}
			set.remove(priority);
		}
		Token token = tokens.getFirst();
		if (!(token instanceof Value)) {
			throw new ExpressionCompilationException("Token is not a value: " + token.toString());
		}
		return (Value) tokens.get(0);
	}
	
	private static void createOperation(ListIterator<Token> iter, Operator op) {
		if (!iter.hasNext()) {
			throw new ExpressionCompilationException("Operator " + op + " has no following operand");
		}
		if (op.isUnary()) {
			Token next = iter.next();
			iter.remove();
			iter.previous();
			if (next.getType() == TokenType.OPERATOR) {
				throw new ExpressionCompilationException("Adjacent operators have no values to operate on");
			}
			if (next.getType() == TokenType.LITERAL_VALUE) {
				Value literal = (Value) next;
				iter.set(new LiteralValue(op.operate(literal.getValue())));
				return;
			}
			iter.set(new Operation(op, (Value) next));
			return;
		}
		if (!iter.hasPrevious()) {
			throw new ExpressionCompilationException("Operator " + op + " has no leading operand");
		}
		Token next = iter.next();
		iter.remove();
		iter.previous();
		Token prev = iter.previous();
		iter.remove();
		iter.next();
		if (prev.getType() == TokenType.OPERATOR || next.getType() == TokenType.OPERATOR) {
			throw new ExpressionCompilationException("Adjacent operators have no values to operate on");
		}
		if (prev.getType() == TokenType.LITERAL_VALUE && next.getType() == TokenType.LITERAL_VALUE) {
			Value lit1 = (Value) prev;
			Value lit2 = (Value) next;
			iter.set(new LiteralValue(op.operate(lit1.getValue(), lit2.getValue())));
			return;
		}
		iter.set(new Operation(op, (Value) prev, (Value) next));
	}
	
	private static Token compileToken(String str, CompiledExpression exp) {
		if (str.charAt(0) == VAR_CHAR) {
			return new Variable(exp, Integer.parseInt(str.substring(1)) - 1);
		}
		Constant constant = constMap.getFrom(str, 0);
		return constant != null && constant.toString().equals(str) ? constant : new LiteralValue(Double.parseDouble(str));
	}
	
}
