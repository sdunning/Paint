import javax.swing.JFrame;

import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.JTextArea;

import java.awt.GridBagConstraints;

import javax.swing.JButton;

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JSlider;
import javax.swing.JComboBox;


public class TextPaint extends JFrame implements KeyListener{
	
	JTextArea paintString = new JTextArea(10, 20);
	JButton paint = new JButton("Paint");
	JSlider fontSize = new JSlider();
	private int x = 0, y = 0, newLines = 0;
	String fonts [] = {"arial", "georgia", "impact", "lucida console", "tahoma", "times new roman", "verdana", "webdings"};
	String styles [] = {"Plain", "Bold", "Italic"};
	JComboBox font = new JComboBox(fonts);
	JComboBox style = new JComboBox(styles);
	
	
	
	
	public TextPaint() {
		this.setVisible(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		paintString.setLineWrap(true);
		paintString.requestFocus();
		paintString.addKeyListener(this);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		GridBagConstraints gbc_paintString = new GridBagConstraints();
		gbc_paintString.gridheight = 6;
		gbc_paintString.gridwidth = 12;
		gbc_paintString.insets = new Insets(0, 0, 5, 5);
		gbc_paintString.fill = GridBagConstraints.BOTH;
		gbc_paintString.gridx = 0;
		gbc_paintString.gridy = 0;
		getContentPane().add(paintString, gbc_paintString);
		
		GridBagConstraints gbc_paint = new GridBagConstraints();
		gbc_paint.insets = new Insets(0, 0, 5, 0);
		gbc_paint.gridx = 13;
		gbc_paint.gridy = 3;
		getContentPane().add(paint, gbc_paint);
		
		fontSize.setMinimum(1);
		fontSize.setValue(12);
		fontSize.setToolTipText("Text size");
		GridBagConstraints gbc_fontSize = new GridBagConstraints();
		gbc_fontSize.gridwidth = 7;
		gbc_fontSize.insets = new Insets(0, 0, 0, 5);
		gbc_fontSize.gridx = 0;
		gbc_fontSize.gridy = 7;
		getContentPane().add(fontSize, gbc_fontSize);
		
		GridBagConstraints gbc_font = new GridBagConstraints();
		gbc_font.gridwidth = 4;
		gbc_font.insets = new Insets(0, 0, 0, 5);
		gbc_font.fill = GridBagConstraints.HORIZONTAL;
		gbc_font.gridx = 7;
		gbc_font.gridy = 7;
		getContentPane().add(font, gbc_font);
		
		GridBagConstraints gbc_style = new GridBagConstraints();
		gbc_style.gridwidth = 2;
		gbc_style.insets = new Insets(0, 0, 0, 5);
		gbc_style.fill = GridBagConstraints.HORIZONTAL;
		gbc_style.gridx = 11;
		gbc_style.gridy = 7;
		getContentPane().add(style, gbc_style);
		
	}
	
	public Font getFont() {
			String sFont = "";
			int size = 0;
			int sStyle = 0;
			
			sFont = (String) font.getSelectedItem();
			switch ((String) style.getSelectedItem()) {
				case "Plain": sStyle = Font.PLAIN; break;
				case "Bold": sStyle = Font.BOLD; break;
				case "Italic": sStyle = Font.ITALIC; break;
			}
			size = fontSize.getValue();
			
			return new Font(sFont, sStyle, size);
		}
	public String getText() { return paintString.getText(); }
	
	public void setXY(int x, int y) { this.x = x; this.y = y; }
	public int getX()               { return x; }
	public int getY()               { return y; }
	public int getNewLines()		{ return newLines; }

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			newLines ++;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
