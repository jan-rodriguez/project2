package main.client;

import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

/**
 * <code> public class extends JFrame</code>
 * Class used to get the IP address and port number specified by the client to
 * connect to the server specified by the IP address and port number.
 */
public class ConnectionGUI extends JFrame {

	private static final long serialVersionUID = 2303658036405295138L;
	private final JTextField IP;
	private final JLabel IPlabel;
	private final JTextField port;
	private final JLabel portLabel;
	private final JButton submit;
	private final JLabel error;
	
	/**
	 * Constructor method for the ConnectionGUI
	 */
	public ConnectionGUI() {
		//JComponents
		IPlabel = new JLabel("Enter IP Address:");
		IPlabel.setName("IPlabel");
		
		IP = new JTextField();
		IP.setName("IP");
		
		portLabel = new JLabel("Enter port number:");
		portLabel.setName("portLabel");
		
		port = new JTextField();
		port.setName("port");
		
		submit = new JButton("connect");
		submit.setName("submit");
		
		error = new JLabel("\n");
		error.setName("error");
		
		//Setting layout
		Container container = getContentPane();
        GroupLayout layout = new GroupLayout(getContentPane());
        container.setLayout(layout);
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        			.addGroup(layout.createParallelGroup(Alignment.CENTER))
        				.addComponent(IPlabel)
        				.addComponent(IP)
        			.addGroup(layout.createParallelGroup(Alignment.CENTER))
        				.addComponent(portLabel)
        				.addComponent(port)
        			.addGroup(layout.createParallelGroup(Alignment.CENTER))
        				.addComponent(error)
        			.addGroup(layout.createParallelGroup(Alignment.CENTER))
        				.addComponent(submit)
        		);

        layout.setHorizontalGroup(
        		layout.createParallelGroup(Alignment.CENTER)
        			.addGroup(layout.createSequentialGroup())
        				.addComponent(IPlabel)
        				.addComponent(IP)
        			.addGroup(layout.createSequentialGroup())
        				.addComponent(portLabel)
        				.addComponent(port)
        			.addGroup(layout.createSequentialGroup())
    					.addComponent(error)
        			.addGroup(layout.createSequentialGroup())
        				.addComponent(submit)
        		);
        
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Getter methods
	 */
	public JButton getSubmit() {
		return submit;
	}
	
	public JTextField getPort() {
		return port;
	}
	
	public JTextField getIP() {
		return IP;
	}
	
	public JLabel getError() {
		return error;
	}
}
