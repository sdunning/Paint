//import javax.swing.JEditorPane;
import java.awt.Color;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


public class CustomChatBox extends JTextPane {
	
	private StyledDocument doc = (StyledDocument) this.getDocument();
	//private HTMLDocument doc = null;
	//private HTMLEditorKit kit = null;
	private StringBuffer buffer = new StringBuffer();
	private StringTokenizer token = null;
	private Vector<String> segments = new Vector<String>();
	private Color current = new Color(0,0,0);
	private Color defaultColor = new Color(0,0,0);
	private VisitorSet users = null;
	DefaultCaret caret = (DefaultCaret) this.getCaret();
	//static String newline = System.getProperty("line.separator");
	
	public CustomChatBox() { 
		super();
		setEditable(false);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		setContentType("text/html");
		buffer.append("<hmtl>");
		//doc = (HTMLDocument) this.getDocument();
		//kit = (HTMLEditorKit) this.getEditorKit();
	}
	
	private void stringSeperator(String str, String delimiter) {
		if (delimiter != null) token = new StringTokenizer(str, delimiter);
		else token = new StringTokenizer(str);
		while(token.hasMoreTokens()) {
			segments.addElement(token.nextToken());
		}
	}
	
	private void getString(String str, String delimiter) {
		buffer.append("<p>");
		int k = str.indexOf(' ');
		String name = str.substring(0, k);
		String message = str.substring(k);
		for(int i=0; i<users.size(); i++) {
			if (users.get(i).name.equals(name)) {
				String whole = "";
				switch(i) {
				case 0: whole = "<font color='#990000'>" + name + "</font>" +
				                "<font color='#CD0000'>"+ message + "</font>";
						break;
				
				case 1: whole = "<font color='#000098'>" + name + "</font>" +
		                        "<font color='#0000CD'>"+ message + "</font>";
				        break;
				
				case 2: whole = "<font color='#009800'>" + name + "</font>" +
		                        "<font color='#00CD00'>"+ message + "</font>";
				        break;
				
				case 3: whole = "<font color='#980098'>" + name + "</font>" +
		                        "<font color='#CD00CD'>"+ message + "</font>";
				        break;
				
				case 4: whole = "<font color='#995C00'>" + name + "</font>" +
		                        "<font color='#CC7A00'>"+ message + "</font>";
				        break;
				
				case 5: whole = "<font color='#009898'>" + name + "</font>" +
		                        "<font color='#00CDCD'>"+ message + "</font>";
				        break;
				
				default: whole = "<font color='#000000'>" + name + "</font>" +
                                 "<font color='#000000'>"+ message + "</font>";
		                 break;
				}
				buffer.append(whole);
			}
		}
		buffer.append("</p>");
		
	}
	
	public void appendString(String str, String delimiter, VisitorSet users) {
		this.users = users;
		this.setContentType("text/html");
		getString(str, delimiter);
		this.setText(buffer.toString());
	}
	
	public void append(String str, Color color) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_LEFT);
		//int len = this.getDocument().getLength();
		//this.setCaretPosition(len);
		//this.setCharacterAttributes(aset, false);
		//this.replaceSelection(str + "\n");
		try {
			//doc.remove(0, doc.getLength());
			doc.insertString(doc.getLength(), str, aset);
		} catch (BadLocationException e) { e.printStackTrace(); } 
		
	}
	
	private Color parseColor(String code) {
		Color color = null;
		if (!code.startsWith("##")) {
			System.out.println("String was not in color code format");
			return current;
		}
		else {
			if      ( code.toLowerCase().startsWith("black", 2) )      { color = Color.BLACK; current = Color.BLACK; return color; }
			else if ( code.toLowerCase().startsWith("black", 2) )      { color = Color.BLACK; current = Color.BLACK; return color; }
			else if ( code.toLowerCase().startsWith("blue", 2) )       { color = Color.BLUE; current = Color.BLUE; return color; }
			else if ( code.toLowerCase().startsWith("cyan", 2) )       { color = Color.CYAN; current = Color.CYAN; return color; }
			else if ( code.toLowerCase().startsWith("gray", 2) )       { color = Color.GRAY; current = Color.GRAY; return color; }
			else if ( code.toLowerCase().startsWith("green", 2) )      { color = Color.GREEN; current = Color.GREEN; return color; }
			else if ( code.toLowerCase().startsWith("magenta", 2) )    { color = Color.MAGENTA; current = Color.MAGENTA; return color; }
			else if ( code.toLowerCase().startsWith("orange", 2) )     { color = Color.ORANGE; current = Color.ORANGE; return color; }
			else if ( code.toLowerCase().startsWith("pink", 2) )       { color = Color.PINK; current = Color.PINK; return color; }
			else if ( code.toLowerCase().startsWith("red", 2) )        { color = Color.RED; current = Color.RED; return color; }
			else if ( code.toLowerCase().startsWith("white", 2) )      { color = Color.WHITE; current = Color.WHITE; return color; }
			else if ( code.toLowerCase().startsWith("yellow", 2) )     { color = Color.YELLOW; current = Color.YELLOW; return color; }
			else if ( code.toLowerCase().startsWith("dark_gray", 2) )  { color = Color.DARK_GRAY; current = Color.DARK_GRAY; return color; }
			else if ( code.toLowerCase().startsWith("light_gray", 2) ) { color = Color.LIGHT_GRAY; current = Color.LIGHT_GRAY; return color; }
			else {
				System.out.println("Color was not valid.");
			}
		}
		return current;
	}
}
