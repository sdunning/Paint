/**  Chatroom Client class:
 * 	It is GUI interface representing a visitor connected to the chatroom,
 * 	and the interface allows a visitor to:
 * 	  1. connects to chatroom server after visitor types his/her name.
 * 	  2. sends a message and sends the message out to
 * 	     other visitors in the chatroom.
 * 	  3. receives messages from visitors.
 * 	  4. maintains a list of visitors so that private messages can be
 * 	     sent to selected visitor(s).
*/
// package chatroom;

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import javax.swing.border.* ;
import java.io.* ;
import java.net.* ;
import java.util.* ;
	
public class Client extends JPanel implements ActionListener, MouseListener  {

   String	hostAddress = "sleipnir.cs.csubak.edu";
   // String	hostAddress = "delphi.cs.csubak.edu";
   int		port = 8705;
   int		frame_height = 350;
   int		font_size = 18;
   JLabel	lbArray[] = {new JLabel("Pub Msg") };
   public JTextField	txtArray[] = { new JTextField(""), new JTextField(20) };
   JTextArea	ta = new JTextArea() ;
   JPanel	plArray[] = { new JPanel(), new JPanel() };
   JPanel	plSouth = new JPanel();
   JPanel	plEast = new JPanel();
   Socket	chatSocket = null;
   ObjectOutputStream	out = null;  
   ObjectInputStream	in  = null; 

   boolean	connected = false;
   boolean	privateMessage = false;
   boolean      justChecked    = false;
   Message 	message = new Message();
   Visitor	me 	= new Visitor();
   VisitorSet	visitorSet = new VisitorSet(); // new VisitorSet(); 
   VisitorSet   privateList = null; 

   //Container		c =  new Container();
   //static JApplet	applet;

   public Client() { 
	   init();
   }
   
   public void init() {
	   setLayout( new BorderLayout() );
   // lbArray[0].setForeground(Color.blue);
	lbArray[0].setText("Pub Msg");
	//getContentPane();
	//plEast.setBorder( new TitledBorder("Users"));
	plEast.setPreferredSize(new Dimension(250, frame_height));
    plEast.setFont(new Font("verdana", Font.PLAIN, font_size));	
	plEast.addMouseListener( this );

	

	plSouth.setLayout(new BorderLayout());
	setBackground(Color.black);
	ta.setBackground(Color.black);
	ta.setForeground(Color.white); 
	ta.setEditable(false);
	ta.setSize(300, 200);
	for ( int i = 0; i < plArray.length; i ++ ) {
			plArray[i].setLayout(new FlowLayout());
        	//	lbArray[i].setFont(new Font("verdana", Font.PLAIN, font_size));	
        		txtArray[i].setFont(new Font("verdana", Font.PLAIN, font_size));	
			//plArray[i].add(lbArray[i]);
			plArray[i].add(txtArray[i]);
			txtArray[i].addActionListener(this);
			if ( i == 0 ) lbArray[i].addMouseListener(this);
			txtArray[i].setEnabled( i == 0 );
	}
	//txtArray[0].setColumns(8); txtArray[0].setForeground(Color.blue);
	txtArray[0].setColumns(20);
	txtArray[0].setToolTipText("Enter message to send and press ENTER");
	lbArray[0].setToolTipText("Click to toggle between public & private message");
	//plSouth.add(plArray[0], BorderLayout.WEST) ;
	plSouth.add(plArray[0], BorderLayout.CENTER);

	ta.setFont(new Font("verdana", Font.PLAIN, font_size) );
	add(new JScrollPane(ta), BorderLayout.CENTER);
	add(plSouth, BorderLayout.SOUTH);
	add(plEast, BorderLayout.EAST);

	setSize( frame_height + 50, frame_height);
   }

   public void stop() {
     try {
	// Make a LOGOUT message, and send out.
	message = new Message (me.ID, Message.LOGOUT, me.name, ""); 
	Thread thr = new ClientSender( out, new Message(message));
	thr.start() ;
	System.out.println("stop called, and LOGOUT is sent out.");
	try { thr.join(); } catch ( InterruptedException e ) { e.printStackTrace(); }
	if ( in != null ) in.close(); 
	if ( out != null ) out.close();
	if ( chatSocket != null ) chatSocket.close();
	out = null; in = null; message = null;
     } catch (IOException e ) {e.printStackTrace();}
   }

   public void paint( Graphics g) {
       super.paint(g);
       // paintVisitors();
   }

   final int xStart = 8, yStart = 30, yGap = 9;

   public void checkVisitor( int y ) {

       if ( visitorSet == null || visitorSet.size() < 1 ) return;
       int yMin = yStart - yGap, yMax = yMin + yGap * visitorSet.size();
       if ( y <= yMin || y >= yMax ) return;
       int k = ( y - yMin) / yGap;
       Visitor v = visitorSet.get(k);
       v.checked = ! v.checked;
       if ( v.ID != me.ID ) justChecked = v.checked;
       paintVisitors();
   }

   // Check whether visitor is checked for private message:
   //     -1 : no one is checked.
   //     0  : visitor himself/herself is checked.
   //     1  : at least one other visitor is checked.
   public int checkChecks ( ) {
       int checkNo = -1;
       Visitor     v = null;
       if ( visitorSet.size() < 1 ) return -1;
       for ( int i = 0; i < visitorSet.size(); i ++ ) {
	      v = visitorSet.get(i);
	      if (v.checked ) 
	     	  if ( v.name.equals( me.name ))  checkNo = 0 ;
		  else return 1;
       }
       return checkNo;
   }  


   public VisitorSet getPrivateList( ) {
       VisitorSet vList = new VisitorSet();
       if ( visitorSet.size() < 1 ) return vList;  
       Iterator<Visitor> itr = visitorSet.iterator();
       Visitor  v = null;
       while ( itr.hasNext() ) {
	  v = itr.next();
	  if ( v.checked ) vList.add ( v );
       }
       return vList;
   }
   

   public void paintVisitors() {

       	     int  k = 0;

      // System.out.printf("In paint() .....\n");
       if ( visitorSet == null ) return;

      if ( visitorSet != null ) {
	  // System.out.printf("In visitor, %s, : \n", me.toShortString() );
	  // System.out.printf("client.visitorSet.size = %d\n", visitorSet.size() ); 
	  }

       Graphics g = plEast.getGraphics();

       g.setColor( plEast.getBackground() );
       g.fillRect(8, 20, plEast.getWidth() - 12, plEast.getHeight() - 25 );
       g.setColor( Color.blue);
       Iterator<Visitor> itr = visitorSet.iterator();
       Visitor v = null;
       while ( itr.hasNext() ) {
	   v = itr.next();
	   g.setColor( v.checked ? Color.red: Color.blue);
	   g.drawString(v.toShortString() , xStart, yStart + k * yGap );
	   k++;
	   // System.out.printf("Paint visit [%d], %s\n", k, v.toShortString());
       }
   }
   
   void setPrivateMessage ( boolean priv ) {
	privateMessage = priv ;
	lbArray[1].setForeground(privateMessage? Color.red : Color.blue);
	lbArray[1].setText( privateMessage? "Pri Msg": "Pub Msg");
	if ( ! privateMessage ) justChecked = false; 
   }

   void processLoginEvent () {

       if ( connected ) return;

	try {
	    	// Connect to server and get input and output streams through
		// socket.
		chatSocket = new Socket(hostAddress, port); 

		// while ( ! in.connected() ) Thread.sleep(200);
		Thread.sleep(200);

		out = new ObjectOutputStream ( chatSocket.getOutputStream());
		in  = new ObjectInputStream( chatSocket.getInputStream());
		

	        // start a client reader for for this visitor
		new ClientReceiver(in, ta, this).start();
		
	        message.set(me.ID, Message.LOGIN, me.name, null); 
		System.out.printf("Client try to to send: [%s]\n", message); 
		Thread thr = new ClientSender( out, message);
		thr.start() ;
        	try { thr.join(); } catch (InterruptedException f )  { f.printStackTrace(); }
		connected = true;
	}
	catch(IOException er) { ta.append(er.toString()); }
	catch(InterruptedException er2) { ta.append(er2.toString()); }
	
   }


   // Implementation of MouserListener
   
   public void mouseEntered( MouseEvent e ) { }
   public void mouseExited( MouseEvent e ) { }
   public void mousePressed( MouseEvent e ) { }
   public void mouseReleased( MouseEvent e ) { }
   public void mouseClicked( MouseEvent e ) {
       
    // Finds the location of the mouse
       PointerInfo a = MouseInfo.getPointerInfo();
       //Point e = a.getLocation();

       // Gets the x -> and y co-ordinates
       int x = (int) e.getX();
       int y = (int) e.getY();
       int k = (y - 21) / 9;
       System.out.printf("Mouse x: %d  y: %d   k: %d\n", x, y, k);
              
       Object obj = e.getSource();
       if ( obj == lbArray[1] ) { // Private or public message label is clicked

	   setPrivateMessage( ! privateMessage );
	   return ;
       }
       if ( obj == plEast ) {
	   checkVisitor( e.getY() );
       }
   }
 
   // Implementation of Action Listener.
   //static int k = 1;
   static String str = null;

   public void actionPerformed(ActionEvent e ) {
	Object obj = e.getSource();

	// Event Source: Visitor Name text field
	// Actions     : Make socket connection, start receiver and send
	// 	 a login message.
	if ( obj == txtArray[0] ) { // user name for login.
		me.set( -1, new String( txtArray[0].getText()).trim(), true ) ;
		me.checked = true;
		if ( me.name.equals("") ) {
		    JOptionPane.showMessageDialog( null, "Enter your name and press ENTER!");
		    return;
		}
		processLoginEvent();
		txtArray[0].setEnabled(false);
		txtArray[1].setEnabled(true);
		txtArray[1].requestFocus();
		return;
	}

	// Event source: message text field.
	// Action      : send a message.
	if ( obj == txtArray[1] ) {
		// get a string from text field.
	        str = new String(txtArray[1].getText()).trim();
		if ( str.equals("") ) return;
		if ( privateMessage && checkChecks() < 1 ) {
		    JOptionPane.showMessageDialog( null, "Try to send a private message\n!"
				    + "without selectedreceiver(s)!");
		    return;
		}
	 
		if ( justChecked && !privateMessage ) {
		    String msg = "Try to send a public message with some visitor(s)" +
				"\nselected selected for private message.\n" +
				"Do you want to comtinue?" ;
		    int    ans = JOptionPane.showConfirmDialog(null, msg);
		    switch( ans ) {
			case JOptionPane.YES_OPTION    : justChecked = false; break;
			case JOptionPane.NO_OPTION     : justChecked = false; return;
			case JOptionPane.CANCEL_OPTION : return;
		    }

		}

		if ( privateMessage )  {
		    privateList = getPrivateList();
		    message.set (me.ID, Message.PRIVATE, str, privateList.toString());
		} else
		    message.set( me.ID, Message.PUBLIC, str, null); 

		Thread thr = new ClientSender( out, new Message(message) );
		thr.start() ;
		try { thr.join(); } catch (InterruptedException f )  { }

		txtArray[1].setText("");
		txtArray[1].requestFocus();
	}
   }

   /*public static void main( String arg[] ) {
	JFrame fm =  new JFrame () ;
	fm.setBackground(Color.black);
	applet = new Client();
	applet.init();
	fm.setTitle("Chatroom Client: Running as appliction.") ;
	fm.addWindowListener( new WindowAdapter () {
		public void windowClosing(WindowEvent e) {
			applet.stop();
			System.exit(0); } } );
	fm.getContentPane().add( applet );
	fm.setSize(400, 400);
	fm.setVisible(true);
  }*/
}

class ClientSender extends Thread {

   ObjectOutputStream	out;  // OutputStreamWrite derived from Socket.
   Message	message;  // Message from a chat room client to other clients
   
   public ClientSender ( ObjectOutputStream w, Message s)
   { 
       out = w; message = s ; 
   } 

   public void run() {
       if ( out == null || message == null || message.message == null || message.message.length() < 1 ) return;	
       try {
		out.writeObject( new Message (message) ) ; out.flush();
       } catch( IOException e )
       {  e.printStackTrace() ; }
   }
}


class ClientReceiver extends Thread {

   ObjectInputStream		in 	= null;  // read message from server.
   JTextArea			ta 	= null;  // Draw messages.
   Client			client  = null;  // Panel to draw visitors 
   Message 			message = null;

   public ClientReceiver ( ObjectInputStream i, JTextArea a, Client c) {
     in = i;  ta = a ; client = c;
   }  

   public void run() {
        if ( in == null || ta == null || client == null ) return;
	try {
	    while ( true ) {

		// Read message
		try {
		    message = (Message) in.readObject() ;
		    // System.out.printf("\n\nMessage received:\n %s\n\n", message);
		} catch (ClassNotFoundException nfdEx) { ta.append("ClientReceiver error: " + nfdEx); }

		switch( message.type ) {
		    case Message.PUBLIC:
			ta.append( message.message + "\n" );
			break;
		    case Message.PRIVATE:
			ta.append(message.message +
				" - pri. to " + message.visitorList + "\n" );
			break;
		    case Message.LOGIN:
			if (client.me.ID == -1 ) client.me.set(message.SID);
		        client.visitorSet.clear();
			client.visitorSet.add( message.visitorList);
			ta.append(message.message + '\n' );
			client.paintVisitors();
			break;
		    case Message.LOGOUT:
			client.visitorSet.clear();
			client.visitorSet.add(message.visitorList);
		    //client.visitorSet.remove( message.SID );
			ta.append(message.message + 'n');
			client.paintVisitors( );
			break;
		}
		System.out.printf("\n\n====The visitor list in %s =====\n", client.me.toShortString());
		client.visitorSet.print();
		System.out.printf("\n==== end of visitor list =====\n\n");
	   }
 	} catch( IOException e ) { ta.append("Chatroom server is down.\n");  }
    }
}
