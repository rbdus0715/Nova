package daw.main.component;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import daw.main.component.metronome.Metronome;
import daw.utils.Utils;

public class TrackRuler extends JPanel {
	private Metronome metronome;

	
	public TrackRuler(Metronome metronome) {
		this.metronome = metronome;
		setPreferredSize(new Dimension(1100, 50));
		setBackground(Utils.ColorMap.COLOR_PANEL);
		setVisible(true);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(Color.LIGHT_GRAY);
		
		Font smallFont = new Font("Arial", Font.PLAIN, 10); 
	    g.setFont(smallFont);
	    g2d.setStroke(new BasicStroke(0.5f));
		int term = 128;
		for(int i=0; i<9; i++) {
			g.drawLine(i * term, 30, i * term, 45);
			if(i==0) continue;
			g.drawString("" + i, i * term - 3, 23);
		}
		for(int i=0; i<8 * 4; i++) {
			g.drawLine(i * term/4, 38, i*term/4, 45);
		}
		
	}
	
}
