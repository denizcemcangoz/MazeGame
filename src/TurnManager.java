public class TurnManager {
    private CustomQueue agentQueue;
    private int currentRound;
    private StringBuilder gameLog;

    public TurnManager(Agent[] agents) {
        this.agentQueue = new CustomQueue();
        this.currentRound = 0;
        this.gameLog = new StringBuilder();
        
        for (Agent agent : agents) {
            agentQueue.enqueue(agent);
        }
    }

    public void advanceTurn() {
        currentRound++;
        rotateAgents();
    }

    private void rotateAgents() {
        // Move the first agent to the end
        if (!agentQueue.isEmpty()) {
            Agent first = agentQueue.dequeue();
            agentQueue.enqueue(first);
        }
    }

    public Agent getCurrentAgent() {
        return agentQueue.peek();
    }

    public boolean allAgentsFinished() {
        return agentQueue.allAgentsFinished(); // Delegate to CustomQueue's method
    }

    

    public void logTurnSummary(Agent a, String action) {
        String logEntry = String.format("Turn %d - Agent %d: %s\n", 
            currentRound, a.getId(), action);
        gameLog.append(logEntry);
        System.out.print(logEntry);
    }

    public String getGameLog() {
        return gameLog.toString();
    }

    public int getCurrentRound() {
        return currentRound;
    }
}