import java.util.Vector;
import java.io.*;
import java.util.Iterator;

public class IndexedOutput implements Comparable<IndexedOutput> {

   // Data Members
   int		      ID  = -1;
   ObjectOutputStream out = null;

   public IndexedOutput(int id, ObjectOutputStream out ) { ID = id; this.out = out; }
   public boolean equals( IndexedOutput o ) { return ID == o.ID; }
   public int     compareTo( IndexedOutput o ) { return ID - o.ID; }
}

class OutputSet {

    // Data members

    Vector<IndexedOutput>	 outSet = new Vector<IndexedOutput>() ;

    public OutputSet() { }

    public void add( IndexedOutput idxOut ) { outSet.add( idxOut ); }
    public void remove( IndexedOutput idxOut ) { outSet.remove( idxOut ); }
    public void remove ( int id ) {
        for ( int i = 0; i < outSet.size() ; i++ ) 
	   if ( id == outSet.get(i).ID ) { outSet.remove( i ); return; }
    }

    public Iterator<IndexedOutput> iterator() { return outSet.iterator(); }
}

