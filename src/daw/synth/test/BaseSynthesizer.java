package daw.synth.test;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.UnitVoice;
import com.jsyn.util.VoiceAllocator;

public class BaseSynthesizer {
	private int MAX_VOICES = 4;
	private int NUM_UNISON = 1;
	private double DETUNE = 5;
	private Synthesizer synth;
	private VoiceAllocator allocator;
	private LineOut lineOut;
	private UnitVoice[] voices;
	private SawtoothOscillator[] sawOscillators = new SawtoothOscillator[NUM_UNISON]; 
	private double baseFreq = 440.0 / 8;
	
	BaseSynthesizer() {
		synth = JSyn.createSynthesizer();
		synth.add(lineOut = new LineOut());
		voices = new UnitVoice[MAX_VOICES];

		for(int j=0; j<NUM_UNISON; j++) {
			sawOscillators[j] = new SawtoothOscillator();
			synth.add(sawOscillators[j]);
			
			double detune = (j - NUM_UNISON / 2) * DETUNE;
			sawOscillators[j].frequency.set(baseFreq + detune);
			sawOscillators[j].amplitude.set(0.3 / NUM_UNISON);
			sawOscillators[j].output.connect(0, lineOut.input, 0);
			sawOscillators[j].output.connect(0, lineOut.input, 1);
		}
		synth.start();
		lineOut.start();
		
		try {
			Thread.sleep(3000);
			synth.stop();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new BaseSynthesizer();
	}
}
