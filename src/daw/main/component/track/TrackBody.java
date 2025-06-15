package daw.main.component.track;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import daw.main.ControlBar;
import daw.main.Daw;
import daw.main.TrackBar;
import daw.main.TrackBar.TRACK_TYPE;
import daw.main.component.PlayHead;
import daw.main.component.TrackRuler;
import daw.main.component.metronome.Metronome;
import daw.utils.Utils;

public class TrackBody extends JPanel {
	private Vector<TrackLane> trackLanes = new Vector<TrackLane>();
	private PlayHead playhead;
	private Daw daw;
	
	public TrackBody(PlayHead playhead, Daw daw) {
		this.daw = daw;
		this.playhead = playhead;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Utils.ColorMap.COLOR_BACKGROUND);
		playhead.setTrackBody(this);
	}	
	
	public void playCurrentNote(int position) {
		for(int i=0; i<trackLanes.size(); i++) {
			trackLanes.get(i).playCurrentNote(position);
		}
	}
	
	public void deleteTrack(int idx) {
		remove(trackLanes.get(idx));
		trackLanes.remove(idx);
		revalidate();  
		repaint(); 
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.DARK_GRAY);	
		for(int i=0; i<2000; i+=80) {
			g.drawLine(0, i, 1500, i);
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
	    int totalHeight = 0;
	    for (int i = 0; i < getComponentCount(); i++) {
	        totalHeight += getComponent(i).getPreferredSize().height;
	    }
	    return new Dimension(200, totalHeight);
	}
	
	public void newTrack(TRACK_TYPE trackType) {
		if (trackType == TRACK_TYPE.AUDIO) {
			TrackLane tl = new TrackLane(trackType, playhead, daw);
			trackLanes.add(tl);
			add(tl);
		}
		if (trackType == TRACK_TYPE.INST) {
			TrackLane tl = new TrackLane(trackType, playhead, daw);
			trackLanes.add(tl);
			add(tl);
		}
	}
	
	public void focus(int idx) {
		trackLanes.get(idx).setFocusable(true);
		trackLanes.get(idx).requestFocus(); 
	}

}
