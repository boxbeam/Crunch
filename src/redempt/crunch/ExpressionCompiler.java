package redempt.crunch;

import redempt.crunch.data.FastNumberParsing;
import redempt.crunch.exceptions.ExpressionCompilationException;
import redempt.crunch.functional.ArgumentList;
import redempt.crunch.functional.ExpressionEnv;
import redempt.crunch.functional.Function;
import redempt.crunch.functional.FunctionCall;
import redempt.crunch.token.*;

class ExpressionCompiler {
	
	private static final char VAR_CHAR = '$';
	
	static CompiledExpression compile(String expression, ExpressionEnv env) {
		if (expression == null || env == null) {
			throw new ExpressionCompilationException(null, "Expression and environment may not be null");
		}
		ExpressionParser parser = new ExpressionParser(expression, env);
		return parser.parse();
	}
	
	private static Value parseExpression(ExpressionParser parser, ExpressionEnv env) {
		ShuntingYard tokens = new ShuntingYard();
		tokens.addValue(parseTerm(parser, env));
		parser.whitespace();
		while (!parser.isAtEnd() && parser.peek() != ')') {
			Token token = env.getNamedTokens().getWith(parser);
			if (!(token instanceof BinaryOperator)) {
				throw new ExpressionCompilationException(parser, "Expected binary operator");
			}
			tokens.addOperator((BinaryOperator) token);
			parser.whitespace();
			tokens.addValue(parseTerm(parser, env));
		}
		return tokens.finish();
	}

	private static Value parseNestedExpression(ExpressionParser parser, ExpressionEnv env) {
		parser.expectChar('(');
		parser.whitespace();
		Value expression = parseExpression(parser, env);
		parser.expectChar(')');
		return expression;
	}

	private static Value parseTerm(ExpressionParser parser, ExpressionEnv env) {
		switch (parser.peek()) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
				return parseLiteral(parser);
			case '(':
				return parseNestedExpression(parser, env);
		}
		Token token = env.getNamedTokens().getWith(parser);
		if (token == null) {
			throw new ExpressionCompilationException(parser, "Expected value");
		}
		if (token instanceof Value) {
			return (Value) token;
		}
		return parseLeadingOperation(parser, env, token);
	}

	private static LiteralValue parseLiteral(ExpressionParser parser) {
		int start = parser.cur;
		char c;
		while (Character.isDigit(c = parser.peek()) || c == '.') {
			parser.advanceCursor();
		}
		return new LiteralValue(FastNumberParsing.parseInt(parser.str, start, parser.cur));
	}

	private static Value parseLeadingOperation(ExpressionParser parser, ExpressionEnv env, Token token) {
		if (token instanceof Value) {
			return (Value) token;
		}
		switch (token.getType()) {
			case UNARY_OPERATOR:
				return new UnaryOperation((UnaryOperator) token, parseTerm(parser, env));
			case FUNCTION:
				Function function = (Function) token;
				ArgumentList args = parseArgumentList(parser, env, function.getArgCount());
				return new FunctionCall(function, args.getArguments());
		}
		throw new ExpressionCompilationException(parser, "Expected leading operation");
	}

	private static ArgumentList parseArgumentList(ExpressionParser parser, ExpressionEnv env, int args) {
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
	
}
