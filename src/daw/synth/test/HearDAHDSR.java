package daw.synth.test;

/*
 * Copyright 2010 Phil Burk, Mobileer Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.swing.DoubleBoundedRangeModel;
import com.jsyn.swing.PortModelFactory;
import com.jsyn.swing.RotaryTextController;
import com.jsyn.unitgen.EnvelopeDAHDSR;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.UnitOscillator;

/**
 * Play a tone using a JSyn oscillator.
 * Modulate the amplitude using a DAHDSR envelope.
 *
 * @author Phil Burk (C) 2010 Mobileer Inc
 */
public class HearDAHDSR extends JFrame {

    private Synthesizer synth;
    private UnitOscillator osc;
    // Use a square wave to trigger the envelope.
    private UnitOscillator gatingOsc;
    private EnvelopeDAHDSR dahdsr;
    private LineOut lineOut;

    public HearDAHDSR() {
        super("Hear DAHDSR Envelope");
        initializeComponents();
        setupWindow();
    }

    private void initializeComponents() {
        synth = JSyn.createSynthesizer();

        // Add a tone generator.
        synth.add(osc = new SawtoothOscillator());
        // Add a trigger.
        synth.add(gatingOsc = new SquareOscillator());
        // Use an envelope to control the amplitude.
        synth.add(dahdsr = new EnvelopeDAHDSR());
        // Add an output mixer.
        synth.add(lineOut = new LineOut());

        gatingOsc.output.connect(dahdsr.input);
        dahdsr.output.connect(osc.amplitude);
        dahdsr.attack.setup(0.001, 0.01, 2.0);
        osc.output.connect(0, lineOut.input, 0);
        osc.output.connect(0, lineOut.input, 1);

        gatingOsc.frequency.setup(0.001, 0.5, 10.0);
        gatingOsc.frequency.setName("Rate");

        osc.frequency.setup(50.0, 440.0, 2000.0);
        osc.frequency.setName("Freq");

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 0));

        // Add knobs
        setupPortKnob(mainPanel, osc.frequency);
        setupPortKnob(mainPanel, gatingOsc.frequency);
        setupPortKnob(mainPanel, dahdsr.attack);
        setupPortKnob(mainPanel, dahdsr.hold);
        setupPortKnob(mainPanel, dahdsr.decay);
        setupPortKnob(mainPanel, dahdsr.sustain);
        setupPortKnob(mainPanel, dahdsr.release);

        add(mainPanel);
    }

    private void setupPortKnob(JPanel panel, UnitInputPort port) {
        DoubleBoundedRangeModel model = PortModelFactory.createExponentialModel(port);
        RotaryTextController knob = new RotaryTextController(model, 10);
        knob.setBorder(BorderFactory.createTitledBorder(port.getName()));
        knob.setTitle(port.getName());
        panel.add(knob);
    }

    private void setupWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 200);
        setLocationRelativeTo(null); // Center the window
        
        // Add window listener to properly stop synthesizer
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopSynthesizer();
            }
        });
    }

    public void startSynthesizer() {
        // Start synthesizer using default stereo output at 44100 Hz.
        synth.start();
        // We only need to start the LineOut. It will pull data from the
        // oscillator.
        lineOut.start();
    }

    public void stopSynthesizer() {
        if (synth != null) {
            synth.stop();
        }
    }

    /* Can be run as an application */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    HearDAHDSR frame = new HearDAHDSR();
                    frame.setVisible(true);
                    frame.startSynthesizer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}