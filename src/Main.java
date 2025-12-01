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


        //Text Area For records
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane statsScroll = new JScrollPane(statsArea);
        statsScroll.setBounds(34 * 34 + 400, 10, 300, 400); // adjust position/size

        layeredPane.add(statsScroll, 1);

        // Maze
        Maze maze = new Maze();
        layeredPane.add(maze, Integer.valueOf(0));
        Runner.setPositionMap(maze.getGrid());

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
                    System.out.println();
                    System.out.println("=== GENERATION 1 ===");
                    Mazetimer timer = new Mazetimer();
                    timer.start(currentGen);

                    while (!timer.runComplete) {
                        Thread.sleep(50);

                    }
                    updateStats(currentGen, 1, statsArea, spawnCount);

                    // GEN 2+
                    for (int gen = 2; gen <= generations; gen++) {

                        while (!timer.runComplete) {
                            Thread.sleep(50);
                        }
                        SwingUtilities.invokeAndWait(() -> {
                        }); // flush EDT

                        System.out.println("\n=== GENERATION " + gen + " ===");

                        ArrayList<Runner> nextGen =
                                reproduce(currentGen, spawnCount, layeredPane);

                        Mazetimer t = new Mazetimer();
                        t.start(nextGen);

                        while (!t.runComplete) {
                            Thread.sleep(50);
                        }
                        int maxFitness = 0;
                        int maxIndex = 0;
                        currentGen = nextGen;
                        updateStats(currentGen, gen, statsArea, spawnCount);
                        if (gen == generations) {
                            for (int i = 0; i < spawnCount; i++) {
                                if (currentGen.get(i).getFitness() >= maxFitness) {
                                    maxFitness = currentGen.get(i).getFitness();
                                    maxIndex = i;
                                }
                            }
                            statsArea.append("Runner with Highest Fitness:\nRunner " + maxIndex + "\nGenome: " + new String(currentGen.get(maxIndex).getGenome()));
                        }

                    }

                    JOptionPane.showMessageDialog(frame,
                            "Simulation Complete! " + generations + " generations finished.");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid input. Enter integers only.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace(); // ✅ prints the REAL error in console
                    JOptionPane.showMessageDialog(frame,
                            "An unexpected error occurred. Check console for details.",
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
            int x_pix = 17;
            int y_pix = 17;

            r.placeAt(x_pix, y_pix);
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

        // Sort is optional now, since roulette wheel uses raw fitness
        // parents.sort((a, b) -> Integer.compare(b.fitness, a.fitness));

        // Precompute total fitness
        double totalFitness = 0;
        for (Runner r : parents) {
            totalFitness += r.fitness;
        }

        for (int i = 0; i < count; i++) {
            int randomIndex = rand.nextInt(count);
            Runner p1 = parents.get(getMaxFitnessIndex(parents, count));
            Runner p2 = selectMate(parents);

            // Ensure p1 is the fitter one (optional)
            if (p2.fitness > p1.fitness) {
                Runner tmp = p1;
                p1 = p2;
                p2 = tmp;
            }

            Runner child = new Runner();
            child.index = i + 1;

            // Crossover
            int cut = p1.genome.length / 2;
            for (int j = 0; j < cut; j++) child.genome[j] = p1.genome[j];
            for (int j = cut; j < p1.genome.length; j++) child.genome[j] = p2.genome[j];

            // children.add(child);


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

        for (Runner r : parents) {
            r.deadEnd = true; // or reachedGoal if appropriate
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

    public static void updateStats(ArrayList<Runner> generation, int genNumber, JTextArea statsArea, int spawnCount) {
        int maxFitness = generation.get(0).getFitness();
        int reachedGoal = 0;
        for (int i = 0; i < spawnCount; i++) {
            if (generation.get(i).getFitness() > maxFitness) {
                maxFitness = generation.get(i).getFitness();
            }
            if (generation.get(i).getIfReachedGoal()) {
                reachedGoal++;
            }
        }

        statsArea.append("=== Generation " + genNumber + " ===\n");
        statsArea.append("Reached Goal: " + reachedGoal + "/" + generation.size() + "\n");
        statsArea.append("Highest Fitness: " + maxFitness + "\n");

        statsArea.append("\n");
    }

    // Roulette wheel selection helper
    private static Runner selectMate(ArrayList<Runner> parents) {
        int totalFitness = 0;
        for (Runner r : parents) {
            totalFitness += r.getFitness();
        }

        Random rand = new Random();
        if (totalFitness <= 0) {
            return parents.get(rand.nextInt(parents.size()));
        }

        int randomNum = rand.nextInt(totalFitness);
        int cumulative = 0;
        for (Runner r : parents) {
            cumulative += r.getFitness();
            if (cumulative > randomNum) {
                return r;
            }
        }
        return parents.get(parents.size() - 1);
    }

   private static int getMaxFitnessIndex(ArrayList<Runner> parents, int spawnCount) {
        int index = 0;
        int maxFitness = parents.get(0).getFitness();;
        for (int i = 0; i < spawnCount; i++) {

            if (parents.get(i).getFitness() > maxFitness) {
                maxFitness = parents.get(i).getFitness();
                index = i;
            }
        }
        return  index;
    }
}