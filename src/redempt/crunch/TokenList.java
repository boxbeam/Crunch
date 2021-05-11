package redempt.crunch;

import redempt.crunch.token.Token;

import java.util.function.Consumer;

class TokenList {
	
	private Node head;
	private Node tail;
	private int size = 0;
	
	public void add(Token token) {
		Node node = new Node(token, tail, null);
		if (size == 0) {
			head = node;
		} else {
			tail.next = node;
		}
		tail = node;
		size++;
	}
	
	public int size() {
		return size;
	}
	
	public Node head() {
		return head;
	}
	
	public Node tail() {
		return tail;
	}
	
	public void forEach(Consumer<Token> lambda) {
		for (Node node = head; node != null; node = node.next) {
			lambda.accept(node.token);
		}
	}
	
	public class Node {
		
		public Token token;
		public Node next;
		public Node prev;
		
		public Node(Token token, Node prev, Node next) {
			this.token = token;
			this.prev = prev;
			this.next = next;
		}
		
		public void insertAfter(Token t) {
			Node node = new Node(t, this, this.next);
			this.next = node;
			if (node.next != null) {
				node.next.prev = node;
			}
			if (tail == this) {
				tail = node;
			}
			size++;
		}
		
		public void insertBefore(Token t) {
			Node node = new Node(t, this.prev, this);
			this.prev = node;
			if (node.prev != null) {
				node.prev.next = node;
			}
			if (head == this) {
				head = node;
			}
			size++;
		}
		
		public void removeAfter() {
			if (tail == this) {
				throw new IllegalStateException("Cannot remove after tail");
			}
			if (next == tail) {
				tail = this;
			}
			if (next != null) {
				this.next = next.next;
				if (next != null) {
					this.next.prev = this;
				}
			}
			size--;
		}
		
		public void removeBefore() {
			if (head == this) {
				throw new IllegalStateException("Cannot remove before head");
			}
			if (prev == head) {
				head = this;
			}
			if (prev != null) {
				this.prev = prev.prev;
				if (prev != null) {
					this.prev.next = this;
				}
			}
			size--;
		}
		
	}

}
