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

    public static final String
	NAME_SEPARATOR = ";"; // string used to separate visitor names. 

    // Data members of Message class.

    public String user = "" ;// Message sender's ID, unique.
    public String	message     = ""; 

    // visitorList holds list of visitor <id; name>, If it is null or empty, all visitors visitorList.
    public String  visitorList = null;

    public  Message( ) { initialize( user, "no message", null); }
    public  Message( Message m ) {
	            initialize( m.user, m.message, m.visitorList );
    }
    public  Message(String user, String msg,  String vName) { initialize(user, msg, vName ); }

    public  void initialize(String user, String msg, String vNames) {
    	this.user = user; message = new String (msg);
    	visitorList = vNames == null ? null : new String(vNames) ; 
    }

    public  void set(String user, String msg, String vNames) {
	this.user = user;  message = msg;
	visitorList = vNames == null ? null : new String(vNames) ; 
    }

    public  String toString() {
	return String.format("%10s %3d %20s %s", user, message,
		visitorList == null ? "null" : "not null");
    }

    public  String getString () { 
	return visitorList;
    }
}
