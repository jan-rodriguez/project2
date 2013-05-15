package main.server;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import main.chat.Chat;

/**
 * ServerProcess is initialized when server is created. It handles the clients' requests
 * by storing them in a blocking queue and then addressing them one by one. 
 * 
 * BlockingQueue: receives chat actions from the GUI (see GUIs and Action class)
 * AtomicInteger chatNumber: used to get each new chat a unique id
 * hashUsers: a ConcurrentHashMap to map username to Socket
 * hashChats: a ConcurrentHashMap to map chat id to Chat
 */
public class ServerProcess extends Thread {
	
	private static ConcurrentHashMap<String, PrintWriter> hashUsers = new ConcurrentHashMap<String, PrintWriter>();
	private static ConcurrentHashMap<String, Chat> hashChats = new ConcurrentHashMap<String, Chat>();
	private static AtomicInteger chatNumber = new AtomicInteger();
	private final BlockingQueue<Action> queue;

	/**
	 * Constructor method for ServerProcess. Instantiates the blocking queue.
	 */
	public ServerProcess() {
		this.queue = new LinkedBlockingQueue<Action>();
	}
	
	@Override
	/**
	 * Run method for the ServerProcess. Accesses actions from the blocking queue
	 * and takes the actions specified by the current action input.
	 */
	public void run() {
		try {
			while (true) {
				Action action = queue.take();
				String[] tokens = action.getText().split("\\s+");
				
		        if (tokens[0].equals("username")) {
		        	// username username
		        	PrintWriter out = action.getWriter();
		        	//add user, writer to hashUsers
		            if (!hashUsers.containsKey(tokens[1])) {
		            	hashUsers.put(tokens[1], out);
		            	//notify user of valid username
		            	out.println("start " + getAllUsers() + " ? " + getAllChats());   

		            	//notify all users of new user's connection
		            	Collection<PrintWriter> writers = hashUsers.values();
		            	for (PrintWriter writer: writers) {
		            		if(!out.equals(writer)) {
						        writer.println("connect " + tokens[1]);
		            		}
		            	}
		            } else {
		            	//notify user of invalid username
		            	out.println("abort");
		            }
		        } else if (tokens[0].equals("new")) {
		        	// new username chat
		        	//create chat, add user, add chat to hashChats
		    		Chat chat = new Chat(chatNumber, tokens[1]);
		    		chatNumber.getAndIncrement();
		    		chat.addMember(tokens[1]);
		    		hashChats.put("" + chat.getID(), chat);	
		    		//notify user of new chat
		    		hashUsers.get(tokens[1]).println("new " + tokens[1] + " " + chat.getID());
		    		//notify all users of new chat
	            	Collection<PrintWriter> writers = hashUsers.values();
	            	for (PrintWriter writer: writers) {
				        writer.println("create " + chat.getID());
	            	}
		        } else if (tokens[0].equals("post")) {
		        	// post username message chat
		        	//retrieve chat
		        	Chat chat = hashChats.get(tokens[tokens.length-1]);
		        	Collection<String> members = chat.getMembers();
		        	//retrieve message
			        StringBuilder message = new StringBuilder("");
			        for (int i = 2; i < tokens.length-1; i++) {
			        	message.append(tokens[i] + " ");
			        }
			        //notify all members of new message
		        	for (String member: members) {
		        		hashUsers.get(member).println("post " + tokens[1] + " " + message + " " + tokens[tokens.length-1]);
		        	}
		        	//add message to chat history
		        	chat.addHistory(tokens[1], message.toString());
		        } else if (tokens[0].equals("invite")) {
		        	// invite username+ chat
		        	Chat chat = hashChats.get(tokens[tokens.length-1]);
		        	//iterate through users to be added
		        	for (int i = 1; i < tokens.length-1; i++) {
		        		String token = tokens[i].replace(",", "");
		        		//check if user connected
		        		if (!hashUsers.containsKey(token))
		        			continue;
		        		//check if user already in chat
		        		if (chat.isMember(token)) 
		        			continue;
		        		//add member
		        		chat.addMember(token);
		        		//iterate through chat members
		        		StringBuilder memberList = new StringBuilder("");
		        		Collection<String> members = chat.getMembers();
			        	for (String member: members) {
			        		memberList.append(member + " ");
			        		//notify all chat members of user's addition
			        		if (!member.equals(token)) {
				        		hashUsers.get(member).println("add " + token + " " + chat.getID());
			        		}
			        	}
			        	//notify user of new chat, current members and history
			        	hashUsers.get(token).println("new " + memberList + " ? " + chat.getHistoryString() + " " + chat.getID());
		        	}
		        } else if (tokens[0].equals("disconnect")) {
		        	// disconnect username chat+
		        	//iterate through user's chats, removing user
		        	for (int i = 2; i < tokens.length; i++) {
		        		Chat chat = hashChats.get(tokens[i]);
		        		chat.removeMember(tokens[1]);
		        		//notify chat members of users removal
			        	Collection<String> members = chat.getMembers();
			        	for (String member: members) {
			        		hashUsers.get(member).println("leave " + tokens[1] + " " + chat.getID());
			        	}
		        	}
		        	//notify all users of disconnection 
		        	Collection<PrintWriter> writers = hashUsers.values();
		        	for (PrintWriter writer: writers) {
		        		writer.println("disconnect " + tokens[1]);
		        	}
		        	//close user's writer, remove from hashUsers
		        	hashUsers.get(tokens[1]).close();
		        	hashUsers.remove(tokens[1]);
		        } else if (tokens[0].equals("leave")) {
		        	// leave username chat
		        	//remove user from chat
		        	Chat chat = hashChats.get(tokens[2]);
					chat.removeMember(tokens[1]);
					//notify chat members of user's departure
					Collection<String> members = chat.getMembers();
					for (String member: members) {
						hashUsers.get(member).println("leave " + tokens[1] + " " + tokens[2]);
					}
		        } else if (tokens[0].equals("creator")) {
		        	// creator username ChatNumber
		        	hashUsers.get(tokens[1]).println("creator " + hashChats.get(tokens[2]).getCreator());
		        }
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add Action to the BlockingQueue, with the given text and socket.
	 * @param line - message being added, a string
	 * @param socket - socket through which message was received
	 */
	public void addLine(String line, PrintWriter writer) {
		queue.offer(new Action(line, writer));
	}
	
	/**
	 * Getter for users, chats as strings.
	 */
	public String getAllUsers() {
    	Collection<String> users = hashUsers.keySet();
    	StringBuilder userString = new StringBuilder("");
    	for (String user: users) {
    		userString.append(user + " ");
    	}
    	return userString.toString().trim();
	}
	
	public String getAllChats() {
		Collection<String> chats = hashChats.keySet();
    	StringBuilder chatString = new StringBuilder("");
    	for (String chat: chats) {
    		chatString.append(chat + " ");
    	}
    	return chatString.toString().trim();
	}

}
