package redempt.crunch;

import redempt.crunch.token.*;

import java.util.ArrayDeque;
import java.util.Deque;

public class ShuntingYard {

    private Deque<BinaryOperator> operators = new ArrayDeque<>();
    private Deque<Value> stack = new ArrayDeque<>();

    public void addOperator(BinaryOperator operator) {
        while (!operators.isEmpty() && operator.priority <= operators.getLast().priority) {
            createOperation();
        }
        operators.add(operator);
    }

    public void addValue(Value value) {
        stack.add(value);
    }

    private void createOperation() {
        BinaryOperator op = operators.removeLast();
        Value right = stack.removeLast();
        Value left = stack.removeLast();
        if (right.getType() == TokenType.LITERAL_VALUE && left.getType() == TokenType.LITERAL_VALUE) {
            stack.add(new LiteralValue(op.operate.applyAsDouble(left.getValue(), right.getValue())));
        } else {
            stack.add(new BinaryOperation(op, left, right));
        }
    }

    public Value finish() {
        while (stack.size() > 1) {
            createOperation();
        }
        return stack.removeLast();
    }

}
