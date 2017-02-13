package ua.rokytne.bakery.forms.newedit;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ua.rokytne.bakery.ImagePanel;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.Main.Role;
import ua.rokytne.bakery.orm.Ingridient;
import ua.rokytne.bakery.orm.User;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.Component;

public class NewUserForm extends JDialog {

	private JPanel contentPane;
	private JTextField tfUsername;
	private JTextField tfPassword;
	private JTextField tfFullName;
	private JComboBox cbRole;
	private JTextField tfPhoto;
	private JButton btnAdd;
	public User lastInsert;

	private String namePhoto;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewUserForm frame = new NewUserForm(false, null);
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
	public NewUserForm(boolean edMode, User user) {
		setResizable(false);

		this.setAlwaysOnTop(Main.onTop);
		if(!edMode)
			setTitle("Додавання нового користувача");
		else 
			setTitle("Редагування користувача");

		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setBounds((Main.WIDTH-384)/2, (Main.HEIGTH-197)/2, 384, 197);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Username:");
		lblNewLabel.setBounds(10, 11, 60, 14);
		contentPane.add(lblNewLabel);
		
		JLabel label = new JLabel("\u041F\u0430\u0440\u043E\u043B\u044C:");
		label.setBounds(10, 36, 60, 14);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("\u041F\u0406\u0411:");
		label_1.setBounds(10, 59, 60, 14);
		contentPane.add(label_1);
		
		JLabel label_2 = new JLabel("\u0424\u043E\u0442\u043E:");
		label_2.setBounds(10, 82, 60, 14);
		contentPane.add(label_2);
		
		tfUsername = new JTextField();
		tfUsername.setBounds(73, 8, 178, 20);
		contentPane.add(tfUsername);
		tfUsername.setColumns(10);
		
		tfPassword = new JTextField();
		tfPassword.setColumns(10);
		tfPassword.setBounds(73, 31, 178, 20);
		contentPane.add(tfPassword);
		
		tfFullName = new JTextField();
		tfFullName.setColumns(10);
		tfFullName.setBounds(73, 56, 178, 20);
		contentPane.add(tfFullName);
		
		tfPhoto = new JTextField();
		tfPhoto.setEditable(false);
		tfPhoto.setColumns(10);
		tfPhoto.setBounds(73, 79, 101, 20);
		contentPane.add(tfPhoto);
				
		if(!edMode)
			btnAdd = new JButton("Додати");
		else
			btnAdd = new JButton("Редагувати");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				lastInsert = null;
				if(tfFullName.getText().length()<3){
					JOptionPane.showMessageDialog(null, "Вкажіть будь ласка ім\'я!");
					return;
				}
				else if(tfUsername.getText().length()<3){
					JOptionPane.showMessageDialog(null, "Вкажіть будь ласка логін (4<символів)!");
					return;
				}
				else if(tfPassword.getText().length()<3){
					JOptionPane.showMessageDialog(null, "Вкажіть будь ласка пароль (4<символів)!");
					return;
				}
				else if(tfPhoto.getText().length()<1){
					JOptionPane.showMessageDialog(null, "Оберіть будь ласка фото!");
					return;
				}
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					if(!edMode){
						String query = " insert into User(username, password, RealName, photo, AccessLevel)" + 
										" values (?, ?, ?, ?, ?)";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setString (1, tfUsername.getText());
						preparedStmt.setString (2, tfPassword.getText());
						preparedStmt.setString (3, tfFullName.getText());
						preparedStmt.setString (4, namePhoto);
						
						// Копируем фото
						Main.copyFile(new File(tfPhoto.getText()), new File("photos/" + namePhoto));
						
						switch ((Role)cbRole.getSelectedItem()) {
						case SUPERADMIN:
							preparedStmt.setInt(5, 0);
							break;
						case EXPEDITOR:
							preparedStmt.setInt(5, 1);
							break;
						case PACKAGER:
							preparedStmt.setInt(5, 2);
							break;
						default:
							preparedStmt.setInt(5, 2);
							break;
						}
						preparedStmt.execute();
						
						query = "SELECT * FROM User WHERE ID = (SELECT MAX(ID) FROM User)";
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
					
						while (rs.next())
						{
						    int id = rs.getInt("id");
						    String username = rs.getString("username");
						    String password = rs.getString("password");
						    String realName = rs.getString("realName");
						    String photo = rs.getString("photo");
						    int al = rs.getInt("accesslevel");
						    
						    lastInsert = new User(id, username, password, realName, photo, al);
						}
						st.close();
					}
					else {
						String query = "UPDATE user SET UserName = ?, Password = ?, RealName = ?, Photo = ?, AccessLevel = ? where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);

						preparedStmt.setString(1, tfUsername.getText());
						preparedStmt.setString(2, tfPassword.getText());
						preparedStmt.setString(3, tfFullName.getText());
						preparedStmt.setString(4, namePhoto);
						
						if(!tfPhoto.getText().equals(user.getPhoto())) {
							// Якщо не рівні нову скопіювати. стару видалити....
							try{
								File delP = new File("photos/" + user.getPhoto());
								delP.delete();
							}
							catch(Exception ex){
								
							}
							Main.copyFile(new File(tfPhoto.getText()), new File("photos/" + namePhoto));
						}
						
						switch ((Role)cbRole.getSelectedItem()) {
						case SUPERADMIN:
							preparedStmt.setInt(5, 0);
							user.setAccessLevel(0);
							break;
						case EXPEDITOR:
							preparedStmt.setInt(5, 1);
							user.setAccessLevel(1);
							break;
						case PACKAGER:
							preparedStmt.setInt(5, 2);
							user.setAccessLevel(2);
							break;
						default:
							preparedStmt.setInt(5, 2);
							user.setAccessLevel(2);
							break;
						}
						preparedStmt.setInt(6, user.getId());
						preparedStmt.executeUpdate();

						user.setUsername(tfUsername.getText());
						user.setPassword(tfPassword.getText());
						user.setRealName(tfFullName.getText());
						user.setPhoto(namePhoto);
					}
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			
				setVisible(false);
				dispose();
			}
		});
		btnAdd.setBounds(10, 134, 89, 23);
		contentPane.add(btnAdd);
		
		JButton btnCancel = new JButton("Відмінити");
		btnCancel.setBounds(162, 137, 89, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		JLabel label_3 = new JLabel("\u0420\u0456\u0435\u043D\u044C \u0434\u043E\u0441\u0442\u0443\u043F\u0443:");
		label_3.setBounds(10, 109, 75, 14);
		contentPane.add(label_3);
		
		cbRole = new JComboBox();
		cbRole.setBounds(95, 106, 156, 20);
		contentPane.add(cbRole);

		cbRole.addItem(Role.EXPEDITOR);
		cbRole.addItem(Role.PACKAGER);
		cbRole.addItem(Role.SUPERADMIN);
		
		
		
		ImagePanel panelForPhoto = new ImagePanel();
		panelForPhoto.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelForPhoto.setBounds(261, 11, 107, 150);
		contentPane.add(panelForPhoto);
		
		if(edMode && user!=null){
			tfUsername.setText(user.getUsername());
			tfPassword.setText(user.getPassword());
			tfFullName.setText(user.getRealName());
			tfPhoto.setText(user.getPhoto());
			panelForPhoto.setPhoto("photos/" + user.getPhoto());
			
			if(user.getAccessLevel()==0)
				cbRole.setSelectedIndex(2);
			else if(user.getAccessLevel()==1)
				cbRole.setSelectedIndex(0);
			else if(user.getAccessLevel()==2)
				cbRole.setSelectedIndex(1);
		}
		
		JButton btnSelectPhoto = new JButton("Обрати");
		btnSelectPhoto.setBounds(179, 78, 72, 23);
		contentPane.add(btnSelectPhoto);
		btnSelectPhoto.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(){
					   @Override
					   protected JDialog createDialog(Component parent) throws HeadlessException {
					       // intercept the dialog created by JFileChooser
					       JDialog dialog = super.createDialog(parent);
					       dialog.setModal(true);  // set modality (or setModalityType)
					       dialog.setAlwaysOnTop(true);
					       return dialog;
					   }
					};
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "JPG & GIF Images", "jpg", "gif");
			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	
			    	if(new File("photos/" + chooser.getSelectedFile().getName()).exists()){
				    	namePhoto = chooser.getSelectedFile().getName() + ".jpg";
					}
					else{
				    	namePhoto = chooser.getSelectedFile().getName();
					}
			    	
			       tfPhoto.setText(chooser.getSelectedFile().getPath());
			       panelForPhoto.setPhoto(chooser.getSelectedFile().getPath());
			    }
			}
		});
	}
}
