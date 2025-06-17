package daw.main.component;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;


import daw.main.LayeredPaneTrackMain;
import daw.main.component.track.TrackLane;
import daw.synth.KeyboardPlayer;
import daw.utils.Utils;
import daw.main.component.track.Note;

public class EditArea extends JPanel {
	private int HEIGHT;
	private int BEATS = 8 * 4 * 4;
	private LayeredPaneTrackMain layeredPane;
	private int headerHeight = 30;
	private TrackLane trackLane;
	private JPanel header;
	private Vector<JButton> noteButtons;
	private JButton clickedButton;
	private JPanel body;
	
	public EditArea(LayeredPaneTrackMain layeredPane, TrackLane trackLane) {
		this.layeredPane = layeredPane;
		this.trackLane = trackLane;
		int HEIGHT = layeredPane.getHeight();
		noteButtons = new Vector<JButton>();
		
		setBackground(Utils.ColorMap.COLOR_CONTROLBAR);
		setBounds(0, layeredPane.getHeight() - HEIGHT, layeredPane.getWidth(), HEIGHT);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.WHITE));
		
		initHeader();
		initBody();
	}
	
	public void close() {
		layeredPane.remove(this);
	}
	
	public void initHeader() {
		header = new JPanel();
		header.setSize(new Dimension(0, headerHeight));
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
	
	public void initBody() {
		body = new JPanel();
		body.setLayout(null);
		body.setPreferredSize(new Dimension(getWidth(), getHeight() - header.getHeight()));
		body.setOpaque(false);
		
		
		int numNote = trackLane.numNote();
		for(int i=0; i<numNote; i++) {
			Note note = trackLane.getNote(i);
			JButton noteButton = new JButton();
			int start_time = note.getStartTime();
			int end_time = note.getEndTime();
			int keyCode = note.getKey();
			int height = getHeight() - headerHeight;
			int blockHeight = height / 60;
//			int noteKey = ((KeyboardPlayer)trackLane.getInst()).getNote(keyCode);
			keyCode -= 24;
			noteButton.setBounds(
					start_time,
					keyCode * blockHeight,
					(end_time - start_time), 
					blockHeight);
			noteButtons.add(noteButton);
			body.add(noteButton);
			noteButton.addActionListener(e -> {
				noteButton.requestFocus();
				clickedButton = noteButton;
			});
			noteButton.addKeyListener(new EraseButtonListener());
			MouseDragListener dragListener = new MouseDragListener();
			noteButton.addMouseListener(dragListener);
			noteButton.addMouseMotionListener(dragListener);
//			System.out.println(
//					start_time + " " +
//					noteKey * blockHeight + " " +
//					(end_time - start_time) + " " +
//					blockHeight
//			);
		}
		
		add(body, BorderLayout.CENTER);
		body.revalidate();
		body.repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int height = getHeight() - headerHeight;
		int blockHeight = height / 60;
		int blockWidth = 16;

		drawGrid(g, blockHeight, blockWidth);
	}
	
	public void drawGrid(Graphics g, int blockHeight, int blockWidth) {
		g.setColor(Color.DARK_GRAY);
		for(int i=headerHeight; i<getHeight(); i+=blockHeight) {
			g.drawLine(0, i + blockHeight, getWidth(), i + blockHeight);
		}
		g.setColor(Color.DARK_GRAY);
		for(int i=0; i<16 * 2 * 4 * 8; i+=blockWidth) {
			g.drawLine(i + blockWidth, 0, i + blockWidth, getHeight());
		}
	}
	
	class EraseButtonListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if(keyCode == KeyEvent.VK_BACK_SPACE) {
				JButton source = (JButton)e.getSource();
				noteButtons.remove(source);
				body.remove(clickedButton);
				revalidate();
				repaint();
			}
		}
	}
	class MouseDragListener extends MouseAdapter {
		JButton button;
		int clickedX, clickedY;
		@Override
		public void mousePressed(MouseEvent m) {
			button = (JButton) m.getSource();
			clickedX = m.getX();
			clickedY = m.getY();
			button.requestFocus();
		}
		@Override
		public void mouseDragged(MouseEvent m) {
			if(button == null) return;
			int newX = button.getX() + m.getX() - clickedX;
			int newY = button.getY() + m.getY() - clickedY;
			button.setLocation(newX, newY);
			revalidate();
			button.getParent().repaint();
		}
		@Override
		public void mouseReleased(MouseEvent m) {
			if(button == null) return;
			int newX = button.getX();
			int newY = button.getY();
			int snappedY = Math.round((float)newY / 9) * 9;
			button.setLocation(newX, snappedY);
			
			int idx = noteButtons.indexOf(button);
			Note note = trackLane.getNote(idx);
			int duration = note.getEndTime() - note.getStartTime();
			note.setStartTime(newX);
			note.setEndTime(newX + duration);
			note.setKey((snappedY / 9) + 24);
		}
	}
	
	public void refresh() {
	    body.removeAll();
	    noteButtons.clear();
	    int numNote = trackLane.numNote();
	    for(int i=0; i<numNote; i++) {
	        Note note = trackLane.getNote(i);
	        JButton noteButton = new JButton();
	        int start_time = note.getStartTime();
	        int end_time = note.getEndTime();
	        int keyCode = note.getKey();
	        int height = getHeight() - headerHeight;
	        int blockHeight = height / 60;
	        keyCode -= 24;
	        noteButton.setBounds(
	            start_time,
	            keyCode * blockHeight,
	            (end_time - start_time), 
	            blockHeight);
	        noteButtons.add(noteButton);
	        body.add(noteButton);
	        noteButton.addActionListener(e -> {
	            noteButton.requestFocus();
	            clickedButton = noteButton;
	        });
	        noteButton.addKeyListener(new EraseButtonListener());
	        MouseDragListener dragListener = new MouseDragListener();
	        noteButton.addMouseListener(dragListener);
	        noteButton.addMouseMotionListener(dragListener);
	    }
	    body.revalidate();
	    body.repaint();
	}
	
}
