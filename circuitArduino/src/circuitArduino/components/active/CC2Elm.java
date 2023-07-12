package circuitArduino.components.active;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.components.chips.ChipElm;
import circuitArduino.components.chips.ChipElm.Pin;

 public   class CC2Elm extends ChipElm {
	double gain;
	public CC2Elm(int xx, int yy) { super(xx, yy); gain = 1; }
	public CC2Elm(int xx, int yy, int g) { super(xx, yy); gain = g; }
	public CC2Elm(int xa, int ya, int xb, int yb, int f,
		      StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    gain = new Double(st.nextToken()).doubleValue();
	}
	protected String dump() {
	    return super.dump() + " " + gain;
	}
	protected String getChipName() { return "CC2"; }
	protected void setupPins() {
	    sizeX = 2;
	    sizeY = 3;
	    pins = new Pin[3];
	    pins[0] = new Pin(0, SIDE_W, "X");
	    pins[0].output = true;
	    pins[1] = new Pin(2, SIDE_W, "Y");
	    pins[2] = new Pin(1, SIDE_E, "Z");
	}
	protected void getInfo(String arr[]) {
	    arr[0] = (gain == 1) ? "CCII+" : "CCII-";
	    arr[1] = "X,Y = " + getVoltageText(volts[0]);
	    arr[2] = "Z = " + getVoltageText(volts[2]);
	    arr[3] = "I = " + getCurrentText(pins[0].current);
	}
	//boolean nonLinear() { return true; }
	protected void stamp() {
	    // X voltage = Y voltage
	    sim.algorithm.stampVoltageSource(0, nodes[0], pins[0].voltSource);
	    sim.algorithm.stampVCVS(0, nodes[1], 1, pins[0].voltSource);
	    // Z current = gain * X current
	    sim.algorithm.stampCCCS(0, nodes[2], pins[0].voltSource, gain);
	}
	protected void draw(Graphics g) {
	    pins[2].current = pins[0].current * gain;
	    drawChip(g);
	}
	protected int getPostCount() { return 3; }
	protected int getVoltageSourceCount() { return 1; }
	protected int getDumpType() { return 179; }
    }

class CC2NegElm extends CC2Elm {
    public CC2NegElm(int xx, int yy) { super(xx, yy, -1); }
    protected Class getDumpClass() { return CC2Elm.class; }
}
