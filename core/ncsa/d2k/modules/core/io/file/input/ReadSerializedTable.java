package ncsa.d2k.modules.core.io.file.input;

public class ReadSerializedTable extends InputSerializedObject {

    /**
     * put your documentation comment here
     * @return
     */
    public String getModuleName () {
        return "ReadSerializedTable";
	}

    public String[] getOutputTypes () {
          String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
          return types;
	}

    public String getOutputInfo (int i) {
          switch (i) {
              case 0: return "The deserialized Table.";
              default: return "No such output";
          }
      }

    public String getOutputName (int i) {
          switch(i) {
              case 0:
                  return "DeserializedTable";
              default: return "NO SUCH OUTPUT!";
          }
	}
}
