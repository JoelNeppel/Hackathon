package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import communication.ByteHelp;
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
					out.write(ByteHelp.toBytes(Movement.UP.getNum()));
					break;
				case 'a':
				case 'A':
					out.write(ByteHelp.toBytes(Movement.LEFT.getNum()));
					break;
				case 's':
				case 'S':
					out.write(ByteHelp.toBytes(Movement.DOWN.getNum()));
					break;
				case 'd':
				case 'D':
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
