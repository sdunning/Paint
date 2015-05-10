import java.awt.Color;
import java.io.*;

import javax.swing.JTextArea;
//import java.net.*;

public class BrushStrokeReciever extends Thread {
    private ObjectInputStream in = null;
    private PadDraw drawPad = null;
    //private BrushStroke stroke = null;
    private Message message = null;
    private Paint2 paint = null;
    private Chat chat = null;
    private Boolean exit = false;
    private Object obj = null;
    private JTextArea ta = null;
    private CustomChatBox box = null;
    
    public BrushStrokeReciever(ObjectInputStream in, PadDraw pad, Paint2 paint, Chat chat, CustomChatBox box) {
        this.in = in; drawPad = pad; this.paint = paint; this.chat = chat; this.box = box; //this.ta = ta;
    }
    
    public void run() {
        if ( in == null ||  drawPad == null || paint == null || chat == null) return;
        while (!exit) {
            try {
            	BrushStroke stroke = null;
            	stroke = (BrushStroke) in.readObject();
            	//in.reset();
            	System.out.printf("RECEIVER: [ %s ]\n", stroke);
            	if ((stroke != null) && (stroke.message != null)) {
            		System.out.printf("MESSAGE: [ %s ]\n",stroke.message.message);
            		//ta.append( stroke.message.message + "\n" );
            		box.appendString(stroke.message.message, ":", chat.visitorSet);
            		chat.visitorSet.clear();
            		if (stroke.message.visitorList != null) { 
            			chat.visitorSet.add(stroke.message.visitorList);
            			chat.paintVisitors();
            			chat.visitorSet.print();
            		}
            		else System.out.println("\nVisitor list is null\n");
            	}
            	else if ((stroke != null) && (stroke.message == null)) {
            		paint.drawPad.paintBrushStroke(stroke);
            	}
            } catch ( IOException e ) { e.printStackTrace(); exit = true;}
              catch ( ClassNotFoundException clf) {clf.printStackTrace(); exit = true; }
        }
    }
}
