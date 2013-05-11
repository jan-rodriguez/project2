package main;

import java.net.Socket;

public class Action {

	private final Socket socket;
	private final String text;

	public Action(String text, Socket socket) {
		this.socket = socket;
		this.text = text;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public String getText() {
		return text;
	}

}