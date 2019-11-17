package host;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import nutty.DoublyLinkedList;
import nutty.Movement;
import nutty.Nut;
import nutty.Squirrel;

/**
 * Class to store a socket and squirrel for each client.
 * 
 * @author JoelNeppel, zgrewell
 *
 */
public class Client
{
	/**
	 * The socket to communicate with the client
	 */
	private Socket soc;

	/**
	 * The squirrel for the client
	 */
	private Squirrel squirrel;

	/**
	 * The number of nuts the player had since the last change reset
	 */
	private int lastNuts;

	/**
	 * The x location of the player since the last change reset
	 */
	private int lastX;

	/**
	 * The y location of the player since the last change reset
	 */
	private int lastY;

	/**
	 * Constructs a client with the given socket and squirrel
	 * @param socket
	 *     The socket to use
	 * @param player
	 *     The player's squirrel
	 */
	public Client(Socket socket, Squirrel player)
	{
		soc = socket;
		squirrel = player;
		updateData();
		resetChanges();
	}

	/**
	 * Sends the given data to the client. Starts a new thread so the thread calling
	 * does not wait for send to finish.
	 * @param data
	 *     The data to send
	 */
	public void write(byte[] data)
	{
		// System.out.println("Writing: " + Arrays.toString(data));
		if(soc.isClosed())
		{
			endPlayer();
		}
		else
		{
			new Thread(()->
			{
				try
				{
					OutputStream out = soc.getOutputStream();
					out.write(data);
				}
				catch(SocketException e)
				{
					endPlayer();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}).start();
		}
	}

	/**
	 * Returns whether the number of nuts has changed since the last update reset.
	 * @return True if there's a change, false otherwise
	 */
	public boolean nutsChanged()
	{
		return squirrel.getNumNuts() != lastNuts;
	}

	/**
	 * Returns whether the player x location has changed since the last update
	 * reset.
	 * @return True if there's a change, false otherwise
	 */
	public boolean xChanged()
	{
		return squirrel.getX() != lastX;
	}

	/**
	 * Returns whether the player x location has changed since the last update
	 * reset.
	 * @return True if there's a change, false otherwise
	 */
	public boolean yChanged()
	{
		return squirrel.getY() != lastY;
	}

	/**
	 * Sets all changes to false.
	 */
	public void resetChanges()
	{
		lastNuts = squirrel.getNumNuts();
		lastX = squirrel.getX();
		lastY = squirrel.getY();
	}

	/**
	 * Returns the squirrel for the client.
	 * @return The client's squirrel
	 */
	public Squirrel getSquirrel()
	{
		return squirrel;
	}

	/**
	 * Moves the squirrel in the given direction and checks if they have collected
	 * any nuts, are touching another player, or have reached the edge of the
	 * screen.
	 * @param clients
	 *     The list of clients used to check if they're touching
	 * @param nuts
	 *     The list of nuts usd to check if the player collected a nut
	 */
	public void doMovement(DoublyLinkedList<Client> clients, DoublyLinkedList<Nut> nuts)
	{
		switch(squirrel.getDirection())
		{
			case UP:
				squirrel.move(0, -2);
				break;
			case DOWN:
				squirrel.move(0, 2);
				break;
			case LEFT:
				squirrel.move(-2, 0);
				break;
			case RIGHT:
				squirrel.move(2, 0);
			default:
				break;
		}
		checkNuts(nuts);
		checkTouching(clients);
		checkBoundry();
	}

	/**
	 * Checks if a player is touching another player based on if their rectangles
	 * intersects. Moves players apart if they are touching with the amount of
	 * movement based on number of nuts.
	 * @param clients
	 *     The list of clients to use
	 */
	private void checkTouching(DoublyLinkedList<Client> clients)
	{
		for(Client c : clients)
		{
			Squirrel other = c.squirrel;
			if(squirrel.touched(other.getRect()))
			{
				int moveDist;
				if(squirrel.getNumNuts() > other.getNumNuts())
				{
					moveDist = 10 + squirrel.getNumNuts();
					if(moveDist > 50)
					{
						moveDist = 50;
					}
					switch(squirrel.getDirection())
					{
						case UP:
							other.move(0, -moveDist);
							break;
						case DOWN:
							other.move(0, moveDist);
							break;
						case LEFT:
							other.move(-moveDist, 0);
							break;
						case RIGHT:
							other.move(moveDist, 0);
							break;
						case STILL:
							break;
					}
				}
				else if(squirrel.getNumNuts() < other.getNumNuts())
				{
					moveDist = 10 + other.getNumNuts();
					if(moveDist > 50)
					{
						moveDist = 50;
					}
					switch(other.getDirection())
					{
						case UP:
							squirrel.move(0, -moveDist);
							break;
						case DOWN:
							squirrel.move(0, moveDist);
							break;
						case LEFT:
							squirrel.move(-moveDist, 0);
							break;
						case RIGHT:
							squirrel.move(moveDist, 0);
							break;
						case STILL:
							break;
					}
				}
				else
				{
					moveDist = 10 + squirrel.getNumNuts();
					if(moveDist > 50)
					{
						moveDist = 50;
					}
					switch(squirrel.getDirection())
					{
						case UP:
							other.move(0, -moveDist);
							squirrel.move(0, moveDist);
							break;
						case DOWN:
							other.move(0, moveDist);
							squirrel.move(0, -moveDist);
							break;
						case LEFT:
							other.move(-moveDist, 0);
							squirrel.move(moveDist, 0);
							break;
						case RIGHT:
							other.move(moveDist, 0);
							squirrel.move(-moveDist, 0);
							break;
						case STILL:
							break;
					}
				}
			}
		}
	}

	/**
	 * Checks if the squirrel collected a nut by checking if the nut point is within
	 * the squirrel's rectangle. Adds a nut to the squirrel and removes nut from
	 * list if the player collected the nut.
	 * @param nuts
	 *     The list of nuts to use
	 */
	private void checkNuts(DoublyLinkedList<Nut> nuts)
	{
		for(Nut n : nuts)
		{
			if(squirrel.touched(n.getX(), n.getY()))
			{
				squirrel.addNut();
				nuts.remove(n);
			}
		}
	}

	/**
	 * Checks if the player has reached the screen boundry. Moves the player back
	 * onto the screen if they have.
	 */
	private void checkBoundry()
	{
		if(squirrel.getX() < 0)
		{
			squirrel.setLocation(0, squirrel.getY());
		}
		else if(squirrel.getX() > 900)
		{
			squirrel.setLocation(900, squirrel.getY());
		}

		if(squirrel.getY() < 0)
		{
			squirrel.setLocation(squirrel.getX(), 0);
		}
		else if(squirrel.getY() > 900)
		{
			squirrel.setLocation(squirrel.getX(), 900);
		}
	}

	/**
	 * Continuously reads data sent from the client to update the direction the
	 * player is moving.
	 */
	private void updateData()
	{
		new Thread(()->
		{
			InputStream in = null;
			while(null == in)
			{
				try
				{
					in = soc.getInputStream();
				}
				catch(IOException e)
				{

				}
			}

			while(!soc.isClosed())
			{
				try
				{
					if(in.available() >= 1)
					{
						char got = (char) in.read();
						Movement dir = Movement.charToMov(got);
						if(null != dir)
						{
							squirrel.setMovement(dir);
						}
						else
						{
							if('N' == got)
							{
								byte[] nameBytes = new byte[in.read()];
								in.read(nameBytes);
								squirrel.setName(new String(nameBytes));
							}
							else if('Q' == got)
							{
								endPlayer();
							}
						}

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
		}).start();
	}

	/**
	 * Removes player from list of clients and closes client socket
	 */
	private void endPlayer()
	{
		Host.removeClient(this);
		try
		{
			soc.close();
		}
		catch(IOException e)
		{
		}
	}

	@Override
	public boolean equals(Object other)
	{
		if(other == null || this.getClass() != other.getClass())
		{
			return false;
		}

		Client o = (Client) other;

		return this.squirrel.getID() == o.squirrel.getID();
	}
}