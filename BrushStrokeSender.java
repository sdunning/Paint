import java.io.*;
import java.net.*;

public class BrushStrokeSender extends Thread{
    
    private BrushStroke stroke = null;
    private ObjectOutputStream out = null;
    
    public BrushStrokeSender(ObjectOutputStream out, BrushStroke stroke) {
        this.out = out; this.stroke = stroke;
    }
    
    public void run() {
        if ( out == null || stroke == null) return;
        try {
                out.writeObject(stroke);
                out.flush();
        } catch( IOException e ) { System.out.println("IO FAILED"); e.printStackTrace(); }
    }
}
