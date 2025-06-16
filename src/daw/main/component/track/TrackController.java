package daw.main.component.track;

import java.awt.BorderLayout;
import java.awt.Color;

import daw.main.TrackBar;
import daw.main.TrackBar.TRACK_TYPE;
import daw.synth.BasicSynthesizer;
import daw.synth.Inst;
import daw.synth.SynthDialog;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jsyn.Synthesizer;

import daw.utils.Utils;

public class TrackController extends JPanel{
	private int tc_idx;
	private JPanel tcHeader;
	private JPanel tcHeaderEastPanel;
	private ImageIcon trackIcon;
	private Image resizedWaveImage;
	private JButton waveButton;
	private JButton recordButton;
	private String [] instruments = {"Drum Kit", "Synthesizer"};
	private JDialog instDialog;
	private TrackBar trackBar;
	
	public TrackController(TRACK_TYPE trackType, TrackBar trackBar, int tc_idx) {
		this.trackBar = trackBar;
		setLayout(new BorderLayout());
		
		tcHeader = new JPanel();
		tcHeader.setLayout(new BorderLayout());
		trackIcon = Utils.Images.AUDIO_TRACK_ICON;
		resizedWaveImage = trackIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
		waveButton = new JButton(new ImageIcon(resizedWaveImage));
		waveButton.setBorderPainted(true);   
		waveButton.setContentAreaFilled(false);
		waveButton.setFocusPainted(false);   
		waveButton.setOpaque(false);  

        JLabel trackLabel = null;
        if(trackType == TRACK_TYPE.AUDIO)
        	trackLabel = new JLabel("audio track");
        if(trackType == TRACK_TYPE.INST)
        	trackLabel = new JLabel("inst track");
		JButton deleteButton = new JButton("x");
		deleteButton.setPreferredSize(new Dimension(20, 20));
		
//		deleteButton.setPreferredSize(new Dimension(20, 20));
		deleteButton.addActionListener(e-> {
			trackBar.deleteTrack(this);
		});
		
		tcHeader.add(tcHeaderEastPanel = new JPanel());
		tcHeaderEastPanel.setLayout(new BoxLayout(tcHeaderEastPanel, BoxLayout.X_AXIS));
		
		tcHeaderEastPanel.add(recordButton = new RecordButton(""));
		recordButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trackBar.focus(tc_idx);
			}
		});
		if(trackType == TRACK_TYPE.INST) {
			tcHeaderEastPanel.add(waveButton);

			
			JComboBox<String> inst = new JComboBox<String>(instruments);
			inst.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JComboBox<String> cb = (JComboBox<String>)e.getSource();
					int index = cb.getSelectedIndex();
					trackBar.changeInst(tc_idx, index);
					System.out.println("Instrument is changed to " + index);
					
					if(index == 0) instDialog = null;
					else {
						BasicSynthesizer synth = (BasicSynthesizer)getInst(tc_idx);
						instDialog = new SynthDialog(synth, TrackController.this);
						waveButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								instDialog.setVisible(true);
							}
						});
					}
				}
			});
			inst.setPreferredSize(new Dimension(40, 20));
			tcHeaderEastPanel.add(inst);
		}
		else if(trackType == TRACK_TYPE.AUDIO) {
			add(new JLabel("\t\t\t\t\t\tAudio Track"));
		}
		
//		tcHeader.add(trackLabel);
		tcHeader.add(deleteButton, BorderLayout.EAST);
		
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
	    return new Dimension(Utils.WindowSize.TRACKBAR_WIDTH, 80);
	}
	
	// 12.chap12_graphic.pdf
	class RecordButton extends JButton {
		public RecordButton(String s) {
			super(s);
			setPreferredSize(new Dimension(20, 20));
			setMaximumSize(new Dimension(20, 20));
			setSize(new Dimension(20, 20));
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.RED);
			g.fillOval(getWidth() / 2 - 5, getHeight() / 2 - 5, 10, 10);
		}
	}
	
	public Inst getInst(int tc_idx) {
		return trackBar.getInst(tc_idx);
	}
	
}
