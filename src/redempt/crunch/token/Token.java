package redempt.crunch.token;

import redempt.crunch.TokenType;

/**
 * Represents a parsed token
 * @author Redempt
 */
public interface Token {
	
	/**
	 * @return The type of this Token
	 */
	public TokenType getType();
	
}
