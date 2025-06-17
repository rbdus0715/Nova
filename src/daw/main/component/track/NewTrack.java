package daw.main.component.track;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import daw.main.TrackBar;
import daw.main.TrackBar.TRACK_TYPE;
import daw.utils.Utils;

public class NewTrack extends JPanel {
	private TrackBar trackBar;
	private TrackBody trackBody;
	
	public NewTrack(TrackBar trackBar, TrackBody trackBody) {
		this.trackBar = trackBar;
		this.trackBody = trackBody;
		
		JButton newInstTrackButton = new JButton("new Inst track");
		newInstTrackButton.addActionListener(new newTrackButtonEventListener(TRACK_TYPE.INST));
		add(newInstTrackButton);
//		JButton newAudioTrackButton = new JButton("AUDIO");
//		newAudioTrackButton.addActionListener(new newTrackButtonEventListener(TRACK_TYPE.AUDIO));
//		add(newAudioTrackButton);
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//		setBorder(Utils.WindowDesign.LINE_BORDER);
		setPreferredSize(new Dimension(200, 50));
		setBackground(Utils.ColorMap.COLOR_PANEL);
		setVisible(true);
	}
	
	class newTrackButtonEventListener implements ActionListener {
		private TRACK_TYPE trackType;
		public newTrackButtonEventListener(TRACK_TYPE trackType) {
			this.trackType = trackType;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(trackBody.numInst() >= 7) {
				JDialog maxAlert = new MaxDialog(NewTrack.this);
				maxAlert.setVisible(true);
				return;
			}
			trackBar.newTrack(trackType);
			trackBody.newTrack(trackType);
			revalidate();  
			repaint();    
		}
	}
	
	class MaxDialog extends JDialog {
		MaxDialog(JPanel p) {
			super(SwingUtilities.getWindowAncestor(p), "Full Instruments!", Dialog.ModalityType.APPLICATION_MODAL);
			add(new JLabel("Full Instruments!"));
			JButton exitButton;
			exitButton = new JButton("Ok");
			exitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			add(exitButton);
			setSize(200, 80);
			setLocation(170, 200);
		}
	}
}
