package ua.rokytne.bakery.forms;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ua.rokytne.bakery.Main;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class PrintForm extends JDialog {

	private JPanel contentPane;

	public boolean FIRSTPRINT = true;
	public boolean OK = false;
	public boolean PRINT = false;
	public int COUNT = 1;
	public JCheckBox chckbxNewCheckBox;
	private JSpinner spinner;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrintForm frame = new PrintForm(false);
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
	public PrintForm(boolean ok) {
		
		
		if(ok)
			FIRSTPRINT = false;
		
		
		
		this.setAlwaysOnTop(Main.onTop);
		setResizable(false);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("Друк заявки(накладної)");
		setBounds((Main.WIDTH-306)/2, (Main.HEIGTH-124)/2, 253, 124);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnPrint = new JButton("Підтвердити друк");
		btnPrint.setBounds(10, 64, 134, 23);
		btnPrint.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				chckbxNewCheckBox.setSelected(true);
				COUNT = ((Integer)spinner.getValue()).intValue();
				OK = chckbxNewCheckBox.isSelected();
				PRINT= true;
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnPrint);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(154, 64, 89, 23);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PRINT = false;
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnCancel);
		
		chckbxNewCheckBox = new JCheckBox("Операцію продажі завершено?");
		chckbxNewCheckBox.setVisible(false);
		chckbxNewCheckBox.setBounds(6, 26, 36, 23);
		contentPane.add(chckbxNewCheckBox);
		chckbxNewCheckBox.setSelected(ok);
		
		if(ok)
			chckbxNewCheckBox.setEnabled(false);
		
		JLabel pictureLabel;
		try{
			BufferedImage myPicture = ImageIO.read(new File("A.png"));
			
			ImageIcon ii = new ImageIcon(myPicture);
			pictureLabel = new JLabel(ii);
		}
		catch(Exception e){
			pictureLabel = new JLabel("");
		}
		pictureLabel.setBounds(10, 10, 50, 50);
		contentPane.add(pictureLabel);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(1, 1, 3, 1));
		spinner.setBounds(208, 27, 29, 20);
		contentPane.add(spinner);
		
		JLabel label = new JLabel("\u041A\u0456\u043B\u044C\u043A\u0456\u0441\u0442\u044C \u043A\u043E\u043F\u0456\u0439:");
		label.setBounds(109, 30, 89, 14);
		contentPane.add(label);

	}
}
