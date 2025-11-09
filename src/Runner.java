import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class Runner extends JPanel {
    public Runner() {
        setBounds(0, 0, 1920, 1080);
        setOpaque(false);
    }

    char right = 'R';
    char left = 'L';
    Color uniqueColor = colorGenerate();
    public char[] genome = createGenome(100);
    public int fitness;

    public int x_pos;
    public int y_pos;
    Maze maze = new Maze();
    char[][] positionMap = maze.getGrid();
    char gridPositionValue = positionMap[0][0];
    String velocity = "positiveX";
    public int genomeIndexPosition = 0;

    int seeNextGridPosX = 34;
    int seeNextGridPosY = 34;

    public static Color colorGenerate() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return new Color(r, g, b);
    }

    public static char[] createGenome(int length) {
        char[] genome = new char[length];
        for (int i = 0; i < length; i++) {
            double randomNum = Math.floor(Math.random() * 3) + 1;
            if (randomNum == 1) {
                genome[i] = 'R';
            } else if (randomNum == 2) {
                genome[i] = 'L';
            } else if (randomNum == 3) {
                genome[i] = 'F';
            }
        }
        return genome;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int tileSize = 34;
        g.setColor(uniqueColor);
        g.fillRect(0, 0, tileSize / 4, tileSize / 4);
    }

    // Updated setters with repaint
    public void movePositiveX() {
         velocity = "positiveX";
        x_pos += 17;
        seeNextGridPosX = 34;
        setBounds(x_pos,y_pos,34/4,34/4);

        repaint();
    }

    public void movePositiveY() {
         velocity = "positiveY";
        y_pos += 17;
        seeNextGridPosY = 34;
        setBounds(x_pos,y_pos,34/4,34/4);
        repaint();
    }

    public void moveNegativeX() {
         velocity = "negativeX";
        x_pos -= 17;
        seeNextGridPosX = -34;
        setBounds(x_pos,y_pos,34/4,34/4);
        repaint();
    }

    public void moveNegativeY() {
         velocity = "negativeY";
        y_pos -= 17;
        seeNextGridPosY = -34;
        setBounds(x_pos,y_pos,34/4,34/4);
        repaint();
    }

    public char getGridPositionValue(int x_pos, int y_pos) {
        int x = (int) Math.ceil(x_pos / 34);
        int y = (int) Math.ceil(y_pos / 34);
        gridPositionValue = positionMap[x][y];

        return gridPositionValue;
    }

    public void makeDecision(char gene) {
        if (velocity == "positiveX") {
            if (gene == 'R') {
                moveNegativeY();
                seeNextGridPosX = 0;
            } else if (gene == 'L') {
                movePositiveY();
                seeNextGridPosX = 0;
            } else if (gene == 'F') {
                movePositiveX();
                seeNextGridPosY = 0;
            }

        } else if (velocity == "positiveY") {
            if (gene == 'R') {
                movePositiveX();
                seeNextGridPosY = 0;
            } else if (gene == 'L') {
                moveNegativeY();
                seeNextGridPosX = 0;
            } else if (gene == 'F') {
                movePositiveY();
                seeNextGridPosX = 0;
            }
        } else if (velocity == "negativeX") {
            if (gene == 'R') {
                movePositiveY();
                seeNextGridPosX = 0;
            } else if (gene == 'L') {
                moveNegativeY();
                seeNextGridPosX = 0;
            } else if (gene == 'F') {
                moveNegativeX();
                seeNextGridPosY = 0;
            }
        } else if (velocity == "negativeY") {
            if (gene == 'R') {
                moveNegativeX();
                seeNextGridPosY = 0;
            } else if (gene == 'L') {
                movePositiveX();
                seeNextGridPosY = 0;
            } else if (gene == 'F') {
                moveNegativeY();
                seeNextGridPosX = 0;
            }
        }
    }

    public void setX(int x) {
        x_pos = x;
    }

    public void setY(int y) {
        y_pos = y;
    }

    public int getX_pos() {
        return x_pos;
    }

    public int getY_pos() {
        return y_pos;
    }

    public char[] getGenome() {
        return genome;
    }

    public int getGenomeIndex() {
        return genomeIndexPosition;
    }

    public void setGenomePosition(int i) {
         genomeIndexPosition = i;
    }

    public String getVelocity() {
        return velocity;
    }
}






    /*
    // Updated getters to match custom position
    @Override
    public int getX() {
        return x_pos;
    }

    @Override
    public int getY() {
        return y_pos;
    }


    private boolean pausedForGenome = false;

    private char direction = 'R'; // R = right, L = left, U = up, D = down

    private int genomeIndex = 0;

   public void moveByGenome(char[][] mazeGrid) {
        if (genomeIndex >= genome.length) return;

        char gene = genome[genomeIndex];
        genomeIndex++;

        if (gene == 'R') {
            if (direction == 'U') direction = 'R';
            else if (direction == 'R') direction = 'D';
            else if (direction == 'D') direction = 'L';
            else if (direction == 'L') direction = 'U';
        } else if (gene == 'L') {
            if (direction == 'U') direction = 'L';
            else if (direction == 'L') direction = 'D';
            else if (direction == 'D') direction = 'R';
            else if (direction == 'R') direction = 'U';
        } else if (gene == 'F') {
            if (direction == 'R') setX(getX() + 1);
            else if (direction == 'L') setX(getX() - 1);
            else if (direction == 'U') setY(getY() - 1);
            else if (direction == 'D') setY(getY() + 1);
        }

        pausedForGenome = false; // resume generic movement

        repaint();
    }
    public boolean isPausedForGenome() {
        return pausedForGenome;
    }

    public void pauseForGenome() {
        pausedForGenome = true;
    }
*/