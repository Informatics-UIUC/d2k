package ncsa.d2k.modules.core.datatype.table.transformations;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;
import ncsa.d2k.modules.core.transform.binning.*;

/**
 * BinTransform encapsulates a binning transformation on a Table.
 */
public class BinTransform implements Transformation, Cloneable {
    private BinDescriptor[] bins;
    private boolean new_column;
    private static final String UNKNOWN = "Unknown";
    private static final String BIN = " bin";

    /**
     * Create a new BinTransform.
     * @param b The BinDescriptors
     * @param new_col true if a new column should be constructed for each
     *  binned column, false if the original column should be overwritten
     */
    public BinTransform (BinDescriptor[] b, boolean new_col) {
        bins = b;
        new_column = new_col;
    }

    /**
     * Bin the columns of a MutableTable.
     * @param mt the table to bin
     * @return true if the transformation was sucessful, false otherwise
     */
    public boolean transform (MutableTable mt) {
        HashMap colIndexLookup = new HashMap(mt.getNumColumns());
        for(int i = 0; i < mt.getNumColumns(); i++) {
          colIndexLookup.put(mt.getColumnLabel(i), new Integer(i));
        }

        // need to figure out which columns have been binned:
        boolean[] binRelevant = new boolean[mt.getNumColumns()];
        for (int i = 0; i < binRelevant.length; i++)
            binRelevant[i] = false;
        for (int i = 0; i < bins.length; i++) {
            Integer idx = (Integer)colIndexLookup.get(bins[i].label);
            if(idx != null)
              binRelevant[idx.intValue()] = true;
            //else
            //   System.out.println("COLUMN LABEL NOT FOUND!!!");
              //binRelevant[bins[i].column_number] = true;
        }
        String[][] newcols = new String[mt.getNumColumns()][mt.getNumRows()];
        for (int i = 0; i < mt.getNumColumns(); i++) {
            if (binRelevant[i])
                for (int j = 0; j < mt.getNumRows(); j++) {
                    // find the correct bin for this column
                    boolean binfound = false;
                    for (int k = 0; k < bins.length; k++) {
                        //if (bins[k].column_number == i) {
                        if( ((Integer)colIndexLookup.get(bins[k].label)).intValue() == i) {
                            // this has the correct column index
                            if (mt.isColumnNumeric(i)) {
                                if (bins[k].eval(mt.getDouble(j, i))) {
                                    newcols[i][j] = bins[k].name;
                                    binfound = true;
                                }
                            }
                            else {
                                if (bins[k].eval(mt.getString(j, i))) {
                                    newcols[i][j] = bins[k].name;
                                    binfound = true;
                                }
                            }
                        }
                        if (binfound) {
                            binRelevant[i] = true;
                            break;
                        }
                    }
                    if (!binfound)
                        newcols[i][j] = UNKNOWN;
                }
        }
        //StringColumn[] sc = new StringColumn[table.getNumColumns()];
        int numColumns = mt.getNumColumns();
        for (int i = 0; i < numColumns; i++) {
            if (binRelevant[i]) {
                //sc[i] = new StringColumn(newcols[i]);
                //sc[i].setComment(table.getColumn(i).getComment());
                if (new_column) {
                    if (binRelevant[i]) {
                        //sc[i].setLabel(table.getColumnLabel(i) + " bin");
                        mt.addColumn(new StringColumn(newcols[i]));
                        mt.setColumnLabel(mt.getColumnLabel(i) + BIN, mt.getNumColumns()
                                - 1);
                    }
                }
                else {
                    String oldLabel = mt.getColumnLabel(i);
                    mt.setColumn(new StringColumn(newcols[i]), i);
                    mt.setColumnLabel(oldLabel, i);
                }
            }
        }
        // 4/7/02 commented out by Loretta...
        // this add gets done by applyTransformation
        //mt.addTransformation(this);
        return  true;
    }

    public BinDescriptor[] getBinDescriptors() {
       return bins;
    }

    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }

}