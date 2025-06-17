package daw.main.component.track;

public class Note {
	private int key;
	private int startTime;
	private int endTime;
	
	public Note(int key, int startTime, int endTime) {
		this.key = key;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public int getKey() {
		return key;
	}
	public int getStartTime() {
		return startTime;
	}
	public int getEndTime() {
		return endTime;
	}
}