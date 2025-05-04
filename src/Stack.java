class Stack {
    private Node top;
    private int size;
    
    private static class Node {
        int[] data;
        Node next;
        Node(int[] data) {
            this.data = data;
            this.next = null;
        }
    }
    
    public Stack() {
        top = null;
        size = 0;
    }
    
    public void push(int[] item) {
        Node newNode = new Node(item);
        newNode.next = top;
        top = newNode;
        size++;
    }
    
    public int[] pop() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        int[] item = top.data;
        top = top.next;
        size--;
        return item;
    }
    
    public int[] peek() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        return top.data;
    }
    
    public boolean isEmpty() {
        return top == null;
    }
    
    public int size() {
        return size;
    }
}