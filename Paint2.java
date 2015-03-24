import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Paint2 extends JApplet implements ActionListener/*, ChangeListener/*MouseMotionListener, MouseListener,*/  {
    
    PadDraw drawPad = new PadDraw(this);
    
    final int BRUSH_SIZE_MAX = 100;
    final int BRUSH_SIZE_MIN = 1;
    final int BRUSH_SIZE_INV = 1;
    
    //JColorChooser colorSelector = new JColorChooser();
    WindowedColorChooser colorChooser = new WindowedColorChooser("Pick a color...");
    Login login = new Login("Login");
    Chat chat = new Chat(this);
    
    JPanel  panWest = new JPanel();
    JPanel  panNorth = new JPanel();
    JPanel  panSouth = new JPanel();
    JPanel  subWest = new JPanel();
    JPanel  subEast = new JPanel();
    JPanel  subSubEast = new JPanel();
    JPanel  colorBox = new JPanel();
    
    JTextField info[] = { new JTextField(10),
            new JTextField(10),
            new JTextField(10) };
    JSlider sldBrushSize = new JSlider(JSlider.HORIZONTAL, BRUSH_SIZE_MIN, BRUSH_SIZE_MAX, BRUSH_SIZE_INV);
    JButton random = new JButton("Random");
    JButton eraseBox = new JButton("Eraser");
    JButton backGround = new JButton("BG Color");
    JButton save = new JButton("Save");
    JButton open = new JButton("Open");
    JButton chooseColor = new JButton("Color");
    JButton connector = new JButton("Connect");
    
    String brushShape[] = { "Square", "Circle", "Pen", "Line" };
    JComboBox<String> shape = new JComboBox<String>(brushShape);
    private int brushMode = 0;
    
    String type[] = { "Clear Forground", "Clear All" };
    JComboBox<String> clear = new JComboBox<String>(type);
    
    public JFileChooser fileChooser = new JFileChooser();
    
    private boolean clearMode = true;
    private boolean eraseMode = false;
    private int brushSize = BRUSH_SIZE_MIN;
    private Color bgColor = Color.WHITE;
    private Color current = Color.BLACK;
    private Color bgGUI = new Color(198, 255, 125);
    private Color buttonColor = new Color(195, 252, 219);
    private File loadedImage = null;
    private String username = "";
    private String password = "";
    
    Connection         cnn = null;
    PreparedStatement  pStmt = null;
    Statement          stmt = null;
    ResultSet          res = null ;
    ResultSetMetaData  meta = null;

    String url = "jdbc:oracle:thin:@delphi.cs.csubak.edu:1521:dbs01";
    
    Socket                     paintSocket = null;
    ObjectOutputStream         out = null;  
    ObjectInputStream          in  = null;
    
    private final int PORT = 8704;
    private final String HOST = "localhost";
    //private final String HOST = "sleipnir.cs.csubak.edu";
    private boolean connected = false;
    
    public static void main(String[] args){
/*        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {}
          catch (ClassNotFoundException e) {}
          catch (InstantiationException e) {}
          catch (IllegalAccessException e) {}
*/        
        JFrame frame = new JFrame("Community Canvas");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(635, 600);
        frame.setBackground(new Color(180, 180, 180));
        Paint2 app = new Paint2();
        app.init();
        frame.getContentPane().add(app);
        //frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
    }
    
    public void init() {
        Container c = getContentPane();
        c.setSize(1100, 700);
        c.setLayout(new BorderLayout() );
        c.setBackground(bgGUI);
        
        initSliders();
        //panWest.add(sldBrushSize);
        //panWest.setBackground(bgGUI);
        panNorth.setLayout(new BorderLayout());
        panNorth.setBackground(bgGUI);
        
        colorBox.setPreferredSize(new Dimension(30, 30));
        colorBox.setBackground(current);
        colorBox.setToolTipText("Current Color");
        
        backGround.addActionListener(this);
        backGround.setToolTipText("Set background color as Current Color.");
        
        eraseBox.addActionListener(this);
        eraseBox.setToolTipText("Toggle Eraser mode.");
        
        fileChooser.addActionListener(this);
        fileChooser.setFileHidingEnabled(true);
        fileChooser.setCurrentDirectory(new File("C:/Users/Owner/Pictures/"));
        
        clear.addActionListener(this);
        connector.addActionListener(this);
        shape.addActionListener(this);
        random.addActionListener(this);
        save.addActionListener(this);
        open.addActionListener(this);
        chooseColor.addActionListener(this);
        info[2].addActionListener(this);
        login.login.addActionListener(this);
        login.cancel.addActionListener(this);
        
        shape.setSelectedIndex(2);
        
        String information[] = { "X : null", "Y : null", "Brush Size : " + brushSize};
        for (int i = 0; i < information.length; i++) {
            info[i].setText(information[i]);
        }
        
        colorChooser.ok.addActionListener(this);
        
        subWest.setLayout( new BorderLayout());
        subWest.add(connector, BorderLayout.WEST);
        subWest.setBackground(bgGUI);
        
        subEast.setLayout(new BorderLayout());
        subEast.setBackground(Color.white);
        subSubEast.setLayout( new GridLayout(2, 7) );
        subSubEast.setBackground(Color.white);
        
        colorBox.setBackground(current);
        backGround.setBackground(buttonColor);
        open.setBackground(buttonColor);
        eraseBox.setBackground(buttonColor);
        shape.setBackground(buttonColor);
        clear.setBackground(buttonColor);
        chooseColor.setBackground(buttonColor);
        random.setBackground(buttonColor);
        save.setBackground(buttonColor);
        
        /*backGround.setForeground(Color.white);
        open.setForeground(Color.white);
        eraseBox.setForeground(Color.white);
        shape.setForeground(Color.white);
        clear.setForeground(Color.white);
        chooseColor.setForeground(Color.white);
        random.setForeground(Color.white);
        save.setForeground(Color.white);*/
        
        subSubEast.add(colorBox);
        subSubEast.add(backGround);
        subSubEast.add(open);
        subSubEast.add(eraseBox);
        subSubEast.add(shape);
        subSubEast.add(clear);
        subSubEast.add(chooseColor);
        subSubEast.add(random);
        subSubEast.add(save);
        
        for (int i = 0; i < info.length; i++) {
            if (i != 2) info[i].setEditable(false);
            info[i].setBackground(Color.WHITE);
            info[i].setForeground(Color.BLACK);
            info[i].setFont(new Font("Arial", Font.PLAIN, 12));
            subSubEast.add(info[i]);
        }
        info[2].setToolTipText("Type in and display brush size. " +
                               "Type value and press ENTER.");
        subEast.add(subSubEast, BorderLayout.WEST);
        
        sldBrushSize.setBackground(buttonColor);
        connector.setBackground(buttonColor);
        
        panSouth.setLayout(new BorderLayout());
        panSouth.add(sldBrushSize, BorderLayout.EAST);
        panSouth.add(subWest, BorderLayout.WEST);
        panNorth.add(subEast, BorderLayout.EAST);
        panSouth.setBackground(bgGUI);
        
        drawPad.setBackground(bgGUI);
        
        c.add(drawPad, BorderLayout.CENTER);
        c.add(panNorth, BorderLayout.NORTH);
        c.add(chat, BorderLayout.EAST);
        c.add(panSouth, BorderLayout.SOUTH);
       }
       
       private void initSliders() {
           sldBrushSize.setMajorTickSpacing(10);
           sldBrushSize.setBackground(new Color(210, 210, 210));
           sldBrushSize.setPaintTicks(true);
           sldBrushSize.setPaintLabels(false);
           sldBrushSize.setToolTipText("Adjust the size of the paint brush ( in pixels ).");
           sldBrushSize.addChangeListener( new ChangeListener() {
               public void stateChanged(ChangeEvent e) {
                   if (sldBrushSize.getValue() < 1) sldBrushSize.setValue(1);
                   brushSize = sldBrushSize.getValue();
                   info[2].setText("Brush Size : " + brushSize);
                   repaint(); }});
       }
       
       private void connect() {
           if (connected) return;
           try {
               try { Class.forName("oracle.jdbc.driver.OracleDriver"); }
               catch (ClassNotFoundException ee) { ee.printStackTrace(); System.exit(-1); }
               cnn = DriverManager.getConnection(url, "cs342", "c3m4p2s");
               cnn.setAutoCommit(false);
           } catch (SQLException e ) { e.printStackTrace(); System.exit(-1); }
           try {
        	   if ( stmt == null ) stmt = cnn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
   		   } catch (SQLException e) { e.printStackTrace(); }
           try {
        	   String query = "SELECT * FROM SDUNN_SROCHA_USERS WHERE UserName = '" + username + "'";
        	   System.out.println(query);
               res = stmt.executeQuery(query);
               meta = res.getMetaData();
               String row;
               int numColumns = meta.getColumnCount();
               while(res.next()) {
                   for (int i=1; i<=numColumns; i++) {
                	   if (meta.getColumnLabel(i).equals("PASSWORD")) {
                		   if (!res.getString(i).equals(password)) {
                			   username = "";
                			   JOptionPane.showMessageDialog(null, "Incorrect Username or Password!");
                		   }
                		   else {
                			   try {
                	               paintSocket = new Socket( HOST, PORT);
                	               out = new ObjectOutputStream( paintSocket.getOutputStream());
                	               in = new ObjectInputStream(paintSocket.getInputStream());
                	               (new Thread (new BrushStrokeReciever(in, drawPad, this, chat, chat.box))).start();
                	               Message msg = new Message();
                	               msg.set(username, "Logged in...", null);
                	               Thread send = new BrushStrokeSender(out, new BrushStroke(0, 0, 0, 0, -1, 0, null, username, msg));
                	               send.start();
                	               try { send.join(); } catch (InterruptedException f )  { }
                	               setConnected(true);
                	           } catch(IOException er) { er.printStackTrace(); }
                			   shape.setSelectedIndex(0);
                               connector.setBackground(Color.GREEN);
                               connector.setText("Connected...");
                		   }
                	   }
                   }
               }
           } catch (SQLException e1) { e1.printStackTrace(); }
       }
       
       private Color generateColor() {
           int r, g, b;
           Random rand = new Random();
           r = rand.nextInt(256);
           g = rand.nextInt(256);
           b = rand.nextInt(256);
           return new Color(r,g,b);
       }

       @Override
       public void actionPerformed(ActionEvent e) {
           // TODO Auto-generated method stub
           Object obj = e.getSource();
           if (obj == info[2]) {
               String s = info[2].getText();
               int grab = Integer.parseInt(s.replaceAll("[\\D]", ""));
               if (grab < 1) grab = 1;
               if (grab > 100) grab = 100;
               brushSize = grab;
               info[2].setText("Brush Size : " + brushSize);
               sldBrushSize.setValue(brushSize);
               return;
           }
           if (obj == open) {
               int returnVal = fileChooser.showOpenDialog(Paint2.this);

               if (returnVal == JFileChooser.APPROVE_OPTION) {
                   loadedImage = fileChooser.getSelectedFile();
                   drawPad.setImage(loadedImage);
               }
               return;
           }
           if (obj == save) {
               SaveBox box = new SaveBox(drawPad.image);
               box.setPathName("Saved_Images/");
               box.displayFrame(true);
               return;
           }
           if (obj == backGround) {
               bgColor = current;
               drawPad.clear(true);
               return;
           }
           if (obj == colorChooser.ok) {
               current = colorChooser.getColor();
               colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == chooseColor) {
               current = colorChooser.getColor();
               colorChooser.setVisible(true);
               return;
           }
           if (obj == random) {
               current = generateColor();
               colorChooser.setColor(current);
               colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == eraseBox) {
               eraseMode = !eraseMode;
               eraseBox.setBackground(eraseMode? new Color(100,255,100) :null);
               return;
           }
           if (obj == connector) {
        	   login.setVisible(true);
               return;
           }
           if (obj == login.login) {
        	   username = login.username.getText().trim();
        	   password = String.valueOf(login.password.getPassword());
        	   login.setVisible(false);  
        	   connect();
           }
           if (obj == login.cancel) {
        	   login.setVisible(false);
        	   login.username.setText("");
        	   login.password.setText("");
           }
           
           if (obj == shape) {
               String t = (String) shape.getSelectedItem();
               if (t.equals(brushShape[0])) { brushMode = BrushStroke.SQUARE; return; }
               if (t.equals(brushShape[1])) { brushMode = BrushStroke.CIRCLE; return; }
               if (t.equals(brushShape[2])) { brushMode = BrushStroke.PEN; return; }
               if (t.equals(brushShape[3])) { brushMode = BrushStroke.LINE; return; }
               return;
           }
           if (obj == clear) {
               String t = (String) clear.getSelectedItem();
               if (t.equals(type[0])) {
                   clearMode = true;
                   drawPad.clear(clearMode);
               }
               else {
                   clearMode = false;
                   bgColor = Color.WHITE;
                   drawPad.clear(clearMode);
               }
               return;
           }
       }
       
       public Color getColor(boolean erase) { if(erase) return bgColor; else return current; }
       public int getBrushSize()            { return brushSize; }
       public void setBrushSize(int size)   { brushSize = size; sldBrushSize.setValue(size);}
       public int getBrushShape()           { return brushMode; }
       public void setBrushShape(int bt)    { brushMode = bt; shape.setSelectedIndex(bt); }
       public boolean isEraseMode()         { return eraseMode; }
       public Color getBgColor()            { return bgColor; }
       public void setBgColor(Color bg)     { bgColor = bg;}
       public boolean getClearMode()        { return clearMode; }
       public boolean getConnected()        { return connected; }
       public void setConnected(boolean c)  { connected = c; }
       public ObjectOutputStream getOutput(){ return out; }
       public ObjectInputStream getInput()  { return in; }
       public String getUser()              { return username; }
       public void setUser(String user)     { username = user; }
}


/* Class that creates an interactive image drawing surface
 * that is internet ready
 */

class PadDraw extends JComponent{
    BufferedImage image;
    Paint2 paint2;
    private int height = 700, width = 1000;
    Graphics2D graphics2D;
    private int currentX, currentY, oldX, oldY,
                oldLineX, oldLineY, newLineX, newLineY;
    private Color current = Color.BLACK;

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
                                paint2.getBrushSize(), paint2.isEraseMode()? paint2.getBgColor() : current, paint2.getUser(), null));
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
                                paint2.getBrushShape(), paint2.getBrushSize(), paint2.isEraseMode()? paint2.getBgColor() : current, paint2.getUser(), null));
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
                                    paint2.getBrushShape(), paint2.getBrushSize(), paint2.isEraseMode()? paint2.getBgColor() : current, paint2.getUser(), null));
                            send.start();
                            try { send.join(); } catch (InterruptedException f )  { }
                        }
                        else {
                            Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(currentX, currentY, 
                                    paint2.getBrushShape(), paint2.getBrushSize(), paint2.isEraseMode()? paint2.getBgColor() : current, paint2.getUser(), null));
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
                paint2.setBgColor(stroke.getColor());
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
        graphics2D.setPaint(background? paint2.getBgColor() : Color.white);
        graphics2D.fillRect(0, 0, width, height);
        graphics2D.setPaint(current);
        repaint();
        if (paint2.getConnected()) {
            Thread send = new BrushStrokeSender(paint2.getOutput(), new BrushStroke(0, 0, 0, 0, BrushStroke.BACKGROUND, 0, 
                    background? paint2.getBgColor() : Color.WHITE, paint2.getUser(), null));
            send.start();
            try { send.join(); } catch (InterruptedException f )  { }
        }
    }
    //this is the clear
    //it sets the colors as white
    //then it fills the window with white
    //thin it sets the color back to black
    
    public void setImage(File file) {
        try {
        	BufferedImage newImage = ImageIO.read(file);
            //image = ImageIO.read(file);
        	graphics2D.drawImage(newImage, 0, 0, width, height, null);
        } catch (IOException e) {}
    }
    
    public void setColor(Color c) {
        current = c;
    }

}