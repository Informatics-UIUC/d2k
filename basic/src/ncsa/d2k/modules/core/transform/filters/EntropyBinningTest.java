/*
 * Created on Apr 1, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ncsa.d2k.modules.core.transform.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import junit.framework.TestCase;
import ncsa.d2k.modules.core.datatype.table.ExampleTable;
import ncsa.d2k.modules.core.datatype.table.basic.ExampleTableImpl;
import ncsa.d2k.modules.core.datatype.table.basic.TableImpl;
import ncsa.d2k.modules.core.datatype.table.transformations.ReplaceNominalsWithIntsTransform;
import ncsa.d2k.modules.core.io.file.input.DelimitedFileParser;
import ncsa.d2k.modules.core.io.file.input.ParseFileToTable;
import weka.core.Instances;
import weka.filters.supervised.attribute.Discretize;
/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EntropyBinningTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(EntropyBinningTest.class);
	}

	//d2k related variables
	EntropyBinning etb = null;
	ExampleTable tbl;
	double delta =0.00001;
	int [] ins = null;
	
	//weka related variables
	Instances data = null;
	Discretize filter = null;
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		//d2k related setup
		etb = new EntropyBinning();
		File f = new File("/home/anca/wekas/UCI/iris.csv");
		int labelsRow = 0;
		int typesRow = 1;
		DelimitedFileParser dfparser= new DelimitedFileParser(f, labelsRow, typesRow);
		ParseFileToTable file2table = new ParseFileToTable();
		TableImpl t = (TableImpl)file2table.createTable(dfparser);
		tbl = new ExampleTableImpl(t);
		int numColumns = tbl.getNumColumns();
		
		//define input and output attributes
		// input attributes are all attributes except the last one
		// output attributes is the last attribute
		
		ins = new int[numColumns-1];
		for (int i =0; i < numColumns-1; i ++)
			ins[i] =i;
		int outs[] = new int[1];
		outs[0] = numColumns-1;
		
		tbl.setInputFeatures(ins);
		tbl.setOutputFeatures(outs);
		
		//nominal variables need to be replaced with ints in WEKA
		ReplaceNominalsWithIntsTransform transform =
			new ReplaceNominalsWithIntsTransform (tbl);
		transform.transform(tbl);

		//System.out.println(transform.toMappingString(tbl));
		
		etb.setUseBetterEncoding(false);
		
		//weka related setup
		Reader input;
		String fileName = "/home/anca/wekas/UCI/iris.arff";
		input = new BufferedReader(new FileReader(fileName));	
		data = new Instances(input, 1);
		data.setClassIndex(data.numAttributes() - 1);
		filter = new Discretize();
		
		if (filter.setInputFormat(data)) {
			System.out.println("Getting output format");
		}
		while (data.readInstance(input)) {
			if (filter.input(data.instance(0))) {
				System.out.println("Filter said collect immediately");
				System.out.println("Getting output instance");
			}
			data.delete(0);
		}
			
		super.setUp();
	
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCalculateCutPoints() {
		//d2k cutPoints
		
		etb.calculateCutPoints(tbl,ins);
		//weka cutPoints
		filter.batchFinished();	
		
		for (int i =0; i < ins.length; i++) {
			double []d2kCutPoints = etb.getCutPoints(i);
			double []wekaCutPoints = filter.getCutPoints(i);
			assertEquals(d2kCutPoints.length, wekaCutPoints.length);
			for(int j =0; j < d2kCutPoints.length; j++) {
			  assertEquals(d2kCutPoints[j], wekaCutPoints[j],delta);
			System.out.println (" cutpoint for attribute " + j + " is " + d2kCutPoints[j]);
			}
		}
		
	}

	
}
