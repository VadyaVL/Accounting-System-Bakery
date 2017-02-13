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

import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.forms.newedit.NewPlaceForm;
import ua.rokytne.bakery.orm.Client;
import ua.rokytne.bakery.orm.Place;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.Color;
import java.awt.Component;

public class ViewPlaceForm extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ArrayList<Place> places = new ArrayList<Place>();
	private DefaultListModel model = new DefaultListModel();
	private JButton btnEditPlace, btnDeletePlace;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewPlaceForm frame = new ViewPlaceForm();
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
	public ViewPlaceForm() {
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Населені пункти");
		setBounds((Main.WIDTH-454)/2, (Main.HEIGTH-274)/2, 454, 274);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JList list = new JList(model);
		list.setBounds(10, 11, 311, 217);
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!btnDeletePlace.isEnabled())
					btnDeletePlace.setEnabled(true);

				if(!btnEditPlace.isEnabled())
					btnEditPlace.setEnabled(true);
			}
		});
		JScrollPane jsp = new JScrollPane(list);
		jsp.setBounds(10, 11, 311, 214);
		contentPane.add(jsp);
		
		JButton btnAddPlace = new JButton("Додати");
		btnAddPlace.setBackground(Color.GREEN);
		btnAddPlace.setBounds(331, 11, 100, 23);
		btnAddPlace.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NewPlaceForm frame = new NewPlaceForm(false, null);
				frame.setModal(true);
				frame.setVisible(true);
				
				if(frame.lastInsert!=null){
					places.add(frame.lastInsert);
					model.addElement(places.get(places.size()-1));
					list.setSelectedValue(places.get(places.size()-1), true);
				}
			}
		});
		contentPane.add(btnAddPlace);
		
		btnEditPlace = new JButton("Редагувати");
		btnEditPlace.setEnabled(false);
		btnEditPlace.setBackground(Color.CYAN);
		btnEditPlace.setBounds(331, 45, 100, 23);
		btnEditPlace.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Place toEdit = (Place)model.getElementAt(list.getSelectedIndex());
				NewPlaceForm frame = new NewPlaceForm(true, toEdit);
				frame.setModal(true);
				frame.setVisible(true);
				int pos = model.indexOf(toEdit);
				model.removeElement(toEdit);
				model.add(pos, toEdit);
				list.setSelectedIndex(pos);
			}
		});
		contentPane.add(btnEditPlace);
		
		btnDeletePlace = new JButton("Видалити");
		btnDeletePlace.setEnabled(false);
		btnDeletePlace.setBackground(Color.RED);
		btnDeletePlace.setBounds(331, 79, 100, 23);
		btnDeletePlace.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Main.insertLog("Спроба видалити н.п. (" + ((Place)model.getElementAt(list.getSelectedIndex())).getName() + ")");
								
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
					Place toDel = (Place)model.getElementAt(list.getSelectedIndex());
					
					int i = Main.okcancel("Дійсно видалити: " + toDel.getName() + "?");
				    
					if(i==0){
						String query = "delete from place where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt(1, toDel.getID());
						// execute the preparedstatement
						preparedStmt.execute();
						model.removeElement(toDel);
						places.remove(toDel);
						
						if(btnDeletePlace.isEnabled())
							btnDeletePlace.setEnabled(false);

						if(btnEditPlace.isEnabled())
							btnEditPlace.setEnabled(false);
					}
					conn.close();
				}
				catch(Exception ex){
					
				}
			}
		});
		contentPane.add(btnDeletePlace);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(331, 203, 100, 23);
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
		            int width=c.getWidth(), heigth=c.getHeight();
		            
		    		jsp.setBounds(10, 11, width-100-15-20, heigth-40-10-5);

		    		btnCancel.setBounds(width-20-100, 203, 100, 23);
		    		
		    		btnAddPlace.setBounds(width-20-100, 11, 100, 23);
		    		btnEditPlace.setBounds(width-20-100, 45, 100, 23);
		    		btnDeletePlace.setBounds(width-20-100, 79, 100, 23);
		        }
		});
		
		initData();
	}
	
	public void initData(){
		
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
			    model.addElement(places.get(places.size()-1));
		    }
		  st.close();
			
			conn.close();
		}
		catch(Exception ex){
			
		}
	}
}
