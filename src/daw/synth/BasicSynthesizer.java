package daw.synth;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.scope.AudioScope;
import com.jsyn.swing.DoubleBoundedRangeModel;
import com.jsyn.swing.PortModelFactory;
import com.jsyn.swing.RotaryTextController;
import com.jsyn.unitgen.EnvelopeDAHDSR;
import com.jsyn.unitgen.FilterLowPass;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Pan;
import com.jsyn.unitgen.PlateReverb;
import com.jsyn.unitgen.RoomReverb;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.UnitOscillator;

import daw.synth.BasicSynthesizer.OSC_TYPE;
import jdk.jfr.Frequency;

public class BasicSynthesizer extends KeyboardPlayer implements Inst {
	private Synthesizer synth;
	private LineOut lineOut;
	private int octave=60;
	private int MAX_VOICE = 5;
	private List<Voice> voices;
	public static enum OSC_TYPE {
		SAWTOOTH, SINE, SQUARE, TRIANGLE
	}
	private AudioScope scope;
	private FilterLowPass filterBiquad;
	private PlateReverb reverb;
	private Pan dryWet;
	
	BasicSynthesizer() {
		
		synth = JSyn.createSynthesizer();
		synth.add(lineOut = new LineOut());
		synth.add(filterBiquad = new FilterLowPass());
		synth.add(reverb = new PlateReverb());
		synth.add(dryWet = new Pan());

		initVoice(OSC_TYPE.SAWTOOTH);
		initAudioScope();
		initFilter();
		initReverb();

		synth.start();
		lineOut.start();
	}
	
	public void initReverb() {
		filterBiquad.output.connect(dryWet.input);
		dryWet.output.connect(1, reverb.input, 0);
		dryWet.output.connect(0, lineOut.input, 0);
        dryWet.output.connect(0, lineOut.input, 1);
		reverb.output.connect(0, lineOut.input, 0);
        reverb.output.connect(0, lineOut.input, 1);
        dryWet.pan.set(-1);
        dryWet.pan.setName("Reverb");
	}
	
	public void initFilter() {
		if (filterBiquad != null) {
			filterBiquad.input.disconnectAll();
			filterBiquad.output.disconnectAll();
			synth.remove(filterBiquad);
		}
		for(var voice:voices) {
			voice.osc.output.connect(filterBiquad.input);		
		}
//		filterBiquad.output.connect(0, lineOut.input, 0);
//		filterBiquad.output.connect(0, lineOut.input, 1);	
	}
	
	public void changeWave(OSC_TYPE type) {
		initVoice(type);
		initAudioScope();
//		initFilter();
	}
	
	public void initVoice(OSC_TYPE type) {
		cleanupVoices();
		voices = new ArrayList<>();
		for(int i=0; i<MAX_VOICE; i++) {
			Voice voice = new Voice(synth, type);
			voices.add(voice);
			voice.osc.output.connect(filterBiquad.input);
		}
	}
	
	private void cleanupVoices() {
		if (voices != null) {
			for (Voice voice : voices) {
//				voice.osc.output.disconnect(filterBiquad.input);
				voice.stop();
				if(filterBiquad != null) {
					voice.osc.output.disconnect(filterBiquad.input);
				}
				
				synth.remove(voice.osc);
				synth.remove(voice.envADSR);
			}
			voices.clear();
		}
	}
	
	public void setupPortKnobs(JPanel panel) {
		Voice firstVoice = voices.get(0);
        setupPortKnob(panel, firstVoice.envADSR.attack);
        setupPortKnob(panel, firstVoice.envADSR.hold);
        setupPortKnob(panel, firstVoice.envADSR.decay);
        setupPortKnob(panel, firstVoice.envADSR.sustain);
        setupPortKnob(panel, firstVoice.envADSR.release);
        setupPortKnob(panel, filterBiquad.frequency);
        setupPortKnob(panel, dryWet.pan);
    }
	
    private void setupPortKnob(JPanel panel, UnitInputPort port) {
        DoubleBoundedRangeModel model = PortModelFactory.createExponentialModel(port);
        RotaryTextController knob = new RotaryTextController(model, 10);
        knob.setBorder(BorderFactory.createTitledBorder(port.getName()));
        knob.setTitle(port.getName());
        
        String param = port.getName();
        model.addChangeListener(e -> {
        	applyKnobValue(model, param);
        });
        
        panel.add(knob);
    }
    
    public void applyKnobValue(DoubleBoundedRangeModel model, String param) {
    	double value = model.getDoubleValue();
    	for(var voice:voices) {
    		UnitInputPort targetPort = getPortByName(voice.envADSR, param);
            if(targetPort != null) {
                targetPort.set(value);
            }
    	}
    	if (param.equalsIgnoreCase("frequency")) {
    		filterBiquad.frequency.set(value);
    	}
    }
    
    private UnitInputPort getPortByName(EnvelopeDAHDSR envelope, String name) {
        switch(name.toLowerCase()) {
            case "attack": return envelope.attack;
            case "hold": return envelope.hold;
            case "decay": return envelope.decay;
            case "sustain": return envelope.sustain;
            case "release": return envelope.release;
            default: return null;
        }
    }
    
    public void initAudioScope() {
        if (scope != null) {
            scope.stop();
            scope = null;
        }
    	scope = new AudioScope(synth);
        scope.addProbe(voices.get(0).osc.output);
        scope.setTriggerMode(AudioScope.TriggerMode.NORMAL);
        scope.getView().setControlsVisible(false);
        scope.start();
    }
    
    public AudioScope getAudioScope() {
    	return scope;
    }
	
	
	public void noteOn(int key) {
		if(!isInOffset(key)) return;
		for(var voice : voices) {
			if(voice.isActive == false) {
				voice.start(getFreq(key), key);
				break;
			}
		}
	}
	public void noteOff(int key) {
		if(!isInOffset(key)) return;
		for(var voice : voices) {
			if(voice.isActive == true && voice.keyPlay == key) {
				voice.stop();
				break;
			}
		}
	}

	@Override
	public void noteOn(char key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noteOff(char key) {
		// TODO Auto-generated method stub
		
	}
}

class Voice {
	public UnitOscillator osc;
	public EnvelopeDAHDSR envADSR;
	public boolean isActive;
	public double frequency;
	public int keyPlay;
	private Synthesizer synth;
	
	public Voice(Synthesizer synth, OSC_TYPE type) {
		this.synth = synth;
		initOsc(type);
		synth.add(osc);
		synth.add(envADSR = new EnvelopeDAHDSR());
		
		osc.frequency.set(0);
		envADSR.output.connect(osc.amplitude);
		envADSR.attack.setup(0.001, 0.01, 2.0); 
		envADSR.input.off();
	}
	
	public void initOsc(OSC_TYPE type) {
		if(type == OSC_TYPE.SAWTOOTH) osc = new SawtoothOscillator();
		else if(type == OSC_TYPE.SINE) osc = new SineOscillator();
		else if(type == OSC_TYPE.TRIANGLE) osc = new TriangleOscillator();
		else if(type == OSC_TYPE.SQUARE) osc = new SquareOscillator();
	}
	
	public void start(double freq, int key) {
		isActive = true;
		osc.frequency.set(freq);
		envADSR.input.on();
		keyPlay = key;
	}
	public void stop() {
		isActive = false;
		osc.frequency.set(0);
		envADSR.input.off();
	}
}


