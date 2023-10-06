package redempt.crunch;

import redempt.crunch.data.CharTree;
import redempt.crunch.data.FastNumberParsing;
import redempt.crunch.data.Pair;
import redempt.crunch.exceptions.ExpressionCompilationException;
import redempt.crunch.functional.ArgumentList;
import redempt.crunch.functional.ExpressionEnv;
import redempt.crunch.functional.Function;
import redempt.crunch.functional.FunctionCall;
import redempt.crunch.token.*;

public class ExpressionParser {
    
    public final String str;
    public int cur = 0;
    public final ExpressionEnv env;
    private CompiledExpression expr = new CompiledExpression();
    
    public ExpressionParser(String str, ExpressionEnv env) {
        this.str = str;
        this.env = env;
    }
    
    public char peek() {
        return str.charAt(cur);
    }
    
    public char advance() {
        return str.charAt(cur++);
    }
    
    public void advanceCursor() {
        cur++;
    }

    public boolean isAtEnd() {
        return cur >= str.length();
    }

    public void expectChar(char c) {
        if (advance() != c) {
            throw new ExpressionCompilationException(this, "Expected '" + c + "'");
        }
    }

    private ExpressionCompilationException error(String msg) {
        throw new ExpressionCompilationException(this, msg);
    }

    public void whitespace() {
        while (Character.isWhitespace(peek())) {
            cur++;
        }
    }
    
    public boolean strMatches(String prefix, boolean advance) {
        boolean matches = str.regionMatches(cur, prefix, 0, prefix.length());
        if (matches && advance) {
            cur += prefix.length();
        }
        return matches;
    }
    
    public <T> T getWith(CharTree<T> tree) {
        Pair<T, Integer> result = tree.getFrom(str, cur);
        T parsed = result.getFirst();
        if (parsed == null) {
            return null;
        }
        int offset = result.getSecond();
        cur += offset;
        return parsed;
    }

    private Value parseExpression() {
        Value first = parseTerm();
        if (isAtEnd() || peek() == ')') {
            return first;
        }
        ShuntingYard tokens = new ShuntingYard();
        tokens.addValue(first);
        whitespace();
        while (!isAtEnd() && peek() != ')') {
            Token token = env.getNamedTokens().getWith(this);
            if (!(token instanceof BinaryOperator)) {
                error("Expected binary operator");
            }
            tokens.addOperator((BinaryOperator) token);
            whitespace();
            tokens.addValue(parseTerm());
        }
        return tokens.finish();
    }

    private Value parseNestedExpression() {
        expectChar('(');
        whitespace();
        Value expression = parseExpression();
        expectChar(')');
        return expression;
    }

    private Value parseTerm() {
        switch (peek()) {
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
                return parseLiteral();
            case '(':
                return parseNestedExpression();
        }
        Token token = env.getNamedTokens().getWith(this);
        if (token == null) {
            error("Expected value");
        }
        if (token instanceof Value) {
            if (token instanceof Variable) {
                ((Variable) token).expression = expr;
            }
            return (Value) token;
        }
        return parseLeadingOperation(token);
    }

    private LiteralValue parseLiteral() {
        int start = cur;
        char c;
        while (Character.isDigit(c = peek()) || c == '.') {
            advanceCursor();
        }
        return new LiteralValue(FastNumberParsing.parseInt(str, start, cur));
    }

    private Value parseLeadingOperation(Token token) {
        if (token instanceof Value) {
            return (Value) token;
        }
        switch (token.getType()) {
            case UNARY_OPERATOR:
                return new UnaryOperation((UnaryOperator) token, parseTerm());
            case FUNCTION:
                Function function = (Function) token;
                ArgumentList args = parseArgumentList(function.getArgCount());
                return new FunctionCall(function, args.getArguments());
        }
        error("Expected leading operation");
        return null;
    }

    private ArgumentList parseArgumentList(int args) {
        expectChar('(');
        whitespace();
        Value[] values = new Value[args];
        if (args == 0) {
            expectChar(')');
            return new ArgumentList(new Value[0]);
        }
        values[0] = parseExpression();
        whitespace();
        for (int i = 1; i < args; i++) {
            expectChar(',');
            values[i] = parseExpression();
            whitespace();
        }

        expectChar(')');
        return new ArgumentList(values);
    }

    public CompiledExpression parse() {
        Value value = parseExpression();
        expr.setValue(value);
        return expr;
    }
    
}