class CustomStack {
    public static class Node {
        String data;
        Node next;
        Node(String data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node top;
    private int size;
    
    public CustomStack() {
        top = null;
        size = 0;
    }
    
    public void push(String item) {
        Node newNode = new Node(item);
        newNode.next = top;
        top = newNode;
        size++;
    }
    
    public String pop() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        String item = top.data;
        top = top.next;
        size--;
        return item;
    }
    
    public String peek() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty");
        return top.data;
    }
    
    public boolean isEmpty() {
        return top == null;
    }
    
    public int size() {
        return size;
    }
    
    public Node getTop() {
        return top;
    }
}