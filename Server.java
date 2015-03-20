import java.net.*;
import java.io.*;
//import java.util.Vector;
import java.util.Scanner;
import java.util.Iterator;

public class Server {

    static Monitor		mtr =  new Monitor();
    static Socket		csk =  null;
    static ServerSocket		ssk =  null;
    static Scanner		scn = new Scanner( System.in);
    static int			vID = 0;

    public static void main( String args[] ) {
        try {
        	System.out.println("Server started...");
            ssk = new ServerSocket( 8704);
	    	while ( true ) {
	    	    csk = ssk.accept();
	    	    (new Thread ( new Agent( mtr,  csk, ++vID ) ) ).start() ;
	    }	
	} catch ( IOException e  ) { e.printStackTrace() ; }
    }
}

class Agent implements Runnable {
    ObjectInputStream 	in = null;
    ObjectOutputStream	out = null;
    IndexedOutput	    idxOut = null;
    Monitor		        mtr = null;
    Visitor             user = null;
    BrushStroke		    stroke = null; 
    Message             message = null;
    int			        vID;
    
    public Agent (Monitor m, Socket ck, int id) {
    	mtr = m; vID = id; user = null;
    	try { 
    		in  = new ObjectInputStream  ( ck.getInputStream() );
    		System.out.println("InputStream created...");
    		out = new ObjectOutputStream ( ck.getOutputStream() );
    		System.out.println("OutputStream created...");
    	} catch ( IOException e ) { e.printStackTrace(); }
    }

    public void run() {
    	try {
    		while ( true ) {
    			stroke = (BrushStroke) in.readObject();
    			System.out.printf("Server  : [ %s ]", stroke);
    			//if (stroke.getType() == -2) System.out.println(stroke.message);
    			if (stroke.getType() == -1) {
    				System.out.println(stroke.message.message);
    				user = new Visitor(vID, stroke.getUser());
    				idxOut = new IndexedOutput ( vID, out);
    				mtr.add(user); mtr.add( idxOut );
    			}
    			//stroke.message.set(stroke.message.user, stroke.message.message, stroke.message.visitorList.toString());
    			if (stroke.message.visitorList != null) System.out.println(stroke.message.visitorList);
    			else System.out.println("Visitor list is null...........");
    			stroke.message.message = user.name + ": " + stroke.message.message;
    			mtr.broadcast(stroke);
    		} 
    	} catch ( Exception e ) {
    		e.printStackTrace();
    		mtr.remove ( user, idxOut );
    		System.out.printf("%s has left the session.", user.toShortString());
    		return;
    	}
    }
}

class Monitor { 
	VisitorSet visitorSet = null;
	OutputSet outSet  = null;
    
	public Monitor() {
		visitorSet = new VisitorSet();
        outSet = new OutputSet();
    }
    
	synchronized public void add( IndexedOutput out  ) { outSet.add(out); }
	synchronized public void add( Visitor v) { visitorSet.add(v); }
    synchronized public void remove( Visitor v, IndexedOutput out ) { 
    	outSet.remove(out); visitorSet.remove(v);
    }

    synchronized public void broadcast( BrushStroke stroke ) {
      Iterator<IndexedOutput> itr = outSet.iterator();
      try {
          IndexedOutput idxOut = null;
          while ( itr.hasNext() ) {
              idxOut = itr.next();
              System.out.println(stroke.message.visitorList);
              stroke.message.visitorList = visitorSet.toString();
              idxOut.out.writeObject( stroke );
              idxOut.out.flush();
              System.out.println(visitorSet);
         }
      } catch (IOException e ) { e.printStackTrace(); }
   }

   /*synchronized public void privBroadcast(Message msg) {
       System.out.println("Enterd Private Chat");
       privList.add(msg.visitorList);
       System.out.printf("\n\n========= Priv-Message will be broadcasted to the following\n");
       privList.print();
       System.out.printf("\n========= end of listing of visitors\n");
       Iterator<IndexedOutput> itr = outSet.iterator();
       try {
           IndexedOutput idxOut = null;
           while (itr.hasNext()) {
               idxOut = itr.next();
               for (int i = 0; i < privList.size(); i++) {
                   if (idxOut.ID == privList.get(i).ID) {
                       idxOut.out.writeObject(msg);;
                       idxOut.out.flush();
                       break;
                   }
               }
           }
           privList.clear();
       } catch (IOException e) { e.printStackTrace(); }
   }*/
}
