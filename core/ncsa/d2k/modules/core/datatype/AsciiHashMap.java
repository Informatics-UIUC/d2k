//package ncsa.util;
package ncsa.d2k.modules.core.datatype;

import java.util.Enumeration;

/**
	AsciiHashMap is an extension of Hashtable which borrows some Column functionality
(sorry no multiple inheritnce)
*/
public class AsciiHashMap extends java.util.Hashtable implements java.io.Serializable
	{
	String label = null;
	String comment = null;

	public AsciiHashMap(StaticDocument sd)
		{
		super();
        int numrows = sd.getNumRows();
        for(int r=0;r<numrows;r++)
            {
            this.put( new String(sd.getBytes(r,0)), new String(sd.getBytes(r,1)) );
            }
		}

	//////////////////////////////////////
	//// Accessing Metadata
	/**
		get the label associated with this Column
		@return the label which describes this Column
	*/
	public String getLabel( ) { return label; }

	/**
		set the label associated with this Column
		@param labl the label which describes this Column
	*/
	public void setLabel( String labl ) { label = labl; }

	/**
		get the comment associated with this Column
		@return the comment which describes this Column
	*/
	public String getComment( ) { return comment; }

	/**
		set the comment associated with this Column
		@param commnt the comment which describes this Column
	*/
	public void setComment( String commnt ) { comment = commnt; }

/**
		get the number of entries this Column holds
		@return this Column's # of entries
	abstract public int getNumRows( );

	/**
		get the capacity of this Column, it's potential maximum # of entries
		@return the max # of entries this Column can hold
	abstract public int getCapacity( );

	/**
		suggests a new capacity for this Column.  If the Column implementation supports
		capacity than the suggestion may be followed. The capacity is it's potential
		max # of entries.  If numEntries > newCapacity then Column may be truncated.
		@param newCapacity a new capacity
	abstract public void suggestCapacity(int newCapacity);
*/
	//////////////////////////////////////

	public void print()
		{
        Enumeration keysEnum = this.keys ();
        while (keysEnum.hasMoreElements ())
			{
			String kstr = (String) keysEnum.nextElement();
			System.out.println( kstr + " , " + this.get(kstr) );
			}
		}
	}/*AsciiHashMap*/



