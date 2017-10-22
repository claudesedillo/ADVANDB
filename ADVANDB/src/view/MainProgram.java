package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import model.Database;

public class MainProgram extends JPanel{

	private Database database;
	private JTable resultTable, timeTable;
	private DefaultTableModel timeTableModel;
	private JComboBox<String> queryComboBox, queryBox;
	private ItemListener queryVariantListener, queryListener;
	private JButton query;
	private DefaultComboBoxModel<String> queryVariantListModel, queryListModel;
	private String[] queryVariants = {"Select all books with a similar name",
								  "Select all publishers with a similar name",
								  "Select book copies of a certain book", 
								  "Select all book loans",
								  "SELECT all authors with a similar name",
								  "SELECT all publishers with a similar name",
								  "SELECT the most popular books in all branches",
								  "SELECT the least popular books in all branches"};
	private String[] firstQueryVersions = {"<html> select * from publisher <br>" +
										   "where address = \"tokyo\" <br>" +
										   "or address = \"osaka\" <br></html>", 
										   "<html> select * from publisher <br> where address = \"toronto\" </html>"};
	private String[] secondQueryVersions = {"second query version 1", "second query version 2"};
	private String[] thirdQueryVersions = {"third query version 1", "third query version 2"};
	private String[] fourthQueryVersions = {"fourth query version 1", "fourth query version 2"};
	private String[] fifthQueryVersions = {"fifth query version 1", "fifth query version 2"};
	private String[] sixthQueryVersions = {"sixth query version 1", "sixth query version 2"};
	private String[] seventhQueryVersions = {"seventh query version 1", "seventh query version 2"};
	private String[] eighthQueryVersions = {"eighth query version 1", "eighth query version 2"};
	private JScrollPane timeTableScrollPane, resultTableScrollPane;
	private JScrollPane queryScrollPane;
	private JLabel queryTextLabel;
	
	public MainProgram(){
		init();
	}
	
	public void init(){
		database = new Database();
		setBackground(Color.LIGHT_GRAY);
		setLayout(null);
		setSize(new Dimension(950, 650));
		query = new JButton("Query");
		query.addActionListener(new queryAL());
		query.setBounds(530, 54, 110, 20);
		query.setForeground(Color.WHITE);
		query.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));
		query.setBackground(new Color(40,40,92));
		query.setOpaque(true);
		query.setBorderPainted(false);
		query.setCursor(new Cursor(Cursor.HAND_CURSOR));
		add(query);
		
		resultTableScrollPane = new JScrollPane();
		resultTableScrollPane.setBounds(10, 85, 650, 300);
		add(resultTableScrollPane);
		resultTable = new JTable();
		resultTableScrollPane.setViewportView(resultTable);
		
		queryVariantListModel = new DefaultComboBoxModel<String>(queryVariants);
		queryComboBox = new JComboBox<String>();
		queryComboBox.setBounds(10, 56, 250, 20);
		queryComboBox.setModel(queryVariantListModel);
		add(queryComboBox);
		
		queryListModel = new DefaultComboBoxModel<String>(firstQueryVersions);
		queryBox = new JComboBox<String>();
		queryBox.setBounds(270, 56, 250, 20);
		queryBox.setModel(queryListModel);
		addQueryListener();
		add(queryBox);
		
		timeTableModel = new DefaultTableModel(new String[]{"Query Number", "Time", "Query"}, 0){
			@Override
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		timeTableScrollPane = new JScrollPane();
		timeTableScrollPane.setBounds(10, 396, 650, 243);
		add(timeTableScrollPane);
		timeTable = new JTable();
		timeTableScrollPane.setViewportView(timeTable);
		timeTable.setModel(timeTableModel);
		
		queryScrollPane = new JScrollPane();
		queryScrollPane.setBounds(670, 85, 265, 300);
		add(queryScrollPane);
		
		queryTextLabel = new JLabel();
		queryScrollPane.setViewportView(queryTextLabel);
		queryTextLabel.setText(queryBox.getSelectedItem().toString());
		queryTextLabel.setVerticalAlignment(SwingConstants.TOP);
		

	}
	
	class queryAL implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			createQuery(removeHTML(queryBox.getSelectedItem().toString()));
			//showProfiles();
		}
	}
	
	public void addQueryListener(){
		queryVariantListener = new ItemListener(){
			public void itemStateChanged(ItemEvent itemEvent){
				changeQueryList(queryComboBox.getSelectedIndex());
			}
		};
		queryListener = new ItemListener(){
			public void itemStateChanged(ItemEvent itemEvent){
				setLabelContent(itemEvent.getItem().toString());
			}
		};
		queryComboBox.addItemListener(queryVariantListener);
		queryBox.addItemListener(queryListener);
	}
	
	public void changeQueryList(int queryNumber){
		queryListModel.removeAllElements();
		switch(queryNumber){	
		case 0:
			for(String i: firstQueryVersions)
				queryListModel.addElement(i);
				break;
		case 1:
			for(String i: secondQueryVersions)
				queryListModel.addElement(i); 
			break;
		case 2:
			for(String i: thirdQueryVersions)
				queryListModel.addElement(i);
			break;
		case 3:
			for(String i: fourthQueryVersions)
				queryListModel.addElement(i);
			break;
		case 4:
			for(String i: fifthQueryVersions)
				queryListModel.addElement(i);
			break;
		case 5:
			for(String i: sixthQueryVersions)
				queryListModel.addElement(i);
			break;
		case 6:
			for(String i: seventhQueryVersions )
				queryListModel.addElement(i);
			break;
		case 7:
			for(String i: eighthQueryVersions)
				queryListModel.addElement(i);
			break;
		}
	}
	
	public void createQuery(String query){
			System.out.println("I am here 3");
			System.out.println(query);
		try {
			Statement statement = database.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			System.out.println("column count is " + columnCount);
			System.out.println("I am here 2");
			int rowCount = 0;
			List<String> columnNames = new ArrayList<String>();
			String[] columnNamesArray = new String[columnCount];
			for(int i = 1; i <= columnCount; i++){
				String name = rsmd.getColumnName(i);
				columnNames.add(name);
			}
			columnNamesArray = columnNames.toArray(columnNamesArray);
			Object[] objects = new Object[columnCount];
			DefaultTableModel resultTableModel = new DefaultTableModel(columnNamesArray, 0){
				@Override
				public boolean isCellEditable(int row, int column){
					return false;
				}
			};
			while(rs.next()){
				System.out.println(rs.getString("PublisherName"));
				for(int i = 0; i < columnCount; i++){
					objects[i] = rs.getObject(i + 1);
				}
				resultTableModel.addRow(objects);
			}
			resultTable.setModel(resultTableModel);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void showProfiles(){
		System.out.println("I am here in show profiles");
		boolean isNotEmpty = false;
		try{
			Statement statement = database.getConnection().createStatement();
			ResultSet rs = statement.executeQuery("SHOW PROFILES");
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			int rowCount = 0;
			while(rs.next()){
				rowCount += rs.getRow();
				isNotEmpty = true;
			}
			System.out.println("rs.next() is " + isNotEmpty);
			System.out.println("Row count for profiles is : " + rowCount);
			System.out.println("Colum count for show profiles is: " + columnCount);
			timeTableModel = buildTableModel(rs);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	public DefaultTableModel buildTableModel(ResultSet rs)
	        throws SQLException {

	    ResultSetMetaData metaData = rs.getMetaData();

	    // names of columns
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = metaData.getColumnCount();
	    for (int column = 1; column <= columnCount; column++) {
	        columnNames.add(metaData.getColumnName(column));
	    }

	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	            vector.add(rs.getObject(columnIndex));
	        }
	        data.add(vector);
	    }

	    return new DefaultTableModel(data, columnNames);

	}
	public void setLabelContent(String query){
		queryTextLabel.setText(query);
	}
	
	public String removeHTML(String query){
		query = query.replaceAll("<html>", "");
		query = query.replaceAll("\n", "");
		query = query.replaceAll("<br>", "");
		query = query.replaceAll("</html>", "");
		return query;
	}
}
