package server;

import static org.junit.Assert.*;

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
			PrintWriter writer = new PrintWriter(new FileWriter("src/server/test.txt", false));
			BufferedReader in = new BufferedReader(new FileReader("src/server/test.txt"));

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
			FileOutputStream eraser = new FileOutputStream("src/server/test.txt");
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
			PrintWriter writer = new PrintWriter(new FileWriter("src/server/test.txt", false));
			BufferedReader in = new BufferedReader(new FileReader("src/server/test.txt"));

			//command through server processor
			processor.addLine("username jan", writer);
			while (true) {
				//flush writer -> write message
				writer.flush();
				//if something was written, break
				if (in.readLine() != null) {
					break;
				}
			}
			//erasing file
			FileOutputStream eraser = new FileOutputStream("src/server/test.txt");
			eraser.write((new String()).getBytes());
			eraser.close();
		
			processor.addLine("new jan", writer);
			String response2;
			while (true) {
				//flush writer -> write message
				writer.flush();
				//read file
				response2 = in.readLine();
				//if something was written, break
				if (response2 != null) {
					break;
				}
			}
			
			assertTrue(response2.replaceAll("\\s+", "").equals("newjan0"));
			//close reader
			in.close();
			writer.close();
			
			//clear file contents
			FileOutputStream eraser1 = new FileOutputStream("src/server/test.txt");
			eraser1.write((new String()).getBytes());
			eraser1.close();
		
		} catch (IOException e){
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}
	}
	
	//timeout ensures we don't loop infinitely
	@Test (timeout = 1000)
	public void inviteChatTest() {
		//start server processor
		ServerProcess processor = new ServerProcess();
		processor.start();
		
		try {
			//writer and reader for file
			PrintWriter writer = new PrintWriter(new FileWriter("src/server/test.txt", false));
			BufferedReader in = new BufferedReader(new FileReader("src/server/test.txt"));

			//command through server processor
			processor.addLine("username jon", writer);
			String response;
			while (true) {
				//flush writer -> write message
				writer.flush();
				//if something was written, break
				if (in.readLine() != null) {
					break;
				}
			}
			//erasing file
			FileOutputStream eraser = new FileOutputStream("src/server/test.txt");
			eraser.write((new String()).getBytes());
			eraser.close();
		
			processor.addLine("new jon", writer);	
			while (true) {
				//flush writer -> write message
				writer.flush();
				
				//if something was written, break
				if (in.readLine() != null) {
					break;
				}
			}			
			while (true) {
				//flush writer -> write message
				writer.flush();
				
				//if something was written, break
				if (in.readLine() != null) {
					break;
				}
			}
			//erasing file
			FileOutputStream eraser1 = new FileOutputStream("src/server/test.txt");
			eraser1.write((new String()).getBytes());
			eraser1.close();
			
			PrintWriter chauWriter = new PrintWriter(new FileWriter("src/server/test.txt", false));

			processor.addLine("username chau", chauWriter);	
			while (true) {
				//flush writer -> write message
				chauWriter.flush();
				//if something was written, break
				if (in.readLine() != null) {
					break;
				}
			}
			
			FileOutputStream eraser2 = new FileOutputStream("src/server/test.txt");
			eraser2.write((new String()).getBytes());
			eraser2.close();

			processor.addLine("invite chau 0", writer);
			while (true) {
				//flush writer -> write message
				writer.flush();
				response = in.readLine();
				//if something was written, break
				if (response != null) {
					break;
				}
			}
//			
//			assertTrue(response.replaceAll("\\s+", "").equals("addchau0"));
//			
//			while (true) {
//				//flush writer -> write message
//				chauWriter.flush();
//				response = in.readLine();
//				//if something was written, break
//				if (response != null) {
//					break;
//				}
//			}
//	
//			assertTrue(response.replaceAll("\\s+", "").equals("newjonchau?0"));
//			
//			//closer reader
//			in.close();
//			writer.close();
//			//clear file contents
//			FileOutputStream eraser3 = new FileOutputStream("src/server/test.txt");
//			eraser3.write((new String()).getBytes());
//			eraser3.close();
			
		} catch (IOException e){
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}
	}
	
}
