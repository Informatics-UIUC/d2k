package ncsa.d2k.modules.core.transform.table;
//package ncsa.d2k.modules.projects.smathur.transform.table;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.transform.table.NFoldExTable;
import java.util.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class NFoldStatified extends NFoldExTable{
    Hashtable uniqueOutputToRows; //maps a uniqueOutput to a vector of rows that have this output

    Vector testIndices;
    Vector trainIndices;

    /**
     * Return the human readable name of the module.
     * @return the human readable name of the module.
     */
    public String getModuleName() {
            return "NFoldStatified";
    }

    /**
     * Return the human readable name of the indexed input.
     * @param index the index of the input.
     * @return the human readable name of the indexed input.
     */
    public String getInputName(int index) {
            switch(index) {
                    case 0:
                            return "input0";
                    default: return "NO SUCH INPUT!";
            }
    }

    /**
     * Return the human readable name of the indexed output.
     * @param index the index of the output.
     * @return the human readable name of the indexed output.
     */
    public String getOutputName(int index) {
            switch(index) {
                    case 0:
                            return "output0";
                    case 1:
                            return "output1";
                    case 2:
                            return "output2";
                    case 3:
                            return "output3";
                    default: return "NO SUCH OUTPUT!";
            }
    }

    public String getOutputInfo(int i){
            switch (i) {
                    case 0: return "The Example Table";
                    case 1: return "The Test Table";
                    case 2: return "The Train Table";
                    case 3: return "The N that was set in the properties";
                    default: return "No such output";
            }
    }

    public String[] getOutputTypes(){
            String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
                              "ncsa.d2k.modules.core.datatype.table.TestTable",
                              "ncsa.d2k.modules.core.datatype.table.TrainTable",
                              "java.lang.Integer"};
            return types;
    }

    public String getModuleInfo(){
            return "<html>  <head>      </head>  <body>    Will produce and push N " +
                   "TestTable/TrainTable pairs. The test sets are of size (1/N)*numExamples" +
                   " and the train sets the rest. The sets are are randomly created based " +
                   "on the seed. PROPS: N - the number of exampleTables to make, Seed - " +
                   "the basis of the random subsampling, allows the user to create the same" +
                   "subsets or insure it changes  </body></html>";
    }

    int totalFires=0;

    String once;

    public void beginExecution() {
        super.beginExecution();
        once = null;
    }

    protected void setup(){
        once = new String("first");
        table = (Table) this.pullInput (0);
//printTable(table);
        createUniqueOutputToRowsHash();
        createTestTrainSets();
    }

    /**
            Does things, especially 'it'
    */
    public void doit () throws Exception {
        if (once == null) {
            setup ();
        }

        // Set up the train and test sets indices
        //convert a Vector of Integer objects to an array of ints
        int testing [] = new int [testIndices.size()];
        for (int i=0; i<testing.length; i++) {
            testing[i] = ((Integer)testIndices.elementAt(i)).intValue();
        }

        int training [] = new int [trainIndices.size()];
        for (int i=0; i<training.length; i++) {
            training[i] = ((Integer)trainIndices.elementAt(i)).intValue();
        }

        // now create a new table.
        ExampleTable examples = table.toExampleTable();
        examples.setTrainingSet (training);
        examples.setTestingSet (testing);

        TestTable testT = examples.getTestTable();
        TrainTable trainT = examples.getTrainTable();

        this.pushOutput(examples, 0);
        this.pushOutput (testT, 1);
        this.pushOutput (trainT, 2);
        if(numFires==0){
//            this.pushOutput (new Integer(breaks.length+1), 3);
            this.pushOutput (new Integer(N), 3);
        }

        numFires++;

        totalFires++;

//        if(debug)
//            System.out.println("Xval:numfires:"+numFires+" totalFires:"+totalFires+" n:"+N);
//       if(numFires==(breaks.length+1)){
          if (numFires == N){
                numFires=0;
               // breaks=null;
        }
    }


        private void printTable(Table t){
            System.out.println("PRINTING TABLE");
            int rows = t.getNumRows();
            int cols = t.getNumColumns();
            if (t instanceof PredictionTable)
                cols--;

            for (int c=0; c<cols; c++) {
                System.out.print(t.getColumnLabel(c)+ "   ");
            }
            System.out.println("");
            System.out.println("*************************************");

            for (int r=0; r<rows; r++) {
                for (int c=0; c<cols; c++) {
                    System.out.print(t.getString(r,c) + "   ");
                }
                System.out.println("");
            }
            System.out.println("*************************************");
        }



    /**
     * Hashtable : key - a Vector of output columns. value - Vector of row no Integers where this occurs
     * For a row of data, create a vector of output columns.
     *    If this vector is a key of the Hashtable
     *        Get the vector of row numbers where this output occurs;
     *        Add the current row number to this vector and put it back into the hash table
     *    Else if this vector is not present in the hashtable
     *        Create a new entry in the table - key: this vector; value: a new Vector
     *        containig the current row number.
     * Repeat this check for all rows.
     */
    private void createUniqueOutputToRowsHash() {
        uniqueOutputToRows = new Hashtable();
        Vector    output; //a vector that holds the output column values for a particular row
        Vector    rowIndices;
        int[] outputCols = table.toExampleTable().getOutputFeatures();

        for (int r=0; r< this.table.getNumRows(); r++) {
            output = new Vector(outputCols.length); // build a vector v of the outputCols
            for (int c=0; c< outputCols.length; c++) // of every row
                output.add(table.getString(r,outputCols[c]));
//printVector(output);
            // try to add output to the HashSet uniqueOutput,
            if (uniqueOutputToRows.containsKey(output)){ // success: lookup output in Hashtable
                rowIndices = (Vector)uniqueOutputToRows.get(output);
                rowIndices.add(new Integer(r));
                uniqueOutputToRows.put(output, rowIndices);
            }
            else {    // failure: create a new entry in the Hashtable
                rowIndices = new Vector(1);
                rowIndices.add(new Integer(r));
                uniqueOutputToRows.put(output, rowIndices);
            }
        }//outer for
//printHashtable(uniqueOutputToRows);
    }//createUniqueOutputToRowsHash

    private void printVector (Vector v) {
//DEBUG
      System.out.println(v);
    }

    private void printHashtable (Hashtable t) {
//DEBUG
        Set keyset = t.keySet();
        Enumeration keyEnum = t.keys();
        while ( keyEnum.hasMoreElements() ) {
            Vector rows = (Vector) t.get(keyEnum.nextElement());
            for (int i=0; i<rows.size(); i++)
                System.out.println(rows.elementAt(i));
            System.out.println("........................................................");
        }
    }

    protected void createTestTrainSets(){
        // loop through all the keys of the hash table and retrive stored row indices
            // if the number of row indices retrived 'n' is > N
                // randomly pick (int)(n/N) of them and add them to testing[]
                // add the remaining  to training[]
            // else if the number of row indices retrived 'n' is < N
                // add them all to the training set for now  (TODO)
            // repeat for every key in the hashtable

        testIndices = new Vector();
        trainIndices = new Vector();
        Random rdm0 = new Random(this.seed);
        Random rdm = new Random(this.seed);

        //Set keyset = uniqueOutputToRows.keySet();
        Enumeration keyEnum = uniqueOutputToRows.keys();
        while ( keyEnum.hasMoreElements() ) {
            Vector rowIndices = (Vector) uniqueOutputToRows.get(keyEnum.nextElement());
            if (rowIndices.size() < N) {         //if the number of row indices retrived 'n' is < N
                for (int i=0; i<rowIndices.size(); i++) {
                    int coin = rdm0.nextInt(2);//add them randomly to either the test or the train set
                    if (coin == 0)
                        testIndices.add(rowIndices.elementAt(i));
                    else if (coin == 1)
                        trainIndices.add(rowIndices.elementAt(i));
                }//for
            }//if

            else {
                HashSet testIndicesSet = new HashSet();//randomly pick (n/N) of them
                while (testIndicesSet.size() < (int)(rowIndices.size()/N)){
                    int index = rdm.nextInt(rowIndices.size());
                    testIndicesSet.add(rowIndices.elementAt(index));
                }//while

                testIndices.addAll(testIndicesSet);
                for (int i=0; i<rowIndices.size(); i++) {
                    if (! testIndicesSet.contains(rowIndices.elementAt(i)))
                        trainIndices.add(rowIndices.elementAt(i));
                }//for
            }//else
        }//while
System.out.println("Test Indices:");
this.printVector(testIndices);
System.out.println("Train Indices:");
this.printVector(trainIndices);
    }//createTestTrainSets

} //NFoldStatified