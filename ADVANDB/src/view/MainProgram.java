package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import model.Database;

public class MainProgram extends JPanel{

	private Color darkerBlue = new Color(0x29B6F6), darkBlue = new Color(0x4FC3F7), lighterBlue = new Color(0xB3E5FC), backgroundColor = new Color(0x81D4FA), lightestBlue = new Color(0xE1F5FE);
	private Database database;
	private JTable resultTable, timeTable;
	private DefaultTableModel timeTableModel, currentQueryTableModel;
	private JComboBox<String> queryComboBox, queryBox;
	private ItemListener queryVariantListener, queryListener;
	private JButton query;
	private DefaultComboBoxModel<String> queryVariantListModel, queryListModel;
	private String[] queryVariants = {"Number of times a book is loaned per branch (1 Table)",
									  "List of all cardNo that has more than 50 books loaned (1 table)",
									  "Number of books that where on loan from each branch from 8/15/2010 to 8/25/2017 (2 tables)", 
									  "List of Borrowers that has at most 2 books borrowed (2 tables)",
									  "List of book loans that were returned exactly on their due date (3 tables)",
									  "List all authors and count of their books that were published in tokyo (3 tables)",
									  "List of library branches that loaned books published by “HarperCollins” from “01/01/2010’ to “01/01/2017”",
									  "<html>List of Publishers that has the same address(New York) <br> with a branch and a list of its books  loaned on the branch. (4 tables)"};
	
	private String[] firstQueryVersions = {"<html> /*Indexing*/ <br> " +
										   "CREATE INDEX index1 ON book(BookID); <br><br>" +
										   "SELECT DISTINCT BookID, BranchID, NoOfLoans <br> " + 
										   "FROM book_loans NATURAL JOIN (SELECT BookID, BranchID, COUNT(*) AS NoOfLoans <br> " +
										   "FROM book_loans <br> " +
										   "GROUP BY BookID, BranchID) NT <br> " +
										   "WHERE BranchID = NT.BranchID <br> " +
										   "AND BookID = NT.BookID; <br><br>" +
										   "DROP INDEX index1 on book; </html>",
										   
										   "<html> /*Natural Join*/ <br> " + 
										   "SELECT DISTINCT BookID, BranchID, NoOfLoans <br> " + 
										   "FROM book_loans NATURAL JOIN (SELECT BookID, BranchID, COUNT(*) AS NoOfLoans <br> " + 
										   "FROM book_loans <br> " +
										   "GROUP BY BookID, BranchID) NT <br> " +
										   "WHERE BranchID = NT.BranchID <br> " +
										   "AND BookID = NT.BookID; </html>",
										   
										   "<html> /*Indexing + Natural Join*/ <br> " +
										   "CREATE index index1 on book(BookID); <br><br> " +
										   "SELECT DISTINCT BookID, BranchID, NoOfLoans <br> " + 
										   "FROM book_loans NATURAL JOIN (SELECT BookID, BranchID, COUNT(*) AS NoOfLoans <br> " + 
										   "FROM book_loans <br> " +
										   "GROUP BY BookID, BranchID) NT <br> " +
										   "WHERE BranchID = NT.BranchID <br> " +
										   "AND BookID = NT.BookID; <br><br>" +
										   "DROP INDEX index1 on book; </html>"};
	
	private String[] secondQueryVersions = {"<html> /*2 indexes*/ <br> " +
										    "CREATE INDEX index2 ON book_loans(branchID); <br> " +
											"CREATE INDEX index1 ON book_loans(cardNo);  <br><br> " +
											"SELECT CardNo, COUNT(*) AS '# of loans'  FROM book_loans <br> " +
											"GROUP BY cardNo <br> " +
											"HAVING count(*) > 50; <br><br>" +
											"DROP INDEX index1 on book_loans; <br> " +
											"DROP INDEX index2 on book_loans; <br> </html>",
											
											"<html> /*1 index */<br> " +
											"CREATE INDEX index1 ON book_loans(cardNo); <br><br> " +
											"SELECT CardNo, COUNT(*) AS '# of loans'  FROM book_loans <br> " +
											"GROUP BY cardNo <br> " +
											"HAVING COUNT(*) > 50;<br><br>" + 
											"DROP INDEX index1 ON book_loans;</html>",
											
											"<html>/*Inner Join*/ <br> " +
											"SELECT book_loans.CardNo, numLoans FROM book_loans INNER JOIN ( <br> " +
											"SELECT CardNo, COUNT(*) AS 'numLoans'  FROM book_loans <br> " +
											"GROUP BY cardNo <br> " +
											"HAVING count(*) > 50 <br> " +
											") table2 <br> " +
											"WHERE book_loans.cardNo = table2.cardNo <br> " +
											"GROUP BY book_loans.CardNo; </html>",
											
											"<html>/*2 Indexes + Inner Join*/ <br>" +
											"CREATE INDEX index2 ON book_loans(branchID); <br> " +
											"CREATE INDEX index1 ON book_loans(cardNo);  <br><br> " +
											"SELECT book_loans.CardNo,  numLoans FROM book_loans INNER JOIN (  <br>" +
											"SELECT CardNo, count(*) as 'numLoans'  FROM book_loans  <br>" +
											"GROUP BY cardNo  <br>" +
											"HAVING COUNT(*) > 50  <br>" +
											") table2  <br>" +
											"WHERE book_loans.cardNo = table2.cardNo  <br>" +
											"GROUP BY book_loans.CardNo; <br><br>" +
											"DROP INDEX index1 on book_loans; <br> " +
											"DROP INDEX index2 on book_loans; <br> </html>"};
	
	private String[] thirdQueryVersions = {"<html> /*Natural Join*/ <br> " +
										   "SELECT BranchID, BranchName, COUNT(*) as NoOfBooksLoaned <br>" +
										   "FROM book_loans NATURAL JOIN library_branch <br>" +
										   "WHERE DateOut BETWEEN '8/15/2010' AND '8/25/2017' <br>" + 
										   "GROUP BY BranchID; </html>",

										   "<html>/*Inner Join*/ <br>" +
										   "SELECT BL.BranchID, BranchName, COUNT(*) as NoOfBooksLoaned <br>" +
										   "FROM book_loans BL INNER JOIN library_branch LB <br>" +
										   "ON LB.BranchID = BL.BranchID <br>" +
										   "WHERE DateOut BETWEEN '8/15/2010' AND '8/25/2017' <br>" +
										   "GROUP BY BL.BranchID; </html>",
										   
										   "<html> /*Indexing + Inner Join*/ <br> " +
										   "CREATE index index1 ON book_loans(BranchID); <br><br>" +
										   "SELECT BL.BranchID, BranchName, COUNT(*) as NoOfBooksLoaned <br>" +
										   "FROM book_loans BL INNER JOIN library_branch LB <br>" +
										   "ON LB.BranchID = BL.BranchID <br>" +
										   "WHERE DateOut BETWEEN '8/15/2010' AND '8/25/2017' <br>" +
										   "GROUP BY BL.BranchID; <br><br> " +
										   "DROP INDEX index1 ON book_loans; </html>",
										   
										   "<html> /*Subquery*/ <br>" +
										   "SELECT BranchName, NoOfBooksLoaned <br>" +
										   "FROM library_branch LB, ( <br>" +
										   "SELECT BranchID, COUNT(*) as NoOfBooksLoaned <br>" +
										   "FROM book_loans BL WHERE DateOut BETWEEN '8/15/2010' AND '8/25/2017' <br>" +
										   "GROUP BY BL.BranchID) NT <br>" +
										   "WHERE LB.BranchID = NT.BranchID; </html>"};
	
	private String[] fourthQueryVersions = {"<html> /*2 Indexes*/ <br>" +
											"Create index cardIndex on borrower(cardNo, borrowerfname, borrowerlname); <br>" +
											"Create Index card2Index on book_loans(cardNo); <br> <br>" +
											"select concat(BorrowerLName, \", \", BorrowerFName) as 'BurrowerName', count(*) as 'Books loaned' from book_loans, borrower <br>" +
											"where borrower.cardNo = book_loans.cardNo <br>" +
											"group by borrower.cardNo <br>" +
											"having count(*) <= 2; <br> <br>" + 
											"DROP INDEX cardIndex on borrower; <br>" +
											"DROP INDEX card2Index on book_loans;<br></html>", 
											
											"<html> /*1 Index*/ <br>" +
											"Create index cardIndex on borrower(cardNo, borrowerfname, borrowerlname); <br><br>" +
											"select concat(BorrowerLName, \", \", BorrowerFName) as 'BurrowerName', count(*) as 'Books loaned' from book_loans, borrower <br>" +
											"where borrower.cardNo = book_loans.cardNo <br>" +
											"group by borrower.cardNo <br>" +
											"having count(*) <= 2; <br> <br>" + 
											"DROP INDEX cardIndex on borrower; </html>",
											
											"<html> /*Inner Join*/ <br><br>" +
											"SELECT concat(borrowerfname, borrowerlname) as 'BorrowerName', NoBooksBor <br>" +
											"FROM borrower BO INNER JOIN (SELECT CardNo, COUNT(*) as NoBooksBor <br>" +
											"FROM book_loans BL <br>" +
											"GROUP BY CardNo <br>" +
											"HAVING COUNT(*) <= 2) mostTwo <br>" +
											"WHERE BO.CardNo = mostTwo.CardNo <br>" +
											"ORDER BY NoBooksBor DESC, BorrowerName ASC; </html>",
											
											"<html> /*Inner Join + 2 Indexes*/ <br><br>" +
											"Create index cardIndex on borrower(cardNo, borrowerfname, borrowerlname); <br>" +
											"Create Index card2Index on book_loans(cardNo); <br> <br>" +
											"SELECT concat(borrowerfname, borrowerlname) as 'BorrowerName', NoBooksBor <br>" +
											"FROM borrower BO INNER JOIN (SELECT CardNo, COUNT(*) as NoBooksBor <br>" +
											"FROM book_loans BL <br>" +
											"GROUP BY CardNo <br>" +
											"HAVING COUNT(*) <= 2) mostTwo <br>" +
											"WHERE BO.CardNo = mostTwo.CardNo <br>" +
											"ORDER BY NoBooksBor DESC, BorrowerName ASC; <br><br>" +
											"DROP INDEX cardIndex on borrower; <br> " +
											"DROP INDEX card2Index on book_loans; <br> </html>"
											};
	
	private String[] fifthQueryVersions = {"<html> /*Cartesian Product*/ <br>" +
										   "SELECT BR.CardNo, BL.BookID, Title, DueDate, DateReturned  <br>" +
										   "FROM book_loans BL, borrower BR, book B <br>" +
										   "WHERE DueDate = DateReturned  <br>" +
										   "AND BL.BookID = B.BookID  <br>" +
										   "AND BL.CardNo = BR.CardNo ORDER BY 3; </html>",
										   
										   "<html> /*Natural Join*/ <br>" +
										   "SELECT CardNo, BookID, Title, DueDate, DateReturned <br>" +
										   "FROM book_loans NATURAL JOIN borrower <br>" +
										   "NATURAL JOIN book <br>" +
										   "WHERE DueDate = DateReturned <br>" +
										   "ORDER BY 3; <br>" +
										   "</html>", 
										   
										   "<html> /*Indexing + Natural Join*/<br>" + 
										   "CREATE index index1 on book_loans(BookID); <br><br>" +
										   "SELECT BorrowerLName, BorrowerFName,  <br>" +
										   "BookID, Title, DueDate, DateReturned <br>" +
										   "FROM book_loans NATURAL JOIN borrower <br>" +
										   "NATURAL JOIN book <br>" +
										   "WHERE DueDate = DateReturned <br>" +
										   "ORDER BY 1; <br><br>" +
										   "DROP index index1 on book_loans;</html>",
										   
										   "<html> /*Indexing + Cartesian Product*/ <br>" +
										   "CREATE index index1 on book_loans(BookID); <br><br>" +
										   "SELECT BR.CardNo, BL.BookID, Title, DueDate, DateReturned  <br>" +
										   "FROM book_loans BL, borrower BR, book B <br>" +
										   "WHERE DueDate = DateReturned  <br>" +
										   "AND BL.BookID = B.BookID  <br>" +
										   "AND BL.CardNo = BR.CardNo ORDER BY 3; <br><br> " +
										   "DROP index index1 on book_loans;</html>"};
	
	private String[] sixthQueryVersions = {"<html>/*2 Indexes*/ <br> " +
										   "Create Index index1 ON Publisher(publisherName, address); <br> " +
										   "create index index2 ON book(publisherName); <br><br> " +
										   "SELECT concat(book_authors.AuthorFirstName , book_authors.AuthorLastName) as Author, <br>" + 
									       "count(*) as 'No. Of books' from publisher, book, book_authors <br> " +
									       "WHERE book.PublisherName = publisher.PublisherName AND <br> " +
									       "book_authors.BookID = book.BookID AND <br>" +
									       "publisher.Address = \"tokyo\" <br>" +
									       "GROUP BY Author <br>" +
									       "ORDER BY 2 desc; <br><br>" +
									       "DROP INDEX index1 on Publisher; <br>" +
									       "DROP INDEX index2 on book; <br></html>", 
									       
									       "<html> /*1 Index*/ <br>" +
									       "Create Index index1 ON Publisher(publisherName, address); <br><br> " +
									       "SELECT concat(book_authors.AuthorFirstName , book_authors.AuthorLastName) as Author, <br>" + 
									       "count(*) as 'No. Of books' from publisher, book, book_authors <br> " +
									       "WHERE book.PublisherName = publisher.PublisherName AND <br> " +
									       "book_authors.BookID = book.BookID AND <br>" +
									       "publisher.Address = \"tokyo\" <br>" +
									       "GROUP BY Author <br>" +
									       "ORDER BY 2 desc; <br><br>" +
									       "DROP INDEX index1 on Publisher; <br>",
									       
									       "<html> /*Inner Join*/ <br>" +
									       "SELECT concat(book_authors.AuthorFirstName , book_authors.AuthorLastName) as Author, <br> " +
									       "count(*) as 'No. Of books' from book, book_authors inner join (SELECT * from publisher where publisher.Address = \"tokyo\") pubLoc <br>" + 
									       "WHERE book.PublisherName = pubLoc.PublisherName AND <br> " +
									       "book_authors.BookID = book.BookID <br> " +
									       "GROUP BY Author <br>" +
									       "ORDER BY 2 desc; <br></html>",

										   "<html> /* Inner Join + Indexing*/ <br>" +
										   "Create Index index1 ON Publisher(publisherName, address); <br> " +
										   "create index index2 ON book(publisherName); <br><br> " +
										   "SELECT concat(book_authors.AuthorFirstName , book_authors.AuthorLastName) as Author, <br> " +
									       "count(*) as 'No. Of books' from book, book_authors inner join (SELECT * from publisher where publisher.Address = \"tokyo\") pubLoc <br>" + 
									       "WHERE book.PublisherName = pubLoc.PublisherName AND <br> " +
									       "book_authors.BookID = book.BookID <br> " +
									       "GROUP BY Author <br>" +
									       "ORDER BY 2 desc; <br><br> " +
									       "DROP INDEX index1 ON Publisher; <br>" +
									       "DROP INDEX index2 ON book; </html>"
									       };
	
	private String[] seventhQueryVersions = {"<html> /*Subquery*/ <br> " + 
									       	 "SELECT DISTINCT BranchName, LB.BranchID <br> " +
											 "FROM library_branch LB, ( <br> " +
											 "SELECT BranchID FROM book_loans BL, <br> " +
											 "(SELECT BookID, PublisherName FROM book b WHERE PublisherName = ( <br> " +
											 "SELECT PublisherName FROM publisher p WHERE PublisherName = 'HarperCollins')) NT <br> " +
											 "WHERE NT.BookID = BL.BookID AND <br> " +
											 "DateOut between '1/1/2010' and '1/1/2017') NT2 <br> " +
											 "WHERE LB.BranchID = NT2.BranchID; </html> ",
											 
											 "<html> /*Natural Join*/ <br> " +
											 "SELECT DISTINCT BranchName, LB.BranchID <br> " + 
											 "FROM library_branch LB NATURAL JOIN ( <br> " +
											 "SELECT BranchID FROM book_loans BL, <br> " +
											 "(SELECT BookID, PublisherName FROM book b WHERE PublisherName = ( <br> " +
											 "SELECT PublisherName FROM publisher p WHERE PublisherName = 'HarperCollins')) NT <br> " +
											 "WHERE NT.BookID = BL.BookID AND <br> " +
											 "DateOut between '1/1/2010' and '1/1/2017') NT2 <br> " +
											 "WHERE LB.BranchID = NT2.BranchID; </html> ",
											 
											 "<html> /*Indexing + Natural Join*/ <br> " +
											 "CREATE index index1 on library_branch(BranchID); <br><br>"  +
											 "SELECT DISTINCT BranchName, LB.BranchID <br> " + 
											 "FROM library_branch LB NATURAL JOIN ( <br> " +
											 "SELECT BranchID FROM book_loans BL, <br> " +
											 "(SELECT BookID, PublisherName FROM book b WHERE PublisherName = ( <br> " +
											 "SELECT PublisherName FROM publisher p WHERE PublisherName = 'HarperCollins')) NT <br> " +
											 "WHERE NT.BookID = BL.BookID AND <br> " +
											 "DateOut between '1/1/2010' and '1/1/2017') NT2 <br> " +
											 "WHERE LB.BranchID = NT2.BranchID; <br><br>" + 
											 "DROP INDEX index1 on library_branch;</html> ",
											 
											 "<html> /*Indexing + Subquery */<br>" +
											 "CREATE index index1 on library_branch(BranchID); <br><br>"  +
											 "SELECT DISTINCT BranchName, LB.BranchID <br> " +
											 "FROM library_branch LB, ( <br> " +
											 "SELECT BranchID FROM book_loans BL, <br> " +
											 "(SELECT BookID, PublisherName FROM book b WHERE PublisherName = ( <br> " +
											 "SELECT PublisherName FROM publisher p WHERE PublisherName = 'HarperCollins')) NT <br> " +
											 "WHERE NT.BookID = BL.BookID AND <br> " +
											 "DateOut between '1/1/2010' and '1/1/2017') NT2 <br> " +
											 "WHERE LB.BranchID = NT2.BranchID;<br><br>" + 
											 "DROP INDEX index1 on library_branch;</html> "};
	
	private String[] eighthQueryVersions = {"<html> /*2 Indexes*/ <br>" +
											"create index index1 on book(publisherName); <br> " +
											"create index index2 on book_loans(BookID); <br><br> "	+
											"SELECT PublisherName, BranchName, publisher.Address, count(*) as '# of books published and placed on branch with same address' FROM book  <br> " +
                                            "natural join book_loans  <br> " +
                                            "natural join library_branch <br> " +
                                            "natural join publisher <br> " +
                                            "WHERE publisher.Address = BranchAddress AND <br> " +
                                            "publisher.Address = \"New York\" <br> " +
                                            "GROUP BY PublisherName, BranchID <br> " +
                                            "ORDER BY 4 DESC;<br>" +
                                            "DROP INDEX index1 on book;<br> " +
                                            "DROP INDEX index2 on book_loans;</html>",
                                            
                                            "<html> /*1 Index*/ <br>" +
                                           	"create index index1 on book(publisherName); <br><br> " +		
        									"SELECT PublisherName, BranchName, publisher.Address, count(*) as '# of books published and placed on branch with same address' FROM book  <br> " +
                                            "natural join book_loans  <br> " +
                                            "natural join library_branch <br> " +
                                            "natural join publisher <br> " +
                                            "WHERE publisher.Address = BranchAddress AND <br> " +
                                            "publisher.Address = \"New York\" <br> " +
                                            "GROUP BY PublisherName, BranchID <br> " +
                                            "ORDER BY 4 DESC; <br><br>" + 
                                            "DROP INDEX index1 on book</html>",
                                            
                                            "<html> /*Subquery*/ <br> " +
                                            "SELECT PublisherName, BranchName, Address, count(*) as '# of books published and placed on branch with same address' FROM book natural join book_loans <br> " +
                                            "natural join (select * from library_branch, publisher <br> " +
                                            "WHERE publisher.Address = branchAddress AND <br> " +
                                            "publisher.Address = \"New York\") table1 <br> " +
			                                "GROUP BY PublisherName, BranchID <br> " +
			                                "ORDER BY 4 DESC; </html> ",

			                                "<html> /*Indexing + Subquery*/ <br> " +
			                               	"create index index1 on book(publisherName); <br> " +
											"create index index2 on book_loans(BookID); <br><br> "	+
		                                    "SELECT PublisherName, BranchName, Address, count(*) as '# of books published and placed on branch with same address' FROM book natural join book_loans <br> " +
		                                    "natural join (select * from library_branch, publisher <br> " +
		                                    "WHERE publisher.Address = branchAddress AND <br> " +
		                                    "publisher.Address = \"New York\") table1 <br> " +
					                        "GROUP BY PublisherName, BranchID <br> " +
					                        "ORDER BY 4 DESC; <br><br>" + 
					                        "DROP index index1 on book; <br>" + 
					                        "DROP index index2 on book_loans;</html> "};
	private JScrollPane timeTableScrollPane, resultTableScrollPane;
	private JScrollPane queryScrollPane;
	private JLabel queryTextLabel;
	private JLabel lblSelectQuery;
	private JLabel lblSelectQueryVersion;
	private JLabel queryObjective;
	private JTable currentQueryTimeTable;
	private JButton btnClearCurrent;
	private JButton btnClearResults;
	
	public MainProgram(){
		init();
	}
	
	public void init(){
		database = new Database();
		setProfiling();
		setBackground(backgroundColor);
		setLayout(null);
		setSize(new Dimension(950, 650));
		query = new JButton("Query");
		query.addActionListener(new queryAL());
		query.setBounds(830, 36, 110, 20);
		query.setCursor(new Cursor(Cursor.HAND_CURSOR));
		query.setBackground(darkerBlue);
		query.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent arg0) {
				query.setBackground(darkBlue);
			}
			public void mouseExited(MouseEvent arg0){
				query.setBackground(darkerBlue);
			}
		});
		add(query);
		
		UIManager.put("ComboBox.background", lighterBlue);
		resultTableScrollPane = new JScrollPane();
		resultTableScrollPane.setBounds(10, 84, 550, 268);
		add(resultTableScrollPane);
		resultTable = new JTable(){
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component comp = super.prepareRenderer(renderer, row, column);
				comp.setBackground(row % 2 == 0 ? lighterBlue : lightestBlue);
				return comp;
			}
		};
		resultTableScrollPane.setViewportView(resultTable);
		resultTableScrollPane.getViewport().setBackground(lighterBlue);
		resultTable.getTableHeader().setResizingAllowed(false);
		resultTable.getTableHeader().setReorderingAllowed(false);
	    JTableHeader resultTableHeader = resultTable.getTableHeader();
	    resultTableHeader.setBackground(darkBlue);
	    
		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
		queryVariantListModel = new DefaultComboBoxModel<String>(queryVariants);
		queryComboBox = new JComboBox<String>();
		queryComboBox.setBounds(10, 36, 400, 20);
		queryComboBox.setModel(queryVariantListModel);
		add(queryComboBox);
		
		queryListModel = new DefaultComboBoxModel<String>(firstQueryVersions);
		queryBox = new JComboBox<String>();
		queryBox.setBounds(420, 36, 400, 20);
		queryBox.setModel(queryListModel);
		addQueryListener();
		add(queryBox);
		
		timeTableModel = new DefaultTableModel(new String[]{"ID", "Time", "Query"}, 0){
			@Override
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		timeTableScrollPane = new JScrollPane();
		timeTableScrollPane.setBounds(570, 84, 370, 341);
		add(timeTableScrollPane);
		timeTable = new JTable(){
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component comp = super.prepareRenderer(renderer, row, column);
				comp.setBackground(row % 2 == 0 ? lighterBlue : lightestBlue);
				return comp;
			}
		};
		timeTable.setBackground(Color.WHITE);
		timeTableScrollPane.setViewportView(timeTable);
		timeTableScrollPane.getViewport().setBackground(lighterBlue);
		timeTable.setModel(timeTableModel);
		timeTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		timeTable.getColumnModel().getColumn(1).setPreferredWidth(79);
		timeTable.getColumnModel().getColumn(2).setPreferredWidth(258);
		timeTable.getTableHeader().setResizingAllowed(false);
		timeTable.getTableHeader().setReorderingAllowed(false);
	    JTableHeader timeTableHeader = timeTable.getTableHeader();
	    timeTableHeader.setBackground(darkBlue);
		
		queryScrollPane = new JScrollPane();
		queryScrollPane.getViewport().setBackground(lighterBlue);
		queryScrollPane.setBounds(10, 461, 550, 147);
		add(queryScrollPane);
		
		queryTextLabel = new JLabel();
		queryScrollPane.setViewportView(queryTextLabel);
		queryTextLabel.setText(queryBox.getSelectedItem().toString());
		queryTextLabel.setVerticalAlignment(SwingConstants.TOP);
		
		JLabel lblQuery = new JLabel("Query");
		lblQuery.setBounds(10, 436, 140, 14);
		add(lblQuery);
		
		JScrollPane objectiveScrollPane = new JScrollPane();
		objectiveScrollPane.getViewport().setBackground(lighterBlue);
		objectiveScrollPane.setBounds(10, 381, 550, 44);
		add(objectiveScrollPane);
		
		queryObjective = new JLabel("New label");
		queryObjective.setText(queryComboBox.getSelectedItem().toString());
		objectiveScrollPane.setViewportView(queryObjective);
		
		JLabel lblQueryObjective = new JLabel("Query Objective");
		lblQueryObjective.setBounds(10, 363, 140, 14);
		add(lblQueryObjective);
		
		JLabel lblQueryTimeTable = new JLabel("Query History");
		lblQueryTimeTable.setBounds(570, 65, 140, 14);
		add(lblQueryTimeTable);
		
		lblSelectQuery = new JLabel("Select Query");
		lblSelectQuery.setBounds(10, 11, 140, 14);
		add(lblSelectQuery);
		
		lblSelectQueryVersion = new JLabel("Select Query Version");
		lblSelectQueryVersion.setBounds(420, 11, 165, 14);
		add(lblSelectQueryVersion);
		
		JScrollPane currentQueryScrollPane = new JScrollPane();
		currentQueryScrollPane.getViewport().setBackground(lighterBlue);
		currentQueryScrollPane.setBounds(570, 461, 370, 147);
		add(currentQueryScrollPane);
		
		currentQueryTimeTable = new JTable(){
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component comp = super.prepareRenderer(renderer, row, column);
				comp.setBackground(row % 2 == 0 ? lighterBlue : lightestBlue);
				return comp;
			}
		};
		currentQueryTimeTable.setBackground(Color.WHITE);
		JTableHeader currentQueryHeader = currentQueryTimeTable.getTableHeader();
		currentQueryHeader.setBackground(darkBlue);
		currentQueryScrollPane.setViewportView(currentQueryTimeTable);
		
		currentQueryTableModel = new DefaultTableModel(new String[]{"ID", "Time", "Query"}, 0){
			@Override
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		currentQueryTimeTable.setModel(currentQueryTableModel);
		currentQueryTimeTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		currentQueryTimeTable.getColumnModel().getColumn(1).setPreferredWidth(79);
		currentQueryTimeTable.getColumnModel().getColumn(2).setPreferredWidth(258);
		currentQueryTimeTable.getTableHeader().setResizingAllowed(false);
		currentQueryTimeTable.getTableHeader().setReorderingAllowed(false);
		JLabel lblCurrentQuery = new JLabel("Current Query");
		lblCurrentQuery.setBounds(570, 436, 165, 14);
		add(lblCurrentQuery);
		
		JButton btnClear = new JButton("Clear History");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				timeTableModel.setRowCount(0);
			}
		});
		btnClear.setBounds(830, 62, 110, 20);
		btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnClear.setBackground(darkerBlue);
		btnClear.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent arg0) {
				btnClear.setBackground(darkBlue);
			}
			public void mouseExited(MouseEvent arg0){
				btnClear.setBackground(darkerBlue);
			}
		});
		add(btnClear);
		
		btnClearCurrent = new JButton("Clear Current");
		btnClearCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentQueryTableModel.setRowCount(0);
			}
		});
		btnClearCurrent.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnClearCurrent.setBackground(darkerBlue);
		btnClearCurrent.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent arg0) {
				btnClearCurrent.setBackground(darkBlue);
			}
			public void mouseExited(MouseEvent arg0){
				btnClearCurrent.setBackground(darkerBlue);
			}
		});
		btnClearCurrent.setBounds(830, 433, 110, 20);
		add(btnClearCurrent);
		
		btnClearResults = new JButton("Clear Results");
		btnClearResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model =  (DefaultTableModel) resultTable.getModel();
				model.setRowCount(0);
			}
		});
		btnClearResults.setBounds(450, 62, 110, 20);
		btnClearResults.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnClearResults.setBackground(darkerBlue);
		btnClearResults.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent arg0) {
				btnClearResults.setBackground(darkBlue);
			}
			public void mouseExited(MouseEvent arg0){
				btnClearResults.setBackground(darkerBlue);
			}
		});
		add(btnClearResults);
		
		JLabel lblQueryResults = new JLabel("Query Results");
		lblQueryResults.setBounds(10, 65, 140, 14);
		add(lblQueryResults);
		

	}
	
	class queryAL implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String query = removeHTML(queryBox.getSelectedItem().toString());
			String[] queries = query.split(";");
			System.out.println("Content of queries is ");
			for(String s: queries){
				System.out.println("S is " + s);
			}
			for(String s: queries){
				if(s.toUpperCase().contains("CREATE") || s.toUpperCase().contains("DROP")){
					createOrDropIndex(s);
					showProfiles();
				}
				else if(s.toUpperCase().contains("SELECT")){
					createQuery(s);
					showProfiles();
				}
				System.out.println("Current s" + s);
			}
			showCurrentQueryProfiles(queries.length - 1);
		}
	}
	
	public void createOrDropIndex(String query){
		try {
			Statement statement;
			statement = database.getConnection().createStatement();
			statement.executeUpdate(query);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public void addQueryListener(){
		queryVariantListener = new ItemListener(){
			public void itemStateChanged(ItemEvent itemEvent){
				changeQueryList(queryComboBox.getSelectedIndex());
				setObjectiveContent(queryComboBox.getSelectedItem().toString());
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
	
	public void setObjectiveContent(String objective){
		queryObjective.setText(objective);
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
	
	public void setProfiling(){
		try{
			Statement statement = database.getConnection().createStatement();
			statement.executeUpdate("SET profiling = 1");
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void showCurrentQueryProfiles(int size){
		try{
			System.out.println("Size passed to showCurrentQueryProfiles is " + size);
			currentQueryTableModel.setRowCount(0);
			Statement statement = database.getConnection().createStatement();
			ResultSet rs = statement.executeQuery("SHOW PROFILES");
			rs.last();
			int rsSize = rs.getRow();
			rs.beforeFirst();
			System.out.println("RS Size is " + rsSize);
			if(rsSize > size){
				for(int i = 0; i < rsSize - size; i++){
					rs.next();
					System.out.println("rs.next " + i + 1);
				}
			}
			while(rs.next()){
				String queryid = rs.getString("Query_ID");
				String duration = rs.getString("Duration");
				String query = rs.getString("Query");
				currentQueryTableModel.addRow(new Object[]{queryid, duration, query});
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public void showProfiles(){
		System.out.println("I am here in show profiles");
		try{
			Statement statement = database.getConnection().createStatement();
			ResultSet rs = statement.executeQuery("SHOW PROFILES");
			rs.last();
			if(rs.last()){
				String queryid = rs.getString("Query_ID");
				String duration = rs.getString("Duration");
				String query = rs.getString("Query");
				timeTableModel.addRow(new Object[]{queryid, duration, query});
				System.out.println("I am here adding tables to timeTableModel");
			}
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