package communication;

/**
 * Commands used to communicate updates between host and clients.
 * 
 * @author JoelNeppel
 *
 */
public enum Command
{
	FULL_UPDATE('F'),
	ADD_SQUIRREL('A'),
	MOVE_SQUIRREL('M'),
	ADD_NUT('N'),
	REMOVE_NUT('R'),
	DONE('D'),
	RESEND('H');

	/**
	 * The char to send as command
	 */
	private char data;

	/**
	 * Constructs enum with the given command char
	 * @param c
	 *     The char command
	 */
	private Command(char c)
	{
		data = c;
	}

	/**
	 * Returns the char command representation for the command.
	 * @return The char command
	 */
	public char getChar()
	{
		return data;
	}

	/**
	 * Returns the command that corresponds with the given char, or null if there is
	 * no correspondence.
	 * @param c
	 *     The char to search for
	 * @return The command that the char represents
	 */
	public Command byteToCommand(char c)
	{
		for(Command com : Command.values())
		{
			if(c == com.data)
			{
				return com;
			}
		}

		return null;
	}
}
