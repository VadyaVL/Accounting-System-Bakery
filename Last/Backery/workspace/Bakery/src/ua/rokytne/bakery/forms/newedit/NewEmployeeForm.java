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

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;

public class NewEmployeeForm extends JDialog {

	private JPanel contentPane;
	private JTextField textField;
	private JButton btnAdd;
	public Employee lastInsert = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewEmployeeForm frame = new NewEmployeeForm(false, null);
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
	public NewEmployeeForm(boolean edMode, Employee empl) {
		this.setAlwaysOnTop(Main.onTop);
		
		if(!edMode)
			setTitle("Додавання робітника");
		else
			setTitle("Редагування робітника");

		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setBounds((Main.WIDTH-341)/2, (Main.HEIGTH-107)/2, 341, 107);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("\u041F\u0406\u0411:");
		label.setBounds(10, 11, 46, 14);
		contentPane.add(label);
		
		textField = new JTextField();
		textField.setBounds(51, 8, 267, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		if(edMode && empl!=null){
			textField.setText(empl.getFullName());
		}
		
		if(!edMode)
			btnAdd = new JButton("Додати");
		else
			btnAdd = new JButton("Редагувати");
		btnAdd.setBounds(10, 39, 105, 23);
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
						String query = " insert into employee(fullname)" + " values (?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString (1, textField.getText());
						preparedStmt.execute();
						query = "SELECT * FROM employee WHERE ID = (SELECT MAX(ID) FROM employee)";
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
					
						while (rs.next())
						{
						    int id = rs.getInt("id");
						    String fullName = rs.getString("fullname");
						    lastInsert = new Employee(id, fullName);
						}
						st.close();
					}
					else {
						String query = "UPDATE employee SET fullname = ? where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString(1, textField.getText());
						preparedStmt.setInt(2, empl.getID());
						preparedStmt.executeUpdate();
						empl.setFullName(textField.getText());
					}
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			
				if(!edMode)
					Main.insertLog("Додавання робітника підпр. " + lastInsert.getFullName());
				else
					Main.insertLog("Редагування робітника підпр. " + empl.getFullName());
				
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnAdd);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(210, 39, 105, 23);
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
	            setSize(c.getWidth(), 107);
	            int width=c.getWidth(), heigth=107;
	            
	            textField.setBounds(78, 8, width-45-97+40, 20);
	            
	    		btnCancel.setBounds(width-15-89-5-3, heigth-40-2-24, 89, 23);
	    		btnAdd.setBounds(11, heigth-40-2-24, 89, 23);
	    		
	        }
		});
		
	}

}
