package circuitArduino.components.passive;
// stub ThermistorElm based on SparkGapElm
// FIXME need to uncomment ThermistorElm line from CirSim.java
// FIXME need to add ThermistorElm.java to srclist

import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;

public class ThermistorElm extends CircuitElm {
	boolean ptc;
    double minresistance, maxresistance;
    double resistance;
    Scrollbar slider;
    Label label;
    public ThermistorElm(int xx, int yy) {
	super(xx, yy);
	maxresistance = 1e4;
	minresistance = 1e3;
	ptc=false;
	createSlider();
    }
    public ThermistorElm(int xa, int ya, int xb, int yb, int f,
		       StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	minresistance = new Double(st.nextToken()).doubleValue();
	maxresistance = new Double(st.nextToken()).doubleValue();
	ptc=false;
	createSlider();
    }
    protected boolean nonLinear() {return true;}
    protected int getDumpType() { return 188; }
    protected String dump() {
	return super.dump() + " " + minresistance + " " + maxresistance;
    }
    Point ps3, ps4,thermCenter;
    void createSlider() {
	sim.main.add(label = new Label("Temperature", Label.CENTER));
	int value = 50;
	sim.main.add(slider = new Scrollbar(Scrollbar.HORIZONTAL, value, 1, 0, 101));
	sim.main.validate();
    }
    protected void setPoints() {
	super.setPoints();
	calcLeads(32);
	ps3 = new Point();
	ps4 = new Point();
	thermCenter = interpPoint(point1, point2, .5);
    }
    public void delete() {
	sim.main.remove(label);
	sim.main.remove(slider);
    }
    
    protected void draw(Graphics g) {
    	int i;
    	int segments = 16;
    	int ox = 0;
    	int hs = sim.euroResistorCheckItem.getState() ? 6 : 7;
    	double v1 = volts[0];
    	double v2 = volts[1];
    	setBbox(point1, point2, 6);
    	draw2Leads(g);
    	// FIXME need to draw properly, see ResistorElm.java
    	setPowerColor(g, true);

    	drawThickCircle(g, thermCenter.x, thermCenter.y, 20);
    	double segf = 1./segments;
    	if (!sim.euroResistorCheckItem.getState()) {
    		// draw zigzag
    		for (i = 0; i != segments; i++) {
    			int nx = 0;
    			switch (i & 3) {
    			case 0: nx = 1; break;
    			case 2: nx = -1; break;
    			default: nx = 0; break;
    			}
    			double v = v1+(v2-v1)*i/segments;
    			setVoltageColor(g, v);
    			interpPoint(lead1, lead2, ps1, i*segf, hs*ox);
    			interpPoint(lead1, lead2, ps2, (i+1)*segf, hs*nx);
    			drawThickLine(g, ps1, ps2);
    			ox = nx;
    		}
    	} else {
    		// draw rectangle
    		setVoltageColor(g, v1);
    		interpPoint2(lead1, lead2, ps1, ps2, 0, hs);
    		drawThickLine(g, ps1, ps2);
    		for (i = 0; i != segments; i++) {
    			double v = v1+(v2-v1)*i/segments;
    			setVoltageColor(g, v);
    			interpPoint2(lead1, lead2, ps1, ps2, i*segf, hs);
    			interpPoint2(lead1, lead2, ps3, ps4, (i+1)*segf, hs);
    			drawThickLine(g, ps1, ps3);
    			drawThickLine(g, ps2, ps4);
    		}
    		interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
    		drawThickLine(g, ps1, ps2);
    	}
    	setPowerColor(g, true);
    	doDots(g);
    	drawPosts(g);
    }
    
    protected void calculateCurrent() {
	double vd = volts[0] - volts[1];
	current = vd/resistance;
    }
    protected void startIteration() {
	double vd = volts[0] - volts[1];
	// FIXME set resistance as appropriate, using slider.getValue()
	//resistance = minresistance;
	if (ptc)
	resistance = slider.getValue() * (maxresistance-minresistance) / 100. + minresistance;
	else
		resistance = -slider.getValue() * (maxresistance-minresistance) / 100. + maxresistance;
		
	//System.out.print(this + " res current set to " + current + "\n");
    }
    protected void doStep() {
	sim.algorithm.stampResistor(nodes[0], nodes[1], resistance);
    }
    protected void stamp() {
	sim.algorithm.stampNonLinear(nodes[0]);
	sim.algorithm.stampNonLinear(nodes[1]);
    }
    protected void getInfo(String arr[]) {
	// FIXME
	arr[0] = "Thermistor";
	getBasicInfo(arr);
	arr[3] = "R = " + getUnitText(resistance, sim.ohmString);
	arr[4] = "Ron = " + getUnitText(minresistance, sim.ohmString);
	arr[5] = "Roff = " + getUnitText(maxresistance, sim.ohmString);
    }
    public EditInfo getEditInfo(int n) {
	// ohmString doesn't work here on linux
	if (n == 0)
	    return new EditInfo("Min resistance (ohms)", minresistance, 0, 0);
	if (n == 1)
		return new EditInfo("Max resistance (ohms)", maxresistance, 0, 0);
	if (n == 2){
		EditInfo ei = new EditInfo("", 0, 0, 0);
		ei.checkbox = new Checkbox("PTC", ptc);
		return ei;}
	//return new EditInfo("Max resistance (ohms)", maxresistance, 0, 0);
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (ei.value > 0 && n == 0)
	    minresistance = ei.value;
	if (ei.value > 0 && n == 1)
	    maxresistance = ei.value;
	if ( n == 2)
	    ptc = ei.checkbox.getState();
    }
}

