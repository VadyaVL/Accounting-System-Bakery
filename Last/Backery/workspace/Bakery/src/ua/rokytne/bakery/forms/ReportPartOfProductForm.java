package ua.rokytne.bakery.forms;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
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

import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.Ingridient;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.ReportTeam;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JButton;
import com.toedter.calendar.JDateChooser;

/***
 * Форма для звіту по сировині.
 * ПРОМІЖОК ЧАСУ
 * Враховуємо:
 * Брак + виробничий звіт + списання.
 * 
 * @author Vadym
 *
 */
public class ReportPartOfProductForm extends JDialog {

	private JPanel contentPane;
	private JDateChooser dcTo, dcFrom;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReportPartOfProductForm frame = new ReportPartOfProductForm();
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
	public ReportPartOfProductForm() {
		this.setAlwaysOnTop(Main.onTop);
		setResizable(false);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Сировина по виробничому звіту");
		setBounds((Main.WIDTH-252)/2, (Main.HEIGTH-130)/2, 252, 130);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnPrint = new JButton("Друк");
		btnPrint.setBounds(10, 73, 81, 23);
		btnPrint.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				String mess = "Друк звіту по сировині [";
				mess += Main.dfDate.format(dcFrom.getDate()) + "; " + Main.dfDate.format(dcTo.getDate()) + "]";
				
				Main.insertLog(mess);
				calculate();
				
			}
		});
		contentPane.add(btnPrint);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(155, 73, 81, 23);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnCancel);
		
		dcTo = new JDateChooser();
		dcTo.setBounds(35, 42, 201, 20);
		contentPane.add(dcTo);
		
		JLabel label = new JLabel("до:");
		label.setBounds(10, 45, 24, 14);
		contentPane.add(label);
		
		dcFrom = new JDateChooser();
		dcFrom.setBounds(35, 11, 201, 20);
		contentPane.add(dcFrom);
		
		JLabel label_1 = new JLabel("Від:");
		label_1.setBounds(10, 14, 24, 14);
		contentPane.add(label_1);
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
		
		if(!from.before(to)){
			JOptionPane.showMessageDialog(null, "Вкажіть проміжок коректно.");
			return;
		}
		

		ArrayList<Production> productions = new ArrayList<>();
		ArrayList<int[]> vb = new ArrayList<int[]>();
		ArrayList<ReportTeam> reporTeams = new ArrayList<>();
		ArrayList<Ingridient> ingridients = new ArrayList<>();
		ArrayList<Float> ingridientsSum = new ArrayList<>();
		
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
			    vb.add(new int[]{0, 0});
		    }
			st.close();
			conn.close();
		}
		catch(Exception ex){ ex.printStackTrace(); }
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM ingridient";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				float countOnStorage = rs.getFloat("CountOnStorage");
			    ingridients.add(new Ingridient(id, name, countOnStorage));
			    ingridientsSum.add(new Float(0.0f));
			 }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){}
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM theproductionreportteam where DateTime >= \"" + Main.dfDateAndTime.format(from) + "\" and DateTime <= \"" +Main.dfDateAndTime.format(to) +"\"";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				int uID = rs.getInt("User_id");
				String datetime = rs.getString("DateTime");
				reporTeams.add(new ReportTeam(id, uID, datetime));
		    }
			st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		for(int i=0; i<productions.size(); i++){

			int vCount = 0, bCount = 0;
			for(ReportTeam rt : reporTeams){
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM produced WHERE PRT_id = " + rt.getID() + " and production_id = " + productions.get(i).getID();
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					if (rs.next())
						vCount += rs.getInt("Count");
					rs.close();
					
					query = "SELECT * FROM brak WHERE PRT_id = " + rt.getID() + " and production_id = " + productions.get(i).getID();
					rs = st.executeQuery(query);
					  
					if (rs.next())
						bCount += rs.getInt("Count");
					
					st.close();
					conn.close();
				}
				catch(Exception ex){ ex.printStackTrace();}
			}

			vb.get(i)[0] = vCount;
			vb.get(i)[1] = bCount;
			
		}
		
		
		/////////////////////
		XWPFDocument document = new XWPFDocument();
		CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
	    CTPageMar pageMar = sectPr.addNewPgMar();
	    pageMar.setLeft(BigInteger.valueOf(400L));
	    pageMar.setTop(BigInteger.valueOf(400L));
	    pageMar.setRight(BigInteger.valueOf(400L));
	    pageMar.setBottom(BigInteger.valueOf(400L));
		int q=4;
		int tableFontSize=8;
		int rowHeight=50;
		String fontName = "Calibri";
		
		XWPFParagraph paragraphTitle = document.createParagraph();
		paragraphTitle.setSpacingAfter(0);
		paragraphTitle.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun runTitle = paragraphTitle.createRun();
		runTitle.setText("Витрати сировини");
		runTitle.setFontSize(14);
		runTitle.setFontFamily("Times New Roman");
		runTitle.setBold(true);

		XWPFParagraph paragraphDate = document.createParagraph();
		paragraphDate.setSpacingAfter(0);
		paragraphDate.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runDate = paragraphDate.createRun();
		runDate.setText("Від: " + Main.dfDate.format(dcFrom.getDate()));
		runDate.setFontSize(11);
		runDate.setFontFamily("Times New Roman");
		runDate.setItalic(true);
		

		XWPFParagraph paragraphDate2 = document.createParagraph();
		paragraphDate2.setAlignment(ParagraphAlignment.RIGHT);
		paragraphDate2.setSpacingAfter(0);
		XWPFRun runDate2 = paragraphDate2.createRun();
		runDate2.setText("До: " + Main.dfDate.format(dcTo.getDate()));
		runDate2.setFontSize(11);
		runDate2.setFontFamily("Times New Roman");
		runDate2.setItalic(true);
		
		
		for(int j=0; j<productions.size(); j++){
			XWPFParagraph prod = document.createParagraph();
			prod.setSpacingAfter(0);
			XWPFRun rdp = prod.createRun();
			rdp.setText(productions.get(j).getName());
			rdp.setFontSize(tableFontSize);
			rdp.setBold(true);
			rdp.setFontFamily("Calibri");
			
			XWPFParagraph prodV = document.createParagraph();
			prodV.setSpacingAfter(0);
			XWPFRun rdpV = prodV.createRun();
			rdpV.setText("Виготовлено: " + vb.get(j)[0]);
			rdpV.addBreak();
			rdpV.setText("Брак: " + vb.get(j)[1]);
			rdpV.setFontSize(tableFontSize);
			rdpV.setFontFamily("Calibri");
		
		
			XWPFTable table = document.createTable();
			table.setCellMargins(50, 50, 50, 50);
			
			XWPFTableRow row = table.getRow(0);	// Взяття першого існуючого стовпця
			XWPFTableCell cell = row.getCell(0);
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1400/q));
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
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf((6200+2000)/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Інгридієнт");
		
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1200/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Витрати, кг");

			// 2-й
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1400/q));
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
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf((6200+2000)/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Інгридієнт");
		
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1200/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Витрати, кг");
			
			// 3-й
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1400/q));
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
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf((6200+2000)/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Інгридієнт");
		
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1200/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Витрати, кг");
		
			// 4-й
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1400/q));
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
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf((6200+2000)/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Інгридієнт");
		
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1200/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Витрати, кг");
			
			int n = 0;
			
			if(ingridients.size()%4==0)
				n = ingridients.size()/4;
			else
				n = ingridients.size()/4+1;
			
			int i=0;
			for (; i< ingridients.size(); i++) {
				if(i<n){
					row = table.createRow();
					
					cell = row.getCell(0);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(Integer.toString(i+1) + ".");
		
					cell = row.getCell(1);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.LEFT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(ingridients.get(i).getName());
					
					float need = 0.0f;
					//Need
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
						
						String query = "SELECT * FROM production_has_ingridient WHERE production_id = " + productions.get(j).getID() + " and ingridient_id = " + ingridients.get(i).getID();
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
						  
						if (rs.next())
							need = rs.getFloat("Count");
						
						rs.close();
						st.close();
						conn.close();
					}
					catch(Exception ex){ ex.printStackTrace();}
					
					cell = row.getCell(2);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.CENTER);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(need*(vb.get(j)[0]+vb.get(j)[1]) + "");
					ingridientsSum.set(i, Float.sum(ingridientsSum.get(i),  need*(vb.get(j)[0]+vb.get(j)[1])));
	
					row.setHeight(rowHeight);
				}
				else if(i<2*n){
					row = table.getRow(i-n+1);
					
					cell = row.getCell(3);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(Integer.toString(i+1) + ".");
		
					cell = row.getCell(4);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.LEFT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(ingridients.get(i).getName());
					
					float need = 0.0f;
					//Need
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
						
						String query = "SELECT * FROM production_has_ingridient WHERE production_id = " + productions.get(j).getID() + " and ingridient_id = " + ingridients.get(i).getID();
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
						  
						if (rs.next())
							need = rs.getFloat("Count");
						
						rs.close();
						st.close();
						conn.close();
					}
					catch(Exception ex){ ex.printStackTrace();}
					
					cell = row.getCell(5);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.CENTER);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(need*(vb.get(j)[0]+vb.get(j)[1]) + "");
					ingridientsSum.set(i, Float.sum(ingridientsSum.get(i),  need*(vb.get(j)[0]+vb.get(j)[1])));
	
					row.setHeight(rowHeight);
				}
				else if(i<3*n){
					row = table.getRow(i-2*n+1);
					
					cell = row.getCell(6);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(Integer.toString(i+1) + ".");
		
					cell = row.getCell(7);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.LEFT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(ingridients.get(i).getName());
					
					float need = 0.0f;
					//Need
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
						
						String query = "SELECT * FROM production_has_ingridient WHERE production_id = " + productions.get(j).getID() + " and ingridient_id = " + ingridients.get(i).getID();
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
						  
						if (rs.next())
							need = rs.getFloat("Count");
						
						rs.close();
						st.close();
						conn.close();
					}
					catch(Exception ex){ ex.printStackTrace();}
					
					cell = row.getCell(8);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.CENTER);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(need*(vb.get(j)[0]+vb.get(j)[1]) + "");
					ingridientsSum.set(i, Float.sum(ingridientsSum.get(i),  need*(vb.get(j)[0]+vb.get(j)[1])));
	
					row.setHeight(rowHeight);
				}
				else if(i<4*n){
					row = table.getRow(i-3*n+1);
					
					cell = row.getCell(9);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(Integer.toString(i+1) + ".");
		
					cell = row.getCell(10);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.LEFT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(ingridients.get(i).getName());
					
					float need = 0.0f;
					//Need
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
						
						String query = "SELECT * FROM production_has_ingridient WHERE production_id = " + productions.get(j).getID() + " and ingridient_id = " + ingridients.get(i).getID();
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
						  
						if (rs.next())
							need = rs.getFloat("Count");
						
						rs.close();
						st.close();
						conn.close();
					}
					catch(Exception ex){ ex.printStackTrace();}
					
					cell = row.getCell(11);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.CENTER);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(need*(vb.get(j)[0]+vb.get(j)[1]) + "");
					ingridientsSum.set(i, Float.sum(ingridientsSum.get(i),  need*(vb.get(j)[0]+vb.get(j)[1])));
	
					row.setHeight(rowHeight);
				}
			}
			
			try{
				for(; i<=4*n; i++){
					row = table.getRow(i-3*n+1);

					cell = row.getCell(9);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText(Integer.toString(i+1) + ".");
		
					cell = row.getCell(10);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.LEFT);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText("---");
					
					cell = row.getCell(11);
					cell.removeParagraph(0);
					paragraphCell = cell.addParagraph();
					paragraphCell.setAlignment(ParagraphAlignment.CENTER);
					paragraphCell.setSpacingAfter(0);
					runCell = paragraphCell.createRun();
					runCell.setFontSize(tableFontSize);
					runCell.setFontFamily(fontName);
					runCell.setText("---");
	
					row.setHeight(rowHeight);
					System.out.println("OK");
				}
			}
			catch(Exception ex){
					System.out.println("oops");
			}
			
		}
		
		
		XWPFParagraph prod = document.createParagraph();
		XWPFRun rdp = prod.createRun();
		rdp.setText("Загальна витрата");
		rdp.setFontSize(8);
		rdp.setBold(true);
		rdp.setFontFamily("Calibri");
			
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
		runCell.setFontSize(11);
		runCell.setFontFamily("Times New Roman");
		runCell.setBold(true);
		runCell.setText("№");
	
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(6200+2000));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(11);
		runCell.setFontFamily("Times New Roman");
		runCell.setBold(true);
		runCell.setText("Інгридієнт");
	
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(900));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(11);
		runCell.setFontFamily("Times New Roman");
		runCell.setBold(true);
		runCell.setText("Витрати, кг");

		
		for (int i=0; i< ingridients.size(); i++) {
			
				row = table.createRow();
				cell = row.getCell(0);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(11);
				runCell.setBold(true);
				runCell.setFontFamily("Times New Roman");
				runCell.setText(Integer.toString(i+1) + ".");
	
				cell = row.getCell(1);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.LEFT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(11);
				runCell.setFontFamily("Times New Roman");
				runCell.setText(ingridients.get(i).getName());
				
				cell = row.getCell(2);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.CENTER);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(11);
				runCell.setFontFamily("Times New Roman");
				runCell.setText(ingridientsSum.get(i) + "");
		}
		
		//float sum = Float.parseFloat(textField.getText());
		/*XWPFParagraph paragraphSUM = document.createParagraph();
		paragraphSUM.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runSUM = paragraphSUM.createRun();
		runSUM.addBreak();
		runSUM.setText("Сума: " + (int)sum + " грн. " + (int)((sum - (int)sum)*100) + " коп.");
		runSUM.setFontSize(11);
		runSUM.setFontFamily("Times New Roman");
		runSUM.setBold(true);
		*/
		String filename = "";
		
		
		filename = "Звіт по сировині " + Main.dfDateAndTime.format(new Date()).replace(":", "-");
		
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
		////////////////////////
		
		
	}
}
