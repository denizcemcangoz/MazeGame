class CustomQueue {
    private Node front, rear;
    private int size;
    
    private static class Node {
        Agent data;
        Node next;
        Node(Agent data) {
            this.data = data;
            this.next = null;
        }
    }
    
    public CustomQueue() {
        front = rear = null;
        size = 0;
    }
    
    public void enqueue(Agent item) {
        Node newNode = new Node(item);
        if (isEmpty()) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }
    
    public Agent dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        Agent item = front.data;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return item;
    }
    
    public Agent peek() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty");
        return front.data;
    }
    
    public boolean isEmpty() {
        return front == null;
    }
    
    public int size() {
        return size;
    }
    
    public boolean allAgentsFinished() {
        Node current = front;
        while (current != null) {
            if (!current.data.hasReachedGoal()) {
                return false;
            }
            current = current.next;
        }
        return true;
    }
}