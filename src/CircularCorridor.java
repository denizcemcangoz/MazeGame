public class CircularCorridor {
    private Node head;
    private int size;
    private int row; // -1 means vertical corridor
    private int col; // -1 means horizontal corridor
    private int x; // For square blocks
    private int y; // For square blocks
    private CorridorType type;

    private static class Node {
        MazeTile data;
        Node next;
        Node(MazeTile data) { this.data = data; }
    }
    
    public enum CorridorType {
        HORIZONTAL, VERTICAL, SQUARE
    }

    public CircularCorridor(MazeTile[] tiles, int rowOrX, int colOrY, CorridorType type) {
        if (tiles == null || tiles.length == 0) throw new IllegalArgumentException("Tiles array cannot be empty");
        
        this.type = type;
        if (type == CorridorType.HORIZONTAL) {
            this.row = rowOrX;
            this.col = -1;
        } else if (type == CorridorType.VERTICAL) {
            this.col = colOrY;
            this.row = -1;
        } else {
            this.x = rowOrX;
            this.y = colOrY;
        }

        // Create circular linked list
        this.head = new Node(tiles[0]);
        Node current = head;
        for (int i = 1; i < tiles.length; i++) {
            current.next = new Node(tiles[i]);
            current = current.next;
        }
        current.next = head; // Connect the last node to the head to make it circular
        this.size = tiles.length;
    }

    // Properly rotate the corridor by shifting tiles in the circular linked list
    public void rotate() {
        if (head == null || head.next == head) return; // Empty or single node
        
        // Make a copy of data from head node (to save the first tile)
        MazeTile firstTile = head.data;
        
        // Shift all tiles one position forward
        Node current = head;
        while (current.next != head) {
            current.data = current.next.data;
            current = current.next;
        }
        
        // Place the first tile at the end
        current.data = firstTile;
        
        // For debugging
        System.out.println("Corridor rotated: " + 
            (type == CorridorType.HORIZONTAL ? "horizontal at row " + row : 
             type == CorridorType.VERTICAL ? "vertical at column " + col : 
             "square at (" + x + "," + y + ")"));
    }

    public MazeTile[] getTiles() {
        MazeTile[] result = new MazeTile[size];
        Node current = head;
        for (int i = 0; i < size; i++) {
            result[i] = current.data;
            current = current.next;
        }
        return result;
    }

    public boolean isHorizontal() {
        return type == CorridorType.HORIZONTAL;
    }

    public boolean isVertical() {
        return type == CorridorType.VERTICAL;
    }

    public boolean isSquare() {
        return type == CorridorType.SQUARE;
    }

    public int getRow() {
        if (!isHorizontal()) throw new IllegalStateException("Not a horizontal corridor");
        return row;
    }

    public int getCol() {
        if (!isVertical()) throw new IllegalStateException("Not a vertical corridor");
        return col;
    }

    public int getX() {
        if (!isSquare()) throw new IllegalStateException("Not a square corridor");
        return x;
    }

    public int getY() {
        if (!isSquare()) throw new IllegalStateException("Not a square corridor");
        return y;
    }
}