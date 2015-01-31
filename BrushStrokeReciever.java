import java.io.*;
//import java.net.*;

public class BrushStrokeReciever extends Thread {
    private ObjectInputStream in = null;
    private PadDraw drawPad = null;
    private BrushStroke stroke = null;
    private Paint2 paint = null;
    
    public BrushStrokeReciever(ObjectInputStream in, PadDraw pad, Paint2 paint) {
        this.in = in; drawPad = pad; this.paint = paint;
    }
    
    public void run() {
        if ( in == null ||  drawPad == null || paint == null) return;
        while (true) {
            try {
                stroke = (BrushStroke) in.readObject();
                System.out.printf("Receiver: [ %s ]\n", stroke);
            } catch ( IOException e ) {e.printStackTrace();}
              catch ( ClassNotFoundException clf) {clf.printStackTrace();}
            
            if (stroke != null) paint.drawPad.paintBrushStroke(stroke);
        }
    }
}
