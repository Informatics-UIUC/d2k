package ncsa.d2k.modules.core.transform.table;
//package ncsa.d2k.modules.projects.smathur.transform.table;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.util.*;
import java.io.Serializable;

public class NFoldStats extends ncsa.d2k.core.modules.DataPrepModule{
        ExampleTable egT;
        TestTable testT;
        TrainTable trainT;
        Integer fold;

        int numRows;

	public String getInputInfo(int i){
		switch (i) {
			case 0: return "The table that contains the data";
			default: return "No such input";
		}
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable",
                                  "ncsa.d2k.modules.core.datatype.table.TestTable",
                                  "ncsa.d2k.modules.core.datatype.table.TrainTable"};
		return types;
	}

	public String getOutputInfo(int i){
/*		switch (i) {
			case 0: return "The ExampleTables with their train and test sets set";
			case 1: return "The N that was set in the properties";
			default: return "No such output";
		}
*/                return null;
	}

	public String[] getOutputTypes(){
//		String[] types = {"ncsa.d2k.modules.core.datatype.table.ExampleTable","java.lang.Integer"};
//		return types;
            return null;
	}

	public String getModuleInfo(){
            return "printing table..";
	}

	public void doit () throws Exception {
            egT = (ExampleTable) this.pullInput (0);
            testT = (TestTable) this.pullInput(1);
            trainT = (TrainTable) this.pullInput(2);
            //fold = (Integer) this.pullInput(3);
//System.out.println("fold = " + fold.intValue());

//            printEgT();
System.out.println("ExampleTable Distribution");
System.out.println("-------------------------");
            distribution(egT);

//            printTestT();
System.out.println("TestTable Distribution");
System.out.println("----------------------");
            distribution(testT);

//            printTrainT();
System.out.println("TrainTable Distribution");
System.out.println("-----------------------");
            distribution(trainT);
	}

        private void distribution (Table t) {
            HashMap hm = new HashMap();
            int[] outputCols = t.toExampleTable().getOutputFeatures();

            int numRows = t.getNumRows();
//for (int i=0; i<outputCols.length; i++)
//    System.out.println("outputCol: " + outputCols[i]);

System.out.println("Total Row Count: " + numRows);

            for (int r=0; r<t.getNumRows(); r++){
                Vector output = new Vector(outputCols.length);
                for (int c=0; c<outputCols.length; c++){
                    output.add(t.getString(r,outputCols[c]));
                }
                if (hm.containsKey(output)){ // retrieve from Hashmap and increment count
                    int count = ((Integer)hm.get(output)).intValue();
                    count++;
                    hm.put(output, new Integer(count));
                }
                else { // create a new entry in hashmap
                    hm.put(output, new Integer(1));
                }
            }

            for (Iterator i=hm.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry e = (Map.Entry) i.next();

                for (int j=0; j< ((Vector)e.getKey()).toArray().length; j++){
                    System.out.print( ((Vector)e.getKey()).toArray()[j] + " ");
                }
                System.out.println("");

//                System.out.println(e.getKey() + " Count: " + e.getValue() +
                int count = ((Integer)e.getValue()).intValue();
                float percent = (float)count/(float)numRows *100;

                System.out.println(" Count: " + count +
                " Percent: " +  percent);
//                System.out.println(" Count: " + e.getValue() +
//                " Percent: " +  (((((Integer)e.getValue()).intValue())*100))/numRows );
            }
        }

        private void printEgT(){
            System.out.println("PRINTING EXAMPLE TABLE");
            int rows = egT.getNumRows();
            int cols = egT.getNumColumns();
            if (egT instanceof PredictionTable)
                cols--;

            for (int c=0; c<cols; c++) {
                System.out.print(egT.getColumnLabel(c)+ "   ");
            }
            System.out.println("");
            System.out.println("*************************************");

            for (int r=0; r<rows; r++) {
                for (int c=0; c<cols; c++) {
                    System.out.print(egT.getString(r,c) + "   ");
                }
                System.out.println("");
            }
            System.out.println("*************************************");
        }

        private void printTestT(){
            System.out.println("PRINTING TEST TABLE");
            int rows = testT.getNumRows();
            int cols = testT.getNumColumns();
            if (testT instanceof PredictionTable)
                cols--;

            for (int c=0; c<cols; c++) {
                System.out.print(testT.getColumnLabel(c)+ "   ");
            }
            System.out.println("");
            System.out.println("*************************************");

            for (int r=0; r<rows; r++) {
                for (int c=0; c<cols; c++) {
                    System.out.print(testT.getString(r,c) + "   ");
                }
                System.out.println("");
            }
            System.out.println("*************************************");
        }

        private void printTrainT(){
            System.out.println("PRINTING TRAIN TABLE");
            int rows = trainT.getNumRows();
            int cols = trainT.getNumColumns();
            if (trainT instanceof PredictionTable)
                cols--;

            for (int c=0; c<cols; c++) {
                System.out.print(trainT.getColumnLabel(c)+ "   ");
            }
            System.out.println("");
            System.out.println("*************************************");

            for (int r=0; r<rows; r++) {
                for (int c=0; c<cols; c++) {
                    System.out.print(trainT.getString(r,c) + "   ");
                }
                System.out.println("");
            }
            System.out.println("*************************************");
        }
}