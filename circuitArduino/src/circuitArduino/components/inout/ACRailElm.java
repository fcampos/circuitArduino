package circuitArduino.components.inout;

public class ACRailElm extends RailElm {
	public ACRailElm(int xx, int yy) { super(xx, yy, WF_AC); }
	protected Class getDumpClass() { return RailElm.class; }
    }
