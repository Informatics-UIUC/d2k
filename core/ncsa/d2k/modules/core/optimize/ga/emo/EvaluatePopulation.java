package ncsa.d2k.modules.core.optimize.ga.emo;

import java.io.*;
import java.util.*;

import gnu.trove.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;

public class EvaluatePopulation
    extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  public void beginExecution() {
    fitnessFunctions = null;
    population = null;
    populationTable = null;
  }

  public void endExecution() {
    fitnessFunctions = null;
    population = null;
    populationTable = null;
  }

  private FitnessFunctions fitnessFunctions;
  private EMOPopulation population;
  private MutableTable populationTable;

  private int numFitnessVars;
  private int numFitnessFunctions;

  private int numExternalFF;

  private boolean hasFF;
  private boolean hasExternalFF;

  private TIntArrayList externFFToExecute;
  private TIntArrayList externFFInputFiles;

  private boolean binaryType;

  public void doit() throws Exception {
    EMOPopulation pop = (EMOPopulation) pullInput(0);
    if (pop != population) {
      population = pop;
      // get the fitness functions
      fitnessFunctions = pop.getParameters().fitnessFunctions;
      // create a table to hold the population
      populationTable = pop.getParameters().decisionVariables.
          createVariableNameTable();
      // we keep a copy just to be safe
      populationTable = (MutableTable) populationTable.copy();

      // check to see if there are any fitness functions done by Constructions
      numFitnessVars = fitnessFunctions.getNumFitnessVariables();
      numFitnessFunctions = fitnessFunctions.getNumFitnessFunctions();
      if (numFitnessFunctions > 0)
        hasFF = true;
      else
        hasFF = false;

      // check to see if there are any FF done by an external executable
      numExternalFF = fitnessFunctions.getNumExternFitnessFunctions();
      if (numExternalFF > 0)
        hasExternalFF = true;
      else
        hasExternalFF = false;

      if ( (numFitnessFunctions + numExternalFF) == 0)
        throw new Exception("No fitness functions could be found.");

      // if we have external FF, determine which executables need to
      // be executed each time (i.e. weed out duplicates) and which
      // files need to be written each time
      if (hasExternalFF) {
        // determine which executables need to be run.
        // if the same executable and input file occur, that only needs
        // to be executed once
        TIntHashSet rows = new TIntHashSet();
        rows.add(0);

        int numFitnessExecs = fitnessFunctions.getNumExternFitnessFunctions();

        // determine which need to be run
        for (int j = 1; j < numFitnessExecs; j++) {
          String exec = fitnessFunctions.getExternFitnessFunctionExec(j);
          String in = fitnessFunctions.getExternFitnessFunctionInput(j);

          for (int k = j; k < numFitnessExecs; k++) {
            String exec2 = fitnessFunctions.getExternFitnessFunctionExec(k);
            String in2 = fitnessFunctions.getExternFitnessFunctionInput(k);

            if (!exec.equals(exec2) && !in.equals(in2)) {
              rows.add(k);
            }
          }
        }
        externFFToExecute = new TIntArrayList(rows.toArray());
        externFFToExecute.sort();

        // find the unique input files
        HashSet inputs = new HashSet();
        inputs.add(fitnessFunctions.getExternFitnessFunctionInput(0));
        rows.add(0);
        for (int j = 1; j < numFitnessExecs; j++) {
          String in = fitnessFunctions.getExternFitnessFunctionInput(j);
          if (!inputs.contains(in)) {
            inputs.add(in);
            rows.add(j);
          }
        }

        externFFInputFiles = new TIntArrayList(rows.toArray());
        externFFInputFiles.sort();
      }

      // determine if the population contains binary or real-coded genes
      Individual[] individuals = ((Population)pop).getMembers();
      if (individuals.length > 0) {
        if (individuals[0] instanceof BinarySolution)
          binaryType = true;
        else
          binaryType = false;
      }
    }

    // first evaluate the variables and functions created by Transformations
    // the population must be copied into the populationTable
    if (hasFF) {
      // copy the population into the population table
      copyPopulationToTable();

      int numDecisionVariables = populationTable.getNumColumns();

      // evaluate the fitness variables
      for (int i = 0; i < numFitnessVars; i++) {
        Construction c = fitnessFunctions.getFitnessVariable(i);
        AttributeTransform at = new AttributeTransform(new Object[] {c});
        boolean retVal = at.transform(populationTable);
        if (!retVal) {
          throw new Exception("Could not calculate fitness variable: " +
                              fitnessFunctions.getFitnessVariableName(i));
        }
      }

      int[] ffColumns = new int[numFitnessFunctions];
      // evaluate the fitness functions
      for (int i = 0; i < numFitnessFunctions; i++) {
        Construction c = fitnessFunctions.getFitnessFunction(i);
        AttributeTransform at = new AttributeTransform(new Object[] {c});
        boolean retVal = at.transform(populationTable);
        if (!retVal) {
          throw new Exception("Could not calculate fitness function: " +
                              fitnessFunctions.getFitnessFunctionName(i));
        }
        ffColumns[i] = populationTable.getNumColumns() - 1;
      }

      // after updating the mutable table, we update the
      // population by filling in the corresponding fitness
      // function values into it.
      int sz = ( (Population) pop).size();
      for (int i = 0; i < sz; i++) {
        Individual mymember = ( (Population) pop).getMember(i);
        if (mymember instanceof MOSolution) {
          MOSolution myni = (MOSolution) mymember;
          for (int j = 0; j < numFitnessFunctions; j++) {
            myni.setObjective(j, populationTable.getDouble(i, ffColumns[j]));
          }
        }
        else {
          // how do you set objective value for an SO Individual?
        }
      }

      // remove all columns created by the transformations
      int numAddedColumns = numFitnessVars + numFitnessFunctions;
      for (int i = 0; i < numAddedColumns; i++) {
        populationTable.removeColumns(numDecisionVariables, numAddedColumns);
      }
    }

    // finally evaluate the external fitness functions
    if (hasExternalFF) {
      // first we need to write out the population
      // we only need to write out the first file, and make copies for
      // the rest
      int firstFile = this.externFFInputFiles.get(0);
      String firstFileName = fitnessFunctions.getExternFitnessFunctionInput(firstFile);
      if(binaryType)
        this.writeBinaryGenesToFile(firstFileName);
      else
        this.writeNumericGenesToFile(firstFileName);

      for(int i = 1; i < externFFInputFiles.size(); i++) {
        int idx = externFFInputFiles.get(i);
        String fileName = fitnessFunctions.getExternFitnessFunctionInput(idx);
        copyFile(new File(firstFileName), new File(fileName));
      }

      // next call the executables
      for(int i = 0; i < this.externFFToExecute.size(); i++) {
        this.callExternalFFExecutable(externFFToExecute.get(i));
      }

      // next read the files and set the objective values on the individuals
      for(int i = 0; i < this.numExternalFF; i++) {
        this.readExternalFFOutput(i);
      }
    }

    pushOutput(pop, 0);
    //pushOutput();
  }

  /**
   * Copy the population into the populationTable.
   */
  private void copyPopulationToTable() {
    Population pop = (Population) population;
    if (pop.size() != populationTable.getNumRows()) {
      populationTable.setNumRows(pop.size());
    }

    int numOfvar = pop.getNumGenes();
    if (!binaryType) {
      for (int i = 0; i < pop.size(); i++) {
        double[] tabrows = (double[]) ( (pop.getMember(i)).getGenes());
        for (int j = 0; j < numOfvar; j++) {
          populationTable.setDouble(tabrows[j], i, j);
        }
      }
    }

    // do the decoding so that the entries copied into the table are
    // real-valued
    else {
      int numTraits = 0;
      BinaryRange[] traits = (BinaryRange[]) pop.getTraits();

      // for each individual
      for (int j = 0; j < pop.size(); j++) {
        double[] genes = ( (MOBinaryIndividual) pop.getMember(j)).toDouble();

        // the max and precision are contained in the boundsAndPrecision table
        DecisionVariables dv = population.getParameters().decisionVariables;

        int curPos = 0;
        for (int k = 0; k < traits.length; k++) {
          int numBits = traits[k].getNumBits();
          double num = 0.0d;
          double max = dv.getVariableMax(k);
          double min = dv.getVariableMin(k);
          double precision = dv.getVariablePrecision(k);

          double interval = (max - min) * precision;

          // this is one trait
          for (int l = 0; l < numBits; l++) {
            if (genes[curPos] != 0) {
              num = num + Math.pow(2.0, l);
            }
            curPos++;
          }

          // if it is above the max, scale it down
          num = num * precision + min;
          if (num > max) {
            num = max;
          }
          if (num < min) {
            num = min;
          }
          populationTable.setDouble(num, j, k);
        }
      }
    }
  }

  private static final String SPACE = " ";

  /*
   * This writes the genes of individuals to input files for different
   * fitness function executables, that might be present in different directories
   *
   * Right now this only works for MO populations!!!
   */
  private void writeBinaryGenesToFile(String fileName) throws IOException {
    Population popul = (Population) population;

    try {
      FileWriter stringFileWriter = new FileWriter(fileName);
      BufferedWriter bw = new BufferedWriter(stringFileWriter);
      PrintWriter pw = new PrintWriter(bw);

      int numTraits = 0;

      BinaryRange[] traits = (BinaryRange[]) popul.getTraits();

      // write population size in file
      pw.println(popul.size());

      // write length of each gene/chromosome
      pw.println(traits.length);

      // write genes
      for (int j = 0; j < popul.size(); j++) {
        double[] genes = ( (MOBinaryIndividual) popul.getMember(j)).toDouble();

        // the max and precision are contained in the DecisionVariables
        DecisionVariables dv = population.getParameters().decisionVariables;

        int curPos = 0;
        for (int k = 0; k < traits.length; k++) {
          int numBits = traits[k].getNumBits();
          double num = 0.0d;
          double max = dv.getVariableMax(k);
          double min = dv.getVariableMin(k);
          double precision = dv.getVariablePrecision(k);

          double interval = (max - min) * precision;

          // this is one trait
          for (int l = 0; l < numBits; l++) {
            if (genes[curPos] != 0) {
              num = num + Math.pow(2.0, l);
            }
            curPos++;
          }

          // if it is above the max, scale it down
          num = num * precision + min;
          if (num > max) {
            num = max;
          }
          if (num < min) {
            num = min;
          }
          //pw.print(num + SPACE);
          pw.print(num);
          pw.print(SPACE);
        }

        pw.println();
      }
      pw.println();
      // close file and streams
      pw.flush();
      bw.flush();
      stringFileWriter.flush();
      pw.close();
      bw.close();
      stringFileWriter.close();
    }
    catch (IOException e) {
      //pw.close();
      //bw.close();
      //stringFileWriter.close();
      throw e;
    }
  }

  /*
   * This writes the genes of individuals to input files for different
   * fitness function executables, that might be present in different directories
   *
   * Right now this only works for MO populations!!!
   */
  private void writeNumericGenesToFile(String fileName) throws
      IOException {
    Population popul = (Population) population;

    try {
      FileWriter stringFileWriter = new FileWriter(fileName);
      BufferedWriter bw = new BufferedWriter(stringFileWriter);
      PrintWriter pw = new PrintWriter(bw);

      MONumericIndividual ni = (MONumericIndividual) popul.getMember(0);
      double[] genes = (double[]) ni.getGenes();
      int numTraits = genes.length;

      // write population size in file
      pw.println(popul.size());
      // write length of each gene/chromosome
      pw.println(numTraits);
      // write genes
      for (int j = 0; j < popul.size(); j++) {
        //   genes = popul.getMember(j).getGenes().toString();
        genes = (double[]) ( (MONumericIndividual) popul.getMember(j)).
            getGenes();
        numTraits = genes.length;

        // write to file
        for (int k = 0; k < numTraits; k++) {
          //pw.print(genes[k] + SPACE);
          pw.print(genes[k]);
          pw.print(SPACE);
        }
        pw.println();
      }
      pw.println();
      // close file and streams
      pw.flush();
      bw.flush();
      stringFileWriter.flush();
      pw.close();
      bw.close();
      stringFileWriter.close();
    }
    catch (IOException e) {
      //pw.close();
      //bw.close();
      //stringFileWriter.close();
      throw e;
    }
  }

  /**
   * Call an external executable
   * @param ffIndex
   * @throws Exception
   */
  private void callExternalFFExecutable(int ffIndex) throws Exception {
    ExecRunner er = new ExecRunner();
    er.exec(fitnessFunctions.getExternFitnessFunctionExec(ffIndex));
  }

  /**
   * Read the output from an external executable and set the objective
   * value on the individual
   * @param ffIndex
   * @throws Exception
   */
  private void readExternalFFOutput(int ffIndex) throws Exception {
    Population popul = (Population)population;
    double[] fit = new double[popul.size()];

    String outFileName = fitnessFunctions.getExternFitnessFunctionOutput(ffIndex);
    FileReader stringFileReader = new FileReader(outFileName);
    BufferedReader br = new BufferedReader(stringFileReader);

    int i = 0;
    String s = br.readLine();
    while (s != null) {
      fit[i] = Double.parseDouble(s);
      i++;
      s = br.readLine();
    }

    // close file and streams
    stringFileReader.close();
    br.close();

    // now that we have the fitness values, set the objective on the individual
    for(i = 0; i < popul.size(); i++) {
      ((NsgaSolution)popul.getMember(i)).setObjective(
          numFitnessFunctions+ffIndex, fit[i]);
    }
  }


  private static void copyFile(File src, File dest) throws
      IOException {
    if (src.equals(dest)) {
      return;
    }

    BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
        dest));

    try {
      byte[] b = new byte[2048];
      int read = 0;

      while ( (read = in.read(b)) != -1) {
        out.write(b, 0, read);
      }

      in.close();
      out.close();
    }
    catch (IOException ioe) {
      in.close();
      out.close();
      throw ioe;
    }
  }
}