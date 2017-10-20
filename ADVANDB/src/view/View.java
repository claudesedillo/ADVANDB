package view;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.Controller;

public class View extends JFrame{

	private CardLayout cl = new CardLayout();
	private JPanel mainPanel = new JPanel();
	private MainMenu mainMenu = new MainMenu();
	private MainProgram mainProgram = new MainProgram();
	private Controller controller;
	
	public View(Controller controller) {
		this.controller = controller;
		initialize();
	}

	private void initialize() {
		setBounds(100, 100, 650, 466);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(mainPanel);
		setVisible(true);
		setResizable(false);
		mainPanel.setLayout(cl);
		addPanels();
		cl.show(mainPanel, "main menu");
		
		initializeALs();
	}
	
	private void addPanels(){
		mainPanel.add(mainMenu, "main menu");
		mainPanel.add(mainProgram, "main program");
	}

	private void initializeALs(){
		mainMenu.addStartAL(new startAL());
		mainProgram.addQueryAL(controller.new queryAL());
	}
	
	class startAL implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			cl.show(mainPanel, "main program");
		}
	}
	
}
