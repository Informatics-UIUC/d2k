//package ncsa.util;
package ncsa.d2k.modules.core.datatype;

import java.io.*;

/**
 StaticDocument is a datastructure of m rows where each row has a variable
 length (# of bytes) and a variable # of columns.
 <br>
 It's internal representation is as a byte array with pointers to all the
 the delimiters and end-of-line's...
 <br>
 end-of-line marks the end of a row, columns in the row are delimited by
 the delimiter, so each column is a bytestring which can be obtained via
 get/set methods.
 <br>
 when a get/set methods is called (ie getString getDouble)
 it will attempt to convert the underlying data into the correct type,
 if this is impossible, Exceptions will occur... so it is up to the
 programmer to call appropriate methods...
 <br>
 */
public class StaticDocument implements Serializable {
    private byte[] internalBuf = null;
    private byte delimiter = (byte)' ';
    private byte eolByte = (byte)10;
    private final byte zero_byte = (byte)'0';
    ///////////////////////
    //// fields for interface methods

    /** the # of rows in this StaticDocument (may not be rowPtrs.length) */
    private int numRows = 0;
    ///////////////////////

    /** references all the rows in the buffer */
    private int rowPtrs[][] = null;

    /**
     This constructor will create a StaticDocument object
     with buffer.  The StaticDocument will have m rows delimited by
     the eol character and a variable # of columns delimited by a delimiter
     (where ' ' space is default delimiter).
     <br>
     It will carry buffer with it as it's internal representation.
     <br>
     If buffer is passed after a file read, make sure to
     remove any partial line that may be at the endI of buffer
     as StaticDocument will expect an eol at the endI of the buffer
     <br>
     fix this stuff
     numrows ... # of rows
     */
    public StaticDocument (byte[] buffer) {
        initStaticDocument(buffer);
    }

    /**
     extension of constructor above for specifying delimiter and eolByte
     */
    public StaticDocument (byte[] buffer, byte delimiter, byte eolByte) {
        // the delimiter between strings in the buffer
        this.delimiter = delimiter;
        // the endI-of-line byte between lines
        this.eolByte = eolByte;
        initStaticDocument(buffer);
    }

    /*StaticDocument constructor*/
    /**
     create internal rox,column pointers,
     use entire buffer
     considerations: an end of row will always end with the eolByte
     except the last row may have been an eof
     an end of col will always end with the delimiter
     except the last col in the row may have ended with an eolByte
     */
    public void initStaticDocument (byte[] buffer) {
        internalBuf = buffer;
        int bufStart = 0;
        //find start (past all initial eol's)
        for (int b = 0; b < buffer.length; b++)
            if (buffer[b] != eolByte) {
                bufStart = b;
                break;
            }
        //1st pass, count rows
        int nrows = 0;
        for (int b = bufStart; b < buffer.length; b++)
            if (buffer[b] == eolByte)
                nrows++;
        if (buffer[buffer.length - 1] != eolByte)
            nrows++;
        //2nd pass, count cols foreach row
        rowPtrs = new int[nrows][];
        int curow = 0, ncols = 1;
        for (int b = bufStart; b < buffer.length; b++) {
            if (buffer[b] == eolByte) {
                if (buffer[b - 1] != delimiter)
                    ncols++;
                rowPtrs[curow] = new int[ncols];
                curow++;
                ncols = 1;
            }
            else if (buffer[b] == delimiter)
                ncols++;
        }
        if (buffer[buffer.length - 1] != eolByte) {
            if (buffer[buffer.length - 1] != delimiter)
                ncols++;
            rowPtrs[curow] = new int[ncols];
        }
        //3rd pass, record positions of rows and cols
        curow = 0;
        int curcol = 1;
        rowPtrs[0][0] = bufStart;
        for (int b = bufStart; b < buffer.length; b++) {
            if (buffer[b] == delimiter) {
                rowPtrs[curow][curcol] = b;
                curcol++;
            }
            else if (buffer[b] == eolByte) {
                if (buffer[b - 1] != delimiter) {
                    rowPtrs[curow][curcol] = b;
                }
                curow++;
                b++;
                if (b < buffer.length) {
                    rowPtrs[curow][0] = b;
                    curcol = 1;
                }
            }
        }
        if (buffer[buffer.length - 1] != eolByte && buffer[buffer.length -
                1] != delimiter)
            rowPtrs[rowPtrs.length - 1][rowPtrs[rowPtrs.length - 1].length] = buffer.length;
        numRows = rowPtrs.length;
    }

    /*StaticDocument constructor*/
    /**
     get a String from StaticDocument at line , col
     */
    public String getString (int line, int col) {
        int startI;
        int endI;
        if (col == 0)
            startI = rowPtrs[line][col];
        else
            startI = rowPtrs[line][col] + 1;
        endI = rowPtrs[line][col + 1] - 1;
        int length = endI - startI;
        return  new String(internalBuf, startI, length + 1).trim();
        //for (int i1 = startI; i1 <= endI; i1++)
        //	sum = sum * 10 + (internalBuf[i1] - zero_byte);
    }

    /**
     get an int value from StaticDocument at line, col
     */
    public int getInt (int line, int col) {
        return  Integer.parseInt(getString(line, col));        /*		int startI;
         int endI;
         if( col == 0)
         startI = rowPtrs[line][col];
         else
         startI = rowPtrs[line][col] + 1;
         endI = rowPtrs[line][col+1] - 1;
         int sum = (int)0;
         for (int i1 = startI; i1 <= endI; i1++)
         {
         sum = sum * 10 + (internalBuf[i1] - zero_byte);
         }
         return sum;
         */

    }

    /**
     get a byte array with word from StaticDocument at line, col
     */
    public byte[] getBytes (int line, int col) {
        byte[] word = null;
        int startI;
        int endI;
        if (col == 0)
            startI = rowPtrs[line][col];
        else
            startI = rowPtrs[line][col] + 1;
        endI = rowPtrs[line][col + 1] - 1;
        int length = endI - startI + 1;
        //System.out.println("line, col, startI, endI, --> " + line + " " + col + " " + startI + " " + endI );
        word = new byte[length];
        for (int w = 0; w < length; w++) {
            word[w] = internalBuf[w + startI];
        }
        return  word;
    }

    /**
     get a double value from StaticDocument at line, col
     jjm ... look at Double static method for same
     */
    public double getDouble (int line, int col) {
        return  Double.valueOf(getString(line, col)).doubleValue();        /*		int startI;
         int endI;
         if( col == 0)
         startI = rowPtrs[line][col];
         else
         startI = rowPtrs[line][col] + 1;
         endI = rowPtrs[line][col+1] - 1;
         double sum = (double)0.0;
         int i1;
         boolean negative = false;
         //check for the sign
         if( internalBuf[startI] == '-') {
         negative = true;
         startI++;
         }
         if( internalBuf[startI] == '+')
         startI++;
         for (i1 = startI; i1 <= endI; i1++)
         {
         //	   System.out.println("Internalbuf[i1] "
         //	       + (internalBuf[i1]-zero_byte));
         // if decimal...startI right side of double
         if ( internalBuf[i1] == '.' )
         break;
         // create left side
         else
         sum = sum * 10 + (internalBuf[i1] - zero_byte);
         }
         if (i1 < endI && internalBuf[i1] ==(byte)'.')
         {
         // increment past '.'
         i1++;
         // create right side
         String rightSide = "0.";
         for (int i2=10; i1 <= endI; i1++, i2*=10 ) {
         //sum += (double)((double)(internalBuf[i1] -zero_byte)/i2);
         rightSide +=(internalBuf[i1]-zero_byte);
         }
         //System.out.println("RightSide " + rightSide);
         sum= sum + new Double(rightSide).doubleValue();
         //System.out.println("sum " + sum);
         //System.out.println("double " +new Double(rightSide).doubleValue());
         }
         if (negative) sum = -sum;
         //		System.out.println("STATIC TABLE :" + sum);
         return sum;
         */

    }

    /**
     get a float value from StaticDocument at line, col
     jjm ... look at Double static method for same
     */
    public float getFloat (int line, int col) {
        return  Float.valueOf(getString(line, col)).floatValue();        /*System.out.println("getFloat line:"+line+" col:"+col);
         int startI;
         int endI;
         if( col == 0)
         startI = rowPtrs[line][col];
         else
         startI = rowPtrs[line][col] + 1;
         endI = rowPtrs[line][col+1] - 1;
         float sum = (float)0.0;
         int i1;
         boolean negative = false;
         //check for the sign
         if( internalBuf[startI] == '-') {
         negative = true;
         startI++;
         }
         if( internalBuf[startI] == '+')
         startI++;
         for (i1 = startI; i1 <= endI; i1++)
         {
         // if decimal...startI right side of float
         if ( internalBuf[i1] == '.' )
         break;
         // create left side
         else
         sum = sum * 10 + (internalBuf[i1] - zero_byte);
         }
         if (i1 < endI && internalBuf[i1] ==(byte)'.')
         {
         // increment past '.'
         i1++;
         // create right side
         String rightSide = "0.";
         for (int i2=10; i1 <= endI; i1++, i2*=10 )
         //sum += (float) ((float)(internalBuf[i1] - zero_byte)/i2);
         rightSide +=(internalBuf[i1]-zero_byte);
         System.out.println("rightSide: "+rightSide);
         sum= sum + new Float(rightSide).floatValue();
         }
         if (negative) sum = -sum;
         return sum;
         */

    }

    /**
     return # of columns in row rowNum
     */
    public int getNumColumns (int lineNum) {
        return  rowPtrs[lineNum].length - 1;                    // no eolByte
    }

    /**
     return # of rows in StaticDocument
     */
    public int getNumRows () {
        return  numRows;
    }

    /**
     Will print the contents of this staticDoc to standard out.
     Each line of the Doc will be printed to a separate line
     of standard out.
     <br>
     This method assumes there is a proper implementation of
     getString for every underlying Document implementation.
     (note: this should never be a problem, as any implmentation
     of Document should be able to support a String rep)
     <br>
     */
    public void print () {
        for (int r = 0; r < numRows; r++) {
            int cols = getNumColumns(r);
            for (int c = 0; c < cols; c++) {
                System.out.print(getString(r, c) + " ");
            }
            System.out.println("");
        }
    }
}
/*StaticDocument*/

