package redempt.crunch.token;

import java.util.function.DoubleBinaryOperator;

/**
 * Represents an Operator which can be used in mathematical expressions
 *
 * @author Redempt
 */
public enum BinaryOperator implements Token {

    BOOLEAN_OR("|", 0, (a, b) -> (a == 1 || b == 1) ? 1d : 0d),
    BOOLEAN_OR_ALT("||", 0, (a, b) -> (a == 1 || b == 1) ? 1d : 0d),
    BOOLEAN_AND("&", 0, (a, b) -> (a == 1 && b == 1) ? 1d : 0d),
    BOOLEAN_AND_ALT("&&", 0, (a, b) -> (a == 1 && b == 1) ? 1d : 0d),
    GREATER_THAN(">", 1, (a, b) -> a > b ? 1d : 0d),
    LESS_THAN("<", 1, (a, b) -> a < b ? 1d : 0d),
    EQUAL_TO("=", 1, (a, b) -> a == b ? 1d : 0d),
    EQUAL_TO_ALT("==", 1, (a, b) -> a == b ? 1d : 0d),
    NOT_EQUAL_TO("!=", 1, (a, b) -> a != b ? 1d : 0d),
    GREATER_THAN_OR_EQUAL_TO(">=", 1, (a, b) -> a >= b ? 1d : 0d),
    LESS_THAN_OR_EQUAL_TO("<=", 1, (a, b) -> a <= b ? 1d : 0d),
    EXPONENT("^", 5, (a, b) -> Math.pow(a, b)),
    MULTIPLY("*", 4, (a, b) -> a * b),
    DIVIDE("/", 4, (a, b) -> a / b),
    MODULUS("%", 4, (a, b) -> a % b),
    ADD("+", 3, (a, b) -> a + b),
    SUBTRACT("-", 3, (a, b) -> a - b),
    SCIENTIFIC_NOTATION("E", 5, (a, b) -> a * Math.pow(10, b));

    private final String symbol;
    private final DoubleBinaryOperator operation;
    private final int priority;

    BinaryOperator(String name, int priority, DoubleBinaryOperator operation) {
        this.symbol = name;
        this.operation = operation;
        this.priority = priority;
    }

    @Override
    public TokenType getType() {
        return TokenType.BINARY_OPERATOR;
    }

    public String toString() {
        return symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public DoubleBinaryOperator getOperation() {
        return operation;
    }

    public int getPriority() {
        return priority;
    }
}
