package ua.rokytne.bakery.forms.newedit;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.JButton;

import javax.swing.JTable;
import com.toedter.calendar.JDateChooser;

import ua.rokytne.bakery.DoubleJTextField;
import ua.rokytne.bakery.LoggedUser;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.Client;
import ua.rokytne.bakery.orm.OldNewReport;
import ua.rokytne.bakery.orm.Place;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.Request;

public class NewRequestForm extends JDialog {

	private JPanel contentPane;
	public JTable table;
	public JTextField tfSum;
	public JComboBox cbClient, cbPlace;
	public JDateChooser dcDate;
	
	Object[] columnNames = {"Назва", "Од. вим.","Ціна (грн)", 	"Кількість", "Сума"};
	Object[][] data = {};
	DefaultTableModel model = new DefaultTableModel(data, columnNames);
	public Request lastInsert;
	public boolean cancel = false;
	private int viewClientID = -1;
	private JTextField tfNumber;
	private JButton btnGo;
	private boolean inGO = false;
	private MyKeyListener listener = new MyKeyListener();

	private ArrayList<Integer> oldValue = new ArrayList<Integer>();
	private static final String solve = "Solve";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewRequestForm frame = new NewRequestForm(0, null);
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
	public NewRequestForm(int mode, Request req) {
		model = new DefaultTableModel(data, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		    	if(column==3)
		    		return true;
		       return false;
		    }
		};

		this.setAlwaysOnTop(Main.onTop);
		setTitle("Формування запиту");

		if(mode==1)
			setTitle("Перегляд заявки №" + req.getId());
		if(mode==2)
			setTitle("Редагування заявки №" + req.getId());

		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setBounds((Main.WIDTH-558)/2, (Main.HEIGTH-356)/2, 558, 356);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("\u041A\u043B\u0456\u0454\u043D\u0442:");
		label.setBounds(294, 14, 46, 14);
		contentPane.add(label);
		
		cbClient = new JComboBox();
		cbClient.setBounds(343, 11, 188, 20);
		contentPane.add(cbClient);
		
		JLabel label_1 = new JLabel("\u041D\u0430\u0441\u0435\u043B\u0435\u043D\u0438\u0439 \u043F\u0443\u043D\u043A\u0442:");
		label_1.setBounds(11, 14, 105, 14);
		contentPane.add(label_1);
		
		cbPlace = new JComboBox();
		cbPlace.setBounds(126, 11, 158, 20);
		cbPlace.addItemListener(new ItemListener() {
			
			private boolean one = true;
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(one){

					// Підвантажувати нових клієнтів
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);

						Statement st = conn.createStatement();
						String query = "SELECT * FROM Client where place_id = " + ((Place)cbPlace.getSelectedItem()).getID();
						ResultSet rs = st.executeQuery(query);
						cbClient.removeAllItems();
						
						while(rs.next()){
							int id = rs.getInt("id");
							String name = rs.getString("name");
							int place_id = rs.getInt("place_id");
							
						    cbClient.addItem(new Client(id, name, place_id));
							
						    if(viewClientID==id)
								cbClient.setSelectedIndex(cbClient.getItemCount()-1);
						}
						rs.close();
						
						// Знаємо місто, пробігаємося по продуктам
						for(int i=0; i<table.getRowCount(); i++){
							boolean insert = true;
							Production prod = (Production)table.getValueAt(i, 0);
							String qP = "SELECT * FROM product_price WHERE production_id = " + prod.getID() + " and place_id = " + ((Place)cbPlace.getSelectedItem()).getID();
							ResultSet rsP = st.executeQuery(qP);
							
							if(rsP.next()){
								table.setValueAt(rsP.getFloat("price"), i, 2);
							} else {
								insert = false;
							}
							rsP.close();
							
							if(!insert){
								qP = "INSERT INTO product_price(production_id, place_id, price) VALUES (?, ?, ?)";
								PreparedStatement preparedStmt = conn.prepareStatement(qP);
								preparedStmt.setInt (1, prod.getID());
								preparedStmt.setInt (2, ((Place)cbPlace.getSelectedItem()).getID());
								preparedStmt.setFloat (3, 0.0f);
								preparedStmt.execute();
								table.setValueAt(0.0f, i, 2);
							}
							
						}
						
						st.close();
						conn.close();
					}
					catch(Exception ex){ }
					
					one = !one;
				}else{
					one = !one;
				}
				
			}
		});
		contentPane.add(cbPlace);
		
		JLabel label_2 = new JLabel("\u0414\u0430\u0442\u0430 \u0437\u0430\u043C\u043E\u0432\u043B\u0435\u043D\u043D\u044F:");
		label_2.setBounds(11, 39, 105, 14);
		contentPane.add(label_2);
		
		table = new JTable(model);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setBounds(11, 92, 328, 158);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(11, 64, 520, 186);
		contentPane.add(jsp);

		
		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.7));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.2));
		table.getColumnModel().getColumn(2).setPreferredWidth((int) (table.getWidth()*0.2));
		table.getColumnModel().getColumn(3).setPreferredWidth((int) (table.getWidth()*0.2));
		table.getColumnModel().getColumn(4).setPreferredWidth((int) (table.getWidth()*0.2));
		
		////////////////////
		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				
				float price = 0.0f;
				
				for(int i=0; i<table.getRowCount(); i++){
					if(table.getValueAt(i, 3).toString()!=""){
						if(Main.isNumeric(table.getValueAt(i, 3).toString())){
							float pO = Float.parseFloat(table.getValueAt(i, 2).toString())*Float.parseFloat(table.getValueAt(i, 3).toString());
							price += pO;
							
							((DoubleJTextField)table.getValueAt(i, 4)).setText(new DecimalFormat("##.##").format(pO));
						}
						else{
							((DoubleJTextField)table.getValueAt(i, 4)).setText(new DecimalFormat("##.##").format(0));
						}
					}
				}
				
				tfSum.setText(new DecimalFormat("##.##").format(price));
				
			}
		});
		
		JButton btnSave = new JButton("\u0417\u0431\u0435\u0440\u0435\u0433\u0442\u0438");
		
		if(mode==1)
			btnSave.setVisible(false);
		
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				lastInsert = null;
				if(dcDate.getDate()==null){
					JOptionPane.showMessageDialog(null, "Вкажіть дату!");
					return;
				}
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					if(mode!=2){
						
						String query = " INSERT INTO request(Client_id, Date_Request, Date_Oformleniya, OK, User_id)" + " values (?,?,?,?,?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt (1, ((Client)cbClient.getSelectedItem()).getID());
						preparedStmt.setString (2, Main.dfDateAndTime.format(dcDate.getDate()));
						preparedStmt.setString (3, Main.dfDateAndTime.format(new Date()));
						preparedStmt.setBoolean(4, false);
						preparedStmt.setInt(5, LoggedUser.ID);
						preparedStmt.execute();
						
						query = "SELECT * FROM request WHERE ID = (SELECT MAX(ID) FROM request)";
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
							lastInsert = new Request(id, client_id, date_Request, date_Oformleniya, ok, user_id);
						}
						rs.close();
						
						// Вставити кількості
						for(int i=0; i<table.getRowCount(); i++){
							if(table.getValueAt(i, 3).toString()!=""){
								
								table.setValueAt(table.getValueAt(i, 3).toString().replace(',', '.'), i, 3);

								query = "INSERT INTO request_has_production(Request_id, Production_id, Count) VALUES(?,?,?)";
								if(Main.isNumeric(table.getValueAt(i, 3).toString())){
									preparedStmt = conn.prepareStatement(query);
									preparedStmt.setInt (1, lastInsert.getId());
									preparedStmt.setInt (2, ((Production)table.getValueAt(i, 0)).getID());
									preparedStmt.setInt (3, Integer.parseInt(table.getValueAt(i, 3).toString()));
									preparedStmt.execute();
								}
							}
						}
						
						st.close();
					}
					else{
						
						// Потрібно дізнатися чи є по даній заявці виробничий звіт?
						Date date = Main.dfDateAndTime.parse(req.getDateRequest());
						//date = Main.addDays(date, 1);
						date.setHours(0);
						date.setMinutes(0);
						date.setSeconds(0);
						OldNewReport oldNewReport = null;
						String queryR = "SELECT * FROM oldnewreport WHERE DateTime >= \'" + Main.dfDateAndTime.format(date) + "\' and DateTime <= \'";
						date.setHours(23);
						date.setMinutes(59);
						date.setSeconds(59);
						queryR += Main.dfDateAndTime.format(date) + "\'";

						//System.out.println(queryR);
						Statement stR = conn.createStatement();
						ResultSet rsR = stR.executeQuery(queryR);
						
						while(rsR.next()){
							if(rsR.getBoolean("old")){
								oldNewReport = new OldNewReport(rsR.getInt("id"),rsR.getString("DateTime"), true, rsR.getInt("User_id"));
								break;
							}
						}
						
						rsR.close();
						stR.close();
						///////////////////////////////////////////////////
						
						
						// Оновити кількість
						if(req.getOK()){
							for(int i=0; i<table.getRowCount(); i++){
								int pID = ((Production)table.getValueAt(i, 0)).getID();
								int nValue=0;
								if(Main.isNumeric(table.getValueAt(i, 3).toString()))
									nValue = Integer.parseInt(table.getValueAt(i, 3).toString());
								int sub = nValue - oldValue.get(i);

								if(sub!=0){
									if(oldNewReport == null) {
										String queryU = "UPDATE production set CountOnStorage = CountOnStorage - ? WHERE id = ?";
										PreparedStatement preparedStmtU = conn.prepareStatement(queryU);
										preparedStmtU.setInt(1, sub);
										preparedStmtU.setInt(2, pID);
										preparedStmtU.execute();
									}
									else{
										String queryU = "UPDATE production_has_oldnewreport SET Count = Count - ? WHERE Production_id = ? and OldNewReport_id = ?";
										PreparedStatement preparedStmtU = conn.prepareStatement(queryU);
										preparedStmtU.setInt(1, sub);
										preparedStmtU.setInt(2, pID);
										preparedStmtU.setInt(3, oldNewReport.getId());
										preparedStmtU.execute();
									}
								}
								
							}
						}
						
						
						
						
						
						//////////////
						
						String query = "UPDATE Request SET Client_id = ?, Date_Request = ?, Date_Oformleniya = ?, OK = ?, User_id = ? where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt (1, ((Client)cbClient.getSelectedItem()).getID());
						preparedStmt.setString (2, Main.dfDateAndTime.format(dcDate.getDate()));
						preparedStmt.setString (3, Main.dfDateAndTime.format(new Date()));
						preparedStmt.setBoolean(4, req.getOK());
						preparedStmt.setInt(5, LoggedUser.ID);
						preparedStmt.setInt(6, req.getId());
						preparedStmt.executeUpdate();
						
						req.setClientId(((Client)cbClient.getSelectedItem()).getID());
						req.setDateRequest(Main.dfDateAndTime.format(dcDate.getDate()));
						req.setUserId(LoggedUser.ID);
						
						// Вставити кількості// Для початку видалити зв'язки
						
						String queryRemove = "delete from request_has_production where request_id = ?";
						PreparedStatement preparedStmtRemove = conn.prepareStatement(queryRemove);
						preparedStmtRemove.setInt(1, req.getId());
						preparedStmtRemove.execute();
						
						for(int i=0; i<table.getRowCount(); i++){
							if(table.getValueAt(i, 3).toString()!=""){
								if(Main.isNumeric(table.getValueAt(i, 3).toString())){
									query = "INSERT INTO request_has_production(Request_id, Production_id, Count) VALUES(?,?,?)";
									preparedStmt = conn.prepareStatement(query);
									preparedStmt.setInt (1, req.getId());
									preparedStmt.setInt (2, ((Production)table.getValueAt(i, 0)).getID());
									preparedStmt.setInt (3, Integer.parseInt(table.getValueAt(i, 3).toString()));
									preparedStmt.execute();
								}
							}
						}
					}
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			
				if(mode==0)
					Main.insertLog("Формування заявки № " + lastInsert.getId());
				else if(mode==2)
					Main.insertLog("Редагування заявки " + req.toString());
				
				setVisible(false);
				dispose();				
			}
		});
		btnSave.setBounds(11, 291, 89, 23);
		contentPane.add(btnSave);
		
		
		
		
		JButton btnCancel = new JButton("Відміна");
		if(mode==1)
			btnCancel.setText("Закрити");
		btnCancel.setBounds(442, 291, 89, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel = true;
				setVisible(false);
				dispose();
			}
		});
		
		JLabel ls = new JLabel("\u0421\u0443\u043C\u0430: ");
		ls.setBounds(389, 264, 46, 14);
		contentPane.add(ls);
		
		tfSum = new JTextField();
		tfSum.setEditable(false);
		tfSum.setBounds(445, 261, 86, 20);
		contentPane.add(tfSum);
		tfSum.setColumns(10);
		
		dcDate = new JDateChooser();
		dcDate.setBounds(126, 39, 159, 20);
		contentPane.add(dcDate);
		
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
		table.getActionMap().put(solve, new EnterAction());
		
		
		tfNumber = new DoubleJTextField();
		//tfNumber.addKeyListener(listener);
		tfNumber.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, solve);
		tfNumber.getActionMap().put(solve, new EnterAction());
		tfNumber.addMouseListener(new MouseListener() {

		    public void mouseClicked(MouseEvent e) {
		    	tfNumber.setText("");
		    	
		    	if(!inGO){
		    		inGO = true;
		    	}
		    	
		    }

		    public void mousePressed(MouseEvent e) {

		    }

		    public void mouseReleased(MouseEvent e) {

		    }

		    public void mouseEntered(MouseEvent e) {

		    }

		    public void mouseExited(MouseEvent e) {

		    }

		});
		tfNumber.setBounds(344, 36, 86, 20);
		contentPane.add(tfNumber);
		tfNumber.setColumns(10);
		
		btnGo = new JButton("\u041F\u0435\u0440\u0435\u0439\u0442\u0438");
		btnGo.setBounds(442, 35, 89, 23);
		btnGo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(!inGO){
					tfNumber.setText("");
					tfNumber.requestFocus();
					return;
				}
				
				
				if(!Main.isNumeric(tfNumber.getText()))
					return;
				
				int pos = Integer.parseInt(tfNumber.getText());
				boolean find = false;
				for(int i=0; i<table.getRowCount(); i++){
					Production p = ((Production)table.getValueAt(i, 0));
					if(p.getID()==pos){
						table.setRowSelectionInterval(i, i);
						table.requestFocus();
						table.editCellAt(i,3);
						table.scrollRectToVisible(new Rectangle(table.getCellRect(i, 0, true)));
						find = true;
						break;
					}
				}
				
				if(!find){
					inGO=true;
					tfNumber.setText("");
					tfNumber.requestFocus();
					return;
				}
				
			}
		});
		contentPane.add(btnGo);
		
		JLabel lblNewLabel = new JLabel("\u041F\u043E\u0437\u0438\u0446\u0456\u044F:");
		lblNewLabel.setBounds(294, 39, 46, 14);
		contentPane.add(lblNewLabel);
		
		try{
		if((mode==1 || mode==2) && req!=null)
			dcDate.setDate(Main.dfDateAndTime.parse(req.getDateRequest()));
		}
		catch(Exception ex){}
		
		/*
		if(req!=null && req.getOK()){
			table.setEnabled(false);
			btnSave.setEnabled(false);
			cbPlace.setEnabled(false);
			cbClient.setEnabled(false);
			dcDate.setEnabled(false);
		}*/
		
		//table.addKeyListener(listener);
		
		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            int width=c.getWidth(), heigth=c.getHeight();
	            
	    		jsp.setBounds(11, 64, width-10-20-3, heigth-40-10-10-64-10-23-15);
	    		btnCancel.setBounds(width-15-89-5-3, heigth-40-2-24, 89, 23);
	    		btnSave.setBounds(11, heigth-40-2-24, 89, 23);

	    		tfSum.setBounds(width-15-89-5-3, heigth-40-2-24-7-23, 86, 20);
	    		ls.setBounds(width-15-89-5-3-46, heigth-40-2-24-7-23, 46, 14);
	    		
	        }
		});
		

		
		
		initData(mode, req);
	}
		
	private void initData(int mode, Request req){
				
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String queryP = "SELECT * FROM Production";
			Statement stp = conn.createStatement();
			ResultSet rsp = stp.executeQuery(queryP);
			while(rsp.next()){
				int id = rsp.getInt("id");
				String name = rsp.getString("name");
				float countOnStorage = rsp.getFloat("CountOnStorage");
			    Production prod = new Production(id, name, countOnStorage);
			    DoubleJTextField tf = new DoubleJTextField();
			    
			    
			    if(mode==0)
			    	model.addRow(new Object[]{prod, "шт.", 0.0f, "", tf});
			    else if((mode==1 || mode==2) && req!=null){
			    	String q = "SELECT * FROM request_has_production WHERE request_id = " + req.getId() + " and production_id = " + id;
			    	Statement sq  = conn.createStatement();
			    	ResultSet rq = sq.executeQuery(q);
			    	
			    	if(rq.next()){
				    	model.addRow(new Object[]{prod, "шт.", 0.0f, rq.getInt("count"), tf});
				    	oldValue.add(rq.getInt("count"));
			    	}
			    	else{
				    	model.addRow(new Object[]{prod, "шт.", 0.0f, "", tf});
				    	oldValue.add(0);
			    	}
			    	
			    	rq.close();
			    	sq.close();
			    }
			}
			rsp.close();
			stp.close();
			
			int selplace = -1;
			if((mode==1 || mode==2) && req!=null){
				Statement stU = conn.createStatement();
				String queryU = "SELECT * FROM Client where id="+req.getClientId();
				ResultSet rsU = stU.executeQuery(queryU);
				
				while(rsU.next()){
					viewClientID = rsU.getInt("id");
					selplace = rsU.getInt("place_id");
				}
				
				rsU.close();
				stU.close();
			}
			
			
			String query = "SELECT * FROM Place";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			int firstid=-1;
			while(rs.next()){
				int id = rs.getInt("id");
			    String name = rs.getString("name");
			    cbPlace.addItem(new Place(id, name));
			    
			    if((mode==1 || mode==2) && selplace==id){
			    	cbPlace.setSelectedIndex(cbPlace.getItemCount()-1);
			    }
			    
			    if(cbPlace.getItemCount()==1)
			    	firstid = id;
			}
			rs.close();
			st.close();
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private class EnterAction extends AbstractAction {

	    @Override
	    public void actionPerformed(ActionEvent e) {
	    	if(!Main.isNumeric(tfNumber.getText())){
				inGO = true;
				return;
			}
			
			btnGo.doClick();
			if(inGO)
				inGO = false;
			else
				inGO = true;
	    }
	}
	
	class MyKeyListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if(e.getKeyCode() == KeyEvent.VK_A){
				
				if(!Main.isNumeric(tfNumber.getText())){
					inGO = true;
					return;
				}
				
				btnGo.doClick();
				if(inGO)
					inGO = false;
				else
					inGO = true;
				
            }
            else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            	//btnCancel.doClick();
            }
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent e) {
            
         }
	}
	
}
