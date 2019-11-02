package hackathon;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

@SuppressWarnings("serial")
public class Paint extends JPanel {
		
	private static DoublyLinkedList<Squirrel> squirrels;

	public Paint() {
		super();
		squirrels = new DoublyLinkedList<>();
		addKeyListener(new Inputs());
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
	}

	private void updateData(Socket client)
	{
		new Thread(()->
		{
			InputStream in = null;
			try
			{
				in = client.getInputStream();
			}
			catch(IOException e)
			{

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
