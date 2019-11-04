package host;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import nutty.ByteHelp;
import nutty.Constants;
import nutty.DoublyLinkedList;
import nutty.Movement;
import nutty.Nut;
import nutty.Squirrel;

public class Host
{
	private static DoublyLinkedList<Squirrel> squirrels;

	private static DoublyLinkedList<Socket> clients;

	private static DoublyLinkedList<Nut> nuts; // haha nuts

	private static boolean allowEntry;

	public static void main(String[] args)
	{
		squirrels = new DoublyLinkedList<>();
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
						clients.add(client);
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
				for(Squirrel s : squirrels)// For each squirrel in list
				{
					switch(s.getDirection())
					{
						case UP:
							s.move(0, -2);
							break;
						case DOWN:
							s.move(0, 2);
							break;
						case LEFT:
							s.move(-2, 0);
							break;
						case RIGHT:
							s.move(2, 0);
						default:
							break;
					}
					checkNuts(s);
					checkTouching(s);
					checkBoundry(s);
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

	private static void checkNuts(Squirrel s)
	{
		for(Nut n : nuts)
		{
			if(s.touched(n.getX(), n.getY()))
			{
				s.addNut();
				nuts.remove(n);
			}
		}
	}

	private static void checkTouching(Squirrel s)
	{
		for(Squirrel n : squirrels)
		{
			if(s.touched(n.getRect()))
			{
				int moveDist;
				if(s.getNumNuts() > n.getNumNuts())
				{
					moveDist = 10 + s.getNumNuts();
					if(moveDist > 50)
					{
						moveDist = 50;
					}
					switch(s.getDirection())
					{
						case UP:
							n.move(0, -moveDist);
							break;
						case DOWN:
							n.move(0, moveDist);
							break;
						case LEFT:
							n.move(-moveDist, 0);
							break;
						case RIGHT:
							n.move(moveDist, 0);
							break;
						case STILL:
							break;
					}
				}
				else if(s.getNumNuts() < n.getNumNuts())
				{
					moveDist = 10 + n.getNumNuts();
					if(moveDist > 50)
					{
						moveDist = 50;
					}
					switch(n.getDirection())
					{
						case UP:
							s.move(0, -moveDist);
							break;
						case DOWN:
							s.move(0, moveDist);
							break;
						case LEFT:
							s.move(-moveDist, 0);
							break;
						case RIGHT:
							s.move(moveDist, 0);
							break;
						case STILL:
							break;
					}
				}
				else
				{
					moveDist = 10 + s.getNumNuts();
					if(moveDist > 50)
					{
						moveDist = 50;
					}
					switch(s.getDirection())
					{
						case UP:
							n.move(0, -moveDist);
							s.move(0, moveDist);
							break;
						case DOWN:
							n.move(0, moveDist);
							s.move(0, -moveDist);
							break;
						case LEFT:
							n.move(-moveDist, 0);
							s.move(moveDist, 0);
							break;
						case RIGHT:
							n.move(moveDist, 0);
							s.move(-moveDist, 0);
							break;
						case STILL:
							break;
					}
				}
			}
		}
	}

	private static void checkBoundry(Squirrel s)
	{
		if(s.getX() < 0)
		{
			s.setLocation(0, s.getY());
		}
		else if(s.getX() > 900)
		{
			s.setLocation(900, s.getY());
		}

		if(s.getY() < 0)
		{
			s.setLocation(s.getX(), 0);
		}
		else if(s.getY() > 900)
		{
			s.setLocation(s.getX(), 900);
		}
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

	private static void update(Socket client, byte[] data)
	{
		new Thread(()->
		{
			try
			{
				OutputStream out = client.getOutputStream();
				out.write(data);
			}
			catch(SocketException e)
			{
				clients.remove(client);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}).start();
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

			Squirrel squirrel = new Squirrel(id, rand.nextInt(1000), 850);
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