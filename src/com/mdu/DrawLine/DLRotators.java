package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class DLRotators extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;
  int n = 100;
  float radius;
  Rotator[] rotating;
  float t = 0;
  float increment = DLUtil.PI / 100f;
  float phaseShift = 10;
  Color lineColor = Color.blue.brighter().brighter();
  Color ellipseColor = Color.red.darker();
  float jitter = 0;
float ellipseSize = 5;

  public DLRotators() {
    super();
  }

  DLRotators(DLRotators src) {
    this();
  }

  public DLRotators(float x, float y) {
    super(x, y);
  }

  DLRotators copy() {
    return new DLRotators(this);
  }

  public void f(Graphics2D g, DLThread t) {

    setup(g);

    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

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

  void step(Graphics2D g) {
    clearImage();
    draw(g);
  }

  public void setJitter(float j) {
    jitter = j;
  }
  public float getJitter() {
    return jitter;
  }
  public float[] rangeJitter() {
    return new float[]{0, 10f};
  }
  
  public void setRadius(float r) {
    radius = r;
    rotators(false);
  }

  public float getRadius() {
    return radius;
  }

  public float[] rangeRadius() {
    return new float[] {
        10, iwidth / 3f - 20f
    };
  }

  public void setLineColor(Color c) {
    lineColor = c;
  }
  public Color getLineColor() {
    return lineColor;
  }

  public void setEllipseSize(float es) {
    ellipseSize = es;
  }
  public float getEllipseSize() {
    return ellipseSize;
  }
  public float[] rangeEllipseSize() {
    return new float[]{1, 10f};
  }
  
  public void setEllipseColor(Color c) {
    ellipseColor = c;
  }
  public Color getEllipseColor() {
    return ellipseColor;
  }

  public void setIncrement(float i) {
    increment = i;
    rotators(false);
  }
  public float getIncrement() {
    return increment;
  }
  public float[] rangeIncrement() {
    return new float[] {
        DLUtil.PI / 200f, DLUtil.PI / 10
    };
  }

  public void setPhaseShift(float fs) {
    phaseShift = fs;
    rotators(false);
  }

  public float getPhaseShift() {
    return phaseShift;
  }

  public float[] rangePhaseShift() {
    return new float[] {
        0, 100f
    };
  }

  public void setN(int n) {
    this.n = n;
    rotators(true);
  }

  public int getN() {
    return n;
  }

  public int[] rangeN() {
    return new int[] {
        1, 300
    };
  }

  void rotators(boolean allocate) {
    if (allocate)
      rotating = new Rotator[n];
    float a = DLUtil.TWO_PI / n;
    float iw2 = iwidth / 2f;
    float ih2 = iheight / 2f;
    for (int i = 0; i < n; i++) {
      float b = a * i;
      rotating[i] = new Rotator(
          DLUtil.Cos(b) * radius + iw2,
          DLUtil.Sin(b) * radius + ih2,
          a * i * phaseShift,
          radius);
    }
  }

  float random(float f) {
    return DLUtil.FloatRandom(0f, f);
  }

  void setup(Graphics2D g) {
    radius = iwidth / 3 - 20;
    phaseShift = random(100);
    rotators(true);
  }

  void draw(Graphics2D g) {
    t += increment;
    synchronized (rotating) {
      for (Rotator current : rotating) {
        current.update(g, t);
      }
    }
  }

  void mousePressed() {
    phaseShift = random(100);
    rotators(false);
  }

  class PVector {
    float x;
    float y;

    PVector(float x, float y) {
      this.x = x;
      this.y = y;
    }

  }

  class Rotator {
    PVector vert;
    float phase = 0;
    float size = 0;
    AffineTransform pushedTransform;

    Rotator(float x, float y, float phase, float size) {
      vert = new PVector(x, y);
      this.phase = phase;
      this.size = size;
    }

    void pushMatrix(Graphics2D g) {
      pushedTransform = g.getTransform();
    }

    void popMatrix(Graphics2D g) {
      g.setTransform(pushedTransform);
    }

    void translate(Graphics2D g, float x, float y) {
      g.translate(x, y);
    }

    void rotate(Graphics2D g, float a) {
      g.rotate(a);
    }

    void line(Graphics2D g, float x1, float y1, float x2, float y2) {
      Shape s = DLUtil.Line(x1, y1, x2, y2);
      g.setPaint(lineColor);
      g.draw(s);
    }

    void ellipse(Graphics2D g, float x, float y, float w, float h) {
      Shape s = DLUtil.Ellipse(x, y, w, h);
      g.setPaint(ellipseColor);
      g.draw(s);
    }

    void update(Graphics2D g, float t) {
      pushMatrix(g);
      translate(g, vert.x, vert.y);
      rotate(g, t + phase);
      float dx = (jitter != 0 ? DLUtil.FloatRandom(-jitter, jitter) : 0f);
      float dy = (jitter != 0 ? DLUtil.FloatRandom(-jitter, jitter) : 0f);
      float x = 0 + dx;
      float y = -size / 2 + dy;
      float w = ellipseSize + dx;
      float h = ellipseSize + dy;
      line(g, x, y, x, -y); 
      ellipse(g, x, y, w, h); 
      ellipse(g, x, -y, w, h);
      popMatrix(g);
    }
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

  boolean mouse(MouseEvent e) {
//    Point p = e.getPoint();
//    float ix = p.x - (this.x - iwidth / 2);
//    float iy = p.y - (this.y - iheight / 2);
    if (e instanceof MouseWheelEvent) {
      return false;
    }
    switch (e.getID()) {
    case MouseEvent.MOUSE_CLICKED: {
      phaseShift = random(100);
      for (int i = 0; i < n; i++) {
        rotating[i] = new Rotator(
            DLUtil.Cos(DLUtil.TWO_PI / n * i) * radius + iwidth / 2,
            DLUtil.Sin(DLUtil.TWO_PI / n * i) * radius + iheight / 2,
            DLUtil.TWO_PI / n * i * phaseShift, radius);
      }
    }
    }
    return true;
  }

  public static void main(String[] a) {
    int w = 700;
    int h = 700;
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

    DLMain.Main(DLRotators.class, params);
  }
}
