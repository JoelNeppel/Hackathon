package nutty;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Tree
{
	public static int x; 
						// x and y for size of screen
	public static int y;

	public static Color tree = new Color(139, 69, 19);

	public static int length = 450;

	public static int width = 25;

	public static void setLoc(int x, int y)
	{
		Tree.x = x;
		Tree.y = y;
	}

	public static void draw(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;

		// Trunk
		g.setColor(tree);
		g.fillRect((2 * x) / 5, 0, x / 5, y);

		// Branches
		/*
		for(int i = 0; i < 5; ++i)
		{
			g.fillRect(50, 100 + (175 * i), length, width);
		}
		
		for(int i = 0; i < 6; ++i)
		{
			g.fillRect(500, 75 + (150 * i), 450, 25);
		}
		*/

		//New Fancy-er Graphics
		
		for(int i = 0; i < 5; ++i)
		{
			g2d.rotate(-Math.PI/12, 500, 100 + (150 * i));
			g.fillRect(500,100 + (150 * i),length - (50 * i),width);
			g2d.rotate(Math.PI/12, 500, 100 + (150 * i));
		}
		
		for(int i = 0; i < 5; ++i)
		{
		g2d.rotate((13 * Math.PI)/12,500,200 + (150 * i));
		g.fillRect(500, 200 + (150 * i), length - (50 * i), width);
		g2d.rotate(-(13 * Math.PI)/12,500,200 + (150 * i));
		}

	}
}