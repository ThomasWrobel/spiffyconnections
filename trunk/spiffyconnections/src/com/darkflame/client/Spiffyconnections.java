package com.darkflame.client;

import com.darkflame.client.SpiffyConnection.ConnectionSide;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		Label meep = new Label ("meep");
		
		Label co = new Label ("co");

		Label la = new Label ("la");
		
		RootPanel.get().add(meep,100,100);
		RootPanel.get().add(co,644,444);
		RootPanel.get().add(la,344,344);
		
		SpiffyConnection meepco = new SpiffyConnection(meep,ConnectionSide.Bottom, co,ConnectionSide.Top);
		SpiffyConnection meepco2 = new SpiffyConnection(la,ConnectionSide.Right, co,ConnectionSide.Right);
		meepco2.setToCurve();
		


		RootPanel.get().add(SpiffyConnection.doddles,0,0);
		
		
		
		
	}
}
