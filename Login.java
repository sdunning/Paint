import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Login extends JFrame{

	protected JLabel lUser = new JLabel("Username: ");
	protected JLabel lPass = new JLabel("Password: ");
	protected JTextField username = new JTextField();
	protected JPasswordField password = new JPasswordField();
	protected JButton login = new JButton("Login");
	protected JButton cancel = new JButton("Cancel");
	
	private Color bgGUI = new Color(198, 255, 125);
    private Color buttonColor = new Color(195, 252, 219);
	
	public Login(String title) {
		this.getContentPane().setBackground(bgGUI);
		login.setBackground(buttonColor);
		cancel.setBackground(buttonColor);
		
		setLocationRelativeTo(null);
		setTitle(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout( new GridLayout(0, 2) );
		add(lUser); add(username);
		add(lPass); add(password);
		add(login); add(cancel);
		setSize(250,120);
		setVisible(false);
		
		username.setText("");
		password.setText("");
	}
}
