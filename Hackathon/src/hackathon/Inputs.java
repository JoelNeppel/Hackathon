package hackathon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;

public class Inputs implements KeyListener {

	private Socket client;
	private OutputStream out;

	public Inputs(Socket client)
	{
		this.client = client;
		try 
		{
			out = client.getOutputStream();
		}
		catch(IOException e) {

		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println(e.getKeyChar());
		Packet p = new Packet();
		switch(e.getKeyChar()){
			case 'w': 
				
				break;
			case 'a': 
				System.out.println("A pressed");
				break;
			case 's': 
				System.out.println("S pressed");
				break;
			case 'd': 
				System.out.println("D pressed");
				break;
		}
	}

/*87 - W
65 - A
83 - S
68 - D
27 - Escape
32 - Space */

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
