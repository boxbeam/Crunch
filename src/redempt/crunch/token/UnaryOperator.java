package redempt.crunch.token;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleUnaryOperator;

public enum UnaryOperator implements Token {
    NEGATE("-", d -> -d),
    NOT("!", d -> d == 1 ? 0 : 1),
    SIN("sin", Math::sin),
    COS("cos", Math::cos),
    TAN("tan", Math::tan),
    SINH("sinh", Math::sinh),
    COSH("cosh", Math::cosh),
    TANH("tanh", Math::tanh),
    ASIN("asin", Math::asin),
    ACOS("acos", Math::acos),
    ATAN("atan", Math::atan),
    ABS("abs", Math::abs),
    ROUND("round", Math::round),
    FLOOR("floor", Math::floor),
    CEIL("ceil", Math::ceil),
    LOG("log", Math::log),
    SQRT("sqrt", Math::sqrt),
    CBRT("cbrt", Math::cbrt),
    RAND("rand", d -> ThreadLocalRandom.current().nextDouble() * d, false);

    public final String symbol;
    public final DoubleUnaryOperator operate;
    private boolean pure = true;
    
    private UnaryOperator(String symbol, DoubleUnaryOperator operate) {
        this.symbol = symbol;
        this.operate = operate;
    }

    private UnaryOperator(String symbol, DoubleUnaryOperator operate, boolean pure) {
        this(symbol, operate);
        this.pure = pure;
    }
    
    @Override
    public TokenType getType() {
        return TokenType.UNARY_OPERATOR;
    }
    
    public int getPriority() {
        return 6;
    }

    public boolean isPure() {
        return pure;
    }

    @Override
    public String toString() {
        return symbol;
    }
}