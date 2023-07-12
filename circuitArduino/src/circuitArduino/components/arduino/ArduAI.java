package circuitArduino.components.arduino;
import java.awt.Checkbox;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;

public class ArduAI extends CircuitElm {
	//final int FLAG_VALUE = 1;
	//public double vOutput=1.3;
	public final int nAnalogPins = 6;
	public int port=0;
	public ArduAI(int xx, int yy) { super(xx, yy);  
	noDiagonal = true;
	boolean testExist=true;
	int i;
	
	if (sim.arduino.exists())
	if (sim.elmList!=null)
		for (i=0;i!=nAnalogPins;i++){
			testExist=true;
			for (CircuitElm ce: sim.elmList){
				if (ce instanceof ArduAI) 
					if (((ArduAI)ce).port==i){testExist=false; break;};
			}
			if(testExist==true) {port=i;break;}
		}
	}
	public ArduAI(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		port= new Integer(st.nextToken());//.intValue();
		noDiagonal = true;
	}
	protected int getDumpType() { return 204; }
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

		//   FontMetrics fm = g.getFontMetrics();
		/*   if (this == sim.plotXElm)
			s = "X";
		    if (this == sim.plotYElm)
			s = "Y";*/
		//    interpPoint(point1, point2, lead1, 1-(fm.stringWidth(s)/2+8)/dn);
		//    setBbox(point1, lead1, 0);
		setBbox(point1, point2, 1);
		//  drawCenteredText(g, s, x2, y2, true);



		setVoltageColor(g, volts[0]);
		if (selected)
			g.setColor(selectColor);
		//  drawThickLine(g, point1, lead1);
		drawThickLine(g, point1, point2);
		drawPosts(g);

		String s = "AI " + port;
		drawValues(g, s, hs);
		//    sim.arduino.interpreter.arduino.DI[port]=volts[0]>=2.5 ;
	}
	protected void setCurrent(int x, double c) { current = -c; }
	/*void stamp() {
		//System.out.println(Arrays.toString(sim.arduino.interpreter.arduino.DO));
		//vOutput = sim.arduino.interpreter.arduino.DO[port]? 5:0 ;// writeDO(port)? 5:0 ;
		//	System.out.println("stamping");
		//	System.out.println("pin mode: "+ sim.arduino.interpreter.arduino.PinMode[port]);
		if  (sim.arduino.interpreter.arduino.PinMode[port]) // if it is an output, behaves like a voltage source
		{//System.out.println("updating voltage");
			//vOutput = sim.arduino.interpreter.arduino.DO[port]? 5:0 ;
			sim.algorithm.stampVoltageSource(0, nodes[0], voltSource);}
	}*/
	
	public double getVoltageDiff() { return volts[0]; }
	protected void doStep() {
		
		// THIS IS WHERE READING UPDATE GOES
		//sim.arduino.interpreter.arduino.analogInput[port] = volts[0];
		sim.arduino.ucModule.adc.adcInput[port]= (short)(volts[0]*1000);
		
		/*if  (!sim.arduino.interpreter.arduino.PinMode[port]){
				sim.arduino.interpreter.arduino.DI[port]=volts[0]>=2.5 ;//
			}//vOutput = sim.arduino.interpreter.arduino.DO[port]? 5:0 ;}
				else{//System.out.println("updating voltage");
					vOutput = sim.arduino.interpreter.arduino.DO[port]? 5:0 ;// writeDO(port)? 5:0 ;
					sim.algorithm.updateVoltageSource(0, nodes[0], voltSource,
							vOutput);}*/

	}
	protected int getVoltageSourceCount() {   return 0; }// if it is an output, behaves like a voltage source
	protected void getInfo(String arr[]) {
		arr[0] = "Arduino Analog Pin " + port;
		arr[1] = "I = " + getCurrentText(getCurrent());
		arr[1] = "V = " + getVoltageText(getVoltageDiff());
	}
	//	boolean hasGroundConnection(int n1) { return sim.arduino.interpreter.arduino.PinMode[port]? true: false; }// if it is an output, behaves like a voltage source
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
			//		sim.needAnalyze();
			//	setPoints();
		}

	}
}

