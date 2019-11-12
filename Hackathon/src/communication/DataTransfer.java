package communication;

import java.io.IOException;
import java.io.InputStream;

import nutty.DoublyLinkedList;
import nutty.Nut;
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
		PLAYER_NUTS('H');

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

	// Byte format [id1, id2, x1, x2, y1, y2, usernameLength, player name]
	public static void addPlayer(InputStream in, DoublyLinkedList<Squirrel> squirrels) throws IOException
	{
		byte[] data = new byte[2];

		// Player ID
		in.read(data);
		int id = ByteHelp.bytesToInt(data);

		// X location
		in.read(data);
		int x = ByteHelp.bytesToInt(data);

		// Y location
		in.read(data);
		int y = ByteHelp.bytesToInt(data);

		// Username
		data = new byte[in.read()];
		in.read(data);
		String username = new String(data);

		squirrels.add(new Squirrel(id, x, y, username));
	}

	// Byte format [numPlayers, player1ID1, player1ID2, newLoc1, newLoc2, ...
	// repeats for each remaining player]
	public static void performYUpdates(InputStream in, DoublyLinkedList<Squirrel> squirrels) throws IOException
	{
		int numElements = in.read();

		byte[] data = new byte[2];
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

	public static void addNut(InputStream in, DoublyLinkedList<Nut> nuts) throws IOException
	{
		doNutAction(in, nuts::add);
	}

	public static void removeNut(InputStream in, DoublyLinkedList<Nut> nuts) throws IOException
	{
		doNutAction(in, nuts::remove);
	}

	// Byte format [x1, x2, y1, y2]
	private static void doNutAction(InputStream in, Action a) throws IOException
	{
		byte[] data = new byte[2];

		// X location
		in.read(data);
		int x = ByteHelp.bytesToInt(data);

		// Y location
		in.read(data);
		int y = ByteHelp.bytesToInt(data);

		a.nutAction(new Nut(x, y));
	}

	private interface Action
	{
		void nutAction(Nut n);
	}
}