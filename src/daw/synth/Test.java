package daw.synth;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Test extends JFrame {
	public Test() {
		BasicSynthesizer basicSynthesizer = new BasicSynthesizer();
		add(new BasicSynthesizerPanel(basicSynthesizer));
		setVisible(true);
		setLocation(150, 200);
		setSize(1300, 300);
//		setResizable(false);
	}
	
	public static void main(String[] args) {
		new Test();
	}
}
