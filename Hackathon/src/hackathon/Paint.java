package stuff;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;

@SuppressWarnings("serial")
public class Paint extends JPanel {
	
	Tree tree = new Tree();	
	private static DoublyLinkedList<Squirrel> squirrels;

	public Paint() {
		super();
		addKeyListener(new Inputs());
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Paint panel = new Paint();

		frame.pack();
		frame.setSize(100,100);
		frame.setVisible(true);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);

		panel.requestFocus();
	}
	
	public void paintComponent(Graphics g) {
		super.paint(g);
		g.setColor(Color.CYAN);
		g.fillRect(100, 100, 20, 30);

		tree.draw();
	}

	public void update() {
		for (int i = 0; i<squirrels.size; i++) {

		}
	}
	
}
