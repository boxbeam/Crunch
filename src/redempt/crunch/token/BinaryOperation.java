package redempt.crunch.token;

public class BinaryOperation implements Value {
	
	private final BinaryOperator operator;
	private final Value first;
	private final Value second;
	
	public BinaryOperation(BinaryOperator operator, Value first, Value second) {
		this.operator = operator;
		this.first = first;
		this.second = second;
	}
	
    public BinaryOperator getOperator() {
        return operator;
    }

	public Value[] getValues() {
		return new Value[] {first, second};
	}
	
	@Override
	public double getValue() {
		return operator.getOperation().applyAsDouble(first.getValue(), second.getValue());
	}
	
	@Override
	public TokenType getType() {
		return TokenType.BINARY_OPERATION;
	}
	
	public String toString() {
		return "(" + first.toString() + operator.getSymbol() + second.toString() + ")";
	}
	
	public BinaryOperation getClone() {
		return new BinaryOperation(operator, first.getClone(), second.getClone());
	}
	
}
