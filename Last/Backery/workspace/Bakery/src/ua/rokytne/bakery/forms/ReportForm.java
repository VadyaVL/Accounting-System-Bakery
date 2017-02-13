package ua.rokytne.bakery.forms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.Client;
import ua.rokytne.bakery.orm.Debit;
import ua.rokytne.bakery.orm.Employee;
import ua.rokytne.bakery.orm.Place;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.Request;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import com.toedter.calendar.JDateChooser;

public class ReportForm extends JDialog {

	private JPanel contentPane;
	private JTextField tfSum;
	private JTable table;
	Object[] columnNames = {"    ", "Сума, грн"};
	Object[][] data = {};
	DefaultTableModel model = new DefaultTableModel(data, columnNames);

	JDateChooser dcFrom, dcTo;
	JRadioButton rbClient, rbProduct;
	ArrayList<Production> productions = new ArrayList<>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReportForm frame = new ReportForm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ReportForm() {
		model = new DefaultTableModel(data, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       
		       return false;
		    }
		};
		this.setAlwaysOnTop(Main.onTop);
		//setResizable(false);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Загальний звіт $€₴");
		setBounds((Main.WIDTH-341)/2, (Main.HEIGTH-370)/2, 341, 370);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Від:");
		lblNewLabel.setBounds(10, 14, 24, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("до:");
		lblNewLabel_1.setBounds(164, 14, 24, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lbSum = new JLabel("Виручка:");
		lbSum.setBounds(178, 284, 57, 14);
		contentPane.add(lbSum);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(235, 307, 89, 23);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnCancel);
		
		JButton btnPrint = new JButton("Друк");
		btnPrint.setBounds(10, 307, 89, 23);
		btnPrint.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String mess = "Друк звіту по ";
				
				if(rbClient.isSelected())
					mess += "клієнтам [";
				else
					mess += "продуктам [";
				
				mess += Main.dfDate.format(dcFrom.getDate()) + "; " + Main.dfDate.format(dcTo.getDate()) + "]";
				
				Main.insertLog(mess);
				makeDoc();
			}
		});
		contentPane.add(btnPrint);
		
		ButtonGroup group = new ButtonGroup();
		
		rbProduct = new JRadioButton("продукт");
		rbProduct.setBounds(69, 35, 74, 14);
		contentPane.add(rbProduct);
		rbProduct.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				while (model.getRowCount() > 0) {
					model.removeRow(0);
				}
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM Production";
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					while (rs.next())
					{
						int id = rs.getInt("id");
						String name = rs.getString("name");
						float countOnStorage = rs.getFloat("CountOnStorage");
						
						Production pr = new Production(id, name, countOnStorage);
						
					    model.addRow(new Object[]{pr, 0.0f});
				    }
					st.close();
					
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				calculate();
			}
		});
		
		rbClient = new JRadioButton("клієнт");
		rbClient.setSelected(true);
		rbClient.setBounds(10, 35, 57, 14);
		contentPane.add(rbClient);
		rbClient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				while (model.getRowCount() > 0) {
					model.removeRow(0);
				}
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM Client";
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					while (rs.next())
					{
						int id = rs.getInt("id");
						String name = rs.getString("name");
						int place_id = rs.getInt("place_id");
						
						Client cl = new Client(id, name, place_id);
						
					    model.addRow(new Object[]{cl, 0.0f});
				    }
				  st.close();
					
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				calculate();
			}
		});

	    group.add(rbClient);
	    group.add(rbProduct);
	    
		dcFrom = new JDateChooser();
		dcFrom.setBounds(32, 11, 122, 20);
		contentPane.add(dcFrom);
		dcFrom.addPropertyChangeListener( new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				calculate();
			}
		});
		
		dcTo = new JDateChooser();
		dcTo.setBounds(189, 11, 135, 20);
		contentPane.add(dcTo);
		dcTo.addPropertyChangeListener( new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				calculate();
			}
		});
		
		tfSum = new JTextField();
		tfSum.setEditable(false);
		tfSum.setBounds(235, 281, 89, 20);
		contentPane.add(tfSum);
		tfSum.setColumns(10);
		
		table = new JTable(model);
		table.setBounds(10, 56, 314, 216);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(10, 56, 314, 216);
		getContentPane().add(jsp);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.8));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.2));
		

		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            int width=c.getWidth(), heigth=c.getHeight();


	    		btnCancel.setBounds(width-89-25, heigth-23-5-40, 89, 23);
	    		btnPrint.setBounds(10, heigth-23-5-40, 89, 23);
				
	    		tfSum.setBounds(width-89-25, heigth-23-5-40-23-5, 89, 20);
	    		lbSum.setBounds(width-89-25-57, heigth-23-5-40-23-5, 57, 14);

	    		jsp.setBounds(10, 56, width-30, heigth-40-23-23-20-50);
	    		
	        }
		});
		
		
		initData();
	}
	
	public void calculate(){
		Date from = dcFrom.getDate();
		Date to = dcTo.getDate();
		
		if(from==null || to==null)
			return;

		from.setHours(0);
		from.setMinutes(0);
		from.setSeconds(0);
		
		to.setHours(23);
		to.setMinutes(59);
		to.setSeconds(59);
		
		if(!from.before(to))
			return;
		
		float sumALL = 0;
				
		for(int i=0; i<table.getRowCount(); i++){
			
			if(rbClient.isSelected()){
				
				Client client = (Client)table.getValueAt(i, 0);
				Place place = null;
				float sum = 0;
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM place";
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					if(rs.next())
					{
						int id = rs.getInt("id");
						String name = rs.getString("name");
						place = new Place(id, name);
				    }
					st.close();
					conn.close();
				}
				catch(Exception ex){ ex.printStackTrace(); }
				
				// Маємо Клієнта і місто.
				// Отримуємо всі заявки.
				ArrayList<Request> requests = new ArrayList<>();
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM request where client_id = " + client.getID()  + " and "
							+ "date_request >= \"" + Main.dfDateAndTime.format(from) + "\" and date_request <= \"" +Main.dfDateAndTime.format(to) +"\"";
					
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					while (rs.next())
					{
						// id, Client_id, Date_Request, Date_Oformleniya, OK, User_id
						int id = rs.getInt("id");
						int client_id = rs.getInt("client_id");
						String date_Request = rs.getString("Date_Request");
						String date_Oformleniya = rs.getString("Date_Oformleniya");
						boolean ok = rs.getBoolean("OK");
						int user_id = rs.getInt("User_id");
						requests.add(new Request(id, client_id, date_Request, date_Oformleniya, ok, user_id));
				    }
					st.close();
					
					conn.close();
				}
				catch(Exception ex){ ex.printStackTrace(); }
				
				
				for(Request r : requests){
					for(Production p : productions){
						float price = 0;
						int count = 0;
						try{
							Class.forName("com.mysql.jdbc.Driver");
							Connection conn = null;
							conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);

					    	String queryPHI = "SELECT * FROM product_price WHERE production_id = " + 
						    	p.getID() +" and place_id = " + place.getID();
								
							Statement stPHI = conn.createStatement();
							ResultSet rsPHI = stPHI.executeQuery(queryPHI);
								
							if(rsPHI.next()){ // Якщо є
							    price = rsPHI.getFloat("price");
							}
								
							stPHI.close();
							conn.close();
						}
						catch(Exception ex){ ex.printStackTrace(); }
						

						try{
							Class.forName("com.mysql.jdbc.Driver");
							Connection conn = null;
							conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);

					    	String queryPHI = "SELECT * FROM request_has_production WHERE production_id = " + 
						    	p.getID() +" and request_id = " + r.getId();
								
							Statement stPHI = conn.createStatement();
							ResultSet rsPHI = stPHI.executeQuery(queryPHI);
								
							if(rsPHI.next()){ // Якщо є
							    count = rsPHI.getInt("count");
							}
								
							stPHI.close();
							conn.close();
						}
						catch(Exception ex){ ex.printStackTrace(); }
						
						sum += count*price;
						
						
					}
				}
				table.setValueAt(sum, i, 1);
				sumALL+=sum;
			}
			else{
				/////////////////////////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////////////////////////
				//ЗВІТ ПО ПРОДУКТАМ. БІЖИМО ПО ПРОДУКТАМ.
				
				// Маємо продукти...
				// Отримуємо всі заявки.
				ArrayList<Request> requests = new ArrayList<>();
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM request where date_request >= \"" + Main.dfDateAndTime.format(from) + "\" and date_request <= \"" +Main.dfDateAndTime.format(to) +"\"";
					
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					while (rs.next())
					{
						int id = rs.getInt("id");
						int client_id = rs.getInt("client_id");
						String date_Request = rs.getString("Date_Request");
						String date_Oformleniya = rs.getString("Date_Oformleniya");
						boolean ok = rs.getBoolean("OK");
						int user_id = rs.getInt("User_id");
						requests.add(new Request(id, client_id, date_Request, date_Oformleniya, ok, user_id));
				    }
					st.close();
					
					conn.close();
				}
				catch(Exception ex){ ex.printStackTrace(); }
				// Маємо продук та зявки
				
				float sum = 0.0f;
				for(Request r : requests){
					int count = 0;
					float price = 0.0f;
					
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);

				    	String queryPHI = "SELECT * FROM product_price WHERE Production_id = " + ((Production)this.table.getValueAt(i, 0)).getID() + " and Place_id = (SELECT id FROM place where id = (SELECT client.Place_id FROM client WHERE id = " + r.getClientId() + "))";
							
						Statement stPHI = conn.createStatement();
						ResultSet rsPHI = stPHI.executeQuery(queryPHI);
							
						if(rsPHI.next()){ // Якщо є
						    price = rsPHI.getFloat("price");
						}
							
						stPHI.close();
						conn.close();
					}
					catch(Exception ex){ ex.printStackTrace(); }
					

					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);

				    	String queryPHI = "SELECT * FROM request_has_production WHERE production_id = " + 
					    	((Production)this.table.getValueAt(i, 0)).getID() +" and request_id = " + r.getId();
							
						Statement stPHI = conn.createStatement();
						ResultSet rsPHI = stPHI.executeQuery(queryPHI);
							
						if(rsPHI.next()){ // Якщо є
						    count = rsPHI.getInt("count");
						}
							
						stPHI.close();
						conn.close();
					}
					catch(Exception ex){ ex.printStackTrace(); }
					
					sum+=count*price;
				}

				table.setValueAt(sum, i, 1);
				sumALL+=sum;
				/////////////////////////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////////////////////////
				/////////////////////////////////////////////////////////////////////////////
			}
			tfSum.setText(sumALL + "");	
		}
		
	}
	

	private void makeDoc(){

		XWPFDocument document = new XWPFDocument();
		CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
	    CTPageMar pageMar = sectPr.addNewPgMar();
	    pageMar.setLeft(BigInteger.valueOf(400L));
	    pageMar.setTop(BigInteger.valueOf(400L));
	    pageMar.setRight(BigInteger.valueOf(400L));
	    pageMar.setBottom(BigInteger.valueOf(400L));
		int q=2;
		int tableFontSize=8;
		String fontName = "Calibri";
		
		XWPFParagraph paragraphTitle = document.createParagraph();
		paragraphTitle.setAlignment(ParagraphAlignment.CENTER);
		paragraphTitle.setSpacingAfter(0);
		XWPFRun runTitle = paragraphTitle.createRun();
		if(rbClient.isSelected())
			runTitle.setText("Звіт по клієнтам");
		else if(rbProduct.isSelected())
			runTitle.setText("Звіт по продуктам");
		runTitle.setFontSize(14);
		runTitle.setFontFamily("Times New Roman");
		runTitle.setBold(true);

		XWPFParagraph paragraphDate = document.createParagraph();
		paragraphDate.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runDate = paragraphDate.createRun();
		runDate.setText("Від: " + Main.dfDate.format(dcFrom.getDate()));
		runDate.setFontSize(11);
		runDate.setFontFamily("Times New Roman");
		runDate.setItalic(true);
		

		XWPFParagraph paragraphDate2 = document.createParagraph();
		paragraphDate2.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runDate2 = paragraphDate2.createRun();
		runDate2.setText("До: " + Main.dfDate.format(dcTo.getDate()));
		runDate2.setFontSize(11);
		runDate2.setFontFamily("Times New Roman");
		runDate2.setItalic(true);
		
		XWPFTable table = document.createTable();
		table.setCellMargins(50, 50, 50, 50);
		
		XWPFTableRow row = table.getRow(0);	// Взяття першого існуючого стовпця
		XWPFTableCell cell = row.getCell(0);
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(700));
		XWPFParagraph paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
		paragraphCell.setSpacingAfter(0);
		XWPFRun runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("№");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(6200+2000));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		if(rbClient.isSelected())
			runCell.setText("Клієнт");
		else if(rbProduct.isSelected())
			runCell.setText("Продукт");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(900));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Сума, грн");
		
		// Другий стовп
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(700));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("№");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(6200+2000));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		if(rbClient.isSelected())
			runCell.setText("Клієнт");
		else if(rbProduct.isSelected())
			runCell.setText("Продукт");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(900));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Сума, грн");
		
		int n=this.table.getRowCount();		
		int pp=0;
		
		if(this.table.getRowCount()%2==0)
			pp=0;
		else
			pp=1;
		
		for (int i=0; i< n; i++) {
			
			if(i<n/2+pp){
				row = table.createRow();
				cell = row.getCell(0);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setBold(true);
				runCell.setText(Integer.toString(i+1) + ".");
	
				cell = row.getCell(1);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.LEFT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText(this.table.getValueAt(i, 0).toString());
				
				cell = row.getCell(2);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.CENTER);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText(this.table.getValueAt(i, 1).toString());
			}
			else{
				row = table.getRow(i-n/2-(pp-1));
				cell = row.getCell(3);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setBold(true);
				runCell.setText(Integer.toString(i+1) + ".");
	
				cell = row.getCell(4);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.LEFT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText(this.table.getValueAt(i, 0).toString());
				
				cell = row.getCell(5);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.CENTER);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText(this.table.getValueAt(i, 1).toString());
			}
		}
		
		float sum = Float.parseFloat(tfSum.getText());
		XWPFParagraph paragraphSUM = document.createParagraph();
		paragraphSUM.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runSUM = paragraphSUM.createRun();
		runSUM.addBreak();
		runSUM.setText("Сума: " + (int)sum + " грн. " + (int)((sum - (int)sum)*100) + " коп.");
		runSUM.setFontSize(11);
		runSUM.setFontFamily("Times New Roman");
		runSUM.setBold(true);
		
		String filename = "";
		
		if(rbClient.isSelected())
			filename = "Звіт по клієнтам";
		else if(rbProduct.isSelected())
			filename = "Звіт по продуктам";
		filename += " " + Main.dfDate.format(new Date());
		
		try {
			FileOutputStream output = new FileOutputStream("docs/" + filename +".docx");
			document.write(output);
			output.close();
			
			
			 //The desktop api can help calling other applications in our machine
		    //and also many other features...
		    Desktop desktop = Desktop.getDesktop();
		    try {
		    //desktop.print(new File("DocXfile.docx"));
		        desktop.print(new File("docs/" + filename +".docx"));
		    } catch (IOException e) {           
		        e.printStackTrace();
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void initData(){

		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM production";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				float countOnStorage = rs.getFloat("CountOnStorage");
			    productions.add(new Production(id, name, countOnStorage));;
		    }
			st.close();
			conn.close();
		}
		catch(Exception ex){ ex.printStackTrace(); }
		
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM Client";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int place_id = rs.getInt("place_id");
				
				Client cl = new Client(id, name, place_id);
				
			    model.addRow(new Object[]{cl, 0.0f});
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
