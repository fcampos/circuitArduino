package circuitArduino.components.sound;

import circuitArduino.components.passive.SpeakerElm;

class CustomWaveform extends Waveform {
	
	int ix;
	static final double pi = 3.14159265358979323846;
	double inputW = 2*pi*1000/sampleRate;
	int getChannels() { return 1; }
	public CustomWaveform() {   }
	public void setElm(SpeakerElm ce) {
		elm = ce;
		System.out.println(elm.dump());
		sampleCount = 0;
	}
	public void timeStep(double value) {
		if (elm!=null){
			if (sampleCount<buffer.length){
		//		System.out.println(sampleCount);
		buffer[sampleCount] =  (short) (elm.volumeGain*value*32000) ;//(elm.getVoltageDiff()*32000);
		sampleCount=sampleCount+1;
	//	System.out.println("written!!!!");
		}
			//else
//		System.out.println("buffer full!!!!");
		if (sampleCount>=buffer.length){
			System.out.println("buffer full!!!!");
		//	sampleCount = buffer.length-1;
		}
		}
	}
	/*void timeStep() {
		if (elm!=null){
			if (sampleCount<=buffer.length-1){
		//		System.out.println(sampleCount);
		buffer[sampleCount] = (short) (elm.getScopeValue(0)*32000);
		sampleCount=sampleCount+1;}
			//else
//		System.out.println("buffer full!!!!");
		if (sampleCount>=buffer.length){
			System.out.println("buffer full!!!!");
			sampleCount = buffer.length-1;
		}
		}
	}*/

	boolean start() {
		getBuffer();
		ix = 0;
		return true;
	}
	
	int getData() {
		return 0;
	};
		/*int oldSampleCount = sampleCount;
		sampleCount = 0;
		//int i;
		//    System.out.println(buffer.length);
		//for (i = 0; i != buffer.length; i++) {
		//ix++;
		//buffer[i] = (short) (Math.sin(ix*inputW)*32000);

		//	System.out.println(ix);
		//	System.out.println(inputW);
		//	System.out.println(Math.sin(ix*inputW)*32000);
		//}
		return oldSampleCount;//buffer.length;
	}*/
	void shuffleBuffer(){
		int i;
		for(i=shortBuffer;i<sampleCount;i++){
			buffer[i-shortBuffer] = buffer[i];
			}
		sampleCount = sampleCount-shortBuffer;
	}
}
