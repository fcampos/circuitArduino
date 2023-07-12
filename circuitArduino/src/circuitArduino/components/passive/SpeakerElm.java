package circuitArduino.components.passive;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;

   public class SpeakerElm extends CircuitElm {
	public double resistance;
	public double volumeGain=1;
	public boolean playSound=true;
	public SpeakerElm(int xx, int yy) { super(xx, yy); volumeGain=1; resistance = 100; }
	public SpeakerElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    resistance = new Double(st.nextToken()).doubleValue();
	    volumeGain = new Double(st.nextToken()).doubleValue();
	    
	}
	protected int getDumpType() { return 211; }
	public String dump() {
	    return super.dump() + " " + resistance+ " " + volumeGain;
	}

	Point ps3, ps4;
	protected void setPoints() {
	    super.setPoints();
	    calcLeads(20);
	    ps3 = new Point();
	    ps4 = new Point();
	}
	public void setup() {
		System.out.println(this.dump());
		if (playSound)
		sim.playThread.wform.setElm(this);
		else
			sim.playThread.wform.setElm(null);
		//sim.soundElm=this;
	}
	
	protected void draw(Graphics g) {
	    int segments = 16;
	    int i;
	    int ox = 0;
	    int hs = 8;//sim.euroResistorCheckItem.getState() ? 6 : 8;
	    double v1 = volts[0];
	    double v2 = volts[1];
	    setBbox(point1, point2, hs);
	    draw2Leads(g);
	    setPowerColor(g, true);
	 //   double segf = 1./segments;
	/*    if (!sim.euroResistorCheckItem.getState()) {
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
	    } else {*/
		// draw rectangle
		setVoltageColor(g, v1);
		interpPoint2(lead1, lead2, ps1, ps2, 0, hs);
		drawThickLine(g, ps1, ps2);
		/*for (i = 0; i != segments; i++) {
		    double v = v1+(v2-v1)*i/segments;
		    setVoltageColor(g, v);
		    interpPoint2(lead1, lead2, ps1, ps2, i*segf, hs);
		    interpPoint2(lead1, lead2, ps3, ps4, (i+1)*segf, hs);
		    drawThickLine(g, ps1, ps3);
		    drawThickLine(g, ps2, ps4);
		}*/
		interpPoint2(lead1, lead2, ps3, ps4, 1, hs);
		drawThickLine(g, ps3, ps4);
		drawThickLine(g, ps1, ps3);
		drawThickLine(g, ps2, ps4);
		interpPoint(lead1, lead2, ps2, -.7, 3*hs);
		interpPoint(lead2, lead1, ps4, -.7, -3*hs);
		drawThickLine(g, ps1, ps2);
		drawThickLine(g, ps3, ps4);
		drawThickLine(g, ps2, ps4);
		//drawThickLine(g, ps1, ps2);
	   // }
	    if (sim.showValuesCheckItem.getState()) {
		String s = getShortUnitText(resistance, "");
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
	public EditInfo getEditInfo(int n) {
	    // ohmString doesn't work here on linux
	    if (n == 0)
		return new EditInfo("Resistance (ohms)", resistance, 0, 0);
	    if (n == 1)
			return new EditInfo("Volume", volumeGain, 0, 0);
	 	    
	    if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Play Sound", false);//(playSound== 1));
			return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
	    if (n == 0 & ei.value > 0)
	        resistance = ei.value;
	    if (n == 1 & ei.value > 0)
	        volumeGain = ei.value;
	    if (n == 2) {
	    	playSound=ei.checkbox.getState();
	    	setup();
			/*if (ei.checkbox.getState())
			{
				playSound=1;
				setup();}
			else
			{
				playSound=0;
				setup();
			}*/
		}
	}
	public void setResistance(int n) {
	    if (n > 0)
	        resistance = n;
	   	}
	protected boolean needsShortcut() { return false; }
    }
