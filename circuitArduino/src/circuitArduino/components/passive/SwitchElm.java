package circuitArduino.components.passive;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;
import circuitArduino.components.inout.LogicInputElm;

public class SwitchElm extends CircuitElm {
    public boolean momentary;
    // position 0 == closed, position 1 == open
    public int position;
    public String name;
    protected int posCount;
    public SwitchElm(int xx, int yy) {
	super(xx, yy);
	momentary = false;
	position = 0;
	posCount = 2;
    }
    public SwitchElm(int xx, int yy, boolean mm) {
	super(xx, yy);
	position = (mm) ? 1 : 0;
	momentary = mm;
	name = "switch";
	posCount = 2;
    }
    public SwitchElm(int xa, int ya, int xb, int yb, int f,
		     StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	String str = st.nextToken();
	if (str.compareTo("true") == 0)
	    position = (this instanceof LogicInputElm) ? 0 : 1;
	else if (str.compareTo("false") == 0)
	    position = (this instanceof LogicInputElm) ? 1 : 0;
	else
	    position = new Integer(str).intValue();
	momentary = new Boolean(st.nextToken()).booleanValue();
	if(this.getClass().equals(SwitchElm.class))
	try {
		 name = st.nextToken();
		}
		catch(Exception e) {
		  //  Block of code to handle errors
		}
 
	
	posCount = 2;
    }
    protected int getDumpType() { return 's'; }
    protected String dump() {
	return super.dump() + " " + position + " " + momentary + " " + name;
    }

    Point ps, ps2;
    protected void setPoints() {
	super.setPoints();
	calcLeads(sim.gridSize*4);//32);
	ps  = new Point();
	ps2 = new Point();
    }
	
    protected void draw(Graphics g) {
	int openhs = sim.gridSize*2;//16;
	int hs1 = (position == 1) ? 0 :sim.gridSize/4;// 2;
	int hs2 = (position == 1) ? openhs+sim.gridSize:sim.gridSize/4;// +10 : 2;
	setBbox(point1, point2, openhs);

	draw2Leads(g);
	    
	if (position == 0)
	    doDots(g);
	    
	if (!needsHighlight())
	    g.setColor(whiteColor);
	interpPoint(lead1, lead2, ps,  0, hs1);
	interpPoint(lead1, lead2, ps2, 1, hs2);
	    
	drawThickLine(g, ps, ps2);
	 drawValues(g, name, openhs);
	drawPosts(g);
	
    }
    protected void calculateCurrent() {
	if (position == 1)
	    current = 0;
    }
    protected void stamp() {
	if (position == 0)
	    sim.algorithm.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
    }
    protected int getVoltageSourceCount() {
	return (position == 1) ? 0 : 1;
    }
    public void mouseUp() {
	if (momentary)
	    toggle();
    }
    public void toggle() {
	position++;
	if (position >= posCount)
	    position = 0;
    }
    protected void getInfo(String arr[]) {
	arr[0] = (momentary) ? "push switch (SPST)" : "switch (SPST)";
	if (position == 1) {
	    arr[1] = "open";
	    arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
	} else {
	    arr[1] = "closed";
	    arr[2] = "V = " + getVoltageText(volts[0]);
	    arr[3] = "I = " + getCurrentDText(getCurrent());
	}
    }
    protected boolean getConnection(int n1, int n2) { return position == 0; }
    protected boolean isWire() { return true; }
    public EditInfo getEditInfo(int n) {
	if (n == 0) {
	    EditInfo ei = new EditInfo("", 0, -1, -1);
	    ei.checkbox = new Checkbox("Momentary Switch", momentary);
	    return ei;
	}
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    momentary = ei.checkbox.getState();
    }
}
