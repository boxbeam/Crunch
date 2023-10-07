package redempt.crunch;

import redempt.crunch.data.FastNumberParsing;
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
    private int maxVarIndex;
    
    ExpressionParser(String str, ExpressionEnv env) {
        if (str == null) {
            throw new ExpressionCompilationException(null, "Expression is null");
        }
        if (env == null) {
            throw new ExpressionCompilationException(null, "Environment is null");
        }
        maxVarIndex = env.getVariableCount() - 1;
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
        if (isAtEnd() || advance() != c) {
            throw new ExpressionCompilationException(this, "Expected '" + c + "'");
        }
    }

    private void error(String msg) {
        throw new ExpressionCompilationException(this, msg);
    }

    private boolean whitespace() {
        while (!isAtEnd() && Character.isWhitespace(peek())) {
            cur++;
        }
        return true;
    }

    private Value parseExpression() {
        if (isAtEnd()) {
            error("Expected expression");
        }
        Value first = parseTerm();
        if (isAtEnd() || peek() == ')' || peek() == ',') {
            return first;
        }
        ShuntingYard tokens = new ShuntingYard();
        tokens.addValue(first);
        while (whitespace() && !isAtEnd() && peek() != ')' && peek() != ',') {
            BinaryOperator token = env.getBinaryOperators().getWith(this);
            if (token == null) {
                error("Expected binary operator");
            }
            tokens.addOperator(token);
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

    private Value parseAnonymousVariable() {
        expectChar('$');
        double value = parseLiteral().getValue();
        if (value % 1 != 0) {
            error("Decimal variable indices are not allowed");
        }
        if (value < 1) {
            error("Zero and negative variable indices are not allowed");
        }
        int index = (int) value - 1;
        maxVarIndex = Math.max(index, maxVarIndex);
        return new Variable(expr, index);
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
            case '$':
                return parseAnonymousVariable();
        }
        Token leadingOperator = env.getLeadingOperators().getWith(this);
        if (leadingOperator != null) {
            return parseLeadingOperation(leadingOperator);
        }
        Value term = env.getValues().getWith(this);
        if (term == null) {
            error("Expected value");
        }
        if (term instanceof Variable) {
            ((Variable) term).expression = expr;
        }
        return term;
    }

    private LiteralValue parseLiteral() {
        int start = cur;
        char c;
        while (Character.isDigit(c = peek()) || c == '.') {
            advanceCursor();
            if (isAtEnd()) break;
        }
        return new LiteralValue(FastNumberParsing.parseDouble(str, start, cur));
    }

    private Value parseLeadingOperation(Token token) {
        whitespace();
        switch (token.getType()) {
            case UNARY_OPERATOR:
                UnaryOperator op = (UnaryOperator) token;
                Value term = parseTerm();
                if (op.isPure() && term.getType() == TokenType.LITERAL_VALUE) {
                    return new LiteralValue(op.operate.applyAsDouble(term.getValue()));
                }
                return new UnaryOperation((UnaryOperator) token, term);
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
            whitespace();
            values[i] = parseExpression();
            whitespace();
        }

        expectChar(')');
        return new ArgumentList(values);
    }

    public CompiledExpression parse() {
        whitespace();
        Value value = parseExpression();
        whitespace();
        if (!isAtEnd()) {
            error("Dangling term");
        }
        expr.initialize(value, maxVarIndex + 1);
        return expr;
    }
    
}