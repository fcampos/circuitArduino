package circuitArduino;
import java.awt.*;

import circuitArduino.CirSim;

class CircuitLayout implements LayoutManager {
	CirSim sim;
    public CircuitLayout(CirSim s) {sim = s;}
    public void addLayoutComponent(String name, Component c) {}
    public void removeLayoutComponent(Component c) {}
    public Dimension preferredLayoutSize(Container target) {
	return new Dimension(500, 500);
    }
    public Dimension minimumLayoutSize(Container target) {
	return new Dimension(100,100);
    }
    public void layoutContainer(Container target) {
	Insets insets = target.insets();
	int cw;
	//int menuHeight = 20;
	//Dimension d = target.getComponent(1).getPreferredSize();
	int targetw = target.size().width - insets.left - insets.right;
	if (!sim.usePanel)
	 cw = targetw* 8/10;
	else
		cw = targetw;
	int targeth = target.size().height - (insets.top+insets.bottom);
	target.getComponent(0).move(insets.left, insets.top);//+d.height);
	target.getComponent(0).resize(cw, targeth);
	
	//target.getComponent(1).move(insets.left, insets.top);
	//target.getComponent(1).resize(cw, d.height);
	
	int barwidth = targetw - cw;
	cw += insets.left;
	int i;
	int h = insets.top-3;
	for (i = 1; i < target.getComponentCount(); i++) {
	    Component m = target.getComponent(i);
	    if (m.isVisible()) {
	    	Dimension		d = m.getPreferredSize();
		if (m instanceof Scrollbar)
		    d.width = barwidth;
		if (m instanceof Choice && d.width > barwidth)
		    d.width = barwidth;
		if (m instanceof Label) {
		    h += d.height/5;
		    d.width = barwidth;
		}
		m.move(cw, h);
		m.resize(d.width, d.height);
		h += d.height;
	    }
	}
    }
};
