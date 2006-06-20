package ncsa.d2k.modules.projects.dtcheng.matrix;

// import java.net.*;
// import java.io.*;

public class SimplePigeonHoles {

  private Object[] thePigeonHoles;
  private int nextOpenSpot;
  private boolean spotsAreOpen = false;

  public SimplePigeonHoles(int numHoles) throws Exception {
    if (numHoles < 1) {
      throw new Exception("SimplePigeonHoles error: numHoles ["
          + numHoles + "] must be > 0.");
    }
    thePigeonHoles = new Object[numHoles];
    nextOpenSpot = 0;
  }

  public synchronized boolean areRoosting() {
    if (nextOpenSpot == 0) {
      return false;
    } else {
      return true;
    }
  }

  public synchronized boolean areOpenings() {
    if (nextOpenSpot < thePigeonHoles.length) {
      return true;
    } else {
      return false;
    }
  }
  
  public synchronized boolean storeObject(Object objectToStore) {
    if (nextOpenSpot < thePigeonHoles.length) {
      // if there is space, we store it in the next open spot and
      // return a "true" saying that we successfully complete the storage
      thePigeonHoles[nextOpenSpot] = objectToStore;
      nextOpenSpot++;
      return true;
    } else {
      // if there is no space left, we do not store it and
      // return a false: that is, try again later...
      return false;
    }
  }
  
  public synchronized Object[] getContents() {
    // create an array of the right size; if this is used stupidly,
    // it will, in fact, return an Object[0]
    Object[] contentsToReport = new Object[nextOpenSpot];
    
    // fill it up with the valid entries
    for (int holeIndex = 0; holeIndex < nextOpenSpot; holeIndex++) {
      contentsToReport[holeIndex] = thePigeonHoles[holeIndex];
    }
    
    // reset the nextOpenSpot
    
    nextOpenSpot = 0;
    
    return contentsToReport;
  }

}