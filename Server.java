import java.net.*;
import java.awt.image.BufferedImage;
import java.io.*;
//import java.util.Vector;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Vector;

public class Server {

    static Monitor		mtr =  new Monitor();
    static Socket		csk =  null;
    static ServerSocket	ssk =  null;
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

class Group {
	private int groupNum = -1;
	private PadDraw canvas = null;
	protected final int MAX = 5;
	private VisitorSet members = null;
	
	public Group(int groupNum, Visitor v) {
		members = new VisitorSet();
		canvas = new PadDraw();
		this.groupNum = groupNum;
		this.addMember(v);
	}
	
	public void addMember( Visitor v) {
		System.out.printf("%s\n", v);
		if (members.size() < MAX) members.add(v);
		else System.out.printf("Group %d is full.", groupNum);
	}
	
	public void removeMember( Visitor v) {
		if (members.size() > 0) members.remove(v);
		else System.out.printf("Group %d is already empty.", groupNum);
	}
	
	public int getNumMembers()     { return members.size(); }
	public int getNum()            { return groupNum; }
	public VisitorSet getMembers() { return members;}
	
	public boolean isMember(Visitor v) {
		for (int i=0; i<getNumMembers(); i++) {
			if (v.ID == members.get(i).ID) return true;
		}
		return false;
	}
	
	public void updateCanvas(BrushStroke stroke) {
		if (stroke.message == null) canvas.paintBrushStroke(stroke);
		else return;
	}
	
	public BufferedImage getCanvas() { return canvas.image; }
	
	/*@Override
	public String toString() {
		String str = "";
		for (int i=0; i<)
	}*/
}

class Agent implements Runnable {
    ObjectInputStream 	in = null, inImage = null;
    ObjectOutputStream	out = null, outImage = null;
    IndexedOutput	    idxOut = null;
    Monitor		        mtr = null;
    Visitor             user = null;
    //BrushStroke		    stroke = null; 
    Message             message = null;
    int			        vID;
    Group               group = null;
    
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
    			BrushStroke stroke = null;
    			stroke = (BrushStroke) in.readObject();
    			//in.reset();
    			System.out.printf("Server  : [ %s ]", stroke);
    			if (stroke.getType() == -3) {
    				mtr.remove ( user, idxOut );
    				mtr.remove(user, group);
    				break;
    			}
    			if (stroke.getType() == -1) {
    				System.out.println(stroke.message.message);
    				user = new Visitor(vID, stroke.getUser());
    				idxOut = new IndexedOutput ( vID, out);
    				mtr.add(user); mtr.add( idxOut );
    				if (!mtr.isGroup(stroke.getGroup()) || mtr.noGroups()) {
    					group = new Group(stroke.getGroup(), user);
    					mtr.add(group);
    				}
    				else if (mtr.isGroup(stroke.getGroup()) && !mtr.isGroupFull(stroke.getGroup())) {
    					mtr.addToGroup(user, stroke.getGroup());
    					group = mtr.groups.get(mtr.getGroup(stroke.getGroup()));
    				}
    				else {
    					int index = 0;
    					while(true) {
    						if (!mtr.isGroup(stroke.getGroup())) {
    							stroke.setGroup(index);
    							break;
    						}
    					}
    					group = new Group(stroke.getGroup(), user);
    					mtr.add(group);
    				}
    			}
    			if(stroke.message != null) stroke.message.message = user.name + " : " + stroke.message.message;
    			System.out.printf("\n%s\n", stroke.getPaintText() == null? "Text is NULL" : stroke.getPaintText());
    			System.out.printf("\n%s\n", stroke.getPaintFont() == null? "Font is NULL" : stroke.getPaintFont());
    			System.out.println(":::::::\n\n");
    			mtr.groupBroadcast(group, stroke);
    		} 
    	} catch ( Exception e ) {
    		e.printStackTrace();
    		mtr.remove(user, group);
    		mtr.remove ( user, idxOut );
    		System.out.printf("%s has left the session.", user.toShortString());
    		return;
    	}
    }
}

class Monitor {
	Vector<Group> groups = null;
	VisitorSet visitorSet = null;
	OutputSet outSet  = null;
    
	public Monitor() {
		groups = new Vector<Group>();
		visitorSet = new VisitorSet();
        outSet = new OutputSet();
    }
	
	synchronized public boolean noGroups() {
		if (groups.size() <= 0) return true;
		else return false;
	}
	
	synchronized public boolean isGroup(int num) {
		for (int i=0; i<groups.size(); i++) {
			if (num == groups.get(i).getNum()) return true;
		}
		return false;
	}
	synchronized public int getGroup(int num) {
		for (int i=0; i<groups.size(); i++) {
			if (num == groups.get(i).getNum()) return i;
		}
		return -1;
	}
	
	synchronized public boolean isGroupFull(int num) {
		if (isGroup(num)) {
			if (groups.get(getGroup(num)).getNumMembers() < 5) return false;
			else return true;
		}
		return false;
	}
	
	synchronized public void addToGroup(Visitor v, int groupNum) {
		groups.get(getGroup(groupNum)).addMember(v);
	}
	
	synchronized public void add( IndexedOutput out  ) { outSet.add(out); }
	synchronized public void add( Visitor v) { visitorSet.add(v); }
	synchronized public void add( Group g) { groups.add(g); }
	synchronized public void remove( Group g) { groups.remove(g); }
	synchronized public void remove(Visitor v, Group g) {
		groups.get(getGroup(g.getNum())).removeMember(v);
	}
    synchronized public void remove( Visitor v, IndexedOutput out ) { 
    	outSet.remove(out); visitorSet.remove(v);
    }
    
    synchronized public void groupBroadcast(Group g, BrushStroke stroke ) {
    	Group grp = groups.get(getGroup(g.getNum()));
    	Iterator<IndexedOutput> itr = outSet.iterator();
    	try{
    		IndexedOutput idxOut = null;
    		while ( itr.hasNext() ) {
    			idxOut = itr.next();
    			if (stroke.message != null){
              	  System.out.println(stroke.message.visitorList);
              	  stroke.message.visitorList = grp.getMembers().toString();
                }
    			for (int i=0; i< grp.getNumMembers(); i++) {
    				if (idxOut.ID == grp.getMembers().get(i).ID) {
    					idxOut.out.writeObject(stroke);
    					idxOut.out.flush();
    					idxOut.out.reset();
    				}
    			}
    		}
    	} catch(IOException e) { e.printStackTrace(); }
    }
    synchronized public void broadcast( BrushStroke stroke ) {
      Iterator<IndexedOutput> itr = outSet.iterator();
      try {
          IndexedOutput idxOut = null;
          while ( itr.hasNext() ) {
              idxOut = itr.next();
              if (stroke.message != null){
            	  System.out.println(stroke.message.visitorList);
            	  stroke.message.visitorList = visitorSet.toString();
              }
              idxOut.out.writeObject( stroke );
              idxOut.out.flush();
              System.out.println(visitorSet);
         }
      } catch (IOException e ) { e.printStackTrace(); }
   }
}
