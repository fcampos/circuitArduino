package circuitArduino;
import java.awt.*;

import circuitArduino.CirSim;

public class CircuitCanvas extends Canvas {
    CirSim pg;
    CircuitCanvas(CirSim p) {
	pg = p;
    }
    public Dimension getPreferredSize() {
	return new Dimension(300,400);
    }
    public void update(Graphics g) {
	pg.updateCircuit(g, false);
    }
    public void paint(Graphics g) {
	pg.updateCircuit(g,false);
    }
};
