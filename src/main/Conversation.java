package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

/**
 * GUI of a conversation. A conversation is created when 
 * "Create A New Conversation button" in AllUsersGUI is clicked
 * 
 * Components: 4 text areas to show all chat history. current active users, 
 * 				 for typing messages, and for typing names to invite
 * 			   2 buttons: Invite and Send 
 * 
 * Actions on this GUI are sent to serverProcess' queue
 * Functions: updateChatMess: update new messages in chat history area;
 * 			  updateActive: update list of active users
 */

public class Conversation extends JFrame{
	// list of components here

	private static final long serialVersionUID = 1L;
	// Chat box and its label and send button
	private JLabel ChatLabel;
    private JTextArea ClientChatArea; 
    private JScrollPane ChatBox;
    private JButton Send;
    
    private JLabel MessageHistoryLabel;
    private JLabel ActiveUsersLabel;
    
    // JScrollPane ChatHistory contains the textarea ChatMess. Messages are updated in ChatMess
    // JScrollPane ChatHistory is just a container
    private JScrollPane ChatHistory ;
    private JTextArea ChatMess;
    
    // Same for ActiveUsers in the conversation
    private JScrollPane ActiveUsers; 
    private JTextArea ActiveUsersList;
    
    // For invitation
    private JButton Invite;
    private JTextField InviteUsers; 
    
    private final ClientSide client;
	private final String chat;		
	
	public Conversation(final String chat, final ClientSide client) {
		this.chat = chat;
		this.client = client;
		Container container = getContentPane();
		container.setBackground(Color.LIGHT_GRAY);
		setTitle(client.getUsername());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // so that closing one conversation does not close others
		
		// Default size when opened
        setPreferredSize(new Dimension(400, 350));
        
		// name all components
        MessageHistoryLabel = new JLabel("Messages");
        ActiveUsersLabel = new JLabel("Active Users");
		ChatLabel = new JLabel("ChatLabel");
		ChatLabel.setName("ChatLabel");
		
		ClientChatArea = new JTextArea(2, 14);
		ClientChatArea.setName("ClientChatArea");
		ClientChatArea.setMargin(new Insets(5, 5, 5, 5));
		ClientChatArea.setToolTipText("Type a message, then click Send");
		ClientChatArea.setWrapStyleWord(true);
		ClientChatArea.setLineWrap(true);
		
		
		ChatBox = new JScrollPane(ClientChatArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ChatBox.setName("ChatBox");
		ChatBox.setPreferredSize(new Dimension(200, 60));
		
		ChatMess = new JTextArea(6, 14);
		ChatMess.setMargin(new Insets(5, 5, 5, 5));
		ChatMess.setEnabled(false);
		ChatMess.setName("ChatMess");
		ChatMess.setWrapStyleWord(true);
		ChatMess.setLineWrap(true);
		
		ChatHistory = new JScrollPane(ChatMess,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ChatHistory.setName("ChatHistory");
		
		ActiveUsersList = new JTextArea(6, 2);
		ActiveUsersList.setMargin(new Insets(5, 5, 5, 5));
		ActiveUsersList.setToolTipText("All users currently in this chat");
		ActiveUsersList.setEnabled(false);
		
		ActiveUsers = new JScrollPane(ActiveUsersList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		ActiveUsers.setName("ActiveUsers");
		
		Send = new JButton("Send");
	    Send.setName("Send");
	    
	    Invite = new JButton("Invite");
	    Invite.setName("Invite");
	    
	    InviteUsers = new  JTextField();
	    InviteUsers.setToolTipText("Type in usernames separated by commas. Then click Invite");
	    InviteUsers.setMargin(new Insets(5, 5, 5, 5));
	    InviteUsers.setName("InviteUsers");
	    
	    // add all components to container
        container.add(ChatHistory);
        container.add(ActiveUsers);
        container.add(Send);
        container.add(Invite);
        container.add(InviteUsers);
        container.add(ChatLabel);
        container.add(ChatBox);
        
        //Scroll down automatically
		((DefaultCaret) ChatMess.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		((DefaultCaret) ClientChatArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		((DefaultCaret) ActiveUsersList.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
        /**
         * Send message : add the current message in the ClientChat area to the
         *  serverProcess queue is sent when either click Send button or press Enter in ClientChatArea
         */
        Send.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!ClientChatArea.getText().trim().equals("")){
					String protocol = "post " + client.getUsername() + " " + ClientChatArea.getText().trim() + " " + chat;
					client.getRequest().addLine(protocol.replaceAll("[\\r\\n]+", ""));
					ClientChatArea.setText("");
				}
			}
        });
        
        ClientChatArea.addKeyListener(new KeyListener(){

			@Override
			public void keyReleased(KeyEvent arg0) {
				if ((arg0.getKeyCode() == KeyEvent.VK_ENTER)) {
					if (!ClientChatArea.getText().trim().equals("")){
						String protocol = "post " + client.getUsername() + " " + ClientChatArea.getText().trim() + " " + chat;
						client.getRequest().addLine(protocol.replaceAll("[\\r\\n]+", ""));
						ClientChatArea.setText("");
					}
				}
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
        	
        });
        
        /**
         * Send invite: adds the invite method to the ServerProcessor blocking queue
         * Called when the client either presses enter or clicks the invite button
         */
        Invite.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!InviteUsers.getText().equals("")){
					client.getRequest().addLine("invite " + InviteUsers.getText() + " " + chat);
					InviteUsers.setText("");
				}
			}
        	
        });
        
        InviteUsers.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!InviteUsers.getText().equals("")){
					client.getRequest().addLine("invite " + InviteUsers.getText() + " " + chat);
					InviteUsers.setText("");
				}
			}
        	
        });
        
		this.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
			@Override
			public void windowClosed(WindowEvent arg0) {
				client.getRequest().addLine("leave " + client.getUsername() + " " + chat);
			}
			@Override
			public void windowClosing(WindowEvent arg0) {	
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {	
			}
			@Override
			public void windowIconified(WindowEvent arg0) {
			}
			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		});

        
        ////////// BEGIN Layout  ///////////////////
        GroupLayout layout = new GroupLayout(getContentPane());
        container.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		        .addGroup(layout.createParallelGroup(Alignment.CENTER)
        						.addComponent(MessageHistoryLabel)
        						.addComponent(ActiveUsersLabel)
        						)
        				.addGroup(layout.createParallelGroup(Alignment.CENTER)
        						.addComponent(ChatHistory)
        						.addComponent(ActiveUsers)
        						)
        				.addGroup(layout.createParallelGroup(Alignment.CENTER)
        						.addComponent(InviteUsers)
        						.addComponent(Invite)
        						)
        				.addGroup(layout.createParallelGroup(Alignment.CENTER)
        						.addComponent(ChatBox)
        						.addComponent(Send)
        						)
        				);
        
        layout.setHorizontalGroup(
        		layout.createSequentialGroup()
        			.addGroup(layout.createParallelGroup(Alignment.CENTER)
        					.addComponent(MessageHistoryLabel)
        					.addComponent(ChatHistory)
        					.addComponent(InviteUsers)
        					.addComponent(ChatBox)
        					)
        			.addGroup(layout.createParallelGroup(Alignment.CENTER)
        					.addComponent(ActiveUsersLabel)
        					.addComponent(ActiveUsers)
        					.addComponent(Invite)
        					.addComponent(Send)
        					)		
        		
        		);
        //Display the window.
        pack();
        ///////////////// END of layout ////////////////////
        
        setVisible(true);
	}
	
	
	/**
	 * updateChatMess: get called from serverProcess
	 * to update the ChatMess
	 * @param username - String of the username of the client that sent the message
	 * @param message - String of the message
	 */
	
	public void updateChatMess(String username, String message){
		// display username followed by the message of that user
		ChatMess.append(username + ": " + message + "\r\n");
	}
	
	/**
	 * updateActive gets called when a client joins or leaves the chat
	 * take in list of clients, get names of them and repaint
	 * ActiveUsersList area with clients in alphabetical order.
	 * @param clients - list of all the clients to be updated
	 */
	public void updateActive(List<String> clients) {
		Collections.sort(clients);
		StringBuilder str = new StringBuilder();
		for (String c: clients) {
			str.append(c + "\r\n");
		}
		ActiveUsersList.setText(str.toString());
	}
	
	public void setHistory(String history) {
		ChatMess.append(history);
	}
	
	public String getHistory(){
		return ChatMess.getText();
	}
	
    public static void main(String[] args) {}
    
}
