package circuitArduino;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import circuitArduino.components.labels_instruments.TextElm;
import circuitArduino.components.passive.SwitchElm;
import circuitArduino.CirSim;
import circuitArduino.CircuitElm;
import circuitArduino.CircuitNode;

public class UItools {
	CirSim sim;
	public UItools(CirSim s) {
		sim=s;
	}

	public void plotGrid(Graphics g)
	{
		g.setColor(Color.yellow);
		int ds = sim.gridSize;
		int x0, y0;

		for (x0 = 0; x0 < 100; x0 += ds) {
			for (y0 = 0; y0 < 100; y0 += ds) {
				g.fillRect(x0-1, y0-1, 4, 4);
			}
		}
	}
	public int snapGrid(int x,boolean coord) { // coord== true means x, false means y
		if (coord)
			return((int)Math.round((float)(x-sim.offsetX)/(float)sim.gridSize)*sim.gridSize+sim.offsetX);//)*gridSize);
		else
			return((int)Math.round((float)(x-sim.offsetY)/(float)sim.gridSize)*sim.gridSize+sim.offsetY);//)*gridSize);

		//return (x+gridRound) & gridMask;
	}
	
	public void dragAll(int x, int y) {
		int dx = x-sim.dragX;
		int dy = y-sim.dragY;
		//System.out.println("DRAGGEDDDDDDDD");
		if (dx == 0 && dy == 0)
			return;
		int i;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = sim.getElm(i);
			ce.move(dx, dy);
		}
		sim.removeZeroLengthElements();
	}
	public void dragRow(int x, int y) {
		int dy = y-sim.dragY;
		if (dy == 0)
			return;
		int i;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.y  == sim.dragY)
				ce.movePoint(0, 0, dy);
			if (ce.y2 == sim.dragY)
				ce.movePoint(1, 0, dy);
		}
		removeZeroLengthElements();
	}

	public void dragColumn(int x, int y) {
		int dx = x-sim.dragX;
		if (dx == 0)
			return;
		int i;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.x  == sim.dragX)
				ce.movePoint(0, dx, 0);
			if (ce.x2 == sim.dragX)
				ce.movePoint(1, dx, 0);
		}
		removeZeroLengthElements();
	}

	public boolean dragSelected(int x, int y) {
		boolean me = false;
		int refx=-10000, refx2=-10000, refx3=-10000, refy=-10000, refy2=-10000, refy3=-10000;
		if (sim.mouseElm != null && !sim.mouseElm.isSelected())
		{ sim.mouseElm.setSelected(me = true);
		}

		// snap grid, unless we're only dragging text elements
		int i;
		int j;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected() && !(ce instanceof TextElm))
				break;
		}
		if (i != sim.elmList.size()) {
			x = snapGrid(x,true);
			y = snapGrid(y,false);
		}

		int dx = x-sim.dragX;
		int dy = y-sim.dragY;
		if (dx == 0 && dy == 0) {
			// don't leave mouseElm selected if we selected it above
			if (me)
			{
				sim.mouseElm.setSelected(false);
				sim.updateMatlabSelection(false);
			}
			return false;
		}
		boolean allowed = true;

		// check if moves are allowed
		for (i = 0; allowed && i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected() && !ce.allowMove(dx, dy))
				allowed = false;
		}

		if (allowed) {
			for (i = 0; i != sim.elmList.size(); i++) {
				CircuitElm ce = getElm(i);
				if (ce.isSelected()){
					refx = ce.x; refy = ce.y; refx2 = ce.x2; refy2 = ce.y2;
					if (ce.getPostCount()==3)
					{refx = ce.getPost(0).x; refy = ce.getPost(0).y; 
					refx2 = ce.getPost(1).x; refy2 = ce.getPost(1).y;
					refx3 = ce.getPost(2).x; refy3 = ce.getPost(2).y;}
					ce.move(dx, dy);
					if ((sim.buttonPressed==1)&&(sim.checkBoxDragElem==0)&(sim.doubleclick==0)) {
						for (j = 0; j != sim.checkElem.size(); j++) {

							CircuitElm ce2 = sim.checkElem.elementAt(j);
							if (ce2.getPostCount()<3){
								if (!ce2.isSelected()){
									if (ce.getPostCount()<3){
										if ((ce2.x==refx && ce2.y==refy)||(ce2.x==refx2 && ce2.y==refy2))
										{ce2.movePoint(0, dx, dy);}
										else {if ((ce2.x2==refx && ce2.y2==refy)||(ce2.x2==refx2 && ce2.y2==refy2))
											ce2.movePoint(1, dx, dy);}}
									else if (ce.getPostCount()==3){
										if ((ce2.x==refx && ce2.y==refy)||(ce2.x==refx2 && ce2.y==refy2)||(ce2.x==refx3 && ce2.y==refy3))
										{ce2.movePoint(0, dx, dy);}
										else {if ((ce2.x2==refx && ce2.y2==refy)||(ce2.x2==refx2 && ce2.y2==refy2)||(ce2.x2==refx3 && ce2.y2==refy3))
											ce2.movePoint(1, dx, dy);}
									}
								}
							}
						}
					}
				}
			}

			sim.needAnalyze();
		}

		// don't leave mouseElm selected if we selected it above
		if (me)
		{
			sim.mouseElm.setSelected(false);
			sim.updateMatlabSelection(false);
		}
		return allowed;
	}


	public void dragPost(int x, int y) {
		CircuitNode cn;
		int i;
		int xref=-10000, yref=-10000, xref2=-10000, yref2=-10000;//, xref3=-10000, yref3=-10000;

		if (sim.draggingPost == -1) {
			sim.draggingPost =
					(distanceSq(sim.mouseElm.x , sim.mouseElm.y , x, y) >
					distanceSq(sim.mouseElm.x2, sim.mouseElm.y2, x, y)) ? 1 : 0;
		}
		if (sim.mouseElm.getPostCount()<3)
			if (sim.draggingPost==1){
				xref=sim.mouseElm.x2; yref=sim.mouseElm.y2;}
			else{
				xref=sim.mouseElm.x; yref=sim.mouseElm.y;}
		for (i = 0; i != sim.checkElem.size(); i++) {
			CircuitElm ce = sim.checkElem.elementAt(i);
			if (ce != sim.mouseElm) {
				if  (((ce.x==xref && ce.y==yref)|(ce.x2==xref && ce.y2==yref))&&(ce.getPostCount()>=3)){
					return;
				}
			}
		}
		/*else {
		xref=mouseElm.getPost(0).x;yref=mouseElm.getPost(0).y;
		xref2=mouseElm.getPost(1).x;yref2=mouseElm.getPost(1).y;
		xref3=mouseElm.getPost(2).x;yref3=mouseElm.getPost(2).y;
	}*/
		int dx = x-sim.dragX;
		int dy = y-sim.dragY;
		if (dx == 0 && dy == 0)
			return;
		sim.mouseElm.movePoint(sim.draggingPost, dx, dy);

		for (i = 0; i != sim.checkElem.size(); i++) {
			CircuitElm ce = sim.checkElem.elementAt(i);
			if (ce != sim.mouseElm) {
				if (sim.mouseElm.getPostCount()<3){
					if  (ce.x==xref && ce.y==yref){
						ce.movePoint(0, dx, dy);}
					else if (ce.x2==xref && ce.y2==yref){
						ce.movePoint(1, dx, dy);}}
				/*	else if (mouseElm.getPostCount()==3){
				if  (ce.x==xref && ce.y==yref)
					ce.movePoint(0, mouseElm.getPost(0).x-xref, mouseElm.getPost(0).y-yref);
				else if (ce.x==xref2 && ce.y==yref2)
					ce.movePoint(0, mouseElm.getPost(1).x-xref2, mouseElm.getPost(1).y-yref2);
				else if (ce.x==xref3 && ce.y==yref3)
					ce.movePoint(0, mouseElm.getPost(2).x-xref3, mouseElm.getPost(2).y-yref3);
				else if (ce.x2==xref && ce.y2==yref)
					ce.movePoint(1, mouseElm.getPost(0).x-xref, mouseElm.getPost(0).y-yref);
				else if (ce.x2==xref2 && ce.y2==yref2)
					ce.movePoint(1, mouseElm.getPost(1).x-xref2, mouseElm.getPost(1).y-yref2);
				else if (ce.x2==xref3 && ce.y2==yref3)
					ce.movePoint(1, mouseElm.getPost(2).x-xref3, mouseElm.getPost(2).y-yref3);
			}*/
			}
		}
		sim.needAnalyze();
	}
	
	
	public void resetOffset()
	{int i;
	for (i = 0; i != sim.elmList.size(); i++) {

		CircuitElm ce = getElm(i);

		ce.movePoint(0, -sim.offsetX, -sim.offsetY);
		ce.movePoint(1, -sim.offsetX, -sim.offsetY);
	}
	sim.offsetX=0;sim.offsetY=0;
	}

	public void scaleCircuit(int centreX, int centreY, int gS, int oldGS)
	{int i, dx,dy;
	sim.offsetX= centreX -(int)Math.floor((float)centreX/(float)sim.gridSize)*sim.gridSize;
	sim.offsetY=centreY-(int)Math.floor((float)centreY/(float)sim.gridSize)*sim.gridSize;
	for (i = 0; i != sim.elmList.size(); i++) {

		CircuitElm ce = getElm(i);

		//nx = (ce.x-xMouse)/oldGridSize; ny=(ce.y-yMouse)/oldGridSize;
		//nx = (ce.x-newMousex)/oldGridSize; ny=(ce.y-newMousey)/oldGridSize;
		dx= centreX+(int)Math.round((float)(ce.x-centreX)/(float)oldGS)*gS-ce.x;
		dy= centreY+(int)Math.round((float)(ce.y-centreY)/(float)oldGS)*gS-ce.y;
		//dx= newMousex+nx*gridSize-ce.x;dy= newMousey+ny*gridSize-ce.y;
		//MOVE FIRST POINT BUT DONT CALL SETPOINTS METHOD:
		
		ce.x += dx; ce.y += dy;
		//ce.movePoint(0, dx, dy);
		//nx = (ce.x2-xMouse)/oldGridSize; ny=(ce.y2-yMouse)/oldGridSize;
		//dx= centreX+nx*gridSize-ce.x2;dy= newMousey+ny*gridSize-ce.y2;
		dx= centreX+(int)Math.round((float)(ce.x2-centreX)/(float)oldGS)*gS-ce.x2;
		dy= centreY+(int)Math.round((float)(ce.y2-centreY)/(float)oldGS)*gS-ce.y2;

		ce.movePoint(1, dx, dy);
		//System.out.println("dx: "+(ce.x-ce.x2) );
		//System.out.println("dx: "+(ce.x-ce.x2)/gS );
		//ce.move(dx, dy);

	}}
	
	public void removeZeroLengthElements() {
		int i;
		boolean changed = false;
		for (i = sim.elmList.size()-1; i >= 0; i--) {
			CircuitElm ce = getElm(i);
			if (ce.x == ce.x2 && ce.y == ce.y2) {
				sim.elmList.removeElementAt(i);
				ce.delete();
				changed = true;
			}
		}
		sim.needAnalyze();
	}


	public boolean checkAnySelected(){
		int i;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected())
				return true;
		}
		return false;
	}
	
	public int checkHowManySelected(){
		int i, count=0;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected())
				count++;
		}
		return count;
	}
	
	public void resetSelection()
	{
		int i;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected())
				if (ce!=sim.mouseElm)
					ce.setSelected(false);
		}
	}
	
	public int selectedCount()
	{
		int i,count=0;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected())
				count++;
		}
		return count;
	}
	
	public void selectArea(int x, int y) {
		int x1 = min(x, sim.initDragX);
		int x2 = max(x, sim.initDragX);
		int y1 = min(y, sim.initDragY);
		int y2 = max(y, sim.initDragY);
		sim.selectedArea = new Rectangle(x1, y1, x2-x1, y2-y1);
		int i;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			ce.selectRect(sim.selectedArea);
		}
	}

	void setSelectedElm(CircuitElm cs) {
		int i;
		for (i = 0; i != sim.elmList.size(); i++) {
			CircuitElm ce = getElm(i);
			ce.setSelected(ce == cs);
		}
		sim.mouseElm = cs;
	}
	public void toggleSwitch(int n) {
		int i;
		for (i = 0; i != sim.elmList.size(); i++) {
		    CircuitElm ce = getElm(i);
		    if (ce instanceof SwitchElm) {
			n--;
			if (n == 0) {
			    ((SwitchElm) ce).toggle();
			    sim.analyzeFlag = true;
			    sim.cv.repaint();
			    return;
			}
		    }
		}
	    }
	public CircuitElm getElm(int n) {
		if (n >= sim.elmList.size())
			return null;
		return (CircuitElm) sim.elmList.elementAt(n);
	}
	
	int distanceSq(int x1, int y1, int x2, int y2) {
		x2 -= x1;
		y2 -= y1;
		return x2*x2+y2*y2;
	}
	
	int min(int a, int b) { return (a < b) ? a : b; }
	int max(int a, int b) { return (a > b) ? a : b; }
}
