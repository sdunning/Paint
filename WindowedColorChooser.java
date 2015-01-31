/* WindowedColorChooser class
 * 
 * Author : Scott Dunning
 * Date : 07 / 08 / 2014
 * 
 * This class is a simple java component allowing for the use
 * of a individually windowed java JColorChooser
 *
 */

// Java Libraries //
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
// End Java Libraries //

// WindowedColorChooser class START //
public class WindowedColorChooser extends JComponent implements ChangeListener, ActionListener {
    
    // Components //
    public JFrame chooserFrame = new JFrame("Choose a Color");
    public JColorChooser colorChooser = new JColorChooser();
    public JButton ok = new JButton("OK");
    //public JButton close = new JButton("CLOSE");
    
    // Color variables //
    private Color color = Color.BLACK;
    private int red = 0, green = 0, blue = 0, alpha = 0;
    
    // Constructors //
    public WindowedColorChooser() { init(); } // Create Default object
    public WindowedColorChooser(String title) { chooserFrame.setTitle(title); init(); } // Create object with specific title
    public WindowedColorChooser(boolean showPreview) {  // Create object with or without preview panel
        colorChooser.setPreviewPanel(showPreview? null : new JPanel()); init ();
    }
    public WindowedColorChooser(String title, boolean showPreview) { // Create object with specific title and w/ || !w/ preview panel
        chooserFrame.setTitle(title);
        colorChooser.setPreviewPanel(showPreview? null : new JPanel());
        init();
    }
    
    public void init() { // initializing function
        chooserFrame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                setVisible(false);
            }
        } );
        chooserFrame.setSize(new Dimension(600, 400));
        chooserFrame.setLayout(new BorderLayout());
        chooserFrame.add(colorChooser, BorderLayout.CENTER);
        colorChooser.getSelectionModel().addChangeListener(this);
        ok.addActionListener(this);
        chooserFrame.add(ok, BorderLayout.SOUTH);
    }

    @Override
    public void stateChanged(ChangeEvent arg0) { // Event Function && set colors
        color = colorChooser.getColor();
        setRGBA();
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        
        if (obj == ok) setVisible(isVisible()? false : true);
    }
    
    private void setRGBA() {
        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        alpha = color.getAlpha();
    }
    
    public void setVisible(boolean visible)     { chooserFrame.setVisible(visible); } // set window visibility
    public boolean isVisible()                  { return chooserFrame.isVisible(); } // check if visible
    
    public Color getColor()                     { return color; } // retrieve color
    public void setColor(Color clr)             { colorChooser.setColor(clr); } // set the Chooser's color
    
    public int getRed()                         { return red; } // retrieve red value
    public void setRed(int r)                   { red = r; } // set red value 
    
    public int getGreen()                       { return green; } // retrieve green value
    public void setGreen(int g)                 { green = g; } // set green value
    
    public int getBlue()                        { return blue; } // retrieve blue value
    public void setBlue(int b)                  { blue = b; } // set blue value
    
    public int getAlpha()                       { return alpha; } // retrieve alpha value
    public void setAlpha(int a)                 { alpha = a; } // set alpha value
    
    @Override
    public String toString() { return String.format("red : %5s green : %5s blue : %5s alpha : %5s", red, green, blue, alpha); }
}
