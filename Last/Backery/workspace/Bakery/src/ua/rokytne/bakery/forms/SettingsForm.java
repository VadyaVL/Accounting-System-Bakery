package ua.rokytne.bakery.forms;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ua.rokytne.bakery.Main;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JButton;

public class SettingsForm extends JDialog {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SettingsForm frame = new SettingsForm();
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
	public SettingsForm() {
		setResizable(false);
		this.setAlwaysOnTop(Main.onTop);
		Main.setUIFont(new javax.swing.plaf.FontUIResource("Tahoma",Font.PLAIN,11));
		setTitle("\u041D\u0430\u043B\u0430\u0448\u0442\u0443\u0432\u0430\u043D\u043D\u044F");
		setBounds((Main.WIDTH-228)/2, (Main.HEIGTH-96)/2, 228, 96);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JCheckBox cbOnTop = new JCheckBox("Завжди поверх усіх вікон");
		cbOnTop.setBounds(6, 7, 159, 23);
		cbOnTop.setSelected(Main.onTop);
		contentPane.add(cbOnTop);
		
		JButton btnSave = new JButton("Зберегти");
		btnSave.setBounds(6, 37, 89, 23);
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.onTop = cbOnTop.isSelected();
				
				// SAVE TO FILE
				File file = new File("configurations.config");
				List<String> fileContent = null;
				
				if(file.exists()){
					try{
						String dbname="", host="", port="", login="", password="";
						fileContent = Files.readAllLines(Paths.get("configurations.config"));
						
						for(int i=0; i < fileContent.size(); i++){
							if(fileContent.get(i).contains("WINDOW_ON_TOP:")){
								if(Main.onTop)
									fileContent.set(i, "WINDOW_ON_TOP: true");
								else
									fileContent.set(i, "WINDOW_ON_TOP: false");
								break;
							}
						}
						
					} catch(Exception ex){
						ex.printStackTrace();
					}
				}
				
				try(FileWriter writer = new FileWriter("configurations.config", false)) {
					for(String s: fileContent)
						writer.write(s + "\r\n");			     
				    writer.flush();
				}
				catch(IOException ex){
				    ex.printStackTrace();
				} 
				
				// END SAVE TO FILE
				
				Main.mainFrame.setNewSettings();
				setVisible(false);
				dispose();
			}
		});
		contentPane.add(btnSave);
		
		JButton btnCancel = new JButton("Відміна");
		btnCancel.setBounds(128, 37, 89, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
	}
}
