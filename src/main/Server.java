package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Client server runner. Keeps track of all of the current Clients
 * connected to the Server. Connects and disconnects the clients.
 */
public class Server {
	
	private final ServerSocket serverSocket;
	private static ServerProcess processor;

	/**
	 * Constructor method for the Server.
	 * @param port - integer specifying the port the SeverSocket where the server socket will be created.
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        processor = new ServerProcess();
        processor.start();
	}
	
	/**
     * Run the server, listening for client connections and handling them.
     * When a client connects, creates a new thread for the client to run in. 
     * Never returns unless an exception is thrown.
     * @throws IOException if the main server socket is broken
     * (IOExceptions from individual clients do *not* terminate serve()).
     */
	public void connect() throws IOException {
		while(true) {
			final Socket socket = serverSocket.accept();
			
            Runnable client = new Runnable() {
				@Override
				public void run() {
					try {
						handleConnection(socket);
		            } finally {
		            	try {
		            		socket.close();
		            	} catch (IOException e) {
		            		e.printStackTrace();
		            	} 
		            }
				}	
            };
            
    		Thread newClient = new Thread(client);
    		newClient.start();
		}
	}
	
	/**
	 * Static method called from the client to disconnect a client from the server,
	 * and close the clients socket. Also removes them from the server's hashUsers. 
	 * removes client from all of it's active chats, and updates all the other clients
	 * GUI's to show that the client has disconnected.
	 * @param client - Client which will be disconnected from the server
	 * @throws IOException
	 */
	public void handleConnection(Socket socket) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
	        for (String line = in.readLine(); line != null; line=in.readLine()) {
	        	processor.addLine(line, socket);
	        }
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    /**
     * Start a chat server from the given arguments. Creates a server at the
     * port specified by first arguemnt.
     * @param String[] args - args[0] must be a valid integer
     */
    public static void main(String[] args) {
    	Server server = null;
    	try {
    		Integer port = Integer.parseInt(args[0]);
    		if (port > 65535 || port < 0)
    			throw new NumberFormatException("Invalid port number.");
			server = new Server(port);
    	} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	if (server != null) {
			try {
				server.connect();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}
