package ncsa.d2k.modules.core.io.file.output;

/**
 * Write a table out as a serialized object.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class WriteSerializedTable extends OutputSerializedObject {

    public String getModuleName() {
        return "WriteSerializedTable";
    }

    public String[] getInputTypes()
      {
        String[] types = {"ncsa.d2k.modules.core.datatype.table.Table","java.lang.String"};
        return types;
	}

    public String getInputInfo(int i)
      {
        switch (i) {
            case 0: return "The Table to Serialize.";
            case 1: return "The filename to write to";
            default: return "No such input";
        }
    }

    public String getInputName(int i)
      {
        switch(i) {
            case 0:
                return "TableToSerialize";
            case 1:
                return "no such input!";
            default: return "NO SUCH INPUT!";
        }
	}
}
