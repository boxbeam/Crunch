package redempt.crunch;

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
	 *
	 * @param str
	 * @param index
	 * @return
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
