package circuitArduino.components.arduino;

import java.util.Arrays;

import javax.swing.JOptionPane;

import com.kollins.project.sofia.UCModule;
//import com.bamless.interpreter.Main;
import com.kollins.project.sofia.atmega328p.iomodule_atmega328p.output.OutputFragment_ATmega328P;

import circuitArduino.CirSim;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.io.File;

import com.kollins.project.sofia.atmega328p.iomodule_atmega328p.IOModule_ATmega328P;
//import com.dandigit.jlox.Lox;
public class Arduino {
//public Lox interpreter;
public UCModule ucModule;
public String urls;
public double arduinoST = 1/16E6; //16E6 is the base frequency of arduino - does it depend on the arduino board?; //arduino sample time
public boolean existsFlag = false;
public boolean pwmAverage = true;
public int prescaler = 1;
public OutputFragment_ATmega328P simulatorOutputs;
public int counter=0;
//public boolean doneInit = false;
CirSim sim;
public boolean[] pinMode = new boolean[14];
	public Arduino(CirSim sim, String urls) {
		// TODO Auto-generated constructor stub
	//	PinMode[0]=true;
	//	PinMode[1]=true;
		this.urls = urls;
		System.out.println(urls);
		this.sim = sim;
	//	interpreter = new Lox();
		init();
		/*if (sim.timeStep!= arduinoST) {
			JOptionPane.showMessageDialog(sim.matlabFrame,
				    "Simulation sample time is being set to arduino sample time " + arduinoST+".");
			sim.timeStep= arduinoST;
		}*/
			
		 
	}
public	void reload(){
	
	init();
	
	if (sim.timeStep!= arduinoST) {
		JOptionPane.showMessageDialog(sim.matlabFrame,
			    "Simulation sample time is being set to arduino sample time " + arduinoST+".");
		sim.timeStep= arduinoST;
	}
	//sim.needAnalyze();
	/*	System.out.println("reloading");
		if (existsFlag)
		terminate();
		interpreter.arduino.reset();
		init();*/
	}
public void init(){
	urls = sim.sketchURL;
	System.out.println("initializing interpreter...");
	ucModule = new UCModule(false);//(false);
	ucModule.main(urls);
	System.out.println("Project Sofia");
	System.out.println("setting up UC");
	ucModule.setUpUc();
	
	 //cycle through program to initialize registers
	// we blindly (i.e. independently of the circuit simulation) run the cycle for a 1000? iterations in the hope that at the end the timers are configured and ports are set as inputs/outputs
	 for (int j=0; j<100000;j++)
		 ucModule.cycle();
	 System.out.println("prescaler: "+  ucModule.getPreScaler());
	 prescaler =ucModule.getPreScaler();
	 /*switch (ucModule.getPreScaler()) {
	 case 1: prescaler = 1;break;
	 case 2: prescaler = 8;break;
	 case 3: prescaler = 64;break;
	 case 4: prescaler = 256;break;
	 case 5: prescaler = 1024;
	 }*/
	 System.out.println("Pin modes:");
	 for (int i=0;i<pinMode.length;i++) {
		 pinMode[i] = ucModule.getPinMode(i)?true:false;
		 System.out.print(ucModule.getPinMode(i)+" ");
	 }
	 System.out.println(" ");
	 // restart to start from time 0
	 
	 ucModule.main(urls);
	 
	ucModule.setUpUc();
	 ucModule.setIncrement(prescaler);
	 simulatorOutputs = (OutputFragment_ATmega328P)ucModule.get_UCView().outputFragment	;
	 arduinoST = (((double)prescaler)/16.0E6); //16E6 is the base frequency of arduino - does it depend on the arduino board?
	
	existsFlag= true;
	
	
	
	/*urls[0]=sim.sketchURL;
	//synchronized (interpreter.arduino.synchObject) {
		interpreter.start(urls);
		while (!interpreter.doneInit)
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		/*try {
			while (!interpreter.doneInit)
			interpreter.arduino.synchObject.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("HAD ERRORRRR.");
		
		}
	//}
	System.out.println("DONE initializing interpreter...");
	interpreter.doneInit = false;*/
}
public void terminate(){
/*	System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::TERMINATING");
	interpreter.exitFlag = true;
	while (interpreter.getThreadState()!=Thread.State.TERMINATED){
		synchronized (interpreter.arduino.synchObject) {
			interpreter.arduino.synchObject.notify();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	existsFlag= false;*/
	existsFlag= false;
}
public String getSourceFile()
{ String sourceFile = "";
Object[] filesInFolder;


File dummyFile = new File(sim.sketchURL);
//Path folder = dummyFile.toPath().getParent();

try {
	Stream<Path> path = Files.walk(dummyFile.toPath().getParent());
	System.out.println("Parent: " + dummyFile.toPath().getParent().toString());
	path = path.filter(var -> var.toString().endsWith(".ino"));
	filesInFolder  =  path.toArray();
	System.out.println("filesInFolder: " + filesInFolder[0]);
	if (filesInFolder[0]!=null)
		return  String.valueOf(filesInFolder[0]);//path.peek.toString();    //filesInFolder[0];
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
return sourceFile;
}
public boolean exists(){ return existsFlag;}
public void cycle(){
	counter++;
	ucModule.cycle();
	//System.out.println("cycling");
	//if (counter%(256*250)==0)
	//	System.out.println("counter "+counter);
	/*
	//arduino.PinMode=Arrays.copyOf(arduino.interpreter.arduino.PinMode, arduino.PinMode.length);
	//System.out.println("cycling arduino-----------------------------------");
	synchronized (interpreter.arduino.synchObject) {
		interpreter.arduino.simulationTime=sim.t;
		interpreter.arduino.serialMessage = null;
		if (!interpreter.arduino.pulsing)
		if (interpreter.arduino.pausing)
		{/*System.out.println("is pausing");
		System.out.println(arduino.interpreter.arduino.simulationTime);
		System.out.println(arduino.interpreter.arduino.delayStart);
		System.out.println(arduino.interpreter.arduino.delay);
		System.out.println(t);
		
			if (interpreter.arduino.delayStart+interpreter.arduino.delay/1000<sim.t)
			{
				interpreter.arduino.pausing=false;
				interpreter.arduino.synchObject.notify();}}
		else
		interpreter.arduino.synchObject.notify();
	}
	if (interpreter.hadError|interpreter.hadRuntimeError)
		sim.stop(interpreter.runtimeErrorMsg + interpreter.compileErrorMsg, null);
	*/
	
		
}
}
