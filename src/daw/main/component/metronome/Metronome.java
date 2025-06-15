package daw.main.component.metronome;

import daw.main.component.metronome.Metronome.BEAT_TYPE;

public class Metronome {
	public static enum BEAT_TYPE {
		BEAT_4_ON_4,
		BEAT_3_ON_4,
	}
	public static final int DEFAULT_TEMPO = 100;
	public static final BEAT_TYPE DEFAULT_BEAT_TYPE = BEAT_TYPE.BEAT_4_ON_4;
	
	private BEAT_TYPE beatType;
	private int tempo;
	
	public Metronome() {
		this.beatType = this.DEFAULT_BEAT_TYPE;
		this.tempo = this.DEFAULT_TEMPO;
	}
	
	public Metronome(BEAT_TYPE beatType, int tempo) {
		this.beatType = beatType;
		this.tempo = tempo;
	}
	
	public BEAT_TYPE getBeatType() {
		return this.beatType;
	}
	public int getTempo() {
		return this.tempo;
	}
}
