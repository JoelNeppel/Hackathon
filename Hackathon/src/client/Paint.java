package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import communication.DataTransfer;
import communication.DataTransfer.TransferType;
import nutty.Constants;
import nutty.DoublyLinkedList;
import nutty.Nut;
import nutty.Squirrel;
import nutty.SquirrelNutComparator;
import nutty.Tree;

@SuppressWarnings("serial")
public class Paint extends JPanel implements WindowListener
{

	private static DoublyLinkedList<Squirrel> squirrels = new DoublyLinkedList<>();

	private static DoublyLinkedList<Nut> nuts = new DoublyLinkedList<>();

	private static BufferedImage FirstPlace = null;

	private static BufferedImage SecondPlace = null;

	private static BufferedImage ThirdPlace = null;

	private static BufferedImage BasicImage = null;

	private static SquirrelNutComparator compare = new SquirrelNutComparator();

	private Socket client;

	public Paint()
	{
		super();
		while(null == client)
		{
			try
			{
				client = new Socket(Constants.ADDRESS, Constants.PORT);
			}
			catch(IOException e)
			{

			}

		}
		updateData(client);
		squirrels = new DoublyLinkedList<>();
		addKeyListener(new Inputs(client));

		try
		{
			FirstPlace = ImageIO.read(this.getClass().getResource(Constants.SQUIRREL_FIRST_PATH));
			SecondPlace = ImageIO.read(this.getClass().getResource(Constants.SQUIRREL_SECOND_PATH));
			ThirdPlace = ImageIO.read(this.getClass().getResource(Constants.SQUIRREL_THIRD_PATH));
			BasicImage = ImageIO.read(this.getClass().getResource(Constants.SQUIRREL_BASIC_PATH));
		}
		catch(IOException e)
		{

		}
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Nutty.io");
		Paint panel = new Paint();
		JPanel menu = new JPanel();
		JTextField username = new JTextField(25);

		menu.setLocation(0, 900);
		menu.setSize(450, 100);
		panel.add(menu);

		username.setLocation(25, 5);
		username.setSize(250, 40);
		menu.add(username);

		panel.setLocation(0, 0);
		panel.setSize(1000, 1000);
		panel.setBackground(Color.CYAN);

		frame.setVisible(true);
		frame.addWindowListener(panel);
		frame.setContentPane(panel);
		frame.pack();
		frame.setSize(1000, 1000);

		panel.requestFocus();
		Tree.setLoc(frame.getWidth(), frame.getHeight());

	}

	@Override
	public synchronized void paint(Graphics g)
	{
		System.out.println("Painting");
		super.paint(g);

		g.setColor(Color.GREEN);
		g.fillRect(0, 800, 1000, 200);

		Tree.draw(g);

		System.out.println("Compare");
		// squirrels.sort(compare);

		for(int i = 0; i < squirrels.size(); ++i)
		{
			if(0 == i)
			{
				g.drawImage(FirstPlace, squirrels.get(i).getX(), squirrels.get(i).getY(), this);
			}
			else if(1 == i)
			{
				g.drawImage(SecondPlace, squirrels.get(i).getX(), squirrels.get(i).getY(), this);
			}
			else if(2 == i)
			{
				g.drawImage(ThirdPlace, squirrels.get(i).getX(), squirrels.get(i).getY(), this);
			}
			else
			{
				g.drawImage(BasicImage, squirrels.get(i).getX(), squirrels.get(i).getY(), this);
			}
		}
		System.out.println("nuts");
		for(Nut n : nuts)
		{
			n.drawNut(g);
		}

		System.out.println("Scoreboard");
		scoreBoard(g);
		System.out.println("Done paint");
		// bottomMenu(g);
	}

	/*
	 * private void bottomMenu(Graphics g) { g.setColor(new Color(0, 0, 0, 127));
	 * g.fillRect(0, 900, 450, 100);; }
	 */

	private void scoreBoard(Graphics g)
	{
		g.setColor(new Color(0, 0, 0, 127));
		g.fillRect(800, 0, 200, 220);
		g.setColor(Color.WHITE);
		for(int i = 0; i < squirrels.size(); ++i)
		{
			if(i > 9)
			{
				return;
			}
			g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
			g.drawString((i + 1) + ": " + squirrels.get(i).getID() + " - " + squirrels.get(i).getNumNuts(), 810,
					25 + (20 * i));

		}

	}

	private void updateData(Socket client)
	{
		new Thread(()->
		{
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

			while(!Thread.interrupted())
			{
				System.out.println("Receiving");
				try
				{
					int got = in.read();
					System.out.println("Got:: " + got);
					System.out.println("Got: " + (char) got);
					TransferType com = TransferType.charToTransfer((char) got);
					System.out.println(com);
					while(null != com && TransferType.DONE != com)
					{
						switch(com)
						{
							case FULL:
								DataTransfer.receiveFullUpdate(in, nuts, squirrels);
								break;
							case ADD_NUT:
								DataTransfer.receiveNutAddition(in, nuts);
								break;
							case ADD_PLAYER:
								DataTransfer.receiveAddPlayer(in, squirrels);
								break;
							case ADD_PLAYER_NUT:
								DataTransfer.performAddNut(in, squirrels);
								break;
							case PLAYER_X:
								DataTransfer.performXUpdates(in, squirrels);
								break;
							case PLAYER_Y:
								DataTransfer.performYUpdates(in, squirrels);
								break;
							case REMOVE_NUT:
								DataTransfer.receiveNutRemoval(in, nuts);
								break;
							case REMOVE_PLAYER:
								DataTransfer.receivePlayerRemoval(in, squirrels);
								break;
							case SET_PLAYER_NUTS:
								DataTransfer.performNutSet(in, squirrels);
								break;
							default:
								break;
						}
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				repaint();
			}

		}).start();
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		try
		{
			client.getOutputStream().write('Q');
		}
		catch(IOException e1)
		{
		}
		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
	}
}