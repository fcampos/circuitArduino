
package circuitArduino.UI;

import java.awt.Checkbox;

import circuitArduino.CirSim;
import circuitArduino.Editable;

public class EditOptionsArduino implements Editable {
    CirSim sim;
    public EditOptionsArduino(CirSim s) { sim = s; }
    public EditInfo getEditInfo(int n) {
    	if (n == 0)
    		return new EditInfo("Ratio Arduino step / Simulation step", sim.periodRatioArduino , 1, 50).
    		    setDimensionless();
    	 if (n == 1) {
    			EditInfo ei = new EditInfo("", 0, -1, -1);
    			ei.checkbox = new Checkbox("Average PWM output", sim.arduino.pwmAverage );
    			return ei;
    		    }
    	/*if (n == 0) {
    		EditInfo ei = new EditInfo("File", 0, -1, -1);
    		
    		ei.text = sim.sketchURL;
    		ei.textf.setColumns(45);
    		return ei;
    	}*/
	return null;
    }
    public void setEditValue(int n, EditInfo ei) {
    	if (n == 0) {
    		sim.periodRatioArduino = (int) ei.value;}
    	if (n == 1) {
    		sim.arduino.pwmAverage = ei.checkbox.getState();}
    	//if (n == 0) {
    		//sim.sketchURL = ei.textf.getText();
		  //  split();
		}
 
};
