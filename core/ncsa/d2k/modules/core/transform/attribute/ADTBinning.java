package ncsa.d2k.modules.core.transform.attribute;

import java.util.*;
import ncsa.d2k.infrastructure.modules.*;

import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


/**
   ADTBinning.java
*/

public class ADTBinning extends DataPrepModule implements HasNames {

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
		return "An ADTree, BinTree and an ExampleTable and classifies all input variables.";
    }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
		return "ADTBinning";
    }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
	String []in = {"ncsa.d2k.modules.core.datatype.ADTree",
		       "ncsa.d2k.modules.core.datatype.BinTree",
		       "ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};
	return in;
    }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
	String []out = {"ncsa.d2k.modules.core.datatype.BinTree",
		       "ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl"};

		return out;
    }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
		if(i == 0)
			return "An ADTree containing counts";
		else if(i == 1)
			return "An empty BinTree.";
		else if(i == 2)
			return "An ExampleTable.";
		else
			return "No such input";
    }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {

		if(i == 0)
			return "ADTree.";
		else if(i == 1)
			return "BinTree";
		else if(i == 2)
			return "exampleTable.";
		else
			return "No such input";
    }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
		if(i == 0)
			return "The full BinTree.";
		else if(i == 1)
			return "The ExampleTable, unchanged";
		else
			return "No such input";
    }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
		if(i == 0)
			return "binTree.";
		else if(i == 1)
			return "exampleTable";
		else
			return "No such input";
    }

    /**
       Perform the calculation.
    */
    public void doit() {
	ADTree adt =(ADTree)pullInput(0);
	BinTree bt = (BinTree)pullInput(1);
	ExampleTableImpl vt = (ExampleTableImpl)pullInput(2);


	//int [] ins = vt.getInputFeatures();
	int [] out = vt.getOutputFeatures();

	// we only support one out variable..
	int classColumn = out[0];
	String classLabel = vt.getColumn(classColumn).getLabel();
	int totalClassified = 0;
	int classTotal;
	int binListTotal;

	long startTime = System.currentTimeMillis();

	// get sql counts and set the tallys
	String cn[] = bt.getClassNames();
	String an[] = bt.getAttributeNames();


	for (int i=0; i <cn.length ; i++) {
	    classTotal =0;
	    for (int j=0; j <an.length ; j++) {
		String [] bn = bt.getBinNames(cn[i],an[j]);
		binListTotal = 0;

		for ( int k = 0; k < bn.length; k ++) {

		    BinTree.ClassTree ct = (BinTree.ClassTree)bt.get(cn[i]);
		    BinTree.ClassTree.BinList bl
			= (BinTree.ClassTree.BinList)ct.get(an[j]);
		    BinTree.ClassTree.Bin b = (BinTree.ClassTree.Bin)bl.get(bn[k]);
		    String condition =  b.getCondition(an[j]);

		    ArrayList pairs = b.getAttrValuePair(an[j]);
		    int len = pairs.size();
		    HashMap hm;
		    for (int l = 0; l < len ; l++) {
			hm = (HashMap)pairs.get(l);
			hm.put(classLabel,cn[i]);
		    }

		    //System.out.println("pairs " + pairs);

		    int s = adt.getDirectCount(adt,pairs);
		    // int s = adt.getCount(adt,pairs);

		    b.setTally(s);
		    totalClassified = totalClassified + s;
		    classTotal = classTotal + s;
		    binListTotal = binListTotal + s;
		    //System.out.println("COUNT: " + s);

		    bl.setTotal(binListTotal);

		}
	    }
	    bt.setClassTotal(cn[i],classTotal);
	}

	bt.setTotalClassified (totalClassified);


	long endTime = System.currentTimeMillis();
	System.out.println ( "time in msec " + (endTime-startTime));

	pushOutput(bt, 0);
	pushOutput(vt, 1);
    }
}

