package main;

import java.net.Socket;

/**
 * Action class represents a request containing a text message and
 * a socket. It is made by the client and added to the ServerProcess
 * BlockingQueue, for later execution.
 */
public class Action {

	private final Socket socket;
	private final String text;

	/**
	 * Constructor for Action class.
	 * @param text
	 * @param socket
	 */
	public Action(String text, Socket socket) {
		this.socket = socket;
		this.text = text;
	}
	
	/**
	 * Getter methods
	 */
	public Socket getSocket() {
		return socket;
	}
	
	public String getText() {
		return text;
	}

}