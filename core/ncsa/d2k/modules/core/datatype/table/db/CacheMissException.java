package ncsa.d2k.modules.core.datatype.table.db;


public class CacheMissException extends Exception { //IndexOutOfBoundsException {

  public CacheMissException() {
      super("A Cache Miss has occured");
  }

  public CacheMissException(String message) {
      super(message);
  }

}