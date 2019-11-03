package hackathon;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Paint extends JPanel {
		
	private static DoublyLinkedList<Squirrel> squirrels;
	private static Nut[] nuts = new Nut[100];
	private static BufferedImage FirstPlace = null;
	private static BufferedImage SecondPlace = null;
	private static BufferedImage ThirdPlace = null;
	private static BufferedImage BasicImage = null;
	private static SquirrelComparator compare = new SquirrelComparator();

	public Paint() {
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
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Paint panel = new Paint();

		frame.pack();
		frame.setSize(1000,1000);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);

		panel.requestFocus();
		panel.setBackground(Color.CYAN);
		Tree.setLoc(frame.getWidth(), frame.getHeight());
	}
	
	public void paint(Graphics g) {
		super.paint(g);

		g.setColor(Color.GREEN);
		g.drawRect(0, 800, 1000, 200);

		Tree.draw(g);

		squirrels.rank(compare);

		System.out.println(squirrels.size());
		for (int i = 0; i < squirrels.size(); ++i) 
		{
			System.out.println(squirrels.get(i));
			if (0 == i)
			{
				g.drawImage(FirstPlace, squirrels.get(i).getX(), squirrels.get(i).getY(), this);
			}
			else if (1 == i)
			{
				g.drawImage(SecondPlace, squirrels.get(i).getX(), squirrels.get(i).getY(), this);
			}
			else if (2 == i)
			{
				g.drawImage(ThirdPlace, squirrels.get(i).getX(), squirrels.get(i).getY(), this);
			}
			else
			{
				g.drawImage(BasicImage, squirrels.get(i).getX(), squirrels.get(i).getY(), this);
			}
		}
		for (int i = 0; i < nuts.length; ++i) {
			if (nuts[i] != null) {
				nuts[i].drawNut(g);
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
					byte[] bytes = new byte[4];
					in.read(bytes);
					int numSquirrels = ByteHelp.bytesToInt(bytes);
					in.read(bytes);
					int numNuts = ByteHelp.bytesToInt(bytes);
					System.out.println("Squirrels: " + numSquirrels + " Nuts: " + numNuts);
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

						Squirrel s = squirrels.get(new Squirrel(id,0,0));
						if (null == s) 
						{
							s = new Squirrel(id, x, y);
							squirrels.add(s);
						}
						else
						{
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
