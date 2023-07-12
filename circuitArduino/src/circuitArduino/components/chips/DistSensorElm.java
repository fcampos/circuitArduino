package circuitArduino.components.chips;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.UI.EditInfo;

//import ChipElm.Pin;

    class DistSensorElm extends ChipElm {
	final int FLAG_RESET = 2;
	 public String name="DistSensor";
	boolean hasReset() { return (flags & FLAG_RESET) != 0; }
	public DistSensorElm(int xx, int yy) { super(xx, yy); }
	public DistSensorElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	   // pins[2].value = !pins[1].value;
	    int i;
	    for (i = 0; i != getPostCount(); i++) {
			    volts[i] = 0;
		    }
	    try {
			name = st.nextToken();
		} catch (Exception e) {
		}
	}
	protected String getChipName() { return "Distance Sensor"; }
	protected String dump() {
		return super.dump() +  " " + name;
	}
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
	
	public void setvoltage(double v){
		if((volts[0]-volts[1])>=3)
		volts[2]=v;
		else
			volts[2]=0;
	}
	protected void doStep() {
	    int i;
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
	   // volts[2]=5;
	    sim.algorithm.updateVoltageSource(0, nodes[2], pins[2].voltSource,volts[2]);
	}
	protected int getDumpType() { return 201; }
	
	public EditInfo getEditInfo(int n) {
	 /*   if (n == 2) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Reset Pin", hasReset());
		return ei;
	    }
	    return super.getEditInfo(n);*/
		if (n == 0) {
			EditInfo ei = new EditInfo("Name", 0, -1, -1);
			ei.text = name;
			return ei;
		}
		return null;
	}
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			name = ei.textf.getText();
			//  split();
		}
	  /*  if (n == 2) {
		if (ei.checkbox.getState())
		    flags |= FLAG_RESET;
		else
		    flags &= ~FLAG_RESET;
		setupPins();
		allocNodes();
		setPoints();
	    }*/
	//    super.setEditValue(n, ei);
	}
    }
