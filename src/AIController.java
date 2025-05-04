import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class AIController {
    private final MazeManager maze;
    private final Agent agent;

    public AIController(MazeManager maze, Agent agent) {
        this.maze = maze;
        this.agent = agent;
    }

    public String decideSmartMove() {
        int startX = agent.getCurrentX();
        int startY = agent.getCurrentY();
        int goalX = maze.getGoalX();
        int goalY = maze.getGoalY();

        // BFS için kuyruk ve ziyaret takibi
        Queue<Node> queue = new LinkedList<>();
        boolean[][] visited = new boolean[maze.getWidth()][maze.getHeight()];
        Node[][] parents = new Node[maze.getWidth()][maze.getHeight()];

        queue.add(new Node(startX, startY));
        visited[startX][startY] = true;

        int[][] directions = {
                {0, -1}, // UP
                {1, 0},  // RIGHT
                {0, 1},  // DOWN
                {-1, 0}  // LEFT
        };

        // BFS döngüsü
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.x == goalX && current.y == goalY) {
                break;
            }

            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                if (nx >= 0 && nx < maze.getWidth() && ny >= 0 && ny < maze.getHeight()) {
                    if (!visited[nx][ny] && maze.isValidMove(current.x, current.y, getDirection(dir))) {
                        visited[nx][ny] = true;
                        queue.add(new Node(nx, ny));
                        parents[nx][ny] = current;
                    }
                }
            }
        }

        // Geriye doğru izleyerek yolu çıkar
        List<Node> path = new ArrayList<>();
        Node step = new Node(goalX, goalY);
        while (step != null && !(step.x == startX && step.y == startY)) {
            path.add(step);
            step = parents[step.x][step.y];
        }

        if (path.isEmpty()) {
            return "BACKTRACK"; // Ulaşamıyorsa
        }

        Node nextStep = path.get(path.size() - 1);
        return getDirectionToMove(startX, startY, nextStep.x, nextStep.y);
    }

    // Yardımcı sınıf: Koordinatları tutar
    private static class Node {
        int x, y;
        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // Vektörel yönü metne çevir
    private String getDirection(int[] dir) {
        if (Arrays.equals(dir, new int[]{0, -1})) return "UP";
        if (Arrays.equals(dir, new int[]{1, 0})) return "RIGHT";
        if (Arrays.equals(dir, new int[]{0, 1})) return "DOWN";
        if (Arrays.equals(dir, new int[]{-1, 0})) return "LEFT";
        return "";
    }

    // İki koordinat arasındaki farktan yön çıkar
    private String getDirectionToMove(int fromX, int fromY, int toX, int toY) {
        if (toX == fromX && toY == fromY - 1) return "UP";
        if (toX == fromX && toY == fromY + 1) return "DOWN";
        if (toX == fromX + 1 && toY == fromY) return "RIGHT";
        if (toX == fromX - 1 && toY == fromY) return "LEFT";
        return "BACKTRACK"; // Hata durumu
    }
}