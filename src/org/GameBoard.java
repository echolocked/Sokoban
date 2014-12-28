package org;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameBoard extends JPanel {
	private JFrame frame;
	public Map map;
	private Image[] imgs = new Image[7];
	// private AudioClip acMove;
	// private AudioClip acMatch;
	private int width = 27;
	public int N = 20;
	public int[] levels;
	public int crtLevel;
	public int step;
	public Font stepFont;

	public GameBoard() {}
	
	public GameBoard(JFrame frame, int numLevels) {
		this.frame = frame;
		setPreferredSize(new Dimension(width*N, width*N));
		setBackground(Color.BLACK);
		stepFont = new Font(Font.SANS_SERIF, Font.BOLD, width-1);
		
		// load images
		String filename;
		File imgFolder = new File("src/images");
		try {
			for (int i = 0; i <= 6; i++) {
				filename = "wall" + i + ".jpg";
				File file = new File(imgFolder, filename);
				imgs[i] = ImageIO.read(file);
			}
		} catch (IOException e) {
			System.out.println("Error loading images. Exit.");
			System.exit(ERROR);
		}
		
		// Lock levels
		levels = new int[numLevels];
		for (int k = 0; k < numLevels; k++) {
			levels[k] = 0;
		}
		levels[0] = 1;
		crtLevel = 0;
		step = 0;
		gotoLevel(crtLevel);
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				requestFocus();
			}
		});
		
		addKeyListener(new KeyHandler());
	}

	public void gotoLevel(int k) {
		String filename = "src/levels/level" + k + ".txt";
		try {
			map = new Map(filename);
			crtLevel = k;
			step = 0;
			repaint();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Cannot load map. Exit.");
			System.exit(1);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// draw the map
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				int k = map.wall[i][j];
				if (k == 7) {
					k = 4;
				}
				g.drawImage(imgs[k], j*width, i*width, (j+1)*width-1, (i+1)*width-1, 
						0, 0, imgs[k].getWidth(this), imgs[k].getHeight(this), this);
			}
		}
		
		// draw the steps
		g.setColor(Color.RED);
		g.setFont(stepFont);
		g.drawString("" + step, (N-3)*width, width);
	}
	
	private void checkWin() {
		int numMatch = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (map.wall[i][j] == 5) {
					numMatch++;
				}
			}
		}
		if (numMatch == map.M) {
			if (crtLevel < levels.length - 1) {
				JOptionPane.showMessageDialog(frame,
					    "Awesome! Next level unlocked.");
				levels[++crtLevel] = 1;
				gotoLevel(crtLevel);
			}
			else {
				JOptionPane.showMessageDialog(frame,
					    "Awesome! You've passed all levels!");
				gotoLevel(crtLevel);
			}
		}
	}
	
	/* Return ture if (x1+dx, y1+dy) is a valid move. 
	 * Return false otherwise. */
	private boolean move(int step, int x1, int y1, int dx, int dy) {
		if (step > 2) {
			return false;
		}
		int x2 = x1 + dx;
		int y2 = y1 + dy;
		// valid move
		if (map.wall[x2][y2] == 0) {
			if (map.wall[x1][y1] == 3) {
				map.wall[x2][y2] = 3;
				map.wall[x1][y1] = 0;
				return true;
			}
			else if (map.wall[x1][y1] == 5) {
				map.wall[x2][y2] = 3;
				map.wall[x1][y1] = 2;
				return true;
			}
			else if (map.wall[x1][y1] == 4) {
				map.wall[x2][y2] = 4;
				map.wall[x1][y1] = 0;
				map.x = x2;
				map.y = y2;
				return true;
			}
			else if (map.wall[x1][y1] == 7) {
				map.wall[x2][y2] = 4;
				map.wall[x1][y1] = 2;
				map.x = x2;
				map.y = y2;
				return true;
			}
			else {
				return false;
			}
		}
		if (map.wall[x2][y2] == 2) {
			if (map.wall[x1][y1] == 3) {
				map.wall[x2][y2] = 5;
				map.wall[x1][y1] = 0;
				return true;
			}
			else if (map.wall[x1][y1] == 5) {
				map.wall[x2][y2] = 5;
				map.wall[x1][y1] = 2;
				return true;
			}
			else if (map.wall[x1][y1] == 4) {
				map.wall[x2][y2] = 7;
				map.wall[x1][y1] = 0;
				map.x = x2;
				map.y = y2;
				return true;
			}
			else if (map.wall[x1][y1] == 7) {
				map.wall[x2][y2] = 7;
				map.wall[x1][y1] = 2;
				map.x = x2;
				map.y = y2;
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (move(++step, x2, y2, dx, dy)) {
				return move(1, x1, y1, dx, dy);
			}
			else {
				return false;
			}
		}
	}
	
	public class KeyHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_UP) {
				if (move(1, map.x, map.y, -1, 0)) {
					step++;
					repaint();
					checkWin();
				}
			}
			else if (key == KeyEvent.VK_DOWN) {
				if (move(1, map.x, map.y, 1, 0)) {
					step++;
					repaint();
					checkWin();
				}
			}
			else if (key == KeyEvent.VK_LEFT) {
				if (move(1, map.x, map.y, 0, -1)) {
					step++;
					repaint();
					checkWin();
				}
			}
			else if (key == KeyEvent.VK_RIGHT) {
				if (move(1, map.x, map.y, 0, 1)) {
					step++;
					repaint();
					checkWin();
				}
			}
			else {
				;
			}
		}
	}

}
