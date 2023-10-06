package redempt.crunch.token;

public class UnaryOperation implements Value {
    private UnaryOperator operator;
    private Value first;

    public UnaryOperation(UnaryOperator operator, Value value) {
        this.operator = operator;
        this.first = value;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    public Value getChild() {
        return first;
    }
    
    @Override
	public double getValue() {
        return operator.operate.applyAsDouble(first.getValue());
    }

    @Override
	public TokenType getType() {
        return TokenType.UNARY_OPERATOR;
    }

    public String toString() {
        return "(" + operator.symbol +  first.toString() + ")";
    }

    public UnaryOperation getClone() {
        return new UnaryOperation(operator, first.getClone());
    }
}