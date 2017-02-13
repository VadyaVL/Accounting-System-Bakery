package ua.rokytne.bakery.forms.newedit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.Client;
import ua.rokytne.bakery.orm.Employee;
import ua.rokytne.bakery.orm.Place;

public class NewClientForm extends JDialog {

	private JPanel contentPane;
	private JTextField textField;
	private JComboBox comboBox;
	private JButton btnAdd;
	public Client lastInsert;
	private ArrayList<Place> places = new ArrayList<Place>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewClientForm frame = new NewClientForm(false, null);
					//Main.setUIFont(new javax.swing.plaf.FontUIResource());
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
	public NewClientForm(boolean edMode, Client client) {
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		if(!edMode)
			setTitle("Додавання нового клієнта");
		else
			setTitle("Редагування клієнта");
		

		this.setAlwaysOnTop(Main.onTop);

		setBounds((Main.WIDTH-340)/2, (Main.HEIGTH-139)/2, 340, 139);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("\u041F\u0406\u0411:");
		//lblNewLabel.setFont(new Font("Tahoma",Font.PLAIN,11));
		lblNewLabel.setBounds(10, 11, 46, 14);
		contentPane.add(lblNewLabel);
		
		if(!edMode)
			btnAdd = new JButton("Додати");
		else 
			btnAdd = new JButton("Редагувати");
		
		btnAdd.setBounds(10, 71, 105, 23);
		contentPane.add(btnAdd);
		btnAdd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				lastInsert = null;
				if(textField.getText().length()<5){
					JOptionPane.showMessageDialog(null, "Вкажіть будь ласка повне ім\'я!");
					return;
				}
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					if(!edMode){
						String query = " insert into client(name, place_id)" + " values (?, ?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString (1, textField.getText());
						preparedStmt.setInt (2, ((Place)comboBox.getSelectedItem()).getID());
						preparedStmt.execute();
						query = "SELECT * FROM client WHERE ID = (SELECT MAX(ID) FROM client)";
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
					
						while (rs.next())
						{
						    int id = rs.getInt("id");
						    String name = rs.getString("name");
						    int place_id = rs.getInt("place_id");
						    lastInsert = new Client(id, name, place_id);
						}
						st.close();
					}
					else {
						String query = "UPDATE client SET name = ?, place_id = ?  where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString(1, textField.getText());
						preparedStmt.setInt(2, ((Place)comboBox.getSelectedItem()).getID());
						preparedStmt.setInt(3, client.getID());
						preparedStmt.executeUpdate();
						client.setName(textField.getText());
						client.setPlaceID(((Place)comboBox.getSelectedItem()).getID());
					}
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
				if(!edMode)
					Main.insertLog("Додавання клієнта " + lastInsert.getName());
				else
					Main.insertLog("Редагування клієнта " + client.getName());
				
				setVisible(false);
				dispose();
			}
		});
		
		JButton btnCancel = new JButton("Відмінити");
		btnCancel.setBounds(210, 71, 105, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		
		textField = new JTextField();
		
		if(edMode && client!=null)
			textField.setText(client.getName());
		
		textField.setBounds(66, 8, 249, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel label = new JLabel("Населений пункт:");
		label.setBounds(10, 42, 93, 14);
		
		//label.setFont(new Font("Tahoma",Font.PLAIN,11));
		contentPane.add(label);
		
		comboBox = new JComboBox();
		comboBox.setBounds(113, 39, 202, 20);
		contentPane.add(comboBox);
		
		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            setSize(c.getWidth(), 139);
	            int width=c.getWidth(), heigth=139;

	    		comboBox.setBounds(113, 39, width-20-113, 20);
	    		textField.setBounds(66, 8, width-20-66, 20);
	    		
	    		btnAdd.setBounds(11, heigth-40-2-24, 89, 23);
	    		btnCancel.setBounds(width-15-89-5-3, heigth-40-2-24, 89, 23);
	    		
	        }
		});
		
		initData(edMode, client);
	}
		
	private void initData(boolean edMode, Client client){
		
		int choose_place_id = -1;
		if(edMode && client!=null)
			choose_place_id = client.getPlaceID();
			
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM place";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				places.add(new Place(id, name));
				comboBox.addItem(places.get(places.size()-1));
				
				if(choose_place_id==id){
					comboBox.setSelectedItem(places.get(places.size()-1));
				}
				
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			
		}
	}
}
