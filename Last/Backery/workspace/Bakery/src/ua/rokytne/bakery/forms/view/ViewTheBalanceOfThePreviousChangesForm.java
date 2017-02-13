package ua.rokytne.bakery.forms.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
import ua.rokytne.bakery.forms.newedit.NewTheBalanceOfThePreviousChangesForm;
import ua.rokytne.bakery.orm.OldNewReport;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.ReportTeam;

public class ViewTheBalanceOfThePreviousChangesForm extends JDialog {

	private final JPanel contentPanel = new JPanel();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	Object[] columnNames = {"Дата", "Опис"};
	Object[][] data = {};
	DefaultTableModel model;
	private XWPFDocument document;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ViewTheBalanceOfThePreviousChangesForm dialog = new ViewTheBalanceOfThePreviousChangesForm();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ViewTheBalanceOfThePreviousChangesForm() {
		model = new DefaultTableModel(data, columnNames) {

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("\u0417\u0432\u0456\u0442\u0438 \u043F\u043E\u043F\u0435\u0440\u0435\u0434\u043D\u0456\u0445 \u0437\u043C\u0456\u043D \u0442\u0430 \u043F\u043E\u0442\u043E\u0447\u043D\u0435");
		setBounds((Main.WIDTH-607)/2, (Main.HEIGTH-293)/2, 655, 293);
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
		
		JButton btnCreate = new JButton("Створити попередній");
		btnCreate.setBackground(Color.GREEN);
		btnCreate.setBounds(480, 11, 154, 23);
		contentPane.add(btnCreate);
		
		boolean enable = false;
		
		for(int i=0; i<Main.mainFrame.productionTable.getRowCount(); i++){
			if((int)Float.parseFloat(Main.mainFrame.productionTable.getValueAt(i, 1).toString())!=0){
				enable = true;
				break;
			}
		}
		btnCreate.setEnabled(enable);
		
		
		JButton btnCreateN = new JButton("Створити новий");
		btnCreateN.setBackground(Color.GREEN);
		btnCreateN.setBounds(480, 45, 154, 23);
		contentPane.add(btnCreateN);
		btnCreateN.setEnabled(!enable);
				
		JButton btnView = new JButton("Переглянути");
		btnView.setEnabled(false);
		btnView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OldNewReport toView = (OldNewReport)model.getValueAt(table.getSelectedRow(), 1);
				NewTheBalanceOfThePreviousChangesForm frame = new NewTheBalanceOfThePreviousChangesForm(true, toView);
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		btnView.setBackground(Color.CYAN);
		btnView.setBounds(480, 45+34, 154, 23);
		contentPane.add(btnView);
		
		JButton btnPrint = new JButton("Друк");
		
		btnPrint.setEnabled(false);
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Main.insertLog("Друк залишку " + ((OldNewReport)model.getValueAt(table.getSelectedRow(), 1)).getDateTime());
				makeDoc((OldNewReport)model.getValueAt(table.getSelectedRow(), 1));
				
			}
		});
		btnPrint.setBackground(Color.CYAN);
		btnPrint.setBounds(480, 79+34, 154, 23);
		contentPane.add(btnPrint);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(480, 226, 154, 23);
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
				
				int result = JOptionPane.showConfirmDialog((Component) null, "Дійсно створити?", "alert", JOptionPane.OK_CANCEL_OPTION);
				
				if(result!=0)
					return;
				
				Main.insertLog("Збережння попереднього залишку");
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					// Вставити Власне звіт
					String query = "insert into oldnewreport(DateTime, old, User_id)" + " values (?, ?, ?)";
					PreparedStatement preparedStmt = conn.prepareStatement(query);
					preparedStmt.setInt (3, LoggedUser.ID);
					preparedStmt.setBoolean(2, true);
					preparedStmt.setString(1, Main.dfDateAndTime.format(new Date()));
					preparedStmt.execute();
					
					
					query = "SELECT * FROM oldnewreport WHERE DateTime = (SELECT MAX(DateTime) FROM oldnewreport)";
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					OldNewReport onr = null;
					if (rs.next())
					{
						int id = rs.getInt("id");
						int uID = rs.getInt("User_id");
						String datetime = rs.getString("DateTime");
						boolean old = rs.getBoolean("old");
						onr = new OldNewReport(id, datetime, old, uID);
						model.addRow(new Object[]{onr.getDateTime(), onr});
					}
					
					if(onr==null){
						conn.close();
						btnCreate.setEnabled(false);
						return;
					}
					
					// Вставка значень
					String queryI = "insert into production_has_oldnewreport(Production_id, OldNewReport_id, Count)" + " values ";
					for(int i=0; i<Main.mainFrame.productionTable.getRowCount(); i++){
						int pId = ((Production)Main.mainFrame.productionTable.getValueAt(i, 0)).getID();
						float count = Float.parseFloat(Main.mainFrame.productionTable.getValueAt(i, 1).toString());
						
						queryI += "("+pId+", "+onr.getId()+", "+(int)count+"),";
						
						Main.mainFrame.productionTable.setValueAt(0, i, 1);
					}
					
					try{
						PreparedStatement preparedStmti = conn.prepareStatement(queryI.substring(0, queryI.length()-1));
						preparedStmti.execute();
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
					
					
					for(int j=0; j<Main.mainFrame.productionTable.getRowCount(); j++){
						Production p = ((Production)Main.mainFrame.productionTable.getValueAt(j, 0));
						
						String queryU = "UPDATE production set CountOnStorage =  ? WHERE id = ?";
						PreparedStatement preparedStmtU = conn.prepareStatement(queryU);
						preparedStmtU.setInt(1, 0);
						preparedStmtU.setInt(2, p.getID());
						preparedStmtU.execute();
							
						
					}
					
					
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
				btnCreate.setEnabled(false);
				btnCreateN.setEnabled(true);
			}
		});
		
		btnCreateN.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NewTheBalanceOfThePreviousChangesForm frame = new NewTheBalanceOfThePreviousChangesForm(false, null);
				frame.setModal(true);
				frame.setVisible(true);
				
				if(frame.lastInsert==null)
					return;
				
				model.addRow(new Object[]{frame.lastInsert.getDateTime(), frame.lastInsert});
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
		            int width=c.getWidth()-55, heigth=c.getHeight();
		            
		            
		    		jsp.setBounds(10, 11, width-100-15-20, heigth-40-10-5);


		    		btnCancel.setBounds(width-20-100, 226, 154, 23);

		    		btnCreate.setBounds(width-20-100, 11, 154, 23);
		    		btnCreateN.setBounds(width-20-100, 11+34, 154, 23);
		    		btnPrint.setBounds(width-20-100, 45+34, 154, 23);
		    		btnView.setBounds(width-20-100, 79+34, 154, 23);
		        }
		});
		
		initData();
	}
	
	private void makeDoc(OldNewReport reportTeam){

		
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
		if(reportTeam.getOld())
			runTitle.setText("Залишок(програмний) зміни за " + reportTeam.getDateTime());
		else
			runTitle.setText("Залишок(реальний) зміни за " + reportTeam.getDateTime());
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
				runCell.setText(p.getID() + ".");
	
				cell = row.getCell(1);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.LEFT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText(p.getName());
				
				int v=0;
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM production_has_oldnewreport where OldNewReport_id = " + reportTeam.getId() + " and production_id = " + p.getID();
					
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					if (rs.next())
						v = rs.getInt("count");
					
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
				runCell.setText(p.getID() + ".");
	
				cell = row.getCell(4);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.LEFT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText(p.getName());
				
				int v=0;
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM production_has_oldnewreport where OldNewReport_id = " + reportTeam.getId() + " and production_id = " + p.getID();
					
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					if (rs.next())
						v = rs.getInt("count");
					
					st.close();
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
				cell = row.getCell(5);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.CENTER);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				if(v!=0)
				runCell.setText(v+"");
	
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
			FileOutputStream output = new FileOutputStream("docs/Залишок " + reportTeam.getDateTime().substring(0, reportTeam.getDateTime().length()-2).replace(":", "-") +".docx");
			document.write(output);
			output.close();
			
			
		    Desktop desktop = Desktop.getDesktop();
		    try {
		        desktop.print(new File("docs/Залишок " + reportTeam.getDateTime().substring(0, reportTeam.getDateTime().length()-2).replace(":", "-")  +".docx"));
		    } catch (IOException e) {           
		        e.printStackTrace();
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void initData(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM oldnewreport";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				int uID = rs.getInt("User_id");
				boolean old = rs.getBoolean("old");
				String datetime = rs.getString("DateTime");
				
				OldNewReport rt = new OldNewReport(id, datetime, old, uID);
				
				
			    model.addRow(new Object[]{rt.getDateTime(), rt});
				
				
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
