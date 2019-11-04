package host;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import nutty.DoublyLinkedList;
import nutty.Nut;
import nutty.Squirrel;

/**
 * @author JoelNeppel
 *
 */
public class Client
{
	private Socket soc;

	private Squirrel squirrel;

	public Client(Socket socket, Squirrel player)
	{
		soc = socket;
		squirrel = player;
	}

	public void write(byte[] data)
	{
		new Thread(()->
		{
			try
			{
				OutputStream out = soc.getOutputStream();
				out.write(data);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}).start();
	}

	public Squirrel getSquirrel()
	{
		return squirrel;
	}

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
}