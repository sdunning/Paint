//import javax.swing.JEditorPane;
import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;


public class CustomChatBox extends JTextPane {
	
	private StyledDocument doc = (StyledDocument) this.getDocument();
	
	public CustomChatBox() { 
		super();
		setEditable(false);
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
			doc.insertString(doc.getLength(), str, aset);
		} catch (BadLocationException e) { e.printStackTrace(); }
		
	}
}
