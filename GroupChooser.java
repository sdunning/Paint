import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class GroupChooser extends JFrame implements ActionListener {
	
	JButton groups[] = { new JButton(), new JButton(), new JButton(), new JButton() };
	JPanel buttonPanel = new JPanel();
	Container c = null;
	int groupNum = -1;
	CommunityCanvas paint = null;
	
	public GroupChooser(CommunityCanvas paint) {
		c = this.getContentPane();
		this.setVisible(false);
		this.paint = paint;
		this.setSize(200, 200);
		this.setLocationRelativeTo(null);
		buttonPanel.setLayout( new GridLayout(0, 2) );
		for (int i=0; i<groups.length; i++) {
			groups[i].setText("Group " + i);
			groups[i].addActionListener(this);
			buttonPanel.add(groups[i]);
		}
		c.add(buttonPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if (obj == groups[0]) groupNum = 0;
		else if (obj == groups[1]) groupNum = 1;
		else if (obj == groups[2]) groupNum = 2;
		else if (obj == groups[3]) groupNum = 3;
		else return;
		paint.connect(groupNum);
		this.dispose();
	}
}
