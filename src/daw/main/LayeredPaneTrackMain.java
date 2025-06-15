package daw.main;

import java.awt.Dimension;

import javax.swing.JLayeredPane;

import daw.main.component.EditArea;
import daw.main.component.PlayHead;
import daw.main.component.track.TrackBody;

public class LayeredPaneTrackMain extends JLayeredPane {
	LayeredPaneTrackMain(PlayHead playhead, TrackBody trackBody) {
		setPreferredSize(new Dimension(2000, 2000));
		add(playhead, Integer.valueOf(5));
		add(trackBody, Integer.valueOf(3));
	}
	
	public void closeEditor(EditArea editArea) {
		remove(editArea);
		revalidate();
		repaint();
	}
}
