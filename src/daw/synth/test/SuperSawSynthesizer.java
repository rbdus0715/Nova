package daw.synth.test;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.*;
import com.jsyn.util.WaveRecorder;

public class SuperSawSynthesizer {
    private Synthesizer synth;
    private LineOut lineOut;
    private Add[] adders;
    private SawtoothOscillator[] oscillators;
    private FilterLowPass antiAliasFilter;
    private Multiply masterVolume;
    
    // 슈퍼소우 파라미터
    private static final int NUM_VOICES = 7;  // 홀수개로 중앙 주파수 유지
    private static final double[] DETUNE_RATIOS = {
        1.0,      // 중앙 (0 cents)
        0.9966,   // -6 cents
        1.0034,   // +6 cents  
        0.9931,   // -12 cents
        1.0069,   // +12 cents
        0.9897,   // -18 cents
        1.0103    // +18 cents
    };
    
    // 위상 충돌 방지를 위한 위상 오프셋 (라디안)
    private static final double[] PHASE_OFFSETS = {
        0.0,           // 중앙
        Math.PI * 0.2, // 36도
        Math.PI * 0.4, // 72도
        Math.PI * 0.6, // 108도
        Math.PI * 0.8, // 144도
        Math.PI * 1.0, // 180도
        Math.PI * 1.2  // 216도
    };
    
    // 각 보이스별 볼륨 (중앙이 가장 크고 양쪽으로 갈수록 작아짐)
    private static final double[] VOICE_VOLUMES = {
        1.0,   // 중앙 (0 cents) - 가장 큰 볼륨
        0.8,   // ±6 cents
        0.8,   
        0.6,   // ±12 cents
        0.6,   
        0.4,   // ±18 cents
        0.4    
    };
    
    public SuperSawSynthesizer() {
        // JSyn 신디사이저 생성
        synth = JSyn.createSynthesizer();
        
        // 오디오 출력 생성
        lineOut = new LineOut();
        synth.add(lineOut);
        
        // 오실레이터 배열과 덧셈기 배열 생성
        oscillators = new SawtoothOscillator[NUM_VOICES];
        adders = new Add[NUM_VOICES - 1]; // N-1개의 덧셈기 필요
        
        // 각 보이스 오실레이터 생성
        for (int i = 0; i < NUM_VOICES; i++) {
            oscillators[i] = new SawtoothOscillator();
            synth.add(oscillators[i]);
        }
        
        // 덧셈기들을 생성하여 오실레이터들을 연결
        for (int i = 0; i < NUM_VOICES - 1; i++) {
            adders[i] = new Add();
            synth.add(adders[i]);
        }
        
        // 첫 번째 덧셈기에 첫 두 오실레이터 연결
        oscillators[0].output.connect(adders[0].inputA);
        oscillators[1].output.connect(adders[0].inputB);
        
        // 나머지 덧셈기들을 체인으로 연결
        for (int i = 1; i < NUM_VOICES - 1; i++) {
            adders[i-1].output.connect(adders[i].inputA);
            oscillators[i+1].output.connect(adders[i].inputB);
        }
        
        // 안티앨리어싱 필터 (고주파 성분 제거)
        antiAliasFilter = new FilterLowPass();
        synth.add(antiAliasFilter);
        antiAliasFilter.frequency.set(8000.0); // 8kHz 컷오프
        antiAliasFilter.Q.set(0.7);
        
        // 마스터 볼륨 컨트롤
        masterVolume = new Multiply();
        synth.add(masterVolume);
        masterVolume.inputB.set(0.3); // 마스터 볼륨 30%
        
        // 신호 체인 연결: 마지막 덧셈기 -> 필터 -> 볼륨 -> 출력
        adders[NUM_VOICES - 2].output.connect(antiAliasFilter.input);
        antiAliasFilter.output.connect(masterVolume.inputA);
        masterVolume.output.connect(lineOut.input);
    }
    
    public void start() {
        synth.start();
        lineOut.start();
    }
    
    public void stop() {
        synth.stop();
    }
    
    public void setFrequency(double baseFreq) {
        for (int i = 0; i < NUM_VOICES; i++) {
            // 각 오실레이터의 주파수를 디튠 비율로 설정
            double detuneFreq = baseFreq * DETUNE_RATIOS[i];
            oscillators[i].frequency.set(detuneFreq);
            
            // 볼륨 가중치 적용 (각 오실레이터의 진폭 조절)
            oscillators[i].amplitude.set(VOICE_VOLUMES[i] / NUM_VOICES);
            
            // 위상 오프셋 설정으로 위상 충돌 방지
            oscillators[i].phase.set(PHASE_OFFSETS[i]);
        }
    }
    
    public void setMasterVolume(double volume) {
        masterVolume.inputB.set(Math.max(0.0, Math.min(1.0, volume)));
    }
    
    public void setCutoffFrequency(double cutoff) {
        antiAliasFilter.frequency.set(Math.max(100.0, Math.min(20000.0, cutoff)));
    }
    
    public void playNote(double frequency, double duration) {
        setFrequency(frequency);
        
        try {
            Thread.sleep((long)(duration * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // 코드 진행을 위한 메서드
    public void playChord(double[] frequencies, double duration) {
        // 각 주파수에 대해 슈퍼소우 효과 적용
        double combinedFreq = 0.0;
        for (double freq : frequencies) {
            combinedFreq += freq;
        }
        combinedFreq /= frequencies.length; // 평균 주파수 사용
        
        setFrequency(combinedFreq);
        
        try {
            Thread.sleep((long)(duration * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("JSyn 슈퍼소우 신디사이저 시작...");
        
        SuperSawSynthesizer synth = new SuperSawSynthesizer();
        synth.start();
        
        try {
            // 테스트 시퀀스
            System.out.println("슈퍼소우 테스트 연주 시작");
            
            // 기본 음계 (C4 = 261.63 Hz)
            double[] notes = {
                261.63 / 6, // C4
                293.66 / 6, // D4  
                329.63/6, // E4
                349.23/6, // F4
                392.00/6, // G4
                440.00/6, // A4
                493.88/6, // B4
                523.25/6  // C5
            };
            
            // 1. 단일 음 테스트
            System.out.println("단일 음 테스트...");
            for (double note : notes) {
                synth.playNote(note, 0.5);
                Thread.sleep(100); // 음 사이의 간격
            }
            
            Thread.sleep(500);
            
            // 2. 코드 테스트 (C Major, F Major, G Major)
            System.out.println("코드 테스트...");
            double[][] chords = {
                {261.63, 329.63, 392.00}, // C Major
                {349.23, 440.00, 523.25}, // F Major  
                {392.00, 493.88, 587.33}  // G Major
            };
            
            for (double[] chord : chords) {
                synth.playChord(chord, 1.0);
                Thread.sleep(200);
            }
            
            Thread.sleep(500);
            
            // 3. 필터 스위프 테스트
            System.out.println("필터 스위프 테스트...");
            synth.setFrequency(220.0); // A3
            
            for (int i = 0; i < 50; i++) {
                double cutoff = 500.0 + (i * 150.0); // 500Hz ~ 7850Hz
                synth.setCutoffFrequency(cutoff);
                Thread.sleep(100);
            }
            
            Thread.sleep(1000);
            
            // 4. 볼륨 페이드 테스트
            System.out.println("볼륨 페이드 테스트...");
            synth.setFrequency(440.0); // A4
            synth.setCutoffFrequency(4000.0); // 필터 리셋
            
            // 페이드 인
            for (int i = 0; i <= 20; i++) {
                synth.setMasterVolume(i / 20.0);
                Thread.sleep(100);
            }
            
            Thread.sleep(1000);
            
            // 페이드 아웃
            for (int i = 20; i >= 0; i--) {
                synth.setMasterVolume(i / 20.0);
                Thread.sleep(100);
            }
            
            System.out.println("테스트 완료!");
            
        } catch (InterruptedException e) {
            System.err.println("연주가 중단되었습니다.");
        } finally {
            synth.stop();
            System.out.println("신디사이저 종료");
        }
    }
}