package daw.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import daw.main.component.PlayHead;
import daw.utils.Utils;

public class ControlBar extends JPanel {
	PlayHead playhead;
	
	public ControlBar(PlayHead playhead) {
		this.playhead = playhead;
		
		// spacer : top empty space
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(0, Utils.WindowSize.CONTROLBAR_HEIGHT / 2));
		spacer.setOpaque(false); 
		
		// leftPanel : undo | do | metronome on/off | BPM | tempo type
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		leftPanel.setOpaque(false); 
		leftPanel.add(new JButton("metronome on/off"));
		leftPanel.add(new JButton("100")); // BPM
		
		// centerPanel : start/pause | go to start point | record 
		JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		centerPanel.setOpaque(false);
		JButton startButton = new JButton("▶");
		JButton initButton = new JButton("■");
		JButton recordButton = new JButton("●");
		startButton.addActionListener(new StartPlayEventListener(false));
		initButton.addActionListener(new InitPlayEventListener());
		recordButton.addActionListener(new StartPlayEventListener(true));
		centerPanel.add(startButton);
		centerPanel.add(initButton);
		centerPanel.add(recordButton);
		
		// rightPanel : master volumn
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		rightPanel.setBackground(Color.WHITE);
		rightPanel.setOpaque(false);
		rightPanel.add(new JButton("master volume"));

		setLayout(new BorderLayout());
		add(spacer, BorderLayout.NORTH);       
		add(leftPanel, BorderLayout.WEST);
		add(centerPanel, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.EAST);
		setBackground(Utils.ColorMap.COLOR_CONTROLBAR);		
		setPreferredSize(new Dimension(Utils.WindowSize.WINDOW_WIDTH, Utils.WindowSize.CONTROLBAR_HEIGHT));
//		setBorder(Utils.WindowDesign.BORDER_BOTTOM);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));

	}
	
	class StartPlayEventListener implements ActionListener {
		private boolean record;
		StartPlayEventListener(boolean record) {
			this.record = record;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(playhead.getIsPlaying() == false) {
				if(record == true) playhead.setIsRecording(true);
				else playhead.setIsRecording(false);
				playhead.startPlay();
			}
			else
				playhead.stopPlay();
		}
	}
	
	class InitPlayEventListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			playhead.initPlay();
		}
		
	}
}





