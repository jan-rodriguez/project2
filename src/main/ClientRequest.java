package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientRequest extends Thread {

	private final BlockingQueue<String> queue;
	private final ClientSide client;
	private PrintWriter writer = null;

	/**
	 * Constructor method for ServerProcess. Instantiates the blocking queue.
	 */
	public ClientRequest(Socket socket, ClientSide client) {
		this.queue = new LinkedBlockingQueue<String>();
		this.client = client;
		
		try {
			this.writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	/**
	 * Run method for the ServerProcess. Accesses actions from the blocking queue
	 * and runs the actions specified by the current action input.
	 */
	public void run() {
        while (true) {
        	String action;
			try {
				action = queue.take();
	        	writer.println(action);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void addLine(String line) {
		queue.offer(line);
	}

	public ClientSide getClient() {
		return client;
	}

}

