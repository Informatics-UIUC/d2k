/*
 * Created on Apr 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ncsa.d2k.modules.core.transform.filters.evaluation;


/**
 * @author anca
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CfsSubsetEvalTest extends TableInstancesSetupTest {

    public static void main(String[] args) {
	junit.textui.TestRunner.run(CfsSubsetEvalTest.class);
    }

    
    ncsa.d2k.modules.core.transform.filters.evaluation.CfsSubsetEval d2kEval;
    weka.attributeSelection.CfsSubsetEval wekaEval;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
	super.setUp();
	d2kEval = new ncsa.d2k.modules.core.transform.filters.evaluation.CfsSubsetEval();
	wekaEval = new weka.attributeSelection.CfsSubsetEval();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
	super.tearDown();
	d2kEval = null;
	wekaEval = null;
    }

    public void testEvaluateAttribute() {
	try {
	    d2kEval.buildEvaluator(tbl);
	    wekaEval.buildEvaluator(data);
	    float [][] d2kValue = d2kEval.getCorrMatrix();
	    float [][] wekaValue = wekaEval.getCorrMatrix();
	    
	    assertEquals(d2kValue[0].length, wekaValue[0].length);
	    assertEquals(d2kValue.length, wekaValue.length);
	    
	    for (int i =0; i < d2kValue.length; i++) {
	    	for (int j =0; j < d2kValue[i].length; j++) {
	    		assertEquals(d2kValue[i][j],wekaValue[i][j],delta);
	    		System.out.println("d2kValue for attribute j " + j + " is " + d2kValue[i][j]);
	    		System.out.println("wekaValue for attribute j " + j + " is " + wekaValue[i][j]);
	    	}
	    }
	    
	} catch( Exception e) {
	    System.out.println("CfsSubsetEvalTest exception " + e);
	}
	
	
    }

}
