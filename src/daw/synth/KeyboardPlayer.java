package daw.synth;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class KeyboardPlayer implements Inst  {
	private Map<Integer, Integer> keyOffset;
	private int octave;

	public KeyboardPlayer() {
		setKeyOffset();
		octave = 60;
	}
	
	void setKeyOffset() {
		keyOffset = new HashMap<Integer, Integer>();
		keyOffset.put(KeyEvent.VK_A, 0);
		keyOffset.put(KeyEvent.VK_W, 1);
		keyOffset.put(KeyEvent.VK_S, 2);
		keyOffset.put(KeyEvent.VK_E, 3);
		keyOffset.put(KeyEvent.VK_D, 4);
		keyOffset.put(KeyEvent.VK_F, 5);
		keyOffset.put(KeyEvent.VK_T, 6);
		keyOffset.put(KeyEvent.VK_G, 7);
		keyOffset.put(KeyEvent.VK_Y, 8);
		keyOffset.put(KeyEvent.VK_H, 9);
		keyOffset.put(KeyEvent.VK_U, 10);
		keyOffset.put(KeyEvent.VK_J, 11);
		keyOffset.put(KeyEvent.VK_K, 12);
		keyOffset.put(KeyEvent.VK_O, 13);
		keyOffset.put(KeyEvent.VK_L, 14);
		keyOffset.put(KeyEvent.VK_P, 15);
		keyOffset.put(KeyEvent.VK_SEMICOLON, 16);
		keyOffset.put(KeyEvent.VK_QUOTE, 17);
	}
	
	// min : 24 ~ max : 84 -> range : 60
	public double getFreq(int key) {
//		int keyToOffset = keyOffset.get(key);
		double freq = 440.0 * Math.pow(2.0, (key - 69) / 12.0);
		System.out.println(key);
		return freq;
	}
	
	public int getNote(int key) {
		if(keyOffset.get(key) == null) return -1;
		return octave + keyOffset.get(key);
	}
	
	public void nextOctave() {
		octave += 12;
	}
	public void prevOctave() {
		octave -= 12;
	}
	
	public boolean isInOffset(int key) {
		return keyOffset.containsKey(key);
	}

	@Override
	public void noteOn(int key) {
		System.out.println("noteOn");
	}

	@Override
	public void noteOff(int key) {
		System.out.println("noteOff");
	}
	public boolean isHaveDialog() {
		return false;
	}
}
