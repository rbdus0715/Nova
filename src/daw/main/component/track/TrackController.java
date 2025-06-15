package daw.main.component.track;

import java.awt.BorderLayout;

import daw.main.TrackBar;
import daw.main.TrackBar.TRACK_TYPE;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.filechooser.FileNameExtensionFilter;

import daw.utils.Utils;

public class TrackController extends JPanel{
	private int tc_idx;
	
	public TrackController(TRACK_TYPE trackType, TrackBar trackBar, int tc_idx) {
		setLayout(new BorderLayout());
		
		JPanel tcHeader = new JPanel();
		ImageIcon trackIcon = Utils.Images.AUDIO_TRACK_ICON;
		Image resized = trackIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
		JButton img = new JButton(new ImageIcon(resized));
        img.setBorderPainted(true);   
        img.setContentAreaFilled(false);
        img.setFocusPainted(false);   
        img.setOpaque(false);  
        img.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				TrackController tc = (TrackController) e.getSource();
				trackBar.focus(tc_idx);
			}
		});
        
        JLabel trackLabel = null;
        if(trackType == TRACK_TYPE.AUDIO)
        	trackLabel = new JLabel("audio track");
        if(trackType == TRACK_TYPE.INST)
        	trackLabel = new JLabel("inst track");
		JButton deleteButton = new JButton("del");
		deleteButton.addActionListener(e-> {
			trackBar.deleteTrack(this);
		});
		tcHeader.add(img);
		tcHeader.add(trackLabel);
		tcHeader.add(deleteButton);
		
		JPanel tcBottom = new JPanel();
		JSlider volumeFader = new JSlider();
		tcBottom.add(volumeFader);
		
		setBorder(Utils.WindowDesign.LINE_BORDER);
		setPreferredSize(new Dimension(200, 100));
		add(tcHeader, BorderLayout.NORTH);
		add(tcBottom, BorderLayout.SOUTH);
	}

	
	@Override
	public Dimension getMaximumSize() {
	    return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
	}
	
	@Override
	public Dimension getPreferredSize() {
	    return new Dimension(Utils.WindowSize.TRACKBAR_WIDTH, 80); // 80px 높이 등 적절히 조절
	}
}
