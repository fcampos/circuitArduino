package circuitArduino.components.inout;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;

  public  class LogicOutputElm extends CircuitElm {
	final int FLAG_TERNARY = 1;
	final int FLAG_NUMERIC = 2;
	final int FLAG_PULLDOWN = 4;
	double threshold;
	String value;
	public LogicOutputElm(int xx, int yy) {
	    super(xx, yy);
	    threshold = 2.5;
	}
	public LogicOutputElm(int xa, int ya, int xb, int yb, int f,
			      StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    try {
		threshold = new Double(st.nextToken()).doubleValue();
	    } catch (Exception e) {
		threshold = 2.5;
	    }
	}
	protected String dump() {
	    return super.dump() + " " + threshold;
	}
	protected int getDumpType() { return 'M'; }
	protected int getPostCount() { return 1; }
	boolean isTernary() { return (flags & FLAG_TERNARY) != 0; }
	boolean isNumeric() { return (flags & (FLAG_TERNARY|FLAG_NUMERIC)) != 0; }
	boolean needsPullDown() { return (flags & FLAG_PULLDOWN) != 0; }
	protected void setPoints() {
	    super.setPoints();
	    lead1 = interpPoint(point1, point2, 1-12/dn);
	}
	protected void draw(Graphics g) {
	    Font f = new Font("SansSerif", Font.BOLD, 20);
	    g.setFont(f);
	    //g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	    g.setColor(lightGrayColor);
	    String s = (volts[0] < threshold) ? "L" : "H";
	    if (isTernary()) {
		if (volts[0] > 3.75)
		    s = "2";
		else if (volts[0] > 1.25)
		    s = "1";
		else
		    s = "0";
	    } else if (isNumeric())
		s = (volts[0] < threshold) ? "0" : "1";
	    value = s;
	    setBbox(point1, lead1, 0);
	    drawCenteredText(g, s, x2, y2, true);
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, point1, lead1);
	    drawPosts(g);
	}
	protected void stamp() {
	    if (needsPullDown())
		sim.algorithm.stampResistor(nodes[0], 0, 1e6);
	}
	public double getVoltageDiff() { return volts[0]; }
	protected void getInfo(String arr[]) {
	    arr[0] = "logic output";
	    arr[1] = (volts[0] < threshold) ? "low" : "high";
	    if (isNumeric())
		arr[1] = value;
	    arr[2] = "V = " + getVoltageText(volts[0]);
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("Threshold", threshold, 10, -10);
	    if (n == 1) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Current Required", needsPullDown());
		return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0)
		threshold = ei.value;
	    if (n == 1) {
		if (ei.checkbox.getState())
		    flags = FLAG_PULLDOWN;
		else
		    flags &= ~FLAG_PULLDOWN;
	    }
	}
    }
