package ncsa.d2k.modules.core.io.file.output;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;

/**
   Write the contents of a Table to a flat file.
   @author David Clutter
*/
public class WriteTableToFile extends OutputModule
    {

   transient String delimiter;

   boolean comma = true;
//	boolean tab = true;
//	boolean space = false;
   boolean useDataTypes = true;
   boolean useColumnLabels = true;

/*    public boolean getComma() {
      return comma;
    }

    public void setComma(boolean b) {
      comma = b;
    }

    public boolean getTab() {
      return tab;
    }

    public void setTab(boolean b) {
      tab = b;
    }

    public boolean getSpace() {
      return space;
    }

    public void setSpace(boolean b) {
      space = b;
    }
*/

   public void setUseDataTypes(boolean b) {
      useDataTypes = b;
   }

   public boolean getUseDataTypes() {
      return useDataTypes;
   }

   public void setUseColumnLabels(boolean b) {
      useColumnLabels = b;
   }

   public boolean getUseColumnLabels() {
      return useColumnLabels;
   }

    /**
       Return a description of the function of this module.
       @return A description of this module.
    */
    public String getModuleInfo() {
       /*
       return "<html>  <head>      </head>  <body>    Write the contents of " +
              "a Table to a flat file. Can use space, comma, or tab " +
              "as a delimiter. If useColumnLabels is set, the first row of " +
              "the file will be the column labels. If useDataTypes is set, " +
              "the data type of each row will be written.  </body></html>";
       */
       StringBuffer sb = new StringBuffer("<p>Overview: ");
       sb.append("This module writes the contents of a Table to a flat, ");
       sb.append("delimited file. A space, a comma, or a tab can be used ");
       sb.append("as a delimiter. If the useColumnLabels property is set, ");
       sb.append("the first row of the file will be the column labels. ");
       sb.append("If the useDataTypes property is set, the data type of ");
       sb.append("each row will also be written.");
       sb.append("</p><p>Data Handling: ");
       sb.append("This module does not destroy or modify its input data.");
       sb.append("</p>");
       return sb.toString();
   }

   public PropertyDescription[] getPropertiesDescriptions() {

      PropertyDescription[] descriptions = new PropertyDescription [2];

      descriptions[0] = new PropertyDescription("useColumnLabels",
         "Write Column Labels",
         "Controls whether the table's column labels are written to the file.");

      descriptions[1] = new PropertyDescription("useDataTypes",
         "Write Data Types",
         "Controls whether the table's column data types are written to the file.");

      return descriptions;

   }

    /**
       Return the name of this module.
       @return The name of this module.
    */
    public String getModuleName() {
      return "Write Table to File";
   }

    /**
       Return a String array containing the datatypes the inputs to this
       module.
       @return The datatypes of the inputs.
    */
    public String[] getInputTypes() {
      String[] types = {"java.lang.String","ncsa.d2k.modules.core.datatype.table.Table"};
      return types;
   }

    /**
       Return a String array containing the datatypes of the outputs of this
       module.
       @return The datatypes of the outputs.
    */
    public String[] getOutputTypes() {
      String[] types = {		};
      return types;
   }

    /**
       Return a description of a specific input.
       @param i The index of the input
       @return The description of the input
    */
    public String getInputInfo(int i) {
      switch (i) {
         case 0: return "The name of the file to be written.";
         case 1: return "The Table to write.";
         default: return "No such input";
      }
   }

    /**
       Return the name of a specific input.
       @param i The index of the input.
       @return The name of the input
    */
    public String getInputName(int i) {
      switch(i) {
         case 0:
            return "File Name";
         case 1:
            return "Table";
         default: return "NO SUCH INPUT!";
      }
   }

    /**
       Return the description of a specific output.
       @param i The index of the output.
       @return The description of the output.
    */
    public String getOutputInfo(int i) {
      switch (i) {
         default: return "No such output";
      }
   }

    /**
       Return the name of a specific output.
       @param i The index of the output.
       @return The name of the output
    */
    public String getOutputName(int i) {
      switch(i) {
         default: return "NO SUCH OUTPUT!";
      }
   }

    /**
      Write the table to the file.
   */
    public void doit() {
      String fileName = (String)pullInput(0);
      Table vt = (Table)pullInput(1);
      FileWriter fw;
      String newLine = "\n";

      if(comma)
         delimiter = ",";
      /*if(tab)
         delimiter = "\t";
      if(space)
         delimiter = " ";
  */

      try {
         /*
         fw = new FileWriter(fileName);

         // write the column labels
         if(useColumnLabels) {
            for(int i = 0; i < vt.getNumColumns(); i++) {
               String s = vt.getColumnLabel(i);
               fw.write(s, 0, s.length());
               if(i != (vt.getNumColumns() - 1))
                  fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }
            fw.write(newLine.toCharArray(), 0, newLine.length());
         }

         // write the datatypes.
         if(useDataTypes) {
            for(int i = 0; i < vt.getNumColumns(); i++) {
               String s = getDataType(vt.getColumnType(i));
               fw.write(s, 0, s.length());
               if(i != (vt.getNumColumns() - 1))
                  fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }
            fw.write(newLine.toCharArray(), 0, newLine.length());
         }

         // write the actual data
         for(int i = 0; i < vt.getNumRows(); i++) {
            for(int j = 0; j < vt.getNumColumns(); j++) {
               String s = vt.getString(i, j);
               //System.out.println("s: "+s);
               fw.write(s, 0, s.length());
               if(j != (vt.getNumColumns() - 1) )
                  fw.write(delimiter.toCharArray(), 0, delimiter.length());
            }
            fw.write(newLine.toCharArray(), 0, newLine.length());
         }
         fw.flush();
         fw.close();
         */
         writeTable(vt, delimiter, fileName, useColumnLabels, useDataTypes);
      }
      catch(IOException e) {
         e.printStackTrace();
      }
   }

   /**
      Get the datatype of a column.
   */
   /*public static final String getDataType(Column c) {
      if(c instanceof StringColumn)
         return "String";
      else if(c instanceof IntColumn)
         return "int";
      else if(c instanceof FloatColumn)
         return "float";
      else if(c instanceof DoubleColumn)
         return "double";
      else if(c instanceof LongColumn)
         return "long";
      else if(c instanceof ShortColumn)
         return "short";
      else if(c instanceof BooleanColumn)
         return "boolean";
      else if(c instanceof ObjectColumn)
         return "Object";
      else if(c instanceof ByteArrayColumn)
         return "byte[]";
      else if(c instanceof CharArrayColumn)
         return "char[]";
      else
         return "unknown";
   }*/

   /**
      Get the datatype of a column.
   */
   public static final String getDataType(int i) {
      switch(i) {
         case 0:
            return "int";
         case 1:
            return "float";
         case 2:
            return "double";
         case 3:
            return "short";
         case 4:
            return "long";
         case 5:
            return "String";
         case 6:
            return "char[]";
         case 7:
            return "byte[]";
         case 8:
            return 	"boolean";
         case 9:
            return "Object";
         case 10:
            return "byte";
         case 11:
            return "char";
         default:
            return "String";
      }
   }

   public static void writeTable(Table vt, String delimiter, String fileName,
      boolean writeColumnLabels, boolean writeColumnTypes) throws IOException {

      FileWriter fw = new FileWriter(fileName);
      String newLine = "\n";

      // write the column labels
      if(writeColumnLabels) {
         for(int i = 0; i < vt.getNumColumns(); i++) {
            String s = vt.getColumnLabel(i);
            fw.write(s, 0, s.length());
            if(i != (vt.getNumColumns() - 1))
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
         }
         fw.write(newLine.toCharArray(), 0, newLine.length());
      }

      // write the datatypes.
      if(writeColumnTypes) {
         for(int i = 0; i < vt.getNumColumns(); i++) {
            String s = getDataType(vt.getColumnType(i));
            fw.write(s, 0, s.length());
            if(i != (vt.getNumColumns() - 1))
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
         }
         fw.write(newLine.toCharArray(), 0, newLine.length());
      }

      // write the actual data
      for(int i = 0; i < vt.getNumRows(); i++) {
         for(int j = 0; j < vt.getNumColumns(); j++) {
            String s = vt.getString(i, j);
            //System.out.println("s: "+s);
            fw.write(s, 0, s.length());
            if(j != (vt.getNumColumns() - 1) )
               fw.write(delimiter.toCharArray(), 0, delimiter.length());
         }
         fw.write(newLine.toCharArray(), 0, newLine.length());
      }
      fw.flush();
      fw.close();

   }

}
