package circuitArduino;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
//import java.nio.file.*;
import org.apache.commons.io.IOUtils;

import circuitArduino.CirSim;

import org.apache.commons.io.FileUtils;
import java.io.*;
class ImportDialog extends FileDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -128146889946898441L;
	/**
	 * 
	 */
	CirSim cframe;
    Button importButton, closeButton;
    TextArea text;
    //TextArea filename;
    URL starturl;
    boolean isURL;
    String inStr;
    String filename;
    String path;
   // String content;
	//String content;
	
    ImportDialog(CirSim f, String str, boolean url)  {
	//super(f, (str.length() > 0) ? "Export" : "Import", false);
    	
	super(f.matlabFrame, "Choose a file", (str.length() > 0) ? FileDialog.SAVE : FileDialog.LOAD );
    	
    	
	isURL = url;
	cframe = f;
	inStr = str;
	starturl = cframe.getCodeBase();
	//System.out.println(str);
	//System.out.println(starturl.toString());
	setDirectory(starturl.toString());// "C:\\");
	setFile("*.txt");
	setVisible(true);
	 filename = getFile();
	 path = getDirectory();
	/*importExport ();
	//FileDialog fd = new FileDialog(cframe, "Choose a file", FileDialog.LOAD);
    }
	public void importExport () {
	*/
	if (filename == null)
	 System.out.println("You cancelled the choice");
	else{
	 // System.out.println("You chose " + path + filename);
	String content;
	if (inStr.length() == 0){ //DOING IMPORT
		InputStream in;
		try {
			File file = new File(path+File.separator+filename );
			in = file.toURI().toURL().openStream();
			//in = new URL(path+filename ).openStream();
			content = IOUtils.toString( in,"UTF-8" ) ;
			IOUtils.closeQuietly(in);
			cframe.filetools.readSetup(content);
			cframe.circuitFilePath = path+File.separator+filename;
			cframe.circuitName = filename;
			cframe.frameWrapper.setTitle(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	/*try {
	//content = new String(Files.readAllBytes(Paths.get(path+filename)));
	//	File file = new File(path+File.separator+filename);
		
		
	content = new String(Files.readAllBytes(path+File.separator+filename));
	System.out.println(content);
	cframe.readSetup(content);
	} catch (IOException e) {
        e.printStackTrace();
    } */
	}
	else // DOING SAVE
		try {
	//	Path file = Paths.get(path+filename);
		//Files.write(file, inStr.getBytes());
		File file = new File(path+File.separator+filename );
		FileUtils.writeStringToFile(file, inStr,"UTF-8");
		cframe.circuitFilePath = path+File.separator+filename;
		cframe.circuitName = filename;
		cframe.frameWrapper.setTitle(filename);
		} catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	/*setLayout(new ImportDialogLayout());
	add(text = new TextArea(str, 10, 60, TextArea.SCROLLBARS_BOTH));
	importButton = new Button("Import");
	if (!isURL)
	    add(importButton);
	importButton.addActionListener(this);
	add(closeButton = new Button("Close"));
	closeButton.addActionListener(this);
	Point x = cframe.main.getLocationOnScreen();
	resize(400, 300);
	Dimension d = getSize();
	setLocation(x.x + (cframe.winSize.width-d.width)/2,
		    x.y + (cframe.winSize.height-d.height)/2);
	show();
	if (str.length() > 0)
	    text.selectAll();*/
    }

 /*   public void actionPerformed(ActionEvent e) {
	int i;
	Object src = e.getSource();
	if (src == importButton) {
	    cframe.readSetup(text.getText());
	    setVisible(false);
	}
	if (src == closeButton)
	    setVisible(false);
    }
	
    public boolean handleEvent(Event ev) {
	if (ev.id == Event.WINDOW_DESTROY) {
	    CirSim.main.requestFocus();
	    setVisible(false);
	    cframe.impDialog = null;
	    return true;
	}
	return super.handleEvent(ev);
    }*/
}
    
