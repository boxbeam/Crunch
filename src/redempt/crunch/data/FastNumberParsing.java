package redempt.crunch.data;

/**
 * Utility class with some methods for parsing base 10 numbers (only ints and doubles for now) that are faster than the standard Java implementation
 */
public class FastNumberParsing {
	
	/**
	 * Parse an integer from base 10 string input
	 * @param input The base 10 string input
	 * @return The parsed integer
	 */
	public static int parseInt(String input) {
		return parseInt(input, 0, input.length());
	}
	
	/**
	 * Parse an integer from base 10 string input
	 * @param input The base 10 string input
	 * @param start The starting index to parse from, inclusive
	 * @param end The ending index to parse to, exclusive
	 * @return The parsed integer
	 */
	public static int parseInt(String input, int start, int end) {
		if (start == end) {
			throw new NumberFormatException("Zero-length input");
		}
		int i = start;
		boolean negative = false;
		if (input.charAt(i) == '-') {
			negative = true;
			i++;
		}
		int output = 0;
		for (; i < end; i++) {
			char c = input.charAt(i);
			if (c > '9' || c < '0') {
				throw new NumberFormatException("Non-numeric character in input '" + input + "'");
			}
			output *= 10;
			output += c - '0';
		}
		return negative ? -output: output;
	}
	
	/**
	 * Parse a double from base 10 string input, only real number values are supported (no NaN or Infinity)
	 * @param input The base 10 string input
	 * @return The parsed double
	 */
	public static double parseDouble(String input) {
		return parseDouble(input, 0, input.length());
	}
	
	/**
	 * Parse a double from base 10 string input, only real number values are supported (no NaN or Infinity)
	 * @param input The base 10 string input
	 * @param start The starting index to parse from, inclusive
	 * @param end The ending index to parse to, exclusive
	 * @return The parsed double
	 */
	public static double parseDouble(String input, int start, int end) {
		if (start == end) {
			throw new NumberFormatException("Zero-length input");
		}
		int i = start;
		boolean negative = false;
		if (input.charAt(start) == '-') {
			negative = true;
			i++;
		}
		double output = 0;
		double after = 0;
		int decimal = -1;
		for (; i < end; i++) {
			char c = input.charAt(i);
			if (c == '.') {
				if (decimal != -1) {
					throw new NumberFormatException("Second period in double for input '" + input + "'");
				}
				decimal = i;
				continue;
			}
			if (c > '9' || c < '0') {
				throw new NumberFormatException("Non-numeric character in input '" + input + "'");
			}
			if (decimal != -1) {
				after *= 10;
				after += c - '0';
			} else {
				output *= 10;
				output += c - '0';
			}
		}
		after /= Math.pow(10, end - decimal - 1);
		return negative ? -output - after: output + after;
	}
	
}
