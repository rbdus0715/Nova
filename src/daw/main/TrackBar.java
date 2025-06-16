package daw.main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicOptionPaneUI;

import daw.main.component.track.TrackBody;
import daw.main.component.track.TrackController;
import daw.synth.Inst;
import daw.utils.Utils;

public class TrackBar extends JPanel {
	ArrayList<TrackController> trackControllers = new ArrayList<TrackController>();
	private TrackBody trackBody;
	
	public static enum TRACK_TYPE {
		INST,
		AUDIO,
	}
	
	TrackBar(TrackBody trackBody) {
		this.trackBody = trackBody;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		setPreferredSize(new Dimension(200, Integer.MAX_VALUE));
		setBackground(Utils.ColorMap.COLOR_PANEL);
//		setMaximumSize(new Dimension(Utils.WindowSize.TRACKBAR_WIDTH, Short.MAX_VALUE));
//		setBorder(Utils.WindowDesign.BORDER_RIGHT);
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
			TrackController tc = new TrackController(TRACK_TYPE.AUDIO, this, trackControllers.size());
			trackControllers.add(tc);
			add(tc);
		}

		if (trackType == TRACK_TYPE.INST) {
			TrackController tc = new TrackController(TRACK_TYPE.INST, this, trackControllers.size());
			trackControllers.add(tc);
			add(tc);
		}
	}
	
	public void deleteTrack(TrackController track) {
		int idx = trackControllers.indexOf(track);
		trackBody.deleteTrack(idx);
		this.remove(track);
		trackControllers.remove(track);
		revalidate();
		repaint();
	}

	public void focus(TrackController tc) {
		int idx = trackControllers.indexOf(tc);
		trackBody.focus(idx);
	}
	public void focus(int tc_idx) {
		trackBody.focus(tc_idx);
	}
	
	public void changeInst(int tc_idx, int index) {
		trackBody.changeInst(tc_idx, index);
	}
	public Inst getInst(int tc_idx) {
		return trackBody.getInst(tc_idx);
	}
	
}
