package ncsa.d2k.modules.core.io.file.input;

import java.io.*;
import ncsa.d2k.core.modules.*;

/*
* Reads a serialized object from a file.
* @author David Tcheng
 */
public class InputSerializedObject extends InputModule {


    /**
     * Returns the name of the module that is appropriate for end-user consumption.
     *
     * @return The name of the module.
     */
    public String getModuleName() {
        return "Input Serialized Object";
    }


    /**
     * Describes the purpose of the module.
     *
     * @return <code>String</code> describing the purpose of the module.
     */
    public String getModuleInfo() {
        StringBuffer sb = new StringBuffer("<p>Overview: ");
        sb.append("This module reads a Java serialized object from a file.");
        sb.append("</p><p>Detailed Description: In Java, an object can be ");
        sb.append("converted to a stream of bytes and written out to a file ");
        sb.append("in a process called <em>serialization</em>.  ");
        sb.append("</p><p>This module opens the file indicated by the <i>File ");
        sb.append("Name</i> input port and reads the file to reload the Java ");
        sb.append("object that was serialized to the file.  The resulting ");
        sb.append("object is made available on the <i>Java Object</i> output ");
        sb.append("port.  If the file contains more than one serialized object,");
        sb.append("this module will read each object and push them out in the ");
        sb.append("order they are read.  One object will be pushed out per ");
        sb.append("execution cycle.");
        sb.append("</p><p>The module will exit with an error if the file cannot ");
        sb.append("be accessed or does not contain a serialized object. </p> ");

        return sb.toString();
    }


    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the input at
     *         the corresponding index.
     */
    public String[] getInputTypes() {
        String[] types = {"java.lang.String"};
        return types;
    }


    /**
     * Returns an array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     * the corresponding index.
     *
     * @return An array of <code>String</code> objects each containing the fully qualified Java data type of the output at
     *         the corresponding index.
     */
    public String[] getOutputTypes() {
        String[] types = {"java.lang.Object"};
        return types;
    }


    /**
     * Returns a description of the input at the specified index.
     *
     * @param i Index of the input for which a description should be returned.
     * @return <code>String</code> describing the input at the specified index.
     */
    public String getInputInfo(int i) {
        switch (i) {
            case 0:
                return "The filename of the serialized object to be read.";
            default:
                return "No such input";
        }
    }


    /**
     * Returns the name of the input at the specified index.
     *
     * @param i Index of the input for which a name should be returned.
     * @return <code>String</code> containing the name of the input at the specified index.
     */
    public String getInputName(int i) {
        switch (i) {
            case 0:
                return "File Name";
            default:
                return "No such input";
        }
    }


    /**
     * Returns a description of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> describing the output at the specified index.
     */
    public String getOutputInfo(int i) {
        switch (i) {
            case 0:
                return "The Java object that was read from the file.";
            default:
                return "No such output";
        }
    }


    /**
     * Returns the name of the output at the specified index.
     *
     * @param i Index of the output for which a description should be returned.
     * @return <code>String</code> containing the name of the output at the specified index.
     */
    public String getOutputName(int i) {
        switch (i) {
            case 0:
                return "Java Object";
            default:
                return "No such output";
        }
    }

    /** true if this is the first execution */
    private boolean firstExec;
    /** object input stream to read objects from */
    private ObjectInputStream objectInputStream;
    /** the next object from the stream to push out */
    private Object nextObjectFromStream;

    /**
     * Clean up fields and set firstExec to true.
     */
    public void beginExecution() {
        firstExec = true;
        objectInputStream = null;
        nextObjectFromStream = null;
    }

    /**
     * Close the object input stream and clean up fields.
     */
    public void endExecution() {
        try {
            objectInputStream.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }

        nextObjectFromStream = null;
    }

    /**
     * If this is the first execution, return true if the file name input is
     * available.  Otherwise return true while there is an object to be
     * pushed out.
     * @return ready condition
     */
    public boolean isReady() {
        if(firstExec) {
            return getInputPipeSize(0) > 0;
        }

        return nextObjectFromStream != null;
    }


    /**
     * Performs the main work of the module.
     *
     * @throws Exception if a problem occurs while performing the work of the module
     */
    public void doit() throws Exception {

        // if this is the first execution, we need to set up the object input
        // stream, read the first object from the stream, push out this object
        // and read the next object from the stream (if it exists)
        if (firstExec) {
            String FileName = (String) (pullInput(0));
            //ObjectInputStream in = null;

            try {
                objectInputStream = ModuleUtilities.getObjectInputStream(FileName);
                Object obj = objectInputStream.readObject();

                pushOutput(obj, 0);
            }
            catch (FileNotFoundException e) {
                throw new FileNotFoundException("Could not open file: " + FileName +
                        "\n" + e);
            } catch (SecurityException e) {
                throw new SecurityException("Could not open file: " + FileName +
                        "\n" + e);
            }

            // read in the next object.  isReady() checks this for nullness
            // to see if it should fire again
            try {
                nextObjectFromStream = objectInputStream.readObject();
            }
            catch (EOFException eof) {
                // this is fine, it just means we've already read all the objects
                // from the file.
                nextObjectFromStream = null;
            }
            firstExec = false;
        }
        // if this is not the first execution, then we already have an object
        // input stream to read from.  push out the last object read
        // (nextObjectFromStream) and read the next one (if it exists)
        else {
            // push the last object read
            pushOutput(nextObjectFromStream, 0);

            // read in the next object.  isReady() checks this for nullness
            // to see if it should fire again
            try {
                nextObjectFromStream = objectInputStream.readObject();
            }
            catch (EOFException eof) {
                // this is fine, it just means we've already read all the objects
                // from the file.
                nextObjectFromStream = null;
            }
            catch (java.lang.ClassNotFoundException e) {
                throw new ClassNotFoundException("Unable to find object class." +
                        "\n" + e);
            }
            catch (IOException e) {
                throw new IOException("Unable to deserialize object." +
                        "\n" + e);
            }
        }

        //in.close();
        //pushOutput(object, 0);

    }
}
