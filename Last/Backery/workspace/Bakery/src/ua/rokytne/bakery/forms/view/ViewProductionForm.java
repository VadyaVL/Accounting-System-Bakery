package ua.rokytne.bakery.forms.view;

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
import javax.swing.table.DefaultTableModel;

import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.forms.newedit.NewProductionForm;
import ua.rokytne.bakery.orm.Employee;
import ua.rokytne.bakery.orm.Production;

import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import java.awt.Color;
import java.awt.Component;

public class ViewProductionForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	
	private ArrayList<Production> productions = new ArrayList<Production>();
	Object[] columnNames = {"Назва", "Наявно"};
	Object[][] data = {};
	DefaultTableModel model;
	private JTable table;
	private JButton btnEditProduction;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewProductionForm frame = new ViewProductionForm();
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
	public ViewProductionForm() {
		model = new DefaultTableModel(data, columnNames) {

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		setTitle("Продукція");
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setBounds((Main.WIDTH-504)/2, (Main.HEIGTH-269)/2, 504, 269);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(382, 203, 100, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		JButton btnDeleteProduction = new JButton("Видалити");
		btnDeleteProduction.setEnabled(false);
		btnDeleteProduction.setBackground(Color.RED);
		btnDeleteProduction.setBounds(382, 79, 100, 23);
		contentPane.add(btnDeleteProduction);
		btnDeleteProduction.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			    
				Main.insertLog("Спроба видалити продукцію (" + ((Production)model.getValueAt(table.getSelectedRow(), 0)).getName() + ")");
								
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
					Production toDel = (Production)model.getValueAt(table.getSelectedRow(), 0);
					
					int i = Main.okcancel("Дійсно видалити: " + toDel.getName() + "?");
				    
					if(i==0){
						
						
						
						// Для початку видалити зв'язки
						String queryRemove = "delete from production_has_ingridient where production_id = ?";
						PreparedStatement preparedStmtRemove = conn.prepareStatement(queryRemove);
						preparedStmtRemove.setInt(1, toDel.getID());
						preparedStmtRemove.execute();
						
						queryRemove = "delete from product_price where production_id = ?";
						preparedStmtRemove = conn.prepareStatement(queryRemove);
						preparedStmtRemove.setInt(1, toDel.getID());
						preparedStmtRemove.execute();
						
						
						String query = "delete from production where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt(1, toDel.getID());
						// execute the preparedstatement
						preparedStmt.execute();
						
						model.removeRow(table.getSelectedRow());
						productions.remove(toDel);
						
						
						if(btnDeleteProduction.isEnabled())
							btnDeleteProduction.setEnabled(false);

						if(btnEditProduction.isEnabled())
							btnEditProduction.setEnabled(false);
					}
					conn.close();
				}
				catch(Exception ex){
					
				}
			}
		});
		
		btnEditProduction = new JButton("Редагувати");
		btnEditProduction.setEnabled(false);
		btnEditProduction.setBackground(Color.CYAN);
		btnEditProduction.setBounds(382, 45, 100, 23);
		contentPane.add(btnEditProduction);
		btnEditProduction.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Production toEdit = (Production)model.getValueAt(table.getSelectedRow(), 0);
				NewProductionForm frame = new NewProductionForm(true, toEdit);
				frame.setModal(true);
				frame.setVisible(true);
				
				// ОБновить
				model.setValueAt(toEdit, table.getSelectedRow(), 0);
				model.setValueAt(toEdit.getCountOnStorage(), table.getSelectedRow(), 1);
			}
		});
		
		JButton btnAddProduction = new JButton("Додати");
		btnAddProduction.setBackground(Color.GREEN);
		btnAddProduction.setBounds(382, 11, 100, 23);
		contentPane.add(btnAddProduction);
		btnAddProduction.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NewProductionForm frame = new NewProductionForm(false, null);
				frame.setModal(true);
				frame.setVisible(true);
				
				if(frame.lastInsert!=null){
					productions.add(frame.lastInsert);
					model.addRow(new Object[]{productions.get(productions.size()-1), productions.get(productions.size()-1).getCountOnStorage()});
				}
				
			}
		});
		
		table = new JTable(model);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setBounds(10, 11, 277, 214);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(10, 11, 362, 214);
		getContentPane().add(jsp);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!btnDeleteProduction.isEnabled())
					btnDeleteProduction.setEnabled(true);

				if(!btnEditProduction.isEnabled())
					btnEditProduction.setEnabled(true);
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
		    		
		    		btnAddProduction.setBounds(width-20-100, 11, 100, 23);
		    		btnEditProduction.setBounds(width-20-100, 45, 100, 23);
		    		btnDeleteProduction.setBounds(width-20-100, 79, 100, 23);
		        }
		});
		
		initData();
	}
	
	public void initData(){

		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = "SELECT * FROM production";
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			  
			while (rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("name");
				float countOnStorage = rs.getFloat("CountOnStorage");
			    productions.add(new Production(id, name, countOnStorage));
			    model.addRow(new Object[]{productions.get(productions.size()-1), productions.get(productions.size()-1).getCountOnStorage()});
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
