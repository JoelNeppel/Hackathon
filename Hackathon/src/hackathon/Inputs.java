package hackathon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.Socket;
import java.io.IOException;
import java.io.OutputStream;

public class Inputs implements KeyListener {

	private OutputStream out;

	public Inputs(Socket client)
	{
		try 
		{
			out = client.getOutputStream();
		}
		catch(IOException e) {

		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

/*87 - W
65 - A
83 - S
68 - D
27 - Escape
32 - Space */

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e.getKeyChar());
		try
		{
			switch(e.getKeyChar()){
				case 'w': 
					out.write(ByteHelp.toBytes(Movement.UP.getNum()));
					break;
				case 'a': 
					out.write(ByteHelp.toBytes(Movement.LEFT.getNum()));
					break;
				case 's': 
					out.write(ByteHelp.toBytes(Movement.DOWN.getNum()));
					break;
				case 'd': 
					out.write(ByteHelp.toBytes(Movement.RIGHT.getNum()));
					break;
			}
		}
		catch(IOException r)
		{

		}
		
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		try 
		{
			out.write(ByteHelp.toBytes(Movement.STILL.getNum()));
		}
		catch(IOException r)
		{

		}
	}

}
