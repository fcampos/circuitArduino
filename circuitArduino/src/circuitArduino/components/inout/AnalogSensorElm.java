package circuitArduino.components.inout;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.UI.EditInfo;
import circuitArduino.components.chips.ChipElm;
import circuitArduino.components.chips.ChipElm.Pin;

//import ChipElm.Pin;

 public   class AnalogSensorElm extends ChipElm {
	final int FLAG_RESET = 2;
	 public String name="AnalogSensor";
	 public int numberOutputs=1;
	boolean hasReset() { return (flags & FLAG_RESET) != 0; }
	public AnalogSensorElm(int xx, int yy) { super(xx, yy);
	setup();
//	numberOutputs=3; 
	}
	public AnalogSensorElm(int xa, int ya, int xb, int yb, int f,
			    StringTokenizer st) {
	    super(xa, ya, xb, yb, f, st);
	    numberOutputs = new Double(st.nextToken()).intValue();
		 name = st.nextToken();
	   // pins[2].value = !pins[1].value;
	    setup();
	    int i;
	    for (i = 0; i != getPostCount(); i++) {
			    volts[i] = 0;
		    }
	    try {
	    	//		name = st.nextToken();
	    } catch (Exception e) {
	    }
	}
	void setup()
	{int i;
		sizeY=numberOutputs;
		if (numberOutputs==1)
			sizeY=2;
		setSize(numberOutputs);
		setupPins();setPoints();	allocNodes();
		initBoundingBox();
		 for (i = 0; i != getPostCount(); i++) {
			    volts[i] = 0;
		    }
	}
	protected String getChipName() { return "Analog Sensor"; }
	protected String dump() {
		return super.dump() + " " + numberOutputs+ " " + name;
	}
	protected void drawChip(Graphics g) {
		super.drawChip(g);
		 Font f = new Font("SansSerif", 0, (int)(sim.gridSize*1.5));
		    g.setFont(f);
		g.drawString(name, rectPointsX[0],
				rectPointsY[0]-sim.gridSize);
		}
	protected void setupPins() {
		int i;
		if (sizeX==0){
	    sizeX = 3;
	    sizeY = 2;}
	    pins = new Pin[getPostCount()];
	    pins[0] = new Pin(0, SIDE_W, "IN");
	    pins[1] = new Pin(0, SIDE_S, "GND");
	  //  pins[1].output = pins[1].state = true;
	    //System.out.println("settong pin 0 1" + "post count:"+ getPostCount()+  "\n");
	    for (i = 2; i != getPostCount(); i++) {
	    	pins[i] = new Pin(i-2, SIDE_E, "OUT"+(i-2));
		    pins[i].output = true;
		    //System.out.println("settong pin" + i + "\n");
		    ////System.out.println(i);
	    }
	    
	    //pins[2].lineOver = true;
	    //pins[3] = new Pin(1, SIDE_W, "");
	    //pins[3].clock = true;
	    //if (hasReset())
		//pins[4] = new Pin(2, SIDE_W, "R");
	}
	protected int getPostCount() {
		 //System.out.println(numberOutputs);
		 //System.out.println(name);
		return (2+numberOutputs);
//	    return hasReset() ? 5 : 4;
	}
	protected int getVoltageSourceCount() { return numberOutputs; }
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

	public void setvoltage(double[] v){
		int i;
		if (v.length==numberOutputs)
		if((volts[0]-volts[1])>=3)
		{
			for (i = 2; i != getPostCount(); i++) {
				volts[i]=v[i-2];
			}
		}
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
		for (i = 2; i != getPostCount(); i++) {
	    sim.algorithm.updateVoltageSource(0, nodes[i], pins[i].voltSource,volts[i]);
		}
	}
	protected int getDumpType() { return 210; }
	
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
		  if (n == 1)
				return new EditInfo("n outputs", numberOutputs , 0, 100).setDimensionless();
		return null;
	}
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			name = ei.textf.getText();
			//  split();
		}
		if (n == 1) {
			numberOutputs = (int) ei.value;
			setup();
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
