package redempt.crunch;

interface Value extends Token, Cloneable {
	
	public double getValue();
	public Value getClone();
	
}
