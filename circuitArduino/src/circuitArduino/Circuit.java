package circuitArduino;
// Circuit.java (c) 2005,2008 by Paul Falstad, www.falstad.com

import java.io.InputStream;
import java.awt.*;
import java.awt.image.*;
import java.applet.Applet;
import java.util.Vector;

import circuitArduino.CirSimWrapper;

import java.io.File;
import java.util.Random;
import java.util.Arrays;
import java.lang.Math;
import java.net.URL;
import java.awt.event.*;
import java.io.FilterInputStream;
import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;



public class Circuit extends Applet {//implements ComponentListener {
	public static String startFolder= new String();
    public static CirSimWrapper ogf;
  /*  void destroyFrame() {
	if (ogf != null)
	    ogf.dispose();
	ogf = null;
	repaint();
    }*/
    boolean started = false;
    public void init() {
	//addComponentListener(this);
    }

    public static void main(String args[]) {
    	if (args.length>0){
    		startFolder = args[0];}
    	else{
    	//	System.out.println("Working Directory = " +
    	 //             System.getProperty("user.dir"));
    		//System.out.println(args[0]);
    	}
    	System.setProperty("sun.java2d.opengl", "True");
    	
	ogf = new CirSimWrapper();//new CirSim(null);
	//ogf.setSize(250, 250);
	ogf.setVisible(true);
	//ogf.init();
    }
    /*
    void showFrame() {
	if (ogf == null) {
	    started = true;
	    ogf = new CirSimWrapper();;
	    //ogf.init();
	    System.out.println("cirsim initiated");
	    repaint();
	}
    }

    public void toggleSwitch(int x) { ogf.toggleSwitch(x); }
    
    public void paint(Graphics g) {
	String s = "Applet is open in a separate window.";
	if (!started)
	    s = "Applet is starting.";
	else if (ogf == null)
	    s = "Applet is finished.";
	else if (ogf.useFrame)
	    ogf.triggerShow();
	g.drawString(s, 10, 30);
	System.out.println("Circuit app painted");
    }
    
    public void componentHidden(ComponentEvent e){}
    public void componentMoved(ComponentEvent e){}
    public void componentShown(ComponentEvent e) { showFrame(); }
    public void componentResized(ComponentEvent e) {
	if (ogf != null)
	    ogf.componentResized(e);
	System.out.println("Circuit app resized");
	   
    }
    
    public void destroy() {
	if (ogf != null)
	    ogf.dispose();
	ogf = null;
	repaint();
	System.out.println("destroyed");
	    }*/
};

