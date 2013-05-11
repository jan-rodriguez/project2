package main;

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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSide {
	
	private ConcurrentHashMap<String, Conversation> conversations = new ConcurrentHashMap<String, Conversation>();
	private ConcurrentHashMap<String, List<String>> chatMembers = new ConcurrentHashMap<String, List<String>>();
	private List<String> allChats = new ArrayList<String>();
	private List<String> allUsers = new ArrayList<String>();
	private final Socket socket;
	private AllUsersGUI rootWindow;
	private ClientSideThread thread = null;
	private ClientRequest request = null;
	private String username = null;
	
	/**
	 * Constructor method for the Client class.
	 * @param Socket, the socket used to establish the connection to the server
	 * @param ServerProcess, separate server thread to update chats from client
	 */
	public ClientSide(Socket socket) {
		this.socket = socket;
		
		UsernameGUI usernameGUI = new UsernameGUI();
        usernameGUI.getSubmit().addActionListener(new SubmitUsername(usernameGUI));
        usernameGUI.getUsername().addActionListener(new SubmitUsername(usernameGUI));
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
			try {
	            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				String regex = "[a-zA-Z0-9]+";
				
				username = usernameGUI.getUsername().getText();
				usernameGUI.getError().setText("\n");
				usernameGUI.getUsername().setText("");
				
	    		if (username == null || !username.matches(regex)) {
	    			usernameGUI.alertInvalid();
	    			return;
	    		}
		        //Assuring that the username has not been taken
	            out.println("username " + username);
	            String response = in.readLine();
	            String[] tokens = response.split("\\s+");
	            
		        if (tokens[0].equals("start")) {
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
		    		
		        	thread = new ClientSideThread(socket, ClientSide.this);
		    		thread.start();
		    		
		    		request = new ClientRequest(socket, ClientSide.this);
		    		request.start();	
	    		} else {
	    			usernameGUI.alertDuplicate();
	    			return;
	    		}
		        
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}		
	
	/**
	 * Method used to update the active users list in the AllUsersGUI for
	 * all currently connected clients. Is called when a client connects
	 * or disconnects.
	 */
	public void setUsers() {
		rootWindow.updateUsers(allUsers);
	}
	
	public void addUser(String user) {
		allUsers.add(user);
		setUsers();
	}
	
	public void removeUser(String user) {
		allUsers.remove(user);
		setUsers();
	}
	
	/**
	 * update chatrooms. gets called from the server
	 * update rootWindow with new list of public chats
	 */
	
	public void setChatRooms(){
		rootWindow.updateChatRooms(allChats);
	}
	
	public void addChatRoom(String id) {
		allChats.add(id);
		setChatRooms();
	}
	
	/**
	 * Updates the messages in a specified chat that the client is currently in.
	 * @param chat - Chat to be updated
	 * @param sender - String of the username of the Client sending the message
	 * @param message - String of the message to be updated in the chat
	 */
	public void newMessage(String sender, String message, String id) {
		conversations.get(id).updateChatMess(sender, message);
	}
	
	/**
	 * Creates a new Conversation from a specified chat that automatically adds 
	 * the client to the active users list, and adds the chat to the client's
	 * ConcurrentHashMap<Chat, Conversation>.
	 * 
	 * @param chat - chat that will be passed into the new converation that is created
	 */
	public void newChat(String id) {
		Conversation conversation = new Conversation(id, this);
		conversations.put(id, conversation);
	}
	
	public void updateHistory(String id, int index, String[] history) {
		String historyString = "";
		for (int i = index; i < history.length-1; i++) {
			historyString += history[i] + " ";
			if (i < history.length-2) {
				if (history[i+1].contains(":")) 
					historyString += "\r\n";
			}
		}
		historyString += "\r\n";
		conversations.get(id).setHistory(historyString);
	}
	
	/**
	 * Updates all of the specified chat's list of active users, is called whenever a 
	 * user either disconnects or connects from the chat.
	 * @param chat - Chat to be updated
	 */
	public void updateChatMembers(String id, List<String> members) {
		conversations.get(id).updateActive(members);
	}
	
	public void setChatMembers(String id, List<String> members) {
		chatMembers.put(id, members);
		updateChatMembers(id, chatMembers.get(id));
	}
	
	/**
	 * Removes a chat from the ConcurrentHashMap of conversations. Is called
	 * whenever a chat contains no members.
	 * @param chat - Chat to be removed
	 */
	public void removeChat(String id) {
		conversations.remove(id);
	}
	
	public void removeFromChat(String client, String id) {
		chatMembers.get(id).remove(client);
		updateChatMembers(id, chatMembers.get(id));
	}
	
	public void addToChat(String client, String id) {
		chatMembers.get(id).add(client);
		updateChatMembers(id, chatMembers.get(id));
	}
	
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

    /**
     * Start a ConnectionGUI to obtain the server information
     */
    public static void main(String[] args) {
    	final ConnectionGUI con = new ConnectionGUI();
    	
		con.getSubmit().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				try {
					int portValue = Integer.parseInt(con.getPort().getText());
					if (portValue < 0 || portValue > 65535)
						throw new IOException();
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
		});
		
		con.getPort().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				try {
					int portValue = Integer.parseInt(con.getPort().getText());
					if (portValue < 0 || portValue > 65535)
						throw new IOException();
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
		});
    }
    
}