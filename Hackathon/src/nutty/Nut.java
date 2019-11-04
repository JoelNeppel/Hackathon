package nutty;

import java.awt.Color;
import java.awt.Graphics;

public class Nut
{
	private int x;

	private int y;

	private int size = 25;

	static Color gold = new Color(224, 169, 38);

	static Color darkGold = new Color(189, 133, 4);

	static Color stem = new Color(169, 117, 3);

	public Nut(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void drawNut(Graphics g)
	{
		g.setColor(gold);
		g.fillOval(x, y, size, size);
		g.setColor(stem);
		g.fillRect(x + (size / 2) - (size / 12), y - (size / 3), size / 6, size / 3);
		g.setColor(darkGold);
		g.fillOval(x, y - (size / 10), size, size / 3);
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public boolean equals(Object other)
	{
		if(other.getClass() != Nut.class)
		{
			return false;
		}

		Nut o = (Nut) other;
		return o.x == this.x && o.y == this.y;
	}
}