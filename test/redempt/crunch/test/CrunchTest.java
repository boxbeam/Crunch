package redempt.crunch.test;

import org.junit.jupiter.api.Test;
import redempt.crunch.Crunch;
import redempt.crunch.exceptions.ExpressionCompilationException;
import redempt.crunch.exceptions.ExpressionEvaluationException;
import redempt.crunch.functional.EvaluationEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CrunchTest {

	private static final double DELTA = 1e-10;
	
	@Test
	public void nullTest() {
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression(null), "Null single argument");
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression(null, null), "Null multi-argument");
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression("1", null), "Second argument null");
	}
	
	@Test
	public void constantTest() {
		assertEquals(Math.PI, Crunch.evaluateExpression("pi"), DELTA, "Pi equality");
		assertEquals(Math.E, Crunch.evaluateExpression("e"), DELTA, "Euler's constant equality");
		assertEquals(1, Crunch.evaluateExpression("true"), DELTA, "True equal to 1");
		assertEquals(0, Crunch.evaluateExpression("false"), DELTA, "False equal to 0");
		assertEquals(-1, Crunch.evaluateExpression("-1"), "Negation operator");
	}

	@Test
	public void basicOperationTest() {
		assertEquals(2, Crunch.evaluateExpression("1+1"), "Simple addition");
		assertEquals(2, Crunch.evaluateExpression("1 + 1"), "Simple expression with whitespace");
		assertEquals(2, Crunch.evaluateExpression("            1      +       1       "), "Lots of whitespace");
		assertEquals(8, Crunch.evaluateExpression("2^3"), "Simple exponent test");
		assertEquals(10, Crunch.evaluateExpression("15 - 5"), "Simple subtraction test");
		assertEquals(2, Crunch.evaluateExpression("1--1"), "Subtraction and negate operator");
		assertEquals(2, Crunch.evaluateExpression("    1     --    1"), "Somewhat confusing whitespace");
	}
	
	@Test
	public void complexOperationTest() {
		assertEquals(9, Crunch.evaluateExpression("6/2*(1+2)"), "Order of operations");
		assertEquals(1, Crunch.evaluateExpression("tan(atan(cos(acos(sin(asin(1))))))"), DELTA, "Trig functions");
		assertEquals(402193.3186140596, Crunch.evaluateExpression("6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4 + 6.5*7.8^2.3 + (3.5^3+7/2)^3 -(5*4/(2-3))*4"), DELTA, "Large expression");
	}
	
	@Test
	public void booleanLogicTest() {
		assertEquals(1, Crunch.evaluateExpression("true & true"), "Boolean and");
		assertEquals(1, Crunch.evaluateExpression("true | false"), "Boolean or");
		assertEquals(0, Crunch.evaluateExpression("true & (true & false | false)"), "More complex boolean expression");
	}
	
	@Test
	public void syntaxTest() {
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression("("), "Lone opening paren");
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression(")"), "Lone closing paren");
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression("1 1"), "No operator");
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression("--1"), "Adjacent operators");
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression("+"), "Only operator");
	}
	
	@Test
	public void variableTest() {
		assertEquals(10, Crunch.evaluateExpression("$1", 10), "Basic variable value");
		assertEquals(14, Crunch.evaluateExpression("$1 - $2", 10, -4), "Multiple variables");
		assertThrows(ExpressionEvaluationException.class, () -> Crunch.evaluateExpression("$1"), "No variable value");
		
		EvaluationEnvironment env = new EvaluationEnvironment();
		env.setVariableNames("x", "y");
		assertEquals(33, Crunch.compileExpression("x * y", env).evaluate(11, 3), "Multiplying named variables");
		assertThrows(ExpressionEvaluationException.class, () -> Crunch.compileExpression("x * y", env).evaluate(1), "Too few values");
		assertThrows(ExpressionEvaluationException.class, () -> Crunch.compileExpression("x", env).evaluate());
	}
	
	@Test
	public void functionTest() {
		EvaluationEnvironment env = new EvaluationEnvironment();
		env.addFunction("mult", 2, d -> d[0] * d[1]);
		env.addFunction("four", 0, d -> 4d);
		assertEquals(45, Crunch.compileExpression("mult(15, 3)", env).evaluate(), "Basic function");
		assertEquals(96, Crunch.compileExpression("mult(2, mult(4, mult(3, 4)))", env).evaluate(), "Nested functions");
		assertEquals(4, Crunch.compileExpression("four()", env).evaluate(), "No-argument function");
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression("mult", env), "No argument list");
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression("mult(1)", env), "Not enough arguments");
		assertThrows(ExpressionCompilationException.class, () -> Crunch.compileExpression("mult(1, 2, 3)", env), "Too many arguments");
	}
	
	@Test
	public void implicitMultiplicationTest() {
		EvaluationEnvironment env = new EvaluationEnvironment();
		env.setVariableNames("x", "y");
		env.addFunction("mult", 2, d -> d[0] * d[1]);
		assertEquals(12, Crunch.evaluateExpression("3(4)"), "Parenthetical");
		assertEquals(6, Crunch.evaluateExpression("3abs(-2)"), "Unary operator");
		assertEquals(9, Crunch.compileExpression("3x", env).evaluate(3), "Single variable");
		assertEquals(15, Crunch.compileExpression("xy", env).evaluate(5, 3), "Two variables");
		assertEquals(16, Crunch.compileExpression("x(4)", env).evaluate(4), "Variable outside parenthesis");
	}

	@Test
	public void rootingTest() {
		assertEquals(2, Crunch.evaluateExpression("sqrt(4)"), "Square Rooting");
		assertEquals(2, Crunch.evaluateExpression("cbrt(8)"), "Cube Rooting");
	}
	
	@Test
	public void lazyVariableTest() {
		EvaluationEnvironment env = new EvaluationEnvironment();
		env.addLazyVariable("x", () -> 2);
		env.addLazyVariable("y", () -> 7);
		assertEquals(14, Crunch.compileExpression("xy", env).evaluate());
	}
	
}
