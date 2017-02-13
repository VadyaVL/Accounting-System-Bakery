package ua.rokytne.bakery.forms.view;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import ua.rokytne.bakery.LoggedUser;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.forms.newedit.NewReportTeamForm;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.ReportTeam;

import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;

public class ViewReportTeamForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	Object[] columnNames = {"Дата", "Експедитор"};
	Object[][] data = {};
	DefaultTableModel model;
	private XWPFDocument document;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewReportTeamForm frame = new ViewReportTeamForm(false);
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
	public ViewReportTeamForm(boolean print) {
		model = new DefaultTableModel(data, columnNames) {

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Виробничі звіти");
		setBounds((Main.WIDTH-607)/2, (Main.HEIGTH-293)/2, 607, 293);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table = new JTable(model);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setBounds(0, 0, 460, 248);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(10, 11, 460, 238);
		contentPane.add(jsp);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JButton btnCreate = new JButton("Створити");
		btnCreate.setBackground(Color.GREEN);
		btnCreate.setBounds(480, 11, 100, 23);
		contentPane.add(btnCreate);
		
		if(print)
			btnCreate.setVisible(false);
		
		JButton btnView = new JButton("Переглянути");
		btnView.setEnabled(false);
		btnView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ReportTeam toView = (ReportTeam)model.getValueAt(table.getSelectedRow(), 0);
				NewReportTeamForm frame = new NewReportTeamForm(true, toView);
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		btnView.setBackground(Color.CYAN);
		btnView.setBounds(480, 45, 100, 23);
		contentPane.add(btnView);
		
		JButton btnPrint = new JButton("Друк");
		btnPrint.setEnabled(false);
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Main.insertLog("Друк виробничого звіту за " + ((ReportTeam)model.getValueAt(table.getSelectedRow(), 0)).getDateTime());
				makeDoc((ReportTeam)model.getValueAt(table.getSelectedRow(), 0));
				
			}
		});
		btnPrint.setBackground(Color.CYAN);
		btnPrint.setBounds(480, 79, 100, 23);
		contentPane.add(btnPrint);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(480, 226, 100, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				NewReportTeamForm frame = new NewReportTeamForm(false, null);
				frame.setModal(true);
				frame.setVisible(true);
				
				String userName = "";
				
				if(frame.lastInsert==null)
					return;
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM User where id = " + frame.lastInsert.getUserID();
					Statement stU = conn.createStatement();
					ResultSet rsU = stU.executeQuery(query);
						
					if(rsU.next())
						userName = rsU.getString("RealName");
					
					stU.close();
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
				if(frame.lastInsert!=null){
					model.addRow(new Object[]{frame.lastInsert, userName});
				}
				
			}
		});
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!btnView.isEnabled())
					btnView.setEnabled(true);

				if(!btnPrint.isEnabled())
					btnPrint.setEnabled(true);
			}
		});
		
		
		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.3));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.7));
		
		addComponentListener(new ComponentAdapter() 
		{  
		        public void componentResized(ComponentEvent evt) {
		            Component c = (Component)evt.getSource();
		            int width=c.getWidth(), heigth=c.getHeight();
		            
		    		jsp.setBounds(10, 11, width-100-15-20, heigth-40-10-5);


		    		btnCancel.setBounds(width-20-100, 226, 100, 23);
		    		
		    		btnCreate.setBounds(width-20-100, 11, 100, 23);
		    		btnPrint.setBounds(width-20-100, 45, 100, 23);
		    		btnView.setBounds(width-20-100, 79, 100, 23);
		        }
		});
		
		initData();
	}
	
	
	private void makeDoc(ReportTeam reportTeam){

		
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
		runTitle.setText("Виробничий звіт за " + reportTeam.getDateTime());
		runTitle.setFontSize(14);
		runTitle.setFontFamily("Times New Roman");
		runTitle.setBold(true);

		XWPFParagraph paragraphDate = document.createParagraph();
		paragraphDate.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runDate = paragraphDate.createRun();
		runDate.setText("Дата початку зміни: " + Main.dfDateAndTime.format(LoggedUser.begin));
		runDate.setFontSize(11);
		runDate.setFontFamily("Times New Roman");
		runDate.setItalic(true);
		

		XWPFParagraph paragraphDate2 = document.createParagraph();
		paragraphDate2.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runDate2 = paragraphDate2.createRun();
		runDate2.setText("Дата друку: " + Main.dfDateAndTime.format(new Date()));
		runDate2.setFontSize(11);
		runDate2.setFontFamily("Times New Roman");
		runDate2.setItalic(true);
		
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(8300/q));
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1100/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Вироблено");
				
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
		runCell.setText("Брак");
		
		
		// Новий стовпець
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(8300/q));
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1100/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Вироблено");
				
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
		runCell.setText("Брак");
		
		ArrayList<Production> productions = new ArrayList<Production>();
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
			    productions.add(new Production(id, name, countOnStorage));
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		int n=productions.size();
		
		int pp=0;
		
		if(n%2==0)
			pp=0;
		else
			pp=1;
		
		int i=0;
		for (Production p : productions) {
			
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
				runCell.setText(p.getName());
				
				int v=0, b=0;
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM produced where PRT_id = " + reportTeam.getID() + " and production_id = " + p.getID();
					
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					if (rs.next())
						v = rs.getInt("count");
					
					st.close();
					
					query = "SELECT * FROM brak where PRT_id = " + reportTeam.getID() + " and production_id = " + p.getID();
					
					st = conn.createStatement();
					rs = st.executeQuery(query);
					  
					if (rs.next())
						b = rs.getInt("count");
					
					st.close();
					
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
				cell = row.getCell(2);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.CENTER);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				if(v!=0)
				runCell.setText(v+"");
	
				cell = row.getCell(3);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				if(b!=0)
				runCell.setText(b+"");
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
				runCell.setText(Integer.toString(i+1) + ".");
	
				cell = row.getCell(5);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.LEFT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText(p.getName());
				
				int v=0, b=0;
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM produced where PRT_id = " + reportTeam.getID() + " and production_id = " + p.getID();
					
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					if (rs.next())
						v = rs.getInt("count");
					
					st.close();
					
					query = "SELECT * FROM brak where PRT_id = " + reportTeam.getID() + " and production_id = " + p.getID();
					
					st = conn.createStatement();
					rs = st.executeQuery(query);
					  
					if (rs.next())
						b = rs.getInt("count");
					
					st.close();
					
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
				cell = row.getCell(6);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.CENTER);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				if(v!=0)
				runCell.setText(v+"");
	
				cell = row.getCell(7);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				if(b!=0)
				runCell.setText(b+"");
			}
			
			i++;
		}
		
		XWPFParagraph paragraphManagerClient = document.createParagraph();
		paragraphManagerClient.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runManagerClient = paragraphManagerClient.createRun();
		runManagerClient.addBreak();
		runManagerClient.setText("Майстер зміни: " + LoggedUser.name);
		runManagerClient.setFontSize(11);
		runManagerClient.setFontFamily("Times New Roman");
		
		try {
			FileOutputStream output = new FileOutputStream("docs/Виробничий звіт за " + reportTeam.getDateTime().substring(0, reportTeam.getDateTime().length()-2).replace(":", "-") +".docx");
			document.write(output);
			output.close();
			
			
		    Desktop desktop = Desktop.getDesktop();
		    try {
		        desktop.print(new File("docs/Виробничий звіт за " + reportTeam.getDateTime().substring(0, reportTeam.getDateTime().length()-2).replace(":", "-")  +".docx"));
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
			
			String query = "SELECT * FROM theproductionreportteam";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				int uID = rs.getInt("User_id");
				String datetime = rs.getString("DateTime");
				ReportTeam rt = new ReportTeam(id, uID, datetime);
				
				query = "SELECT * FROM User where id = " + uID;
				Statement stU = conn.createStatement();
				ResultSet rsU = stU.executeQuery(query);
				
				if(rsU.next()){
				    model.addRow(new Object[]{rt, rsU.getString("RealName")});
				}else{
				    model.addRow(new Object[]{rt, null});
				}
				stU.close();
				
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
