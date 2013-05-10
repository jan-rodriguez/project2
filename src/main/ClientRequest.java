package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientRequest extends Thread {

	private final Socket socket;
	private final ClientSide client;
	private final BlockingQueue<String> queue;

	/**
	 * Constructor method for ServerProcess. Instantiates the blocking queue.
	 */
	public ClientRequest(Socket socket, ClientSide client) {
		this.queue = new LinkedBlockingQueue<String>();
		this.client = client;
		this.socket = socket;
	}
	
	@Override
	/**
	 * Run method for the ServerProcess. Accesses actions from the blocking queue
	 * and runs the actions specified by the current action input.
	 */
	public void run() {
        PrintWriter out;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
            	String action = queue.take();
            	out.println(action);
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addLine(String line) {
		queue.offer(line);
	}

	public ClientSide getClient() {
		return client;
	}

}

