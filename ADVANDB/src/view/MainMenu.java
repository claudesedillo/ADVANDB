package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainMenu extends JPanel{
	
	private Image backgroundImage = new ImageIcon(this.getClass().getResource("/welcomeScreenImage.png")).getImage();
	private JLabel backgroundImageLabel;
	private JLabel welcome;
	private JLabel authors;
	private JButton start;
	
	public MainMenu() {
		init();
	}
	
	public void init(){
		setBackground(Color.LIGHT_GRAY);
		setLayout(null);
		
		welcome = new JLabel("Query Optimization Program");
		welcome.setBounds(155, 53, 322, 31);
		welcome.setHorizontalAlignment(SwingConstants.CENTER);
		welcome.setForeground(Color.WHITE);
		welcome.setFont(new Font("Trebuchet MS", Font.PLAIN, 25));
		add(welcome);
		
		authors = new JLabel("\u00A92017 Agno, Gano, Sedillo");
		authors.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		authors.setBounds(487, 412, 153, 24);
		add(authors);
		
		start = new JButton("Start");
		start.setBounds(210, 214, 212, 38);
		start.setForeground(Color.WHITE);
		start.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
		start.setBackground(new Color(40,40,92));
		start.setOpaque(true);
		start.setBorderPainted(false);
		start.setCursor(new Cursor(Cursor.HAND_CURSOR));
		add(start);
		
		backgroundImageLabel = new JLabel();
		backgroundImageLabel.setBounds(0, 0, 650, 466);
		backgroundImageLabel.setIcon(new ImageIcon(backgroundImage));
		add(backgroundImageLabel);
	}
	
	public void addStartAL(ActionListener e){
		start.addActionListener(e);
	}
	
}
