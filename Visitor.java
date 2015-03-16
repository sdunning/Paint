// package chatroom
//
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;

public class Visitor implements Comparable<Visitor>, Serializable {

    public int       ID      = -1;
    public String    name    = null;   // visitor's name
    public boolean   checked = false;  // check to receive private message

    public Visitor() {initialize( -1, "", false); }

    // used on chatroom server side to add visitor to server list.
    public Visitor(int id, String n) { initialize(id, new String(n), false); }
  
    public Visitor( String name ) { initialize (-1, name, false ) ; }
    public Visitor( int id ) { initialize (id, "", false ) ; }

    public void initialize(int id, String n, boolean ck ) {
	ID = id; name = new String(n) ; checked = ck;
    }

    public Visitor ( Visitor v ) { ID = v.ID; name = new String( v.name ); checked = v.checked; }

    public void set(int id) { ID = id ; }
    public void set(String nm ) { name = new String(nm); }
    public void set(int id, String nm ) { ID = id ; name = new String(nm); }
    public void set(int id, String nm, boolean ck) { ID = id; name = new String(nm); checked = ck; }

    public int 	   compareTo( Visitor v )  { return ID - v.ID; }

    public boolean equals( Visitor v )     { return ID == v.ID ; }

    public String  toString() {
         return String.format("%5d %s %s", ID, name == null ? "null" : name,
		checked ? "yes" : "no");
	}
    public String  toShortString() {
         return String.format("%s", name + " (" + ID + ')');
	}

}

class VisitorSet implements Serializable {

  static char SEPARATOR = '|';
  Vector<Visitor> vs = new Vector<Visitor>(10);

   public  void 	clear( ) { vs.clear( ) ; }
   public  void 	add( Visitor v) { vs.add( v ) ; }
   public  void 	remove ( Visitor v) { vs.remove( v ) ; }
   public  void 	remove ( int id ) { 
	for ( int i = 0; i < vs.size(); i++ ) {
	   if ( vs.get(i).ID == id ) vs.remove( vs.get(i) );
	   return;
	}
   }
   public int	size() { return vs.size() ; }

   Visitor get( int k ) {
       if ( k >= 0 && k < vs.size() ) return vs.get(k);
	       else return null;
   }
   public VisitorSet () {}
   public Iterator<Visitor> iterator() { return vs.iterator(); }

   public void print() {
     if ( vs == null ) { System.out.printf ("vector of visitor is null\n"); return; }

     Vector v = null;
     int k = 0;
     Iterator<Visitor> itr = vs.iterator();
     while ( itr.hasNext() )  
      System.out.printf("%3d: %s\n", ++k, itr.next().toShortString( ));
  }

  public String toString() {

     StringBuffer buff = new StringBuffer ( 200 );
     if ( vs == null || vs.size() < 1 ) return ""; 
     Visitor v = null;
     for ( int i = 0; i < vs.size(); i++ ) {
	v = vs.get(i);
	String tmp = String.format("%d%c%s%c", v.ID, SEPARATOR, v.name, SEPARATOR);
	System.out.printf("Next visitor to add:  [%s]\n", tmp);
	buff.append( tmp );
     }
     System.out.printf("visitor list: %s\n", buff.toString() );
     return buff.toString();
  }

  public int[] toVisitorIDArray( ) {
     int a[] = new int[ vs.size() ];
     for ( int i = 0; i < vs.size(); i ++ ) a[i] = vs.get(i).ID;
     return a;
  }
  public void add( String visitorList ) {
     System.out.printf("Visitotliet to be added into visitor set: [%s]\n", visitorList);
     StringTokenizer token = new StringTokenizer( visitorList, "" + SEPARATOR);
     while ( token.hasMoreTokens() ) 
	vs.add ( new Visitor ( Integer.parseInt(token.nextToken()),  token.nextToken() ) );
  }

}
