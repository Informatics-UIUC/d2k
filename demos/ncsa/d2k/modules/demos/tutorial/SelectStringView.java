package ncsa.d2k.modules.demos.tutorial;
import ncsa.d2k.userviews.*;
import ncsa.d2k.userviews.widgets.*;

import java.awt.*;
import java.util.Hashtable;

public class SelectStringView extends UserInputPane
	{
	DSStringChoice list = new DSStringChoice("result string");
	public SelectStringView ( )
		{
		super(	);
		this.add( "Center", list );
		}

	public void setInput( Object input, int index )
		{
		list.add (( String ) input );
		list.setSize( list.getPreferredSize( ));
		}
	}
