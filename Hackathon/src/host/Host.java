package host;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import nutty.ByteHelp;
import nutty.Constants;
import nutty.DoublyLinkedList;
import nutty.Movement;
import nutty.Nut;
import nutty.Squirrel;

public class Host
{
	private static DoublyLinkedList<Nut> nuts; // haha nuts

	private static DoublyLinkedList<Client> clients;

	private static boolean allowEntry;

	public static void main(String[] args)
	{
		nuts = new DoublyLinkedList<>();
		clients = new DoublyLinkedList<>();
		nutGeneration();
		doRounds();
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

		while(!Thread.interrupted())
		{
			try
			{
				Socket client = server.accept();
				boolean done = false;
				while(!done)
				{
					if(allowEntry)
					{
						client.setTcpNoDelay(true);
						handleClient(client);
						done = true;
					}
					else
					{
						try
						{
							Thread.sleep(1);
						}
						catch(InterruptedException e)
						{

						}
					}
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
		new Thread(()->
		{
			long lastUpdate = System.currentTimeMillis();
			int updateTime = 1000 / 40;
			while(true)
			{
				// Do player movements/updates
				for(Client c : clients)// For each squirrel in list
				{
					c.doMovement(clients, nuts);
				}

				// Send updates to players
				// get packet data and calculate cordinets?
				byte[] data = getBytes();
				for(Socket client : clients)
				{
					update(client, data);
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

	private static byte[] getBytes()
	{
		while(true)
		{
			try
			{
				allowEntry = false;
				int at = 0;
				byte[] data = new byte[8 + 16 * squirrels.size() + 8 * nuts.size()];
				ByteHelp.toBytes(squirrels.size(), at, data);
				at += 4;
				ByteHelp.toBytes(nuts.size(), at, data);
				at += 4;

				for(Squirrel s : squirrels)
				{
					byte[] sData = s.getBytes();
					for(int i = 0; i < 16; i++)
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
				allowEntry = true;

				return data;
			}
			catch(NullPointerException | IndexOutOfBoundsException e)
			{

			}
		}

	}

	private static void handleClient(Socket client)
	{
		new Thread(()->
		{
			Random rand = new Random();
			int id = rand.nextInt(100);
			while(squirrels.contains(new Squirrel(id, 0, 0)))
			{
				id = rand.nextInt(100);
			}

			Squirrel squirrel = new Squirrel(id, rand.nextInt(900), 850);
			rand = null;
			squirrels.add(squirrel);
			byte[] bytes = ByteHelp.toBytes(id);
			InputStream in = null;
			while(null == in)
			{
				try
				{
					in = client.getInputStream();
				}
				catch(IOException e)
				{

				}
			}

			while(!client.isClosed())
			{
				try
				{
					if(in.available() >= 4)
					{
						bytes = new byte[4];
						in.read(bytes);
						squirrel.setMovement(Movement.intToMov(ByteHelp.bytesToInt(bytes)));
					}
					else
					{
						try
						{
							Thread.sleep(10);
						}
						catch(InterruptedException e)
						{

						}
					}
				}
				catch(IOException e)
				{

				}

			}
			squirrels.remove(squirrel);
			clients.remove(client);
		}).start();
	}

	private static void nutGeneration()
	{
		new Thread(()->
		{
			while(true)
			{
				if(nuts.size() < 30 && allowEntry)
				{
					Random rand = new Random();

					int x = rand.nextInt(1000);
					int y = rand.nextInt(1000);

					nuts.add(new Nut(x, y));
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
}