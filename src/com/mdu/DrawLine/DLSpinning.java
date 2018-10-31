package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class DLSpinning extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;

  public DLSpinning() {
    super();
  }

  DLSpinning(DLSpinning src) {
    this();
  }

  public DLSpinning(float x, float y) {
    super(x, y);
  }

  DLSpinning copy() {
    return new DLSpinning(this);
  }

  public void f(Graphics2D g, DLThread t) {
    translate(g, iwidth / 2, iheight / 2);

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

  void translate(Graphics2D g, float x, float y) {
    g.translate(x, y);
  }

  void ellipse(Graphics2D g, float x, float y, float w, float h) {

    Shape s = DLUtil.Ellipse(x + w / 2f, y + h / 2f, w, h);
    g.fill(s);
  }

  int n = 257;
  float starttime = System.currentTimeMillis();

  float flt(int i) {
    return (float)i;
  }
  float sin(double d) {
    return DLUtil.Sin((float)d);
  }
  float cos(double d) {
    return DLUtil.Cos((float)d);
  }
  void step(Graphics2D g) {
    float time = System.currentTimeMillis() * 0.0001f;
    for( int i=1; i<=n; ++i ) {
      float s = flt(i)/n;
      Color c = Color.red;//new Color( (int)(255 * (1.0 - s)), 255 * s, 255 );
      g.setPaint(c);
      float t_i = time * s;
      float r = (1.0f + sin(t_i*1.618034)) * iwidth/5 + iwidth/20;
      //r = s * width/2.5 + width/20;
      float a = t_i - 0.5f*time;
      ellipse(g, r*cos(a), r*sin(a), 5, 5 );
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

    Class<? extends DLComponent> cls = DLSpinning.class;
    DLMain.Main(cls, params);
  }
}
