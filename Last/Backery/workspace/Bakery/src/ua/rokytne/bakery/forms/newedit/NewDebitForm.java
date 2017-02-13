package ua.rokytne.bakery.forms.newedit;

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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import ua.rokytne.bakery.DoubleJTextField;
import ua.rokytne.bakery.LoggedUser;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.Debit;
import ua.rokytne.bakery.orm.Employee;
import ua.rokytne.bakery.orm.Ingridient;
import ua.rokytne.bakery.orm.Production;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JButton;

public class NewDebitForm extends JDialog {

	private JPanel contentPane;
	private DoubleJTextField tfCount;
	private JComboBox cbEmpl, cbProd;
	public Object[] lastInsert;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewDebitForm frame = new NewDebitForm();
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
	public NewDebitForm() {
		this.setAlwaysOnTop(Main.onTop);
		setTitle("\u0421\u043F\u0438\u0441\u0430\u043D\u043D\u044F \u043F\u0440\u043E\u0434\u0443\u043A\u0446\u0456\u0457");
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setBounds((Main.WIDTH-336)/2, (Main.HEIGTH-161)/2, 336, 161);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("\u041A\u043E\u043C\u0443:");
		label.setBounds(10, 11, 46, 14);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("\u041F\u0440\u043E\u0434\u0443\u043A\u0442:");
		label_1.setBounds(10, 36, 63, 14);
		contentPane.add(label_1);
		
		JLabel label_2 = new JLabel("\u041A\u0456\u043B\u044C\u043A\u0456\u0441\u0442\u044C:");
		label_2.setBounds(10, 64, 63, 14);
		contentPane.add(label_2);
		
		cbEmpl = new JComboBox();
		cbEmpl.setBounds(66, 8, 244, 20);
		contentPane.add(cbEmpl);
		
		cbProd = new JComboBox();
		cbProd.setBounds(66, 33, 244, 20);
		contentPane.add(cbProd);
		
		tfCount = new DoubleJTextField();
		tfCount.setBounds(66, 61, 244, 20);
		contentPane.add(tfCount);
		tfCount.setColumns(10);
		
		JButton btnAddDebit = new JButton("Списати");
		btnAddDebit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				lastInsert = null;
				if(!Main.isNumeric(tfCount.getText())){
					JOptionPane.showMessageDialog(null, "Кількість вкажіть числом!");
					return;
				}
				
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "INSERT INTO spisannya(User_id, Production_id, Count, Employee_id, DateTime) VALUES(?,?,?,?,?)";
					PreparedStatement preparedStmt = conn.prepareStatement(query);
					preparedStmt.setInt (1, LoggedUser.ID);
					preparedStmt.setInt (2, ((Production)cbProd.getSelectedItem()).getID());
					preparedStmt.setFloat (3, Float.parseFloat(tfCount.getText()));
					preparedStmt.setInt (4, ((Employee)cbEmpl.getSelectedItem()).getID());
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					preparedStmt.setString(5, dateFormat.format(new Date()));
					preparedStmt.execute();
					
					// Відняти від наявної продукції
					String updProduction = "UPDATE production set CountOnStorage = CountOnStorage - ? WHERE id = ?";
					PreparedStatement preparedStmtupdProduction = conn.prepareStatement(updProduction);
					preparedStmtupdProduction.setInt (1, (int)Float.parseFloat(tfCount.getText()));
					preparedStmtupdProduction.setInt (2, ((Production)cbProd.getSelectedItem()).getID());
					preparedStmtupdProduction.execute();
					
					query = "SELECT * FROM spisannya WHERE DateTime = (SELECT MAX(DateTime) FROM spisannya)";
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
				
					while (rs.next())
					{

						int uid = rs.getInt("user_id");
						int pid = rs.getInt("production_id");
						int eid = rs.getInt("employee_id");
						int count = rs.getInt("count");
						String datetime = rs.getString("datetime");
						
						Production product = null;
						Employee employee = null;
						
						String queryT = "SELECT * FROM production where id = " + pid;
						Statement stT = conn.createStatement();
						ResultSet rsT = stT.executeQuery(queryT);
						
						if(rsT.next())
							product = new Production(rsT.getInt("id"), rsT.getString("name"), rsT.getFloat("countOnStorage"));
						
						queryT = "SELECT * FROM employee where id = " + eid;
						rsT = stT.executeQuery(queryT);
						
						if(rsT.next())
							employee = new Employee(rsT.getInt("id"), rsT.getString("fullname"));

						lastInsert = new Object[]{employee, product, count, new Debit(uid, pid, eid, count, datetime)};
					}
					st.close();
					
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			
				
				String mess = "Списання для " + ((Employee)cbEmpl.getSelectedItem()).getFullName() + " " + ((Production)cbProd.getSelectedItem()).getName() + " (" + tfCount.getText() + " шт.)";
				
				Main.insertLog(mess);
				
				setVisible(false);
				dispose();
				
			}
		});
		btnAddDebit.setBounds(10, 92, 89, 23);
		contentPane.add(btnAddDebit);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(221, 92, 89, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            setSize(c.getWidth(), 161);
	            int width=c.getWidth(), heigth=161;
	            
	    		cbEmpl.setBounds(66, 8, width-20-66, 20);
	    		cbProd.setBounds(66, 33, width-20-66, 20);
	    		tfCount.setBounds(66, 61, width-20-66, 20);
	    		
	    		btnAddDebit.setBounds(11, heigth-40-2-24, 89, 23);
	    		btnCancel.setBounds(width-15-89-5-3, heigth-40-2-24, 89, 23);
	    		
	        }
		});
		
		initData();
	}
		
	private void initData(){
					
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM Employee";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
				cbEmpl.addItem(new Employee(rs.getInt("id"), rs.getString("fullname")));
			
			query = "SELECT * FROM Production";
			rs = st.executeQuery(query);
			  
			while (rs.next())
				cbProd.addItem(new Production(rs.getInt("id"), rs.getString("name"), rs.getFloat("countOnStorage")));
			
			
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			
		}
	}
}
