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

import ua.rokytne.bakery.DoubleJTextField;
import ua.rokytne.bakery.LoggedUser;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.Employee;
import ua.rokytne.bakery.orm.Ingridient;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class NewIngridientForm extends JDialog {

	private JPanel contentPane;
	private JTextField tfName;
	private JTextField tfCount;
	private JButton btnAdd;
	public Ingridient lastInsert;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewIngridientForm frame = new NewIngridientForm(false, null);
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
	public NewIngridientForm(boolean edMode, Ingridient ing) {
		this.setAlwaysOnTop(Main.onTop);

		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		if(!edMode)
			setTitle("Додавання інгрідієнту");
		else			
			setTitle("Редагування інгридієнту");
		
		setBounds((Main.WIDTH-309)/2, (Main.HEIGTH-140)/2, 309, 140);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnCancel = new JButton("Відмінити");
		btnCancel.setBounds(198, 71, 89, 23);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnCancel);
		
		if(!edMode)
			tfName = new JTextField();
		else
			tfName = new JTextField(ing.getName());
		tfName.setColumns(10);
		tfName.setBounds(84, 11, 203, 20);
		contentPane.add(tfName);
		
		JLabel label = new JLabel("\u041D\u0430\u0437\u0432\u0430:");
		label.setBounds(10, 14, 119, 14);
		contentPane.add(label);
		
		if(!edMode)
			btnAdd = new JButton("Додати");
		else
			btnAdd = new JButton("Редагувати");
		
		btnAdd.setBounds(10, 71, 120, 23);
		contentPane.add(btnAdd);
		btnAdd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				lastInsert = null;
				if(tfName.getText().length()<3){
					JOptionPane.showMessageDialog(null, "Вкажіть будь ласка назву!");
					return;
				}
				
				if(!Main.isNumeric(tfCount.getText())){
					JOptionPane.showMessageDialog(null, "Наявну кількість вкажіть числом!");
					return;
				}
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					if(!edMode){
						String query = " insert into ingridient(name, countOnStorage)" + " values (?, ?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString (1, tfName.getText());
						preparedStmt.setFloat(2, Float.parseFloat(tfCount.getText()));
						preparedStmt.execute();
						query = "SELECT * FROM ingridient WHERE ID = (SELECT MAX(ID) FROM ingridient)";
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
					
						while (rs.next())
						{
						    int id = rs.getInt("id");
						    String fullName = rs.getString("name");
						    float count = rs.getFloat("countOnStorage");
						    lastInsert = new Ingridient(id, fullName, count);
						}
						st.close();
					}
					else {
						String query = "UPDATE ingridient SET name = ?, countOnStorage = ? where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString(1, tfName.getText());
						preparedStmt.setFloat(2, Float.parseFloat(tfCount.getText()));
						preparedStmt.setInt(3, ing.getID());
						preparedStmt.executeUpdate();
						ing.setName(tfName.getText());
						ing.setCountOnStorage(Float.parseFloat(tfCount.getText()));
					}
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			

				if(!edMode)
					Main.insertLog("Додавання нової сировини " + lastInsert.getName());
				else
					Main.insertLog("Редагування сировини " + ing.getName());
				
				setVisible(false);
				dispose();

			}
		});
		
		JLabel label_1 = new JLabel("\u0412\u0436\u0435 \u043D\u0430\u044F\u0432\u043D\u043E:");
		label_1.setBounds(10, 45, 63, 14);
		contentPane.add(label_1);
		
		tfCount = new DoubleJTextField();
		
		
		if(edMode){

			if(LoggedUser.ROLE!=0){
				tfCount.setEnabled(false);
			}
				
			tfCount.setText(ing.getCountOnStorage() + "");
		}
		tfCount.setColumns(10);
		tfCount.setBounds(80, 42, 71, 20);
		contentPane.add(tfCount);
		
		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            setSize(c.getWidth(), 140);
	            int width=c.getWidth(), heigth=140;
	            
	            tfName.setBounds(78, 8, width-45-97+40, 20);
	            
	    		btnCancel.setBounds(width-15-89-5-3, heigth-40-2-24, 89, 23);
	    		btnAdd.setBounds(11, heigth-40-2-24, 89, 23);
	    		
	        }
		});
		
	}

}
