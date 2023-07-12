package circuitArduino.components.active;

public class PTransistorElm extends TransistorElm {
	public PTransistorElm(int xx, int yy) { super(xx, yy, true); }
	protected Class getDumpClass() { return TransistorElm.class; }
    }
