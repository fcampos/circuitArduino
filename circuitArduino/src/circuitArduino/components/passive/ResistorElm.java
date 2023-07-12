package circuitArduino.components.passive;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;

    public class ResistorElm extends CircuitElm {
	public double resistance;
	public ResistorElm(int xx, int yy) { super(xx, yy); resistance = 100; }
	public ResistorElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    resistance = new Double(st.nextToken()).doubleValue();
	}
	protected int getDumpType() { return 'r'; }
	protected String dump() {
	    return super.dump() + " " + resistance;
	}

	Point ps3, ps4;
	protected void setPoints() {
	    super.setPoints();
	    calcLeads(sim.gridSize*4);
	    ps3 = new Point();
	    ps4 = new Point();
	}
	
	protected void draw(Graphics g) {
	    int segments = 16;
	    int i;
	    int ox = 0;
	    int hs = sim.gridSize;///2;//sim.euroResistorCheckItem.getState() ? 6 : 8;
	    double v1 = volts[0];
	    double v2 = volts[1];
	    setBbox(point1, point2, hs);
	    draw2Leads(g);
	    setPowerColor(g, true);
	    double segf = 1./segments;
	    if (!sim.euroResistorCheckItem.getState()) {
	    	// draw zigzag
	    	//	    	first segment
	    	double v = v1;
	    	setVoltageColor(g, v);
	    	interpPoint(lead1, lead2, ps1, 0, 0);
	    	interpPoint(lead1, lead2, ps2, (1)*segf, hs*1);
	    	drawThickLine(g, ps1, ps2);
//	    	last segment
	    	v = v1+(v2-v1)*(segments-1)/segments;
	    	setVoltageColor(g, v);
	    	   interpPoint(lead1, lead2, ps1, (segments-1)*segf, -hs);
			    interpPoint(lead1, lead2, ps2, (segments)*segf, 0);
	    	//  System.out.println(ps1.toString());
	    	//  System.out.println(ps2.toString());
	    	drawThickLine(g, ps1, ps2);
	    	for (i = 1; i != segments/2; i++) {
			    int nx = 0;
			    switch (i & 1) {
			    case 0: nx = 1; break;
			    default: nx = -1; break;
			    }
			    v = v1+(v2-v1)*i/segments;
			    setVoltageColor(g, v);
			    interpPoint(lead1, lead2, ps1, i*2*segf-segf, -hs*nx);
			    interpPoint(lead1, lead2, ps2, (i+1)*2*segf-segf, hs*nx);
			  //  System.out.println(ps1.toString());
			  //  System.out.println(ps2.toString());
			    drawThickLine(g, ps1, ps2);
			    ox = nx;
			}
		/*for (i = 0; i != segments; i++) {
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
		  //  System.out.println(ps1.toString());
		  //  System.out.println(ps2.toString());
		    drawThickLine(g, ps1, ps2);
		    ox = nx;
		}*/
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
	    if (sim.showValuesCheckItem.getState()) {
		String s = getShortUnitText(resistance, sim.ohmString);//"");
		drawValues(g, s, hs);
	    }
	    doDots(g);
	    drawPosts(g);
	}
    
	protected void calculateCurrent() {
	    current = (volts[0]-volts[1])/resistance;
	    //System.out.print(this + " res current set to " + current + "\n");
	}
	protected void stamp() {
	    sim.algorithm.stampResistor(nodes[0], nodes[1], resistance);
	}
	protected void getInfo(String arr[]) {
	    arr[0] = "resistor";
	    getBasicInfo(arr);
	    arr[3] = "R = " + getUnitText(resistance, sim.ohmString);
	    arr[4] = "P = " + getUnitText(getPower(), "W");
	}
	@ Override
	public EditInfo getEditInfo(int n) {
	    // ohmString doesn't work here on linux
		System.out.println("Resistance (ohms)");
	    if (n == 0)
		return new EditInfo("Resistance (ohms)", resistance, 0, 0);
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (ei.value > 0)
	        resistance = ei.value;
	}
	public void setResistance(int n) {
	    if (n > 0)
	        resistance = n;
	}
	protected boolean needsShortcut() { return true; }
    }
