package  ncsa.d2k.modules.core.transform.attribute;

import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;

import java.util.*;

/**
 * BinTransform encapsulates a binning transformation on a Table.
 */
public class BinTransform implements Transformation {
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
		 * Retrieve BinDescriptor array from BinTransform
		 * @ return array containg BinDescriptors
		 */

		public BinDescriptor[] getBinDescriptors() {
		  return bins;
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
        for (int i = 0; i < mt.getNumColumns(); i++) {
            if (binRelevant[i]) {
                //sc[i] = new StringColumn(newcols[i]);
                //sc[i].setComment(table.getColumn(i).getComment());
                if (new_column) {
                    if (binRelevant[i]) {
                        //sc[i].setLabel(table.getColumnLabel(i) + " bin");
                        mt.addColumn(newcols[i]);
                        mt.setColumnLabel(mt.getColumnLabel(i) + BIN, mt.getNumColumns()
                                - 1);
                    }
                }
                else {
                    String oldLabel = mt.getColumnLabel(i);
                    mt.setColumn(newcols[i], i);
                    mt.setColumnLabel(oldLabel, i);
                }
            }
        }
        mt.addTransformation(this);
        return  true;
    }

    public BinTree createBinTree(ExampleTable et) {
      int[] outputs = et.getOutputFeatures();
      int[] inputs = et.getInputFeatures();

      HashMap used = new HashMap();

      String[] cn = TableUtilities.uniqueValues(et, outputs[0]);
      String[] an = new String[inputs.length];
      for(int i = 0; i < an.length; i++)
        an[i] = et.getColumnLabel(inputs[i]);

      BinTree bt = new BinTree(cn, an);

      for(int i = 0; i < bins.length; i++) {
        BinDescriptor bd = bins[i];
        String attlabel = bd.label;
        if(bd instanceof NumericBinDescriptor) {
          double max = ((NumericBinDescriptor)bd).max;
          double min = ((NumericBinDescriptor)bd).min;

          try {
              bt.addNumericBin(bd.label, bd.name, min, false,
                max, true);
          }
          catch(Exception e) {
          }
        }
        else {
          HashSet vals = ((TextualBinDescriptor)bd).vals;
          String[] values = new String[vals.size()];
          Iterator ii = vals.iterator();
          int idx = 0;
          while(ii.hasNext()) {
            values[idx] = (String)ii.next();
            idx++;
          }

          try {
            bt.addStringBin(bd.label, bd.name, values);
          }
          catch(Exception e) {
          }
        }

      }


      return bt;
    }
}