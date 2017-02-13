package ua.rokytne.bakery.forms;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
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
import ua.rokytne.bakery.orm.Place;
import ua.rokytne.bakery.orm.Production;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JButton;

/**
 * Форма для заповнення цін на продукцію в залежності від міста. 
 */
public class PriceOfPlaceForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;

	private JComboBox<Place> cbPlace;
	private JButton btnCancel, btnSave;
	private JButton button;
	
	Object[] columnNames = {
            "Назва",
            "Ціна (грн)"};
	
	Object[][] data = {};
	
	DefaultTableModel model = new DefaultTableModel(data, columnNames);
	private XWPFDocument document;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PriceOfPlaceForm frame = new PriceOfPlaceForm(false);
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
	public PriceOfPlaceForm(boolean print) {
		model = new DefaultTableModel(data, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       if(column==1)
		    	   return true;
		       return false;
		    }
		};
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Ціни на продукцію по населеним пунктам");
		setBounds((Main.WIDTH-451)/2, (Main.HEIGTH-347)/2, 451, 347);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("Населений пункт:");
		label.setBounds(10, 11, 99, 14);
		contentPane.add(label);
		
		cbPlace = new JComboBox<Place>();
		cbPlace.setBounds(104, 8, 320, 20);
		contentPane.add(cbPlace);
		cbPlace.addItemListener(new ItemListener() {
			
			private boolean one = true;
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(one){

					// Підвантажувати нові ціни
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
						Statement stPHI = conn.createStatement();
						
						for(int i=0; i<model.getRowCount(); i++){
					    	String queryPHI = "SELECT * FROM product_price WHERE production_id = " + 
					    	((Production)model.getValueAt(i, 0)).getID() +
					    	" and place_id = " + ((Place)cbPlace.getSelectedItem()).getID();
							
							ResultSet rsPHI = stPHI.executeQuery(queryPHI);
							
							if(rsPHI.next()){ // Якщо є
								model.setValueAt(rsPHI.getFloat("price"), i, 1);
							}
							else{ // Якщо нема
								String query = " insert into product_price(production_id, place_id, price)" + " values (?, ?, ?)";
								PreparedStatement preparedStmt = conn.prepareStatement(query);
								preparedStmt.setInt (1, ((Production)model.getValueAt(i, 0)).getID());
								preparedStmt.setInt (2, ((Place)cbPlace.getSelectedItem()).getID());
								preparedStmt.setFloat(3, 0.0f);
								preparedStmt.execute();
								model.setValueAt(0.0f, i, 1);
							}
						}
						
						stPHI.close();
						conn.close();
					}
					catch(Exception ex){ }
					
					one = !one;
				}else{
					one = !one;
				}
				
			}
		});
		
		table = new JTable(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		        return column == 1 ? true : false;
		    }
		};
		table.setBounds(10, 36, 414, 237); 
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(10, 36, 414, 231);
		getContentPane().add(jsp);
		
		table.setModel(model);

		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.8));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.2));

		
		btnCancel = new JButton("Відміна");
		btnCancel.setBounds(335, 278, 89, 23);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnCancel);
		
		if(!print){
			btnSave = new JButton("Зберегти");
			btnSave.setBounds(10, 278, 89, 23);
			btnSave.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String mess = "Збереження інформації про ціни " + ((Place)cbPlace.getSelectedItem()).getName();
										
					Main.insertLog(mess);
					
					
					Place place = ((Place)cbPlace.getSelectedItem());
					
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
						Statement stPHI = conn.createStatement();
						
						for(int i=0; i<model.getRowCount(); i++){

							model.setValueAt(model.getValueAt(i, 1).toString().replace(',', '.'), i, 1);
							if(!Main.isNumeric(model.getValueAt(i, 1).toString())){
								JOptionPane.showMessageDialog(null, "Вкажіть ціну для " + ((Production)model.getValueAt(i, 0)).getName() );
								break;
							}
							
					    	String queryPHI = "UPDATE product_price SET price = " + Float.parseFloat(model.getValueAt(i, 1).toString()) + " WHERE production_id = " + 
					    	((Production)model.getValueAt(i, 0)).getID() +
					    	" and place_id = " + ((Place)cbPlace.getSelectedItem()).getID();
					    	PreparedStatement preparedStmt = conn.prepareStatement(queryPHI);
					    	preparedStmt.executeUpdate();
						}
						
						stPHI.close();
						conn.close();
					}
					catch(Exception ex){ ex.printStackTrace();}
					
				}
			});
			contentPane.add(btnSave);
		}
		else{

			btnSave = new JButton("");
			btnSave.setVisible(false);
			
			button = new JButton("\u0414\u0440\u0443\u043A \u043F\u0440\u0430\u0439\u0441\u0443");
			button.setBounds(162, 278, 111, 23);
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
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
					
					Main.insertLog("Друк прайсу (" + ((Place)cbPlace.getSelectedItem()).getName() + ")");
					makeDoc((Place)cbPlace.getSelectedItem(), productions);
				}
			});
			contentPane.add(button);
		}
		
		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            int width=c.getWidth(), heigth=c.getHeight();

	    		btnCancel.setBounds(width-89-25, heigth-23-5-40, 89, 23);
	    		if(btnSave!=null)
	    			btnSave.setBounds(10, heigth-23-5-40, 89, 23);
				cbPlace.setBounds(104, 8, width-104-25, 20);

				jsp.setBounds(10, 36, width-10-25, heigth-40-23-10-23-10);
				
	        }
		});
		
		
		initData();
	}

	
	private void makeDoc(Place place, ArrayList<Production> productions){

		
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
		paragraphTitle.setAlignment(ParagraphAlignment.CENTER);
		paragraphTitle.setSpacingAfter(0);
		XWPFRun runTitle = paragraphTitle.createRun();
		runTitle.setText("Ціни на продукцію " + place.getName());
		runTitle.setFontSize(14);
		runTitle.setFontFamily("Times New Roman");
		runTitle.setBold(true);
		
		XWPFParagraph paragraphDate = document.createParagraph();
		paragraphDate.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun runDate = paragraphDate.createRun();
		runDate.setText("Дата: " + Main.dfDate.format(new Date()));
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(9300/q));
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1100/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Ціна, грн");
		
		// Другий рядок
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(9300/q));
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
		cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1100/q));
		paragraphCell = cell.addParagraph();
		paragraphCell.setAlignment(ParagraphAlignment.CENTER);
		paragraphCell.setSpacingAfter(0);
		runCell = paragraphCell.createRun();
		runCell.setFontSize(tableFontSize);
		runCell.setFontFamily(fontName);
		runCell.setBold(true);
		runCell.setText("Ціна, грн");
		
		int i=0;
		int pp=0;
		
		if(productions.size()%2==0)
			pp=0;
		else
			pp=1;
		
		for (Production p : productions) {
			
			if(i<productions.size()/2+pp)
			{
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
				//runCell.setText(Integer.toString(i+1) + ".");
				runCell.setText(p.getID()+"");
	
				cell = row.getCell(1);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.LEFT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText(p.getName());
				
				cell = row.getCell(2);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.CENTER);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText("шт.");
	
				float price = 0.0f;
				
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
					else{ // Якщо нема
						String query = " insert into product_price(production_id, place_id, price)" + " values (?, ?, ?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt (1, p.getID());
						preparedStmt.setInt (2, ((Place)cbPlace.getSelectedItem()).getID());
						preparedStmt.setFloat(3, 0.0f);
						preparedStmt.execute();
					}
					
					stPHI.close();
					conn.close();
				}
				catch(Exception ex){}
				
				cell = row.getCell(3);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText(String.format("%.2f", price));
			}
			else{
				row = table.getRow(i-productions.size()/2-(pp-1));
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
				
				cell = row.getCell(6);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.CENTER);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText("шт.");
	
				float price = 0.0f;
				
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
					else{ // Якщо нема
						String query = " insert into product_price(production_id, place_id, price)" + " values (?, ?, ?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt (1, p.getID());
						preparedStmt.setInt (2, ((Place)cbPlace.getSelectedItem()).getID());
						preparedStmt.setFloat(3, 0.0f);
						preparedStmt.execute();
					}
					
					stPHI.close();
					conn.close();
				}
				catch(Exception ex){}
				
				cell = row.getCell(7);
				cell.removeParagraph(0);
				paragraphCell = cell.addParagraph();
				paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
				paragraphCell.setSpacingAfter(0);
				runCell = paragraphCell.createRun();
				runCell.setFontSize(tableFontSize);
				runCell.setFontFamily(fontName);
				runCell.setText(String.format("%.2f", price));
			}
			
			i++;
			
		}
		
		try {
			FileOutputStream output = new FileOutputStream("docs/Ціни на продукцію " + place.getName() + " " + Main.dfDate.format(new Date())+".docx");
			document.write(output);
			output.close();
		    Desktop desktop = Desktop.getDesktop();
		    try {
		        desktop.print(new File("docs/Ціни на продукцію " + place.getName() + " " + Main.dfDate.format(new Date())+".docx"));
		    } catch (IOException e) {           
		        e.printStackTrace();
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 
	 */
	public void initData(){
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);

			Statement st = conn.createStatement();
			String query = "SELECT * FROM place";
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				cbPlace.addItem(new Place(id, name));
		    }
			
			query = "SELECT * FROM production";
			rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				float countOnStorage = rs.getFloat("CountOnStorage");
				Production production = new Production(id, name, countOnStorage);
			    
			    
		    	String queryPHI = "SELECT * FROM product_price WHERE production_id = " + 
		    	id +" and place_id = " + ((Place)cbPlace.getSelectedItem()).getID();
				
				Statement stPHI = conn.createStatement();
				ResultSet rsPHI = stPHI.executeQuery(queryPHI);
				
				if(rsPHI.next()){ // Якщо є
				    model.addRow(new Object[]{production, rsPHI.getFloat("price")});
				}
				else{ // Якщо нема
					query = " insert into product_price(production_id, place_id, price)" + " values (?, ?, ?)";
					PreparedStatement preparedStmt = conn.prepareStatement(query);
					preparedStmt.setInt (1, id);
					preparedStmt.setInt (2, ((Place)cbPlace.getSelectedItem()).getID());
					preparedStmt.setFloat(3, 0.0f);
					preparedStmt.execute();
				    model.addRow(new Object[]{production, 0.0f});
				}
				
				stPHI.close();
		    }
			
			st.close();
			
			conn.close();
		}
		catch(Exception ex){
			
		}
	}
}
