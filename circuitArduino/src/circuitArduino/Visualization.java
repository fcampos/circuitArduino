package circuitArduino;

import circuitArduino.components.passive.CapacitorElm;
import circuitArduino.components.passive.InductorElm;
import circuitArduino.components.passive.ResistorElm;
import circuitArduino.CirSim;
import circuitArduino.CircuitElm;

public class Visualization {
	 static final int HINT_LC = 1;
	    static final int HINT_RC = 2;
	    static final int HINT_3DB_C = 3;
	    static final int HINT_TWINT = 4;
	    static final int HINT_3DB_L = 5;
	    static final double pi = 3.14159265358979323846;
CirSim sim;
	Visualization(CirSim s){
		sim= s;
	}
	
	String getHint(int hintItem1, int hintItem2, int hintType) {
		CircuitElm c1 = sim.getElm(hintItem1);
		CircuitElm c2 = sim.getElm(hintItem2);
		if (c1 == null || c2 == null)
		    return null;
		if (hintType == HINT_LC) {
		    if (!(c1 instanceof InductorElm))
			return null;
		    if (!(c2 instanceof CapacitorElm))
			return null;
		    InductorElm ie = (InductorElm) c1;
		    CapacitorElm ce = (CapacitorElm) c2;
		    return "res.f = " + CircuitElm.getUnitText(1/(2*pi*Math.sqrt(ie.inductance*
							    ce.capacitance)), "Hz");
		}
		if (hintType == HINT_RC) {
		    if (!(c1 instanceof ResistorElm))
			return null;
		    if (!(c2 instanceof CapacitorElm))
			return null;
		    ResistorElm re = (ResistorElm) c1;
		    CapacitorElm ce = (CapacitorElm) c2;
		    return "RC = " + CircuitElm.getUnitText(re.resistance*ce.capacitance,
						 "s");
		}
		if (hintType == HINT_3DB_C) {
		    if (!(c1 instanceof ResistorElm))
			return null;
		    if (!(c2 instanceof CapacitorElm))
			return null;
		    ResistorElm re = (ResistorElm) c1;
		    CapacitorElm ce = (CapacitorElm) c2;
		    return "f.3db = " +
			CircuitElm.getUnitText(1/(2*pi*re.resistance*ce.capacitance), "Hz");
		}
		if (hintType == HINT_3DB_L) {
		    if (!(c1 instanceof ResistorElm))
			return null;
		    if (!(c2 instanceof InductorElm))
			return null;
		    ResistorElm re = (ResistorElm) c1;
		    InductorElm ie = (InductorElm) c2;
		    return "f.3db = " +
			CircuitElm.getUnitText(re.resistance/(2*pi*ie.inductance), "Hz");
		}
		if (hintType == HINT_TWINT) {
		    if (!(c1 instanceof ResistorElm))
			return null;
		    if (!(c2 instanceof CapacitorElm))
			return null;
		    ResistorElm re = (ResistorElm) c1;
		    CapacitorElm ce = (CapacitorElm) c2;
		    return "fc = " +
			CircuitElm.getUnitText(1/(2*pi*re.resistance*ce.capacitance), "Hz");
		}
		return null;
	    }
}
