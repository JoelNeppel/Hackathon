import java.awt.Graphics;
import java.awt.Color;

public class Tree
{
    int x; //x and y for size of screen
    int y;
    Graphics g;

    public Tree(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public static void draw() {
        //base of tree
        g.setColor(new Color(139,69,19));
        g.fillRect(x/3, y, x/3, y);
    }
}