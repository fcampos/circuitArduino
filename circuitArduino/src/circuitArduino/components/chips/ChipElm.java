package circuitArduino.components.chips;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;

    public abstract class ChipElm extends CircuitElm {
	int csize, cspc, cspc2;
	protected int bits;
	final int FLAG_SMALL = 1;
	final int FLAG_FLIP_X = 1024;
	final int FLAG_FLIP_Y = 2048;
	public ChipElm(int xx, int yy) {
	    super(xx, yy);
	    if (needsBits())
		bits = (this instanceof DecadeElm) ? 10 : 4;
	    noDiagonal = true;
	    setupPins();
	    setSize(sim.smallGridCheckItem.getState() ? 1 : 2);
	}
	public ChipElm(int xa, int ya, int xb, int yb, int f,
		       StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    if (needsBits())
		bits = new Integer(st.nextToken()).intValue();
	    noDiagonal = true;
	    setupPins();
	   // setSize(1);
	    setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
	    int i;
	    for (i = 0; i != getPostCount(); i++) {
		if (pins[i].state) {
		    volts[i] = new Double(st.nextToken()).doubleValue();
		    pins[i].value = volts[i] > 2.5;
		}
	    }
	}
	protected boolean needsBits() { return false; }
	protected void setSize(int s) {
	    csize = s;
	    //cspc = 8*s;
	    cspc = sim.gridSize*s;
	    cspc2 = cspc*2;
	    flags &= ~FLAG_SMALL;
	    flags |= (s == 1) ? FLAG_SMALL : 0;
	}
	protected abstract void setupPins();
	protected void draw(Graphics g) {
	    drawChip(g);
	}
	protected void drawChip(Graphics g) {
	    int i;
	    Font f = new Font("SansSerif", 0, (sim.gridSize));
	    g.setFont(f);
	    FontMetrics fm = g.getFontMetrics();
	    for (i = 0; i != getPostCount(); i++) {
		Pin p = pins[i];
		setVoltageColor(g, volts[i]);
		Point a = p.post;
		Point b = p.stub;
		drawThickLine(g, a, b);
		p.curcount = updateDotCount(p.current, p.curcount);
		drawDots(g, b, a, p.curcount);
		if (p.bubble) {
		    g.setColor(sim.printableCheckItem.getState() ?
			       Color.white : Color.black);
		    drawThickCircle(g, p.bubbleX, p.bubbleY, 1);
		    g.setColor(lightGrayColor);
		    drawThickCircle(g, p.bubbleX, p.bubbleY, 3);
		}
		g.setColor(whiteColor);
		int sw = fm.stringWidth(p.text);
		g.drawString(p.text, p.textloc.x-sw/2,
			     p.textloc.y+fm.getAscent()/2);
		if (p.lineOver) {
		    int ya = p.textloc.y-fm.getAscent()/2;
		    g.drawLine(p.textloc.x-sw/2, ya, p.textloc.x+sw/2, ya);
		}
	    }
	    g.setColor(needsHighlight() ? selectColor : lightGrayColor);
	    drawThickPolygon(g, rectPointsX, rectPointsY, 4);
	    if (clockPointsX != null)
		g.drawPolyline(clockPointsX, clockPointsY, 3);
	    for (i = 0; i != getPostCount(); i++)
		drawPost(g, pins[i].post.x, pins[i].post.y, nodes[i]);
	} 
	protected int rectPointsX[];
	protected int rectPointsY[];
	int clockPointsX[], clockPointsY[];
	protected Pin pins[];
	protected int sizeX;
	protected int sizeY;
	boolean lastClock;
	protected void drag(int xx, int yy) {
	    yy = sim.uitools.snapGrid(yy,false);
	    if (xx < x) {
		xx = x; yy = y;
	    } else {
		y = y2 = yy;
		x2 = sim.uitools.snapGrid(xx,true);
	    }
	    setPoints();
	}
	protected void setPoints() {
		//int s;
		//setSize(1);
		point1 = new Point(x , y );
		point2 = new Point(x2, y2);
	    if (x2-x > sizeX*cspc2 && this == sim.dragElm)
		setSize(2);
	    //if ((flags & FLAG_SMALL) != 0 )
	   // 	s=1;
	    //else
	    //	s=2;
	    
	    cspc = sim.gridSize*csize;
	    cspc2 = cspc*2;
	    int hs = cspc;
	    int x0 = x+cspc2; int y0 = y;
	    int xr = x0-cspc;
	    int yr = y0-cspc;
	    int xs = sizeX*cspc2;
	    int ys = sizeY*cspc2;
	    rectPointsX = new int[] { xr, xr+xs, xr+xs, xr };
	    rectPointsY = new int[] { yr, yr, yr+ys, yr+ys };
	    setBbox(xr, yr, rectPointsX[2], rectPointsY[2]);
	    int i;
	    for (i = 0; i != getPostCount(); i++) {
	   // 	System.out.println("setting point" + i + "\n");
		Pin p = pins[i];
		switch (p.side) {
		case SIDE_N: p.setPoint(x0, y0, 1, 0, 0, -1, 0, 0); break;
		case SIDE_S: p.setPoint(x0, y0, 1, 0, 0,  1, 0, ys-cspc2);break;
		case SIDE_W: p.setPoint(x0, y0, 0, 1, -1, 0, 0, 0); break;
		case SIDE_E: p.setPoint(x0, y0, 0, 1,  1, 0, xs-cspc2, 0);break;
		}
	    }
	}
	protected Point getPost(int n) {
	    return pins[n].post;
	}
	protected abstract int getVoltageSourceCount(); // output count
	protected void setVoltageSource(int j, int vs) {
	    int i;
	    for (i = 0; i != getPostCount(); i++) {
		Pin p = pins[i];
		if (p.output && j-- == 0) {
		    p.voltSource = vs;
		    return;
		}
	    }
	    System.out.println("setVoltageSource failed for " + this);
	}
	protected void stamp() {
	    int i;
	    for (i = 0; i != getPostCount(); i++) {
		Pin p = pins[i];
		if (p.output)
		    sim.algorithm.stampVoltageSource(0, nodes[i], p.voltSource);
	    }
	}
	protected void execute() {}
	protected void doStep() {
	    int i;
	    for (i = 0; i != getPostCount(); i++) {
		Pin p = pins[i];
		if (!p.output)
		    p.value = volts[i] > 2.5;
	    }
	    execute();
	    for (i = 0; i != getPostCount(); i++) {
		Pin p = pins[i];
		if (p.output)
		    sim.algorithm.updateVoltageSource(0, nodes[i], p.voltSource,
					p.value ? 5 : 0);
	    }
	}
	protected void reset() {
	    int i;
	    for (i = 0; i != getPostCount(); i++) {
		pins[i].value = false;
		pins[i].curcount = 0;
		volts[i] = 0;
	    }
	    lastClock = false;
	}
	
	protected String dump() {
	  //  int t = getDumpType();
	    String s = super.dump();
	    if (needsBits())
		s += " " + bits;
	    int i;
	    for (i = 0; i != getPostCount(); i++) {
		if (pins[i].state)
		    s += " " + volts[i];
	    }
	    return s;
	}
	
	protected void getInfo(String arr[]) {
	    arr[0] = getChipName();
	    int i, a = 1;
	    for (i = 0; i != getPostCount(); i++) {
		Pin p = pins[i];
		if (arr[a] != null)
		    arr[a] += "; ";
		else
		    arr[a] = "";
		String t = p.text;
		if (p.lineOver)
		    t += '\'';
		if (p.clock)
		    t = "Clk";
		arr[a] += t + " = " + getVoltageText(volts[i]);
		if (i % 2 == 1)
		    a++;
	    }
	}
	protected void setCurrent(int x, double c) {
	    int i;
	    for (i = 0; i != getPostCount(); i++)
		if (pins[i].output && pins[i].voltSource == x)
		    pins[i].current = c;
	}
	protected String getChipName() { return "chip"; }
	protected boolean getConnection(int n1, int n2) { return false; }
	protected boolean hasGroundConnection(int n1) {
	    return pins[n1].output;
	}
	
	public EditInfo getEditInfo(int n) {
	    if (n == 0) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Flip X", (flags & FLAG_FLIP_X) != 0);
		return ei;
	    }
	    if (n == 1) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Flip Y", (flags & FLAG_FLIP_Y) != 0);
		return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0) {
		if (ei.checkbox.getState())
		    flags |= FLAG_FLIP_X;
		else
		    flags &= ~FLAG_FLIP_X;
		setPoints();
	    }
	    if (n == 1) {
		if (ei.checkbox.getState())
		    flags |= FLAG_FLIP_Y;
		else
		    flags &= ~FLAG_FLIP_Y;
		setPoints();
	    }
	}

	final int SIDE_N = 0;
	protected final int SIDE_S = 1;
	protected final int SIDE_W = 2;
	protected final int SIDE_E = 3;
	public class Pin {
	    public Pin(int p, int s, String t) {
		pos = p; side = s; text = t;
	    }
	    Point post, stub;
	    Point textloc;
	    int pos, side;
		public int voltSource;
		int bubbleX;
		int bubbleY;
	    String text;
	    boolean lineOver, bubble, clock;
		public boolean output;
		public boolean value;
		boolean state;
	    double curcount;
		public double current;
	    void setPoint(int px, int py, int dx, int dy, int dax, int day,
			  int sx, int sy) {
		if ((flags & FLAG_FLIP_X) != 0) {
		    dx = -dx;
		    dax = -dax;
		    px += cspc2*(sizeX-1);
		    sx = -sx;
		}
		if ((flags & FLAG_FLIP_Y) != 0) {
		    dy = -dy;
		    day = -day;
		    py += cspc2*(sizeY-1);
		    sy = -sy;
		}
		int xa = px+cspc2*dx*pos+sx;
		int ya = py+cspc2*dy*pos+sy;
		post    = new Point(xa+dax*cspc2, ya+day*cspc2);
		stub    = new Point(xa+dax*cspc , ya+day*cspc );
		textloc = new Point(xa       , ya       );
		//System.out.print(cspc2*dx*pos+sx+dax*cspc2);
		//System.out.print(cspc2*dy*pos+sy+day*cspc2);
		if (bubble) {
		    bubbleX = xa+dax*10*csize;
		    bubbleY = ya+day*10*csize;
		}
		if (clock) {
		    clockPointsX = new int[3];
		    clockPointsY = new int[3];
		    clockPointsX[0] = xa+dax*cspc-dx*cspc/2;
		    clockPointsY[0] = ya+day*cspc-dy*cspc/2;
		    clockPointsX[1] = xa;
		    clockPointsY[1] = ya;
		    clockPointsX[2] = xa+dax*cspc+dx*cspc/2;
		    clockPointsY[2] = ya+day*cspc+dy*cspc/2;
		}
	    }
	}
    }

