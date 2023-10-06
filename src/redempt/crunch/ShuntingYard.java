package redempt.crunch;

import redempt.crunch.token.BinaryOperation;
import redempt.crunch.token.BinaryOperator;
import redempt.crunch.token.Value;

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

    private BinaryOperation createOperation() {
        BinaryOperator op = operators.removeLast();
        Value right = stack.removeLast();
        Value left = stack.removeLast();
        return new BinaryOperation(op, left, right);
    }

    public Value finish() {
        while (stack.size() > 1) {
            createOperation();
        }
        return stack.removeLast();
    }

}
