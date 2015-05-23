import java.awt.*;
import java.awt.event.*;

import javax.imageio.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

public class SaveBox implements ActionListener{
    
    private String[] types = { "jpg", "png", "gif" };
    private String pathName = "";
    private String fileType = "";
    private String fileName = "";
    
    private Color bgGUI = new Color(198, 255, 125);
    private Color buttonColor = new Color(195, 252, 219);
    
    private BufferedImage imageToSave = null;
    private File file = null;
    
    protected JPanel north = new JPanel();
    protected JPanel south = new JPanel();
    
    protected JTextField imageName = new JTextField(15);
    protected JComboBox imageType = new JComboBox(types);
    protected JButton save = new JButton("Save");
    protected JButton cancel = new JButton("Cancel");
    
    public JFrame saveBox = new JFrame("Save Image");
    
    public SaveBox(BufferedImage image) {
        imageToSave = image;
        initializeComponents();
        organizeComponents();
        //addComponents();
    }
    
    private void initializeComponents() {
        imageName.addActionListener(this);
        imageType.addActionListener(this);
        save.addActionListener(this);
        cancel.addActionListener(this);
        
        saveBox.getContentPane().setBackground(bgGUI);
        save.setBackground(buttonColor);
        cancel.setBackground(buttonColor);
        imageType.setBackground(buttonColor);
        north.setBackground(bgGUI);
        south.setBackground(bgGUI);
        
        saveBox.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        saveBox.setSize(220, 100);
        saveBox.setLocationRelativeTo(null);
        
        imageName.setFont(new Font("Arial", Font.PLAIN, 10));
        imageName.setForeground(Color.BLACK);
        imageName.setBackground(Color.WHITE);
        imageName.setToolTipText("Enter file name. Do not add extention here!");
        imageName.requestFocus();
        
        north.setLayout(new BorderLayout());
        south.setLayout(new BorderLayout());
    }
    
    private void organizeComponents() {
        north.add(imageName, BorderLayout.WEST);
        north.add(imageType, BorderLayout.EAST);
        south.add(save, BorderLayout.WEST);
        south.add(cancel, BorderLayout.EAST);
        saveBox.add(north, BorderLayout.NORTH);
        saveBox.add(south, BorderLayout.SOUTH);
        
    }
    
    public void displayFrame(boolean dis) {
        saveBox.setVisible(dis);
    }
    
    public void setPathName(String path) {
        pathName = path;
    }
    public void setFileName(String name) {
        fileName = name;
    }
    public void setFileType(String e) {
        fileType = e;
    }
    public void setFile(File f) {
        file = f;
    }
    public void save() {
        if (file == null) {
            JOptionPane.showMessageDialog(null, "File name is blank");
            return;
        }
        if (imageToSave == null) {
            JOptionPane.showMessageDialog(null, "No image to save!");
            return;
        }
        if (fileType.equals("")) {
            JOptionPane.showMessageDialog(null, "File type is blank!");
            return;
        }
        else {
            try {
                file = new File(pathName + fileName + "." + fileType);
                ImageIO.write(imageToSave, fileType, file);
            } catch (IOException er) {}
            JOptionPane.showMessageDialog(null, "Image saved!");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        Object obj = e.getSource();
        
        if (obj == save) {
            fileName = imageName.getText();
            fileType = (String) imageType.getSelectedItem();
            if (fileName.equals("")) {
                JOptionPane.showMessageDialog(null, "File name is blank");
                return;
            }
            if (imageToSave == null) {
                JOptionPane.showMessageDialog(null, "No image to save!");
                return;
            }
            if (fileType.equals("")) {
                JOptionPane.showMessageDialog(null, "File type is blank!");
                return;
            }
            else {
                try {
                    file = new File(pathName + fileName + "." + fileType);
                    ImageIO.write(imageToSave, fileType, file);
                } catch (IOException er) {}
                JOptionPane.showMessageDialog(null, "Image saved!");
                saveBox.dispose();
            }
        }
        if (obj == cancel) { saveBox.dispose(); }
        if (obj == imageName) {
            fileName = imageName.getText();
        }
        if (obj == imageType) {
            fileType = (String) imageType.getSelectedItem();
        }
        
    }
}
