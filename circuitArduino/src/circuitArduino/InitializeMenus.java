package circuitArduino;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.Font;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.KeyEvent;

import circuitArduino.CirSim;
import circuitArduino.CircuitElm;

import java.awt.Button;
//import com.lushprojects.circuitjs1.client.AboutBox;;

public class InitializeMenus {
	static CirSim sim;
	public Menu mFile;
	boolean useFrame = true;
	boolean convention = true;
	InitializeMenus(CirSim s){
		sim=s;
	}
	public void init(){
		sim.mainMenu = new PopupMenu();
		MenuBar mb = null;
		//if (useFrame)
		
		/////////////////////
		//// UPPER MENU BAR
		if (!sim.usePanel)
		mb = new MenuBar();
		
		
		
		///////////////////////////////////////
		///// FILE MENU
		///////////////////////////////////////
		
		mFile = new Menu("File");
		if (!sim.usePanel)
		mb.add(mFile);
		//else
		//    sim.mainMenu.add(m);

		mFile.add(sim.importItem = getMenuItem("Open"));
		mFile.add(sim.exportItem = getMenuItem("Save"));
		//mFile.add(sim.importFritzingItem = getMenuItem("Import Fritzing"));
		//mFile.add(sim.robotSimulation = getMenuItem("Robot Simulation"));
		mFile.add(sim.printItem = getMenuItem("Print to SVG"));

		mFile.addSeparator();
		mFile.add(sim.exitItem   = getMenuItem("Exit"));

		///////////////////////////////////////
		///// EDIT MENU
		///////////////////////////////////////

		Menu mEdit = new Menu("Edit");
		mEdit.add(sim.undoItem = getMenuItem("Undo"));
		sim.undoItem.setShortcut(new MenuShortcut(KeyEvent.VK_Z));
		mEdit.add(sim.redoItem = getMenuItem("Redo"));
		sim.redoItem.setShortcut(new MenuShortcut(KeyEvent.VK_Z, true));
		mEdit.addSeparator();
		mEdit.add(sim.cutItem = getMenuItem("Cut"));
		sim.cutItem.setShortcut(new MenuShortcut(KeyEvent.VK_X));
		mEdit.add(sim.copyItem = getMenuItem("Copy"));
		sim.copyItem.setShortcut(new MenuShortcut(KeyEvent.VK_C));
		mEdit.add(sim.pasteItem = getMenuItem("Paste"));
		sim.pasteItem.setShortcut(new MenuShortcut(KeyEvent.VK_V));
		sim.pasteItem.setEnabled(false);
		mEdit.add(sim.selectAllItem = getMenuItem("Select All"));
		sim.selectAllItem.setShortcut(new MenuShortcut(KeyEvent.VK_A));
		if (!sim.usePanel)
		    mb.add(mEdit);
		//else
		//    sim.mainMenu.add(m);

		///////////////////////////////////////
		/////  SCOPE MENU
		///////////////////////////////////////

		Menu mScope = new Menu("Scope");
		if (!sim.usePanel)
		    mb.add(mScope);
		//else
		//    sim.mainMenu.add(m);
		
		mScope.add(getMenuItem("Stack All", "stackAll"));
		mScope.add(getMenuItem("Unstack All", "unstackAll"));

		///////////////////////////////////////
		///// OPTIONS MENU
		//// MOSTLY MADE UP OF CHECKITEMS
		///////////////////////////////////////
		Menu mOptions= new Menu("Options");
		sim.optionsMenu = mOptions;//m = new Menu("Options");
		if (!sim.usePanel)
		    mb.add(mOptions);
		//else
		 //   sim.mainMenu.add(m);
		mOptions.add(sim.dotsCheckItem = getCheckItem("Show Current"));
		sim.dotsCheckItem.setState(true);
		mOptions.add(sim.voltsCheckItem = getCheckItem("Show Voltage"));
		sim.voltsCheckItem.setState(true);
		mOptions.add(sim.powerCheckItem = getCheckItem("Show Power"));
		mOptions.add(sim.showValuesCheckItem = getCheckItem("Show Values"));
		sim.showValuesCheckItem.setState(true);
		//m.add(conductanceCheckItem = getCheckItem("Show Conductance"));
		mOptions.add(sim.smallGridCheckItem = getCheckItem("Small Grid"));
		//smallGridCheckItem = getCheckItem("Small Grid");
//		smallGridCheckItem.setEnabled(true);
		sim.smallGridCheckItem.setState(true);
		mOptions.add(sim.showGridCheckItem = getCheckItem("Show Grid"));
		//smallGridCheckItem = getCheckItem("Small Grid");
//		smallGridCheckItem.setEnabled(true);
		sim.showGridCheckItem.setState(false);
		mOptions.add(sim.euroResistorCheckItem = getCheckItem("European Resistors"));
	//	sim.euroResistorCheckItem.setState(euro);
		mOptions.add(sim.printableCheckItem = getCheckItem("White Background"));
		//sim.printableCheckItem.setState(printable);
		mOptions.add(sim.conventionCheckItem = getCheckItem("Conventional Current Motion"));
		sim.conventionCheckItem.setState(convention);
		mOptions.add(sim.optionsItem = getMenuItem("Other Options..."));
		
		
		
		
		///////////////////////////////////////
		///// CIRCUITS MENU
		////  HIERARCHY OF DEFAULT CIRCUITS
		////  I think it is later read from file
		//////////////////////////////////////
		Menu circuitsMenu = new Menu("Circuits");
		if (!sim.usePanel)
		    mb.add(circuitsMenu);
	//	else
		//    sim.mainMenu.add(circuitsMenu);
		if (!sim.usePanel)
			sim.frameWrapper.setMenuBar(mb);

		
		///////////////////////////////////////
		///// ARDUINO MENU
		//// 
		///////////////////////////////////////
		Menu mArduino= new Menu("Arduino");
		sim.arduinoMenu = mArduino;//m = new Menu("Options");
		if (!sim.usePanel)
			mb.add(mArduino);
		//mArduino.add( sim.arduinoSketchItem=getMenuItem("Sketch: " + sim.sketchName));

		mArduino.add(sim.arduinoSelectSketchItem = getMenuItem("Select sketch"));
		mArduino.add(sim.arduinoEditFileItem = getMenuItem("Edit file"));
		mArduino.add(sim.arduinoReloadItem = getMenuItem("Reload"));
		mArduino.add(sim.arduinoEditOptionItem = getMenuItem("Options"));
		

		///////////////////////////////////////
		///// MAIN MENU - POPUP
		////  HIERARCHY OF ELECTRONIC COMPONENTS
		///////////////////////////////////////
		sim.mainMenu.add(getClassCheckItem("Add Wire", "WireElm"));
		sim.mainMenu.add(getClassCheckItem("Add Resistor", "components.passive.ResistorElm"));
		
		Menu passMenu = new Menu("Passive Components");
		sim.mainMenu.add(passMenu);
		passMenu.add(getClassCheckItem("Add Capacitor", "components.passive.CapacitorElm"));
		passMenu.add(getClassCheckItem("Add Inductor", "components.passive.InductorElm"));
		passMenu.add(getClassCheckItem("Add Switch", "components.passive.SwitchElm"));
		passMenu.add(getClassCheckItem("Add Push Switch", "components.passive.PushSwitchElm"));
		passMenu.add(getClassCheckItem("Add SPDT Switch", "components.passive.Switch2Elm"));
		passMenu.add(getClassCheckItem("Add Potentiometer", "components.passive.PotElm"));
		passMenu.add(getClassCheckItem("Add Linear axis Potentiometer", "components.passive.LinAxisPotElm"));
		passMenu.add(getClassCheckItem("Add Transformer", "components.passive.TransformerElm"));
		passMenu.add(getClassCheckItem("Add Tapped Transformer",
					       "components.passive.TappedTransformerElm"));
		passMenu.add(getClassCheckItem("Add Transmission Line", "components.passive.TransLineElm"));
		passMenu.add(getClassCheckItem("Add Relay", "components.passive.RelayElm"));
		passMenu.add(getClassCheckItem("Add Bistable Relay", "components.passive.RelayBistableElm"));
		passMenu.add(getClassCheckItem("Add Memristor", "components.passive.MemristorElm"));
		passMenu.add(getClassCheckItem("Add Spark Gap", "components.passive.SparkGapElm"));
	//	passMenu.add(getClassCheckItem("Add Photo Resistor", "components.passive.PhotoResistorElm"));
		passMenu.add(getClassCheckItem("Add DC Motor", "components.passive.DCmotorElm"));
		passMenu.add(getClassCheckItem("Add Speaker", "components.passive.SpeakerElm"));
	//	passMenu.add(getClassCheckItem("Add Thermistor", "components.passive.ThermistorElm"));
		//passMenu.add(getClassCheckItem("Add Fan", "components.passive.DCfanElm"));
		Menu inputMenu = new Menu("Inputs/Outputs");
		sim.mainMenu.add(inputMenu);
		inputMenu.add(getClassCheckItem("Add Ground", "components.inout.GroundElm"));
		inputMenu.add(getClassCheckItem("Add Voltage Source (2-terminal)", "components.inout.DCVoltageElm"));
		inputMenu.add(getClassCheckItem("Add A/C Source (2-terminal)", "components.inout.ACVoltageElm"));
		inputMenu.add(getClassCheckItem("Add Voltage Source (1-terminal)", "components.inout.RailElm"));
		inputMenu.add(getClassCheckItem("Add A/C Source (1-terminal)", "components.inout.ACRailElm"));
		inputMenu.add(getClassCheckItem("Add Square Wave (1-terminal)", "components.inout.SquareRailElm"));
		inputMenu.add(getClassCheckItem("Add Analog Output", "components.inout.OutputElm"));
		inputMenu.add(getClassCheckItem("Add Logic Input", "components.inout.LogicInputElm"));
		inputMenu.add(getClassCheckItem("Add Logic Output", "components.inout.LogicOutputElm"));
		inputMenu.add(getClassCheckItem("Add Clock", "components.inout.ClockElm"));
		inputMenu.add(getClassCheckItem("Add A/C Sweep", "components.inout.SweepElm"));
		inputMenu.add(getClassCheckItem("Add Var. Voltage", "components.inout.VarRailElm"));
		inputMenu.add(getClassCheckItem("Add Antenna", "components.inout.AntennaElm"));
		inputMenu.add(getClassCheckItem("Add Current Source", "components.inout.CurrentElm"));
		inputMenu.add(getClassCheckItem("Add LED", "components.inout.LEDElm"));
		inputMenu.add(getClassCheckItem("Add Photodiode", "components.inout.PhotodiodeElm"));
		//inputMenu.add(getClassCheckItem("Add Distance Sensor", "DistSensorElm"));
		inputMenu.add(getClassCheckItem("Add Analog Sensor", "components.inout.AnalogSensorElm"));
		inputMenu.add(getClassCheckItem("Add Audio Input", "components.inout.AudioInElm"));
		//inputMenu.add(getClassCheckItem("Add 3 post IC", "ThreePostElm"));
		inputMenu.add(getClassCheckItem("Add Lamp (beta)", "components.inout.LampElm"));
		
		Menu activeMenu = new Menu("Active Components");
		sim.mainMenu.add(activeMenu);
		activeMenu.add(getClassCheckItem("Add Diode", "components.active.DiodeElm"));
		activeMenu.add(getClassCheckItem("Add Zener Diode", "components.active.ZenerElm"));
		activeMenu.add(getClassCheckItem("Add Transistor (bipolar, NPN)",
					    "components.active.NTransistorElm"));
		activeMenu.add(getClassCheckItem("Add Transistor (bipolar, PNP)",
					    "components.active.PTransistorElm"));
		activeMenu.add(getClassCheckItem("Add Op Amp (- on top)", "components.active.OpAmpElm"));
		activeMenu.add(getClassCheckItem("Add Op Amp (+ on top)",
					    "components.active.OpAmpSwapElm"));
		activeMenu.add(getClassCheckItem("Add MOSFET (n-channel)",
					    "components.active.NMosfetElm"));
		activeMenu.add(getClassCheckItem("Add MOSFET (p-channel)",
					    "components.active.PMosfetElm"));
		activeMenu.add(getClassCheckItem("Add JFET (n-channel)",
						 "components.active.NJfetElm"));
	//	activeMenu.add(getClassCheckItem("Add JFET (p-channel)",
	//					 "PJfetElm")); // WHERE IS PJFET????
		activeMenu.add(getClassCheckItem("Add Analog Switch (SPST)", "components.active.AnalogSwitchElm"));
		activeMenu.add(getClassCheckItem("Add Analog Switch (SPDT)", "components.active.AnalogSwitch2Elm"));
		activeMenu.add(getClassCheckItem("Add SCR", "components.active.SCRElm"));
		//activeMenu.add(getClassCheckItem("Add Varactor/Varicap", "VaractorElm"));
		activeMenu.add(getClassCheckItem("Add Tunnel Diode", "components.active.TunnelDiodeElm"));
		activeMenu.add(getClassCheckItem("Add Triode", "components.active.TriodeElm"));
		//activeMenu.add(getClassCheckItem("Add Diac", "DiacElm"));
		//activeMenu.add(getClassCheckItem("Add Triac", "TriacElm"));
		//activeMenu.add(getClassCheckItem("Add Photoresistor", "PhotoResistorElm"));
		//activeMenu.add(getClassCheckItem("Add Thermistor", "ThermistorElm"));
		activeMenu.add(getClassCheckItem("Add CCII+", "components.active.CC2Elm"));
		activeMenu.add(getClassCheckItem("Add CCII-", "components.active.CC2NegElm"));

		Menu gateMenu = new Menu("Logic Gates");
		sim.mainMenu.add(gateMenu);
		gateMenu.add(getClassCheckItem("Add Inverter", "components.logicGates.InverterElm"));
		gateMenu.add(getClassCheckItem("Add NAND Gate", "components.logicGates.NandGateElm"));
		gateMenu.add(getClassCheckItem("Add NOR Gate", "components.logicGates.NorGateElm"));
		gateMenu.add(getClassCheckItem("Add AND Gate", "components.logicGates.AndGateElm"));
		gateMenu.add(getClassCheckItem("Add OR Gate", "components.logicGates.OrGateElm"));
		gateMenu.add(getClassCheckItem("Add XOR Gate", "components.logicGates.XorGateElm"));

		Menu chipMenu = new Menu("Chips");
		sim.mainMenu.add(chipMenu);
		chipMenu.add(getClassCheckItem("Add D Flip-Flop", "components.chips.DFlipFlopElm"));
		chipMenu.add(getClassCheckItem("Add JK Flip-Flop", "components.chips.JKFlipFlopElm"));
		chipMenu.add(getClassCheckItem("Add 7 Segment LED", "components.chips.SevenSegElm"));
		chipMenu.add(getClassCheckItem("Add VCO", "components.chips.VCOElm"));
		chipMenu.add(getClassCheckItem("Add Phase Comparator", "components.chips.PhaseCompElm"));
		chipMenu.add(getClassCheckItem("Add Counter", "components.chips.CounterElm"));
		chipMenu.add(getClassCheckItem("Add Decade Counter", "components.chips.DecadeElm"));
		chipMenu.add(getClassCheckItem("Add 555 Timer", "components.chips.TimerElm"));
		chipMenu.add(getClassCheckItem("Add DAC", "components.chips.DACElm"));
		chipMenu.add(getClassCheckItem("Add ADC", "components.chips.ADCElm"));
		chipMenu.add(getClassCheckItem("Add Latch", "components.chips.LatchElm"));
		chipMenu.add(getClassCheckItem("Add Voltage Regulator", "components.chips.VRegElm"));
	//	chipMenu.add(getClassCheckItem("Add Arduino Input", "ArduDI"));
	//	chipMenu.add(getClassCheckItem("Add Arduino Output", "ArduDO"));
		
		Menu otherMenu = new Menu("Other");
		sim.mainMenu.add(otherMenu);
		otherMenu.add(getClassCheckItem("Add Text", "components.labels_instruments.TextElm"));
		otherMenu.add(getClassCheckItem("Add Scope Probe", "components.labels_instruments.ProbeElm"));
		otherMenu.add(getCheckItem("Drag All (Alt-drag)", "DragAll"));
	/*	otherMenu.add(getCheckItem(
				  isMac ? "Drag Row (Alt-S-drag, S-right)" :
				  "Drag Row (S-right)",
				  "DragRow"));*/
		otherMenu.add(getCheckItem(
				  sim.isMac ? "Drag Element (Alt-\u2318-drag, \u2318-right)" :
				  "Drag Element (C-right)",
				  "DragElement"));
		//otherMenu.add(getCheckItem("Drag Selected", "DragSelected"));
		otherMenu.add(getCheckItem("Drag Post (" + sim.ctrlMetaKey + "-drag)",
					   "DragPost"));

		sim.mainMenu.add(getCheckItem("Select/Drag Selected (space or Shift-drag)", "Select"));
		
		Menu arduinoMenu = new Menu("Arduino Ports");
		sim.mainMenu.add(arduinoMenu);
		arduinoMenu.add(getClassCheckItem("Add Arduino Digital Pin", "components.arduino.ArduDO"));
		arduinoMenu.add(getClassCheckItem("Add Arduino Analog Input", "components.arduino.ArduAI"));
		
	//	sim.mainMenu.add(passMenu);
		
		
		
		///////////////////////////////////////////////////////////
		//// IF not using frame, add menus to popup
		///////////////////////////////////////////////////////////////
		if (sim.usePanel){
		sim.mainMenu.addSeparator();
		//sim.mainMenu.add(mFile);
		sim.mainMenu.add(mEdit);
		sim.mainMenu.add(mOptions);
		sim.mainMenu.add(circuitsMenu);
		sim.mainMenu.add(mArduino);
		}

		//circuitsMenu
		sim.main.add(sim.mainMenu);
		///////////////////////////////////////////
		//// RIGHT HAND SIDE COMPONENTS
		///////////////////////////////////////////
		//sim.main.add(new Label("                                    "));
		sim.durationTextItem = new TextField(8);
		sim.resetButton = new Button("Reset");
	//	sim.dummyButton = new Button("dummy");
		
		sim.dumpMatrixButton = new Button("Dump Matrix");
		//main.add(dumpMatrixButton);
		//sim.dumpMatrixButton.addActionListener(sim);
		sim.stoppedCheck = new Checkbox("Stopped");
		
	//// SPEED BAR
		// was max of 140
	//	sim.speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 0, 260);
		sim.speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 0, 460);
		
	//// CURRENT SPEED BAR
		
		sim.currentBar = new Scrollbar(Scrollbar.HORIZONTAL,
					   50, 1, 1, 100);
		
	//// POWER BAR
		sim.powerLabel = new Label("Power Brightness", Label.CENTER);
		
		sim.powerBar = new Scrollbar(Scrollbar.HORIZONTAL,
			    50, 1, 1, 100);
		
		
		
		if (!sim.usePanel){
			sim.main.add(sim.durationTextItem);
			sim.durationTextItem.addActionListener(sim);
		sim.main.add(sim.resetButton);
		sim.resetButton.addActionListener(sim);
	//	sim.main.add(sim.dummyButton);
	//	sim.dummyButton.addActionListener(sim);
		sim.stoppedCheck.addItemListener(sim);
		sim.main.add(sim.stoppedCheck);
		sim.main.add(new Label("Simulation Speed", Label.CENTER));
		sim.main.add(sim.speedBar);
		sim.speedBar.addAdjustmentListener(sim);
		sim.main.add(new Label("Current Speed", Label.CENTER));
		sim.currentBar.addAdjustmentListener(sim);
		sim.main.add(sim.currentBar);
		sim.main.add(sim.powerLabel);
		sim.main.add(sim.powerBar);
		sim.powerBar.addAdjustmentListener(sim);
		sim.powerBar.disable();
		sim.powerLabel.disable();
		
		sim.main.add(new Label("www.falstad.com"));

		if (useFrame)
			sim.main.add(new Label(""));
		Font f = new Font("SansSerif", 0, 10);
		Label l;
		l = new Label("Current Circuit:");
		l.setFont(f);
		sim.titleLabel = new Label("Label");
		sim.titleLabel.setFont(f);
		if (useFrame) {
			sim.main.add(l);
			sim.main.add(sim.titleLabel);
		}
		}
		else
		
		{sim.stoppedCheck.addItemListener(sim);
		sim.main.add(sim.stoppedCheck);
	//	sim.main.add(sim.dummyButton);
	//	sim.dummyButton.addActionListener(sim);
		}
		
		
		///////////////////////////////////////////////////////////
		//// EDIT ELEMENT POP-UP MENU
		///////////////////////////////////////////////////////////
		sim.elmMenu = new PopupMenu();
		sim.elmMenu.add(sim.elmEditMenuItem = getMenuItem("Edit"));
		sim.elmMenu.add(sim.elmScopeMenuItem = getMenuItem("View in Scope"));
		sim.elmMenu.add(sim.elmCutMenuItem = getMenuItem("Cut"));
		sim.elmMenu.add(sim.elmCopyMenuItem = getMenuItem("Copy"));
		sim.elmMenu.add(sim.elmDeleteMenuItem = getMenuItem("Delete"));
		sim.main.add(sim.elmMenu);
		
		sim.scopeMenu = buildScopeMenu(false);
		sim.transScopeMenu = buildScopeMenu(true);

		sim.filetools.getSetupList(circuitsMenu, false);
	//	if (useFrame)
	//	    sim.setMenuBar(mb);
	}


	PopupMenu buildScopeMenu(boolean t) {
		PopupMenu m = new PopupMenu();
		m.add(getMenuItem("Remove", "remove"));
		m.add(getMenuItem("Speed 2x", "speed2"));
		m.add(getMenuItem("Speed 1/2x", "speed1/2"));
		m.add(getMenuItem("Scale 2x", "scale"));
		m.add(getMenuItem("Max Scale", "maxscale"));
		m.add(getMenuItem("Stack", "stack"));
		m.add(getMenuItem("Unstack", "unstack"));
		m.add(getMenuItem("Reset", "reset"));
		if (t) {
		    m.add(sim.scopeIbMenuItem = getCheckItem("Show Ib"));
		    m.add(sim.scopeIcMenuItem = getCheckItem("Show Ic"));
		    m.add(sim.scopeIeMenuItem = getCheckItem("Show Ie"));
		    m.add(sim.scopeVbeMenuItem = getCheckItem("Show Vbe"));
		    m.add(sim.scopeVbcMenuItem = getCheckItem("Show Vbc"));
		    m.add(sim.scopeVceMenuItem = getCheckItem("Show Vce"));
		    m.add(sim.scopeVceIcMenuItem = getCheckItem("Show Vce vs Ic"));
		} else {
		    m.add(sim.scopeVMenuItem = getCheckItem("Show Voltage"));
		    m.add(sim.scopeIMenuItem = getCheckItem("Show Current"));
		    m.add(sim.scopePowerMenuItem = getCheckItem("Show Power Consumed"));
		    m.add(sim.scopeMaxMenuItem = getCheckItem("Show Peak Value"));
		    m.add(sim.scopeMinMenuItem = getCheckItem("Show Negative Peak Value"));
		    m.add(sim.scopeFreqMenuItem = getCheckItem("Show Frequency"));
		    m.add(sim.scopeVIMenuItem = getCheckItem("Show V vs I"));
		    m.add(sim.scopeXYMenuItem = getCheckItem("Plot X/Y"));
		    m.add(sim.scopeSelectYMenuItem = getMenuItem("Select Y", "selecty"));
		    m.add(sim.scopeResistMenuItem = getCheckItem("Show Resistance"));
		}
		sim.main.add(m);
		return m;
	    }
	 MenuItem getMenuItem(String s) {
			MenuItem mi = new MenuItem(s);
			mi.addActionListener(sim);
			return mi;
		    }

		    MenuItem getMenuItem(String s, String ac) {
			MenuItem mi = new MenuItem(s);
			mi.setActionCommand(ac);
			mi.addActionListener(sim);
			return mi;
		    }

		    CheckboxMenuItem getCheckItem(String s) {
			CheckboxMenuItem mi = new CheckboxMenuItem(s);
			mi.addItemListener(sim);
			mi.setActionCommand("");
			return mi;
		    }

		    CheckboxMenuItem getClassCheckItem(String s, String t) {
			try {
				Class c;
				//if (t.equals("CapacitorElm"))
			   //  c = Class.forName("circuitMatlab.components.passive." + t);
				//else
					   c = Class.forName("circuitArduino." + t);
			    CircuitElm elm = sim.constructElement(c, 0, 0);
			  //  System.out.println(elm.dump());
			    sim.register(c, elm);
			    int dt = 0;
			    if (elm.needsShortcut() && elm.getDumpClass() == c) {
				dt = elm.getDumpType();
				s += " (" + (char)dt + ")";
			    }
			    elm.delete();
			} catch (Exception ee) {
			    ee.printStackTrace();
			}
			return getCheckItem(s, t);
		    }
		    
		    CheckboxMenuItem getCheckItem(String s, String t) {
			CheckboxMenuItem mi = new CheckboxMenuItem(s);
			mi.addItemListener(sim);
			mi.setActionCommand(t);
			return mi;
		    }
}

