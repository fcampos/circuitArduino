/*
 * Copyright 2018
 * Kollins Lima (kollins.lima@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kollins.project.sofia;
//import circuitMatlab.Arduino;
/*
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
*/
//import android.content.res.Resources;
import com.kollins.project.sofia.atmega328p.ADC_ATmega328P;
import com.kollins.project.sofia.atmega328p.ProgramMemory_ATmega328P;
import com.kollins.project.sofia.atmega328p.DataMemory_ATmega328P;
import com.kollins.project.sofia.atmega328p.Timer0_ATmega328P;
import com.kollins.project.sofia.atmega328p.Timer1_ATmega328P;
import com.kollins.project.sofia.atmega328p.Timer2_ATmega328P;
import com.kollins.project.sofia.atmega328p.USART_ATmega328P;
//import com.kollins.project.sofia.atmega328p.ADC_ATmega328P;
import com.kollins.project.sofia.atmega328p.InterruptionModule_ATmega328P;
import com.kollins.project.sofia.atmega328p.iomodule_atmega328p.output.OutputFragment_ATmega328P;
import com.kollins.project.sofia.ucinterfaces.DataMemory;
import com.kollins.project.sofia.ucinterfaces.InterruptionModule;
//import com.kollins.project.sofia.ucinterfaces.OutputFragment;
//import com.kollins.project.sofia.ucinterfaces.ProgramMemory;
import com.kollins.project.sofia.ucinterfaces.Timer0Module;
import com.kollins.project.sofia.ucinterfaces.Timer1Module;
import com.kollins.project.sofia.ucinterfaces.Timer2Module;
import com.kollins.project.sofia.ucinterfaces.USARTModule;

import java.util.Arrays;


/**
 * Created by kollins on 3/7/18.
 * Modified by Francisco Campos
 */

public class UCModule  {

    //To calculate efective clock
    public static int sum, n = 0;
    private static long time1 = 0, time2 = 0;

    //// private static Context context;

    public static String PACKAGE_NAME;

    public static final String SETTINGS = "Settings";
    public static final String MODEL_SETTINGS = "ArduinoModel";
    public static final String START_PAUSED_SETTINGS = "StartPaused";
    public static final String AREF_SETTINGS = "AREF";

    //Default location
//    public static final String DEFAULT_HEX_LOCATION = Environment.getExternalStorageDirectory().getPath();
      private String hexFileLocation = "C:\\Users\\FranciscoMateus\\workspace\\circuitMatlab\\arduino\\Blink\\Blink.ino.hex";

    //Default device
    public static String device;
    public static String model;
    private static int numberOfModules;

   // public static Resources resources;

    public static final int RESET_ACTION = 0;
    public static final int CLOCK_ACTION = 1;
    public static final int SHORT_CIRCUIT_ACTION = 2;
    public static final int STOP_ACTION = 3;
    public static final int PAUSE_ACTION = 4;
    public static final int RESUME_ACTION = 5;
    public static final int UPDATE_SETTINGS_ACTION = 6;

    public static final String MY_LOG_TAG = "LOG_SIMULATOR";

    public static InterruptionModule interruptionModule;

    private DataMemory dataMemory;
    private ProgramMemory_ATmega328P programMemory;

    private CPUModule cpuModule;

    public Timer0Module timer0;

    private Timer1Module timer1;

    private Timer2Module timer2;

    public ADC_ATmega328P adc;

    private USARTModule usart;
    public static String serialBuffer = "";
    public static StringBuffer serialBufferOut ;
  //  private UCHandler uCHandler;

    private boolean resetFlag;
	private static boolean updateScreenFlag;
    private static boolean shortCircuitFlag;

    public static boolean setUpSuccessful;

  ////  private FrameLayout frameIO;
    private UCModule_View ucView;
    private Thread threadUCView;

    private Thread threadScheduler;

    private boolean firstLoadFileFail = true;

    private static boolean startPaused = false;
    private boolean pauseSimulation = startPaused;
public boolean standAloneFlag = true;

private static final int[] memoryAddress = getInputMemoryAddress();
private static final int[] memoryBitPosition = getInputMemoryBitPosition();
    public UCModule_View get_UCView(){ return ucView;}
    public UCModule(boolean standAloneFlag)
    {
    	this.standAloneFlag = standAloneFlag;
    }
    public  void main(String args){
    	
    	hexFileLocation = args;
    	startPaused = false;//settings.getBoolean(START_PAUSED_SETTINGS, false);
        ADC_ATmega328P.AREF = 5000;//(short) settings.getInt(AREF_SETTINGS, 5000);
        model = "UNO";//settings.getString(MODEL_SETTINGS, "UNO");
/*    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        // Ask for permission to use external storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        //Load device settings
        SharedPreferences settings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        startPaused = settings.getBoolean(START_PAUSED_SETTINGS, false);
        ADC_ATmega328P.AREF = (short) settings.getInt(AREF_SETTINGS, 5000);
        model = settings.getString(MODEL_SETTINGS, "UNO");

        context = getApplicationContext();

        PACKAGE_NAME = getApplicationContext().getPackageName();
        resources = getResources();
*/
     //   uCHandler = new UCHandler();

       // setTitle("Arduino " + model);
        PACKAGE_NAME = "com.kollins.project.sofia";//getApplicationContext().getPackageName();
        device = getDevice(model);
        numberOfModules = getDeviceModules() + 1;

        setUpSuccessful = false;
        shortCircuitFlag = false;
        updateScreenFlag = false;

        ucView = new UCModule_View();

     /*   frameIO = (FrameLayout) findViewById(R.id.fragmentIO);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.fragmentIO, ucView, OutputFragment.TAG_OUTPUT_FRAGMENT);
        ft.commit();

        ucView.setUCHandler(uCHandler);
        */
        ucView.setUCDevice(this);


        // Class interruptionDevice = Class.forName(PACKAGE_NAME + "." + device.toLowerCase() + ".InterruptionModule_" + device);
         // interruptionModule = (InterruptionModule) interruptionDevice.newInstance();
		interruptionModule = new InterruptionModule_ATmega328P();


//System.out.println("Exited");
//System.exit(0);
 
    }
   // @Override
    protected void onResume() {
      //  super.onResume();
        reset();
    }

   // @Override
    protected void onPause() {
     //   super.onPause();
        stopSystem();
    }

    public void setUpUc() {
      //  Log.i(MY_LOG_TAG, "SetUp");
        System.out.println(MY_LOG_TAG + "SetUp");

        if (shortCircuitFlag  && ucView.getIOModule().checkShortCircuit()) {
            return;
        }

        shortCircuitFlag = false;
        pauseSimulation = startPaused;
        setResetFlag(false);
        serialBufferOut = new StringBuffer("");
//        resetClockVector();

        //Init FLASH
		//Class programMemoryDevice = Class.forName(PACKAGE_NAME + "." + device.toLowerCase() + ".ProgramMemory_" + device);
		programMemory = new ProgramMemory_ATmega328P();
		//(ProgramMemory) programMemoryDevice
		        //.getDeclaredConstructor(Handler.class)
		        //.newInstance(uCHandler);

		System.out.println(MY_LOG_TAG+ "Flash size: " + programMemory.getMemorySize());

		if (programMemory.loadProgramMemory(  hexFileLocation )) {//hexFileLocation, getContentResolver())) {
		    //hexFile read Successfully

		    programMemory.setPC(0);
		 //   ((TextView) findViewById(R.id.hexFileErrorInstructions)).setVisibility(View.GONE);
		    System.out.println(" going Init RAM Init RAM Init RAM Init RAM");
		    //Init RAM
/*System.out.println(PACKAGE_NAME + "." + device.toLowerCase() + ".DataMemory_" + device);
		    Class dataMemoryDevice = Class.forName(PACKAGE_NAME + "." + device.toLowerCase() + ".DataMemory_" + device);
		    dataMemory = (DataMemory) dataMemoryDevice.getDeclaredConstructor(IOModule.class)
		            .newInstance(ucView.getIOModule());*/
		    dataMemory = new DataMemory_ATmega328P(ucView.getIOModule());
              ucView.getIOModule().getPINConfig();

		    System.out.println(MY_LOG_TAG + "SDRAM size: " + dataMemory.getMemorySize());

		    ucView.setMemoryIO(dataMemory);
		    interruptionModule.setMemory(dataMemory);

		    //Init CPU
		    cpuModule = new CPUModule(programMemory, dataMemory);

		    //Init Timer0
		    System.out.println(PACKAGE_NAME + "." + device.toLowerCase() + ".Timer0_" + device);
		   /* Class timer0Device = Class.forName(PACKAGE_NAME + "." + device.toLowerCase() + ".Timer0_" + device);
		    timer0 = (Timer0Module) timer0Device.getDeclaredConstructor(DataMemory.class, IOModule.class)
		            .newInstance(dataMemory, ucView.getIOModule());*/
		    timer0  = new Timer0_ATmega328P(dataMemory,ucView.getIOModule());
		    //Init Timer1
		   /* Class timer1Device = Class.forName(PACKAGE_NAME + "." + device.toLowerCase() + ".Timer1_" + device);
		    timer1 = (Timer1Module) timer1Device.getDeclaredConstructor(DataMemory.class, IOModule.class)
		            .newInstance(dataMemory, ucView.getIOModule());*/
		    timer1  = new Timer1_ATmega328P(dataMemory,ucView.getIOModule());
		    //Init Timer2
		  /*  Class timer2Device = Class.forName(PACKAGE_NAME + "." + device.toLowerCase() + ".Timer2_" + device);
		    timer2 = (Timer2Module) timer2Device.getDeclaredConstructor(DataMemory.class, IOModule.class)
		            .newInstance(dataMemory, ucView.getIOModule());*/
		    timer2  = new Timer2_ATmega328P(dataMemory,ucView.getIOModule());
		    //Init ADC
		    System.out.println(PACKAGE_NAME + "." + device.toLowerCase() + ".ADC_" + device);
		  /*  Class adcDevice = Class.forName(PACKAGE_NAME + "." + device.toLowerCase() + ".ADC_" + device);
		    adc = (ADCModule) adcDevice.getDeclaredConstructor(DataMemory.class)
		            .newInstance(dataMemory);*/
		    adc = new ADC_ATmega328P(dataMemory);
		    //Init USART
		   /* Class usartDevice = Class.forName(PACKAGE_NAME + "." + device.toLowerCase() + ".USART_" + device);
		    usart = (USARTModule) usartDevice.getDeclaredConstructor(DataMemory.class)
		            .newInstance(dataMemory);*/
		    usart = new  USART_ATmega328P(dataMemory);
		    setUpSuccessful = true;
		  if (pauseSimulation) {
		      //  ucView.setStatus(UCModule_View.LED_STATUS.PAUSED);
		      //  ucView.resetSimulatedTime();
		        System.out.println("paused");
		    } else {
		       // ucView.setStatus(UCModule_View.LED_STATUS.RUNNING);
		        System.out.println("running");
		    }


              //threadScheduler = new Thread(new Scheduler());
		    //threadScheduler.start();
		  if(standAloneFlag){
			  setIncrement(64);
			  System.out.println("Running test");
		  for (int j=0;j<500;j++){
			  cpuModule.run();
		  }
		  
		  for (int j=0;j<100000;j++){//15_000_000;j++){
		//	  if(j%100_000==0)
		//  System.out.println("run "+ j + " ");
			     timer0.run();
	                timer1.run();
	                timer2.run();
	                adc.run();
	             //   usart.run();
			       dummyChar =usart.run();
			       
			       //  System.out.println("dummyChar "+dummyChar);
			        	 if (dummyChar!=0)
			        		 System.out.println(dummyChar);
			        		 //  serialBufferOut.append(dummyChar);
			      //   usart.run();
				   
			     //if(j%10000==0)
			    	// System.out.println()
	                cpuModule.run();
	                ucView.run(); // this updates simulated time
		  }
		  System.out.println("Exit test runs");
		  }
		
		} else {
		    setUpSuccessful = false;
		   // ucView.setStatus(UCModule_View.LED_STATUS.HEX_FILE_ERROR);
		    System.out.println("HEX_FILE_ERROR");
		    if (!firstLoadFileFail) {
		    	System.out.println("fail_to_load_file");
		        //Toast.makeText(this, getString(R.string.fail_to_load_file), Toast.LENGTH_LONG).show();
		    }
		    firstLoadFileFail = false;
		}

    }
    public void setInput(int value, int index){
    	ucView.inputFragment.inputRequest_inputChanel(value, memoryAddress[index], memoryBitPosition[index]);
    }
    
    //next: find lower prescaler among timers
   public int getPreScaler(){
	   int[] values = {0,0,0};int min=1024;//= new int[];
	   // read bits that set prescaler value
	   values[0] = 0x07 & dataMemory.readByte(DataMemory_ATmega328P.TCCR0B_ADDR);
	   values[1] = 0x07 & dataMemory.readByte(DataMemory_ATmega328P.TCCR1B_ADDR);
	   values[2] = 0x07 & dataMemory.readByte(DataMemory_ATmega328P.TCCR2B_ADDR);
	   System.out.println("preacaler bits: " + values[0] +" "+ values[1] +" "+ values[2] );
	   // configuration bits have different interpretation depending on the timer
	   switch (values[0]) {
	   case 0: values[0] = 64; break;
	   case 1: values[0] = 1; break;
	   case 2: values[0] = 8; break;
	   case 3: values[0] = 64; break;
	   case 4: values[0] = 256; break;
	   case 5: values[0] = 1024; break;
	   }
	   switch (values[1]) {
	   case 0: values[1] = 64; break;
	   case 1: values[1] = 1; break;
	   case 2: values[1] = 8; break;
	   case 3: values[1] = 64; break;
	   case 4: values[1] = 256; break;
	   case 5: values[1] = 1024; break;
	   }
	   switch (values[2]) {
	   case 0: values[2] = 64; break;
	   case 1: values[2] = 1; break;
	   case 2: values[2] = 8; break;
	   case 3: values[2] = 32; break;
	   case 4: values[2] = 64; break;
	   case 5: values[2] = 128; break;
	   case 6: values[2] = 256; break;
	   case 7: values[2] = 1024; break;
	   }
	   //Find the minimum value of prescaler
	   for (int i=0;i<=2;i++)
		   if (values[i]<min)
			   min=values[i];
	   
	return   min;//0x07 & dataMemory.readByte(DataMemory_ATmega328P.TCCR0B_ADDR);
   }
  
   char dummyChar;
public void cycle(){
	
	  timer0.run();
         timer1.run();
         timer2.run();
         adc.run();
         dummyChar =usart.run();
       //  System.out.println("dummyChar "+dummyChar);
        	 if (dummyChar!=0)
         serialBufferOut.append(dummyChar);
      //   usart.run();
	   
         cpuModule.run();
         ucView.run(); // this updates simulated time
}
    public static int getDeviceModules() {
      //  int id = resources.getIdentifier(device, "integer", PACKAGE_NAME);
      //  return resources.getInteger(id);
    	return 5;
    }

    public static String getDevice(String model) {
        //int id = resources.getIdentifier(model, "string", PACKAGE_NAME);
        //return resources.getString(id);
        return "ATmega328P";
    }

    public static int getDefaultPinPosition() {
        //int id = resources.getIdentifier(UCModule.model + "_defaultPinPosition", "integer", PACKAGE_NAME);
        //return resources.getInteger(id);
    	return 13;
    }

    public static String[] getPinArray() {
//        int id = resources.getIdentifier(UCModule.model + "_pins", "array", PACKAGE_NAME);
//        return resources.getStringArray(id);
        return new String[]{
                "Pin0",
                "Pin1",
                "Pin2",
                "Pin3",
                "Pin4",
                "Pin5",
                "Pin6",
                "Pin7",
                "Pin8",
                "Pin9",
                "Pin10",
                "Pin11",
                "Pin12",
                "Pin13",
                "A0",
                "A1",
                "A2",
                "A3",
                "A4",
                "A5"
        };
    }

    public static boolean[] getHiZInput() {
        boolean[] hiZInput = new boolean[getPinArray().length];
        Arrays.fill(hiZInput, true);
        return hiZInput;
    }

    public static String[] getPinModeArray() {
        return new String[] {"Push-GND",
        "Push-VDD",
        "Pull-up",
        "Pull-down",
        "Toggle"};//resources.getStringArray(R.array.inputModes);
    }

    public static String getAREFError() {
        return "Enter a positive value between 1 and 5";//resources.getString(R.string.arefError);
    }

    public static int getSourcePower() {
        //return resources.getInteger(R.integer.defaultSourcePower);
        return 5;
        
    }

    public static String getSofiaVersion() {
        return "tryrtyr";//String.format("v%s", BuildConfig.VERSION_NAME);
    }

    public static double getMaxVoltageLowState() {
        //return (resources.getInteger(R.integer.maxVoltageLow) / 1000f);
        return (1000 / 1000f);
        
    }

    public static double getMinVoltageHighState() {
        //return (resources.getInteger(R.integer.minVoltageHigh) / 1000f);
        return (3000 / 1000f);
    }

    public static String[] getPinArrayWithHint() {
//        int id = resources.getIdentifier(UCModule.model + "_pins", "array", PACKAGE_NAME);
//        String[] pinArrayWithHint = resources.getStringArray(id);
        String[] pinArrayWithHint = UCModule.getPinArray();
        pinArrayWithHint = Arrays.copyOf(pinArrayWithHint, pinArrayWithHint.length + 1);

//        pinArrayWithHint[pinArrayWithHint.length - 1] = resources.getString(R.string.inputHint);
        pinArrayWithHint[pinArrayWithHint.length - 1] = "Pin X";
        return pinArrayWithHint;
    }

   /* public static String getNumberSelected(int number) {
        return resources.getQuantityString(
                R.plurals.number_selected,
                number, number
        );
    }*/
    public boolean getPinMode(int index){
 	   return dataMemory.readBit(memoryAddress[index] + 1, memoryBitPosition[index]);
    }
    
    public void setIncrement(int inc){
    	timer0.setIncrement(inc);
    	timer1.setIncrement(inc);
    	timer2.setIncrement(inc);
    	ucView.setIncrement(inc);
    }
    public static int[] getInputMemoryAddress() {
    	return new int [] { 0x29,
    			0x29,
    			0x29,
    			0x29,
    			0x29,
    			0x29,
    			0x29,
    			0x29,
    			0x23,
    			0x23,
    			0x23,
    			0x23,
    			0x23,
    			0x23,
    			0x26,
    			0x26,
    			0x26,
    			0x26,
    			0x26,
    			0x26    };
    }

    public static int[] getInputMemoryBitPosition() {
    	//  int id = resources.getIdentifier(UCModule.model + "_inputMemoryBitPosition", "array", PACKAGE_NAME);
    	// return resources.getIntArray(id);
    	return new int [] {   0,
    			1,
    			2,
    			3,
    			4,
    			5,
    			6,
    			7,
    			0,
    			1,
    			2,
    			3,
    			4,
    			5,
    			0,
    			1,
    			2,
    			3,
    			4,
    			5
    	};
    }
 
    /*public static int getSelectedColor() {
        return ContextCompat.getColor(context, R.color.selectedItem);
    }

    public static int getButonOnCollor() {
        return ContextCompat.getColor(context, R.color.on_button);
    }

    public static int getButonOffCollor() {
        return ContextCompat.getColor(context, R.color.off_button);
    }

    public static String getButtonTextOn() {
        return resources.getString(R.string.buttonOn);
    }

    public static String getButtonTextOff() {
        return resources.getString(R.string.buttonOff);
    }

    public static String getStatusRunning() {
        return resources.getString(R.string.running);
    }

    public static int getStatusRunningColor() {
        return ContextCompat.getColor(context, R.color.running);
    }

    public static String getStatusPaused() {
        return resources.getString(R.string.paused);
    }

    public static int getStatusPausedColor() {
        return ContextCompat.getColor(context, R.color.paused);
    }

    public static String getStatusShortCircuit() {
        return resources.getString(R.string.short_circuit);
    }

    public static int getStatusShortCircuitColor() {
        return ContextCompat.getColor(context, R.color.short_circuit);
    }
*/
    public static String getStatusHexFileError() {
        return ">Fail to read program";
    }

  /*  public static int getStatusHexFileErrorColor() {
        return ContextCompat.getColor(context, R.color.hex_file_error);
    }*/

    public synchronized boolean getResetFlag() {
        return resetFlag;
    }

    private synchronized void setResetFlag(boolean state) {
        resetFlag = state;
    }

    private void reset() {

        System.out.println(MY_LOG_TAG+ "Reset");

        pauseSimulation = false;
        stopSystem();
    //    ucView.resetIO();
        setUpUc();
    }

    private void stopSystem() {
    	 System.out.println(MY_LOG_TAG + "Stopping system");

        pauseSimulation = false;
        setResetFlag(true);

        try {
            threadScheduler.join(1000);
        } catch (InterruptedException | NullPointerException e) {
        	System.out.println("ERROR" + "ERROR: stopSystem -> join" + e);
        }

        Arrays.fill(OutputFragment_ATmega328P.evalFreq, false);

        if (setUpSuccessful) {
            programMemory.stopCodeObserver();
        }
    }

    private void shortCircuit() {
    	System.out.println(MY_LOG_TAG+ "Short Circuit - UCModule");
        shortCircuitFlag = true;
      //  ucView.setStatus(UCModule_View.LED_STATUS.SHORT_CIRCUIT);
        stopSystem();
    }

    private void pauseSystem() {
     //   ucView.setStatus(UCModule_View.LED_STATUS.PAUSED);
        System.out.println("Paused");
        pauseSimulation = true;
    }

    private void resumeSystem() {
    //    ucView.setStatus(UCModule_View.LED_STATUS.RUNNING);
        System.out.println("Running");
        pauseSimulation = false;
    }

    //    public void changeFileLocation(String newHexFileLocation) {
    public void changeFileLocation(String newHexFileLocation) {
        if (newHexFileLocation == null) {
            //Toast.makeText(this, getString(R.string.fail_to_load_file), Toast.LENGTH_LONG).show();
            return;
        }

//        if (newHexFileLocation.toString().substring(newHexFileLocation.toString().length() - 3).equals("hex")) {
////            hexFileLocation = newHexFileLocation.replace("/storage/emulated/0/", "");
            hexFileLocation = newHexFileLocation;
            System.out.println("FileImporter"+ "New Path: " + hexFileLocation);
//        } else {
//            Toast.makeText(this, getString(R.string.not_hex_error), Toast.LENGTH_LONG).show();
//        }
    }

   /* public class UCHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            int action = msg.what;

            switch (action) {
                case RESET_ACTION:
                    reset();
                    break;

                case SHORT_CIRCUIT_ACTION:
                    shortCircuit();
                    break;

                case STOP_ACTION:
                    stopSystem();
                    break;

                case PAUSE_ACTION:
                    pauseSystem();
                    break;

                case RESUME_ACTION:
                    resumeSystem();
                    break;

                case UPDATE_SETTINGS_ACTION:
                    //Load device settings
                    SharedPreferences settings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
                    startPaused = settings.getBoolean(START_PAUSED_SETTINGS, false);
                    ADC_ATmega328P.AREF = (short) settings.getInt(AREF_SETTINGS, 5000);
                    model = settings.getString(MODEL_SETTINGS, "UNO");
                    break;

                default:
                    Log.e(MY_LOG_TAG, "ERROR: Action not found UCModule");
                    break;
            }
        }
    }
*/
    private class Scheduler implements Runnable {

        private double getAvgClock(double newClock) {
            sum += newClock;
            return (sum / ++n);
        }

        @Override
        public void run() {
            Thread.currentThread().setName("Scheduler");
            while (!getResetFlag()) {

                if (pauseSimulation) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                //Measure efective clock
//                time2 = SystemClock.elapsedRealtimeNanos();
//                Log.i("Clock", String.valueOf(getAvgClock(Math.pow(10, 9) / (time2 - time1))));
//                time1 = time2;

             //   timer0.run();
             //   timer1.run();
             //   timer2.run();
             //   adc.run();
             //   usart.run();
                cpuModule.run();
                ucView.run(); // this updates simulated time

            }

            System.out.println(UCModule.MY_LOG_TAG+ "Finishing Scheduler");
        }
    }
}
