package main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * serverProcess is a thread of server. It gets started when server is created
 * BlockingQueue: receives chat actions from the GUI (see GUIs and Action class)
 * AtomicInteger chatNumber: used to get each new chat a unique id
 * hashChats: a ConcurrentHashMap to map chatNumber to Chat
 * <p>
 * Functions: addPublicChat, addPrivateChat, newChat, leaveConversation,
 * 			  invite, sendMessage, notMember
 */
public class ServerProcess extends Thread {
	
	private static ConcurrentHashMap<Integer, Chat> hashChats = new ConcurrentHashMap<Integer, Chat>();
	private static AtomicInteger chatNumber = new AtomicInteger();
	private final BlockingQueue<Action> queue;
	private static List<String> publicChats = Collections.synchronizedList(new ArrayList<String>());
	
	/**
	 * Constructor method for ServerProcess. Instantiates the blocking queue.
	 */
	public ServerProcess() {
		this.queue = new LinkedBlockingQueue<Action>();
	}
	
	@Override
	/**
	 * Run method for the ServerProcess. Accesses actions from the blocking queue
	 * and runs the actions specified by the current action input.
	 */
	public void run() {

		while (true) {
			try {
				Action action = queue.take();
				
				if (action.getAction().equals("invite")) {
					if (notMember(action.getChat(), action.getClient().getUsername())) {
						action.getChat().addMember(action.getClient());
						action.getClient().newChat(action.getChat());
						for (Client client: action.getChat().getMembers()) {
							client.updateChatMembers(action.getChat());
						}
					}
				} else if (action.getAction().equals("post")) {
					for (Client client: action.getChat().getMembers()) {
						client.newMessage(action.getChat(), action.getClient().getUsername(), action.getMessage());
					}
				} else if (action.getAction().equals("leave")) {
					action.getChat().removeMember(action.getClient());
					action.getClient().removeChat(action.getChat());
					
					if (action.getChat().getMemberCount() == 0){
						hashChats.remove(action.getChat().getID());
						publicChats.remove(String.valueOf(action.getChat().getID()));
						action.getClient().getChatsUpdate();
					} else {
						for (Client client: action.getChat().getMembers()) {
							client.updateChatMembers(action.getChat());
						}
					}
				} else if (action.getAction().equals("newPublic")) {
					addPublicChat(action.getClient());
				} else if (action.getAction().equals("newPrivate")){
					addPrivateChat(action.getClient());
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Method used to add a chat to the ServerProcess. Instantiates the new chat, and
	 * adds the chat to the ConcurrentHashMap<AtomicInteger, Chat> that keeps track of
	 * each unique chat.
	 * the ID is also added to publicChats list to keep track of all public chats
	 * @param client - Client that sent the addChat command
	 */
	
	public static void addPublicChat(Client client) {
		Chat chat = new Chat(chatNumber);
		chatNumber.getAndIncrement();
		chat.addMember(client);
		client.newChat(chat);
		hashChats.put(chat.getID(), chat);	
		publicChats.add(String.valueOf(chat.getID()));
		client.getChatsUpdate();
	}
	
	/**
	 * Similar to addPublicChat, except that the id of the conversation
	 * is NOT added to publicChats, i.e. not all users can join private conversations
	 * @param client
	 */
	public static void addPrivateChat(Client client) {
		Chat chat = new Chat(chatNumber);
		chatNumber.getAndIncrement();
		chat.addMember(client);
		client.newChat(chat);
		hashChats.put(chat.getID(), chat);	
	}

	/**
	 * Method used to add the addPublicChat action to the queue.
	 * @param client - Client that requested a new Chat
	 */
	public void newPublicChat(Client client) {
		queue.offer(new Action(null, client, "newPublic", null));
	}
	
	/**
	 * Method used to add the addPrivateChat action to the queue.
	 * @param client - Client that requested a new Chat
	 */
	public void newPrivateChat(Client client) {
		queue.offer(new Action(null, client, "newPrivate", null));
	}
	
	/**
	 * Method used to invite new clients to a pre-existing chat.
	 * <p>
	 * Separates the string of users by commas, and iterates through the array
	 * adding members that are not currently member of the chat to the chat.
	 * @param chat - Chat to add clients in
	 * @param users - name of all users to be added to chat
	 */
	public void invite(Chat chat, String users) {
		String userString = users.replaceAll("\\s+", "");
		String[] userArray = userString.split(",");
		for (String user: userArray) {
			if (notMember(chat, user)) {
				Client client = Server.gethashUsers().get(user);
				queue.offer(new Action(chat, client, "invite", null));
			}
		}
	}
	
	/**
	 * Method used to add the leave action to the queue and remove a client from a specified chat
	 * @param chat - Chat for the client to be removed
	 * @param client - Client to be removed form the specified chat
	 */
	public void leaveConversation(Chat chat, Client client) {
		queue.offer(new Action(chat, client, "leave", null));
	}
	
	/**
	 * Method used to add the post method to a specified chat from a Client.
	 * <p>
	 * Updates the specified chat's messages by appending the client's username followed
	 * by the message to the message text field.
	 * @param chat - Chat to be updated
	 * @param client - Client that sent the message
	 * @param message - String representing the message
	 */
	public void sendMessage(Chat chat, Client client, String message) {
		queue.offer(new Action(chat, client, "post", message));
	}
	
	/**
	 * Method used to check whether a client is a member of a specified chat.
	 * @param chat - Chat to check whether the client is a member
	 * @param user - String representing the Client's username
	 * @return boolean - true if the user is not a member of the chat, false otherwise
	 */
	public boolean notMember(Chat chat, String user) {
		Client client = Server.gethashUsers().get(user);
		if (client != null)
			return !chat.isMember(client);
		return false;
	}

	/**
	 * join takes idString to look up the chat and
	 * invite user to that chat
	 * @param idString
	 * @param user
	 */
	public void join(String idString, String user) {
		Chat chat = hashChats.get(Integer.parseInt(idString));
		invite(chat, user);
	}
	
	/**
	 * Get publicChats (public conversations)
	 * @return publicChats
	 */
	public List<String> getpublicChats(){
		return publicChats;
	}
	
	/**
	 * gets called from AllUsersGUI when a chat room is selected
	 * @param idString
	 * @return String username of the creator of the chat room
	 */
	public String getCreator(String idString){
		Chat currentChat = hashChats.get(Integer.parseInt(idString));
		return currentChat.getMembers().get(0).getUsername();
	}

}
