package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;

import libsvm.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import ncsa.gui.*;



/**
  Builds a Support Vector Machine (SVM).  This is a wrapper for the
  popular libsvm library (the Java version).  The desired SVM could
  be of several types and kernel types.<p>

  The original libsvm can be found at http://www.csie.ntu.edu.tw/~cjlin/libsvm/.
  It has a modified BSD licence that is compatible with GPL.

  @author Xiaolei Li
  */
public class SVMBuilder extends SVMBuilderOPT
{
	/**
	  Type of SVM.  Has 5 possible choices: C-SVC, nu-SVC, one-class
	  SVM, epsilon-SVR, and nu-SVR.  Default is C-SVC.
	  */
	private int SvmType = SVMParameters.C_SVC;

	/**
	  Type of kernel.  Has 4 possible choices: linear, polynomial,
	  radial basis, and sigmoid.  Default is radial basis.
	  */
	private int KernelType = SVMParameters.RADIAL;

	/**
	  Degree of the kernel function (if a polynomial kernel is chosen).
	  Default is 3.
	  */
	private double Degree = 3.0;

	/**
	  Gamma of the kernel function.  Applicable for the polynomial,
	  radial, and sigmoid kernels only.  Default is 1 / (number of
	  attributes in the input data).
	  */
	private double Gamma;

	/**
	  Coefficent 0 of the kernel.  Applicable for the polynomial and
	  sigmoid kernels.  Default is 0.
	  */
	private double Coef0 = 0.0;

	/**
	  Cache memory size in MB.  Default is 40MB.
	  */
	private double CacheSize = 40.0;

	/**
	  Tolerance of termination (stopping criterion).  Default is 0.001.
	 */
	private double Eps = 0.001;

	/**
	  Parameter C of C-SVC, Epsilon-SVR, and nu-SVR.  Default is 1.0.
	  */
	private double C = 1.0;

	/* the following 3 parameters are for C-SVC only and haven't been
	 * coded in yet.  It will require a dynamic properties module
	 * because the size of the weight array depends on user input */
	//int nrWeight = 0;
	//int[] weight_label;
	//double[] weight;

	/**
	  Parameter nu of nu-SVC, One-class SVM, and nu-SVR.  Default value
	  is 1.
	  */
	private double Nu = 0.5;

	/**
	  Epsilon in the loss function of epsilon-SVR.  Default is 0.1.
	  */
	private double P = 0.1;

	/**
	  Binary value (0 or 1) to disable or enable the shrinking
	  heuristics.  Default is on or 1.
	  */
	private int Shrinking = 1;


        public String getModuleName()
        {
          return "SVM Builder";

        }

	public String getModuleInfo()
	{
		return "<b>Overview</b>: Builds a Support Vector Machine (SVM).<p> " +
			"<b>Detailed description</b>: This is a " +
			"wrapper for the popular libsvm library (the Java " +
			"version).  It builds a Support Vector Machine (SVM) for " +
			"the given sample data.  SVMs are popular in classification"
			+ " due to its marginal maximization property which finds "
			+ "a decision hyperplane that maximizes the distance to"
			+ " the separate classes.  This makes for better " +
			"generalization.<p>" +


			"<b>Data Restrictions</b>: The SVM can deal with binary or " +
			"multi-class classification.  The classes need to be " +
			"integers and the attribute values need to be numerical.<p>"
			+

			"<b>Reference</b>: Chih-Chung Chang and Chih-Jen Lin, LIBSVM : a "
			+ "library for support vector machines, 2001. Software " +
			"available at http://www.csie.ntu.edu.tw/~cjlin/libsvm/." +

			"<p>Note that libsvm has a modified BSD licence that is " +
			"compatible with GPL.";
	}

	/* requires two inputs */
	public String getInputInfo(int index)
	{
		switch (index) {
			case 0:
				return "Training data to be used for building the SVM.";
			case 1:
				return "Number of attributes in a single input.  The " +
					"size of the input vector.";
			default:
				return "";
		}
	}

	/* requires two inputs */
	public String getInputName(int index)
	{
		switch (index) {
			case 0:
				return "Training Data";
			case 1:
				return "Number of Attributes";
			default:
				return "";
		}
	}

	public String[] getInputTypes()
	{
		String[] in = {"libsvm.svm_problem", "java.lang.Integer"};
		return in;
	}

	protected void doit() throws Exception
	{
		try {
			/* get the training data */
			svm_problem prob = (libsvm.svm_problem) this.pullInput(0);

			/* create the parameters */
			svm_parameter param = createParameters((Integer) this.pullInput(1));

			/* check for errors */
			String error_msg = svm.svm_check_parameter(prob,param);

			if(error_msg != null) {
				System.out.println("ERROR: SVMBuilder.doit()");
				System.err.print(error_msg + "\n");
			}

			/* build the actual SVM */
			svm_model model = svm.svm_train(prob, param);

			this.pushOutput(model, 0);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			System.out.println("ERROR: SVMBuilder.doit()");
			throw ex;
		}
	}

	/**
	  Given the properties set by the user, create the native
	  svm_parameter class that is used by svm_train().

	  @param num_attributes The number of attributes in the input data
	  (i.e., size of input vector).

	  @return The parameters chosen for training the SVM encapsulated in
	  the native svm_parameter class.
	  */
	private svm_parameter createParameters(Integer num_attributes)
	{
		svm_parameter param = new svm_parameter();

		param.svm_type = this.SvmType;
		param.kernel_type = this.KernelType;
		param.degree = this.Degree;

		/* if the user entered 0.0, default to 1/k where k is the
		 * number of attributes in the input data. */
		param.gamma = this.Gamma;
		if (this.Gamma == 0.0)
			param.gamma = 1.0 / num_attributes.doubleValue();

		param.coef0 = this.Coef0;
		param.nu = this.Nu;
		param.cache_size = this.CacheSize;
		param.C = this.C;
		param.eps = this.Eps;
		param.p = this.P;
		param.shrinking = this.Shrinking;

		/* these are hard-coded.  should be user inputs. */
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		return param;
	}

	public int getSvmType() { return SvmType; }
	public void setSvmType(int val) { SvmType = val; }

	public int getKernelType() { return KernelType; }
	public void setKernelType(int val) { KernelType = val; }

	public double getDegree() { return Degree; }
	public void setDegree(double val) { Degree = val; }

	public double getGamma() { return Gamma; }
	public void setGamma(double val) { Gamma = val; }

	public double getCoef0() { return Coef0; }
	public void setCoef0(double val) { Coef0 = val; }

	public double getCacheSize() { return CacheSize; }
	public void setCacheSize(double val) { CacheSize = val; }

	public double getEps() { return Eps; }
	public void setEps(double val) { Eps = val; }

	public double getC() { return C; }
	public void setC(double val) { C = val; }

	public double getNu() { return Nu; }
	public void setNu(double val) { Nu = val; }

	public double getP() { return P; }
	public void setP(double val) { P = val; }

	public int getShrinking() { return Shrinking; }
	public void setShrinking(int val) { Shrinking = val; }

	public PropertyDescription[] getPropertiesDescriptions()
	{

             return SVMParameters.getPropertiesDescriptions();
	}



        public CustomModuleEditor getPropertyEditor() {
          return new PropEdit();
        }

private class PropEdit extends JPanel implements CustomModuleEditor {

    public  final String[] BOOLEANS = {"false", "true"};
    public  final int FALSE = 0;
    public  final int TRUE = 1;


    private JLabel[] propLabels;
    private JComboBox svm_type_list = new JComboBox(SVMParameters.SVM_TYPE_NAMES);
    private JComboBox kernel_type_list = new JComboBox(SVMParameters.KERNEL_TYPE_NAMES);
    private JComboBox shrinking_list = new JComboBox(BOOLEANS);

    private JTextField _gamma = new JTextField(Double.toString(getGamma()));
    private JTextField _nu = new JTextField(Double.toString(getNu()));
    private JTextField _c = new JTextField(Double.toString(getC()));
    private JTextField _p = new JTextField(Double.toString(getP()));
    private JTextField _degree = new JTextField(Double.toString(getDegree()));
    private JTextField _epsilon = new JTextField(Double.toString(getEps()));
    private JTextField _cache = new JTextField(Double.toString(getCacheSize()));
    private JTextField _coef = new JTextField(Double.toString(getCoef0()));

    private boolean[] change;




    private PropEdit() {

      change = new boolean[SVMParameters.NUM_PROPS];
      //creating the labels
      propLabels = new JLabel[SVMParameters.NUM_PROPS];
      for (int i = 0; i < SVMParameters.NUM_PROPS; i++) {
        propLabels[i] = new JLabel(SVMParameters.PROPS_NAMES[i]);
        propLabels[i].setToolTipText(SVMParameters.PROPS_DESCS[i]);
      }

      //selecting the items in the combo boxes.
      svm_type_list.setSelectedIndex(getSvmType());
      kernel_type_list.setSelectedIndex(getKernelType());
      shrinking_list.setSelectedIndex(getShrinking());

      //adding listeners
      _gamma.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          change[SVMParameters.GAMMA] = true;
        }
      });

      _nu.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          change[SVMParameters.NU] = true;
        }
      });

      _c.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          change[SVMParameters.C] = true;
        }
      });


      _p.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          change[SVMParameters.GAMMA] = true;
        }
      });

      _coef.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          change[SVMParameters.COEF0] = true;
        }
      });

      _epsilon.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          change[SVMParameters.EPS] = true;
        }
      });


      _cache.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
           change[SVMParameters.CACHE_SIZE] = true;
         }
       });

       _degree.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
           change[SVMParameters.DEGREE] = true;
         }
       });
       svm_type_list.addActionListener(new SVMTypeListener());
       kernel_type_list.addActionListener(new KernelTypeListener());
       shrinking_list.addActionListener(new ShrinkingListener());


       enableKernelDependencies();
      enableSvmDependencies();

   setLayout(new GridBagLayout());



   //adding svm type drop down list
   Constrain.setConstraints(this, propLabels[SVMParameters.SVM_TYPE], 1, 0,
                            1, 1,
                            GridBagConstraints.HORIZONTAL,
                            GridBagConstraints.WEST,
                            1, 1);
   Constrain.setConstraints(this, svm_type_list, 2, 0,
                            1, 1,
                            GridBagConstraints.HORIZONTAL,
                            GridBagConstraints.WEST,
                            1, 1);


   //adding parameters that are svm type dependencies
    JPanel type_panel = new JPanel(new GridBagLayout());
   Constrain.setConstraints(type_panel, propLabels[SVMParameters.C], 0, 0, 1, 1,
                            GridBagConstraints.HORIZONTAL,
                            GridBagConstraints.EAST,
                            1, 1);
   Constrain.setConstraints(type_panel, _c, 1, 0,
                            1, 1,
                            GridBagConstraints.HORIZONTAL,
                            GridBagConstraints.WEST,
                            1, 1);

   Constrain.setConstraints(type_panel, propLabels[SVMParameters.NU], 0, 1, 1, 1,
                            GridBagConstraints.HORIZONTAL,
                            GridBagConstraints.EAST,
                            1, 1);
   Constrain.setConstraints(type_panel, _nu, 1, 1,
                            1, 1,
                            GridBagConstraints.HORIZONTAL,
                            GridBagConstraints.WEST,
                            1, 1);

   Constrain.setConstraints(type_panel, propLabels[SVMParameters.P], 0, 2, 1, 1,
                            GridBagConstraints.HORIZONTAL,
                            GridBagConstraints.EAST,
                            1, 1);
   Constrain.setConstraints(type_panel, _p, 1, 2,
                            1, 1,
                            GridBagConstraints.HORIZONTAL,
                            GridBagConstraints.WEST,
                            1, 1);

   Constrain.setConstraints(this, type_panel, 2, 1,
                              1, 1,
                              GridBagConstraints.HORIZONTAL,
                              GridBagConstraints.WEST,
                              1, 1);




   //adding kernel type drop down list
     Constrain.setConstraints(this, propLabels[SVMParameters.KERNEL_TYPE], 1, 2,
                              1, 1,
                              GridBagConstraints.HORIZONTAL,
                              GridBagConstraints.WEST,
                              1, 1);
     Constrain.setConstraints(this, kernel_type_list, 2, 2,
                              1, 1,
                              GridBagConstraints.HORIZONTAL,
                              GridBagConstraints.WEST,
                              1, 1);
     //adding parameters that are kernel type dependencies
     JPanel kernel_panel = new JPanel(new GridBagLayout());

     Constrain.setConstraints(kernel_panel, propLabels[SVMParameters.GAMMA], 0, 0,
                              1, 1,
                              GridBagConstraints.HORIZONTAL,
                              GridBagConstraints.EAST,
                              1, 1);
     Constrain.setConstraints(kernel_panel, _gamma, 1, 0,
                              1, 1,
                              GridBagConstraints.HORIZONTAL,
                              GridBagConstraints.WEST,
                              1, 1);

     Constrain.setConstraints(kernel_panel, propLabels[SVMParameters.DEGREE], 0, 1,
                              1, 1,
                              GridBagConstraints.HORIZONTAL,
                              GridBagConstraints.EAST,
                              1, 1);
     Constrain.setConstraints(kernel_panel, _degree, 1, 1,
                              1, 1,
                              GridBagConstraints.HORIZONTAL,
                              GridBagConstraints.WEST,
                              1, 1);

     Constrain.setConstraints(kernel_panel, propLabels[SVMParameters.COEF0], 0, 2,
                              1, 1,
                              GridBagConstraints.HORIZONTAL,
                              GridBagConstraints.EAST,
                              1, 1);
     Constrain.setConstraints(kernel_panel, _coef, 1, 2,
                              1, 1,
                              GridBagConstraints.HORIZONTAL,
                              GridBagConstraints.WEST,
                              1, 1);

     Constrain.setConstraints(this, kernel_panel, 2, 3,
                                1, 1,
                                GridBagConstraints.HORIZONTAL,
                                GridBagConstraints.WEST,
                                1, 1);




     Constrain.setConstraints(this, propLabels[SVMParameters.EPS], 1, 4,
                                 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);


     Constrain.setConstraints(this, _epsilon, 2, 4,
                                 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        Constrain.setConstraints(this, propLabels[SVMParameters.CACHE_SIZE], 1, 5,
                                 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        Constrain.setConstraints(this, _cache, 2, 5,
                                 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        Constrain.setConstraints(this, propLabels[SVMParameters.SHRINKING], 1, 6,
                                 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);

        Constrain.setConstraints(this, shrinking_list, 2, 6,
                                 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);


        Constrain.setConstraints(this, new Label("\t"), 4, 0,
                                 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);


        Constrain.setConstraints(this, new Label("\t"), 0, 0,
                                 1, 1,
                                 GridBagConstraints.HORIZONTAL,
                                 GridBagConstraints.WEST,
                                 1, 1);







      } //PropEdit

      private void enableSvmDependencies() {
        int selected = svm_type_list.getSelectedIndex();

        boolean enableC = (selected == SVMParameters.C_SVC ||
                           selected == SVMParameters.EPSILON_SVR ||
                           selected == SVMParameters.NU_SVR);

        boolean enableNu = (selected == SVMParameters.NU_SVC ||
                            selected == SVMParameters.ONE_CLASS_SVM ||
                            selected == SVMParameters.NU_SVR);

        _c.setEnabled(enableC);
        change[SVMParameters.C] = true;

        _nu.setEnabled(enableNu);
        change[SVMParameters.NU] = true;

        _p.setEnabled(selected == SVMParameters.EPSILON_SVR);
        change[SVMParameters.P] = true;

      } //enableSvmDependencies

      private void enableKernelDependencies() {
        int selected = kernel_type_list.getSelectedIndex();

        boolean enableGamma = (selected == SVMParameters.POLYNOMIAL ||
                               selected == SVMParameters.RADIAL ||
                               selected == SVMParameters.SIGMOID);

        boolean enableCoef = (selected == SVMParameters.POLYNOMIAL ||
                              selected == SVMParameters.SIGMOID);

        _degree.setEnabled(selected == SVMParameters.POLYNOMIAL);
        change[SVMParameters.DEGREE] = true;

        _gamma.setEnabled(enableGamma);
        change[SVMParameters.GAMMA] = true;

        _coef.setEnabled(enableCoef);
        change[SVMParameters.COEF0] = true;

      } //enableKernelDependencies

    private class ShrinkingListener implements ActionListener{
      public void actionPerformed(ActionEvent e) {
        int selected = shrinking_list.getSelectedIndex();
//        setShrinking(selected);
        change[SVMParameters.SHRINKING] = true;
      }
    }

    private class SVMTypeListener implements ActionListener{
      public void actionPerformed(ActionEvent e){
       enableSvmDependencies();
       change[SVMParameters.SVM_TYPE] = true;
      }//action performed

    }//SVMTypeListener


    private class KernelTypeListener implements ActionListener{

    public void actionPerformed(ActionEvent e){
      enableKernelDependencies();
      change[SVMParameters.KERNEL_TYPE] = true;
    }//action performed

  }//KernelTypeListener





    public boolean updateModule() throws Exception {
      boolean didChange = false;

      if(change[SVMParameters.SVM_TYPE]){
        setSvmType(svm_type_list.getSelectedIndex());
        didChange = true;
      }


      if(change[SVMParameters.KERNEL_TYPE]){
        setKernelType(kernel_type_list.getSelectedIndex());
        didChange = true;
      }

      if(change[SVMParameters.SHRINKING]){
        setShrinking(shrinking_list.getSelectedIndex());
        didChange = true;
      }



      if (change[SVMParameters.GAMMA] ) {
        try {
          double val = Double.parseDouble(_gamma.getText());
          setGamma(val);
        }
        catch (NumberFormatException e) {
          throw new Exception("Gamma parameter must be a real number.");
        }
        didChange = true;
      } //change gamma


      if (change[SVMParameters.C] ) {
        try {
          double val = Double.parseDouble(_c.getText());
          if (val <= 0) throw new Exception("C Parameter should be greater than 0");
          setC(val);
        }
        catch (NumberFormatException e) {
          throw new Exception("C parameter should be a real number greater than 0.");
        }
        didChange = true;
      } //change c

      if (change[SVMParameters.P] ) {
        try {
          double val = Double.parseDouble(_p.getText());
          if (val < 0) throw new Exception("P Parameter should be greater than or equal to 0");
          setP(val);
        }
        catch (NumberFormatException e) {
          throw new Exception("P parameter must be a real number greater than or equal to 0.");
        }
        didChange = true;
      } //change p

      if (change[SVMParameters.NU] ) {
        try {
          double val = Double.parseDouble(_nu.getText());
          if (val < 0 || val > 1) throw new Exception("nu Parameter should be between 0 and 1");
          setNu(val);
        }
        catch (NumberFormatException e) {
          throw new Exception("NU parameter should be a real number between 0 and 1.");
        }
        didChange = true;
      } //change nu

      if (change[SVMParameters.CACHE_SIZE]) {
        try {
          double val = Double.parseDouble(_cache.getText());
          if(val <=0) throw new Exception("Cache Size parameter should be greater than zero");
          setCacheSize(val);
        }
        catch (NumberFormatException e) {
          throw new Exception("Cache Size parameter must be a number greater than zero.");
        }
        didChange = true;
      } //change cache size

      if (change[SVMParameters.EPS] ) {
        try {
          double val = Double.parseDouble(_epsilon.getText());
          if(val <=0) throw new Exception("Epsilon parameter should be greater than zero");
          setEps(val);
        }
        catch (NumberFormatException e) {
          throw new Exception("Epsilon parameter should be a real number greater than 0.");
        }
        didChange = true;
      } //change c

      if (change[SVMParameters.DEGREE] ) {
        try {
          double val = Double.parseDouble(_degree.getText());
          setDegree(val);
        }
        catch (NumberFormatException e) {
          throw new Exception("Degree parameter must be a real number.");
        }
        didChange = true;
      } //change degree

      if (change[SVMParameters.COEF0]) {
        try {
          double val = Double.parseDouble(_coef.getText());
          setCoef0(val);
        }
        catch (NumberFormatException e) {
          throw new Exception("Coef0 parameter should be a real number.");
        }
        didChange = true;
      } //change coef0

        return didChange;
    }
}//class prop edit

}
