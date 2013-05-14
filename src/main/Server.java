package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server for the chat. Has a server socket to which clients can connect and a
 * ServerProcess thread that handles client requests.
 */
public class Server {
	
	private final ServerSocket serverSocket;
	private static ServerProcess processor;

	/**
	 * Constructor method for the Server. Starts ServerProcess thread.
	 * @param port - integer specifying the port the SeverSocket where the server socket will be created.
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        processor = new ServerProcess();
        processor.start();
	}
	
	/**
     * Runs the server, listening for client connections and handling them.
     * When a client connects, creates a new thread for the client to run in. 
     * Never returns unless an exception is thrown.
     * @throws IOException if the main server socket is broken
     * (IOExceptions from individual clients do *not* terminate connect()).
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
	 * Method that reads input from the client socket and handles their
	 * requests by sending them to the ServerProcess thread. Returns when 
	 * the client connection is terminated.
	 * @param socket - Socket of the client with whom we are communicating
	 * @throws IOException
	 */
	public void handleConnection(Socket socket) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//Read client input and pass on to the ServerProcess thread
	        for (String line = in.readLine(); line != null; line=in.readLine()) {
	        	processor.addLine(line, new PrintWriter(socket.getOutputStream(), true));
	        	//if client disconnects close reader, break
	        	if (line.split("\\s+")[0].equals("disconnect")) {
	        		in.close();
	        		break;
	        	}
	        }
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
    /**
     * Creates a server at the port specified by first arguemnt.
     * @param String[] args - args[0] must be a valid port integer
     */
    public static void main(String[] args) {
    	Server server = null;
    	try {
    		//validates port number in args[0]
    		Integer port = Integer.parseInt(args[0]);
    		if (port > 65535 || port < 0)
    			throw new NumberFormatException("Invalid port number.");
    		//creates server
			server = new Server(port);
    	} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	//server starts listening for connections
    	if (server != null) {
			try {
				server.connect();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}
