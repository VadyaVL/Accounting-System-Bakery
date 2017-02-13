package ua.rokytne.bakery.forms;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import ua.rokytne.bakery.DataBase;
import ua.rokytne.bakery.LoggedUser;
import ua.rokytne.bakery.Main;
import ua.rokytne.bakery.forms.newedit.NewRequestForm;
import ua.rokytne.bakery.forms.view.ViewTheBalanceOfThePreviousChangesForm;
import ua.rokytne.bakery.forms.view.ViewClientForm;
import ua.rokytne.bakery.forms.view.ViewDebitForm;
import ua.rokytne.bakery.forms.view.ViewEmployeeForm;
import ua.rokytne.bakery.forms.view.ViewIngridientForm;
import ua.rokytne.bakery.forms.view.ViewPlaceForm;
import ua.rokytne.bakery.forms.view.ViewProductionForm;
import ua.rokytne.bakery.forms.view.ViewReportTeamForm;
import ua.rokytne.bakery.forms.view.ViewUserForm;
import ua.rokytne.bakery.orm.Client;
import ua.rokytne.bakery.orm.OldNewReport;
import ua.rokytne.bakery.orm.Place;
import ua.rokytne.bakery.orm.Production;
import ua.rokytne.bakery.orm.Request;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int heigth, width;
	
	private static MyTimeUpdater mtu;
	
	private JPanel contentPane;
	private JButton btnAdd, btnEdit, btnDelete, btnPrintNakladna, btnView;;
	private JMenuBar menuBar;
	private JMenu menuFile, menuEdit, menuReport, menu;
	private JMenuItem miExit, miPrintDate, miUpdReq, miPrintNakladnaByWay, miProduct, miClient, miReport, miSpisannya, miPlace, miIngridient, miUser, miEmployee;
	private JPanel statusPanel;
	public JLabel labelLoginAs, labelTime;
	private JMenuItem menuItem_1;
	private JMenuItem menuItem_3;
	private JMenuItem menuItem_4;
	private JMenuItem menuItem_5;
	private JMenuItem menuItem_6;
	private JMenuItem menuItem_7;
	private JMenuItem menuItem_2;
	private JMenuItem menuItem;
	private Point initialClick;
	private JButton btnPrint = null;
	private JButton btnZalishok = null;

	private JTree tree;
	private JLabel labelPath;
	private JLabel label;
	private JMenuItem miSetting;
	public JTable productionTable;
	private ArrayList<Production> productions = new ArrayList<Production>();
	Object[] columnNames = {"Назва", "Наявно"};
	Object[][] data = {};
	DefaultTableModel model;
	JScrollPane jspP;
	private JMenuItem menuItem_8;
	
	
	/**
	 * Повидаляти в кінці.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
					Main.mainFrame = frame;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public JTree getTreeProd(){
		return this.tree;
	}

	public void setNewSettings(){
		this.setAlwaysOnTop(Main.onTop);
		this.setResizable(!Main.onTop);
		if(Main.onTop){
			this.setBounds(0,0,Main.WIDTH, Main.HEIGTH);
		}
	}
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		
		model = new DefaultTableModel(data, columnNames) {
		    
			private static final long serialVersionUID = 1L;

			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		
		Main.loadConfig();
		// тут підкліючаємося..
		// відключаємося при виході
		//Main.DATABASE = DataBase.getInstance();
		
		Dimension sSize = Toolkit.getDefaultToolkit ().getScreenSize ();

		Main.HEIGTH = sSize.height;
		Main.WIDTH = sSize.width;
		
		//heigth = sSize.height/2;
		//width = sSize.width/2+200;
		
		heigth = 500;
		width = 900;
		//heigth = sSize.height;
		//width = sSize.width;
		setMinimumSize(new Dimension(600, 300));
		// Розміри частини вікна з продукцією
		int p_x = 10, p_y = 32, p_w=2*155, p_h=heigth-60;
		//this.setUndecorated(true);
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.addWindowStateListener(new WindowStateListener(){
            
			@Override
			public void windowStateChanged(WindowEvent arg0) {
				// TODO Auto-generated method stub
				setState(JFrame.NORMAL);
			}
        });
		
		setTitle("\u0421\u0438\u0441\u0442\u0435\u043C\u0430 \u043E\u0431\u043B\u0456\u043A\u0443 - \"\u041F\u0435\u043A\u0430\u0440\u043D\u044F\"");
		setBounds(Main.WIDTH/2-(width+16)/2, Main.HEIGTH/2-(heigth+38)/2, width+16, heigth+38);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, width, 21);
		
		menuFile = new JMenu("Файл");
		menuEdit = new JMenu("Ресурси");
		menuReport = new JMenu("Звіти");
		
		
		
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
		
		miExit = new JMenuItem("Вихід");
		miExit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				setVisible(false);
				dispose();
				System.exit(0);
			}
		});
		
		miPrintNakladnaByWay = new JMenuItem("Друк накладної по маршруту");
		miPrintNakladnaByWay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PrintDayAndWayForm frame = new PrintDayAndWayForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		miProduct = new JMenuItem("Продукція");
		miProduct.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewProductionForm frame = new ViewProductionForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		miClient = new JMenuItem("Клієнти");
		miClient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewClientForm frame = new ViewClientForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		miPlace = new JMenuItem("Населені пункти");
		miPlace.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewPlaceForm frame = new ViewPlaceForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		miIngridient = new JMenuItem("Cировина");
		miIngridient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewIngridientForm frame = new ViewIngridientForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		miUser = new JMenuItem("Користувачі системи");
		miUser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewUserForm frame = new ViewUserForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		miEmployee = new JMenuItem("Робітники підприємства");
		miEmployee.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewEmployeeForm frame = new ViewEmployeeForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		
		miReport = new JMenuItem("Виробничі звіти");
		miReport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewReportTeamForm frame = new ViewReportTeamForm(false);
				frame.setModal(true);
				frame.setVisible(true);
				miUpdReq.doClick();
			}
		});
		
		miSpisannya = new JMenuItem("Списання");
		miSpisannya.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewDebitForm frame = new ViewDebitForm();
				frame.setModal(true);
				frame.setVisible(true);
				miUpdReq.doClick();
			}
		});
		contentPane.setLayout(null);


		// Додавання меню до форми
		contentPane.add(menuBar);
		
		// Додавання до головного меню - пунктів
		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuReport);
		
		// Додавання підпунктів до пунктів меню
		menuFile.add(miPrintNakladnaByWay);
		
		menuItem_3 = new JMenuItem("\u0414\u0440\u0443\u043A \u043F\u0440\u0430\u0439\u0441\u0443");
		menuItem_3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PriceOfPlaceForm frame = new PriceOfPlaceForm(true);
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		miPrintDate = new JMenuItem("\u0414\u0440\u0443\u043A \u043D\u0430\u043A\u043B\u0430\u0434\u043D\u043E\u0457 \u043F\u043E \u0447\u0438\u0441\u043B\u0443");
		menuFile.add(miPrintDate);
		
		miPrintDate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PrintDayForm frame = new PrintDayForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		menuFile.add(menuItem_3);
		
		menuItem_5 = new JMenuItem("\u0414\u0440\u0443\u043A \u0432\u0438\u0440\u043E\u0431\u043D\u0438\u0447\u043E\u0433\u043E \u0437\u0432\u0456\u0442\u0443");
		menuItem_5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewReportTeamForm frame = new ViewReportTeamForm(true);
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		menuFile.add(menuItem_5);
		menuFile.addSeparator();
		
		menuItem_1 = new JMenuItem("\u0423\u0432\u0456\u0439\u0442\u0438/\u041F\u0456\u0434\u043A\u043B\u044E\u0447\u0438\u0442\u0438\u0441\u044F");
		menuFile.add(menuItem_1);
		menuItem_1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LoginForm frame = new LoginForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		menuItem = new JMenuItem("\u0412\u0456\u0434\u043B\u0443\u0447\u0438\u0442\u0438\u0441\u044F");
		menuFile.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				LoggedUser.STATUS = 2;
				Main.insertLog("Відлучився");
				enable();
				LoginForm frame = new LoginForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		menuItem_2 = new JMenuItem("\u0417\u0430\u0432\u0435\u0440\u0448\u0438\u0442\u0438 \u0437\u043C\u0456\u043D\u0443");
		menuItem_2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LoggedUser.STATUS = 0;
				LoggedUser.name="";

				btnEdit.setEnabled(false);
				btnDelete.setEnabled(false);
				btnView.setEnabled(false);
				btnPrintNakladna.setEnabled(false);
				
				enable();
				Main.insertLog("Завершив зміну");


				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
				model.reload(root);
			}
		});
		menuFile.add(menuItem_2);
		menuFile.addSeparator();
		menuFile.add(miExit);
		menuEdit.addSeparator();
		menuEdit.add(miPlace);
		menuEdit.add(miClient);
		menuEdit.addSeparator();
		menuEdit.add(miIngridient);
		menuEdit.add(miProduct);
		menuEdit.addSeparator();
		
		menuItem_4 = new JMenuItem("\u0420\u0435\u0434\u0430\u0433\u0443\u0432\u0430\u043D\u043D\u044F \u0446\u0456\u043D");
		menuItem_4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PriceOfPlaceForm frame = new PriceOfPlaceForm(false);
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		menuEdit.add(menuItem_4);
		menuEdit.addSeparator();
		menuEdit.add(miEmployee);
		menuEdit.add(miUser);
		
		JMenuItem miTheRest = new JMenuItem("Залишок попередньої зміни");
		menuReport.add(miTheRest);
		miTheRest.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewTheBalanceOfThePreviousChangesForm frame = new ViewTheBalanceOfThePreviousChangesForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		
		menuReport.add(miReport);
		menuReport.add(miSpisannya);
		
		menuItem_6 = new JMenuItem("\u0417\u0432\u0456\u0442 \u043F\u043E \u0441\u0438\u0440\u043E\u0432\u0438\u043D\u0456");
		menuItem_6.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ReportPartOfProductForm frame = new ReportPartOfProductForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		menuReport.add(menuItem_6);
		
		menuItem_7 = new JMenuItem("\u0417\u0432\u0456\u0442 \u043F\u043E \u043A\u043B\u0456\u0454\u043D\u0442\u0430\u043C/\u043F\u0440\u043E\u0434\u0443\u043A\u0442\u0430\u043C");
		menuItem_7.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ReportForm frame = new ReportForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		menuReport.add(menuItem_7);
		
		
		menu = new JMenu("Допомога");
		menuBar.add(menu);
		
		miUpdReq = new JMenuItem("\u041E\u043D\u043E\u0432\u0438\u0442\u0438 \u0437\u0430\u044F\u0432\u043A\u0438");
		miUpdReq.setVisible(false);
		menu.add(miUpdReq);
		miUpdReq.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				/*tree.setModel(new DefaultTreeModel(
						new DefaultMutableTreeNode("Заявки") {
							
							private static final long serialVersionUID = 1L;

							{
							}
						}
					)); */
				//loadData();
				loadProductionData();

				tree.repaint();
				/*
				TreeNode expNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
				TreeNode[] nodes = ((DefaultMutableTreeNode) tree.getModel().getRoot()).getPath();
				int count = 0;
				for(int i=0;i<nodes.length;i++)
				{
					 DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) nodes[i];
					 count = count+expNode.getIndex(tempNode)+1;
					 tree.expandRow(count);
					 expNode = tempNode;
				}*/
				
			}
		});
		
		menuItem_8 = new JMenuItem("Оновити вигляд дерева заявок");
		menuItem_8.setToolTipText("При додаванні нової заявки числом, що є заднім для \r\n"
								+ "існуючої - порядок дат буде невірним. Дана опція слугує \r\n"
								+ "для оновлення дерева і відображення дат в хронології.");
		menuItem_8.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tree.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("Заявки") {
					
					private static final long serialVersionUID = 1L;

					{
					}
				}
				)); 
				loadData();
			
			
				TreeNode expNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
				TreeNode[] nodes = ((DefaultMutableTreeNode) tree.getModel().getRoot()).getPath();
				int count = 0;
				for(int i=0;i<nodes.length;i++)
				{
					 DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) nodes[i];
					 count = count+expNode.getIndex(tempNode)+1;
					 tree.expandRow(count);
					 expNode = tempNode;
				}
				tree.repaint();
			}
		});
		menu.add(menuItem_8);
		
		miSetting = new JMenuItem("Налаштування");
		menu.add(miSetting);

		miSetting.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsForm frame = new SettingsForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		
		JPanel requestPanel = new JPanel();
		requestPanel.setBorder(new TitledBorder(null, "Заявки", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		requestPanel.setBounds(322, 61, 420, 411);
		contentPane.add(requestPanel);
		requestPanel.setLayout(null);
		
		tree = new JTree();
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				try{
					// TODO Auto-generated method stub
					labelPath.setText("");
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
						if(selectedNode.getUserObject() instanceof Request){
							btnDelete.setEnabled(true);
							btnView.setEnabled(true);
							btnPrintNakladna.setEnabled(true);
							//btnPrint.setEnabled(true);
							btnEdit.setEnabled(true);
						}
						else{
							btnDelete.setEnabled(false);
							btnView.setEnabled(false);
							btnPrintNakladna.setEnabled(false);
							//btnPrint.setEnabled(false);
							btnEdit.setEnabled(false);
						}
					
					if(selectedNode.getParent()!=null){
						while(selectedNode.getParent().getParent()!=null){
							selectedNode = (DefaultMutableTreeNode) selectedNode.getParent();
							labelPath.setText(selectedNode.toString() + " " + labelPath.getText());
						}
					}
				}
				catch(Exception ex){
					
				}
			}
		});
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Заявки") {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
				}
			}
		));
		tree.setBounds(10, 21, width-15-165-p_w, heigth-70);
		//requestPanel.add(tree);
		
		//contentPane.add(table);
		JScrollPane jsp = new JScrollPane(tree);
		jsp.setBounds(10, 21, 400, 379);
		requestPanel.add(jsp);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Інструменти", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(740, 61, 155, 411);
		contentPane.add(panel);
		panel.setLayout(null);
		
		btnAdd = new JButton("Додати заявку");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NewRequestForm frame = new NewRequestForm(0, null);
				frame.setModal(true);
				frame.setVisible(true);
				
				if(frame.lastInsert==null)
					return;
				
				/////////////////////////////////////////////////////////////////////////////
				 DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
				 
				 try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
						
						Request rquest = frame.lastInsert;
						Client client = null;
						Place place = null;

						Statement stGET = conn.createStatement();
						String queryGetClient = "SELECT * FROM client WHERE id = " + rquest.getClientId();
						ResultSet rsGet = stGET.executeQuery(queryGetClient);
							
						if(rsGet.next()){
							int idC = rsGet.getInt("id");
							String name = rsGet.getString("name");
							int place_id = rsGet.getInt("place_id");
							client = new Client(idC, name, place_id);
						}
						rsGet.close();
							
						if(client!=null){
							String queryGetPlace = "SELECT * FROM place WHERE id = " + client.getPlaceID();
							rsGet = stGET.executeQuery(queryGetPlace);
							
							if(rsGet.next()){
								int idP = rsGet.getInt("id");
								String name = rsGet.getString("name");
								place = new Place(idP, name);
							}
						}
						stGET.close();
						// "Клієнт Васька (Місто) Заявка №" + rquest.getId()
						if(client!=null && place!=null){
							if(!rquest.getOK())
								rquest.setPrintableName(client.getName() + " (" + place.getName() + "), заявка №" + rquest.getId());
							else
								rquest.setPrintableName("B_" + client.getName() + " (" + place.getName() + "), заявка №" + rquest.getId());
							
							rquest.setPlace(place.getName());
						}
							
						String date[] = rquest.getDateRequest().split(" ")[0].split("-");
						int year = Integer.parseInt(date[0]);
						int mounth = Integer.parseInt(date[1]);
						int day = Integer.parseInt(date[2]);

						DefaultMutableTreeNode yearN = null;
						DefaultMutableTreeNode mounthN = null;
						DefaultMutableTreeNode dayN = null;
						boolean upd1=false, upd2=false, upd3=false;
						
						for(int i=0; i<root.getChildCount(); i++){
							 if(root.getChildAt(i).toString().equals(Integer.toString(year))){
								 yearN = (DefaultMutableTreeNode) root.getChildAt(i);
								 break;
							 }
						 }
						
						if(yearN == null){
							 yearN = new DefaultMutableTreeNode(year);
							 root.add(yearN);
							 upd1=true;
						}
						
						for(int i=0; i<yearN.getChildCount(); i++){
							 if(yearN.getChildAt(i).toString().equals(Main.getMounth(mounth))){
								 mounthN = (DefaultMutableTreeNode) yearN.getChildAt(i);
								 break;
							 }
						 }
						
						if(mounthN == null){
							mounthN = new DefaultMutableTreeNode(Main.getMounth(mounth));
							yearN.add(mounthN);
							 upd2=true;
						}
						
						for(int i=0; i<mounthN.getChildCount(); i++){
							 if(mounthN.getChildAt(i).toString().equals(Integer.toString(day))){
								 dayN = (DefaultMutableTreeNode) mounthN.getChildAt(i);
								 break;
							 }
						 }
						
						if(dayN == null){
							dayN = new DefaultMutableTreeNode(day);
							mounthN.add(dayN);
							 upd3=true;
						}
						
						DefaultMutableTreeNode r = new DefaultMutableTreeNode(rquest);
						dayN.add(r);
						
						DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
						
						if(upd1)
							model.reload(yearN.getParent());
						else if(upd2)
							model.reload(mounthN.getParent());
						else if(upd3)
							model.reload(dayN.getParent());
						else
							model.reload(dayN);
					    						
						miUpdReq.doClick();
/*
						TreeNode expNode = (TreeNode)root;
						TreeNode[] nodes = root.getPath();
						int count = 0;
						for(int i=0;i<nodes.length;i++)
						{
							 DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) nodes[i];
							 count = count+expNode.getIndex(tempNode)+1;
							 tree.expandRow(count);
							 expNode = tempNode;
						}
						*/
						stGET.close();
						
						conn.close();
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
				/////////////////////////////////////////////////////////////////////////////
				
			}
		});
		btnAdd.setBackground(Color.GREEN);
		btnAdd.setBounds(10, 22, 133, 23);
		panel.add(btnAdd);
		
		btnEdit = new JButton("\u0420\u0435\u0434\u0430\u0433\u0443\u0432\u0430\u0442\u0438 \u0437\u0430\u044F\u0432\u043A\u0443");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Взяти виділене id
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				Request request = (Request)selectedNode.getUserObject();
				
				//((DefaultMutableTreeNode)selectedNode.getParent()).remove(selectedNode);
				
				NewRequestForm frame = new NewRequestForm(2, request);
				frame.setModal(true);
				frame.setVisible(true);

				if(frame.cancel)
					return;
				
				 DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
				 try{
						Class.forName("com.mysql.jdbc.Driver");
						Connection conn = null;
						conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
						
						Request rquest = request;
						Client client = null;
						Place place = null;

						Statement stGET = conn.createStatement();
						String queryGetClient = "SELECT * FROM client WHERE id = " + rquest.getClientId();
						ResultSet rsGet = stGET.executeQuery(queryGetClient);
							
						if(rsGet.next()){
							int idC = rsGet.getInt("id");
							String name = rsGet.getString("name");
							int place_id = rsGet.getInt("place_id");
							client = new Client(idC, name, place_id);
						}
						rsGet.close();
							
						if(client!=null){
							String queryGetPlace = "SELECT * FROM place WHERE id = " + client.getPlaceID();
							rsGet = stGET.executeQuery(queryGetPlace);
							
							if(rsGet.next()){
								int idP = rsGet.getInt("id");
								String name = rsGet.getString("name");
								place = new Place(idP, name);
							}
						}
						stGET.close();
						// "Клієнт Васька (Місто) Заявка №" + rquest.getId()
						if(client!=null && place!=null){
							if(!rquest.getOK())
								rquest.setPrintableName(client.getName() + " (" + place.getName() + "), заявка №" + rquest.getId());
							else
								rquest.setPrintableName("B_" + client.getName() + " (" + place.getName() + "), заявка №" + rquest.getId());

							rquest.setPlace(place.getName());
						}
						
							DefaultMutableTreeNode p = (DefaultMutableTreeNode)selectedNode.getParent();
							p.remove(selectedNode);
							DefaultTreeModel modelr = (DefaultTreeModel) tree.getModel();
							modelr.reload(p);
							if(p.getChildCount()==0){
								((DefaultMutableTreeNode)p.getParent()).remove(p);
							}
						
						
						
							
						String date[] = rquest.getDateRequest().split(" ")[0].split("-");
						int year = Integer.parseInt(date[0]);
						int mounth = Integer.parseInt(date[1]);
						int day = Integer.parseInt(date[2]);

						DefaultMutableTreeNode yearN = null;
						DefaultMutableTreeNode mounthN = null;
						DefaultMutableTreeNode dayN = null;
						boolean upd1=false, upd2=false, upd3=false;
						
						for(int i=0; i<root.getChildCount(); i++){
							 if(root.getChildAt(i).toString().equals(Integer.toString(year))){
								 yearN = (DefaultMutableTreeNode) root.getChildAt(i);
								 break;
							 }
						 }
						
						if(yearN == null){
							 yearN = new DefaultMutableTreeNode(year);
							 root.add(yearN);
							 upd1=true;
						}
						
						for(int i=0; i<yearN.getChildCount(); i++){
							 if(yearN.getChildAt(i).toString().equals(Main.getMounth(mounth))){
								 mounthN = (DefaultMutableTreeNode) yearN.getChildAt(i);
								 break;
							 }
						 }
						
						if(mounthN == null){
							mounthN = new DefaultMutableTreeNode(Main.getMounth(mounth));
							yearN.add(mounthN);
							 upd2=true;
						}
						
						for(int i=0; i<mounthN.getChildCount(); i++){
							 if(mounthN.getChildAt(i).toString().equals(Integer.toString(day))){
								 dayN = (DefaultMutableTreeNode) mounthN.getChildAt(i);
								 break;
							 }
						 }
						
						if(dayN == null){
							dayN = new DefaultMutableTreeNode(day);
							mounthN.add(dayN);
							 upd3=true;
						}
						
						DefaultMutableTreeNode r = new DefaultMutableTreeNode(rquest);
						dayN.add(r);
						
						DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
						
						if(upd1)
							model.reload(yearN.getParent());
						else if(upd2)
							model.reload(mounthN.getParent());
						else if(upd3)
							model.reload(dayN.getParent());
						else
							model.reload(dayN);
					    						
						miUpdReq.doClick();
						
						stGET.close();
						conn.close();
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
				
				
				
				miUpdReq.doClick();
				btnDelete.setEnabled(false);
				btnView.setEnabled(false);
				btnPrintNakladna.setEnabled(false);
				btnEdit.setEnabled(false);
			}
			
			
		});
		btnEdit.setEnabled(false);
		btnEdit.setBackground(Color.CYAN);
		btnEdit.setBounds(10, 56, 133, 23);
		panel.add(btnEdit);
		
		btnDelete = new JButton("\u0412\u0438\u0434\u0430\u043B\u0438\u0442\u0438 \u0437\u0430\u044F\u0432\u043A\u0443");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					// Взяти виділене id
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
					Request request = (Request)selectedNode.getUserObject();

					Main.insertLog("Видалення заявки: " + request.toString());
					int i = Main.okcancel("Дійсно видалити: " + request + "?");

					if(i==0){

						if(request.getOK()){
							// Для початку повернути товар на склад
							String queryGetProd = "SELECT * FROM request_has_production WHERE request_id = " + request.getId();
							Statement stGET = conn.createStatement();
							ResultSet rsGET = stGET.executeQuery(queryGetProd);
							
							// Потрібно дізнатися чи є по даній заявці виробничий звіт?
							Date date = Main.dfDateAndTime.parse(request.getDateRequest());
							//date = Main.addDays(date, 1);
							date.setHours(0);
							date.setMinutes(0);
							date.setSeconds(0);
							OldNewReport oldNewReport = null;
							String queryR = "SELECT * FROM oldnewreport WHERE DateTime >= \'" + Main.dfDateAndTime.format(date) + "\' and DateTime <= \'";
							date.setHours(23);
							date.setMinutes(59);
							date.setSeconds(59);
							queryR += Main.dfDateAndTime.format(date) + "\'";

							//System.out.println(queryR);
							Statement stR = conn.createStatement();
							ResultSet rsR = stR.executeQuery(queryR);
							
							while(rsR.next()){
								if(rsR.getBoolean("old")){
									oldNewReport = new OldNewReport(rsR.getInt("id"),rsR.getString("DateTime"), true, rsR.getInt("User_id"));
									break;
								}
							}
							
							rsR.close();
							stR.close();
							///////////////////////////////////////////////////
														
							if(oldNewReport == null){
								while (rsGET.next())
								{
									int idProd = rsGET.getInt("Production_id");
									int count = rsGET.getInt("Count");
									
									for(int j=0; j<productionTable.getRowCount(); j++){
										Production p = ((Production)productionTable.getValueAt(j, 0));
										Float cf = Float.parseFloat(productionTable.getValueAt(j, 1).toString());
										Integer co = new Integer(cf.intValue());
										
										if(p.getID()==idProd){
											productionTable.setValueAt(Integer.sum(co.intValue(), count), j, 1);
											// Оновити
											String queryU = "UPDATE production set CountOnStorage = CountOnStorage + ? WHERE id = ?";
											PreparedStatement preparedStmtU = conn.prepareStatement(queryU);
											preparedStmtU.setInt(1, count);
											preparedStmtU.setInt(2, idProd);
											preparedStmtU.execute();
											
											break;
										}
									}
								}
							}
							else{
								while (rsGET.next())
								{
									int idProd = rsGET.getInt("Production_id");
									int count = rsGET.getInt("Count");
									
									String queryU = "UPDATE production_has_oldnewreport SET Count = Count - ? WHERE Production_id = ? and OldNewReport_id = ?";
									PreparedStatement preparedStmtU = conn.prepareStatement(queryU);
									preparedStmtU.setInt(1, count);
									preparedStmtU.setInt(2, idProd);
									preparedStmtU.setInt(3, oldNewReport.getId());
									preparedStmtU.execute();
									
								}
							}
							
							
							rsGET.close();
							stGET.close();
						}
						
						// потім видалити зв'язки
						String queryRemove = "delete from request_has_production where request_id = ?";
						PreparedStatement preparedStmtRemove = conn.prepareStatement(queryRemove);
						preparedStmtRemove.setInt(1, request.getId());
						preparedStmtRemove.execute();
						
						String query = "delete from request where id = ?";
						PreparedStatement preparedStmt = conn.prepareStatement(query);
						preparedStmt.setInt(1,  request.getId());
						preparedStmt.execute();
						
						
						DefaultMutableTreeNode p = (DefaultMutableTreeNode)selectedNode.getParent();
						p.remove(selectedNode);
						DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
						model.reload(p);
						
						if(p.getChildCount()==0){
							((DefaultMutableTreeNode)p.getParent()).remove(p);
						}
						
						miUpdReq.doClick();
					}
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				btnDelete.setEnabled(false);
				btnView.setEnabled(false);
				btnPrintNakladna.setEnabled(false);
				btnEdit.setEnabled(false);
				
			}
		});
		btnDelete.setEnabled(false);
		btnDelete.setBackground(Color.RED);
		btnDelete.setBounds(10, 90, 133, 23);
		panel.add(btnDelete);
		
		btnPrintNakladna = new JButton("\u0414\u0440\u0443\u043A \u043D\u0430\u043A\u043B\u0430\u0434\u043D\u043E\u0457");
		btnPrintNakladna.setBackground(Color.CYAN);
		btnPrintNakladna.setEnabled(false);
		btnPrintNakladna.addActionListener(actListener);
		btnPrintNakladna.setBounds(10, 347, 133, 23);
		panel.add(btnPrintNakladna);

		btnView = new JButton("\u041F\u0435\u0440\u0435\u0433\u043B\u044F\u043D\u0443\u0442\u0438");
		btnView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Взяти виділене id
				
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				Request request = (Request)selectedNode.getUserObject();

				NewRequestForm frame = new NewRequestForm(1, request);
				frame.setModal(true);
				frame.setVisible(true);
				
			}
		});
		btnView.setBackground(Color.CYAN);
		btnView.setEnabled(false);
		btnView.setBounds(10, 380, 133, 23);
		panel.add(btnView);
		
		statusPanel = new JPanel();
		statusPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		statusPanel.setBounds(0, heigth-20, width, 20);
		contentPane.add(statusPanel);
		statusPanel.setLayout(null);
		
		labelLoginAs = new JLabel("");
		labelLoginAs.setForeground(Color.BLUE);
		labelLoginAs.setBounds(10, 0, 293, 21);
		statusPanel.add(labelLoginAs);
		
		labelTime = new JLabel("yyyy/MM/dd HH:mm:ss");
		labelTime.setForeground(Color.BLUE);
		labelTime.setBounds(width-5-115, 3, 115, 14);
		statusPanel.add(labelTime);
		
		labelPath = new JLabel("");
		labelPath.setBounds(width/2+72, 3, 154, 14);
		statusPanel.add(labelPath);
		labelPath.setForeground(Color.BLUE);
		
		label = new JLabel("\u0417\u0430\u044F\u0432\u043A\u0430 \u0437\u0430: ");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		label.setBounds(width/2, 3, 72, 14);
		statusPanel.add(label);
		
		JPanel productionPanel = new JPanel();
		productionPanel.setBorder(new TitledBorder(null, "\u041D\u0430\u044F\u0432\u043D\u0430 \u043F\u0440\u043E\u0434\u0443\u043A\u0446\u0456\u044F", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		productionPanel.setBounds(10, 61, 310, 411);
		contentPane.add(productionPanel);
		productionPanel.setLayout(null);
		
		productionTable = new JTable(model);
		productionTable.setBounds(10, 21, p_w-20, heigth-90);
		//productionPanel.add(productionTable);
		jspP = new JScrollPane(productionTable);
		jspP.setBounds(10, 21, 290, 379);
		productionPanel.add(jspP);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBackground(Color.WHITE);
		toolBar.setLayout(null);
		toolBar.setBounds(0, 21, 900, 35);
		contentPane.add(toolBar);

		Image printPic = null;
		Image printPicDef = null;
		double scale = 1;
		try {
			printPic = ImageIO.read(new File("icons/print.png"));
			printPicDef = ImageIO.read(new File("icons/print_g.png"));
			
			double needSize = 31;
			double realSize = ((BufferedImage)printPic).getHeight();
			scale = needSize/realSize;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			printPic = null;
			e1.printStackTrace();
		}
		
		
		if(printPic==null){
			btnPrint = new JButton(new ImageIcon("icons/print.png"));
			btnPrint.setDisabledIcon(new ImageIcon("icons/print_g.png"));
		}
		else{
			btnPrint = new JButton(Main.getScaledIcon(printPic,scale));
			btnPrint.setDisabledIcon(Main.getScaledIcon(printPicDef,scale));
		}
		btnPrint.setBounds(2, 2, 31, 31);
		btnPrint.setBorderPainted(false);
		btnPrint.setContentAreaFilled(false); 
		btnPrint.setFocusPainted(false); 
		btnPrint.setOpaque(false);
		//btnPrint.setEnabled(false);
		btnPrint.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				PrintAllByDayForm frame = new PrintAllByDayForm();

				frame.setModal(true);
				frame.setVisible(true);
				
				miUpdReq.doClick();
				menuItem_8.doClick();
			}
		});
		btnPrint.setToolTipText("Спільний друк");
		toolBar.add(btnPrint);
		
		////// Залишок
		
		Image printPicZ = null;
		Image printPicDefZ = null;
		double scaleZ = 1;
		try {
			printPicZ = ImageIO.read(new File("icons/z.png"));
			printPicDefZ = ImageIO.read(new File("icons/z_g.png"));
			
			double needSize = 31;
			double realSize = ((BufferedImage)printPicZ).getHeight();
			scaleZ = needSize/realSize;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			printPicZ = null;
			e1.printStackTrace();
		}
		
		if(printPicZ==null){
			btnZalishok = new JButton(new ImageIcon("icons/print.png"));
			btnZalishok.setDisabledIcon(new ImageIcon("icons/print_g.png"));
		}
		else{
			btnZalishok = new JButton(Main.getScaledIcon(printPicZ,scaleZ));
			btnZalishok.setDisabledIcon(Main.getScaledIcon(printPicDefZ,scaleZ));
		}
		btnZalishok.setBounds(2+31+2, 2, 31, 31);
		btnZalishok.setBorderPainted(false);
		btnZalishok.setContentAreaFilled(false); 
		btnZalishok.setFocusPainted(false); 
		btnZalishok.setOpaque(false);
		//btnPrint.setEnabled(false);
		btnZalishok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ViewTheBalanceOfThePreviousChangesForm frame = new ViewTheBalanceOfThePreviousChangesForm();
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		btnZalishok.setToolTipText("Залишок попередньої зміни");
		toolBar.add(btnZalishok);
		
		///////////
		
		
		
		
		
		jspP.setVisible(false);

		productionTable.getColumnModel().getColumn(0).setPreferredWidth((int) (productionTable.getWidth()*0.8));
		productionTable.getColumnModel().getColumn(1).setPreferredWidth((int) (productionTable.getWidth()*0.2));
		
		mtu = new MyTimeUpdater();
		mtu.start();
		
		//initData();
		
		if(LoggedUser.STATUS==0){

			menuEdit.setEnabled(false);
			menu.setEnabled(false);
			menuReport.setEnabled(false);
			btnAdd.setEnabled(false);
			tree.setVisible(false);
			btnPrint.setEnabled(false);
			btnZalishok.setEnabled(false);
			menuItem.setEnabled(false);
			menuItem_2.setEnabled(false);
			menuItem_3.setEnabled(false);
			menuItem_5.setEnabled(false);
			miSetting.setEnabled(false);
			miPrintNakladnaByWay.setEnabled(false);
			miPrintDate.setEnabled(false);
		}
		
		//miUpdReq.doClick();
		
		loadData();
		loadProductionData();
		setNewSettings();
		
		addComponentListener(new ComponentAdapter() 
		{  
			public void componentMoved(ComponentEvent e) {
				if(Main.onTop)
					setLocation(0, 0);
		    }
			
		        public void componentResized(ComponentEvent evt) {
		            Component c = (Component)evt.getSource();
		            
		            // MIN 600x300
		            /*
					if(c.getWidth()<600)
						setBounds(getX(), getY(), 600, getHeight());
					if(c.getHeight()<300)
						setBounds(getX(), getY(), getWidth(), 300);
		            */
		            int width=c.getWidth()-16, heigth=c.getHeight()-38-26;
		            menuBar.setBounds(0, 0, width, 21);
		            toolBar.setBounds(0, 21, width, 35);
		    		statusPanel.setBounds(0, heigth-20+26, width, 20);
		    		panel.setBounds(width-5-155, 61, 155, heigth-60);
		    		btnPrintNakladna.setBounds(10, heigth-60 - 46 - 20, 133, 23);
		    		btnView.setBounds(10, heigth-60 - 23 - 10, 133, 23);
		    		jsp.setBounds(10, 21, width-15-175-p_w, heigth-90);
		    		requestPanel.setBounds(10+p_w, 61, width-15-155-p_w, heigth-60);

		    		// Розміри частини вікна з продукцією
		    		int p_x = 10, p_y = 61, p_w=2*155, p_h=heigth-60;
		    		jspP.setBounds(10, 61-40, p_w-20, heigth-90);
		    		productionPanel.setBounds(p_x, p_y, p_w, p_h);
		    		labelTime.setBounds(width-5-115, 3, 115, 14);
		    		labelPath.setBounds(width/2+72, 3, 154, 14);
		    		label.setBounds(width/2, 3, 72, 14);
		        }
		});
		
	}
	
	public void enable(){
		if(LoggedUser.STATUS==1){

			menuEdit.setEnabled(true);
			menu.setEnabled(true);
			menuReport.setEnabled(true);
			btnAdd.setEnabled(true);
			tree.setVisible(true);

			menuItem.setEnabled(true);
			btnPrint.setEnabled(true);
			btnZalishok.setEnabled(true);
			menuItem_2.setEnabled(true);
			menuItem_3.setEnabled(true);
			menuItem_5.setEnabled(true);
			miPrintNakladnaByWay.setEnabled(true);
			miPrintDate.setEnabled(true);
			miSetting.setEnabled(true);
			jspP.setVisible(true);

			menuItem_1.setEnabled(false);
			miExit.setEnabled(false);
			
			if(LoggedUser.ROLE==2){
				menuItem_4.setVisible(false);
				menuItem_6.setVisible(false);
				menuItem_7.setVisible(false);
				miReport.setVisible(false);
			} 
			
			if(LoggedUser.ROLE!=0){
				miEmployee.setVisible(false);
				miUser.setVisible(false);
				miSetting.setVisible(false);
				//btnPrint.setEnabled(false);
			} else {
				miEmployee.setVisible(true);
				miUser.setVisible(true);
				miSetting.setVisible(true);
				menuItem_4.setVisible(true);
				miReport.setVisible(true);
				menuItem_6.setVisible(true);
				menuItem_7.setVisible(true);
			}
			
			Main.mainFrame.labelLoginAs.setText(LoggedUser.name);
		}
		else if(LoggedUser.STATUS==2){
			menuEdit.setEnabled(false);
			menu.setEnabled(false);
			menuReport.setEnabled(false);
			btnAdd.setEnabled(false);
			tree.setVisible(false);
			jspP.setVisible(false);

			menuItem.setEnabled(false);
			menuItem_2.setEnabled(false);
			menuItem_3.setEnabled(false);
			menuItem_5.setEnabled(false);
			miPrintNakladnaByWay.setEnabled(false);
			miPrintDate.setEnabled(false);
			miSetting.setEnabled(false);
			btnPrint.setEnabled(true);
			btnZalishok.setEnabled(true);

			menuItem_1.setEnabled(true);
			miExit.setEnabled(true);
			
			Main.mainFrame.labelLoginAs.setText(LoggedUser.name);
			labelLoginAs.setText("");
		}
		else if(LoggedUser.STATUS==0){
			menuEdit.setEnabled(false);
			menu.setEnabled(false);
			menuReport.setEnabled(false);
			btnAdd.setEnabled(false);
			tree.setVisible(false);
			btnPrint.setEnabled(false);
			btnZalishok.setEnabled(false);
			jspP.setVisible(false);
			labelLoginAs.setText("");

			menuItem.setEnabled(false);
			menuItem_2.setEnabled(false);
			menuItem_3.setEnabled(false);
			menuItem_5.setEnabled(false);
			miPrintNakladnaByWay.setEnabled(false);
			miPrintDate.setEnabled(false);
			miSetting.setEnabled(false);

			menuItem_1.setEnabled(true);
			miExit.setEnabled(true);
			
			Main.mainFrame.labelLoginAs.setText(LoggedUser.name);
		}
	}

	
	private void loadData(){
		 DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		 
		 try{
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = null;
				conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
				
				String query = "SELECT * FROM request ORDER BY date_request ASC";
				
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
				  
				while (rs.next())
				{
					// id, Client_id, Date_Request, Date_Oformleniya, OK, User_id
					int id = rs.getInt("id");
					int client_id = rs.getInt("client_id");
					String date_Request = rs.getString("Date_Request");
					String date_Oformleniya = rs.getString("Date_Oformleniya");
					boolean ok = rs.getBoolean("OK");
					int user_id = rs.getInt("User_id");
					
					Request rquest = new Request(id, client_id, date_Request, date_Oformleniya, ok, user_id);
					Client client = null;
					Place place = null;

					Statement stGET = conn.createStatement();
					String queryGetClient = "SELECT * FROM client WHERE id = " + client_id;
					ResultSet rsGet = stGET.executeQuery(queryGetClient);
					
					if(rsGet.next()){
						int idC = rsGet.getInt("id");
						String name = rsGet.getString("name");
						int place_id = rsGet.getInt("place_id");
						client = new Client(idC, name, place_id);
					}
					rsGet.close();
					
					if(client!=null){
						String queryGetPlace = "SELECT * FROM place WHERE id = " + client.getPlaceID();
						rsGet = stGET.executeQuery(queryGetPlace);
						
						if(rsGet.next()){
							int idP = rsGet.getInt("id");
							String name = rsGet.getString("name");
							place = new Place(idP, name);
						}
					}
					stGET.close();
					// "Клієнт Васька (Місто) Заявка №" + rquest.getId()
					if(client!=null && place!=null){
						if(!rquest.getOK())
							rquest.setPrintableName(client.getName() + " (" + place.getName() + "), заявка №" + rquest.getId());
						else
							rquest.setPrintableName("B_" + client.getName() + " (" + place.getName() + "), заявка №" + rquest.getId());

						rquest.setPlace(place.getName());
					
					}
					
					String date[] = date_Request.split(" ")[0].split("-");
					int year = Integer.parseInt(date[0]);
					int mounth = Integer.parseInt(date[1]);
					int day = Integer.parseInt(date[2]);

					DefaultMutableTreeNode yearN = null;
					DefaultMutableTreeNode mounthN = null;
					DefaultMutableTreeNode dayN = null;
					
					for(int i=0; i<root.getChildCount(); i++){
						 if(root.getChildAt(i).toString().equals(Integer.toString(year))){
							 yearN = (DefaultMutableTreeNode) root.getChildAt(i);
							 break;
						 }
					 }
					
					if(yearN == null){
						 yearN = new DefaultMutableTreeNode(year);
						 root.add(yearN);
					}
					
					for(int i=0; i<yearN.getChildCount(); i++){
						 if(yearN.getChildAt(i).toString().equals(Main.getMounth(mounth))){
							 mounthN = (DefaultMutableTreeNode) yearN.getChildAt(i);
							 break;
						 }
					 }
					
					if(mounthN == null){
						mounthN = new DefaultMutableTreeNode(Main.getMounth(mounth));
						yearN.add(mounthN);
					}
					
					for(int i=0; i<mounthN.getChildCount(); i++){
						 if(mounthN.getChildAt(i).toString().equals(Integer.toString(day))){
							 dayN = (DefaultMutableTreeNode) mounthN.getChildAt(i);
							 break;
						 }
					 }
					
					if(dayN == null){
						dayN = new DefaultMutableTreeNode(day);
						mounthN.add(dayN);
					}
					
					dayN.add(new DefaultMutableTreeNode(rquest));
					
			    }
				st.close();
				
				conn.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
	}
	
	public void loadProductionData(){


		int selected = productionTable.getSelectedRow();
		
		if (model.getRowCount() > 0) {
		    for (int i = model.getRowCount() - 1; i > -1; i--) {
		    	model.removeRow(i);
		    }
		}
		
		if(!productions.isEmpty())
			productions.clear();
		
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
		
		if(selected>=0)
			productionTable.setRowSelectionInterval(selected, selected);
		
	}
	
	
	private class MyTimeUpdater extends Thread {

		private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
	    public void run(){
	    	do{
    		 	labelTime.setText(dateFormat.format(new Date()));
	    		try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    		
	    	} while(true);
	    }
	  }
	
	private ActionListener actListener = new ActionListener() {
		
		private int count = 1;
		private XWPFDocument document;

		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			Request request = (Request)selectedNode.getUserObject();
			
			PrintForm frame = new PrintForm(request.getOK());
			frame.setModal(true);
			frame.setVisible(true);
			
			if(!frame.PRINT)
				return;
			
			if(!request.getOK())
			{
				request.setOK(frame.OK);
				request.setPrintableName("В_" + request.toString());
			
			
				DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
				
				model.reload(selectedNode.getParent());
			}
			
			if(frame.OK){
				count = frame.COUNT;
				
				Main.insertLog("Друк накладної (" + count + ") " + request.toString());
				
				try{
					Class.forName("com.mysql.jdbc.Driver");
					Connection conn = null;
					conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
					
					String query = "UPDATE request SET ok = ? where id = ?";
					PreparedStatement preparedStmt = conn.prepareStatement(query);
					preparedStmt.setBoolean(1, true);
					preparedStmt.setInt(2, request.getId());
					preparedStmt.executeUpdate();
										
					conn.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			NewRequestForm frameGetData = new NewRequestForm(1, request);
			
			document = new XWPFDocument();
			
			CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
		    CTPageMar pageMar = sectPr.addNewPgMar();
		    pageMar.setLeft(BigInteger.valueOf(400L));
		    pageMar.setTop(BigInteger.valueOf(400L));
		    pageMar.setRight(BigInteger.valueOf(400L));
		    pageMar.setBottom(BigInteger.valueOf(400L));
			int q=2;
			int tableFontSize=Main.FONT_SIZE;
			int rowHeight=115;
			String fontName = "Calibri";
			
			XWPFParagraph paragraphTitle = document.createParagraph();
			paragraphTitle.setSpacingAfter(0);
			paragraphTitle.setAlignment(ParagraphAlignment.CENTER);
			XWPFRun runTitle = paragraphTitle.createRun();
			runTitle.setText("Накладна №" + request.getId());
			runTitle.setFontSize(14);
			runTitle.setFontFamily("Times New Roman");
			runTitle.setBold(true);
			
			XWPFParagraph paragraphDate = document.createParagraph();
			paragraphDate.setAlignment(ParagraphAlignment.RIGHT);
			XWPFRun runDate = paragraphDate.createRun();
			runDate.setText("Дата: " + request.getDateRequest().split(" ")[0]);
			runDate.setFontSize(11);
			runDate.setFontFamily("Times New Roman");
			runDate.setItalic(true);
			
			XWPFParagraph paragraphPP = document.createParagraph();
			paragraphPP.setAlignment(ParagraphAlignment.RIGHT);
			XWPFRun runPP = paragraphPP.createRun();
			runPP.setText("Відпустив: ПП Уманець");
			runPP.setFontSize(11);
			runPP.setFontFamily("Times New Roman");	
			runPP.addBreak();
			runPP.setText("Отримав: " + ((Client)frameGetData.cbClient.getSelectedItem()).getName());
			
			
			XWPFTable table = document.createTable();
			table.setCellMargins(50, 50, 50, 50);
			
			
			System.out.println(Main.WIDTH_1 + "x" + Main.WIDTH_2 + "x" + Main.WIDTH_3 + "x" + Main.WIDTH_4 + "x" + Main.WIDTH_5 + "x" + Main.WIDTH_6);
			
			XWPFTableRow row = table.getRow(0);	// Взяття першого існуючого стовпця
			row.setHeight(rowHeight);
			XWPFTableCell cell = row.getCell(0);
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_1/q));
			XWPFParagraph paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
			paragraphCell.setSpacingAfter(0);
			XWPFRun runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("№");
			
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_2/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Назва продукту");
			
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_3/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Од.в.");
			
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_4/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("К-сть");
			
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_5/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Ціна,");
			runCell.addBreak();
			runCell.setText("грн");
			
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_6/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Сума,");
			runCell.addBreak();
			runCell.setText("грн ");
							
			// Другий стовпець
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_1/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("№");
			
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_2/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Назва продукту");
			
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_3/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Од.в.");
			
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_4/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("К-сть");
			
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_5/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Ціна,");
			runCell.addBreak();
			runCell.setText("грн");
			
			cell = row.createCell();
			cell.removeParagraph(0);
			cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(Main.WIDTH_6/q));
			paragraphCell = cell.addParagraph();
			paragraphCell.setAlignment(ParagraphAlignment.CENTER);
			paragraphCell.setSpacingAfter(0);
			runCell = paragraphCell.createRun();
			runCell.setFontSize(tableFontSize);
			runCell.setFontFamily(fontName);
			runCell.setBold(true);
			runCell.setText("Сума,");
			runCell.addBreak();
			runCell.setText("грн");
	
			int j=0;
			
			try{
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = null;
				conn = DriverManager.getConnection(Main.DBWAY, Main.DBLOGIN, Main.DBPASSWORD);
				Statement st = conn.createStatement();
				String updProduction = "UPDATE production set CountOnStorage = CountOnStorage - ? WHERE id = ?";
				
				int n=0;
				
				for (int i=0; i<frameGetData.table.getRowCount(); i++) {
					
					if(frameGetData.table.getValueAt(i, 3).toString()=="")
						continue;
					n++;
				}
				
				int pp = 0;
				
				if(n%2==0)
					pp=1;
				else
					pp=2;
				
				for (int i=0; i<frameGetData.table.getRowCount(); i++) {

					// Відняти кількість кожної продукції зі списку замовлення
					//	((Production)frameGetData.table.getValueAt(i, 0))
					//	frameGetData.table.getValueAt(i, 3).toString()
					
					if(frame.OK && frame.FIRSTPRINT){
						PreparedStatement preparedStmt = conn.prepareStatement(updProduction);
						if(frameGetData.table.getValueAt(i, 3).toString()!=null){
							if(Main.isNumeric(frameGetData.table.getValueAt(i, 3).toString()))
								preparedStmt.setInt(1, Integer.parseInt((frameGetData.table.getValueAt(i, 3).toString())));
							else
								preparedStmt.setInt(1, 0);
						}
						else
							preparedStmt.setInt(1, 0);
						preparedStmt.setInt (2, ((Production)frameGetData.table.getValueAt(i, 0)).getID());
						preparedStmt.execute();
					}
					
					////////////////////////////////////////////////////////////
					////////////////////////////////////////////////////////////
					
					if(frameGetData.table.getValueAt(i, 3).toString()=="")
						continue;
					
					
					
					if(Integer.parseInt(frameGetData.table.getValueAt(i, 3).toString())!=0){
						j++;
						
						if(j<n/2+pp){
							row = table.createRow();
							row.setHeight(rowHeight);
							cell = row.getCell(0);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setBold(true);
							runCell.setFontFamily(fontName);
							runCell.setText(Integer.toString(j) + ".");
				
							cell = row.getCell(1);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.LEFT);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setText(((Production)frameGetData.table.getValueAt(i, 0)).getName());
							
							cell = row.getCell(2);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.CENTER);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setText("шт.");
				
							cell = row.getCell(3);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setText(frameGetData.table.getValueAt(i, 3).toString());
							
							cell = row.getCell(4);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setText(frameGetData.table.getValueAt(i, 2).toString());
							
							cell = row.getCell(5);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setText(frameGetData.table.getValueAt(i, 4).toString());
						}
						else{
							row = table.getRow(j-n/2-(pp-1));
							row.setHeight(rowHeight);
															 
							cell = row.getCell(6);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setBold(true);
							runCell.setText(Integer.toString(j) + ".");
				
							cell = row.getCell(7);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.LEFT);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setText(((Production)frameGetData.table.getValueAt(i, 0)).getName());
							
							cell = row.getCell(8);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.CENTER);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setText("шт.");
				
							cell = row.getCell(9);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setText(frameGetData.table.getValueAt(i, 3).toString());
							
							cell = row.getCell(10);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setText(frameGetData.table.getValueAt(i, 2).toString());
							
							cell = row.getCell(11);
							cell.removeParagraph(0);
							paragraphCell = cell.addParagraph();
							paragraphCell.setAlignment(ParagraphAlignment.RIGHT);
							paragraphCell.setSpacingAfter(0);
							runCell = paragraphCell.createRun();
							runCell.setFontSize(tableFontSize);
							runCell.setFontFamily(fontName);
							runCell.setText(frameGetData.table.getValueAt(i, 4).toString());
						}
					}
				}
				st.close();
				conn.close();
			}
			catch(Exception ex){ex.printStackTrace();}
			float sum = Float.parseFloat(frameGetData.tfSum.getText().replace(",", "."));
			XWPFParagraph paragraphSUM = document.createParagraph();
			paragraphSUM.setAlignment(ParagraphAlignment.RIGHT);
			XWPFRun runSUM = paragraphSUM.createRun();
			runSUM.addBreak();
			runSUM.setText("СУМА ДО СПЛАТИ: " + new DecimalFormat("##.##").format(sum) + " грн.");
			runSUM.setFontSize(11);
			runSUM.setFontFamily("Times New Roman");
			runSUM.setBold(true);
			
			XWPFParagraph paragraphManagerClient = document.createParagraph();
			paragraphManagerClient.setAlignment(ParagraphAlignment.RIGHT);
			XWPFRun runManagerClient = paragraphManagerClient.createRun();
			//runManagerClient.addBreak();
			runManagerClient.setText("Експедитор: " + LoggedUser.name);
			runManagerClient.setFontSize(11);
			runManagerClient.setFontFamily("Times New Roman");
			runManagerClient.setItalic(true);
			
			String filename = "docs/Накладна " + request.toString()+".docx";
			filename = filename.replace('\"', '_').replace('\'', '_').replace('>', '_').replace('<', '_');
			
			try {
				FileOutputStream output = new FileOutputStream(filename);
				document.write(output);
				output.close();
				
				 //The desktop api can help calling other applications in our machine
			    //and also many other features...
			    Desktop desktop = Desktop.getDesktop();
			    try {
			    //desktop.print(new File("DocXfile.docx"));
			    	for(int i=0; i<count; i++){
			    		desktop.print(new File(filename));
			    	}
			    } catch (IOException exs) {           
			    	exs.printStackTrace();
			    }
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			////////////////////////////////////////////
			////////////////////////////////////////////
			miUpdReq.doClick();
		}
	};
}
