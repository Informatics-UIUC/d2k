package ncsa.d2k.modules;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.util.*;

//import ncsa.d2k.modules.PredictionModelModule;

/**
 * TO DO add useNameChecking
 */
abstract public class PredictionModelModule extends /*Prediction*/ModelModule {

    /** the size of the training set for this model */
    private int trainingSetSize;
    /** the labels for the input columns */
    private String[] inputColumnLabels;
    /** the labels for the outputs columns */
    private String[] outputColumnLabels;
    /** the datatypes for the input features */
    private int[] inputFeatureTypes;
    /** the datatypes for the output features */
    private int[] outputFeatureTypes;


    private PredictionModelModule() {
    }

    protected PredictionModelModule(ExampleTable train) {
        setTrainingTable(train);
    }

    protected PredictionModelModule(PredictionModelModule model) {
        setMetaDataFromModel(model);
    }

    /**
     *	Describe the function of this module.
     *	@return the description of this module.
     */
    public String getModuleInfo() {
        return "Makes predictions on a set of examples.";
    }

    /**
     *	The input is a Table.
     *	@return the input types
     */
    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.datatype.table.Table"};
        return in;
    }

    /**
     *	The input is an ExampleTable.
     *	@param i the index of the input
     *	@return the description of the input
     */
    public String getInputInfo(int i) {
        return "A set of examples to make predicitons on.";
    }

    /**
     *	Get the name of the input.
     *	@param i the index of the input
     *	@return the name of the input
     */
    public String getInputName(int i) {
        return "Examples";
    }

    /**
     *	The output is a PredictionTable.
     *	@return the output types
     */
    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.table.PredictionTable"};
        return out;
    }

    /**
     *	Describe the output.
     *	@param i the index of the output
     *	@return the description of the output
     */
    public String getOutputInfo(int i) {
        String out = "The input set of examples, with extra columns of "
                   +"predicitions added to the end.";
        return out;
    }

    /**
     *	Get the name of the output.
     *	@param i the index of the output
     *	@return the name of the output
     */
    public String getOutputName(int i) {
        return "Predictions";
    }

    /**
     *	Pull in the ExampleTable, pass it to the predict() method,
     *	and push out the PredictionTable returned by predict();
     */
    public void doit() throws Exception {
        Table et = (Table)pullInput(0);
        pushOutput(predict(et), 0);
    }

    /**
     * Predict the outcomes given a set of examples.  The return value
     * is a PredictionTable, which is the same as the input set with
     * extra column(s) of predictions added to the end.
     * @param value a table with a set of examples to predict on
     * @return the input table, with extra columns for predictions
     */
    public PredictionTable predict (Table table) throws Exception {
        PredictionTable pt;
        if(table instanceof PredictionTable) {
            // ensure that the inputFeatures and predictionSet are correct..
            pt = (PredictionTable)table;
            Map columnToIndexMap = new HashMap(pt.getNumColumns());
            for(int i = 0; i < pt.getNumColumns(); i++)
                columnToIndexMap.put(pt.getColumnLabel(i), new Integer(i));

            // ensure that the input features of pt are set correctly.
            int[] curInputFeat = pt.getInputFeatures();
            boolean inok = true;
            if(curInputFeat != null) {
                if(curInputFeat.length != inputColumnLabels.length)
                    inok = false;
                else {
                    // for each input feature
                    for(int i = 0; i < curInputFeat.length; i++) {
                        String lbl = pt.getColumnLabel(curInputFeat[i]);
                        if(!lbl.equals(inputColumnLabels[i]))
                            inok = false;
                        if(!inok)
                            break;
                    }
                }
            }
            else
                inok = false;

            if(!inok) {
                // if the inputs are not ok, redo them
                int[] newInputFeat = new int[inputColumnLabels.length];
                for(int i = 0; i < inputColumnLabels.length; i++) {
                    Integer idx = (Integer)columnToIndexMap.get(inputColumnLabels[i]);
                    if(idx == null)
                        // the input column did not exist!!
                        throw new Exception();
                    else
                        newInputFeat[i] = idx.intValue();
                }
                pt.setInputFeatures(newInputFeat);
            }

            // ensure that the prediction columns are set correctly.
            int[] curPredFeat = pt.getPredictionSet();
            boolean predok = true;
            if(curPredFeat != null) {
                if(curPredFeat.length != outputColumnLabels.length)
                    predok = false;
                else {
                    // for each input feature
                    for(int i = 0; i < curPredFeat.length; i++) {
                        String lbl = pt.getColumnLabel(curPredFeat[i]);
                        if(!lbl.equals(outputColumnLabels[i]+PredictionTable.PREDICTION_COLUMN_APPEND_TEXT))
                            predok = false;
                        if(!predok)
                            break;
                    }
                }
            }
            else
                predok = false;

            if(!predok) {
                int[] newPredSet = new int[outputFeatureTypes.length];
                // if the predictions are not ok, just make new columns for each one
                for(int i = 0; i < outputFeatureTypes.length; i++) {
                    int typ = outputFeatureTypes[i];
                    String name = outputColumnLabels[i]+PredictionTable.PREDICTION_COLUMN_APPEND_TEXT;
                    switch(typ) {
                        case(ColumnTypes.BOOLEAN):
                            boolean[] vals = new boolean[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(vals, name);
                            break;
                        case(ColumnTypes.BYTE):
                            byte[] bvals = new byte[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(bvals, name);
                            break;
                        case(ColumnTypes.BYTE_ARRAY):
                            byte[][] bbvals = new byte[pt.getNumRows()][];
                            newPredSet[i] = pt.addPredictionColumn(bbvals, name);
                            break;
                        case(ColumnTypes.CHAR):
                            char[] cvals = new char[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(cvals, name);
                            break;
                        case(ColumnTypes.CHAR_ARRAY):
                            char[][] ccvals = new char[pt.getNumRows()][];
                            newPredSet[i] = pt.addPredictionColumn(ccvals, name);
                            break;
                        case(ColumnTypes.DOUBLE):
                            double[] dvals = new double[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(dvals, name);
                            break;
                        case(ColumnTypes.FLOAT):
                            float[] fvals = new float[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(fvals, name);
                            break;
                        case(ColumnTypes.INTEGER):
                            int[] ivals = new int[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(ivals, name);
                            break;
                        case(ColumnTypes.LONG):
                            long[] lvals = new long[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(lvals, name);
                            break;
                        case(ColumnTypes.OBJECT):
                            Object[] ovals = new Object[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(ovals, name);
                            break;
                        case(ColumnTypes.SHORT):
                            short[] shvals = new short[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(shvals, name);
                            break;
                        default:
                            String[] stvals = new String[pt.getNumRows()];
                        newPredSet[i] = pt.addPredictionColumn(stvals, name);
                        break;
                    }
                }
                pt.setPredictionSet(newPredSet);
            }
        }

        // it was not a prediction table.  make it one and set the input features
        // and prediction set accordingly
        else {
            ExampleTable et = table.toExampleTable();
            // turn it into a prediction table
            pt = et.toPredictionTable();
            Map columnToIndexMap = new HashMap(pt.getNumColumns());
            for(int i = 0; i < pt.getNumColumns(); i++)
                columnToIndexMap.put(pt.getColumnLabel(i), new Integer(i));

            int[] inputFeat = new int[inputColumnLabels.length];
            for(int i = 0; i < inputColumnLabels.length; i++) {
                Integer idx = (Integer)columnToIndexMap.get(inputColumnLabels[i]);
                if(idx == null)
                    // the input column was missing, throw exception
                    throw new Exception();
                else
                    inputFeat[i] = idx.intValue();
            }
            pt.setInputFeatures(inputFeat);

            // ensure that the prediction columns are set correctly.
            int[] curPredFeat = pt.getPredictionSet();
            boolean predok = true;
            if(curPredFeat != null) {
                if(curPredFeat.length != outputColumnLabels.length)
                    predok = false;
                else {
                    // for each input feature
                    for(int i = 0; i < curPredFeat.length; i++) {
                        String lbl = pt.getColumnLabel(curPredFeat[i]);
                        if(!lbl.equals(outputColumnLabels[i]+PredictionTable.PREDICTION_COLUMN_APPEND_TEXT))
                            predok = false;
                        if(!predok)
                            break;
                    }
                }
            }
            else
                predok = false;

            if(!predok) {
                int[] newPredSet = new int[outputFeatureTypes.length];
                // just make new columns for each prediction
                for(int i = 0; i < outputFeatureTypes.length; i++) {
                    int typ = outputFeatureTypes[i];
                    String name = outputColumnLabels[i]+PredictionTable.PREDICTION_COLUMN_APPEND_TEXT;
                    switch(typ) {
                        case(ColumnTypes.BOOLEAN):
                            boolean[] vals = new boolean[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(vals, name);
                            break;
                        case(ColumnTypes.BYTE):
                            byte[] bvals = new byte[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(bvals, name);
                            break;
                        case(ColumnTypes.BYTE_ARRAY):
                            byte[][] bbvals = new byte[pt.getNumRows()][];
                            newPredSet[i] = pt.addPredictionColumn(bbvals, name);
                            break;
                        case(ColumnTypes.CHAR):
                            char[] cvals = new char[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(cvals, name);
                            break;
                        case(ColumnTypes.CHAR_ARRAY):
                            char[][] ccvals = new char[pt.getNumRows()][];
                            newPredSet[i] = pt.addPredictionColumn(ccvals, name);
                            break;
                        case(ColumnTypes.DOUBLE):
                            double[] dvals = new double[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(dvals, name);
                            break;
                        case(ColumnTypes.FLOAT):
                            float[] fvals = new float[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(fvals, name);
                            break;
                        case(ColumnTypes.INTEGER):
                            int[] ivals = new int[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(ivals, name);
                            break;
                        case(ColumnTypes.LONG):
                            long[] lvals = new long[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(lvals, name);
                            break;
                        case(ColumnTypes.OBJECT):
                            Object[] ovals = new Object[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(ovals, name);
                            break;
                        case(ColumnTypes.SHORT):
                            short[] shvals = new short[pt.getNumRows()];
                            newPredSet[i] = pt.addPredictionColumn(shvals, name);
                            break;
                        default:
                            String[] stvals = new String[pt.getNumRows()];
                        newPredSet[i] = pt.addPredictionColumn(stvals, name);
                        break;
                    }
                }
                pt.setPredictionSet(newPredSet);
            }
        }

        makePredictions(pt);
        return pt;
    }

    abstract protected void makePredictions(PredictionTable pt) throws Exception;

    /**
     * Set up all the meta-data related to the training set for this model.
     * @param et
     */
    protected void setTrainingTable(ExampleTable et) {
        trainingSetSize = et.getNumRows();
        int[] inputs = et.getInputFeatures();
        inputColumnLabels = new String[inputs.length];
        for(int i = 0; i < inputColumnLabels.length; i++)
            inputColumnLabels[i] = et.getColumnLabel(inputs[i]);
        int[] outputs = et.getOutputFeatures();
        outputColumnLabels = new String[outputs.length];
        for(int i = 0; i < outputColumnLabels.length; i++)
            outputColumnLabels[i] = et.getColumnLabel(outputs[i]);
        inputFeatureTypes = new int[inputs.length];
        for(int i = 0; i < inputs.length; i++)
            inputFeatureTypes[i] = et.getColumnType(inputs[i]);
        outputFeatureTypes = new int[outputs.length];
        for(int i = 0; i < outputs.length; i++)
            outputFeatureTypes[i] = et.getColumnType(outputs[i]);
    }

    protected void setMetaDataFromModel(PredictionModelModule model) {
      this.trainingSetSize = model.trainingSetSize;
      this.inputColumnLabels = model.inputColumnLabels;
      this.outputColumnLabels = model.outputColumnLabels;
      this.inputFeatureTypes = model.inputFeatureTypes;
      this.outputFeatureTypes = model.outputFeatureTypes;
    }

    /**
     * Get the size of the training set that built this model.
     * @return the size of the training set used to build this model
     */
    public int getTrainingSetSize() {
        return trainingSetSize;
    }

    /**
     * Get the labels of the input columns.
     * @return the labels of the input columns
     */
    public String[] getInputColumnLabels() {
        return inputColumnLabels;
    }

    /**
     * Get the labels of the output columns.
     * @return the labels of the output columns
     */
    public String[] getOutputColumnLabels() {
        return outputColumnLabels;
    }

    /**
     * Get the data types of the input columns in the training table.
     * @return the datatypes of the input columns in the training table
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int[] getInputFeatureTypes() {
        return inputFeatureTypes;
    }

    /**
     * Get the data types of the output columns in the training table.
     * @return the data types of the output columns in the training table
     * @see ncsa.d2k.modules.core.datatype.table.ColumnTypes
     */
    public int[] getOutputFeatureTypes() {
        return outputFeatureTypes;
    }
}