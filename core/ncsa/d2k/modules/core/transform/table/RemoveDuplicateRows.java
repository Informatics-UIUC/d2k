package ncsa.d2k.modules.projects.clutter;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

import gnu.trove.*;

import java.util.*;

/**
 * Remove duplicate rows from a MutableTable.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class RemoveDuplicateRows extends DataPrepModule {

    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.MutableTable"};
        return out;
    }

    public String getInputInfo(int i) {
        return "A Mutable Table";
    }

    public String getOutputInfo(int i) {
        return "The input table, with all duplicate rows removed.";
    }

    public String getModuleInfo() {
        return "Remove all the duplicate rows from a MutableTable.";
    }

    public void doit() {
        MutableTable mt = (MutableTable)pullInput(0);
        HashSet setOfUniqueRows = new HashSet();

        TIntArrayList rowsToRemove = new TIntArrayList();

        int numRows = mt.getNumRows();
        int numCols = mt.getNumColumns();
        for(int i = 0; i < numRows; i++) {
            String[] row = new String[numCols];
            mt.getRow(row, i);
            RowSet rs = new RowSet(row);
            if(setOfUniqueRows.contains(rs)) {
                rowsToRemove.add(i);
            }
            else
                setOfUniqueRows.add(rs);
        }
        int[] toRem = rowsToRemove.toNativeArray();
        mt.removeRowsByIndex(toRem);
        pushOutput(mt, 0);
    }

    private class RowSet {
        String[] keys;

        RowSet(String[] k) {
            keys = k;
        }

        public boolean equals(Object o) {
            RowSet other = (RowSet)o;
            String [] otherkeys = other.keys;

            if(otherkeys.length != keys.length)
                return false;

            for(int i = 0; i < keys.length; i++)
                if(!keys[i].equals(otherkeys[i]))
                    return false;
            return true;
        }

        public int hashCode() {
            int result = 37;
            for(int i = 0; i < keys.length; i++)
                result *= keys[i].hashCode();
            return result;
        }
	}
}
