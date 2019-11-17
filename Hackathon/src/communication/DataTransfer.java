package communication;

import java.io.IOException;
import java.io.InputStream;

import host.Client;
import nutty.DoublyLinkedList;
import nutty.Nut;
import nutty.SinglyLinkedList;
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
		SET_PLAYER_NUTS('H'),
		ADD_PLAYER_NUT('E'),
		DONE('D');

		private char toSend;

		private TransferType(char c)
		{
			toSend = c;
		}

		public char getCharacterToSend()
		{
			return toSend;
		}

		public static TransferType charToTransfer(char c)
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

	// Byte format [
	public static void receiveFullUpdate(InputStream in, DoublyLinkedList<Nut> nuts, DoublyLinkedList<Squirrel> squirrels) throws IOException
	{
		byte[] bytes = new byte[4];
		in.read(bytes);
		int numSquirrels = ByteHelp.bytesToInt(bytes);
		System.out.println("reading");
		in.read(bytes);
		int numNuts = ByteHelp.bytesToInt(bytes);
		for(int i = 0; i < numSquirrels; i++)
		{
			in.read(bytes);
			int id = ByteHelp.bytesToInt(bytes);
			in.read(bytes);
			int x = ByteHelp.bytesToInt(bytes);
			in.read(bytes);
			int y = ByteHelp.bytesToInt(bytes);
			in.read(bytes);
			int squirrelNuts = ByteHelp.bytesToInt(bytes);
			in.read(bytes);
			byte[] nameBytes = new byte[ByteHelp.bytesToInt(bytes)];
			in.read(nameBytes);
			String name = new String(nameBytes);

			boolean result = squirrels.contains(new Squirrel(id, 0, 0));
			Squirrel s;
			if(!result)
			{
				s = new Squirrel(id, x, y, name);
				squirrels.add(s);
			}
			else
			{
				s = squirrels.get(new Squirrel(id));
				s.setLocation(x, y);
				s.setName(name);
			}
			s.setNuts(squirrelNuts);

		}

		nuts.clear();
		for(int i = 0; i < numNuts; i++)
		{
			in.read(bytes);
			int x = ByteHelp.bytesToInt(bytes);
			in.read(bytes);
			int y = ByteHelp.bytesToInt(bytes);

			nuts.add(new Nut(x, y));
		}
	}

	public static byte[] sendFullUpdate(DoublyLinkedList<Nut> nuts, DoublyLinkedList<Client> clients)
	{
		int at = 1;
		int namesLen = 0;
		for(Client c : clients)
		{
			namesLen += c.getSquirrel().getName().length();
		}

		byte[] data = new byte[10 + 20 * clients.size() + namesLen + 8 * nuts.size()];
		data[0] = (byte) TransferType.FULL.getCharacterToSend();
		ByteHelp.toBytes(clients.size(), at, data);
		at += 4;
		ByteHelp.toBytes(nuts.size(), at, data);
		at += 4;

		for(Client c : clients)
		{
			Squirrel s = c.getSquirrel();
			byte[] sData = s.getBytes();
			for(int i = 0; i < 20 + s.getName().length(); i++)
			{
				data[at] = sData[i];
				at++;
			}
		}

		for(Nut n : nuts)
		{
			ByteHelp.toBytes(n.getX(), at, data);
			at += 4;
			ByteHelp.toBytes(n.getY(), at, data);
			at += 4;
		}

		data[data.length - 1] = (byte) TransferType.DONE.getCharacterToSend();

		return data;
	}

	public static void receiveNutAddition(InputStream in, DoublyLinkedList<Nut> nuts) throws IOException
	{
		receiveNutAction(in, nuts::add);
	}

	public static byte[] sendNutAddition(Nut add)
	{
		byte[] data = new byte[5];

		data[0] = (byte) TransferType.ADD_NUT.getCharacterToSend();
		nutToBytes(data, add);

		return data;
	}

	public static void receiveNutRemoval(InputStream in, DoublyLinkedList<Nut> nuts) throws IOException
	{
		receiveNutAction(in, nuts::remove);
	}

	public static byte[] sendNutRemoval(Nut add)
	{
		byte[] data = new byte[5];

		data[0] = (byte) TransferType.REMOVE_NUT.getCharacterToSend();
		nutToBytes(data, add);

		return data;
	}

	// Byte format [x1, x2, y1, y2]
	private static void receiveNutAction(InputStream in, Action a) throws IOException
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

	private static void nutToBytes(byte[] put, Nut n)
	{
		byte[] data = ByteHelp.toBytes(n.getX());
		put[1] = data[2];
		put[2] = data[3];

		data = ByteHelp.toBytes(n.getY());
		put[3] = data[2];
		put[4] = data[3];
	}

	// Byte format [id1, id2, x1, x2, y1, y2, usernameLength, player name]
	public static void receiveAddPlayer(InputStream in, DoublyLinkedList<Squirrel> squirrels) throws IOException
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

		Squirrel s = new Squirrel(id, x, y, username);

		if(!squirrels.contains(s))
		{
			squirrels.add(s);
		}
	}

	public static byte[] sendAddPlayer(Squirrel s)
	{
		byte[] data = new byte[8 + s.getName().length()];

		data[0] = (byte) TransferType.ADD_PLAYER.getCharacterToSend();

		// ID
		byte[] bytes = ByteHelp.toBytes(s.getID());
		data[1] = bytes[2];
		data[2] = bytes[3];

		// x location
		bytes = ByteHelp.toBytes(s.getX());
		data[3] = bytes[2];
		data[4] = bytes[3];

		// y location
		bytes = ByteHelp.toBytes(s.getY());
		data[5] = bytes[2];
		data[6] = bytes[3];

		// User name
		bytes = ByteHelp.toBytes(s.getName().length());
		data[7] = bytes[3];
		int at = 8;
		bytes = s.getName().getBytes();
		for(byte b : s.getName().getBytes())
		{
			data[at] = b;
			at++;
		}

		return data;
	}

	// Byte format [id1, id2]
	public static void receivePlayerRemoval(InputStream in, DoublyLinkedList<Squirrel> squirrels) throws IOException
	{
		byte[] data = new byte[2];
		in.read(data);

		int id = ByteHelp.bytesToInt(data);

		squirrels.remove(new Squirrel(id));
	}

	public static byte[] sendPlayerRemoval(Squirrel s)
	{
		byte[] data = new byte[3];

		data[0] = (byte) TransferType.REMOVE_PLAYER.getCharacterToSend();
		byte[] id = ByteHelp.toBytes(s.getID());
		data[1] = id[2];
		data[2] = id[3];

		return data;
	}

	// Byte format [numPlayers, player1ID1, player1ID2, newLoc1, newLoc2, ...
	// repeats for each remaining player]
	public static void performXUpdates(InputStream in, DoublyLinkedList<Squirrel> squirrels) throws IOException
	{
		int numElements = in.read();

		byte[] data = new byte[2];
		for(int i = 0; i < numElements; i++)
		{
			// Player ID
			in.read(data);
			int id = ByteHelp.bytesToInt(data);

			// New player X
			in.read(data);
			int newX = ByteHelp.bytesToInt(data);

			squirrels.get(new Squirrel(id)).setX(newX);
		}
	}

	public static byte[] sendXUpdates(SinglyLinkedList<Squirrel> squirrels)
	{
		byte[] data = new byte[2 + 4 * squirrels.size()];

		data[0] = (byte) TransferType.PLAYER_X.getCharacterToSend();
		data[1] = ByteHelp.toBytes(squirrels.size())[3];

		int at = 2;
		for(Squirrel s : squirrels)
		{
			// Player ID
			byte[] bytes = ByteHelp.toBytes(s.getID());
			data[at] = bytes[2];
			at++;
			data[at] = bytes[3];
			at++;

			// New x
			bytes = ByteHelp.toBytes(s.getX());
			data[at] = bytes[2];
			at++;
			data[at] = bytes[3];
			at++;
		}

		return data;
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

	public static byte[] sendYUpdates(SinglyLinkedList<Squirrel> squirrels)
	{
		byte[] data = new byte[2 + 4 * squirrels.size()];

		data[0] = (byte) TransferType.PLAYER_X.getCharacterToSend();
		data[1] = ByteHelp.toBytes(squirrels.size())[3];

		int at = 2;
		for(Squirrel s : squirrels)
		{
			// Player ID
			byte[] bytes = ByteHelp.toBytes(s.getID());
			data[at] = bytes[2];
			at++;
			data[at] = bytes[3];
			at++;

			// New y
			bytes = ByteHelp.toBytes(s.getY());
			data[at] = bytes[2];
			at++;
			data[at] = bytes[3];
			at++;
		}

		return data;
	}

	// Byte format [id1, id2, numNuts1, numNuts2]
	public static void performNutSet(InputStream in, DoublyLinkedList<Squirrel> squirrels) throws IOException
	{
		byte[] data = new byte[2];

		// ID
		in.read(data);
		int id = ByteHelp.bytesToInt(data);

		// Num nuts
		in.read(data);
		int numNuts = ByteHelp.bytesToInt(data);

		squirrels.get(new Squirrel(id)).setNuts(numNuts);
	}

	public static byte[] sendSetPlayerNut(Squirrel s)
	{
		byte[] data = new byte[5];

		data[0] = (byte) TransferType.SET_PLAYER_NUTS.getCharacterToSend();

		// Player ID
		byte[] bytes = ByteHelp.toBytes(s.getID());
		data[1] = bytes[2];
		data[2] = bytes[3];

		// New nuts
		bytes = ByteHelp.toBytes(s.getNumNuts());
		data[3] = bytes[2];
		data[4] = bytes[3];

		return data;
	}

	// Byte format [id1, id2]
	public static void performAddNut(InputStream in, DoublyLinkedList<Squirrel> squirrels) throws IOException
	{
		byte[] data = new byte[2];

		in.read(data);
		int id = ByteHelp.bytesToInt(data);

		squirrels.get(new Squirrel(id)).addNut();
	}

	public static byte[] sendPlayerAddNut(Squirrel s)
	{
		byte[] data = new byte[3];

		data[0] = (byte) TransferType.ADD_PLAYER_NUT.getCharacterToSend();
		byte[] id = ByteHelp.toBytes(s.getID());
		data[1] = id[2];
		data[2] = id[3];

		return data;
	}
}