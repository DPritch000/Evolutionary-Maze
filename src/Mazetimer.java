import javax.swing.*;
import java.util.ArrayList;

public class Mazetimer {

    private Timer timer;
    public boolean runComplete = false;
    private long startTime;

    public void start(ArrayList<Runner> runners) {

        runComplete = false;
        startTime = System.currentTimeMillis();

        int delay = 120; // ~8 FPS (safe for Swing)

        timer = new Timer(delay, e -> {

            boolean allFinished = true;

            for (Runner r : runners) {

                // If runner is frozen but needs to decide
                if (r.isFrozen() && r.isDeciding()) {
                    char[] genome = r.getGenome();
                    int idx = r.getGenomePosition();

                    boolean decided = false;

                    int attempts = 0;
                    while (attempts < genome.length && !decided) {
                        char gene = genome[idx];
                        boolean valid = r.makeDecision(gene);

                        idx = (idx + 1) % genome.length; // always advance
                        attempts++;

                        if (valid) {
                            r.setFrozen(false);
                            r.setDeciding(false);
                            decided = true;
                        }
                    }

// update genome position even if no valid move was found
                    r.setGenomePosition(idx);
                    if (attempts >= genome.length && !decided) {
                        // No valid moves left → runner is stuck
                        r.deadEnd = true;
                        r.setFrozen(true);
                        r.setDeciding(false);
                    }
                }

                // If not frozen, move
                if (!r.isFrozen()) {
                    r.moveOneStep();
                }

                // Check if runner is done
                if (!r.deadEnd && !r.reachedGoal) {
                    allFinished = false;
                }

                // Print final time once
                if ((r.deadEnd || r.reachedGoal) && !r.printedFinalTime) {
                    double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
                    r.finalTime = elapsed;
                    r.printedFinalTime = true;
                    System.out.println("Runner " + r.index +
                            " finished: Time= " + elapsed + "s | Fitness= " + r.fitness);
                }
            }
            if(((System.currentTimeMillis() - startTime)/1000.0) > 20.0){
                runComplete = true;
                timer.stop();
            }
            if (allFinished) {
                runComplete = true;
                timer.stop();
            }
        });

        timer.start();
    }
}