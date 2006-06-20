package ncsa.d2k.modules.projects.dtcheng.streams;


public class StreamMarker {

  
  String label = "Level_0";
  int    level = 0;
  
  public StreamMarker() {
    this.label = "unknown";
    this.level = -1;
  }
  
  public StreamMarker(String label, int level) {
    this.label = label;
    this.level = level;
  }
  
  public StreamMarker(StreamMarker marker) {
    this.label = marker.getLabel();
    this.level =  marker.getLevel();
  }
  
  public String getLabel () {
    return this.label;
  }
  public int getLevel () {
    return this.level;
  }
}