package ncsa.d2k.modules.core.optimize.ga.emo;

import java.io.*;
import java.util.*;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.optimize.util.*;
import ncsa.d2k.modules.core.transform.table.*;

/**
 * Evaluate an EMO population's fitness and contraints.
 */
public class EvaluateEMOPopulation
    extends EvaluateModule {

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOPopulation"};
    return types;
  }

  // used for the computing FF using a table
  private MutableTable table;
  private EMOPopulation population;
  private int numCols;

  // used for computing FF using executable
  private Table fitnessExecs;
  private String[] fitnessExecPathNames;
  private String[] fitnessExecInputFileNames;
  private String[] fitnessExecOutputFileNames;

  // used for computing C using executable
  private Table constraintExecs;
  private String[] constraintExecPathNames;
  private String[] constraintExecInputFileNames;
  private String[] constraintExecOutputFileNames;

  private boolean useExternalFitness = false;
  private boolean useExternalConstraints = false;
  private boolean firstTime = true;

  private Collection fitExecs;
  private Collection conExecs;
  private Collection inputFileNames;

  private int numFitnessExecs;
  private int numConstraintExecs;

  public void beginExecution() {
    useExternalFitness = false;
    useExternalConstraints = false;
    firstTime = true;

    fitnessExecs = null;
    population = null;
  }

  public void endExecution() {
    table = null;
    population = null;

    fitnessExecs = null;
    fitnessExecPathNames = null;
    fitnessExecInputFileNames = null;
    fitnessExecOutputFileNames = null;
  }

  /**
     Do the evaluation, using the evaluate function provided by the population
     subclass we received.
     @param outV the array to contain output object.
   */
  public void doit() throws Exception {
    // the current population of the GA run
    EMOPopulation pop = (EMOPopulation)this.pullInput(0);

    if (firstTime) {
      useExternalFitness = pop.getPopulationInfo().useExternalFitnessEvaluation;
      useExternalConstraints = pop.getPopulationInfo().
          useExternalConstraintEvaluation;
      firstTime = false;
    }

    if(population == null || pop != population) {
      if(useExternalFitness || useExternalConstraints) {
        Table fit = pop.getPopulationInfo().externalFitnessInfo;
        numFitnessExecs = fit.getNumRows();

        fitExecs = this.createFitnessExecCollection(pop);

        Table con = pop.getPopulationInfo().externalConstraintInfo;
        conExecs = this.createConstraintExecCollection(pop);
        if(con != null) {
          numConstraintExecs = con.getNumRows();
        }
        else {
          conExecs = new HashSet();
          numConstraintExecs = 0;
        }

        inputFileNames = this.createInputFileCollection();
      }
      if(!useExternalFitness || !useExternalConstraints) {
        table = (MutableTable) pop.getPopulationInfo().varNames.copy();
        table.setNumRows(((Population)pop).size());
        numCols = table.getNumColumns();
      }

      population = pop;
    }

    if (!useExternalFitness || !useExternalConstraints) {
      this.copyPopulationToTable(table, (Population)pop);

      if (!useExternalFitness) {
        // calculate the FF using transformations on a table
        this.evaluateFitness(table, pop);
      }

      if (!useExternalConstraints) {
        // calculate C using a filter on the table
        this.evaluateConstraints(table, pop);
      }

      // now remove the added columns so we can reuse the table again
      int newNumCols = table.getNumColumns();
      for (int i = numCols; i < newNumCols; i++) {
        table.removeColumn(numCols);
      }
    }

    if (useExternalFitness || useExternalConstraints) {
      // now write out the population
      Individual[] members = ((Population)pop).getMembers();
      if (members.length > 0) {
//        if (members[0] instanceof MOBinaryIndividual)
        if(pop instanceof MOPopulation)
          writeBinaryGenesToFile((Population)pop);
        else
          writeNumericGenesToFile((Population)pop);
      }

      if (useExternalFitness) {
        this.evaluateExecutableFitness((NsgaPopulation)pop, fitExecs);
      }
      if (useExternalConstraints) {
;//        this.evaluateExecutableConstraints((NsgaPopulation)pop, conExecs);
      }
    }

    this.pushOutput(pop, 0);
  }

  private void evaluateFitness(Table t, EMOPopulation pop) throws Exception {
    //extract the fitness function variables information
    Construction[] fitvarConstructions = pop.getPopulationInfo().
        fitnessVariableConstructions;

    //extract the fitness function information
    Construction[] fitConstructions = pop.getPopulationInfo().
        fitnessFunctionConstructions;

    // update the mutable table by calculating the
    //fitness function varaibles
    for (int i = 0; i < fitvarConstructions.length; i++) {
      AttributeTransform myvarct = new AttributeTransform(
          new Object[] {fitvarConstructions[i]});
      //apply the transformation
      myvarct.transform(table);
    }

    // update the mutable table by calculating the
    // fitness function
    for (int i = 0; i < fitConstructions.length; i++) {
      AttributeTransform myfitct = new AttributeTransform(
          new Object[] {fitConstructions[i]});
      //apply the transformation
      boolean retVal = myfitct.transform(table);
      if (!retVal) {
        throw new Exception("Could not calculate fitness function.");
      }
      int column = table.getNumColumns() - 1;

      // after updating the mutable table, we update the
      // population by filling in the corresponding fitness
      // function values into it.
      int sz = ((Population)pop).size();
      for (int mynewii = 0; mynewii < sz; mynewii++) {
        Individual mymember = ((Population)pop).getMember(mynewii);
        MOSolution myni = (MOSolution) mymember;
        myni.setObjective(i, table.getFloat(mynewii, column));
      }
    }

  }

  private void evaluateConstraints(Table t, EMOPopulation pop) throws Exception {
    //extract the fitness constraints variables information
    Construction[] constraintVarConstructions =
        pop.getPopulationInfo().constraintVariableConstructions;

    //extract the fitness constraints information
    Construction[] constraintVariableConstructions =
        pop.getPopulationInfo().constraintFunctionConstructions;

    // update the mutable table by calculating the
    // constraint varaibles
    for (int i = 0; i < constraintVarConstructions.length; i++) {
      AttributeTransform myvarct = new AttributeTransform(
          new Object[] {constraintVarConstructions[i]});
      //apply the transformation
      myvarct.transform(table);
    }

    // update the mutable table by calculating the constraints
    for (int i = 0; i < constraintVariableConstructions.length; i++) {
      /*int fitpos = 0;
           while ( ( (constraintVariableConstructions[i]).getLabel()).compareTo(newmt.
          getColumnLabel(fitpos)) != 0) {
        fitpos++;
               }*/
      //fitpos has the column number of the corresponding
      //constraint
      //remove that row
      //newmt.removeColumn(fitpos);
      //create a column transform for the corresponding
      //constraint
      EMOFilter myfitct = new EMOFilter(constraintVariableConstructions[i]);
      //apply the transformation
      // It is important to note that transform function adds
      // a new column to the table and hence we deleted the column
      // corresponding to the constraint before transforming
      myfitct.transform(table);
    }

    //this is the array of integers that contain the column
    // number of the constraints
    int[] myconstraintpos = new int[constraintVariableConstructions.length];
    for (int i = 0; i < constraintVariableConstructions.length; i++) {
      int fitpos = 0;
      while ( ( (constraintVariableConstructions[i]).label).compareTo(table.
          getColumnLabel(fitpos)) != 0) {
        fitpos++;
      }
      myconstraintpos[i] = fitpos;
    }

    //myconstraintpos now contains the column number of the constraints
    float constrvalue = 0;
    //calculate the constraint for each member
    // and update the population accordingly
    int sz = ((Population)pop).size();
    for (int mynewii = 0; mynewii < sz; mynewii++) {
      constrvalue = 0;
      for (int iijj = 0; iijj < constraintVariableConstructions.length; iijj++) {
        constrvalue += table.getFloat(mynewii, myconstraintpos[iijj]);
      }
      Individual mymember = ((Population)pop).getMember(mynewii);
      ( (NsgaSolution) mymember).setConstraint(constrvalue);
    }

  }

  private void copyPopulationToTable(Table t, Population pop) {
    Individual[] individuals = pop.getMembers();
    boolean binaryType = false;
    if (individuals.length > 0) {
      if (individuals[0] instanceof MOBinaryIndividual) {
        binaryType = true;
      }
      else {
        binaryType = false;
      }
    }

    int numOfvar = pop.getNumGenes();
    if (!binaryType) {
      for (int i = 0; i < pop.size(); i++) {
        double[] tabrows = (double[]) ( (pop.getMember(i)).getGenes());
        for (int j = 0; j < numOfvar; j++) {
          table.setDouble(tabrows[j], i, j);
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
        Table bounds = ( (EMOPopulation) pop).getPopulationInfo().
            boundsAndPrecision;

        int curPos = 0;
        for (int k = 0; k < traits.length; k++) {
          int numBits = traits[k].getNumBits();
          double num = 0.0d;
          double max = bounds.getDouble(k, 2);
          double min = bounds.getDouble(k, 1);
          double precision = bounds.getDouble(k, 3);

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
          table.setDouble(num, j, k);
        }
      }
    }
  }

  /**
     Compute the fitness for a single individual.
     @param ind the member to compute the fitness of.
   */
  public void evaluateIndividual(Individual memb) {}

  private class EMOFilter
      implements Transformation {
    // this is the construction that contains
    // the mathematical expression and the name of varaible
    private Object constructions;

    public EMOFilter(Object constructions) {
      this.constructions = constructions;
    }

    // this function transforms the mutable table passed to it
    // using the construction belonging to this class
    public boolean transform(MutableTable table) {
      // table is the mutable table to be transformed
      // create a filter expression for the table
      FilterExpression exp = new FilterExpression(table);
      // create a construction
      Construction current = (Construction) constructions;
      //this will contain the evaluated values of the expression
      Object evaluation = null;
      // an exception will be thrown if the expression is
      // not fit for the table
      try {
        exp.setExpression(current.expression);
        evaluation = exp.evaluate();
      }
      catch (Exception e) {
        e.printStackTrace();
        return false;
        // return false if failed to transform
      }
      // if expression is fit for the table then transform the table
      boolean[] b = (boolean[]) evaluation;
      float[] myf = new float[b.length];
      for (int myi = 0; myi < b.length; myi++) {
        if (b[myi] == true) {
          myf[myi] = 0;
        }
        else {
          myf[myi] = 1;
        }
      }
      table.addColumn(myf);
      // set the column label
      table.setColumnLabel(current.label, table.getNumColumns() - 1);
      // table is successfully transformed and hence return true
      return true;
    }
  }

  //////////////////////////////////////////////////////////////
  // Executable fitness

  private Collection createFitnessExecCollection(EMOPopulation pop) {
    fitnessExecs = pop.getPopulationInfo().externalFitnessInfo;

    fitnessExecPathNames = this.getFitnessExecPaths();
    fitnessExecInputFileNames = this.getInputFileNames();
    fitnessExecOutputFileNames = this.getOutputFileNames();

    HashSet rows = new HashSet();
    if (numFitnessExecs == 0) {
      return rows;
    }

    rows.add(new Integer(0));
    // determine which need to be run
    for (int j = 1; j < numFitnessExecs; j++) {

      String exec = fitnessExecPathNames[j];
      String in = fitnessExecInputFileNames[j];

      for (int k = j; k < numFitnessExecs; k++) {
        String exec2 = fitnessExecPathNames[j];
        String in2 = fitnessExecInputFileNames[j];

        if (!exec.equals(exec2) && !in.equals(in2)) {
          rows.add(new Integer(k));
        }
      }
    }
    return rows;
  }

  private Collection createConstraintExecCollection(EMOPopulation pop) {
    constraintExecs = pop.getPopulationInfo().externalFitnessInfo;

    constraintExecPathNames = this.getConstraintExecPaths();
    constraintExecInputFileNames = this.getConstraintInputFileNames();
    constraintExecOutputFileNames = this.getConstraintOutputFileNames();

    HashSet rows = new HashSet();
    if (numConstraintExecs == 0) {
      return rows;
    }

    rows.add(new Integer(0));
    // determine which need to be run
    for (int j = 1; j < numConstraintExecs; j++) {

      String exec = constraintExecPathNames[j];
      String in = constraintExecInputFileNames[j];

      for (int k = j; k < numConstraintExecs; k++) {
        String exec2 = constraintExecPathNames[j];
        String in2 = constraintExecInputFileNames[j];

        if (!exec.equals(exec2) && !in.equals(in2)) {
          rows.add(new Integer(k));
        }
      }
    }
    return rows;
  }

  private Collection createInputFileCollection() {
    HashSet inputs = new HashSet();

    for (int i = 0; i < fitnessExecInputFileNames.length; i++) {
      String s = fitnessExecInputFileNames[i];
      if (!inputs.contains(s)) {
        inputs.add(s);
      }
    }
    for (int i = 0; i < constraintExecInputFileNames.length; i++) {
      String s = constraintExecInputFileNames[i];
      if (!inputs.contains(s)) {
        inputs.add(s);
      }
    }
    return inputs;
  }

  /*
   * This writes the genes of individuals to input files for different
       * fitness function executables, that might be present in different directories
   */
  private void writeBinaryGenesToFile(Population popul) throws
      IOException {

    Iterator iter = inputFileNames.iterator();

    // instead of writing out the file multiple times, we will copy it
    int idx = 0;
    String firstFile = null;

    while (iter.hasNext()) {
      String fileName = (String) iter.next();
      if (idx == 0) {
        try {
          firstFile = fileName;
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
            double[] genes = ( (MOBinaryIndividual) popul.getMember(j)).
                toDouble();

            // the max and precision are contained in the boundsAndPrecision table
            Table bounds = ( (NsgaPopulation) popul).getPopulationInfo().
                boundsAndPrecision;

            int curPos = 0;
            for (int k = 0; k < traits.length; k++) {
              int numBits = traits[k].getNumBits();
              double num = 0.0d;
              double max = bounds.getDouble(k, 2);
              double min = bounds.getDouble(k, 1);
              double precision = bounds.getDouble(k, 3);

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
              pw.print(num + SPACE);
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
        catch (Exception e) {
          e.printStackTrace();
        }
      }
      else {
        // simply copy the file
        copyFile(new File(firstFile), new File(fileName));
      }
      idx++;
    }
  }

  /*
   * This writes the genes of individuals to input files for different
       * fitness function executables, that might be present in different directories
   */
  private void writeNumericGenesToFile(Population popul) throws
      IOException {

    Iterator iter = inputFileNames.iterator();

    // instead of writing out the file multiple times, we will copy it
    int idx = 0;
    String firstFile = null;

    while (iter.hasNext()) {
      String fileName = (String) iter.next();
      if (idx == 0) {
        try {
          firstFile = fileName;
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
              pw.print(genes[k] + SPACE);
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
        catch (Exception e) {

          e.printStackTrace();
          //System.exit(1);

        }
      }
      else {
        copyFile(new File(firstFile), new File(fileName));
      }
      idx++;
    }
  }

  private void evaluateExecutableFitness(NsgaPopulation popul, Collection rows) {
    NsgaSolution[] ni = new NsgaSolution[popul.size()];
    String outputFileName = new String();

    for (int i = 0; i < popul.size(); i++) {
      ni[i] = (NsgaSolution) popul.getMember(i);
    }

    int fitnessID = 0;
    for (int i = 0; i < numFitnessExecs; i++) {
      double[] fitness = new double[popul.size()];

      try {
        if (rows.contains(new Integer(i))) {
          ExecRunner er = new ExecRunner();
          //er.setMaxRunTimeSecs(0);
          er.exec(fitnessExecPathNames[i].toString());
        }

        // Obtain fitnesses after evaluation is over
        // reading fitness of all individuals from all output files
        outputFileName = fitnessExecOutputFileNames[i];
        fitness = this.readFitnessFromFile(popul, outputFileName);
        for (int j = 0; j < popul.size(); j++) {
          ni[j].setObjective(fitnessID, fitness[j]);
        }

        fitnessID = fitnessID + 1;
      }
      catch (Exception e) {
        e.printStackTrace();
      }

    } // i loop for number of fitnesses

  } // evaluateIndividual

/*  private void evaluateExecutableConstraints(NsgaPopulation popul, Collection rows) {
    NsgaSolution[] ni = new NsgaSolution[popul.size()];
    String outputFileName = new String();

    for (int i = 0; i < popul.size(); i++) {
      ni[i] = (NsgaSolution) popul.getMember(i);
    }

    int fitnessID = 0;
    for (int i = 0; i < numConstraintExecs; i++) {
      double[] fitness = new double[popul.size()];

      try {
        if (rows.contains(new Integer(i))) {
          ExecRunner er = new ExecRunner();
          //er.setMaxRunTimeSecs(0);
          er.exec(fitnessExecPathNames[i].toString());
          //System.out.println("<STDOUT>\n" + er.getOutString() + "</STDOUT>");
          //System.out.println("<STDERR>\n" + er.getErrString() + "</STDERR>");
        }

        // Exit nicely
        //System.exit(0);

        // Obtain fitnesses after evaluation is over
        // reading fitness of all individuals from all output files
        outputFileName = fitnessExecOutputFileNames[i];
        fitness = this.readFitnessFromFile(popul, outputFileName);
        for (int j = 0; j < popul.size(); j++) {
          ni[j].setObjective(fitnessID, fitness[j]);
        }

        fitnessID = fitnessID + 1;
      }
      catch (Exception e) {
        e.printStackTrace();
      }

    } // i loop for number of fitnesses
  }*/

  private static final String SPACE = " ";
  private static final String ONE = "1 ";
  private static final String ZERO = "0 ";

  /*
   * This read the fitness of individuals from output files for different
   * fitness function executables, that might be present in different directories
   */
  private double[] readFitnessFromFile(Population popul, String outFileName) throws
      IOException {
    double[] fit = new double[popul.size()];
    String[] sline = new String[2 * popul.size()];
    try {
      if (outFileName != null) {
        FileReader stringFileReader = new FileReader(outFileName);
        BufferedReader br = new BufferedReader(stringFileReader);

        int i;
        i = 0;
        sline[i] = br.readLine();
        while (sline[i] != null) {
          fit[i] = Double.parseDouble(sline[i]); //db.parseDouble(sline[i]);
          i = i + 1;
          sline[i] = br.readLine();
        }

        // close file and streams
        br.close();
        stringFileReader.close();

      }
    }
    catch (Exception e) {
      e.printStackTrace();
      //System.exit(1);
    }
    return fit;
  }

  /*
   * This returns an array with entire path names of fitness executables
   * and the directory structure where the executable is.
   */
  private String[] getFitnessExecPaths() {
    String[] execFitNames = new String[numFitnessExecs];
    for (int i = 0; i < numFitnessExecs; i++) {
      //execFitNames[i] = fitnessExecs.getString(i,1);
      execFitNames[i] = fitnessExecs.getString(i, 1);
    }
    return execFitNames;
  }

  /*
   * This returns an array with entire path names of fitness executables
   * and the directory structure where the executable is.
   */
  private String[] getConstraintExecPaths() {
    String[] execFitNames = new String[numFitnessExecs];
    for (int i = 0; i < numFitnessExecs; i++) {
      //execFitNames[i] = fitnessExecs.getString(i,1);
      execFitNames[i] = fitnessExecs.getString(i, 1);
    }
    return execFitNames;
  }

  /*
   * This returns the input file names of all the fitness functions that are evaluated using
   * an executable. These files would contain the population for evaluation.
   */
  private String[] getInputFileNames() {
    String[] execInputFileName = new String[numFitnessExecs];
    for (int i = 0; i < numFitnessExecs; i++) {
      execInputFileName[i] = fitnessExecs.getString(i, 2);
    }
    return execInputFileName;
  }

  /*
   * This returns the input file names of all the fitness functions that are evaluated using
   * an executable. These files would contain the population for evaluation.
   */
  private String[] getConstraintInputFileNames() {
    String[] execInputFileName = new String[numFitnessExecs];
    for (int i = 0; i < numFitnessExecs; i++) {
      execInputFileName[i] = fitnessExecs.getString(i, 2);
    }
    return execInputFileName;
  }

  /*
   * This returns the input file names of all the fitness functions that are evaluated using
   * an executable. These files would contain the population for evaluation.
   */
  private String[] getOutputFileNames() {
    String[] execOutputFileName = new String[numFitnessExecs];
    for (int i = 0; i < numFitnessExecs; i++) {
      execOutputFileName[i] = fitnessExecs.getString(i, 3);
    }
    return execOutputFileName;
  }

  /*
   * This returns the input file names of all the fitness functions that are evaluated using
   * an executable. These files would contain the population for evaluation.
   */
  private String[] getConstraintOutputFileNames() {
    String[] execOutputFileName = new String[numFitnessExecs];
    for (int i = 0; i < numConstraintExecs; i++) {
      execOutputFileName[i] = constraintExecs.getString(i, 3);
    }
    return execOutputFileName;
  }

  private static void copyFile(File src, File dest) throws
      FileNotFoundException, IOException {
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