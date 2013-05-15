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
			
			while((response =in.readLine()) == null){
				//flush writer -> write message
				writer.flush();
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
			String response;
			
			//command through server processor
			processor.addLine("username jan", writer);
			
			while((response =in.readLine()) == null){
				//flush writer -> write message
				writer.flush();
			}
			
			//checking if correct message was sent
			assertTrue(response.replaceAll("\\s+", "").equals("startjan?"));
			
			//erasing file
			FileOutputStream eraser = new FileOutputStream("src/server/test.txt");
			eraser.write((new String()).getBytes());
			eraser.close();
		
			processor.addLine("new jan", writer);
			
			while((response =in.readLine()) == null){
				//flush writer -> write message
				writer.flush();
			}
			
			//checking if correct message was sent
			assertTrue(response.replaceAll("\\s+", "").equals("newjan0"));
			
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
			
			while ((response = in.readLine())==null) {
				//flush writer -> write message
				writer.flush();
			}
			//checking for correct response
			assertTrue(response.replaceAll("\\s+", "").equals("startjon?"));
			
			//erasing file
			FileOutputStream eraser = new FileOutputStream("src/server/test.txt");
			eraser.write((new String()).getBytes());
			eraser.close();
		
			processor.addLine("new jon", writer);
			
			while ((response = in.readLine())== null) {
				//flush writer -> write message
				writer.flush();
			}
			
			//checking for correct response
			assertEquals(response, "new jon 0");
			
			while ((response = in.readLine())== null) {
				//flush writer -> write message
				writer.flush();

			}
			
			//checking if correct message was sent
			assertEquals(response, "create 0");
			
			//erasing file
			FileOutputStream eraser1 = new FileOutputStream("src/server/test.txt");
			eraser1.write((new String()).getBytes());
			eraser1.close();
			
			PrintWriter chauWriter = new PrintWriter(new FileWriter("src/server/test1.txt", false));
			BufferedReader inChau = new BufferedReader(new FileReader("src/server/test1.txt"));

			processor.addLine("username chau", chauWriter);
			
			while ((response = inChau.readLine())== null) {
				//flush writer -> write message
				chauWriter.flush();
			}
			
			//checking if correct message was sent
			assertEquals(response, "start chau jon ? 0");
			
			//erasing file
			FileOutputStream eraser2 = new FileOutputStream("src/server/test1.txt");
			eraser2.write((new String()).getBytes());
			eraser2.close();
			
			processor.addLine("invite chau 0", writer);
			
			while ((response = in.readLine())==null) {
				//flush writer -> write message
				writer.flush();
			}
			
			//checking if correct message was sent
			assertEquals(response, "connect chau");
			
			while ((response = in.readLine())==null) {
				//flush writer -> write message
				writer.flush();
			}
			
			//checking if correct message was sent
			assertEquals(response, "add chau 0");
			
			//erasing file
			FileOutputStream eraser3 = new FileOutputStream("src/server/test.txt");
			eraser3.write((new String()).getBytes());
			eraser3.close();
			
			while ((response = inChau.readLine())==null) {
				//flush writer -> write message
				chauWriter.flush();
			}
		
			assertTrue(response.replaceAll("\\s+", "").equals("newjonchau?0"));

			//closer reader
			in.close();
			inChau.close();
			writer.close();
			//clear file contents
			FileOutputStream eraser4 = new FileOutputStream("src/server/test1.txt");
			eraser4.write((new String()).getBytes());
			eraser4.close();
			
		} catch (IOException e){
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}
	}
	//timeout ensures we don't loop infinitely
	@Test (timeout = 1000)
	public void testDisconnect(){
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
			
			while((response =in.readLine()) == null){
				//flush writer -> write message
				writer.flush();
			}
			//Checking correct message
			assertEquals(response, "start gabo ? ");
			//erasing file
			FileOutputStream eraser = new FileOutputStream("src/server/test.txt");
			eraser.write((new String()).getBytes());
			eraser.close();
			
			processor.addLine("disconnect gabo", writer);
			
			
			while((response =in.readLine()) == null){
				//flush writer -> write message
				writer.flush();
			}
			assertEquals(response, "disconnect gabo");
			FileOutputStream eraser2 = new FileOutputStream("src/server/test.txt");
			eraser2.write((new String()).getBytes());
			eraser2.close();
			//Closing writer and reader
			in.close();
			writer.close();
		} catch (IOException e){
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}
	}
	//timeout ensures we don't loop infinitely
	@Test (timeout = 1000)
	public void testUsedname(){
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
			
			while((response =in.readLine()) == null){
				//flush writer -> write message
				writer.flush();
			}
			//Checking correct message
			assertEquals(response, "start gabo ? ");
			//erasing file
			FileOutputStream eraser = new FileOutputStream("src/server/test.txt");
			eraser.write((new String()).getBytes());
			eraser.close();
			
			//adding same username, before other disconnects
			processor.addLine("username gabo", writer);
			
			while((response =in.readLine()) == null){
				//flush writer -> write message
				writer.flush();
			}
			//Checking correct message
			assertEquals(response, "abort");
			
			//erasing file
			FileOutputStream eraser2 = new FileOutputStream("src/server/test.txt");
			eraser2.write((new String()).getBytes());
			eraser2.close();
			
			//Closing reader and writer
			in.close();
			writer.close();
			
		} catch (IOException e){
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}		
	}
	//timeout ensures we don't loop infinitely
	@Test (timeout = 1000)
	public void testLeave() {
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
			
			while((response =in.readLine()) == null){
				//flush writer -> write message
				writer.flush();
			}
			//Checking correct message
			assertEquals(response, "start gabo ? ");
			//erasing file
			FileOutputStream eraser = new FileOutputStream("src/server/test.txt");
			eraser.write((new String()).getBytes());
			eraser.close();
			
		
			processor.addLine("new gabo", writer);
					
			while ((response = in.readLine())== null) {
				//flush writer -> write message
				writer.flush();
			}
			
			//checking for correct response
			assertEquals(response, "new gabo 0");
			
			//erasing file
			FileOutputStream eraser2 = new FileOutputStream("src/server/test.txt");
			eraser2.write((new String()).getBytes());
			eraser2.close();
			
			while((response =in.readLine()) == null){
				//flush writer -> write message
				writer.flush();
			}
			//Checking correct message
			assertEquals(response, "create 0");
			
			//erasing file
			FileOutputStream eraser3 = new FileOutputStream("src/server/test.txt");
			eraser3.write((new String()).getBytes());
			eraser3.close();
			
			PrintWriter chauWriter = new PrintWriter(new FileWriter("src/server/test1.txt", false));
			BufferedReader inChau = new BufferedReader(new FileReader("src/server/test1.txt"));

			processor.addLine("username chau", chauWriter);
			
			while ((response = inChau.readLine())== null) {
				//flush writer -> write message
				chauWriter.flush();
			}
			
			//checking if correct message was sent
			assertEquals(response, "start chau gabo ? 0");
			
			//erasing file
			FileOutputStream eraserchau = new FileOutputStream("src/server/test1.txt");
			eraserchau.write((new String()).getBytes());
			eraserchau.close();
			
			processor.addLine("invite chau 0", writer);
			
			while ((response = in.readLine())==null) {
				//flush writer -> write message
				writer.flush();
			}
			
			//checking if correct message was sent
			assertEquals(response, "connect chau");
			
			while ((response = inChau.readLine())==null) {
				//flush writer -> write message
				chauWriter.flush();
			}
			assertEquals(response, "new gabo chau  ?  0");

			while ((response = in.readLine())==null) {
				//flush writer -> write message
				writer.flush();
			}
			//checking if correct message was sent
			assertEquals(response, "add chau 0");

			//leave message to server
			processor.addLine("leave gabo 0", writer);
			
			while((response =inChau.readLine()) == null){
				//flush writer -> write message
				chauWriter.flush();
			}
			//checking for correct response
			assertEquals(response, "leave gabo 0");
			
			//erasing file
			FileOutputStream eraser4 = new FileOutputStream("src/server/test.txt");
			eraser4.write((new String()).getBytes());
			eraser4.close();
			
			//Closing writer and reader
			in.close();
			inChau.close();
			writer.close();
			
			
		} catch (IOException e){
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}	
	}
}
