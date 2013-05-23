package com.darkflame.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.mortbay.log.Log;

import com.darkflame.client.SpiffyConnection.ConnectionPoint;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class SpiffyConnection {

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
			return y + sourceWidget.getAbsoluteTop();
		}

		public int getX() {
			return x + sourceWidget.getAbsoluteLeft();
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

	enum ConnectionSide {
		Top, Bottom, Left, Right, Auto
	}

	enum ConnectionType {
		Line, Curve
	}

	enum ConnectionStyle {
		Plane, ArrowsBothEnd, Start,End
	}

	ConnectionStyle currentStyle = ConnectionStyle.ArrowsBothEnd;
	ConnectionType currentType = ConnectionType.Line;
	ConnectionSide currentStartSide = ConnectionSide.Auto;
	ConnectionSide currentEndSide = ConnectionSide.Auto;
	
	
	ConnectionPoint ChosenStart = null;
	ConnectionPoint ChosenEnd = null;

	// base div for drawing
	static HTMLPanel doddles;
	// all lines
	static HashMap<SpiffyConnection, String> alllines = new HashMap<SpiffyConnection, String>();

	static private void removePathFromDoddle(SpiffyConnection spiffyConnection) {

		alllines.remove(spiffyConnection);


	}

	static private void addPathToDoddle(SpiffyConnection sc, String svgPath) {

		alllines.put(sc, svgPath);


	}

	public static void refreshLines() {
		// refresh lines
		String allPaths = "";

		Iterator<String> lit = alllines.values().iterator();
		while (lit.hasNext()) {

			String path = lit.next();
			allPaths = allPaths + path;

		}
		
		//System.out.println(allPaths);
		
		doddles.getElement().setInnerHTML("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">"
				+ allPaths + "</svg>test4");
		
		doddles.setSize("100%", "100%");
	}

	ArrayList<ConnectionPoint> sourceSides = new ArrayList<ConnectionPoint>();

	ArrayList<ConnectionPoint> destinationSides = new ArrayList<ConnectionPoint>();

	public SpiffyConnection(Widget source, ConnectionSide point,
			Widget destination, ConnectionSide point2) {
		if (doddles == null) {
			firstTimeSetUp();
		}

		MakeSpiffyConnection(source, point, destination, point2);

	}

	public SpiffyConnection(Widget source, Widget destination) {

		if (doddles == null) {
			firstTimeSetUp();
		}

		MakeSpiffyConnection(source, ConnectionSide.Auto, destination,
				ConnectionSide.Auto);

	}

	private void firstTimeSetUp() {

		doddles = new HTMLPanel("<svg></svg>test");
		doddles.setSize("100%", "100%");

	}

	public ConnectionPoint getDestSide(ConnectionSide side) {

		for (ConnectionPoint cside : destinationSides) {
			if (cside.side == side) {
				return cside;
			}

		}
		return null;

	}

	public HTMLPanel getDoddlePlane() {
		return doddles;
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
		addPathToDoddle(this, svgPath);

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
		addPathToDoddle(this, svgPath);

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
		removePathFromDoddle(this);
	}

	private void MakeSpiffyConnection(Widget source, ConnectionSide startSide,
			Widget destination, ConnectionSide endSide) {
		// store all the possible sides of each widget
		sourceSides = getSides(source);
		destinationSides = getSides(destination);
		
		currentStartSide = startSide;
		currentEndSide = endSide;
		

		// work out any sides set to auto
		workOutAutoConnectionPoints();
		

		refreshPath();

	}

	private void workOutAutoConnectionPoints() {
		ArrayList<ConnectionPoint> possibleStarts;
		ArrayList<ConnectionPoint> possibleEnds;

		if (currentStartSide == ConnectionSide.Auto) {
			possibleStarts = sourceSides;
		} else {
			possibleStarts = new ArrayList<ConnectionPoint>();
			possibleStarts.add(getSourceSide(currentStartSide));

		}

		if (currentEndSide == ConnectionSide.Auto) {

			possibleEnds = destinationSides;
		} else {
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
