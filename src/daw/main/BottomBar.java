package daw.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import daw.utils.Utils;

public class BottomBar extends JPanel {
	BottomBar() {
		setBackground(new Color(25, 25, 25));
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(0, 30));
		setBorder(BorderFactory.createMatteBorder(1,0,0,0, Color.DARK_GRAY));
		
//		JPanel leftPanel = new JPanel();
//		leftPanel.
//		add(leftPanel, BorderLayout.WEST);
//		
//		
//		JPanel rightPanel = new JPanel();
//		add(rightPanel, BorderLayout.EAST);
	}
}
