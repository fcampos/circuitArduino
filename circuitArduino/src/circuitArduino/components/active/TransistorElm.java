package circuitArduino.components.active;
import java.awt.*;
import java.util.StringTokenizer;

import circuitArduino.CircuitElm;
import circuitArduino.UI.EditInfo;
import circuitArduino.components.labels_instruments.Scope;

public    class TransistorElm extends CircuitElm {
	int pnp;
	int darlington=0;
	double darBeta = 10E4;
	double beta;
	double fgain;
	double gmin;
	final int FLAG_FLIP = 1;
	TransistorElm(int xx, int yy, boolean pnpflag) {
	    super(xx, yy);
	    pnp = (pnpflag) ? -1 : 1;
	    beta = 100;
	    darlington=0;
	    darBeta = 10E4;
	    setup();
	}
	public TransistorElm(int xa, int ya, int xb, int yb, int f,
		      StringTokenizer st) {
	    super(xa, ya, xb, yb, f);
	    pnp = new Integer(st.nextToken()).intValue();
	    beta = 100;
	    try {
		lastvbe = new Double(st.nextToken()).doubleValue();
		lastvbc = new Double(st.nextToken()).doubleValue();
		volts[0] = 0;
		volts[1] = -lastvbe;
		volts[2] = -lastvbc;
		beta = new Double(st.nextToken()).doubleValue();
		darBeta = new Double(st.nextToken()).doubleValue();
		darlington = new Integer(st.nextToken()).intValue();
	    } catch (Exception e) {
	    }
	    setup();
	}
	void setup() {
		//System.out.println(darlington);
		if (darlington==0)
			{
			vt = .025;
			fgain = beta/(beta+1);
			}
		else{
			vt = .025*2.4;
			fgain = darBeta/(darBeta+1);}
		
		//System.out.println(vt);
		 vdcoef = 1/vt;
		vcrit =  vt * Math.log(vt/(Math.sqrt(2)*leakage));
		//fgain = beta/(beta+1);
		//System.out.println(fgain);
		noDiagonal = true;
	}
	protected boolean nonLinear() { return true; }
	protected void reset() {
	    volts[0] = volts[1] = volts[2] = 0;
	    lastvbc = lastvbe = curcount_c = curcount_e = curcount_b = 0;
	}
	protected int getDumpType() { return 't'; }
	protected String dump() {
	    return super.dump() + " " + pnp + " " + (volts[0]-volts[1]) + " " +
		(volts[0]-volts[2]) + " " + beta + " " + darBeta + " " + darlington;
	}
	double ic, ie, ib, curcount_c, curcount_e, curcount_b;
	Polygon rectPoly, arrowPoly;
	
	protected void draw(Graphics g) {
		int hs = sim.gridSize;
	    setBbox(point1, point2,hs*2);// 16);
	    setPowerColor(g, true);
	    // draw collector
	    setVoltageColor(g, volts[1]);
	    drawThickLine(g, coll[0], coll[1]);
	    // draw emitter
	    setVoltageColor(g, volts[2]);
	    drawThickLine(g, emit[0], emit[1]);
	    // draw arrow
	    g.setColor(lightGrayColor);
	    g.fillPolygon(arrowPoly);
	    if (sim.exportEPS)
			sim.g2.fillPolygon(arrowPoly);
		    
	    // draw base
	    setVoltageColor(g, volts[0]);
	    if (sim.powerCheckItem.getState())
		g.setColor(Color.gray);
	    drawThickLine(g, point1, base);
	    // draw dots
	    curcount_b = updateDotCount(-ib, curcount_b);
	    drawDots(g, base, point1, curcount_b);
	    curcount_c = updateDotCount(-ic, curcount_c);
	    drawDots(g, coll[1], coll[0], curcount_c);
	    curcount_e = updateDotCount(-ie, curcount_e);
	    drawDots(g, emit[1], emit[0], curcount_e);
	    // draw base rectangle
	    setVoltageColor(g, volts[0]);
	    setPowerColor(g, true);
	    g.fillPolygon(rectPoly);
	    if (sim.exportEPS)
			sim.g2.fillPolygon(rectPoly);
		
	    if ((needsHighlight() || sim.dragElm == this) && dy == 0) {
		g.setColor(Color.white);
		g.setFont(unitsFont);
		int ds = sign(dx);
		g.drawString("B", base.x-10*ds, base.y-5);
		g.drawString("C", coll[0].x-3+9*ds, coll[0].y+4); // x+6 if ds=1, -12 if -1
		g.drawString("E", emit[0].x-3+9*ds, emit[0].y+4);
	    }
	    drawPosts(g);
	}
	protected Point getPost(int n) {
	    return (n == 0) ? point1 : (n == 1) ? coll[0] : emit[0];
	}
	
	protected int getPostCount() { return 3; }
	protected double getPower() {
	    return (volts[0]-volts[2])*ib + (volts[1]-volts[2])*ic;
	}

	Point rect[], coll[], emit[], base;
	protected void setPoints() {
	    super.setPoints();
	    //System.out.println(sim.gridSize*2);
	    int hs = sim.gridSize*2;//16;
	    if ((flags & FLAG_FLIP) != 0)
		dsign = -dsign;
	    int hs2 = hs*dsign*pnp;
	    // calc collector, emitter posts
	    coll = newPointArray(2);
	    emit = newPointArray(2);
	    interpPoint2(point1, point2, coll[0], emit[0], 1, hs2);
	    // calc rectangle edges
	    rect = newPointArray(4);
	    interpPoint2(point1, point2, rect[0], rect[1], 1-hs/dn, hs);//1-16/dn, hs);
	    interpPoint2(point1, point2, rect[2], rect[3], 1-0.81*hs/dn, hs);//1-13/dn, hs);
	    // calc points where collector/emitter leads contact rectangle
	    interpPoint2(point1, point2, coll[1], emit[1], 1-0.81*hs/dn,0.375*hs*dsign*pnp);// 6*dsign*pnp);
	    // calc point where base lead contacts rectangle
	    base = new Point();
	    interpPoint (point1, point2, base, 1-hs/dn);//1-16/dn);

	    // rectangle
	    rectPoly = createPolygon(rect[0], rect[2], rect[3], rect[1]);

	    // arrow
	    if (pnp == 1)
		arrowPoly = calcArrow(emit[1], emit[0],hs/2, hs/4);// 8, 4);
	    else {
		Point pt = interpPoint(point1, point2, 1-0.81*hs/dn, -0.3125*hs*dsign*pnp);// 1-11/dn, -5*dsign*pnp);
		arrowPoly = calcArrow(emit[0], pt, hs/2, hs/4);// 8, 4);
	    }
	}
	
	static final double leakage = 1e-13; // 1e-6;
	double vt = .025;
	double vdcoef = 1/vt;
	static final double rgain = .5;
	double vcrit;
	double lastvbc, lastvbe;
	double limitStep(double vnew, double vold) {
	    double arg;
	    double oo = vnew;
	    
	    if (vnew > vcrit && Math.abs(vnew - vold) > (vt + vt)) {
		if(vold > 0) {
		    arg = 1 + (vnew - vold) / vt;
		    if(arg > 0) {
			vnew = vold + vt * Math.log(arg);
		    } else {
			vnew = vcrit;
		    }
		} else {
		    vnew = vt *Math.log(vnew/vt);
		}
		sim.converged = false;
		//System.out.println(vnew + " " + oo + " " + vold);
	    }
	    return(vnew);
	}
	protected void stamp() {
	    sim.algorithm.stampNonLinear(nodes[0]);
	    sim.algorithm.stampNonLinear(nodes[1]);
	    sim.algorithm.stampNonLinear(nodes[2]);
	}
	protected void doStep() {
	    double vbc = volts[0]-volts[1]; // typically negative
	    double vbe = volts[0]-volts[2]; // typically positive
	    if (Math.abs(vbc-lastvbc) > .01 || // .01
		Math.abs(vbe-lastvbe) > .01)
		sim.converged = false;
	    gmin = 0;
	    if (sim.subIterations > 100) {
		// if we have trouble converging, put a conductance in parallel with all P-N junctions.
		// Gradually increase the conductance value for each iteration.
		gmin = Math.exp(-9*Math.log(10)*(1-sim.subIterations/3000.));
		if (gmin > .1)
		    gmin = .1;
	    }
	    //System.out.print("T " + vbc + " " + vbe + "\n");
	    vbc = pnp*limitStep(pnp*vbc, pnp*lastvbc);
	    vbe = pnp*limitStep(pnp*vbe, pnp*lastvbe);
	    lastvbc = vbc;
	    lastvbe = vbe;
	    double pcoef = vdcoef*pnp;
	    double expbc = Math.exp(vbc*pcoef);
	    /*if (expbc > 1e13 || Double.isInfinite(expbc))
	      expbc = 1e13;*/
	    double expbe = Math.exp(vbe*pcoef);
	    if (expbe < 1)
		expbe = 1;
	    /*if (expbe > 1e13 || Double.isInfinite(expbe))
	      expbe = 1e13;*/
	    ie = pnp*leakage*(-(expbe-1)+rgain*(expbc-1));
	    ic = pnp*leakage*(fgain*(expbe-1)-(expbc-1));
	    ib = -(ie+ic);
	    //System.out.println("gain " + ic/ib);
	    //System.out.print("T " + vbc + " " + vbe + " " + ie + " " + ic + "\n");
	    double gee = -leakage*vdcoef*expbe;
	    double gec = rgain*leakage*vdcoef*expbc;
	    double gce = -gee*fgain;
	    double gcc = -gec*(1/rgain);

	    /*System.out.print("gee = " + gee + "\n");
	    System.out.print("gec = " + gec + "\n");
	    System.out.print("gce = " + gce + "\n");
	    System.out.print("gcc = " + gcc + "\n");
	    System.out.print("gce+gcc = " + (gce+gcc) + "\n");
	    System.out.print("gee+gec = " + (gee+gec) + "\n");*/
	    
	    // stamps from page 302 of Pillage.  Node 0 is the base,
	    // node 1 the collector, node 2 the emitter.  Also stamp
	    // minimum conductance (gmin) between b,e and b,c
	    sim.algorithm.stampMatrix(nodes[0], nodes[0], -gee-gec-gce-gcc + gmin*2);
	    sim.algorithm.stampMatrix(nodes[0], nodes[1], gec+gcc - gmin);
	    sim.algorithm.stampMatrix(nodes[0], nodes[2], gee+gce - gmin);
	    sim.algorithm.stampMatrix(nodes[1], nodes[0], gce+gcc - gmin);
	    sim.algorithm.stampMatrix(nodes[1], nodes[1], -gcc + gmin);
	    sim.algorithm.stampMatrix(nodes[1], nodes[2], -gce);
	    sim.algorithm.stampMatrix(nodes[2], nodes[0], gee+gec - gmin);
	    sim.algorithm.stampMatrix(nodes[2], nodes[1], -gec);
	    sim.algorithm.stampMatrix(nodes[2], nodes[2], -gee + gmin);

	    // we are solving for v(k+1), not delta v, so we use formula
	    // 10.5.13, multiplying J by v(k)
	    sim.algorithm.stampRightSide(nodes[0], -ib - (gec+gcc)*vbc - (gee+gce)*vbe);
	    sim.algorithm.stampRightSide(nodes[1], -ic + gce*vbe + gcc*vbc);
	    sim.algorithm.stampRightSide(nodes[2], -ie + gee*vbe + gec*vbc);
	}
	protected void getInfo(String arr[]) {
	    arr[0] = "transistor (" + ((pnp == -1) ? "PNP)" : "NPN)") + " beta=" +
		showFormat.format(beta);
	    double vbc = volts[0]-volts[1];
	    double vbe = volts[0]-volts[2];
	    double vce = volts[1]-volts[2];
	    if (vbc*pnp > .2)
		arr[1] = vbe*pnp > .2 ? "saturation" : "reverse active";
	    else
		arr[1] = vbe*pnp > .2 ? "fwd active" : "cutoff";
	    arr[2] = "Ic = " + getCurrentText(ic);
	    arr[3] = "Ib = " + getCurrentText(ib);
	    arr[4] = "Vbe = " + getVoltageText(vbe);
	    arr[5] = "Vbc = " + getVoltageText(vbc);
	    arr[6] = "Vce = " + getVoltageText(vce);
	}
	public double getScopeValue(int x) {
	    switch (x) {
	    case Scope.VAL_IB: return ib;
	    case Scope.VAL_IC: return ic;
	    case Scope.VAL_IE: return ie;
	    case Scope.VAL_VBE: return volts[0]-volts[2];
	    case Scope.VAL_VBC: return volts[0]-volts[1];
	    case Scope.VAL_VCE: return volts[1]-volts[2];
	    }
	    return 0;
	}
	public String getScopeUnits(int x) {
	    switch (x) {
	    case Scope.VAL_IB: case Scope.VAL_IC:
	    case Scope.VAL_IE: return "A";
	    default: return "V";
	    }
	}
	public EditInfo getEditInfo(int n) {
	    if (n == 0)
		return new EditInfo("Beta/hFE standard", beta , 10, 1000).
		    setDimensionless();
	    if (n == 1)
			return new EditInfo("Beta/hFE Darlington", darBeta , 100, 50000).
			    setDimensionless();
	    if (n == 2) {
		EditInfo ei = new EditInfo("", 0, -1, -1);
		ei.checkbox = new Checkbox("Swap E/C", (flags & FLAG_FLIP) != 0);
		return ei;
	    }
	    if (n == 3) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Darlington", (darlington== 1));
			return ei;
	    }
	    return null;
	}
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			beta = ei.value;
			setup();
		}
		if (n == 1) {
			darBeta = ei.value;
			setup();
		}
		if (n == 2) {
			if (ei.checkbox.getState())
				flags |= FLAG_FLIP;
			else
				flags &= ~FLAG_FLIP;
			setPoints();
		}
		if (n == 3) {
			if (ei.checkbox.getState())
			{
				darlington=1;
				setup();}
			else
			{
				darlington=0;
				setup();
			}
		}

	}
	protected boolean canViewInScope() { return true; }
    }
