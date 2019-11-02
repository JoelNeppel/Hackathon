package hackathon;

public class Packet
{
    private int playerID;
    private Movement mov;
    private Squirrel s;

    public Packet(Squirrel squirrel)
    {
        s = squirrel;
    }

    public Packet(int id, Movement m)
    {

    }

    public Packet(byte[] data)
    {

    }

    public byte[] toBytes()
    {
        if(null != s)
        {
            byte[] data = new byte[16];
            
        }
        return null;
    }
    
    public static void main(String[] args) {

    
    }
}