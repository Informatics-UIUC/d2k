package ncsa.d2k.modules.demos.lifedemo;


import ncsa.d2k.core.modules.ComputeModule;

/**
	Compute the next generation of the game.
*/
public class ComputeNextGeneration extends ComputeModule  {

	/**
		Provide a description of this module.
		@return A description of this module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module computes the next game board.  </body></html>";
	}

	/**
		Provide a name for this module.
		@return The name of this module.
	*/
	public String getModuleName() {
		return "computeNextGen";
	}

	/**
		Return an array containing the input types to this module.
		@return The input types.
	*/
	public String[] getInputTypes() {
		String[] types = {"ncsa.d2k.modules.demos.lifedemo.LifeGameBoard"};
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
			case 0: return "The current game board.";
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
			case 0:
				return "currentBoard";
			default: return "NO SUCH INPUT!";
		}
	}

	/**
		Return the info for a particular output.
		@param i The index of the output
		@return The info about the output
	*/
	public String getOutputInfo(int i) {
		switch (i) {
			case 0: return "The next game board.";
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
				return "nextBoard";
			default: return "NO SUCH OUTPUT!";
		}
	}

	LifeGameBoard brd =null;
	int cellsCount[][];
	int w = 0;
	int h = 0;

	public void doit() {
		brd = (LifeGameBoard)this.pullInput(0);
		w = brd.width;
		h = brd.height;
		cellsCount = new int[w][h];
		LifeGameBoard b = createNextGeneration();
		this.pushOutput(b, 0);
	}

	protected LifeGameBoard createNextGeneration() {
		boolean[][] nextGen = new boolean[w][h];

		int count = 0;

		for(int i = 0; i < w; i++) {
			for(int j = 0; j < h; j++) {
				count = countNeighbours(i, j);
				cellsCount[i][j] = count;
			}
		}

		for(int x = 0; x < w; x++) {
			for(int y = 0; y < h; y++) {
				switch(cellsCount[x][y]) {
					// with two neighbors, the state stays the same
					case 2:
						nextGen[x][y] = brd.board[x][y];
						break;

					case 3:
						// if there were 3 neighbors and the cell
						// was alive, it will die from overcrowding
						if(brd.board[x][y])
							nextGen[x][y] = false;
						// otherwise the cell will spring to life
						// with 3 neighbors
						else
							nextGen[x][y] = true;
						break;
					default:
						nextGen[x][y] = false;
						break;
				}
			}
		}

		LifeGameBoard b = new LifeGameBoard();
		b.board = nextGen;
		b.width = w;
		b.height = h;
		return b;
	}

	protected int countNeighbours(int r, int c) {
		int tally = 0;

		for(int i = r-1; i <= r+1; i++) {
			for(int j = c-1; j <= c+1; j++) {

				if( (i >= 0) && (j >= 0) && (i < h) && (j < w) && !((i == r) && (j == c))) {
					if(brd.board[i][j])
						tally++;
				}
			}
		}
		return tally;
	}
}
