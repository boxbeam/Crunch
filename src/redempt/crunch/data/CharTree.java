package redempt.crunch.data;

import redempt.crunch.ExpressionParser;

/**
 * A simple implementation of a prefix tree for better parsing
 * Only supports ASCII characters
 * @param <T> The type stored in this CharTree
 */
public class CharTree<T> {
	
	private Node root = new Node();
	
	/**
	 * Sets a String in this CharTree
	 * @param str The String to use as the key
	 * @param value The value to store
	 */
	public void set(String str, T value) {
		Node node = root;
		for (char c : str.toCharArray()) {
			node = node.getOrCreateNode(c);
		}
		node.setValue(value);
	}
	
	/**
	 * Gets a value by its key
	 * @param str The key
	 * @return The value mapped to the key, or null if it is not present
	 */
	public T get(String str) {
		Node node = root;
		for (char c : str.toCharArray()) {
			node = node.getNode(c);
			if (node == null) {
				return null;
			}
		}
		return (T) node.getValue();
	}
	
	/**
	 * Check if the character exists at the root level in this tree
	 * @param c The character to check
	 * @return Whether the character exists at the root level
	 */
	public boolean containsFirstChar(char c) {
		return root.getNode(c) != null;
	}
	
	/**
	 * Gets a token forward from the given index in a string
	 * @param str The string to search in
	 * @param index The starting index to search from
	 * @return A pair with the token or null if none was found, and the length parsed
	 */
	public Pair<T, Integer> getFrom(String str, int index) {
		Node node = root;
		T val = null;
		for (int i = index; i < str.length(); i++) {
			node = node.getNode(str.charAt(i));
			if (node == null) {
				return new Pair<>(val, i - index);
			}
			if (node.getValue() != null) {
				val = (T) node.getValue();
			}
		}
		return new Pair<>(val, str.length() - index);
	}
	
	public T getWith(ExpressionParser parser) {
		Node node = root;
		T val = null;
		int lastParsed = parser.cur;
		for (int i = lastParsed; i < parser.str.length(); i++) {
			node = node.getNode(parser.str.charAt(i));
			if (node == null) {
				parser.cur = (val == null ? parser.cur : lastParsed + 1);
				return val;
			}
			T nodeValue = (T) node.getValue();
			if (nodeValue != null) {
				lastParsed = i;
				val = nodeValue;
			}
		}
		if (val != null) {
			parser.cur = lastParsed + 1;
		}
		return val;
	}
	
	private static class Node {
		
		private Object value;
		private Node[] children = new Node[256];
		
		public Node getNode(char c) {
			return children[c];
		}
		
		public Node getOrCreateNode(char c) {
			if (children[c] == null) {
				children[c] = new Node();
			}
			return children[c];
		}
		
		public Object getValue() {
			return value;
		}
		
		public void setValue(Object value) {
			this.value = value;
		}
		
	}
	
}
