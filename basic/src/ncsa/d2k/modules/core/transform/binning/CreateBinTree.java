package ncsa.d2k.modules.core.transform.binning;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.util.*;

/**
 * <p>Overview:
 * Creates an empty BinTree.
 * </p><p>Detailed Description:
 * Given a Binning Transformation containing the definition of the bins,
 * and an Example Table that has the input/ output attribute labels and types,
 * this module builds a Bin Tree that can be later used to classify data.
 * </p><p>A Bin Tree holds information about the number of examples that fall
 * into each bin for each class. The Bin Tree can use only one output feature
 * as a class. If more are selected in the Example Table, only the first one
 * will be used.
 * </p><p> Scalability: a large enough number of features will result in an
 * OutOfMemory error. Use feature selection to reduce the number of features.</p>
 */
public class CreateBinTree extends DataPrepModule {

    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     *         the corresponding index.
     */
    public String[] getInputTypes() {
        return new String[]{
                "ncsa.d2k.modules.core.datatype.table.transformations.BinTransform",
                "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
	}

    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     *         the corresponding index.
     */
    public String[] getOutputTypes() {
        return new String[]{"ncsa.d2k.modules.core.transform.binning.BinTree"};
	}

    /**
     * Returns a description of the input at the specified index.
     *
     * @param i Index of the input for which a description should be returned.
     * @return <code>String</code> describing the input at the specified index.
     */
    public String getInputInfo(int i) {
        switch (i) {
            case 0 :
                return "Binning Transformation containing the bin definitions";
            case 1 :
                return "Example Table containing the names of the input/output features";
            default :
                return "No such input";
        }
	}

    /**
     * Returns a description of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> describing the output at the specified index.
     */
    public String getOutputInfo(int i) {
        return "Bin Tree structure holding counts";
	}

    /**
     * Returns the name of the input at the specified index.
     *
     * @param i Index of the input for which a name should be returned.
     * @return <code>String</code> containing the name of the input at the specified index.
     */
    public String getInputName(int i) {
        switch (i) {
            case 0 :
                return "Binning Transformation";
            case 1 :
                return "Example Table";
            default :
                return "No such input";
        }
	}

    /**
     * Returns the name of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> containing the name of the output at the specified index.
     */
    public String getOutputName(int i) {
        return "Bin Tree";
	}

    /**
     * Describes the purpose of the module.
     *
     * @return <code>String</code> describing the purpose of the module.
     */
    public String getModuleInfo() {
        StringBuffer sb = new StringBuffer("<p>Overview: ");
        sb.append("Creates an empty BinTree.");
        sb.append("</p><p>Detailed Description: ");
        sb.append(
                "Given a Binning Transformation containing the definition of the bins, ");
        sb.append(
                "and an Example Table that has the input/ output attribute labels and types, ");
        sb.append(
                "this module builds a Bin Tree that can be later used to classify data. ");
        sb.append(
                "</p><p>A Bin Tree holds information about the number of examples that fall into each bin ");
        sb.append("for each class. The Bin Tree can use only one output ");
        sb.append(
                "feature as a class. If more are selected in the Example Table, only the first one will be used.");
        sb.append(
                "</p><p> Scalability: a large enough number of features will result ");
        sb.append(
                "in an OutOfMemory error. Use feature selection to reduce the number of features.</p>");
        return sb.toString();

    }

    /**
     * Returns the name of the module that is appropriate for end-user consumption.
     *
     * @return The name of the module.
     */
    public String getModuleName() {
        return "Create Bin Tree";
	}
    private D2KModuleLogger myLogger;
    
    public void beginExecution() {
 	  myLogger = D2KModuleLoggerFactory.getD2KModuleLogger(this.getClass());
    }

    /**
     * Performs the main work of the module.
     *
     * @throws Exception if a problem occurs while performing the work of the module
     */
    public void doit() throws Exception {
        BinTransform bt = (BinTransform) pullInput(0);
        ExampleTable et;
        try {
            et = (ExampleTable) pullInput(1);
        } catch (ClassCastException ce) {
            throw new Exception(
                    getAlias()
                            + ": Select input/output features using ChooseAttributes before this module");
        }

        int[] ins = et.getInputFeatures();
        int[] out = et.getOutputFeatures();

        if ((ins == null) || (ins.length == 0))
            throw new Exception(
                    getAlias()
                            + ": Please select the input features, they are missing.");

        if (out == null || out.length == 0)
            throw new Exception(
                    getAlias()
                            + ": Please select an output feature, it is missing");

        if (bt == null)
            throw new Exception(
                    getAlias()
                            + ": Bins must be defined before creating a BinTree");

        // we only support one out variable..
        int classColumn = out[0];

        if (et.isColumnScalar(classColumn))
            throw new Exception(
                    getAlias()
                            + ": Output feature must be nominal. Please transform it.");

        BinDescriptor[] bins = bt.getBinDescriptors();

        if (bins.length == 0 || bins.length < ins.length)
            throw new Exception(
                    getAlias()
                            + ": Bins must be defined for each input before creating BinTree.");

        BinTree tree = createBinTree(bt, et);

        int numRows = et.getNumRows();
        long startTime = System.currentTimeMillis();

        /*	if (et.hasMissingValues()) {
		
              for (int i = 0; i < ins.length; i++) {
                      // numeric columns
		
                      if (et.isColumnScalar(ins[i])) {
                          for (int j = 0; j < numRows; j++) {
                              if (et.isValueMissing(j, ins[j]))
                                  tree.classifyMissing(
                                      et.getString(j, classColumn),
                                      et.getColumnLabel(ins[i]));
                              else
                                  tree.classify(
                                      et.getString(j, classColumn),
                                      et.getColumnLabel(ins[i]),
                                      et.getDouble(j, ins[i]));
                          }
                      }
		
                      // everything else is treated as textual columns
                      else {
                          for (int j = 0; j < numRows; j++)
                              if (et.isValueMissing(j, ins[j]))
                                  tree.classifyMissing(
                                      et.getString(j, classColumn),
                                      et.getColumnLabel(ins[i]));
                              else
                                  tree.classify(
                                      et.getString(j, classColumn),
                                      et.getColumnLabel(ins[i]),
                                      et.getString(j, ins[i]));
                      }
                  }
              } else { // no missing values in the table
          */
        for (int i = 0; i < ins.length; i++) {

            // numeric columns
            if (et.isColumnScalar(ins[i])) {
                for (int j = 0; j < numRows; j++) {
                    if (et.isValueMissing(j, ins[i]))
                        tree.classify(
                                et.getString(j, classColumn),
                                et.getColumnLabel(ins[i]),
                                et.getMissingString());
                    else
                        tree.classify(
                                et.getString(j, classColumn),
                                et.getColumnLabel(ins[i]),
                                et.getDouble(j, ins[i]));
                }
            }

            // everything else is treated as textual columns
            else {
                for (int j = 0; j < numRows; j++)
                    tree.classify(
                            et.getString(j, classColumn),
                            et.getColumnLabel(ins[i]),
                            et.getString(j, ins[i]));
            }
        }
        //	}

        long endTime = System.currentTimeMillis();
        if (debug){
      	  myLogger.setDebugLoggingLevel();//temp set to debug
      	  myLogger.debug("time in msec " + (endTime - startTime));
          myLogger.resetLoggingLevel();//re-set level to original level
        }
        if (debug){
            tree.printAll();
        }
        pushOutput(tree, 0);

    }

    /**
     * Create BinTree given bin descriptor, class names, and attribute names
     * @param bins bin descriptors
     * @param cn class names
     * @param an attribute names
     * @return BinTree
     */
    public static BinTree createBinTree(
		BinDescriptor[] bins,
		String[] cn,
		String[] an) {

		// converting the attribute labels to lower case
		// done for compatibility with BinTrees produced by SQL
		/*   String lowerCaseAn [] = new String[an.length];
		for (int i =0; i < an.length ; i++)
		lowerCaseAn[i] = an[i].toLowerCase();
		*/

		BinTree bt = new BinTree(cn, an);

		System.out.println("bins.length " + bins.length);
		for (int i = 0; i < bins.length; i++) {
			BinDescriptor bd = bins[i];
			String attLabel = bd.label; //.toLowerCase();
			//System.out.println("bin label " + attLabel + " " + bd.name);
			if (bd instanceof NumericBinDescriptor) {
				double max = ((NumericBinDescriptor) bd).max;
				double min = ((NumericBinDescriptor) bd).min;

				try {
					bt.addNumericBin(attLabel, bd.name, min, false, max, true);
					//System.out.println("bin min " + min + " max " + max);
				} catch (Exception e) { }
				
			} else {
				HashSet vals = ((TextualBinDescriptor) bd).vals;
				String[] values = new String[vals.size()];
				Iterator ii = vals.iterator();
				int idx = 0;
				while (ii.hasNext()) {
					values[idx] = (String) ii.next();
					//System.out.println(values[idx]);
					idx++;
				}

				try {
					bt.addStringBin(attLabel, bd.name, values);
					//System.out.println("addStringBin in CreateBinTree called");	
				} catch (Exception e) {}
				
			}

		}

		return bt;

	}

    /**
     * Create a BinTree given a BinTransform and ExmapleTable
     * @param bt bin transform
     * @param et example table
     * @return bin tree
     */
    public static BinTree createBinTree(BinTransform bt, ExampleTable et) {

		int[] outputs = et.getOutputFeatures();
		int[] inputs = et.getInputFeatures();

		HashMap used = new HashMap();

		String[] cn = TableUtilities.uniqueValues(et, outputs[0]);
		String[] an = new String[inputs.length];
		for (int i = 0; i < an.length; i++)
			an[i] = et.getColumnLabel(inputs[i]);

		BinDescriptor[] bd = bt.getBinDescriptors();

		// if there are missing values in any of the columns
		// create a bin for missing/unkown values for that column; 
		//- might not be needed see default bins in BinTree

		/*	ArrayList unknownBinDescriptors = new ArrayList();
			boolean [] columnProcessed = bt.relevantBins((MutableTable)et);
			for ( int i =0; i < columnProcessed.length; i ++) {
			 if (columnProcessed[i]){
			 	if (et.hasMissingValues(i))
			 		if (et.isColumnNominal(i)) {
			 			TextualBinDescriptor tbd = new TextualBinDescriptor(i,"missing",null,et.getColumnLabel(i));
			 			unknownBinDescriptors.add(tbd);
			 		}
			 		if (et.isColumnScalar(i)) {
			 			NumericBinDescriptor tbd = new NumericBinDescriptor(i,"missing",0,0,et.getColumnLabel(i));
						unknownBinDescriptors.add(tbd);	
			 		}
			 	 }
			}
				BinDescriptor [] newBinDescriptors = new BinDescriptor[bd.length+unknownBinDescriptors.size()]; 
			 	if (unknownBinDescriptors.size() > 0) { 
			 	  System.arraycopy(bd,0,newBinDescriptors,0,bd.length);
			      for (int j = bd.length ; j < newBinDescriptors.length; j++ )
						newBinDescriptors[j] = (BinDescriptor)unknownBinDescriptors.get(j-bd.length); 	      
			 	
			 	return createBinTree( newBinDescriptors,cn,an);
			 	
			 	}else 
			*/

		return createBinTree(bd, cn, an);
	}

    /** This property controls the module's output to the stdout. If set to
     *  true the created BinTree will be printed.
     */
    private boolean debug;

    /**
     * Get debug
     * @return  debug
     */
    public boolean getDebug() {
		return debug;
	}

    /**
     * Set debug
     * @param deb debug
     */
    public void setDebug(boolean deb) {
		debug = deb;
	}

    /**
     * Returns an array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects for each property of the
     * module.
     *
     * @return An array of <code>ncsa.d2k.core.modules.PropertyDescription</code> objects.
     */
    public PropertyDescription[] getPropertiesDescriptions() {
        PropertyDescription[] pd = new PropertyDescription[1];
        pd[0] =
                new PropertyDescription(
                        "debug",
                        "Verbose Mode",
                        "This property controls the module's output to the stdout. "
                                + "If set to true the created BinTree will be printed. ");

        return pd;
	}

}
