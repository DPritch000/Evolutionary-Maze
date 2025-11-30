import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main extends JPanel {

    public static void main(String[] args) {

        //JFame and visuals will go here
        JFrame frame = new JFrame("Maze Viewer");

        //For Text Fields
        JTextField spawnCountField;
        JTextField generationCountField;
        JTextField simulationSpeed;
        JButton startButton;

        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JLayeredPane layeredPane = new JLayeredPane();
        frame.add(layeredPane);
        layeredPane.setPreferredSize(new Dimension(1920, 1080));

        //Add Labels and Fields
        JLabel spawnCountLabel = new JLabel("SpawnCount:");
        spawnCountLabel.setBounds(34 * 34 + 250, 10, 100, 50);
        spawnCountField = new JTextField();
        spawnCountField.setBounds(34 * 34 + 250, 30, 100, 50);
        JLabel generationCountLabel = new JLabel("Generations:");
        generationCountLabel.setBackground(Color.white);
        generationCountLabel.setForeground(Color.black);
        generationCountLabel.setBounds(34 * 34 + 250, 80, 100, 50);
        generationCountField = new JTextField();
        generationCountField.setBounds(34 * 34 + 250, 100, 100, 50);
        startButton = new JButton("Start");
        startButton.setBounds(34 * 34 + 250, 200, 100, 30);

        //JLabel simulationSpeedLabel = new JLabel("Simulation Speed (fps)");
        layeredPane.setLayout(null);
        layeredPane.add(spawnCountLabel, 1);
        layeredPane.add(spawnCountField, 1);
        layeredPane.add(generationCountField, 1);
        layeredPane.add(generationCountLabel, 1);
        layeredPane.add(startButton, 1);

        // Maze
        Maze maze = new Maze();
        layeredPane.add(maze, Integer.valueOf(0));

        frame.add(layeredPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Start button logic
        startButton.addActionListener(e -> {

            new Thread(() -> {

                try {
                    int spawnCount = Integer.parseInt(spawnCountField.getText());
                    int generations = Integer.parseInt(generationCountField.getText());

                    ArrayList<Runner> currentGen = spawnRunners(spawnCount, layeredPane);

                    // GEN 1
                    System.out.println("=== GENERATION 1 ===");
                    Mazetimer timer = new Mazetimer();
                    timer.start(currentGen);

                    while (!timer.runComplete) {
                        Thread.sleep(50);
                    }

                    // GEN 2+
                    for (int gen = 2; gen <= generations; gen++) {

                        System.out.println("\n=== GENERATION " + gen + " ===");

                        ArrayList<Runner> nextGen =
                                reproduce(currentGen, spawnCount, layeredPane);

                        Mazetimer t = new Mazetimer();
                        t.start(nextGen);

                        while (!t.runComplete) {
                            Thread.sleep(50);
                        }

                        currentGen = nextGen;
                    }

                    JOptionPane.showMessageDialog(frame,
                            "Simulation Complete! " + generations + " generations finished.");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid input. Enter integers only.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            }).start();
        });
    }

    // --- SPAWN RUNNERS ---
    public static ArrayList<Runner> spawnRunners(int count, JLayeredPane pane) {

        ArrayList<Runner> list = new ArrayList<>();

        for (int i = 1; i <= count; i++) {

            Runner r = new Runner();
            r.index = i;

            int tileSize = 34;
            int x_pix = tileSize / 2;
            int y_pix = 17 + (int) ((Math.random() * (20)) - 10);
            r.setX(x_pix);
            r.setY(y_pix);

            pane.add(r, Integer.valueOf(1000 + i));

            System.out.println("Runner " + i + " genome: " + Arrays.toString(r.genome));

            list.add(r);
        }

        pane.repaint();
        return list;
    }

    // --- REPRODUCTION ---
    public static ArrayList<Runner> reproduce(ArrayList<Runner> parents,
                                              int count, JLayeredPane pane) {

        ArrayList<Runner> children = new ArrayList<>();
        Random rand = new Random();

        parents.sort((a, b) -> Integer.compare(b.fitness, a.fitness));

        for (int i = 0; i < count; i++) {
            Runner p1 = parents.get(rand.nextInt(Math.min(10, parents.size())));
            Runner p2 = parents.get(rand.nextInt(Math.min(10, parents.size())));

            if (p2.fitness > p1.fitness) {
                Runner tmp = p1; p1 = p2; p2 = tmp;
            }

            Runner child = new Runner();
            child.index = i + 1;

            // Crossover
            int cut = rand.nextInt(p1.genome.length);
            for (int j = 0; j < cut; j++) child.genome[j] = p1.genome[j];
            for (int j = cut; j < p1.genome.length; j++) child.genome[j] = p2.genome[j];

            // Mutation
            mutate(child.genome);

            // Reset state
            int tileSize = 34;
            int x_pix = tileSize / 2;
            int y_pix = 17 + (int) ((Math.random() * (20)) - 10);
            child.setX(x_pix);
            child.setY(y_pix);
            child.fitness = 0;
            child.deadEnd = false;
            child.reachedGoal = false;
            child.printedFinalTime = false;
            child.finalTime = -1;
            child.setFrozen(false);
            child.setDeciding(false);
            child.setGenomePosition(0);

            pane.add(child, Integer.valueOf(2000 + i));
            children.add(child);
        }

        // Remove old runners
        for (Runner r : parents) pane.remove(r);

        pane.repaint();
        return children;
    }

    // --- MUTATION ---
    public static void mutate(char[] genome) {
        Random rand = new Random();
        double rate = 0.1;

        for (int i = 0; i < genome.length; i++) {
            if (rand.nextDouble() < rate) {
                genome[i] = "RLF".charAt(rand.nextInt(3));
            }
        }
    }
}