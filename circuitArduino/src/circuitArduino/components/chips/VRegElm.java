package circuitArduino.components.chips;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.UI.EditInfo;



public    class VRegElm extends ChipElm {
	final int FLAG_RESET = 2;
	double regulatedVoltage=5;
	 public String name="Vreg";
	boolean hasReset() { return (flags & FLAG_RESET) != 0; }
	public VRegElm(int xx, int yy) { super(xx, yy); volts[2]=5;}
	public VRegElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	   // pins[2].value = !pins[1].value;
	    int i;
	    for (i = 0; i != getPostCount(); i++) {
			    volts[i] = 0;
		    }
	    regulatedVoltage = new Double(st.nextToken()).doubleValue();//5;
	    try {
			name = st.nextToken();
		} catch (Exception e) {
		}
	}
	//import ChipElm.Pin;
	protected String getChipName() { return "Voltage Regulator"; }
	protected void drawChip(Graphics g) {
		super.drawChip(g);
		 Font f = new Font("SansSerif", 0, (int)(sim.gridSize*1.5));
		    g.setFont(f);
		g.drawString(name, rectPointsX[0],
				rectPointsY[0]-sim.gridSize);
		}
	protected void setupPins() {
	    sizeX = 2;
	    sizeY = 2;
	    pins = new Pin[getPostCount()];
	    pins[0] = new Pin(0, SIDE_W, "IN");
	    pins[1] = new Pin(0, SIDE_S, "GND");
	  //  pins[1].output = pins[1].state = true;
	    pins[2] = new Pin(0, SIDE_E, "OUT");
	    pins[2].output = true;
	    //pins[2].lineOver = true;
	    //pins[3] = new Pin(1, SIDE_W, "");
	    //pins[3].clock = true;
	    //if (hasReset())
		//pins[4] = new Pin(2, SIDE_W, "R");
	}
	protected int getPostCount() {
		return 3;
//	    return hasReset() ? 5 : 4;
	}
	protected int getVoltageSourceCount() { return 1; }
	protected void execute() {
	   /* if (pins[3].value && !lastClock) {
		pins[1].value =  pins[0].value;
		pins[2].value = !pins[0].value;
	    }
	    if (pins.length > 4 && pins[4].value) {
		pins[1].value = false;
		pins[2].value = true;
	    }
	    lastClock = pins[3].value;*/
	}
	
	protected void doStep() {
	   // int i;
	    /*for (i = 0; i != getPostCount(); i++) {
		Pin p = pins[i];
		if (!p.output)
		    p.value = volts[i] > 2.5;
	    }
	    execute();
	    for (i = 0; i != getPostCount(); i++) {
		Pin p = pins[i];
	    
		if (p.output)
		    sim.updateVoltageSource(0, nodes[i], p.voltSource,
					p.value ? 5 : 0);
	    }*/
	    if ((volts[0]-volts[1])>regulatedVoltage){
	    	volts[2] = regulatedVoltage;
	    sim.algorithm.updateVoltageSource(0, nodes[2], pins[2].voltSource,volts[2]);}
	    else
	    	sim.algorithm.updateVoltageSource(0, nodes[2], pins[2].voltSource,volts[0]);
	}
	protected int getDumpType() { return 200; }
	protected String dump() {
	    return super.dump() + " " + regulatedVoltage;
	}
	
	
	public EditInfo getEditInfo(int n) {
		  if (n == 0)
				return new EditInfo("Voltage output", regulatedVoltage, 0, 0);
			    return null;
	   /* if (n == 2) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Reset Pin", hasReset());
		return ei;
	    }
	    return super.getEditInfo(n);*/
	}
	public void setEditValue(int n, EditInfo ei) {
		//if (ei.value > 0)
		regulatedVoltage = ei.value;
	   /* if (n == 2) {
		if (ei.checkbox.getState())
		    flags |= FLAG_RESET;
		else
		    flags &= ~FLAG_RESET;
		setupPins();
		allocNodes();
		setPoints();
	    }
	    super.setEditValue(n, ei);*/
	//	System.out.print("Setting value"+volts[2]);
	}
    }
