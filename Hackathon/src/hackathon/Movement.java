package hackathon;

public enum Movement
{
    UP(0), DOWN(1), LEFT(2), RIGHT(3), STILL(4);

    private int num;

    private Movement(int num)
    {
        this.num = num;
    }

    public int getNum()
    {
        
        return num;
    }

    public Movement intToMov(int i)
    {
        for(Movement m : Movement.values())
        {
            if(m.getNum() == i)
            {
                return m;
            }
        }
        return null;
    }
}