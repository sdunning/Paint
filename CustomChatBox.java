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
	
	private StringBuffer buffer = new StringBuffer();
	protected VisitorSet users = null;
	DefaultCaret caret = (DefaultCaret) this.getCaret();
	CommunityCanvas paint;
	
	public CustomChatBox(CommunityCanvas paint) { 
		super();
		this.paint = paint;
		setEditable(false);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		setContentType("text/html");
		buffer.append("<hmtl><h3>Welcome to Community Canvas</h3>");
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
				case 0: whole = "<font color='#990000'><b>" + name + "</b></font>" +
				                "<font color='#CD0000'>"+ message + "</font>";
				        paint.setGUIcolor(new Color(255, 120, 120));
						break;
				
				case 1: whole = "<font color='#000098'><b>" + name + "</b></font>" +
		                        "<font color='#0000CD'>"+ message + "</font>";
				        paint.setGUIcolor(new Color(88, 88, 255));
				        break;
				
				case 2: whole = "<font color='#009800'><b>" + name + "</b></font>" +
		                        "<font color='#00CD00'>"+ message + "</font>";
				        paint.setGUIcolor(new Color(170, 245, 100));
				        break;
				
				case 3: whole = "<font color='#980098'><b>" + name + "</b></font>" +
		                        "<font color='#CD00CD'>"+ message + "</font>";
				        paint.setGUIcolor(new Color(190, 161, 211));
				        break;
				
				case 4: whole = "<font color='#995C00'><b>" + name + "</b></font>" +
		                        "<font color='#CC7A00'>"+ message + "</font>";
				        paint.setGUIcolor(new Color(235, 200, 82));
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
	
	public Color getUserColor(String username) {
		int i;
		Color color = null;
		if (users != null) {
			for (i = 0; i < users.size(); i++) {
				if (users.get(i).equals(username))
					break;
			}
			switch (i) {
			case 0:
				color = new Color(221, 54, 54);
				break;
			//case 1 : color = new Color(); break;
			//case 2 : color = new Color(); break;
			//case 3 : color = new Color(); break;
			//case 4 : color = new Color(); break;
			}
		}
		return color;
	}
}
