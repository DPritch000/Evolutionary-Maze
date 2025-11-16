import javax.swing.*;
import java.util.ArrayList;

public class Mazetimer {
    private javax.swing.Timer timer;

    public void start(ArrayList<Runner> runners, int spawnCount, JLayeredPane layeredPane) {
        int delay = 100; // one frame every 100 ms (~10 FPS)

        timer = new javax.swing.Timer(delay, e -> {
            for (Runner r : runners) {

                int x = r.getX_pos();
                int y = r.getY_pos();
                char currentTile = r.getGridPositionValue(x, y);

                // if we hit a decision tile and not frozen, freeze and start thinking
                if (r.getGridPositionValue(r.getX_pos(), r.getY_pos()) == '5') {
                    if (!r.isFrozen()) {
                        r.setFrozen(true);
                        r.setDeciding(true);
                    }
                }

                if (r.isFrozen() && r.isDeciding()) {
                    char[] genome = r.getGenome();
                    int idx = r.getGenomePosition();

                    if (idx < genome.length) {
                        char gene = genome[idx];
                        boolean valid = r.makeDecision(gene, layeredPane);
                        if (!valid) {
                            r.setGenomePosition(idx + 1); // try next gene next frame
                        } else {
                            r.setGenomePosition(idx + 1);
                        }
                    } else {
                        r.setGenomePosition(0);
                    }
                }

                if (!r.isFrozen()) {
                    r.moveOneStep(layeredPane);
                }

                r.repaint();
            }
        });

        timer.start();
    }
}

