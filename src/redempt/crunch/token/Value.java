package redempt.crunch.token;

/**
 * Represents a lazy value which can be evaluated
 * @author Redempt
 */
public interface Value extends Token, Cloneable {
	
	double getValue();
	Value getClone();
	
}
