import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.*;

public class Main extends JPanel {
    public static void main(String[] args){

        //JFame and visuals will go
        JFrame frame = new JFrame("Maze Viewer");
        frame.setResizable(true);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1920,1080));


        Maze maze = new Maze();
        layeredPane.add(maze, Integer.valueOf(0));
        ArrayList<Runner> gen1 = spawn(maze,frame, layeredPane, 100);
        frame.add(layeredPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);




        //This section of code is for demo
        System.out.println(Runner.createGenome(10));

/*
        ArrayList<Evolution> gen2 = spawn(100);

        for(int i=0; i<100; i++){                                 // for loop to create gen 2
            gen2.add(reproduction(gen1,100));
        }
        for(int j=0;j<100;j++){
            System.out.println("Runner"+j+ "     Genome: "+ Arrays.toString(Evolution.gen2.genome)+ "     Color: " + Evolution.gen2.uniqueColor);
        }
*/
        int tileSize = 34;
        int x_pix= tileSize/2;
        int y_pix = tileSize/2;

    }
    public static ArrayList<Runner> spawn(Maze maze, JFrame frame, JLayeredPane pane, int spawnCount) {  //spawns runners
        ArrayList<Runner> runners = new ArrayList<>();
        for(int i=1; i<=spawnCount; i++){
            Runner runner = new Runner();
            runners.add(runner);


            int tileSize = 34;
            int x_pix= tileSize/2;
            int y_pix =  17 + (int)((Math.random()*(20))-10);

            runner.setX(x_pix);
            runner.setY(y_pix);


            pane.add(runner, Integer.valueOf(i));








            System.out.println("Runner"+i+ "     Genome: "+ Arrays.toString(runner.genome)+ "     Color: " + runner.uniqueColor);
        }
        return runners;
    }
    public static ArrayList<Runner> reproduction(ArrayList<Runner> parents, int spawnCount){
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
        if( trueOrFalse== true){
            child1.uniqueColor=parent1.uniqueColor;
        }
        else if(trueOrFalse == false ){
            child1.uniqueColor=parent2.uniqueColor;
        }

        //For Child 2

        for (int i = 0; i < crossoverPoint; i++){
            child2.genome[i] = parent2.genome[i];
        }

        for (int i = crossoverPoint; i < length; i++) {
            child2.genome[i] = parent1.genome[i];
        }
        mutate(child2.genome);

        parents.set(randomNumber1,child1);
        parents.set(randomNumber2,child2);

        //Choosing Color

        Random rand4 = new Random();
        boolean trueOrFalse2 = rand4.nextBoolean();
        if( trueOrFalse2== true){
            child2.uniqueColor=parent1.uniqueColor;
        }
        else if(trueOrFalse2 == false ){
           child2.uniqueColor=parent2.uniqueColor;
        }

        return parents;


    }

    public static char[] mutate(char[] genome){  // adds chance of mutation to genome
        double mutationRate= 0.1;
        Random rand = new Random();
        for(int i=0; i<= genome.length;i++)
        if(rand.nextDouble() < mutationRate){
            if (genome[i] == 'R'){
                genome[i] = 'L';
            }
            else if(genome[i]=='L'){
                genome[i] = 'R';
            }
        }
        return genome;
    }

    // Create Spawning Children Loop Method
/*
    public int evaluateFitness(List<Point> path, char[][] map) {
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
                    fitness -= 50;
                    break;
            }
        }

        // Bonus for reaching the goal early
        if (reachedGoal) {
            fitness += 50 - path.size(); // shorter paths are better
        }

        return fitness;
    }
*/
 public void Start(Maze maze){

     char[][] positionMap = maze.getGrid();
     //Testing for time
     int tileSize = 34;
     int x_pix= tileSize/2;
     int y_pix = tileSize/2;

     int gridPositionValue = (int) positionMap[0][0];


     // Make something update x_pos and y_pos


     Timer timer = new Timer();
     TimerTask task = new TimerTask() {
         @Override
         public void run() {

         }
     };
     timer.schedule(task,10);    //timer.schedule needs a task and a time in milliseconds

 }
 public void update(char[] genome){



 }


}
