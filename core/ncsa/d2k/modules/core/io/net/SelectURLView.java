package ncsa.d2k.modules.core.io.net;

import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;

import java.awt.Label;
import java.util.Hashtable;

public class SelectURLView extends UserInputPane  {
	DSTextField list = new DSTextField ("result string");
	public SelectURLView () {
		super ();
		this.add ("North", new Label ("Enter a URL:"));
		this.add ("Center", list);
	}
	
	public void setInput (Object obj, int i) {
	}
}
