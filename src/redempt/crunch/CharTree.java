package redempt.crunch;

class CharTree<T> {
	
	private Node root = new Node();
	
	public void set(String str, T op) {
		Node node = root;
		for (char c : str.toCharArray()) {
			node = node.getOrCreateNode(c);
		}
		node.setValue(op);
	}
	
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
	
	public boolean containsFirstChar(char c) {
		return root.getNode(c) != null;
	}
	
	public T getFrom(String str, int index) {
		Node node = root;
		T val = null;
		for (int i = index; i < str.length(); i++) {
			node = node.getNode(str.charAt(i));
			if (node == null) {
				return val;
			}
			if (node.getValue() != null) {
				val = (T) node.getValue();
			}
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
