package communication;

/**
 * @author JoelNeppel
 *
 */
public enum Command
{
	FULL_UPDATE('F'),
	ADD_SQUIRREL('A'),
	MOVE_SQUIRREL('M'),
	ADD_NUT('N'),
	REMOVE_NUT('R');

	private char data;

	private Command(char b)
	{
		data = b;
	}

	public char getByte()
	{
		return data;
	}

	public Command byteToCommand(char b)
	{
		for(Command c : Command.values())
		{
			if(b == c.data)
			{
				return c;
			}
		}

		return null;
	}
}
