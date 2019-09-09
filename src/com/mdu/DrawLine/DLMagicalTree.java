package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

public class DLMagicalTree extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;

  public DLMagicalTree() {
    super();
  }

  DLMagicalTree(DLMagicalTree src) {
    this();
  }

  public DLMagicalTree(float x, float y) {
    super(x, y);
  }

  DLMagicalTree copy() {
    return new DLMagicalTree(this);
  }

  public void f(Graphics2D g, DLThread t) {
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

//      synchronized (this) {
        step(g);
//      }

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
    background(g);
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
    return new int[] { 0, 100 };
  }
  
  void background(Graphics2D g) {   
    for (float diam = 1.5f*iwidth; diam > 0.5f*iwidth; diam -= 20) {
      float v = DLUtil.map(diam, 0.5f*iwidth, 1.5f*iwidth, 255f, 210f);
      Color c = new Color(DLUtil.IntColor(v));
      g.setColor(c);
      Shape s = DLUtil.Ellipse(iwidth/2f, iheight/2f, diam, diam);
      g.fill(s);
    }
  }

  public static void main(String[] a) {
    int w = 600;
    int h = 400;
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
            "threadSleep", 500
        }, {
            "backgroundColor", null
        }
    };
    DLMain.Main(DLMagicalTree.class, params);
  }

}
