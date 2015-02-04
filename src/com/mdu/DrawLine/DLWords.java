package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DLWords extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;

  public DLWords() {
    super();
  }

  DLWords(DLWords src) {
    this();
  }

  public DLWords(float x, float y) {
    super(x, y);
  }

  DLWords copy() {
    return new DLWords(this);
  }

  MyWordle myWordle;

  public void f(Graphics2D g, DLThread t) {
    myWordle = new MyWordle(iwidth, iheight);

    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      synchronized (this) {
        step(g);
      }

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
    myWordle.doLayout();
    clearImage();
    myWordle.draw(image);
//    try {
//      myWordle.saveAsPNG(new File("/tmp/bar" + frameCount + ".png"));
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 500);
    iheight = iwidth;
    threadSleep = 2000;
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
