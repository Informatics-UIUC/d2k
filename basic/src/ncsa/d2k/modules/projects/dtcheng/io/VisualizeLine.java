package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

import java.util.Random;

public class VisualizeLine extends InputModule {

  private boolean OverlayGraphs = true;

  public void setOverlayGraphs(boolean value) {
    this.OverlayGraphs = value;
  }

  public boolean getOverlayGraphs() {
    return this.OverlayGraphs;
  }

  private long WaitTimeInMilliSeconds = 0;

  public void setWaitTimeInMilliSeconds(long value) {
    this.WaitTimeInMilliSeconds = value;
  }

  public long getWaitTimeInMilliSeconds() {
    return this.WaitTimeInMilliSeconds;
  }

  public String getModuleName() {
    return "VisualizeLine";
  }

  public String getModuleInfo() {
    return "VisualizeLine";
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "1d or 2d double array";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "1d or 2d double data";
    default:
      return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = { "java.lang.Object" };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "PixelVector";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "PixelVector";
    default:
      return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[D", };
    return types;
  }

  private Container windowPane;

  int XSize;

  int YSize;

  int HalfXSize;

  int HalfYSize;

  GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

  GraphicsDevice device = env.getDefaultScreenDevice();

  BufferStrategy bufferStrategy;

  private static Color[] COLORS = new Color[] { Color.red, Color.blue, Color.green, Color.white, Color.black, Color.yellow,
      Color.gray, Color.cyan, Color.pink, Color.lightGray, Color.magenta, Color.orange, Color.darkGray };

  private static DisplayMode[] BEST_DISPLAY_MODES = new DisplayMode[] {
  //new DisplayMode(320, 240, 32, 0),
      //new DisplayMode(320, 240, 16, 0),
      //new DisplayMode(320, 240, 8, 0),
      //new DisplayMode(640, 480, 32, 0),
      //new DisplayMode(640, 480, 16, 0),
      //new DisplayMode(640, 480, 8, 0),
      new DisplayMode(1024, 768, 32, 0), new DisplayMode(1024, 768, 16, 0), new DisplayMode(1024, 768, 8, 0),
  //new DisplayMode(1280, 1024, 32, 0), 
  //new DisplayMode(1280, 1024, 16, 0), 
  //new DisplayMode(1280, 1024, 8, 0), 
  };

  Frame mainFrame;

  private static DisplayMode getBestDisplayMode(GraphicsDevice device) {
    for (int x = 0; x < BEST_DISPLAY_MODES.length; x++) {
      DisplayMode[] modes = device.getDisplayModes();
      for (int i = 0; i < modes.length; i++) {
        if (modes[i].getWidth() == BEST_DISPLAY_MODES[x].getWidth() && modes[i].getHeight() == BEST_DISPLAY_MODES[x].getHeight()
            && modes[i].getBitDepth() == BEST_DISPLAY_MODES[x].getBitDepth()) {
          return BEST_DISPLAY_MODES[x];
        }
      }
    }
    return null;
  }

  public static void chooseBestDisplayMode(GraphicsDevice device) {
    DisplayMode best = getBestDisplayMode(device);
    if (best != null) {
      device.setDisplayMode(best);
    }
  }

  public void endExecution() {
    //device.setFullScreenWindow(null);
    //if (device.isDisplayChangeSupported()) {
    //  chooseBestDisplayMode(device);
    //}
    //if (mainFrame != null)
    //  mainFrame.dispose();
  }

  Color [] Colors = {
      new Color(0.0f, 0.0f, 0.0f),
      new Color(1.0f, 0.0f, 0.0f),
      new Color(0.0f, 1.0f, 0.0f),
      new Color(0.0f, 0.0f, 1.0f),
      new Color(1.0f, 1.0f, 0.0f),
      new Color(1.0f, 0.0f, 1.0f),
      new Color(0.0f, 0.5f, 0.5f),
      new Color(0.5f, 0.0f, 0.0f),
      new Color(0.0f, 0.5f, 0.0f),
      new Color(0.0f, 0.0f, 0.5f),
      new Color(0.5f, 0.5f, 0.0f),
      new Color(0.5f, 0.0f, 0.5f),
      new Color(0.0f, 0.5f, 0.5f),
      new Color(1.0f, 0.5f, 0.5f),
      new Color(0.5f, 1.0f, 1.0f),
      new Color(1.0f, 0.5f, 1.0f),
      new Color(1.0f, 1.0f, 0.5f),
      new Color(0.5f, 0.5f, 1.0f),
      new Color(0.5f, 1.0f, 0.5f),
      new Color(1.0f, 0.5f, 0.5f)
      }; 
  
  
  public void doit() throws Exception {

    if (mainFrame == null) {

      //int numBuffers = 2;

      env = GraphicsEnvironment.getLocalGraphicsEnvironment();
      device = env.getDefaultScreenDevice();
      //MultiBufferTest test = new MultiBufferTest(numBuffers, device);

      try {

        //GraphicsConfiguration gc = device.getDefaultConfiguration();
        //mainFrame = new Frame(gc);
        mainFrame = new Frame();
        //mainFrame.setUndecorated(true);
        //mainFrame.setIgnoreRepaint(true);
        device.setFullScreenWindow(mainFrame);
        //if (device.isDisplayChangeSupported()) {
        //  chooseBestDisplayMode(device);
        //}
        //mainFrame.createBufferStrategy(numBuffers);
        bufferStrategy = mainFrame.getBufferStrategy();

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    Object object = this.pullInput(0);

    if (object == null) {
      this.pushOutput(null, 0);
      return;
    }

    int NumDimensions = -1;
    double[] data1D = null;
    double[][] data2D = null;

    if (object.getClass().getName().equals("[D")) {
      //System.out.println("1D array");
      NumDimensions = 1;
      data1D = (double[]) object;
    }
    if (object.getClass().getName().equals("[[D")) {
      //System.out.println("2D array");
      data2D = (double[][]) object;
      NumDimensions = 2;
    }

    int NumSamples = -1;
    int NumRows = -1;

    if (NumDimensions == 1) {
      NumRows = 1;
      data2D = new double[1][];
      data2D[0] = data1D;
    }
    if (NumDimensions == 2) {
      NumRows = data2D.length;
    }

    //System.out.println("NumRows = " + NumRows);

    if (NumRows > Colors.length) {
      Random generator = new Random(1);
      Colors = new Color[NumRows];

      for (int i = 0; i < NumRows; i++) {
        Colors[i] = new Color(generator.nextFloat(), generator.nextFloat(), generator.nextFloat());
      }
      
    }

    NumSamples = data2D[0].length;

    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (int r = 0; r < NumRows; r++) {
      for (int i = 0; i < NumSamples; i++) {
        if (data2D[r][i] > max)
          max = data2D[r][i];
        if (data2D[r][i] < min)
          min = data2D[r][i];
      }
    }
    double range = max - min;


    //System.out.println("min = " + min);
    //System.out.println("max = " + max);
    //System.out.println("range = " + range);

    double[][] scaledData = new double[NumRows][NumSamples];
    
    for (int r = 0; r < NumRows; r++) {
      for (int i = 0; i < NumSamples; i++) {
        if (range == 0)
          scaledData[r][i] = 0.5;
        else
          scaledData[r][i] = (data2D[r][i] - min) / (range);
      }
    }
    

    Rectangle bounds = mainFrame.getBounds();
    int TopMarginSize = 31;
    int BottomMarginSize = 6;
    int LeftMarginSize = 5;
    int RightMarginSize = 5;
    int Xsize = (int) bounds.getWidth() - LeftMarginSize - RightMarginSize;

    int Ysize = -1;
    if (OverlayGraphs)
      Ysize = (int) (bounds.getHeight() - TopMarginSize - BottomMarginSize);
    else
      Ysize = (int) (bounds.getHeight() - TopMarginSize - BottomMarginSize) / NumRows;

    //System.out.println("Ysize = " + Ysize);

    Graphics g = bufferStrategy.getDrawGraphics();
    if (!bufferStrategy.contentsLost()) {

      g.setColor(new Color(1.0f, 1.0f, 1.0f));
      if (OverlayGraphs)
        g.clearRect(LeftMarginSize, TopMarginSize, LeftMarginSize + Xsize, BottomMarginSize + Ysize);
      else
        g.clearRect(LeftMarginSize, TopMarginSize, LeftMarginSize + Xsize, BottomMarginSize + Ysize * NumRows);
      g.setColor(new Color(0.0f, 0.0f, 0.0f));

      double Xfactor = (double) Xsize / (double) (NumSamples - 1);
      for (int r = 0; r < NumRows; r++) {

        g.setColor(new Color(0.0f, 0.0f, 0.0f));

        if (OverlayGraphs) {
          //g.drawLine(LeftMarginSize - 1, 
          //    TopMarginSize + Ysize + 1, 
          //    LeftMarginSize + Xsize + 1, 
          //    TopMarginSize + Ysize + 1);
        } else {
          g.drawLine(LeftMarginSize - 1, TopMarginSize + (NumRows - r - 1) * Ysize + 1, LeftMarginSize + Xsize + 1, TopMarginSize
              + (NumRows - r - 1) * Ysize + 1);
        }
        if (OverlayGraphs) {
          g.setColor(Colors[r]);
        }

        for (int i = 0; i < NumSamples - 1; i++) {
          double X1 = (i) * Xfactor;
          double X2 = (i + 1) * Xfactor;
          if (OverlayGraphs) {
            double Y1 = scaledData[r][i] * Ysize;
            double Y2 = scaledData[r][i + 1] * Ysize;
            g.drawLine((int) X1 + LeftMarginSize, 
                (int) (Ysize - Y1) + TopMarginSize, 
                (int) X2 + LeftMarginSize, 
                (int) (Ysize - Y2)
                + TopMarginSize);

            //System.out.println("X1,Y1,X2,Y2 = " + X1 + "," + Y1 + "," + X2 + "," + Y2);

          } else {
            double Y1 = scaledData[r][i] * Ysize + ((NumRows - r - 1) * Ysize);
            double Y2 = scaledData[r][i + 1] * Ysize + ((NumRows - r - 1) * Ysize);
            g.drawLine((int) X1 + LeftMarginSize, (int) (Ysize * NumRows - Y1) + TopMarginSize, (int) X2 + LeftMarginSize,
                (int) (Ysize * NumRows - Y2) + TopMarginSize);
          }
        }
      }

      //g.drawImage(image, 0, 0, null);
      //g.drawImage(image, 0, 0, bounds.width, bounds.height, null);
      //g.fillRect(0, 0, bounds.width, bounds.height);
      bufferStrategy.show();
      g.dispose();
    }

    Thread.sleep(WaitTimeInMilliSeconds);

    this.pushOutput(data1D, 0);

  }
}

/*

 10000x
 NumExchanges = 2805
 3553.2942361854025	5488.400481515225	3094.3339186262647	5247.846497163995	0(1172) 1(1664) 2(1545) 3(1489) 



 */