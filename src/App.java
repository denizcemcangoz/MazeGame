import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Maze Escape Game ===");
        System.out.println("Power-Up Info:");
        System.out.println("- L (Purple ⌧): Teleport - Teleporting to a random tile");
        System.out.println("- S (Blue ⛨): Shield - Immune to traps for 5 rounds");
        System.out.println("- D (Yellow ↝): Double Move - 2 times faster movement for 3 rounds");
        System.out.println("- X (Cyan ⏱): Time Extend - Increases the game rounds for player");
        System.out.println("\nPlease enter the maze size:");

        int width = getValidInput(scanner, "Width (5-30): ", 5, 30);
        int height = getValidInput(scanner, "Height (5-30): ", 5, 30);
        int numAgents = getValidInput(scanner, "Number of Agents (1-6): ", 1, 6);
        int maxTurns = 50 + (width * height / 5); // Labirent boyutuna göre otomatik ayar

        GameController game = new GameController(width, height, numAgents, maxTurns);
        game.startAutonomousGame();
        scanner.close();
    }


    private static int getValidInput(Scanner scanner, String prompt, int min, int max) {
        int value;
        do {
            System.out.print(prompt);
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid entry! Please enter a number.");
                scanner.next();
            }
            value = scanner.nextInt();
        } while (value < min || value > max);
        return value;
    }
    }

