package daw.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.ModuleLayer.Controller;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import daw.main.component.EditArea;
import daw.main.component.PlayHead;
import daw.main.component.TrackRuler;
import daw.main.component.metronome.Metronome;
import daw.main.component.navigation.Bar;
import daw.main.component.track.NewTrack;
import daw.main.component.track.TrackBody;
import daw.main.component.track.TrackController;
import daw.utils.Utils;

public class Daw extends JFrame {

	private Point mouseClickPoint;
	private Metronome metronome;
	private JPanel mainPanel;
	private PlayHead playhead;
	private JPanel trackPanel;
	private JPanel newTrackAndRuler;
	private TrackBody trackBody;
	private TrackBar trackBar;
	private JPanel trackContolAndTrackLanes;
	private TrackRuler trackRuler;
	private JScrollPane trackBarScroll;
	private JScrollPane trackRulerScroll;
	private LayeredPaneTrackMain layeredPane;
	private JScrollPane trackBodyScroll;
	private Container container;
	private EditArea editArea;
	
	Daw() {		

		setTitle("Nova");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
	    String os = System.getProperty("os.name").toLowerCase();
    	ImageIcon icon = new ImageIcon("src/images/logo.png"); 
	    if(os.contains("mac") || os.contains("darwin")) {
	        setIconImage(icon.getImage());
			if (Taskbar.isTaskbarSupported()) {
	            Taskbar taskbar = Taskbar.getTaskbar();
	            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
	                taskbar.setIconImage(icon.getImage());
	            }
	        }
	    }
	    else
	    	setIconImage(icon.getImage());
  

	    
		metronome = new Metronome();
		add(new Bar(), BorderLayout.WEST);
		
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		playhead = new PlayHead();
		mainPanel.add(new ControlBar(playhead), BorderLayout.NORTH);
		
		trackPanel = new JPanel(new BorderLayout());
		// 1
		newTrackAndRuler = new JPanel(new BorderLayout());
		trackBody = new TrackBody(playhead, this);
		trackBar = new TrackBar(trackBody);
		newTrackAndRuler.add(new NewTrack(trackBar, trackBody), BorderLayout.WEST);
		trackRuler = new TrackRuler(metronome);
		trackRulerScroll = new JScrollPane(
				trackRuler,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		trackRulerScroll.setBorder(null);
		newTrackAndRuler.add(trackRulerScroll, BorderLayout.CENTER);
		trackPanel.add(newTrackAndRuler, BorderLayout.NORTH);
		// 2
		trackContolAndTrackLanes = new JPanel(new BorderLayout());
		trackContolAndTrackLanes.setLayout(new BorderLayout());
		trackBarScroll = new JScrollPane(
				trackBar,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		playhead.setPreferredSize(new Dimension(2000, 2000));
		playhead.setOpaque(false);
		playhead.setBounds(0, 0, 2000, 2000);
		trackBody.setBounds(0, 0, 2000, 2000);
		layeredPane = new LayeredPaneTrackMain(playhead, trackBody);
		
//		trackBodyScroll = new JScrollPane(
//				layeredPane,
//				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
//				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		trackBarScroll.setBorder(null);
		trackContolAndTrackLanes.add(trackBarScroll, BorderLayout.WEST);
		trackContolAndTrackLanes.add(layeredPane, BorderLayout.CENTER);
		trackPanel.add(trackContolAndTrackLanes, BorderLayout.CENTER);
		
		mainPanel.add(trackPanel, BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);	
		add(new BottomBar(), BorderLayout.SOUTH);
		
		container = getContentPane();
		container.setBackground(Utils.ColorMap.COLOR_BACKGROUND);
		setSize(Utils.WindowSize.WINDOW_WIDTH, Utils.WindowSize.WINDOW_HEIGHT);
		setLocation(100, 100);
		setResizable(false);
		setVisible(true);
	}
	
	public void viewEditor() {
		System.out.println("DAW : editor is opened");
		JPanel editArea = new EditArea(layeredPane);
		layeredPane.add(editArea, Integer.valueOf(4));
	}
	public void closeEditor() {
		layeredPane.closeEditor(editArea);
	}
}





// custom title bar
//setUndecorated(true);
//JPanel titleBar = new JPanel();
//titleBar.addMouseListener(new MouseAdapter() {
//    @Override
//    public void mousePressed(MouseEvent e) {
//        mouseClickPoint = e.getPoint();
//    }
//});
//titleBar.addMouseMotionListener(new MouseMotionAdapter() {
//    @Override
//    public void mouseDragged(MouseEvent e) {
//        Point currentPoint = e.getLocationOnScreen();
//        setLocation(currentPoint.x - mouseClickPoint.x, 
//                   currentPoint.y - mouseClickPoint.y);
//    }
//});
//titleBar.setLayout(new BorderLayout());
//JButton x_button = new JButton("    exit    ");
//x_button.setBackground(new Color(30, 30, 30));
//x_button.setBorder(null);
//x_button.addActionListener(e->System.exit(0));
//titleBar.add(x_button, BorderLayout.WEST);
//titleBar.setPreferredSize(new Dimension(0, 30));
//titleBar.setBackground(new Color(30, 30, 30));
//add(titleBar, BorderLayout.NORTH);
