package daw.synth;

public interface Inst {
	void noteOn(int key);
	void noteOff(int key);
	boolean isHaveDialog();
}
