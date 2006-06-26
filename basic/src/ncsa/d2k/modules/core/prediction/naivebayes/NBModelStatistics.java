package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.core.modules.*;
import java.io.*;
import java.util.*;

/**
 * Decipher the evidence that comes back.  Is it %?
 * @author clutter
 */
public class NBModelStatistics extends ComputeModule {

	public String getModuleName() {
	  return "Naive Bayes Model Statistics";	
	}
	
    public String[] getInputTypes() {
        return new String[] {
            "ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel",
            "java.lang.String"
        };
    }

    public String[] getOutputTypes() {
        return null;
    }

    public String getInputInfo(int i) {
        if(i == 0)
            return "The NaiveBayesModel to generate statistics about.";
        else
            return "File name to write the output to.";
    }

    public String getInputName(int i) {
        if(i == 0)
            return "NaiveBayesModel";
        else
            return "FileName";
    }

    public String getOutputInfo(int i) {
        return null;
    }

    public String getModuleInfo() {
        return "<p>Overview: Generate statistics about a NaiveBayesModel. This will output the probabilites of each "+
                "possible combination of bins. The results will be written to a file. The file is "+
                "pipe-delimited.</p>";
    }

    public void endExecution() {
        attributes = null;
        classes = null;
        nbModel = null;

        binsForAttribute = null;
        allBins = null;
        attributeToIndexMap = null;
    }

    private String[] attributes;
    private String[] classes;
    private NaiveBayesModel nbModel;

    private Object[] binsForAttribute;
    private List allBins;
    private HashMap attributeToIndexMap;

    public void doit() throws Exception {
        nbModel = (NaiveBayesModel)pullInput(0);

        // just in case
        if(!nbModel.isReadyForVisualization()) {
        	nbModel.setupForVis();
        	nbModel.setIsReadyForVisualization(true);
        }

        String file = (String)pullInput(1);

        attributes = nbModel.getAttributeNames();
        classes = nbModel.getClassNames();

        attributeToIndexMap = new HashMap();
        for(int i = 0; i < attributes.length; i++)
        	attributeToIndexMap.put(attributes[i], new Integer(i));

        // create a list of all the possible combinations
        binsForAttribute = new Object[attributes.length];
        allBins = new ArrayList();

        for(int i = 0; i < attributes.length; i++) {
            String an = attributes[i];

            String[] binNames = nbModel.getBinNames(an);

            List bins = new ArrayList();

            for(int j = 0; j < binNames.length; j++) {
            	String bn = binNames[j];

            	Bin d = new Bin();
            	d.attributeName = an;
            	d.binName = bn;
            	bins.add(d);
            	allBins.add(d);
            }

            binsForAttribute[i] = bins;
        }

        // a list of all the possible combinations
        // this can get quite large!
        List allCombos = new ArrayList();

        for(int idx = 0; idx < allBins.size(); idx++) {
        	Bin bin = (Bin)allBins.get(idx);

        	List lst = new ArrayList();
        	lst.add(bin);
        	allCombos.addAll(generateCombos(lst));
        }

        // now we have a list of all the possible combinations.

        // create the file writer
        BufferedWriter writer  = new BufferedWriter(new FileWriter(file));

        // write out the first line, which simply says the column names
        writer.write("Condition");
        for(int i = 0; i < classes.length; i++) {
        	if(i != classes.length-1)
        		writer.write("|");
        	writer.write("Prob("+classes[i]+")");
        }
        writer.write("\n");

        // process each combination of bins
        for(int idx = 0; idx < allCombos.size(); idx++) {
        	List lst = (List)allCombos.get(idx);
        	String str = process(lst);
        	writer.write(str);
        }

        writer.flush();
        writer.close();

        nbModel.clearEvidence();
    }

    /**
     *
     * @param bins
     * @return
     */
    private List generateCombos(List bins) {
    	List retVal = new ArrayList();

    	// first, add the argument
    	retVal.add(bins);

    	// now, get the last bin in the list
    	Bin b = (Bin)bins.get(bins.size()-1);

    	// add each bin from the next attribute
        // and recurse on each
        int attnum =
    		((Integer)attributeToIndexMap.get(b.attributeName)).intValue();

    	attnum++;

    	if(attnum < attributes.length) {
    		List binList = (List)this.binsForAttribute[attnum];

    		for(int i = 0; i < binList.size(); i++) {
    			Bin bb = (Bin)binList.get(i);

                // need to make a copy of the argument.  since we are recursing we cannot pass the same
                // list
                List ll = new ArrayList(bins);
    			ll.add(bb);
    			retVal.addAll(generateCombos(ll));
    		}
    	}

    	return retVal;
    }

    /**
     * Generate a string to print to the file and compute the probabilities.
     * @param data
     * @return
     */
    private String process(List data) {
    	nbModel.clearEvidence();
    	double[] evidence = new double[0];

       StringBuffer desc = new StringBuffer();
       int size = data.size();
       for(int i = 0; i < size; i++) {
    	   Bin d = (Bin)data.get(i);

    	   // add this as evidence to the model
    	   evidence = nbModel.addEvidence(d.attributeName, d.binName);

           if(i != 0) {
                desc.append(" ^ ");
           }
           desc.append(d.attributeName+" = "+d.binName);
        }

        // now normalize the evidence
        double total = 0;
        for(int i = 0; i < evidence.length; i++)
            total += evidence[i];

        for(int i = 0; i < evidence.length; i++)
            evidence[i] /= total;

        desc.append("|");

        for(int i = 0; i < evidence.length; i++) {
            if(i != 0)
                desc.append("|");
            desc.append(evidence[i]);
        }
        desc.append("\n");
        return desc.toString();
    }

    private class Bin {
        String attributeName;
        String binName;

        public String toString() {
        	return attributeName+" = "+binName;
        }
    }
}
