package circuitArduino.components.passive;
import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;

public class PotElm extends CircuitElm implements AdjustmentListener {
    double  maxResistance, resistance1, resistance2;
    public double position;
    public String name = "Pot";
    double current1, current2, current3;
    double curcount1, curcount2, curcount3;
    int segments = 16+8;
    int length = 0;
   public Scrollbar slider;
    public Label label;
    //String sliderText;
    public PotElm(int xx, int yy) {
	super(xx, yy);
	//noDiagonal = false;
	setup();
	maxResistance = 1000;
	position = .5;
	name = "Pot";
	createSlider();
    }
    public PotElm(int xa, int ya, int xb, int yb, int f,
		  StringTokenizer st) {
	super(xa, ya, xb, yb, f);
	maxResistance = new Double(st.nextToken()).doubleValue();
	position = new Double(st.nextToken()).doubleValue();
	name = st.nextToken();
	while (st.hasMoreTokens())
	    name += ' ' + st.nextToken();
	createSlider();
    }
    void setup() {
    }
     protected int getPostCount() { return 3; }
    protected int getDumpType() { return 174; }
    protected Point getPost(int n) {
	return (n == 0) ? point1 : (n == 1) ? point2 : post3;
    }
    protected String dump() { return super.dump() + " " + maxResistance + " " +
	    position + " " + name; }
    void createSlider() {
	sim.main.add(label = new Label(name, Label.CENTER));
	int value = (int) (position*100);
	sim.main.add(slider = new Scrollbar(Scrollbar.HORIZONTAL, value, 1, 0, 101));
	sim.main.validate();
	slider.addAdjustmentListener(this);
    }
    public void adjustmentValueChanged(AdjustmentEvent e) {
	sim.analyzeFlag = true;
	position = slider.getValue()*.0099+.005;
	setPoints();
    }
    protected void delete() {
	sim.main.remove(label);
	sim.main.remove(slider);
    }
    Point post3, corner2, arrowPoint, midpoint, arrow1, arrow2,textPoint;;
    Point ps3, ps4;
    int bodyLen;
    public void setPoints() {
	super.setPoints();
	int offset = 0;
	//int segments = 16+4;
	if (abs(dx) > abs(dy)) {
		//System.out.println("----dx: "+(x-x2));
		//System.out.println("----dx: "+(x-x2)/sim.gridSize);
		/*System.out.println(" oldGridSize:" + sim.oldGridSize);
		System.out.println(" dx:" + dx/sim.oldGridSize);
		if (sim.oldGridSize!=sim.gridSize) {
			dx = dx/sim.oldGridSize*sim.gridSize;
			System.out.println("different gridSizes  xx");
		}
		else {
			
		dx = Math.round(dx/(2*sim.gridSize))*2*sim.gridSize;}
		System.out.println(" newdGridSize:" + sim.gridSize);
		System.out.println(" dx new:" + dx/sim.gridSize);*/
		//dx = (int) (sign(dx)*Math.round(Math.abs(dx)/(sim.gridSize))*sim.gridSize);
		//dx must be an even number of gridsizes
	    //dx = sim.uitools.snapGrid(dx/2,true)*2;//sign(dx)*sim.gridSize*12;//sim.snapGrid(dx/2,true)*2;
	//	dx = (int) (sign(dx)*Math.round((double)Math.abs(dx)/(double)(sim.gridSize))*sim.gridSize);
		//dx = Math.round(dx/(sim.gridSize))*sim.gridSize;
	//	System.out.println("number grid sizes: " +dx/sim.gridSize);
		//	System.out.println("grid size: " +sim.gridSize);
		//	System.out.println("dx: " + dx);
		//	System.out.println("dx: " + (point1.x-point2.x));

		//	System.out.println("is odd: " +(dx%(2*sim.gridSize)!=0));
				if (dx%(2*sim.gridSize)!=0)
			dx = (int) (Math.round(((double)dx)/(2*(double)sim.gridSize))*2*sim.gridSize);
		//		System.out.println("number grid sizes final: " +dx/sim.gridSize);
	    point2.x = x2 = point1.x + dx;
	    offset = (dx < 0) ? dy : -dy;
	    point2.y = point1.y;
	} else {
	    //dy = sim.uitools.snapGrid(dy/2,false)*2;//sign(dy)*sim.gridSize*12;//sim.snapGrid(dy/2,false)*2;
		//dy = Math.round(dy/(2*sim.gridSize))*2*sim.gridSize;
		//dy=(int) (sign(dy)*Math.round(Math.abs(dy)/(sim.gridSize))*sim.gridSize);
	/*	if (sim.oldGridSize!=sim.gridSize) {
			dy = dy/sim.oldGridSize*sim.gridSize;
			System.out.println("different gridSizes yy");
		}
		dy = Math.round(dy/(2*sim.gridSize))*2*sim.gridSize;*/
		if (dy%(2*sim.gridSize)!=0)
			dy = (int) (Math.round(((double)dy)/(2*(double)sim.gridSize))*2*sim.gridSize);
		point2.y = y2 = point1.y + dy;
	    offset = (dy > 0) ? dx : -dx;
	    point2.x = point1.x;
	}
	if (offset == 0)
	    offset = sim.gridSize*4;
	dn = distance(point1, point2);
	int bodyLen = segments*2;//32;
	calcLeads(bodyLen);
//	position = slider.getValue()*.0099+.005;
	//System.out.println("slider value " + slider.getValue() + " position "+ position);
	//System.out.println("dn " + dn + " soff " + soff);
	int soff = (int) ((position-.5)*bodyLen);
	//int offset2 = offset - sign(offset)*4;
	post3 =      interpPoint(point1, point2, .5, offset);
	textPoint =      interpPoint(point1, point2, .5, -offset);
	//corner2 =    interpPoint(point1, point2, .5, offset*2);
	//System.out.println("dn " + dn + " soff " + soff + " soff/dn+.5" + (soff/dn+.5) + "8*sign(offset)" + (8*sign(offset)));
	corner2 =    interpPoint(point1, point2, soff/dn+.5, offset);
	arrowPoint = interpPoint(point1, point2, soff/dn+.5,
				 8*sign(offset));
	midpoint = interpPoint(point1, point2, soff/dn+.5);
	//System.out.println("arrowPoint " + arrowPoint.x + " " +arrowPoint.y);
	//System.out.println("midPoint " + midpoint.x + " " +midpoint.y);
	arrow1 = new Point();
	arrow2 = new Point();
	double clen = abs(offset)-8;
//	System.out.println("offset " + offset);
	//interpPoint2(corner2, arrowPoint, arrow1, arrow2, (clen-8)/clen, 8);
	interpPoint2(corner2, arrowPoint, arrow1, arrow2, 8, 8);
//	System.out.println("corner2 " + corner2.x + " " +corner2.y);
//	System.out.println("arrow1 " + arrow1.x + " " +arrow1.y);
//	System.out.println("arrow2 " + arrow2.x + " " +arrow2.y);

	ps3 = new Point();
	ps4 = new Point();
    }
	
    protected void draw(Graphics g) {
	//int segments = 16+4;
	int i;
	int ox = 0;
	int hs = sim.euroResistorCheckItem.getState() ? 6 : 8;
	double v1 = volts[0];
	double v2 = volts[1];
	double v3 = volts[2];
	setBbox(point1, point2, hs);
	draw2Leads(g);
	g.setColor(whiteColor);
	drawCenteredText(g, name, textPoint.x, textPoint.y,true);
	setPowerColor(g, true);
	double segf = 1./segments;
	int divide = (int) (segments*position);
	if (!sim.euroResistorCheckItem.getState()) {
	    // draw zigzag
	    for (i = 0; i != segments; i++) {
		int nx = 0;
		switch (i & 3) {
		case 0: nx = 1; break;
		case 2: nx = -1; break;
		default: nx = 0; break;
		}
		double v = v1+(v3-v1)*i/divide;
		if (i >= divide)
		    v = v3+(v2-v3)*(i-divide)/(segments-divide);
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
		double v = v1+(v3-v1)*i/divide;
		if (i >= divide)
		    v = v3+(v2-v3)*(i-divide)/(segments-divide);
		setVoltageColor(g, v);
		interpPoint2(lead1, lead2, ps1, ps2, i*segf, hs);
		interpPoint2(lead1, lead2, ps3, ps4, (i+1)*segf, hs);
		drawThickLine(g, ps1, ps3);
		drawThickLine(g, ps2, ps4);
	    }
	    interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
	    drawThickLine(g, ps1, ps2);
	}
	setVoltageColor(g, v3);
	//g.setColor(new Color(255,0,0));//)whiteColor);
	drawThickLine(g, post3, corner2);
//	g.setColor(new Color(255,0,0));//)whiteColor);
	drawThickLine(g, corner2, arrowPoint);
	//drawThickLine(g, arrow1, arrowPoint);
	//drawThickLine(g, arrow2, arrowPoint);
	
	curcount1 = updateDotCount(current1, curcount1);
	curcount2 = updateDotCount(current2, curcount2);
	curcount3 = updateDotCount(current3, curcount3);
	if (sim.dragElm != this) {
	    drawDots(g, point1, midpoint, curcount1);
	    drawDots(g, point2, midpoint, curcount2);
	    drawDots(g, post3, corner2, curcount3);
	    drawDots(g, corner2, midpoint,
		     curcount3+distance(post3, corner2));
	}
	drawPosts(g);
    }
    protected void calculateCurrent() {
	current1 = (volts[0]-volts[2])/resistance1;
	current2 = (volts[1]-volts[2])/resistance2;
	current3 = -current1-current2;
    }
    protected void stamp() {
	resistance1 = maxResistance*position;
	resistance2 = maxResistance*(1-position);
	sim.algorithm.stampResistor(nodes[0], nodes[2], resistance1);
	sim.algorithm.stampResistor(nodes[2], nodes[1], resistance2);
    }
    protected void getInfo(String arr[]) {
	arr[0] = "potentiometer";
	arr[1] = "Vd = " + getVoltageDText(getVoltageDiff());
	arr[2] = "R1 = " + getUnitText(resistance1, sim.ohmString);
	arr[3] = "R2 = " + getUnitText(resistance2, sim.ohmString);
	arr[4] = "I1 = " + getCurrentDText(current1);
	arr[5] = "I2 = " + getCurrentDText(current2);
    }
    public EditInfo getEditInfo(int n) {
	// ohmString doesn't work here on linux
	if (n == 0)
	    return new EditInfo("Resistance (ohms)", maxResistance, 0, 0);
	if (n == 1) {
	    EditInfo ei = new EditInfo("Slider Text", 0, -1, -1);
	    ei.text = name;
	    return ei;
	}
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
	if (n == 0)
	    maxResistance = ei.value;
	if (n == 1) {
	    name = ei.textf.getText();
	    label.setText(name);
	}
    }
}

