package main;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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
 *
 */
public class ConnectionGUI extends JFrame {

	private final JTextField IP;
	private final JLabel IPlabel;
	private final JTextField port;
	private final JLabel portLabel;
	private final JButton submit;
	private final JLabel error;
	
	/**
	 * Construcor method for the ConnectionGUI
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
        
        /**
         * Action listener for submit button. Retrieves information in IP and 
         * port TextFields and tries to connect to the server at that 
         * location. Closes window if successful, otherwise resets TextFields.
         */
//		submit.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e){
//				try {
//					int portValue = Integer.parseInt(port.getText());
//					if (portValue < 0 || portValue > 65535)
//						throw new IOException();
//					new Socket(IP.getText(), portValue);
//					dispose();
//				} catch (UnknownHostException ue) {
//					IP.setText("");
//					port.setText("");
//					error.setText("Invalid IP.");
//				} catch (IOException ioe) {
//					IP.setText("");
//					port.setText("");
//					error.setText("Invalid port.");
//				} catch (NumberFormatException ne) {
//					IP.setText("");
//					port.setText("");
//					error.setText("Invalid port.");
//				}
//			}
//		});
	}
	
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
