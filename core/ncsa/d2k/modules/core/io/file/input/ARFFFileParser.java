//package ncsa.d2k.modules.projects.clutter.rdr;
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

    private HashSet[] allowedAttributes;
    private int dataRow;

    public ARFFFileParser(File f) throws Exception {
        file = f;
        lineReader = new LineNumberReader(new FileReader(file));
        initialize();
    }

    private void initialize() throws Exception {
        ArrayList attributes = new ArrayList();
        ArrayList types = new ArrayList();

        // find all the attributes
        String line = null;
        while( (line = lineReader.readLine().toLowerCase()).indexOf(DATA_TAG) == -1) {
            if( line.indexOf(ATTRIBUTE_TAG) != -1) {
                // drop the attribute tag, find the attribute name, type
                // if it is nominal, add its values to the allowedAttributes.
                parseAttributeLine(line, attributes, types);
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
            if(line.trim().length() != 0 && !line.startsWith(COMMENT))
                ctr++;
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
        //_blankRows = new ArrayList();
        //_blankColumns = new ArrayList();
        blanks = new boolean[numRows][numColumns];
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numColumns; j++)
                blanks[i][j] = false;
        }
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
                types.add(tok);
            ctr++;
        }
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
     * not including the optional meta data rows.
     * @param rowNum the row number to skip to
     */
    protected void skipToRow(int rowNum) {
        rowNum += dataRow;
        try {
            if(rowNum < lineReader.getLineNumber())
                lineReader = new LineNumberReader(new FileReader(file));
            int current = lineReader.getLineNumber();
            while(current < rowNum-1) {
                lineReader.readLine();
                current++;
            }
        }
        catch(Exception e) {
        }
    }
}