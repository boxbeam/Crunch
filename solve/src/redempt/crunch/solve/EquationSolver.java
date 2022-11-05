package redempt.crunch.solve;

import redempt.crunch.CompiledExpression;
import redempt.crunch.token.Operation;
import redempt.crunch.token.Operator;
import redempt.crunch.token.Value;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EquationSolver {

    private CompiledExpression left;
    private CompiledExpression right;
    private Random random = ThreadLocalRandom.current();

    public EquationSolver(CompiledExpression expression) {
        if (expression.getVariableCount() != 1) {
            throw new IllegalArgumentException("Input must have exactly one variable");
        }
        Value value = expression.getValue();
        if (!(value instanceof Operation)) {
            throw new IllegalArgumentException("Input is not an equation");
        }
        Operation operation = (Operation) value;
        if (operation.getOperator() != Operator.EQUAL_TO) {
            throw new IllegalArgumentException("Input is not an equation");
        }
        Value[] values = operation.getValues();
        Value left = values[0];
        Value right = values[1];
        this.left = new CompiledExpression(left);
        this.right = new CompiledExpression(right);
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public double error(double input) {
        return Math.abs(left.evaluate(input) - right.evaluate(input));
    }

    private double slope(double input) {
        return slope(input, 0.01);
    }

    private double slope(double input, double h) {
        return (error(input + h) - error(input)) / h;
    }

    public double solve() {
        return solve(1000);
    }

    public double solve(int maxTries) {
        return solve(maxTries, 0);
    }

    public double solve(int maxTries, double threshold) {
        double current = 1;
        double minError = error(current);
        double slope = slope(current);
        for (int i = 0; i < maxTries && minError > threshold; i++) {
            double step = Math.signum(slope) * (minError / maxTries);
            step *= random.nextDouble(Math.sqrt(minError));
            double next = current - step;
            double nextError = error(next);
            if (nextError < minError) {
                current = next;
                minError = nextError;
                slope = slope(current);
            }
        }
        return current;
    }

}
