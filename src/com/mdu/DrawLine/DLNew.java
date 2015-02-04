package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DLNew extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;

  public DLNew() {
    super();
  }

  DLNew(DLNew src) {
    this();
  }

  public DLNew(float x, float y) {
    super(x, y);
  }

  DLNew copy() {
    return new DLNew(this);
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

}
