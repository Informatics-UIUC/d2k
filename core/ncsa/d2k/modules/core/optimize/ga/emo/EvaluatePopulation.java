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

/**
 * Evaluate the fitness functions and constraints for an EMOPopulation.
 */
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

  /** the population */
  private EMOPopulation population;
  /** the table to copy the population into, if needed for calculation of FF */
  private MutableTable populationTable;

  /** the fitness functions for the problem */
  private FitnessFunctions fitnessFunctions;
  /** the number of fitness variables defined by Constructions */
  private int numFitnessVars;
  /** the number of fitness functions defined by Constructions */
  private int numFitnessFunctions;
  /** the number of fitness functions calculated by an executle */
  private int numExternalFF;

  private Constraints constraints;
  private int numConstraintVars;
  private int numConstraintFunctions;
  private int numExternalConstraints;

  /** true if FF through constructions are present */
  private boolean hasFF;
  private boolean hasFVars;
  /** true if FF through executables are present */
  private boolean hasExternalFF;

  private boolean hasCon;
  private boolean hasCVars;
  private boolean hasExternalCon;

  private TIntArrayList externFFToExecute;
  private TIntArrayList externFFInputFiles;

  /** true if the individuals in the population are binary solutions */
  private boolean binaryType;

  public void doit() throws Exception {
    // pull in the population
    EMOPopulation pop = (EMOPopulation) pullInput(0);
    // if the population has changed, get the parameters of this new population
    if (pop != population) {
      population = pop;
      // get the fitness functions
      fitnessFunctions = pop.getParameters().fitnessFunctions;
      // create a table to copy the population into
      populationTable = pop.getParameters().decisionVariables.
          createVariableNameTable();

      // check to see if there are any fitness functions done by Constructions
      numFitnessVars = fitnessFunctions.getNumFitnessVariables();
      hasFVars = (numFitnessVars > 0);
      numFitnessFunctions = fitnessFunctions.getNumFitnessFunctions();
      hasFF = (numFitnessFunctions > 0);

      // check to see if there are any FF done by an external executable
      numExternalFF = fitnessFunctions.getNumExternFitnessFunctions();
      hasExternalFF = (numExternalFF > 0);

      // if no fitness functions were given, quit
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

      constraints = pop.getParameters().constraints;
      numConstraintVars = constraints.getNumConstraintVariables();
      hasCVars = (numConstraintVars > 0);
      numConstraintFunctions = constraints.getNumConstraintFunctions();
      hasCon = (numConstraintFunctions > 0);
      numExternalConstraints = constraints.getNumExternConstraints();
      hasExternalCon = (numExternalConstraints > 0);

      if(hasCon) {
        ;
      }

      if(hasExternalCon) {
        ;
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

    // copy the population into the population table
    // if any constructions are used
    if(hasFVars || hasCVars || hasFF || hasCon) {
      copyPopulationToTable();
    }

    // this is the number of columns in the table by default
    int numDecisionVariables = populationTable.getNumColumns();

    // first evaluate the variables and functions created by Constructions
    // the population must be copied into the populationTable
    if (hasFF) {
      // evaluate the fitness variables.  this is done by performing a
      // transformation on the table.  the last column is the new variable
      for (int i = 0; i < numFitnessVars; i++) {
        Construction c = fitnessFunctions.getFitnessVariable(i);
        AttributeTransform at = new AttributeTransform(new Object[] {c});
        boolean retVal = at.transform(populationTable);
        if (!retVal) {
          throw new Exception("Could not calculate fitness variable: " +
                              fitnessFunctions.getFitnessVariableName(i));
        }
      }

      // keep the indices of the FF columns, so that we can set the
      // values on the individuals later
      int[] ffColumns = new int[numFitnessFunctions];

      // evaluate the fitness functions.  this is done by performing a
      // transformation on the table.  the last column is the value of the FF
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

      // after transforming the table, we update the
      // population by filling in the corresponding fitness
      // function values into it.
      int sz = ( (Population) pop).size();
      for (int i = 0; i < sz; i++) {
        Individual mymember = ( (Population) pop).getMember(i);
        // set all the objective values for an MO Solution
        if (mymember instanceof MOSolution) {
          MOSolution myni = (MOSolution) mymember;
          for (int j = 0; j < numFitnessFunctions; j++) {
            myni.setObjective(j, populationTable.getDouble(i, ffColumns[j]));
          }
        }
        // set the single objective value for an SO Solution
        else {
          SOSolution mysi = (SOSolution)mymember;
          mysi.setObjective(populationTable.getDouble(i, ffColumns[0]));
        }
      }

      // remove all columns created by the transformations
      int numAddedColumns = numFitnessVars + numFitnessFunctions;
      for (int i = 0; i < numAddedColumns; i++) {
        populationTable.removeColumns(numDecisionVariables, numAddedColumns);
      }
    }

    // write out the population if external executables are used
    // we only need to write out the first file, and make copies for
    // the rest
    if(hasExternalFF || hasExternalCon) {
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
    }

    // finally evaluate the external fitness functions
    if (hasExternalFF) {
      // call the executables
      for(int i = 0; i < this.externFFToExecute.size(); i++) {
        this.callExternalFFExecutable(externFFToExecute.get(i));
      }

      // next read the files and set the objective values on the individuals
      for(int i = 0; i < this.numExternalFF; i++) {
        this.readExternalFFOutput(i);
      }
    }

    if(hasExternalCon) {

    }

    pushOutput(pop, 0);
  }

  /**
   * Copy the population into the populationTable.
   */
  private void copyPopulationToTable() {
    Population pop = (Population) population;
    // ensure that the table has the same number of rows as the number
    // of individuals in the population
    if (pop.size() != populationTable.getNumRows()) {
      populationTable.setNumRows(pop.size());
    }

    int numOfvar = pop.getNumGenes();
    // copy real-valued variables directly into the table
    if (!binaryType) {
      for (int i = 0; i < pop.size(); i++) {
        double[] tabrows = (double[]) ( (pop.getMember(i)).getGenes());
        for (int j = 0; j < numOfvar; j++) {
          populationTable.setDouble(tabrows[j], i, j);
        }
      }
    }

    // otherwise, decode binary variables so that the entries copied into the
    // table are real-valued
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
   * This writes the genes of individuals to a file.  The format is as follows:
   *  the first line contains the size of the population, the second line
   *  contains the length of each gene, and all following lines contain
   *  the actual genes, converted to a real value.
   * @param fileName the name of the file to copy to
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
        double[] genes = ( (BinaryIndividual) popul.getMember(j)).toDouble();

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

      Individual ni = (Individual) popul.getMember(0);
      double[] genes = (double[]) ni.getGenes();
      int numTraits = genes.length;

      // write population size in file
      pw.println(popul.size());
      // write length of each gene/chromosome
      pw.println(numTraits);
      // write genes
      for (int j = 0; j < popul.size(); j++) {
        //   genes = popul.getMember(j).getGenes().toString();

        ////////////////////////////////////////////////////////////
        // !!! LAM
        // if an IntInvidual is used, an int[] will be returned...
        // right now IntIndividuals are not used so thia is currently
        // not a problem
        genes = (double[]) ( (Individual) popul.getMember(j)).getGenes();
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

  private void callExternalConExecutable(int conIndex) throws Exception {
    ExecRunner er = new ExecRunner();
    er.exec(constraints.getExternConstraintExec(conIndex));
  }

  /**
   * Read the output from an external executable and set the objective
   * value on the individual
   * @param ffIndex the index of the ff
   * @throws Exception
   */
  private void readExternalFFOutput(int ffIndex) throws IOException {
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

    if(popul instanceof SOPopulation ) {
      // now that we have the fitness values, set the objective on the individual
      for (i = 0; i < popul.size(); i++) {
        ( (SOSolution) popul.getMember(i)).setObjective(fit[i]);
      }
    }
    else {
      // now that we have the fitness values, set the objective on the individual
      for (i = 0; i < popul.size(); i++) {
        ( (MOSolution) popul.getMember(i)).setObjective(
            numFitnessFunctions + ffIndex, fit[i]);
      }
    }
  }

  /**
   * Make a copy of a file.
   * @param src
   * @param dest
   * @throws IOException
   */
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