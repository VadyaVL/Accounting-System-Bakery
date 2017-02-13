package ua.rokytne.bakery.forms.newedit;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import ua.rokytne.bakery.LoggedUser;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.ProductionHasIngridient;
import ua.rokytne.bakery.orm.ReportTeam;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JDialog;

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
import java.awt.event.ActionEvent;

public class NewReportTeamForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	Object[] columnNames = {"Продукція", "Виготовлено", "Брак"};
	Object[][] data = {};
	DefaultTableModel model = new DefaultTableModel(data, columnNames);
	
	public ReportTeam lastInsert;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewReportTeamForm frame = new NewReportTeamForm(false, null);
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
	public NewReportTeamForm(boolean viewMode, ReportTeam repTe) {
		model = new DefaultTableModel(data, columnNames) {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		    	if(column==1 || column==2)
		    		return true;
		       return false;
		    }
		};
		this.setAlwaysOnTop(Main.onTop);
		if(!viewMode)
			setTitle("Виробничий звіт зміни");
		else{
			setTitle("Виробничий звіт зміни за " + repTe.getDateTime());
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
		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.6));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.2));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.2));
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
				int i = Main.okcancel("Дійсно зберегти поточний звіт?");
			    
				if(i!=0)
					return;
				
				lastInsert = null;
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					if(!viewMode){
						String query = " insert into theproductionreportteam(user_id, datetime)" + " values (?, ?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt (1, LoggedUser.ID);
						preparedStmt.setString(2, Main.dfDateAndTime.format(new Date()));
						preparedStmt.execute();
						
						
						query = "SELECT * FROM theproductionreportteam WHERE DateTime = (SELECT MAX(DateTime) FROM theproductionreportteam)";
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
					
						while (rs.next())
						{
							int id = rs.getInt("id");
							int uID = rs.getInt("User_id");
							String datetime = rs.getString("DateTime");
							lastInsert = new ReportTeam(id, uID, datetime);
							
						}

						query = " insert into produced(PRT_id, Production_id, Count)" + " values (?, ?, ?)";
						String bquery = " insert into brak(PRT_id, Production_id, Count)" + " values (?, ?, ?)";
						String updProduction = "UPDATE production set CountOnStorage = CountOnStorage + ? WHERE id = ?";
						String updIngridient = "UPDATE ingridient SET CountOnStorage = CountOnStorage - ? * ? WHERE id = ?";
						
						// WORK NOW
						//////////////////////////////////////////////////////////////////////////////////////////
						//////////////////////////////////////////////////////////////////////////////////////////

						String queryP = "insert into produced(PRT_id, Production_id, Count)" + " values ";
						String queryB = "insert into brak(PRT_id, Production_id, Count)" + " values ";
						String prt_id, p_id, c;
						
						for(int j=0; j<table.getRowCount(); j++){
							// Виготовлено
							//preparedStmt = conn.prepareStatement(query);
							
							prt_id = lastInsert.getID() + "";
							p_id = ((Production)table.getValueAt(j, 0)).getID() + "";
							
							//preparedStmt.setInt (1, lastInsert.getID());
							//preparedStmt.setInt (2, ((Production)table.getValueAt(j, 0)).getID());

							if(table.getValueAt(j, 1)!=null)
								table.setValueAt(table.getValueAt(j, 1).toString().replace(',', '.'), j, 1);
							if(table.getValueAt(j, 2)!=null)
								table.setValueAt(table.getValueAt(j, 2).toString().replace(',', '.'), j, 2);
							
							if(table.getValueAt(j, 1)!=null){
								if(Main.isNumeric(table.getValueAt(j, 1).toString())){
									c = table.getValueAt(j, 1).toString();
									//preparedStmt.setInt(3, Integer.parseInt((table.getValueAt(j, 1).toString())));
								}
								else{
									c = "0";
									//preparedStmt.setInt(3, 0);
								}
							}
							else{
								c = "0";
								//preparedStmt.setInt(3, 0);
							}
							queryP += "(" + prt_id + ", " + p_id + ", " + c + "), ";
							//preparedStmt.execute();
							
							// Списання
							//preparedStmt = conn.prepareStatement(bquery);
							//preparedStmt.setInt (1, lastInsert.getID());
							//preparedStmt.setInt (2, ((Production)table.getValueAt(j, 0)).getID());
							
							if(table.getValueAt(j, 2)!=null){
								if(Main.isNumeric(table.getValueAt(j, 2).toString())){
									c = table.getValueAt(j, 2).toString();
									//preparedStmt.setInt(3, Integer.parseInt((table.getValueAt(j, 2).toString())));
								}
								else{
									c = "0";
									//preparedStmt.setInt(3, 0);
								}
							}
							else{
								c = "0";
								//preparedStmt.setInt(3, 0);
							}
							queryB += "(" + prt_id + ", " + p_id + ", " + c + "), ";
							//preparedStmt.execute();
							
							// Додавання продукції до наявного
							preparedStmt = conn.prepareStatement(updProduction);
							
							if(table.getValueAt(j, 1)!=null){
								if(Main.isNumeric(table.getValueAt(j, 1).toString()))
									preparedStmt.setInt(1, Integer.parseInt((table.getValueAt(j, 1).toString())));
								else
									preparedStmt.setInt(1, 0);
							}
							else
								preparedStmt.setInt(1, 0);
							preparedStmt.setInt (2, ((Production)table.getValueAt(j, 0)).getID());
							preparedStmt.execute();
							
							// Необхідно оновлювати сировину
							// Потрібна сума виготовлено + брак
							int sumProdandBrak = 0;
							
							if(table.getValueAt(j, 1)!=null && Main.isNumeric(table.getValueAt(j, 1).toString()))
									sumProdandBrak += Integer.parseInt((table.getValueAt(j, 1).toString()));
							if(table.getValueAt(j, 2)!=null && Main.isNumeric(table.getValueAt(j, 2).toString()))
								sumProdandBrak += Integer.parseInt((table.getValueAt(j, 2).toString()));
							// Необхідно отримати рецептуру кожного
							// І оновити сировину
							ArrayList<ProductionHasIngridient> phis = new ArrayList<ProductionHasIngridient>();
							
							String queryPHIS = "SELECT * FROM production_has_ingridient WHERE Production_id = " + ((Production)table.getValueAt(j, 0)).getID();
							Statement stPHIS = conn.createStatement();
							ResultSet rsPHIS = stPHIS.executeQuery(queryPHIS);
							  
							while (rsPHIS.next())
							{
								int idP = rsPHIS.getInt("Production_id");
								int idI = rsPHIS.getInt("Ingridient_id");
								float count = rsPHIS.getFloat("Count");
								
								phis.add(new ProductionHasIngridient(idP, idI, count));
							}
							rsPHIS.close();
							stPHIS.close();
							
							
							for (ProductionHasIngridient ph : phis) {
								if(sumProdandBrak!=0){
									preparedStmt = conn.prepareStatement(updIngridient);
									preparedStmt.setInt(1, sumProdandBrak);
									preparedStmt.setFloat(2, ph.getCount());
									preparedStmt.setInt(3, ph.getIngridientID());
									preparedStmt.execute();
								}
							}
							
						}

						queryB = queryB.substring(0, queryB.length()-2);
						queryP = queryP.substring(0, queryP.length()-2);

						System.out.println(queryP);
						System.out.println(queryB);
						

						preparedStmt = conn.prepareStatement(queryP);
						preparedStmt.execute();
						
						preparedStmt = conn.prepareStatement(queryB);
						preparedStmt.execute();
						
						//////////////////////////////////////////////////////////////////////////////////////////
						//////////////////////////////////////////////////////////////////////////////////////////
						// END WORK NOW
						
						
						st.close();
						
					}
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			
				
				String mess = "Створення виробничого звіту";
				
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
	
	private void initData(boolean viewMode, ReportTeam repTe){
	
		
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
			    	model.addRow(new Object[]{prod, null, null});
			    else if(viewMode && repTe!=null){
			    	int brak=0, produced=0;
			    	String querryForBrak = "SELECT * FROM Brak where PRT_id = " + repTe.getID() + " and production_id = " + id;
			    	String querryForProduced = "SELECT * FROM Produced where PRT_id = " + repTe.getID() + " and production_id = " + id;
			    	
			    	Statement stGET = conn.createStatement();
			    	ResultSet rsGET = stGET.executeQuery(querryForBrak);
					
					if(rsGET.next())
						brak = rsGET.getInt("count");
					
					rsGET = stGET.executeQuery(querryForProduced);
					if(rsGET.next())
						produced = rsGET.getInt("count");

					if(brak!=0 && produced!=0)
				    	model.addRow(new Object[]{prod, produced, brak});
					else if(brak==0 && produced!=0)
				    	model.addRow(new Object[]{prod, produced, null});
					else if(brak!=0 && produced==0)
				    	model.addRow(new Object[]{prod, null, brak});
					else if(brak==0 && produced==0)
				    	model.addRow(new Object[]{prod, null, null});
			    	
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
