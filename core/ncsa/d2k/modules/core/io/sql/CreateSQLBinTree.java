package ncsa.d2k.modules.core.io.sql;

import java.sql.*;

import ncsa.d2k.core.modules.*;

import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;

public class CreateSQLBinTree extends DataPrepModule {

    public String[] getInputTypes() {
        String[] in = {"ncsa.d2k.modules.core.datatype.table.transformations.BinTransform",
            "[Ljava.lang.String;", "[Ljava.lang.String;",
            "ncsa.d2k.modules.io.sql.ConnectionWrapper",
            "java.lang.String"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.datatype.BinTree"};
        return out;
    }

    public String getInputInfo(int i) {
        return "";
    }

    public String getOutputInfo(int i) {
        return "";
    }

    public String getInputName(int i) {
        return "";
    }

    public String getOutputName(int i) {
        return "";
    }

    public String getModuleInfo() {
        return "";
    }

    public String getModuleName() {
        return "";
    }

    public void doit() {
        BinTransform btrans = (BinTransform)pullInput(0);
        String[] cn = (String[])pullInput(0);
        String[] an = (String[])pullInput(1);
        ConnectionWrapper conn = (ConnectionWrapper)pullInput(2);
        String tableName = (String)pullInput(3);

        BinTree bt = btrans.createBinTree(cn, an);

        // !!!!!!!!!!!!! need class label
        String classLabel = "";

        // we can do the counting here too if we want.

    /*ConnectionWrapper conn  = (ConnectionWrapper)pullInput(0);
    String tableName = (String)pullInput(1);
    BinTree bt = (BinTree)pullInput(2);
    ExampleTable vt = (ExampleTable)pullInput(3);

    int [] ins = vt.getInputFeatures();
    int [] out = vt.getOutputFeatures();
    */

        // we only support one out variable..
        //int classColumn = out[0];
        //String classLabel = vt.getColumnLabel(classColumn);
        int totalClassified = 0;
        int classTotal;
        int binListTotal;

        long startTime = System.currentTimeMillis();

        // get sql counts and set the tallys
        //String cn[] = bt.getClassNames();
        //String an[] = bt.getAttributeNames();

        try {
            Statement stmt = conn.getConnection().createStatement();
            for (int i=0; i <cn.length ; i++) {
                classTotal =0;
                for (int j=0; j <an.length ; j++) {
                    String [] bn = bt.getBinNames(cn[i],an[j]);
                    binListTotal = 0;

                    for ( int k = 0; k < bn.length; k ++)
                    {

                        BinTree.ClassTree ct = (BinTree.ClassTree)bt.get(cn[i]);
                        BinTree.ClassTree.BinList bl
                                = (BinTree.ClassTree.BinList)ct.get(an[j]);
                        BinTree.ClassTree.Bin b = (BinTree.ClassTree.Bin)bl.get(bn[k]);
                        String condition =  b.getCondition(an[j]);

                        //String condition = bt.getCondition(cn[i],an[j],bn[k]);
                        //System.out.println("cn, an, bn, BIN Condition: "
                        //         + cn[i] + " " + an[j] + " "
                        //                 + bn[k] + " " + condition);

                        String query = "SELECT COUNT(*)  FROM " + tableName
                                     + " WHERE " + classLabel + " = \'"
                                     + cn[i] + "\' AND " + condition;
                        // System.out.println("BIN Query: "+ query);
                        ResultSet count =
                                stmt.executeQuery("SELECT COUNT(*)  FROM " + tableName
                                + " WHERE " + classLabel + " = \'"
                                + cn[i] + "\' AND " + condition);

                        while (count.next()) {
                            int s = count.getInt(1);
                            b.setTally(s);
                            totalClassified = totalClassified + s;
                            classTotal = classTotal + s;
                            binListTotal = binListTotal + s;
                            //System.out.println("COUNT: " + s);

                        }
                        bl.setTotal(binListTotal);
                        count.close();
                    }
                }
                bt.setClassTotal(cn[i],classTotal);
            }

            bt.setTotalClassified (totalClassified);
            } catch (SQLException e) { System.out.println(e); }
            catch (ClassNotFoundException ne) { System.out.println( ne); }
            catch (InstantiationException ee) { System.out.println( ee); }
            catch (IllegalAccessException nie) { System.out.println( nie); };

            long endTime = System.currentTimeMillis();
            System.out.println ( "time in msec " + (endTime-startTime));



/*
            pushOutput(bt, 0);
  pushOutput(vt, 1);

    */

            pushOutput(bt, 0);
    }
}