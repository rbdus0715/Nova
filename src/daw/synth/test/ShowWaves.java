package daw.synth.test;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ShowWaves extends JFrame {
    private WaveformPanel waveformPanel;
    private JButton loadButton;
    private JButton playButton;
    private Clip audioClip;
    
    public ShowWaves() {
        setTitle("WAV Waveform Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 파형 패널
        waveformPanel = new WaveformPanel();
        waveformPanel.setPreferredSize(new Dimension(800, 400));
        waveformPanel.setBackground(Color.BLACK);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        loadButton = new JButton("Load WAV File");
        playButton = new JButton("Play");
        playButton.setEnabled(false);
        
        buttonPanel.add(loadButton);
        buttonPanel.add(playButton);
        
        add(waveformPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 이벤트 리스너
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadWaveFile();
            }
        });
        
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playAudio();
            }
        });
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void loadWaveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".wav");
            }
            
            @Override
            public String getDescription() {
                return "WAV Files (*.wav)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                loadAudioFile(selectedFile);
                playButton.setEnabled(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    private void loadAudioFile(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();
        
        // 오디오 데이터를 byte 배열로 읽기
        long frameLength = audioInputStream.getFrameLength();
        int frameSize = format.getFrameSize();
        byte[] audioBytes = new byte[(int) (frameLength * frameSize)];
        audioInputStream.read(audioBytes);
        
        // 샘플 데이터 추출
        float[] samples = extractSamples(audioBytes, format);
        
        // 파형 패널에 데이터 설정
        waveformPanel.setSamples(samples);
        waveformPanel.repaint();
        
        // 오디오 재생을 위한 Clip 준비
        audioInputStream.close();
        audioInputStream = AudioSystem.getAudioInputStream(file);
        audioClip = AudioSystem.getClip();
        audioClip.open(audioInputStream);
        
        setTitle("WAV Waveform Visualizer - " + file.getName());
    }
    
    private float[] extractSamples(byte[] audioBytes, AudioFormat format) {
        int bytesPerSample = format.getSampleSizeInBits() / 8;
        int channels = format.getChannels();
        int totalSamples = audioBytes.length / bytesPerSample;
        int samplesPerChannel = totalSamples / channels;
        
        System.out.println("Audio Format: " + format);
        System.out.println("Channels: " + channels + ", Sample Rate: " + format.getSampleRate());
        System.out.println("Sample Size: " + format.getSampleSizeInBits() + " bits");
        System.out.println("Total bytes: " + audioBytes.length + ", Samples per channel: " + samplesPerChannel);
        
        float[] samples = new float[samplesPerChannel];
        
        if (format.getSampleSizeInBits() == 16) {
            // 16-bit samples
            for (int i = 0; i < samplesPerChannel; i++) {
                int byteIndex = i * bytesPerSample * channels; // 첫 번째 채널만 사용
                int sample = 0;
                
                if (byteIndex + 1 < audioBytes.length) {
                    if (format.isBigEndian()) {
                        sample = (audioBytes[byteIndex] << 8) | (audioBytes[byteIndex + 1] & 0xFF);
                    } else {
                        sample = (audioBytes[byteIndex + 1] << 8) | (audioBytes[byteIndex] & 0xFF);
                    }
                    
                    // Sign extension for 16-bit
                    if (sample > 32767) sample -= 65536;
                    
                    samples[i] = sample / 32768.0f; // normalize to -1.0 to 1.0
                }
            }
        } else if (format.getSampleSizeInBits() == 8) {
            // 8-bit samples
            for (int i = 0; i < samplesPerChannel; i++) {
                int byteIndex = i * channels; // 첫 번째 채널만 사용
                if (byteIndex < audioBytes.length) {
                    samples[i] = (audioBytes[byteIndex] - 128) / 128.0f;
                }
            }
        } else if (format.getSampleSizeInBits() == 24) {
            // 24-bit samples
            for (int i = 0; i < samplesPerChannel; i++) {
                int byteIndex = i * bytesPerSample * channels;
                int sample = 0;
                
                if (byteIndex + 2 < audioBytes.length) {
                    if (format.isBigEndian()) {
                        sample = (audioBytes[byteIndex] << 16) | 
                                ((audioBytes[byteIndex + 1] & 0xFF) << 8) | 
                                (audioBytes[byteIndex + 2] & 0xFF);
                    } else {
                        sample = (audioBytes[byteIndex + 2] << 16) | 
                                ((audioBytes[byteIndex + 1] & 0xFF) << 8) | 
                                (audioBytes[byteIndex] & 0xFF);
                    }
                    
                    // Sign extension for 24-bit
                    if (sample > 8388607) sample -= 16777216;
                    
                    samples[i] = sample / 8388608.0f;
                }
            }
        }
        
        // 샘플 데이터 확인
        float maxSample = 0;
        for (float sample : samples) {
            maxSample = Math.max(maxSample, Math.abs(sample));
        }
        System.out.println("Max sample value: " + maxSample);
        System.out.println("First 10 samples: ");
        for (int i = 0; i < Math.min(10, samples.length); i++) {
            System.out.print(samples[i] + " ");
        }
        System.out.println();
        
        return samples;
    }
    
    private void playAudio() {
        if (audioClip != null) {
            if (audioClip.isRunning()) {
                audioClip.stop();
                playButton.setText("Play");
            } else {
                audioClip.setFramePosition(0);
                audioClip.start();
                playButton.setText("Stop");
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ShowWaves().setVisible(true);
        });
    }
}

class WaveformPanel extends JPanel {
    private float[] samples;
    private static final Color WAVEFORM_COLOR = new Color(255, 165, 0); // Orange
    private static final Color GRID_COLOR = new Color(50, 50, 50);
    
    public void setSamples(float[] samples) {
        this.samples = samples;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (samples == null || samples.length == 0) {
            g.setColor(Color.WHITE);
            g.drawString("No audio data loaded", 10, 20);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int centerY = height / 2;
        
        // 격자 그리기
        drawGrid(g2d, width, height, centerY);
        
        // 파형 그리기
        drawWaveform(g2d, width, height, centerY);
        
        // 정보 표시
        drawInfo(g2d);
    }
    
    private void drawGrid(Graphics2D g2d, int width, int height, int centerY) {
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(1.0f));
        
        // 중앙선
        g2d.drawLine(0, centerY, width, centerY);
        
        // 수평 격자선
        int gridSpacing = height / 8;
        for (int y = gridSpacing; y < height; y += gridSpacing) {
            g2d.drawLine(0, y, width, y);
        }
        
        // 수직 격자선
        int verticalSpacing = width / 10;
        for (int x = verticalSpacing; x < width; x += verticalSpacing) {
            g2d.drawLine(x, 0, x, height);
        }
    }
    
    private void drawWaveform(Graphics2D g2d, int width, int height, int centerY) {
        g2d.setColor(WAVEFORM_COLOR);
        g2d.setStroke(new BasicStroke(1.5f));
        
        if (samples.length == 0) {
            g2d.setColor(Color.RED);
            g2d.drawString("No sample data available", 10, 40);
            return;
        }
        
        int samplesPerPixel = Math.max(1, samples.length / width);
        
        // 디버그 정보
        g2d.setColor(Color.YELLOW);
        g2d.drawString("Samples: " + samples.length + ", SPP: " + samplesPerPixel, 10, 60);
        
        g2d.setColor(WAVEFORM_COLOR);
        
        for (int x = 0; x < width - 1; x++) {
            int sampleIndex = x * samplesPerPixel;
            if (sampleIndex >= samples.length) break;
            
            // 현재 픽셀의 최대값과 최소값 찾기 (더 나은 파형 표현)
            float minSample = Float.MAX_VALUE;
            float maxSample = Float.MIN_VALUE;
            
            for (int i = sampleIndex; i < Math.min(sampleIndex + samplesPerPixel, samples.length); i++) {
                minSample = Math.min(minSample, samples[i]);
                maxSample = Math.max(maxSample, samples[i]);
            }
            
            // Y 좌표 계산 (화면에 맞게 스케일링)
            int y1 = centerY - (int) (maxSample * (centerY - 10));
            int y2 = centerY - (int) (minSample * (centerY - 10));
            
            // 수직선 그리기 (최대값에서 최소값까지)
            if (y1 != y2) {
                g2d.drawLine(x, y1, x, y2);
            } else {
                g2d.drawLine(x, y1, x + 1, y1);
            }
        }
    }
    
    private void drawInfo(Graphics2D g2d) {
        if (samples != null) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            
            String info = String.format("Samples: %,d | Duration: %.2fs", 
                samples.length, samples.length / 44100.0); // 44.1kHz 가정
            g2d.drawString(info, 10, 20);
        }
    }
}