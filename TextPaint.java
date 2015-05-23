import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.JTextArea;

import java.awt.GridBagConstraints;

import javax.swing.JButton;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JSlider;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JScrollPane;


public class TextPaint extends JFrame implements KeyListener, ActionListener, ChangeListener{
	JButton paint = new JButton("Paint");
	JSlider fontSize = new JSlider();
	private int x = 0, y = 0, newLines = 0;
	String fonts [] = {"arial", "georgia", "impact", "lucida console", "tahoma", "times new roman", "verdana", "webdings"};
	String styles [] = {"Plain", "Bold", "Italic"};
	JComboBox font = new JComboBox(fonts);
	JComboBox style = new JComboBox(styles);
	JButton cancel = new JButton("Cancel");
	JButton reset = new JButton("Reset");
	JTextArea paintString = new JTextArea(10, 40);
	JScrollPane scrollPane = new JScrollPane(paintString);
	
	private Color bgGUI = new Color(198, 255, 125);
    private Color buttonColor = new Color(195, 252, 219);
	
	
	
	
	
	public TextPaint() {
		paintString.setFont(new Font("Arial", Font.PLAIN, 12));
		paintString.setToolTipText("Text to be painted");
		this.setVisible(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.getContentPane().setBackground(bgGUI);
		
		font.setBackground(buttonColor);
		style.setBackground(buttonColor);
		fontSize.setBackground(buttonColor);
		paint.setBackground(buttonColor);
		cancel.setBackground(buttonColor);
		reset.setBackground(buttonColor);
		
		font.addActionListener(this);
		style.addActionListener(this);
		fontSize.addChangeListener(this);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 46, 0, 0, 28, 0, 0, 31};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 7;
		gbc_scrollPane.gridwidth = 13;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		getContentPane().add(scrollPane, gbc_scrollPane);
		
		GridBagConstraints gbc_paint = new GridBagConstraints();
		gbc_paint.fill = GridBagConstraints.HORIZONTAL;
		gbc_paint.insets = new Insets(0, 0, 5, 0);
		gbc_paint.gridx = 13;
		gbc_paint.gridy = 0;
		paint.setToolTipText("Paint text to canvas.");
		getContentPane().add(paint, gbc_paint);
		cancel.addActionListener(this);
		
		GridBagConstraints gbc_cancel = new GridBagConstraints();
		gbc_cancel.fill = GridBagConstraints.HORIZONTAL;
		gbc_cancel.insets = new Insets(0, 0, 5, 0);
		gbc_cancel.gridx = 13;
		gbc_cancel.gridy = 1;
		cancel.setToolTipText("Cancel text painting.");
		getContentPane().add(cancel, gbc_cancel);
		reset.addActionListener(this);
		
		GridBagConstraints gbc_reset = new GridBagConstraints();
		gbc_reset.fill = GridBagConstraints.HORIZONTAL;
		gbc_reset.insets = new Insets(0, 0, 5, 0);
		gbc_reset.gridx = 13;
		gbc_reset.gridy = 3;
		reset.setToolTipText("Set to default values.");
		getContentPane().add(reset, gbc_reset);
		fontSize.setFont(new Font("Tahoma", Font.PLAIN, 8));
		
		fontSize.setMajorTickSpacing(8);
		fontSize.setPaintLabels(true);
		fontSize.setPaintTicks(true);		
		fontSize.setMinimum(4);
		fontSize.setValue(12);
		fontSize.setToolTipText("Text size");
		GridBagConstraints gbc_fontSize = new GridBagConstraints();
		gbc_fontSize.fill = GridBagConstraints.BOTH;
		gbc_fontSize.gridwidth = 11;
		gbc_fontSize.insets = new Insets(0, 0, 0, 5);
		gbc_fontSize.gridx = 0;
		gbc_fontSize.gridy = 7;
		getContentPane().add(fontSize, gbc_fontSize);
		
		GridBagConstraints gbc_font = new GridBagConstraints();
		gbc_font.anchor = GridBagConstraints.EAST;
		gbc_font.gridwidth = 2;
		gbc_font.insets = new Insets(0, 0, 0, 5);
		gbc_font.gridx = 11;
		gbc_font.gridy = 7;
		getContentPane().add(font, gbc_font);
		
		GridBagConstraints gbc_style = new GridBagConstraints();
		gbc_style.fill = GridBagConstraints.HORIZONTAL;
		gbc_style.gridx = 13;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == font || obj == style) {
			paintString.setFont(this.getFont());
		}
		if (obj == cancel) {
			this.setVisible(false);
			this.setAlwaysOnTop(false);
		}
		if (obj == reset) {
			paintString.setText("");
			font.setSelectedIndex(0);
			style.setSelectedIndex(0);
			fontSize.setValue(12);
			newLines =0;
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == fontSize) {
			paintString.setFont(this.getFont());
		}
		
	}
}
