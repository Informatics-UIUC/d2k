package ncsa.d2k.modules.projects.aehmann;


import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.PropertyDescription;


public class Window extends ComputeModule {


	private String     WindowName = "Rectangular";
	public  void    setWindowName (String value) {       this.WindowName = value;}
	public  String     getWindowName ()          {return this.WindowName;}

	public PropertyDescription [] getPropertiesDescriptions () {
		PropertyDescription [] pds = new PropertyDescription [1];
		pds[0] = new PropertyDescription ("windowName", "Window Function", "The name of the window function which can be Rectangular, Bartlett, Hanning, Hamming.");
		return pds;
	}

  public String getModuleName() {
    return "Window";
  }
  public String getModuleInfo() {
    return "This module applies the specified window to an array.  The choices are: Rectangular, Bartlett, Hanning, and Hamming.  ";
  }

  public String getInputName(int i) {
    switch(i) {
      case 0:  return "Input";
      default: return "Error!  No such input.  ";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Double1DArray";
      default: return "Error!  No such input.  ";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"[D"};
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:  return "Double1DArray";
      default: return "Error!  No such output.  ";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Double1DArray";
      default: return "Error!  No such output.  ";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"[D"};
    return types;
  }
  
  public CustomModuleEditor getPropertyEditor() {
	  return new SetWindow();
  }


  public void doit() {
    double [] double1DArray = (double []) this.pullInput(0);
    int dim1Size = double1DArray.length;
    
    int windowType = 1;
    
	if (WindowName.equals("Rectangular"))
	  windowType =  1;
	if (WindowName.equals("Bartlett"))
	  windowType =  2;
	if (WindowName.equals("Hanning"))
	  windowType =  3;
	if (WindowName.equals("Hamming"))
	  windowType =  4;
    

    switch(windowType) {
    	case 1:									/* Rectangular */
    	  this.pushOutput(double1DArray, 0);
    	  break;
		case 2:									/* Bartlett */
		  for (int d1 = 0; d1 < dim1Size; d1++) {
			if (d1 <= dim1Size/2) {
				double1DArray[d1] = (2*d1/dim1Size)*double1DArray[d1];
			}
			else {
				double1DArray[d1] = (2-2*d1/dim1Size)*double1DArray[d1];
			}
		  }
		  this.pushOutput(double1DArray, 0);
		  break;
    	case 3:									/* Hanning */
		  for (int d1 = 0; d1 < dim1Size; d1++) {
		  	double1DArray[d1] = (0.5 + 0.5*Math.cos(2*Math.PI*d1/dim1Size))*double1DArray[d1];
		  }
		  this.pushOutput(double1DArray, 0);
		  break;
		case 4:									/* Hamming */
		  for (int d1 = 0; d1 < dim1Size; d1++) {
			double1DArray[d1] = (0.54 + 0.46*Math.cos(2*Math.PI*d1/dim1Size))*double1DArray[d1];
		  }
		  this.pushOutput(double1DArray, 0);
		  break;
		
    }
			
    	  
  }

  /**
   * This panel displays the editable properties of the SimpleTestTrain modules.
   * @author Thomas Redman
   */
  class SetWindow extends JPanel implements CustomModuleEditor {
	final String [] errors = {"Rectangular","Bartlett","Hanning","Hamming"};
	JComboBox errorsSelection = new JComboBox(errors);
	SetWindow() {
		JLabel tt = new JLabel("Window");
		tt.setToolTipText(Window.this.getPropertiesDescriptions()[0].getDescription());
		this.add ("West",tt);
		errorsSelection.setSelectedItem(WindowName);
		this.add ("Center",errorsSelection);
	}

	/**
	 * Update the fields of the module
	 * @return a string indicating why the properties could not be set, or null if successfully set.
	 */
	public boolean updateModule() throws Exception {
		String newError = (String) errorsSelection.getSelectedItem();
		if (WindowName.equals(newError)) {
			return false;
		}

		// we have a new error function name.
		WindowName = newError;
		return true;
	}
  }
}