import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Agent {
    private final int id;
    private int currentX, currentY;
    private boolean hasReachedGoal;
    private boolean hasPowerUp;
    private int totalMoves;
    private int backtracks;
    private CustomStack moveHistory;

    private int trapsTriggered;
    private int powerUpsCollected;
    private int goalReachedTurn = -1;

    public void incrementTrapsTriggered() {
        trapsTriggered++;
    }
    
    public void incrementPowerUpsCollected() {
        powerUpsCollected++;
    }
    
    public void setGoalReachedTurn(int turn) {
        goalReachedTurn = turn;
    }
    
    public int getGoalReachedTurn() {
        return goalReachedTurn;
    }
    
    public int getTrapsTriggered() {
        return trapsTriggered;
    }
    
    public int getPowerUpsCollected() {
        return powerUpsCollected;
    }

   

    public Agent(int id, int startX, int startY) {
        this.id = id;
        this.currentX = startX;
        this.currentY = startY;
        this.hasReachedGoal = false;
        this.hasPowerUp = false;
        this.totalMoves = 0;
        this.backtracks = 0;
        this.moveHistory = new CustomStack();
        recordMove(startX, startY);
        
        // Initialize power-up fields
        this.teleportCharges = 0;
        this.shieldTurnsRemaining = 0;
        this.doubleMoveTurnsRemaining = 0;
        this.isShieldActive = false;
    }
    private void recordMove(int x, int y) {
        moveHistory.push(x + "," + y);
    }

   
    

    public void move(String direction) {
        recordMove(currentX, currentY);
        totalMoves++;
    }

    public void backtrack() {
        if (moveHistory.size() > 1) {
            // Remove current position
            moveHistory.pop();
            
            // Get previous position
            String[] coords = moveHistory.peek().split(",");
            this.currentX = Integer.parseInt(coords[0]);
            this.currentY = Integer.parseInt(coords[1]);
            
            backtracks++;
        }
    }
    
    public List<String> getMoveHistoryAsList() {
        List<String> history = new ArrayList<>();
        CustomStack.Node current = moveHistory.getTop();
        while (current != null) {
            history.add(current.data);
            current = current.next;
        }
        Collections.reverse(history); 
        return history;
    }
    

    
    

  

    


    // Getters and setters
    public int getId() { return id; }
    public int getCurrentX() { return currentX; }
    public int getCurrentY() { return currentY; }
    public void setCurrentX(int x) { currentX = x; }
    public void setCurrentY(int y) { currentY = y; }
    public boolean hasReachedGoal() { return hasReachedGoal; }
    public boolean hasPowerUp() { return hasPowerUp; }
    public void setPowerUp(boolean powerUp) { hasPowerUp = powerUp; }
    public int getTotalMoves() { return totalMoves; }
    public int getBacktracks() { return backtracks; }

    
     private int teleportCharges;
     private int shieldTurnsRemaining;
     private int doubleMoveTurnsRemaining;
     private boolean isShieldActive;
 
     public void applyPowerUp(char powerUpType) {
        this.hasPowerUp = true;
        
        switch(powerUpType) {
            case MazeTile.POWERUP_TELEPORT:
                teleportCharges += 2;
                System.out.println("Agent " + (id+1) + " got Teleport (2 charges)!");
                break;
            case MazeTile.POWERUP_SHIELD:
                isShieldActive = true;
                shieldTurnsRemaining = 5;
                System.out.println("Agent " + (id+1) + " got Shield (5 turns)!");
                break;
            case MazeTile.POWERUP_DOUBLE_MOVE:
                doubleMoveTurnsRemaining = 3;
                System.out.println("Agent " + (id+1) + " got Double Move (3 turns)!");
                break;
            case MazeTile.POWERUP_TIME_EXTEND:
                // Add game logic for time extension
                System.out.println("Agent " + (id+1) + " got Time Extend!");
                break;
        }
    }

    public void setReachedGoal(boolean reached) {
        this.hasReachedGoal = reached;
        if (reached) {
            System.out.println("Agent " + (id+1) + " has reached to target and waiting for its turn!");
        }
    }
 
     public boolean canDoubleMove() {
         return doubleMoveTurnsRemaining > 0;
     }
 
     public boolean hasShield() {
         return isShieldActive;
     }
 
     public boolean canTeleport() {
         return teleportCharges > 0;
     }
 
     public void useTeleport() {
         if (teleportCharges > 0) {
             teleportCharges--;
         }
     }
 
     public void decrementPowerUpEffects() {
        if (isShieldActive && --shieldTurnsRemaining <= 0) {
            isShieldActive = false;
            System.out.println("Agent " + (id+1) + "'s shield expired!");
        }
        
        if (doubleMoveTurnsRemaining > 0) {
            doubleMoveTurnsRemaining--;
            if (doubleMoveTurnsRemaining == 0) {
                System.out.println("Agent " + (id+1) + "'s double move expired!");
            }
        }
        
        // Check if any power-up is still active
        hasPowerUp = teleportCharges > 0 || isShieldActive || doubleMoveTurnsRemaining > 0;
    }

     
  

    
}