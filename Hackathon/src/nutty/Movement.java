package nutty;

/**
 * Enum for which direction the player is moving.
 * 
 * @author JoelNeppel
 *
 */
public enum Movement
{
	UP(0),
	DOWN(1),
	LEFT(2),
	RIGHT(3),
	STILL(4);

	/**
	 * The number representation for the direction.
	 */
	private int num;

	/**
	 * Constructs new movement with the given number representation.
	 * @param num
	 *     The number to use
	 */
	private Movement(int num)
	{
		this.num = num;
	}

	/**
	 * Returns the numerical representation.
	 * @return The number representation
	 */
	public int getNum()
	{
		return num;
	}

	/**
	 * Returns the movement that is associated with the given integer, or null if
	 * the number does not represent a movement.
	 * @param i
	 *     The number that represents a movement
	 * @return The movement associated with the number
	 */
	public static Movement intToMov(int i)
	{
		for(Movement m : Movement.values())
		{
			if(m.num == i)
			{
				return m;
			}
		}

		return null;
	}
}