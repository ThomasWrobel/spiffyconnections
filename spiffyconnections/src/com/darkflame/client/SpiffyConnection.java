package com.darkflame.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.mortbay.log.Log;

import com.darkflame.client.SpiffyConnection.ConnectionPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


/** a class that represents a single connection between two points **/
public class SpiffyConnection {
	static boolean widgetsInContainerMode = false;
	static boolean UseCSSMode = false;
	
	
	
	static class ConnectionPoint {
		
		static int margin = 10;
		int arrowHeadSize = 15;
		Widget sourceWidget;
		ConnectionSide side;
		int x; // relative to the widgets top left
		int y;

		public ConnectionPoint(Widget sourceWidget, ConnectionSide side, int x,
				int y) {
			
			this.sourceWidget = sourceWidget;
			this.side = side;
			this.x = x;
			this.y = y;
			
		}

		public int getY() {
			
			//first get the correct element			
			Element useThis = sourceWidget.getElement();
			if (widgetsInContainerMode){
				useThis = sourceWidget.getElement().getParentElement();
			}
			
			//then return Y based on css or javascript data
			if (UseCSSMode){
				String topCSS = useThis.getStyle().getTop();
				
				return Integer.parseInt(topCSS.substring(0, topCSS.length()-2));
				
			}
			
			return y + useThis.getOffsetTop();
			
			
		//	if (widgetsInContainerMode){
		//		return y + sourceWidget.getElement().getParentElement().getOffsetTop();
			//}
		//	
			//return y + sourceWidget.getElement().getOffsetTop();
		}

		public int getX() {
			if (widgetsInContainerMode){
				return x + sourceWidget.getElement().getParentElement().getOffsetLeft();
			}
			return x + sourceWidget.getElement().getOffsetLeft();
		}

		public int getArrowPoint1Y() {

			if (side == ConnectionSide.Left) {
				return getOffsetY() - arrowHeadSize;
			}
			if (side == ConnectionSide.Right) {
				return getOffsetY() + arrowHeadSize;
			}

			return getOffsetY();
		}

		public int getArrowPoint2Y() {

			if (side == ConnectionSide.Left) {
				return getOffsetY() + 15;
			}
			if (side == ConnectionSide.Right) {
				return getOffsetY() - 15;
			}

			return getOffsetY();
		}

		public int getArrowPoint1X() {

			if (side == ConnectionSide.Top) {
				return getOffsetX() + 15;
			}
			if (side == ConnectionSide.Bottom) {
				return getOffsetX() - 15;
			}

			return getOffsetX();
		}

		public int getArrowPoint2X() {

			if (side == ConnectionSide.Top) {
				return getOffsetX() - 15;
			}
			if (side == ConnectionSide.Bottom) {
				return getOffsetX() + 15;
			}

			return getOffsetX();
		}

		public int getOffsetY() {
			if (side == ConnectionSide.Top) {
				return getY() - margin;
			}
			if (side == ConnectionSide.Bottom) {
				return getY() + margin;
			}
			return getY();
		}

		public int getOffsetX() {
			if (side == ConnectionSide.Left) {
				return getX() - margin;
			}
			if (side == ConnectionSide.Right) {
				return getX() + margin;
			}
			return getX();
		}
	}

	public enum ConnectionSide {
		Top, Bottom, Left, Right, Auto,MiddleOfObject
	}

	enum ConnectionType {
		Line, Curve
	}

	enum ConnectionStyle {
		Plane, ArrowsBothEnd, Start,End
	}

	ConnectionStyle currentStyle = ConnectionStyle.End;
	ConnectionType currentType = ConnectionType.Curve;
	
	ConnectionSide currentStartSide = ConnectionSide.Auto;
	ConnectionSide currentEndSide = ConnectionSide.Auto;
	
	
	
	
	ConnectionPoint ChosenStart = null;
	ConnectionPoint ChosenEnd = null;

	// base div for drawing
	//static 
	
	
	/** The SpiffyConnectionPanel has to be given to the panel, and it draws everything on that **/
	SpiffyConnectionPanel doddles = null;
	
	
	


	ArrayList<ConnectionPoint> sourceSides = new ArrayList<ConnectionPoint>();
	
	ConnectionPoint middleOfSourceObject;
	ConnectionPoint middleOfDestObject;
	
	ArrayList<ConnectionPoint> destinationSides = new ArrayList<ConnectionPoint>();

	public SpiffyConnection(Widget source, ConnectionSide point,
			Widget destination, ConnectionSide point2, SpiffyConnectionPanel DoddlePanel) {

		doddles=DoddlePanel;
		
		if (doddles.firstTimeSetupNeeded) {
			doddles.firstTimeSetUp();
		}

		MakeSpiffyConnection(source, point, destination, point2);

		refreshPath();
	}

	
	
	public SpiffyConnection(Widget source, Widget destination, SpiffyConnectionPanel DoddlePanel) {

		doddles=DoddlePanel;
		
		if (doddles.firstTimeSetupNeeded) {
			doddles.firstTimeSetUp();
		}

		MakeSpiffyConnection(source, ConnectionSide.Auto, destination,
				ConnectionSide.Auto);
		
		refreshPath();

	}


	
	public ConnectionPoint getDestSide(ConnectionSide side) {

		for (ConnectionPoint cside : destinationSides) {
			if (cside.side == side) {
				return cside;
			}

		}
		return null;

	}


	private ArrayList<ConnectionPoint> getSides(Widget source) {
		ArrayList<ConnectionPoint> allsides = new ArrayList<ConnectionPoint>();

		// get all 4 sides
		// top
		allsides.add(new ConnectionPoint(source, ConnectionSide.Top, source
				.getOffsetWidth() / 2, 0));
		// bottom
		allsides.add(new ConnectionPoint(source, ConnectionSide.Bottom, source
				.getOffsetWidth() / 2, source.getOffsetHeight()));
		// left
		allsides.add(new ConnectionPoint(source, ConnectionSide.Left, 0, source
				.getOffsetHeight() / 2));
		// right
		allsides.add(new ConnectionPoint(source, ConnectionSide.Right, source
				.getOffsetWidth(), source.getOffsetHeight() / 2));

		return allsides;
	}

	public ConnectionPoint getSourceSide(ConnectionSide side) {

		for (ConnectionPoint cside : sourceSides) {
			if (cside.side == side) {
				return cside;
			}

		}
		return null;

	}

	private void MakeCurveBetween(ConnectionPoint chosenStart,
			ConnectionPoint chosenEnd) {

		// first we go out by the margin
		int mx = chosenStart.getOffsetX();
		int my = chosenStart.getOffsetY();

		int mex = chosenEnd.getOffsetX();
		int mey = chosenEnd.getOffsetY();

		// get the start/end data and then make the svg string
		int cx = (mx + mex) / 2;
		int cy = (my + mey) / 2;

		String svgPath = "<path id=\"curveAB\" d=\"M" + chosenStart.getX()
				+ "," + chosenStart.getY() + " S " + mx + "," + my + " " + cx
				+ "," + cy + " " + mex + "," + mey + " " + chosenEnd.getX()
				+ "," + chosenEnd.getY()
				+ " \" stroke=\"blue\" stroke-width=\"3\" fill=\"none\" />";
		if ((currentStyle == ConnectionStyle.Start) ||(currentStyle == ConnectionStyle.ArrowsBothEnd) ){
			//add arrow to path
			svgPath = addArrowToConnector(chosenStart, svgPath,"blue");
						
		}
		if ((currentStyle == ConnectionStyle.End) ||(currentStyle == ConnectionStyle.ArrowsBothEnd) ){
			//add arrow to path
			svgPath = addArrowToConnector(chosenEnd, svgPath,"blue");
						
		}

		// save it to the main paths, which in turn updates the doddle
		doddles.addPathToDoddle(this, svgPath);

	}

	private void MakeLineBetween(ConnectionPoint chosenStart,
			ConnectionPoint chosenEnd) {
		// get the start/end data and then make the svg string

		// first we go out by the margin
		int mx = chosenStart.getOffsetX();
		int my = chosenStart.getOffsetY();

		int mex = chosenEnd.getOffsetX();
		int mey = chosenEnd.getOffsetY();

		String svgPath = "<path id=\"lineAB\" d=\"M" + chosenStart.getX() + " "
				+ chosenStart.getY() + " L" + mx + "," + my + " L" + mex + ","
				+ mey + " L" + chosenEnd.getX() + "," + chosenEnd.getY()
				+ " \" stroke=\"red\" stroke-width=\"3\" fill=\"none\" />";
		
		if ((currentStyle == ConnectionStyle.Start) ||(currentStyle == ConnectionStyle.ArrowsBothEnd) ){
			//add arrow to path
			svgPath = addArrowToConnector(chosenStart, svgPath,"red");
						
		}
		if ((currentStyle == ConnectionStyle.End) ||(currentStyle == ConnectionStyle.ArrowsBothEnd) ){
			//add arrow to path
			svgPath = addArrowToConnector(chosenEnd, svgPath,"red");
						
		}


		
		// save it to the main paths, which in turn updates the doddle
		doddles.addPathToDoddle(this, svgPath);

	}

	private String addArrowToConnector(ConnectionPoint chosenStart, String svgPath,String col) {
		svgPath=svgPath+"<path id=\"arrowHead\" d=\"M "
					+ chosenStart.getX() + " "+ chosenStart.getY()						
					+ " L" + chosenStart.getArrowPoint1X() + "," + chosenStart.getArrowPoint1Y()
					+ " M" + chosenStart.getX() + " "+ chosenStart.getY() 
					+ " L" +chosenStart.getArrowPoint2X() + "," + chosenStart.getArrowPoint2Y()
					+ " \" stroke=\""+col+"\" stroke-width=\"3\" fill=\"none\" />";;
		return svgPath;
	}

	public void removeLink() {
		doddles.removePathFromDoddle(this);
	}

	private void MakeSpiffyConnection(Widget source, ConnectionSide startSide,
			Widget destination, ConnectionSide endSide) {
		
		// store all the possible sides of each widget
		sourceSides = getSides(source);
		destinationSides = getSides(destination);
		
		currentStartSide = startSide;
		currentEndSide = endSide;
		
		//get middle of objects
		middleOfSourceObject = new ConnectionPoint(source, ConnectionSide.MiddleOfObject,
				source.getOffsetWidth() / 2, 
				source.getOffsetHeight() / 2);
		
		middleOfDestObject = new ConnectionPoint(destination, ConnectionSide.MiddleOfObject,
				destination.getOffsetWidth() / 2, 
				destination.getOffsetHeight() / 2);

		// work out any sides set to auto
		workOutAutoConnectionPoints();
		

		refreshPath();

	}

	private void workOutAutoConnectionPoints() {
		ArrayList<ConnectionPoint> possibleStarts;
		ArrayList<ConnectionPoint> possibleEnds;

		if (currentStartSide == ConnectionSide.Auto) {
			possibleStarts = sourceSides;
			
		} else if (currentStartSide == ConnectionSide.MiddleOfObject){
			
			possibleStarts = new ArrayList<ConnectionPoint>();	
			possibleStarts.add(middleOfSourceObject);
			
			
		} else {
			possibleStarts = new ArrayList<ConnectionPoint>();
			possibleStarts.add(getSourceSide(currentStartSide));

		}

		if (currentEndSide == ConnectionSide.Auto) {

			possibleEnds = destinationSides;
		} else if (currentEndSide == ConnectionSide.MiddleOfObject){
			
			possibleEnds = new ArrayList<ConnectionPoint>();	
			possibleEnds.add(middleOfDestObject);
			
			
		}else {
			possibleEnds = new ArrayList<ConnectionPoint>();
			possibleEnds.add(getDestSide(currentEndSide));

		}
		// Log.info("got sides to connect, now connecting...");

		//loop over all starts
		int HighestDis=990000; //higher then the highest distance ever!
		int cdis=0;
		
		for (ConnectionPoint connectionPointStart : possibleStarts) {
			
			//loop over all ends
			for (ConnectionPoint connectionPointEnd : possibleEnds) {
				
				cdis = (int) Math.hypot(connectionPointEnd.getX()-connectionPointStart.getX(), 
								  connectionPointEnd.getY()-connectionPointStart.getY());
				
				//System.out.println(cdis);
				
				if (cdis<HighestDis){
					HighestDis=cdis;
					//System.out.println("setting start points"+connectionPointStart.side.toString());
					
					
					
				//if shortest distance, then use them
				ChosenStart = connectionPointStart;
				ChosenEnd = connectionPointEnd;
				}
				
			}
		}
	}

	public void setToCurve() {

		currentType = ConnectionType.Curve;

		// make drawing based on type
		refreshPath();
	}
	
	public void setToLine() {

		currentType = ConnectionType.Line;

		// make drawing based on type
		refreshPath();
	}
	
	public void clearAllPaths(){
		
		doddles.alllines.clear();
		
	}

	/** if the objects move, this should be triggered **/
	public void refreshPath() {

		// work out any sides set to auto
		workOutAutoConnectionPoints();
		
		
		if (currentType == ConnectionType.Line) {
			MakeLineBetween(ChosenStart, ChosenEnd);
		}

		if (currentType == ConnectionType.Curve) {
			MakeCurveBetween(ChosenStart, ChosenEnd);
		}
		

	}

}
