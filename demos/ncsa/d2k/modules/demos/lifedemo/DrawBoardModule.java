package ncsa.d2k.modules.demos.lifedemo;


import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import ncsa.d2k.core.modules.UIModule;
import ncsa.d2k.core.modules.ViewModule;
import ncsa.d2k.userviews.*;
import ncsa.d2k.core.modules.UserView;
import ncsa.d2k.userviews.listeners.DSCancelInputListener;

/**
	DrawBoardModule draws the board in a UserView.  The board is presented as a grid with
	red circles showing which cells are alive.  The user can click the mouse on a cell
	to toggle it on or off.

	TODO: There are still some bugs associated with the painting and toggling of cells
	on the grid.
*/
public class DrawBoardModule extends UIModule  {

	/**
		Provide a description of this module.
		@return A description of this module.
	*/
	public String getModuleInfo() {
		return "<html>  <head>      </head>  <body>    This module draws the life board.  </body></html>";
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
			case 0: return "The next game board.";
			default: return "No such input";
		}
	}

	/**
		Return the name of a particular input.
		@param i The index of the input
		@return The name of the input
	*/
	public String getInputName(int i) {
		switch(i) {
			case 0:
				return "nextBoard";
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
			case 0: return "The current game board.";
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
				return "currentBoard";
			default: return "NO SUCH OUTPUT!";
		}
	}

	/**
		Get the name of the module.
		@return The name of the module.
	*/
	public String getModuleName() {
		return "DrawBoard";
	}

	final String BOARD_OUTPUT = "board";

	/**
		Get the field name map for this module-view combination.
		@return The field name map.
	*/
	protected String[] getFieldNameMapping() {
		String [] tmp = {BOARD_OUTPUT};
		return tmp;
	}

	/**
		Return the UserView.
		@return The UserView that we show.
	*/
	protected UserView createUserView() {
		return new LifeGameView();
	}

	/**
		This UserView shows the game board with the cells on it.  The graphical
		component of the game board is implemented in the inner class GridCanvas.
	*/
	protected class LifeGameView extends PersistInputPane implements ActionListener {

		/** The module that owns this view */
		DrawBoardModule parentModule = null;
		/** The board */
		LifeGameBoard board = null;
		/** The canvas that the grid is painted on */
		GridCanvas grid = null;
		/** Automatic button */
		Button auto = new Button ("Automatic");
		/** Are we currently automatic? */
		boolean automatic = false;

		/**
			Initialize the view.
			@param m The ViewModule that is associated with this view
		*/
		public void initView(ViewModule m) {

			// the superclass will create the button panel at the bottom
			// and put the buttons in there,
			super.initView (m);
			parentModule = (DrawBoardModule)m;

			// create the grid and give it a default size
			grid = new GridCanvas();
			grid.setSize(300, 300);

			// By default the button panel is a flow layout.
			buttonPanel.add(auto);

			// this component is the action listener for the buttons
			auto.addActionListener(this);

			// add the grid and button panel
			add(grid, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.SOUTH);

			// Done button not needed,
			buttonPanel.remove (okButton);
		}

		/**
			This method is called when inputs arrive to the module.
			@param o The input Object
			@param idx The index of the input
		*/
		public void setInput(Object o, int idx) {
			board = (LifeGameBoard)o;

			// set the grid's current data to be the data that was just
			// received
			grid.setCurrentData(board);

			// repaint the grid
			grid.repaint();

			// If the game is over, turn off automatic mode.
			if (board.isGameOver ())
				automatic = false;
			else
				if(automatic)
					parentModule.viewContinue ("continue");
		}

		/**
			Listen for ActionEvents on the Buttons.
			@param e The ActionEvent
		*/
		public void actionPerformed(ActionEvent e) {
			// Continue was pressed.
			if(e.getSource() == auto) {
				if(!automatic)
					parentModule.viewContinue ("continue");

				automatic = !automatic;
			}
		}

		/**
			Shows the grid and the cells in the grid.  A white background is used.
			The grid rows and columns are shown using black lines.  The alive cells
			are represented by filled red ovals.
		*/
		class GridCanvas extends Canvas implements AddField {
			/** The board that this grid represents */
			LifeGameBoard currentData = null;
			/** The width of the board */
			int w;
			/** The height of the board */
			int h;

			/** The width of this Canvas */
			int myWidth;
			/** The height of this Canvas */
			int myHeight;

			/** The width of a single cell in the grid */
			int cellWidth;
			/** The height of a single cell in the grid */
			int cellHeight;

			/** The offscreen image used for double-buffering */
			Image offScreenBuffer;

			/**
				Construct a new GridCanvas
			*/
			public GridCanvas() {
				enableEvents(AWTEvent.MOUSE_EVENT_MASK);
				enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
			}

			/**
				This method will push the output, which is the board object onto
				the stack by the name board.

				@param hash the hashtable containing the results in this case the board.
			*/
			public Hashtable addFields( Hashtable message) {
				message.put (BOARD_OUTPUT, currentData);
				return message;
			}

			/**
				Repaint this component.  This just calls the update() method.
			*/
			public void repaint() {
				update(getGraphics());
			}

			/**
				Paint the grid and cells.  We are actually painting onto an
				offscreen image.

				@param g The Graphics object of the offscreen image

				TODO: Make this method more efficient.  Currently it erases the
				current grid and paints an entirely new grid, without taking the
				previous generation into consideration.  We shouldn't be painting
				*everything* over each time paint is called.
			*/
			public void paint(Graphics g) {
				if(currentData == null)
					return;

				// clear out all the old junk
				g.clearRect(0, 0, getSize().width, getSize().height);

				// draw the lines of the grid
				g.setColor(Color.black);
				for(int i = 0; i < myWidth; i+=cellWidth)
					g.drawLine(i, 0, i, myHeight);
				for(int i = 0; i < myHeight; i+=cellHeight)
					g.drawLine(0, i, myWidth, i);

				// draw the cells that are alive
				g.setColor(Color.red);
				for(int i = 0; i < w; i++) {
					for(int j = 0; j < h; j++) {
						if(currentData.board[i][j]) {
							int x = i*cellWidth;
							int y = j*cellHeight;
							g.fillRect(x+1, y+1, cellWidth-1, cellHeight-1);
						}
					}
				}
			}

			/**
				Paint the offscreen image onto this component.  This is called
				double buffering, and is used to reduce the amout of flash
				visible when repainting.
				@param g The Graphics object for this component
			*/
			public void update(Graphics g) {
				if(g != null) {
					Graphics gr;

					while(offScreenBuffer == null || (!(offScreenBuffer.getWidth(this) == getSize().width)
						&& offScreenBuffer.getHeight(this) == getSize().height) )
						offScreenBuffer = this.createImage(getSize().width, getSize().height);

					gr = offScreenBuffer.getGraphics();

					paint(gr);

					g.drawImage(offScreenBuffer, 0, 0, this);
				}
			}

			public void setCurrentData(LifeGameBoard b) {
				currentData = b;
				w = b.width;
				h = b.height;
				cellWidth = (int)myWidth/w;
				cellHeight = (int)myHeight/h;
			}


			/**
				Set the bounds of this component
				@param x The x location
				@param y The y location
				@param wi The new width
				@param he The new height
			*/
			public void setBounds(int x, int y, int wi, int he) {
				super.setBounds(x, y, wi, he);
				myWidth = wi;
				myHeight = he;

				if(w != 0)
					cellWidth = (int)myWidth/w;
				if(h != 0)
					cellHeight = (int)myHeight/h;

				offScreenBuffer = null;
			}

			boolean pressed = false;
			Point lastDrag=new Point(-1, -1);

			/**
				Process any MouseEvents that occur over this component.  We are
				actually only interested in the MOUSE_PRESSED and
				MOUSE_RELEASED events.
				@param e The MouseEvent to handle.
			*/
			public void processMouseEvent(MouseEvent e) {
				// if the mouse was pressed, we toggle the cell that the
				// mouse was pressed over

				if(e.getID() == MouseEvent.MOUSE_PRESSED)
					pressed = true;

				// if the mouse was released, we set the pressed variable to false
				if(e.getID() == MouseEvent.MOUSE_RELEASED) {
					int x = e.getX();
					int y = e.getY();
					currentData.board[x/cellWidth][y/cellHeight] =
						!currentData.board[x/cellWidth][y/cellHeight];

					pressed = false;
					repaint();
				}
			}

			/**
				Process any MouseMotionEvents that occur over this component.
				We are mainly interested in the MOUSE_DRAGGED event.
				@param e The MouseEvent to handle.
			*/
			public void processMouseMotionEvent(MouseEvent e) {
				// if the mouse is dragged, and the mouse button is pressed,
				// toggle the cell underneath the cursor.
				if( (e.getID() == MouseEvent.MOUSE_DRAGGED) && pressed ) {

					int x = e.getX();
					int y = e.getY();

					if( (x/cellWidth < w) && (y/cellHeight < h) && (x/cellWidth > -1) && (y/cellHeight > -1) ) {
						currentData.board[x/cellWidth][y/cellHeight] =
							!currentData.board[x/cellWidth][y/cellHeight];
					}
					repaint();
					if(e.getID() == MouseEvent.MOUSE_RELEASED)
						pressed = false;
				}
			}
		}
	}
}
