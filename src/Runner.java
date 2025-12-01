import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.*;

public class Runner extends JPanel {

    // --- BASIC SETUP ---
    public int index;
    public boolean printedFinalTime = false;
    public double finalTime = -1;

    public Color uniqueColor = colorGenerate();
    public char[] genome = createGenome(100);

    private int genomePosition = 0;
    private boolean deciding = false;
    private boolean frozen = false;

    public int fitness = 0;
    public int x_pos;
    public int y_pos;

    private String velocity = "positiveX";
    private String lastVelocity = "positiveX";

    public boolean deadEnd = false;
    public boolean reachedGoal = false;

    public int pathSize = 0;
    private Set<String> visitedTiles = new HashSet<>();

    // --- Maze grid provided by Main ---
    private static char[][] positionMap;

    public static void setPositionMap(char[][] map) {
        positionMap = map;
    }

    public Runner() {
        setSize(34/4, 34/4);
        setOpaque(false);
    }

    public void placeAt(int x, int y) {
        setX(x);
        setY(y);
        setLocation(x, y);
    }

    // --- COLOR + GENOME ---
    public static Color colorGenerate() {
        Random random = new Random();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static char[] createGenome(int length) {
        char[] genome = new char[length];
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            int r = rand.nextInt(3);
            genome[i] = (r == 0 ? 'R' : r == 1 ? 'L' : 'F');
        }
        return genome;
    }

    // --- DRAW RUNNER ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(uniqueColor);
        g.fillRect(0, 0, 34 / 4, 34 / 4);
    }

    // --- TILE LOOKUP ---
    public char getGridPositionValue(int x, int y) {
        int tileX = x / 34;
        int tileY = y / 34;

        if (tileX < 0 || tileY < 0 ||
                tileY >= positionMap.length || tileX >= positionMap[0].length) {
            return '0';
        }

        return positionMap[tileY][tileX];
    }

    public char currentTile() {
        return getGridPositionValue(x_pos, y_pos);
    }

    // --- DECISION LOGIC ---
    private boolean isOpposite(String a, String b) {
        return (a.equals("positiveX") && b.equals("negativeX")) ||
                (a.equals("negativeX") && b.equals("positiveX")) ||
                (a.equals("positiveY") && b.equals("negativeY")) ||
                (a.equals("negativeY") && b.equals("positiveY"));
    }

    public boolean makeDecision(char gene) {
        int step = 34;
        int nextX = x_pos;
        int nextY = y_pos;
        String proposed = velocity;

        // Map gene to proposed direction
        switch (velocity) {
            case "positiveX":
                proposed = (gene == 'R') ? "positiveY" :
                        (gene == 'L') ? "negativeY" : "positiveX";
                break;
            case "positiveY":
                proposed = (gene == 'R') ? "positiveX" :
                        (gene == 'L') ? "negativeX" : "positiveY";
                break;
            case "negativeX":
                proposed = (gene == 'R') ? "negativeY" :
                        (gene == 'L') ? "positiveY" : "negativeX";
                break;
            case "negativeY":
                proposed = (gene == 'R') ? "negativeX" :
                        (gene == 'L') ? "positiveX" : "negativeY";
                break;
        }

        // Check if front is blocked
        int frontX = x_pos;
        int frontY = y_pos;
        switch (velocity) {
            case "positiveX": frontX += step; break;
            case "negativeX": frontX -= step; break;
            case "positiveY": frontY += step; break;
            case "negativeY": frontY -= step; break;
        }
        boolean frontBlocked = (getGridPositionValue(frontX, frontY) == '0');

        // Forbid reverse only if front is open
        if (!frontBlocked && isOpposite(lastVelocity, proposed)) {
            return false;
        }

        // Compute next step
        switch (proposed) {
            case "positiveX": nextX += step; break;
            case "negativeX": nextX -= step; break;
            case "positiveY": nextY += step; break;
            case "negativeY": nextY -= step; break;
        }

        char nextTile = getGridPositionValue(nextX, nextY);
        if (nextTile == '0') return false;

        lastVelocity = velocity;
        velocity = proposed;

        x_pos = nextX;
        y_pos = nextY;
        setBounds(x_pos, y_pos, 34 / 4, 34 / 4);
        repaint();
        return true;
    }

    // --- MOVE ONE STEP ---
    public void moveOneStep() {

        int nextX = x_pos;
        int nextY = y_pos;

        switch (velocity) {
            case "positiveX" -> nextX += 34;
            case "negativeX" -> nextX -= 34;
            case "positiveY" -> nextY += 34;
            case "negativeY" -> nextY -= 34;
        }

        char nextTile = getGridPositionValue(nextX, nextY);

        // Wall → freeze and decide
        if (nextTile == '0') {
            frozen = true;
            deciding = true;
            return;
        }

        // Move
        x_pos = nextX;
        y_pos = nextY;
        setBounds(x_pos, y_pos, 34 / 4, 34 / 4);
        repaint();

        // Score the tile we just stepped onto
        evaluateFitness(x_pos, y_pos);

        // Dead end
        if (currentTile() == '3') {
            deadEnd = true;
            frozen = true;
            deciding = false;
            return;
        }

        // Goal
        if (currentTile() == '2') {
            reachedGoal = true;
            frozen = true;
            deciding = false;
            return;
        }

        // Turn tile → must decide next
        if (currentTile() == '5') {
            frozen = true;
            deciding = true;
        }
    }

    // --- FITNESS (Option B) ---
    public void evaluateFitness(int x, int y) {
        int tileX = x / 34;
        int tileY = y / 34;

        String key = tileX + "," + tileY;

        // Only score unique tiles
        if (!visitedTiles.add(key)) return;

        char tile = getGridPositionValue(x, y);

        if (tile == '1') {
            fitness += 1;
            pathSize++;
        }
        else if (tile == '5') {
            fitness += 1;
            pathSize++;
        }
        else if (tile == '3') {
            fitness -= 500;
            deadEnd = true;
        }
        else if (tile == '2') {
            fitness += 5000;
            reachedGoal = true;
        }
    }

    // --- GETTERS / SETTERS ---
    public char[] getGenome() { return genome; }
    public int getGenomePosition() { return genomePosition; }
    public void setGenomePosition(int pos) { genomePosition = pos; }

    public boolean isFrozen() { return frozen; }
    public void setFrozen(boolean f) { frozen = f; }

    public boolean isDeciding() { return deciding; }
    public void setDeciding(boolean d) { deciding = d; }

    public boolean getIfReachedGoal(){ return reachedGoal; }

    public int getX_pos() { return x_pos; }
    public int getY_pos() { return y_pos; }
    public void setX(int x) { x_pos = x; }
    public void setY(int y) { y_pos = y; }

    public int getFitness(){ return fitness; }
}