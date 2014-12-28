package org;

import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

public class SokobanPanel extends JPanel 
												implements ActionListener {
	protected JFrame frame;
	protected GameBoard board;
	protected JMenuBar menuBar;
	protected JTextField textField;
	protected JTextArea textArea;
	protected JFileChooser fc;
	
	public SokobanPanel(JFrame frame) {
		this.frame = frame;
		int numLevels = getNumLevels();
		board = new GameBoard(frame, numLevels);
		menuBar = getMenuBar(numLevels);
		textArea = getTextArea();
		textField = getTextField();
		fc = new JFileChooser();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(menuBar);
		add(board);
		add(textField);
		add(textArea);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				board.requestFocus();
			}
		});
	}
	
	public static int getNumLevels() {
		File levelFolder = new File("src/levels/");
		String files[] = levelFolder.list();	
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".txt")) {
				count++;
			}
		}
		return count;
	}
	
	public JTextField getTextField() {
		final JTextField textField = new JTextField();
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				commandParser(textField.getText());
			}
		});
		return textField;
	}
	
	public void commandParser(String command) {
		switch (command) {
		case "showmeyourmoney":
				for (int i = 0; i < board.levels.length; i++) {
					board.levels[i] = 1;
					textArea.setText("All levels unlocked.\nClick the game board to restore focus.");
				}
				break;
		case "exit":
			System.exit(0);
		default:
			textArea.setText("Invalid command.\nClick the game board to restore focus.");
			break;
		}
	}
	
	public JTextArea getTextArea() {
		JTextArea textArea = new JTextArea();
		textArea.setRows(2);
		textArea.setEditable(false);
		return textArea;
	}
	
	public JMenuBar getMenuBar(int numLevels) {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		menuBar.add(file);
		
		JMenuItem restart = new JMenuItem("Restart");
		restart.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_R, Event.CTRL_MASK));
		restart.addActionListener(this);
		file.add(restart);
		
		JMenuItem save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, Event.CTRL_MASK));
		save.addActionListener(this);
		file.add(save);
		
		JMenuItem load = new JMenuItem("Load");
		load.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_L, Event.CTRL_MASK));
		load.addActionListener(this);
		file.add(load);
		
		JMenu level = new JMenu("Level");
		level.setMnemonic(KeyEvent.VK_L);
		menuBar.add(level);
		
		JMenuItem[] levels = new JMenuItem[numLevels];
		String itemName;
		for (int i = 0; i < numLevels; i++) {
			itemName = "Level " + i;
			levels[i] = new JMenuItem(itemName);
			levels[i].addActionListener(this);
			level.add(levels[i]);
		}
		
		return menuBar;
	}
	
	public void actionPerformed(ActionEvent e) {
		String itemName = e.getActionCommand();
		if ("Restart".equals(itemName)) {
			board.gotoLevel(board.crtLevel);
			textArea.setText(itemName + " loaded successfully.");
		}
		else if ("Save".equals(itemName)) {
			int returnVal = fc.showOpenDialog(SokobanPanel.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					FileOutputStream fo = new FileOutputStream(file);
					ObjectOutputStream oo = new ObjectOutputStream(fo);
					oo.writeObject(board.map);
					oo.writeObject(board.history);
					oo.writeObject(board.levels);
					oo.writeObject(board.crtLevel);
					oo.writeObject(board.step);
					oo.writeObject(board.stepFont);
					oo.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					textArea.setText("Failed saving file.");
					return;
				}
				textArea.setText("File saveed successfully.");
			}
		}
		else if ("Load".equals(itemName)) {
			int returnVal = fc.showOpenDialog(SokobanPanel.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					FileInputStream fi = new FileInputStream(file);
					ObjectInputStream oi = new ObjectInputStream(fi);
					board.map = (Map)oi.readObject();
					board.history = (LinkedList<Map>)oi.readObject();
					board.levels = (int[])oi.readObject();
					board.crtLevel = (int)oi.readObject();
					board.step = (int)oi.readObject();
					board.stepFont = (Font)oi.readObject();
					oi.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					textArea.setText("Failed loading file.");
					return;
				}
				textArea.setText("File loaded successfully.");
				board.repaint();
			}
		}
		else {
			int level = Integer.parseInt(itemName.split(" ")[1]);
			// the level is unlocked
			if (board.levels[level] == 1) {
				board.gotoLevel(level);
				textArea.setText(itemName + " loaded successfully.");
			}
			else {
				textArea.setText(itemName + " is locked.");
			}
		}
	}
	

}
