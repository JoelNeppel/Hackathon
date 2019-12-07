package nutty;

import java.awt.Color;
import java.awt.Graphics;
//import java.awt.Graphics2D;

public class Tree
{
	public static int x; // x and y for size of screen

	public static int y;

	public static void setLoc(int x, int y)
	{
		Tree.x = x;
		Tree.y = y;
	}

	public static void draw(Graphics g)
	{
		//Graphics2D g2d = (Graphics2D)g;

		// Trunk
		g.setColor(new Color(139, 69, 19));
		g.fillRect((2 * x) / 5, 0, x / 5, y);

		// Branches
		g.setColor(new Color(139, 69, 19));
		
		for(int i = 0; i < 5; ++i)
		{
			g.fillRect(50, 100 + (175 * i), 450, 25);
		}
		for(int i = 0; i < 6; ++i)
		{
			g.fillRect(500, 75 + (150 * i), 450, 25);
		}
		

		//New Fancy Graphics
		/*
		g2d.rotate(Math.PI/6, 500, 500);
		g.fillRect(50, 100, 450, 25);

		g2d.rotate(-Math.PI/6,500,500);
		*/
	}
}