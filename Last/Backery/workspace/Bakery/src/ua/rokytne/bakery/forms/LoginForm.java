package ua.rokytne.bakery.forms;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPasswordField;

import ua.rokytne.bakery.LoggedUser;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.orm.User;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.Color;

public class LoginForm extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField tfUSERNAME;
	private JTextField tfPassword;
	private Point initialClick;
	private JButton btnIN, btnCancel;
	private MyKeyListener listener = new MyKeyListener();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LoginForm dialog = new LoginForm();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public LoginForm() {
		getContentPane().setBackground(Color.ORANGE);
		this.setUndecorated(true);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		this.setAlwaysOnTop(Main.onTop);
		setTitle("\u0412\u0445\u0456\u0434");
		setResizable(false);
		setBounds((Main.WIDTH-204)/2, (Main.HEIGTH-93)/2, 204, 93);
		getContentPane().setLayout(null);
		

		
		addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	            initialClick = e.getPoint();
	            getComponentAt(initialClick);
	        }
	    });

	    addMouseMotionListener(new MouseMotionAdapter() {
	        @Override
	        public void mouseDragged(MouseEvent e) {

	            // get location of Window
	            int thisX = getLocation().x;
	            int thisY = getLocation().y;

	            // Determine how much the mouse moved since the initial click
	            int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
	            int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

	            // Move window to this position
	            int X = thisX + xMoved;
	            int Y = thisY + yMoved;
	            setLocation(X, Y);
	        }
	    });
		
		
		
		JLabel lblNewLabel = new JLabel("\u041B\u043E\u0433\u0456\u043D:");
		lblNewLabel.setBounds(10, 11, 46, 14);
		getContentPane().add(lblNewLabel);
		
		JLabel label = new JLabel("\u041F\u0430\u0440\u043E\u043B\u044C:");
		label.setBounds(10, 40, 46, 14);
		getContentPane().add(label);
		
		tfUSERNAME = new JTextField();
		tfUSERNAME.setBounds(56, 8, 140, 20);
		getContentPane().add(tfUSERNAME);
		tfUSERNAME.setColumns(10);
		
		tfPassword = new JPasswordField();
		tfPassword.setBounds(56, 36, 140, 20);
		getContentPane().add(tfPassword);
		tfPassword.setColumns(10);
		
		btnIN = new JButton("\u0423\u0432\u0456\u0439\u0442\u0438");
		btnIN.setBackground(Color.GREEN);
		btnIN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "SELECT * FROM User WHERE password = \"" + tfPassword.getText() + "\" and username = \"" + tfUSERNAME.getText() + "\"";
					
					Statement st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
					  
					if (rs.next())
					{
						int id = rs.getInt("id");
						String username = rs.getString("username");
						String password = rs.getString("password");
						String realName = rs.getString("realname");
						String photo = rs.getString("photo");
						int al = rs.getInt("AccessLevel");
						
						
						if(LoggedUser.STATUS==0)
						    new LoggedUser(new User(id, username, password, realName, photo, al));
						if(LoggedUser.STATUS==2){
							if(LoggedUser.username.equals(username)){
								LoggedUser.STATUS=1;
								Main.mainFrame.enable();
								Main.insertLog("Повернувся");
							}
							else{
								JOptionPane.showMessageDialog(null, "Логін чи пароль уведено не коректно.");
								st.close();
								conn.close();
								return;
							}
						}
						
					  }
					else{
						JOptionPane.showMessageDialog(null, "Логін чи пароль уведено не коректно.");
						st.close();
						conn.close();
						return;
						
					}
					st.close();
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				setVisible(false);
				dispose();
			}
		});
		btnIN.setBounds(10, 65, 89, 23);
		getContentPane().add(btnIN);
		
		btnCancel = new JButton("\u0412\u0456\u0434\u043C\u0456\u043D\u0430");
		btnCancel.setBackground(Color.RED);
		btnCancel.setBounds(107, 65, 89, 23);
			
		
		if(LoggedUser.STATUS==2)
			btnCancel.setVisible(false);
		
		getContentPane().add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});

		tfPassword.addKeyListener(listener);
		tfUSERNAME.addKeyListener(listener);
		btnIN.addKeyListener(listener);
		btnCancel.addKeyListener(listener);
	}
	
	class MyKeyListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
            	btnIN.doClick();
            }
            else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            	btnCancel.doClick();
            }
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent e) {
            
         }
	}
	
}
