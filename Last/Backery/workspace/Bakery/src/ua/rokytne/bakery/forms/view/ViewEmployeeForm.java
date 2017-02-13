package ua.rokytne.bakery.forms.view;

import java.awt.BorderLayout;
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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.forms.newedit.NewEmployeeForm;
import ua.rokytne.bakery.orm.Employee;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Component;

public class ViewEmployeeForm extends JDialog {

	private ArrayList<Employee> employees = new ArrayList<Employee>();
	private DefaultListModel model = new DefaultListModel();
	
	private JPanel contentPane;
	private JList list;
	private JButton btnDeleteEmpl, btnEditEmpl, btnAddEmpl;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewEmployeeForm frame = new ViewEmployeeForm();
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
	public ViewEmployeeForm() {
		setTitle("Робітники підприємства");
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setBounds((Main.WIDTH-441)/2, (Main.HEIGTH-271)/2, 441, 271);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(317, 202, 100, 23);
		contentPane.add(btnCancel);
		
		list = new JList(model);
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!btnDeleteEmpl.isEnabled())
					btnDeleteEmpl.setEnabled(true);

				if(!btnEditEmpl.isEnabled())
					btnEditEmpl.setEnabled(true);
			}
		});
		list.setBounds(0, 0, 296, 214);
		JScrollPane jsp = new JScrollPane(list);
		jsp.setBounds(10, 11, 296, 214);
		contentPane.add(jsp);
		//panel.add(list);
		
		btnDeleteEmpl = new JButton("Видалити");
		btnDeleteEmpl.setBounds(317, 79, 100, 23);
		contentPane.add(btnDeleteEmpl);
		btnDeleteEmpl.setBackground(Color.RED);
		btnDeleteEmpl.setEnabled(false);
		
		btnEditEmpl = new JButton("Редагувати");
		btnEditEmpl.setBounds(317, 45, 100, 23);
		contentPane.add(btnEditEmpl);
		btnEditEmpl.setBackground(Color.CYAN);
		btnEditEmpl.setEnabled(false);
		
		btnAddEmpl = new JButton("Додати");
		btnAddEmpl.setBounds(317, 11, 100, 23);
		contentPane.add(btnAddEmpl);
		btnAddEmpl.setBackground(Color.GREEN);
		btnAddEmpl.setForeground(Color.BLACK);
		btnAddEmpl.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NewEmployeeForm frame = new NewEmployeeForm(false, null);
				frame.setModal(true);
				frame.setVisible(true);
				
				if(frame.lastInsert!=null){
					employees.add(frame.lastInsert);
					model.addElement(employees.get(employees.size()-1));
					list.setSelectedValue(employees.get(employees.size()-1), true);
				}
			}
		});
		btnEditEmpl.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Employee toEdit = (Employee)model.getElementAt(list.getSelectedIndex());
				NewEmployeeForm frame = new NewEmployeeForm(true, toEdit);
				frame.setModal(true);
				frame.setVisible(true);
				int pos = model.indexOf(toEdit);
				model.removeElement(toEdit);
				model.add(pos, toEdit);
				list.setSelectedIndex(pos);
			}
		});
		btnDeleteEmpl.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Main.insertLog("Спроба видалити робітника підприємства (" + ((Employee)model.getElementAt(list.getSelectedIndex())).getFullName() + ")");
				
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
					Employee toDel = (Employee)model.getElementAt(list.getSelectedIndex());
					
					int i = Main.okcancel("Дійсно видалити: " + toDel.getFullName() + "?");
				    
					if(i==0){
						String query = "delete from employee where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt(1, toDel.getID());
						// execute the preparedstatement
						preparedStmt.execute();
						model.removeElement(toDel);
						employees.remove(toDel);
						
						if(btnDeleteEmpl.isEnabled())
							btnDeleteEmpl.setEnabled(false);

						if(btnEditEmpl.isEnabled())
							btnEditEmpl.setEnabled(false);
					}
					conn.close();
				}
				catch(Exception ex){
					
				}
			}
		});
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
	            int width=c.getWidth(), heigth=c.getHeight();
	            
	    		jsp.setBounds(10, 11, width-100-15-20, heigth-40-10-5);

	    		btnCancel.setBounds(width-20-100, 203, 100, 23);
	    		
	    		btnAddEmpl.setBounds(width-20-100, 11, 100, 23);
	    		btnEditEmpl.setBounds(width-20-100, 45, 100, 23);
	    		btnDeleteEmpl.setBounds(width-20-100, 79, 100, 23);
	        }
		});
		
		initData();
	}
	
	public void initData(){
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM employee";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String fullName = rs.getString("fullname");
			    employees.add(new Employee(id, fullName));
			    model.addElement(employees.get(employees.size()-1));
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			
		}
	}

}
