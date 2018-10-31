package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.jhlabs.image.EdgeFilter;

public class DLMetaball extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;
  int numBlobs = 30;
  float deltaT = 5f;
  float colorFactor = 1;
  ArrayList<Blob> blobs;
  int pixels[];
  boolean edges;

  final static String SQUARE = "Square";
  final static String CUBIC = "Cubic";
  final static String EUCLIDEAN = "Euclidean";
  final static String MANHATTAN = "Manhattan";
  final static String ALIEN = "Alien";
  final static String MINKOWSKI = "Minkowski";
  final static String TCHEBYCHEV = "Tchebychev";
  String distance = ALIEN;

  final static String GREY = "grey";
  final static String CM1 = "cm 1";
  final static String CM2 = "cm 2";
  final static String CM3 = "cm 3";

  String coloring = GREY;

  void setup() {
    blobs = new ArrayList<Blob>(numBlobs);
    synchronized (blobs) {
      int i = numBlobs;
      while (i-- > 0) {
        Blob b = new Blob(
            DLUtil.RangeRandom(0f, iwidth),
            DLUtil.RangeRandom(0f, iheight),
            DLUtil.RangeRandom(-1f, 1f),
            DLUtil.RangeRandom(-1f, 1f),
            DLUtil.RangeRandom(50f, 100f));
        blobs.add(b);
      }
      pixels = new int[iwidth * iheight];
    }
  }

  public DLMetaball() {
    super();
  }

  DLMetaball(DLMetaball src) {
    this();
  }

  public DLMetaball(float x, float y) {
    super(x, y);
  }

  DLMetaball copy() {
    return new DLMetaball(this);
  }

  public void f(Graphics2D g, DLThread t) {

    setup();

    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      update();

      draw();

      edges();

      if (parent != null)
        parent.paint(this);

      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          System.err.println(e);
        }
      }
    }
  }

  BufferedImage image() {
    BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 400);
    iheight = iwidth;
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] { 0, 100 };
  }

  void edges() {
    if (!edges)
      return;
    EdgeFilter ef = new EdgeFilter();
    image = ef.filter(image, image);
  }

  void update() {
    synchronized (blobs) {
      for (Blob b : blobs)
        b.update();
    }
  }

  int cmColor(DLColorModel cm, float f) {
    float g = DLUtil.Normalize(cm._limits[cm._limits.length - 1], cm._limits[0], 0, 1000, f);
    int c = 0xff000000 | cm.getColor(g);
    return c;
  }

  int greyColor(float c) {
    int ic = (int) (c + 0.5f);
    int g = ic < 255 ? ic : 255;
    int r = (0xff000000) | (g << 16) | (g << 8) | g;
    return r;
  }

  float color(int x, int y) {
    float col = 0;
    synchronized (blobs) {
      for (Blob b : blobs) {
        float dist = 0;
        switch (distance) {
        case SQUARE:
          dist = DLUtil.SquareDist(b.x, b.y, x, y);
          col += 200 * b.size / dist;
          break;
        case MANHATTAN:
          dist = DLUtil.ManhattanDist(b.x, b.y, x, y);
          col += 10 * b.size / dist;
          break;
        case EUCLIDEAN:
          dist = DLUtil.EuclideanDist(b.x, b.y, x, y);
          col += 10 * b.size / dist;
          break;
        case ALIEN:
          dist = DLUtil.AlienDist2(b.x, b.y, x, y);
          col += 500 * b.size / dist;
          break;
        case MINKOWSKI:
          dist = DLUtil.Minkowski(b.x, b.y, x, y, 3);
          col += b.size / dist;
          break;
        case CUBIC:
          dist = DLUtil.Cubic(b.x, b.y, x, y);
          col += 500 * b.size / dist;
          break;
        case TCHEBYCHEV:
          dist = DLUtil.Tchebychev(b.x, b.y, x, y);
          col += b.size / dist;
          break;
        }
      }
    }
    return col;
  }

  int getColor(int x, int y) {
    int col = 0;
    float fcol = colorFactor * color(x, y);
    switch (coloring) {
    case GREY:
      col = greyColor(fcol);
      break;
    case CM1:
      col = cmColor(DLUtil.ColorModel1, fcol);
      break;
    case CM2:
      col = cmColor(DLUtil.ColorModel2, fcol);
      break;
    case CM3:
      col = cmColor(DLUtil.ColorModel3, fcol);
      break;
    }
    return col;
  }

  void draw() {
    for (int y = 0, p = 0; y < iheight; y++) {
      for (int x = 0; x < iwidth; x++, p++) {
        pixels[p] = getColor(x, y);
      }
    }
    image.setRGB(0, 0, iwidth, iheight, pixels, 0, iwidth);
  }

  class Blob {
    float size;
    float x, y;
    float vx, vy;

    Blob(float x, float y, float vx, float vy, float size) {
      this.x = x;
      this.y = y;
      this.vx = vx;
      this.vy = vy;
      this.size = size;
    }

    void update() {
      x += vx * deltaT;
      y += vy * deltaT;

      float sz2 = 0; //size ;
      float minPosX = sz2;
      float maxPosX = iwidth - sz2;
      float minPosY = sz2;
      float maxPosY = iheight - sz2;

      if (x < minPosX) {
        x = 2f * minPosX - x;
        vx = -vx;
      } else if (x > maxPosX) {
        x = 2f * maxPosX - x;
        vx = -vx;
      }

      if (y < minPosY) {
        y = 2f * minPosY - y;
        vy = -vy;
      } else if (y > maxPosY) {
        y = 2f * maxPosY - y;
        vy = -vy;
      }
    }
  }

  public float getDeltaT() {
    return deltaT;
  }

  public void setDeltaT(float d) {
    deltaT = d;
  }

  public float[] rangeDeltaT() {
    return new float[] { 0.5f, 100f };
  }

  public String getDistance() {
    return distance;
  }

  public void setDistance(String d) {
    distance = d;
  }

  public String[] enumDistance() {
    return new String[] { SQUARE, EUCLIDEAN, MANHATTAN, ALIEN, MINKOWSKI, CUBIC, TCHEBYCHEV };
  }

  public String getColoring() {
    return coloring;
  }

  public void setColoring(String d) {
    coloring = d;
  }

  public String[] enumColoring() {
    return new String[] { GREY, CM1, CM2, CM3 };
  }

  public void setColorFactor(float f) {
    colorFactor = f;
  }

  public float getColorFactor() {
    return colorFactor;
  }

  public float[] rangeColorFactor() {
    return new float[] { 0f, 50f };
  }

  public void setEdges(boolean e) {
    edges = e;
  }

  public boolean getEdges() {
    return edges;
  }
}
