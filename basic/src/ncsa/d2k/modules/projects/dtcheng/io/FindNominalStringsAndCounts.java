package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;

public class FindNominalStringsAndCounts extends InputModule {

  private String NominalFeatureTypeList = "Y,N";

  public void setNominalFeatureTypeList(String value) {
    this.NominalFeatureTypeList = value;
  }

  public String getNominalFeatureTypeList() {
    return this.NominalFeatureTypeList;
  }

  public String getModuleName() {
    return "FindNominalStringsAndCounts";
  }

  public String getModuleInfo() {
    return "FindNominalStringsAndCounts";
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "NominalStrings";
    case 1:
      return "NominalCounts";
    case 2:
      return "NominalCountHashtable";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "NominalStrings";
    case 1:
      return "NominalCounts";
    case 2:
      return "NominalCountHashtables";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = { "[[Ljava.lang.String;", "[I", "[Ljava.util.Hashtable;" };
    return out;
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "StringArray";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] out = { "[Ljava.lang.String;" };
    return out;
  }

  public final String[] SortStrings(String[] labels) {
    Arrays.sort(labels, new StringComp());
    return labels;
  }

  private final class StringComp implements Comparator {
    public int compare(Object o1, Object o2) {
      String s1 = (String) o1;
      String s2 = (String) o2;
      return s1.toLowerCase().compareTo(s2.toLowerCase());
    }

    public boolean equals(Object o) {
      return super.equals(o);
    }
  }

  int NumColumns = 0;

  boolean[] NominalColumn = null;

  Hashtable[] NominalIndexCountHashtable = null;

  int[] UniqueValueCount = null;

  public void beginExecution() {

    String[] FeatureNumbersToMakeNominal = Utility.parseCSVList(NominalFeatureTypeList);
    NumColumns = FeatureNumbersToMakeNominal.length;

    NominalColumn = new boolean[NumColumns];

    for (int i = 0; i < NumColumns; i++) {
      if (FeatureNumbersToMakeNominal[i].equalsIgnoreCase("Y")) {
        NominalColumn[i] = true;
      }
    }

    NominalIndexCountHashtable = new Hashtable[NumColumns];

    for (int i = 0; i < NumColumns; i++)
      NominalIndexCountHashtable[i] = new Hashtable();

    // set UniqueValueCount to zero
    UniqueValueCount = new int[NumColumns];

  }

  public void doit() throws Exception {

    String[] StringArray = (String[]) this.pullInput(0);

    if (StringArray == null) {
      
      // output results

      String[][] NominalNames = new String[NumColumns][];
      int[] NominalCounts = new int[NumColumns];

      for (int f = 0; f < NumColumns; f++) {

        NominalNames[f] = new String[UniqueValueCount[f]];
        
        Enumeration InputKeys = NominalIndexCountHashtable[f].keys();
        for (int v = 0; v < UniqueValueCount[f]; v++) {
          String key = (String) InputKeys.nextElement();
          int [] ValueArray = (int []) NominalIndexCountHashtable[f].get(key);
          int index = ValueArray[0];
          int count = ValueArray[1];
          NominalNames[f][index] = key;
        }

        //NominalNames[f] = SortStrings(NominalNames[f]);
      }

      this.pushOutput(NominalNames, 0);
      this.pushOutput(UniqueValueCount, 1);
      this.pushOutput(NominalIndexCountHashtable, 2);

      beginExecution();
      
      return;
    }

    // update hash tables
    for (int f = 0; f < NumColumns; f++) {

      if (NominalColumn[f]) {

        String Name;

        Name = StringArray[f];
        
        int [] ValueArray = (int []) NominalIndexCountHashtable[f].get(Name);

        if (ValueArray == null) {
          int [] NewValueArray = new int[2];
          NewValueArray[0] = UniqueValueCount[f];
          NewValueArray[1] = 1;
          
          NominalIndexCountHashtable[f].put(Name, NewValueArray);
          UniqueValueCount[f]++;
        }
        else {
          ValueArray[1] = ValueArray[1] + 1;
        }
      }
    }

  }

}