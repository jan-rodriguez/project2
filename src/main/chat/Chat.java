package main.chat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Chat represents a conversation, used to keep track of current users
 * members: a synchronizedCollection of current users
 * history: a synchronizedCollection of all messages sent
 * creator: a string representing the username of the chat creator
 * id: each chat has a unique integer id
 * 
 * 
 */
public class Chat {
	
	private Collection<String> history;
	private Collection<String> members;
	private final String creator;
	private final int id;
	
	/**
	 * Constructor method for Chat
	 * @param id - AtomicInteger representing unique id assigned to a chat
	 * @param creator - username of chat creator, a string
	 */
	public Chat(AtomicInteger id, String creator) {
		history = Collections.synchronizedCollection( new ArrayList<String>() );
		members = Collections.synchronizedCollection( new ArrayList<String>() );
		this.id = id.intValue();
		this.creator = creator;
	}
	
	/**
	 * Method used to add clients to the chat
	 * @param username - Username of client that is added to the chat
	 */
	public void addMember(String username) {
		members.add(username);
	}

	/**
	 * Method used to remove clients from the chat
	 * @param username - Username of client that is removed form the chat
	 */
	public void removeMember(String username) {
		members.remove(username);
	}
	
	/**
	 * Method used to get the current number of clients in a chat
	 * @return int - number of people in the current chat
	 */
	public int getMemberCount() {
		return members.size();
	}
	
	/**
	 * Method used to check if a client is an active member of a chat
	 * @param client - Client checked whether is an active user in the chat
	 * @return boolean - true if the client is a member of the chat, false otherwise
	 */
	public boolean isMember(String username) {
		return members.contains(username);
	}
	
	/**
	 * Method to add a sender and message to the chat history
	 * @param sender - username of person sending message, a string
	 * @param message - message sent, a string
	 */
	public void addHistory(String sender, String message) {
		history.add(sender.trim() + ": " + message.trim());
	}
	
	/**
	 * Getter methods
	 */
	public Collection<String> getMembers() {
		return members;
	}
	
	public Collection<String> getHistoryFromChat() {
		return history;
	}
	
	public int getID() {
		return id;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public String getHistoryString() {
		StringBuilder result = new StringBuilder("");
		for (String line: history) {
			result.append(line + " ");
		}
		return result.toString();
	}
	
}
