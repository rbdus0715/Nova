package daw.utils;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.border.Border;

public class Utils {
	public static class WindowSize {
		public static final int WINDOW_WIDTH = 1300;		
		public static final int WINDOW_HEIGHT = 800;
		public static final int TRACKBAR_WIDTH = 200;		
		public static final int TRACKBAR_HEIGHT = 100;		
		public static final int CONTROLBAR_HEIGHT = 100;		
		public static final int TRACKRULER_WIDTH = 1040;		
		public static final int TRACKRULER_HEIGHT = 35;
	}
	
	public static class WindowDesign {
		public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.WHITE);
		public static final Border LINE_BORDER_BLACK = BorderFactory.createLineBorder(Color.BLACK);
		public static final Border BORDER_TOP = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK);
		public static final Border BORDER_BOTTOM = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK);
		public static final Border BORDER_LEFT = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK);
		public static final Border BORDER_RIGHT = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK);
	}
	
	public static class ColorMap {
//		public static final Color COLOR_CONTROLBAR = new Color(30, 30, 46);
		public static final Color COLOR_CONTROLBAR = new Color(25, 25, 25);
		public static final Color COLOR_BACKGROUND = new Color(40, 40, 40);
		public static final Color COLOR_PANEL = new Color(35, 35, 35);
		public static final Color COLOR_BUTTON = new Color(61, 64, 91);
		public static final Color COLOR_PLAY_HEAD = Color.WHITE;
	}
	
	public static class Images {
		public static final ImageIcon AUDIO_TRACK_ICON = new ImageIcon(Images.class.getResource("/images/wave-sound.png"));
	}
}
