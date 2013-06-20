package com.darkflame.client;

import com.darkflame.client.SpiffyConnection.ConnectionSide;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Spiffyconnections implements EntryPoint {

	Label meep = new Label ("meep");
	
	Label co = new Label ("co");

	Label la = new Label ("la");
	
	public void onModuleLoad() {
		
		RootPanel.get().add(meep,100,100);
		RootPanel.get().add(co,644,444);
		RootPanel.get().add(la,544,344);
		
		final SpiffyConnection meepco = new SpiffyConnection(meep,ConnectionSide.Auto, co,ConnectionSide.Auto);
		final SpiffyConnection meepco2 = new SpiffyConnection(la,ConnectionSide.Auto, co,ConnectionSide.Auto);
		
		meepco2.setToCurve();
		meepco2.refreshPath();
		meepco.refreshPath();
		
		SpiffyConnection.refreshLines();
		
		
		Timer test = new Timer() {
			double t=0;
			int x=0;
			int y=0;
			@Override
			public void run() {
				
				t=t+0.1; 
				
				x=(int) (200.0*Math.sin(t));
				y=(int) (200.0*Math.cos(t));
				
				RootPanel.get().setWidgetPosition(co, 600+x, 400+y);
				
				meepco2.refreshPath();
				meepco.refreshPath();
				SpiffyConnection.refreshLines();
				
			}
		};
		test.scheduleRepeating(50);
		
		RootPanel.get().add(SpiffyConnection.doddles,0,0);
		
		
		
		
	}
}
