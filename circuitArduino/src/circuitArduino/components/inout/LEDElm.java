package circuitArduino.components.inout;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.UI.EditInfo;
import circuitArduino.components.active.DiodeElm;

 public   class LEDElm extends DiodeElm {
	double colorR, colorG, colorB;
	 public String name="LED";
	public LEDElm(int xx, int yy) {
	    super(xx, yy);
	    fwdrop = 2.1024259;
	    setup();
	    colorR = 1; colorG = colorB = 0;
	}
	public LEDElm(int xa, int ya, int xb, int yb, int f,
		      StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    if ((f & FLAG_FWDROP) == 0)
		fwdrop = 2.1024259;
	    setup();
	    colorR = new Double(st.nextToken()).doubleValue();
	    colorG = new Double(st.nextToken()).doubleValue();
	    colorB = new Double(st.nextToken()).doubleValue();
	try {
	name = st.nextToken();}
			catch (Exception e)
	{					};
	}
	protected int getDumpType() { return 162; }
	protected String dump() {
	    return super.dump() + " " + colorR + " " + colorG + " " + colorB + " " + name;
	}
public double getCurrent(){ return current;}

	Point ledLead1, ledLead2, ledCenter;
	protected void setPoints() {
	    super.setPoints();
	    int cr = (int)Math.round((double)(sim.gridSize)*3/2);//12;
	    ledLead1  = interpPoint(point1, point2, .5-cr/dn);
	    ledLead2  = interpPoint(point1, point2, .5+cr/dn);
	    ledCenter = interpPoint(point1, point2, .5);
	}
	
	protected void draw(Graphics g) {
		 
	    if (needsHighlight() || this == sim.dragElm || sim.stoppedCheck.getState()) {
		super.draw(g);
		 g.setColor(Color.gray);
		 setVoltageColor(g, volts[0]);
		  //  int cr = (int)Math.round((double)(sim.gridSize)*3/2);//12;
		    drawThickCircle(g, ledCenter.x, ledCenter.y, sim.gridSize*2);
		    drawValues(g, name, hs);
		return;
	    }
	   
	    setVoltageColor(g, volts[0]);
	    drawThickLine(g, point1, ledLead1);
	    setVoltageColor(g, volts[1]);
	    drawThickLine(g, ledLead2, point2);
	    
	    g.setColor(Color.gray);
	    int cr = (int)Math.round((double)(sim.gridSize)*3/2);//12;
	    drawThickCircle(g, ledCenter.x, ledCenter.y, cr);
	    cr -= 4;
	    double w = 255*current/.01;
	    if (w > 255)
		w = 255;
	    Color cc = new Color((int) (colorR*w), (int) (colorG*w),
				 (int) (colorB*w));
	    g.setColor(cc);
	    g.fillOval(ledCenter.x-cr, ledCenter.y-cr, cr*2, cr*2);
	    setBbox(point1, point2, cr);
	    updateDotCount();
	    drawDots(g, point1, ledLead1, curcount);
	    drawDots(g, point2, ledLead2, -curcount);
	    drawPosts(g);
	}

	protected void getInfo(String arr[]) {
	    super.getInfo(arr);
	    arr[0] = "LED";
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
    		EditInfo ei = new EditInfo("Name", 0, -1, -1);
    		ei.text = name;
    		return ei;
    	}
	    if (n == 1)
		return super.getEditInfo(n-1);
	    if (n == 2)
		return new EditInfo("Red Value (0-1)", colorR, 0, 1).
		    setDimensionless();
	    if (n == 3)
		return new EditInfo("Green Value (0-1)", colorG, 0, 1).
		    setDimensionless();
	    if (n == 4)
		return new EditInfo("Blue Value (0-1)", colorB, 0, 1).
		    setDimensionless();
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
		    name = ei.textf.getText();
		  //  split();
		}
	    if (n == 1)
		super.setEditValue(1, ei);
	    if (n == 2)
		colorR = ei.value;
	    if (n == 3)
		colorG = ei.value;
	    if (n == 4)
		colorB = ei.value;
	}
    }
