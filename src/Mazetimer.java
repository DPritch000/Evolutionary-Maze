import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

public class Mazetimer {
    private Timer timer;
    int geneIndex = 0;

    public void start(ArrayList<Runner> runners) {
        timer = new Timer();

        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                for (Runner runner : runners) {
                    int x = runner.getX_pos();
                    int y = runner.getY_pos();
                    char gridPosition = runner.getGridPositionValue(x, y);

                    char[] genome = runner.getGenome();

                    while (runners.trimToSize((runners.getX()) + (runners.seeNextGridPosX), (runners.getY())) + (runners.seeNextGridPosY) == '1') {
            if (runners.getVelocity() == "positiveX") {
                runners.movePositiveX();

            if (runners.getVelocity() == "positiveY") {
                runners.movePositiveY();
            }
            if (runners.getVelocity() == "negativeX") {
                runners.moveNegativeX();
            }
            if (runners.getVelocity() == "negativeY") {
                runners.moveNegativeY();
            }
        }
                        }
                    } while (gridPosition == '5') {
                        char gene = genome[geneIndex];
                        runner.makeDecision(gene);
                        runner.setGenomePosition(geneIndex + 1);
                    }
                    geneIndex+=1;

                     // Ensure visual update
                }
            }
        };

        timer.scheduleAtFixedRate(updateTask, 0, 16); // ~60 FPS
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
}

//for (int i = 0; i < runners.size(); i++) {
//        char[] genome = runners.get(i).getGenome();
//        int x_pos = runners.get(i).getX_pos();
//        int y_pos = runners.get(i).getY_pos();
//        char gridPosition = runners.get(i).getGridPositionValue(x_pos, y_pos);
//
//        while (runners.get(i).getGridPositionValue((runners.get(i).getX()) + (runners.get(i).seeNextGridPosX), (runners.get(i).getY())) + (runners.get(i).seeNextGridPosY) == '1') {
//            if (runners.get(i).getVelocity() == "positiveX") {
//                runners.get(i).movePositiveX();
//            }
//            if (runners.get(i).getVelocity() == "positiveY") {
//                runners.get(i).movePositiveY();
//            }
//            if (runners.get(i).getVelocity() == "negativeX") {
//                runners.get(i).moveNegativeX();
//            }
//            if (runners.get(i).getVelocity() == "negativeY") {
//                runners.get(i).moveNegativeY();
//            }
//        }
//        if (gridPosition == '5') {
//            int geneIndex = runners.get(i).getGenomeIndex();
//            char gene = genome[geneIndex];
//            runners.get(i).makeDecision(gene);
//            while (runners.get(i).getGridPositionValue((runners.get(i).getX()) + (runners.get(i).seeNextGridPosX), (runners.get(i).getY())) + (runners.get(i).seeNextGridPosY) == '0') {
//                gene = genome[geneIndex + 1];
//                runners.get(i).makeDecision(gene);
//                geneIndex += 1;
//
//            }
//            geneIndex += 1;
