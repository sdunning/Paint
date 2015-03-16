/* Message class define a set of messages sent between the chatroom server
 *      and  chatroom visitors.
 *  Data members:
 *  	. type    : an integer to indicate kind of message
 *  	. message : a message sent out to each visitor including the sender.
 * 	. visitName : a string contains one or more visitor names. 
*/

// package chatroom;

import java.io.*;
import java.util.*;


public class Message implements Serializable {

    public static final int
	PUBLIC = 0, // Message: (senderID, PUBLIC, "message", null).
		     // Client : sends to server, or receives it and append to it
			 // At this time, sid is the visitor who sends the message..
		     //          text area.
		     // Server : receives the message broadcasters it very visitor
		     //          without any change.
		   
	PRIVATE = 1, // Message: (sid, PRIVATE, "message", "name1; name2. ..")
		     // Client sid : writes this type of message(sid, 1, message, list_visitorList )
		     //		 to server agent.
		     // Server  receives the message and send the message to visitors in the vector
		     //  agent:  and make a general message
		     //             as (sid, 1 , "private to name1, name2m, ...", null).
		     //          broadcasts above message to each visitor whose name
		     //          appeard in the vector.
	LOGIN   = 2, // Message: (-1, LOGIN, null, name)
		     // Client : When a visitor is logined, a message of the format aveove sends it to 
		     //	 agent.  The agent change teh sid from -1 to nextID, create a new visitor and
  		     //		add the new visitor to the list (vector of visitor)
		     // 	 and send the message to every visitor, and message
		     //  Add Client (name, null, OutputStream) to its visitor list.
		     //          broadcasts the message to each visitor.
		     // Client : Receives the message, and add Client (sid, name, false, vectorOfVisitors)
		     //          Append message on message board (JTextArea), and reset/repaint name list
		     //          on the east area. User JList should be easier.
	LOGOUT  = 3; // Message: (sid, LOGOUT, name, null is send out by visitor
		     // Client : send a message of format above and walk out.
    		     // Server : Agent detects the logout, removes the the visitor from visitor list
		     //          by sid; broadcasts the message to every visitor.
		     // Client : Removes vVisitors.remove (sid) from its name list.
		     // 	 Repaint name list.
		     
    public static final String
	NAME_SEPARATOR = ";"; // string used to separate visitor names. 

    // Data members of Message class.

    public int SID = -1 ;// Message sender's ID, unique.
    public int 		type 	    = PUBLIC;	// type of message defined above
    // message message to visitorList, or name if message type is either LOGIN or LOGOUT type.
    public String	message     = ""; 

    // visitorList holds list of visitor <id; name>, If it is null or empty, all visitors visitorList.
    public String  visitorList = null;

    public  Message( ) { initialize( SID, PUBLIC, "no message", null); }
    public  Message( Message m ) {
	            initialize( m.SID, m.type, m.message, m.visitorList );
            }
    public  Message(int sid, int t, String msg,  String vName) { initialize(SID,  t, msg, vName ); }

    public  void initialize(int sid, int t, String msg, String vNames) {
	SID = sid; type = t;  message = new String (msg);
	visitorList = vNames == null ? null : new String(vNames) ; 
    }

    public  void set(int sid, int t, String msg, String vNames) {
	SID = sid; type = t;  message = msg;
	visitorList = vNames == null ? null : new String(vNames) ; 
    }

    public  String toString() {
	return String.format("%3d %3d %20s %s", SID, type, message,
		visitorList == null ? "null" : "not null");
    }

    public  String getString () { 
	return visitorList;
    }
}
