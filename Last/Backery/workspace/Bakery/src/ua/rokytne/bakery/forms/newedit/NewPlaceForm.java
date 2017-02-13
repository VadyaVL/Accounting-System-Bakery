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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.Employee;
import ua.rokytne.bakery.orm.Place;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;

public class NewPlaceForm extends JDialog {

	private JPanel contentPane;
	private JTextField textField;
	private JButton btnAdd;
	public Place lastInsert;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewPlaceForm frame = new NewPlaceForm(false, null);
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
	public NewPlaceForm(boolean edMode, Place place) {

		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		if(!edMode)
			setTitle("Додавання нового населеного пункту");
		else
			setTitle("Редагування населеного пункту");

		setBounds((Main.WIDTH-356)/2, (Main.HEIGTH-106)/2, 356, 106);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Населений пункт:");
		lblNewLabel.setBounds(10, 11, 97, 14);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(118, 8, 214, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		if(edMode && place!=null)
			textField.setText(place.getName());
		
		if(!edMode)
			btnAdd = new JButton("Додати");
		else
			btnAdd = new JButton("Редагувати");
		btnAdd.setBounds(10, 36, 120, 23);
		btnAdd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				lastInsert = null;
				if(textField.getText().length()<1){
					JOptionPane.showMessageDialog(null, "Вкажіть будь ласка назву населеного пункту!");
					return;
				}
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					if(!edMode){
						String query = " insert into place(name)" + " values (?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString (1, textField.getText());
						preparedStmt.execute();
						query = "SELECT * FROM place WHERE ID = (SELECT MAX(ID) FROM place)";
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
					
						while (rs.next())
						{
						    int id = rs.getInt("id");
						    String name = rs.getString("name");
						    lastInsert = new Place(id, name);
						}
						st.close();
					}
					else {
						String query = "UPDATE place SET name = ? where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString(1, textField.getText());
						preparedStmt.setInt(2, place.getID());
						preparedStmt.executeUpdate();
						place.setName(textField.getText());
					}
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			
				if(!edMode)
					Main.insertLog("Додавання н.п. " + lastInsert.getName());
				else
					Main.insertLog("Редагування н.п. " + place.getName());
				
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnAdd);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(244, 39, 89, 23);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnCancel);
		
		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            setSize(c.getWidth(), 106);
	            int width=c.getWidth(), heigth=106;
	            textField.setBounds(118, 8, width-45-97, 20);
	    		btnCancel.setBounds(width-15-89-5-3, heigth-40-2-24, 89, 23);
	    		btnAdd.setBounds(11, heigth-40-2-24, 89, 23);
	    		
	        }
		});
		
	}

}
