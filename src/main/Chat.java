package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Chat represents a conversation, used to keep track of current users
 * id: each chat has a unique AtomicInteger id
 * members: a synchronizedCollection of current users
 * <p>
 * Functions: addMember(), removeMember(), getMemberCount(), 
 * 			  getMembers(), isMember(), getID()
 */
public class Chat {
	
	private Collection<Client> members;
	private final int id;
	/**
	 * Constructor method for Chat
	 * @param id - AtomicInteger representing unique id assigned to a chat
	 */
	public Chat(AtomicInteger id) {
		members = Collections.synchronizedCollection( new ArrayList<Client>() );
		this.id = id.intValue();
	}
	
	/**
	 * Method used to add clients to the chat
	 * @param client - Client that is added to the chat
	 */
	public void addMember(Client client) {
		members.add(client);
	}

	/**
	 * Method used to remove clients from the chat
	 * @param client - Client that is removed form the chat
	 */
	public void removeMember(Client client) {
		members.remove(client);
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
	public boolean isMember(Client client) {
		return members.contains(client);
	}
	
	/**
	 * Getter methods
	 */
	public List<Client> getMembers() {
		List<Client> membersCopy = new ArrayList<Client>(members);
		return membersCopy;
	}
	
	public int getID() {
		return id;
	}
	
}
