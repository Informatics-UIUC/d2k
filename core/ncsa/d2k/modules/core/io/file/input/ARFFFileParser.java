
package ncsa.d2k.modules.core.io.file.input;

import java.io.*;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;


/**
 * A FlatFileReader that reads an ARFF File.  This is a delimited file with
 * extra metadata included.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class ARFFFileParser extends DelimitedFileParser {

    private static final String OPEN = "{";
    private static final String CLOSE = "}";
    private static final String STRING = "string";
    private static final String DATE = "date";
    private static final String RELATION = "relation";
    private static final String COMMENT = "%";
    private static final String REAL = "real";
    private static final String DATA = "data";
    private static final String FLAG = "@";
    private static final String NUMERIC = "numeric";
    private static final String ATTRIBUTE = "attribute";
    private static final String MISSING = "?";
    private static final String DATA_TAG = FLAG+DATA;
    private static final String ATTRIBUTE_TAG = FLAG+ATTRIBUTE;
    private static final char QUESTION = '?';

    private HashSet[] allowedAttributes;
    private int dataRow;

    public ARFFFileParser(File f) throws Exception {
        file = f;
        FileReader filereader = null;
        try {
           filereader = new FileReader(file);
        }
        catch (FileNotFoundException e) {
           throw new FileNotFoundException( "ARFF File Parser: " +
                    "Could not open file: " + file +
                    "\n" + e );
        }

        lineReader = new LineNumberReader(filereader);

       try {
            initialize();
        }
        catch(Exception e) {
            throw new Exception( "ARFF File Parser: " +
                    "Problems parsing ARFF file: " + file +
                    "\n" + e );
        }
    }

    private void initialize() throws Exception {
        ArrayList attributes = new ArrayList();
        ArrayList types = new ArrayList();

        // find all the attributes
        String line = lineReader.readLine();
        while( line.toLowerCase().indexOf(DATA_TAG) == -1) {
            if( line.toLowerCase().indexOf(ATTRIBUTE_TAG) != -1) {
                // drop the attribute tag, find the attribute name, type
                // if it is nominal, add its values to the allowedAttributes.
                parseAttributeLine(line, attributes, types);
            }
            line = lineReader.readLine();
            if ( line == null ) {
                throw new Exception ( "Keyword DATA not found in input file" );
            }
        }

        // now we have the names of the attributes and the types.
        columnLabels = new String[attributes.size()];
        columnTypes = new int[attributes.size()];
        allowedAttributes = new HashSet[attributes.size()];
        for(int i = 0; i < attributes.size(); i++) {
            columnLabels[i] = (String)attributes.get(i);
            String typ = (String)types.get(i);
            if( (typ.indexOf(NUMERIC) != -1 || typ.indexOf(REAL) != -1) && typ.indexOf(OPEN) == -1 && typ.indexOf(CLOSE) == -1)
                columnTypes[i] = ColumnTypes.DOUBLE;
            else if(typ.indexOf(STRING) != -1 && typ.indexOf(OPEN) == -1 && typ.indexOf(CLOSE) == -1)
                columnTypes[i] = ColumnTypes.STRING;
            else if(typ.indexOf(DATE) != -1 && typ.indexOf(OPEN) == -1 && typ.indexOf(CLOSE) == -1)
                columnTypes[i] = ColumnTypes.STRING;
            else {
                columnTypes[i] = ColumnTypes.STRING;
                // parse allowed values
                allowedAttributes[i] = parseAllowedAttributes(line);
            }
        }

        // now count the number of data lines
        int ctr = 0;
        while( (line = lineReader.readLine()) != null) {

            if(line.trim().length() != 0 && !line.startsWith(COMMENT)) {
                ctr++;
            }
        }
        numRows = ctr;
        numColumns = columnLabels.length;
        delimiter = COMMA;

        // now reset the reader and read in comments and find index of the @data tag
        resetReader();
        boolean done = false;
        while(!done) {
            line = lineReader.readLine().toLowerCase();
            if(line.indexOf(DATA_TAG) != -1) {
                dataRow++;
                done = true;
            }
            else
                dataRow++;
        }

/*        blanks = new boolean[numRows][numColumns];
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numColumns; j++)
                blanks[i][j] = false;
        }*/
    }

    private HashSet parseAllowedAttributes(String line) {
        return null;
    }

    private void parseAttributeLine(String line, ArrayList atts, ArrayList types) {
        StringTokenizer st = new StringTokenizer(line);
        int ctr = 0;
        while(st.hasMoreTokens()) {
            String tok = st.nextToken();
            // this will be the name of the attribute.
            if(ctr == 1)
                atts.add(tok);
            // this is the datatype of the attribute.
            else if(ctr == 2)
                types.add(tok.toLowerCase());
            ctr++;
        }
    }

    /**
    * Get the elements that make up row i of the file.
    * @return the elements of row i in the file.
    */
    //public char[][] getRowElements(int i) {
    public ParsedLine getRowElements(int i) {
//        char[][] retVal = super.getRowElements(i);
        ParsedLine retVal = super.getRowElements(i);

        if(retVal != null && retVal.elements != null && retVal.elements.length > 0) {
        // here we check each element to see if it was a missing value
        for(int j = 0; j < this.numColumns; j++) {
            if(retVal.elements[j].length > 0 && retVal.elements[j][0] == QUESTION) {
                //System.out.println("YES!");
                //addBlank(i, j);
                retVal.blanks[j] = true;
            }
        }
        }

        return retVal;
    }



    private void resetReader() {
        try {
            lineReader = new LineNumberReader(new FileReader(file));
        }
        catch(Exception e) {
        }
    }

    /**
     * Skip to a specific row in the file.  Rows are lines of data in the file,
     * not including the optional meta data rows.  When a comment is found, the
     * datarow index is incremented to account for it.  This will fail if the
     * file is not accessed in a serial fashion.
     * @param rowNum the row number to skip to
     */
    protected String skipToRow(int rowNum) {
        rowNum += dataRow;
        try {
            if(rowNum < lineReader.getLineNumber())
                lineReader = new LineNumberReader(new FileReader(file));
            int current = lineReader.getLineNumber();
            while(current < rowNum-1) {
                lineReader.readLine();
                current++;
            }
            String line;
            while( (line = lineReader.readLine()).startsWith(COMMENT))
                dataRow++;

            return line;
        }
        catch(Exception e) {
            return null;
        }
    }
}

// QA Comments
// 2/14/03 - Handed off to QA by David Clutter
// 2/12/03 - Ruth started QA process in conjunction with CreateARFFParser
//           module.   There needs to be better error checking - if a
//           file that can't be read is passed in, the exception message
//           isn't user friendly.   If the ARFF file has bogus data (for
//           example, illegal attributes or wrong type, no error is
//           shown, For example (last line bogus):
// @relation weather
// @attribute outlook {sunny, overcast, rainy}
// @attribute temperature numeric
// @data
// sunny, 85
// overcast, 64
// windy, hot
//     Ruth emailed.   Error checking should be fixed.  Perhaps
//     just state (in CreateARFFParser) that careful checking
//     for content is not performed
// 2/25/03 - Got confirmation that parser not validating.  Documented
//           in CreateARFFParser
//         - Found that all attribute variable names and nominal
//           values converted to lower case;  fixed
//         - Added more extensive exception handlers.
//         - ARFF sparse data not handled - documented in CreateARFFParser.
//         - Removed code that was commented out.
//         - Missing values denoted by ? in inputs not translated
//           to d2K missing values.  Documented in CreateARFFParser but
//           decided w/ other QA folks not to put into basic until some of
//           these resolved (next release).
// 3/3/03  - Missing values are in fact handled (down the road, not here).
//         - Committing to basic.   Still wish for validating parser.
// END QA Comments
