import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Maze extends JPanel{

    public Maze(){
        setBounds(0,0,1920,1080);
    }
        static String tileMap =
                    "4151111111115000030000000000000000300000\n" +
                    "0010000000001000010000051111115000100000\n" +
                    "0051151150005111151111150000005111511113\n" +
                    "0010010010000000010000010000001000000000\n" +
                    "0010010051111150051130051111115003115000\n" +             // add values for turns
                    "0010010000000010000000000000001000001000\n" +             // make values add torward fitness func
                    "3151551511500051115130051113001000001000\n" +
                    "0000100100100000001000010000005111115003\n" +
                    "3150100100511500003051151150001000001001\n" +
                    "0051500100000511500010000010001000001001\n" +
                    "0000000100000000515030000051115150005115\n" +
                    "0005111500511150101000000000000010000000\n" +
                    "0001000000100010305511111111115011111130\n" +
                    "0001000000100010000100000000001000001000\n" +
                    "0005111111500010300511111111501030001113\n" +
                    "3500000000000010100000010000101010001000\n" +
                    "0100511111111150515130011031501010511115\n" +
                    "0100100000000000001000001000105111501001\n" +
                    "0100100003000030001000001000101000001001\n" +
                    "0511511115150051111111115000105111301055\n" +
                    "0000000001051150000000005111500000001010\n" +
                    "3111111115000051113000000000513003115052";

    public static String[] tileRowLabels = tileMap.split("\n");
    public static char[][] tileMapPositionValues = stringToCharArrays(tileRowLabels);



    public static char[][] stringToCharArrays(String[] stringArray) {
        char[][] result = new char[stringArray.length][];

        for (int i = 0; i < stringArray.length; i++) {
            result[i] = stringArray[i].toCharArray();
        }

        return result;
    }
    public char[][] getGrid() {
        return tileMapPositionValues;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

         int tileSize = 34;
        String[] rows = tileMap.split("\n");// splits the string into rows


        for (int y = 0; y < rows.length; y++) {
            String row = rows[y];
            for (int x = 0; x < row.length(); x++) {      //makes grid to itterate to
                char tile = row.charAt(x);

                if (tile == '1') {                         //assign colors
                    g.setColor(Color.WHITE); // path
                }else if (tile == '2') {
                    g.setColor(Color.GREEN); // finish
                }else if (tile == '3') {
                    g.setColor(Color.RED); // dead end
                }else if (tile == '4') {
                    g.setColor(Color.BLUE); // start
                }else if (tile == '5') {
                    g.setColor(Color.YELLOW); // turn
                }else if (tile == 0) {
                    g.setColor(Color.BLACK); // wall
                }

                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                g.setColor(Color.GRAY); // optional grid lines
                g.drawRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }


        }
      }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1920,1080);
    }
}


