package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class MainProgram extends JPanel{

	private Image backgroundImage = new ImageIcon(this.getClass().getResource("/welcomeScreenImage.png")).getImage();
	private JLabel backgroundImageLabel;
	private JTable resultTable;
	private DefaultTableModel model;
	private JComboBox<String> numberOfTables, queryVariant, queryVersion;
	private DefaultComboBoxModel<String> queryVariantList, queryVersionList;
	private JLabel lblSelectQueryVariant;
	private JLabel lblNumOfTables;
	private JLabel lblSelectQueryVersion;
	private JLabel txtSelectFrom;
	private ItemListener numberOfTablesListener;
	private JButton query;
	private String[] oneTableVariants = {"1.1 - Select all books", "1.2 -Select all publishers"};
	private String[] twoTableVariants = {"2.1 - Select all books and their publishers", "2.2 - Select number of unique books in each branch"};
	private String[] threeTableVariants = {"3.1 - Select all books that were never borrowed in each branch"};
	private String[] fourTableVariants = {"4.1 - Select most popular book for each branch", "4.2 - Select least popular book for each branch"};
	private String[] oneTable1stVariantQuery = {"SELECT * FROM books, SELECT * FROM books"};
	private String[] oneTable2ndVariantQuery = {"SELECT * FROM publishers, SELECT * FROM publishers"};
	private String[] twoTable1stVariantQuery = {};
	private String[] twoTable2ndVariantQuery = {};
	private String[] threeTable1stVariantQuery = {};
	private String[] threeTable2ndVariantQuery = {};
	private String[] fourTable1stVariantQuery = {};
	private String[] fourTable2ndVariantQuery = {};
	
	public MainProgram(){
		init();
	}
	
	public void init(){
		setBackground(Color.LIGHT_GRAY);
		setLayout(null);
		
		query = new JButton("Query");
		query.setBounds(44, 395, 143, 33);
		query.setForeground(Color.WHITE);
		query.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
		query.setBackground(new Color(40,40,92));
		query.setOpaque(true);
		query.setBorderPainted(false);
		query.setCursor(new Cursor(Cursor.HAND_CURSOR));
		add(query);
		
		resultTable = new JTable();
		resultTable.setBounds(247, 105, 393, 350);
		model = new DefaultTableModel(3, 3);
		resultTable.setModel(model);
		add(resultTable);
		
		lblNumOfTables = new JLabel("Select Number of Tables");
		lblNumOfTables.setBounds(10, 112, 227, 14);
		add(lblNumOfTables);
		
		numberOfTables = new JComboBox<String>();
		numberOfTables.setBounds(10, 126, 227, 20);
		numberOfTables.addItem("1 Table");
		numberOfTables.addItem("2 Tables");
		numberOfTables.addItem("3 Tables");
		numberOfTables.addItem("4-6 Tables");
		add(numberOfTables);
		addNumberOfTablesListener();
		 
		queryVariantList = new DefaultComboBoxModel<String>(oneTableVariants);
		lblSelectQueryVariant = new JLabel("Select Query Variant");
		lblSelectQueryVariant.setBounds(10, 157, 227, 14);
		add(lblSelectQueryVariant);
		queryVariant = new JComboBox<String>();
		queryVariant.setBounds(10, 182, 227, 20);
		queryVariant.setModel(queryVariantList);
		add(queryVariant);
		
		lblSelectQueryVersion = new JLabel("Select Query Version");
		lblSelectQueryVersion.setBounds(10, 213, 227, 14);
		add(lblSelectQueryVersion);
		
		queryVersion = new JComboBox<String>();
		queryVersion.setBounds(10, 236, 227, 20);
		add(queryVersion);
		
		txtSelectFrom = new JLabel();
		txtSelectFrom.setVerticalAlignment(SwingConstants.TOP);
		txtSelectFrom.setBounds(10, 267, 227, 119);
		add(txtSelectFrom);
		
		backgroundImageLabel = new JLabel();
		backgroundImageLabel.setBounds(0, 0, 650, 466);
		backgroundImageLabel.setIcon(new ImageIcon(backgroundImage));
		add(backgroundImageLabel);
	}
	
	public void addNumberOfTablesListener(){
	    numberOfTablesListener = new ItemListener() {
		      public void itemStateChanged(ItemEvent itemEvent) {
		    	  changeVariantContent(itemEvent.getItem().toString());
		      }
		    };
		    
		numberOfTables.addItemListener(numberOfTablesListener);
	}
	
	public void changeVariantContent(String numOfTables){
		if(numOfTables.contains("1")){
			queryVariantList.removeAllElements();
			for(String i: oneTableVariants)
				queryVariantList.addElement(i);
		}
		else if(numOfTables.contains("2")){
			queryVariantList.removeAllElements();
			for(String i: twoTableVariants)
				queryVariantList.addElement(i);
		}
			
		else if (numOfTables.contains("3")){
			queryVariantList.removeAllElements();
			for(String i: threeTableVariants)
				queryVariantList.addElement(i);
		}
		else{
			queryVariantList.removeAllElements();
			for(String i: fourTableVariants)
				queryVariantList.addElement(i);
		}
	}
	
	public void addQueryAL(ActionListener e){
		query.addActionListener(e);
	}
}
