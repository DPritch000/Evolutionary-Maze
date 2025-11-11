import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.*;

public class Main extends JPanel {
    public static void main(String[] args) {

        //JFame and visuals will go
        JFrame frame = new JFrame("Maze Viewer");
        frame.setResizable(true);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        frame.add(layeredPane);
        layeredPane.setPreferredSize(new Dimension(1920, 1080));

        int spawnCount = 100;
        Maze maze = new Maze();
        layeredPane.add(maze, Integer.valueOf(0));
        ArrayList<Runner> gen1 = spawn(maze, frame, layeredPane, spawnCount);
        Mazetimer mazetimer = new Mazetimer();
        mazetimer.start(gen1, spawnCount, layeredPane);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        for (int i = 0; i < maze.getGrid().length; i++) {
            for (int j = 0; j < maze.getGrid()[i].length; j++) {
                System.out.print(maze.getGrid()[i][j] + " ");
            }
            System.out.println(); // new line after each row
        }
    }

    public static ArrayList<Runner> spawn(Maze maze, JFrame frame, JLayeredPane pane, int spawnCount) {  //spawns runners
        ArrayList<Runner> runners = new ArrayList<>();
        for (int i = 1; i <= spawnCount; i++) {
            Runner runner = new Runner();
            runners.add(runner);


            int tileSize = 34;
            int x_pix = tileSize / 2;
            int y_pix = 17 + (int) ((Math.random() * (20)) - 10);

            runner.setX(x_pix);
            runner.setY(y_pix);

            pane.add(runner, Integer.valueOf(i));

            System.out.println("Runner" + i + "     Genome: " + Arrays.toString(runner.genome) + "     Color: " + runner.uniqueColor);
        }
        return runners;

    }

    public static ArrayList<Runner> reproduction(ArrayList<Runner> parents, int spawnCount) {
        Random rand1 = new Random();
        int randomNumber1 = rand1.nextInt(spawnCount); //MAKE THIS WEIGHTED BASED ON FITNESS LATER
        Random rand2 = new Random();
        int randomNumber2 = rand2.nextInt(spawnCount);
        Runner parent1 = parents.get(randomNumber1);
        Runner parent2 = parents.get(randomNumber2);

        int length = parent1.genome.length;
        int crossoverPoint = rand1.nextInt(length);

        Runner child1 = null;
        Runner child2 = null;

        //For Child1
// First half from parent1
        for (int i = 0; i < crossoverPoint; i++) {
            child1.genome[i] = parent1.genome[i];
        }

        // Second half from parent2
        for (int i = crossoverPoint; i < length; i++) {
            child1.genome[i] = parent2.genome[i];
        }

        mutate(child1.genome);  // Using the mutation Method

        //Chosing Color
        Random rand3 = new Random();
        boolean trueOrFalse = rand3.nextBoolean();
        if (trueOrFalse == true) {
            child1.uniqueColor = parent1.uniqueColor;
        } else if (trueOrFalse == false) {
            child1.uniqueColor = parent2.uniqueColor;
        }

        //For Child 2

        for (int i = 0; i < crossoverPoint; i++) {
            child2.genome[i] = parent2.genome[i];
        }

        for (int i = crossoverPoint; i < length; i++) {
            child2.genome[i] = parent1.genome[i];
        }
        mutate(child2.genome);

        parents.set(randomNumber1, child1);
        parents.set(randomNumber2, child2);

        //Choosing Color

        Random rand4 = new Random();
        boolean trueOrFalse2 = rand4.nextBoolean();
        if (trueOrFalse2 == true) {
            child2.uniqueColor = parent1.uniqueColor;
        } else if (trueOrFalse2 == false) {
            child2.uniqueColor = parent2.uniqueColor;
        }

        return parents;

    }

    public static char[] mutate(char[] genome) {  // adds chance of mutation to genome
        double mutationRate = 0.1;
        Random rand = new Random();
        for (int i = 0; i <= genome.length; i++)
            if (rand.nextDouble() < mutationRate) {
                if (genome[i] == 'R') {
                    double randomNum1 = Math.floor(Math.random() * 2) + 1;
                    if (randomNum1 == 1) {
                        genome[i] = 'L';
                    } else if (randomNum1 == 2) {
                        genome[i] = 'F';
                    }
                } else if (genome[i] == 'L') {
                    double randomNum2 = Math.floor(Math.random() * 2) + 1;
                    if (randomNum2 == 1) {
                        genome[i] = 'R';
                    } else if (randomNum2 == 2) {
                        genome[i] = 'F';
                    }
                } else if (genome[i] == 'F') {
                    double randomNum3 = Math.floor(Math.random() * 2) + 1;
                    if (randomNum3 == 1) {
                        genome[i] = 'L';
                    } else if (randomNum3 == 2) {
                        genome[i] = 'R';
                    }
                }
            }
        return genome;
    }
    // Create Spawning Children Loop Method

    public int evaluateFitness(java.util.List<Point> path, char[][] map) {
        int fitness = 0;
        boolean reachedGoal = false;

        for (Point p : path) {
            int x = p.x;
            int y = p.y;

            char tile = map[y][x];

            switch (tile) {
                case '1': // path
                    fitness += 1;
                    break;
                case '2': // finish
                    fitness += 1000; // big reward for reaching goal
                    reachedGoal = true;
                    break;
                case '3': // dead end (represented as -1 in char map)
                    fitness -= 500;
                    break;
            }
        }
/*
        // Bonus for reaching the goal early
        if (reachedGoal) {
            fitness += 50 - path.size(); // shorter paths are better
        }
*/
        System.out.println(fitness);
        return fitness;
    }
}
