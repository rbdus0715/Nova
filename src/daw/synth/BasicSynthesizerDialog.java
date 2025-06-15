package daw.synth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.foreign.MemorySegment.Scope;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.jsyn.scope.AudioScope;

import daw.synth.BasicSynthesizer.OSC_TYPE;


public class BasicSynthesizerDialog extends JDialog {
	private BasicSynthesizer basicSynthesizer;
	private Set<Integer> keySet;
	private String[] waves = {"saw tooth", "sine", "triangle", "sqaure"};
	private OSC_TYPE curType;
	private JPanel scopePanel;
	
	BasicSynthesizerDialog(BasicSynthesizer basicSynthesizer) {
		keySet = new HashSet<Integer>();
		this.basicSynthesizer = basicSynthesizer;
		curType = OSC_TYPE.SAWTOOTH;
		
		setupKnobs();
		setupWaveComboBox();
		addKeyListener(new KeyPlayListener());
		
		AudioScope scope = basicSynthesizer.getAudioScope();
		scopePanel = scope.getView();
        scopePanel.setPreferredSize(new Dimension(100, 100));
		add(scopePanel, BorderLayout.CENTER);
        scope.start();
		
		setFocusable(true);
		requestFocusInWindow();
	}
	
	public void setupKnobs() {
//		basicSynthesizer.setupPortKnobs(this);
	}

	public void setupWaveComboBox() {
		JComboBox<String> strCombo = new JComboBox<String>(waves);
		strCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				var cb = (JComboBox<String>)e.getSource();
				int index = cb.getSelectedIndex();
				if(index == 0) curType = OSC_TYPE.SAWTOOTH;
				else if(index == 1) curType = OSC_TYPE.SINE;
				else if(index == 2) curType = OSC_TYPE.TRIANGLE;
				else if(index == 3) curType = OSC_TYPE.SQUARE;
				
				basicSynthesizer.changeWave(curType);
				changeWave();
				BasicSynthesizerDialog.this.requestFocusInWindow();
			}
		});
		
		this.add(strCombo);
	}
	
	public void changeWave() {
		this.remove(scopePanel);
		scopePanel = basicSynthesizer.getAudioScope().getView();
		scopePanel.setPreferredSize(new Dimension(100, 100));
		this.add(scopePanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
		
	class KeyPlayListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();			
			if(keySet.contains(keyCode)) return;
			keySet.add(keyCode);
			if(keyCode == KeyEvent.VK_Z)
				basicSynthesizer.prevOctave();
			if(keyCode == KeyEvent.VK_X)
				basicSynthesizer.nextOctave();
			
			basicSynthesizer.noteOn(keyCode);
			System.out.println("ASDf");
		}
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			keySet.remove(keyCode);
			basicSynthesizer.noteOff(keyCode);
			System.out.println("off");
		}
	}

}
