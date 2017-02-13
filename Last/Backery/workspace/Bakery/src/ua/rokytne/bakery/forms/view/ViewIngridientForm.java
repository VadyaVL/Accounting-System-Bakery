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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.forms.newedit.NewEmployeeForm;
import ua.rokytne.bakery.forms.newedit.NewIngridientForm;
import ua.rokytne.bakery.orm.Employee;
import ua.rokytne.bakery.orm.Ingridient;
import ua.rokytne.bakery.orm.Production;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class ViewIngridientForm extends JDialog {

	private JPanel contentPane;
	private JTable table;
	private JButton btnDeleteIngridient, btnEditIngridient;
	private ArrayList<Ingridient> ingridients = new ArrayList<Ingridient>();
	Object[] columnNames = {"Назва", "Наявно"};
	Object[][] data = {};
	DefaultTableModel model;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewIngridientForm frame = new ViewIngridientForm();
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
	public ViewIngridientForm() {
		
		model = new DefaultTableModel(data, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Інгрідієнти - сировинний ресурс");
		setBounds((Main.WIDTH-528)/2, (Main.HEIGTH-273)/2, 528, 273);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(406, 203, 100, 23);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnCancel);
		
		btnDeleteIngridient = new JButton("Видалити");
		btnDeleteIngridient.setEnabled(false);
		btnDeleteIngridient.setBackground(Color.RED);
		btnDeleteIngridient.setBounds(406, 79, 100, 23);
		contentPane.add(btnDeleteIngridient);
		btnDeleteIngridient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Main.insertLog("Спроба видалити сировину (" + ((Ingridient)model.getValueAt(table.getSelectedRow(), 0)).getName() + ")");
				
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
					Ingridient toDel = (Ingridient)model.getValueAt(table.getSelectedRow(), 0);
					
					int i = Main.okcancel("Дійсно видалити: " + toDel.getName() + "?");
				    
					if(i==0){
						String query = "delete from ingridient where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt(1, toDel.getID());
						// execute the preparedstatement
						preparedStmt.execute();
						model.removeRow(table.getSelectedRow());
						ingridients.remove(toDel);
						
						if(btnDeleteIngridient.isEnabled())
							btnDeleteIngridient.setEnabled(false);

						if(btnEditIngridient.isEnabled())
							btnEditIngridient.setEnabled(false);
					}
					conn.close();
				}
				catch(Exception ex){
					
				}
			}
		});
		
		btnEditIngridient = new JButton("Редагувати");
		btnEditIngridient.setBackground(Color.CYAN);
		btnEditIngridient.setEnabled(false);
		btnEditIngridient.setForeground(Color.BLACK);
		btnEditIngridient.setBounds(406, 45, 100, 23);
		contentPane.add(btnEditIngridient);
		btnEditIngridient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Ingridient toEdit = (Ingridient)model.getValueAt(table.getSelectedRow(), 0);
				NewIngridientForm frame = new NewIngridientForm(true, toEdit);
				frame.setModal(true);
				frame.setVisible(true);
				
				// ОБновить
				model.setValueAt(toEdit.getCountOnStorage(), table.getSelectedRow(), 1);
				model.setValueAt(toEdit, table.getSelectedRow(), 0);
			}
		});
		
		JButton btnAddIngridient = new JButton("Додати");
		btnAddIngridient.setBackground(Color.GREEN);
		btnAddIngridient.setForeground(Color.BLACK);
		btnAddIngridient.setBounds(406, 11, 100, 23);
		contentPane.add(btnAddIngridient);
		btnAddIngridient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NewIngridientForm frame = new NewIngridientForm(false, null);
				frame.setModal(true);
				frame.setVisible(true);
				
				if(frame.lastInsert!=null){
					ingridients.add(frame.lastInsert);
					model.addRow(new Object[]{ingridients.get(ingridients.size()-1), ingridients.get(ingridients.size()-1).getCountOnStorage()});
				}
				
			}
		});
		
		table = new JTable(model);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setBounds(10, 11, 303, 216);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(10, 11, 386, 216);
		getContentPane().add(jsp);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//System.out.println(table.getValueAt(table.getSelectedRow(), 0));
				if(!btnDeleteIngridient.isEnabled())
					btnDeleteIngridient.setEnabled(true);

				if(!btnEditIngridient.isEnabled())
					btnEditIngridient.setEnabled(true);
			}
		});
		
		
		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.8));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.2));
		
		addComponentListener(new ComponentAdapter() 
		{  
		        public void componentResized(ComponentEvent evt) {
		            Component c = (Component)evt.getSource();
		            int width=c.getWidth(), heigth=c.getHeight();
		            
		    		jsp.setBounds(10, 11, width-100-15-20, heigth-40-10-5);


		    		btnCancel.setBounds(width-20-100, 203, 100, 23);
		    		
		    		btnAddIngridient.setBounds(width-20-100, 11, 100, 23);
		    		btnEditIngridient.setBounds(width-20-100, 45, 100, 23);
		    		btnDeleteIngridient.setBounds(width-20-100, 79, 100, 23);
		        }
		});
		
		initData();
	}
	
	public void initData(){

		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM ingridient";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				float countOnStorage = rs.getFloat("CountOnStorage");
			    ingridients.add(new Ingridient(id, name, countOnStorage));
			    model.addRow(new Object[]{ingridients.get(ingridients.size()-1), ingridients.get(ingridients.size()-1).getCountOnStorage()});
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
