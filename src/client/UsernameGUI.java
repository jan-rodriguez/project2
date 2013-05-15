package client;

import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;


/**
 * GUI used in client selection of a username.
 */
public class UsernameGUI extends JFrame {

	private static final long serialVersionUID = -8621144903321536569L;
	private final JTextField username;
	private final JLabel usernameLabel;
	private final JLabel errorLabel;
	private final JButton submit;
	private final ClientSide client;

	public UsernameGUI(final ClientSide client) {	
		this.client = client; 
		
		//JComponents
		usernameLabel = new JLabel("Enter alphanumeric username:");
		usernameLabel.setName("usernameLabel");
		
		username = new JTextField();
		username.setName("username");
		
		errorLabel = new JLabel("\n");
		errorLabel.setName("errorLabel");
		
		submit = new JButton("submit");
		submit.setName("submit");
		
		//Setting layout
		Container container = getContentPane();
        GroupLayout layout = new GroupLayout(getContentPane());
        container.setLayout(layout);
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        			.addGroup(layout.createParallelGroup(Alignment.CENTER))
        				.addComponent(usernameLabel)
        				.addComponent(username)
        			.addGroup(layout.createParallelGroup(Alignment.CENTER))
        				.addComponent(errorLabel)
        			.addGroup(layout.createParallelGroup(Alignment.CENTER))
        				.addComponent(submit)
        		);

        layout.setHorizontalGroup(
        		layout.createParallelGroup(Alignment.CENTER)
        			.addGroup(layout.createSequentialGroup())
        				.addComponent(usernameLabel)
        				.addComponent(username)
        			.addGroup(layout.createSequentialGroup())
        				.addComponent(errorLabel)
        			.addGroup(layout.createSequentialGroup())
        				.addComponent(submit)
        		);
        
        pack();
        setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Alerts the user that the username is not currently available
	 */
	public void alertDuplicate() {
		errorLabel.setText("Username already in use.");
	}
	
	/**
	 * Alerts the user that the username is not valid
	 */
	public void alertInvalid() {
		errorLabel.setText("Invalid characters.");
	}
	
	/**
	 * Getter Methods
	 */
	public JButton getSubmit() {
		return submit;
	}
	
	public JTextField getUsername() {
		return username;
	}
	
	public JLabel getError() {
		return errorLabel;
	}

	public ClientSide getClient() {
		return client;
	}

}
