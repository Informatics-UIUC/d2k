package ncsa.d2k.modules.core.transform.binning;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.transformations.BinTransform;
import java.util.*;
import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 * <p>Title: </p>
 * <p>Description: This is a suppoty class for binning Headless UI modules.
 * its methods will be called by the doit method.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author: vered goren
 * @version 1.0
 */

public class BinningUtils {
  public BinningUtils() {
  }


  public static boolean validateBins(Table t, BinDescriptor[] binDes, String commonName) throws Exception{

    if(binDes == null)
      throw new Exception (commonName + " has not been configured. Before running headless, run with the gui and configure the parameters.");

      if(binDes.length == 0){

      System.out.println(commonName + ": No bins were configured. The transformation will be an empty one.");
       return true;
    }

//validating relevancy of bins to the input table.
    HashMap columns = StaticMethods.getAvailableAttributes(t);

    for (int i=0; i<binDes.length; i++){
      if(!columns.containsKey(binDes[i].label.toUpperCase()))
        throw new Exception(commonName + ": Bin " +  binDes[i].toString() + " does not match any column label in the input table. Please reconfigure this module.");
//
    }//for
    return true;
  }



  public static boolean validateBins(HashMap colMap, BinDescriptor[] binDes, String commonName) throws Exception{

   if(binDes == null)
     throw new Exception (commonName + " has not been configured. Before running headless, run with the gui and configure the parameters.");

     if(binDes.length == 0){
     System.out.println(commonName + ": No bins were configured. The transformation will be an empty one.");
      return true;
   }


   for (int i=0; i<binDes.length; i++){
    if(!colMap.containsKey(binDes[i].label.toUpperCase()))
      throw new Exception(commonName+ ": Bin " +  binDes[i].toString() + " does not match any column label in the database table." +
                                       " Please reconfigure this module via a GUI run so it can run Headless.");

  }//for

   return true;
 }


}