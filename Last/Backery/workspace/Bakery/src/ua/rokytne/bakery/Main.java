package ua.rokytne.bakery;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ua.rokytne.bakery.forms.MainFrame;

public class Main {

	private static String[] products = new String[] 	{"Õë³á ïøåíè÷íèé á³ëèé 0,8êã.", "Õë³á ïøåíè÷íèé ôîðìîâèé 0,5",
														"ÕË²Á 0,7", "Õë³á äîìàøí³é 0,9",
														"Õë³á àðíàóò Êè¿âñüêèé 0,6", "õë³á àðíàóò ð³çàíèé 0,6",
														"Õë³á àðíàóò 0,6 ÁÀÁ", "ÕË²Á ÊÓÊÓÐÓÄÇßÍÈÉ",
														"ÇÄÎÐÎÂß", "Õë³á á³ëîðóñüêèé 0,5 ð³çàí",
														"Õë³á ð³âåíñüêèé êðóãëèé ð³çàíèé", "Õë³á Ð³âåíñüêèé  ÊÐÓÃ",
														"Õë³á Ð³âåíñüêèé ç êìèíîì 0,65 ð", "Õë³á Âèñ³âêîâèé 0,4",
														"Õë³á âèñ³âêîâèé 0,4 ð³çàí", "Áàãåò âèñ³âêîâèé 0,2",
														"Ð³æîê âèñ³âêîâèé 0,1", "Õë³á óêðà¿íñüêèé 0,9",
														"Õë³á óêðà¿íñüêèé 1,0(ôîðì)", "Õë³á Ä³àìàíò-øàìïàíü 0,4ð³ç",
														"Õë³á Êîëîñîê 0,8", "Õë³á Ìîçà¿êà 0,4ð³ç",
														"Áàòîí Äîðîæí³é 0,45", "Áàòîí Äîðîæí³é 0,45 ð³ç",
														"Ïàìïóøêà  0,550", "Ïàìïóøêà ç ÷àñíèêîì 0,3",
														"Ïëåò³íêà çäîáíà 0,45", "Ïëåò³íêà çäîáíà 0,65",
														"Êàëà÷ 0,4  ÌÈÊ", "Ð³æîê ç ïîâèäëîì 0,4",
														"Áóëêà Ñ³ìåéêà 0,4", "Çàãîòîâêà äëÿ ï³öè 0,150",
														"Õë³á æèòí ïøåíè÷íèé 0,45", "Õë³á çàâàðíèé 0,7",
														"Õë³á çàâàðíèé 0,45", "Áóëî÷êà çâè÷àéíà 0,1",
														"Áóëî÷êà ç ïîâèäëîì ,0,1", "Áóëî÷êà ç ìàêîì 0,1",
														"Ïëþøêà 0,1", "Ïëþøêà ôîðìîâà 0,4",
														"Áóáëèê ç ìàêîì 0,1", "Âàòðóøêè 0,075", "ÂÀÒÐÓØÊÀ 0,100",
														"Ëàïêè", "ÊÎÊÎÑ", "Ïèð³æêè 0,1 êàï,ÿá,ìàê.êàðò",
														"Ïèð³æêè ç ìàêîì 0,075", "Ïèð³æêè ç  êàïóñòîþ 0,075",
														"ÏÈÐ²ÆÊÈ ç ÿáëóêàìè 0,075", "Ïèðîãè ç ìàêîì 0,45",
														"ÏÈÐÎÃÈ Ç ïîâèäëîì 0,45", "ÊÐÅÍÄÅËÜ ÌÀÊ", "Êðåíäåëü  âèøíÿ",
														"ÏÈÐ²Ã  ÒÂÎÐÎÃ+ÀÁÐÈÊÎÑ", "ÌÀËÜÂÀ 0,400", "ÏÈÐ²ÆÊÈ Ó ÔÎÐÌ²",
														"ÊÅÊÑ 0,075", "ÊÅÊÑ Ë²ÒÎ ", "ÂÀÃÎÂÀ",
														"Ï³öà ç êîâáàñîþ 0,120", "ÁÀÃÅÒ 0,3",
														"ØÈØÊÈ", "ÇÀÂÈÒÎÊ", "ÑÎËÎÍ² ÏÀË ",
														"Ì'ßÑÎ", "ÊÎÒËÅÒÀ", "Áóëî÷êà ç ÿáëóêîì ³êîðèöåþ",
														"ÑËÈÂÀ", "ÂÈØÍß", "ÊÅÊÑ  1,0", "ÏÎÄ²ËÜÑÜÊÀ",
														"ÌÀËßÒÊÎ  ÏÎ  9 ØÒ", "ÊÎÒËÅÒÀ ÌÀËÅÍÜÊÀ", "ßêèéñü íîâèé òîâàð"};
	private static String[] mounths = new String[]	{"ñ³÷åíü", "ëþòèé", "áåðåçåíü", "êâ³òåíü", 
													"òðàâåíü", "÷åðâåíü", "ëèïåíü", "ñåðïåíü", 
													"âåðåñåíü", "æîâòåíü", "ëèñòîïàä", "ãðóäåíü"};	
	
	public static String DBWAY = "jdbc:mysql://localhost:3306/bakery";
	public static String DBLOGIN = "root";
	public static String DBPASSWORD = "1111";

	public static DataBase DATABASE = null;

	public static int HEIGTH=1024, WIDTH=1280;
	public static boolean onTop = false;
	public static MainFrame mainFrame = null;
	

	public static int FONT_SIZE = 10;
	
	public static int WIDTH_1 = 700;
	public static int WIDTH_2 = 5700;
	public static int WIDTH_3 = 800;
	public static int WIDTH_4 = 500;
	public static int WIDTH_5 = 2550;
	public static int WIDTH_6 = 1700;
	

	public static SimpleDateFormat dfDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
	
	public enum Role { 
		SUPERADMIN {
			@Override
			public String toString(){
				return "Ñóïåðàäì³í";
			}
		}, 
		EXPEDITOR {
			@Override
			public String toString(){
				return "Åêñïåäèòîð";
			}
		},  
		PACKAGER {
			@Override
			public String toString(){
				return "Ïàêóâàëüíèê";
			}
		};
	}
	
	public static void expandAll(JTree tree) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root));
	}
	 
	public static void expandAll(JTree tree, TreePath parent) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path);
			}
		}
		tree.expandPath(parent);
	}
	
	public static void expandAll(JTree tree, DefaultMutableTreeNode model) {
		tree.expandPath(new TreePath(model));
	}
	
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
    
    public static ImageIcon getScaledIcon(final Image image, final double scale)
    {
        ImageIcon scaledIcon = new ImageIcon(image)
        {
            public int getIconWidth()
            {
                return (int)(image.getWidth(null) * scale);
            }
  
            public int getIconHeight()
            {
                return (int)(image.getHeight(null) * scale);
            }
  
            public void paintIcon(Component c, Graphics g, int x, int y)
            {
                g.drawImage(image, x, y, getIconWidth(), getIconHeight(), c);
            }
        };
        return scaledIcon;
    }
	
	public static void main(String[] args) {
		//insertProductions();
	}

	public static void loadConfig(){
		File file = new File("configurations.config");
		boolean read = true;
		
		if(file.exists()){
			try{
				String dbname="", host="", port="", login="", password="";
				
				for (String line : Files.readAllLines(Paths.get("configurations.config"))) {
					String s[] = line.split(" ");
					
					switch (s[0]) {
					case "MySQL_server_database:":
						dbname = s[1];
						break;
					case "MySQL_server_host:":
						host = s[1];
						break;
					case "MySQL_server_port:":
						port = s[1];
						break;
					case "MySQL_server_login:":
						login = s[1];
						break;
					case "MySQL_server_password:":
						password = s[1];
						break;
					case "WINDOW_ON_TOP:":
						if(s[1].toLowerCase().equals("true"))
							onTop = true;
						else
							onTop = false;
						break;
					case "FONT_SIZE:":
						FONT_SIZE = Integer.parseInt(s[1]);
						break;
					case "WIDTH_1:":
						WIDTH_1 = Integer.parseInt(s[1]);
						break;
					case "WIDTH_2:":
						WIDTH_2 = Integer.parseInt(s[1]);
						break;
					case "WIDTH_3:":
						WIDTH_3 = Integer.parseInt(s[1]);
						break;
					case "WIDTH_4:":
						WIDTH_4 = Integer.parseInt(s[1]);
						break;
					case "WIDTH_5:":
						WIDTH_5 = Integer.parseInt(s[1]);
						break;
					case "WIDTH_6:":
						WIDTH_6 = Integer.parseInt(s[1]);
						break;
					}
				}
				
				DBWAY = "jdbc:mysql://" + host + ":" + port + "/" + dbname;
				DBLOGIN = login;
				DBPASSWORD = password;
				
			} catch(Exception ex){
				read = false;
				ex.printStackTrace();
			}
		}
		
		if(!file.exists() || !read){
			DBWAY = "jdbc:mysql://localhost:3306/bakery";
			DBLOGIN = "root";
			DBPASSWORD = "1111";
		}
		
	}
	
	public static void setUIFont(FontUIResource f) {
		Enumeration keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	    	Object key = keys.nextElement();
	    	Object value = UIManager.get (key);
	    	
	    	if (value != null && value instanceof javax.swing.plaf.FontUIResource)
	    		UIManager.put (key, f);
      	}
	}
	
	public static String getMounth(int i) {
		return mounths[i-1];
	}
	
	public static int okcancel(String theMessage) {
		int result = JOptionPane.showConfirmDialog((Component) null, theMessage, "", JOptionPane.OK_CANCEL_OPTION);
	    return result;
	}
	
	public static boolean isNumeric(String str)
	{
		try {
			 float d = Float.parseFloat(str);
		 }
		 catch(NumberFormatException nfe) {
			 return false;
		 }
		 return true;
	}

	public static void insertLog(String message){		 
		 try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			
			String query = " INSERT INTO LOG(DateTime, Action, User_Id)" + " values (?, ?, ?)";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, dfDateAndTime.format(new Date()));
			preparedStmt.setString(2, message);
			preparedStmt.setInt(3, LoggedUser.ID);
			preparedStmt.execute();
			conn.close();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void copyFile(File source, File dest) throws IOException {
		 
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        
	        while ((length = is.read(buffer)) > 0)
	            os.write(buffer, 0, length);
	        
	        is.close();
	        os.close();
	    } catch (Exception ex){
	    	ex.printStackTrace();
	    }
}
	 
	private static void insertProductions(){
		 try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = null;
			conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
			String query = " INSERT INTO Production(Name, CountOnStorage)" + " values (?, ?)";
			
			for(String s : products){
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, s);
				preparedStmt.setFloat(2, 0.0f);
				preparedStmt.execute();
			}
			
			conn.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
