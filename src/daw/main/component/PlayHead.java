package daw.main.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import daw.main.component.track.TrackBody;
import daw.utils.Utils;

public class PlayHead extends JPanel {
	private Timer timer;
	private int position;
    private int playheadPosition = 0;
    private boolean isPlaying = false;
    private final int TRACK_WIDTH = 2000;
    private final int PLAYHEAD_SPEED = 1;
    private TrackBody trackBody;
    private boolean record;
	
	public PlayHead() {
		position = 0;
		setBackground(Utils.ColorMap.COLOR_PLAY_HEAD);
		setBounds(position, 0, 2, 1000); 
		timer = new Timer(10, e->{
			trackBody.playCurrentNote(position);
			trackBody.stopCurrentNote(position);
			updatePlayhead();
		});
	}
	
	public void setTimer(int speed) {
		timer = new Timer(speed, e->{
			trackBody.playCurrentNote(position);
			trackBody.stopCurrentNote(position);
			updatePlayhead();
		});
	}
	
	public void setTrackBody(TrackBody trackBody) {
		this.trackBody = trackBody;
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.RED);
		g.drawLine(position, 0, position, 1000);
		int[] a = {position-10, position+10, position};
		int[] b = {0, 0, 10};
		g.fillPolygon(a, b, 3);
	}
	public int getPosition() {
		return position;
	}
	
	public boolean getIsPlaying() {
		return isPlaying;
	}
	
	public void initPlay() {
		isPlaying = false;
		setPlayhead(0);
		timer.stop();
	}
	
	public void startPlay() {
		if(!isPlaying) {
			isPlaying = true;
			timer.start();
		}
	}
	
	public void stopPlay() {
		if(isPlaying) {
			isPlaying = false;
			timer.stop();
		}
	}
	
	public void setPlayhead(int position) {
		this.position = position;
		repaint();
	}
	
	public void updatePlayhead() {
		position += PLAYHEAD_SPEED;
		if(position >= 1024) {
			position = 0;
		}
		repaint();
	}
	
	public void setIsRecording(boolean record) {
		this.record = record;
	}
	public boolean isRecording() {
		return record;
	}
}
