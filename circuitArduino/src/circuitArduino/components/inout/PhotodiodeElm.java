package circuitArduino.components.inout;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;
import circuitArduino.components.active.Diode;

public class PhotodiodeElm extends CircuitElm {
    Diode diode;
    static final int FLAG_FWDROP = 1;
    final double defaultdrop = .805904783;
    double fwdrop, zvoltage;
    public double leakage;
    public String name="Photo";
    public PhotodiodeElm(int xx, int yy) {
	super(xx, yy);
	diode = new Diode(sim);
	fwdrop = defaultdrop;
	zvoltage = 1000;
	setup();
    }
    public PhotodiodeElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	diode = new Diode(sim);
	fwdrop = defaultdrop;
	zvoltage = 1000;
	name = "Photo";
	if ((f & FLAG_FWDROP) > 0) {
	    try {
		fwdrop = new Double(st.nextToken()).doubleValue();
		name = st.nextToken();
	    } catch (Exception e) {
	    }
	}
	setup();
    }
    protected boolean nonLinear() { return true; }
    
    public void setup() {
    	if (leakage> 0)
    	diode.leakage = leakage;
	diode.setup(fwdrop, 1000);

    }
    
    protected int getDumpType() { return 'P'; }
    protected String dump() {
	flags |= FLAG_FWDROP;
	return super.dump() + " " + fwdrop + " " + name;
    }
    

    final int hs = 8;
    Polygon poly;
    Point cathode[];
    
    Point  ledCenter;
    protected void setPoints() {
	super.setPoints();
	int hs = sim.gridSize;
	calcLeads(sim.gridSize*2);//calcLeads(16);
	
	//calcLeads(16);
	cathode = newPointArray(2);
	Point pa[] = newPointArray(2);
	interpPoint2(lead1, lead2, pa[0], pa[1], 0, hs);
	interpPoint2(lead1, lead2, cathode[0], cathode[1], 1, hs);
	poly = createPolygon(pa[0], pa[1], lead2);
	ledCenter = interpPoint(point1, point2, .5);
    }
	
    protected void draw(Graphics g) {
	drawDiode(g);
	doDots(g);
	 drawValues(g, name, hs);
	drawPosts(g);
    }
	
    protected void reset() {
	diode.reset();
	volts[0] = volts[1] = curcount = 0;
    }
	
    void drawDiode(Graphics g) {
	setBbox(point1, point2, hs);

	double v1 = volts[0];
	double v2 = volts[1];

	draw2Leads(g);

	// draw arrow thingy
	setPowerColor(g, true);
	setVoltageColor(g, v1);
	g.fillPolygon(poly);

	// draw thing arrow is pointing to
	setVoltageColor(g, v2);
	drawThickLine(g, cathode[0], cathode[1]);
	 int cr = sim.gridSize*2 ;//18;
	    drawThickCircle(g, ledCenter.x, ledCenter.y, cr);
    }
	
    protected void stamp() { diode.stamp(nodes[0], nodes[1]); }
    protected void doStep() {
	diode.doStep(volts[0]-volts[1]);
    }
    protected void calculateCurrent() {
	current = diode.calculateCurrent(volts[0]-volts[1]);
    }
    protected void getInfo(String arr[]) {
	arr[0] = "diode";
	arr[1] = "I = " + getCurrentText(getCurrent());
	arr[2] = "Vd = " + getVoltageText(getVoltageDiff());
	arr[3] = "P = " + getUnitText(getPower(), "W");
	arr[4] = "Vf = " + getVoltageText(fwdrop);
    }
    public EditInfo getEditInfo(int n) {
    	if (n == 0) {
    		EditInfo ei = new EditInfo("Name", 0, -1, -1);
    		ei.text = name;
    		return ei;
    	}
    	if (n == 1)
    		return new EditInfo("Fwd Voltage @ 1A", fwdrop, 10, 1000);
	 if (n == 2)
			return new EditInfo("leakage", leakage, 0, 0.001);
	return null;
    } 
    public void setEditValue(int n, EditInfo ei) {
    	if (n == 0) {
		    name = ei.textf.getText();
		  //  split();
		}
    if (ei.value > 0 & n==1)
	fwdrop = ei.value;
	if (ei.value > 0 & n==2)
       leakage = ei.value;
	setup();
    }
    protected boolean needsShortcut() { return getClass() == PhotodiodeElm.class; }
}
