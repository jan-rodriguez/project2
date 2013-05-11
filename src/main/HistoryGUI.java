package main;

import java.awt.Container;
import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;

public class HistoryGUI  extends JFrame{
	private JTextArea history;
	private JScrollPane historyPane;
	private JList Chats;
	private JLabel ChatsID;
	private JLabel Message;
	private DefaultListModel arrChats;
	private final String username;
	
	
	public HistoryGUI(String username) {
		this.username = username;
		
		Container container = getContentPane();
		setTitle("History");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	// might want to use EXIT_ON_CLOSE to close all conversations
		setPreferredSize(new Dimension(340, 200));
		setMinimumSize(new Dimension(230, 230));
		
		ChatsID = new JLabel("Chat Room No. ");
		Message = new JLabel("History");
		history = new JTextArea(8, 4);
		// arrChats is a DefaultListModel that stores chat room # of active public chats
//		List<String> rooms = client.getProcessor().getpreviousChats();
		arrChats = new DefaultListModel();
//		for (int i = 0; i < rooms.size(); i++){
//			arrChats.addElement(rooms.get(i));
//		} 
		
		// Chats is a JList that contains arrChats DefaulListModel
		arrChats.addElement("0");
		
		Chats = new JList(arrChats);
		Chats.setName("Chats");
		
		historyPane = new JScrollPane(Chats, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		historyPane.setMaximumSize(new Dimension(30, 160));
		
//////////BEGIN Layout    ///////////////////
     GroupLayout layout = new GroupLayout(getContentPane());
     container.setLayout(layout);
     
     layout.setAutoCreateGaps(true);
     layout.setAutoCreateContainerGaps(true);

     layout.setVerticalGroup(
    		 layout.createParallelGroup()
    		 .addGroup(
    				 layout.createSequentialGroup()
    				 .addComponent(ChatsID)
    				 .addComponent(historyPane)
    				 )
    		 .addGroup(
    				 layout.createSequentialGroup()
    				 .addComponent(Message)
    				 .addComponent(history)
    		 )
    		 );
		
     layout.setHorizontalGroup(
    		 layout.createSequentialGroup()
    		 .addGroup(
    				 layout.createParallelGroup(Alignment.CENTER)
    				 .addComponent(ChatsID)
    				 .addComponent(historyPane)
    				 )
    		 .addGroup(
    				 layout.createParallelGroup(Alignment.CENTER)
    				 .addComponent(Message)
    				 .addComponent(history)
    		 )
    		 );
     pack();
     setVisible(true);
	}
	
	public static void main(String[] args){
		new HistoryGUI("Chau");
		
	}

}
