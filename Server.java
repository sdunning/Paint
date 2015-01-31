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
    BrushStroke		    stroke = null; 
    int			        vID;
    
    public Agent (Monitor m, Socket ck, int id ) {
	mtr = m;
	vID = id; 
	try { 
	    in  = new ObjectInputStream  ( ck.getInputStream() );
	    out = new ObjectOutputStream ( ck.getOutputStream() );
	} catch ( IOException e ) { e.printStackTrace(); }
    }

    public void run() {
	try {
	    while ( true ) {
	        stroke = (BrushStroke) in.readObject() ;
	        System.out.printf("Server  : [ %s ]\n", stroke);
	        if (stroke.getType() == -1) {
	            idxOut = new IndexedOutput ( vID, out);
	            mtr.add( idxOut  );

	        }
	        mtr.broadcast(stroke);
	    }
	} catch ( Exception e ) { mtr.remove ( idxOut ); return;  }

    }
}

class Monitor { 
    OutputSet		        outSet  = null;
    public Monitor() {
        outSet = new OutputSet();
        }
    synchronized public void	add( IndexedOutput out  ) { outSet.add(out); }

    synchronized public void remove( IndexedOutput out ) { outSet.remove(out); }

    synchronized public void broadcast( BrushStroke stroke ) {
      Iterator<IndexedOutput> itr = outSet.iterator();
      try {
          IndexedOutput idxOut = null;
          while ( itr.hasNext() ) {
              idxOut = itr.next();
              idxOut.out.writeObject( stroke );
              idxOut.out.flush();
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
