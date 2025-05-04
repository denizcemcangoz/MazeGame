import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;





public class GameController {
    private MazeManager maze;
    private Agent[] agents;
    private int currentAgentIndex;
    private int turnCount;
    private int maxTurns;
    
    private PlayerRegistry agentRegistry;

    public GameController(int width, int height, int numAgents, int maxTurns) {
        this.maze = new MazeManager(width, height, numAgents);
        this.maze.initializeRotatingCorridors();
        this.agents = maze.getAgents();
        this.currentAgentIndex = 0;
        this.turnCount = 0;
        this.maxTurns = maxTurns;
        this.agentRegistry = new PlayerRegistry();
        for (Agent agent : agents) {
            agentRegistry.register(agent);
        }
    }

    public MazeManager getMaze() {
        return this.maze;
    }
    private boolean allAgentsFinished() {
        for (Agent agent : agents) {
            if (!agent.hasReachedGoal()) {
                return false;
            }
        }
        return true;
    }
    

    // Add to printFinalResults()
private void printFinalResults() {
    System.out.println("\n=== Game Over ===");
    System.out.println("Total turns: " + turnCount);
    
    // Track goal achievement order
    List<Agent> goalAchievers = new ArrayList<>();
    for (Agent agent : agents) {
        if (agent.hasReachedGoal()) {
            goalAchievers.add(agent);
        }
    }
    
    // Sort by when they reached goal
    goalAchievers.sort(Comparator.comparingInt(Agent::getGoalReachedTurn));
    
    System.out.println("\nGoal Achievement Order:");
    if (goalAchievers.isEmpty()) {
        System.out.println("No agents reached the goal!");
    } else {
        for (int i = 0; i < goalAchievers.size(); i++) {
            Agent a = goalAchievers.get(i);
            System.out.println((i+1) + ". Player " + (a.getId()+1) + 
                " (Turn " + a.getGoalReachedTurn() + ")");
        }
    }
    
    System.out.println("\nDetailed Statistics:");
    System.out.println("+------+-------+-------+--------+------------+----------------+");
    System.out.println("| Player | Moves | Traps | PwrUps | Backtracks | Goal Turn |");
    System.out.println("+------+-------+-------+--------+------------+----------------+");
    
    for (Agent agent : agents) {
        System.out.printf("| %-6d | %-5d | %-5d | %-6d | %-10d | %-12s |\n",
            agent.getId()+1,
            agent.getTotalMoves(),
            agent.getTrapsTriggered(),
            agent.getPowerUpsCollected(),
            agent.getBacktracks(),
            agent.hasReachedGoal() ? agent.getGoalReachedTurn() : "DNF");
    }
    System.out.println("+------+-------+-------+--------+------------+----------------+");
}

    private void printAgentStatus(Agent agent) {
        String colorCode = getAgentColor(agent.getId());
        System.out.println(
            colorCode + "Player " + (agent.getId()+1) + "\u001B[0m at (" + 
            agent.getCurrentX() + "," + agent.getCurrentY() + ")" +
            " | Moves: " + agent.getTotalMoves() +
            " | Backtracks: " + agent.getBacktracks()
        );
        
        // Display power-up status
        System.out.print("  Power-ups: ");
        if (agent.canTeleport()) System.out.print("Teleport, ");
        if (agent.hasShield()) System.out.print("Shield, ");
        if (agent.canDoubleMove()) System.out.print("Double Move, ");
        if (!agent.hasPowerUp()) System.out.print("None");
        System.out.println();
        
        // Display status
        System.out.println("  Status: " + (agent.hasReachedGoal() ? "REACHED GOAL! 🏁" : "Still exploring"));
    }
    
    private String getAgentColor(int agentId) {
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

    
    public void startAutonomousGame() {
        System.out.println("\n=== Game Starting ===");
        System.out.println("Maze Size: " + maze.getWidth() + "x" + maze.getHeight());
        System.out.println("Agent Number: " + agents.length);
        System.out.println("Max Turns: " + maxTurns);
        System.out.println("\nPower-Up Distribution:");
        System.out.println("- Teleport (L): 25% ");
        System.out.println("- Shield (S): 35% ");
        System.out.println("- Double Move (D): 30% ");
        System.out.println("- Time Extend (X): 10% ");
        System.out.println("Goal Position: (" + maze.getGoalX() + "," + maze.getGoalY() + ")");
        
        // Display initial maze state
        System.out.println("\n=== Initial Maze State ===");
        maze.printMazeState();
        
        // Display corridors information
        maze.displayCorridorInfo();
        
        // Create AI controllers for each agent
        AIController[] aiControllers = new AIController[agents.length];
        for (int i = 0; i < agents.length; i++) {
            aiControllers[i] = new AIController(maze, agents[i]);
        }
    
        // Main game loop
        while (turnCount < maxTurns && !allAgentsFinished()) {
            turnCount++;
            
            // Check for corridor rotation (every 5 turns)
            if (turnCount % 5 == 0) {
                System.out.println("\n=== Turn " + turnCount + ": CORRIDOR ROTATION ===");
                maze.rotateRandomCorridor();
                maze.printMazeState();
                
                // Wait a bit longer to observe rotation effects
                try { 
                    Thread.sleep(1500); 
                } catch (InterruptedException e) { 
                    e.printStackTrace(); 
                }
            } else {
                System.out.println("\n=== Turn " + turnCount + " ===");
            }
            
            // Process current agent's turn
            Agent currentAgent = agents[currentAgentIndex];
            
            if (!currentAgent.hasReachedGoal()) {
                String move = aiControllers[currentAgentIndex].decideSmartMove();
                if (move.equals("BACKTRACK")) {
                    currentAgent.backtrack();
                    System.out.println("Agent " + (currentAgent.getId()+1) + " backtracked!");
                } else {
                    processAIMove(currentAgent, move);
                }
            }
    
            // Display updated maze state after the move
            maze.printMazeState();
            printAgentStatus(currentAgent);
            
            // Move to the next agent who hasn't reached the goal
            do {
                currentAgentIndex = (currentAgentIndex + 1) % agents.length;
            } while (agents[currentAgentIndex].hasReachedGoal() && !allAgentsFinished());
    
            // Add a delay for visual tracking
            try { 
                Thread.sleep(1000); 
            } catch (InterruptedException e) { 
                e.printStackTrace(); 
            }
        }
        
        
        printFinalResults();
        logGameSummaryToFile("game_summary.txt");
    }
     

private void processAIMove(Agent agent, String move) {
    int newX = agent.getCurrentX();
    int newY = agent.getCurrentY();
    
    switch (move) {
        case "UP": newY--; break;
        case "DOWN": newY++; break;
        case "LEFT": newX--; break;
        case "RIGHT": newX++; break;
        default: return;
    }
    
    // Check if valid move
    if (maze.getGrid()[newX][newY].isTraversable()) {
        // Update the agent's internal move count
        agent.move(move);
        
        // Update the position in the maze (which updates the grid)
        maze.updateAgentPosition(agent, newX, newY);
        System.out.println("Agent " + (agent.getId()+1) + " moved " + move);
        
        // Process tile effects
        maze.checkTileEffects(agent);
        
        // Double move logic (if the agent still has that power)
        if (agent.canDoubleMove()) {
            System.out.println("Agent " + (agent.getId()+1) + " uses double move!");
            
            // Calculate second move in same direction
            newX = agent.getCurrentX();
            newY = agent.getCurrentY();
            
            switch (move) {
                case "UP": newY--; break;
                case "DOWN": newY++; break;
                case "LEFT": newX--; break;
                case "RIGHT": newX++; break;
            }
            
            // Apply second move if valid
            if (newX >= 0 && newX < maze.getWidth() && 
                newY >= 0 && newY < maze.getHeight() &&
                maze.getGrid()[newX][newY].isTraversable()) {
                
                agent.move(move); // Count this as another move
                maze.updateAgentPosition(agent, newX, newY);
                System.out.println("Agent " + (agent.getId()+1) + " used second move: " + move);
                
                // Check effect of second move's tile too
                maze.checkTileEffects(agent);
            }
        }
        
        // Handle teleport power-up
        if (agent.canTeleport() && Math.random() < 0.3) {  // 30% chance to use teleport
            performTeleport(agent);
        }
    }
    
    // Decrement power-up effects at the end of the turn
    agent.decrementPowerUpEffects();
}


// Make sure the performTeleport method is correctly implemented
private void performTeleport(Agent agent) {
    if (!agent.canTeleport()) return;
    
    agent.useTeleport();
    System.out.println("Agent " + (agent.getId()+1) + " using teleport!");
    
    int newX, newY;
    int attempts = 0;
    do {
        newX = 1 + (int)(Math.random() * (maze.getWidth() - 2));
        newY = 1 + (int)(Math.random() * (maze.getHeight() - 2));
        attempts++;
    } while (!maze.getGrid()[newX][newY].isTraversable() && attempts < 50);
    
    if (attempts < 50) {
        // Clear agent from current position before teleporting
        maze.getGrid()[agent.getCurrentX()][agent.getCurrentY()].clearAgent();
        
        // Update position
        maze.updateAgentPosition(agent, newX, newY);
        System.out.println("Agent " + (agent.getId()+1) + " teleported to (" + newX + "," + newY + ")");
        
        // Check tile effects at the new position
        maze.checkTileEffects(agent);
    } else {
        System.out.println("Agent " + (agent.getId()+1) + " failed to teleport!");
    }
}

public void logGameSummaryToFile(String filename) {
    try (FileWriter writer = new FileWriter(filename)) {
        // Write header with timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        writer.write("=== Maze Escape Game Summary ===\n");
        writer.write("Generated on: " + dateFormat.format(new Date()) + "\n\n");
        
        // Game configuration
        writer.write("Game Configuration:\n");
        writer.write(String.format("- Maze Size: %dx%d\n", maze.getWidth(), maze.getHeight()));
        writer.write(String.format("- Number of Agents: %d\n", agents.length));
        writer.write(String.format("- Max Turns: %d\n", maxTurns));
        writer.write(String.format("- Goal Position: (%d,%d)\n\n", maze.getGoalX(), maze.getGoalY()));
        
        // Game outcome
        writer.write("Game Outcome:\n");
        writer.write(String.format("- Total Turns Played: %d\n", turnCount));
        
        // Goal achievement order
        List<Agent> goalAchievers = new ArrayList<>();
        for (Agent agent : agents) {
            if (agent.hasReachedGoal()) {
                goalAchievers.add(agent);
            }
        }
        goalAchievers.sort(Comparator.comparingInt(Agent::getGoalReachedTurn));
        
        writer.write("\nGoal Achievement Order:\n");
        if (goalAchievers.isEmpty()) {
            writer.write("No agents reached the goal!\n");
        } else {
            for (int i = 0; i < goalAchievers.size(); i++) {
                Agent a = goalAchievers.get(i);
                writer.write(String.format("%d. Player %d (Turn %d)\n", 
                    i+1, a.getId()+1, a.getGoalReachedTurn()));
            }
        }
        
        // Detailed statistics
        writer.write("\nDetailed Statistics:\n");
        writer.write("+--------+-------+-------+--------+------------+-------------+\n");
        writer.write("| Player | Moves | Traps | PwrUps | Backtracks | Goal Reached |\n");
        writer.write("+--------+-------+-------+--------+------------+-------------+\n");
        
        for (Agent agent : agents) {
            writer.write(String.format("| %-6d | %-5d | %-5d | %-6d | %-10d | %-11s |\n",
                agent.getId()+1,
                agent.getTotalMoves(),
                agent.getTrapsTriggered(),
                agent.getPowerUpsCollected(),
                agent.getBacktracks(),
                agent.hasReachedGoal() ? "Yes" : "No"));
        }
        writer.write("+--------+-------+-------+--------+------------+-------------+\n\n");
        
        
       
        
        writer.write("\n=== End of Summary ===\n");
        System.out.println("Game summary successfully written to " + filename);
    } catch (IOException e) {
        System.err.println("Error writing game summary to file: " + e.getMessage());
        e.printStackTrace();
    }
}


    

    

  
    
}