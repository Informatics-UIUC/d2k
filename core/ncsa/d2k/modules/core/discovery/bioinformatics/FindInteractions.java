package ncsa.d2k.modules.core.discovery.bioinformatics;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.util.*;

public class FindInteractions
    extends ComputeModule {

  int numProperties = 4;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numProperties];

    pds[0] = new PropertyDescription(
        "queryGeneName",
        "Query Gene Name",
        "Name of the query gene");

    pds[1] = new PropertyDescription(
        "expertListGeneNames",
        "Expert List Gene Names",
        "A comma delimited list of gene names that in the expert list");

    pds[2] = new PropertyDescription(
        "numRounds",
        "Number of Rounds",
        "Number of rounds of graph expansion to perform");

    pds[3] = new PropertyDescription(
        "printExampleValues",
        "Print Example Values",
        "Report the feature values for each example in the example table");

    return pds;
  }

  private String QueryGeneName = "YMR284W";
  public void setQueryGeneName(String value) {
    this.QueryGeneName = value;
  }

  public String getQueryGeneName() {
    return this.QueryGeneName;
  }

  private String ExpertListGeneNameCSVList = "YNL216W,YDR432W,YCL011C";
  public void setExpertListGeneNames(String value) {
    this.ExpertListGeneNameCSVList = value;
  }

  public String getExpertListGeneNames() {
    return this.ExpertListGeneNameCSVList;
  }

  private int NumRounds = 2;
  public void setNumRounds(int value) {
    this.NumRounds = value;
  }

  public int getNumRounds() {
    return this.NumRounds;
  }

  private boolean PrintExampleValues = true;
  public void setPrintExampleValues(boolean value) {
    this.PrintExampleValues = value;
  }

  public boolean getPrintExampleValues() {
    return this.PrintExampleValues;
  }

  public String getModuleName() {
    return "Find Interactions";
  }

  public String getModuleInfo() {
    return "This module finds gene interactions through network analysis.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Interaction Example Table";
      case 1:
        return "interaction db systematic gene names";
      case 2:
        return "interaction db experimental system names";
      case 3:
        return "Master Example Table";
      case 4:
        return "master db systematic gene names";
      case 5:
        return "master db common locus names";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "An example table with inputs being interacting genes indicies, and output being the interaction index";
      case 1:
        return "An array of strings relating integer indices to interaction db systematic gene names";
      case 2:
        return "An array of strings relating integer indices to interaction db experimental system names";
      case 3:
        return "An example table with inputs being systematic genes name indicies, and output being the common locus name index";
      case 4:
        return "An array of strings relating integer indices to master db systematic gene names";
      case 5:
        return "An array of strings relating integer indices to master db common locus names";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "[S",
        "[S",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "[S",
        "[S"};

    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
    }
    return "No such output";
  }

  public String getOutputInfo(int i) {
    switch (i) {
    }
    return "No such output";
  }

  public String[] getOutputTypes() {
    String[] out = {};
    return out;
  }

  public void doit() throws Exception {

    ExampleTable interactionDBExampleTable = (ExampleTable)this.pullInput(0);
    int numRowsInInteractionDB = interactionDBExampleTable.getNumExamples();

    {
      int numInputs = interactionDBExampleTable.getNumInputs(0);
      int numOutputs = interactionDBExampleTable.getNumOutputs(0);

      if ( (numInputs != 2) || (numOutputs != 1)) {
        System.out.println("wrong table dimension (numInputs != 2) || (numOutputs != 1)");
        throw new Exception();
      }
    }

    String[] interactionDBSystematicGeneNames = (String[])this.pullInput(1);
    int numSystematicGeneNamesInInteractionDB = interactionDBSystematicGeneNames.length;

    String[] interactionDBExperimentalSystemNames = (String[])this.pullInput(2);
    int numExperimentalSystemNamesInInteractionDB = interactionDBExperimentalSystemNames.length;

    ExampleTable masterDBExampleTable = (ExampleTable)this.pullInput(3);
    int numMasterDBRows = masterDBExampleTable.getNumExamples();

    String[] masterDBSystematicGeneNames = (String[])this.pullInput(4);
    int numSystematicGenesNamesInMasterDB = masterDBSystematicGeneNames.length;

    String[] masterDBCommonLocusNames = (String[])this.pullInput(5);
    int numCommonLocusNamesInMasterDB = masterDBCommonLocusNames.length;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // rename SystmaticGeneNames in gene interaction DB to their common locus names from the mast gene db  //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    Hashtable masterDBCommonLocusNamesHashTable = new Hashtable();
    int masterDBSystematicGeneNameCount = 0;

    for (int j = 0; j < numMasterDBRows; j++) {

      String masterDBSystematicGeneName = masterDBSystematicGeneNames[masterDBExampleTable.getInputInt(j, 0)];
      String masterDBCommonLocusName = masterDBCommonLocusNames[masterDBExampleTable.getOutputInt(j, 0)];

      if (!masterDBCommonLocusNamesHashTable.containsKey(masterDBSystematicGeneName)) {
        masterDBCommonLocusNamesHashTable.put(masterDBSystematicGeneName, masterDBCommonLocusName);
        masterDBSystematicGeneNameCount++;
      }

    }

    for (int i = 0; i < numSystematicGeneNamesInInteractionDB; i++) {
      String interactionDBSystematicGeneName = interactionDBSystematicGeneNames[i];
      if (masterDBCommonLocusNamesHashTable.containsKey(interactionDBSystematicGeneName)) {
        interactionDBSystematicGeneNames[i] = (String) masterDBCommonLocusNamesHashTable.get(interactionDBSystematicGeneName);
      }

    }

    /////////////////////////////
    //  find query gene index  //
    /////////////////////////////

    int OriginalQueryGeneIndex = -1;
    for (int i = 0; i < numSystematicGeneNamesInInteractionDB; i++) {
      if (interactionDBSystematicGeneNames[i].equals(QueryGeneName)) {
        OriginalQueryGeneIndex = i;
        break;
      }
    }

    if (OriginalQueryGeneIndex == -1) {
      System.out.println("Error!  Query gene (" + QueryGeneName + ") not found");
      throw new Exception();
    }

    ////////////////////////////////
    //  find expert gene indices  //
    ////////////////////////////////

    int length = ExpertListGeneNameCSVList.length();

    int numCommas = 0;
    for (int i = 0; i < length; i++) {
      if (ExpertListGeneNameCSVList.charAt(i) == ',') {
        numCommas++;
      }
    }
    int numExpertListGenes = numCommas + 1;
    String[] expertGeneNames = new String[numExpertListGenes];

    int expertGeneCount = 0;
    int lastCharIndex = 0;
    for (int i = 0; i < length; i++) {
      if (ExpertListGeneNameCSVList.charAt(i) == ',') {
        expertGeneNames[expertGeneCount++] = ExpertListGeneNameCSVList.
            substring(lastCharIndex, i);
        lastCharIndex = i + 1;
      }
    }
    expertGeneNames[expertGeneCount] = ExpertListGeneNameCSVList.substring(
        lastCharIndex, length);
    for (int i = 0; i < numExpertListGenes; i++) {
      System.out.println("expertGeneNames[" + i + "] = " + expertGeneNames[i]);
    }

    int[] expertListGenes = new int[numExpertListGenes];
    for (int e = 0; e < numExpertListGenes; e++) {
      expertListGenes[e] = -1;
      for (int i = 0; i < numSystematicGeneNamesInInteractionDB; i++) {
        if (interactionDBSystematicGeneNames[i].equals(expertGeneNames[e])) {
          expertListGenes[e] = i;
          break;
        }
      }
      if (expertListGenes[e] == -1) {
        System.out.println("Error!  Expert gene (" + expertGeneNames[e] + ") not found");
        throw new Exception();
      }

    }
    if (false) {
      for (int i = 0; i < numExpertListGenes; i++) {
        System.out.println("expertListGenes[" + i + "] = " + expertListGenes[i]);
      }
    }

    //////////////////////////////////
    //  report summary information  //
    //////////////////////////////////

    System.out.println("");
    System.out.println("Query Gene               = " + QueryGeneName);
    System.out.println("Num Genes                = " + numSystematicGeneNamesInInteractionDB);
    System.out.println("Num Experimental Systems = " + numExperimentalSystemNamesInInteractionDB);
    System.out.println("Num Interactions         = " + numRowsInInteractionDB);
    System.out.println("");

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //  count number of different types (experimental systems) of interactions for each gene pair  //
    /////////////////////////////////////////////////////////////////////////////////////////////////

    byte[][] interactionCounts = new byte[numSystematicGeneNamesInInteractionDB][numSystematicGeneNamesInInteractionDB];
    int numInteractionTypes = numExperimentalSystemNamesInInteractionDB + 1;

    if (numInteractionTypes > 30) {
      System.out.println("numInteractionTypes > 30");
      throw new Exception();
    }
    int[][] interactionTypes = new int[numSystematicGeneNamesInInteractionDB][numSystematicGeneNamesInInteractionDB];
    boolean[][] interactionReported = new boolean[numSystematicGeneNamesInInteractionDB][numSystematicGeneNamesInInteractionDB];

    // count experiment system type interactions //
    for (int i = 0; i < numRowsInInteractionDB; i++) {

      int gene1 = interactionDBExampleTable.getInputInt(i, 0);
      int gene2 = interactionDBExampleTable.getInputInt(i, 1);
      int experimentalSystemType = interactionDBExampleTable.getOutputInt(i, 0);

      if ( (interactionTypes[gene1][gene2] & (1 << experimentalSystemType)) == 0) {
        interactionCounts[gene1][gene2]++;
        interactionCounts[gene2][gene1]++;

        interactionTypes[gene1][gene2] = interactionTypes[gene1][gene2] | (1 << experimentalSystemType);
        interactionTypes[gene2][gene1] = interactionTypes[gene2][gene1] | (1 << experimentalSystemType);
      }
    }

    // count expert list type interactions //
    for (int i = 0; i < numRowsInInteractionDB; i++) {

      int gene1 = interactionDBExampleTable.getInputInt(i, 0);
      int gene2 = interactionDBExampleTable.getInputInt(i, 1);

      boolean expertListInteraction = false;
      for (int e = 0; e < numExpertListGenes; e++) {

        if (gene1 == expertListGenes[e] || gene2 == expertListGenes[e]) {
          interactionCounts[gene1][gene2]++;
          interactionCounts[gene2][gene1]++;
          interactionTypes[gene2][gene1] = interactionTypes[gene2][gene1] | 1 << numExperimentalSystemNamesInInteractionDB; ;
          interactionTypes[gene1][gene2] = interactionTypes[gene1][gene2] | 1 << numExperimentalSystemNamesInInteractionDB; ;
        }
      }

    }

    ////////////////////////////
    //  initialize query set  //
    ////////////////////////////

    int maxNumPaths = 1000;
    int maxPathLength = NumRounds + 1;

    int numPaths = 0;
    int[] pathLengths = new int[maxNumPaths];
    int[][] pathGenes = new int[maxNumPaths][maxPathLength];

    pathLengths[numPaths] = 1;
    pathGenes[numPaths][0] = OriginalQueryGeneIndex;
    numPaths++;


    /////////////////////////////////////////////////////////////////////////////
    //  round by round, add to the set of related genes (i.e., the query set)  //
    /////////////////////////////////////////////////////////////////////////////

    for (int r = 0; r < NumRounds; r++) {

      // report round summary information //

      System.out.println("");
      System.out.println("");
      System.out.println("Round #" + (r + 1));
      System.out.println("numPaths = " + numPaths);

      for (int pathIndex = 0; pathIndex < numPaths; pathIndex++) {
        System.out.println("Path #" + (pathIndex + 1));
        for (int geneIndex = 0; geneIndex < pathLengths[pathIndex]; geneIndex++) {
          System.out.println("  Gene: " + interactionDBSystematicGeneNames[pathGenes[pathIndex][geneIndex]]);
        }
      }

      int newNumPaths = 0;
      int[] newPathLengths = new int[maxNumPaths];
      int[][] newPathGenes = new int[maxNumPaths][maxPathLength];

      // identify strong (>= 2 different interaction types) //

      int oldNumPaths = numPaths;
      for (int q = 0; q < oldNumPaths; q++) {

        int queryGeneIndex = pathGenes[q][pathLengths[q] - 1];

        for (int currentGeneIndex = 0; currentGeneIndex < numSystematicGeneNamesInInteractionDB; currentGeneIndex++) {

          if (interactionCounts[currentGeneIndex][queryGeneIndex] >= 2) {

            // report interaction if neccessary
            if (!interactionReported[queryGeneIndex][currentGeneIndex]) {

              System.out.println("found >= 2 interactions showing interaction between  " +
                                 interactionDBSystematicGeneNames[queryGeneIndex] + " and " +
                                 interactionDBSystematicGeneNames[currentGeneIndex]);
              for (int j = 0; j < numExperimentalSystemNamesInInteractionDB; j++) {
                if ( (interactionTypes[currentGeneIndex][queryGeneIndex] & (1 << j)) > 0) {

                  System.out.println("  Interaction with Experimental System  " + interactionDBExperimentalSystemNames[j]);

                }
              }
              if ( (interactionTypes[currentGeneIndex][queryGeneIndex] &
                    (1 << numExperimentalSystemNamesInInteractionDB)) > 0) {

                System.out.println("  Interaction with Expert List  ");

              }
              interactionReported[queryGeneIndex][currentGeneIndex] = true;
            }

            // create new paths //
            // check to see of gene is alread on path
            boolean geneAlreadyInPath = false;
            for (int g = 0; g < pathLengths[q]; g++) {
              if (currentGeneIndex == pathGenes[q][g]) {
                geneAlreadyInPath = true;
                break;
              }
            }

            if (!geneAlreadyInPath) {
              // copy current path
              for (int g = 0; g < pathLengths[q]; g++) {
                newPathGenes[newNumPaths][g] = pathGenes[q][g];
              }
              // add last node
              newPathGenes[newNumPaths][pathLengths[q]] = currentGeneIndex;
              newPathLengths[newNumPaths] = pathLengths[q] + 1;
              newNumPaths++;
            }

          }

        }
      }

      numPaths = newNumPaths;
      pathLengths = newPathLengths;
      pathGenes = newPathGenes;
    }

    for (int pathIndex = 0; pathIndex < numPaths; pathIndex++) {
      System.out.println("Path #" + (pathIndex + 1));
      for (int geneIndex = 0; geneIndex < pathLengths[pathIndex]; geneIndex++) {
        System.out.println("  Gene: " + interactionDBSystematicGeneNames[pathGenes[pathIndex][geneIndex]]);
      }
    }

  }
}