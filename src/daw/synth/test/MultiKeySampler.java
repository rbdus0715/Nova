package daw.synth.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;

/**
 * 키마다 다른 소리를 재생하는 샘플러
 */
public class MultiKeySampler {
    
    private Synthesizer synth;
    private LineOut lineOut;
    private Map<Character, FloatSample> keySamples;
    private Map<Character, VariableRateDataReader> keyPlayers;
    
    public MultiKeySampler() {
        keySamples = new HashMap<>();
        keyPlayers = new HashMap<>();
        initializeSynth();
        loadSamples();
    }
    
    private void initializeSynth() {
        synth = JSyn.createSynthesizer();
        synth.add(lineOut = new LineOut());
        synth.start();
        lineOut.start();
    }
    
    private void loadSamples() {
        // 방법 1: 같은 샘플을 다른 피치로 재생
        loadSampleForAllKeys("https://www.softsynth.com/samples/Clarinet.wav");
        
        // 방법 2: 다른 샘플 파일들 (있다면)
        // loadSampleForKey('a', "path/to/sample1.wav");
        // loadSampleForKey('s', "path/to/sample2.wav");
        // ... 등등
    }
    
    private void loadSampleForAllKeys(String sampleUrl) {
        try {
            URL url = new URL(sampleUrl);
            FloatSample baseSample = SampleLoader.loadFloatSample(url);
            
            // 키보드 키들과 피치 매핑
            String keys = "asdfghjkl;";
            double[] pitchRatios = {
                0.5,    // a - 낮은 음
                0.6,    // s
                0.7,    // d
                0.8,    // f
                1.0,    // g - 원래 음
                1.2,    // h
                1.4,    // j
                1.6,    // k
                1.8,    // l
                2.0     // ; - 높은 음
            };
            
            for (int i = 0; i < keys.length(); i++) {
                char key = keys.charAt(i);
                keySamples.put(key, baseSample);
                
                // 각 키마다 플레이어 생성
                VariableRateDataReader player;
                if (baseSample.getChannelsPerFrame() == 1) {
                    player = new VariableRateMonoReader();
                    // 모노 신호를 양쪽 채널로 출력
                    player.output.connect(0, lineOut.input, 0); // 왼쪽
                    player.output.connect(0, lineOut.input, 1); // 오른쪽 (같은 신호)
                } else {
                    player = new VariableRateStereoReader();
                    player.output.connect(0, lineOut.input, 0);
                    player.output.connect(1, lineOut.input, 1);
                }
                
                synth.add(player);
                
                // 피치 설정 (샘플레이트 조정으로 피치 변경)
                player.rate.set(baseSample.getFrameRate() * pitchRatios[i]);
                
                keyPlayers.put(key, player);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadSampleForKey(char key, String sampleUrl) {
        try {
            URL url = new URL(sampleUrl);
            FloatSample sample = SampleLoader.loadFloatSample(url);
            keySamples.put(key, sample);
            
            // 플레이어 생성
            VariableRateDataReader player;
            if (sample.getChannelsPerFrame() == 1) {
                player = new VariableRateMonoReader();
                // 모노 신호를 양쪽 채널로 출력
                player.output.connect(0, lineOut.input, 0); // 왼쪽
                player.output.connect(0, lineOut.input, 1); // 오른쪽
            } else {
                player = new VariableRateStereoReader();
                player.output.connect(0, lineOut.input, 0);
                player.output.connect(1, lineOut.input, 1);
            }
            
            synth.add(player);
            player.rate.set(sample.getFrameRate());
            keyPlayers.put(key, player);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 특정 키의 소리 재생
     */
    public void playKey(char key) {
        VariableRateDataReader player = keyPlayers.get(key);
        FloatSample sample = keySamples.get(key);
        
        if (player != null && sample != null) {
            try {
                // 기존 재생 중인 것이 있다면 정지
                player.dataQueue.clear();
                
                // 샘플 재생
                player.dataQueue.queue(sample);
                
                System.out.println("Playing key: " + key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 특정 키의 소리 정지
     */
    public void stopKey(char key) {
        VariableRateDataReader player = keyPlayers.get(key);
        if (player != null) {
            player.dataQueue.clear();
            System.out.println("Stopped key: " + key);
        }
    }
    
    /**
     * 모든 소리 정지
     */
    public void stopAll() {
        for (VariableRateDataReader player : keyPlayers.values()) {
            player.dataQueue.clear();
        }
    }
    
    /**
     * 리소스 해제
     */
    public void cleanup() {
        stopAll();
        synth.stop();
    }
    
    // 테스트용
    public static void main(String[] args) {
        MultiKeySampler sampler = new MultiKeySampler();
        
        try {
            // 몇 개 키 테스트
            sampler.playKey('a');
//            Thread.sleep(1000);
            
            sampler.playKey('s');
//            Thread.sleep(1000);
            
            sampler.playKey(';');
            Thread.sleep(1000);
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sampler.cleanup();
        }
    }
}