import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class Runner extends JPanel {
    public Runner(){
        setBounds(0,0,1920,1080);
        setOpaque(false);
    }
    char right= 'R';
    char left = 'L';
    Color uniqueColor = colorGenerate();
    public char[] genome = createGenome(30);

    public int fitness;

    public int x_pos;
    public int y_pos;


    public static Color colorGenerate(){  //gives random color for each genome
        Random random = new Random();

        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        Color color1= new Color(r,g,b);

        return color1;
    }
    public static char[] createGenome(int length){   // creates random starter genome

        char[] genome = new char[length];

        for(int i=0; i<length; i++){
            double randomNum = Math.floor(Math.random() * 3) + 1;
            if(randomNum == 1){
                genome[i] = 'R';
            }
            else if(randomNum == 2){
                genome[i] = 'L';
            }
            else if(randomNum == 3){
                genome[i] = 'F';
            }

        }

        return genome;
    }
    //fitness method here



    //render square with its unique color here
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int tileSize = 34;
        g.setColor(uniqueColor);
        g.fillRect(x_pos, y_pos, tileSize / 2, tileSize / 4);

    }
    public void setX(int x) {
        this.x_pos = x;
    }

    public void setY(int y) {
        this.y_pos = y;
    }



    //add controls for movement here
}
