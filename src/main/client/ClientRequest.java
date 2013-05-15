package main.client;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * ClientRequest handles requests made by the client to the server.
 * They are stored in a BlockingQueue and then sent one by one
 * through the socket, using a PrintWriter.
 */
public class ClientRequest extends Thread {

	private final BlockingQueue<String> queue;
	private PrintWriter writer = null;
	private final ClientSide client;

	/**
	 * Constructor method for ClientRequest. Instantiates the blocking queue and PrintWriter.
	 */
	public ClientRequest(PrintWriter writer, ClientSide client) {
		this.queue = new LinkedBlockingQueue<String>();
		this.client = client;
		this.writer = writer;
	}
	
	@Override
	/**
	 * Run method for the ClientRequest. Accesses requests from the blocking queue
	 * and writes them to the socket.
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
	
	/**
	 * Adds a request to the blocking queue.
	 * @param line - a string representing a client request
	 */
	public void addLine(String line) {
		queue.offer(line);
	}

	/**
	 * Getter function
	 */
	public ClientSide getClient() {
		return client;
	}

}

