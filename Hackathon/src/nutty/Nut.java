package nutty;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Stores the x and y coordinate for the nut. Also stores the colors for the nut
 * graphics.
 * 
 * @author JoelNeppel, ZackGrewell
 *
 */
public class Nut
{
	/**
	 * The x coordinate
	 */
	private int x;

	/**
	 * The y coordinate
	 */
	private int y;

	/**
	 * The nut size in pixels
	 */
	private static int size = 25;

	/**
	 * The nut body color
	 */
	private static Color gold = new Color(224, 169, 38);

	/**
	 * The nut top color
	 */
	private static Color darkGold = new Color(189, 133, 4);

	/**
	 * The nut stem color
	 */
	private static Color stem = new Color(169, 117, 3);

	/**
	 * Constructs a new nut at the given coordinates.
	 * @param x
	 *     The x coordinate for this nut
	 * @param y
	 *     The y coordinate for this nut
	 */
	public Nut(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Draws the nut with the given graphics.
	 * @param g
	 *     The graphics to use
	 */
	public void drawNut(Graphics g)
	{
		g.setColor(gold);
		g.fillOval(x, y, size, size);
		g.setColor(stem);
		g.fillRect(x + (size / 2) - (size / 12), y - (size / 3), size / 6, size / 3);
		g.setColor(darkGold);
		g.fillOval(x, y - (size / 10), size, size / 3);
	}

	/**
	 * Returns the x coordinate for the nut.
	 * @return The x coordinate
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns the y coordinate for the nut.
	 * @return The y coordinate
	 */
	public int getY()
	{
		return y;
	}

	@Override
	public boolean equals(Object other)
	{
		if(null == other || other.getClass() != Nut.class)
		{
			return false;
		}

		Nut o = (Nut) other;
		return o.x == this.x && o.y == this.y;
	}
}