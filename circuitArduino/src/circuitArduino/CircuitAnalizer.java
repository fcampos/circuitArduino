package circuitArduino;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.interfaces.linsol.LinearSolverSparse;
import org.ejml.sparse.FillReducing;
import org.ejml.sparse.csc.factory.LinearSolverFactory_DSCC;

import circuitArduino.components.inout.CurrentElm;
import circuitArduino.components.inout.GroundElm;
import circuitArduino.components.inout.RailElm;
import circuitArduino.components.inout.VoltageElm;
import circuitArduino.components.passive.CapacitorElm;
import circuitArduino.components.passive.InductorElm;
import circuitArduino.components.passive.SpeakerElm;
import circuitArduino.CirSim;
import circuitArduino.CircuitElm;
import circuitArduino.CircuitNode;
import circuitArduino.CircuitNodeLink;
import circuitArduino.RowInfo;
import circuitArduino.WireElm;

public class CircuitAnalizer {
	public CirSim sim;
	public CircuitAnalizer(CirSim s) {
		sim=s;
	}
	public void analyzeCircuit() {
		calcCircuitBottom();
		if (sim.elmList.isEmpty())
			return;
		sim.stopMessage = null;
		sim.stopElm = null;
		int i, j;
		int vscount = 0;
		sim.nodeList = new Vector();
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
			sim.nodeList.addElement(cn);
		} else {
			// otherwise allocate extra node for ground
			CircuitNode cn = new CircuitNode();
			cn.x = cn.y = -1;
			sim.nodeList.addElement(cn);
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
				for (k = 0; k != sim.nodeList.size(); k++) {
					CircuitNode cn = sim.getCircuitNode(k);
					if (pt.x == cn.x && pt.y == cn.y)
						break;
				}
				if (k == sim.nodeList.size()) {
					CircuitNode cn = new CircuitNode();
					cn.x = pt.x;
					cn.y = pt.y;
					CircuitNodeLink cnl = new CircuitNodeLink();
					cnl.num = j;
					cnl.elm = ce;
					cn.links.addElement(cnl);
					ce.setNode(j, sim.nodeList.size());
					sim.nodeList.addElement(cn);
				} else {
					CircuitNodeLink cnl = new CircuitNodeLink();
					cnl.num = j;
					cnl.elm = ce;
					sim.getCircuitNode(k).links.addElement(cnl);
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
				ce.setNode(cnl.num, sim.nodeList.size());
				sim.nodeList.addElement(cn);
			}
			vscount += ivs;
		}
		sim.voltageSources = new CircuitElm[vscount];
		vscount = 0;
		sim.circuitNonLinear = false;
		//System.out.println("ac3");

		// determine if circuit is nonlinear
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.nonLinear())
				sim.circuitNonLinear = true;
			int ivs = ce.getVoltageSourceCount();
			for (j = 0; j != ivs; j++) {
				sim.voltageSources[vscount] = ce;
				ce.setVoltageSource(j, vscount++);
			}
		}
		sim.voltageSourceCount = vscount;

		int matrixSize = sim.nodeList.size()-1 + vscount;
		sim.circuitMatrix = new DMatrixSparseCSC(matrixSize,matrixSize);//new double[matrixSize][matrixSize];
		// System.out.println("is full0?" + circuitMatrix.isFull());
		sim.circuitRightSide = new DMatrixRMaj(matrixSize,1);//new double[matrixSize];
		sim.origMatrix = new DMatrixSparseCSC(matrixSize,matrixSize);//new double[matrixSize][matrixSize];
		sim.origRightSide = new DMatrixRMaj(matrixSize,1);//new double[matrixSize];
		sim.circuitMatrixSize = sim.circuitMatrixFullSize = matrixSize;
		sim.circuitRowInfo = new RowInfo[matrixSize];
		sim.circuitPermute = new int[matrixSize];
		//System.out.println("matrix size " + matrixSize);
		//System.out.println("circuitRightSide size " + circuitRightSide.getNumElements());
		//System.out.println("inBounds 5 1 " + circuitRightSide.isInBounds(5,1));
		//System.out.println("inBounds 6 1 " + circuitRightSide.isInBounds(6,1));
		//System.out.println("inBounds 5 1 " + circuitRightSide.get(0,0));
		//System.out.println("inBounds 5 1 " + circuitRightSide.get(1,1));
		//System.out.println("inBounds 6 1 " + circuitRightSide.get(6,1));
		int vs = 0;
		for (i = 0; i != matrixSize; i++)
			sim.circuitRowInfo[i] = new RowInfo();
		sim.circuitNeedsMap = false;

		// stamp linear circuit elements
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			ce.stamp();
		}
		//System.out.println("ac4");

		// determine nodes that are unconnected
		boolean closure[] = new boolean[sim.nodeList.size()];
		boolean tempclosure[] = new boolean[sim.nodeList.size()];
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
			for (i = 0; i != sim.nodeList.size(); i++)
				if (!closure[i] && !getCircuitNode(i).internal) {
					// System.out.println("node " + i + " unconnected");
					sim.algorithm.stampResistor(0, i, 1e8);
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
			RowInfo re = sim.circuitRowInfo[i];
			//   /*System.out.println("row " + i + " " + re.lsChanges + " " + re.rsChanges + " " +
			//		       re.dropRow);
			if (re.lsChanges || re.dropRow || re.rsChanges)
				continue;
			double rsadd = 0;

			// look for rows that can be removed
			for (j = 0; j != matrixSize; j++) {
				double q = sim.circuitMatrix.unsafe_get(i,j);//circuitMatrix[i][j];
				if (sim.circuitRowInfo[j].type == RowInfo.ROW_CONST) {
					// keep a running total of const values that have been
					// removed already
					rsadd -= sim.circuitRowInfo[j].value*q;
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
			//  /*if (qp != -1 && sim.circuitRowInfo[qp].lsChanges) {
			//	System.out.println("lschanges");
			//	continue;
			//   }
			//  if (qm != -1 && sim.circuitRowInfo[qm].lsChanges) {
			//	System.out.println("lschanges");
			//	continue;
			//	}
			if (j == matrixSize) {
				if (qp == -1) {
					sim.stop("Matrix error", null);
					return;
				}
				RowInfo elt = sim.circuitRowInfo[qp];
				if (qm == -1) {
					// we found a row with only one nonzero entry; that value
					// is a constant
					int k;
					for (k = 0; elt.type == RowInfo.ROW_EQUAL && k < 100; k++) {
						// follow the chain
						//	/*System.out.println("following equal chain from " +
						//			   i + " " + qp + " to " + elt.nodeEq);
						qp = elt.nodeEq;
						elt = sim.circuitRowInfo[qp];
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
					elt.value = (sim.circuitRightSide.get(i,0)+rsadd)/qv;//(circuitRightSide[i]+rsadd)/qv;
					sim.circuitRowInfo[i].dropRow = true;
					//System.out.println(qp + " * " + qv + " = const " + elt.value);
					i = -1; // start over from scratch
				} else if (sim.circuitRightSide.get(i,0)+rsadd == 0) {//(circuitRightSide[i]+rsadd == 0) {
					// we found a row with only two nonzero entries, and one
					// is the negative of the other; the values are equal
					if (elt.type != RowInfo.ROW_NORMAL) {
						//System.out.println("swapping");
						int qq = qm;
						qm = qp; qp = qq;
						elt = sim.circuitRowInfo[qp];
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
					sim.circuitRowInfo[i].dropRow = true;
					//System.out.println(qp + " = " + qm);
				}
			}
		}
		//System.out.println("ac7");

		// find size of new matrix
		int nn = 0;
		for (i = 0; i != matrixSize; i++) {
			RowInfo elt = sim.circuitRowInfo[i];
			if (elt.type == RowInfo.ROW_NORMAL) {
				elt.mapCol = nn++;
				//System.out.println("col " + i + " maps to " + elt.mapCol);
				continue;
			}
			if (elt.type == RowInfo.ROW_EQUAL) {
				RowInfo e2 = null;
				// resolve chains of equality; 100 max steps to avoid loops
				for (j = 0; j != 100; j++) {
					e2 = sim.circuitRowInfo[elt.nodeEq];
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
			RowInfo elt = sim.circuitRowInfo[i];
			if (elt.type == RowInfo.ROW_EQUAL) {
				RowInfo e2 = sim.circuitRowInfo[elt.nodeEq];
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

		///*System.out.println("matrixSize = " + matrixSize);

		//for (j = 0; j != circuitMatrixSize; j++) {
		//    System.out.println(j + ": ");
		//    for (i = 0; i != circuitMatrixSize; i++)
		//	System.out.print(circuitMatrix[j][i] + " ");
		//    System.out.print("  " + circuitRightSide[j] + "\n");
		//}
		//System.out.print("\n");


		// make the new, simplified matrix
		int newsize = nn;
		DMatrixSparseCSC newmatx = new DMatrixSparseCSC(newsize,newsize);
		//double newmatx[][] = new double[newsize][newsize];
		//double newrs  []   = new double[newsize];
		DMatrixRMaj newrs = new DMatrixRMaj(newsize,1);
		int ii = 0;
		for (i = 0; i != matrixSize; i++) {
			RowInfo rri = sim.circuitRowInfo[i];
			if (rri.dropRow) {
				rri.mapRow = -1;
				continue;
			}
			//   newrs[ii] = circuitRightSide.get(i,0);//circuitRightSide[i];
			newrs.set(ii,0,sim.circuitRightSide.get(i,0));
			rri.mapRow = ii;
			//System.out.println("Row " + i + " maps to " + ii);
			for (j = 0; j != matrixSize; j++) {
				RowInfo ri = sim.circuitRowInfo[j];
				if (ri.type == RowInfo.ROW_CONST)
					newrs.set(ii,0,newrs.unsafe_get(ii,0)-ri.value*sim.circuitMatrix.unsafe_get(i,j));//circuitMatrix[i][j];

				//newrs[ii] -= ri.value*circuitMatrix.unsafe_get(i,j);//circuitMatrix[i][j];
				else if (sim.circuitMatrix.isAssigned(i,j))
					newmatx.unsafe_set(ii,ri.mapCol, newmatx.unsafe_get(ii,ri.mapCol)+sim.circuitMatrix.unsafe_get(i,j));//circuitMatrix[i][j];
				//  newmatx[ii][ri.mapCol] += circuitMatrix.unsafe_get(i,j);//circuitMatrix[i][j];
			}
			ii++;
		}

		sim.circuitMatrix = newmatx;
		// System.out.println("is full2?" + circuitMatrix.isFull());
		sim.circuitRightSide = newrs;
		matrixSize = sim.circuitMatrixSize = newsize;
		//for (i = 0; i != matrixSize; i++)
		//    origRightSide[i] = circuitRightSide[i];
		sim.origRightSide = sim.circuitRightSide.copy();
		sim.origMatrix = sim.circuitMatrix.copy();
		/*for (i = 0; i != matrixSize; i++)
    for (j = 0; j != matrixSize; j++)
	origMatrix[i][j] = circuitMatrix[i][j];*/
		sim.circuitNeedsMap = true;

		//
		//System.out.println("matrixSize = " + matrixSize + " " + circuitNonLinear);
		//for (j = 0; j != circuitMatrixSize; j++) {
		//    for (i = 0; i != circuitMatrixSize; i++)
		//	System.out.print(circuitMatrix[j][i] + " ");
		//    System.out.print("  " + circuitRightSide[j] + "\n");
		//}
		//System.out.print("\n");

		// if a matrix is linear, we can do the lu_factor here instead of
		// needing to do it every frame
		LinearSolverSparse<DMatrixSparseCSC,DMatrixRMaj> solver = LinearSolverFactory_DSCC.lu(FillReducing.NONE);//       circuitMatrix.numRows);
		solver.setA(sim.circuitMatrix);
		if (false){//(!circuitNonLinear&&matrixSize>1) {
			if ( solver.quality() <= 1e-8 ){//(!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
				sim.stop("Singular matrix!", null);
				return;
			}
		}
		boolean needsArduino=false;
		//(i = 0; i != sim.elmList.size(); i++)
		for (CircuitElm ce :sim.elmList)
		{
			if (ce.needsArduino()){needsArduino=true;break; }
		}
		boolean needsSound=false;
		for (CircuitElm ce :sim.elmList)
		{
			if (ce instanceof SpeakerElm){
				((SpeakerElm) ce).setup();
				needsSound=true;
				sim.speedBar.setValue(sim.speedBar.getMaximum());
				if (sim.nextTimeStep==0) {sim.nextTimeStep = sim.timeStep;};

				break; }
		}
		if(!needsSound)
			sim.playThread.wform.elm=null;
		//System.out.println("Needs arduino: " +needsArduino);

		if (needsArduino&&!sim.arduino.exists()){sim.arduino.init();sim.needAnalyze();}
		else
			if(!needsArduino&&sim.arduino.exists())
			{sim.arduino.terminate();}
	}
	void calcCircuitBottom() {
		int i;
		sim.circuitBottom = 0;
		for (i = 0; i != sim.elmList.size(); i++) {
			Rectangle rect = getElm(i).boundingBox;
			int bottom = rect.height + rect.y;
			if (bottom > sim.circuitBottom)
				sim.circuitBottom = bottom;
		}
	}
	public CircuitElm getElm(int n) {
		if (n >= sim.elmList.size())
			return null;
		return (CircuitElm) sim.elmList.elementAt(n);
	}
	public CircuitNode getCircuitNode(int n) {
		if (n >= sim.nodeList.size())
			return null;
		return (CircuitNode) sim.nodeList.elementAt(n);
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
		    used = new boolean[sim.nodeList.size()];
		}
		public boolean findPath(int n1) { return findPath(n1, -1); }
		 public boolean findPath(int n1, int depth) {
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
	   }
}
