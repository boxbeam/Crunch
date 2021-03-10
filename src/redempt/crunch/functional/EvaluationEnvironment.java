package redempt.crunch.functional;

import redempt.crunch.CharTree;

/**
 * Represents an environment containing functions that can be called in expressions
 * @author Redempt
 */
public class EvaluationEnvironment {
	
	private CharTree<Function> functions;
	
	public EvaluationEnvironment() {
		functions = new CharTree<>();
	}
	
	public void addFunction(Function function) {
		char[] chars = function.getName().toCharArray();
		for (char c : chars) {
			if (c > 255) {
				throw new IllegalArgumentException("Function names must be ASCII only");
			}
		}
		functions.set(function.getName(), function);
	}
	
	public void clearFunctions() {
		functions = new CharTree<>();
	}
	
	public CharTree<Function> getFunctions() {
		return functions;
	}
	
}
