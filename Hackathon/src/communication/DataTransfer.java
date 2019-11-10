package communication;

/**
 * @author JoelNeppel
 *
 */
public class DataTransfer
{
	public enum TransferType
	{
		FULL('F'),
		ADD_NUT('+'),
		REMOVE_NUT('-'),
		ADD_PLAYER('A'),
		REMOVE_PLAYER('R'),
		PLAYER_X('X'),
		PLAYER_Y('Y'),
		PLAYER_NUTS('H');

		private char toSend;

		private TransferType(char c)
		{
			toSend = c;
		}
	}
}