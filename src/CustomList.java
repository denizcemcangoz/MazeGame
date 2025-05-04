class CustomList {
    private int[][] items;
    private int size;
    private int capacity;
    
    public CustomList() {
        capacity = 10;
        items = new int[capacity][4]; // Each item has 4 coordinates (x, y, wallX, wallY)
        size = 0;
    }
    
    public void add(int[] item) {
        if (size == capacity) {
            // Resize the array if needed
            capacity *= 2;
            int[][] newItems = new int[capacity][4];
            for (int i = 0; i < size; i++) {
                System.arraycopy(items[i], 0, newItems[i], 0, 4);
            }
            items = newItems;
        }
        
        System.arraycopy(item, 0, items[size], 0, 4);
        size++;
    }
    
    public int[] removeRandom() {
        if (isEmpty()) throw new IllegalStateException("List is empty");
        
        int randomIndex = (int)(Math.random() * size);
        int[] result = new int[4];
        System.arraycopy(items[randomIndex], 0, result, 0, 4);
        
        // Move the last item to fill the gap
        if (randomIndex < size - 1) {
            System.arraycopy(items[size - 1], 0, items[randomIndex], 0, 4);
        }
        
        size--;
        return result;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int size() {
        return size;
    }
}