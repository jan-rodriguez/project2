package main;

/**
 * Instances of Action class are put in serverProcess' BlockingQueue<Action>,
 * representing actions from GUIs
 * 
 * Takes in Chat, Client, usernames, and messages 
 * 
 * Functions: getChat, getUser, getAction, getMessage
 *
 */
public class Action {

	private final Chat chat;
	private final Client client;
	private final String action;
	private final String message;
	
	public Action(Chat chat, Client user, String action, String message) {
		this.chat = chat;
		this.client = user;
		this.action = action;
		this.message = message;
	}

	/**
	 * Getter Methods
	 */
	public Chat getChat() {
		return chat;
	}

	public Client getClient() {
		return client;
	}

	public String getAction() {
		return action;
	}

	public String getMessage() {
		return message;
	}
}
