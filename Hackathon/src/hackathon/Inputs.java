package stuff;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Inputs implements KeyListener {

	public static void main(String[] args) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		switch(e.getKeyCode()){
			case 87: 
				System.out.println("W pressed");
				break;
			case 65: 
				System.out.println("A pressed");
				break;
			case 83: 
				System.out.println("S pressed");
				break;
			case 68: 
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
