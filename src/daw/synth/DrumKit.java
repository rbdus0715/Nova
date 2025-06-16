package daw.synth;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;
import com.jsyn.util.VoiceAllocator;

public class DrumKit extends KeyboardPlayer implements Inst {
	private Synthesizer synth;
	private VoiceAllocator allocator;
	private LineOut lineOut;
	private int VOICE_NUM = 5;
	private File[] drumKitInst;
	private Map<Character, FloatSample> keySamples;
	private Map<Character, VariableRateDataReader> keyPlayers;

	public DrumKit() {
		keySamples = new HashMap<>();
		keyPlayers = new HashMap<>();
		
		synth = JSyn.createSynthesizer();
		synth.add(lineOut = new LineOut());
		
		drumKitInst = new File[VOICE_NUM];
		File sampleFolder = new File("src/samples/drumkit");
		drumKitInst = sampleFolder.listFiles();
		
		String key = "asdfg";
		for(int i=0; i<drumKitInst.length; i++) 
			loadSampleForKey(key.charAt(i), i);
		
		synth.start();
		lineOut.start();
	}
	
	void loadSampleForKey(char key, int i) {
		try {
			FloatSample sample = SampleLoader.loadFloatSample(drumKitInst[i]);
			keySamples.put(key, sample);
			
			VariableRateDataReader samplePlayer;
            if (sample.getChannelsPerFrame() == 1) {
                synth.add(samplePlayer = new VariableRateMonoReader());
                samplePlayer.output.connect(0, lineOut.input, 0);
            } else if (sample.getChannelsPerFrame() == 2) {
                synth.add(samplePlayer = new VariableRateStereoReader());
                samplePlayer.output.connect(0, lineOut.input, 0);
                samplePlayer.output.connect(1, lineOut.input, 1);
            } else {
                throw new RuntimeException("Can only play mono or stereo samples.");
            }
            synth.add(samplePlayer);
            samplePlayer.rate.set(sample.getFrameRate());
            keyPlayers.put(key, samplePlayer);
			
		} catch(IOException e) {
			System.out.print(e);
		}
	}
	
	public void noteOn(char key) {
		VariableRateDataReader player = keyPlayers.get(key);
		FloatSample sample = keySamples.get(key);
		if(player != null && sample != null ) {
			try {
				player.dataQueue.clear();
				player.dataQueue.queue(sample);
				System.out.println("key: "+ key);
			} catch(Exception e) {
				System.out.print(e);
			}
		}
	}

	@Override
	public void noteOff(char key) {
		// TODO Auto-generated method stub
		
	}

}
