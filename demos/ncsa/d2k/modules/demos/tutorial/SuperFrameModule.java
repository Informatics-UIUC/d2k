package ncsa.d2k.modules.demos.tutorial;

import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.*;
import ncsa.gui.Constrain;
import java.awt.*;

public class SuperFrameModule extends ViewModule  {
		
	private String [] fieldMap = {"result string"};
	protected String[] getFieldNameMapping () {
		return fieldMap;
	}

	public String [] getInputTypes () {
		String[] types = {"java.lang.String","java.lang.String","java.lang.String"};
		return types;
	}

	public String [] getOutputTypes () {
		String[] types = {"java.lang.String"};
		return types;
	}
	
	/**
		Default constructor simply sets up the input and output types.
	*/
	public SuperFrameModule() {
		super ();
	}
	
	/**
		Return a description of this module.
		
		@returns a text description of the modules function
	*/
	public String getModuleInfo () {
		return "<html>  <head>      </head>  <body>    This module allows the user to select one from the three strings passed     in. A List user interface component is used to display the inputs, the     user selects one of the inputs from the list.  </body></html>";
	}
	
	/**
		This method will return a text description of the the input indexed by
		the value passed in.
		
		@param inputIndex the index of the input we want the description of.
		@returns a text description of the indexed input
	*/
	public String getInputInfo (int index) {
		switch (index) {
			case 0: return "This is the first of three string to chose from.";
			case 1: return "This is the second of three string to chose from.";
			case 2: return "This is the third of three string to chose from.";
			default: return "No such input";
		}
	}
	
	/**
		This method will return a text description of the the output indexed by
		the value passed in.
		
		@param index the index of the output we want the description of.
		@returns a text description of the indexed output
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			case 0: return "This output is the string that the user selected from those passed in.";
			default: return "No such output";
		}
	}
	
	/**
		Create a new instance of a UserView object that provides the 
		user interface for this user interaction module.
		
		@returns a new instance of a UserView providing the interface
			 for this module.
	*/
	protected UserView createUserView () {
		return new SelectStringView ();
	}
	
	/**
		Return the window containing the embedded views.
	*/
	public Window getWindow( )
		{
		Frame frame = new Frame( );
		frame.setLayout( new GridBagLayout( ));
		
		EmbeddedPane ep = new EmbeddedPane( "north", new Dimension( 300, 200 ));
		ep.setBackground (Color.red);
		Constrain.setConstraints( frame, ep,
			0, 0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1.0, 0.0);
			
		ep = new EmbeddedPane( "center", new Dimension( 300, 200 ));
		ep.setBackground (Color.blue);
		Constrain.setConstraints( frame, ep,
			0, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1.0, 0.0);
			
		ep = new EmbeddedPane( "south", new Dimension( 300, 200 ));
		ep.setBackground (Color.green);
		Constrain.setConstraints( frame, ep,
			0, 2, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1.0, 0.0);
		frame.pack ();
		return frame;
		}
		
	/**
		Get the window name.
	*/
	public String getWindowName( )
		{
		return "Kaakaa";
		}
	/**
		Get the window name.
	*/
	public String getViewName( )
		{
		return "north";
		}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "SuperFrameModule";
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "input0";
			case 1:
				return "input1";
			case 2:
				return "input2";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "output0";
			default: return "NO SUCH OUTPUT!";
		}
	}
}
