package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import communication.DataTransfer;
import nutty.Constants;
import nutty.DoublyLinkedList;
import nutty.Nut;
import nutty.Squirrel;
import nutty.SquirrelNutComparator;
import nutty.Tree;

@SuppressWarnings("serial")
public class Paint extends JPanel implements WindowListener, ActionListener
{

	private static DoublyLinkedList<Squirrel> squirrels = new DoublyLinkedList<>();

	private static DoublyLinkedList<Nut> nuts = new DoublyLinkedList<>();

	private static BufferedImage FirstPlace = null;

	private static BufferedImage SecondPlace = null;

	private static BufferedImage ThirdPlace = null;

	private static BufferedImage BasicImage = null;

	private static SquirrelNutComparator compare = new SquirrelNutComparator();

	private Socket client;

	static Paint panel;

	static JTextField username;

	static JButton enterButton;

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
		panel = new Paint();
		JPanel menu = new JPanel();
		username = new JTextField(25);
		enterButton = new JButton("Enter");

		panel.setLayout(null);

		menu.setLayout(null);
		menu.setLocation(0, 0);
		menu.setSize(450, 60);
		menu.setBackground(new Color(0, 0, 0, 127));

		panel.add(menu);

		username.setLocation(15, 5);
		username.setSize(250, 40);
		menu.add(username);

		enterButton.setActionCommand("buttonPressed");
		enterButton.addActionListener(panel);
		enterButton.setLocation(280, 10);
		enterButton.setSize(100, 30);
		menu.add(enterButton);

		panel.setLocation(0, 0);
		panel.setSize(1000, 1000);
		panel.setBackground(Color.CYAN);

		frame.setVisible(true);
		frame.addWindowListener(panel);
		frame.setContentPane(panel);
		frame.pack();
		frame.setSize(1000, 1000);

		panel.requestFocus();
		panel.revalidate();
		panel.repaint();
		Tree.setLoc(frame.getWidth(), frame.getHeight());

	}

	@Override
	public synchronized void paint(Graphics g)
	{
		super.paint(g);

		g.setColor(Color.GREEN);
		g.fillRect(0, 800, 1000, 200);

		Tree.draw(g);

		squirrels.sort(compare);

		Iterator<Squirrel> iter = squirrels.iterator();
		int i = 0;
		while(iter.hasNext())
		{
			Squirrel s = iter.next();
			if(0 == i)
			{
				g.drawImage(FirstPlace, s.getX(), s.getY(), this);
			}
			else if(1 == i)
			{
				g.drawImage(SecondPlace, s.getX(), s.getY(), this);
			}
			else if(2 == i)
			{
				g.drawImage(ThirdPlace, s.getX(), s.getY(), this);
			}
			else
			{
				g.drawImage(BasicImage, s.getX(), s.getY(), this);
			}
			i++;
		}

		for(Nut n : nuts)
		{
			n.drawNut(g);
		}

		scoreBoard(g);
	}

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
			if(squirrels.get(i).getName() == "")
			{
				g.drawString((i + 1) + ": " + squirrels.get(i).getID() + " - " + squirrels.get(i).getNumNuts(), 810, 25 + (20 * i));
			}
			else
			{
				g.drawString((i + 1) + ": " + squirrels.get(i).getName() + " - " + squirrels.get(i).getNumNuts(), 810, 25 + (20 * i));
			}

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
				try
				{
					char got = ' ';
					while('D' != got)
					{
						int g = in.read();
						got = (char) g;
						if(got != 'F')
						{
							System.out.println("Bad juju " + g + got);
							clearInput(in);

						}
						else
						{
							synchronized(this)
							{
								DataTransfer.receiveFullUpdate(in, nuts, squirrels);
							}
							g = in.read();
							got = (char) g;
							if(got != 'D')
								System.out.println("Bad juju " + g + got);
						}
					}

					// int got = in.read();
					// TransferType com = TransferType.charToTransfer((char) got);
					// while(null != com && TransferType.DONE != com)
					// {
					// switch(com)
					// {
					// case FULL:
					// DataTransfer.receiveFullUpdate(in, nuts, squirrels);
					// break;
					// case ADD_NUT:
					// DataTransfer.receiveNutAddition(in, nuts);
					// break;
					// case ADD_PLAYER:
					// DataTransfer.receiveAddPlayer(in, squirrels);
					// break;
					// case ADD_PLAYER_NUT:
					// DataTransfer.performAddNut(in, squirrels);
					// break;
					// case PLAYER_X:
					// DataTransfer.performXUpdates(in, squirrels);
					// break;
					// case PLAYER_Y:
					// DataTransfer.performYUpdates(in, squirrels);
					// break;
					// case REMOVE_NUT:
					// DataTransfer.receiveNutRemoval(in, nuts);
					// break;
					// case REMOVE_PLAYER:
					// DataTransfer.receivePlayerRemoval(in, squirrels);
					// break;
					// case SET_PLAYER_NUTS:
					// DataTransfer.performNutSet(in, squirrels);
					// break;
					// default:
					// clearInput(in);
					// break;
					// }
					// }
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				repaint();
			}

		}).start();
	}

	private void clearInput(InputStream in) throws IOException
	{
		System.out.println("\n\nCleared!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
		int got = in.read();
		while('D' != got)
		{
			got = in.read();
		}
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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("buttonPressed"))
		{

			if(null == username.getText())
			{
				return;
			}

			byte[] name = username.getText().getBytes();

			try
			{
				OutputStream out = client.getOutputStream();
				byte[] send = new byte[2 + Math.min(name.length, 10)];
				send[0] = (byte) 'N';
				send[1] = (byte) Math.min(name.length, 10);
				for(int i = 2; i < send.length; i++)
				{
					send[i] = name[i - 2];
				}

				out.write(send);
			}
			catch(IOException ex)
			{

			}
		}

		panel.grabFocus();
	}
}