package com.kollins.project.sofia;



public class startSimulation {
	
	static UCModule ucmodule;
	public static void main(String args[])  {//static method  
	
		ucmodule = new UCModule(true);
		ucmodule.main(null);
		System.out.println("Project Sofia");
		System.out.println("setting up UC");
		ucmodule.setUpUc();
		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		for (int i=0;i<12;i++)
			System.out.print(ucmodule.getPinMode(i)?1:0);
		System.out.println("exiting--------------");
	      System.exit(0);
		}  
	
}
