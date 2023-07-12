package circuitArduino.components.sound;
//import circuitMatlab.CircuitElm;

import circuitArduino.components.passive.SpeakerElm;

public abstract class Waveform {
	int sampleRate = 22050;
	public short buffer[];
	public SpeakerElm elm;
	public int sampleCount = 0;
	public int shortBuffer = getPower2(sampleRate/12)*getChannels();
	boolean start() { return true; }
	abstract int getData();
	int getChannels() { return 2; }
	public void timeStep(double value) {};
	void shuffleBuffer(){};
	void getBuffer() {
		buffer = new short[getPower2(sampleRate*4)*getChannels()];
	}
	public int getBufferLength() {
		return shortBuffer;
	}
	public int getSampleCount() {
		return sampleCount;
	}
	public void setElm(SpeakerElm ce) {
		elm = (SpeakerElm) ce;}
	String getInputText() { return "Input Frequency"; }
	boolean needsFrequency() { return true; }
	
	int getPower2(int n) {
		int o = 2;
		while (o < n)
			o *= 2;
		return o;
	}
}
