package redempt.crunch.token;

public class UnaryOperation implements Value {
    private final UnaryOperator operator;
    private final Value first;

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
    public double getValue(double[] variableValues) {
        return operator.getOperation().applyAsDouble(first.getValue(variableValues));
    }

    @Override
	public TokenType getType() {
        return TokenType.UNARY_OPERATOR;
    }

    public String toString() {
        return "(" + operator.getSymbol() +  first.toString() + ")";
    }

    public UnaryOperation getClone() {
        return new UnaryOperation(operator, first.getClone());
    }
}