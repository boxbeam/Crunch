package redempt.crunch;

import redempt.crunch.data.FastNumberParsing;
import redempt.crunch.exceptions.ExpressionCompilationException;
import redempt.crunch.functional.ArgumentList;
import redempt.crunch.functional.ExpressionEnv;
import redempt.crunch.functional.Function;
import redempt.crunch.functional.FunctionCall;
import redempt.crunch.token.BinaryOperator;
import redempt.crunch.token.LiteralValue;
import redempt.crunch.token.Token;
import redempt.crunch.token.TokenType;
import redempt.crunch.token.UnaryOperation;
import redempt.crunch.token.UnaryOperator;
import redempt.crunch.token.Value;

public class ExpressionParser {

    private final String input;
    private final ExpressionEnv environment;
    private final CompiledExpression expression = new CompiledExpression();

    private int maxVarIndex;
    private int cursor = 0;

    ExpressionParser(String input, ExpressionEnv env) {
        if (input == null) {
            throw new ExpressionCompilationException(null, "Expression is null");
        }
        if (env == null) {
            throw new ExpressionCompilationException(null, "Environment is null");
        }
        maxVarIndex = env.getVariableCount() - 1;
        this.input = input;
        this.environment = env;
    }

    public char peek() {
        return input.charAt(cursor);
    }

    public char advance() {
        return input.charAt(cursor++);
    }

    public void advanceCursor() {
        cursor++;
    }

    public boolean isAtEnd() {
        return cursor >= input.length();
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public String getInput() {
        return input;
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
            cursor++;
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
            BinaryOperator token = environment.getBinaryOperators().getWith(this);
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
        double value = parseLiteral().getValue(new double[0]);
        if (value % 1 != 0) {
            error("Decimal variable indices are not allowed");
        }
        if (value < 1) {
            error("Zero and negative variable indices are not allowed");
        }
        int index = (int) value - 1;
        maxVarIndex = Math.max(index, maxVarIndex);
        return new Variable(index);
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
            default:
                break; // Ignore
        }

        Token leadingOperator = environment.getLeadingOperators().getWith(this);
        if (leadingOperator != null) {
            return parseLeadingOperation(leadingOperator);
        }
        Value term = environment.getValues().getWith(this);
        if (term == null) {
            error("Expected value");
        }
        return term;
    }

    private LiteralValue parseLiteral() {
        int start = cursor;
        char c;
        while (Character.isDigit(c = peek()) || c == '.') {
            advanceCursor();
            if (isAtEnd()) {
                break;
            }
        }
        return new LiteralValue(FastNumberParsing.parseDouble(input, start, cursor));
    }

    private Value parseLeadingOperation(Token token) {
        whitespace();
        switch (token.getType()) {
            case UNARY_OPERATOR:
                UnaryOperator op = (UnaryOperator) token;
                Value term = parseTerm();
                if (op.isPure() && term.getType() == TokenType.LITERAL_VALUE) {
                    return new LiteralValue(op.getOperation().applyAsDouble(term.getValue(new double[0])));
                }
                return new UnaryOperation(op, term);
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
        expression.initialize(value, maxVarIndex + 1);
        return expression;
    }

}