package circuitArduino.components.passive;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;

public class DCmotorElm extends CircuitElm {
	
	Inductor ind, indInertia;
	// Electrical parameters
	public double resistance, inductance;
	// Electro-mechanical parameters
public	double K, Kb, J, b, gearRatio, tau; //tau reserved for static friction parameterization  
	public double angle;
	public double speed;
	public String name;
	//double accel;
	double maxSpeed;
	double coilCurrent;//,  coilCurCount;
	double inertiaCurrent;//,  inertiaCurCount;
	int[] voltSources = new int[2];
	public DCmotorElm(int xx, int yy) { 
		super(xx, yy); 
		ind = new Inductor(sim);
		indInertia = new Inductor(sim);
		//inductance = 3.5e-4; resistance = .6; angle = pi/2; speed = 0; maxSpeed=2; K = 0.019; b= 5.96e-4; J = .155e-4; Kb = 0.019; gearRatio=1; tau=0;
		inductance = 1.5e-4; resistance = .6; angle = pi/2; speed = 0; maxSpeed=2; K = 0.019; b= 5.96e-4; J = .155e-5; Kb = 0.019; gearRatio=1; tau=0;
		
		ind.setup(inductance, 0, Inductor.FLAG_BACK_EULER);
		indInertia.setup(J, 0, Inductor.FLAG_BACK_EULER);
		name = "Motor";
		}
	public DCmotorElm(int xa, int ya, int xb, int yb, int f,
		    StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    angle = pi/2;speed = 0;
	    //read:
	    // inductance; resistance, K, Kb, J, b, gearRatio, tau
	    inductance = new Double(st.nextToken()).doubleValue();
	    resistance = new Double(st.nextToken()).doubleValue(); 
	    K = 		new Double(st.nextToken()).doubleValue();
	    Kb = 		new Double(st.nextToken()).doubleValue();
	    J = 		new Double(st.nextToken()).doubleValue();
	    b = 		new Double(st.nextToken()).doubleValue();
	    gearRatio = new Double(st.nextToken()).doubleValue();
	    tau = 		new Double(st.nextToken()).doubleValue();
	    System.out.println(tau);
	    name = st.nextToken();
	    ind = new Inductor(sim);
	    indInertia = new Inductor(sim);
		ind.setup(inductance, 0, Inductor.FLAG_BACK_EULER);
		indInertia.setup(J, 0, Inductor.FLAG_BACK_EULER);
		searchExistingLinAxis();
		}
	protected int getDumpType() { return 'D'; }
	protected String dump() {
		// dump: inductance; resistance, K, Kb, J, b, gearRatio, tau
		return super.dump() + " " +  inductance + " " + resistance + " " + K + " " +  Kb + " " + J + " " + b + " " + gearRatio + " " + tau + " " + name;
	}
	public double getAngle(){ return(angle);}
	Point ps3, ps4, motorCenter;
	
	protected void setPoints() {
		super.setPoints();
		calcLeads((int)(sim.gridSize*4.5));//36);//(52);
		ps3 = new Point();
		ps4 = new Point();
		motorCenter = interpPoint(point1, point2, .5);
		allocNodes();
	}
	protected int getPostCount() { return 2; }
	protected int getInternalNodeCount() { return 4; }//{ return 4; }
	protected int getVoltageSourceCount() { return 2; }
	protected void setVoltageSource(int n, int v) { voltSources[n] = v; }
	protected void reset() {
		super.reset();
		ind.reset();
		indInertia.reset();
		coilCurrent =  0;
		inertiaCurrent = 0;
	}

	protected void stamp() {
	//	System.out.println("doing stamp");
		//nodes[0] nodes [1] are the external nodes
		//Electrical part:
		// inductor from motor nodes[0] to internal nodes[2]
		ind.stamp(nodes[0], nodes[2]);
		// resistor from internal nodes[2] to internal nodes[3] // motor post 2
		sim.algorithm.stampResistor(nodes[2], nodes[3], resistance);
		//sim.algorithm.stampResistor(nodes[2], nodes[1], resistance);
		// Back emf voltage source from internal nodes[3] to external nodes [1]
		sim.algorithm.stampVoltageSource(nodes[3],nodes[1], voltSources[0]); // 
		
		//Mechanical part:
		// inertia inductor from internal nodes[4] to internal nodes[5]
		indInertia.stamp(nodes[4], nodes[5]);
		//indInertia.stamp(nodes[3], nodes[4]);
		
		// resistor from  internal nodes[5] to  ground 
		sim.algorithm.stampResistor(nodes[5], 0, b);
		//.algorithm.stampResistor(nodes[4], 0, b);
		
		// Voltage Source from  internal nodes[4] to ground
		//System.out.println("doing stamp voltage");
		sim.algorithm.stampVoltageSource(nodes[4], 0, voltSources[1]); 
		//sim.algorithm.stampVoltageSource(nodes[3], 0, voltSource); 
		
		//System.out.println("doing stamp voltage "+voltSource);
	}
	protected void startIteration() {
		ind.startIteration(volts[0]-volts[2]);
		indInertia.startIteration(volts[4]-volts[5]);
		//indInertia.startIteration(volts[3]-volts[4]);
		
		// update angle:
		angle= angle + speed*sim.timeStep/gearRatio;
		if (Double.isNaN(angle))
			angle=0;
		
			
	//	System.out.println("R: "+ resistance + "H: " + inductance + "K: " + K + "Kb: " + Kb +  "J: " + J +  "b: " + b);
	//	System.out.println( "current at startIteration "+ speed);
	//	System.out.println("t= "+sim.t);
	//	System.out.println("ind " + this + " " + current + " " + (volts[0]-volts[2]));
	}

/*	boolean hasGroundConnection(int n1) {
		if (n1==4|n1==5) return true;
		else return false;
	}
	boolean getConnection(int n1, int n2) { 
		if((n1==0&n2==2)|(n1==2&n2==3)|(n1==1&n2==3)|(n1==4&n2==5))
		return true;
		else
			return false;
		}*/
	// we need this to be able to change the matrix for each step
	protected boolean nonLinear() { return false; }//{ return true; }
	// what does this actually do?
	protected void doStep() {
		double voltdiff = volts[0]-volts[2];
		sim.algorithm.updateVoltageSource(nodes[4],0, voltSources[1],
				coilCurrent*K);
		sim.algorithm.updateVoltageSource(nodes[3],nodes[1], voltSources[0],
				inertiaCurrent*Kb);
		//sim.algorithm.updateVoltageSource(nodes[3],0, voltSource,5);
				//coilCurrent*K);
		
		ind.doStep(voltdiff);
		indInertia.doStep(volts[4]-volts[5]);
		//indInertia.doStep(volts[3]-volts[4]);
		//System.out.println( "current at doStep "+ current);
	//	System.out.println(volts[3] + " " + volts[4]);
	//	System.out.println(nodes[3]);
		//	System.out.println(nodes[5]);
		//	System.out.println(voltSource);
		
		//		System.out.println(coilCurrent);
		//		System.out.println(K);
		//		System.out.println(coilCurrent*K);
		//		System.out.println((volts[3]-0));
		//System.out.println(inertiaCurrent);
	
	}
	protected void calculateCurrent() {
		double voltdiff = volts[0]-volts[2];
		coilCurrent = ind.calculateCurrent(voltdiff);
		inertiaCurrent = indInertia.calculateCurrent(volts[4]-volts[5]);
		//inertiaCurrent = indInertia.calculateCurrent(volts[3]-volts[4]);
		
	//	current = (volts[2]-volts[3])/resistance;
		//current = (volts[2]-volts[1])/resistance;
		speed=inertiaCurrent;//(volts[2]-volts[1]);
		//if ((volts[0]-volts[1])<20 & (volts[0]-volts[1])>-20) {
		//	 speed = 0.997*speed +0.003*(volts[0]-volts[1])*maxSpeed;
		  //angle= angle + speed*sim.timeStep;}
	 
	// System.out.println(coilCurrent + " " + current + " " + inertiaCurrent );
	// System.out.println("coil current "+coilCurrent + " " + current + " inertia current " + inertiaCurrent+" "+(volts[3]-0));//+" "+ (volts[3]-volts[5]) );
	 
	// System.out.println(speed+" "+sim.timeStep );
	}
	// public double getCurrent(){ current = (volts[2]-volts[1])/resistance; return current; }// { current = (volts[2]-volts[3])/resistance; return current; }
	   protected void setCurrent(int vn, double c) {
		   	if (vn == voltSources[0])
		   	    current = c;
		      }
	protected void draw(Graphics g) {
		int px[]= new int[4]; ;
		int py[]= new int[4]; ;
		double angle0;
		//  int segments = 16;
		int cr = (int)(sim.gridSize*2.25);//18;
		//  int i;
		// int ox = 0;
		int hs = sim.gridSize;//8;//sim.euroResistorCheckItem.getState() ? 6 : 8;
	   // double v1 = volts[0];
	    //double v2 = volts[1];
	    setBbox(point1, point2, hs);
	    draw2Leads(g);
	    getCurrent();
	    doDots(g);
	    setPowerColor(g, true);
	   // double segf = 1./segments;
	    Color cc = new Color((int) (165), (int) (165),
	    		(int) (165));
	    g.setColor(cc);
	    g.fillOval(motorCenter.x-(cr), motorCenter.y-(cr), (cr)*2, (cr)*2);
	
	    //cc = new Color((int) (60), (int) (70),
	    //		(int) (150));
	  cc = new Color((int) (10), (int) (10),
	    		(int) (10));
	    
	    g.setColor(cc);
	    //drawThickCircle(g, motorCenter.x, motorCenter.y, (int)Math.round(.38*60));
	    g.fillOval(motorCenter.x-(int)(cr/2.4), motorCenter.y-(int)(cr/2.4), (int)(2*cr/2.4), (int)(2*cr/2.4));
	    //		(int) (150));
	  //  System.out.println("angle*1000.0: "+ angle*1000.0);
	    double angleAux = Math.round(angle*500.0)/500.0;
	  //  System.out.println("angleAux "+ angleAux +" angle " +angle);
	    g.setColor(cc);
	    interpPointFix(lead1, lead2, ps1, 0.5 + .38*Math.cos(angleAux-pi/15), .38*Math.sin(angleAux-pi/15));
	    interpPointFix(lead1, lead2, ps2, 0.5 - .38*Math.cos(angleAux+pi/15), -.38*Math.sin(angleAux+pi/15));
	    interpPointFix(lead1, lead2, ps3, 0.5 + .38*Math.cos(angleAux+pi/15), .38*Math.sin(angleAux+pi/15));
	    interpPointFix(lead1, lead2, ps4, 0.5 - .38*Math.cos(angleAux-pi/15), -.38*Math.sin(angleAux-pi/15));

	    //drawThickLine(g, ps1, ps2);
	    px[0] =ps1.x;  py[0] =ps1.y;
	    px[1] =ps2.x;  py[1] =ps2.y;

	    px[3] = ps3.x;    py[3] = ps3.y;	
	    px[2] = ps4.x;    py[2] = ps4.y;
	    g.fillPolygon(px,py,4);//drawThickLine(g, ps1, ps2);
	    interpPointFix(lead1, lead2, ps1, 0.5 + .38*Math.cos(angleAux-pi/15+pi/3), .38*Math.sin(angleAux-pi/15+pi/3));
	    interpPointFix(lead1, lead2, ps2, 0.5 - .38*Math.cos(angleAux+pi/15+pi/3), -.38*Math.sin(angleAux+pi/15+pi/3));
	    interpPointFix(lead1, lead2, ps3, 0.5 + .38*Math.cos(angleAux+pi/15+pi/3), .38*Math.sin(angleAux+pi/15+pi/3));
	    interpPointFix(lead1, lead2, ps4, 0.5 - .38*Math.cos(angleAux-pi/15+pi/3), -.38*Math.sin(angleAux-pi/15+pi/3));

	    //drawThickLine(g, ps1, ps2);
	    px[0] =ps1.x;  py[0] =ps1.y;
	    px[1] =ps2.x;  py[1] =ps2.y;

	    px[3] = ps3.x;    py[3] = ps3.y;	
	    px[2] = ps4.x;    py[2] = ps4.y;
	    g.fillPolygon(px,py,4);//drawThickLine(g, ps1, ps2);
	    interpPointFix(lead1, lead2, ps1, 0.5 + .38*Math.cos(angleAux-pi/15+2*pi/3), .38*Math.sin(angleAux-pi/15+2*pi/3));
	    interpPointFix(lead1, lead2, ps2, 0.5 - .38*Math.cos(angleAux+pi/15+2*pi/3), -.38*Math.sin(angleAux+pi/15+2*pi/3));
	    interpPointFix(lead1, lead2, ps3, 0.5 + .38*Math.cos(angleAux+pi/15+2*pi/3), .38*Math.sin(angleAux+pi/15+2*pi/3));
	    interpPointFix(lead1, lead2, ps4, 0.5 - .38*Math.cos(angleAux-pi/15+2*pi/3), -.38*Math.sin(angleAux-pi/15+2*pi/3));

	    //drawThickLine(g, ps1, ps2);
	    px[0] =ps1.x;  py[0] =ps1.y;
	    px[1] =ps2.x;  py[1] =ps2.y;

	    px[3] = ps3.x;    py[3] = ps3.y;	
	    px[2] = ps4.x;    py[2] = ps4.y;
	    g.fillPolygon(px,py,4);//drawThickLine(g, ps1, ps2);
	    /* if (sim.showValuesCheckItem.getState()) {
		String s = getShortUnitText(resistance, "");
		drawValues(g, s, hs);
	    }*/
	    drawValues(g, name, hs);
	    drawPosts(g);
	}
    
	/*void calculateCurrent() {
		current = (volts[0]-volts[1])/resistance;
		//accel = (K/resistance*(volts[0]-volts[1]) -b*speed )/J;
		 //speed = speed + accel*sim.timeStep;
		 //angle= angle + speed*sim.timeStep;
	   // current = (volts[0]-volts[1]-K*speed)/resistance;
	   // accel = (K/resistance*(volts[0]-volts[1]) -(K*K/resistance + b)*speed )/J;
	   // speed = speed + accel*sim.timeStep;//
		if ((volts[0]-volts[1])<20 & (volts[0]-volts[1])>-20) {
		 speed = 0.997*speed +0.003*(volts[0]-volts[1])*maxSpeed;
	  angle= angle + speed*sim.timeStep;}
	 // System.out.println(angle);}
	    //System.out.print(this + " res current set to " + current + "\n");
	}*/
	
	
	//void stamp() {
	//    sim.algorithm.stampResistor(nodes[0], nodes[1], resistance);
	    
	//}
	void searchExistingLinAxis()
	{
		LinAxisPotElm foundLinAxis;
    	int i;
        for (i = 0; i != sim.elmList.size(); i++) {
		    CircuitElm ce = sim.getElm(i);
		    if (ce instanceof LinAxisPotElm)
		    {foundLinAxis = (LinAxisPotElm)ce;
		    foundLinAxis.searchExistingMotors();    	
		    }
        }
	}
	protected void getInfo(String arr[]) {
	    arr[0] = "DC Motor";
	    getBasicInfo(arr);
	    arr[3] = "L = " + getUnitText(inductance, "H");
	    arr[4] = "R = " + getUnitText(resistance, sim.ohmString);
	 //   arr[5] = "K = " + getUnitText(K, "Nm/A");
	    //   arr[6] = "Kb = " + getUnitText(Kb, "Vs");
	    //  arr[7] = "J = " + getUnitText(J, "Kgm^2");
	    //arr[8] = "b = " + getUnitText(b, "Vs");
	    arr[5] = "Speed = " + String.format ("%.1f", speed) + "  " + String.format ("%.1f", speed/gearRatio) ;
	   // arr[5] = "P = " + getUnitText(getPower(), "W");
	}
	public EditInfo getEditInfo(int n) {
	    // ohmString doesn't work here on linux
		if (n == 0) {
		    EditInfo ei = new EditInfo("Name", 0, -1, -1);
		    ei.text = name;
		    return ei;
		}
		// dump: inductance; resistance, K, Kb, J, b, gearRatio, tau
	    if (n == 1)
		return new EditInfo("Armature inductance (H)", inductance, 0, 0);
	    if (n == 2)
			return new EditInfo("Armature Resistance (ohms)", resistance, 0, 0);
	    if (n == 3)
			return new EditInfo("Torque constant (Nm/A)", K, 0, 0);
	    if (n == 4)
			return new EditInfo("Back emf constant (Vs/rad)", Kb, 0, 0);
	  	    if (n == 5)
			return new EditInfo("Moment of inertia (Kg.m^2", J, 0, 0);
	    if (n == 6)
			return new EditInfo("Friction coefficient (Nms/rad)", b, 0, 0);
	    if (n == 7)
			return new EditInfo("Gear Ratio", gearRatio, 0, 0);
		return null;
	}
	public void setup(){
		ind.setup(inductance, current, Inductor.FLAG_BACK_EULER);
		indInertia.setup(J, inertiaCurrent, Inductor.FLAG_BACK_EULER);
	}
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
		    name = ei.textf.getText();
		    searchExistingLinAxis();
		  //  split();
		}
	    if (ei.value > 0 & n==1)
	    {
	    	inductance = ei.value;
	    	ind.setup(inductance, current, Inductor.FLAG_BACK_EULER);
			}
	    if (ei.value > 0 & n==2)
	    	resistance = ei.value;
	    if (ei.value > 0 & n==3)
	    	K = ei.value;
	    if (ei.value > 0 & n==4)
	    	Kb = ei.value;
	    if (ei.value > 0 & n==5)
	    {
	    	J = ei.value;
	    	indInertia.setup(J, inertiaCurrent, Inductor.FLAG_BACK_EULER);
	    }
	    if (ei.value > 0 & n==6)
	    	b = ei.value;
	    if (ei.value > 0 & n==7)
	    	gearRatio = ei.value;
		
	 /*   if (ei.value > 0 & n==2)
	        K = ei.value;
	    if (ei.value > 0 & n==3)
	        b = ei.value;
	    if (ei.value > 0 & n==4)
	        J = ei.value;*/
	}
	protected boolean needsShortcut() { return true; }
    }
