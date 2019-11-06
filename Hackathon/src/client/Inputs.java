package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import nutty.Movement;

public class Inputs implements KeyListener
{

	private OutputStream out;

	public Inputs(Socket client)
	{
		try
		{
			out = client.getOutputStream();
		}
		catch(IOException e)
		{

		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	/*
	 * 87 - W 65 - A 83 - S 68 - D 27 - Escape 32 - Space
	 */

	@Override
	public void keyPressed(KeyEvent e)
	{
		try
		{
			switch(e.getKeyChar())
			{
				case 'w':
				case 'W':
					out.write(Movement.UP.getChar());
					break;
				case 'a':
				case 'A':
					out.write(Movement.LEFT.getChar());
					break;
				case 's':
				case 'S':
					out.write(Movement.DOWN.getChar());
					break;
				case 'd':
				case 'D':
					out.write(Movement.RIGHT.getChar());
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
			out.write(Movement.STILL.getChar());
		}
		catch(IOException r)
		{

		}
	}

}
