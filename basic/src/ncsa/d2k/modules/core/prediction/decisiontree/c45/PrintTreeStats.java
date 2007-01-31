package ncsa.d2k.modules.core.prediction.decisiontree.c45;

import ncsa.d2k.core.modules.OutputModule;
import ncsa.d2k.modules.core.prediction.decisiontree.c45.DecisionTreeModel;
import ncsa.d2k.modules.core.prediction.decisiontree.c45.DecisionTreeNode;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.core.modules.PropertyDescription;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PrintTreeStats
    extends OutputModule {
  public PrintTreeStats() {
    super();
  }

  /**
   * Subclasses should override this method to perform their desired function.
   *
   * @throws Exception we have no idea what may happen during execution.
   * @todo Implement this ncsa.d2k.core.modules.ExecModule method
   */
  protected void doit() throws Exception {
     String file = (String) pullInput(1);
     BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    DecisionTreeModel model = (DecisionTreeModel) pullInput(0);

    Table tbl = vectorToTable(this.getTreeRules(model), model);

    int numCols = tbl.getNumColumns();

    for(int i=0; i<numCols; i++){
      writer.write(tbl.getColumnLabel(i));
      if(i<numCols-1)
        writer.write(this.getDelimiter());
    }
    writer.write("\n");

    for (int i = 0; i < numCols; i++) {
      writer.write("String");
      if (i < numCols - 1)
        writer.write(this.getDelimiter());
    }
    writer.write("\n");

    for(int i=0; i<tbl.getNumRows(); i++){
      for(int j=0; j<numCols; j++){
        writer.write(tbl.getString(i,j));
        if (j < numCols - 1)
        writer.write(this.getDelimiter());
      }
      writer.write("\n");
    }
    writer.close();
    pushOutput(tbl, 0);


    return;
    /*
    try{
      if(file != null ){
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        root.print(writer);
     writer.close();
      }else root.print();
    }catch(Exception e){
      root.print();
    }*/
  }

  public static final String TARGET = "target";
  public static final String CONDITION = "condition";


  public static void getNodeRules(DecisionTreeNode node, ArrayList rule, Vector allRules){
    try{
      if (node.isLeaf()) {
        rule.add(TARGET + " " + node.getLabel() + " 100");
        allRules.add(rule);
        return;
      }
      ArrayList myRule = (ArrayList)rule.clone();
      int total = node.getTotal();
      for (int i = 0; i < node.getOutputValues().length; i++) {
        int number = node.getOutputTally(node.getOutputValues()[i]);
        double percentage = ((int)(((double)number/(double)total)*10000))/((double)100);
        myRule.add(TARGET+ " "  + node.getOutputValues()[i] + " " + percentage);
      }
      allRules.add(myRule);

      for (int i = 0; i < node.getNumChildren(); i++) {
        DecisionTreeNode child = node.getChild(i);
        ArrayList _rule = (ArrayList) rule.clone();
        _rule.add(CONDITION + " " + node.getBranchLabel(i));
        getNodeRules(child, _rule, allRules);
      }
    }catch(Exception e){}
  }
  public static Vector getTreeRules(DecisionTreeModel tree){
    Vector vector = new Vector();
    ArrayList firstRule = new ArrayList();
    getNodeRules(tree.getRoot(), firstRule, vector);
    return vector;
  }

  public static void printTreeRules(DecisionTreeModel tree){
    Vector vec = getTreeRules(tree);
    Iterator it = vec.iterator();
    while(it.hasNext()){
      System.out.println(it.next());
    }
  }

  public Table vectorToTable(Vector rules, DecisionTreeModel tree){
    MutableTable mTbl = new MutableTableImpl();

    HashMap inputColumnLabelToIndex = new HashMap();
    HashMap outputColumnLabelToIndex = new HashMap();
    String[] outputs = tree.getUniqueOutputValues();
    String[] inputs = tree.getInputColumnLabels();
    Column[] allColumns = new Column[outputs.length + inputs.length];
    for(int i=0; i<inputs.length; i++){
      allColumns[i] = new StringColumn(rules.size());
      allColumns[i].setLabel(inputs[i]);
      inputColumnLabelToIndex.put(inputs[i], new Integer(i));
    }
    for(int i=inputs.length, j=0; j<outputs.length; j++){
      allColumns[i+j] = new StringColumn(rules.size());
      allColumns[i+j].setLabel(outputs[j]);
      outputColumnLabelToIndex.put(outputs[j], new Integer(i+j));
    }

    Iterator rulesIt = rules.iterator();
    int currRow = 0;
    while(rulesIt.hasNext()){
      Iterator condIt = ((List)rulesIt.next()).iterator();
      while(condIt.hasNext()){
        String[] conditions = ((String)condIt.next()).split(" ", 0);
        if(conditions[0].equals(CONDITION)){
          int index = ((Integer)inputColumnLabelToIndex.get(conditions[1])).intValue();
          String operator = conditions[2];
          String value = conditions[3];
          String bin = createBin(operator, value);
          String val = null;
          try{
            val = allColumns[index].getString(currRow);
          }catch(Exception e){}
          if(val == null || val.equals("?")){
            val = bin;
          }else{
            val = combineBins(val, bin);
          }
          allColumns[index].setString(val, currRow);
        }//in case this is a condition
        if (conditions[0].equals(TARGET)) {
          int index = ( (Integer) outputColumnLabelToIndex.get(conditions[1])).
              intValue();
          allColumns[index].setString(conditions[2], currRow);
        }

      }//iterating over the conditions in a rule
      currRow++;
    }//iterating over the rules

    mTbl.addColumns(allColumns);

    return mTbl;
  }

  /**
   * Given 2 bins for the same attribute - this method combines them into the
   * intersection of both bins. for example: [0,2] and [1,...] yields [1,2]
   * @param val1 String bin value to be intersected with val2
   * @param val2 String bin value to intersect with val1
   * @return String the intersection of val1 and val2.
   */
  public static String combineBins(String val1, String val2){
    int comma1 = val1.indexOf(",");
    int comma2 = val2.indexOf(",");
    String low_1 = val1.substring(1, comma1);
    String low_2 = val2.substring(1, comma2);
    String low = null;
    double compVal = compareBounds(low_1, low_2);
    if(compVal == Double.MAX_VALUE){
      low = low_1;
    }
    else if (compVal == Double.MIN_VALUE) {
      low = low_2;
    }else if(compVal < 0){
      low = low_2;
    }else if(compVal > 0){
      low = low_1;
    }else low = low_1;


    String up_1 = val1.substring(comma1+1, val1.length()-1);
   String up_2 = val2.substring(comma2+1, val2.length()-1);
   String up = null;
   compVal = compareBounds(up_1, up_2);
   if(compVal == Double.MAX_VALUE){
     up = up_1;
   }
   else if (compVal == Double.MIN_VALUE) {
     up = up_2;
   }else if(compVal < 0){
     up = up_1;
   }else if(compVal > 0){
     up = up_2;
   }else up = up_1;

   char[] first_parenth = new char[1];
   char parenth_1 = val1.charAt(0);
   char parenth_2 = val2.charAt(0);
   if(parenth_1 == parenth_2){
     first_parenth[0] = parenth_1;
   }else first_parenth[0] = '(';


   char[] second_parenth = new char[1];
    parenth_1 = val1.charAt(val1.length()-1);
    parenth_2 = val2.charAt(val2.length()-1);
   if(parenth_1 == parenth_2){
     second_parenth[0] = parenth_1;
   }else second_parenth[0] = ')';

   String retVal = new String(first_parenth) + low + "," + up + new String(second_parenth);


    return retVal;
  }

  /**
   * returns zero if equal.
   * if bound_2 is ... and bound_1 is not - returns Integer.MAX_VALUE
   * if bound_1 is ... and bound_2 is not - returns Integer.MIN_VALUE
   * if both a regular numbers - returns (bound_1 - bound_2)
   * @param bound_1 String
   * @param bound_2 String
   * @return int
   */
  public static double compareBounds(String bound_1, String bound_2){
    if(bound_1.equals(bound_2))
        return 0;
      try{
        double b_1 = Double.parseDouble(bound_1);
        double b_2 = Double.parseDouble(bound_2);
        return (b_1 - b_2);
      }catch(Exception e){
        if (bound_1.equals("..."))
          return Double.MIN_VALUE;
        if (bound_2.equals("..."))
          return Double.MAX_VALUE;
      }
      return 0;
  }

  public static final String GREATER = ">";
  public static final String GREATER_EQ = ">=";
  public static final String SMALLER = "<";
  public static final String SMALLERR_EQ = "<=";

  /**
   * creates a bin String from an operator (<, > etc.) and a value.
   * For example: operator <= and value 3 yield [...,3]
   * @param operator String the operator for the values
   * @param value String the number that is a lower or upper bound to the bin
   * @return String a bin formatted String.
   */
  public static String createBin(String operator, String value){
    String low = "...";
    String up = "...";
    String first="[";
    String second = "]";
    if(operator.charAt(0) == '>'){
      low = value;
      if(operator.equals(GREATER)){
        first = "(";
      }
    }else{
      up = value;
      if(operator.equals(SMALLER)){
        second = ")";
      }
    }
    String retVal = first + low + "," + up+ second;
    return retVal;
  }

  /**
   * This method will return a text description of the the input indexed by the
   * value passed in.
   *
   * @param index the index of the input we want the description of.
   * @return a text description of the indexed input.
   * @todo Implement this ncsa.d2k.core.modules.Module method
   */
  public String getInputInfo(int index) {
    if (index == 0) {
         return "The Decision Tree Model to generates stats file from.";
      } else {
         return "File name to write the output to.";
      }

  }

  public String getInputName(int i) {

     if (i == 0) {
        return "Decision Tree Model";
     } else {
        return "FileName";
     }
  }


  /**
   * This method returns an array of strings containing Java data types of the
   * input.
   *
   * @return the <b>fully qualified</b> names of the java objects taken as
   *   input.
   * @todo Implement this ncsa.d2k.core.modules.Module method
   */
  public String[] getInputTypes() {return new String[] {
                                               "ncsa.d2k.modules.core.prediction.decisiontree.c45.DecisionTreeModel",
                                               "java.lang.String"
                                            }; }


  /**
   * This method returns a Java String that describes the purpose of the
   * module, what it does and what it needs.
   *
   * @return a text description of the modules function
   * @todo Implement this ncsa.d2k.core.modules.Module method
   */
  public String getModuleInfo() {
    return "Accepts a Decision Tree model and prints it out to a delimited " +
        "file, as a table. Similar to naive bayse statistics.";
  }

  /**
   * This method returns a text description of the one of the outputs of the
   * module.
   *
   * @param outputIndex the index of the output we want the description of.
   * @return a text description of the indexed output.
   * @todo Implement this ncsa.d2k.core.modules.Module method
   */
  public String getOutputInfo(int outputIndex) {
    switch(outputIndex){
      case 0:
        return "A Table format of the decision tree model";
      default:
        return "no such output";
    }
  }
  public String getOutputName(int outputIndex) {
    switch(outputIndex){
      case 0:
        return "Table";
      default:
        return "no such output";
    }
 }


  /**
   * This method returns an array of strings containing Java data types of the
   * outputs.
   *
   * @return the <b>fully qualified</b> names of the java objects returned as
   *   results.
   * @todo Implement this ncsa.d2k.core.modules.Module method
   */
  public String[] getOutputTypes(){
  String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
    return types;
  }


 /* private String replaceEmptyValue = "";
  public void setReplaceEmptyValue(String str){replaceEmptyValue = str;}
  public String getReplaceEmptyValue(){return replaceEmptyValue;}
*/
  private String delimiter = ",";
  public void setDelimiter(String str){delimiter = str;}
  public String getDelimiter(){return delimiter;}
  public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] pds = new PropertyDescription[1];

  /*    pds[0] = new PropertyDescription("replaceEmptyValue", "Empty Value Replacement",
                                       "Empty values in conditions will be replaced by this string. " +
                                       "The default value is an empty string");
 */
     pds[0] = new PropertyDescription("delimiter", "Delimiter", "Set the delimiter to be used int he output file." +
                                      " The default is comma ','");
     return pds;
   }
}
