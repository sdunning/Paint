import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;


public class PadDraw extends JComponent{
    BufferedImage image, openedImage;
    Paint2 paint2;
    private int height = 700, width = 1000;
    Graphics2D graphics2D;
    private int currentX, currentY, oldX, oldY,
                oldLineX, oldLineY, newLineX, newLineY;
    private Color current = Color.BLACK;
    private Boolean bgImage = false;

    public PadDraw(Paint2 p){
        paint2 = p;
        setDoubleBuffered(false);
        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                oldX = e.getX();
                oldY = e.getY();
                if (paint2.getConnected()) {
                    if (paint2.getBrushShape() != BrushStroke.LINE && paint2.getBrushShape() != BrushStroke.PEN) {
                        Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(oldX, oldY, paint2.getBrushShape(),
                                paint2.getBrushSize(), paint2.isEraseMode()? paint2.getBgColor() : current, paint2.getUser(), null, paint2.getGroup(), null));
                        send.start();
                        try { send.join(); } catch (InterruptedException f )  { }
                    }
                }
                int size = paint2.getBrushSize();
                graphics2D.setColor(paint2.isEraseMode()? paint2.getBgColor() : current);
                if (paint2.getBrushShape() == BrushStroke.SQUARE) graphics2D.fillRect(oldX-(size/2), oldY-(size/2), size, size);
                if (paint2.getBrushShape() == BrushStroke.CIRCLE) graphics2D.fillOval(oldX-(size/2), oldY-(size/2), size, size);
                if (paint2.getBrushShape() == BrushStroke.LINE) {
                        oldLineX = e.getX(); oldLineY = e.getY();
                }
                repaint();
            }
            public void mouseReleased(MouseEvent e) {
                if (paint2.getBrushShape() == BrushStroke.LINE) {
                    newLineX = e.getX(); newLineY = e.getY();
                    if (paint2.getConnected()) {
                        Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(oldLineX, newLineX, oldLineY, newLineY, 
                                paint2.getBrushShape(), paint2.getBrushSize(), paint2.isEraseMode()? paint2.getBgColor() : current, paint2.getUser(), null, paint2.getGroup(), null));
                        send.start();
                        try { send.join(); } catch (InterruptedException f )  { }
                    }
                    graphics2D.setStroke(new BasicStroke(paint2.getBrushSize()));
                    graphics2D.drawLine(oldLineX, oldLineY, newLineX, newLineY);
                    graphics2D.setStroke(new BasicStroke(1));
                    repaint();
                    }
                }
        });
        //if the mouse is pressed it sets the oldX & oldY
        //coordinates as the mouses x & y coordinates
        addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseDragged(MouseEvent e){
                currentX = e.getX();
                currentY = e.getY();
                String information[] = { "X : " + currentX,
                                         "Y : " + currentY,};
                for (int i = 0; i < information.length; i++) {
                    paint2.info[i].setText(information[i]);
                }
                if(graphics2D != null) {
                    graphics2D.setColor(paint2.isEraseMode()? paint2.getBgColor() : current);
                    int size = paint2.getBrushSize();
                    if (paint2.getBrushShape() == BrushStroke.SQUARE) graphics2D.fillRect(currentX-(size/2), currentY-(size/2), size, size);
                    if (paint2.getBrushShape() == BrushStroke.CIRCLE) graphics2D.fillOval(currentX-(size/2), currentY-(size/2), size, size);
                    if (paint2.getBrushShape() == BrushStroke.PEN) graphics2D.drawLine(oldX, oldY, currentX, currentY);
                }
                if (paint2.getConnected()) {
                    if (paint2.getBrushShape() != BrushStroke.LINE) {
                        if (paint2.getBrushShape() == BrushStroke.PEN) {
                            Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(oldX, currentX, oldY, currentY, 
                                    paint2.getBrushShape(), paint2.getBrushSize(), paint2.isEraseMode()? paint2.getBgColor() : current, paint2.getUser(), null, paint2.getGroup(), null));
                            send.start();
                            try { send.join(); } catch (InterruptedException f )  { }
                        }
                        else {
                            Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(currentX, currentY, 
                                    paint2.getBrushShape(), paint2.getBrushSize(), paint2.isEraseMode()? paint2.getBgColor() : current, paint2.getUser(), null, paint2.getGroup(), null));
                            send.start();
                            try { send.join(); } catch (InterruptedException f )  { }
                        }
                    }
                }
                repaint();
                oldX = currentX;
                oldY = currentY;
            }
            public void mouseMoved(MouseEvent e) {
                currentX = e.getX() > 1000 ? 1000 : e.getX();
                currentY = e.getY() > 700 ? 700 : e.getY();
                String information[] = { "X : " + currentX,
                                         "Y : " + currentY,};
                for (int i = 0; i < information.length; i++) {
                    paint2.info[i].setText(information[i]);
                }
            }
        });
        //while the mouse is dragged it sets currentX & currentY as the mouses x and y
        //then it draws a line at the coordinates
        //it repaints it and sets oldX and oldY as currentX and currentY
    }
    
    public void paintBrushStroke(BrushStroke stroke) {
        if(graphics2D != null) {
            graphics2D.setColor(stroke.getColor());
            int size = stroke.getSize();
            if (stroke.getType() == BrushStroke.SQUARE) graphics2D.fillRect(stroke.getX()-(size/2), stroke.getY()-(size/2), size, size);
            if (stroke.getType() == BrushStroke.CIRCLE) graphics2D.fillOval(stroke.getX()-(size/2), stroke.getY()-(size/2), size, size);
            if (stroke.getType() == BrushStroke.PEN) graphics2D.drawLine(stroke.getOldX(), stroke.getOldY(), stroke.getNewX(), stroke.getNewY());
            if (stroke.getType() == BrushStroke.LINE) {
                graphics2D.setStroke(new BasicStroke(size));
                graphics2D.drawLine(stroke.getOldX(), stroke.getOldY(), stroke.getNewX(), stroke.getNewY());
                graphics2D.setStroke(new BasicStroke(1));
            }
            if (stroke.getType() == BrushStroke.BACKGROUND) {
            	if (stroke.getImage() == null) paint2.setBgColor(stroke.getColor());
            	else {
            		openedImage = stroke.getImage().get();
            		bgImage = true;
            		clear(true);
            	}
                graphics2D.setPaint(paint2.getBgColor());
                graphics2D.fillRect(0, 0, width, height);
            }
            graphics2D.setColor(current);
        }
        repaint();
    }

    public void paintComponent(Graphics g){
        if(image == null){
            //height = getSize().height; width = getSize().width;
            image = (BufferedImage) createImage(width, height);
            graphics2D = (Graphics2D)image.getGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clear(false);

        }
        g.drawImage(image, 0, 0, null);
    }
    //this is the painting bit
    //if it has nothing on it then
    //it creates an image the size of the window
    //sets the value of Graphics as the image
    //sets the rendering
    //runs the clear() method
    //then it draws the image


    public void clear(boolean background){
        if (background) {
        	if (bgImage) {
        		graphics2D.drawImage(openedImage, 0, 0, width, height, null);
        		if (paint2.getConnected()) {
        			SerializableBufferedImage sImage = new SerializableBufferedImage(openedImage);
                    Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(0, 0, 0, 0, BrushStroke.BACKGROUND, 0, 
                            Color.WHITE, paint2.getUser(), null, paint2.getGroup(), sImage));
                    send.start();
                    try { send.join(); } catch (InterruptedException f )  { }
                }
        	}
        	else {
        		bgImage = false;
        		graphics2D.setPaint(paint2.getBgColor());
        		graphics2D.fillRect(0, 0, width, height);
        		graphics2D.setPaint(current);
        		if (paint2.getConnected()) {
                    Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(0, 0, 0, 0, BrushStroke.BACKGROUND, 0, 
                            paint2.getBgColor(), paint2.getUser(), null, paint2.getGroup(), null));
                    send.start();
                    try { send.join(); } catch (InterruptedException f )  { }
                }
        	}
        }
        else {
        	bgImage = false;
    		graphics2D.setPaint(Color.WHITE);
    		graphics2D.fillRect(0, 0, width, height);
    		graphics2D.setPaint(current);
    		if (paint2.getConnected()) {
                Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(0, 0, 0, 0, BrushStroke.BACKGROUND, 0, 
                        Color.WHITE, paint2.getUser(), null, paint2.getGroup(), null));
                send.start();
                try { send.join(); } catch (InterruptedException f )  { }
            }
    		//repaint();
        }
        repaint();
        /*if (paint2.getConnected()) {
            Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(0, 0, 0, 0, bgImage? BrushStroke.BGIMAGE : BrushStroke.BACKGROUND, 0, 
                    background? paint2.getBgColor() : Color.WHITE, paint2.getUser(), null, paint2.getGroup(), null));
            send.start();
            try { send.join(); } catch (InterruptedException f )  { }
        }*/
    }
    //this is the clear
    //it sets the colors as white
    //then it fills the window with white
    //thin it sets the color back to black
    
    public void setImage(File file) {
        try {
        	openedImage = ImageIO.read(file);
            //image = ImageIO.read(file);
        	//graphics2D.drawImage(openedImage, 0, 0, width, height, null);
        	bgImage = true;
        	clear(true);
        } catch (IOException e) {}
        
        if (paint2.getConnected()) {
        	SerializableBufferedImage sImage = new SerializableBufferedImage(openedImage);
            Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(0, 0, 0, 0, BrushStroke.BACKGROUND, 0, 
                    Color.WHITE, paint2.getUser(), null, paint2.getGroup(), sImage));
            send.start();
            try { send.join(); } catch (InterruptedException f )  { }
        }
    }
    
    public void setColor(Color c) {
        current = c;
    }
}
