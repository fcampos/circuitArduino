package circuitArduino.components.active;

public class NTransistorElm extends TransistorElm {
	public NTransistorElm(int xx, int yy) { super(xx, yy, false); }
	protected Class getDumpClass() { return TransistorElm.class; }
    }
