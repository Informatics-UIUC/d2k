package ncsa.d2k.modules.demos.lifedemo;

/**
	This is the game of life board, posessing a boolean for each cell with a 
	true if the cell has life, and a false otherwise.
*/
public class LifeGameBoard {

	public boolean[][] board;
	public int width;
	public int height;
	
	/**
		This method will return true when there is no live
		on the board, false otherwise.
		@return true when there is no liveon the board, false otherwise.
	*/
	public boolean isGameOver () {
		for (int i = 0 ; i < board.length ; i++)
			for (int j = 0 ; j < board[0].length ; j++)
				if (board[i][j] == true)
					return false;
		return true;
	}
}
