package server;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


import org.junit.Test;

public class ServerProcessTest {

	//timeout ensures we don't loop infinitely
	@Test (timeout = 1000)
	public void usernameTest() {
		//start server processor
		ServerProcess processor = new ServerProcess();
		processor.start();
		
		try {
			//writer and reader for file
			PrintWriter writer = new PrintWriter(new FileWriter("test.txt", true));
			BufferedReader in = new BufferedReader(new FileReader("test.txt"));

			//command through server processor
			processor.addLine("username gabo", writer);
			String response;
			
			while (true) {
				//flush writer -> write message
				writer.flush();
				//read file
				response = in.readLine();
				//if something was written, break
				if (response != null) {
					break;
				}
			}
			
			//close writer
			writer.close();
			//assert message was the one we expected
			assertTrue(response.replaceAll("\\s+", "").equals("startgabo?"));
			//close reader
			in.close();
			
			//clear file contents
			FileOutputStream eraser = new FileOutputStream("test.txt");
			eraser.write((new String()).getBytes());
			eraser.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}
	}
	
	//timeout ensures we don't loop infinitely
	@Test (timeout = 1000)
	public void newChatTest() {
		//start server processor
		ServerProcess processor = new ServerProcess();
		processor.start();
		
		try {
			//writer and reader for file
			PrintWriter writer = new PrintWriter(new FileWriter("test.txt", true));
			BufferedReader in = new BufferedReader(new FileReader("test.txt"));

			//command through server processor
			processor.addLine("new jan", writer);
			String response;
			while (true) {
				//flush writer -> write message
				writer.flush();
				//read file
				response = in.readLine();
				//if something was written, break
				if (response != null) {
					break;
				}
			}
		} catch (IOException e){
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}
	}
	
}
