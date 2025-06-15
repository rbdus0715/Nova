package daw.main.component.track;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import daw.main.Daw;
import daw.main.TrackBar.TRACK_TYPE;
import daw.main.component.PlayHead;
import daw.synth.DrumKit;
import daw.synth.Inst;
import daw.utils.Utils;

class Note extends JPanel {
	private char key;
	private int startTime;
	private int endTime;
	
	Note(char key, int startTime, int endTime) {
		this.key = key;
		this.startTime = startTime;
		this.endTime = endTime;
		setBounds(startTime, 30, endTime - startTime, 5);
	}
	char getKey() {
		return key;
	}
	int getStartTime() {
		return startTime;
	}
	int getEndTime() {
		return endTime;
	}
}

public class TrackLane extends JPanel {
	
	private Inst inst;
	private Map<Character, Integer> keyStartTime;
	private Vector<Note> playData;
	private PlayHead playhead;
	private Map<Character, Integer> CharToKeyMap;
	private Daw daw;
	
	TrackLane(TRACK_TYPE trackType, PlayHead playhead, Daw daw) {
		this.daw = daw;
		
		inst = new DrumKit();
		
		CharToKeyMap = new HashMap<>();
		keyStartTime = new HashMap<>();
		playData = new Vector<Note>();
		
		String keys = "asdfghjkl;";
		for(int i=0; i<keys.length(); i++) {
			CharToKeyMap.put(keys.charAt(i), i);
		}
		
		this.playhead = playhead;
		
		
		// focus
		setFocusable(true); 
		requestFocus();
		addKeyListener(new playKeyListener());
		addMouseListener(new DoubleClickEvent());
		setLayout(null);
		setPreferredSize(new Dimension(1024, 80));
		this.setMaximumSize(this.getPreferredSize());
		setAlignmentX(this.LEFT_ALIGNMENT);
		
//		Random random = new Random();
//	    int r = random.nextInt(256);
//	    int g = random.nextInt(256);
//	    int b = random.nextInt(256);
//		setBackground(new Color(r, g, b));
		Color color;
		if(trackType == TRACK_TYPE.INST) color = new Color(67, 170, 139);
		else color = Utils.ColorMap.COLOR_BACKGROUND;
		setBackground(color);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
	}
	
	class playKeyListener extends KeyAdapter {
		private Set<Character> pressedKeys = new HashSet<>();
		
		public void keyPressed(KeyEvent e) {
			char keyChar = e.getKeyChar();
			inst.noteOn(keyChar);

			if(pressedKeys.contains(keyChar))
				return;
			pressedKeys.add(keyChar);
			int start_time = playhead.getPosition();
//			System.out.print(keyChar);
		    System.out.println("Key pressed '" + keyChar + "' at position: " + start_time);
			keyStartTime.put(keyChar, start_time);
		}
		public void keyReleased(KeyEvent e) {
			char keyChar = e.getKeyChar();
			pressedKeys.remove(keyChar);

			int end_time = playhead.getPosition();
			int start_time = keyStartTime.get(keyChar);
			keyStartTime.remove(keyChar);
			Note note;
			playData.add(note = new Note(keyChar, start_time, end_time));
		    System.out.println("Key released '" + keyChar + "' at position: " + end_time);
		    addNote(note);
			revalidate();
			repaint();
		}
	}
	
	void addNote(Note note) {
		add(note);
	}
	
//	@Override
//	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		try {
//			g.setColor(Color.BLUE);
//			for(int i=0; i<playData.size(); i++) {
//				Note note = playData.get(i);
//				int key = CharToKeyMap.get(note.getKey());
//				int NOTE_HEIGHT = getHeight() / 10;
//				g.fillRect(
//						note.getStartTime(), 
//						NOTE_HEIGHT * key, 
//						note.getEndTime() - note.getStartTime(), 
//						NOTE_HEIGHT);
//			} 
//		} catch(Exception e){
//			System.out.print(e);
//		};
//	}
	
	public void playCurrentNote(int position) {
		for(int i=0; i<playData.size(); i++) {
			if(position == playData.get(i).getStartTime()) {
				inst.noteOn(playData.get(i).getKey());
			}
		}
	}
	
	class DoubleClickEvent extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				daw.viewEditor();
			}
		}
	}

}
