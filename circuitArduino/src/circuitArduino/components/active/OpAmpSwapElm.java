package circuitArduino.components.active;

public class OpAmpSwapElm extends OpAmpElm {
	public OpAmpSwapElm(int xx, int yy) {
	    super(xx, yy);
	    flags |= FLAG_SWAP;
	}
	protected Class getDumpClass() { return OpAmpElm.class; }
    }
