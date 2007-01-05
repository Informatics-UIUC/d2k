package ncsa.d2k.modules.core.io.file.output.examples;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ncsa.d2k.core.modules.CustomModuleEditor;
import ncsa.d2k.core.modules.InputModule;
import ncsa.d2k.core.modules.PropertyDescription;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxy;
import ncsa.d2k.modules.core.io.proxy.DataObjectProxyException;
import ncsa.gui.Constrain;


public class putDirectory extends InputModule {
	
	private String depthLevel;
	
	 private void listDir(DataObjectProxy dop, int depth)  throws DataObjectProxyException{
		 
		 Vector list = dop.getChildrenURLs(depth);
	   
	   Enumeration en = list.elements();

	   while (en.hasMoreElements()) {
	      Object s = en.nextElement();
	      System.out.println("  " + s);
	   }
	   }
	   
	
   public void doit() throws Exception {
	   
	   DataObjectProxy srcdop = (DataObjectProxy) pullInput(0);
	   DataObjectProxy desdop = (DataObjectProxy) pullInput(1);
	   int depth;
	   System.out.println("depth is "+this.getDepth());
	   System.out.println("Source DOP is "+srcdop.getURL());
	   System.out.println("Destination DOP is "+desdop.getURL());
	   if(this.getDepth().equals("1")) {
		   depth=1;
	   }
	   else if(this.getDepth().equalsIgnoreCase("infinity")) {
		   depth=DataObjectProxy.DEPTH_INFINITY;
	   }
	   else {
		   throw new DataObjectProxyException("Not valid depth value.");
	   }
	   System.out.println("List the contents of the source:");
	   long b1 = System.currentTimeMillis();  
	   listDir(srcdop,depth);
	   long a1 = System.currentTimeMillis(); 
	   
	   System.out.println("Start uploading "+ srcdop.getURL()+ " to "+desdop.getURL());
	   long b2 = System.currentTimeMillis(); 
	   desdop.uploadDir(srcdop,depth);
	   long a2 = System.currentTimeMillis(); 
	   System.out.println("End uploading "+ srcdop.getURL()+ " to "+desdop.getURL());
	   System.out.println("List the contents of the destination:");
	   long b3 = System.currentTimeMillis(); 
	   listDir(desdop,depth);
	   long a3 = System.currentTimeMillis(); 
	   srcdop.close();
	   desdop.close();
	   System.out.println("Timing:");
	      System.out.println("  List Source: "+(a1-b1)+ " ms.");
	      System.out.println("  Upload: "+(a2-b2)+ " ms.");
	      System.out.println("  List Dest: "+(a3-b3)+ " ms.");

   
   } // end method doit
   
   public PropertyDescription[] getPropertiesDescriptions() {
	      PropertyDescription[] pds = new PropertyDescription[1];

	      pds[0] =
	         new PropertyDescription("depthLevel",
	                                 "depth Level",
	                                 "The depth level, it should be one of \"1\" or \"infinity\"");
	      return pds;
	   }
   
   public String getDepth() {
	    return depthLevel;
   }
   
   public void setDepth(String s) {
	   depthLevel=s;	   
   }

   public String getInputInfo(int i) {

	      switch (i) {

	         case 0:
	            return "The DataObjectProxy for the source directory (local).";

	         case 1:
	            return "The DataObjectProxy for destination directory (remote).";

	         default:
	            return "No such input";
	      }
	   }
   
   public String getInputName(int i) {

	      switch (i) {

	         case 0:
	            return "DataObjectProxy";

	         case 1:
	            return "DataObjectProxy";

	         default:
	            return "No such input";
	      }
	   }


   public String[] getInputTypes() {
	      String[] types =
	      {
	         "ncsa.d2k.modules.core.io.proxy.DataObjectProxy",
	         "ncsa.d2k.modules.core.io.proxy.DataObjectProxy"
	      };

	      return types;
	   }
   


   /**
    * Describes the purpose of the module.
    *
    * @return <code>String</code> describing the purpose of the module.
    */
   public String getModuleInfo() {
      String s = "<p>Overview: Testing upload a directory.";
      s+="<p>This module illustrates multi-file upload using the DataProxyObject."+
      "<p>The input are two directories, the module copies all the files and " +
      "directories from the source to the destination." +
      "<p><b>Note:</b> This module sis intended to copy from local to remote, " +
      "wher the local is a local directory URL on a WEBDAV enabled server.  " +
      "However, the DataObjectProxy will also work for local-to-local, " +
      " remote-to-local, and remote-remote.";
      return s;
   } // end method getModuleInfo

   /**
    * Return the name of this module.
    *
    * @return The display name for this module.
    */
   public String getModuleName() { return "putDirectory"; }

   /**
 * Returns the name of the output at the specified index.
 *
 * @param outputIndex Index of the output for which a description should be returned.
 *
 * @return <code>String</code> containing the name of the output at the specified index.
 */
   public String getOutputName() { return null; }

   /**
    * Returns an array of <code>String</code> objects each containing the 
    * fully qualified Java data type of the output at the corresponding index.
    *
    * @return An array of <code>String</code> objects each containing the 
    * fully qualified Java data type of the output at the corresponding index.
    */
   public String[] getOutputTypes() {
      return null;
   }


public String getOutputInfo(int arg0) {
	return null;
}

public CustomModuleEditor getPropertyEditor() { 
	return new PropEdit(); 
}

//~ Inner Classes ***********************************************************
// This class needs additional work to make it nicer to use.  Ideally would 
// like 'browse' buttons for remote objects.
private class PropEdit extends JPanel implements CustomModuleEditor {

   /** Use serialVersionUID for interoperability. */
   static private final long serialVersionUID = 2637786544956495261L;

    private JTextField depthjtf;


   private PropEdit() {
      setLayout(new GridBagLayout());
      this.setMinimumSize(new Dimension(14, 14));

      String dl = getDepth();
      depthjtf = new JTextField(10);
      depthjtf.setText(dl);

      Constrain.setConstraints(this, new JLabel("Depth Level"), 0, 0, 1, 1,
                               GridBagConstraints.NONE,
                               GridBagConstraints.CENTER, 0, 0);
      Constrain.setConstraints(this, depthjtf, 1, 0, 1, 1,
                               GridBagConstraints.HORIZONTAL,
                               GridBagConstraints.CENTER, 1, 0);


   }

   public boolean updateModule() throws Exception {
      String f0 = depthjtf.getText();

      boolean didChange = false;
      String sdl = getDepth();

      if (f0 != sdl) {
         setDepth(f0);
         didChange = true;
      }

      return didChange;
   } // end method updateModule
}
 
} 
