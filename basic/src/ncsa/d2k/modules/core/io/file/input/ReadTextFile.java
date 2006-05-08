package ncsa.d2k.modules.core.io.file.input;
import java.io.*;
import ncsa.d2k.core.modules.*;


/**
 *
 * <p>Title: ReadTextFile</p>
 * <p>Description: This module reads in a text file and outputs its  content in a String.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Vered
 * @version 1.0
 */
public class ReadTextFile extends ReentrantComputeModule {
  public ReadTextFile() {
  }

  /**

   */
  protected void doit(){
    String fileName = (String)pullInput(0);
    String output = this.readFile(fileName);
    pushOutput(output, 0);
  }




   /**

    * @return PropertyDescription[]
    */
   public PropertyDescription[] getPropertiesDescriptions()
 {
   PropertyDescription[] pds = new PropertyDescription[1];
   pds[0] = new PropertyDescription("bufferSize", "Buffer Size", "The size " +
                                    "of the buffer to read the file in to.");

         return pds;
 }



  /**

   * @return String
   */
  public String getModuleInfo(){
    return "<P><B>Overview:</b><br>" +
        "This module reads the content of a text file and outputs that content as a String.</P>" +
        "<P><B>Detailed Description:</b><BR>" +
        "This module attempts to read into a buffered reader with size <i>Buffer Size</i> " +
        "the content of a text file named <i>File Name</i>. The content of the buffer " +
        "is appended to a String during reading. At the end of the process " +
        "this String is pushed out.</p>";
  }



  /**

   * @return String
   */
  public String getModuleName(){
    return "Read Text File";
  }

  /**

   * @return String[]
   */
  public String[] getInputTypes(){
    String [] types = new String[1];
    types[0] = "java.lang.String";
    return types;
  }

  /**
  * @todo implement this method
  * @return String
  */
  public String getInputInfo(int index){
    switch(index){
      case 0: return "A path to the text file to read in";
      default: return "no such input";
    }
  }


  /**

  * @return String
  */
 public String getInputName(int index){
    switch(index){
      case 0: return "File Name";
      default: return "no such input";
    }
  }



  /**

  * @return String[]
  */
 public String[] getOutputTypes(){
   String [] types = new String[1];
   types[0] = "java.lang.String";
   return types;
 }

 /**
 * @return String
 */
 public String getOutputInfo(int index){
   switch(index){
     case 0: return "The content of the input text file.";
     default: return "no such output";
   }
 }


 /**
 * @return String
 */
public String getOutputName(int index){
   switch(index){
     case 0: return "File Content";
     default: return "no such output";
   }
 }


  private int bufferSize;

  public int getBufferSize(){return bufferSize;}
  public void setBufferSize(int size){bufferSize = size;}


  /**
   * reads the content of <codE>fileName</code> into a buffered reader and
   * appends it to a String object. when reaching the end of the file,
   * the String is returned.
   * @param fileName String a path to the text file to read
   * @return String the content of the text file.
   */
  public String readFile(String fileName){
    BufferedReader in = null;

    char[] buffer = new char[bufferSize];
    String retVal = "";

    try {
      in = new BufferedReader(new FileReader(fileName));


    int read = bufferSize;
     /* while ( read == bufferSize) {
        read = in.read(buffer, 0, bufferSize);
        retVal += new String(buffer, 0, read);
      }//while*/
     String line;
     while((line = in.readLine())!= null){
       retVal += line + "\n";
     }

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        in.close();
        return retVal;
      }
      catch (IOException e) {
        e.printStackTrace();
        return retVal;
    }
    }


  }
}
