package circuitArduino;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Menu;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

import javax.print.PrintException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import circuitArduino.components.labels_instruments.Scope;
import circuitArduino.CirSim;
import circuitArduino.CircuitElm;
import circuitArduino.ImportDialog;

public class FileTools {
	CirSim sim;
public FileTools(CirSim s) {sim = s;}


void getSetupList(Menu menu, boolean retry) {
	Menu stack[] = new Menu[6];
	int stackptr = 0;
	stack[stackptr++] = menu;
	URL url;
	try {
		if (!sim.applet.startFolder.isEmpty()){
			System.out.println("file:///" + sim.applet.startFolder);
			//System.out.println(applet.startFolder);
			//System.out.println(applet.startFolder);
			  url = new URL("file:///" +  sim.applet.startFolder+ File.separator+"setuplist.txt");}
		else{
	  //  url = new URL(getCodeBase() + "setuplist.txt");
	  //  System.out.println(url.toString());
	    url = new URL("file:///" +System.getProperty("user.dir")+ File.separator+ "setuplist.txt");
	    System.out.println(System.getProperty("user.dir"));
	   System.out.println(url.toString());
	  //  System.out.println("Working Directory = " +
	   //           System.getProperty("user.dir"));
		};// System.out.println(getCodeBase() + "setuplist.txt");}
	    ByteArrayOutputStream ba = sim.readUrlData(url);
	    byte b[] = ba.toByteArray();
	    int len = ba.size();
	    int p;
	    if (len == 0 || b[0] != '#') {
		// got a redirect, try again
		getSetupList(menu, true);
		return;
	    }
	    for (p = 0; p < len; ) {
		int l;
		for (l = 0; l != len-p; l++)
		    if (b[l+p] == '\n') {
			l++;
			break;
		    }
		String line = new String(b, p, l-1);
		if (line.charAt(0) == '#')
		    ;
		else if (line.charAt(0) == '+') {
		    Menu n = new Menu(line.substring(1));
		    menu.add(n);
		    menu = stack[stackptr++] = n;
		} else if (line.charAt(0) == '-') {
		    menu = stack[--stackptr-1];
		} else {
		    int i = line.indexOf(' ');
		    if (i > 0) {
			String title = line.substring(i+1);
			boolean first = false;
			if (line.charAt(0) == '>')
			    first = true;
			String file = line.substring(first ? 1 : 0, i);
			menu.add(sim.menuInitializer.getMenuItem(title, "setup " + file));
			if (first && sim.startCircuit == null) {
				sim.startCircuit = file;
				sim.startLabel = title;
			}
		    }
		}
		p += l;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    sim.stop("Can't read setuplist.txt!", null);
	}
   }

   public void readSetup(String text) {
	   sim.gridSize= sim.defaultGridSize; // set default gridsize when opening
	   sim.offsetX=0;
	   sim.offsetY=0;
   	readSetup(text, false);
   }
   
  public void readSetup(String text, boolean retain) {
	   readSetup(text.getBytes(), text.length(), retain);
	   if (!sim.usePanel)
		   sim.titleLabel.setText("untitled");
  }

  public void readSetupFile(String str, String title) {
	  sim.t = 0;
	   URL url ;
	   //System.out.println(str);
	   try {
		   if (!sim.usePanel)
		   url = new URL(sim.getCodeBase() + "circuits/" + str);
		   else
			url = new URL("file:///" + sim.startDir + "\\Falstad\\"+ "circuits\\" + str);
		   System.out.println(url);
		   //System.out.println(getCodeBase().toString());
		   ByteArrayOutputStream ba = sim.readUrlData(url);
		   readSetup(ba.toByteArray(), ba.size(), false);
	   } catch (Exception e) {
		   e.printStackTrace();
		   sim.stop("Unable to read " + sim.getCodeBase() + "circuits/" +  str + "!", null);
		   try {
			   url = new URL(str);
			   // System.out.println(getCodeBase().toString());
			   ByteArrayOutputStream ba = sim.readUrlData(url);
			   readSetup(ba.toByteArray(), ba.size(), false);
		   } catch (Exception e1) {
			   e1.printStackTrace();
			   sim.stop("Unable to read " + str + "!", null);
		   }
	   }
	   //titleLabel.setText(title);
  }

   public void readSetup(byte b[], int len, boolean retain) {
	int i;
	if (!retain) {
	    for (i = 0; i != sim.elmList.size(); i++) {
		CircuitElm ce = getElm(i);
		ce.delete();
	    }
	    sim.elmList.removeAllElements();
	    sim.hintType = -1;
	    sim.timeStep = 5e-6;
	    sim.dotsCheckItem.setState(true);
	    sim.smallGridCheckItem.setState(true);
	    sim.powerCheckItem.setState(false);
	    sim.voltsCheckItem.setState(true);
	    sim.showValuesCheckItem.setState(true);
	   // setGrid();
	    sim.gridSize = sim.defaultGridSize;
	    sim.speedBar.setValue(117); // 57
	    sim.currentBar.setValue(50);
	    sim.powerBar.setValue(50);
	    CircuitElm.voltageRange = 5;
	    sim.scopeCount = 0;
	}
	sim.cv.repaint();
	int p;
	for (p = 0; p < len; ) {
	    int l;
	    int linelen = 0;
	    for (l = 0; l != len-p; l++)
		if (b[l+p] == '\n' || b[l+p] == '\r') {
		    linelen = l++;
		    if (l+p < b.length && b[l+p] == '\n')
			l++;
		    break;
		}
	    String line = new String(b, p, linelen);
	    StringTokenizer st = new StringTokenizer(line);
	    while (st.hasMoreTokens()) {
		String type = st.nextToken();
		int tint = type.charAt(0);
		try {
		    if (tint == 'o') {
			Scope sc = new Scope(sim);
			sc.position = sim.scopeCount;
			sc.undump(st);
			sim.scopes[sim.scopeCount++] = sc;
			break;
		    }
		    if (tint == 'h') {
			readHint(st);
			break;
		    }
		    if (tint == '$') {
			readOptions(st);
			break;
		    }
		    if (tint == '%' || tint == '?' || tint == 'B') {
			// ignore afilter-specific stuff
			break;
		    }
		    if (tint == '&') {
				// ignore afilter-specific stuff
		    	sim.sketchName = st.nextToken();
		    	sim.sketchURL = st.nextToken();
		    	Path path = Paths.get(sim.sketchURL);
		    	System.out.println("-----" + path.toString());
		    	System.out.println("-----" + path.toString());
		    	if (!Files.isRegularFile(path)) {
		    		URL url;
		    		if (!sim.applet.startFolder.isEmpty()){
		    			System.out.println("file:///" + sim.applet.startFolder);
		    			  url = new URL("file:///" +  sim.applet.startFolder+ File.separator+ "arduino"+ File.separator + sim.sketchName);
		    //			  System.out.println("file:///" +System.getProperty("user.dir"));
				//    	    System.out.println(sketchName);
				  //  	    System.out.println(File.separator);
				    //	    System.out.println(File.separator);
		    			  }
		    		else{
		    	    url = new URL("file:///" +System.getProperty("user.dir")+ File.separator+ "arduino"+ File.separator + sim.sketchName);
		    	  
		    		}
		    		System.out.println(url.toString());
		    		sim.sketchURL = url.toString();
		    		  // path is regular file
		    		}
		   // 	else System.out.println("no need to correct");
		   // 	arduinoSketchItem.setLabel("Sketch: "+sketchName);
		    if	(Files.notExists(path)) {
		    	System.out.println("Arduino ino File does not exist");
		    	sim.sketchURL =  sim.getCodeBase().getPath() + File.separator + "arduino" + File.separator + "blank" + File.separator + "blank.ino" ;//"C:\\Users\\FranciscoMateus\\workspace\\pl-projects-master\\pl-projects-master\\jlox\\examples\\arduinoTest.lox";
		    	sim.sketchName = "blank.ino";
		    }
		    else	
		    	sim.arduino.reload();
		    
		    			
				break;
			    }
		    if (tint >= '0' && tint <= '9')
			tint = new Integer(type).intValue();
		    int x1 = new Integer(st.nextToken()).intValue();
		    int y1 = new Integer(st.nextToken()).intValue();
		    int x2 = new Integer(st.nextToken()).intValue();
		    int y2 = new Integer(st.nextToken()).intValue();
		    int f  = new Integer(st.nextToken()).intValue();
		    CircuitElm ce = null;
		    Class cls = sim.dumpTypes[tint];
		    if (cls == null) {
			System.out.println("unrecognized dump type: " + type);
			break;
		    }
		    // find element class
		    Class carr[] = new Class[6];
		    //carr[0] = getClass();
		    carr[0] = carr[1] = carr[2] = carr[3] = carr[4] =
			int.class;
		    carr[5] = StringTokenizer.class;
		    Constructor cstr = null;
		    cstr = cls.getConstructor(carr);
		
		    // invoke constructor with starting coordinates
		    Object oarr[] = new Object[6];
		    //oarr[0] = this;
		    oarr[0] = new Integer(x1);
		    oarr[1] = new Integer(y1);
		    oarr[2] = new Integer(x2);
		    oarr[3] = new Integer(y2);
		    oarr[4] = new Integer(f );
		    oarr[5] = st;
		    ce = (CircuitElm) cstr.newInstance(oarr);
		    ce.setPoints();
		    sim.elmList.addElement(ce);
		} catch (java.lang.reflect.InvocationTargetException ee) {
		    ee.getTargetException().printStackTrace();
		    break;
		} catch (Exception ee) {
		    ee.printStackTrace();
		    break;
		}
		break;
	    }
	    p += l;
	    
	}
	sim.enableItems();
	if (!retain)
		sim.handleResize(); // for scopes
	sim.needAnalyze();
//	System.out.println("voltage source count: "+ (sim.arduino.ucModule.getPinMode(5)?1:0));
	//System.out.println("voltage source count: "+ (sim.arduino.ucModule.getPinMode(8)?1:0));
   }

   void readHint(StringTokenizer st) {
	   sim.hintType  = new Integer(st.nextToken()).intValue();
	   sim.hintItem1 = new Integer(st.nextToken()).intValue();
	   sim.hintItem2 = new Integer(st.nextToken()).intValue();
   }

   void readOptions(StringTokenizer st) {
	int flags = new Integer(st.nextToken()).intValue();
	sim.dotsCheckItem.setState((flags & 1) != 0);
	//smallGridCheckItem.setState((flags & 2) != 0);
	sim.voltsCheckItem.setState((flags & 4) == 0);
	sim.powerCheckItem.setState((flags & 8) == 8);
	sim.showValuesCheckItem.setState((flags & 16) == 0);
	sim.timeStep = new Double (st.nextToken()).doubleValue();
	double sp = new Double(st.nextToken()).doubleValue();
	int sp2 = (int) (Math.log(10*sp)*24+61.5);
	//int sp2 = (int) (Math.log(sp)*24+1.5);
	sim.speedBar  .setValue(sp2);
	sim.currentBar.setValue(new Integer(st.nextToken()).intValue());
	CircuitElm.voltageRange = new Double (st.nextToken()).doubleValue();
	try {
		sim.powerBar.setValue(new Integer(st.nextToken()).intValue());
	} catch (Exception e) {
	}
	//setGrid();
   }
   
   public void printCircuit(String filename, String path){
		sim.printableCheckItem.setState(true);
		try {
			saveImage(sim.cv, filename,path);
		} catch (PrintException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//saveFileName.toString());//"C:\\Users\\FranciscoMateus\\Documents\\circuito.ps");
//		doExportComponent(cv, new Rectangle(0, 0, 1300, 1300), path+filename);
		sim.printableCheckItem.setState(false);
	}
   
   public void saveImage(final Component comp, String fileName , String Path) throws PrintException, IOException  {

	    //	FileOutputStream outputStream ;
	    //	System.out.println(Path+"example.eps");
	    	System.out.println(fileName);
	    	int i, xMin=10000, xMax=-10000, yMin=10000,yMax=-10000;
	    	for (i = 0; i != sim.elmList.size(); i++) {
	    		
	    	    CircuitElm ce = getElm(i);
	    	    // SET DIMENSIONS
	    	    if (xMin>ce.boundingBox.getX())
	    	    	xMin= (int)Math.round(ce.boundingBox.getX());
	    	    if (xMax<(ce.boundingBox.getX()+ce.boundingBox.getWidth()))
	    	    	xMax= (int)Math.round(ce.boundingBox.getX()+ce.boundingBox.getWidth());
	    	    if (yMin>ce.boundingBox.getY())
	    	    	yMin= (int)Math.round(ce.boundingBox.getY());
	    	    if (yMax<(ce.boundingBox.getY()+ce.boundingBox.getHeight()))
	    	    	yMax= (int)Math.round(ce.boundingBox.getY()+ce.boundingBox.getHeight());
	    	}
	    		
	    		sim.g2 = new SVGGraphics2D(xMax+sim.gridSize, yMax+sim.gridSize); 
	    		sim.g2.setStroke(new BasicStroke(2f));
	    		sim.updateCircuit(sim.dbimage.getGraphics(),true);
	            File f = new File(fileName);//Path+"SwingUIToSVGDemo.svg"); 
	            try { 
	                SVGUtils.writeToSVG(f, sim.g2.getSVGElement()); 
	            } catch (IOException ex) { 
	                System.err.println(ex); 
	            }
	  
	    	sim.exportEPS = false;
	    	
	     }
   public CircuitElm getElm(int n) {
		if (n >= sim.elmList.size())
			return null;
		return (CircuitElm) sim.elmList.elementAt(n);
	}
  /* URL getCodeBase() {
   	try {
   		//if (true){//(applet != null){
   		if (!sim.applet.startFolder.isEmpty()){
   			return  new URL("file:///" + sim.applet.startFolder + "/");}
   		else{
   		//	return applet.getCodeBase();}}
   		File f = new File(".");
   		return new URL("file:" + f.getCanonicalPath() + "/");}
   		} catch (Exception e) {
   			e.printStackTrace();
   			return null;
   		}
   	}*/
   public  String doImport(boolean imp, boolean url, String path) {
	   // imp true means open file, imp false means save file
    	int currentScale = sim.gridSize;
    	if (sim.impDialog != null) {
    		sim.requestFocus();
    		sim.impDialog.setVisible(false);
    		sim.impDialog = null;
    	}

    	if (imp){
    		sim.gridSize= sim.defaultGridSize; // set default gridsize when opening
    		sim.offsetX=0;
    		sim.offsetY=0;}
    	// (saving is done at default grid size)
    	else {
    		sim.gridSize = sim.defaultGridSize;
    		sim.uitools.resetOffset();
    		sim.uitools.scaleCircuit(0, 0, sim.gridSize, currentScale);
    		sim.needAnalyze();
    		System.out.print(sim.offsetX);
    		System.out.print(sim.offsetY);
    	}
    	String dump = (imp) ? "" : sim.dumpCircuit();
    	/*if (false) {//(!imp){
    		
    		gridSize = currentScale;
    		scaleCircuit(offsetX, offsetY, gridSize, defaultGridSize);
    		needAnalyze();}
*/
    	if (url)
    		dump = sim.baseURL + "#" + URLEncoder.encode(dump);
    	if (path.length()==0)
    		sim.impDialog = new ImportDialog(sim, dump, url);
    	else{
    		if (imp){
    			try {
    			File file = new File(path );
    			InputStream in = file.toURI().toURL().openStream();
    			//in = new URL(path+filename ).openStream();
    			String content = IOUtils.toString( in,"UTF-8" ) ;
    			IOUtils.closeQuietly(in);
    			sim.filetools.readSetup(content);		
    			} catch (MalformedURLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	}
    		else if (!path.equals("noFile"))
    		{
    			try {
    				//	Path file = Paths.get(path+filename);
    					//Files.write(file, inStr.getBytes());
    					File file = new File(path );
    					FileUtils.writeStringToFile(file, dump,"UTF-8");


    					} catch (IOException e) {
    				        e.printStackTrace();
    				    }
    		}
    	}
    	//impDialog.show();
    	sim.edittools.pushUndo();
    	return (dump);
    }
}
