package circuitArduino;
import java.awt.Frame;
import java.awt.event.WindowEvent;  
import java.awt.event.WindowListener;  
//import javax.swing.JFrame;

import circuitArduino.CirSim;
import circuitArduino.Circuit;


public class CirSimWrapper extends Frame  {
public static CirSim cirsim;


Circuit applet;

CirSimWrapper() {
	
	super("frame");
	System.setProperty("sun.java2d.opengl", "True");
	this.addWindowListener(new WindowListener ()
	{
	    public void windowClosing(WindowEvent we)
	    {
	    	System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Exiting Circuit simulator~~~~~~~~~~~~~~~~");
	        System.exit(0);
	    }

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
	});
	//this.setDefaultCloseOperation(Frame.EXIT_ON_CLOSE);
	//System.out.println("constructing wrapper");
	cirsim = new CirSim(this);
	cirsim.init();

}
}
