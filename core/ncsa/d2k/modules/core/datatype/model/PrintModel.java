package ncsa.d2k.modules.core.datatype.model;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;

public class PrintModel extends OutputModule {
  public boolean Enabled     = true;
  public void    setEnabled (boolean value) {       this.Enabled       = value;}
  public boolean getEnabled ()              {return this.Enabled;}

  public boolean AsciiInputs     = false;
  public void    setAsciiInputs (boolean value) {       this.AsciiInputs       = value;}
  public boolean getAsciiInputs ()              {return this.AsciiInputs;}

  public boolean EnumerateSplitValues     = false;
  public void    setEnumerateSplitValues (boolean value) {       this.EnumerateSplitValues       = value;}
  public boolean getEnumerateSplitValues ()              {return this.EnumerateSplitValues;}

  public boolean PrintInnerNodeModels     = false;
  public void    setPrintInnerNodeModels (boolean value) {       this.PrintInnerNodeModels       = value;}
  public boolean getPrintInnerNodeModels ()              {return this.PrintInnerNodeModels;}

  public int     MaximumFractionDigits     = 3;
  public void    setMaximumFractionDigits (int value) {       this.MaximumFractionDigits       = value;}
  public int     getMaximumFractionDigits ()              {return this.MaximumFractionDigits;}

  private String OutputLabel = "PrintModel: ";
  public  void   setOutputLabel (String value) {this.OutputLabel = value;}
  public  String getOutputLabel ()          {return this.OutputLabel;}

  public String getModuleInfo()
    {
    return "PrintModel";
    }
  public String getModuleName()
    {
    return "PrintModel";
    }

  public String[] getInputTypes()
    {
    String [] in = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return in;
    }

  public String[] getOutputTypes()
    {
    String [] out = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return out;
    }

  public String getInputInfo(int i)
    {
    switch (i)
      {
      case 0: return "Model";
      }
    return "";
    }

  public String getInputName(int i)
    {
    switch (i)
      {
      case 0: return "Model";
      }
    return "";
    }

  public String getOutputInfo(int i)
    {
    switch (i)
      {
      case 0: return "Model";
      }
    return "";
    }

  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "Model";
      }
    return "";
    }



  public void doit() throws Exception
    {
    ModelPrintOptions printOptions = null;
    if (Enabled)
      {
      printOptions = new ModelPrintOptions();

      printOptions.AsciiInputs           = AsciiInputs;
      printOptions.EnumerateSplitValues  = EnumerateSplitValues;
      printOptions.PrintInnerNodeModels  = PrintInnerNodeModels;
      printOptions.MaximumFractionDigits = MaximumFractionDigits;
      }

    Model      model      = (Model)      this.pullInput(0);

    if (Enabled)
      {
      synchronized( System.out )
         {
         System.out.println();
         System.out.println( OutputLabel + "(start)");
         model.print(printOptions);
         System.out.println();
         System.out.println( OutputLabel + "(finish)");
         System.out.println();
         }
      }

    this.pushOutput(model, 0);

    }
  }
