package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client server runner. Keeps track of all of the current Clients
 * connected to the Server. Connects and disconnects the clients.
 */
public class Server {
	
	private static ConcurrentHashMap<String, Client> hashUsers = new ConcurrentHashMap<String, Client>();
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
			Socket socket = serverSocket.accept();
    		Client client = new Client(socket, processor);
    		client.start();
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
	public static void handleConnection(Socket socket) throws IOException {	
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), false);
        
        System.out.println(out);
        
        try {
            for (String line =in.readLine(); line!=null; line=in.readLine()) {
                String output = handleRequest(line);
                if(output != null) {
                    //Handling 'bye' command from the client
                    if(output.equals("disconnected")){
                        //When a person disconnects
                    	return;
                    }
                    }
                }
        } finally {        
            out.close();
            in.close();

        }

	}
	
	public static void handleRequest(String input){
		
	}
	
	/**
	 * updateClients is used to update the AllUsersGUI to show all other active users
	 * that a client has either connected or disconnected and updates their GUI's
	 * accordingly.
	 */
	public static void updateClients() {
		Collection<Client> clients = hashUsers.values();
		for (Client client : clients){
			client.setUsers();
		}
	}
	
	/**
	 * update public chat rooms list for all users
	 * updateChats gets called from a client who just opens a public conversation
	 * so that all AllUsersGUIs are updated with the new list of public conversations
	 */
	
	public static void updateChats(){
		Collection<Client> clients = hashUsers.values();
		for (Client client : clients){
			client.setChatRooms();
		}
		
	}
	
	/**
	 * Getter function for ConcurrentHashMap<String, Client> mapping all users connected
	 * to the server from their username to the client.
	 * @return
	 */
	public static ConcurrentHashMap<String, Client> gethashUsers() {
		return hashUsers;
	}
	
    /**
     * Start a chat server from the given arguments. Creates a server at the
     * port specified by first arguemnt.
     * @param String[] args - args[0] must be a valid integer
     */
    public static void main(String[] args) {
    	Server instantmess = null;
    	try {
    		Integer port = Integer.parseInt(args[0]);
    		if (port > 6035 || port < 0)
    			throw new NumberFormatException("Invalid port number.");
			instantmess = new Server(port);
    	} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	if (instantmess != null)
			try {
				instantmess.connect();
			} catch (IOException e) {
				e.printStackTrace();
			}
    }
}
