package circuitArduino.components.arduino;
import java.awt.Checkbox;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.StringTokenizer;

//import com.dandigit.jlox.ArduinoLox;
import com.kollins.project.sofia.atmega328p.iomodule_atmega328p.output.OutputFragment_ATmega328P;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;
public class ArduDO extends CircuitElm {
	//final int FLAG_VALUE = 1;
	public double vOutput=1.3;
	public int port=0;
	//private ArduinoLox arduino;
	private OutputFragment_ATmega328P simulatorOutputs;
	public ArduDO(int xx, int yy) { super(xx, yy);   
//	arduino =sim.arduino.interpreter.arduino;
	simulatorOutputs = sim.arduino.simulatorOutputs;
	noDiagonal = true;
	boolean testExist=true;
	int i;
	if (sim.arduino.exists())
	if (sim.elmList!=null)
		for (i=0;i!=simulatorOutputs.pinbuffer.length;i++){
			testExist=true;
			for (CircuitElm ce: sim.elmList){
				if (ce instanceof ArduDO) 
					if (((ArduDO)ce).port==i){testExist=false; break;};
			}
			if(testExist==true) {port=i;break;}
		}

	}
	public ArduDO(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		//arduino =sim.arduino.interpreter.arduino;
		simulatorOutputs = sim.arduino.simulatorOutputs;
		port= new Integer(st.nextToken());//.intValue();
		noDiagonal = true;
	}
	protected int getDumpType() { return 203; }
	protected String dump() {

		return super.dump() + " " + port;
	}
	protected int getPostCount() { return 1; }
	protected void setPoints() {
		super.setPoints();
		lead1 = new Point();
	}
	protected void draw(Graphics g) {
		int hs = sim.gridSize;
		boolean selected = (needsHighlight() || sim.plotYElm == this);
		Font f = new Font("SansSerif", selected ? Font.BOLD : 0, 14);
		g.setFont(f);
		g.setColor(selected ? selectColor : whiteColor);

		FontMetrics fm = g.getFontMetrics();
		/*   if (this == sim.plotXElm)
			s = "X";
		    if (this == sim.plotYElm)
			s = "Y";*/
		//    interpPoint(point1, point2, lead1, 1-(fm.stringWidth(s)/2+8)/dn);
		//    setBbox(point1, lead1, 0);
		setBbox(point1, point2,1);
		//  drawCenteredText(g, s, x2, y2, true);

//System.out.println("drawn voltage " + volts[0]);

		setVoltageColor(g, volts[0]);
		if (selected)
			g.setColor(selectColor);
		//  drawThickLine(g, point1, lead1);
		drawThickLine(g, point1, point2);
		drawPosts(g);

		String s = "DPin " + port;
		drawValues(g, s, hs);
		//    arduino.DI[port]=volts[0]>=2.5 ;
	}
	protected void setCurrent(int x, double c) { current = sim.arduino.ucModule.getPinMode(port)? c:-c; }//pinMode[port]? c:-c; }//sim.arduino.ucModule.getPinMode(port)? c:-c; }
	protected void stamp() {
		//System.out.println(Arrays.toString(arduino.DO));
		//vOutput = arduino.DO[port]? 5:0 ;// writeDO(port)? 5:0 ;
		//	System.out.println("stamping");
		//	System.out.println("pin mode: "+ sim.arduino.ucModule.getPinMode(port));//.pinMode[port]);
		
		if  (sim.arduino.ucModule.getPinMode(port))//.pinMode[port]) //( sim.arduino.ucModule.getPinMode(port))//(arduino.PinMode[port]) // if it is an output, behaves like a voltage source
		{//System.out.println("updating voltage");
			//vOutput = arduino.DO[port]? 5:0 ;
			vOutput = simulatorOutputs.pinbuffer[port]==1? 5:0 ;
		//	System.out.println("stamping output " + simulatorOutputs.pinbuffer[port]);
			sim.algorithm.stampVoltageSource(0, nodes[0], voltSource);
		
		}
	}
	public double getVoltageDiff() { return volts[0]; }
	protected void doStep() {
		if (! sim.arduino.ucModule.getPinMode(port)) {//.pinMode[port]){//(!sim.arduino.ucModule.getPinMode(port)){// (!arduino.PinMode[port]){
			sim.arduino.ucModule.setInput(volts[0]>=2.5?1:0, port);
		//	System.out.println("input");
		/*	arduino.DI[port]=volts[0]>=2.5 ;//
			if (arduino.pulsing){
				if ((arduino.DI[port]!=arduino.pulsingTarget)&&
				!arduino.pulsingTriggered){
					arduino.pulsingTriggered=true;
					
				}
				if ((arduino.DI[port]==arduino.pulsingTarget)&&
						arduino.pulsingTriggered&&!arduino.pulsingStarted){
					arduino.pulsingStarted=true;
					arduino.pulsingStartTime=sim.t;
				}
				if (arduino.pulsingStarted&&
						(arduino.DI[port]!=arduino.pulsingTarget))
				{arduino.pulsing=false;
				arduino.pulsingDuration = sim.t-arduino.pulsingStartTime;
				synchronized (arduino.synchObject) {
				//	System.out.println("exit pausing");
					arduino.synchObject.notify();
				}
				}
			}*/
		}//vOutput = arduino.DO[port]? 5:0 ;}
		else{//System.out.println("updating voltage");
		//	System.out.println("output");
			/*if (!arduino.isPWM[port]){
				
			vOutput = arduino.DO[port]? 5:0 ;// writeDO(port)? 5:0 ;
			}
			else{
			//	System.out.println("doing pwm");
			//		System.out.println("sim.t " +sim.t);
				//	System.out.println("pwmPeriod " +arduino.pwmPeriod);
				//System.out.println("% " +sim.t % arduino.pwmPeriod);
				//System.out.println("pwm value " + arduino.pwm[port]);
				//System.out.println("compare value " +arduino.pwm[port]/255*arduino.pwmPeriod);
				if (!sim.arduino.pwmAverage){
				vOutput =((sim.t % arduino.pwmPeriod)< 
				(arduino.analogOutput[port]/255*arduino.pwmPeriod))? 5:0;}
				else{
					vOutput = arduino.analogOutput[port]/255 * 5;
				}
			}*/
			vOutput = (simulatorOutputs.pinbuffer[port]==0)?0:5;//(simulatorOutputs.pinbuffer[port]==0)?0:5;
		//	System.out.println("port " + port + " voltage "+vOutput);
			sim.algorithm.updateVoltageSource(0, nodes[0], voltSource,
							vOutput);}
							
		}
		protected int getVoltageSourceCount() {  //System.out.println("voltage source count: "+ (sim.arduino.ucModule.getPinMode(port)?1:0));
		return  sim.arduino.ucModule.getPinMode(port)?1: 0;}//.pinMode[port]?1: 0;}//sim.arduino.ucModule.getPinMode(port)?1: 0; }// arduino.PinMode[port]? 1: 0; }// if it is an output, behaves like a voltage source
		protected void getInfo(String arr[]) {
			arr[0] = "Arduino Digital Pin " + port;
			arr[1] = "I = " + getCurrentText(getCurrent());
			arr[1] = "V = " + getVoltageText(getVoltageDiff());
		}
		protected boolean hasGroundConnection(int n1) { return  sim.arduino.ucModule.getPinMode(port);};//.pinMode[port];};//sim.arduino.ucModule.getPinMode(port); }//return arduino.PinMode[port]? true: false; }// if it is an output, behaves like a voltage source
	//	boolean needsShortcut() {return sim.arduino.PinMode[port]? true: false; }// if it is an output, behaves like a voltage source
		 protected boolean needsArduino(){ return true;};
		public EditInfo getEditInfo(int n) {
			if (n == 0)
				return new EditInfo("Pin: ", port, 0, 12).
						setDimensionless();

			return null;
		}
		public void setEditValue(int n, EditInfo ei) {

			if (n == 0 && ei.value >= 0) {
				port = (int) ei.value;
				sim.needAnalyze();
			//	setPoints();
			}

		}
}

