package circuitArduino.components.active;

public class NJfetElm extends JfetElm {
	public NJfetElm(int xx, int yy) { super(xx, yy, false); }
	protected Class getDumpClass() { return JfetElm.class; }
    }

    class PJfetElm extends JfetElm {
	public PJfetElm(int xx, int yy) { super(xx, yy, true); }
	protected Class getDumpClass() { return JfetElm.class; }
    }

