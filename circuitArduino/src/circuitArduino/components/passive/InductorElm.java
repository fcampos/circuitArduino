package circuitArduino.components.passive;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;

    public class InductorElm extends CircuitElm {
	Inductor ind;
	public double inductance;
	public InductorElm(int xx, int yy) {
	    super(xx, yy);
	    ind = new Inductor(sim);
	    inductance = 1;
	    ind.setup(inductance, current, flags);
	}
	public InductorElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    ind = new Inductor(sim);
	    inductance = new Double(st.nextToken()).doubleValue();
	    current = new Double(st.nextToken()).doubleValue();
	    ind.setup(inductance, current, flags);
	}
	protected int getDumpType() { return 'l'; }
	protected String dump() {
	    return super.dump() + " " + inductance + " " + current;
	}
	protected void setPoints() {
	    super.setPoints();
	    //calcLeads(32);
	    calcLeads(sim.gridSize*4);
	}
	protected void draw(Graphics g) {
	    double v1 = volts[0];
	    double v2 = volts[1];
	    int i;
	    int hs = sim.gridSize;//8;
	    setBbox(point1, point2, hs);
	    draw2Leads(g);
	    setPowerColor(g, false);
	   
	    drawCoil(g, sim.gridSize, lead1, lead2, v1, v2);
	    //drawCoil(g, 8, lead1, lead2, v1, v2);
	    if (sim.showValuesCheckItem.getState()) {
		String s = getShortUnitText(inductance, "H");
		drawValues(g, s, hs);
	    }
	    doDots(g);
	    drawPosts(g);
	}
	protected void reset() {
	    current = volts[0] = volts[1] = curcount = 0;
	    ind.reset();
	}
	protected void stamp() { ind.stamp(nodes[0], nodes[1]); }
	protected void startIteration() {
	    ind.startIteration(volts[0]-volts[1]);
	}
	protected boolean nonLinear() { return ind.nonLinear(); }
	protected void calculateCurrent() {
	    double voltdiff = volts[0]-volts[1];
	    current = ind.calculateCurrent(voltdiff);
	}
	protected void doStep() {
	    double voltdiff = volts[0]-volts[1];
	    ind.doStep(voltdiff);
	}
	protected void getInfo(String arr[]) {
	    arr[0] = "inductor";
	    getBasicInfo(arr);
	    arr[3] = "L = " + getUnitText(inductance, "H");
	    arr[4] = "P = " + getUnitText(getPower(), "W");
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("Inductance (H)", inductance, 0, 0);
	    if (n == 1) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Trapezoidal Approximation",
					   ind.isTrapezoidal());
		return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0)
		inductance = ei.value;
	    if (n == 1) {
		if (ei.checkbox.getState())
		    flags &= ~Inductor.FLAG_BACK_EULER;
		else
		    flags |= Inductor.FLAG_BACK_EULER;
	    }
	    ind.setup(inductance, current, flags);
	}
    }
