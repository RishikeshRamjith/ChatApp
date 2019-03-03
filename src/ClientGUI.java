import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClientGUI {

	public ClientGUI() {
		JFrame client = new JFrame();

		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.setTitle("NetChatter");
		client.setSize(300,250);

		client.setVisible(true);
	}

	class LoginDialog() {

		public LoginDialog() {
			JFrame login = new JFrame();

			login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			login.setTitle("NetChatter Login");
			login.setSize(300,250);

			JLabel uLabel = new JLabel("Username:");
			JTextField user = new JTextField();

			JLabel pLabel = new JLabel("Password:");
			JTextField pass = new JTextField();

			login.setLayout(new GridLayout(0,2));
			login.add(uLabel);
			login.add(user);
			login.add(pLabel);
			login.add(pass);

			login.setVisible(true);
		}
	}

	public static void main(String[] args) {
		
	}
}