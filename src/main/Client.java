package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <code> public class extends Thread</code>
 * <p>
 * Client class used to communicate with the server and ServerProcess. The server keeps
 *  track of all currently connected Clients.
 * <p>
 * Maintains track of: Socket, ServerProcess, AllUsersGUI, conversations, and username.
 * <p>
 * Functions: newChat, newMessage, setUsers, updateChatMembers, removeChat, getChatMap,
 * getProcessor, getUsername, getSocket, setChatRooms, getChatsUpdate
 */
public class Client extends Thread {
	
	private ConcurrentHashMap<Chat, Conversation> conversations = new ConcurrentHashMap<Chat, Conversation>();
	private final ServerProcess processor;
	private AllUsersGUI rootWindow;
	private String username = null;
	private final Socket socket;
	
	/**
	 * Constructor method for the Client class.
	 * @param Socket, the socket used to establish the connection to the server
	 * @param ServerProcess, separate server thread to update chats from client
	 */
	public Client(Socket socket, ServerProcess processor){
		this.processor = processor;
		this.socket = socket;
	}
	
	/**
	 * Run method for the Client. Handles the connection for the client.
	 * After establishing a connection to a server, the client is prompted to enter
	 * a username.  If the server currently contains the username, the client is asked
	 * to pick another username. After choosing a valid, unused username, the clients'
	 * username is added to the server, the initial prompt is closed, and they are presented 
	 * with the AllUsersGUI.
	 */
	@Override
	public void run() {
		final UsernameGUI usernameGUI = new UsernameGUI();
        usernameGUI.getSubmit().addActionListener(new SubmitUsername(usernameGUI));
        usernameGUI.getUsername().addActionListener(new SubmitUsername(usernameGUI));
		while (!socket.isClosed()) {}			
	}
	
	/**
	 * <code> private class implements ActionListener</code>
	 * ActionListener used to submit a possible username to the server.
	 */
	private class SubmitUsername implements ActionListener{
		private final UsernameGUI usernameGUI;
		
		public SubmitUsername(UsernameGUI usernameGUI){
			this.usernameGUI = usernameGUI;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			final String regex = "[a-zA-Z0-9]+";
			username = usernameGUI.getUsername().getText();
			usernameGUI.getError().setText("\n");
			usernameGUI.getUsername().setText("");
			
    		if (username == null || !username.matches(regex)) {
    			usernameGUI.alertInvalid();
    			return;
    		}

	        //Assuring that the username has not been taken
	        if (!Server.gethashUsers().containsKey(username)) {
    			Server.gethashUsers().put(username, Client.this);
	        	usernameGUI.dispose();
    		} else {
    			usernameGUI.alertDuplicate();
    			return;
    		}
	        
			rootWindow = new AllUsersGUI(Client.this);
			Server.updateClients();
		}
	}		
	
	/**
	 * Method used to update the active users list in the AllUsersGUI for
	 * all currently connected clients. Is called when a client connects
	 * or disconnects.
	 */
	public void setUsers() {
		rootWindow.updateUsers(Server.gethashUsers().keySet());
	}
	
	/**
	 * update chatrooms. gets called from the server
	 * update rootWindow with new list of public chats
	 */
	
	public void setChatRooms(){
		rootWindow.updateChatRooms(this.getProcessor().getpublicChats());
	}
	
	/**
	 * Updates the messages in a specified chat that the client is currently in.
	 * @param chat - Chat to be updated
	 * @param sender - String of the username of the Client sending the message
	 * @param message - String of the message to be updated in the chat
	 */
	public void newMessage(Chat chat, String sender, String message) {
		conversations.get(chat).updateChatMess(sender, message);
	}
	
	/**
	 * Creates a new Conversation from a specified chat that automatically adds 
	 * the client to the active users list, and adds the chat to the client's
	 * ConcurrentHashMap<Chat, Conversation>.
	 * 
	 * @param chat - chat that will be passed into the new converation that is created
	 */
	public void newChat(Chat chat) {
		Conversation conversation = new Conversation(chat, this);
		conversation.updateActive(chat.getMembers());
		conversations.put(chat, conversation);
	}
	
	/**
	 * Updates all of the speciefied chat's list of active users, is called whenever a 
	 * user either disconnects or connects from the chat.
	 * @param chat - Chat to be updated
	 */
	public void updateChatMembers(Chat chat) {
		conversations.get(chat).updateActive(chat.getMembers());
	}
	
	/**
	 * Removes a chat from the ConcurrentHashMap of conversations. Is called
	 * whenever a chat contains no members.
	 * @param chat - Chat to be removed
	 */
	public void removeChat(Chat chat) {
		conversations.remove(chat);
	}
	
    /**
     * this client calls the server to update all clients' rootWindow
     * so that they display all active public chats
     */
	public void getChatsUpdate() {
		Server.updateChats();
	}
	
	/**
	 * Getter Methods
	 */
	public ConcurrentHashMap<Chat, Conversation> getChatMap() {
		return conversations;
	}
	
	public ServerProcess getProcessor() {
		return processor;
	}
	
	public String getUsername(){
		return username;
	}
	
	public Socket getSocket(){
		return socket;
	}

    /**
     * Start a ConnectionGUI to obtain the server information
     */
    public static void main(String[] args) {
    	new ConnectionGUI();
    }
    
}
