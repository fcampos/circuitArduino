package circuitArduino;
import java.awt.*;
//import java.awt.event.*;
//import java.io.IOException;
//import java.net.MalformedURLException;
import java.net.URL;

import circuitArduino.CirSim;

//import java.nio.file.*;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.io.FileUtils;
import java.io.*;
public class SelectFileDialog extends FileDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = -128146889946898441L;
	/**
	 * 
	 */
	CirSim cframe;
  //  Button importButton, closeButton;
    //TextArea text;
    //TextArea filename;
    URL starturl;
   // boolean isURL;
  //  String inStr;
    String filename;
    String path;
   // String content;
	//String content;
	
    public SelectFileDialog(CirSim f)  {
	//super(f, (str.length() > 0) ? "Export" : "Import", false);
    	
	super(f.matlabFrame, "Choose a file",  FileDialog.LOAD );
    	
    	
	/*isURL = url;
	inStr = str;*/
	cframe = f;
	starturl = cframe.getCodeBase();
	//System.out.println(str);
	//System.out.println(starturl.toString());
	setDirectory(starturl.toString());// "C:\\");
	//setFile("*.txt");
	setVisible(true);
	 filename = getFile();
	 System.out.println(filename);
	 path = getDirectory();
	 System.out.println(path);
	 cframe.selectedFile=path+filename;//path+File.separator+filename; EXCLUDED FILE SEPARATOR
	 cframe.selectedFileName=filename;
	/*importExport ();
	//FileDialog fd = new FileDialog(cframe, "Choose a file", FileDialog.LOAD);
    }
	public void importExport () {
	*/
	 
	 /*
	if (filename == null)
	 System.out.println("You cancelled the choice");
	else{
	 // System.out.println("You chose " + path + filename);
	String content;
	if (inStr.length() == 0){
		InputStream in;
		try {
			File file = new File(path+File.separator+filename );
			in = file.toURI().toURL().openStream();
			//in = new URL(path+filename ).openStream();
			content = IOUtils.toString( in,"UTF-8" ) ;
			IOUtils.closeQuietly(in);
			cframe.readSetup(content);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	
	}
	else
		try {
	//	Path file = Paths.get(path+filename);
		//Files.write(file, inStr.getBytes());
		File file = new File(path+File.separator+filename );
		FileUtils.writeStringToFile(file, inStr,"UTF-8");


		} catch (IOException e) {
	        e.printStackTrace();
	    }
	}*/
	/*setLayout(new SelectFileDialogLayout());
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
    
