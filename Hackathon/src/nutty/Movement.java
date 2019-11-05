package nutty;

/**
 * Enum for which direction the player is moving.
 * 
 * @author JoelNeppel
 *
 */
public enum Movement
{
	UP('W'),
	DOWN('S'),
	LEFT('A'),
	RIGHT('D'),
	STILL(' ');

	/**
	 * The char representation for the direction.
	 */
	private char c;

	/**
	 * Constructs new movement with the given char representation.
	 * @param c
	 *     The char to use
	 */
	private Movement(char c)
	{
		this.c = c;
	}

	/**
	 * Returns the char representation.
	 * @return The char representation
	 */
	public char getChar()
	{
		return c;
	}

	/**
	 * Returns the movement that is associated with the given char, or null if the
	 * char does not represent a movement.
	 * @param c
	 *     The char that represents a movement
	 * @return The movement associated with the char
	 */
	public static Movement charToMov(char c)
	{
		for(Movement m : Movement.values())
		{
			if(m.c == c)
			{
				return m;
			}
		}

		return null;
	}
}