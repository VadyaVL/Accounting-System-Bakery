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
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import ua.rokytne.bakery.DoubleJTextField;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.Ingridient;
import ua.rokytne.bakery.orm.Production;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;

public class NewProductionForm extends JDialog {

	private JPanel contentPane;
	private JTextField tfName;
	private DoubleJTextField tfCount;
	private JTable table;
	private JButton btnAdd;
	private ArrayList<Ingridient> ingridients = new ArrayList<Ingridient>();
	Object[] columnNames = {"Сировина", "Необхідно"};
	Object[][] data = {};
	DefaultTableModel model = new DefaultTableModel(data, columnNames);
	
	public Production lastInsert;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewProductionForm frame = new NewProductionForm(false, null);
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
	public NewProductionForm(boolean edMode, Production prod) {
		model = new DefaultTableModel(data, columnNames) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		    	if(column==1)
		    		return true;
		       return false;
		    }
		};

		this.setAlwaysOnTop(Main.onTop);
		if(!edMode)
			setTitle("Додавання нової продукції");
		else
			setTitle("Редагування продукції");

		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setBounds((Main.WIDTH-353)/2, (Main.HEIGTH-270)/2, 353, 270);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		if(!edMode)
			btnAdd = new JButton("Додати");
		else
			btnAdd = new JButton("Редагувати");
			
		btnAdd.setBounds(10, 204, 120, 23);
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
						String query = " insert into production(name, countOnStorage)" + " values (?, ?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString (1, tfName.getText());
						preparedStmt.setFloat(2, Float.parseFloat(tfCount.getText()));
						preparedStmt.execute();
						query = "SELECT * FROM production WHERE ID = (SELECT MAX(ID) FROM production)";
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
					
						if (rs.next())
						{
						    int id = rs.getInt("id");
						    String fullName = rs.getString("name");
						    float count = rs.getFloat("countOnStorage");
						    lastInsert = new Production(id, fullName, count);
						}

						query = " insert into production_has_ingridient(production_id, ingridient_id, count)" + " values (?, ?, ?)";
						for(int i=0; i<table.getRowCount(); i++){
							preparedStmt = conn.prepareStatement(query);
							preparedStmt.setInt (1, lastInsert.getID());
							preparedStmt.setInt (2, ((Ingridient)table.getValueAt(i, 0)).getID());
							table.setValueAt(table.getValueAt(i, 1).toString().replace(',', '.'), i, 1);
							
							if(Main.isNumeric(table.getValueAt(i, 1).toString()))
								preparedStmt.setFloat(3, Float.parseFloat((table.getValueAt(i, 1).toString())));
							else
								preparedStmt.setFloat(3, 0.0f);
							preparedStmt.execute();
						}
						
						st.close();
					}
					else {
						String query = "UPDATE production SET name = ?, countOnStorage = ? where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString(1, tfName.getText());
						preparedStmt.setFloat(2, Float.parseFloat(tfCount.getText()));
						preparedStmt.setInt(3, prod.getID());
						preparedStmt.executeUpdate();
						
						prod.setName(tfName.getText());
						prod.setCountOnStorage(Float.parseFloat(tfCount.getText()));
						
						// Оновити інгрідієнти
						query = " UPDATE production_has_ingridient SET count = ? WHERE production_id = ? and ingridient_id = ?";
						for(int i=0; i<table.getRowCount(); i++){
							preparedStmt = conn.prepareStatement(query);
							preparedStmt.setFloat (1,  Float.parseFloat((table.getValueAt(i, 1).toString())));
							preparedStmt.setInt (2, prod.getID());
							preparedStmt.setInt (3, ((Ingridient)table.getValueAt(i, 0)).getID());
							preparedStmt.execute();
						}
					}
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			
				if(!edMode)
					Main.insertLog("Додавання продукції " + lastInsert.getName());
				else
					Main.insertLog("Редагування продукції " + prod.getName());
				
				setVisible(false);
				dispose();

			}
		});
		
		JLabel label = new JLabel("\u041D\u0430\u0437\u0432\u0430:");
		label.setBounds(10, 14, 119, 14);
		contentPane.add(label);
		
		tfName = new JTextField();
		tfName.setColumns(10);
		tfName.setBounds(82, 11, 250, 20);
		contentPane.add(tfName);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(244, 207, 89, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		JLabel label_1 = new JLabel("\u0412\u0436\u0435 \u043D\u0430\u044F\u0432\u043D\u043E:");
		label_1.setBounds(10, 42, 63, 14);
		contentPane.add(label_1);
		
		tfCount = new DoubleJTextField();
		tfCount.setText("");
		tfCount.setBounds(83, 39, 71, 20);
		contentPane.add(tfCount);
		tfCount.setColumns(10);
		
		table = new JTable(model);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setBounds(10, 67, 320, 132);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(10, 67, 320, 132);
		getContentPane().add(jsp);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		table.getColumnModel().getColumn(0).setPreferredWidth((int) (table.getWidth()*0.8));
		table.getColumnModel().getColumn(1).setPreferredWidth((int) (table.getWidth()*0.2));

		if(edMode && prod!=null){
			tfName.setText(prod.getName());
			tfCount.setText(prod.getCountOnStorage() + "");
		}
		
		addComponentListener(new ComponentAdapter() 
		{  
	        public void componentResized(ComponentEvent evt) {
	            Component c = (Component)evt.getSource();
	            int width=c.getWidth(), heigth=c.getHeight();
	            tfName.setBounds(78, 8, width-45-97+40, 20);
	    		jsp.setBounds(10, 67, width-10-20-3, heigth-100-10-23);
	    		btnCancel.setBounds(width-15-89-5-3, heigth-40-2-24, 89, 23);
	    		btnAdd.setBounds(11, heigth-40-2-24, 89, 23);
	    		
	        }
		});
		
		initData(edMode, prod);
	}
		
	public void initData(boolean edMode, Production prod){

		
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
			    
			    if(!edMode)
			    	model.addRow(new Object[]{ingridients.get(ingridients.size()-1), ""});
			    else if(edMode && prod!=null){
			    	// Отримати prod_has_ing
			    	// Якщо немає то додати і заповнити нулем..
			    	// Якщо є, то просто ініціалізувати змінну по кількості
			    	String queryPHI = "SELECT * FROM production_has_ingridient WHERE production_id = " + 
			    	prod.getID() +" and ingridient_id = " + ingridients.get(ingridients.size()-1).getID();
					
					Statement stPHI = conn.createStatement();
					ResultSet rsPHI = stPHI.executeQuery(queryPHI);
					
					if(rsPHI.next()){ // Якщо є
				    	model.addRow(new Object[]{ingridients.get(ingridients.size()-1), rsPHI.getFloat("count")});
					}
					else{ // Якщо нема
						query = " insert into production_has_ingridient(production_id, ingridient_id, count)" + " values (?, ?, ?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt (1, prod.getID());
						preparedStmt.setInt (2, ingridients.get(ingridients.size()-1).getID());
						preparedStmt.setFloat(3, 0.0f);
						preparedStmt.execute();
				    	model.addRow(new Object[]{ingridients.get(ingridients.size()-1), 0.0f});
					}
					
					stPHI.close();
			    }
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
