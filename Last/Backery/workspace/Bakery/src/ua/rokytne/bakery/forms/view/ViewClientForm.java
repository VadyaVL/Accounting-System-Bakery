package ua.rokytne.bakery.forms.view;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.forms.newedit.NewClientForm;
import ua.rokytne.bakery.orm.Client;
import ua.rokytne.bakery.orm.Place;

import javax.swing.JOptionPane;
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
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class ViewClientForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ArrayList<Client> clients = new ArrayList<Client>();
	Object[] columnNames = {"ПІБ", "Н. п."};
	Object[][] data = {};
	DefaultTableModel model;
	private JTable table;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewClientForm frame = new ViewClientForm();
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
	public ViewClientForm() {
		model = new DefaultTableModel(data, columnNames) {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Клієнти");
		setBounds((Main.WIDTH-492)/2, (Main.HEIGTH-291)/2, 492, 291);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(370, 221, 100, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		
		JButton btnAddClient = new JButton("Додати");
		btnAddClient.setBackground(Color.GREEN);
		btnAddClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NewClientForm frame = new NewClientForm(false, null);
				frame.setModal(true);
				frame.setVisible(true);
				
				if(frame.lastInsert!=null){
					clients.add(frame.lastInsert);
					
					Place cPlace = null;
					try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
						
						//потрібно взяти Place
						String queryPlace = "SELECT * FROM place where id = " + frame.lastInsert.getPlaceID();
						Statement stPlace = conn.createStatement();
						ResultSet rsPlace = stPlace.executeQuery(queryPlace);
						if(rsPlace.next()){
							cPlace = new Place(rsPlace.getInt("id"), rsPlace.getString("name"));
						}
						stPlace.close();
						conn.close();
					}
					catch(Exception ex){ }
					
				    model.addRow(new Object[]{clients.get(clients.size()-1), cPlace});
				}
				
			}
		});
		btnAddClient.setBounds(370, 8, 100, 23);
		contentPane.add(btnAddClient);
		
		JButton btnEditClient = new JButton("Редагувати");
		btnEditClient.setEnabled(false);
		btnEditClient.setBackground(Color.CYAN);
		btnEditClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Client toEdit = (Client)model.getValueAt(table.getSelectedRow(), 0);
				NewClientForm frame = new NewClientForm(true, toEdit);
				frame.setModal(true);
				frame.setVisible(true);
				
				// ОБновить
				model.setValueAt(toEdit, table.getSelectedRow(), 0);
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					//потрібно взяти Place
					String queryPlace = "SELECT * FROM place where id = " + toEdit.getPlaceID();
					Statement stPlace = conn.createStatement();
					ResultSet rsPlace = stPlace.executeQuery(queryPlace);
					Place cPlace = null;
					if(rsPlace.next()){
						cPlace = new Place(rsPlace.getInt("id"), rsPlace.getString("name"));
					}
					model.setValueAt(cPlace, table.getSelectedRow(), 1);
					stPlace.close();
					conn.close();
				}
				catch(Exception ex){ }
			}
		});
		btnEditClient.setBounds(370, 42, 100, 23);
		contentPane.add(btnEditClient);
		
		JButton btnDeleteClient = new JButton("Видалити");
		btnDeleteClient.setEnabled(false);
		btnDeleteClient.setBackground(Color.RED);
		btnDeleteClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Main.insertLog("Спроба видалити клієнта (" + ((Client)model.getValueAt(table.getSelectedRow(), 0)).getName() + ")");
				
				
				boolean del = false;
				if(!del){
					JOptionPane.showMessageDialog(null, "Видалення не можливе. Існують зв'язки. Зверніться до адміністратора БД.");
					return;
				}
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					// Взяти виділене id
					Client toDel = (Client)model.getValueAt(table.getSelectedRow(), 0);
					
					int i = Main.okcancel("Дійсно видалити: " + toDel.getName() + "?");
				    
					if(i==0){
						String query = "delete from client where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt(1, toDel.getID());
						// execute the preparedstatement
						preparedStmt.execute();
						model.removeRow(table.getSelectedRow());
						clients.remove(toDel);
						
						if(btnDeleteClient.isEnabled())
							btnDeleteClient.setEnabled(false);

						if(btnEditClient.isEnabled())
							btnEditClient.setEnabled(false);
					}
					conn.close();
				}
				catch(Exception ex){
					
				}
			}
		});
		btnDeleteClient.setBounds(370, 76, 100, 23);
		contentPane.add(btnDeleteClient);
		
		table = new JTable(model);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setBounds(10, 8, 280, 236);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(10, 8, 350, 236);
		getContentPane().add(jsp);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//System.out.println(table.getValueAt(table.getSelectedRow(), 0));
				if(!btnDeleteClient.isEnabled())
					btnDeleteClient.setEnabled(true);

				if(!btnEditClient.isEnabled())
					btnEditClient.setEnabled(true);
			}
		});
		
		
		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.65));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.35));

		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            int width=c.getWidth(), heigth=c.getHeight();
	            
	    		jsp.setBounds(10, 11, width-100-15-20, heigth-40-10-5);

	    		btnCancel.setBounds(width-20-100, 221, 100, 23);
	    		
	    		btnAddClient.setBounds(width-20-100, 11, 100, 23);
	    		btnEditClient.setBounds(width-20-100, 45, 100, 23);
	    		btnDeleteClient.setBounds(width-20-100, 79, 100, 23);
	        }
		});
		
		initData();
	}
	
	public void initData(){
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM client";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int place_id = rs.getInt("place_id");
			    
				//потрібно взяти Place
				String queryPlace = "SELECT * FROM place where id = " + place_id;
				
				Statement stPlace = conn.createStatement();
				ResultSet rsPlace = stPlace.executeQuery(queryPlace);
				Place cPlace = null;
				if(rsPlace.next()){
					cPlace = new Place(rsPlace.getInt("id"), rsPlace.getString("name"));
				}
				
				clients.add(new Client(id, name, place_id));
			    model.addRow(new Object[]{clients.get(clients.size()-1), cPlace});
				stPlace.close();
		   
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			
		}
	}
}
