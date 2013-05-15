package main.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import main.chat.Conversation;
import main.chat.HistoryGUI;
import main.lobby.AllUsersGUI;

/**
 * ClientSide is the client program for the chat. It runs the connection GUI, then the
 * username GUI, and finally the AllUsersGUI, historyGUI, and Conversation GUIs. Has a
 * ClientSideThread to read input from server and ClientRequest to send output to the 
 * server. Communication done through socket.
 * 
 * conversations: a ConcurrentHashMap to map chat id to Conversation GUI
 * chatMembers: a ConcurrentHashMap to map chat id to a List of its members' usernames
 * allChats: Collection of all current chats
 * allUsers: Collection of all connected users 
 */
public class ClientSide {
	
	private ConcurrentHashMap<String, Conversation> conversations = new ConcurrentHashMap<String, Conversation>();
	private ConcurrentHashMap<String, List<String>> chatMembers = new ConcurrentHashMap<String, List<String>>();
	private Collection<String> allChats = Collections.synchronizedCollection(new ArrayList<String>());
	private Collection<String> allUsers = Collections.synchronizedCollection(new ArrayList<String>());
	private final Socket socket;
	private AllUsersGUI rootWindow;
	private ClientSideThread thread = null;
	private ClientRequest request = null;
	private String username = null;
	private HistoryGUI historyGUI;
	
	/**
	 * Constructor method for the Client class.
	 * @param Socket, the socket used to establish the connection to the server
	 */
	public ClientSide(Socket socket) {
		this.socket = socket;
		
		//usernameGUI for client to select his/her username
		UsernameGUI usernameGUI = new UsernameGUI(this);
        usernameGUI.getSubmit().addActionListener(new SubmitUsername(usernameGUI));
        usernameGUI.getUsername().addActionListener(new SubmitUsername(usernameGUI));
	}
	
	/**
	 * <code> private class implements ActionListener</code>
	 * ActionListener used to submit a possible username to the server.
	 * If successful, initiates the AllUsersGUI, setting connected
	 * users and active chats. Starts ClientSideThread to handle
	 * server responses and ClientSideRequest to handle requests to 
	 * the server.
	 */
	private class SubmitUsername implements ActionListener{
		private final UsernameGUI usernameGUI;
		
		public SubmitUsername(UsernameGUI usernameGUI){
			this.usernameGUI = usernameGUI;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
	            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				String regex = "[a-zA-Z0-9]+";
				
				username = usernameGUI.getUsername().getText();
				usernameGUI.getError().setText("\n");
				usernameGUI.getUsername().setText("");
				//validate username syntax
	    		if (username == null || !username.matches(regex)) {
	    			usernameGUI.alertInvalid();
	    			return;
	    		}
		        //validate username uniqueness
	            out.println("username " + username);
	            String response = in.readLine();
	            String[] tokens = response.split("\\s+");
	            
		        if (tokens[0].equals("start")) {
		        	//Set AllUsersGUI, users, chats from server message
					rootWindow = new AllUsersGUI(ClientSide.this);
					List<String> users = new ArrayList<String>();
					List<String> chats = new ArrayList<String>();
					boolean mark = false;
					for (int i = 1; i < tokens.length; i++) {
						if (tokens[i].equals("?")) {
							mark = true;
							continue;
						}
						if (!mark)
							users.add(tokens[i]);
						else
							chats.add(tokens[i]);
					}
					allUsers.addAll(users);
					setUsers();
					allChats.addAll(chats);
					setChatRooms();
		        	usernameGUI.dispose();
		    		
		        	//Start ClientRequest, ClientSideThread
		        	thread = new ClientSideThread(new BufferedReader(new InputStreamReader(socket.getInputStream())), ClientSide.this);
		    		thread.start();
		    		request = new ClientRequest(new PrintWriter(socket.getOutputStream(), true), ClientSide.this);
		    		request.start();	
	    		} else {
	    			//prompt for another username
	    			usernameGUI.alertDuplicate();
	    			return;
	    		}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}		
	
	/**
	 * Method used to update the active users list in the AllUsersGUI. 
	 */
	public void setUsers() {
		rootWindow.updateUsers(allUsers);
	}
	
	/**
	 * Method used to add a user to active user list in AllUsersGUI.
	 * @param user - username of user being added, a string
	 */
	public void addUser(String user) {
		allUsers.add(user);
		setUsers();
	}
	
	/**
	 * Method used to remove a user from active user list in AllUsersGUI.
	 * @param user - username of user being removed, a string
	 */
	public void removeUser(String user) {
		allUsers.remove(user);
		setUsers();
	}
	
	/**
	 * Method used to update list of created chat rooms in AllUsersGUI.
	 */
	public void setChatRooms() {
		rootWindow.updateChatRooms(allChats);
	}
	
	/**
	 * Method used to add a chat room to the list of chat rooms in AllUsersGUI.
	 * @param id - id of chat room being added, a string
	 */
	public void addChatRoom(String id) {
		allChats.add(id);
		setChatRooms();
	}
	
	/**
	 * Creates a new Conversation from a specified chat and adds the info to the 
	 * client's ConcurrentHashMap<String, Conversation>.
	 * @param id -  id of the chat representing the new conversation, a string
	 */
	public void newChat(String id) {
		Conversation conversation = new Conversation(id, this);
		conversations.put(id, conversation);
	}
	
	/**
	 * Updates the messages in a specified chat that the client is currently in.
	 * @param id - id of chat to be updated, a string
	 * @param sender - String of the username of the Client sending the message
	 * @param message - String of the message to be updated in the chat
	 */
	public void newMessage(String sender, String message, String id) {
		conversations.get(id).updateChatMess(sender, message);
	}
	
	/**
	 * Displays the creator of a chat in AllUsersGUI.
	 * @param creator - username of creator of chat to be displayed, a string
	 */
	public void showCreator(String creator) {
		rootWindow.displayCreator(creator);
	}
	
	/**
	 * Open a new historyGUI.
	 */
	public void showHistory() {
		setHistoryGUI(new HistoryGUI(this.getChatMap()));
	}
	
	/**
	 * Sets historyGUI for this client
	 * @param historyGUI - historyGUI being set
	 */
	public void setHistoryGUI(HistoryGUI historyGUI) {
		this.historyGUI = historyGUI;
	}
	
	/**
	 * Updates the chat history for a specific chat, as a single string. 
	 * @param id - id of chat whose history will be updated, a string
	 * @param index - index where history starts in server message, an int
	 * @param history - server message with chat history, a string array
	 */
	public void updateHistory(String id, int index, String[] history) {
		StringBuilder historyString = new StringBuilder("");
		for (int i = index; i < history.length-1; i++) {
			historyString.append(history[i] + " ");
			if (i < history.length-2) {
				if (history[i+1].contains(":")) 
					historyString.append("\r\n");
			}
		}
		historyString.append("\r\n");
		conversations.get(id).setHistory(historyString.toString());
	}
	
	/**
	 * Updates the specified chat's list of active users.
	 * @param id - id of chat to be updated, a string
	 * @param members - List of members in chat by username
	 */
	public void updateChatMembers(String id, List<String> members) {
		conversations.get(id).updateActive(members);
	}
	
	/**
	 * Initializes chat members for a specific chat in chatMembers hashMap.
	 * Then, updates chat members in that chat. 
	 * @param id - id of chat being added, a string
	 * @param members - List of members in chat, by username
	 */
	public void setChatMembers(String id, List<String> members) {
		chatMembers.put(id, members);
		updateChatMembers(id, chatMembers.get(id));
	}
	
	/**
	 * Adds a user to active members in chat, updates chat members.
	 * @param client - username of client being added to chat, a string
	 * @param id - id of chat from where client is added, a string
	 */
	public void addToChat(String client, String id) {
		chatMembers.get(id).add(client);
		updateChatMembers(id, chatMembers.get(id));
	}
	
	/**
	 * Removes a user from active members in chat, updates chat members.
	 * @param client - username of client being removed from chat, a string
	 * @param id - id of chat from where client is removed, a string
	 */
	public void removeFromChat(String client, String id) {
		chatMembers.get(id).remove(client);
		updateChatMembers(id, chatMembers.get(id));
	}
	
	/**
	 * Closes all the client's conversation windows.
	 */
	public void removeAllChats() {
		Collection<Conversation> convos = conversations.values(); 
		for (Conversation convo: convos) {
			convo.dispose();
		}
	}
	
	/**
	 * Getter Methods
	 */
	public ConcurrentHashMap<String, Conversation> getChatMap() {
		return conversations;
	}
	
	public String getUsername(){
		return username;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public ClientRequest getRequest() {
		return request;
	}
	
	public HistoryGUI getHistoryGUI() {
		return historyGUI;
	}

    /**
     * Start a ConnectionGUI to obtain the server information
     */
    public static void main(String[] args) {
    	final ConnectionGUI con = new ConnectionGUI();
    	
    	class ConnectionSend implements ActionListener{
        	ConnectionGUI con;
        	
        	public ConnectionSend(ConnectionGUI con){
        		this.con = con;
        	}

    		@Override
    		public void actionPerformed(ActionEvent arg0) {
    			try {
    				//validate port input
    				int portValue = Integer.parseInt(con.getPort().getText());
    				if (portValue < 0 || portValue > 65535)
    					throw new IOException();
    				//create socket connection
    				new ClientSide(new Socket(con.getIP().getText(), portValue));
    				con.dispose();
    			} catch (UnknownHostException ue) {
    				con.getIP().setText("");
    				con.getPort().setText("");
    				con.getError().setText("Invalid IP.");
    			} catch (IOException ioe) {
    				con.getIP().setText("");
    				con.getPort().setText("");
    				con.getError().setText("Invalid port.");
    			} catch (NumberFormatException ne) {
    				con.getIP().setText("");
    				con.getPort().setText("");
    				con.getError().setText("Invalid port.");
    			}
    		}    	
        }
    	
		con.getSubmit().addActionListener(new ConnectionSend(con));
		con.getPort().addActionListener(new ConnectionSend(con));
    }
    
}