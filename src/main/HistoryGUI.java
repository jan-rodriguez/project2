package main;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Timer;

public class HistoryGUI  extends JFrame{
	private JTextArea history;
	private JScrollPane historyPane;
	private JList Chats;
	private JLabel ChatsID;
	private JLabel Message;
	private Object[] arrChats;
	private final ConcurrentHashMap<String, Conversation> ChatMap;
	
	public HistoryGUI(final ConcurrentHashMap<String, Conversation> ChatMap) {
		this.ChatMap = ChatMap;
		
		Container container = getContentPane();
		setTitle("Chats as of " + (new Date()).toString());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	// might want to use EXIT_ON_CLOSE to close all conversations
		setPreferredSize(new Dimension(380, 200));
		setMinimumSize(new Dimension(380, 200));
		
		ChatsID = new JLabel("Chat Room No. ");
		Message = new JLabel("History");
		history = new JTextArea(8, 4);
		// arrChats is a DefaultListModel that stores chat room # of active public chats
		Set<String> ids = ChatMap.keySet();

		arrChats = ids.toArray();
		System.out.println(arrChats.toString() + " line 48");
		// Chats is a JList that contains arrChats DefaulListModel
		
		Chats = new JList(arrChats);
		Chats.setName("Chats");
		Chats.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				history.setText(ChatMap.get(Chats.getSelectedValue()).getHistory());
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
		});
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
		
	}

}
