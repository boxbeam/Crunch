package redempt.crunch.solve;

import redempt.crunch.CompiledExpression;
import redempt.crunch.token.Operation;
import redempt.crunch.token.Operator;
import redempt.crunch.token.Value;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple equation solver that approximates the values of single-variable equations using gradient descent
 * @author Redempt
 */
public class EquationSolver {

    private CompiledExpression left;
    private CompiledExpression right;
    private Random random = ThreadLocalRandom.current();

    /**
     * Takes in a CompiledExpression representing the equation. The CompiledExpression must be an equality of two values, and have exactly one variable.
     *
     * @param expression The equation
     */
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

    /**
     * Sets the random number generator to be used for gradient descent
     */
    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Calculates the error for a given input on this equation
     */
    public double error(double input) {
        return Math.abs(left.evaluate(input) - right.evaluate(input));
    }

    private double slope(double input) {
        return slope(input, 0.01);
    }

    private double slope(double input, double h) {
        return (error(input + h) - error(input)) / h;
    }

    /**
     * Attempts to solve the equation with 1000 max tries and 0 threshold.
     * @return The approximated value
     */
    public double solve() {
        return solve(1000);
    }

    /**
    * Attempts to solve the equation with 0 threshold.
    * @param maxTries The max number of steps to apply in gradient descent for approximation. Higher = more accurate
    * @return The approximated value
    */
    public double solve(int maxTries) {
        return solve(maxTries, 0);
    }

    /**
    * Attempts to solve the equation
    * @param maxTries The max number of steps to apply in gradient descent for approximation. Higher = more accurate
    * @param threshold The error threshold under which the code can return early
    * @return The approximated value
    */
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
