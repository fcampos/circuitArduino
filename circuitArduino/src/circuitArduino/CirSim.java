package circuitArduino;
// CirSim.java (c) 2010 by Paul Falstad

// For information about the theory behind this, see Electronic Circuit & System Simulation Methods by Pillage

//import circuitMatlab.UI.*;
import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
//import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.Menu;
//import java.awt.MenuBar;
import java.awt.MenuItem;
//import java.awt.MenuShortcut;
//import java.awt.Panel;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
//import java.awt.image.BufferedImage;
//import java.awt.print.PageFormat;
//import java.awt.print.Printable;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
//import java.nio.file.*;
import org.apache.commons.io.IOUtils;
import java.io.*;

import java.io.IOException;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.Writer;
import java.lang.reflect.Constructor;
//import java.net.MalformedURLException;
//import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.util.Arrays;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.HashSet;
import java.util.Set;
//import java.util.concurrent.CancellationException;
//import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
//import javax.swing.JCheckboxMenuItem;
//import javax.swing.Menu;
//import javax.swing.MenuItem;
import javax.swing.JPanel;
//import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//import org.apache.commons.io.IOUtils;
import org.jfree.graphics2d.svg.SVGGraphics2D; 
import org.jfree.graphics2d.svg.SVGUtils;

/*import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory; 
*/
import com.mathworks.jmi.*;
//import statictrigger.triggerCls;
 //import com.mathworks.engine.*;

import circuitArduino.UI.EditDialog;
import circuitArduino.UI.EditOptions;
import circuitArduino.UI.EditOptionsArduino;
import circuitArduino.components.arduino.Arduino;
import circuitArduino.components.labels_instruments.Scope;
import circuitArduino.components.labels_instruments.TextElm;
import circuitArduino.components.passive.SwitchElm;
import circuitArduino.components.sound.PlayThread;
import circuitArduino.Algorithm;
import circuitArduino.CirSimWrapper;
import circuitArduino.Circuit;
import circuitArduino.CircuitCanvas;
import circuitArduino.CircuitElm;
import circuitArduino.CircuitLayout;
import circuitArduino.CircuitNode;
import circuitArduino.CircuitNodeLink;
import circuitArduino.Editable;
import circuitArduino.ImportDialog;
import circuitArduino.InitializeMenus;
import circuitArduino.RowInfo;
import circuitArduino.Visualization;

//import net.sf.epsgraphics.*;
/*import javax.swing.JComponent;
import  org.apache.batik.anim.dom.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;

import org.jfree.chart.JFreeChart;*/
//import org.jfree.ui.Drawable;
//org/jfree/ui/Drawable
//import javax.swing.JComponent;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.*;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparseCSC;
//import org.ejml.data.Matrix;
//import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.interfaces.linsol.*;
//import org.ejml.ops.ConvertDMatrixStruct;
//import org.ejml.sparse.ComputePermutation;
import org.ejml.sparse.FillReducing;
//import org.ejml.sparse.csc.decomposition.lu.LuUpLooking_DSCC;
import org.ejml.sparse.csc.factory.LinearSolverFactory_DSCC;
//import org.ejml.sparse.csc.linsol.lu.LinearSolverLu_DSCC;
//import org.ejml.sparse.csc.linsol.*;

public class CirSim extends JPanel
  implements ComponentListener, ActionListener, AdjustmentListener,
  MouseMotionListener, MouseWheelListener,MouseListener, ItemListener, KeyListener {

	//Thread engine = null;

	public Dimension winSize;
	BufferedImage dbimage;

	Random random;
	public static final int sourceRadius = 7;
	public static final double freqMult = 3.14159265*2*4;

	public String getAppletInfo() {
		return "Circuit by Paul Falstad";
	}
	

    // -----------------------------------------------------------
    // USER INTERFACE DATA
    // -----------------------------------------------------------
	
	public static Container main;
	Label titleLabel;
	public Button resetButton;
	Button dumpMatrixButton;
	MenuItem exportItem,exportAsItem, importItem, exitItem, undoItem, redoItem, //exportLinkItem,
	deleteItem, cutItem, copyItem, pasteItem, selectAllItem, optionsItem, printItem;
	public MenuItem importFritzingItem, robotSimulation,arduinoSelectSketchItem,arduinoSketchItem,arduinoEditFileItem,arduinoReloadItem,arduinoEditOptionItem;
	Menu optionsMenu;
	Menu arduinoMenu;
	FileDialog fileDialog;
	public Checkbox stoppedCheck;
	CheckboxMenuItem dotsCheckItem;
	CheckboxMenuItem voltsCheckItem;
	public CheckboxMenuItem powerCheckItem;
	public CheckboxMenuItem smallGridCheckItem;
	CheckboxMenuItem showGridCheckItem;
	public CheckboxMenuItem showValuesCheckItem;
    CheckboxMenuItem conductanceCheckItem;
    public CheckboxMenuItem euroResistorCheckItem;
    public CheckboxMenuItem printableCheckItem;
    CheckboxMenuItem conventionCheckItem;
    public TextField durationTextItem;
    public Scrollbar speedBar;
    public Scrollbar currentBar;
    Label powerLabel;
    Scrollbar powerBar;
    PopupMenu elmMenu;
    MenuItem elmEditMenuItem;
    MenuItem elmCutMenuItem;
    MenuItem elmCopyMenuItem;
    MenuItem elmDeleteMenuItem;
    MenuItem elmScopeMenuItem;
    public PopupMenu scopeMenu;
    public PopupMenu transScopeMenu;
    PopupMenu mainMenu;
    public CheckboxMenuItem scopeVMenuItem;
    public CheckboxMenuItem scopeIMenuItem;
    public CheckboxMenuItem scopeMaxMenuItem;
    public CheckboxMenuItem scopeMinMenuItem;
    public CheckboxMenuItem scopeFreqMenuItem;
    public CheckboxMenuItem scopePowerMenuItem;
    public CheckboxMenuItem scopeIbMenuItem;
    public CheckboxMenuItem scopeIcMenuItem;
    public CheckboxMenuItem scopeIeMenuItem;
    public CheckboxMenuItem scopeVbeMenuItem;
    public CheckboxMenuItem scopeVbcMenuItem;
    public CheckboxMenuItem scopeVceMenuItem;
    public CheckboxMenuItem scopeVIMenuItem;
    public CheckboxMenuItem scopeXYMenuItem;
    public CheckboxMenuItem scopeResistMenuItem;
    public CheckboxMenuItem scopeVceIcMenuItem;
    public MenuItem scopeSelectYMenuItem;
    
    public static EditDialog editDialog;
    static ImportDialog impDialog;
    static SelectFileDialog selFileDialog;
    
    Class addingClass;
    int mouseMode = MODE_SELECT;
    int tempMouseMode = MODE_SELECT;
    public int buttonPressed = 1 ;
    boolean dragged = false;
    boolean popupOpen = false;
   // int Csize=16; //component Size
    int selectPost = 0 ;
    public int doubleclick = 0 ;
    public int checkBoxDragElem = 0;
    String mouseModeStr = "Select";
    static final double pi = 3.14159265358979323846;
    static final int MODE_ADD_ELM = 0;
    static final int MODE_DRAG_ALL = 1;
    static final int MODE_DRAG_ROW = 2;
    static final int MODE_DRAG_COLUMN = 3;
    static final int MODE_DRAG_SELECTED = 4;
    static final int MODE_DRAG_POST = 5;
    static final int MODE_SELECT = 6;
    
    static final int infoWidth = 120;
    public int dragX;

	public int dragY;

	public int initDragX;

	public int initDragY;
    int selectedSource;
    public Rectangle selectedArea;
    public static final int defaultGridSize= 8;
    public int gridSize = defaultGridSize;
    public int oldGridSize = defaultGridSize;
    boolean showGrid = false;
    int minGridSize = 4;
    public int offsetX=0;
    public int offsetY=0;
  
    int gridMask, gridRound;
    boolean dragging;
    public boolean analyzeFlag;
    boolean dumpMatrix;
    public boolean useBufferedImage;
    boolean isMac;
    String ctrlMetaKey;

    int pause = 10;
    public int scopeSelected = -1;
    int menuScope = -1;
    int hintType = -1, hintItem1, hintItem2;
    String stopMessage;
    
    public CircuitElm dragElm;
	CircuitElm menuElm;
	CircuitElm stopElm;
    public CircuitElm  mouseElm;
    int mousePost = -1;
    public CircuitElm plotXElm;
	public CircuitElm plotYElm;
    public int draggingPost;
    SwitchElm heldSwitchElm;
    

    // -----------------------------------------------------------
    // CIRCUIT SIMULATION DATA
    // -----------------------------------------------------------
    public double t;  // Simulation time
    public double timeStep; // Simulation timestep
    public double nextTimeStep=0;
    
    
    // -----------------------------------------------------------
    // CIRCUIT REPRESENTATION DATA
    // -----------------------------------------------------------
    public Vector<CircuitElm> elmList;
    public Vector<CircuitElm> checkElem = new Vector<CircuitElm>();
    Vector setupList;
    
    //double circuitMatrix[][], circuitRightSide[],
	//origRightSide[], origMatrix[][];
    //double  circuitRightSide[],
	//origRightSide[];
    
    // -----------------------------------------------------------
    // CIRCUIT SOLVING TOOLS
    // -----------------------------------------------------------
    DMatrixSparseCSC circuitMatrix, origMatrix;
    
    DMatrixRMaj circuitRightSide,origRightSide;
    RowInfo circuitRowInfo[];
    int circuitPermute[];
    boolean circuitNonLinear;
    int voltageSourceCount;
    int circuitMatrixSize, circuitMatrixFullSize;
    
     boolean circuitNeedsMap;

     // -----------------------------------------------------------
     // APP MANAGEMENT DATA
     // -----------------------------------------------------------
     public CircuitCanvas cv; // This is the playground area  
     public Circuit applet; // This is the main class- it is only used in the standalone app. If the simulator is started from MAtlab it is null
     CirSimWrapper frameWrapper;
     Boolean usePanel = false; // it is true if the simulator is started from Matlab
     public double simDuration=-1;
   //  public boolean useFrame;
     int scopeCount;
     Scope scopes[];
     int scopeColCount[];

     Class dumpTypes[];
     static String muString = "u";
     public static String ohmString = "ohm";
     String clipboard;
     Rectangle circuitArea;
     int circuitBottom;
     Vector<String> undoStack, redoStack; 
     public boolean exportEPS = false;
     String startCircuit = null;
     String startLabel = "lrc";
     String startCircuitText = null;
     String baseURL = "http://www.falstad.com/circuit/";
     public  String startDir = null;
     public String circuitFilePath = "";//new String("");
     public String circuitName = "";
     //////////////////////// NEW CLASSES /////////////////////////////////////////////////////
     public SVGGraphics2D g2;
     InitializeMenus menuInitializer;
     public Algorithm algorithm;
     Visualization visualizer;
     public UItools uitools;
     public Frame matlabFrame;
     public CircuitAnalizer analyzer;
     FileTools filetools;
     EditTools edittools;
     /////////////////////////NEW PROPERTIES //////////////////////////////////////////////////
   
    //Matlab related properties
    
    public boolean doSynchronizedSimulationPG=true;
    public boolean updateMatlabGraphics=false;
    public boolean MatlabBusy=false;
    public boolean updateMatlabGraphicsFinal=false;
    public String serialMessage = "";
    String serialMessagePersistent = "";
    public long matlabGraphicsFramerate = 20; // is this period of grapic updates, in milliseconds? should be lower, then? (it was 50)
    long lastMatlabUpdateGraphics;
    public boolean doUpdateMatlabGraphics;
    Matlab matlabProxy;
   
    int counterSimulationPG=0;
    public int periodRatioPG=50;
    //public Object matlabApp= new Object();;
   
    
    //Arduino related properties - Arduino related properties- Arduino related properties - Arduino related properties
    public int periodRatioArduino = 1;
    int counterSimulationArduino=0;
    public  String sketchURL ;
    public  String sketchName ;
    public String arduinoFolder; 
    // arduinoFolder = new URL(getCodeBase() + "arduino" ); 
    // public  String headerURL ;//= arduinoFolder + "arduinoHeader.lox";// "C:\\Users\\FranciscoMateus\\workspace\\circuitMatlab\\arduino\\arduinoHeader.lox";
    public String selectedFile;
    String selectedFileName;
    public  Arduino arduino;
    // Sound related properties -Sound related properties - Sound related properties - Sound related properties -  
    public PlayThread playThread;
    double playSoundCount = 0;
    double playSoundCount2 = 0;
    double timeSoundElapsed = 0;
    double lastSoundElmSample=0;
    public double soundSampleRate=22050;

   

    
  //  MatlabProxyFactory factory;
  //  public MatlabProxy proxy;
   
  // String[] engines;
  // MatlabEngine mEngine;
   
    
    // ------------------------------------------------------------------
    //                               CONSTRUCTOR
    // ------------------------------------------------------------------
    CirSim(CirSimWrapper a) {  // CALL WITHOUT MATLAB
  //  	useFrame = true;
    	usePanel = false;
    	frameWrapper = a;
    	matlabFrame = a; 
    	System.out.println("Here we go");
    }
    public CirSim(String s) {  // CALL FROM MATLAB
    	usePanel = true;
    //	useFrame = true;
    	startDir = s;
        }
   
    
    
  
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///
    /// 			START INIT()
    ///
    ///////////////////////////////////////////////////////////////////////////////////////////////
   
    public void init() {
    	String euroResistor = null;
    //	String useFrameStr = null;
    	boolean printable = false;
    	boolean convention = true;
    	System.setProperty("sun.java2d.opengl", "True");
    	try{
    		UIManager.setLookAndFeel(	            UIManager.getSystemLookAndFeelClassName());}
    	catch (UnsupportedLookAndFeelException|ClassNotFoundException|InstantiationException|IllegalAccessException e) {
    	}
    	CircuitElm.initClass(this);
    	String doc;
    	filetools = new FileTools(this);
   
	boolean euro = (euroResistor != null && euroResistor.equalsIgnoreCase("true"));
	//useFrame = (useFrameStr == null || !useFrameStr.equalsIgnoreCase("false"));
	//if (useFrame)
	//    main = this;
	//else
	//    main = applet;

	if (usePanel)
	    main = this;
	else
	    main = frameWrapper;
	System.out.println("opengl: " + System.getProperty("sun.java2d.opengl"));
	
	String os = System.getProperty("os.name");
	isMac = (os.indexOf("Mac ") == 0);
	ctrlMetaKey = (isMac) ? "\u2318" : "Ctrl";
	String jv = System.getProperty("java.class.version");
	double jvf = new Double(jv).doubleValue();
	if (jvf >= 48) {
	    muString = "\u03bc";
	    ohmString = "\u03a9";
	    useBufferedImage = true;
	}
	
	dumpTypes = new Class[300];
	// these characters are reserved
	dumpTypes[(int)'o'] = Scope.class;
	dumpTypes[(int)'h'] = Scope.class;
	dumpTypes[(int)'$'] = Scope.class;
	dumpTypes[(int)'%'] = Scope.class;
	dumpTypes[(int)'?'] = Scope.class;
	dumpTypes[(int)'B'] = Scope.class;

	main.setLayout(new CircuitLayout(this));
	cv = new CircuitCanvas(this);
	cv.addComponentListener(this);
	cv.addMouseMotionListener(this);
	cv.addMouseWheelListener(this);
	cv.addMouseListener(this);
	cv.addKeyListener(this);
	main.add(cv);
	//MenuBar mb = null;

		String dummy = getCodeBase().getPath();
	

		if (usePanel){
			dummy = startDir;
		}

		// -----------------------------------------------------------------------------------------------------------------	
		// Arduino INITIALIZATION - Arduino INITIALIZATION - Arduino INITIALIZATION - Arduino INITIALIZATION - Arduino INITIALIZATION - Arduino INITIALIZATION
		// -----------------------------------------------------------------------------------------------------------------	
		arduinoFolder = dummy + File.separator + "arduino";
		//headerURL = dummy + File.separator + "arduino" + File.separator + "arduinoHeader.lox";
		sketchURL =  dummy + File.separator + "arduino" + File.separator + "blank" + File.separator + "blank.ino.standard.hex" ;//"blank.ino" ;//"C:\\Users\\FranciscoMateus\\workspace\\pl-projects-master\\pl-projects-master\\jlox\\examples\\arduinoTest.lox";

		//arduinoFolder = "C:\\Users\\FranciscoMateus\\Documents\\MATLAB\\EIPlayground\\arduino\\";

		//headerURL = "C:\\Users\\FranciscoMateus\\Documents\\MATLAB\\EIPlayground\\arduino\\arduinoHeader.lox";
		System.out.println(arduinoFolder);
		//System.out.println(headerURL);
		//sketchURL =  "C:\\Users\\FranciscoMateus\\Documents\\MATLAB\\EIPlayground\\arduino\\blank\\blank.ino";

		sketchName = "blank.ino";

		arduino = new Arduino(this,  sketchURL);//,headerURL});
		System.out.println("User dir:" + System.getProperty("user.dir").toString());

		// THREAD FOR PLAYING SOUND
		if (playThread == null){// && !unstable && soundCheck.getState()) {
			playThread = new PlayThread(this);
			playThread.start();

		}
		// usePanel== true means we are running on Matlab
		if (usePanel){
			matlabProxy  = new Matlab();
			/*	try {
		proxy.eval("disp('HI HI HI HI HI HI  HI')");
	} catch (MatlabInvocationException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}*/
			try {
				Object[] ss = {"HI HI Hi HI HI Hi HI HI Hi"};
				Matlab.mtFevalConsoleOutput("disp",ss,0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
		try {
			engines = MatlabEngine.findMatlab();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			mEngine = MatlabEngine.connectMatlab(engines[0]);
		} catch (EngineException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Execute command on shared MATLAB session
        try {
			mEngine.eval("disp('Successfully connected')");
		} catch (CancellationException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			mEngine.close();
		} catch (EngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	/*	factory = new MatlabProxyFactory();
		try {
			proxy = factory.getProxy();
		} catch (MatlabConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			proxy.eval("disp('HI HI HI HI HI HI  HI')");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	//arduino.interpreter.start();
		//-----------------------------------------------------------------------------------------
		// -------------------------------- INITIALIZE NEW CLASSES --------------------------------
	//---------------------------------------------------------------------------------------------
	menuInitializer = new InitializeMenus(this);
	menuInitializer.init();
	algorithm = new Algorithm(this);
	visualizer = new Visualization(this);
	analyzer = new CircuitAnalizer(this);
	uitools = new UItools(this);
	edittools = new EditTools(this);
	

	setGrid();
	elmList = new Vector<CircuitElm>();
	setupList = new Vector<CircuitElm>();
	undoStack = new Vector<String>();
	redoStack = new Vector<String>();

	scopes = new Scope[20];
	scopeColCount = new int[20];
	scopeCount = 0;
	
	random = new Random();
	cv.setBackground(Color.black);
	cv.setForeground(Color.lightGray);
	
		
	if (startCircuitText != null)
		filetools.readSetup(startCircuitText);
	else if (stopMessage == null && startCircuit != null)
	    filetools.readSetupFile(startCircuit, startLabel);

	if (!usePanel) {
	    Dimension screen = getToolkit().getScreenSize();
	    frameWrapper.resize(860, 640);
	    handleResize();
	    Dimension xx = frameWrapper.getSize();
	    frameWrapper.setLocation((screen.width  - xx.width)/2,
			(screen.height - xx.height)/2);
	    show();
	} 
	System.out.println("---- END INIT() -----");
	System.out.println(main.toString());
	main.requestFocus();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///
    /// 			END INIT()
    ///
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
   public  void setMatlabApp(){
    	/*try {
			matlabApp = proxy.getVariable("pg");
		} catch (MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
  
   
    
    
    boolean shown = false;
    
    
	public void triggerShow() {
	if (!shown)
	    show();
	shown = true;
    }
    
 
  
    
    void register(Class c, CircuitElm elm) {
	int t = elm.getDumpType();
	if (t == 0) {
	    System.out.println("no dump type: " + c);
	    return;
	}
	Class dclass = elm.getDumpClass();
	if (dumpTypes[t] == dclass)
	    return;
	if (dumpTypes[t] != null) {
	    System.out.println("dump type conflict: " + c + " " +
			       dumpTypes[t]);
	    return;
	}
	dumpTypes[t] = dclass;
    }
    
    

    // cv is the instance of the CircuitCanvas class, which extends Canvas.
    // it has methods update(Graphics g) and paint(Graphics g) which both redirect to CirSim.updateCircuit(g,false);

    public void paint(Graphics g) {
    	cv.repaint();
    }

    static final int resct = 6;
    long lastTime = 0, lastFrameTime, lastIterTime, secTime = 0;
    int frames = 0;
    int steps = 0;
    int framerate = 0, steprate = 0;


    public void updateCircuit(Graphics realg, boolean flag) {
    	CircuitElm realMouseElm;
    	exportEPS=flag; 
    	if (winSize == null || winSize.width == 0)
	    return;
	if (analyzeFlag) {
	    analyzer.analyzeCircuit();
	    analyzeFlag = false;
	}
	if (editDialog != null && editDialog.elm instanceof CircuitElm)
	    mouseElm = (CircuitElm) (editDialog.elm);
	realMouseElm = mouseElm;
	if (mouseElm == null)
	    mouseElm = stopElm;
	setupScopes();
        Graphics g = null; // g is the image that is being drawn throughtout this method. It is later sent to the canvas through realg.drawImage(dbimage, 0, 0, this);
	g = dbimage.getGraphics();
	CircuitElm.selectColor = Color.cyan;
	if (printableCheckItem.getState()) {
	    CircuitElm.whiteColor = Color.gray;//Color.black;
	    CircuitElm.lightGrayColor = Color.black;
	    g.setColor(Color.white);
	} else {
	    CircuitElm.whiteColor = Color.white;
	    CircuitElm.lightGrayColor = Color.lightGray;
	    g.setColor(Color.black);
	}
	g.fillRect(0, 0, winSize.width, winSize.height);
	if (!stoppedCheck.getState()) {
	    try {
	    	
		//runCircuit();
	    	//String[] stt= new String[1];
	    	//stt[0]="C:\\Users\\FranciscoMateus\\workspace\\test.c+-";
	    	//arduino.interpreter.main(stt);
	    	if (t>simDuration&simDuration>0)
	    	{
	    	stoppedCheck.setState(true);
	    	if (usePanel)
	    	{updateMatlabGraphics=true;
	    	updateMatlabGraphicsFinal=true;
			doUpdateMatlabGraphics = false;	
				System.out.println("last update");
				// WHY WAS THIS NEEDED?
				/*try {
					proxy.feval("pg_timerCallback");
				} catch (MatlabInvocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				}
	    	return;
	    	}
	    	else
	    	runCircuit();
	    } catch (Exception e) {
		e.printStackTrace();
		analyzeFlag = true;
		cv.repaint();
		exportEPS=false;
		return;
	    }
	}
	if (!stoppedCheck.getState()) {
	    long sysTime = System.currentTimeMillis();
	    if (lastTime != 0) {
		int inc = (int) (sysTime-lastTime);
		double c = currentBar.getValue();
		c = java.lang.Math.exp(c/3.5-14.2);
		CircuitElm.currentMult = 1.7 * inc * c;
		if (!conventionCheckItem.getState())
		    CircuitElm.currentMult = -CircuitElm.currentMult;
	    }
	    //////////////////////////////////////////////////////////////////////////
	    ////  THIS SHOULD PROBABLY BE PLACED IN A FUNCTION I ALGORITHM CLASS
	    //////////////////////////////////////////////////////////////////////////
	    /////////////////////////////////////////////////////////////////////////////
	    if (sysTime-secTime >= 1000) {
	    	framerate = frames; steprate = steps;
	    	frames = 0; steps = 0;
	    	secTime = sysTime;
	    }
	    lastTime = sysTime;
	    
	   
	    
	} else { /// IF STOPPPED CHECK IF MATLAB GRAPHICS NEED UPDATE (this happens if the simulation was just stopped)
		 if (doUpdateMatlabGraphics){
		    	updateMatlabGraphics=true;
		    	updateMatlabGraphicsFinal=true;
				doUpdateMatlabGraphics = false;	
					System.out.println("last update");
					// WHY WAS THIS NEEDED?
		    	/*try {
						proxy.feval("pg_timerCallback");
					} catch (MatlabInvocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
		    }
		lastTime = 0;
		}
	CircuitElm.powerMult = Math.exp(powerBar.getValue()/4.762-7);
	
	int i;
	Font oldfont = g.getFont();
	for (i = 0; i != elmList.size(); i++) {
	    if (powerCheckItem.getState())
		g.setColor(Color.gray);
	    /*else if (conductanceCheckItem.getState())
	      g.setColor(Color.white);*/
	    getElm(i).draw(g);
	}
	if (tempMouseMode == MODE_DRAG_ROW || tempMouseMode == MODE_DRAG_COLUMN ||
	    tempMouseMode == MODE_DRAG_POST || tempMouseMode == MODE_DRAG_SELECTED)
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		ce.drawPost(g, ce.x , ce.y );
		ce.drawPost(g, ce.x2, ce.y2);
	    }
	int badnodes = 0;
	if (arduino.exists()){
		g.setColor(Color.white);
		g.drawString("Sketch: " + sketchName,10,20);
		if (arduino.ucModule.serialBufferOut.length()>0)//&&!usePanel)
			//serialMessagePersistent = arduino.interpreter.arduino.serialMessage;
		//if(serialMessagePersistent.length()>0)
			//g.drawString(serialMessagePersistent, 10, circuitArea.height-15);//
		{//System.out.println(arduino.ucModule.serialBufferOut.substring(0));
		//System.out.println(arduino.ucModule.serialBufferOut.substring(arduino.ucModule.serialBufferOut.length()-1));
			if (arduino.ucModule.serialBufferOut.charAt(arduino.ucModule.serialBufferOut.length()-1)==10)
		{if (arduino.ucModule.serialBufferOut.length()>1)
				serialMessagePersistent = arduino.ucModule.serialBufferOut.substring(0);
		//		System.out.println("FOUND FOUND FOUND");
			//	System.out.println(arduino.ucModule.serialBufferOut.length());
				
		arduino.ucModule.serialBufferOut.delete(0,arduino.ucModule.serialBufferOut.length()-1);}
	g.drawString(serialMessagePersistent, 10, circuitArea.height-15);}
		}
	///////////////////////////////////////
	if (showGridCheckItem.getState()){
		g.setColor(Color.gray);
		int ds = gridSize;
	if (ds <=16) 
		ds=ds*2;
	int x00=0, y00=0;
	//g.fillRect(x00, y00, 4, 4);
	for (x00 = 0; x00 < 1000; x00 += ds) {
		for (y00 = 0; y00 < 1000; y00 += ds) {
			g.fillRect(x00+offsetX, y00+offsetY, 1, 1);
		}}
	}
	///////////////////////////////////////	
	// find bad connections, nodes not connected to other elements which
	// intersect other elements' bounding boxes
	for (i = 0; i != nodeList.size(); i++) {
		CircuitNode cn = getCircuitNode(i);
		if (!cn.internal && cn.links.size() == 1) {
			int bb = 0, j;
			CircuitNodeLink cnl = (CircuitNodeLink)
					cn.links.elementAt(0);
			for (j = 0; j != elmList.size(); j++)
				if (cnl.elm != getElm(j) &&
				getElm(j).boundingBox.contains(cn.x, cn.y))
					bb++;
			if (bb > 0) {
				g.setColor(Color.red);
				g.fillOval(cn.x-3, cn.y-3, 7, 7);
				badnodes++;
			}
		}
	}
	/*if (mouseElm != null) {
	    g.setFont(oldfont);
	    g.drawString("+", mouseElm.x+10, mouseElm.y);
	    }*/
	if (dragElm != null &&
	      (dragElm.x != dragElm.x2 || dragElm.y != dragElm.y2))
	    dragElm.draw(g);
	g.setFont(oldfont);
	int ct = scopeCount;
	if (stopMessage != null)
	    ct = 0;
	for (i = 0; i != ct; i++)
	    scopes[i].draw(g);
	g.setColor(CircuitElm.whiteColor);
	if (stopMessage != null) {
	    g.drawString(stopMessage, 10, circuitArea.height);
	} else {
	    if (circuitBottom == 0)
		analyzer.calcCircuitBottom();
	    String info[] = new String[10];
	    if (mouseElm != null) {
		if (mousePost == -1)
		    mouseElm.getInfo(info);
		else
		    info[0] = "V = " +
			CircuitElm.getUnitText(mouseElm.getPostVoltage(mousePost), "V");
		/* //shownodes
		for (i = 0; i != mouseElm.getPostCount(); i++)
		    info[0] += " " + mouseElm.nodes[i];
		if (mouseElm.getVoltageSourceCount() > 0)
		    info[0] += ";" + (mouseElm.getVoltageSource()+nodeList.size());
		*/
		
	    } else {
		CircuitElm.showFormat.setMinimumFractionDigits(2);
		info[0] = "t = " + CircuitElm.getUnitText(t, "s");
		CircuitElm.showFormat.setMinimumFractionDigits(0);
	    }
	    if (hintType != -1) {
		for (i = 0; info[i] != null; i++)
		    ;
		String s = visualizer.getHint(hintItem1, hintItem2, hintType);
		if (s == null)
		    hintType = -1;
		else
		    info[i] = s;
	    }
	    int x = 0;
	    if (ct != 0)
		x = scopes[ct-1].rightEdge() + 20;
	    x = max(x, winSize.width*2/3);
	    
	    // count lines of data
	    for (i = 0; info[i] != null; i++)
		;
	    if (badnodes > 0)
		info[i++] = badnodes + ((badnodes == 1) ?
					" bad connection" : " bad connections");
	    
	    // find where to show data; below circuit, not too high unless we need it
	    int ybase = winSize.height-15*i-5;
	    ybase = min(ybase, circuitArea.height);
	    ybase = max(ybase, circuitBottom);
	    for (i = 0; info[i] != null; i++)
		g.drawString(info[i], x,
			     ybase+15*(i+1));
	}
	if (selectedArea != null) {
	    g.setColor(CircuitElm.selectColor);
	    g.drawRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);
	}
	mouseElm = realMouseElm;
	frames++;
	/*
	g.setColor(Color.white);
	g.drawString("Framerate: " + framerate, 10, 10);
	g.drawString("Steprate: " + steprate,  10, 30);
	g.drawString("Steprate/iter: " + (steprate/getIterCount()),  10, 50);
	g.drawString("iterc: " + (getIterCount()),  10, 70);
	*/

	realg.drawImage(dbimage, 0, 0, this);
	if ((!stoppedCheck.getState()) && circuitMatrix != null) {//||(matlabControl&matlabGo)
		 
		// Limit to 50 fps (thanks to Jürgen Klötzer for this)
		//	stoppedCheck.setLabel("sss");
		//counterSimulationPG++;
		//public int periodRatioPG=10;
		
		
		///// IS THE FOLLOWING REALLY NEEDED???? //////////////////////////////
		//if (doSynchronizedSimulationPG&counterSimulationPG>=periodRatioPG){
		/*	if(usePanel&&!MatlabBusy){
			counterSimulationPG=0;
		//	triggerCls.setTrigger("bbbb");
			updateMatlabGraphics=true;
			MatlabBusy = true;
			matlabProxy.evalConsoleOutput("pg_timerCallback;");;
		/*	try {
			//	System.out.println("sleeping...");
			//	proxy.feval("pause",2);
				proxy.feval("pg_timerCallback", 0,0,matlabApp);
				
			//	System.out.println("waking...");
				
			} catch (MatlabInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			*/
	
		//}
		
		// SIMULATE ARDUINO
		/*if (arduino.existsFlag){
		arduino.cycle();
		}*/
		//arduino.interpreter.runCycle();
		long delay = 1000/50 - (System.currentTimeMillis() - lastFrameTime);
		//realg.drawString("delay: " + delay,  10, 90);
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
		}
	    }
	  
	    cv.repaint(0);
	}
	oldGridSize = gridSize;
	lastFrameTime = lastTime;
	exportEPS=false;
    }

    


    
    
    public void needAnalyze() {
	analyzeFlag = true;
	cv.repaint();
	//plotGrid();
    }
    
    public Vector<CircuitNode> nodeList;
    CircuitElm voltageSources[];

    public CircuitNode getCircuitNode(int n) {
	if (n >= nodeList.size())
	    return null;
	return (CircuitNode) nodeList.elementAt(n);
    }

    public CircuitElm getElm(int n) {
	if (n >= elmList.size())
	    return null;
	return (CircuitElm) elmList.elementAt(n);
    }
    void setupCheckElem()
    { int i,k;
    // if (checkElem.size()>0)
    checkElem.removeAllElements();
    for (i = 0; i != nodeList.size(); i++) {
    	CircuitNode cn = getCircuitNode(i);
    	int j;
    	for (k = 0; k < cn.links.size(); k++) {
    		if (cn.links.elementAt(k).elm.isSelected()){
    			for (j = 0; j < cn.links.size(); j++){
    				//CircuitNodeLink cnl2 = (CircuitNodeLink) cn.links.elementAt(j);
    				if (!cn.links.elementAt(j).elm.isSelected()){
    					checkElem.add(cn.links.elementAt(j).elm);
    				}
    			}
    			break;
    		}

    	}
    }

    }
    
     

    public void stop(String s, CircuitElm ce) {
	stopMessage = s;
	circuitMatrix = null;
	stopElm = ce;
	stoppedCheck.setState(true);
	analyzeFlag = false;
	cv.repaint();
    }
    
   

    double getIterCount() {
	if (speedBar.getValue() == 0)
	    return 0;
	//return (Math.exp((speedBar.getValue()-1)/24.) + .5);
	return .1*Math.exp((speedBar.getValue()-61)/24.);
   }
    
    public boolean converged;
    public int subIterations;
    
    public void runCircuit() {
	if (circuitMatrix == null || elmList.size() == 0) {
	    circuitMatrix = null;
	    return;
	}
	int iter;

	//int maxIter = getIterCount();
	boolean debugprint = dumpMatrix;
	dumpMatrix = false;
	long steprate = (long) (160*getIterCount());
	long tm = System.currentTimeMillis();
	long lit = lastIterTime;
	long time1=0, time2=0, time3=0, time4=0,time5=0, lastTime=System.nanoTime();
	LinearSolverSparse<DMatrixSparseCSC,DMatrixRMaj> solver = LinearSolverFactory_DSCC.lu(FillReducing.NONE);//       circuitMatrix.numRows);
//	 ActionEvent ae = 	          new ActionEvent((Object)dummyButton, ActionEvent.ACTION_PERFORMED, "");
	
	DMatrixRMaj solution = circuitRightSide.copy();
	
	lastTime=System.nanoTime();
	if (1000 >= steprate*(tm-lastIterTime))
	    return;
//	 time4=0;time2=0;;time3=0;;time1=0;
	for (iter = 1; ; iter++) {
		
	//	lastTime=System.nanoTime();
	    int i, j, k, subiter;
	 /*   elmList.parallelStream().forEach((o) -> {
	        ((CircuitElm) o).startIteration();
	    });*/
	    for(CircuitElm e: elmList){
	    	e.startIteration();
	    }
	    /*
	    for (i = 0; i != elmList.size(); i++) {
		getElm(i).startIteration();;
		//ce.startIteration();
	    }*/
	    //time1+= System.nanoTime()-lastTime;lastTime=System.nanoTime();
	    steps++;
	    final int subiterCount = 5000;
	  //  lastTime=System.nanoTime();
	   ;
	    for (subiter = 0; subiter != subiterCount; subiter++) {
	    //	lastTime=System.nanoTime();
	    	converged = true;
	    	subIterations = subiter;

	    	//for (i = 0; i != circuitMatrixSize; i++)
	    	//    circuitRightSide[i] = origRightSide[i];
	    	circuitRightSide = origRightSide.copy();
	    	if (circuitNonLinear) {
	    		/*for (i = 0; i != circuitMatrixSize; i++)
			for (j = 0; j != circuitMatrixSize; j++)
			    circuitMatrix[i][j] = origMatrix[i][j];*/
	    		circuitMatrix = origMatrix.copy();
	    	}
	    	//time1+= System.nanoTime()-lastTime;
	    	//lastTime=System.nanoTime();
	    	/*elmList.parallelStream().filter(o->((CircuitElm) o).nonLinear()).forEach((o) -> {
        ((CircuitElm) o).doStep();
    });*/
	    	for(CircuitElm e: elmList){
	    		e.doStep();
	    	}
	    	/*for (i = 0; i != elmList.size(); i++) {
			getElm(i).doStep();
		    //CircuitElm ce = getElm(i).doStep();
		    //ce.doStep();
		}*/
	    		//time2+= System.nanoTime()-lastTime;
	    		//lastTime=System.nanoTime();
	    	if (stopMessage != null)
	    		return;
	  //  	boolean printit = debugprint;
	  //  	debugprint = false;
	    	/*	if (true){
		for (j = 0; j != circuitMatrixSize; j++) {
		    for (i = 0; i != circuitMatrixSize; i++) {
			double x = circuitMatrix.unsafe_get(i,j);//[i][j];
			if (Double.isNaN(x) || Double.isInfinite(x)) {
			    stop("nan/infinite matrix!", null);
			    return;
			}
		    }
		}
	    }*/
	    	
	    	// RECENTLY COMMENTED OUT THE FOLLOWING SAFE PROCEDURE:
	    	/*for (j = 0; j != circuitMatrix.nz_length; j++) {
	    		//double x = circuitMatrix.nz_values[j];//[i][j];
	    		if (Double.isNaN(circuitMatrix.nz_values[j]) || Double.isInfinite(circuitMatrix.nz_values[j])) {
	    			stop("nan/infinite matrix!", null);
	    			return;
	    		}
	    	}*/

	    
	    	//	lastTime=System.nanoTime();
	    	
	    	if (!solver.setA(circuitMatrix))
	    		stop("Couldn´t set Matrix A", null);
	    	if (circuitNonLinear) {
	    		if (converged && subiter > 0)
	    			break;

	    		//if (!lu_factor(circuitMatrix, circuitMatrixSize,
	    		//  circuitPermute)) {
	    		//System.out.println("solver quality: "+solver.quality());
	    		if (false){//(solver.quality()<1e-8) {
	    			stop("Singular matrix!", null);
	    			return;
	    		}
	    	}
	    //	time3+= System.nanoTime()-lastTime;lastTime=System.nanoTime();
	    	//lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute,
	    	//	 circuitRightSide);
	    	/*System.out.println("circuitMatrix: " + circuitMatrix);
		System.out.println("circuitMatrix: " + circuitMatrix.isFull());
		System.out.println("circuitMatrix size: "  + circuitMatrix.getNonZeroLength() + " " + circuitMatrix.getNumElements());
		System.out.println(circuitRightSide);System.out.println(solution);
	    	 */
	    	//System.out.println(solver);
	    	try {
	    		solver.solve(circuitRightSide,solution);
	    	}
	    	catch (Exception e)
	    	{
	    		reset(false);
	    		break;
	    	}
	    		//time3+= System.nanoTime()-lastTime;
	    		//lastTime=System.nanoTime();
	    	j=0;
	    	//for (j = 0; j != circuitMatrixFullSize; j++) {
	    		for(RowInfo ri:circuitRowInfo){
	    		//RowInfo ri = circuitRowInfo[j];
	    		double res = 0;
	    		if (ri.type == RowInfo.ROW_CONST)
	    			res = ri.value;
	    		else
	    			res = solution.get(ri.mapCol,0);//circuitRightSide[ri.mapCol];
	    		//  System.out.println(j + " " + res + " " +
	    		//    ri.type + " " + ri.mapCol);
	    		if (Double.isNaN(res)) {
	    			converged = false;
	    			//debugprint = true;
	    			break;
	    		}
	    		if (j < nodeList.size()-1) {
	    		
	    			//CircuitNode cn = getCircuitNode(j+1);
	    			for(CircuitNodeLink cnl: getCircuitNode(j+1).links){
	    				cnl.elm.setNodeVoltage(cnl.num, res);
	    			}
	    			/*for (k = 0; k != cn.links.size(); k++) {
			    CircuitNodeLink cnl = (CircuitNodeLink)
				cn.links.elementAt(k);
			    cnl.elm.setNodeVoltage(cnl.num, res);
			//    cn.links.elementAt(k).elm.setNodeVoltage(cn.links.elementAt(k).num, res);
			}*/

	    		} else {
	    			int ji = j-(nodeList.size()-1);
	    			//System.out.println("setting vsrc " + ji + " to " + res);
	    			voltageSources[ji].setCurrent(ji, res);
	    		}
	    		j++;
	    		if(j == circuitMatrixFullSize)
	    			break;
	    	}
	    		//time4+= System.nanoTime()-lastTime;
	    	//circuitRightSide = solution.copy();
	  //  	time5+= System.nanoTime()-lastTime;lastTime=System.nanoTime();

	    	if (!circuitNonLinear)
	    		break;
	    }
	    //  if (subiter > 5)
	    //System.out.print("processing time " + time1/1e3);//+ time4/1e3 + " " + time2/1e3+ " "  + time3/1e3+ " "  + time1/1e3+ " ");
	    
	  //  System.out.print("converged after " + subiter + " iterations\n");
	    if (subiter == subiterCount) {
	    	stop("Convergence failed!", null);
	    	break;
	    }
	    t += timeStep;
	    for (i = 0; i != scopeCount; i++)
	    	scopes[i].timeStep();
	    if (playThread.wform.elm!=null){ //(false){//
	    	//	System.out.println("timeStepping");
	    	lastTime=System.nanoTime();
	    	timeSoundElapsed+=timeStep;
	    	playSoundCount2++;
	    	//System.out.println("soundTime" + playSoundCount/playThread.wform.sampleRate);
	    	//System.out.println("stepTime" + playSoundCount2*timeStep);
	    	if (playSoundCount/soundSampleRate<timeSoundElapsed){//playSoundCount2*timeStep){

	    		synchronized  (playThread.synchObject){	
	    			playThread.wform.timeStep(//playThread.wform.elm.getVoltageDiff());
	    					lastSoundElmSample + (playThread.wform.elm.getVoltageDiff()-lastSoundElmSample)*
	    					(-(timeSoundElapsed-timeStep)+playSoundCount/soundSampleRate)/timeStep);//	(-(playSoundCount2-1)*timeStep+playSoundCount/soundSampleRate)/timeStep);
	    			//playThread.wform.timeStep();
	    			if (playThread.wform.getSampleCount()>=playThread.wform.getBufferLength())
	    				playThread.synchObject.notify();
	    			playSoundCount++;

	    		}
	    		if (playSoundCount/soundSampleRate>1)
	    		{playSoundCount=1;playSoundCount2=1;timeSoundElapsed=0;}//;timeStep=nextTimeStep
	    	}
	    	lastSoundElmSample  = playThread.wform.elm.getVoltageDiff();
	    	time1= System.nanoTime()-lastTime;lastTime=System.nanoTime();
	    	// 	    System.out.println("time per iter: " +time1);
	    }
	    tm = System.currentTimeMillis();
	    lit = tm;
	    counterSimulationPG++;
	    counterSimulationArduino++;
	    // SIMULATE ARDUINO
	    if (arduino.existsFlag&counterSimulationArduino>=periodRatioArduino){
	    	counterSimulationArduino = 0;
	    
	    	/*if ( arduino.interpreter.arduino.serialMessage!=null & usePanel)
	    		if  (!doSynchronizedSimulationPG)
	    			;
			/*	try {
					serialMessage = arduino.interpreter.arduino.serialMessage;
					proxy.feval("updateSerialMonitor");
				} catch (MatlabInvocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} A comment ended here * /
	    		else {
	    			if (serialMessage.length()>0)
	    			serialMessage = serialMessage + '\n' + arduino.interpreter.arduino.serialMessage;
	    			else
	    				serialMessage = arduino.interpreter.arduino.serialMessage;
	    		}
	    	*/
	    	arduino.cycle();
	    		//g.drawString(arduino.interpreter.arduino.serialMessage, 10, circuitArea.height-15);
	    		//}
	    }
	    // SIMULATE MATLAB PLAYGROUND
	    if (usePanel&MatlabBusy&counterSimulationPG>=2*periodRatioPG){
	    try {
			Thread.sleep(0,100000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    }
	    if (usePanel&!MatlabBusy){
	    if (doSynchronizedSimulationPG&counterSimulationPG>=periodRatioPG){
	    	counterSimulationPG=0;
	    	
	    	try {
	    		//	System.out.println("sleeping...");
	    		//	proxy.feval("pause",2);
	    	//	lastTime=System.nanoTime();
	    		//proxy.feval("pg_timerCallback",matlabApp);
	    		//	System.out.println(tm);
	    		//			System.out.println(lastIterTime);
	    		///					System.out.println(graphicUpdatesCount*50);
	    		

	    		//Thread.sleep(1);
	    		//currentBar.setValue(40);
	    	//	durationTextItem.setText("-1");
	    	
	    		if(tm-(lastMatlabUpdateGraphics) > matlabGraphicsFramerate){
	    			updateMatlabGraphics=true;
	    			//	System.out.println("****************************************************************************************");
	    			lastMatlabUpdateGraphics = tm;}
	    		
	    	
	    	MatlabBusy= true;
	    	//nMatlabCalls++;
	    //		       Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(ae);
	    	//}
			//nMatlabCallTries++;
  		/*	29/4  
  		 
	    		proxy.feval("pg_timerCallback");
*/
	    		matlabProxy.evalConsoleOutput("pg_timerCallback;");;
	    		//Matlab.mtFevalConsoleOutput("TriggerCallback",null,0);;
	    		//System.out.println("trigger here!");
	    		//triggerCls.setTrigger("aaaa");
	    		//Thread.sleep(1);
	    		//proxy.feval("dummyFunction");
	    		//System.out.println((lastTime-System.nanoTime())*1E-9);
	    		//	System.out.println("waking...");

	    	} catch  (Exception e) {//(MatlabInvocationException e) {//(InterruptedException e) {//
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	}
	    	
	    }
	    
	}
	    if (iter*1000 >= steprate*(tm-lastIterTime)||(tm-lastIterTime > 50)
	    		||analyzeFlag) // 
	    {//System.out.println("time exceeded");
	   // 	System.out.print("processing time " + (int)time1/iter + " " + (int)time2/iter+ " "  + (int)time3/iter+ " "  + (int)time4/iter+ " \n");
	   // 	System.out.println((int)(System.nanoTime()-lastTime)/iter);
	    	/*if (nMatlabCallTries>10&&doSynchronizedSimulationPG){
	    	System.out.println(((double)nMatlabCalls)/((double)nMatlabCallTries) + " "+nMatlabCallTries) ;
	    	nMatlabCalls=0;
	    	nMatlabCallTries=0;}*/
	    /*	if (doSynchronizedSimulationPG){
	    	//triggerCls.setTrigger("aaaa");
	    	//Object arg[] = { 5};
	    	try {
	    		proxy.eval("TriggerCallback();");
				//matlabProxy.evalConsoleOutput("TriggerCallback();");;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}}*/
	    	break;
	    }
	}
	time1= tm-lastIterTime;//System.nanoTime()-initialTm;
	//	System.out.println("time elapsed" + time1);
//	System.out.println("simulation time elapsed" + iter*timeStep*1e3);
	//ONLY ADJUST TIME STEP IF NOT SIMULATING ARDUINO - IN THAT CASE THE TIME STEP IS FIXED BT ARDUINO
	 if (playThread.wform.elm!=null&&time1<500&&!arduino.existsFlag){ //(false){//
	if ((time1>iter*timeStep*1e3*1.1)){//||(playThread.wform.sampleCount>=.75*playThread.wform.buffer.length)){
	timeStep=timeStep*1.1;
	System.out.println("increasing time step");
	}
	else if(time1<iter*timeStep*1e3*.8||(playThread.wform.sampleCount>=.75*playThread.wform.buffer.length)){
		System.out.println("decreasing time step");
		timeStep=timeStep*.9;
		
	}
	else if ((time1<iter*timeStep*1e3*1.1)&&(playThread.wform.sampleCount<=2*playThread.wform.shortBuffer)){
		timeStep=timeStep*1.1;
		System.out.println("increasing time step");
		}
	}
	lastIterTime = lit;
	//System.out.println((System.currentTimeMillis()-lastFrameTime)/(double) iter);
	//System.out.println( time1 + " " + time2 + " " + time3 + " " + time4 + " " + time5);
    }

    // --------------------------------------------------------------------
    //                            USER INTERFACE METHODS
    // ---------------------------------------------------------------------
    void editFuncPoint(int x, int y) {
    	// XXX
    	cv.repaint(pause);
    }
    void destroyFrame() {
    	//if (applet == null)
    	    frameWrapper.dispose();
    	//else
    	    // applet.destroyFrame();
        }

        public boolean handleEvent(Event ev) {
        	if (ev.id == Event.WINDOW_DESTROY) {
        		destroyFrame();
        		return true;
        	}
        	return super.handleEvent(ev);
        }
    public void componentHidden(ComponentEvent e){}
    public void componentMoved(ComponentEvent e){}
    public void componentShown(ComponentEvent e) {
	cv.repaint();
    }

    public void componentResized(ComponentEvent e) {
	handleResize();
	cv.repaint(100);
	this.repaint();
	}
    
    void handleResize() {
        winSize = cv.getSize();
	if (winSize.width == 0)
	    return;
	
	dbimage = (BufferedImage) main.createImage(winSize.width, winSize.height);
	dbimage = toCompatibleImage(dbimage);
	int h = winSize.height / 5;
	/*if (h < 128 && winSize.height > 300)
	  h = 128;*/
	circuitArea = new Rectangle(0, 0, winSize.width, winSize.height-h);
	int i;
	int minx = 1000, maxx = 0, miny = 1000, maxy = 0;
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    // centered text causes problems when trying to center the circuit,
	    // so we special-case it here
	    if (!ce.isCenteredText()) {
		minx = min(ce.x, min(ce.x2, minx));
		maxx = max(ce.x, max(ce.x2, maxx));
	    }
	    miny = min(ce.y, min(ce.y2, miny));
	    maxy = max(ce.y, max(ce.y2, maxy));
	}
	// center circuit; we don't use snapGrid() because that rounds
	int dx = (Math.round((circuitArea.width -(maxx-minx))/2-minx)/gridSize)*gridSize; //gridMask & ((circuitArea.width -(maxx-minx))/2-minx);
	int dy = (Math.round((circuitArea.height-(maxy-miny))/2-miny)/gridSize)*gridSize;//gridMask & ((circuitArea.height-(maxy-miny))/2-miny);
	/*if (dx+minx < 0)
	    dx = gridMask & (-minx);
	if (dy+miny < 0)
	    dy = gridMask & (-miny);*/
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    ce.move(dx, dy);
	}
	// after moving elements, need this to avoid singular matrix probs
	needAnalyze();
	circuitBottom = 0;
    }
    public void reset(boolean stop){ 
    	int i;
    	System.out.println("Doing reset");
    	if (!usePanel)
    		try {
    			simDuration = Double.parseDouble(durationTextItem.getText());}
    	catch (NumberFormatException ee)
    	{simDuration=-1;};
    	for (i = 0; i != elmList.size(); i++)
    		getElm(i).reset();
    	for (i = 0; i != scopeCount; i++)
    		scopes[i].resetGraph();
    	analyzeFlag = true;
    	t = 0;
    	if (arduino.exists()){
    		arduino.reload();
    		System.out.println("Doing reset");}
    	stoppedCheck.setState(stop);
    	cv.repaint();
    }
    public void actionPerformed(ActionEvent e) {
	String ac = e.getActionCommand();
	if (e.getSource() == resetButton) {
		int i;
		try {
		simDuration = Double.parseDouble(durationTextItem.getText());}
				catch (NumberFormatException ee)
		{simDuration=-1;};
		System.out.println("Doing reset");
				
	    // on IE, drawImage() stops working inexplicably every once in
	    // a while.  Recreating it fixes the problem, so we do that here.
	  //  dbimage = (BufferedImage) main.createImage(winSize.width, winSize.height);
	    
	    for (i = 0; i != elmList.size(); i++)
		getElm(i).reset();
	    for (i = 0; i != scopeCount; i++)
	    	scopes[i].resetGraph();
	    analyzeFlag = true;
	    t = 0;
	    if (arduino.exists()){
    		arduino.reload();
    		System.out.println("Doing reset");}
	    stoppedCheck.setState(false);
	    cv.repaint();
	}
	if (e.getSource() == dumpMatrixButton)
		dumpMatrix = true;
	if (e.getSource() == exportItem)
		filetools.doImport(false, false,circuitFilePath);
	if (e.getSource() == exportAsItem)
		filetools.doImport(false, false,new String(""));
	if (e.getSource() == optionsItem)
		doEdit(new EditOptions(this));
	if (e.getSource() == arduinoEditOptionItem)
		doEdit(new EditOptionsArduino(this));
	if (e.getSource() == arduinoSelectSketchItem)
		selectArduinoFile();
	if (e.getSource() == arduinoEditFileItem)
		try {
			Desktop.getDesktop().open(new File(arduino.getSourceFile()));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	if (e.getSource() == arduinoReloadItem)
		arduino.reload();
	if (e.getSource() == importItem)
		filetools.doImport(true, false,new String(""));
	if (e.getSource() == printItem){
		fileDialog = new FileDialog(frameWrapper, "Save", FileDialog.SAVE);
		fileDialog.setDirectory(this.getCodeBase().toString());
		fileDialog.setFile("*.svg");
		fileDialog.setVisible(true);
		String filename = fileDialog.getFile();
		String path = fileDialog.getDirectory();
		//File fileImage = new File(path+File.separator+filename );
		//String fileImageString = fileImage.toURI().toURL().toString();
		//Path saveFileName = Paths.get(path+filename);
		try {
			//System.out.println(saveFileName.toString());
			printableCheckItem.setState(true);
			filetools.saveImage(cv, path+filename,path);//saveFileName.toString());//"C:\\Users\\FranciscoMateus\\Documents\\circuito.ps");
		//	doExportComponent(cv, new Rectangle(0, 0, 1300, 1300), path+filename);
			printableCheckItem.setState(false);}
		catch (IOException ee) {
			ee.printStackTrace();
		} catch (PrintException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*catch (TranscoderException ee) {
			ee.printStackTrace();
		}*/
	} ;
	//if (e.getSource() == exportLinkItem)
	//    doImport(false, true);
	if (e.getSource() == undoItem)
		edittools.doUndo();
	if (e.getSource() == redoItem)
		edittools.doRedo();
	if (ac.compareTo("Cut") == 0) {
		if (e.getSource() != elmCutMenuItem)
			menuElm = null;
		edittools.doCut();
	}
	if (ac.compareTo("Copy") == 0) {
	    if (e.getSource() != elmCopyMenuItem)
		menuElm = null;
	    edittools.doCopy();
	}
	if (ac.compareTo("Paste") == 0)
	    edittools.doPaste();
	if (e.getSource() == selectAllItem)
	    doSelectAll();
	if (e.getSource() == exitItem) {
	    destroyFrame();
	    return;
	}
	if (ac.compareTo("stackAll") == 0)
	    stackAll();
	if (ac.compareTo("unstackAll") == 0)
	    unstackAll();
	if (e.getSource() == elmEditMenuItem)
	    doEdit(menuElm);
	if (ac.compareTo("Delete") == 0) {
		{//System.out.print("DOING DELETE");
	    if (e.getSource() != elmDeleteMenuItem)
		menuElm = null;}
	    edittools.doDelete();
	}
	if (e.getSource() == elmScopeMenuItem && menuElm != null) {
	    int i;
	    for (i = 0; i != scopeCount; i++)
		if (scopes[i].elm == null)
		    break;
	    if (i == scopeCount) {
		if (scopeCount == scopes.length)
		    return;
		scopeCount++;
		scopes[i] = new Scope(this);
		scopes[i].position = i;
		handleResize();
	    }
	    scopes[i].setElm(menuElm);
	}
	if (menuScope != -1) {
	    if (ac.compareTo("remove") == 0)
		scopes[menuScope].setElm(null);
	    if (ac.compareTo("speed2") == 0)
		scopes[menuScope].speedUp();
	    if (ac.compareTo("speed1/2") == 0)
		scopes[menuScope].slowDown();
	    if (ac.compareTo("scale") == 0)
		scopes[menuScope].adjustScale(.5);
	    if (ac.compareTo("maxscale") == 0)
		scopes[menuScope].adjustScale(1e-50);
	    if (ac.compareTo("stack") == 0)
		stackScope(menuScope);
	    if (ac.compareTo("unstack") == 0)
		unstackScope(menuScope);
	    if (ac.compareTo("selecty") == 0)
		scopes[menuScope].selectY();
	    if (ac.compareTo("reset") == 0)
		scopes[menuScope].resetGraph();
	    cv.repaint();
	}
	if (ac.indexOf("setup ") == 0) { // THIS IS TO LOAD EXAMPLES FROM SETUPLIST
		edittools.pushUndo();
	    filetools.readSetupFile(ac.substring(6),
			  ((MenuItem) e.getSource()).getLabel());
	}
    }
/*public void printCircuit(String filename, String path){
	printableCheckItem.setState(true);
	try {
		saveImage(cv, filename,path);
	} catch (PrintException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}//saveFileName.toString());//"C:\\Users\\FranciscoMateus\\Documents\\circuito.ps");
//	doExportComponent(cv, new Rectangle(0, 0, 1300, 1300), path+filename);
	printableCheckItem.setState(false);
}*/
    
    //--------------------------------------------------------------------------------------
    // ---------------------------- SCOPES--------------------------------------------------
    //----------------------------------------------------------------------------------------
    void setupScopes() {
    	int i;
    	
    	// check scopes to make sure the elements still exist, and remove
    	// unused scopes/columns
    	int pos = -1;
    	for (i = 0; i < scopeCount; i++) {
    	    if (locateElm(scopes[i].elm) < 0)
    		scopes[i].setElm(null);
    	    if (scopes[i].elm == null) {
    		int j;
    		for (j = i; j != scopeCount; j++)
    		    scopes[j] = scopes[j+1];
    		scopeCount--;
    		i--;
    		continue;
    	    }
    	    if (scopes[i].position > pos+1)
    		scopes[i].position = pos+1;
    	    pos = scopes[i].position;
    	}
    	while (scopeCount > 0 && scopes[scopeCount-1].elm == null)
    	    scopeCount--;
    	int h = winSize.height - circuitArea.height;
    	pos = 0;
    	for (i = 0; i != scopeCount; i++)
    	    scopeColCount[i] = 0;
    	for (i = 0; i != scopeCount; i++) {
    	    pos = max(scopes[i].position, pos);
    	    scopeColCount[scopes[i].position]++;
    	}
    	int colct = pos+1;
    	int iw = infoWidth;
    	if (colct <= 2)
    	    iw = iw*3/2;
    	int w = (winSize.width-iw) / colct;
    	int marg = 10;
    	if (w < marg*2)
    	    w = marg*2;
    	pos = -1;
    	int colh = 0;
    	int row = 0;
    	int speed = 0;
    	for (i = 0; i != scopeCount; i++) {
    	    Scope s = scopes[i];
    	    if (s.position > pos) {
    		pos = s.position;
    		colh = h / scopeColCount[pos];
    		row = 0;
    		speed = s.speed;
    	    }
    	    if (s.speed != speed) {
    		s.speed = speed;
    		s.resetGraph();
    	    }
    	    Rectangle r = new Rectangle(pos*w, winSize.height-h+colh*row,
    					w-marg, colh);
    	    row++;
    	    if (!r.equals(s.rect))
    		s.setRect(r);
    	}
        }
        
    void stackScope(int s) {
	if (s == 0) {
	    if (scopeCount < 2)
		return;
	    s = 1;
	}
	if (scopes[s].position == scopes[s-1].position)
	    return;
	scopes[s].position = scopes[s-1].position;
	for (s++; s < scopeCount; s++)
	    scopes[s].position--;
    }
    
    void unstackScope(int s) {
	if (s == 0) {
	    if (scopeCount < 2)
		return;
	    s = 1;
	}
	if (scopes[s].position != scopes[s-1].position)
	    return;
	for (; s < scopeCount; s++)
	    scopes[s].position++;
    }

    void stackAll() {
	int i;
	for (i = 0; i != scopeCount; i++) {
	    scopes[i].position = 0;
	    scopes[i].showMax = scopes[i].showMin = false;
	}
    }

    void unstackAll() {
	int i;
	for (i = 0; i != scopeCount; i++) {
	    scopes[i].position = i;
	    scopes[i].showMax = true;
	}
    }
    
    void doEdit(Editable eable) {
	clearSelection();
	edittools.pushUndo();
	if (editDialog != null) {
	    requestFocus();
	    editDialog.setVisible(false);
	    editDialog = null;
	}
	editDialog = new EditDialog(eable, this);
	editDialog.show();
    }

    public void selectArduinoFile(){
    	if (selFileDialog != null) {
    		requestFocus();
    		selFileDialog.setVisible(false);
    		selFileDialog = null;
    	}
    	selFileDialog = new SelectFileDialog(this);
    	if (selectedFileName!=null)
    	{sketchURL = selectedFile;
    	sketchName = selectedFileName;
    //	arduinoSketchItem.setLabel("Sketch: "+selectedFileName);
    	arduino.reload();}
    }
 /*  public  String doImport(boolean imp, boolean url, String path) {
	   // imp true means open file, imp false means save file
    	int currentScale = gridSize;
    	if (impDialog != null) {
    		requestFocus();
    		impDialog.setVisible(false);
    		impDialog = null;
    	}

    	if (imp){
    		gridSize= defaultGridSize; // set default gridsize when opening
    	offsetX=0;
    	offsetY=0;}
    	// (saving is done at default grid size)
    	else {
    		gridSize = defaultGridSize;
    		uitools.resetOffset();
    		uitools.scaleCircuit(0, 0, gridSize, currentScale);
    		needAnalyze();
    		System.out.print(offsetX);
    		System.out.print(offsetY);
    	}
    	String dump = (imp) ? "" : dumpCircuit();
    //	/*if (false) {//(!imp){
    //		
    //		gridSize = currentScale;
    //		scaleCircuit(offsetX, offsetY, gridSize, defaultGridSize);
    //		needAnalyze();}
//
    	if (url)
    		dump = baseURL + "#" + URLEncoder.encode(dump);
    	if (path.length()==0)
    	impDialog = new ImportDialog(this, dump, url);
    	else{
    		if (imp){
    			try {
    			File file = new File(path );
    			InputStream in = file.toURI().toURL().openStream();
    			//in = new URL(path+filename ).openStream();
    			String content = IOUtils.toString( in,"UTF-8" ) ;
    			IOUtils.closeQuietly(in);
    			filetools.readSetup(content);		
    			} catch (MalformedURLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	}
    		else if (!path.equals("noFile"))
    		{
    			try {
    				//	Path file = Paths.get(path+filename);
    					//Files.write(file, inStr.getBytes());
    					File file = new File(path );
    					FileUtils.writeStringToFile(file, dump,"UTF-8");


    					} catch (IOException e) {
    				        e.printStackTrace();
    				    }
    		}
    	}
    	//impDialog.show();
    	edittools.pushUndo();
    	return (dump);
    }*/

    String dumpCircuit() {

	int i;
	int f = (dotsCheckItem.getState()) ? 1 : 0;
	f |= (smallGridCheckItem.getState()) ? 2 : 0;
	f |= (voltsCheckItem.getState()) ? 0 : 4;
	f |= (powerCheckItem.getState()) ? 8 : 0;
	f |= (showValuesCheckItem.getState()) ? 0 : 16;
	// 32 = linear scale in afilter
	String dump = "$ " + f + " " +
	    timeStep + " " + getIterCount() + " " +
	    currentBar.getValue() + " " + CircuitElm.voltageRange + " " +
	    powerBar.getValue() + "\n";
	if (arduino.exists())
		dump= dump + "&" + " " +sketchName + " " + sketchURL + "\n";
	for (i = 0; i != elmList.size(); i++)
	    dump += getElm(i).dump() + "\n";
	for (i = 0; i != scopeCount; i++) {
	    String d = scopes[i].dump();
	    if (d != null)
		dump += d + "\n";
	}
	if (hintType != -1)
	    dump += "h " + hintType + " " + hintItem1 + " " +
		hintItem2 + "\n";
	
	return dump;
	}
    
    public void adjustmentValueChanged(AdjustmentEvent e) {
	System.out.print(((Scrollbar) e.getSource()).getValue() + "\n");
    }

    ByteArrayOutputStream readUrlData(URL url) throws java.io.IOException {
	Object o = url.getContent();
	FilterInputStream fis = (FilterInputStream) o;
	ByteArrayOutputStream ba = new ByteArrayOutputStream(fis.available());
	int blen = 1024;
	byte b[] = new byte[blen];
	while (true) {
	    int len = fis.read(b);
	    if (len <= 0)
		break;
	    ba.write(b, 0, len);
	}
	return ba;
    }

    URL getCodeBase() {
    	try {
    		//if (true){//(applet != null){
    		if (!applet.startFolder.isEmpty()){
    			return  new URL("file:///" + applet.startFolder + "/");}
    		else{
    		//	return applet.getCodeBase();}}
    		File f = new File(".");
    		return new URL("file:" + f.getCanonicalPath() + "/");}
    		} catch (Exception e) {
    			e.printStackTrace();
    			return null;
    		}
    	}
    
    boolean doSwitch(int x, int y) {
	if (mouseElm == null || !(mouseElm instanceof SwitchElm))
	{return false;}
	SwitchElm se = (SwitchElm) mouseElm;
	se.toggle();
	if (se.momentary)
	    heldSwitchElm = se;
	needAnalyze();
	//triggerCls.setTrigger("triggered");
	return true;
    }

    public int locateElm(CircuitElm elm) {
	int i;
	for (i = 0; i != elmList.size(); i++)
	    if (elm == elmList.elementAt(i))
		return i;
	return -1;
    }
    
    public void mouseDragged(MouseEvent e) {
	// ignore right mouse button with no modifiers (needed on PC)
    	//System.out.println("DRA");
	if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
	    int ex = e.getModifiersEx();
	    if (((ex & (MouseEvent.META_DOWN_MASK|
		       MouseEvent.SHIFT_DOWN_MASK|
		       MouseEvent.CTRL_DOWN_MASK|
		       MouseEvent.ALT_DOWN_MASK)) == 0)&(tempMouseMode!=MODE_DRAG_ALL)){
	    	//dragAll(snapGrid(e.getX(),true), snapGrid(e.getY(),false));
		return;}
	}
	dragged  = true;
	if (!circuitArea.contains(e.getX(), e.getY()))
	    return;
	if (dragElm != null)
	    dragElm.drag(e.getX(), e.getY());
	boolean success = true;
	if (mouseElm != null){
		if(!mouseElm.isSelected())	{
			if (uitools.checkAnySelected())
				uitools.resetSelection();
			mouseElm.setSelected(true);
			setupCheckElem();
			updateMatlabSelection(true);}
	}
	switch (tempMouseMode) {
	case MODE_DRAG_ALL:
		uitools.dragAll(uitools.snapGrid(e.getX(),true), uitools.snapGrid(e.getY(),false));
	    break;
	case MODE_DRAG_ROW:
		uitools.dragRow(uitools.snapGrid(e.getX(),true), uitools.snapGrid(e.getY(),false));
	    break;
	case MODE_DRAG_COLUMN:
		uitools.dragColumn(uitools.snapGrid(e.getX(),true), uitools.snapGrid(e.getY(),false));
	    break;
	case MODE_DRAG_POST:
		if (mouseElm != null)
			//if (mouseElm.getPostCount()>1)
				uitools.dragPost(uitools.snapGrid(e.getX(),true), uitools.snapGrid(e.getY(),false));
		break;
	case MODE_SELECT:
	    if (mouseElm == null){
	    	uitools.selectArea(e.getX(), e.getY());
	    setupCheckElem();}
	    else {
	    	if (selectPost==0|uitools.checkHowManySelected()>1){
		tempMouseMode = MODE_DRAG_SELECTED;		
		if(!mouseElm.isSelected())
		//	mouseElm.setSelected(true);
		//setupCheckElem();
		success = uitools.dragSelected(e.getX(), e.getY());}
	    	else{tempMouseMode =MODE_DRAG_POST;uitools.dragPost(uitools.snapGrid(e.getX(),true), uitools.snapGrid(e.getY(),false));}
	    }
	    break;
	case MODE_DRAG_SELECTED:
	    success = uitools.dragSelected(e.getX(), e.getY());
	    break;
	}
	dragging = true;
	if (success) {
	    if (tempMouseMode == MODE_DRAG_SELECTED && mouseElm instanceof TextElm) {
		dragX = e.getX(); dragY = e.getY();
	    } else {
		dragX = uitools.snapGrid(e.getX(),true); dragY = uitools.snapGrid(e.getY(),false);
	    }
	}
	cv.repaint(pause);
    }

      public void mouseWheelMoved(MouseWheelEvent e)
    {//int newGridSize = gridSize;
    //int oldGridSize = gridSize;
    	  if (oldGridSize!=gridSize)
    		  return;
     oldGridSize = gridSize;
  //  int oldCsize = Csize;
	  // int xMouse=uitools.snapGrid( e.getX(),true);
    //int yMouse =uitools.snapGrid(e.getY(),false);
   
    //int nx=0; 
    //int ny=0; 
   // int dx=0; int dy=0;
  //  System.out.println("roll on!");
    System.out.println(gridSize);
   
    System.out.println(e.getComponent()== this.cv);
    System.out.println(e.getSource());
    int newMousex = e.getX();//(int)Math.round((float)e.getX()/(float)gridSize)*gridSize;
    int newMousey = e.getY();//(int)Math.round((float)e.getY()/(float)gridSize)*gridSize;
    
   System.out.println("--------------");
   System.out.println(newMousex + " " + newMousey);
   Dimension wsize = cv.getSize();
   System.out.println(wsize.width + " " + wsize.height);
  
   if (newMousex>wsize.width|newMousey>wsize.height|newMousex<0|newMousey<0) 
	   return; // RETURN, IN CASE MOUSE POINTER IS OUTSIDE THE CIRCUIT AREA
    
    
  //  System.out.println(e.getScrollType());
    if (true)// ((e.getScrollType()&MouseWheelEvent.WHEEL_BLOCK_SCROLL )!=0	)
    { //System.out.println("roll on!!!!!");
    gridSize = (e.getWheelRotation()>0)? gridSize-1: gridSize+1; //INCREMENT gridzize
    e.consume() ;
  //  Csize = (e.getWheelRotation()>0)? Csize-2: Csize+2;
    if ((gridSize<minGridSize)||(gridSize>30)){
    	gridSize = oldGridSize;
  //  	Csize = oldCsize;
    	return;
    }
    
    uitools.scaleCircuit(newMousex, newMousey, gridSize, oldGridSize);
   
    }
   // gridSize = newGridSize;
    needAnalyze();
    //cv.repaint();
    //removeZeroLengthElements();
    }
      
    public void removeZeroLengthElements() {
	int i;
	boolean changed = false;
	for (i = elmList.size()-1; i >= 0; i--) {
	    CircuitElm ce = getElm(i);
	    if (ce.x == ce.x2 && ce.y == ce.y2) {
		elmList.removeElementAt(i);
		ce.delete();
		changed = true;
	    }
	}
	needAnalyze();
    }

    public void mouseMoved(MouseEvent e) {
	doMouseMovedEvent(e);
    }
    
public void doMouseMovedEvent( MouseEvent e){
    	//if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
    	//    return;
    	int dist2diag;
    	int x = e.getX();
    	int y = e.getY();
    	dragX = uitools.snapGrid(x,true); dragY = uitools.snapGrid(y,false);
    	draggingPost = -1;
    	int i;
    	CircuitElm origMouse = mouseElm;
    	mouseElm = null;
    	mousePost = -1;
    	plotXElm = plotYElm = null;
    	int bestDist = 100000;
    	int bestArea = 100000;
    	for (i = 0; i != elmList.size(); i++) {
    		
	    CircuitElm ce = getElm(i);
	   
	    if (true) {//(ce.boundingBox.contains(x, y)) {
		int j;
		int area = ce.boundingBox.width * ce.boundingBox.height;
		int centerX = (int) (ce.boundingBox.getCenterX());
		int centerY = (int) (ce.boundingBox.getCenterY());
		int dist2centre = distanceSq(x, y, centerX, centerY);

		int jn = ce.getPostCount();
	
		 if (!(ce instanceof TextElm)){
		 dist2diag = ce.distance2diag(x,y);}
		 else
		 {
		 dist2diag = dist2centre;
		 }
		if (jn== 1)//(jn > 2||jn== 1)
			jn = 2;
		if (dist2diag <= bestDist&& dist2diag<(4*gridSize*gridSize)){//&& area <= bestArea) {
			bestDist = dist2diag;
			mouseElm = ce;
			selectPost=0;
			if (jn==2)
			for (j = 0; j != jn; j++) {
				Point pt = ce.getPost(j);
				int dist = distanceSq(x, y, pt.x, pt.y);

				// if multiple elements have overlapping bounding boxes,
				// we prefer selecting elements that have posts close
				// to the mouse pointer and that have a small bounding
				// box area.

				//	bestArea = area;
				if (dist<dist2centre/2){
					selectPost=1;mousePost=j;//System.out.println("selectedPost");  
				}
				//else
				//System.out.println(selectPost);
			}
		}
		if (ce.getPostCount() == 0&&!(ce instanceof TextElm))
		    mouseElm = ce;
	    }
	}
	scopeSelected = -1;
	if (mouseElm == null) {
	    for (i = 0; i != scopeCount; i++) {
		Scope s = scopes[i];
		if (s.rect.contains(x, y)) {
		    s.select();
		    scopeSelected = i;
		}
	    }
	    // the mouse pointer was not in any of the bounding boxes, but we
	    // might still be close to a post
	    for (i = 0; i != elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		int j;
		int jn = ce.getPostCount();
		for (j = 0; j != jn; j++) {
		    Point pt = ce.getPost(j);
		    int dist = distanceSq(x, y, pt.x, pt.y);
		    if (distanceSq(pt.x, pt.y, x, y) < 26) {
			mouseElm = ce;
			mousePost = j;
			break;
		    }
		}
	    }
	} else {
	    mousePost = -1;
	    // look for post close to the mouse pointer
	    for (i = 0; i != mouseElm.getPostCount(); i++) {
		Point pt = mouseElm.getPost(i);
		if (distanceSq(pt.x, pt.y, x, y) < 26)
		    mousePost = i;
	    }
	}
	if (mouseElm != origMouse)
	    cv.repaint();
    }

    
    public void mouseClicked(MouseEvent e) {
    	if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) { // left mouse button
    		if (mouseMode == MODE_SELECT || mouseMode == MODE_DRAG_SELECTED){
    			if (mouseElm!=null){
    				//System.out.println(e.getModifiers());
    				//System.out.println(MouseEvent.SHIFT_DOWN_MASK);
    				if (((e.getModifiersEx() &MouseEvent.SHIFT_DOWN_MASK)==0)&(uitools.selectedCount()>0))
    				{//System.out.println("clearing selection");
    					clearSelection();}
    				mouseElm.selected=true;
    				updateMatlabSelection(true);}
    				//System.out.println("setting selected");}
    			else
    				clearSelection();
    				
    		}
    	}

    }	


    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
	scopeSelected = -1;
	mouseElm = plotXElm = plotYElm = null;
	cv.repaint();
    }
    
    public void mousePressed(MouseEvent e) {
//	System.out.println(e.getModifiers());
	int ex = e.getModifiersEx();
	if ((ex & (MouseEvent.META_DOWN_MASK| //(false)//
		   MouseEvent.SHIFT_DOWN_MASK)) == 0 && e.isPopupTrigger()) 
		{
		doPopupMenu(e);
	    return;
	}
	dragged = false;
	
	
	if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
	    // left mouse
		doubleclick=0;
		 buttonPressed=1;
		 popupOpen = false;
		 if ((e.getClickCount() == 2) & (mouseMode==MODE_ADD_ELM)){
		//	 Cursor CS= getCursor();
			// System.out.println(this.getCursor());
			    main.setCursor(Cursor.getDefaultCursor());
			 //   System.out.println(this.getCursor());
			 mouseMode = MODE_SELECT;}
	    tempMouseMode = mouseMode;
	    //if (selectPost==1)
		//	tempMouseMode = MODE_DRAG_POST;
	    if ((ex & MouseEvent.ALT_DOWN_MASK) != 0 &&
		(ex & MouseEvent.META_DOWN_MASK) != 0)
		tempMouseMode = MODE_DRAG_COLUMN;
	    else if ((ex & MouseEvent.ALT_DOWN_MASK) != 0 &&
		     (ex & MouseEvent.SHIFT_DOWN_MASK) != 0)
		tempMouseMode = MODE_DRAG_ROW;
	    else if ((ex & MouseEvent.SHIFT_DOWN_MASK) != 0){
		tempMouseMode = MODE_SELECT;}//System.out.println("shiftdown");}
	    else if ((ex & MouseEvent.ALT_DOWN_MASK) != 0)
		tempMouseMode = MODE_DRAG_ALL;
	    else if ((ex & (MouseEvent.CTRL_DOWN_MASK|
			    MouseEvent.META_DOWN_MASK)) != 0)
		tempMouseMode = MODE_DRAG_POST;
	    else if (e.getClickCount() == 2) 
	    	doubleclick=1;
	    
	    
	} else if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
	    // right mouse
		if(popupOpen)
			doMouseMovedEvent(e);
		 buttonPressed=3;
	    if ((ex & MouseEvent.SHIFT_DOWN_MASK) != 0)
		tempMouseMode = MODE_DRAG_ROW;
	    else if ((ex & (MouseEvent.CTRL_DOWN_MASK|
			    MouseEvent.META_DOWN_MASK)) != 0)
		tempMouseMode = MODE_DRAG_SELECTED;//= MODE_DRAG_COLUMN;
	   
	    /*else if (!popupOpen&&mouseElm==null)
	    {
	    	tempMouseMode = MODE_DRAG_ALL;
	    	//System.out.println("GOING DRAGGG");
	    	}*/
	    else
		return;
	}
	
	if (tempMouseMode != MODE_SELECT && tempMouseMode != MODE_DRAG_SELECTED)
	    clearSelection();
	if (doSwitch(e.getX(), e.getY()))
	    return;
	edittools.pushUndo();
	initDragX = e.getX();
	initDragY = e.getY();
	dragging = true;
	if (tempMouseMode != MODE_ADD_ELM || addingClass == null)
	    return;
	
	int x0 = uitools.snapGrid(e.getX(),true);
	int y0 = uitools.snapGrid(e.getY(),false);
	if (!circuitArea.contains(x0, y0))
	    return;

	dragElm = constructElement(addingClass, x0, y0);
    }

    CircuitElm constructElement(Class c, int x0, int y0) {
	// find element class
	Class carr[] = new Class[2];
	//carr[0] = getClass();
	carr[0] = carr[1] = int.class;
	Constructor cstr = null;
	try {
	    cstr = c.getConstructor(carr);
	} catch (NoSuchMethodException ee) {
	    System.out.println("caught NoSuchMethodException ---- " + c);
	    return null;
	} catch (Exception ee) {
	    ee.printStackTrace();
	    return null;
	}

	// invoke constructor with starting coordinates
	Object oarr[] = new Object[2];
	oarr[0] = new Integer(x0);
	oarr[1] = new Integer(y0);
	try {
		cstr.setAccessible(true);
	    return (CircuitElm) cstr.newInstance(oarr);
	} catch (Exception ee) { ee.printStackTrace(); }
	return null;
    }
    
    void doPopupMenu(MouseEvent e) {
    	
    	popupOpen=true;
	menuElm = mouseElm;
	menuScope = -1;
	if (scopeSelected != -1) {
	    PopupMenu m = scopes[scopeSelected].getMenu();
	    menuScope = scopeSelected;
	    if (m != null)
		m.show(e.getComponent(), e.getX(), e.getY());
	} else if (mouseElm != null) {
		//System.out.println("mouseElm geteditinfo: " +mouseElm);
	    elmEditMenuItem .setEnabled(mouseElm.getEditInfo(0) != null);
	    elmScopeMenuItem.setEnabled(mouseElm.canViewInScope());
	    elmMenu.show(e.getComponent(), e.getX(), e.getY());
	} else {
	    doMainMenuChecks( mainMenu);
	    mainMenu.show(e.getComponent(), e.getX(), e.getY());
	}
	
    }

   
    
    void doMainMenuChecks(Menu m) {
	int i;
	if (m == optionsMenu)
	    return;
	for (i = 0; i != m.getItemCount(); i++) {
	    MenuItem mc = m.getItem(i);
	    if (mc instanceof Menu)
		doMainMenuChecks((Menu) mc);
	    if (mc instanceof CheckboxMenuItem) {
		CheckboxMenuItem cmi = (CheckboxMenuItem) mc;
		cmi.setState(
		      mouseModeStr.compareTo(cmi.getActionCommand()) == 0);
	    }
	}
    }
    
    public void mouseReleased(MouseEvent e) {
	int ex = e.getModifiersEx();
	if ((ex & (MouseEvent.SHIFT_DOWN_MASK|MouseEvent.CTRL_DOWN_MASK|
		   MouseEvent.META_DOWN_MASK)) == 0 && e.isPopupTrigger()&&!dragged) {
		tempMouseMode = mouseMode;
		dragging = false;
		//System.out.println("going popup");
	    doPopupMenu(e);
	    return;
	}
	tempMouseMode = mouseMode;
	selectedArea = null;
	dragging = false;
	boolean circuitChanged = false;
	if (heldSwitchElm != null) {
	    heldSwitchElm.mouseUp();
	    heldSwitchElm = null;
	    circuitChanged = true;
	}
	if (dragElm != null) {
	    // if the element is zero size then don't create it
	    if (dragElm.x == dragElm.x2 && dragElm.y == dragElm.y2)
		dragElm.delete();
	    else {
		elmList.addElement(dragElm);
		circuitChanged = true;
	    }
	    dragElm = null;
	}
	if (circuitChanged)
	    needAnalyze();
	if (dragElm != null)
	    dragElm.delete();
	dragElm = null;
	cv.repaint();
    }

    void enableItems() {
	if (powerCheckItem.getState()) {
		//System.out.println(powerBar);
	    powerBar.enable();
	    powerLabel.enable();
	} else {
	    powerBar.disable();
	    powerLabel.disable();
	}
	edittools.enableUndoRedo();
    }
    
    public void itemStateChanged(ItemEvent e) {
	cv.repaint(pause);
	Object mi = e.getItemSelectable();
	if (mi == stoppedCheck){
		if (!stoppedCheck.getState()){
			System.out.println("running circuit");
		//arduino.interpreter.start();
		}
	    return;}
	if (mi == smallGridCheckItem)
	   // setGrid();
	if (mi == powerCheckItem) {
	    if (powerCheckItem.getState())
		voltsCheckItem.setState(false);
	    else
		voltsCheckItem.setState(true);
	}
	if (mi == voltsCheckItem && voltsCheckItem.getState())
	    powerCheckItem.setState(false);
	enableItems();
	if (menuScope != -1) {
	    Scope sc = scopes[menuScope];
	    sc.handleMenu(e, mi);
	}
	if (mi instanceof CheckboxMenuItem) {
	    MenuItem mmi = (MenuItem) mi;
	   // Cursor CS= getCursor();
	   // setCursor(Cursor.getDefaultCursor()); // CS.CROSSHAIR_CURSOR);
	  //  System.out.println("setting cursor");
	    main.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	    		//CS.CROSSHAIR_CURSOR); // CS.CROSSHAIR_CURSOR);
	    mouseMode = MODE_ADD_ELM;
	    checkBoxDragElem = 0;
	    String s = mmi.getActionCommand();
	    if (s.length() > 0)
		mouseModeStr = s;
	    if (s.compareTo("DragAll") == 0)
		mouseMode = MODE_DRAG_ALL;
	    else if (s.compareTo("DragRow") == 0)
		mouseMode = MODE_DRAG_ROW;
	    else if (s.compareTo("DragColumn") == 0)
		mouseMode = MODE_DRAG_COLUMN;
	    else if (s.compareTo("DragSelected") == 0)
		mouseMode = MODE_DRAG_SELECTED;
	    else if (s.compareTo("DragPost") == 0)
		mouseMode = MODE_DRAG_POST;
	    else if (s.compareTo("DragElement") == 0){
	    	checkBoxDragElem = 1;
			mouseMode = MODE_DRAG_SELECTED;}
	    else if (s.compareTo("Select") == 0)
		mouseMode = MODE_SELECT;
	    else if (s.length() > 0) {
		try {
		    addingClass = Class.forName("circuitArduino." + s);
		} catch (Exception ee) {
		    ee.printStackTrace();
		}
	    }
	    tempMouseMode = mouseMode;
	}
    }

    void setGrid() {
	minGridSize = 4;//(smallGridCheckItem.getState()) ? 4 : 16;
	gridMask = ~(minGridSize-1);
	gridRound = minGridSize/2-1;
    }

 

    void setMenuSelection() {
	if (menuElm != null) {
	    if (menuElm.selected)
		return;
	    clearSelection();
	    menuElm.setSelected(true);
	}
    }
   

    void clearSelection() {
	int i;
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    ce.setSelected(false);
	}
	updateMatlabSelection(false);
    }
    public void updateMatlabSelection(boolean flag){
    	if (usePanel)
    		
 		   try {
 			   if (flag)
 				  // proxy.eval("pg_select_PG_elements");
 				matlabProxy.evalConsoleOutput("pg_select_PG_elements");
 			   else
 				  //proxy.eval("pg_deselect_PG_elements");
 				  matlabProxy.evalConsoleOutput("pg_deselect_PG_elements");
 			} catch (Exception e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
    }
    void doSelectAll() {
	int i;
	for (i = 0; i != elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    ce.setSelected(true);
	}
    }
    boolean controlKeyOn;
   // private final Set<Integer> pressedKeys = new HashSet<>();
    public  void keyPressed(KeyEvent e) {
    	if (e.getKeyCode()==KeyEvent.VK_CONTROL) {
    		 controlKeyOn = true; 
		}
    if (controlKeyOn)
    	if (e.getKeyCode() == 'C') 
			edittools.doCopy();
    	else if(e.getKeyCode() == 'E')
    		edittools.doPaste();
    }

    public void keyReleased(KeyEvent e) { 
    	if (e.getKeyCode()==KeyEvent.VK_CONTROL)
		controlKeyOn = false;  };
    
    public void keyTyped(KeyEvent e) {
    	int ex = e.getModifiersEx();
	if (e.getKeyChar() > ' ' && e.getKeyChar() < 127) {
	    Class c = dumpTypes[e.getKeyChar()];
	    if (c == null || c == Scope.class)
		return;
	    CircuitElm elm = null;
	    elm = constructElement(c, 0, 0);
	    if (elm == null || !(elm.needsShortcut() && elm.getDumpClass() == c))
		return;
	    mouseMode = MODE_ADD_ELM;
	    mouseModeStr = c.getName();
	    addingClass = c;
	}
	if (e.getKeyChar() == ' '||e.getKeyChar() == 27) {
	    mouseMode = MODE_SELECT;
	    mouseModeStr = "Select";
	    
	    main.setCursor(Cursor.getDefaultCursor()); //CS.DEFAULT_CURSOR);
	}
	if (e.getKeyChar() == 127) {
	    edittools.doDelete();
	}
	
	tempMouseMode = mouseMode;
    }
    
    
  

   
 
   
    private BufferedImage toCompatibleImage(BufferedImage image)
    {
        // obtain the current system graphical settings
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.
            getLocalGraphicsEnvironment().getDefaultScreenDevice().
            getDefaultConfiguration();

        /*
         * if image is already compatible and optimized for current system 
         * settings, simply return it
         */
        if (image.getColorModel().equals(gfxConfig.getColorModel()))
            return image;

        // image is not optimized, so create a new image that is
        BufferedImage newImage = gfxConfig.createCompatibleImage(
                image.getWidth(), image.getHeight(), image.getTransparency());

        // get the graphics context of the new image to draw the old image on
        Graphics2D g2d = newImage.createGraphics();

        // actually draw the image and dispose of context no longer needed
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // return the new optimized image
        return newImage; 
    }
    // -------------------------------------------------------------------------------------------------------------------
    // --------------------------                  MATH FUNCTIONS            -------------------------------------------
    // --------------------------------------------------------------------------------------------------------------------
    int distanceSq(int x1, int y1, int x2, int y2) {
    	x2 -= x1;
    	y2 -= y1;
    	return x2*x2+y2*y2;
        }
    int min(int a, int b) { return (a < b) ? a : b; }
    int max(int a, int b) { return (a > b) ? a : b; }
    public int getrand(int x) {
    	int q = random.nextInt();
    	if (q < 0) q = -q;
    	return q % x;
        }
       
}
