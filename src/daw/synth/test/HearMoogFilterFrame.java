package daw.synth.test;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.scope.AudioScope;
import com.jsyn.swing.DoubleBoundedRangeModel;
import com.jsyn.swing.PortModelFactory;
import com.jsyn.swing.RotaryTextController;
import com.jsyn.unitgen.*;

public class HearMoogFilterFrame extends JFrame {
    private Synthesizer synth;
    private UnitOscillator oscillator;
    private FilterFourPoles filterMoog;
    private FilterLowPass filterBiquad;
    private LinearRamp rampCutoff;
    private PassThrough tieQ;
    private PassThrough tieCutoff;
    private PassThrough mixer;
    private LineOut lineOut;

    private AudioScope scope;
    private boolean useCutoffRamp = false;

    public HearMoogFilterFrame() {
        setTitle("Hear Moog Style Filter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // 화면 중앙

        synth = JSyn.createSynthesizer();
        synth.add(oscillator = new SawtoothOscillatorBL());
        synth.add(rampCutoff = new LinearRamp());
        synth.add(tieQ = new PassThrough());
        synth.add(tieCutoff = new PassThrough());
        synth.add(filterMoog = new FilterFourPoles());
        synth.add(filterBiquad = new FilterLowPass());
        synth.add(mixer = new PassThrough());
        synth.add(lineOut = new LineOut());

        oscillator.output.connect(filterMoog.input);
        oscillator.output.connect(filterBiquad.input);
        if (useCutoffRamp) {
            rampCutoff.output.connect(filterMoog.frequency);
            rampCutoff.output.connect(filterBiquad.frequency);
            rampCutoff.time.set(0.000);
        } else {
            tieCutoff.output.connect(filterMoog.frequency);
            tieCutoff.output.connect(filterBiquad.frequency);
        }
        tieQ.output.connect(filterMoog.Q);
        tieQ.output.connect(filterBiquad.Q);
        filterMoog.output.connect(mixer.input);
        mixer.output.connect(0, lineOut.input, 0);
        mixer.output.connect(0, lineOut.input, 1);

        filterBiquad.amplitude.set(0.1);
        oscillator.frequency.setup(50.0, 130.0, 3000.0);
        oscillator.amplitude.setup(0.0, 0.336, 1.0);
        rampCutoff.input.setup(filterMoog.frequency);
        tieCutoff.input.setup(filterMoog.frequency);
        tieQ.input.setup(0.1, 0.7, 10.0);

        setupGUI();

        synth.start();
        lineOut.start();
    }

    private void setupGUI() {
        setLayout(new BorderLayout());
        add(new JLabel("Sawtooth through a \"Moog\" style filter."), BorderLayout.NORTH);

        JPanel rackPanel = new JPanel();
        rackPanel.setLayout(new BoxLayout(rackPanel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        ButtonGroup cbg = new ButtonGroup();
        JRadioButton radioButton = new JRadioButton("Moog", true);
        cbg.add(radioButton);
        radioButton.addItemListener(e -> {
            mixer.input.disconnectAll();
            filterMoog.output.connect(mixer.input);
        });
        buttonPanel.add(radioButton);

        radioButton = new JRadioButton("Biquad", false);
        cbg.add(radioButton);
        radioButton.addItemListener(e -> {
            mixer.input.disconnectAll();
            filterBiquad.output.connect(mixer.input);
        });
        buttonPanel.add(radioButton);

        rackPanel.add(buttonPanel);

        JPanel knobPanel = new JPanel(new GridLayout(1, 0));
        knobPanel.add(setupPortKnob(oscillator.frequency, "OscFreq"));
        knobPanel.add(setupPortKnob(oscillator.amplitude, "OscAmp"));

        if (useCutoffRamp) {
            knobPanel.add(setupPortKnob(rampCutoff.input, "Cutoff"));
        } else {
            knobPanel.add(setupPortKnob(tieCutoff.input, "Cutoff"));
        }

        knobPanel.add(setupPortKnob(tieQ.input, "Q"));

        rackPanel.add(knobPanel);
        add(rackPanel, BorderLayout.SOUTH);

        scope = new AudioScope(synth);
        scope.addProbe(oscillator.output);
        scope.addProbe(filterMoog.output);
        scope.addProbe(filterBiquad.output);
        scope.setTriggerMode(AudioScope.TriggerMode.NORMAL);
        scope.getView().setControlsVisible(false);
        add(scope.getView(), BorderLayout.CENTER);
        scope.start();
    }

    private RotaryTextController setupPortKnob(UnitInputPort port, String label) {
        DoubleBoundedRangeModel model = PortModelFactory.createExponentialModel(port);
        RotaryTextController knob = new RotaryTextController(model, 10);
        knob.setBorder(BorderFactory.createTitledBorder(label));
        knob.setTitle(label);
        return knob;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HearMoogFilterFrame frame = new HearMoogFilterFrame();
            frame.setVisible(true);
        });
    }
}
