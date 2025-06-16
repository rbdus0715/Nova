package daw.main.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import daw.main.LayeredPaneTrackMain;
import daw.main.component.track.TrackLane;
import daw.utils.Utils;

public class EditArea extends JPanel {
	private int HEIGHT;
	private int BEATS = 8 * 4 * 4;
	private LayeredPaneTrackMain layeredPane;
	private int headerHeight = 30;
	private TrackLane trackLane;
	
	public EditArea(LayeredPaneTrackMain layeredPane, TrackLane trackLane) {
		this.layeredPane = layeredPane;
		this.trackLane = trackLane;
		int HEIGHT = layeredPane.getHeight();

		
		setBackground(Utils.ColorMap.COLOR_CONTROLBAR);
		setBounds(0, layeredPane.getHeight() - HEIGHT, layeredPane.getWidth(), HEIGHT);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.WHITE));
		
		initHeader();
	}
	
	public void close() {
		layeredPane.remove(this);
	}
	
	public void initHeader() {
		JPanel header = new JPanel();
		header.setPreferredSize(new Dimension(0, headerHeight));
		header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
		header.setBackground(Utils.ColorMap.COLOR_BACKGROUND);
		JButton closeButton = new JButton();
		closeButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				layeredPane.closeEditor(EditArea.this);
				System.out.println("EditArea : editArea is closed");
		    }
		});
		JLabel closeLabel = new JLabel("X");
		closeButton.add(closeLabel);
		header.add(closeButton);
		add(header, BorderLayout.NORTH);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int height = getHeight() - headerHeight;
		int blockHeight = height / 60;
		int blockWidth = 16;

		g.setColor(Color.DARK_GRAY);
		for(int i=headerHeight; i<getHeight(); i+=blockHeight) {
			g.drawLine(0, i + blockHeight, getWidth(), i + blockHeight);
		}
		for(int i=0; i<16 * 2 * 4 * 8; i+=blockWidth) {
			g.drawLine(i + blockWidth, 0, i + blockWidth, getHeight());
		}
	}
}
