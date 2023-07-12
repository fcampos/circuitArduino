package circuitArduino;
import java.awt.Point;
import java.util.Vector;

import circuitArduino.CirSim;
import circuitArduino.CircuitElm;
import circuitArduino.CircuitNode;
import circuitArduino.RowInfo;

//import CirSim.FindPathInfo;

public class Algorithm {
	CirSim sim;
	// double circuitMatrix[][], circuitRightSide[],
		//origRightSide[], origMatrix[][];
	 //   RowInfo circuitRowInfo[];
	//   int circuitPermute[];
	//   boolean circuitNonLinear;
	//    int voltageSourceCount;
	//    int circuitMatrixSize, circuitMatrixFullSize;
	//    boolean circuitNeedsMap;
	//    boolean dumpMatrix;
	//    long lastTime = 0, lastFrameTime, lastIterTime, secTime = 0;
	//    int frames = 0;
	    //    int steps = 0;
	//    int framerate = 0, steprate = 0;
	//    boolean converged;
	//    int subIterations;
	  //  Vector<CircuitNode> nodeList;
	//    CircuitElm voltageSources[];
	  //  double t;
	Algorithm(CirSim s){
		sim = s;
	}
	 // control voltage source vs with voltage from n1 to n2 (must
    // also call stampVoltageSource())
    public void stampVCVS(int n1, int n2, double coef, int vs) {
	int vn = sim.nodeList.size()+vs;
	stampMatrix(vn, n1, coef);
	stampMatrix(vn, n2, -coef);
    }
    
    // stamp independent voltage source #vs, from n1 to n2, amount v
    public void stampVoltageSource(int n1, int n2, int vs, double v) {
	int vn = sim.nodeList.size()+vs;
	stampMatrix(vn, n1, -1);
	stampMatrix(vn, n2, 1);
	stampRightSide(vn, v);
	stampMatrix(n1, vn, 1);
	stampMatrix(n2, vn, -1);
    }

    // use this if the amount of voltage is going to be updated in doStep()
    public void stampVoltageSource(int n1, int n2, int vs) {
	int vn = sim.nodeList.size()+vs;
	stampMatrix(vn, n1, -1);
	stampMatrix(vn, n2, 1);
	stampRightSide(vn);
	stampMatrix(n1, vn, 1);
	stampMatrix(n2, vn, -1);
    }
    
    public void updateVoltageSource(int n1, int n2, int vs, double v) {
	int vn = sim.nodeList.size()+vs;
	stampRightSide(vn, v);
    }
    
    public void stampResistor(int n1, int n2, double r) {
	double r0 = 1/r;
	if (Double.isNaN(r0) || Double.isInfinite(r0)) {
	    System.out.print("bad resistance " + r + " " + r0 + "\n");
	    int a = 0;
	    a /= a;
	}
	stampMatrix(n1, n1, r0);
	stampMatrix(n2, n2, r0);
	stampMatrix(n1, n2, -r0);
	stampMatrix(n2, n1, -r0);
    }

    public void stampConductance(int n1, int n2, double r0) {
	stampMatrix(n1, n1, r0);
	stampMatrix(n2, n2, r0);
	stampMatrix(n1, n2, -r0);
	stampMatrix(n2, n1, -r0);
    }

    // current from cn1 to cn2 is equal to voltage from vn1 to 2, divided by g
    public void stampVCCurrentSource(int cn1, int cn2, int vn1, int vn2, double g) {
	stampMatrix(cn1, vn1, g);
	stampMatrix(cn2, vn2, g);
	stampMatrix(cn1, vn2, -g);
	stampMatrix(cn2, vn1, -g);
    }

    public void stampCurrentSource(int n1, int n2, double i) {
	stampRightSide(n1, -i);
	stampRightSide(n2, i);
    }

    // stamp a current source from n1 to n2 depending on current through vs
    public void stampCCCS(int n1, int n2, int vs, double gain) {
	int vn = sim.nodeList.size()+vs;
	stampMatrix(n1, vn, gain);
	stampMatrix(n2, vn, -gain);
    }

    // stamp value x in row i, column j, meaning that a voltage change
    // of dv in node j will increase the current into node i by x dv.
    // (Unless i or j is a voltage source node.)
    public void stampMatrix(int i, int j, double x) {
	if (i > 0 && j > 0) {
	    if (sim.circuitNeedsMap) {
		i = sim.circuitRowInfo[i-1].mapRow;
		RowInfo ri = sim.circuitRowInfo[j-1];
		if (ri.type == RowInfo.ROW_CONST) {
		    //System.out.println("Stamping constant " + i + " " + j + " " + x);
		//	sim.circuitRightSide[i] -= x*ri.value;
			sim.circuitRightSide.unsafe_set(i,0,sim.circuitRightSide.unsafe_get(i,0) - x*ri.value);
		    return;
		}
		j = ri.mapCol;
		//System.out.println("stamping " + i + " " + j + " " + x);
	    } else {
		i--;
		j--;
	    }
	  //  System.out.println("stamping " + i + " " + j + " " + x);
	  //  System.out.println("is full?" + sim.circuitMatrix.isFull());
	  //  if (Math.abs(x)>0)
	    sim.circuitMatrix.unsafe_set(i,j,sim.circuitMatrix.unsafe_get(i,j) + x);
	    //sim.circuitMatrix[i][j] += x;
	}
    }

    // stamp value x on the right side of row i, representing an
    // independent current source flowing into node i
   public void stampRightSide(int i, double x) {
	if (i > 0) {
	    if (sim.circuitNeedsMap) {
		i = sim.circuitRowInfo[i-1].mapRow;
		//System.out.println("stamping " + i + " " + x);
	    } else
		i--;
	//    System.out.println("matrixsize: " + sim.circuitMatrixSize + " i: " + i);
	    sim.circuitRightSide.unsafe_set(i,0,sim.circuitRightSide.unsafe_get(i,0) + x);
	    //sim.circuitRightSide[i] += x;
	}
    }

    // indicate that the value on the right side of row i changes in doStep()
   public void stampRightSide(int i) {
	//System.out.println("rschanges true " + (i-1));
	if (i > 0)
		sim.circuitRowInfo[i-1].rsChanges = true;
    }
    
    // indicate that the values on the left side of row i change in doStep()
    public void stampNonLinear(int i) {
	if (i > 0)
		sim.circuitRowInfo[i-1].lsChanges = true;
    }
/*
    public void runCircuit() {
    	if (circuitMatrix == null || sim.elmList.size() == 0) {
    	    circuitMatrix = null;
    	    return;
    	}
    	int iter;
    	//int maxIter = sim.getIterCount();
    	boolean debugprint = dumpMatrix;
    	dumpMatrix = false;
    	long steprate = (long) (160*sim.getIterCount());
    	long tm = System.currentTimeMillis();
    	long lit = lastIterTime;
    	if (1000 >= steprate*(tm-lastIterTime))
    	    return;
    	for (iter = 1; ; iter++) {
    	    int i, j, k, subiter;
    	    for (i = 0; i != sim.elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		ce.startIteration();
    	    }
    	    steps++;
    	    final int subiterCount = 5000;
    	    for (subiter = 0; subiter != subiterCount; subiter++) {
    		converged = true;
    		subIterations = subiter;
    		for (i = 0; i != circuitMatrixSize; i++)
    		    circuitRightSide[i] = origRightSide[i];
    		if (circuitNonLinear) {
    		    for (i = 0; i != circuitMatrixSize; i++)
    			for (j = 0; j != circuitMatrixSize; j++)
    			    circuitMatrix[i][j] = origMatrix[i][j];
    		}
    		for (i = 0; i != sim.elmList.size(); i++) {
    		    CircuitElm ce = getElm(i);
    		    ce.doStep();
    		}
    		if (sim.stopMessage != null)
    		    return;
    		boolean printit = debugprint;
    		debugprint = false;
    		for (j = 0; j != circuitMatrixSize; j++) {
    		    for (i = 0; i != circuitMatrixSize; i++) {
    			double x = circuitMatrix[i][j];
    			if (Double.isNaN(x) || Double.isInfinite(x)) {
    			    sim.stop("nan/infinite matrix!", null);
    			    return;
    			}
    		    }
    		}
    		if (printit) {
    		    for (j = 0; j != circuitMatrixSize; j++) {
    			for (i = 0; i != circuitMatrixSize; i++)
    			    System.out.print(circuitMatrix[j][i] + ",");
    			System.out.print("  " + circuitRightSide[j] + "\n");
    		    }
    		    System.out.print("\n");
    		}
    		if (circuitNonLinear) {
    		    if (converged && subiter > 0)
    			break;
    		    if (!lu_factor(circuitMatrix, circuitMatrixSize,
    				  circuitPermute)) {
    			sim.stop("Singular matrix!", null);
    			return;
    		    }
    		}
    		lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute,
    			 circuitRightSide);
    		
    		for (j = 0; j != circuitMatrixFullSize; j++) {
    		    RowInfo ri = circuitRowInfo[j];
    		    double res = 0;
    		    if (ri.type == RowInfo.ROW_CONST)
    			res = ri.value;
    		    else
    			res = circuitRightSide[ri.mapCol];
    		//    /*System.out.println(j + " " + res + " " +
    		//      ri.type + " " + ri.mapCol);
    		    if (Double.isNaN(res)) {
    			converged = false;
    			//debugprint = true;
    			break;
    		    }
    		    if (j < nodeList.size()-1) {
    			CircuitNode cn = getCircuitNode(j+1);
    			for (k = 0; k != cn.links.size(); k++) {
    			    CircuitNodeLink cnl = (CircuitNodeLink)
    				cn.links.elementAt(k);
    			    cnl.elm.setNodeVoltage(cnl.num, res);
    			}
    		    } else {
    			int ji = j-(nodeList.size()-1);
    			//System.out.println("setting vsrc " + ji + " to " + res);
    			voltageSources[ji].setCurrent(ji, res);
    		    }
    		}
    		if (!circuitNonLinear)
    		    break;
    	    }
    	  //  if (subiter > 5)
    		//System.out.print("converged after " + subiter + " iterations\n");
    	    if (subiter == subiterCount) {
    		sim.stop("Convergence failed!", null);
    		break;
    	    }
    	    sim.t += sim.timeStep;
    	    for (i = 0; i != sim.scopeCount; i++)
    		sim.scopes[i].timeStep();
    	    tm = System.currentTimeMillis();
    	    lit = tm;
    	    if (iter*1000 >= steprate*(tm-lastIterTime) ||
    		(tm-lastFrameTime > 500))
    		break;
    	}
    	lastIterTime = lit;
    	//System.out.println((System.currentTimeMillis()-lastFrameTime)/(double) iter);
        }

    */
    
    /*
    void analyzeCircuit() {
    	sim.calcCircuitBottom();
	if (sim.elmList.isEmpty())
	    return;
	sim.stopMessage = null;
	sim.stopElm = null;
	int i, j;
	int vscount = 0;
	nodeList = new Vector();
	boolean gotGround = false;
	boolean gotRail = false;
	CircuitElm volt = null;

	//System.out.println("ac1");
	// look for voltage or ground element
	for (i = 0; i != sim.elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (ce instanceof GroundElm) {
		gotGround = true;
		break;
	    }
	    if (ce instanceof RailElm)
		gotRail = true;
	    if (volt == null && ce instanceof VoltageElm)
		volt = ce;
	}

	// if no ground, and no rails, then the voltage elm's first terminal
	// is ground
	if (!gotGround && volt != null && !gotRail) {
	    CircuitNode cn = new CircuitNode();
	    Point pt = volt.getPost(0);
	    cn.x = pt.x;
	    cn.y = pt.y;
	    nodeList.addElement(cn);
	} else {
	    // otherwise allocate extra node for ground
	    CircuitNode cn = new CircuitNode();
	    cn.x = cn.y = -1;
	    nodeList.addElement(cn);
	}
	//System.out.println("ac2");

	// allocate nodes and voltage sources
	for (i = 0; i != sim.elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    int inodes = ce.getInternalNodeCount();
	    int ivs = ce.getVoltageSourceCount();
	    int posts = ce.getPostCount();
	    
	    // allocate a node for each post and match posts to nodes
	    for (j = 0; j != posts; j++) {
		Point pt = ce.getPost(j);
		int k;
		for (k = 0; k != nodeList.size(); k++) {
		    CircuitNode cn = getCircuitNode(k);
		    if (pt.x == cn.x && pt.y == cn.y)
			break;
		}
		if (k == nodeList.size()) {
		    CircuitNode cn = new CircuitNode();
		    cn.x = pt.x;
		    cn.y = pt.y;
		    CircuitNodeLink cnl = new CircuitNodeLink();
		    cnl.num = j;
		    cnl.elm = ce;
		    cn.links.addElement(cnl);
		    ce.setNode(j, nodeList.size());
		    nodeList.addElement(cn);
		} else {
		    CircuitNodeLink cnl = new CircuitNodeLink();
		    cnl.num = j;
		    cnl.elm = ce;
		    getCircuitNode(k).links.addElement(cnl);
		    ce.setNode(j, k);
		    // if it's the ground node, make sure the node voltage is 0,
		    // cause it may not get set later
		    if (k == 0)
			ce.setNodeVoltage(j, 0);
		}
	    }
	    for (j = 0; j != inodes; j++) {
		CircuitNode cn = new CircuitNode();
		cn.x = cn.y = -1;
		cn.internal = true;
		CircuitNodeLink cnl = new CircuitNodeLink();
		cnl.num = j+posts;
		cnl.elm = ce;
		cn.links.addElement(cnl);
		ce.setNode(cnl.num, nodeList.size());
		nodeList.addElement(cn);
	    }
	    vscount += ivs;
	}
	voltageSources = new CircuitElm[vscount];
	vscount = 0;
	circuitNonLinear = false;
	//System.out.println("ac3");

	// determine if circuit is nonlinear
	for (i = 0; i != sim.elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    if (ce.nonLinear())
		circuitNonLinear = true;
	    int ivs = ce.getVoltageSourceCount();
	    for (j = 0; j != ivs; j++) {
		voltageSources[vscount] = ce;
		ce.setVoltageSource(j, vscount++);
	    }
	}
	voltageSourceCount = vscount;

	int matrixSize = nodeList.size()-1 + vscount;
	circuitMatrix = new double[matrixSize][matrixSize];
	circuitRightSide = new double[matrixSize];
	origMatrix = new double[matrixSize][matrixSize];
	origRightSide = new double[matrixSize];
	circuitMatrixSize = circuitMatrixFullSize = matrixSize;
	circuitRowInfo = new RowInfo[matrixSize];
	circuitPermute = new int[matrixSize];
	int vs = 0;
	for (i = 0; i != matrixSize; i++)
	    circuitRowInfo[i] = new RowInfo();
	circuitNeedsMap = false;
	
	// stamp linear circuit elements
	for (i = 0; i != sim.elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    ce.stamp();
	}
	//System.out.println("ac4");

	// determine nodes that are unconnected
	boolean closure[] = new boolean[nodeList.size()];
	boolean tempclosure[] = new boolean[nodeList.size()];
	boolean changed = true;
	closure[0] = true;
	while (changed) {
	    changed = false;
	    for (i = 0; i != sim.elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		// loop through all ce's nodes to see if they are connected
		// to other nodes not in closure
		for (j = 0; j < ce.getPostCount(); j++) {
		    if (!closure[ce.getNode(j)]) {
			if (ce.hasGroundConnection(j))
			    closure[ce.getNode(j)] = changed = true;
			continue;
		    }
		    int k;
		    for (k = 0; k != ce.getPostCount(); k++) {
			if (j == k)
			    continue;
			int kn = ce.getNode(k);
			if (ce.getConnection(j, k) && !closure[kn]) {
			    closure[kn] = true;
			    changed = true;
			}
		    }
		}
	    }
	    if (changed)
		continue;

	    // connect unconnected nodes
	    for (i = 0; i != nodeList.size(); i++)
		if (!closure[i] && !getCircuitNode(i).internal) {
		   // System.out.println("node " + i + " unconnected");
		    stampResistor(0, i, 1e8);
		    closure[i] = true;
		    changed = true;
		    break;
		}
	}
	//System.out.println("ac5");

	for (i = 0; i != sim.elmList.size(); i++) {
	    CircuitElm ce = getElm(i);
	    // look for inductors with no current path
	    if (ce instanceof InductorElm) {
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
						    ce.getNode(1));
		// first try findPath with maximum depth of 5, to avoid slowdowns
		if (!fpi.findPath(ce.getNode(0), 5) &&
		    !fpi.findPath(ce.getNode(0))) {
		 //   System.out.println(ce + " no path");
		    ce.reset();
		}
	    }
	    // look for current sources with no current path
	    if (ce instanceof CurrentElm) {
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce,
						    ce.getNode(1));
		if (!fpi.findPath(ce.getNode(0))) {
		    sim.stop("No path for current source!", ce);
		    return;
		}
	    }
	    // look for voltage source loops
	    if ((ce instanceof VoltageElm && ce.getPostCount() == 2) ||
		ce instanceof WireElm) {
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce,
						    ce.getNode(1));
		if (fpi.findPath(ce.getNode(0))) {
		    sim.stop("Voltage source/wire loop with no resistance!", ce);
		    return;
		}
	    }
	    // look for shorted caps, or caps w/ voltage but no R
	    if (ce instanceof CapacitorElm) {
		FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce,
						    ce.getNode(1));
		if (fpi.findPath(ce.getNode(0))) {
		    System.out.println(ce + " shorted");
		    ce.reset();
		} else {
		    fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1));
		    if (fpi.findPath(ce.getNode(0))) {
			sim.stop("Capacitor loop with no resistance!", ce);
			return;
		    }
		}
	    }
	}
	//System.out.println("ac6");

	// simplify the matrix; this speeds things up quite a bit
	for (i = 0; i != matrixSize; i++) {
	    int qm = -1, qp = -1;
	    double qv = 0;
	    RowInfo re = circuitRowInfo[i];
	 //   /*System.out.println("row " + i + " " + re.lsChanges + " " + re.rsChanges + " " +
	//		       re.dropRow);
	    if (re.lsChanges || re.dropRow || re.rsChanges)
		continue;
	    double rsadd = 0;

	    // look for rows that can be removed
	    for (j = 0; j != matrixSize; j++) {
		double q = circuitMatrix[i][j];
		if (circuitRowInfo[j].type == RowInfo.ROW_CONST) {
		    // keep a running total of const values that have been
		    // removed already
		    rsadd -= circuitRowInfo[j].value*q;
		    continue;
		}
		if (q == 0)
		    continue;
		if (qp == -1) {
		    qp = j;
		    qv = q;
		    continue;
		}
		if (qm == -1 && q == -qv) {
		    qm = j;
		    continue;
		}
		break;
	    }
	    //System.out.println("line " + i + " " + qp + " " + qm + " " + j);
	   // /*if (qp != -1 && circuitRowInfo[qp].lsChanges) {
	//	System.out.println("lschanges");
	//	continue;
	 //   }
	  //  if (qm != -1 && circuitRowInfo[qm].lsChanges) {
	//	System.out.println("lschanges");
	//	continue;
	//	}
	    if (j == matrixSize) {
		if (qp == -1) {
		    sim.stop("Matrix error", null);
		    return;
		}
		RowInfo elt = circuitRowInfo[qp];
		if (qm == -1) {
		    // we found a row with only one nonzero entry; that value
		    // is a constant
		    int k;
		    for (k = 0; elt.type == RowInfo.ROW_EQUAL && k < 100; k++) {
			// follow the chain
	//		/*System.out.println("following equal chain from " +
	//				   i + " " + qp + " to " + elt.nodeEq);
			qp = elt.nodeEq;
			elt = circuitRowInfo[qp];
		    }
		    if (elt.type == RowInfo.ROW_EQUAL) {
			// break equal chains
			//System.out.println("Break equal chain");
			elt.type = RowInfo.ROW_NORMAL;
			continue;
		    }
		    if (elt.type != RowInfo.ROW_NORMAL) {
			System.out.println("type already " + elt.type + " for " + qp + "!");
			continue;
		    }
		    elt.type = RowInfo.ROW_CONST;
		    elt.value = (circuitRightSide[i]+rsadd)/qv;
		    circuitRowInfo[i].dropRow = true;
		    //System.out.println(qp + " * " + qv + " = const " + elt.value);
		    i = -1; // start over from scratch
		} else if (circuitRightSide[i]+rsadd == 0) {
		    // we found a row with only two nonzero entries, and one
		    // is the negative of the other; the values are equal
		    if (elt.type != RowInfo.ROW_NORMAL) {
			//System.out.println("swapping");
			int qq = qm;
			qm = qp; qp = qq;
			elt = circuitRowInfo[qp];
			if (elt.type != RowInfo.ROW_NORMAL) {
			    // we should follow the chain here, but this
			    // hardly ever happens so it's not worth worrying
			    // about
			 //   System.out.println("swap failed");
			    continue;
			}
		    }
		    elt.type = RowInfo.ROW_EQUAL;
		    elt.nodeEq = qm;
		    circuitRowInfo[i].dropRow = true;
		    //System.out.println(qp + " = " + qm);
		}
	    }
	}
	//System.out.println("ac7");

	// find size of new matrix
	int nn = 0;
	for (i = 0; i != matrixSize; i++) {
	    RowInfo elt = circuitRowInfo[i];
	    if (elt.type == RowInfo.ROW_NORMAL) {
		elt.mapCol = nn++;
		//System.out.println("col " + i + " maps to " + elt.mapCol);
		continue;
	    }
	    if (elt.type == RowInfo.ROW_EQUAL) {
		RowInfo e2 = null;
		// resolve chains of equality; 100 max steps to avoid loops
		for (j = 0; j != 100; j++) {
		    e2 = circuitRowInfo[elt.nodeEq];
		    if (e2.type != RowInfo.ROW_EQUAL)
			break;
		    if (i == e2.nodeEq)
			break;
		    elt.nodeEq = e2.nodeEq;
		}
	    }
	    if (elt.type == RowInfo.ROW_CONST)
		elt.mapCol = -1;
	}
	for (i = 0; i != matrixSize; i++) {
	    RowInfo elt = circuitRowInfo[i];
	    if (elt.type == RowInfo.ROW_EQUAL) {
		RowInfo e2 = circuitRowInfo[elt.nodeEq];
		if (e2.type == RowInfo.ROW_CONST) {
		    // if something is equal to a const, it's a const
		    elt.type = e2.type;
		    elt.value = e2.value;
		    elt.mapCol = -1;
		    //System.out.println(i + " = [late]const " + elt.value);
		} else {
		    elt.mapCol = e2.mapCol;
		    //System.out.println(i + " maps to: " + e2.mapCol);
		}
	    }
	}
	//System.out.println("ac8");

//	/*System.out.println("matrixSize = " + matrixSize);
	
//	for (j = 0; j != circuitMatrixSize; j++) {
//	    System.out.println(j + ": ");
//	    for (i = 0; i != circuitMatrixSize; i++)
//		System.out.print(circuitMatrix[j][i] + " ");
//	    System.out.print("  " + circuitRightSide[j] + "\n");
//	}
//	System.out.print("\n");
	

	// make the new, simplified matrix
	int newsize = nn;
	double newmatx[][] = new double[newsize][newsize];
	double newrs  []   = new double[newsize];
	int ii = 0;
	for (i = 0; i != matrixSize; i++) {
	    RowInfo rri = circuitRowInfo[i];
	    if (rri.dropRow) {
		rri.mapRow = -1;
		continue;
	    }
	    newrs[ii] = circuitRightSide[i];
	    rri.mapRow = ii;
	    //System.out.println("Row " + i + " maps to " + ii);
	    for (j = 0; j != matrixSize; j++) {
		RowInfo ri = circuitRowInfo[j];
		if (ri.type == RowInfo.ROW_CONST)
		    newrs[ii] -= ri.value*circuitMatrix[i][j];
		else
		    newmatx[ii][ri.mapCol] += circuitMatrix[i][j];
	    }
	    ii++;
	}

	circuitMatrix = newmatx;
	circuitRightSide = newrs;
	matrixSize = circuitMatrixSize = newsize;
	for (i = 0; i != matrixSize; i++)
	    origRightSide[i] = circuitRightSide[i];
	for (i = 0; i != matrixSize; i++)
	    for (j = 0; j != matrixSize; j++)
		origMatrix[i][j] = circuitMatrix[i][j];
	circuitNeedsMap = true;

	///*
//	System.out.println("matrixSize = " + matrixSize + " " + circuitNonLinear);
//	for (j = 0; j != circuitMatrixSize; j++) {
//	    for (i = 0; i != circuitMatrixSize; i++)
//		System.out.print(circuitMatrix[j][i] + " ");
//	    System.out.print("  " + circuitRightSide[j] + "\n");
//	}
//	System.out.print("\n");

	// if a matrix is linear, we can do the lu_factor here instead of
	// needing to do it every frame
	if (!circuitNonLinear) {
	    if (!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
		sim.stop("Singular matrix!", null);
		return;
	    }
	}
    }

 // factors a matrix into upper and lower triangular matrices by
    // gaussian elimination.  On entry, a[0..n-1][0..n-1] is the
    // matrix to be factored.  ipvt[] returns an integer vector of pivot
    // indices, used in the lu_solve() routine.
    boolean lu_factor(double a[][], int n, int ipvt[]) {
	double scaleFactors[];
	int i,j,k;

	scaleFactors = new double[n];
	
        // divide each row by its largest element, keeping track of the
	// scaling factors
	for (i = 0; i != n; i++) { 
	    double largest = 0;
	    for (j = 0; j != n; j++) {
		double x = Math.abs(a[i][j]);
		if (x > largest)
		    largest = x;
	    }
	    // if all zeros, it's a singular matrix
	    if (largest == 0)
		return false;
	    scaleFactors[i] = 1.0/largest;
	}
	
        // use Crout's method; loop through the columns
	for (j = 0; j != n; j++) {
	    
	    // calculate upper triangular elements for this column
	    for (i = 0; i != j; i++) {
		double q = a[i][j];
		for (k = 0; k != i; k++)
		    q -= a[i][k]*a[k][j];
		a[i][j] = q;
	    }

	    // calculate lower triangular elements for this column
	    double largest = 0;
	    int largestRow = -1;
	    for (i = j; i != n; i++) {
		double q = a[i][j];
		for (k = 0; k != j; k++)
		    q -= a[i][k]*a[k][j];
		a[i][j] = q;
		double x = Math.abs(q);
		if (x >= largest) {
		    largest = x;
		    largestRow = i;
		}
	    }
	    
	    // pivoting
	    if (j != largestRow) {
		double x;
		for (k = 0; k != n; k++) {
		    x = a[largestRow][k];
		    a[largestRow][k] = a[j][k];
		    a[j][k] = x;
		}
		scaleFactors[largestRow] = scaleFactors[j];
	    }

	    // keep track of row interchanges
	    ipvt[j] = largestRow;

	    // avoid zeros
	    if (a[j][j] == 0.0) {
		System.out.println("avoided zero");
		a[j][j]=1e-18;
	    }

	    if (j != n-1) {
		double mult = 1.0/a[j][j];
		for (i = j+1; i != n; i++)
		    a[i][j] *= mult;
	    }
	}
	return true;
    }

    // Solves the set of n linear equations using a LU factorization
    // previously performed by lu_factor.  On input, b[0..n-1] is the right
    // hand side of the equations, and on output, contains the solution.
    void lu_solve(double a[][], int n, int ipvt[], double b[]) {
	int i;

	// find first nonzero b element
	for (i = 0; i != n; i++) {
	    int row = ipvt[i];

	    double swap = b[row];
	    b[row] = b[i];
	    b[i] = swap;
	    if (swap != 0)
		break;
	}
	
	int bi = i++;
	for (; i < n; i++) {
	    int row = ipvt[i];
	    int j;
	    double tot = b[row];
	    
	    b[row] = b[i];
	    // forward substitution using the lower triangular matrix
	    for (j = bi; j < i; j++)
		tot -= a[i][j]*b[j];
	    b[i] = tot;
	}
	for (i = n-1; i >= 0; i--) {
	    double tot = b[i];
	    
	    // back-substitution using the upper triangular matrix
	    int j;
	    for (j = i+1; j != n; j++)
		tot -= a[i][j]*b[j];
	    b[i] = tot/a[i][i];
	}
    }

    class FindPathInfo {
    	static final int INDUCT  = 1;
    	static final int VOLTAGE = 2;
    	static final int SHORT   = 3;
    	static final int CAP_V   = 4;
    	boolean used[];
    	int dest;
    	CircuitElm firstElm;
    	int type;
    	FindPathInfo(int t, CircuitElm e, int d) {
    	    dest = d;
    	    type = t;
    	    firstElm = e;
    	    used = new boolean[nodeList.size()];
    	}
    	boolean findPath(int n1) { return findPath(n1, -1); }
    	boolean findPath(int n1, int depth) {
    	    if (n1 == dest)
    		return true;
    	    if (depth-- == 0)
    		return false;
    	    if (used[n1]) {
    		//System.out.println("used " + n1);
    		return false;
    	    }
    	    used[n1] = true;
    	    int i;
    	    for (i = 0; i != sim.elmList.size(); i++) {
    		CircuitElm ce = getElm(i);
    		if (ce == firstElm)
    		    continue;
    		if (type == INDUCT) {
    		    if (ce instanceof CurrentElm)
    			continue;
    		}
    		if (type == VOLTAGE) {
    		    if (!(ce.isWire() || ce instanceof VoltageElm))
    			continue;
    		}
    		if (type == SHORT && !ce.isWire())
    		    continue;
    		if (type == CAP_V) {
    		    if (!(ce.isWire() || ce instanceof CapacitorElm ||
    			  ce instanceof VoltageElm))
    			continue;
    		}
    		if (n1 == 0) {
    		    // look for posts which have a ground connection;
    		    // our path can go through ground
    		    int j;
    		    for (j = 0; j != ce.getPostCount(); j++)
    			if (ce.hasGroundConnection(j) &&
    			    findPath(ce.getNode(j), depth)) {
    			    used[n1] = false;
    			    return true;
    			}
    		}
    		int j;
    		for (j = 0; j != ce.getPostCount(); j++) {
    		    //System.out.println(ce + " " + ce.getNode(j));
    		    if (ce.getNode(j) == n1)
    			break;
    		}
    		if (j == ce.getPostCount())
    		    continue;
    		if (ce.hasGroundConnection(j) && findPath(0, depth)) {
    		    //System.out.println(ce + " has ground");
    		    used[n1] = false;
    		    return true;
    		}
    		if (type == INDUCT && ce instanceof InductorElm) {
    		    double c = ce.getCurrent();
    		    if (j == 0)
    			c = -c;
    		    //System.out.println("matching " + c + " to " + firstElm.getCurrent());
    		    //System.out.println(ce + " " + firstElm);
    		    if (Math.abs(c-firstElm.getCurrent()) > 1e-10)
    			continue;
    		}
    		int k;
    		for (k = 0; k != ce.getPostCount(); k++) {
    		    if (j == k)
    			continue;
    		    //System.out.println(ce + " " + ce.getNode(j) + "-" + ce.getNode(k));
    		    if (ce.getConnection(j, k) && findPath(ce.getNode(k), depth)) {
    			//System.out.println("got findpath " + n1);
    			used[n1] = false;
    			return true;
    		    }
    		    //System.out.println("back on findpath " + n1);
    		}
    	    }
    	    used[n1] = false;
    	    //System.out.println(n1 + " failed");
    	    return false;
    	}
        }*/
    
    public CircuitNode getCircuitNode(int n) {
    	if (n >= sim.nodeList.size())
    	    return null;
    	return (CircuitNode) sim.nodeList.elementAt(n);
        }
    public CircuitElm getElm(int n) {
    	if (n >= sim.elmList.size())
    	    return null;
    	return (CircuitElm) sim.elmList.elementAt(n);
        }
    
    // factors a matrix into upper and lower triangular matrices by
    // gaussian elimination.  On entry, a[0..n-1][0..n-1] is the
    // matrix to be factored.  ipvt[] returns an integer vector of pivot
    // indices, used in the lu_solve() routine.
    boolean lu_factor(double a[][], int n, int ipvt[]) {
	double scaleFactors[];
	int i,j,k;

	scaleFactors = new double[n];
	
        // divide each row by its largest element, keeping track of the
	// scaling factors
	for (i = 0; i != n; i++) { 
	    double largest = 0;
	    for (j = 0; j != n; j++) {
		double x = Math.abs(a[i][j]);
		if (x > largest)
		    largest = x;
	    }
	    // if all zeros, it's a singular matrix
	    if (largest == 0)
		return false;
	    scaleFactors[i] = 1.0/largest;
	}
	
        // use Crout's method; loop through the columns
	for (j = 0; j != n; j++) {
	    
	    // calculate upper triangular elements for this column
	    for (i = 0; i != j; i++) {
		double q = a[i][j];
		for (k = 0; k != i; k++)
		    q -= a[i][k]*a[k][j];
		a[i][j] = q;
	    }

	    // calculate lower triangular elements for this column
	    double largest = 0;
	    int largestRow = -1;
	    for (i = j; i != n; i++) {
		double q = a[i][j];
		for (k = 0; k != j; k++)
		    q -= a[i][k]*a[k][j];
		a[i][j] = q;
		double x = Math.abs(q);
		if (x >= largest) {
		    largest = x;
		    largestRow = i;
		}
	    }
	    
	    // pivoting
	    if (j != largestRow) {
		double x;
		for (k = 0; k != n; k++) {
		    x = a[largestRow][k];
		    a[largestRow][k] = a[j][k];
		    a[j][k] = x;
		}
		scaleFactors[largestRow] = scaleFactors[j];
	    }

	    // keep track of row interchanges
	    ipvt[j] = largestRow;

	    // avoid zeros
	    if (a[j][j] == 0.0) {
		System.out.println("avoided zero");
		a[j][j]=1e-18;
	    }

	    if (j != n-1) {
		double mult = 1.0/a[j][j];
		for (i = j+1; i != n; i++)
		    a[i][j] *= mult;
	    }
	}
	return true;
    }


    // Solves the set of n linear equations using a LU factorization
    // previously performed by lu_factor.  On input, b[0..n-1] is the right
    // hand side of the equations, and on output, contains the solution.
    void lu_solve(double a[][], int n, int ipvt[], double b[]) {
	int i;

	// find first nonzero b element
	for (i = 0; i != n; i++) {
	    int row = ipvt[i];

	    double swap = b[row];
	    b[row] = b[i];
	    b[i] = swap;
	    if (swap != 0)
		break;
	}
	
	int bi = i++;
	for (; i < n; i++) {
	    int row = ipvt[i];
	    int j;
	    double tot = b[row];
	    
	    b[row] = b[i];
	    // forward substitution using the lower triangular matrix
	    for (j = bi; j < i; j++)
		tot -= a[i][j]*b[j];
	    b[i] = tot;
	}
	for (i = n-1; i >= 0; i--) {
	    double tot = b[i];
	    
	    // back-substitution using the upper triangular matrix
	    int j;
	    for (j = i+1; j != n; j++)
		tot -= a[i][j]*b[j];
	    b[i] = tot/a[i][i];
	}
    }
}
