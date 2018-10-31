package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DLBadKaleidoscope extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;
  int NB_MAX = 5; //16;
  int NB = DLUtil.RangeRandom(2, NB_MAX);
  int RADIUS = 220;
  DLPoint prevPoint1 = new DLPoint(0, 0);
  DLPoint prevPoint2 = new DLPoint(0, 0);
  float R;
  float G;
  float B;
  float Rspeed;
  float Gspeed;
  float Bspeed;

  boolean reset = false;

  public DLBadKaleidoscope() {
    super();
  }

  DLBadKaleidoscope(DLBadKaleidoscope src) {
    this();
  }

  public DLBadKaleidoscope(float x, float y) {
    super(x, y);
  }

  DLBadKaleidoscope copy() {
    return new DLBadKaleidoscope(this);
  }

  void setup() {
    setBackground(Color.DARK_GRAY);
    generateColors();
  }

  float random(float r) {
    return DLUtil.RangeRandom(0, r);
  }

  float random(float r1, float r2) {
    return DLUtil.RangeRandom(r1, r2);
  }

  float random(double r1, double r2) {
    return (float) DLUtil.RangeRandom(r1, r2);
  }

  void generateColors() {
    R = random(0, 255);
    G = random(0, 255);
    B = random(0, 255);
    Rspeed = (random(0., 1.) > 0.5 ? 1f : -1f) * random(.8f, 1.5f);
    Gspeed = (random(0., 1.) > 0.5 ? 1f : -1f) * random(.8f, 1.5f);
    Bspeed = (random(0., 1.) > 0.5 ? 1f : -1f) * random(.8f, 1.5f);
  }

  public void f(Graphics2D g, DLThread t) {
    clearImage();
    setup();
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
    draw(g);
  }

  public void randomize() {
    iwidth = 500; // DLUtil.RangeRandom(500, 500);
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

  public int getRADIUS() {
    return RADIUS;
  }
  public void setRADIUS(int r) {
    RADIUS = r;
  }
  public int[] rangeRADIUS() {
    return new int[] {10, 500};
  }
  int abs(float f) {
    f = f < 0 ? -f : f;
    return (int) f;
  }

  ArrayList<DLPath> curves = new ArrayList<DLPath>();

  void draw(Graphics2D g) {
    float thetaD = DLUtil.RangeRandom(0, .01f);

    float tx = iwidth / 2;
    float ty = iheight / 2;
    translate(g, tx, ty);

    Rspeed = ((R += Rspeed) > 255 || (R < 0)) ? -Rspeed : Rspeed;
    Gspeed = ((G += Gspeed) > 255 || (G < 0)) ? -Gspeed : Gspeed;
    Bspeed = ((B += Bspeed) > 255 || (B < 0)) ? -Bspeed : Bspeed;

    Color c = new Color(abs(R) % 255, abs(G) % 255, abs(B) % 255);
    g.setColor(c);

    float angle = random(DLUtil.TWO_PI);

    float radius = random(8);

    float tmpX = prevPoint1.x + radius * DLUtil.Cos(angle);
    float tmpY = prevPoint1.y + radius * DLUtil.Sin(angle);

    // adding the mouse rotation
    float x = tmpX * DLUtil.Cos(thetaD) - tmpY * DLUtil.Sin(thetaD);
    float y = tmpY * DLUtil.Cos(thetaD) + tmpX * DLUtil.Sin(thetaD);

    if (x * x + y * y > RADIUS * RADIUS) {
      x = RADIUS * DLUtil.Cos(DLUtil.Atan2(prevPoint1.y, prevPoint1.x));
      y = RADIUS * DLUtil.Sin(DLUtil.Atan2(prevPoint1.y, prevPoint1.x));
    }

    for (int i = 0; i < NB; i++) {
      rotate(g, DLUtil.TWO_PI / (float) NB);
      line(g, prevPoint1.x, prevPoint1.y, x, y);
      line(g, prevPoint2.x, prevPoint2.y, x, -y);
    }
    prevPoint1 = new DLPoint(x, y);
    prevPoint2 = new DLPoint(x, -y);

    translate(g, -tx, -ty);
  }

  void rotate(Graphics2D g, float r) {
    g.rotate(r);
  }

  void translate(Graphics2D g, float tx, float ty) {
    g.translate(tx, ty);
  }

  void line(Graphics2D g, float x1, float y1, float x2, float y2) {
    Shape s = DLUtil.Line(x1, y1, x2, y2);
    g.draw(s);
  }

  public void reset() {
//    super.reset();
    clearImage();
  }

  public boolean getReset() {
    return reset;
  }

  public void setReset(boolean reset) {
    this.reset = reset;
    generateColors();
    reset();
  }

  public static void main(String[] a) {
    int w = 800;
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
        }
    };

    DLMain.Main(DLBadKaleidoscope.class, params);
  }

}
