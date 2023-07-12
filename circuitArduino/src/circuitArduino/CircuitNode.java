package circuitArduino;
import java.util.Vector;

import circuitArduino.CircuitNodeLink;

public class CircuitNode {
    int x, y;
    Vector<CircuitNodeLink> links;
    boolean internal;
    CircuitNode() { links = new Vector(); }
}
