public class PlayerRegistry {
    private static class Node {
        Agent data;
        Node next;
        
        Node(Agent data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node head;
    private int size;
    
    public void register(Agent agent) {
        Node newNode = new Node(agent);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    public Agent getAgent(int index) {
        if (index < 0 || index >= size) return null;
        
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
}