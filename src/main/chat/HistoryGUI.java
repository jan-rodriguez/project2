package main.chat;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
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
/**
 * HistoryGUI pops up when user clicks on View History button in AllUsersGUI
 * <p>
 * The GUI has a HashMap that maps String ChatNumber to a conversation
 * When a user selects a ChatNumber from a JList, the history of the 
 * corresponding conversation will show up in a text area.
 * The ChatNumbers that are shown up are the ones that are active
 * by the time the user clicks View History, i.e. chat rooms that are created
 * after that point will not show up in History GUI. 
 * However, the history of chat rooms in the History GUI will be updated 
 * if users reselect them. For example, if a user sees room 0, then room 1,
 * and back to room 0 again. If during that time room 0 is updated with new messages
 * then the new messages will show up when the user sees room 0 the second time. 
 * <p>
 * Components: 2 JLabels to show ChatNumber and history area
 * 			   1 JScrollPane that contains a JList. This JList stores ChatNumber
 * 			   1 JScrollPane that contains a JTextArea to show histories
 */
public class HistoryGUI  extends JFrame{
	private JTextArea history;
	private JScrollPane historyPane;
	private JList Chats;
	private JLabel ChatsID;
	private JLabel Message;
	private JScrollPane messPane;
	private Object[] arrChats;
	private final ConcurrentHashMap<String, Conversation> ChatMap;
	
	public HistoryGUI(final ConcurrentHashMap<String, Conversation> ChatMap) {
		this.ChatMap = ChatMap;
		
		// container settings
		Container container = getContentPane();
		setTitle("Chats as of " + (new Date()).toString());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	// might want to use EXIT_ON_CLOSE to close all conversations
		setPreferredSize(new Dimension(380, 230));
		setMinimumSize(new Dimension(380, 230));
		
		// initialize components
		ChatsID = new JLabel("Chat Room No. ");
		Message = new JLabel("History");
		history = new JTextArea(8, 4);
		history.setWrapStyleWord(true);
		history.setMargin(new Insets(5, 5, 5, 5));
		history.setLineWrap(true);
		history.setEnabled(false);
		messPane = new JScrollPane(history, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// arrChats is a DefaultListModel that stores chat room # of active public chats
		Set<String> ids = ChatMap.keySet();
		arrChats = ids.toArray();
		
		// Chats is a JList that contains arrChats DefaulListModel
		Chats = new JList(arrChats);
		Chats.setName("Chats");
		// Add mouse listener so that users can select rooms and see history
		Chats.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (Chats.getSelectedValue() != null){
					// get history from the conversation with selected id
					history.setText(ChatMap.get(Chats.getSelectedValue()).getHistory());
				}
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
		historyPane.setMaximumSize(new Dimension(30, 170));
		
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
    				 .addComponent(messPane)
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
    				 .addComponent(messPane)
    		 )
    		 );
     pack();
     setVisible(true);
	}
	
	public static void main(String[] args){
		
	}

}
