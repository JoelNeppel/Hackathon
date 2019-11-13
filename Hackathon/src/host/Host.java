package host;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import communication.DataTransfer;
import nutty.Constants;
import nutty.DoublyLinkedList;
import nutty.Nut;
import nutty.SinglyLinkedList;
import nutty.Squirrel;

/**
 * Methods and data that will run on the host.
 * 
 * @author JoelNeppel, ZackGrewell
 *
 */
public class Host
{
	/**
	 * The list of nuts
	 */
	private static DoublyLinkedList<Nut> nuts; // haha nuts

	private static DoublyLinkedList<Nut> removed;

	private static DoublyLinkedList<Nut> added;

	/**
	 * The list of clients
	 */
	private static DoublyLinkedList<Client> clients;

	/**
	 * The number of players that have joined since the last server restart, used
	 * for player ID
	 */
	private static int playerNum;

	/**
	 * Sets up all resources necessary for clients to connect and play game.
	 * Continuously accepts connections from players.
	 * @param args
	 */
	public static void main(String[] args)
	{
		added = new DoublyLinkedList<>();
		removed = new DoublyLinkedList<>();
		nuts = new DoublyLinkedList<Nut>()
		{
			@Override
			public void add(Nut n)
			{
				super.add(n);
				added.add(n);
			}

			@Override
			public void remove(Nut n)
			{
				super.remove(n);
				removed.add(n);
			}
		};
		clients = new DoublyLinkedList<>();

		ServerSocket server = null;
		while(null == server)
		{
			try
			{
				server = new ServerSocket(Constants.PORT);
			}
			catch(IOException e)
			{

			}
		}

		nutGeneration();
		doRounds();

		while(!Thread.interrupted())
		{
			try
			{
				Socket client = server.accept();
				boolean done = false;
				while(!done)
				{
					client.setTcpNoDelay(true);
					handleClient(client);
					done = true;
				}
			}
			catch(IOException e)
			{

			}
		}

		try
		{
			server.close();
		}
		catch(IOException e)
		{

		}
	}

	/**
	 * Moves players in set intervals of 40 times a second and sends updated
	 * locations to all clients.
	 */
	private static void doRounds()
	{
		int updateTime = 1000 / 40;
		new Thread(()->
		{
			long lastUpdate = 0;
			while(true)
			{
				// Do player movements/updates
				for(Client c : clients)
				{
					c.doMovement(clients, nuts);
				}

				// Convert data into bytes
				byte[] data = getBytes();
				// Send bytes to each client
				for(Client c : clients)
				{
					c.write(data);
				}

				// Only update 40 times a second
				long timeTaken = System.currentTimeMillis() - lastUpdate;
				lastUpdate = System.currentTimeMillis();
				if(timeTaken < updateTime)
				{
					try
					{
						Thread.sleep(updateTime - timeTaken);
					}
					catch(InterruptedException e)
					{
					}
				}
			}
		}).start();
	}

	/**
	 * Returns the byte data to send to each client to update the information.
	 * @return The bytes to send
	 */
	private static synchronized byte[] getBytes()
	{
		SinglyLinkedList<byte[]> data = new SinglyLinkedList<>();

		for(Nut n : added)
		{
			data.add(DataTransfer.sendNutAddition(n));
		}

		for(Nut n : removed)
		{
			data.add(DataTransfer.sendNutRemoval(n));
		}

		SinglyLinkedList<Squirrel> xChange = new SinglyLinkedList<>();
		SinglyLinkedList<Squirrel> yChange = new SinglyLinkedList<>();

		for(Client c : clients)
		{
			if(c.xChanged())
			{
				xChange.add(c.getSquirrel());
			}
			if(c.yChanged())
			{
				yChange.add(c.getSquirrel());
			}

			if(c.nutsChanged())
			{
				data.add(DataTransfer.sendSetPlayerNut(c.getSquirrel()));
			}
		}

		data.add(DataTransfer.sendXUpdates(yChange));
		data.add(DataTransfer.sendYUpdates(yChange));

		int totLen = 1;
		for(byte[] b : data)
		{
			totLen += b.length;
		}

		byte[] send = new byte[totLen];
		int at = 0;
		for(byte[] bytes : data)
		{
			for(byte b : bytes)
			{
				send[at] = b;
				at++;
			}
		}

		send[send.length - 1] = (byte) DataTransfer.TransferType.DONE.getCharacterToSend();

		return send;
	}

	/**
	 * Creates a new client with their ID being the number of players that joined
	 * using the given socket.
	 * @param soc
	 *     The socket to use for communicate to client
	 */
	private static synchronized void handleClient(Socket soc)
	{
		Squirrel squirrel = new Squirrel(playerNum, new Random().nextInt(900), 850);

		byte[] send = DataTransfer.sendAddPlayer(squirrel);
		for(Client c : clients)
		{
			c.write(send);
		}

		Client newC = new Client(soc, squirrel);
		clients.add(newC);
		newC.write(DataTransfer.sendFullUpdate(nuts, clients));
		playerNum++;
	}

	/**
	 * Begins a new thread that generates a new nut every 1250 milliseconds with a
	 * maximum of 30 nuts.
	 */
	private static void nutGeneration()
	{
		new Thread(()->
		{
			Random rand = new Random();
			while(true)
			{
				if(nuts.size() < Constants.NUT_GENERATION_LIMIT)
				{
					int x = rand.nextInt(1000);
					int y = rand.nextInt(1000);

					synchronized(Host.class)
					{
						nuts.add(new Nut(x, y));
					}
				}

				try
				{
					Thread.sleep(1250);
				}
				catch(InterruptedException e)
				{

				}
			}
		}).start();
	}

	/**
	 * Removes the given client from the game.
	 * @param c
	 *     The client to remove
	 */
	public static synchronized void removeClient(Client c)
	{
		clients.remove(c);

		byte[] send = DataTransfer.sendPlayerRemoval(c.getSquirrel());
		for(Client client : clients)
		{
			client.write(send);
		}
	}
}