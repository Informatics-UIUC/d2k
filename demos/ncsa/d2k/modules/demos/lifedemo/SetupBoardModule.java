package ncsa.d2k.modules.demos.lifedemo;


import ncsa.d2k.core.modules.ComputeModule;

/**
	Create the initial LifeGameBoard.
*/
public class SetupBoardModule extends ComputeModule {

	/**
		Provide a description of this module.
		@return A description of this module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module creates the initial game board.  </body></html>";
	}

	/**
		Provide a name for this module.
		@return The name of this module.
	*/
	public String getModuleName() {
		return "setupBoard";
	}


	/**
		Return an array containing the input types to this module.
		@return The input types.
	*/
	public String[] getInputTypes() {
		String[] types = {"java.lang.String","java.lang.String"};
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
			case 0: return "The width of the game board.";
			case 1: return "The height of the game board.";
			default: return "No such input";
		}
	}

	/**
		Return the name for a particular input.
		@param i The index of the input
		@return The name of the input
	*/
	public String getInputName(int i) {
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
		Return the info for a particular output.
		@param i The index of the output to get info about
	*/
	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The game board.";
			default: return "No such output";
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
				return "board";
			default:
				return "No such output!";
		}
	}

	public void doit() {
		String w = (String)this.pullInput(0);
		String h = (String)this.pullInput(1);

		int width = Integer.valueOf(w).intValue();
		int height = Integer.valueOf(h).intValue();

		LifeGameBoard brd = new LifeGameBoard();
		brd.board = new boolean[width][height];
		brd.width = width;
		brd.height = height;

		// Randomly populate the board about 20 percent populated
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++)
				brd.board[i][j] = Math.random () < .2;
		}

		this.pushOutput(brd, 0);
	}
}
