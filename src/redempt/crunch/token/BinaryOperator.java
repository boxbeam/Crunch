package redempt.crunch.token;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

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

    public final String symbol;
    public final DoubleBinaryOperator operate;
    public final int priority;

    private BinaryOperator(String name, int priority, DoubleBinaryOperator operate) {
        this.symbol = name;
        this.operate = operate;
        this.priority = priority;
    }

    @Override
    public TokenType getType() {
        return TokenType.BINARY_OPERATOR;
    }

    public String toString() {
        return symbol;
    }

}
