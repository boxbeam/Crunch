package redempt.crunch;

class CharTree {
	
	private Node root = new Node();
	
	public void set(String str, Operator op) {
		Node node = root;
		for (char c : str.toCharArray()) {
			node = node.getOrCreateNode(c);
		}
		node.setValue(op);
	}
	
	public Operator get(String str) {
		Node node = root;
		for (char c : str.toCharArray()) {
			node = node.getNode(c);
			if (node == null) {
				return null;
			}
		}
		return node.getValue();
	}
	
	public boolean containsFirstChar(char c) {
		return root.getNode(c) != null;
	}
	
	public Operator getFrom(String str, int index) {
		Node node = root;
		Operator val = null;
		for (int i = index; i < str.length(); i++) {
			node = node.getNode(str.charAt(i));
			if (node == null) {
				return val;
			}
			if (node.getValue() != null) {
				val = node.getValue();
			}
		}
		return val;
	}
	
	private static class Node {
		
		private Operator value;
		private Node[] children = new Node[512];
		
		public Node getNode(char c) {
			return children[c];
		}
		
		public Node getOrCreateNode(char c) {
			if (children[c] == null) {
				children[c] = new Node();
			}
			return children[c];
		}
		
		public Operator getValue() {
			return value;
		}
		
		public void setValue(Operator value) {
			this.value = value;
		}
		
	}
	
}
