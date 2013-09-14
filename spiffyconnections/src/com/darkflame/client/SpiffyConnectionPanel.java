package com.darkflame.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.darkflame.client.SpiffyConnection.ConnectionSide;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/** A panel which lets you draw lines between widgets using SVGs **/
public class SpiffyConnectionPanel extends HTMLPanel {
	
	/** All the known connection objects **/
	static ArrayList<SpiffyConnection> allConnectionObjects = new ArrayList<SpiffyConnection>();
	
	// all lines
	HashMap<SpiffyConnection, String> alllines = new HashMap<SpiffyConnection, String>();

	/** The doddle plane has to be given to the panel, and it draws everything on that **/
	HTMLPanel doddles = this;


	boolean firstTimeSetupNeeded=true;

	public SpiffyConnectionPanel() {

		super("<svg></svg>test");
		
		this.setSize("100%", "100%");
		
		
	}
	
	public void clearAllPaths(){
		
		alllines.clear();
		
	}
	void addPathToDoddle(SpiffyConnection sc, String svgPath) {

		alllines.put(sc, svgPath);


	}
    void firstTimeSetUp() {
		
		System.out.println("running first time setup");

		
				
		firstTimeSetupNeeded=false;
	}
	
    
    public SpiffyConnection AddNewConnection(Widget source, ConnectionSide point,
			Widget destination, ConnectionSide point2) {
    	

		System.out.println("AddNewConnection");
    	
    	return new SpiffyConnection(source ,point, destination,point2,this);
    	
    	
	}

	public SpiffyConnection SpiffyConnection(Widget source, Widget destination) {		

		System.out.println("AddNewConnection");
		
		return new SpiffyConnection(source ,destination,this);
	}

    
	public ArrayList<SpiffyConnection> MakeSpiffyConnections(Widget[] objects, Widget destination) {
				

		System.out.println("MakeSpiffyConnections");
		
		ArrayList<SpiffyConnection> newConnection=new ArrayList<SpiffyConnection>();
		//makes a whole bunch of these things
		for (Widget widget : objects) {
			
			newConnection.add(new SpiffyConnection(widget,destination,this));
			
		}
		return newConnection;
	}
	
	public ArrayList<SpiffyConnection> MakeSpiffyConnectionsInv(Widget[] objects, Widget destination) {
				
		System.out.println("MakeSpiffyConnections");
		
		ArrayList<SpiffyConnection> newConnection=new ArrayList<SpiffyConnection>();
		//makes a whole bunch of these things
		for (Widget widget : objects) {
			
			newConnection.add(new SpiffyConnection(destination,widget,this));
			
		}
		return newConnection;
	}


	//public HTMLPanel getDoodlePlane() {
	//	if (firstTimeSetupNeeded) {
	//		firstTimeSetUp();
	//	}
	//	return doddles;
	///	
	//}
	
	public void refreshLines() {
		// refresh lines
		String allPaths = "";

		Iterator<String> lit = alllines.values().iterator();
		while (lit.hasNext()) {

			String path = lit.next();
			allPaths = allPaths + path;

		}
		if (firstTimeSetupNeeded) {
			firstTimeSetUp();
		}
		//System.out.println(allPaths);

		System.out.println("updating html");

		doddles.getElement().setInnerHTML("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">"
				+ allPaths + "</svg>");
		
		doddles.setSize("100%", "100%");
	}
	void removePathFromDoddle(SpiffyConnection spiffyConnection) {

		alllines.remove(spiffyConnection);


	}

	static public void refreshAllConnections(){
		
		for (SpiffyConnection spiffyconnections : allConnectionObjects) {

			spiffyconnections.refreshPath();
		}
		
	}

	
	
	

}
