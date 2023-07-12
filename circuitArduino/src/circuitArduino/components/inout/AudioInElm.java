package circuitArduino.components.inout;

import java.util.StringTokenizer;

import circuitArduino.SelectFileDialog;
import circuitArduino.UI.EditInfo;
import circuitArduino.components.sound.WavFile;

import java.awt.*;
import java.io.File;
import java.net.URL;

public class AudioInElm extends RailElm {
	
	double timeOffset;
	int samplingRate;
	int fileNum;
	String fileName;
	double maxVoltage;
	double startPosition;
	int currentFrame=0;
	double currentFrameDouble = 0;
	double duration = 0;
	int maxFrame=0;
	double[][] buffer;
	static int lastSamplingRate;
	WavFile wavFile;
	double gain=1;
	// cache to preserve audio data when doing cut/paste, or undo/redo
	static int fileNumCounter = 1;
	
public AudioInElm(int xx, int yy) {
    super(xx, yy, WF_AC);
    maxVoltage = 5;
  //  URL url;
    try {
		if (!sim.applet.startFolder.isEmpty()){
    		System.out.println("file:///" + sim.applet.startFolder);
    		fileName =  sim.applet.startFolder+ File.separator+"Guitar.wav";}
    	else{
    		fileName = System.getProperty("user.dir")+ File.separator+ "Guitar.wav";
	   //fileName = url.toString();
    		  System.out.println(fileName);
	   readWavFile (fileName );
	   };//
    } catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't read default wav file!");
	}
}

public AudioInElm(int xa, int ya, int xb, int yb, int f,
	       StringTokenizer st) {
    super(xa, ya, xb, yb, f, st);
    waveform = WF_AC;
 //   maxVoltage = Double.parseDouble(st.nextToken());
 //   startPosition = Double.parseDouble(st.nextToken());
    gain = Double.parseDouble(st.nextToken());
    fileName = st.nextToken();
    try {
		   readWavFile (fileName );
    } catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Can't read default wav file!");
	}
   reset();
   
}

double fmphase;

protected String dump() {
    // add a file number to the dump so we can preserve the audio file data when doing cut and paste, or undo/redo.
    // we don't save the entire file in the dump because it would be huge.
   
    return super.dump() + " "  + gain + " "  + fileName;
}

protected void reset() {
    timeOffset = startPosition;
    currentFrame = 0;
    currentFrameDouble = 0;
}

void drawRail(Graphics g) {
  //  drawRailText(g, fileName == null ? "No file" : fileName);
}

String getRailText() {
    return fileName == null ? "No file" : fileName;
}

void setSamplingRate(int sr) {
    samplingRate = sr;
}

double getVoltage() {
   /* if (data == null)
	return 0;
    if (timeOffset < startPosition)
	timeOffset = startPosition;
    int ptr = (int) (timeOffset * samplingRate);
    if (ptr >= data.length()) {
	ptr = 0;
	timeOffset = 0;
    }
    return data.get(ptr) * maxVoltage;*/
	
	if (wavFile!=null){
		//System.out.println(duration);
	//	System.out.println(maxFrame);
		currentFrameDouble = (sim.t % duration)*(double)samplingRate ; // currentFrameDouble + (sim.timeStep*(double)samplingRate);
		//System.out.println(sim.timeStep);
		//System.out.println((int)(sim.timeStep*(double)samplingRate));
		//System.out.println(sim.t);
		//System.out.println(sim.t % duration);
		//System.out.println(samplingRate);
		if (currentFrameDouble>maxFrame)
			currentFrameDouble = 0;
		//System.out.println(buffer[0][currentFrame]);
	//	System.out.println(gain);
		return gain*buffer[0][(int)currentFrameDouble];
	}
	else 
	{
		
		return 0.0;
	
	}
}

void stepFinished() {
    timeOffset += sim.timeStep;
}

protected int getDumpType() { return 212; }
int getShortcut() { return 0; }

public void  readWavFile (String sfileName )
{
	try
	{
		// Open the wav file specified as the first argument
    wavFile = WavFile.openWavFile(new File(sfileName));

	// Display information about the wav file
	wavFile.display();
samplingRate = (int)wavFile.getSampleRate();
	// Get the number of audio channels in the wav file
	int numChannels = wavFile.getNumChannels();
	maxFrame = (int)wavFile.getNumFrames();
	duration = (double)maxFrame/(double)samplingRate;
	// Create a buffer of 100 frames
	 buffer = new double[2][(int)wavFile.getNumFrames()];

	int framesRead;
	double min = Double.MAX_VALUE;
	double max = Double.MIN_VALUE;

	do
	{
		// Read frames into buffer
		framesRead = wavFile.readFrames(buffer, maxFrame);

		// Loop through frames and look for minimum and maximum value
		for (int s=0 ; s<framesRead ; s++)
		{
			if (buffer[0][s] > max) max = buffer[0][s];
			if (buffer[0][s] < min) min = buffer[0][s];
		}
	}
	while (framesRead != 0);

	// Close the wavFile
	wavFile.close();

	
	
	// Output the minimum and maximum value
	System.out.printf("Min: %f, Max: %f\n", min, max);
	System.out.println("Sampling rate " + samplingRate);
	//System.out.printf("Sampling rate%f \n", samplingRate);
	System.out.println(buffer[0][20000] +" "+ buffer[0][40000]);
}
catch (Exception e)
{
	System.err.println(e);
}
}
public EditInfo getEditInfo(int n) {
       /* if (n == 0) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            SelectFileDialog selFileDialog = new SelectFileDialog(sim);
            fileName =sim.selectedFile;
            readWavFile (fileName);
			
            return ei;
        }*/
        if (n == 0) {
		    EditInfo ei = new EditInfo("Name", 0, -1, -1);
		    ei.text = fileName;
		    return ei;
		}
       /* if (n == 1)
            return new EditInfo("Max Voltage", maxVoltage, 0.0 ,5.0);
        if (n == 2)
            return new EditInfo("Start Position (s)", startPosition, 0.0, 10000.0);*/
        if (n == 1) {
		    EditInfo ei = new EditInfo("", 0, -1, -1);
		    ei.button = new Button("Select file");
		    return ei;
		}
        if (n == 2)
            return new EditInfo("Scale factor", gain, 0.0, 10);
    return null;
}

public void setEditValue(int n, EditInfo ei) {
	if (n == 0) {
	    fileName = ei.textf.getText();
	  //  split();
	}
	if (n == 1) {
		 SelectFileDialog selFileDialog = new SelectFileDialog(sim);
         fileName =sim.selectedFile;
         readWavFile (fileName);
	  //  split();
	}
    /*if (n == 1)
	maxVoltage = ei.value;;*/
    if (n == 2)
	gain = ei.value;
}
/*
// fetch audio data for a selected file
static native String fetchLoadFileData(AudioInputElm elm, Element uploadElement) /*-{
    var oFiles = uploadElement.files;
   	    var context = new (window.AudioContext || window.webkitAudioContext)();
   	    elm.@com.lushprojects.circuitjs1.client.AudioInputElm::setSamplingRate(I)(context.sampleRate);
    if (oFiles.length >= 1) {
                    var reader = new FileReader();
                    reader.onload = function(e) {
            		context.decodeAudioData(reader.result, function(buffer) {
                			var data = buffer.getChannelData(0); 
                			elm.@com.lushprojects.circuitjs1.client.AudioInputElm::gotAudioData(*)(data);
            		},
            		function(e){ console.log("Error with decoding audio data" + e.err); });
                    };

                    reader.readAsArrayBuffer(oFiles[0]);
    }
}-*/ //;
/*
void gotAudioData(JsArrayNumber d) {
    data = d;
    lastSamplingRate = samplingRate;
    AudioOutputElm.lastSamplingRate = samplingRate;
}

void getInfo(String arr[]) {
    arr[0] = "audio input";
    if (data == null) {
	arr[1] = "no file loaded";
	return;
    }
    arr[1] = "V = " + getVoltageText(volts[0]);
    arr[2] = "pos = " + getUnitText(timeOffset, "s");
    double dur = data.length() / (double)samplingRate;
    arr[3] = "dur = " + getUnitText(dur, "s");
}

public static void clearCache() {
    audioFileMap.clear();
}
*/
}
