package ncsa.d2k.modules.demos.lifedemo;


import ncsa.d2k.core.modules.ComputeModule;

/**
	Whenever an input arrives, push it.
*/
public class LifeDispatchModule extends ComputeModule  {

	/**
		Provide a description of this module.
		@return A description of this module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module creates the initial game board.  </body></html>";
	}

	/**
		Get the name of the module.
		@return The name of the module
	*/
	public String getModuleName() {
		return "Dispatch";
	}

	/**
		Return an array containing the input types to this module.
		@return The input types.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.demos.lifedemo.LifeGameBoard","ncsa.d2k.modules.demos.lifedemo.LifeGameBoard"};
		return types;
	}

	/**
		Return an array containing the output types of this module.
		@return The output types.
	*/
	public String[] getOutputTypes() {
		String[] types = {"ncsa.d2k.modules.demos.lifedemo.LifeGameBoard"};
		return types;
	}

	/**
		Return the info for a particular input.
		@param i The index of the input to get info about
		@return The info about the input
	*/
	public String getInputInfo(int i) {
		switch (i) {
			case 0: return "A game board.";
			case 1: return "A game board.";
			default: return "No such input";
		}
	}

	/**
		Return the name a particular input.
		@param i The index of the input
		@return The input name
	*/
	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "board1";
			case 1:
				return "board2";
			default: return "NO SUCH INPUT!";
		}
	}


	/**
		Return the info for a particular output.
		@param i The index of the output to get info about
		@return The info about the output
	*/
	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The game board.";
			default: return "No such output";
		}
	}

	/**
		Return the name of a particular output.
		@param i The index of the output
		@return The name of the output
	*/
	public String getOutputName(int i) {
		switch(i) {
			case 0:
				return "board";
			default: return "NO SUCH OUTPUT!";
		}
	}

	public boolean isReady() {
		return inputFlags[0] > 0 || inputFlags[1] > 0;
	}

	public void doit() {
		LifeGameBoard brd;

		if(inputFlags[0] > 0)
			brd = (LifeGameBoard)this.pullInput(0);
		else
			brd = (LifeGameBoard)this.pullInput(1);

		this.pushOutput(brd, 0);
	}
}
