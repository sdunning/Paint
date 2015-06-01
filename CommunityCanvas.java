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


public class CommunityCanvas extends JApplet implements ActionListener  {
	public CommunityCanvas() {
	}
    
    final int BRUSH_SIZE_MAX = 100;
    final int BRUSH_SIZE_MIN = 1;
    final int BRUSH_SIZE_INV = 1;
    
    //JColorChooser colorSelector = new JColorChooser();
    WindowedColorChooser colorChooser = new WindowedColorChooser("Pick a color...");
    Login login = new Login("Login");
    TextPaint textPaint = new TextPaint();
    GroupChooser groupChooser = new GroupChooser(this);
    Chat chat = new Chat(this);
    PadDraw drawPad = new PadDraw(this);
    
    JPanel  panWest = new JPanel();
    JPanel  panNorth = new JPanel();
    JPanel  panSouth = new JPanel();
    JPanel  subWest = new JPanel();
    JPanel  subWestWest = new JPanel();
    JPanel  subWestEast = new JPanel();
    JPanel  subEast = new JPanel();
    JPanel  subSubWest = new JPanel();
    JPanel  subCenter = new JPanel();
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
    JButton bBlack = new JButton("");
    JButton bGrey = new JButton("");
    JButton bMaroon = new JButton("");
    JButton bRed = new JButton("");
    JButton bOrange = new JButton("");
    JButton bYellow = new JButton("");
    JButton bGreen = new JButton("");
    JButton bBlue = new JButton("");
    JButton bNavy = new JButton("");
    JButton bPurple = new JButton("");
    JButton bWhite = new JButton("");
    JButton bLightGrey = new JButton("");
    JButton bBrown = new JButton("");
    JButton bMagenta = new JButton("");
    JButton bLightOrange = new JButton("");
    JButton bPaleYellow = new JButton("");
    JButton bLime = new JButton("");
    JButton bSkyBlue = new JButton("");
    JButton bSlate = new JButton("");
    JButton bLavender = new JButton("");
    
    
    String brushShape[] = { "Square", "Circle", "Pen", "Line", "Text" };
    JComboBox shape = new JComboBox(brushShape);
    private int brushMode = 0;
    
    String type[] = { "Clear Forground", "Clear All" };
    JComboBox clear = new JComboBox(type);
    
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
    private int group = -1;
    private Font paintFont = new Font("Arial", Font.PLAIN, 12);
    
    private ImageIcon redWifi = new ImageIcon("client_images/redcnct20px-01.png");
    private ImageIcon greenWifi = new ImageIcon("client_images/greencnct20px-01.png");
    
    
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
    //private final String HOST = "192.168.1.109";
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
        CommunityCanvas app = new CommunityCanvas();
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
        changeBackGround(fileChooser, bgGUI);
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
        login.password.addActionListener(this);
        colorChooser.ok.addActionListener(this);
        textPaint.paint.addActionListener(this);
        
        
        
        shape.setSelectedIndex(2);
        
        String information[] = { "X : null", "Y : null", "Brush Size : " + brushSize};
        for (int i = 0; i < information.length; i++) {
            info[i].setText(information[i]);
        }
        
        subWest.setLayout( new BorderLayout());
        subWestWest.add(connector);
        subWestWest.setBackground(bgGUI.darker());
        subWest.add(subWestWest, BorderLayout.WEST);
        subWest.setBackground(bgGUI);
        
        subEast.setLayout(new BorderLayout());
        subEast.setBackground(Color.white);
        subSubWest.setLayout( new GridLayout(2, 7) );
        subSubWest.setBackground(bgGUI);
        subCenter.setBackground(bgGUI);
        
        colorBox.setBackground(current);
        backGround.setBackground(buttonColor);
        open.setBackground(buttonColor);
        eraseBox.setBackground(buttonColor);
        shape.setBackground(buttonColor);
        clear.setBackground(buttonColor);
        chooseColor.setBackground(buttonColor);
        random.setBackground(buttonColor);
        save.setBackground(buttonColor);
        
        backGround.setIcon(new ImageIcon("client_images/backroundcolor20px-01.png"));
        open.setIcon(new ImageIcon("client_images/openicond20px-01.png"));
        eraseBox.setIcon(new ImageIcon("client_images/eraser20px-01-01.png"));
        //shape.setIcon(new ImageIcon("client_images/pencil20px-01.png"));
        //clear.setIcon(new ImageIcon("client_images/mail_send.png"));
        chooseColor.setIcon(new ImageIcon("client_images/clrslct20px-01.png"));
        random.setIcon(new ImageIcon("client_images/rndmcolor-01.png"));
        save.setIcon(new ImageIcon("client_images/save20px-01.png"));
        connector.setIcon(new ImageIcon("client_images/redcnct20px-01.png"));
        connector.setHorizontalTextPosition(SwingConstants.LEFT);
        
        
        subSubWest.add(colorBox);
        subSubWest.add(backGround);
        subSubWest.add(open);
        subSubWest.add(eraseBox);
        subSubWest.add(shape);
        subSubWest.add(clear);
        subSubWest.add(chooseColor);
        subSubWest.add(random);
        subSubWest.add(save);
        
        subWestEast.setLayout(new FlowLayout());
        for (int i = 0; i < info.length; i++) {
            if (i != 2) info[i].setEditable(false);
            info[i].setBackground(Color.WHITE);
            info[i].setForeground(Color.BLACK);
            info[i].setFont(new Font("Arial", Font.PLAIN, 12));
            info[i].setPreferredSize( new Dimension(100, 30));
            subWestEast.add(info[i]);
        }
        
        //subWest.add(subWestEast, BorderLayout.EAST);
        info[2].setToolTipText("Type in and display brush size. " +
                               "Type value and press ENTER.");
        subEast.add(subSubWest, BorderLayout.WEST);
        subWestEast.setBackground(bgGUI);
        
        sldBrushSize.setBackground(buttonColor);
        connector.setBackground(buttonColor);
        connector.setPreferredSize(new Dimension(200, 30));
        
        panSouth.setLayout(new BorderLayout());
        panSouth.add(subWestEast, BorderLayout.CENTER);
        panSouth.add(sldBrushSize, BorderLayout.EAST);
        panSouth.add(subWest, BorderLayout.WEST);
        
        panNorth.add(subCenter, BorderLayout.CENTER);
        GridBagLayout gbl_subCenter = new GridBagLayout();
        gbl_subCenter.columnWidths = new int[]{39, 39, 39, 39, 39, 39, 39, 39, 39, 39, 0, 0};
        gbl_subCenter.rowHeights = new int[]{30, 30, 0};
        gbl_subCenter.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_subCenter.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        subCenter.setLayout(gbl_subCenter);
        bPurple.addActionListener(this);
        bNavy.addActionListener(this);
        bBlue.addActionListener(this);
        bGreen.addActionListener(this);
        bYellow.addActionListener(this);
        bOrange.addActionListener(this);
        bRed.addActionListener(this);
        bMaroon.addActionListener(this);
        bGrey.addActionListener(this);
        
        bBlack.addActionListener(this);
        bBlack.setToolTipText("Black - 0, 0, 0");
        bBlack.setBackground(new Color(0, 0, 0));
        
        GridBagConstraints gbc_bBlack = new GridBagConstraints();
        gbc_bBlack.fill = GridBagConstraints.BOTH;
        gbc_bBlack.insets = new Insets(0, 0, 5, 5);
        gbc_bBlack.gridx = 1;
        gbc_bBlack.gridy = 0;
        subCenter.add(bBlack, gbc_bBlack);
        bGrey.setToolTipText("Grey - 128, 128, 128");
        bGrey.setBackground(new Color(128, 128, 128));
        
        GridBagConstraints gbc_bGrey = new GridBagConstraints();
        gbc_bGrey.fill = GridBagConstraints.BOTH;
        gbc_bGrey.insets = new Insets(0, 0, 5, 5);
        gbc_bGrey.gridx = 2;
        gbc_bGrey.gridy = 0;
        subCenter.add(bGrey, gbc_bGrey);
        bMaroon.setToolTipText("Maroon - 128, 0, 0");
        bMaroon.setBackground(new Color(128, 0, 0));
        
        GridBagConstraints gbc_bMaroon = new GridBagConstraints();
        gbc_bMaroon.fill = GridBagConstraints.BOTH;
        gbc_bMaroon.insets = new Insets(0, 0, 5, 5);
        gbc_bMaroon.gridx = 3;
        gbc_bMaroon.gridy = 0;
        subCenter.add(bMaroon, gbc_bMaroon);
        bRed.setToolTipText("Red - 255, 0, 0");
        bRed.setBackground(new Color(255, 0, 0));
        
        GridBagConstraints gbc_bRed = new GridBagConstraints();
        gbc_bRed.fill = GridBagConstraints.BOTH;
        gbc_bRed.insets = new Insets(0, 0, 5, 5);
        gbc_bRed.gridx = 4;
        gbc_bRed.gridy = 0;
        subCenter.add(bRed, gbc_bRed);
        bOrange.setToolTipText("Orange - 255, 140, 0");
        bOrange.setBackground(new Color(255, 140, 0));
        
        GridBagConstraints gbc_bOrange = new GridBagConstraints();
        gbc_bOrange.fill = GridBagConstraints.BOTH;
        gbc_bOrange.insets = new Insets(0, 0, 5, 5);
        gbc_bOrange.gridx = 5;
        gbc_bOrange.gridy = 0;
        subCenter.add(bOrange, gbc_bOrange);
        bYellow.setToolTipText("Yellow - 255, 255, 0");
        bYellow.setBackground(new Color(255, 255, 0));
        bYellow.setEnabled(true);
        
        GridBagConstraints gbc_bYellow = new GridBagConstraints();
        gbc_bYellow.fill = GridBagConstraints.BOTH;
        gbc_bYellow.insets = new Insets(0, 0, 5, 5);
        gbc_bYellow.gridx = 6;
        gbc_bYellow.gridy = 0;
        subCenter.add(bYellow, gbc_bYellow);
        bGreen.setToolTipText("Dark Green - 0, 128, 0");
        bGreen.setEnabled(true);
        bGreen.setBackground(new Color(0, 128, 0));
        
        GridBagConstraints gbc_bGreen = new GridBagConstraints();
        gbc_bGreen.fill = GridBagConstraints.BOTH;
        gbc_bGreen.insets = new Insets(0, 0, 5, 5);
        gbc_bGreen.gridx = 7;
        gbc_bGreen.gridy = 0;
        subCenter.add(bGreen, gbc_bGreen);
        bBlue.setToolTipText("Blue - 0, 0, 225");
        bBlue.setBackground(new Color(0, 0, 255));
        
        GridBagConstraints gbc_bBlue = new GridBagConstraints();
        gbc_bBlue.fill = GridBagConstraints.BOTH;
        gbc_bBlue.insets = new Insets(0, 0, 5, 5);
        gbc_bBlue.gridx = 8;
        gbc_bBlue.gridy = 0;
        subCenter.add(bBlue, gbc_bBlue);
        bNavy.setToolTipText("Navy - 25, 25, 112");
        bNavy.setBackground(new Color(25, 25, 112));
        
        GridBagConstraints gbc_bNavy = new GridBagConstraints();
        gbc_bNavy.fill = GridBagConstraints.BOTH;
        gbc_bNavy.insets = new Insets(0, 0, 5, 5);
        gbc_bNavy.gridx = 9;
        gbc_bNavy.gridy = 0;
        subCenter.add(bNavy, gbc_bNavy);
        bPurple.setToolTipText("Purple - 128, 0, 128");
        bPurple.setBackground(new Color(128, 0, 128));
        bPurple.setEnabled(true);
        
        GridBagConstraints gbc_bPurple = new GridBagConstraints();
        gbc_bPurple.fill = GridBagConstraints.BOTH;
        gbc_bPurple.insets = new Insets(0, 0, 5, 0);
        gbc_bPurple.gridx = 10;
        gbc_bPurple.gridy = 0;
        subCenter.add(bPurple, gbc_bPurple);
        bLavender.addActionListener(this);
        bSlate.addActionListener(this);
        bSkyBlue.addActionListener(this);
        bLime.addActionListener(this);
        bPaleYellow.addActionListener(this);
        bLightOrange.addActionListener(this);
        bMagenta.addActionListener(this);
        bBrown.addActionListener(this);
        bLightGrey.addActionListener(this);
        bWhite.addActionListener(this);
        bWhite.setToolTipText("White - 255, 255, 255");
        bWhite.setBackground(new Color(255, 255, 255));
        bWhite.setEnabled(true);
        
        GridBagConstraints gbc_bWhite = new GridBagConstraints();
        gbc_bWhite.fill = GridBagConstraints.BOTH;
        gbc_bWhite.insets = new Insets(0, 0, 0, 5);
        gbc_bWhite.gridx = 1;
        gbc_bWhite.gridy = 1;
        subCenter.add(bWhite, gbc_bWhite);
        bLightGrey.setToolTipText("Light Grey - 192, 192, 192");
        bLightGrey.setBackground(new Color(192, 192, 192));
        
        GridBagConstraints gbc_bLightGrey = new GridBagConstraints();
        gbc_bLightGrey.fill = GridBagConstraints.BOTH;
        gbc_bLightGrey.insets = new Insets(0, 0, 0, 5);
        gbc_bLightGrey.gridx = 2;
        gbc_bLightGrey.gridy = 1;
        subCenter.add(bLightGrey, gbc_bLightGrey);
        bBrown.setToolTipText("Brown - 210, 105, 30");
        bBrown.setBackground(new Color(210, 105, 30));
        
        GridBagConstraints gbc_bBrown = new GridBagConstraints();
        gbc_bBrown.fill = GridBagConstraints.BOTH;
        gbc_bBrown.insets = new Insets(0, 0, 0, 5);
        gbc_bBrown.gridx = 3;
        gbc_bBrown.gridy = 1;
        subCenter.add(bBrown, gbc_bBrown);
        bMagenta.setToolTipText("Magenta - 255, 0, 255");
        bMagenta.setBackground(new Color(255, 0, 255));
        
        GridBagConstraints gbc_bMagenta = new GridBagConstraints();
        gbc_bMagenta.fill = GridBagConstraints.BOTH;
        gbc_bMagenta.insets = new Insets(0, 0, 0, 5);
        gbc_bMagenta.gridx = 4;
        gbc_bMagenta.gridy = 1;
        subCenter.add(bMagenta, gbc_bMagenta);
        bLightOrange.setToolTipText("LightOrange - 255, 215, 0");
        bLightOrange.setBackground(new Color(255, 215, 0));
        
        GridBagConstraints gbc_bLightOrange = new GridBagConstraints();
        gbc_bLightOrange.fill = GridBagConstraints.BOTH;
        gbc_bLightOrange.insets = new Insets(0, 0, 0, 5);
        gbc_bLightOrange.gridx = 5;
        gbc_bLightOrange.gridy = 1;
        subCenter.add(bLightOrange, gbc_bLightOrange);
        bPaleYellow.setToolTipText("Pale Yellow - 255, 255, 102");
        bPaleYellow.setBackground(new Color(255, 255, 102));
        bPaleYellow.setEnabled(true);
        
        GridBagConstraints gbc_bPaleYellow = new GridBagConstraints();
        gbc_bPaleYellow.fill = GridBagConstraints.BOTH;
        gbc_bPaleYellow.insets = new Insets(0, 0, 0, 5);
        gbc_bPaleYellow.gridx = 6;
        gbc_bPaleYellow.gridy = 1;
        subCenter.add(bPaleYellow, gbc_bPaleYellow);
        bLime.setToolTipText("Lime - 124, 252, 0");
        bLime.setBackground(new Color(124, 252, 0));
        bLime.setEnabled(true);
        
        GridBagConstraints gbc_bLime = new GridBagConstraints();
        gbc_bLime.fill = GridBagConstraints.BOTH;
        gbc_bLime.insets = new Insets(0, 0, 0, 5);
        gbc_bLime.gridx = 7;
        gbc_bLime.gridy = 1;
        subCenter.add(bLime, gbc_bLime);
        bSkyBlue.setToolTipText("Sky Blue - 135, 206, 250");
        bSkyBlue.setBackground(new Color(135, 206, 250));
        bSkyBlue.setEnabled(true);
        
        GridBagConstraints gbc_bSkyBlue = new GridBagConstraints();
        gbc_bSkyBlue.fill = GridBagConstraints.BOTH;
        gbc_bSkyBlue.insets = new Insets(0, 0, 0, 5);
        gbc_bSkyBlue.gridx = 8;
        gbc_bSkyBlue.gridy = 1;
        subCenter.add(bSkyBlue, gbc_bSkyBlue);
        bSlate.setToolTipText("Slate - 123, 104, 238");
        bSlate.setBackground(new Color(123, 104, 238));
        bSlate.setEnabled(true);
        
        GridBagConstraints gbc_bSlate = new GridBagConstraints();
        gbc_bSlate.fill = GridBagConstraints.BOTH;
        gbc_bSlate.insets = new Insets(0, 0, 0, 5);
        gbc_bSlate.gridx = 9;
        gbc_bSlate.gridy = 1;
        subCenter.add(bSlate, gbc_bSlate);
        bLavender.setToolTipText("Lavender - 221, 160, 221");
        bLavender.setBackground(new Color(221, 160, 221));
        bLavender.setEnabled(true);
        
        GridBagConstraints gbc_bLavender = new GridBagConstraints();
        gbc_bLavender.fill = GridBagConstraints.BOTH;
        gbc_bLavender.gridx = 10;
        gbc_bLavender.gridy = 1;
        subCenter.add(bLavender, gbc_bLavender);
        panNorth.add(subEast, BorderLayout.WEST);
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
       
       public void connect(int group) {
           if (connected) return;
           this.group = group;
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
                	               Thread send = new BrushStrokeSender(out, new BrushStroke(0, 0, 0, 0, -1, 0, null, username, msg, group, null, null, null));
                	               send.start();
                	               try { send.join(); } catch (InterruptedException f )  { }
                	               setConnected(true);
                	           } catch(IOException er) { er.printStackTrace(); }
                			   shape.setSelectedIndex(2);
                               connector.setText("Disconnect...");
                               connector.setIcon(greenWifi);
                		   }
                	   }
                   }
               }
           } catch (SQLException e1) { e1.printStackTrace(); }
       }
       
       public void disconnect() {
    	   try {
               
               Message msg = new Message();
               msg.set(username, "Logged OUT", null);
               Thread send = new BrushStrokeSender(out, new BrushStroke(0, 0, 0, 0, -3, 0, null, username, msg, group, null, null, null));
               send.start();
               try { send.join(); } catch (InterruptedException f )  { }
           	   if ( paintSocket != null ) paintSocket.close();
           	   out = null; in = null;
               setConnected(false);
           } catch(IOException er) { er.printStackTrace(); }
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
               int returnVal = fileChooser.showOpenDialog(CommunityCanvas.this);

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
               drawPad.bgImage = false;
               drawPad.clear(true);
               if (this.getConnected()) {
                   Thread send = new BrushStrokeSender(this.getOutput(), new BrushStroke(0, 0, 0, 0, BrushStroke.BACKGROUND, 0, 
                           this.getBgColor(), this.getUser(), null, this.getGroup(), null, null, null));
                   send.start();
                   try { send.join(); } catch (InterruptedException f )  { }
               }
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
        	   if (connected) {
        		   disconnect();
        		   connector.setIcon(redWifi);
        		   connector.setText("Connect");
        	   }
        	   else {
        		   login.setVisible(true);
        	   }
               return;
           }
           if (obj == login.login || obj == login.password) {
        	   username = login.username.getText().trim();
        	   password = String.valueOf(login.password.getPassword());
        	   login.setVisible(false);
        	   groupChooser.setVisible(true);
        	   //connect();
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
               if (t.equals(brushShape[4])) { brushMode = BrushStroke.TEXT; return; }
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
           if (obj == textPaint.paint) {
        	   textPaint.setVisible(false);
        	   textPaint.setAlwaysOnTop(false);
        	   if (connected) {
        		   Thread send = new BrushStrokeSender(out, new BrushStroke(textPaint.getX(), textPaint.getY(), BrushStroke.TEXT, 0, current, 
        				   username, null, group, textPaint.getText(), textPaint.getFont(), null));
                   send.start();
                   try { send.join(); } catch (InterruptedException f )  { }
        	   }
        	   else {
        		   drawPad.paintTextStroke(textPaint.getX(), textPaint.getY(), textPaint.getText(), textPaint.getFont(), current);
        	   }
           }
           
           /* Begin Color
            * pallet
            */
           if (obj == bBlack) {
        	   current = bBlack.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
        	   return;
           }
           if (obj == bGrey) {
        	   current = bGrey.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bMaroon) {
        	   current = bMaroon.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bRed) {
        	   current = bRed.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bOrange) {
        	   current = bOrange.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bYellow) {
        	   current = bYellow.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bGreen) {
        	   current = bGreen.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bBlue) {
        	   current = bBlue.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bNavy) {
        	   current = bNavy.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bPurple) {
        	   current = bPurple.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bWhite) {
        	   current = bWhite.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bLightGrey) {
        	   current = bLightGrey.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bBrown) {
        	   current = bBrown.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bMagenta) {
        	   current = bMagenta.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bLightOrange) {
        	   current = bLightOrange.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bPaleYellow) {
        	   current = bPaleYellow.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bLime) {
        	   current = bLime.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bSkyBlue) {
        	   current = bSkyBlue.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bSlate) {
        	   current = bSlate.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           if (obj == bLavender) {
        	   current = bLavender.getBackground();
        	   colorBox.setBackground(current);
               drawPad.setColor(current);
               return;
           }
           /*End Color
            * Pallet
            */
           
       }
       
       public void setGUIcolor(Color color) {
    	   this.changeBackGround(colorChooser, color);
    	   this.setBackground(color);
    	   this.getContentPane().setBackground(color);
    	   panWest.setBackground(color);
    	   panNorth.setBackground(color);
    	   panSouth.setBackground(color);
    	   subWest.setBackground(color);
    	   subWestWest.setBackground(color);
    	   subWestEast.setBackground(color);
    	   subEast.setBackground(color);
    	   subSubWest.setBackground(color);
    	   subCenter.setBackground(color);
    	   colorBox.setBackground(color);
    	   chat.setBackground(color);
    	   chat.pl.setBackground(color);
    	   chat.plSouth.setBackground(color);
    	   
       }
       
       private void changeBackGround(Container container, Color color) {
       	container.setBackground(color);
       	for (Component component : container.getComponents()) {
       		if (component instanceof Container)
       			changeBackGround((Container) component, color);
       		else
       			component.setBackground(color);
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
       public int getGroup()                { return group; }
       public void setGroup(int group)      { this.group = group; }
       public void setFont(Font f)          { paintFont = f; }
       public Font getFont()                { return paintFont; }
}