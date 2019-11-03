package hackathon;

import java.awt.Rectangle;

public class Squirrel
{
    private Rectangle loc;
    private int playerID;
    private int numNuts;
    private Movement dir;

    public Squirrel(int id, int x, int y)
    {
        playerID = id;
        loc = new Rectangle(x, y, 100, 100);
        dir = Movement.STILL;
    }

    public void setLocation(int x, int y)
    {
        loc.setLocation(x, y);
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

    public byte[] getBytes()
    {
        byte[] data = new byte[16];
        ByteHelp.toBytes(playerID, 0, data);
        ByteHelp.toBytes(loc.x, 4, data);
        ByteHelp.toBytes(loc.y, 8, data);
        ByteHelp.toBytes(numNuts, 12, data);

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