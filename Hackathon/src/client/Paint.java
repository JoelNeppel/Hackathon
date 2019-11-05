package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import communication.ByteHelp;
import nutty.Constants;
import nutty.Nut;
import nutty.Squirrel;
import nutty.SquirrelNutComparator;
import nutty.Tree;

@SuppressWarnings("serial")
public class Paint extends JPanel
{

	private static ArrayList<Squirrel> squirrels;

	private static Nut[] nuts = new Nut[100];

	private static BufferedImage FirstPlace = null;

	private static BufferedImage SecondPlace = null;

	private static BufferedImage ThirdPlace = null;

	private static BufferedImage BasicImage = null;

	private static SquirrelNutComparator compare = new SquirrelNutComparator();

	public Paint()
	{
		super();
		Socket client = null;
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
		squirrels = new ArrayList<>();
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
		JFrame frame = new JFrame();
		Paint panel = new Paint();

		frame.pack();
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);

		panel.requestFocus();
		panel.setBackground(Color.CYAN);
		Tree.setLoc(frame.getWidth(), frame.getHeight());
	}

	@Override
	public void paint(Graphics g)
	{
		super.paint(g);

		g.setColor(Color.GREEN);
		g.fillRect(0, 800, 1000, 200);

		Tree.draw(g);

		squirrels.sort(compare);

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
		for(int i = 0; i < nuts.length; ++i)
		{
			if(nuts[i] != null)
			{
				nuts[i].drawNut(g);
			}
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
				try
				{
					byte[] bytes = new byte[4];
					in.read(bytes);
					int numSquirrels = ByteHelp.bytesToInt(bytes);
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

						int result = squirrels.lastIndexOf(new Squirrel(id, 0, 0));
						Squirrel s;
						if(result == -1)
						{
							s = new Squirrel(id, x, y);
							squirrels.add(s);
						}
						else
						{
							s = squirrels.get(result);
							s.setLocation(x, y);
						}
						s.setNuts(squirrelNuts);

					}

					for(int i = 0; i < nuts.length; i++)
					{
						if(i < numNuts)
						{
							in.read(bytes);
							int x = ByteHelp.bytesToInt(bytes);
							in.read(bytes);
							int y = ByteHelp.bytesToInt(bytes);

							nuts[i] = new Nut(x, y);
						}
						else
						{
							nuts[i] = null;
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
}