package ua.rokytne.bakery.forms.view;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import ua.rokytne.bakery.ImagePanel;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.Main.Role;
import ua.rokytne.bakery.forms.newedit.NewUserForm;
import ua.rokytne.bakery.orm.User;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

public class ViewUserForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ImagePanel photoPanel;
	private ArrayList<User> users = new ArrayList<User>();
	Object[] columnNames = {"Логін", "ПІБ", "Права"};
	Object[][] data = {};
	DefaultTableModel model;
	private JTable table;
	private JButton btnEditUser;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewUserForm frame = new ViewUserForm();
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
	public ViewUserForm() {
		
		model = new DefaultTableModel(data, columnNames) {

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Користувачі системи");
		setBounds((Main.WIDTH-594)/2, (Main.HEIGTH-337)/2, 594, 337);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(470, 269, 100, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		JButton btnDeleteUser = new JButton("Видалити");
		btnDeleteUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					// Взяти виділене id
					User toDel = (User)model.getValueAt(table.getSelectedRow(), 0);
					
					int i = Main.okcancel("Дійсно видалити: " + toDel.getUsername() + "?");
				    
					if(i==0){
						String query = "delete from User where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt(1, toDel.getId());
						// execute the preparedstatement
						preparedStmt.execute();
						model.removeRow(table.getSelectedRow());
						users.remove(toDel);
						
						try{
							File delP = new File("photos/" + toDel.getPhoto());
							delP.delete();
						}
						catch(Exception ex){
							
						}
						
						if(btnDeleteUser.isEnabled())
							btnDeleteUser.setEnabled(false);

						if(btnEditUser.isEnabled())
							btnEditUser.setEnabled(false);
					}
					conn.close();
				}
				catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Видалення не можливе.Існують зв'язки");
				}
				
			}
		});
		btnDeleteUser.setEnabled(false);
		btnDeleteUser.setBackground(Color.RED);
		btnDeleteUser.setBounds(470, 79, 100, 23);
		contentPane.add(btnDeleteUser);
		
		btnEditUser = new JButton("Редагувати");
		btnEditUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				User toEdit = (User)model.getValueAt(table.getSelectedRow(), 0);
				NewUserForm frame = new NewUserForm(true, toEdit);
				frame.setModal(true);
				frame.setVisible(true);
				
				// ОБновить
				model.setValueAt(toEdit, table.getSelectedRow(), 0);
				model.setValueAt(toEdit.getRealName(), table.getSelectedRow(), 1);
				
				Role role = Role.PACKAGER;
				
				if(toEdit.getAccessLevel()==0)
					role = Role.SUPERADMIN;
				else if(toEdit.getAccessLevel()==1)
					role = Role.EXPEDITOR;
				else if(toEdit.getAccessLevel()==2)
					role = Role.PACKAGER;
				
				model.setValueAt(role, table.getSelectedRow(), 2);
				photoPanel.setPhoto("photos/" + toEdit.getPhoto()); 
			}
		});
		btnEditUser.setEnabled(false);
		btnEditUser.setBackground(Color.CYAN);
		btnEditUser.setBounds(470, 45, 100, 23);
		contentPane.add(btnEditUser);
		
		JButton btnAddUser = new JButton("Додати");
		btnAddUser.setBackground(Color.GREEN);
		btnAddUser.setBounds(470, 11, 100, 23);
		contentPane.add(btnAddUser);
		btnAddUser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NewUserForm frame = new NewUserForm(false, null);
				frame.setModal(true);
				frame.setVisible(true);
				
				if(frame.lastInsert!=null){
					users.add(frame.lastInsert);
					
					Role role = Role.PACKAGER;
					
					if(frame.lastInsert.getAccessLevel()==0)
						role = Role.SUPERADMIN;
					else if(frame.lastInsert.getAccessLevel()==1)
						role = Role.EXPEDITOR;
					else if(frame.lastInsert.getAccessLevel()==2)
						role = Role.PACKAGER;
					
					model.addRow(new Object[]{users.get(users.size()-1), users.get(users.size()-1).getRealName(), role});
				}
			}
		});
		
		table = new JTable(model);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setBounds(10, 11, 283, 233);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(10, 11, 450, 281);
		getContentPane().add(jsp);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		photoPanel = new ImagePanel();
		photoPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		photoPanel.setBounds(470, 113, 100, 145);
		contentPane.add(photoPanel);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//System.out.println(table.getValueAt(table.getSelectedRow(), 0));
				if(!btnDeleteUser.isEnabled())
					btnDeleteUser.setEnabled(true);

				if(!btnEditUser.isEnabled())
					btnEditUser.setEnabled(true);
				
				try{
					photoPanel.setPhoto("photos/" + ((User)table.getValueAt(table.getSelectedRow(), 0)).getPhoto());
				}
				catch (Exception ex){
					photoPanel.setPhoto("photos/-9.jpg");
				}
				
			}
		});
		
		
		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.2));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.6));
		table.getColumnModel().getColumn(2).setPreferredWidth((int) (table.getWidth()*0.2));
		
		
		addComponentListener(new ComponentAdapter() 
		{  
		        public void componentResized(ComponentEvent evt) {
		            Component c = (Component)evt.getSource();
		            int width=c.getWidth(), heigth=c.getHeight();
		            
		    		jsp.setBounds(10, 11, width-100-15-20, heigth-40-10-5);


		    		btnCancel.setBounds(width-20-100, 269, 100, 23);
		    		btnDeleteUser.setBounds(width-20-100, 79, 100, 23);
		    		btnEditUser.setBounds(width-20-100, 45, 100, 23);
		    		btnAddUser.setBounds(width-20-100, 11, 100, 23);
		    		photoPanel.setBounds(width-20-100, 113, 100, 145);
		        }
		});
		
		
		
		initData();
	}
	
	public void initData(){

		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM user";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String username = rs.getString("username");
				String password = rs.getString("password");
				String realName = rs.getString("realname");
				String photo = rs.getString("photo");
				int al = rs.getInt("AccessLevel");
				
				Role role = Role.PACKAGER;
				
				if(al==0)
					role = Role.SUPERADMIN;
				else if(al == 1)
					role = Role.EXPEDITOR;
				
			    users.add(new User(id, username, password, realName, photo, al));
			    model.addRow(new Object[]{users.get(users.size()-1), users.get(users.size()-1).getRealName(), role});
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
