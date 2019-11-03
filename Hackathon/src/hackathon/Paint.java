package hackathon;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class Paint extends JPanel {
		
	private static DoublyLinkedList<Squirrel> squirrels;
	private static Nut[] nuts;
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
		Tree.setLoc(frame.getWidth(), frame.getHeight());
	}
	
	public void paint(Graphics g) {
		super.paint(g);

		Tree.draw(g);

		squirrels.rank(compare);

		for (int i = 0; i < squirrels.size(); ++i) 
		{
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
				}
				catch(IOException e)
				{

				}
			}

		}).start();
	}
}
