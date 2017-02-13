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

public class PrintDayForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JDateChooser dateChooser;

	private XWPFDocument document;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrintDayForm frame = new PrintDayForm();
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
	public PrintDayForm() {
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Друк необхідної кількості продукції");
		setBounds((Main.WIDTH-305)/2, (Main.HEIGTH-103)/2, 305, 103);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
				
		JLabel label_1 = new JLabel("Дата відправлення:");
		label_1.setBounds(10, 42-30, 113, 14);
		contentPane.add(label_1);
		
		JButton btnPrint = new JButton("Друк");
		btnPrint.setBounds(10, 67-30, 89, 23);
		btnPrint.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String message = "Друк накладної на число ";
				
				if(dateChooser.getDate() == null){
					JOptionPane.showMessageDialog(null, "Вкажіть будь ласка дату приготування!");
					return;
				}
				
				LinkedHashMap<Production, Integer> productions = new LinkedHashMap <Production, Integer>();
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
						
						int count = 0;
						
						if(Main.mainFrame!=null)
							for(int i=0; i<Main.mainFrame.productionTable.getRowCount(); i++){
								if(((Production)Main.mainFrame.productionTable.getValueAt(i, 0)).getID() == id)
								{
									count = (int)Float.parseFloat((Main.mainFrame.productionTable.getValueAt(i, 1)).toString());
									break;
								}
							}
						
					    productions.put(new Production(id, name, countOnStorage), new Integer(count));
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
					String query = "SELECT * FROM request WHERE "+
					"Date_Request >= \"" + Main.dfDateAndTime.format(d) + "\" AND Date_Request < \"" + Main.dfDateAndTime.format(d2) + "\"";

					message +=  Main.dfDate.format(d);
					
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
					
					if(r.getOK())
						continue;
					
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
								entry.setValue(entry.getValue() - count);
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
				makeDoc(d, productions);
				
			}
		});
		contentPane.add(btnPrint);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(195, 67-30, 89, 23);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnCancel);
				
		dateChooser = new JDateChooser();
		dateChooser.setBounds(119, 36-30, 165, 20);
		contentPane.add(dateChooser);
		
		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            setSize(c.getWidth(), 123-20);
	            int width=c.getWidth(), heigth=123-20;

	    		btnCancel.setBounds(width-89-25, 67-30, 89, 23);
	    		dateChooser.setBounds(119, 36-30, width-23-119, 20);
				
	        }
		});
		
	}
	
	private void makeDoc(Date date, LinkedHashMap<Production, Integer> productions){
		
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
		runTitle.setText("Список необхідної продукції");
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(10500/q));
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
		
		// Другий
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(10500/q));
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
		
		int n=0;
		for (Map.Entry<Production, Integer> entry : productions.entrySet()) {
			if(entry.getValue()<0)
				n++;
		}
		
		int pp=0;
		
		if(n%2==0)
			pp=0;
		else
			pp=1;
		
		
		int i=0;
		for (Map.Entry<Production, Integer> entry : productions.entrySet()) {
			
			if(entry.getValue()<0){

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
					runCell.setText(entry.getValue().toString().replace('-', ' '));
				}
				else{
					row = table.getRow(i-n/2-(pp-1));
					cell = row.getCell(4);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setBold(true);
					runCell.setText(entry.getKey().getID() + ".");
		
					cell = row.getCell(5);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.LEFT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(entry.getKey().getName());
					
					cell = row.getCell(6);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.CENTER);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText("шт.");
		
					cell = row.getCell(7);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(entry.getValue().toString().replace('-', ' '));
				}
				i++;
			}
		}
		
		try {
			FileOutputStream output = new FileOutputStream("docs/Список необхідної продукції " + Main.dfDate.format(date)+".docx");
			document.write(output);
			output.close();
			
			 //The desktop api can help calling other applications in our machine
		    //and also many other features...
		    Desktop desktop = Desktop.getDesktop();
		    try {
		    //desktop.print(new File("DocXfile.docx"));
		        desktop.print(new File("docs/Список необхідної продукції " + Main.dfDate.format(date)+".docx"));
		    } catch (IOException e) {           
		        e.printStackTrace();
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
