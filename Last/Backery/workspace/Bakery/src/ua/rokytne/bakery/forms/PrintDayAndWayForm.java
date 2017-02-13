package ua.rokytne.bakery.forms;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import com.toedter.calendar.JDateChooser;

import ua.rokytne.bakery.LoggedUser;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.Place;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.Request;
import javax.swing.border.TitledBorder;

public class PrintDayAndWayForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private JComboBox<Place> cbPlace;
	private JDateChooser dateChooser;

	private XWPFDocument document;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrintDayAndWayForm frame = new PrintDayAndWayForm();
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
	public PrintDayAndWayForm() {
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Друк накладної по маршруту");
		setBounds((Main.WIDTH-330)/2, (Main.HEIGTH-159)/2, 330, 159);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnPrint = new JButton("Друк");
		btnPrint.setBounds(10, 94, 89, 23);
		btnPrint.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String message = "Друк накладної по маршруту ";
				
				if(dateChooser.getDate() == null){
					JOptionPane.showMessageDialog(null, "Вкажіть будьласка дату відправлення!");
					return;
				}
				
				LinkedHashMap<Production, Integer> productions = new LinkedHashMap<Production, Integer>();
				ArrayList<Request> reqs = new ArrayList<Request>();
				
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
					    productions.put(new Production(id, name, countOnStorage), new Integer(0));
				    }
					st.close();
					
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
				Date d = null;
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					d = dateChooser.getDate();
					d.setHours(0);
					d.setMinutes(0);
					d.setSeconds(0);
					Date d2 = new Date(d.getTime());
					d2 = Main.addDays(d2, 1);
					String query = "SELECT * FROM request WHERE client_id in (SELECT id FROM client WHERE place_id= " + ((Place)cbPlace.getSelectedItem()).getID() + " ) AND "+
					"Date_Request >= \"" + Main.dfDateAndTime.format(d) + "\" AND Date_Request < \"" + Main.dfDateAndTime.format(d2) + "\"";

					message += ((Place)cbPlace.getSelectedItem()).getName() + "(" + Main.dfDate.format(d) + ")";
					
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
					
						Request rquest = new Request(id, client_id, date_Request, date_Oformleniya, ok, user_id);
						reqs.add(rquest);
				    }
				  st.close();
					
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
				if(reqs.size()==0){
					JOptionPane.showMessageDialog(null, "По даному запиту нічого не знайдено!");
					return;
				}
				
				// Обчислюємо count for everyProduction
				for(Request r:reqs){
					
					for (Map.Entry<Production, Integer> entry : productions.entrySet()) {
				        //System.out.println("Продукт =  " + entry.getKey() + " Кількість = " + entry.getValue());  
				        
				        try{
							Class.forName("com.mysql.jdbc.Driver");
							Connection conn = null;
							conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);

							Statement st = conn.createStatement();
							
							String query = "SELECT * FROM request_has_production where request_id = " + r.getId() + " and production_id = " +
							((Production)entry.getKey()).getID();
							ResultSet rs = st.executeQuery(query);
							  
							if (rs.next())
							{
								int count = rs.getInt("count");
								entry.setValue(entry.getValue() + count);
						    }
							st.close();
							
							conn.close();
						}
						catch(Exception ex){
							ex.printStackTrace();
						}
				        
				    }
					
				}
				Main.insertLog(message);
				makeDoc(((Place)cbPlace.getSelectedItem()), d, productions);
				
			}
		});
		contentPane.add(btnPrint);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(216, 94, 89, 23);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnCancel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "\u041F\u0430\u0440\u0430\u043C\u0435\u0442\u0440\u0438 \u0432\u0438\u0431\u0456\u0440\u043A\u0438", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 11, 295, 79);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel label = new JLabel("Населений пункт:");
		label.setBounds(10, 23, 99, 14);
		panel.add(label);
		
		cbPlace = new JComboBox<Place>();
		cbPlace.setBounds(119, 20, 165, 20);
		panel.add(cbPlace);
		
		dateChooser = new JDateChooser();
		dateChooser.setBounds(119, 48, 165, 20);
		panel.add(dateChooser);
		
		JLabel label_1 = new JLabel("Дата відправлення:");
		label_1.setBounds(10, 54, 113, 14);
		panel.add(label_1);
	
		
		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            setSize(c.getWidth(), 159);
	            int width=c.getWidth(), heigth=159;

	    		btnCancel.setBounds(width-89-25, 94, 89, 23);
	    		dateChooser.setBounds(119, 48, width-23-119-20, 20);
	    		cbPlace.setBounds(119, 20, width-23-119-20, 20);
	    		panel.setBounds(10, 11, width-30, 79);
				
	        }
		});
		
		
		setTestData();
	}
	
	private void makeDoc(Place place, Date date, LinkedHashMap<Production, Integer> productions){
		
		document = new XWPFDocument();
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
		runTitle.setText("Накладна " + place.getName());
		runTitle.setFontSize(14);
		runTitle.setFontFamily("Times New Roman");
		runTitle.setBold(true);
		
		XWPFParagraph paragraphDate = document.createParagraph();
		paragraphDate.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runDate = paragraphDate.createRun();
		runDate.setText("Дата: " + Main.dfDate.format(date));
		runDate.setFontSize(11);
		runDate.setFontFamily("Times New Roman");
		runDate.setItalic(true);
		
		XWPFTable table = document.createTable();
		table.setCellMargins(50, 50, 50, 50);
		
		XWPFTableRow row = table.getRow(0);	// Взяття першого існуючого стовпця
		XWPFTableCell cell = row.getCell(0);
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(700/q));
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(6900/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Назва продукту");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(900/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Од.вим.");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(500/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Кількість");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1100/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Ціна, грн");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1100/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Сума, грн");
		
		// Другий стовпець
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(700/q));
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(6900/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Назва продукту");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(900/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Од.вим.");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(500/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Кількість");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1100/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Ціна, грн");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1100/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Сума, грн");
		
		int n=0;
		for (Map.Entry<Production, Integer> entry : productions.entrySet()) {
			if(entry.getValue()!=0)
				n++;
		}
		
		int pp=0;
		
		if(n%2==0)
			pp=0;
		else
			pp=1;
		
		double sum = 0.0d;
		int i=0;
		for (Map.Entry<Production, Integer> entry : productions.entrySet()) {
			
			if(entry.getValue()!=0){
			
				if(i<n/2+pp){
					row = table.createRow();
					cell = row.getCell(0);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setBold(true);
					runCell.setFontFamily(fontName);
					runCell.setText(entry.getKey().getID() + ".");
		
					cell = row.getCell(1);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.LEFT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(entry.getKey().getName());
					
					cell = row.getCell(2);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.CENTER);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText("шт.");
		
					cell = row.getCell(3);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(entry.getValue().toString());
		
					float price = 0.0f;
					
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
	
				    	String queryPHI = "SELECT * FROM product_price WHERE production_id = " + 
				    	entry.getKey().getID() +" and place_id = " + place.getID();
						Statement stPHI = conn.createStatement();
						ResultSet rsPHI = stPHI.executeQuery(queryPHI);
						
						if(rsPHI.next()){ // Якщо є
							price = rsPHI.getFloat("price");
						}
						else{ // Якщо нема
							String query = " insert into product_price(production_id, place_id, price)" + " values (?, ?, ?)";
							PreparedStatement preparedStmt = conn.prepareStatement(query);
							preparedStmt.setInt (1, entry.getKey().getID());
							preparedStmt.setInt (2, ((Place)cbPlace.getSelectedItem()).getID());
							preparedStmt.setFloat(3, 0.0f);
							preparedStmt.execute();
						}
						
						stPHI.close();
						conn.close();
					}
					catch(Exception ex){}
					
					cell = row.getCell(4);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(String.format("%.2f", price));
					
					cell = row.getCell(5);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(String.format("%.2f", price*entry.getValue()));
					sum += price*entry.getValue();
				}
				else{
					//
					row = table.getRow(i-n/2-(pp-1));
					cell = row.getCell(6);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setBold(true);
					runCell.setFontFamily(fontName);
					runCell.setText(entry.getKey().getID() + ".");
		
					cell = row.getCell(7);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.LEFT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(entry.getKey().getName());
					
					cell = row.getCell(8);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.CENTER);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText("шт.");
		
					cell = row.getCell(9);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(entry.getValue().toString());
		
					float price = 0.0f;
					
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
	
				    	String queryPHI = "SELECT * FROM product_price WHERE production_id = " + 
				    	entry.getKey().getID() +" and place_id = " + place.getID();
						Statement stPHI = conn.createStatement();
						ResultSet rsPHI = stPHI.executeQuery(queryPHI);
						
						if(rsPHI.next()){ // Якщо є
							price = rsPHI.getFloat("price");
						}
						else{ // Якщо нема
							String query = " insert into product_price(production_id, place_id, price)" + " values (?, ?, ?)";
							PreparedStatement preparedStmt = conn.prepareStatement(query);
							preparedStmt.setInt (1, entry.getKey().getID());
							preparedStmt.setInt (2, ((Place)cbPlace.getSelectedItem()).getID());
							preparedStmt.setFloat(3, 0.0f);
							preparedStmt.execute();
						}
						
						stPHI.close();
						conn.close();
					}
					catch(Exception ex){ex.printStackTrace();}
					
					cell = row.getCell(10);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(String.format("%.2f", price));
					
					cell = row.getCell(11);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(String.format("%.2f", price*entry.getValue()));
					sum += price*entry.getValue();
				}
				i++;
			}
		}
		
		XWPFParagraph paragraphSUM = document.createParagraph();
		paragraphSUM.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runSUM = paragraphSUM.createRun();
		runSUM.addBreak();
		runSUM.setText("СУМА ДО СПЛАТИ: " + String.format("%.2f", sum) + " грн.");
		runSUM.setFontSize(11);
		runSUM.setFontFamily("Times New Roman");
		runSUM.setBold(true);
		
		try {
			FileOutputStream output = new FileOutputStream("docs/Накладна " + place.getName() + " " + Main.dfDate.format(date)+".docx");
			document.write(output);
			output.close();
			
			 //The desktop api can help calling other applications in our machine
		    //and also many other features...
		    Desktop desktop = Desktop.getDesktop();
		    try {
		    //desktop.print(new File("DocXfile.docx"));
		        desktop.print(new File("docs/Накладна " + place.getName() + " " + Main.dfDate.format(date)+".docx"));
		    } catch (IOException e) {           
		        e.printStackTrace();
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Видалити, або не запускати в продакшені.
	 */
	public void setTestData(){
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM place";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				cbPlace.addItem(new Place(id, name));
				
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			
		}
	}
}
