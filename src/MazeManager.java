import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MazeManager {
    private MazeTile[][] grid;
    private int width, height;
    private Agent[] agents;
    private int goalX, goalY;


    

    // Directions for movement
    private static final int[][] DIRECTIONS = {
        {0, -1}, // UP
        {1, 0},  // RIGHT
        {0, 1},  // DOWN
        {-1, 0}  // LEFT
    };

    public MazeManager(int width, int height, int numAgents) {
        this.width = width;
        this.height = height;
        this.grid = new MazeTile[width][height];
        this.agents = new Agent[numAgents];
        generatePerfectMaze();
        placeSpecialTiles();
        initializeAgents(numAgents);
        initializeRotatingCorridors();
    }

    private void generatePerfectMaze() {
        // Initialize all cells as walls except borders
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Keep outer borders as walls
                if (x == 0 || y == 0 || x == width-1 || y == height-1) {
                    grid[x][y] = new MazeTile(x, y, 'W');
                } else {
                    grid[x][y] = new MazeTile(x, y, 'W'); // Will be carved
                }
            }
        }
    
        // Stack for DFS
        Stack stack = new Stack();
        
        // Start from (1,1)
        int startX = 1;
        int startY = 1;
        grid[startX][startY].setType('E');
        stack.push(new int[]{startX, startY});
    
        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];
    
            // Find all unvisited neighbors
            CustomList neighbors = new CustomList();
            for (int[] dir : DIRECTIONS) {
                int nx = x + 2 * dir[0];
                int ny = y + 2 * dir[1];
                // Ensure we stay inside bounds (not touching outer walls)
                if (nx >= 1 && nx <= width-2 && ny >= 1 && ny <= height-2 && 
                    grid[nx][ny].getType() == 'W') {
                    neighbors.add(new int[]{nx, ny, x + dir[0], y + dir[1]});
                }
            }
    
            if (!neighbors.isEmpty()) {
                int[] chosen = neighbors.removeRandom();
                int nx = chosen[0];
                int ny = chosen[1];
                int wallX = chosen[2];
                int wallY = chosen[3];
    
                // Carve path
                grid[nx][ny].setType('E');
                grid[wallX][wallY].setType('E');
                stack.push(new int[]{nx, ny});
            } else {
                stack.pop();
            }
        }
    
        // Ensure last row and column have at least one opening
        for (int x = 1; x < width-1; x++) {
            if (grid[x][height-3].getType() == 'E') {
                grid[x][height-2].setType('E'); // Open path to bottom
            }
        }
        for (int y = 1; y < height-1; y++) {
            if (grid[width-3][y].getType() == 'E') {
                grid[width-2][y].setType('E'); // Open path to right
            }
        }
    
        // Place goal near but not on the edge
        goalX = width - 2;
        goalY = height - 2;
        grid[goalX][goalY].setType('G');
    }

    

    private void placeSpecialTiles() {
        // Tuzakları yerleştir (%10)
        int trapCount = (int)(countEmptyTiles() * 0.1);
        while (trapCount > 0) {
            int x = 1 + (int)(Math.random() * (width - 2));
            int y = 1 + (int)(Math.random() * (height - 2));
            if (grid[x][y].getType() == MazeTile.EMPTY && !(x == goalX && y == goalY)) {
                grid[x][y].setType(MazeTile.TRAP);
                trapCount--;
            }
        }
    
          char[] powerUpTypes = new char[100];
    // Dağılımı ayarla: 25% L, 35% S, 30% D, 10% X
    Arrays.fill(powerUpTypes, 0, 25, MazeTile.POWERUP_TELEPORT);
    Arrays.fill(powerUpTypes, 25, 60, MazeTile.POWERUP_SHIELD);
    Arrays.fill(powerUpTypes, 60, 90, MazeTile.POWERUP_DOUBLE_MOVE);
    Arrays.fill(powerUpTypes, 90, 100, MazeTile.POWERUP_TIME_EXTEND);

    int powerUpCount = (int)(countEmptyTiles() * 0.15);
    while (powerUpCount > 0) {
        int x = 1 + (int)(Math.random() * (width - 2));
        int y = 1 + (int)(Math.random() * (height - 2));
        
        if (grid[x][y].getType() == 'E' && !(x == goalX && y == goalY)) {
            char randomType = powerUpTypes[(int)(Math.random() * powerUpTypes.length)];
            grid[x][y].setType(randomType);
            powerUpCount--;
        }
    }
    }

    

  
   

    private int countEmptyTiles() {
        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].getType() == 'E') count++;
            }
        }
        return count;
    }

     private void initializeAgents(int numAgents) {
        List<int[]> validStartPositions = new ArrayList<>();
        
        // Collect all valid starting positions (not walls, not goal)
        for (int x = 1; x < width-1; x++) {
            for (int y = 1; y < height-1; y++) {
                if (grid[x][y].getType() == 'E' && !(x == goalX && y == goalY)) {
                    validStartPositions.add(new int[]{x, y});
                }
            }
        }
        
        // Randomly assign starting positions
        Collections.shuffle(validStartPositions);
        for (int i = 0; i < numAgents && i < validStartPositions.size(); i++) {
            int[] pos = validStartPositions.get(i);
            agents[i] = new Agent(i, pos[0], pos[1]);
            grid[pos[0]][pos[1]].setAgent(i);
        }
    }

    public void printMazeState() {
        System.out.println("\nCurrent Maze State:");
        
        // Print top border
        System.out.print("  ┌");
        for (int x = 0; x < width; x++) System.out.print("──");
        System.out.println("┐");
        
        for (int y = 0; y < height; y++) {
            System.out.print(y + " │");
            for (int x = 0; x < width; x++) {
                System.out.print(" " + grid[x][y]);
            }
            System.out.println(" │");
        }
        
        // Print bottom border
        System.out.print("  └");
        for (int x = 0; x < width; x++) System.out.print("──");
        System.out.println("┘");
        
        // Print coordinates
        System.out.print("    ");
        for (int x = 0; x < width; x++) {
            System.out.print(x + " ");
        }
        System.out.println();
        
        // Print agent status
        System.out.println("\nAgent Status:");
        for (Agent agent : agents) {
            int displayId = agent.getId() + 1; // Show 1-based to players
            String colorCode = getAgentColor(agent.getId());
            System.out.println(colorCode + "Agent " + displayId + "\u001B[0m: " +
                            "At (" + agent.getCurrentX() + "," + agent.getCurrentY() + ") " +
                            (agent.hasPowerUp() ? "⚡" : "") +
                            (agent.hasReachedGoal() ? "🏁" : ""));
    }
    }

    public boolean isValidMove(int fromX, int fromY, String direction) {
        int toX = fromX, toY = fromY;
        
        switch (direction) {
            case "UP": toY--; break;
            case "DOWN": toY++; break;
            case "LEFT": toX--; break;
            case "RIGHT": toX++; break;
            default: return false;
        }
        
        if (toX < 0 || toX >= width || toY < 0 || toY >= height) {
            return false;
        }
        
        return grid[toX][toY].isTraversable();
    }
    public String getAgentColor(int agentId) {
        switch(agentId % 6) {
            case 0: return "\u001B[31m"; // Red
            case 1: return "\u001B[32m"; // Green
            case 2: return "\u001B[33m"; // Yellow
            case 3: return "\u001B[34m"; // Blue
            case 4: return "\u001B[35m"; // Purple
            case 5: return "\u001B[36m"; // Cyan
            default: return "\u001B[37m"; // White
        }
    }

    public void updateAgentPosition(Agent agent, int newX, int newY) {
        int oldX = agent.getCurrentX();
        int oldY = agent.getCurrentY();
        
        // First, clear the agent from old position
        grid[oldX][oldY].clearAgent();
        
        // Update agent's coordinates
        agent.setCurrentX(newX);
        agent.setCurrentY(newY);
        
        // Mark the new position with the agent
        grid[newX][newY].setAgent(agent.getId());
    }

    public void checkTileEffects(Agent agent) {
        MazeTile currentTile = grid[agent.getCurrentX()][agent.getCurrentY()];
        if (agent.hasReachedGoal()) {
            return; // Skip effects if already reached goal
        }
        
        if (currentTile.isTrap()) {
            System.out.println("Agent " + (agent.getId()+1) + " hit a trap!");
            agent.incrementTrapsTriggered();
            
            // First clear the agent from current position
            currentTile.clearAgent();
            
            // Apply backtrack
            if (!agent.hasShield()) {
               
            
                
                agent.backtrack();
                
                // Only after backtracking, update the position
                MazeTile backtrackTile = grid[agent.getCurrentX()][agent.getCurrentY()];
                backtrackTile.setAgent(agent.getId());
                
                System.out.println("Agent " + (agent.getId()+1) + " backtracked to (" + 
                    agent.getCurrentX() + "," + agent.getCurrentY() + ")");
                    
                // Convert trap to empty space
                currentTile.setType(MazeTile.EMPTY);
            } else {
                System.out.println("Agent " + (agent.getId()+1) + "'s shield protected from trap!");
                currentTile.setType(MazeTile.EMPTY);
            }
        }
        else if (currentTile.isPowerUp()) {
            agent.incrementPowerUpsCollected();
            System.out.println("Agent " + (agent.getId()+1) + " found a power-up!");
            
            char powerUpType = currentTile.getType();
            agent.applyPowerUp(powerUpType);
            
            // Clear power-up from the tile
            currentTile.setType(MazeTile.EMPTY);
        }
        
        // Check for goal
        if (grid[agent.getCurrentX()][agent.getCurrentY()].getType() == MazeTile.GOAL) {
            agent.setReachedGoal(true);
            agent.setGoalReachedTurn(agent.getTotalMoves());
            System.out.println("Agent " + (agent.getId()+1) + " reached the goal!");
        }
    }

    public Agent[] getAgents() {
        return agents;
    }

    public int getGoalX() { return goalX; }
    public int getGoalY() { return goalY; }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private List<CircularCorridor> rotatingCorridors;
    private int rotationInterval = 5; // Her 5 turda bir dönme

    public void initializeRotatingCorridors() {
        rotatingCorridors = new ArrayList<>();
        
        // Add corridors based on maze size
        if (width >= 5 && height >= 5) {
            // Add horizontal corridors (one at top third, one at bottom third)
            addHorizontalCorridor(height / 3);
            addHorizontalCorridor((height * 2) / 3);
            
            // Add vertical corridors (one at left third, one at right third)
            addVerticalCorridor(width / 3);
            addVerticalCorridor((width * 2) / 3);
            
            // Add square corridors at corners if maze is large enough
            if (width >= 7 && height >= 7) {
                addSquareCorridor(2, 2); // Top-left
                addSquareCorridor(width - 4, 2); // Top-right
                addSquareCorridor(2, height - 4); // Bottom-left
                addSquareCorridor(width - 4, height - 4); // Bottom-right
            }
        }
        
        System.out.println("Initialized " + rotatingCorridors.size() + " rotating corridors");
    }
    
    private void addHorizontalCorridor(int row) {
        // Skip if out of bounds or too close to edge
        if (row <= 0 || row >= height - 1) return;
        
        MazeTile[] tiles = new MazeTile[width];
        for (int x = 0; x < width; x++) {
            tiles[x] = grid[x][row];
        }
        rotatingCorridors.add(new CircularCorridor(tiles, row, -1, CircularCorridor.CorridorType.HORIZONTAL));
        System.out.println("Added horizontal corridor at row " + row);
    }
    
    private void addVerticalCorridor(int col) {
        // Skip if out of bounds or too close to edge
        if (col <= 0 || col >= width - 1) return;
        
        MazeTile[] tiles = new MazeTile[height];
        for (int y = 0; y < height; y++) {
            tiles[y] = grid[col][y];
        }
        rotatingCorridors.add(new CircularCorridor(tiles, -1, col, CircularCorridor.CorridorType.VERTICAL));
        System.out.println("Added vertical corridor at column " + col);
    }
    
    
    private void addSquareCorridor(int x, int y) {
        // Skip if out of bounds or intersects with border
        if (x <= 0 || y <= 0 || x + 1 >= width - 1 || y + 1 >= height - 1) return;
        
        // Only add if all tiles are traversable (not walls)
        if (!grid[x][y].isTraversable() || !grid[x+1][y].isTraversable() ||
            !grid[x][y+1].isTraversable() || !grid[x+1][y+1].isTraversable()) {
            return;
        }
        
        MazeTile[] tiles = {
            grid[x][y],     // Top-left
            grid[x+1][y],   // Top-right
            grid[x+1][y+1], // Bottom-right
            grid[x][y+1]    // Bottom-left
        };
        rotatingCorridors.add(new CircularCorridor(tiles, x, y, CircularCorridor.CorridorType.SQUARE));
        System.out.println("Added square corridor at position (" + x + "," + y + ")");
    }
    
    public void checkForRotation(int turnCount) {
        if (turnCount % rotationInterval == 0) {
            rotateRandomCorridor();
        }
    }
    
    public void rotateRandomCorridor() {
        if (rotatingCorridors.isEmpty()) {
            System.out.println("No corridors available to rotate!");
            return;
        }
        
        int index = (int)(Math.random() * rotatingCorridors.size());
        CircularCorridor corridor = rotatingCorridors.get(index);
        
        // Store agent positions before rotation
        List<int[]> agentPositions = new ArrayList<>();
        for (Agent agent : agents) {
            agentPositions.add(new int[]{agent.getId(), agent.getCurrentX(), agent.getCurrentY()});
        }
        
        // Perform rotation
        corridor.rotate();
        
        // Update grid based on the rotated corridor
        updateGridAfterRotation(corridor);
        
        // Check if any agents need to be moved with the corridor
        updateAgentPositionsAfterRotation(agentPositions);
        
        // Print information about the rotation
        if (corridor.isHorizontal()) {
            System.out.println("Rotated horizontal corridor at row " + corridor.getRow());
        } else if (corridor.isVertical()) {
            System.out.println("Rotated vertical corridor at column " + corridor.getCol());
        } else {
            System.out.println("Rotated square corridor at (" + corridor.getX() + "," + corridor.getY() + ")");
        }
    }
    
    private void updateGridAfterRotation(CircularCorridor corridor) {
        MazeTile[] tiles = corridor.getTiles();
        
        if (corridor.isHorizontal()) {
            int row = corridor.getRow();
            for (int x = 0; x < width; x++) {
                grid[x][row] = tiles[x];
            }
        } else if (corridor.isVertical()) {
            int col = corridor.getCol();
            for (int y = 0; y < height; y++) {
                grid[col][y] = tiles[y];
            }
        } else if (corridor.isSquare()) {
            int x = corridor.getX();
            int y = corridor.getY();
            grid[x][y] = tiles[0];       // Top-left
            grid[x+1][y] = tiles[1];     // Top-right
            grid[x+1][y+1] = tiles[2];   // Bottom-right
            grid[x][y+1] = tiles[3];     // Bottom-left
        }
    }


public void testCorridorRotation() {
    initializeRotatingCorridors();
    
    System.out.println("Before rotation:");
    printMazeState();
    
    for (CircularCorridor corridor : rotatingCorridors) {
        System.out.println("\nRotating " + 
            (corridor.isHorizontal() ? "horizontal" : 
             corridor.isVertical() ? "vertical" : "square") + " corridor");
        corridor.rotate();
        updateGridAfterRotation(corridor);
        printMazeState();
    }
}

public void displayCorridorInfo() {
    if (rotatingCorridors == null || rotatingCorridors.isEmpty()) {
        System.out.println("There is no rotating corridor.");
        return;
    }
    
    System.out.println("\n=== Rotating Corridors ===");
    System.out.println("Total number of rotating corridors: " + rotatingCorridors.size());
    
    for (int i = 0; i < rotatingCorridors.size(); i++) {
        CircularCorridor corridor = rotatingCorridors.get(i);
        System.out.print((i+1) + ". ");
        
        if (corridor.isHorizontal()) {
            System.out.println("Horizontal Corridor - Row " + corridor.getRow());
        } else if (corridor.isVertical()) {
            System.out.println("Vertical Corridor - Column" + corridor.getCol());
        } else {
            System.out.println("Square Block - Location (" + corridor.getX() + "," + corridor.getY() + ")");
        }
    }
}



public MazeTile[][] getGrid() {
    return grid;
}

public MazeTile getGridTile(int x, int y) {
    if (x >= 0 && x < width && y >= 0 && y < height) {
        return grid[x][y];
    }
    return null;
}

public MazeTile getTile(int x, int y) {
    if (x >= 0 && x < width && y >= 0 && y < height) {
        return grid[x][y];
    }
    return null;
}

private void updateAgentPositionsAfterRotation(List<int[]> previousPositions) {
    // For each agent, check if its tile still has the agent marked
    for (int[] pos : previousPositions) {
        int agentId = pos[0];
        int oldX = pos[1];
        int oldY = pos[2];
        
        Agent agent = agents[agentId];
        
        // If the agent's tile no longer has the agent (it was rotated),
        // we need to find where it went
        if (!grid[oldX][oldY].hasAgent() || grid[oldX][oldY].getAgentId() != agentId) {
            // Search for the agent in the grid
            boolean found = false;
            for (int x = 0; x < width && !found; x++) {
                for (int y = 0; y < height && !found; y++) {
                    if (grid[x][y].hasAgent() && grid[x][y].getAgentId() == agentId) {
                        // Update agent's position
                        agent.setCurrentX(x);
                        agent.setCurrentY(y);
                        found = true;
                        System.out.println("Agent " + (agentId+1) + " moved from (" + 
                            oldX + "," + oldY + ") to (" + x + "," + y + ") due to corridor rotation");
                    }
                }
            }
            
            // If agent not found (rare case), place them back
            if (!found) {
                grid[oldX][oldY].setAgent(agentId);
                System.out.println("Agent " + (agentId+1) + " restored to original position");
            }
        }
    }
}
}





