package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import main.Chat;

import org.junit.Test;

public class ChatTest {

	@Test
	public void memberTest() {
		Chat test = new Chat(new AtomicInteger(0), "jan");
		test.addMember("jan");
		test.addMember("gabo");
		Collection<String> out = Collections.synchronizedCollection(new ArrayList<String>());
		out.add("jan");
		out.add("gabo");
		Iterator<String> testit = out.iterator();
		
		
		for(String mem: test.getMembers()){
			assertTrue(mem.equals(testit.next()));
		}
		
		assertTrue(!test.isMember("chau"));
		
		assertTrue(test.isMember("gabo"));
	}
	
	@Test
	public void historyTest() {
		Chat test = new Chat(new AtomicInteger(0), "chau");
		test.addHistory("chau", "hello");
		test.addHistory("chau", "bye");
		Collection<String> out = Collections.synchronizedCollection(new ArrayList<String>());
		out.add("chau: hello");
		out.add("chau: bye");
		Iterator<String> testit = out.iterator();
		for(String mem: test.getHistory()){
			assertTrue(mem.equals(testit.next()));
		}
	}
	@Test
	public void creatorandIDtest() {
		Chat test = new Chat(new AtomicInteger(100), "gabo");
		assertTrue(test.getCreator().equals("gabo"));
		assertTrue(test.getID() == 100);
	}
	@Test
	public void concatHistorytest() {
		Chat test = new Chat(new AtomicInteger(0), "jan");
		test.addHistory("jan", "hello");
		test.addHistory("jan", "how are you?");
		test.addHistory("jan", "who am I even talking to?");
		test.addHistory("jan", "yeah, that's right no one");
		String out = "jan: hello jan: how are you? jan: who am I even talking to? jan: yeah, that's right no one ";
		assertEquals(test.getHistoryString(), out);
	}
}
