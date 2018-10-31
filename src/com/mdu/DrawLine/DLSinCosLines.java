package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

public class DLSinCosLines extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;
  float t;
  int numLines = 450;
  float v = 0.4f;
  boolean increment = false;
  float factor = 0.00001f;
  float pointSize = 10;
  Color pointColor = Color.lightGray;
  Color lineColor = Color.black;
  float tIncr = 0.005f;
  boolean randomLinkColor = true;
  String reflection = RECTANGLE;
  static final String RANDOM = "random";
  static final String STARS = "stars";
  static final String RECTANGLE = "rectangle";
  static final String CIRCLE = "circle";
  static final String POLYGON = "poly";
  static final String HEART = "heart";
  static final String CHAR = "char";

  public boolean getRandomLinkColor() {
    return randomLinkColor;
  }

  public void setRandomLinkColor(boolean c) {
    randomLinkColor = c;
  }

  public void setTIncr(float t) {
    tIncr = t;
  }

  public float getTIncr() {
    return tIncr;
  }

  public float[] rangeTIncr() {
    return new float[] {
      0.0005f, 0.05f
    };
  }

  public void setLineColor(Color c) {
    lineColor = c;
  }

  public Color getLineColor() {
    return lineColor;
  }

  public void setPointColor(Color c) {
    pointColor = c;
  }

  public Color getPointColor() {
    return pointColor;
  }

  public void setIncrement(boolean b) {
    increment = b;
  }

  public boolean getIncrement() {
    return increment;
  }

  public void setFactor(float f) {
    factor = f;
  }

  public float getFactor() {
    return factor;
  }

  public float[] rangeFactor() {
    return new float[] {
      0, 1
    };
  }

  public void setPointSize(float s) {
    pointSize = s;
  }

  public float getPointSize() {
    return pointSize;
  }

  public float[] rangePointSize() {
    return new float[] {
      1, 50
    };
  }

  public void setNumLines(int nl) {
    numLines = nl;
  }

  public int getNumLines() {
    return numLines;
  }

  public int[] rangeNumLines() {
    return new int[] {
      1, 500
    };
  }

  public void setV(float v) {
    this.v = v;
  }

  public float getV() {
    return v;
  }

  public float[] rangeV() {
    return new float[] {
      0f, 1f
    };
  }

  public String getReflection() {
    return reflection;
  }

  public void setReflection(String mode) {
    reflection = mode;
  }

  public String[] enumReflection() {
    return new String[] {
      RANDOM, STARS, RECTANGLE, CIRCLE, POLYGON, HEART, CHAR
    };
  }

  public DLSinCosLines() {
    super();
  }

  DLSinCosLines(DLSinCosLines src) {
    this();
  }

  public DLSinCosLines(float x, float y) {
    super(x, y);
  }

  DLSinCosLines copy() {
    return new DLSinCosLines(this);
  }

  public void f(Graphics2D g, DLThread t) {
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      clearImage();
      step(g);

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
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  Color randomColor() {
    return DLUtil.RandomColor(0, 1, 0, 0.5f, 0, 0.5f);
  }

  void point(Graphics2D g, float x, float y) {
    DLUtil.SetHints(g);
    Shape s = DLUtil.Ellipse(x, y, pointSize, pointSize);
    g.setColor(pointColor);
    g.fill(s);

    Color c = pointColor.darker().darker();
    g.setColor(c);
    g.draw(s);

    float o = pointSize / 8;
    float sz = pointSize / 4;
    switch (reflection) {
    case RECTANGLE:
      s = DLUtil.Rectangle(x - o, y - o, sz, sz);
      break;
    case CIRCLE:
      s = DLUtil.Ellipse(x - o, y - o, sz, sz);
      break;
    case POLYGON:
      s = DLUtil.Polygon(x - o, y - o, 5, sz);
      break;
    case STARS:
      s = DLUtil.Star(x - o, y - o, sz / 4, sz, 7);
      break;
    }

    g.setColor(Color.white);
    g.fill(s);

    float start = -45 - 40;
    float end = -45 + 40;
    float extent = end - start;
    s = DLUtil.Arc(x - 0.5f, y - 0.5f, pointSize, pointSize, start, extent);
    g.setColor(c.darker().darker());
    g.draw(s);
  }

  void line(Graphics2D g, float x1, float y1, float x2, float y2) {
    Shape s = DLUtil.Line(x1, y1, x2, y2);

    g.draw(s);
  }

  void step(Graphics2D g) {

    float iw2 = iwidth / 2f;
    float ih2 = iheight / 2f;

    if (randomLinkColor)
      lineColor = randomColor();
    g.setColor(lineColor);
    for (int i = 1; i < numLines; i++) {
      line(g, iw2 + x(t + i), ih2 + y(t + i), iw2 + x2(t + i), ih2 + y2(t + i));
    }

    for (int i = 1; i < numLines; i++) {
      point(g, iw2 + x(t + i), ih2 + y(t + i));
      point(g, iw2 + x2(t + i), ih2 + y2(t + i));
    }

    t += tIncr;

    if (increment)
      v += factor;
  }

  float sin(float x) {
    return DLUtil.Sin(x);
  }

  float cos(float x) {
    return DLUtil.Cos(x);
  }

  float a = 10;
  float b = 100;
  float c = 100;

  public void setA(float x) {
    a = x;
  }

  public float getA() {
    return a;
  }

  public float[] rangeA() {
    return new float[] {
      1, 20
    };
  }

  public void setB(float x) {
    b = x;
  }

  public float getB() {
    return b;
  }

  public float[] rangeB() {
    return new float[] {
      1, 200
    };
  }

  float x(float t) {
    return sin(t / a) * b + cos(t / v) * b;
  }

  float y(float t) {
    return cos(t / a) * b + sin(t / v) * b;
  }

  float x2(float t) {
    return sin(t / a) * a + cos(t / v) * b;
  }

  float y2(float t) {
    return cos(t / a) * a + sin(t / v) * b;
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 500);
    iheight = iwidth;
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] {
      0, 100
    };
  }

  public static void main(String[] a) {
    int w = 600;
    int h = 600;
    Object[][] params = {
      {
        "iwidth", w
      }, {
        "iheight", h
      }, {
        "x", w / 2
      }, {
        "y", h / 2
      }, {
        "threadSleep", 5
      }, {
        "backgroundColor", new Color(53, 53, 20).brighter().brighter().brighter()
      }
    };
    DLMain.Main(DLSinCosLines.class, params);
  }
}
