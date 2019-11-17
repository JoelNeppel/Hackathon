package nutty;

import java.awt.Rectangle;

import communication.ByteHelp;

public class Squirrel
{
	private Rectangle loc;

	private int playerID;

	private int numNuts;

	private Movement dir;

	private String playerName;

	public Squirrel(int id, int x, int y, String username)
	{
		playerID = id;
		loc = new Rectangle(x, y, 100, 100);
		dir = Movement.STILL;
		setName(username);
	}

	public Squirrel(int id, int x, int y)
	{
		this(id, x, y, "");
	}

	public Squirrel(int id)
	{
		this(id, 0, 0);
	}

	public void setLocation(int x, int y)
	{
		loc.setLocation(x, y);
	}

	public void setY(int y)
	{
		setLocation(loc.x, y);
	}

	public void setX(int x)
	{
		setLocation(x, loc.y);
	}

	public void move(int x, int y)
	{
		loc.setLocation(loc.x + x, loc.y + y);
	}

	public Rectangle getRect()
	{
		return loc;
	}

	public int getX()
	{
		return (int) loc.getX();
	}

	public int getY()
	{
		return (int) loc.getY();
	}

	public Movement getDirection()
	{
		return dir;
	}

	public void addNut()
	{
		numNuts++;
	}

	public void setNuts(int nuts)
	{
		numNuts = nuts;
	}

	public int getNumNuts()
	{
		return numNuts;
	}

	public Movement getMovement()
	{
		return dir;
	}

	public void setMovement(Movement dir)
	{
		this.dir = dir;
	}

	public boolean touched(int x, int y)
	{
		return loc.contains(x, y);
	}

	public boolean touched(Rectangle r)
	{
		return loc.intersects(r);
	}

	public int getID()
	{
		return playerID;
	}

	public String getName()
	{
		return playerName;
	}

	public void setName(String name)
	{
		playerName = name;
	}

	public byte[] getBytes()
	{
		byte[] data = new byte[20 + playerName.length()];
		ByteHelp.toBytes(playerID, 0, data);
		ByteHelp.toBytes(loc.x, 4, data);
		ByteHelp.toBytes(loc.y, 8, data);
		ByteHelp.toBytes(numNuts, 12, data);
		ByteHelp.toBytes(playerName.length(), 16, data);
		int at = 20;
		for(byte b : playerName.getBytes())
		{
			data[at] = b;
			at++;
		}
		return data;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o.getClass() != Squirrel.class)
		{
			return false;
		}

		Squirrel s = (Squirrel) o;
		return s.playerID == this.playerID;
	}

	@Override
	public String toString()
	{
		return "Squirrel: " + playerID + " at (" + loc.x + ", " + loc.y + ") with: " + numNuts + " nuts.";
	}
}