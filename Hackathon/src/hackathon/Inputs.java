package hackathon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Inputs implements KeyListener {

	public static void main(String[] args) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println(e.getKeyChar());
		switch(e.getKeyChar()){
			case 'w': 
				System.out.println("W pressed");
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
