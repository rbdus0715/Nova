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
import daw.synth.BasicSynthesizer;
import daw.synth.DrumKit;
import daw.synth.Inst;
import daw.synth.KeyboardPlayer;
import daw.utils.Utils;


public class TrackLane extends JPanel {
	private KeyboardPlayer inst;
	private Map<Integer, Integer> keyStartTime;
	private Map<Integer, Integer> activeNotes;
	private Vector<Note> playData;
	private PlayHead playhead;
//	private Map<Character, Integer> CharToKeyMap;
	private Daw daw;
	
	TrackLane(TRACK_TYPE trackType, PlayHead playhead, Daw daw) {
		this.daw = daw;
		
		inst = new DrumKit();
		
//		CharToKeyMap = new HashMap<>();
		keyStartTime = new HashMap<>();
		activeNotes = new HashMap<>();
		playData = new Vector<Note>();
		
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
		
		Color color;
		if(trackType == TRACK_TYPE.INST) color = new Color(67, 170, 139);
		else color = Utils.ColorMap.COLOR_BACKGROUND;
		setBackground(color);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
	}
	
	class playKeyListener extends KeyAdapter {
		private Set<Integer> pressedKeys = new HashSet<>();
		
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if(pressedKeys.contains(keyCode))
				return;		

			pressedKeys.add(keyCode);

			if(keyCode == KeyEvent.VK_Z) {
				inst.prevOctave();
				return;
			}
			else if(keyCode == KeyEvent.VK_X) {
				inst.nextOctave();
				return;
			}
			
			int note = inst.getNote(keyCode);
			if(note == -1) return;

			inst.noteOn(note);

			int start_time = playhead.getPosition();
		    
			System.out.println("Key pressed '" + keyCode + "' at position: " + start_time);
			activeNotes.put(keyCode, note);
			keyStartTime.put(keyCode, start_time);
		}
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if(!pressedKeys.contains(keyCode)) return;
			
			pressedKeys.remove(keyCode);

			if(!activeNotes.containsKey(keyCode)) return;

			int end_time = playhead.getPosition();
			int start_time = keyStartTime.get(keyCode);
			int noteValue = activeNotes.get(keyCode);

			keyStartTime.remove(keyCode);
			activeNotes.remove(keyCode);

			Note note;
//			playData.add(note = new Note(inst.getNote(keyCode), start_time, end_time));
			if(playhead.isRecording() == true)
				playData.add(note = new Note(noteValue, start_time, end_time));
			inst.noteOff(noteValue);
			System.out.println("Key released '" + noteValue + "' at position: " + end_time);
			if(daw.isEditor())
				daw.getEditArea().refresh();
		    revalidate();
		    repaint();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			g.setColor(Color.BLUE);
			for(int i=0; i<playData.size(); i++) {
				Note code = playData.get(i);
				int note = code.getKey();
				note -= 24;
//				int key = inst.getNote(note);
//				if(key == -1) continue;
				int NOTE_HEIGHT = getHeight() / 60;
				g.fillRect(
						code.getStartTime(), 
						NOTE_HEIGHT * note, 
						code.getEndTime() - code.getStartTime(), 
						NOTE_HEIGHT);
			} 
		} catch(Exception e){
			System.out.print("##");
		};
	}
	
	public void playCurrentNote(int position) {
	    for(int i=0; i<playData.size(); i++) {
	        if(position == playData.get(i).getStartTime()) {
	            inst.noteOn(playData.get(i).getKey());
	        }
	    }
	}
	public void stopCurrentNote(int position) {
	    for(int i=0; i<playData.size(); i++) {
	        if(position == playData.get(i).getEndTime()) {
	            inst.noteOff(playData.get(i).getKey());
	        }
	    }
	}
	
	class DoubleClickEvent extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				daw.viewEditor(TrackLane.this);
			}
		}
	}

	public void changeInst(int idx) {
		switch(idx) {
		case 0:
			inst = new DrumKit();
			break;
		case 1:
			inst = new BasicSynthesizer();
			break;
		}
	}
	public Inst getInst() {
		return inst;
	}
	
	public int numNote() {
		return playData.size();
	}
	
	public Note getNote(int idx) {
		return playData.get(idx);
	}

	public void removeNote(int idx) {
	    if (idx >= 0 && idx < playData.size()) {
	        playData.remove(idx);
	    }
	}
}
