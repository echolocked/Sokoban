/**
 * A game with the goal to push all boxes to the storage locations.
 * @version beta 12/27/2014
 * @author Echo Liu
 * 
 */

package org;

import javax.swing.JFrame;

public class SokomanGame extends JFrame {
	
	public static void main(String[] args) {
		JFrame window = new JFrame("Sokoban");
		SokobanPanel gamePanel = new SokobanPanel(window);
		window.setSize(580, 600);
		window.setLocation(100, 100);	
		window.setContentPane(gamePanel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
