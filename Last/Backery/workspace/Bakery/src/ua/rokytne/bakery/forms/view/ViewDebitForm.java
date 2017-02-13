package ua.rokytne.bakery.forms.view;

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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.forms.newedit.NewDebitForm;
import ua.rokytne.bakery.forms.newedit.NewEmployeeForm;
import ua.rokytne.bakery.orm.Debit;
import ua.rokytne.bakery.orm.Employee;
import ua.rokytne.bakery.orm.Ingridient;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.User;

import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class ViewDebitForm extends JDialog {

	private JPanel contentPane;
	private JTable table;
	private ArrayList<Debit> debits = new ArrayList<Debit>();
	Object[] columnNames = {"Робітник", "Продукт", "Кількість", "Час"};
	Object[][] data = {};
	DefaultTableModel model;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewDebitForm frame = new ViewDebitForm();
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
	public ViewDebitForm() {
		model = new DefaultTableModel(data, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		setTitle("Списання");
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setBounds((Main.WIDTH-632)/2, (Main.HEIGTH-326)/2, 632, 326);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnSpisat = new JButton("Списати");
		btnSpisat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NewDebitForm frame = new NewDebitForm();
				frame.setModal(true);
				frame.setVisible(true);
				
				if(frame.lastInsert!=null){
					debits.add((Debit)frame.lastInsert[3]);
				    model.addRow(frame.lastInsert);
					//model.addElement(employees.get(employees.size()-1));
					//list.setSelectedValue(employees.get(employees.size()-1), true);
				}
				
			}
		});
		btnSpisat.setBounds(10, 261, 82, 23);
		contentPane.add(btnSpisat);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(523, 261, 82, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		table = new JTable(model);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setBounds(10, 11, 373, 238);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(10, 11, 595, 238);
		getContentPane().add(jsp);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.40));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.35));
		table.getColumnModel().getColumn(2).setPreferredWidth((int) (table.getWidth()*0.05));
		table.getColumnModel().getColumn(3).setPreferredWidth((int) (table.getWidth()*0.20));

		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            int width=c.getWidth(), heigth=c.getHeight();
	            
	    		jsp.setBounds(10, 11, width-33, heigth-40-23-15);

	    		btnCancel.setBounds(width-22-100, heigth-15-50, 100, 23);
	    		btnSpisat.setBounds(10, heigth-15-50, 82, 23);
	    		
	        }
		});
		
		initData();
	}
	
	public void initData(){

		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM spisannya";
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
				
				if(rsT.next()){
					employee = new Employee(rsT.getInt("id"), rsT.getString("fullname"));
				}
				
				stT.close();
				
				debits.add(new Debit(uid, pid, eid, count, datetime));
			    model.addRow(new Object[]{employee, product, count, debits.get(debits.size()-1)});
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
