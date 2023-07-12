package circuitArduino;

import java.awt.Rectangle;
import java.awt.MouseInfo;
import java.awt.PointerInfo;

import circuitArduino.CirSim;
import circuitArduino.CircuitElm;

public class EditTools {
	CirSim sim;
	public EditTools(CirSim s)
	{
		sim = s;
	}
	public	void pushUndo() {
		sim.redoStack.removeAllElements();
		String s = sim.dumpCircuit();
		if (sim.undoStack.size() > 0 && s.compareTo((String) (sim.undoStack.lastElement())) == 0)
			return;
		sim.undoStack.add(s);
		enableUndoRedo();
	}

	public void doUndo() {
		if (sim.undoStack.size() == 0)
			return;
		sim.redoStack.add(sim.dumpCircuit());
		String s = (String) (sim.undoStack.remove(sim.undoStack.size()-1));
		sim.filetools.readSetup(s);
		enableUndoRedo();
	}

	public	void doRedo() {
		if (sim.redoStack.size() == 0)
			return;
		sim.undoStack.add(sim.dumpCircuit());
		String s = (String) (sim.redoStack.remove(sim.redoStack.size()-1));
		sim.filetools.readSetup(s);
		enableUndoRedo();
	}

	public	void enableUndoRedo() {
		// 	System.out.println(redoItem);
		//  	System.out.println(sim.redoStack.size());
		//		redoItem.setEnabled(sim.redoStack.size() > 0);
		//		undoItem.setEnabled(sim.undoStack.size() > 0);
	}
	public	void doCut() {
		int i;
		pushUndo();
		sim.setMenuSelection();
		sim.clipboard = "";
		for (i = sim.elmList.size()-1; i >= 0; i--) {
			CircuitElm ce = sim.getElm(i);
			if (ce.isSelected()) {
				sim.clipboard += ce.p_dump() + "\n";
				ce.delete();
				sim.elmList.removeElementAt(i);
			}
		}
		enablePaste();
		sim.needAnalyze();
	}

	public	void doDelete() {
		int i;
		pushUndo();
		sim.setMenuSelection();
		for (i = sim.elmList.size()-1; i >= 0; i--) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected()) {
				ce.delete();
				sim.elmList.removeElementAt(i);
			}
		}
		if (sim.usePanel);
		/*
	    	try {
	    	//	proxy.feval("pg_delete_Falstad_PG_Connections");
	    	} catch (MatlabInvocationException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	}*/
		sim.needAnalyze();
	}

	public void doCopy() {
		int i;
		sim.clipboard = "";
		sim.setMenuSelection();
		for (i = sim.elmList.size()-1; i >= 0; i--) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected())
				sim.clipboard += ce.p_dump() + "\n";
		}
		enablePaste();
	}

	public void enablePaste() {
		sim.pasteItem.setEnabled(sim.clipboard.length() > 0);
	}

	public void doPaste() {
		pushUndo();
		sim.clearSelection();
		int i;
		Rectangle oldbb = null;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			Rectangle bb = ce.p_getBoundingBox();
			if (oldbb != null)
				oldbb = oldbb.union(bb);
			else
				oldbb = bb;
		}
		int oldsz = sim.elmList.size();
		sim.filetools.readSetup(sim.clipboard, true);

		// select new items
		Rectangle newbb = null;
		for (i = oldsz; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			ce.setSelected(true);
			Rectangle bb = ce.p_getBoundingBox();
			if (newbb != null)
				newbb = newbb.union(bb);
			else
				newbb = bb;
		}
		if (oldbb != null && newbb != null && oldbb.intersects(newbb)) {
			// find a place for new items
			int dx = 0, dy = 0;
			int spacew = sim.circuitArea.width - oldbb.width - newbb.width;
			int spaceh = sim.circuitArea.height - oldbb.height - newbb.height;
			if (spacew > spaceh)
				dx = sim.uitools.snapGrid(oldbb.x + oldbb.width  - newbb.x + sim.gridSize,true);
			else
				dy = sim.uitools.snapGrid(oldbb.y + oldbb.height - newbb.y + sim.gridSize,false);
			
			// move new items near the mouse if possible
			PointerInfo a = MouseInfo.getPointerInfo();
			int mouseCursorX = a.getLocation().x-sim.cv.getLocationOnScreen().x;
			int mouseCursorY = a.getLocation().y-sim.cv.getLocationOnScreen().y;
			//int x = (int) b.getX();
			//int y = (int) b.getY();
			System.out.println("mouse: "+mouseCursorX +" "+ mouseCursorY);
    		if (mouseCursorX > 0 && sim.circuitArea.contains(mouseCursorX, mouseCursorY)) {
    	    	   // int gx = inverseTransformX(mouseCursorX);
    	    	   // int gy = inverseTransformY(mouseCursorY);
    	    	    int mdx = sim.uitools.snapGrid(mouseCursorX-(newbb.x+newbb.width/2),true);
    	    	    int mdy = sim.uitools.snapGrid(mouseCursorY-(newbb.y+newbb.height/2),false);
    	    	    for (i = oldsz; i != sim.elmList.size(); i++) {
    	    		if (!getElm(i).allowMove(mdx, mdy))
    	    		    break;
    	    	    }
    	    	    if (i == sim.elmList.size()) {
    	    		dx = mdx;
    	    		dy = mdy;
    	    	    }
    		}
			
			
			for (i = oldsz; i != sim.elmList.size(); i++) {
				CircuitElm ce = getElm(i);
				ce.move(dx, dy);
			}
			// center circuit
			sim.handleResize();
		}
		sim.needAnalyze();
	}
	
	 CircuitElm getElm(int n) {
		if (n >= sim.elmList.size())
			return null;
		return (CircuitElm) sim.elmList.elementAt(n);
	}
}
