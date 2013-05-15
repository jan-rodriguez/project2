package main.server;

import java.io.PrintWriter;

/**
 * Action class represents a request containing a text message and
 * a socket. It is made by the client and added to the ServerProcess
 * BlockingQueue, for later execution.
 */
public class Action {

	private final PrintWriter writer;
	private final String text;

	/**
	 * Constructor for Action class.
	 * @param text
	 * @param socket
	 */
	public Action(String text, PrintWriter writer) {
		this.writer = writer;
		this.text = text;
	}
	
	/**
	 * Getter methods
	 */
	public PrintWriter getWriter() {
		return writer;
	}
	
	public String getText() {
		return text;
	}

}