package main.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClientSideThread has a BufferedReader on the socket to receive
 * input and call the appropriate methods and update the GUIs, for
 * the client.
 */
public class ClientSideThread extends Thread {

	private final ClientSide client;
	private BufferedReader in = null;

	/**
	 * Constructor method for ClientSideThread. Creates BufferedReader for socket.
	 */
	public ClientSideThread(BufferedReader reader, ClientSide client) {
		this.client = client;
		this.in = reader;
	}
	
	@Override
	/**
	 * Run method for the ClientSideThread. Reads input from server through
	 * the socket and takes appropriate measures to update GUIs or client state.
	 */
	public void run() {
		try {	
	        for (String line = in.readLine(); line != null; line=in.readLine()) {        
				String[] tokens = line.split("\\s+");
	     
				if (tokens[0].equals("connect")) {
					// connect username
					client.addUser(tokens[1]);
				} else if (tokens[0].equals("create")) {
					// create chat
					client.addChatRoom(tokens[1]);
				} else if (tokens[0].equals("add")) {
					// add username chat
					client.addToChat(tokens[1], tokens[2]);
				} else if (tokens[0].equals("new")) {
					// new username* ? (username: message)* chat
					client.newChat(tokens[tokens.length-1]);
					//retrieve current members in chat
					List<String> members = new ArrayList<String>();
					int count = tokens.length;
					for (int i = 1; i < tokens.length-1; i++) {
						//check for end of member sequence
						if (tokens[i].equals("?")) {
							count = i;
							break;
						}
						members.add(tokens[i]);
					}
					//if applicable, retrieve and update chat history, set chat members
					if (count < tokens.length-2)
						client.updateHistory(tokens[tokens.length-1], count+1, tokens);
					client.setChatMembers(tokens[tokens.length-1], members);
				} else if (tokens[0].equals("post")) {
					// post username message chat
					StringBuilder message = new StringBuilder("");
					for (int i = 2; i < tokens.length-1; i++) {
						message.append(tokens[i] + " ");
					}
					client.newMessage(tokens[1], message.toString().trim(), tokens[tokens.length-1]);
				} else if (tokens[0].equals("disconnect")) {
					// disconnect username
					client.removeUser(tokens[1]);
				} else if (tokens[0].equals("leave")) {
					// leave username chat
					client.removeFromChat(tokens[1], tokens[2]);
				} else if(tokens[0].equals("creator")) {
					// creator username
					client.showCreator(tokens[1]);
				}
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
