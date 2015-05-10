import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.Serializable;

public class BrushStroke implements Serializable{
    
    public static final int SQUARE = 0;
    public static final int CIRCLE = 1;
    public static final int PEN = 2;
    public static final int LINE = 3;
    public static final int TEXT = 4;
    public static final int BACKGROUND = 9;
    
    private static final long serialVersionUID = 1052570845038232579L;
    private int newX = 0, oldX = 0;
    private int newY = 0, oldY = 0;
    private int brushType = 0;
    private int brushSize = 0;
    private int red = 0, green = 0, blue = 0, alpha = 0;
    private Color color = null;
    private String user = "";
    private int group = -1;
    private String paintText = "";
    private Font paintFont = null;
    private SerializableBufferedImage bgImage = null;
    
    protected Message message = null;
    
    private void colorSeperator() {
        if (color != null) {
            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();
            alpha = color.getAlpha();
        }
    }
    
    public BrushStroke(int x, int y, int type, int size, Color color, String user, Message msg, int group, 
    		String paintText, Font paintFont, SerializableBufferedImage image) {
        oldX = x; oldY = y; brushType = type; brushSize = size; this.color = color; this.user = user; this.group = group;
        bgImage = image;
        message = msg;
        this.paintText = paintText;
        this.paintFont = paintFont;
        colorSeperator();
    }
    public BrushStroke(int oldX, int newX, int oldY, int newY, int type, int size, Color color, String user, Message msg, int group, 
    		String paintText, Font paintFont, SerializableBufferedImage image) {
        this.oldX = oldX; this.newX = newX;
        this.oldY = oldY; this.newY = newY;
        brushType = type; brushSize = size;
        this.color = color; colorSeperator();
        this.user = user;
        bgImage = image;
        message = msg;
        this.paintText = paintText;
        this.paintFont = paintFont;
        this.group = group;
    }
    
    public void setX(int x)            { oldX = x; }
    public void setY(int y)            { oldY = y; }
    public void setOldX(int x)         { oldX = x; }
    public void setNewX(int x)         { newX = x; }
    public void setOldY(int y)         { oldY = y; }
    public void setNewY(int y)         { newY = y; }
    public void setOld(int x, int y)   { oldX = x; oldY = y; }
    public void setNew(int x, int y)   { newX = x; newY = y; }
    public void setType(int type)      { brushType = type; }
    public void setSize(int size)      { brushSize = size; }
    public void setColor(Color color)  { this.color = color; colorSeperator(); }
    public void setUser(String user)   { this.user = user; }
    public void setMessage(Message m)  { message = m; }
    public void setGroup(int group)    { this.group = group; }
    public void setPaintText(String p) { paintText = p; }
    public void setPaintFont(Font f)   { paintFont = f; }
    public void setImage(SerializableBufferedImage image) { bgImage = image; }
    
    public int     getX()           { return oldX; }
    public int     getY()           { return oldY; }
    public int     getOldX()        { return oldX; }
    public int     getNewX()        { return newX; }
    public int     getOldY()        { return oldY; }
    public int     getNewY()        { return newY; }
    public int     getType()        { return brushType; }
    public int     getSize()        { return brushSize; }
    public Color   getColor()       { return color;}
    public int     getRed()         { return red; }
    public int     getGreen()       { return green; }
    public int     getBlue()        { return blue; }
    public int     getAlpha()       { return alpha; }
    public String  getUser()        { return user; }
    public Message getMessage()     { return message; }
    public int     getGroup()       { return group; }
    public String  getPaintText()   { return paintText; }
    public Font    getPaintFont()   { return paintFont; }
    public SerializableBufferedImage getImage()     { return bgImage; }
    
    @Override
    public String toString() {
        return String.format("user=%20s | group=%4d | oldX=%4d | oldY=%4d | newX=%4d | newY=%4d \n"
        		+ "| brushType=%4d | brushSize=%4d | red=%4d | green=%4d | blue=%4d | alpha=%4d\n"
        		+ "| message=%30s |\n| paintText=%30s |\n| PaintFont=%20s\n", user, group, oldX, 
        		oldY, newX, newY, brushType, brushSize, red, green, blue, alpha, message == null? "NULL" : message.message,
        		paintText == null? "NULL" : paintText, paintFont == null? "NULL" : paintFont);
    }
}
