package ncsa.d2k.modules.core.optimize.ga.emo.gui;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.emo.crossover.*;
import ncsa.d2k.modules.core.optimize.ga.emo.mutation.*;
import ncsa.d2k.modules.core.optimize.ga.emo.selection.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;

public class Params
    extends UIModule {

  public String[] getInputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOParams"};
  }

  public String[] getOutputTypes() {
    return new String[] {
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOParams",
        "ncsa.d2k.modules.core.optimize.ga.emo.EMOParams"};
  }

  public String getInputInfo(int i) {
    return "";
  }

  public String getOutputName(int i) {
    return "Parameters";
  }

  public String getInputName(int i) {
    return "Parameters";
  }

  public String getOutputInfo(int i) {
    return "";
  }

  public String getModuleInfo() {
    return "";
  }

  protected UserView createUserView() {
    return new ParamsView();
  }

  public String[] getFieldNameMapping() {
    return null;
  }

  public PropertyDescription[] getPropertiesDescriptions() {
    return new PropertyDescription[0];
  }

  private CachedParams cachedParams;
  public void setCachedParams(CachedParams cp) {
    cachedParams = cp;
  }

  public CachedParams getCachedParams() {
    return cachedParams;
  }

  /**
   * Contains the cached parameters that were input.
   */
  private class CachedParams
      implements java.io.Serializable {

    /** the number of variables in the problem */
    int numVariables;
    /** the number of objectives in the problem */
    int numObjectives;
    /** the total string length */
    int totalStringLength;
    /** the names of the variables in the problem */
    String[] variableNames;

    /** the recommended values from the table model */
    String[] recommended;
    /** the overriden values from the table model */
    String[] override;
    /** the estimated time required */
    String estimatedTimeReq;
    /** the maximum run time */
    String maxRunTime;
    /** the difference in time */
    String diff;

    /** the name of the selected mutation type */
    String mutName;
    /** the properties for the selected mutation */
    HashMap mutProps;
    /** the name of the selected crossover type */
    String crossName;
    /** the properties for the selected crossover */
    HashMap crossProps;
    /** the name of the selected selection type */
    String selName;
    /** the properties for the selected selection type */
    HashMap selProps;
    /** true if binary individuals should be created, false otherwise */
    boolean binaryInd;
  }

  private class ParamsView
      extends JUserPane {
    /** the parameters for EMO */
    transient private EMOParams params;
    /** the table model, holds several parameters */
    transient private ParamsTableModel paramsModel;
    /** the estimated time that the evaluation will take */
    transient private JTextField estimatedTime;
    /** the maximum run time a user is willing to wait */
    transient private JTextField maxRunTime;
    /** */
    transient private JLabel numSolutionsLabel;
    /** the number of solutions desired */
    transient private JTextField numSolutions;
    /** the advanced settings */
    transient private AdvSettingsPanel adv;
    /** the frame that holds the advanced settings */
    transient private JFrame advFrame;

    /** the label in the TimePanel that shows the estimated run time */
    transient private JLabel estimatedRunTime;
        /** the label in the TimePanel that shows the user-specified max run time */
    transient private JLabel specifiedMaxTime;
    /** the label in the TimePanel that shows the difference in times */
    transient private JLabel difference;
    transient private int numObjectives;

    transient private Mutation selectedMutation;
    transient private Crossover selectedCrossover;
    transient private Selection selectedSelection;

    private static final int OVERRIDE = 2;
    private static final int REC = 1;
    private static final int POP_SIZE = 0;
    private static final int MAX_GEN = 1;
    private static final int TOURNAMENT = 2;
    private static final int CROSSOVER_RATE = 3;
    private static final int MUTATION_RATE = 4;
    private static final int GEN_GAP = 5;

    /**
     * Add the components to this user view.
     */
    public void initView(ViewModule vm) {
      // ParamsPanel contains the jtable, times, and advanced settings
      ParamsPanel pp = new ParamsPanel();
      pp.setBorder(new EmptyBorder(0, 10, 10, 0));
      setLayout(new BorderLayout());
      add(pp, BorderLayout.WEST);

      // time panel contains the time graph, the time labels, and
      // optimizing tips
      TimePanel tp = new TimePanel();
      tp.setBorder(new EmptyBorder(0, 10, 10, 60));
      add(tp, BorderLayout.EAST);

      JButton done = new JButton("Done");
      JButton abort = new JButton("Abort");
      abort.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          viewCancel();
        }
      });
      done.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          done();
        }
      });

      JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      pnl.add(abort);
      pnl.add(done);

      add(pnl, BorderLayout.SOUTH);
    }

    /**
     * When done is pressed, gather all the parameters and
     * set them on the EMOParams object.  Also, create a
     * new CachedParams object and set the appropriate values
     * so that the next time this module is run the parameters can
     * be re-loaded.
     */
    private void done() {
      // gather all parameters
      for(int i = 0; i < adv.mutationRadio.length; i++) {
        if(adv.mutationRadio[i].isSelected()) {
          params.mutation = adv.mutationRadio[i].mutation;  
          
          HashMap propLookup = new HashMap();
          Property[] p = params.mutation.getProperties();
          if(p == null)
            continue;
            
          for(int j = 0; j < p.length; j++)
            propLookup.put(p[j].getName(), p[j]);
          
          // now make sure that the values of the properties are set
          HashSet propPanels = adv.mutationRadio[i].propertyPanels; 
          
          // iterate through the property panels
          Iterator iter = propPanels.iterator();
          while(iter.hasNext()) {
            AdvSettingsPanel.PropPanel pp = (AdvSettingsPanel.PropPanel)iter.next();  
             
            String propName = pp.label.getText();
            // now match it to a property.
            Property property = (Property)propLookup.get(propName);
            switch(property.getType()) {
                case (Property.DOUBLE):
                  try {
                    Double d = new Double(pp.jtf.getText());
                    property.setValue(d);
                  }
                  catch (NumberFormatException nfe) {
                  }
                  break;
                case (Property.INT):
                  try {
                    Integer ii = new Integer(pp.jtf.getText());
                    property.setValue(ii);  
                  }
                  catch (NumberFormatException nfe) {
                  }
                  break;
                case (Property.STRING):
                  property.setValue(pp.jtf.getText());
                  break;
                default:
                  break;
            } // switch
          } // while
        } // if
      } // for
      
      for(int i = 0; i < adv.crossoverRadio.length; i++) {
        if(adv.crossoverRadio[i].isSelected()) {
          params.crossover = adv.crossoverRadio[i].crossover;
          
          HashMap propLookup = new HashMap();
          Property[] p = params.crossover.getProperties();
          if(p == null)
            continue;

          for(int j = 0; j < p.length; j++)
            propLookup.put(p[j].getName(), p[j]);

          // now make sure that the values of the properties are set
          HashSet propPanels = adv.crossoverRadio[i].propertyPanels; 

          // iterate through the property panels
          Iterator iter = propPanels.iterator();
          while(iter.hasNext()) {
            AdvSettingsPanel.PropPanel pp = (AdvSettingsPanel.PropPanel)iter.next();  

            String propName = pp.label.getText();
            // now match it to a property.
            Property property = (Property)propLookup.get(propName);
            switch(property.getType()) {
                case (Property.DOUBLE):
                  try {
                    Double d = new Double(pp.jtf.getText());
                    property.setValue(d);
                  }
                  catch (NumberFormatException nfe) {
                  }
                  break;
                case (Property.INT):
                  try {
                    Integer ii = new Integer(pp.jtf.getText());
                    property.setValue(ii);  
                  }
                  catch (NumberFormatException nfe) {
                  }
                  break;
                case (Property.STRING):
                  property.setValue(pp.jtf.getText());
                  break;
                default:
                  break;
            } // switch
          } // while
        } // if
      } 
      
      for(int i = 0; i < adv.selectionRadio.length; i++) {
        if(adv.selectionRadio[i].isSelected()) {
          params.selection = adv.selectionRadio[i].selection;
          HashMap propLookup = new HashMap();
          Property[] p = params.selection.getProperties();
          if(p == null)
            continue;

          for(int j = 0; j < p.length; j++)
            propLookup.put(p[j].getName(), p[j]);

          // now make sure that the values of the properties are set
          HashSet propPanels = adv.selectionRadio[i].propertyPanels; 

          // iterate through the property panels
          Iterator iter = propPanels.iterator();
          while(iter.hasNext()) {
            AdvSettingsPanel.PropPanel pp = (AdvSettingsPanel.PropPanel)iter.next();  

            String propName = pp.label.getText();
            // now match it to a property.
            Property property = (Property)propLookup.get(propName);
            switch(property.getType()) {
                case (Property.DOUBLE):
                  try {
                    Double d = new Double(pp.jtf.getText());
                    property.setValue(d);
                  }
                  catch (NumberFormatException nfe) {
                  }
                  break;
                case (Property.INT):
                  try {
                    Integer ii = new Integer(pp.jtf.getText());
                    property.setValue(ii);  
                  }
                  catch (NumberFormatException nfe) {
                  }
                  break;
                case (Property.STRING):
                  property.setValue(pp.jtf.getText());
                  break;
                default:
                  break;
            } // switch
          } // while
          
        }
      } 

      double popSize;
      // if the overriden population size is valid, use that
      try {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(POP_SIZE, OVERRIDE));
      }
      // otherwise use the recommended value
      catch (Exception ex) {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(POP_SIZE, REC));
      }
      params.populationSize = (int) popSize;

      // if the overriden max gens is valid, use that
      try {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(MAX_GEN, OVERRIDE));
      }
      // otherwise use the recommended value
      catch (Exception ex) {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(MAX_GEN, REC));
      }
      params.maxGenerations = (int) popSize;

      // if the overriden tournament size is valid, use that
      try {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(TOURNAMENT, OVERRIDE));
      }
      // otherwise use the recommended value
      catch (Exception ex) {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(TOURNAMENT, REC));
      }
      params.selection.setTournamentSize( (int) popSize);

      // if the overriden crossover rate is valid, use that
      try {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(CROSSOVER_RATE, OVERRIDE));
      }
      // otherwise use the recommended value
      catch (Exception ex) {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(CROSSOVER_RATE, REC));
      }
      params.crossover.setCrossoverRate(popSize);

      // if the overriden mutation rate is valid, use that
      try {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(MUTATION_RATE, OVERRIDE));
      }
      // otherwise use the recommended value
      catch (Exception ex) {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(MUTATION_RATE, REC));
      }
      params.mutation.setMutationRate(popSize);

      // if the overriden generation gap is valid, use that
      try {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(GEN_GAP, OVERRIDE));
      }
      // otherwise use the recommended value
      catch (Exception ex) {
        popSize = Double.parseDouble(
            (String) paramsModel.getValueAt(GEN_GAP, REC));
      }
      params.crossover.setGenerationGap(popSize);

      // get the number of solutions
      int numSol;
      try {
        String s = numSolutions.getText();
        numSol = Integer.parseInt(s);
      }
      catch (Exception e) {
        numSol = 0;
      }

      // save the values of parameters so the
      // gui can show these values the next time the module is run
      // the values are saved in a CachedParams object
      CachedParams cp = new CachedParams();

      // save the number of objectives
      cp.numObjectives = numObjectives;
      // save the number of decision variables
      cp.numVariables = params.decisionVariables.getNumVariables();
      // save the total string length
      cp.totalStringLength = (int) params.decisionVariables.
          getTotalStringLength();
      // save the variable names
      String[] varNames = new String[cp.numVariables];
      for (int i = 0; i < cp.numVariables; i++) {
        varNames[i] = params.decisionVariables.getVariableName(i);
      }
      cp.variableNames = varNames;

      // save the recommended values from the table model
      String[] recVals = paramsModel.data[REC];
      String[] recCpy = new String[recVals.length];
      System.arraycopy(recVals, 0, recCpy, 0, recVals.length);
      cp.recommended = recCpy;

      // save the overriden values from the table model
      String[] overVals = paramsModel.data[OVERRIDE];
      String[] overCpy = new String[recVals.length];
      System.arraycopy(overVals, 0, overCpy, 0, overVals.length);
      cp.override = overCpy;

      // save the maximum run time
      cp.maxRunTime = maxRunTime.getText();
      // save the estimated time required
      cp.estimatedTimeReq = estimatedTime.getText();
      // save the difference
      cp.diff = difference.getText();

      // need to save which type of mutation, sel, crossover selected
      // save the name of the type selected
      cp.mutName = params.mutation.getName();
      // we also keep a hash map for the extra properties of mutation
      // key is prop name, the value is the property value
      HashMap mutProps = new HashMap();
      Property[] props = params.mutation.getProperties();
      if (props != null) {
        for (int i = 0; i < props.length; i++) {
          mutProps.put(props[i].getName(), props[i].getValue());
        }
      }
      cp.mutProps = mutProps;

      cp.crossName = params.crossover.getName();
      HashMap crossProps = new HashMap();
      props = params.crossover.getProperties();
      if (props != null) {
        for (int i = 0; i < props.length; i++) {
          crossProps.put(props[i].getName(), props[i].getValue());
        }
      }
      cp.crossProps = crossProps;

      cp.selName = params.selection.getName();
      HashMap selProps = new HashMap();
      props = params.selection.getProperties();
      if (props != null) {
        for (int i = 0; i < props.length; i++) {
          selProps.put(props[i].getName(), props[i].getValue());
        }
      }
      cp.selProps = selProps;

      // need to save binary/real individuals
      cp.binaryInd = adv.binaryInd.isSelected();

      setCachedParams(cp);

      viewDone("done");
      pushOutput(params, 0);
    }

    public void setInput(Object o, int i) {
      params = (EMOParams) o;

      // how many objectives are there?
      numObjectives = params.fitnessFunctions.getTotalNumFitnessFunctions();

      // now get the cached params.  check if the cached params match this
      // population.  if so, fill in everything with the cached params

      // cached params are assumed to be equal if the number of objectives,
      // the number of decision variables, and the names of the decision vars
      // are the same.
      if (useCachedParams()) {
        setCachedValues();
        // else, fill in default values
      }
      else {
        setRecommendedValues();
      }
    }

    /**
     * Return true if the cached parameters should be used, false otherwise.
     * The cached parameters will be used if all the following are true:
     * the number of objectives match, the number of decision variables match,
     * the total string length matches, and the names of the decision variables
     * match.
     *
     * @return true if cached parameters should be used, false if not
     */
    private boolean useCachedParams() {
      CachedParams cache = getCachedParams();
      if (cache == null) {
        return false;
      }
      if (numObjectives != cache.numObjectives) {
        return false;
      }

      int numVars = params.decisionVariables.getNumVariables();
      if (numVars != cache.numVariables) {
        return false;
      }

      int strLength = (int) params.decisionVariables.getTotalStringLength();
      if (strLength != cache.totalStringLength) {
        return false;
      }

      // loop through the names.  if a name is missing, return false
      for (int i = 0; i < numVars; i++) {
        String name = params.decisionVariables.getVariableName(i);

        boolean found = false;
        for (int j = 0; j < cache.variableNames.length; j++) {
          if (cache.variableNames[j].equals(name)) {
            found = true;
          }
        }
        if (!found) {
          return false;
        }
      }

      // otherwise we passed the tests, return true
      return true;
    }

    /**
     * Set all the parameter values to the recommended values.
     */
    private void setRecommendedValues() {
      boolean multiObjective = (numObjectives > 1);

      // the recommended population size is twice the number of
      // solutions desired for an MO pop
      double popSize;
      if (multiObjective) {
        this.numSolutions.setEnabled(true);
        this.numSolutionsLabel.setEnabled(true);
        this.numSolutions.setText("20");
        paramsModel.setValueAt("40", POP_SIZE, REC);
        popSize = 40;
      }
      // otherwise for an SO pop, the reccommended population size
      // is 1.4 * total string length
      else {
        this.numSolutions.setEnabled(false);
        this.numSolutionsLabel.setEnabled(false);
        double strLen = params.decisionVariables.getTotalStringLength();
        popSize = 1.4 * strLen;
        popSize = Math.ceil(popSize);
        paramsModel.setValueAt(Integer.toString( (int) popSize), POP_SIZE, REC);
      }

      // recommended max # of generations
      paramsModel.setValueAt(Integer.toString(100), MAX_GEN, REC);

      // recommended tournmaent size
      paramsModel.setValueAt(Integer.toString(4), TOURNAMENT, REC);

      // recommended crossover rate
      paramsModel.setValueAt(Double.toString(.75), CROSSOVER_RATE, REC);

      // rec mutation rate is 1/population size
      double mutrate = 1 / popSize;
      paramsModel.setValueAt(Double.toString(mutrate), MUTATION_RATE, REC);

      // rec gen gap
      paramsModel.setValueAt(Double.toString(1), GEN_GAP, REC);

      // binary individuals is the default.
      adv.binaryInd.setSelected(true);
      adv.binaryIndividualSelected();
    }

    /**
     * Set all the parameter values to the cached values.
     */
    private void setCachedValues() {
      boolean multiObjective = (numObjectives > 1);
      CachedParams cache = getCachedParams();
      // set the recommended values in the jtable
      for (int i = 0; i < cache.recommended.length; i++) {
        paramsModel.setValueAt(cache.recommended[i], i, REC);
      }
      // set the overridden values in the jtable
      for (int i = 0; i < cache.override.length; i++) {
        paramsModel.setValueAt(cache.override[i], i, OVERRIDE);
      }

      boolean bin = cache.binaryInd;
      if (bin) {
        adv.binaryInd.setSelected(true);
        adv.binaryIndividualSelected();
      }
      else {
        adv.realInd.setSelected(true);
        adv.realIndividualSelected();
      }

      // if it is multiobjective, fill in the number of solutions.
      if (multiObjective) {
        this.numSolutions.setEnabled(true);
        this.numSolutionsLabel.setEnabled(true);
//        this.numSolutions.setText(Integer.toString(cache.numS);
//        paramsModel.setValueAt("40", POP_SIZE, REC);
      }
      // if it is an SO problem, disable the numSolutions and numSolutionsLabel
      else {
        this.numSolutions.setEnabled(false);
        this.numSolutionsLabel.setEnabled(false);
      }

      // set the mutation properties
      String mutName = cache.mutName;
      HashMap mutProps = cache.mutProps;

      // match the saved name to the mutation type
      for (int i = 0; i < adv.mutationRadio.length; i++) {
        if (mutName.equals(adv.mutationRadio[i].getText())) {
          adv.mutationRadio[i].setSelected(true);
          // loop through the props and set them

          if (mutProps != null && adv.mutationRadio[i].propertyPanels != null) {
            Iterator propIter = mutProps.keySet().iterator();
            while (propIter.hasNext()) {
              String name = (String) propIter.next();
              Object val = mutProps.get(name);

              // now we have to loop through the prop panels to find
              // the one with this prop and set its value
              Iterator panelIter = adv.mutationRadio[i].propertyPanels.iterator();
              while (panelIter.hasNext()) {
                AdvSettingsPanel.PropPanel pp = (AdvSettingsPanel.PropPanel)
                    panelIter.next();
                if (pp.label.getText().equals(name)) {
                  pp.jtf.setText(val.toString());
                } // if
              } // while(panelIter.hasNext()
            } // while(propIter.hasNext())
          } // if
        } // if 
      } // for

      // set the crossover parameters
      String crossName = cache.crossName;
      HashMap crossProps = cache.crossProps;

      // match the saved name to the crossover type
      for (int i = 0; i < adv.crossoverRadio.length; i++) {
        if (crossName.equals(adv.crossoverRadio[i].getText())) {
          adv.crossoverRadio[i].setSelected(true);
          // loop through the props and set them

          if (crossProps != null && adv.crossoverRadio[i].propertyPanels != null) {
            Iterator propIter = crossProps.keySet().iterator();
            while (propIter.hasNext()) {
              String name = (String) propIter.next();
              Object val = crossProps.get(name);

              // now we have to loop through the prop panels to find
              // the one with this prop and set its value
              Iterator panelIter = adv.crossoverRadio[i].propertyPanels.
                  iterator();
              while (panelIter.hasNext()) {
                AdvSettingsPanel.PropPanel pp = (AdvSettingsPanel.PropPanel)
                    panelIter.next();
                if (pp.label.getText().equals(name)) {
                  pp.jtf.setText(val.toString());
                } // if
              } // while(panelIter.hasNext())
            } // while(propIter.hasNext())
          } // if
        } // if
      } // for

      // set the selection parameters
      String selName = cache.selName;
      HashMap selProps = cache.selProps;

      // match the saved name to the selection type
      for (int i = 0; i < adv.selectionRadio.length; i++) {
        if (selName.equals(adv.selectionRadio[i].getText())) {
          adv.selectionRadio[i].setSelected(true);
          // loop through the props and set them

          if (selProps != null && adv.selectionRadio[i].propertyPanels != null) {
            Iterator propIter = selProps.keySet().iterator();
            while (propIter.hasNext()) {
              String name = (String) propIter.next();
              Object val = selProps.get(name);

              // now we have to loop through the prop panels to find
              // the one with this prop and set its value
              Iterator panelIter = adv.selectionRadio[i].propertyPanels.
                  iterator();
              while (panelIter.hasNext()) {
                AdvSettingsPanel.PropPanel pp = (AdvSettingsPanel.PropPanel)
                    panelIter.next();
                if (pp.label.getText().equals(name)) {
                  pp.jtf.setText(val.toString());
                } // if
              } // while(panelIter.hasNext())
            } // while(propIter.hasNext())
          } // if
        } // if 
      } // for

    }

    public Dimension getPreferredSize() {
      return new Dimension(700, 425);
    }

    /**
     * make everything anti-aliased
     * @param g
     */
    protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      super.paintComponent(g2);
    }

    /**
     * arrange all the standard parameters.  the advanced settings are
     * contained in the AdvancedSettingsPanel
     */
    private class ParamsPanel
        extends JPanel {
      ParamsPanel() {
        setLayout(new GridBagLayout());

        paramsModel = new ParamsTableModel();
        JTable paramsTable = new JTable(paramsModel);
        paramsTable.getTableHeader().setReorderingAllowed(false);
        paramsTable.setPreferredScrollableViewportSize(new Dimension(300, 65));
        // now set the widths of the table columns
        int numColumns = paramsModel.getColumnCount();
        for (int i = 1; i < numColumns; i++) {
          TableColumn column = paramsTable.getColumnModel().getColumn(i);
          column.setMaxWidth(60);
        }
        JScrollPane jsp = new JScrollPane(paramsTable);

        JLabel lbl = new JLabel("Set GA Parameters");
        Font f = lbl.getFont();
        Font newFont = new Font(f.getFamily(), Font.BOLD, 16);
        lbl.setFont(newFont);
        Constrain.setConstraints(this, lbl, 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        Constrain.setConstraints(this, jsp, 0, 1, 1, 1,
                                 GridBagConstraints.BOTH,
                                 GridBagConstraints.WEST,
                                 1, 1);

        JPanel timePanel = new JPanel(new GridBagLayout());
        timePanel.setBorder(new CompoundBorder(new TopBorder(),
                                               new EmptyBorder(10, 0, 0, 0)));
        JLabel l1 = new JLabel("Time for Fitness and Constraint Eval:");
        //timePanel.add(l1);
        Constrain.setConstraints(timePanel, l1, 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        estimatedTime = new JTextField(5);
        estimatedTime.setHorizontalAlignment(JTextField.CENTER);
        //timePanel.add(estimatedTime);
        Constrain.setConstraints(timePanel, estimatedTime, 1, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        JLabel minLabel = new JLabel("min", JLabel.LEFT);
        //timePanel.add(minLabel);
        Constrain.setConstraints(timePanel, minLabel, 2, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
        JButton estimate = new JButton("Estimate");
        Constrain.setConstraints(timePanel, estimate, 0, 1, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST,
                                 0, 0);
        Constrain.setConstraints(this, timePanel, 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        JPanel maxPanel = new JPanel();
        maxPanel.setBorder(new CompoundBorder(new TopBorder(),
                                              new EmptyBorder(10, 0, 0, 0)));

        maxPanel.setLayout(new GridBagLayout());
        Constrain.setConstraints(maxPanel, new JLabel("Maximum Run Time:"),
                                 0, 0, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST, 0, 0);
        maxRunTime = new JTextField(4);
        maxRunTime.setHorizontalAlignment(JTextField.CENTER);
        Constrain.setConstraints(maxPanel, maxRunTime,
                                 1, 0, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST, 0, 0);
        JLabel minLabel2 = new JLabel("min", JLabel.LEFT);
        Constrain.setConstraints(maxPanel, minLabel2,
                                 2, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        numSolutionsLabel = new JLabel("Number of Solutions Desired:");
        Constrain.setConstraints(maxPanel, numSolutionsLabel,
                                 0, 1, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST, 0, 0);
        numSolutions = new JTextField(4);
        numSolutions.setHorizontalAlignment(JTextField.CENTER);
        Constrain.setConstraints(maxPanel, numSolutions,
                                 1, 1, 1, 1,
                                 GridBagConstraints.NONE,
                                 GridBagConstraints.WEST, 0, 0);
        Constrain.setConstraints(maxPanel, new JPanel(),
                                 2, 1, 1, 1,
                                 GridBagConstraints.BOTH,
                                 GridBagConstraints.WEST, 1, 1);

        Constrain.setConstraints(this, maxPanel, 0, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        settingsPanel.setBorder(new CompoundBorder(new TopBorder(),
            new EmptyBorder(10, 0, 0, 0)));
        JButton advSettings = new JButton("Advanced Settings");
        settingsPanel.add(advSettings);
        advSettings.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent ae) {
            if (!advFrame.isVisible()) {
              advFrame.show();
            }
          }
        });
        Constrain.setConstraints(this, settingsPanel, 0, 4, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        numSolutions.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            // set the recommended population size in the table model

            if (numSolutions.isEnabled()) {
              String num = numSolutions.getText();
              try {
                int numSol = Integer.parseInt(num);

                // the recommended pop size is twice the number of solutions
                int popSize = 2 * numSol;
                paramsModel.setValueAt(Integer.toString(popSize), 0, 1);
                paramsModel.fireTableCellUpdated(0, 1);
              }
              catch (Exception en) {
                // show dialog
              }
            }
          }
        });
        numSolutions.addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              numSolutions.transferFocus();
            }
          }
        });

        estimatedTime.setEditable(false);
        estimatedTime.setBorder(new EmptyBorder(0, 0, 0, 0));
        estimatedTime.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            estimatedTime.setEditable(true);
          }
        });
        estimatedTime.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            // set the #solutions ...
            estimatedTime.setEditable(false);
            estimatedRunTime.setText(estimatedTime.getText());
          }
        });
        // make the session name field uneditable and transfer the focus
        // to someone else when "enter' is pressed
        estimatedTime.addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              estimatedTime.transferFocus();
            }
          }
        });

        maxRunTime.addFocusListener(new FocusAdapter() {
          public void focusLost(FocusEvent e) {
            String txt = maxRunTime.getText();
            specifiedMaxTime.setText(txt);
          }
        });
        // make the session name field uneditable and transfer the focus
        // to someone else when "enter' is pressed
        maxRunTime.addKeyListener(new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              maxRunTime.transferFocus();
            }
          }
        });

        adv = new AdvSettingsPanel();
        advFrame = new JD2KFrame("Advanced Settings");
        advFrame.getContentPane().add(adv);
        advFrame.pack();
        advFrame.setVisible(false);
      }
    }

    /**
     * arrange the EstimatedTimeFactor components
     */
    private class TimePanel
        extends JPanel {
      TimePanel() {
        setLayout(new GridBagLayout());
        JLabel lbl = new JLabel("Estimated Time Factor");
        Font f = lbl.getFont();
        Font newFont = new Font(f.getFamily(), Font.BOLD, 16);
        lbl.setFont(newFont);
        Constrain.setConstraints(this, lbl, 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        BarPanel bp = new BarPanel();
        Constrain.setConstraints(this, bp, 0, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        JPanel lblPanel = new JPanel(new GridBagLayout());
        JLabel l1 = new JLabel("Estimated Run Time:");
        l1.setBorder(new EmptyBorder(2, 0, 2, 10));
        Constrain.setConstraints(lblPanel, l1, 0, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);
        estimatedRunTime = new JLabel("      ");
        Constrain.setConstraints(lblPanel, estimatedRunTime, 1, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);
        JLabel minLabel = new JLabel("min", JLabel.LEFT);
        Constrain.setConstraints(lblPanel, minLabel, 2, 0, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        JLabel l2 = new JLabel("Specified Max Run Time:");
        l2.setBorder(new EmptyBorder(2, 0, 2, 10));
        Constrain.setConstraints(lblPanel, l2, 0, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);
        specifiedMaxTime = new JLabel("      ");
        Constrain.setConstraints(lblPanel, specifiedMaxTime, 1, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        JLabel minLabel2 = new JLabel("min", JLabel.LEFT);
        Constrain.setConstraints(lblPanel, minLabel2, 2, 1, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        JLabel l3 = new JLabel("Difference of:");
        l3.setBorder(new EmptyBorder(2, 0, 2, 10));
        Constrain.setConstraints(lblPanel, l3, 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);
        difference = new JLabel("      ");
        Constrain.setConstraints(lblPanel, difference, 1, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        JLabel minLabel3 = new JLabel("min", JLabel.LEFT);
        Constrain.setConstraints(lblPanel, minLabel3, 2, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST, 1, 1);

        lblPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        Constrain.setConstraints(this, lblPanel, 0, 2, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btn = new JButton("Optimizing Tips");
        buttonPanel.add(btn);
        Constrain.setConstraints(this, buttonPanel, 0, 3, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        Constrain.setConstraints(this, new JPanel(), 0, 4, 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);
      }
    }

    /**
     * Arrange the advanced settings
     */
    private class AdvSettingsPanel
        extends JPanel {

      MutationRadioButton[] mutationRadio;
      CrossoverRadioButton[] crossoverRadio;
      SelectionRadioButton[] selectionRadio;
      JRadioButton binaryInd;
      JRadioButton realInd;

      AdvSettingsPanel() {
        ButtonGroup bg = new ButtonGroup();
        binaryInd = new JRadioButton("Binary-coded Individuals");
        bg.add(binaryInd);
        binaryInd.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent ae) {
            binaryIndividualSelected();
          }
        });

        realInd = new JRadioButton("Real-coded Individuals");
        bg.add(realInd);
        realInd.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent ae) {
            realIndividualSelected();
          }
        });

        JOutlinePanel individualType = new JOutlinePanel("Individual Type");
        individualType.setLayout(new GridLayout(2, 1));
        individualType.add(binaryInd);
        individualType.add(realInd);

        Mutation[] mutationChoices = MutationFactory.createMutationOptions();
        int numMutation = mutationChoices.length;
        // count the number of extra properties...
        int numExtraMutationProps = 0;
        for (int i = 0; i < numMutation; i++) {
          Property[] props = mutationChoices[i].getProperties();
          if (props != null) {
            numExtraMutationProps += props.length;
          }
        }

        JOutlinePanel mutType = new JOutlinePanel("Mutation Technique");
        mutType.setLayout(new GridLayout(numMutation + numExtraMutationProps, 1));
        mutationRadio = new MutationRadioButton[numMutation];
        ButtonGroup mutGroup = new ButtonGroup();
        for (int i = 0; i < numMutation; i++) {
          Mutation mut = mutationChoices[i];
          mutationRadio[i] = new MutationRadioButton(mut.getName(),
              mut.getDescription(), mut);
          mutGroup.add(mutationRadio[i]);
          mutType.add(mutationRadio[i]);

          Property[] props = mut.getProperties();
          HashSet propSet = new HashSet();
          if (props != null) {
            for (int j = 0; j < props.length; j++) {
              Property prop = props[j];
              PropPanel pp = new PropPanel(prop);
              propSet.add(pp);
              mutType.add(pp);
            }
          }
          mutationRadio[i].propertyPanels = propSet;

          // if a mutation type has properties, enable or disable them 
          // when the mutation type's state changes
          mutationRadio[i].addChangeListener(new AbstractChangeListener() {
            public void stateChanged(ChangeEvent ce) {
              // iterate through the prop panels...
              Iterator ii = ( (MutationRadioButton) ce.getSource()).
                  propertyPanels.iterator();
              boolean isSelected = ( (JRadioButton) ce.getSource()).isSelected();
              while (ii.hasNext()) {
                PropPanel pp = (PropPanel) ii.next();
                pp.setEnabled(isSelected);
              }
            }
          });
        }

        Crossover[] crossoverChoices = CrossoverFactory.createCrossoverOptions();
        int numCrossover = crossoverChoices.length;
        int numExtraCrossoverProps = 0;
        for (int i = 0; i < numCrossover; i++) {
          Property[] props = crossoverChoices[i].getProperties();
          if (props != null) {
            numExtraCrossoverProps += props.length;
          }
        }

        JOutlinePanel xType = new JOutlinePanel("Crossover Technique");
        xType.setLayout(new GridLayout(numCrossover + numExtraCrossoverProps, 1));
        crossoverRadio = new CrossoverRadioButton[numCrossover];
        ButtonGroup crsGroup = new ButtonGroup();
        for (int i = 0; i < numCrossover; i++) {
          Crossover crs = crossoverChoices[i];
          crossoverRadio[i] = new CrossoverRadioButton(crs.getName(),
              crs.getDescription(), crs);
          crsGroup.add(crossoverRadio[i]);
          xType.add(crossoverRadio[i]);

          Property[] props = crs.getProperties();
          HashSet propSet = new HashSet();
          if (props != null) {
            for (int j = 0; j < props.length; j++) {
              Property prop = props[j];
              PropPanel pp = new PropPanel(prop);
              propSet.add(pp);
              xType.add(pp);
            }
          }
          crossoverRadio[i].propertyPanels = propSet;

          // if a crossover type has properties, enable or disable them 
          // when the crossover type's state changes
          crossoverRadio[i].addChangeListener(new AbstractChangeListener() {
            public void stateChanged(ChangeEvent ce) {
              // iterate through the prop panels...
              Iterator ii = ( (CrossoverRadioButton) ce.getSource()).
                  propertyPanels.iterator();
              boolean isSelected = ( (JRadioButton) ce.getSource()).isSelected();
              while (ii.hasNext()) {
                PropPanel pp = (PropPanel) ii.next();
                pp.setEnabled(isSelected);
              }
            }
          });
        }

        Selection[] selectionChoices = SelectionFactory.createSelectionOptions();
        int numSelection = selectionChoices.length;
        int numExtraSelectionProps = 0;
        for (int i = 0; i < numSelection; i++) {
          Property[] props = selectionChoices[i].getProperties();
          if (props != null) {
            numExtraSelectionProps += props.length;
          }
        }

        JOutlinePanel selType = new JOutlinePanel("Selection Technique");
        selType.setLayout(new GridLayout(numSelection + numExtraSelectionProps,
                                         1));
        selectionRadio = new SelectionRadioButton[numSelection];
        ButtonGroup selGroup = new ButtonGroup();
        for (int i = 0; i < numSelection; i++) {
          Selection sel = selectionChoices[i];
          selectionRadio[i] = new SelectionRadioButton(sel.getName(),
              sel.getDescription(), sel);
          selGroup.add(selectionRadio[i]);

          selType.add(selectionRadio[i]);
          Property[] props = sel.getProperties();
          HashSet propSet = new HashSet();
          if (props != null) {
            for (int j = 0; j < props.length; j++) {
              Property prop = props[j];
              PropPanel pp = new PropPanel(prop);
              propSet.add(pp);
              selType.add(pp);
            }
          }
          selectionRadio[i].propertyPanels = propSet;

          // if a selection type has properties, enable or disable them 
          // when the selection type's state changes
          selectionRadio[i].addChangeListener(new AbstractChangeListener() {
            public void stateChanged(ChangeEvent ce) {
              // iterate through the prop panels...
              Iterator ii = ( (SelectionRadioButton) ce.getSource()).
                  propertyPanels.iterator();
              boolean isSelected = ( (JRadioButton) ce.getSource()).isSelected();
              while (ii.hasNext()) {
                PropPanel pp = (PropPanel) ii.next();
                pp.setEnabled(isSelected);
              }
            }
          });
        }

        this.setLayout(new GridLayout(2, 2));
        add(individualType);
        add(mutType);
        add(xType);
        add(selType);
      }

      /**
       * an abstract class that implements the ChangeListener interface.
       * used as an anonymous inner class in addChangeListener() methods.
       */
      private abstract class AbstractChangeListener
          implements ChangeListener {}

      private class MutationRadioButton
          extends JRadioButton {
        transient Mutation mutation;
        transient HashSet propertyPanels;

        MutationRadioButton(String n, String d, Mutation m) {
          super(n);
          setToolTipText(d);
          mutation = m;
        }
      }

      private class CrossoverRadioButton
          extends JRadioButton {
        transient Crossover crossover;
        transient HashSet propertyPanels;

        CrossoverRadioButton(String n, String d, Crossover c) {
          super(n);
          setToolTipText(d);
          crossover = c;
        }
      }

      private class SelectionRadioButton
          extends JRadioButton {
        transient Selection selection;
        transient HashSet propertyPanels;

        SelectionRadioButton(String n, String d, Selection s) {
          super(n);
          setToolTipText(d);
          selection = s;
        }
      }

      private class PropPanel
          extends JPanel {
        private JTextField jtf;
        private JLabel label;
        transient private Property property;

        PropPanel(Property prop) {
          // each prop will get a JLabel and an editor.
          // the editor will be a jtextfield

          property = prop;

          setLayout(new FlowLayout(FlowLayout.RIGHT));
          label = new JLabel(prop.getName());
          label.setEnabled(false);
          label.setToolTipText(prop.getDescription());
          jtf = new JTextField(4);
          jtf.setText(prop.getValue().toString());
          jtf.setEnabled(false);
          /*jtf.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
              String txt = jtf.getText();
              switch (property.getType()) {
                case (Property.DOUBLE):
                  try {
                    Double d = new Double(txt);
                    property.setValue(d);
                  }
                  catch (NumberFormatException nfe) {
                  }
                  break;
                case (Property.INT):
                  try {
                    Integer i = new Integer(txt);
                  }
                  catch (NumberFormatException nfe) {
                  }
                  break;
                case (Property.STRING):
                  property.setValue(txt);
                  break;
                default:
                  break;
              }
            }
          });*/

          add(label);
          add(jtf);
        }

        public void setEnabled(boolean b) {
          label.setEnabled(b);
          jtf.setEnabled(b);
        }
      }

      /**
       * If binary individuals are selected, enable or diable the
       * appropriate options.
       *
       * If an option is usable on a population with binary individuals,
       * it must implement the BinaryIndividualProcess.
       *
       * If an option is usable on a population with real individuals,
       * it must implement the RealIndividualProcess.
       *
       * If an option can be used on either real or binary individuals it
       * should implement both BinaryIndividualProcess and RealIndividualProcess.
       */
      private void binaryIndividualSelected() {
        // only enable the mutation types that implement BinaryIndividualProcess
        for (int i = 0; i < mutationRadio.length; i++) {
          Mutation mut = mutationRadio[i].mutation;
          if (mut instanceof BinaryIndividualProcess) 
            mutationRadio[i].setEnabled(true);
          else 
            mutationRadio[i].setEnabled(false);
        }

        // only enable the crossover types that implement BinaryIndividualProcess
        for (int i = 0; i < crossoverRadio.length; i++) {
          Crossover x = crossoverRadio[i].crossover;
          if (x instanceof BinaryIndividualProcess) 
            crossoverRadio[i].setEnabled(true);
          else 
            crossoverRadio[i].setEnabled(false);
        }

        // only enable the selection types that implement BinaryIndividaulProcesss
        for (int i = 0; i < selectionRadio.length; i++) {
          Selection sel = selectionRadio[i].selection;
          if (sel instanceof BinaryIndividualProcess) 
            selectionRadio[i].setEnabled(true);
          else 
            selectionRadio[i].setEnabled(false);
        }

        // get the default mutation type for binary ind
        int defMutation = MutationFactory.getBinaryDefault();
        // set the default mutation type to be selected
        mutationRadio[defMutation].setSelected(true);

        // get the default crossover type for binary ind
        int defCrossover = CrossoverFactory.getBinaryDefault();
        // set the default crossover type to be selected
        crossoverRadio[defCrossover].setSelected(true);

        // get the default selection type for binary ind
        int defSelection = SelectionFactory.getBinaryDefault();
        // set the default selection type for binary ind
        selectionRadio[defSelection].setSelected(true);
      }

      /**
       * If real individuals are selected, enable or diable the
       * appropriate options.
       *
       * If an option is usable on a population with binary individuals,
       * it must implement the BinaryIndividualProcess.
       *
       * If an option is usable on a population with real individuals,
       * it must implement the RealIndividualProcess.
       *
       * If an option can be used on either real or binary individuals it
           * should implement both BinaryIndividualProcess and RealIndividualProcess.
       */
      private void realIndividualSelected() {
        // only enable mutation types that implement RealIndividualProcess
        for (int i = 0; i < mutationRadio.length; i++) {
          Mutation mut = mutationRadio[i].mutation;
          if (mut instanceof RealIndividualProcess) 
            mutationRadio[i].setEnabled(true);
          else 
            mutationRadio[i].setEnabled(false);
        }

        // only enable crossover types that implement RealIndividualProcess
        for (int i = 0; i < crossoverRadio.length; i++) {
          Crossover x = crossoverRadio[i].crossover;
          if (x instanceof RealIndividualProcess) 
            crossoverRadio[i].setEnabled(true);
          else 
            crossoverRadio[i].setEnabled(false);
        }

        // only enable selection types that implement RealIndividualProcess
        for (int i = 0; i < selectionRadio.length; i++) {
          Selection sel = selectionRadio[i].selection;
          if (sel instanceof RealIndividualProcess) 
            selectionRadio[i].setEnabled(true);
          else 
            selectionRadio[i].setEnabled(false);
        }

        // get the default mutation type for real ind
        int defMutation = MutationFactory.getRealDefault();
        // set the default selection type for real ind
        mutationRadio[defMutation].setSelected(true);

        // get the default mutation type for real ind
        int defCrossover = CrossoverFactory.getRealDefault();
        // set the default selection type for real ind
        crossoverRadio[defCrossover].setSelected(true);

        // get the default mutation type for real ind
        int defSelection = SelectionFactory.getRealDefault();
        // set the default selection type for real ind
        selectionRadio[defSelection].setSelected(true);
      }

      /**
       * make everything anti-aliased
       * @param g
       */
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        super.paintComponent(g2);
      }
    }

    private class BarPanel
        extends JPanel {
      /*      public Dimension getPreferredSize() {
              return new Dimension(300, 100);
            }*/
    }

    /**
     * The table model for the JTable.  It holds the starting population size,
     * the maximum number of generations, the tournament size,
     * the probability of crossover, the probability of mutation, and
     * the generation gap.
     */
    private class ParamsTableModel
        extends DefaultTableModel {
      String[] names = {
          "", "Rec", "Override"};

      String[][] data;

      public int getColumnCount() {
        return 3;
      }

      public String getColumnName(int i) {
        return names[i];
      }

      public boolean isCellEditable(int r, int c) {
        if (c == OVERRIDE) {
          return true;
        }
        return false;
      }

      public Object getValueAt(int r, int c) {
        return data[c][r];
      }

      ParamsTableModel() {
        data = new String[3][6];

        data[0] = new String[] {
            "Starting Population Size",
            "Maximum Number of Generations",
            "Tournament Size",
            "Probability of Crossover",
            "Probability of Mutation",
            "Generation Gap"};

        data[1] = new String[] {
            //"", "100", "4", ".75", "", "1"};
            "", "", "", "", "", ""};

        data[2] = new String[] {
            "", "", "", "", "", ""};
      }

      public int getRowCount() {
        return 6;
      }

      public void setValueAt(Object value, int r, int c) {
        if (c == 0) {
          data[c][r] = (String) value;
        }
        else if (c == REC) {
          data[c][r] = (String) value;
          if (r == POP_SIZE) {
            try {
              int popsize = Integer.parseInt( (String) value);
              double mutRate = 1.0 / popsize;
              setValueAt(Double.toString(mutRate), MUTATION_RATE, REC);
              fireTableDataChanged();
            }
            catch (Exception ee) {
              return;
            }
          }
          /*if (r == 2) {
            try {
              double d = Double.parseDouble( (String) value);
              double crossover = (d - 1) / d;
              setValueAt(Double.toString(crossover), 3, 1);
              fireTableDataChanged();
            }
            catch (Exception e) {
              return;
            }
                     }*/
        }
        else if (c == OVERRIDE) {
          data[c][r] = (String) value;
        }
      }
    }

    /**
     * draw a line across the top of a component
     * @author David Clutter
     * @version 1.0
     */
    class TopBorder
        extends AbstractBorder {
      public void paintBorder(Component c, Graphics g, int x, int y, int w,
                              int h) {
        g.setColor(Color.darkGray);
        g.drawLine(x, y, x + w, y);
      }
    }
  }
}