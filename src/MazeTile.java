public class MazeTile {
    private int x, y;
    private char type; // 'E':Empty, 'W':Wall, 'T':Trap, 'P':Power-up, 'G':Goal
    private boolean hasAgent;
    private int agentId;

    public static final char EMPTY = 'E';
    public static final char WALL = 'W';
    public static final char TRAP = 'T';
    public static final char GOAL = 'G';
    public static final char POWERUP_TELEPORT = 'L';  // 'T' trap ile çakışmaması için
    public static final char POWERUP_SHIELD = 'S';
    public static final char POWERUP_DOUBLE_MOVE = 'D';
    public static final char POWERUP_TIME_EXTEND = 'X';


    public MazeTile(int x, int y, char type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.hasAgent = false;
        this.agentId=-1;
    }

 
    public boolean isTraversable() {
        // Tuzaklar artık geçilebilir (etkisi adım atıldığında tetiklenecek)
        // Hedef karesi her zaman geçişe açık
        return type != 'W' || type == 'G';
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public void setHasAgent(boolean hasAgent) {
        this.hasAgent = hasAgent;
    }

    public boolean hasAgent() {
        return hasAgent;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    
    @Override
    public String toString() {
        if (hasAgent) {
            // Ensure agentId is never negative
            int displayId = Math.max(0, agentId) + 1; // Show 1,2,3 instead of 0,1,2
            switch(displayId % 6) {  // 6 different colors
                case 1: return "\u001B[31m" + displayId + "\u001B[0m"; // Red 1
                case 2: return "\u001B[32m" + displayId + "\u001B[0m"; // Green 2
                case 3: return "\u001B[33m" + displayId + "\u001B[0m"; // Yellow 3 
                case 4: return "\u001B[34m" + displayId + "\u001B[0m"; // Blue 4
                case 5: return "\u001B[35m" + displayId + "\u001B[0m"; // Purple 5
                case 0: return "\u001B[36m6\u001B[0m"; // Cyan 6
                default: return "\u001B[37m?\u001B[0m"; // White ?
            }
        }
        
        switch (type) {
            case WALL: return "\u001B[37m█\u001B[0m";
            case EMPTY: return " ";
            case TRAP: return "\u001B[31mX\u001B[0m";
            case POWERUP_TELEPORT: return "\u001B[35m⌧\u001B[0m";
            case POWERUP_SHIELD: return "\u001B[34m⛨\u001B[0m";
            case POWERUP_DOUBLE_MOVE: return "\u001B[33m↝\u001B[0m";
            case POWERUP_TIME_EXTEND: return "\u001B[36m⏱\u001B[0m";
            case GOAL: return "\u001B[32m◉\u001B[0m";
            default: return "?";
        }
        
    }

    public void setAgent(int agentId) {
        this.hasAgent = true;
        this.agentId = agentId;
    }

    public void clearAgent() {
        this.hasAgent = false;
        this.agentId = -1;
    }

    public int getAgentId() {
        return agentId;
    }

    
    public boolean isPowerUp() {
        return type == POWERUP_TELEPORT || 
               type == POWERUP_SHIELD || 
               type == POWERUP_DOUBLE_MOVE || 
               type == POWERUP_TIME_EXTEND;
    }

    public boolean isTrap() {
        return type == 'T'; // Veya TRAP sabitine göre
    }
    
}