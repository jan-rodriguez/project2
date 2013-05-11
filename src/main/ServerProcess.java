package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
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
	
	private static ConcurrentHashMap<String, Socket> hashUsers = new ConcurrentHashMap<String, Socket>();
	private static ConcurrentHashMap<String, Chat> hashChats = new ConcurrentHashMap<String, Chat>();
	private static AtomicInteger chatNumber = new AtomicInteger();
	private final BlockingQueue<Object[]> queue;

	/**
	 * Constructor method for ServerProcess. Instantiates the blocking queue.
	 */
	public ServerProcess() {
		this.queue = new LinkedBlockingQueue<Object[]>();
	}
	
	@Override
	/**
	 * Run method for the ServerProcess. Accesses actions from the blocking queue
	 * and runs the actions specified by the current action input.
	 */
	public void run() {
		PrintWriter out = null;
		try {
			while (true) {
				Object[] action = queue.take();
				String[] tokens = ((String) action[0]).split("\\s+");
				Socket socket = (Socket) action[1];
				
		        out = new PrintWriter(socket.getOutputStream(), true);
				
		        if (tokens[0].equals("username")) {
		            if (!hashUsers.containsKey(tokens[1])) {
		            	hashUsers.put(tokens[1], socket);
		            	out.println("start " + getAllUsers() + " ? " + getAllChats());   

		            	Collection<Socket> sockets = hashUsers.values();
		            	for (Socket subSocket: sockets) {
		            		if(!subSocket.equals(hashUsers.get(tokens[1]))){
						        PrintWriter subOut = new PrintWriter(subSocket.getOutputStream(), true);
						        subOut.println("connect " + tokens[1]);
		            		}
		            	}
		            } else {
		            	out.println("abort");
		            }
		        } else if (tokens[0].equals("new")) {
		    		Chat chat = new Chat(chatNumber);
		    		chatNumber.getAndIncrement();
		    		chat.addMember(tokens[1]);
		    		hashChats.put("" + chat.getID(), chat);	
		    		out.println("new " + tokens[1] + " " + chat.getID());
	            	Collection<Socket> sockets = hashUsers.values();
	            	for (Socket subSocket: sockets) {
				        PrintWriter subOut = new PrintWriter(subSocket.getOutputStream(), true);
				        subOut.println("create " + chat.getID());
	            	}
		        } else if (tokens[0].equals("post")) {
		        	System.out.println(tokens[tokens.length-1]);
		        	Chat chat = hashChats.get(tokens[tokens.length-1]);
		        	System.out.println(chat);
		        	Collection<String> members = chat.getMembers();
		        	
			        StringBuilder message = new StringBuilder("");
			        for (int i = 2; i < tokens.length-1; i++) {
			        	message.append(tokens[i] + " ");
			        }
			        
		        	for (String member: members) {
		        		Socket subSocket = hashUsers.get(member);
				        PrintWriter subOut = new PrintWriter(subSocket.getOutputStream(), true);
				        subOut.println("post " + tokens[1] + " " + message + " " + tokens[tokens.length-1]);
		        	}
		        	
		        	chat.addHistory(tokens[1], message.toString());
		        } else if (tokens[0].equals("invite")) {
		        	Chat chat = hashChats.get(tokens[tokens.length-1]);

		        	for (int i = 1; i < tokens.length-1; i++) {
		        		String token = tokens[i].replace(",", "");

		        		if (!hashUsers.containsKey(token))
		        			continue;
		        		
		        		if (chat.isMember(token)) 
		        			continue;
		        		
		        		Socket subSocket = hashUsers.get(token);
		        		chat.addMember(token);
		        		
		        		StringBuilder memberList = new StringBuilder("");
		        		Collection<String> members = chat.getMembers();
			        	for (String member: members) {
			        		memberList.append(member + " ");
			        		if (!member.equals(token)) {
				        		Socket theSocket = hashUsers.get(member);
				        		PrintWriter theOut = new PrintWriter(theSocket.getOutputStream(), true);
						        theOut.println("added " + token + " " + chat.getID());
			        		}
			        	}
			        	
			    		PrintWriter subOut = new PrintWriter(subSocket.getOutputStream(), true);
				        subOut.println("new " + memberList + " ? " + chat.getHistoryString() + " " + chat.getID());
		        	}
		        } else if (tokens[0].equals("disconnect")) {
		        	for (int i = 2; i < tokens.length; i++) {
		        		Chat chat = hashChats.get(tokens[i]);
		        		chat.removeMember(tokens[1]);
			        	Collection<String> members = chat.getMembers();
			        	for (String member: members) {
			        		Socket subSocket = hashUsers.get(member);
					        PrintWriter subOut = new PrintWriter(subSocket.getOutputStream(), true);
					        subOut.println("leave " + tokens[1] + " " + chat.getID());
			        	}
		        	}
		        	Collection<Socket> sockets = hashUsers.values();
		        	for (Socket subSocket: sockets) {
				        PrintWriter subOut = new PrintWriter(subSocket.getOutputStream(), true);
				        subOut.println("disconnect " + tokens[1]);
		        	}
		        	out.close();
		        	socket.close();
		        } else if (tokens[0].equals("leave")) {
		        	Chat chat = hashChats.get(tokens[2]);
					chat.removeMember(tokens[1]);

					Collection<String> members = chat.getMembers();
					for (String member: members) {
						Socket subSocket = hashUsers.get(member);
				        PrintWriter subOut = new PrintWriter(subSocket.getOutputStream(), true);
				        subOut.println("leave " + tokens[1] + " " + tokens[2]);
					}
		        }
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addLine(String line, Socket socket) {
		queue.offer(new Object[] {line, socket});
	}
	
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
