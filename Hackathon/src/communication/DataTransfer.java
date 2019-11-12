package communication;

import java.io.IOException;
import java.io.InputStream;

import nutty.DoublyLinkedList;
import nutty.Squirrel;

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
		PLAYER_NUTS('H'),
		CHANGE_USERNAME('U');

		private char toSend;

		private TransferType(char c)
		{
			toSend = c;
		}

		public char getCharacterToSend()
		{
			return toSend;
		}

		public TransferType charToTransfer(char c)
		{
			for(TransferType t : TransferType.values())
			{
				if(c == t.toSend)
				{
					return t;
				}
			}

			return null;
		}
	}

	// Byte format [numPlayers, player1ID1, player1ID2, newLoc1, newLoc2, ...
	// repeats for each remaining player]
	public static void performYUpdates(InputStream in, DoublyLinkedList<Squirrel> squirrels) throws IOException
	{
		byte[] data = new byte[2];

		int numElements = in.read();

		for(int i = 0; i < numElements; i++)
		{
			// Player ID
			in.read(data);
			int id = ByteHelp.bytesToInt(data);

			// New player Y
			in.read(data);
			int newY = ByteHelp.bytesToInt(data);

			squirrels.get(new Squirrel(id, 0, 0)).setY(newY);
		}
	}
}