package ncsa.d2k.modules.core.prediction.naivebayes;

import java.util.*;
import gnu.trove.*;

import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.parameter.*;

import java.text.NumberFormat;

public class NaiveBayesAutoBin extends DataPrepModule {

    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
          "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
        return in;
    }

    public String getInputInfo(int i) {
        return "";
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
            "ncsa.d2k.modules.core.datatype.BinTree"};
        return out;
    }

    public String getOutputInfo(int i) {
        return "";
    }

    public String getModuleInfo() {
        return "";
    }

    public void doit() throws Exception {
        ExampleTable et = (ExampleTable)pullInput(0);
        ParameterPoint pp = (ParameterPoint)pullInput(1);
        HashMap nameToIndexMap = new HashMap();


        for(int i = 0; i < pp.getNumParameters(); i++) {
          String name = pp.getName(i);
          nameToIndexMap.put(name, new Integer(i));
        }

        Integer method = (Integer)nameToIndexMap.get(NaiveBayesParamSpaceGenerator.BIN_METHOD);
        if(method == null)
          throw new Exception(getAlias()+":  Could not find Bin Method!");
        int type = method.intValue();

        // if type == 0, specify the number of bins
        // if type == 1, use uniform weight
        BinTree bt;
        if(type == 0) {
          Integer number = (Integer)nameToIndexMap.get(NaiveBayesParamSpaceGenerator.NUMBER_OF_BINS);
          if(number == null)
            throw new Exception(getAlias()+": Number of bins not specified!");
          bt = numberOfBins(et, (int)pp.getValue(number.intValue()));
        }
        else {
          Integer weight = (Integer)nameToIndexMap.get(NaiveBayesParamSpaceGenerator.ITEMS_PER_BIN);
          if(weight == null)
            throw new Exception(getAlias()+": Items per bin not specified!");
          bt = sameWeight(et, (int)pp.getValue(weight.intValue()));
        }

        pushOutput(et, 0);
        pushOutput(bt, 1);
    }

    private BinTree numberOfBins(ExampleTable et, int num) throws Exception {
        int[] inputs = et.getInputFeatures();
        int[] outputs = et.getOutputFeatures();

        String [] an = new String[inputs.length];
        for(int i = 0; i < inputs.length; i++) {
            an[i] = et.getColumnLabel(inputs[i]);
        }
        String[] cn = TableUtilities.uniqueValues(et, outputs[0]);
        BinTree bt = new BinTree(cn, an);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);

        for(int i = 0; i < inputs.length; i++) {
            boolean isScalar = et.isColumnScalar(inputs[i]);
            // if it is scalar, find the max and min and create equally-spaced bins
            if(isScalar) {
                // find the maxes and mins
                ScalarStatistics ss = TableUtilities.getScalarStatistics(et, inputs[i]);
                double max = ss.getMaximum();
                double min = ss.getMinimum();
                System.out.println("MIN: "+min+" MAX: "+max);
                double[] binMaxes = new double[num - 1];
                double interval = (max - min)/(double)num;
                binMaxes[0] = min + interval;

                // add the first bin manually
                StringBuffer name = new StringBuffer();
                name.append("(...,");
                name.append(nf.format(binMaxes[0]));
                name.append("]");
                //bt.addNumericBin(et.getColumnLabel(inputs[i]), Integer.toString(counter),
                bt.addNumericBin(et.getColumnLabel(inputs[i]), name.toString(),
                  Double.MIN_VALUE, false, binMaxes[0], true);
                //counter++;


                // now add the rest
                for (int j = 1; j < binMaxes.length; j++) {
                    binMaxes[j] = binMaxes[j - 1] + interval;
                    name = new StringBuffer();
                    name.append("(");
                    name.append(nf.format(binMaxes[j-1]));
                    name.append(",");
                    name.append(nf.format(binMaxes[j]));
                    name.append("]");
                        //bt.addNumericBin(et.getColumnLabel(inputs[i]), Integer.toString(counter),
                        bt.addNumericBin(et.getColumnLabel(inputs[i]), name.toString(),
                           binMaxes[j-1], false, binMaxes[j], true);
                       // counter++;
                }

                // now add the last bin
                name = new StringBuffer();
                name.append("(");
                name.append(nf.format(binMaxes[binMaxes.length-1]));
                name.append(",");
                name.append("...");
                name.append(")");
                //bt.addNumericBin(et.getColumnLabel(inputs[i]), Integer.toString(counter),
                bt.addNumericBin(et.getColumnLabel(inputs[i]), name.toString(),
                  binMaxes[binMaxes.length-1], false, Double.MAX_VALUE, true);
                //counter++;
            }

            // if it is nominal, create a bin for each unique value.
            else {
                String[] vals = TableUtilities.uniqueValues(et, inputs[i]);
                for(int j = 0; j < vals.length; j++) {
                        //bt.addStringBin(et.getColumnLabel(inputs[i]), Integer.toString(counter),
                        bt.addStringBin(et.getColumnLabel(inputs[i]), vals[j],
                            vals[j]);
                        //counter++;
                }
            }
        }

        return bt;
    }

    private BinTree sameWeight(ExampleTable et, int weight) throws Exception {
        int[] inputs = et.getInputFeatures();
        int[] outputs = et.getOutputFeatures();
        String [] an = new String[inputs.length];
        for(int i = 0; i < inputs.length; i++) {
            an[i] = et.getColumnLabel(inputs[i]);
        }
        String[] cn = TableUtilities.uniqueValues(et, outputs[0]);
        BinTree bt = new BinTree(cn, an);

        for(int i = 0; i < inputs.length; i++) {
            // if it is scalar, get the data and sort it.  put (num) into each bin,
            // and create a new bin when the last one fills up
            boolean isScalar = et.isColumnScalar(inputs[i]);
            if(isScalar) {
                double[] vals = new double[et.getNumRows()];
                et.getColumn(vals, inputs[i]);
                Arrays.sort(vals);

                //!!!!!!!!!!!!!!!
                TDoubleArrayList list = new TDoubleArrayList();
                // now find the bin maxes...
                // loop through the sorted data.  the next max will lie at
                // data[curLoc+weight] items
                int curIdx = 0;
                while (curIdx < vals.length - 1) {
                    curIdx += weight;
                    if (curIdx > vals.length - 1)
                        curIdx = vals.length - 1;
                    // now loop until the next unique item is found
                    boolean done = false;
                    if (curIdx == vals.length - 1)
                        done = true;
                    while (!done) {
                        if (vals[curIdx] != vals[curIdx + 1])
                            done = true;
                        else
                            curIdx++;
                        if (curIdx == vals.length - 1)
                            done = true;
                    }
                    // now we have the current index
                    // add the value at this index to the list
                    //Double dbl = new Double(vals[curIdx]);
                    list.add(vals[curIdx]);
                }
                double[] binMaxes = new double[list.size()];
                for (int j = 0; j < binMaxes.length; j++)
                    //binMaxes[j] = ((Double)list.get(j)).doubleValue();
                    binMaxes[j] = list.get(j);

                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(3);

                // now we have the binMaxes.  add the bins to the bin tree.
                // add the first one manually
                StringBuffer name = new StringBuffer();
                name.append("(...,");
                name.append(nf.format(binMaxes[0]));
                name.append("]");
                    //bt.addNumericBin(et.getColumnLabel(inputs[i]), Integer.toString(counter),
                    bt.addNumericBin(et.getColumnLabel(inputs[i]), name.toString(),
                                     Double.MIN_VALUE, false, binMaxes[0], true);
                    //counter++;

                // add the other bins
                // now add the rest
                for (int j = 1; j < binMaxes.length; j++) {
                    //binMaxes[j] = binMaxes[j - 1] + interval;
                    name = new StringBuffer();
                    name.append("(");
                    name.append(nf.format(binMaxes[j-1]));
                    name.append(",");
                    name.append(nf.format(binMaxes[j]));
                    name.append("]");

                        bt.addNumericBin(et.getColumnLabel(inputs[i]), name.toString(),
                           binMaxes[j-1], false, binMaxes[j], true);
               //         counter++;
                }

                // now add the last bin
                name = new StringBuffer();
                name.append("(");
                name.append(nf.format(binMaxes[binMaxes.length-1]));
                name.append(",");
                name.append("...");
                name.append(")");
                //bt.addNumericBin(et.getColumnLabel(inputs[i]), Integer.toString(counter),
                bt.addNumericBin(et.getColumnLabel(inputs[i]), name.toString(),
                  binMaxes[binMaxes.length-1], false, Double.MAX_VALUE, true);
                //counter++;

            }

            else {
                // if it is nominal, create a bin for each unique value.
                String[] vals = TableUtilities.uniqueValues(et, inputs[i]);
                for(int j = 0; j < vals.length; j++) {
                        bt.addStringBin(et.getColumnLabel(inputs[i]), vals[j], vals[j]);
                //        counter++;
                }
            }
        }

        return bt;
    }

}
