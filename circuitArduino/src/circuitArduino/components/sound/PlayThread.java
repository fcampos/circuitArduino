package circuitArduino.components.sound;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import circuitArduino.CirSim;

public class PlayThread extends Thread {
	SourceDataLine line;
	public Waveform wform;
	boolean shutdownRequested;
	boolean stereo;
//	Filter filt, newFilter;
	double fbufLi[];
	double fbufRi[];
	double fbufLo[];
	double fbufRo[];
	double stateL[], stateR[];
	int fbufmask, fbufsize;
	int spectrumOffset, spectrumLen;
	int sampleRate = 22050;
	int lineBufferSize;
	double outputGain = 1;
	static final double pi = 3.14159265358979323846;
	boolean soundCheck = true;
	boolean unstable;
	public Object synchObject= new Object();
	CirSim sim;
	public PlayThread(CirSim sim) {this.sim=sim; shutdownRequested = false; }
	void requestShutdown() { shutdownRequested = true; }
	//void setFilter(Filter f) { newFilter = f; }
	
/*	abstract class Waveform {
		short buffer[];
		CircuitElm elm;
		int sampleCount = 0;
		int shortBuffer = getPower2(sampleRate/12)*getChannels();
		boolean start() { return true; }
		abstract int getData();
		int getChannels() { return 2; }
		void timeStep() {};
		void shuffleBuffer(){};
		void getBuffer() {
			buffer = new short[getPower2(sampleRate/4)*getChannels()];
		}
		int getBufferLength() {
			return shortBuffer;
		}
		int getSampleCount() {
			return sampleCount;
		}
		void setElm(CircuitElm ce) {
			elm = ce;}
		String getInputText() { return "Input Frequency"; }
		boolean needsFrequency() { return true; }
	}*/
/*	class CustomWaveform extends Waveform {
		
		int ix;
		
		double inputW = 2*pi*1000/sampleRate;
		int getChannels() { return 1; }
		void setElm(CircuitElm ce) {
			elm = ce;
			System.out.println(elm.dump());
			sampleCount = 0;
		}
		void timeStep() {
			if (elm!=null){
				if (sampleCount<=buffer.length-1){
			//		System.out.println(sampleCount);
			buffer[sampleCount] = (short) (elm.getScopeValue(0)*32000);
			sampleCount=sampleCount+1;}
				//else
	//		System.out.println("buffer full!!!!");
	//		if (sampleCount>=buffer.length){
	//			System.out.println("buffer full!!!!");
	//			sampleCount = buffer.length-1;
	//		}
			}
		}

		boolean start() {
			getBuffer();
			ix = 0;
			return true;
		}
		
		int getData() {
			return 0;
		};
		//	/*int oldSampleCount = sampleCount;
		//	sampleCount = 0;
			//int i;
			//    System.out.println(buffer.length);
			//for (i = 0; i != buffer.length; i++) {
			//ix++;
			//buffer[i] = (short) (Math.sin(ix*inputW)*32000);

			//	System.out.println(ix);
			//	System.out.println(inputW);
			//	System.out.println(Math.sin(ix*inputW)*32000);
			//}
	//		return oldSampleCount;//buffer.length;
	//	}
		void shuffleBuffer(){
			int i;
			for(i=shortBuffer;i<sampleCount;i++){
				buffer[i-shortBuffer] = buffer[i];
				}
			sampleCount = sampleCount-shortBuffer;
		}
	}*/
	 class SineWaveform extends Waveform {
			int ix;
			double inputW = 2*pi*1000/sampleRate;
			int getChannels() { return 1; }
			boolean start() {
			    getBuffer();
			    ix = 0;
			    return true;
			}
			int getData() {
			    int i;
			//    System.out.println(buffer.length);
			    //for (i = 0; i != buffer.length; i++) {
			    for (i = 0; i < sampleRate/1000 ; i++) {
			    	
			    if (sampleCount+i==buffer.length-1){
			    	break;
			    	
			    }
			    System.out.println("hehe");
			    System.out.println(sampleCount);
		    	System.out.println(buffer.length);
				//ix++;
				buffer[sampleCount+i] = (short) (Math.sin(i*inputW)*32000);
				
			//	System.out.println(ix);
			//	System.out.println(inputW);
			//	System.out.println(Math.sin(ix*inputW)*32000);
			    
			    }
			    sampleCount =sampleCount+i-1;//buffer.length;
			    return sampleCount;//buffer.length;
			}
			void shuffleBuffer(){
				int i;
				for(i=shortBuffer;i<sampleCount;i++){
					buffer[i-shortBuffer] = buffer[i];
					}
				sampleCount = sampleCount-shortBuffer;
			    System.out.println("haha");
			    System.out.println(sampleCount);
			}
		    }
	Waveform getWaveformObject() {
		Waveform wform;
		wform = new CustomWaveform();//SineWaveform();//CustomWaveform();//SineWaveform();
		return wform;
	    }

	int getPower2(int n) {
		int o = 2;
		while (o < n)
			o *= 2;
		return o;
	}
	void openLine() {
		try {
			stereo = (wform.getChannels() == 2);
			AudioFormat playFormat =
					new AudioFormat(sampleRate, 16, 2, true, false);
			DataLine.Info info= new DataLine.Info(SourceDataLine.class,
					playFormat);

			if (!AudioSystem.isLineSupported(info)) {
		    throw new LineUnavailableException(
			"sorry, the sound format cannot be played");
		}
		line = (SourceDataLine)AudioSystem.getLine(info);
		line.open(playFormat, getPower2(sampleRate/4));
		lineBufferSize = line.available();
		line.start();
		System.out.println("line bufferSize " + line.getBufferSize());
		System.out.println("line  available Size " + line.available());
		System.out.println("opened line");
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	int inbp, outbp;
	int spectCt;

	public void run() {
	    try {
	    	System.out.println("doing run") ;
		doRun();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	   // playThread = null;
	}
	
	void doRun() {
	    //rateChooser.enable();
	    wform = getWaveformObject();
	    sim.soundSampleRate = (double)wform.sampleRate;
	 //   mp3Error = null;
	    unstable = false;
	    if (!wform.start()) {
	    	System.out.println("waveform started");
		//cv.repaint();
		try {
		    Thread.sleep(1000L);
		    System.out.println("sleeping") ;
		} catch (Exception e) { }
		return;
	    }

	    fbufsize = 32768;
	    fbufmask = fbufsize-1;
	    fbufLi = new double[fbufsize];
	    fbufRi = new double[fbufsize];
	    fbufLo = new double[fbufsize];
	    fbufRo = new double[fbufsize];
	    openLine();
	    inbp = outbp = spectCt = 0;
	    int ss = (stereo) ? 2 : 1;
	    outputGain = 1;
	  //  if (wform.elm!=null)
	  //  outputGain = wform.elm.volumeGain;
	 //   newFilter = filt = curFilter;
	    spectrumLen = wform.getBufferLength()/2;// getPower2(sampleRate/12);
	    int gainCounter = 0;
	    boolean maxGain = true;
	    boolean useConvolve = false;

	    ob = new byte[16384];
	    int shiftCtr = 0;
	//	System.out.println(wform.elm);
	    while (!shutdownRequested && soundCheck){// &&
	    	// System.out.println(wform.elm);
	    	 synchronized (synchObject) {
					try {
						synchObject.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	    	if (wform.elm!=null&&!sim.stoppedCheck.getState()){
	    	// System.out.println("while") ;
	    		//applet.ogf != null) {
	    	//System.out.println("nf " + newFilter + " " +(inbp-outbp));
	    	//	if (newFilter != null) {
		    gainCounter = 0;
		    maxGain = true;
		  //  if (wform instanceof SweepWaveform ||
		//	wform instanceof SineWaveform)
			maxGain = false;
			outputGain = 1;
		//	System.out.println(wform.elm.volumeGain);
		//	outputGain = wform.elm.volumeGain;
		    // we avoid doing this unless necessary because it sounds bad
		    //if (filt == null || filt.getLength() != newFilter.getLength())
			//convBufPtr = inbp = outbp = spectCt = 0;
		    inbp = outbp = 0;
		    //filt = newFilter;
		    //newFilter = null;
		    //impulseBuf = null;
		    //useConvolve = filt.useConvolve();
		    //stateL = filt.createState();
		    //stateR = filt.createState();
		//}*/
		   
		    // DO THIS FOR SINE WAVEFORM
		    //wform.getData();
		    
		    
		    //wform.getSampleCount();
		    //wform.getBufferLength();
		   // if (wform.elm!=null){
		 //   System.out.println("wow_wow");
		//    System.out.println(wform.getSampleCount());
			   
		  // System.out.println(wform.getBufferLength());
		 //  System.out.println("line  available Size " + line.available());
		//   System.out.println(wform.elm);
		   if (line.available()==lineBufferSize)
			   System.out.println("unstable");
		    //}
		  /*  try {
			    Thread.sleep(10L);
			   // System.out.println("sleeping") ;
			} catch (Exception e) { }*/
		    if (wform.getSampleCount()>=wform.getBufferLength()){
		    	int length = wform.getBufferLength();//wform.getData();
		 synchronized  (synchObject) {
		   // 	System.out.println("outputing outputing outputing outputing outputing outputing outputing outputing outputing");
	    	
	    	if (length == 0)
	    		break;
	    	short ib[] = wform.buffer;

	    	int i2;
	    	int i = inbp;
	    	for (i2 = 0; i2 < length; i2 += ss) {
	    	//	System.out.println(ib[i2]);
	    		fbufLi[i] = ib[i2];
	    		i = (i+1) & fbufmask;
	    	}
	    	i = inbp;
	    	if (stereo) {
	    		for (i2 = 0; i2 < length; i2 += 2) {
	    			fbufRi[i] = ib[i2+1];
	    			i = (i+1) & fbufmask;
	    		}
	    	} else {
	    		for (i2 = 0; i2 < length; i2++) {
	    			fbufRi[i] = fbufLi[i];
	    			i = (i+1) & fbufmask;
	    		}
	    	}
	    	wform.shuffleBuffer();
		 }
	    	/*	if (shiftSpectrumCheck.getState()) {
		    double shiftFreq = shiftFreqBar.getValue()*pi/1000.;
		    if (shiftFreq > pi)
			shiftFreq = pi;
		    i = inbp;
		    for (i2 = 0; i2 < length; i2 += ss) {
			double q = Math.cos(shiftFreq*shiftCtr++);
			fbufLi[i] *= q;
			fbufRi[i] *= q;
			i = (i+1) & fbufmask;
		    }
		}*/
	    	//    WHY THIS?
	    	int sampleCount = length/ss;
	    	
	    	
	    	/*if (useConvolve)
		    doConvolveFilter(sampleCount, maxGain);
		else {
		    doFilter(sampleCount);
		    if (unstable)
			break;
		    int outlen = sampleCount*4;
		    doOutput(outlen, maxGain);
		}*/
	    	if (unstable){
	    		System.out.println("breaking");
	    	
	    		break;
	    	}	
	    	int outlen = sampleCount*4;
	    	//System.out.println(outlen) ;
	    	doOutput(outlen, maxGain);

	    	if (unstable)
	    		break;

	   /* 	if (spectCt >= spectrumLen) {
	    		spectrumOffset = (outbp-spectrumLen) & fbufmask;
	    		spectCt -= spectrumLen;
	    		//    cv.repaint();
	    	}*/
	    	gainCounter += sampleCount;
	    	if (maxGain && gainCounter >= sampleRate) {
	    		gainCounter = 0;
	    		maxGain = false;
	    		//System.out.println("gain ctr up " + outputGain);
	    	}
		    }
		  
	    }
	    	}
	    System.out.println("out of while");
	    if (shutdownRequested || unstable)// || !soundCheck.getState())
	    	line.flush();
	    else{
	    	line.drain();
	    	}
	    // cv.repaint();
	}
	/*
	void doFilter(int sampleCount) {
	    filt.run(fbufLi, fbufLo, inbp, fbufmask, sampleCount, stateL);
	    filt.run(fbufRi, fbufRo, inbp, fbufmask, sampleCount, stateR);
	    inbp = (inbp+sampleCount) & fbufmask;
	    double q = fbufLo[(inbp-1) & fbufmask];
	    if (Double.isNaN(q) || Double.isInfinite(q))
		unstable = true;
	}
	 */
	/*double impulseBuf[], convolveBuf[];
	int convBufPtr;
	FFT convFFT;

	void doConvolveFilter(int sampleCount, boolean maxGain) {
		int i;
		int fi2 = inbp, i20;
		double filtA[] = ((DirectFilter) filt).aList;
		int cblen = getPower2(512+filtA.length*2);
		if (convolveBuf == null || convolveBuf.length != cblen)
			convolveBuf = new double[cblen];
		if (impulseBuf == null) {
			// take FFT of the impulse response
			impulseBuf = new double[cblen];
			for (i = 0; i != filtA.length; i++)
				impulseBuf[i*2] = filtA[i];
			convFFT = new FFT(convolveBuf.length/2);
			convFFT.transform(impulseBuf, false);
		}
		int cbptr = convBufPtr;
		// result = impulseLen+inputLen-1 samples long; result length
		// is fixed, so use it to get inputLen
		int cbptrmax = convolveBuf.length+2-2*filtA.length;
		//System.out.println("reading " + sampleCount);
		for (i = 0; i != sampleCount; i++, fi2++) {
			i20 = fi2 & fbufmask;
			convolveBuf[cbptr  ] = fbufLi[i20];
			convolveBuf[cbptr+1] = fbufRi[i20];
			cbptr += 2;
			if (cbptr == cbptrmax) {
				// buffer is full, do the transform
				convFFT.transform(convolveBuf, false);
				double mult = 2./cblen;
				int j;
				// multiply transforms to get convolution
				for (j = 0; j != cblen; j += 2) {
					double a = convolveBuf[j]*impulseBuf[j] -
							convolveBuf[j+1]*impulseBuf[j+1];
					double b = convolveBuf[j]*impulseBuf[j+1] +
							convolveBuf[j+1]*impulseBuf[j];
					convolveBuf[j]   = a*mult;
					convolveBuf[j+1] = b*mult;
				}
				// inverse transform to get signal
				convFFT.transform(convolveBuf, true);
				int fj2 = outbp, j20;
				int overlap = cblen-cbptrmax;
				// generate output that overlaps with old data
				for (j = 0; j != overlap; j += 2, fj2++) {
					j20 = fj2 & fbufmask;
					fbufLo[j20] += convolveBuf[j];
					fbufRo[j20] += convolveBuf[j+1];
				}
				// generate new output
				for (; j != cblen; j += 2, fj2++) {
					j20 = fj2 & fbufmask;
					fbufLo[j20] = convolveBuf[j];
					fbufRo[j20] = convolveBuf[j+1];
				}
				cbptr = 0;
				// output the sound
				doOutput(cbptrmax*2, maxGain);
				//System.out.println("outputting " + cbptrmax);
				// clear transform buffer
				for (j = 0; j != cblen; j++)
					convolveBuf[j] = 0;
			}
		}
		inbp = fi2 & fbufmask;
		convBufPtr = cbptr;
	}
	 */
	byte ob[];

	void doOutput(int outlen, boolean maxGain) {
	//	System.out.println("doing Output");
	    if (ob.length < outlen)
		ob = new byte[outlen];
	    int qi;
	    int i, i2;
	    while (true) {
		int max = 0;
		i = outbp;
		for (i2 = 0; i2 < outlen; i2 += 4) {
		//	System.out.println(fbufLi[i]);
		    qi = (int) (fbufLi[i]*outputGain);
		    if (qi > max)  max = qi;
		    if (qi < -max) max = -qi;
		   // System.out.println(fbufLi[i]);
		//	System.out.println(qi);
			ob[i2+1] = (byte) (qi>>8);
		    ob[i2] = (byte) qi;
		    i = (i+1) & fbufmask;
		}
		i = outbp;
		for (i2 = 2; i2 < outlen; i2 += 4) {
		    qi = (int) (fbufRi[i]*outputGain);
		    if (qi > max)  max = qi;
		    if (qi < -max) max = -qi;
		    ob[i2+1] = (byte) (qi>>8);
		    ob[i2] = (byte) qi;
		    i = (i+1) & fbufmask;
		}
		// if we're getting overflow, adjust the gain
		if (max > 32767) {
		    //System.out.println("max = " + max);
		    outputGain *= 30000./max;
		    if (outputGain < 1e-8 || Double.isInfinite(outputGain)) {
			unstable = true;
			break;
		    }
		    continue;
		} else if (maxGain && max < 24000) {
			System.out.println(maxGain);
		    if (max == 0) {
			if (outputGain == 1)
			    break;
			outputGain = 1;
		    } else
			outputGain *= 30000./max;
		    continue;
		}
		break;
	    }
	    if (unstable)
		return;
	    int oldoutbp = outbp;
	    outbp = i;
	    
	    line.write(ob, 0, outlen);
	    spectCt += outlen/4;
	}
    }
