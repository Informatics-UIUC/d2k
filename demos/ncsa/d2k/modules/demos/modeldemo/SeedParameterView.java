package ncsa.d2k.modules.demos.modeldemo;

import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;

import ncsa.d2k.infrastructure.modules.*;

import java.awt.*;

/**
	This is the UserView that is used by SeedParameterInput.
*/
public class SeedParameterView extends UserInputPane {

	/**
		These are the DSComponents that are used.  The argument to the constructor
		of each DSComponent is the value that is used to identify it in the fieldMap 
		of SeedParameterInput.
	*/
	DSTextField light = new DSTextField("light");
	DSTextField moisture = new DSTextField("moisture");
	DSTextField soil = new DSTextField("soil");
	DSStringChoice result = new DSStringChoice("result");

	public void initView(ViewModule mod) {
		super.initView(mod);
		
		Panel p = new Panel();
		
		p.setLayout(new GridLayout(4, 2));
		p.add(new Label("Light"));
		p.add(light);
		p.add(new Label("Moisture"));
		p.add(moisture);
		p.add(new Label("Soil"));
		p.add(soil);
		p.add(new Label("Result"));
		
		// result is a DSStringChoice, so we need to add the items here.
		result.add("success");
		result.add("failure");
		p.add(result);
		add(p);
	}



	/**
		Place all the components on a Panel and then add the Panel to me.
	*/
	/*public SeedParameterView() {
		super();
		
		Panel p = new Panel();
		
		p.setLayout(new GridLayout(4, 2));
		p.add(new Label("Light"));
		p.add(light);
		p.add(new Label("Moisture"));
		p.add(moisture);
		p.add(new Label("Soil"));
		p.add(soil);
		p.add(new Label("Result"));
		
		// result is a DSStringChoice, so we need to add the items here.
		result.add("success");
		result.add("failure");
		p.add(result);
		add(p);
	}*(/

	/**
		Not used.
	*/
	public void setInput(Object input, int index) {
	}
}
