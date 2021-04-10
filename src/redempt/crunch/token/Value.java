package redempt.crunch.token;

/**
 * Represents a lazy value which can be evaluated
 * @author Redempt
 */
public interface Value extends Token, Cloneable {
	
	public double getValue();
	public Value getClone();
	
}
