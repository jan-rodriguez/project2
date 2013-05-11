package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientSideThread extends Thread {

	private final Socket socket;
	private final ClientSide client;

	/**
	 * Constructor method for ServerProcess. Instantiates the blocking queue.
	 */
	public ClientSideThread(Socket socket, ClientSide client) {
		this.client = client;
		this.socket = socket;
	}
	
	@Override
	/**
	 * Run method for the ServerProcess. Accesses actions from the blocking queue
	 * and runs the actions specified by the current action input.
	 */
	public void run() {
		try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
	        for (String line = in.readLine(); line != null; line=in.readLine()) {        
				String[] tokens = line.split("\\s+");
	     
				if (tokens[0].equals("connect")) {
					client.addUser(tokens[1]);
				}else if (tokens[0].equals("create")) {
					client.addChatRoom(tokens[1]);
				} else if (tokens[0].equals("added")) {
					client.addToChat(tokens[1], tokens[2]);
				} else if (tokens[0].equals("new")) {
					client.newChat(tokens[tokens.length-1]);
					List<String> members = new ArrayList<String>();
					int count = tokens.length;
					for (int i = 1; i < tokens.length-1; i++) {
						if (tokens[i].equals("?")) {
							count = i;
							break;
						}
						members.add(tokens[i]);
					}
					if (count < tokens.length-2)
						client.updateHistory(tokens[tokens.length-1], count+1, tokens);
					client.setChatMembers(tokens[tokens.length-1], members);
				} else if (tokens[0].equals("post")) {
					String message = "";
					for (int i = 2; i < tokens.length-1; i++) {
						message += tokens[i] + " ";
					}
					client.newMessage(tokens[1], message.trim(), tokens[tokens.length-1]);
				} else if (tokens[0].equals("disconnect")) {
					client.removeUser(tokens[1]);
				} else if (tokens[0].equals("leave")) {
					client.removeFromChat(tokens[1], tokens[2]);
				}
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
