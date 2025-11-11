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
    int geneIndex = 0;
    private boolean deciding = false; // true while deciding

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
    private boolean frozen = false;
    private int genomePosition = 0;
    private String lastVelocity = "";


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
    public void movePositiveX(JLayeredPane layeredPane) {
        velocity = "positiveX";
        x_pos += 17;
        seeNextGridPosX = 34;
        setBounds(x_pos, y_pos, 34 / 4, 34 / 4);

        layeredPane.repaint();
    }

    public void movePositiveY(JLayeredPane layeredPane) {
        velocity = "positiveY";
        y_pos += 17;
        seeNextGridPosY = 34;
        setBounds(x_pos, y_pos, 34 / 4, 34 / 4);
        layeredPane.repaint();
    }

    public void moveNegativeX(JLayeredPane layeredPane) {
        velocity = "negativeX";
        x_pos -= 17;
        seeNextGridPosX = -34;
        setBounds(x_pos, y_pos, 34 / 4, 34 / 4);
        layeredPane.repaint();
    }

    public void moveNegativeY(JLayeredPane layeredPane) {
        velocity = "negativeY";
        y_pos -= 17;
        seeNextGridPosY = -34;
        setBounds(x_pos, y_pos, 34 / 4, 34 / 4);
        layeredPane.repaint();
    }

    public char getGridPositionValue(int x_pos, int y_pos) {
        int tileX = x_pos / 34;
        int tileY = y_pos / 34;

        // Safety clamp: keep indices within bounds
        if (tileX < 0 || tileY < 0 ||
                tileY >= positionMap.length || tileX >= positionMap[0].length) {
            return ' '; // return a blank space or wall for out-of-bounds
        }

        return positionMap[tileY][tileX]; // Note: row-major order [y][x]
    }

    // Decide the next velocity based on the current gene, without moving
    public void decideNextMove(char gene) {
        switch (velocity) {
            case "positiveX" -> {
                switch (gene) {
                    case 'R' -> velocity = "negativeY";
                    case 'L' -> velocity = "positiveY";
                    case 'F' -> velocity = "positiveX";
                }
            }
            case "positiveY" -> {
                switch (gene) {
                    case 'R' -> velocity = "positiveX";
                    case 'L' -> velocity = "negativeX";
                    case 'F' -> velocity = "positiveY";
                }
            }
            case "negativeX" -> {
                switch (gene) {
                    case 'R' -> velocity = "positiveY";
                    case 'L' -> velocity = "negativeY";
                    case 'F' -> velocity = "negativeX";
                }
            }
            case "negativeY" -> {
                switch (gene) {
                    case 'R' -> velocity = "negativeX";
                    case 'L' -> velocity = "positiveX";
                    case 'F' -> velocity = "negativeY";
                }
            }
        }

        // Reset offsets for next grid check
        seeNextGridPosX = 0;
        seeNextGridPosY = 0;
    }

    // Move one step according to current velocity

    public void moveOneStep(JLayeredPane pane) {
        switch (velocity) {
            case "positiveX" -> movePositiveX(pane);
            case "positiveY" -> movePositiveY(pane);
            case "negativeX" -> moveNegativeX(pane);
            case "negativeY" -> moveNegativeY(pane);
        }
    }

    // --- Genome & Decisions ---
    public boolean makeDecision(char gene, JLayeredPane layeredPane) {
        int step = 34;
        int nextX = x_pos;
        int nextY = y_pos;
        String newVelocity = velocity;

        // Predict next direction based on gene
        switch (velocity) {
            case "positiveX":
                if (gene == 'R') newVelocity = "positiveY";
                else if (gene == 'L') newVelocity = "negativeY";
                else if (gene == 'F') newVelocity = "positiveX";
                break;
            case "positiveY":
                if (gene == 'R') newVelocity = "positiveX";
                else if (gene == 'L') newVelocity = "negativeX";
                else if (gene == 'F') newVelocity = "positiveY";
                break;
            case "negativeX":
                if (gene == 'R') newVelocity = "negativeY";
                else if (gene == 'L') newVelocity = "positiveY";
                else if (gene == 'F') newVelocity = "negativeX";
                break;
            case "negativeY":
                if (gene == 'R') newVelocity = "negativeX";
                else if (gene == 'L') newVelocity = "positiveX";
                else if (gene == 'F') newVelocity = "negativeY";
                break;
        }

        // Prevent reversing
        if (isReverse(newVelocity)) {
            return false; // invalid decision, try next gene
        }

        // Compute next tile based on newVelocity
        switch (newVelocity) {
            case "positiveX" -> nextX += step;
            case "negativeX" -> nextX -= step;
            case "positiveY" -> nextY += step;
            case "negativeY" -> nextY -= step;
        }

        char nextTile = getGridPositionValue(nextX, nextY);

        if (nextTile == '0' || nextTile == ' ') return false; // wall or invalid

        // valid decision → update velocity and position
        lastVelocity = velocity;
        velocity = newVelocity;
        x_pos = nextX;
        y_pos = nextY;
        setBounds(x_pos, y_pos, 34 / 4, 34 / 4);
        layeredPane.repaint();

        return true;
    }

    // --- Getters & Setters ---
    public char[] getGenome() {
        return genome;
    }

    public int getGenomePosition() {
        return genomePosition;
    }

    public void setGenomePosition(int genomePosition) {
        this.genomePosition = genomePosition;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isDeciding() {
        return deciding;
    }

    public void setDeciding(boolean deciding) {
        this.deciding = deciding;
    }

    public String getVelocity() {
        return velocity;
    }

    public void setVelocity(String v) {
        this.velocity = v;
    }

    public int getX_pos() {
        return x_pos;
    }

    public int getY_pos() {
        return y_pos;
    }

    public void setX(int x) {
        x_pos = x;
    }

    public void setY(int y) {
        y_pos = y;
    }

    private boolean isReverse(String newDir) {
        return (lastVelocity.equals("positiveX") && newDir.equals("negativeX")) ||
                (lastVelocity.equals("negativeX") && newDir.equals("positiveX")) ||
                (lastVelocity.equals("positiveY") && newDir.equals("negativeY")) ||
                (lastVelocity.equals("negativeY") && newDir.equals("positiveY"));
    }
}