package main;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

/**
 * First GUI clients see right after getting connection
 * Exit this GUI means to disconnect from the server, all active 
 * conversation windows are closed as well.
 * <p>
 * Components: 1 text area showing all "online" users
 * 			   1 button to create a new conversation
 * <p>
 * Functions: 	updateUsers(): display online users
 * 				updateChatRooms(): display public chat rooms
 * 				displayCreator()
 * 
 *
 */
public class AllUsersGUI extends JFrame {
	private JLabel Header;
    private JScrollPane AllUsers ;
    private JTextArea Usernames;
    private JList Chats;
    
    private JLabel ChatsID;
    private JLabel CreatorLabel;
    private JTextField Creator;
    private JButton Join;
    private DefaultListModel arrChats;
    private JButton menuItemPublic;
    private JButton menuItemHistory;
    private final ClientSide client;
    
    private final JScrollPane ChatsPane;
    public AllUsersGUI(final ClientSide client){    	
    	this.client = client;
    	Container container = getContentPane();
		setTitle(client.getUsername());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	// might want to use EXIT_ON_CLOSE to close all conversations
		setPreferredSize(new Dimension(250, 380));
		setMinimumSize(new Dimension(230, 250));
		// name all components
		Header = new JLabel("Header");
		Header.setName("Header");
		Header.setText("Online users");
		
		Usernames = new JTextArea("Usernames", 8, 4);
		Usernames.setEnabled(false);
		Usernames.setMargin(new Insets(5, 5, 5, 5));
		Usernames.setWrapStyleWord(true);
		Usernames.setLineWrap(true);
		
		AllUsers = new JScrollPane(Usernames,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		AllUsers.setName("AllUsers");
		
        //Scroll down automatically
		((DefaultCaret) Usernames.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		
		////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////// NEW FEATURES //////////////////////////////////
		menuItemHistory = new JButton("View History");		
		menuItemHistory.addActionListener(new ActionListener(){		
			public void actionPerformed(ActionEvent e){		
				client.showHistory();
			}		
		});
		
		menuItemPublic = new JButton("New Room");
		menuItemPublic.addActionListener(new ActionListener(){		
			public void actionPerformed(ActionEvent e){	
				client.getRequest().addLine("new " + client.getUsername());
			}		
		});		
				        
				
		// List of chats. For now just list 1, 2, 3
		ChatsID = new JLabel("Chat Room No. ");
		
		
		// arrChats is a DefaultListModel that stores chat room # of active public chats
		arrChats = new DefaultListModel();
		
		// Chats is a JList that contains arrChats DefaulListModel
		Chats = new JList(arrChats);
		Chats.setName("Chats");
		ChatsPane = new JScrollPane(Chats, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,		
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);		
		ChatsPane.setMaximumSize(new Dimension(40, 80));
		// addMouseListener so that the name of the creator of selected chat is shown in Creator box
		Chats.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (Chats.getSelectedValue()!= null){
					client.getRequest().addLine("creator " + client.getUsername() + " " + Chats.getSelectedValue());
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
		
		// Creator or something to distinguish chats
		CreatorLabel = new JLabel("Creator");
		CreatorLabel.setName("CreatorLabel");
		
		Creator = new JTextField("Creator");
		Creator.setName("Creator");
		Creator.setMaximumSize(new Dimension(100, 10));
		Creator.setEnabled(false);
		Creator.setMargin(new Insets(5, 5, 5, 5));
		
		// Join button to join the selected chat room from Chats list
		Join = new JButton("Join");
		Join.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!Chats.isSelectionEmpty())
				// Join a conversation is like inviting that person to the chat
				// call join() from the processor, provide id of chat (string) and username
				client.getRequest().addLine("invite " + client.getUsername() + " " + Chats.getSelectedValue().toString());
			}
			
		});

	
		this.addWindowListener(new WindowListener(){
	
			@Override
			public void windowActivated(WindowEvent arg0) {
				
			}
			@Override
			public void windowClosed(WindowEvent arg0) {
				StringBuilder chatString = new StringBuilder("");
				Collection<String> chats = client.getChatMap().keySet();
				for (String chat: chats) {
					chatString.append(chat + " ");
				}
				client.removeAllChats();
				client.getRequest().addLine("disconnect " + client.getUsername() + " " + chatString);
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
		
	
        ////////// BEGIN Layout    ///////////////////
        GroupLayout layout = new GroupLayout(getContentPane());
        container.setLayout(layout);
        
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		    .addGroup(layout.createParallelGroup(Alignment.CENTER)
        					.addComponent(menuItemPublic)
        					.addComponent(menuItemHistory)
        					)
        			.addComponent(Header)
        			.addComponent(AllUsers)
        			.addGroup(layout.createParallelGroup(Alignment.CENTER)
        					.addComponent(ChatsID)
        					.addComponent(CreatorLabel)
        					)
        			.addGroup(layout.createParallelGroup()
        					.addComponent(ChatsPane)
        					.addGroup(layout.createSequentialGroup()
        							.addComponent(Creator)
        							.addComponent(Join)
        							)
        					)
        		);

        layout.setHorizontalGroup(
        		layout.createParallelGroup(Alignment.CENTER)
        		    .addGroup(layout.createSequentialGroup()
        					.addComponent(menuItemPublic)
        					.addComponent(menuItemHistory)
        					)
        			.addComponent(Header)
        			.addComponent(AllUsers)
        			.addGroup(layout.createSequentialGroup()
        					.addGroup(layout.createParallelGroup(Alignment.CENTER) // chat label and chats
        							.addComponent(ChatsID)
//        							.addComponent(Chats)
        							.addComponent(ChatsPane)
        							)
        					.addGroup(layout.createParallelGroup(Alignment.CENTER)
        							.addComponent(CreatorLabel)
        							.addGroup(layout.createParallelGroup(Alignment.CENTER)
        									.addComponent(Creator)
        									.addComponent(Join)
        									)
        							)
        					)
        		);
        
        pack();
        ///////////////// END of layout ////////////////////
        setVisible(true);
    }
    
	/**
	 * updateUsers panel. Iterates through the given list of client names and adds them to the 
	 * GUI in alphabetical order. Called when a client connects or disconnects from the server.
	 * @param clientsName - Collection<String> of all clients currently connected to the server
	 */
	
	public void updateUsers(Collection<String> clientsName) {
		List<String> sortedClients = new ArrayList<String>(clientsName);
		Collections.sort(sortedClients);
		StringBuilder str = new StringBuilder();
		for (String c: sortedClients){
			str.append(c + "\r\n");
		}
		Usernames.setText(str.toString());
	}
	
	/**
	 * updateChatRooms. add each room# from rooms to
	 * arrChats - the DefaultListModel - so that
	 * the list of active public rooms gets updated
	 * 
	 * @param rooms: passed from serverprocessor
	 */
	public void updateChatRooms(Collection<String> rooms){
		arrChats.clear();
		// copy each element in rooms and convert to string
		for (String room: rooms) {
			arrChats.addElement(room);
		}
	}

	public ClientSide getClient() {
		return client;
	}

    public static void main(String[] args) {}

    /**
     * Display the creator of selected Chat Room
     * @param string
     */
	public void displayCreator(String string) {
		Creator.setText(string);
	}
    
}
