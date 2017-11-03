package view;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class View extends JFrame{

	private CardLayout cl = new CardLayout();
	private JPanel mainPanel = new JPanel();
	private MainProgram mainProgram = new MainProgram();
	
	public View() {
		super("ADVANDB MCO1");
		initialize();
	}

	private void initialize() {
		setBounds(100, 100, 950, 650);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(mainPanel);
		setVisible(true);
		setResizable(false);
		mainPanel.setLayout(cl);
		addPanels();
		cl.show(mainPanel, "main program");
	}
	
	private void addPanels(){
		mainPanel.add(mainProgram, "main program");
	}
	
}
