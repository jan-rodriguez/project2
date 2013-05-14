package tests;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import main.ServerProcess;

import org.junit.Test;

public class ServerProcessTest {

	//timeout ensures we don't loop infinitely
	@Test (timeout = 1000)
	public void someTest() {
		//start server processor
		ServerProcess processor = new ServerProcess();
		processor.start();
		
		try {
			//writer and reader for file
			PrintWriter writer = new PrintWriter(new FileWriter("src/tests/test.txt", true));
			BufferedReader in = new BufferedReader(new FileReader("src/tests/test.txt"));

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
			FileOutputStream erasor = new FileOutputStream("src/tests/test.txt");
			erasor.write((new String()).getBytes());
			erasor.close();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
}
