package ncsa.d2k.modules.demos.lifedemo;

import ncsa.d2k.infrastructure.modules.UIModule;
import ncsa.d2k.infrastructure.modules.ViewModule;
import ncsa.d2k.infrastructure.modules.HasNames;
import ncsa.d2k.infrastructure.views.UserView;

import ncsa.d2k.controller.userviews.*;
import ncsa.d2k.controller.userviews.widgits.*;

import java.awt.*;

/**
	Presents a simple GUI to allow the user to enter the length and width.
*/
public class GetBoundsModule extends UIModule implements HasNames {

	/**
		Provide a description of this module.
		@return A description of this module.	
	*/
	public String getModuleInfo() {
		return "This module presents an interface to input dimensions of"+ 
		"the life board.";
	}

	/**
		Return an array containing the input types to this module.
		@return The input types.
	*/
	public String[] getInputTypes() {
		return null;
	}
	
	/**
		Return an array containing the output types of this module.
		@return The output types.
	*/
	public String[] getOutputTypes() {
		String []out = {"java.lang.String", "java.lang.String"};
		return out;
	}

	/**
		Return the info for a particular input.
		@param i The index of the input to get info about
		@return The info about the input
	*/
	public String getInputInfo(int i) {
		return "No such input!";
	}

	/**
		Return the name for a particular input.
		@param i The index of the input
		@return The name of the input
	*/
	public String getInputName(int i) {
		return "No such input!";
	}
	
	/**
		Return the info for a particular output.
		@param i The index of the output to get info about
		@return The info about the output
	*/
	public String getOutputInfo(int i) {
		switch(i) {
			case(0):
				return "The width of the board.";
			case(1):
				return "The height of the board.";
			default:
				return "No such output!";
		}
	}

	/**
		Return the name for a particular output.
		@param i The index of the output
		@return The name of the output 
	*/
	public String getOutputName(int i) {
		switch(i) {
			case(0):
				return "width";
			case(1):
				return "height";
			default:
				return "No such output!";
		}
	}

	/**
		Get the name for this module.
		@return The name of this module
	*/
	public String getModuleName() {
		return "GetBounds";
	}
	
	/**
		Get the field name map for this module-view combination.
		@return The field name map.
	*/
	protected String[] getFieldNameMapping() {
		String[] fieldMap = {"tf0", "tf1"};
		return fieldMap;
	}
	
	/**
		Create the UserView object for this module-view combination.
		@return The UserView associated with this module.
	*/
	protected UserView createUserView() {
		return new GetBoundsView();
	}
	
	protected class GetBoundsView extends UserInputPane {
		
		DSTextField tf0 = new DSTextField("tf0");
		DSTextField tf1 = new DSTextField("tf1");
		
		public void initView(ViewModule m) {
			super.initView(m);
			Panel p = new Panel();
			p.setLayout(new GridLayout(2, 2));
			p.add(new Label("Width: "));
			p.add(tf0);
			p.add(new Label("Height: "));
			p.add(tf1);
			
			add(p);
		}
		
		public void setInput(Object o, int i) {
		}
	}
}
