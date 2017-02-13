package ua.rokytne.bakery.forms;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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

import com.toedter.calendar.JDateChooser;

import ua.rokytne.bakery.LoggedUser;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.forms.newedit.NewRequestForm;
import ua.rokytne.bakery.orm.Client;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.Request;

public class PrintAllByDayForm extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PrintAllByDayForm dialog = new PrintAllByDayForm();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PrintAllByDayForm() {
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("\u0421\u043F\u0456\u043B\u044C\u043D\u0438\u0439 \u0434\u0440\u0443\u043A");
		setResizable(false);
		setBounds(100, 100, 305, 100);
		getContentPane().setLayout(null);
		
		JLabel label = new JLabel("\u0414\u0430\u0442\u0430:");
		label.setBounds(10, 17, 41, 14);
		getContentPane().add(label);
		
		JDateChooser dateChooser = new JDateChooser();
		dateChooser.setBounds(56, 11, 235, 20);
		getContentPane().add(dateChooser);
		
		JButton button = new JButton("\u0414\u0440\u0443\u043A");
		button.setBounds(10, 42, 89, 23);
		getContentPane().add(button);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
								
				if(dateChooser.getDate() == null){
					JOptionPane.showMessageDialog(null, "Вкажіть будь ласка дату!");
					return;
				}
				
				Main.insertLog("Друк всіх накладних за " + Main.dfDate.format(dateChooser.getDate()));
				
				// Взяти всі накладні
				// Посортувати по місту
				// Друк
				
				ArrayList<Request> res = new ArrayList<>();
				JTree tree = Main.mainFrame.getTreeProd();

				String[] dates = Main.dfDate.format(dateChooser.getDate()).split("-");
				int y, m, d;

				y = Integer.parseInt(dates[0]);
				m = Integer.parseInt(dates[1]);
				d = Integer.parseInt(dates[2]);
				
				DefaultMutableTreeNode model = (DefaultMutableTreeNode) tree.getModel().getRoot();
				DefaultMutableTreeNode years = null;
				
				for(int i=0; i<model.getChildCount(); i++){
					DefaultMutableTreeNode tmp = (DefaultMutableTreeNode)model.getChildAt(i);
					if(tmp.toString().equals(Integer.toString(y))){
						years = tmp;
						break;
					}
				}
				
				DefaultMutableTreeNode mounths = null;
				
				for(int i=0; i<years.getChildCount(); i++){
					DefaultMutableTreeNode tmp = (DefaultMutableTreeNode)years.getChildAt(i);
					if(tmp.toString().equals(Main.getMounth(m))){
						mounths = tmp;
						break;
					}
				}
				
				DefaultMutableTreeNode days = null;
				
				for(int i=0; i<mounths.getChildCount(); i++){
					DefaultMutableTreeNode tmp = (DefaultMutableTreeNode)mounths.getChildAt(i);
					if(tmp.toString().equals(Integer.toString(d))){
						days = tmp;
						break;
					}
				}
				
				for(int i=0; i<days.getChildCount(); i++){
					DefaultMutableTreeNode tmp = (DefaultMutableTreeNode)days.getChildAt(i);
					if(tmp.getUserObject()!=null){
						res.add((Request)tmp.getUserObject());
						System.out.println((Request)tmp.getUserObject());
					}
					else{
						System.out.println("nooo");
					}
				}
				
				Collections.sort(res, new Comparator<Request>() {
			        public int compare(Request o1, Request o2) {
		                return o1.getPlace().compareTo(o2.getPlace());
			        }
				});
				
				for(Request r : res){
					printReq(r);
				}
				
			}
		});
		
		JButton button_1 = new JButton("\u0412\u0456\u0434\u043C\u0456\u043D\u0430");
		button_1.setBounds(202, 42, 89, 23);
		button_1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		getContentPane().add(button_1);
	}
	
	

	public void printReq(Request request) {
		
		XWPFDocument document;
				
		PrintForm frame = new PrintForm(request.getOK());
		frame.setModal(true);
		
		frame.chckbxNewCheckBox.setSelected(true);
		frame.OK = frame.chckbxNewCheckBox.isSelected();
		frame.PRINT= true;
		frame.dispose();
		//frame.setVisible(true);
		
		if(!frame.PRINT)
			return;
		
		if(!request.getOK())
		{
			request.setOK(frame.OK);
			request.setPrintableName("В_" + request.toString());
		
		}
		
		if(frame.OK){
			try{
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = null;
				conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
				
				String query = "UPDATE request SET ok = ? where id = ?";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setBoolean(1, true);
				preparedStmt.setInt(2, request.getId());
				preparedStmt.executeUpdate();
									
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		NewRequestForm frameGetData = new NewRequestForm(1, request);
		
		document = new XWPFDocument();
		
		CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
	    CTPageMar pageMar = sectPr.addNewPgMar();
	    pageMar.setLeft(BigInteger.valueOf(400L));
	    pageMar.setTop(BigInteger.valueOf(400L));
	    pageMar.setRight(BigInteger.valueOf(400L));
	    pageMar.setBottom(BigInteger.valueOf(400L));
		int q=2;
		int tableFontSize=8;
		int rowHeight=115;
		String fontName = "Calibri";
		
		XWPFParagraph paragraphTitle = document.createParagraph();
		paragraphTitle.setSpacingAfter(0);
		paragraphTitle.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun runTitle = paragraphTitle.createRun();
		runTitle.setText("Накладна №" + request.getId());
		runTitle.setFontSize(14);
		runTitle.setFontFamily("Times New Roman");
		runTitle.setBold(true);
		
		XWPFParagraph paragraphDate = document.createParagraph();
		paragraphDate.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runDate = paragraphDate.createRun();
		runDate.setText("Дата: " + request.getDateRequest().split(" ")[0]);
		runDate.setFontSize(11);
		runDate.setFontFamily("Times New Roman");
		runDate.setItalic(true);
		
		XWPFTable table = document.createTable();
		table.setCellMargins(50, 50, 50, 50);
		
		XWPFTableRow row = table.getRow(0);	// Взяття першого існуючого стовпця
		row.setHeight(rowHeight);
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf((4600+1100)/q));
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(800/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Од.в.");
		
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2550/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Ціна,");
		runCell.setText("грн");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1700/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Сума, ");
		runCell.setText("грн ");
						
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf((4600+1100)/q));
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(800/q));
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2550/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Ціна, ");
		runCell.setText("грн");
		
		cell = row.createCell();
		cell.removeParagraph(0);
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1700/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Сума, ");
		runCell.setText("грн");
		
		
		
		
		int j=0;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			Statement st = conn.createStatement();
			String updProduction = "UPDATE production set CountOnStorage = CountOnStorage - ? WHERE id = ?";
			
			int n=0;
			
			for (int i=0; i<frameGetData.table.getRowCount(); i++) {
				
				if(frameGetData.table.getValueAt(i, 3).toString()=="")
					continue;
				n++;
			}
			
			int pp = 0;
			
			if(n%2==0)
				pp=1;
			else
				pp=2;
			
			for (int i=0; i<frameGetData.table.getRowCount(); i++) {

				// Відняти кількість кожної продукції зі списку замовлення
				//	((Production)frameGetData.table.getValueAt(i, 0))
				//	frameGetData.table.getValueAt(i, 3).toString()
				
				if(frame.OK && frame.FIRSTPRINT){
					PreparedStatement preparedStmt = conn.prepareStatement(updProduction);
					if(frameGetData.table.getValueAt(i, 3).toString()!=null){
						if(Main.isNumeric(frameGetData.table.getValueAt(i, 3).toString()))
							preparedStmt.setInt(1, Integer.parseInt((frameGetData.table.getValueAt(i, 3).toString())));
						else
							preparedStmt.setInt(1, 0);
					}
					else
						preparedStmt.setInt(1, 0);
					preparedStmt.setInt (2, ((Production)frameGetData.table.getValueAt(i, 0)).getID());
					preparedStmt.execute();
				}
				
				////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////
				
				if(frameGetData.table.getValueAt(i, 3).toString()=="")
					continue;
				
				
				
				if(Integer.parseInt(frameGetData.table.getValueAt(i, 3).toString())!=0){
					j++;
					
					if(j<n/2+pp){
						row = table.createRow();
						row.setHeight(rowHeight);
						cell = row.getCell(0);
						cell.removeParagraph(0);
						paragraphCell = cell.addParagraph();
						paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
						paragraphCell.setSpacingAfter(0);
						runCell = paragraphCell.createRun();
						runCell.setFontSize(tableFontSize);
						runCell.setBold(true);
						runCell.setFontFamily(fontName);
						runCell.setText(Integer.toString(j) + ".");
			
						cell = row.getCell(1);
						cell.removeParagraph(0);
						paragraphCell = cell.addParagraph();
						paragraphCell.setAlignment(ParagraphAlignment.LEFT);
						paragraphCell.setSpacingAfter(0);
						runCell = paragraphCell.createRun();
						runCell.setFontSize(tableFontSize);
						runCell.setFontFamily(fontName);
						runCell.setText(((Production)frameGetData.table.getValueAt(i, 0)).getName());
						
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
						runCell.setText(frameGetData.table.getValueAt(i, 3).toString());
						
						cell = row.getCell(4);
						cell.removeParagraph(0);
						paragraphCell = cell.addParagraph();
						paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
						paragraphCell.setSpacingAfter(0);
						runCell = paragraphCell.createRun();
						runCell.setFontSize(tableFontSize);
						runCell.setFontFamily(fontName);
						runCell.setText(frameGetData.table.getValueAt(i, 2).toString());
						
						cell = row.getCell(5);
						cell.removeParagraph(0);
						paragraphCell = cell.addParagraph();
						paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
						paragraphCell.setSpacingAfter(0);
						runCell = paragraphCell.createRun();
						runCell.setFontSize(tableFontSize);
						runCell.setFontFamily(fontName);
						runCell.setText(frameGetData.table.getValueAt(i, 4).toString());
					}
					else{
						row = table.getRow(j-n/2-(pp-1));
						row.setHeight(rowHeight);
														 
						cell = row.getCell(6);
						cell.removeParagraph(0);
						paragraphCell = cell.addParagraph();
						paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
						paragraphCell.setSpacingAfter(0);
						runCell = paragraphCell.createRun();
						runCell.setFontSize(tableFontSize);
						runCell.setFontFamily(fontName);
						runCell.setBold(true);
						runCell.setText(Integer.toString(j) + ".");
			
						cell = row.getCell(7);
						cell.removeParagraph(0);
						paragraphCell = cell.addParagraph();
						paragraphCell.setAlignment(ParagraphAlignment.LEFT);
						paragraphCell.setSpacingAfter(0);
						runCell = paragraphCell.createRun();
						runCell.setFontSize(tableFontSize);
						runCell.setFontFamily(fontName);
						runCell.setText(((Production)frameGetData.table.getValueAt(i, 0)).getName());
						
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
						runCell.setText(frameGetData.table.getValueAt(i, 3).toString());
						
						cell = row.getCell(10);
						cell.removeParagraph(0);
						paragraphCell = cell.addParagraph();
						paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
						paragraphCell.setSpacingAfter(0);
						runCell = paragraphCell.createRun();
						runCell.setFontSize(tableFontSize);
						runCell.setFontFamily(fontName);
						runCell.setText(frameGetData.table.getValueAt(i, 2).toString());
						
						cell = row.getCell(11);
						cell.removeParagraph(0);
						paragraphCell = cell.addParagraph();
						paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
						paragraphCell.setSpacingAfter(0);
						runCell = paragraphCell.createRun();
						runCell.setFontSize(tableFontSize);
						runCell.setFontFamily(fontName);
						runCell.setText(frameGetData.table.getValueAt(i, 4).toString());
					}
				}
			}
			st.close();
			conn.close();
		}
		catch(Exception ex){ex.printStackTrace();}
		float sum = Float.parseFloat(frameGetData.tfSum.getText().replace(",", "."));
		XWPFParagraph paragraphSUM = document.createParagraph();
		paragraphSUM.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runSUM = paragraphSUM.createRun();
		runSUM.addBreak();
		runSUM.setText("СУМА ДО СПЛАТИ: " + new DecimalFormat("##.##").format(sum) + " грн.");
		runSUM.setFontSize(11);
		runSUM.setFontFamily("Times New Roman");
		runSUM.setBold(true);
		
		XWPFParagraph paragraphManagerClient = document.createParagraph();
		paragraphManagerClient.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runManagerClient = paragraphManagerClient.createRun();
		//runManagerClient.addBreak();
		runManagerClient.setText("Відпустив: " + LoggedUser.name + "\t\tОтримав: " + ((Client)frameGetData.cbClient.getSelectedItem()).getName());
		runManagerClient.setFontSize(11);
		runManagerClient.setFontFamily("Times New Roman");
		runManagerClient.setItalic(true);
		
		String filename = "docs/Накладна " + request.toString()+".docx";
		filename = filename.replace('\"', '_').replace('\'', '_').replace('>', '_').replace('<', '_');
		
		try {
			FileOutputStream output = new FileOutputStream(filename);
			document.write(output);
			output.close();
			
			 //The desktop api can help calling other applications in our machine
		    //and also many other features...
		    Desktop desktop = Desktop.getDesktop();
		    try {
		    //desktop.print(new File("DocXfile.docx"));
	    		desktop.print(new File(filename));
		    	
		    } catch (IOException exs) {           
		    	exs.printStackTrace();
		    }
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		////////////////////////////////////////////
		////////////////////////////////////////////
	}
	
}
