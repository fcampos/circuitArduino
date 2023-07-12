package circuitArduino.components.inout;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;

 public   class GroundElm extends CircuitElm {
	public GroundElm(int xx, int yy) { super(xx, yy); }
	public GroundElm(int xa, int ya, int xb, int yb, int f,
			 StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	}
	protected int getDumpType() { return 'g'; }
	protected int getPostCount() { return 1; }
	protected void draw(Graphics g) {
		double hs = (double)sim.gridSize;
	    setVoltageColor(g, 0);
	    drawThickLine(g, point1, point2);
	    int i;
	    for (i = 0; i != 3; i++) {
	//	int a = 10-i*4;//10-i*4;
	//	int b = i*5; // -10;
		interpPoint2(point1, point2, ps1, ps2, 1+(i*hs*0.625)/dn, hs*1.25-i*hs/2); // 1+b/dn,   a);
		drawThickLine(g, ps1, ps2);
	    }
	    doDots(g);
	    interpPoint(point1, point2, ps2, 1+1.375*hs/dn);//1+11./dn);
	    setBbox(point1, ps2, 1.375*hs);//11);
	    drawPost(g, x, y, nodes[0]);
	}
	protected void setCurrent(int x, double c) { current = -c; }
	protected void stamp() {
	    sim.algorithm.stampVoltageSource(0, nodes[0], voltSource, 0);
	}
	public double getVoltageDiff() { return 0; }
	protected int getVoltageSourceCount() { return 1; }
	protected void getInfo(String arr[]) {
	    arr[0] = "ground";
	    arr[1] = "I = " + getCurrentText(getCurrent());
	}
	protected boolean hasGroundConnection(int n1) { return true; }
	protected boolean needsShortcut() { return true; }
    }
