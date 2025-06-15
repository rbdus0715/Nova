package daw.main.component.navigation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.color.ColorSpace;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import daw.utils.Utils;

public class Bar extends JPanel{

	public Bar() {
		setBackground(new Color(25,25,25));
		setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.DARK_GRAY));
		
		ImageIcon icon = new ImageIcon("src/images/logo.png"); 
        Image resizedImage = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);		
        JLabel iconLabel = new JLabel(resizedIcon);
		add(iconLabel);
		
		setPreferredSize(new Dimension(60, 0));
		setVisible(true);
	}
}
