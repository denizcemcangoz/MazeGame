# Maze Escape Game

A Java-based multiplayer maze game where agents navigate through a dynamically changing maze, collecting power-ups and avoiding traps while racing to reach the goal.

## Game Overview

In Maze Escape Game, AI-controlled agents compete to navigate a procedurally generated maze. The game features:

- Dynamically generated perfect mazes
- Rotating corridors that change the maze layout every 5 turns
- Various power-ups that grant special abilities
- Traps that impede progress
- Multiple AI agents competing to reach the goal first

## How to Run the Game

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- A terminal or command prompt that supports ANSI color codes

### Compilation

Open a terminal (Command Prompt on Windows, Terminal on macOS/Linux). 

Navigate to the folder where your .jar file is located: 

bash 

cd /path/to/jar 


### Running the Game

After compilation, run the game using:

bash 

java –jar MazeGame.jar 

## Game Setup

When you run the game, you'll be prompted to enter:

1. Maze width (5-30)
2. Maze height (5-30)
3. Number of agents (1-6)

The game will automatically calculate the maximum number of turns based on the maze size.

## Game Elements

### Maze Features

- **Walls (█)**: Obstacles that can't be passed through
- **Empty spaces ( )**: Traversable paths
- **Goal (◉)**: The destination all agents are trying to reach

### Power-ups

- **Teleport (⌧)**: Grants the ability to teleport to random locations (2 charges)
- **Shield (⛨)**: Provides immunity to traps for 5 turns
- **Double Move (↝)**: Allows moving twice as fast for 3 turns
- **Time Extend (⏱)**: Increases available game rounds

### Traps

- **Traps (X)**: Cause the agent to backtrack to their previous position unless they have a shield

## Gameplay Mechanics

- Agents take turns making moves
- Every 5 turns, a random corridor in the maze rotates, changing the layout
- When an agent reaches the goal, they're marked as finished and no longer take turns
- The game ends when all agents reach the goal or the maximum number of turns is reached

## Game Display

- Colored numbers (1-6) represent agents
- A detailed status display shows agent positions, power-ups, and progress
- After each turn, the updated maze is displayed
- When corridors rotate, the new maze layout is shown

## Game Summary

At the end of the game, a summary is displayed showing:
- Total turns taken
- Order in which agents reached the goal
- Detailed statistics for each agent (moves, traps triggered, power-ups collected, etc.)
- A summary is also written to a file named `game_summary.txt`

## Java Classes

- **App.java**: Main class with game initialization and user input handling
- **GameController.java**: Controls the game flow and turn management
- **MazeManager.java**: Handles maze generation and maintenance
- **MazeTile.java**: Represents individual maze cells and their properties
- **Agent.java**: Controls agent behavior and tracks their state
- **AIController.java**: Implements AI decision-making for agents
- **CircularCorridor.java**: Manages the rotating corridors
- **CustomQueue.java** and **CustomStack.java**: Custom data structures

## Example Game Session

1. Run `java MazeGame`
2. Enter maze dimensions (e.g., width: 15, height: 15)
3. Enter number of agents (e.g., 4)
4. Watch as the agents navigate the maze, competing to reach the goal first
5. Review the game summary at the end to see performance statistics

## Additional Notes

- The game utilizes ANSI color codes for visual representation, so make sure your terminal supports them
- For optimal display, use a terminal with a monospaced font
- The game creates a log file (`game_summary.txt`) in the same directory
