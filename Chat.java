import java.awt.* ;
import java.awt.event.* ;

import javax.swing.* ;
import javax.swing.border.* ;

import java.io.* ;
import java.net.* ;
import java.util.* ;
	
public class Chat extends JPanel implements ActionListener, MouseListener  {

	CustomChatBox box = new CustomChatBox();
    int		port = 8705;
    int		frame_height = 350;
    int		font_size = 9;
    JLabel	lb = new JLabel("Message: ");
    public JTextField txt = new JTextField(20);
    JTextArea	ta = new JTextArea();
    JPanel	pl = new JPanel();
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
    Paint2 paint = null;

    public Chat(Paint2 paint) { 
	    this.paint = paint; init();
    }
   
    public void init() {
    	setLayout( new BorderLayout() );
    	plEast.setBorder( new TitledBorder("Users"));
    	plEast.setPreferredSize(new Dimension(100, frame_height));
    	plEast.setFont(new Font("verdana", Font.PLAIN, font_size));	
    	plEast.addMouseListener( this );

    	plSouth.setLayout(new BorderLayout());
    	plSouth.setBackground(new Color(198, 255, 125));
    	pl.setBackground(new Color(198, 255, 125));
    	setBackground(new Color(198, 255, 125));
    	ta.setBackground(Color.white);
    	ta.setForeground(Color.black); 
    	ta.setEditable(false);
    	ta.setSize(300, 200);
    	pl.setLayout(new FlowLayout());
    	txt.setFont(new Font("verdana", Font.PLAIN, font_size));
    	pl.add(lb);
    	pl.add(txt);
    	txt.addActionListener(this);
    	txt.setEnabled(true);
    	txt.setColumns(20);
    	txt.setToolTipText("Enter message to send and press ENTER");
    	plSouth.add(pl, BorderLayout.CENTER);
    	
    	ta.setFont(new Font("verdana", Font.PLAIN, font_size) );
    	//add(new JScrollPane(ta), BorderLayout.CENTER);
    	add(new JScrollPane(box), BorderLayout.CENTER);
    	add(plSouth, BorderLayout.SOUTH);
    	add(plEast, BorderLayout.WEST);
    	
    	setSize( frame_height + 50, frame_height);
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
   

   // Implementation of MouserListener
   
   public void mouseEntered( MouseEvent e ) { }
   public void mouseExited( MouseEvent e ) { }
   public void mousePressed( MouseEvent e ) { }
   public void mouseReleased( MouseEvent e ) { }
   public void mouseClicked( MouseEvent e ) {
       
       PointerInfo a = MouseInfo.getPointerInfo();

       int x = (int) e.getX();
       int y = (int) e.getY();
       int k = (y - 21) / 9;
       System.out.printf("Mouse x: %d  y: %d   k: %d\n", x, y, k);
   }
 
   static String str = null;

   public void actionPerformed(ActionEvent e ) {
	Object obj = e.getSource();

	if ( obj == txt ) {
	        str = new String(txt.getText()).trim();
		if ( str.equals("") ) return;
		message.set( paint.getUser(), str, null); 

		Thread thr = new BrushStrokeSender( paint.getOutput(), new BrushStroke(0,0,-2,0,null,paint.getUser(),new Message(message)) );
		thr.start() ;
		try { thr.join(); } catch (InterruptedException f )  { }

		txt.setText("");
		txt.requestFocus();
	}
   }
}



