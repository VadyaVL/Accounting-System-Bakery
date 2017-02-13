package ua.rokytne.bakery.forms.newedit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.View;

import ua.rokytne.bakery.LoggedUser;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.OldNewReport;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.ProductionHasIngridient;
import ua.rokytne.bakery.orm.ReportTeam;

public class NewTheBalanceOfThePreviousChangesForm extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	Object[] columnNames = {"Продукція", "Наявно"};
	Object[][] data = {};
	DefaultTableModel model = new DefaultTableModel(data, columnNames);
	
	public OldNewReport lastInsert;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			NewTheBalanceOfThePreviousChangesForm dialog = new NewTheBalanceOfThePreviousChangesForm(false, null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public NewTheBalanceOfThePreviousChangesForm(boolean viewMode, OldNewReport repTe) {
		model = new DefaultTableModel(data, columnNames) {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		    	if(column==1)
		    		return true;
		       return false;
		    }
		};
		this.setAlwaysOnTop(Main.onTop);
		if(!viewMode)
			setTitle("Введення залишку");
		else{
			setTitle("Перегляд попереднього/нового залишку " + repTe.getDateTime());
		}
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setBounds((Main.WIDTH-469)/2, (Main.HEIGTH-407)/2, 469, 407);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		table = new JTable(model);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setBounds(10, 11, 434, 316);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(10, 11, 434, 316);
		getContentPane().add(jsp);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.7));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.3));
		JButton btnCancel;
		if(!viewMode)
			btnCancel = new JButton("Відміна");
		else
			btnCancel = new JButton("Вихід");
		btnCancel.setBounds(355, 338, 89, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		JButton btnSave = new JButton("Зберегти");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int q = Main.okcancel("Дійсно зберегти наявну кількість?");
			    
				if(q!=0)
					return;
				
				lastInsert = null;

				///////////////////////
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					// Вставити Власне звіт
					String query = "insert into oldnewreport(DateTime, old, User_id)" + " values (?, ?, ?)";
					PreparedStatement preparedStmt = conn.prepareStatement(query);
					preparedStmt.setInt (3, LoggedUser.ID);
					preparedStmt.setBoolean(2, false);
					preparedStmt.setString(1, Main.dfDateAndTime.format(new Date()));
					preparedStmt.execute();
					
					
					query = "SELECT * FROM oldnewreport WHERE DateTime = (SELECT MAX(DateTime) FROM oldnewreport)";
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);

					if (rs.next())
					{
						int id = rs.getInt("id");
						int uID = rs.getInt("User_id");
						String datetime = rs.getString("DateTime");
						boolean old = rs.getBoolean("old");
						lastInsert = new OldNewReport(id, datetime, old, uID);
						//model.addRow(new Object[]{onr.getDateTime(), onr});
					}
					
					// Вставка значень
					for(int i=0; i<table.getRowCount(); i++){
						int pId = ((Production)table.getValueAt(i, 0)).getID();
						float count = 0;
						if(table.getValueAt(i, 1)!=null && Main.isNumeric(table.getValueAt(i, 1).toString()))
							count = Float.parseFloat(table.getValueAt(i, 1).toString());
						
						if(count==0)
							continue;
						
						String queryI = "insert into production_has_oldnewreport(Production_id, OldNewReport_id, Count)" + " values (?, ?, ?)";
						PreparedStatement preparedStmti = conn.prepareStatement(queryI);
						preparedStmti.setInt(1, pId);
						preparedStmti.setInt(2, lastInsert.getId());
						preparedStmti.setInt(3, (int)count);
						
						preparedStmti.execute();
						Main.mainFrame.productionTable.setValueAt(0, i, 1);
					}
					
					
					for(int j=0; j<table.getRowCount(); j++){
						Production p = ((Production)table.getValueAt(j, 0));
						float count = 0;
						if(table.getValueAt(j, 1)!=null &&  Main.isNumeric(table.getValueAt(j, 1).toString()))
							count = Float.parseFloat(table.getValueAt(j, 1).toString());
						
						Main.mainFrame.productionTable.setValueAt(count, j, 1);
						String queryU = "UPDATE production set CountOnStorage =  ? WHERE id = ?";
						PreparedStatement preparedStmtU = conn.prepareStatement(queryU);
						preparedStmtU.setInt(1, (int)count);
						preparedStmtU.setInt(2, p.getID());
						preparedStmtU.execute();
							
						
					}
					
					
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
				///////////////////////
			
				
				String mess = "Ведення нової наявності";
				
				Main.insertLog(mess);
				
				setVisible(false);
				dispose();
				
			}
		});
		
		if(viewMode)
			btnSave.setVisible(false);
		
		btnSave.setBounds(10, 338, 89, 23);
		contentPane.add(btnSave);
	
		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            int width=c.getWidth(), heigth=c.getHeight();
	            
	    		jsp.setBounds(11, 10, width-10-20-3, heigth-40-10-10-23);
	    		btnCancel.setBounds(width-15-89-5-3, heigth-40-2-24, 89, 23);
	    		btnSave.setBounds(11, heigth-40-2-24, 89, 23);
	    		
	        }
		});
		
		initData(viewMode, repTe);
	}
	
	private void initData(boolean viewMode, OldNewReport repTe){
	
		
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
			    Production prod = new Production(id, name, countOnStorage);
			    
			    if(!viewMode)
			    	model.addRow(new Object[]{prod, null});
			    else if(viewMode && repTe!=null){
			    	int produced=0;
			    	String querryForProduced = "SELECT * FROM production_has_oldnewreport where OldNewReport_id = " + repTe.getId() + " and production_id = " + id;
			    	
			    	Statement stGET = conn.createStatement();
			    	ResultSet rsGET = stGET.executeQuery(querryForProduced);
			    	
					if(rsGET.next())
						produced = rsGET.getInt("count");

					if(produced!=0)
				    	model.addRow(new Object[]{prod, produced});
					else if(produced==0)
				    	model.addRow(new Object[]{prod, null});
			    	
			    	stGET.close();
			    	
			    }
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
