 package ncsa.d2k.modules.core.discovery.ruleassociation;

import java.io.*;
import java.lang.Math;
import java.util.*;
import gnu.trove.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.discovery.ruleassociation.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import java.beans.PropertyVetoException;

/**
 * @author Hong Cheng
 * @version 1.0
 */

public class PatSummarization extends ncsa.d2k.core.modules.ComputeModule{

     /**
      * Return the human readable name of the module.
      * @return the human readable name of the module.
      */
     public String getModuleName() {
         return "Frequent Pattern Summarization";
     }

     /**
      * This method returns the description of the module.
      * @return the description of the module.
      */
      public String getModuleInfo () {
          StringBuffer sb = new StringBuffer( "<p>Overview: ");
          sb.append("This module summarizes the frequent itemsets output by apriori or fpgrowth.");
          sb.append("It partitions the itemsets into K clusters based on their KL divergence on the data");
          sb.append("This module can effectively reduce the number of frequent patterns and achieve higher ");
          sb.append("usability and interpretability on frequent itemsets for end users.");
          sb.append("</p><p>");
          sb.append("User needs to specify a parameter K, the number of clusters he wants.");
          sb.append("Each cluster center will be returned as a profile modeling the patterns.");

          return sb.toString();
      }

      /**
       * Return the human readable name of the indexed input.
       * @param index the index of the input.
       * @return the human readable name of the indexed input.
       */

      public String getInputName(int index) {
         switch(index) {
                 case 0:
                         return "Item Sets";
                 case 1:
                         return "Frequent Itemsets";
                 default:
                         return "No such input";
         }
      }

      /**
       * This method returns the description of the various inputs.
       * @return the description of the indexed input.
       */

      public String getInputInfo(int index) {
        switch (index) {
            case 0:
                return "An Item Sets object containing the items of interest in the original data. " +
                       "This object is typically produced by a <i>Table To Item Sets</i> module.";
            case 1:
                return "The frequent itemsets found by an <i>Apriori</i> or <i>FPGrowth</i> module.  These are the " +
                         "item combinations that frequently appear together in the original examples.";
            default:
                return "No such input";
        }
      }

      /**
       * This method returns an array of strings that contains the data types for the inputs.
       * @return the data types of all inputs.
       */
      public String[] getInputTypes () {
        String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.ItemSets","[[I"};
        return types;
      }

      /**
       * Return the human readable name of the indexed output.
       * @param index the index of the output.
       * @return the human readable name of the indexed output.
       */
      public String getOutputName(int index) {
              switch(index) {
                      case 0:
                              return "Rule Table";
                      default:
                              return "No such output";
              }
      }


      /**
       * This method returns the description of the outputs.
       * @return the description of the indexed output.
       */
      public String getOutputInfo (int index) {
              switch (index) {
                case 0:
                   String s = "A representation of the association rules found and accepted by this module. " +
                             "This output is typically connected to a <i>Rule Visualization</i> module.";
                  return s;
                default:
                  return "No such output";
              }
      }

      /**
       * This method returns an array of strings that contains the data types for the outputs.
       * @return the data types of all outputs.
       */
      public String[] getOutputTypes () {
        String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.RuleTable"};
        return types;
      }

      /** Properties **/

      /** K - number of clusters */
      int clusterNum = 20;
      Vector [] patIDList;
      double [][] patProfiles;

      public int getClusterNum () {
              return clusterNum;
      }
      public void setClusterNum (int K) throws PropertyVetoException {
              if ( K <= 0 ) {
                       throw new PropertyVetoException (
                              " Number of clusters must be greater than 0.",
                              null );
              }
              clusterNum = K;
      }

      /**
       * Return a list of the property descriptions.
       * @return a list of the property descriptions.
       */
      public PropertyDescription [] getPropertiesDescriptions () {
        PropertyDescription [] pds = new PropertyDescription [1];
        pds[0] = new PropertyDescription(
                "clusterNum",
                "Number of Clusters K",
                "The set of frequent itemsets will be partitioned into K clusters.");
        return pds;
      }

      public boolean contains(ItemSets iss, int [][] fis, int tranIdx, int patIdx)
      {
        int i,j;
        boolean [][] itemF = iss.getItemFlags();

        for(i = 0; i < fis[patIdx].length - 1 ; )
        {
          if(itemF[tranIdx][fis[patIdx][i]])
          {
            i ++;
          }
          else
            return false;
        }

        return true;
      }

      public void computeProfile(ItemSets iss, int [][] fis, int idx)
      {
        int i, j;

        int totalCnt = 0;

        boolean [][] itemF = iss.getItemFlags();

        for(i = 0; i < iss.names.length; i ++)
          patProfiles[idx][i] = 0;

        for(i = 0; i < iss.numExamples; i ++)
        {
          if(contains(iss, fis, i, idx))
          {
            patIDList[idx].addElement(new Integer(i));
            totalCnt ++;
            for(j = 0; j < itemF[i].length; j ++)
              if(itemF[i][j])
                patProfiles[idx][j] += 1.0;

          }
        }

        for(i = 0; i < iss.names.length; i ++)
          patProfiles[idx][i] /= totalCnt;
     }

   //KL divergence on fixed dimension
   public double KLDivergence1(double [] patProfiles, double [] patProfile1, int numItems)
   {
           int i;
           double kld = 0;
           double tmp = 0;

           for(i = 0; i < numItems; i ++)
           {
             if(patProfiles[i] < 0.01)
               patProfiles[i] = 0.01;

             if(patProfiles[i] > 0.99)
               patProfiles[i] = 0.99;

             if(patProfile1[i] < 0.01)
               patProfile1[i] = 0.01;

             if(patProfile1[i] > 0.99)
               patProfile1[i] = 0.99;

              tmp = Math.log(patProfiles[i]/patProfile1[i])/Math.log(2.0);
              tmp *= patProfiles[i];
              kld += tmp;

              tmp = Math.log((1.0-patProfiles[i])/(1.0-patProfile1[i]))/Math.log(2);
              tmp *= (1.0-patProfiles[i]);
              kld += tmp;
           }

           return kld;
      }

      public void simpleClusterCenter(int idx, Vector [] clusterMem, double [][] initCenter, double [][] patProfiles, int numItems)
      {
              int i, j;

              for(i = 0; i < numItems; i ++)
                initCenter[idx][i] = 0;

              for(i = 0; i < clusterMem[idx].size(); i ++)
                for(j = 0; j < numItems; j ++)
                  initCenter[idx][j] += patProfiles[((Integer)(clusterMem[idx].elementAt(i))).intValue()][j];

              if(clusterMem[idx].size() > 0)
                for(i = 0; i < numItems; i ++)
                  initCenter[idx][i] /= clusterMem[idx].size();
      }

      public void computeItemUnion(int idx, Vector [] clusterMem, int[][] fis, boolean [] initUnion, int numItems)
      {
              int i, j;
              int tmp;
              for(i = 0; i < numItems; i ++)
                initUnion[i] = false;

              for(i = 0; i < clusterMem[idx].size(); i ++)
              {
                tmp = ((Integer)clusterMem[idx].elementAt(i)).intValue();
                for(j = 0; j < fis[tmp].length-1; j ++)
                  initUnion[fis[tmp][j]] = true;
              }
      }

      public void doit () throws Exception {

        // pull the inputs.
        ItemSets iss = (ItemSets) this.pullInput(0);
        int [][] fis = (int [][]) this.pullInput (1);
        String [] items = iss.names;

        // Init the counters.
        int numExamples = iss.numExamples;
        int numFis = fis.length;
        int numItems = items.length;

        int Iter = 10;
        int i, j;

/*
        FileWriter fw = new FileWriter("iss1.txt");

        fw.write("names:\n");
        for(i = 0; i < items.length; i ++)
        {
          fw.write(items[i] + " ");
        }
        fw.write("\n");
*/
        boolean [][] itemF = iss.getItemFlags();
/*
        fw.write("nums: " + iss.numExamples + "\n");
        fw.write("flags\n");
        for(i = 0; i < 3; i ++)
        {
          for (j = 0; j < itemF[i].length; j++)
            fw.write(itemF[i][j] + " ");
          fw.write("\n");
        }

        fw.write("num of fis " + fis.length + "\n");
        for(i = 0; i < fis.length; i ++)
        {
          for(j = 0; j < fis[i].length; j ++)
            fw.write(fis[i][j] + " ");
          fw.write("\n");
        }
*/
       patProfiles = new double[fis.length][iss.names.length];

       this.patIDList = new Vector [fis.length];
       for(i = 0; i < fis.length; i ++)
         patIDList[i] = new Vector();

       for(i = 0; i < numFis; i ++)
         computeProfile(iss, fis, i);
/*
       fw.write("pat profiles: \n");
       for(i = 0; i < numFis; i ++)
       {
         for (j = 0; j < numItems; j++)
           fw.write(this.patProfiles[i][j] + " ");
         fw.write("\n");
       }

       fw.write("patIDList:\n");

       for(i = 0; i < numFis; i ++)
       {
         for (j = 0; j < patIDList[i].size(); j++)
           fw.write( ( (Integer) patIDList[i].elementAt(j)).intValue() + " ");
         fw.write("\n");
       }
       fw.flush();
       fw.close();
*/
       double [][] initCenter = new double[this.clusterNum][numItems];

       boolean [][] initUnion = new boolean [this.clusterNum][numItems];
       int item=0;

       //initialization
       for(i = 0; i < clusterNum; i ++)
         for(j = 0; j < numItems; j ++)
           initUnion[i][j] = false;

      Random rdm = new Random();
      Date d = new Date();
      rdm.setSeed(d.getTime());

      int [] centerID = new int [this.clusterNum];
      for(i = 0; i < this.clusterNum; i ++)
        centerID[i] = 0;

      for(i = 0; i < this.clusterNum; i ++)
      {
        while(true)
        {
          item = rdm.nextInt(numFis);

          for(j = 0; j < i; j ++)
            if(item == centerID[j])
              break;

          if(j == i)
          {
            centerID[i] = item;
            break;
          }
        }//end of while

        for(j = 0; j < numItems; j ++)
          initCenter[i][j] = patProfiles[item][j];

        for(j = 0; j < fis[item].length - 1; j ++)
          initUnion[i][fis[item][j]] = true;
      }

      int count = 0;
      double tmpKL = 0;
      double minKL = 0;
      int idxI = 0;

      Vector [] clusterMem = new Vector [clusterNum];
      for(i = 0; i < clusterNum; i ++)
        clusterMem[i] = new Vector();

      //2nd step, kmeans
      //for each patProfile
      //for each cluster center
      //compute a KL divergence and select the minimum
      //record the patIDList for each cluster

      //after patIDList is computed, compute the center profiles
      //iterate the whole process

      int iter;
      for(iter = 0; iter < Iter; iter ++)
      {
         for(i = 0; i < clusterNum; i ++)
           clusterMem[i].clear();

         for(i = 0; i < numFis; i ++)
         {
           for(j = 0; j < clusterNum; j ++)
           {
             if(j == 0)
             {
               minKL = KLDivergence1(patProfiles[i], initCenter[j], numItems);
               idxI = 0;
             }
             else
             {
                 tmpKL = KLDivergence1(patProfiles[i], initCenter[j], numItems);
                 if(minKL > tmpKL)
                 {
                   minKL = tmpKL;
                   idxI = j;
                 }
             }
          }

          //the ith pat belongs to cluster j
          clusterMem[idxI].addElement(new Integer(i));
         }

         int subCnt = 0;
         for(i = 0; i < clusterNum; i ++)
           if(clusterMem[i].size() == 0)
             ;//	printf("cluster %d is empty\n", i);
           else
             subCnt += clusterMem[i].size();
/*
         for(i = 0; i < numItems; i ++)
           System.out.print(initCenter[0][i]+" ");
         System.out.println();
*/
         for(i = 0; i < clusterNum; i ++)
           simpleClusterCenter(i, clusterMem, initCenter, patProfiles, numItems);

/*
         System.out.println("after");
         for(i = 0; i < numItems; i ++)
           System.out.print(initCenter[0][i]+" ");
         System.out.println();
*/
         for(i = 0; i < clusterNum; i ++)
           computeItemUnion(i, clusterMem, fis, initUnion[i], numItems);
       }

       //the output is ready in initCenter, next step is visulization
       //initCenter is a two dimensional array
       for(i = 0;  i < items.length; i ++)
         System.out.print(items[i] + " ");
       System.out.println();

       for(i = 0; i < clusterNum; i ++)
       {    for(j = 0; j < numItems; j ++)
         {
           System.out.print(initCenter[i][j] + " ");
         }
         System.out.print("cluster member #: "+clusterMem[i].size());
         System.out.println();
       }
    }
}
