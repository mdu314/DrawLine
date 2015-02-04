package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DLPlasma extends DLImage {
  Color backgroundColor;
  int threadSleep = 50;
  int[][] waves = new int[2000][3]; // sine waves
  int[] luma = new int[1024]; // brightness curve
  int[][][] pos = new int[3][2][3]; // positions RGB,XY,123
  float[][][] velocity = new float[3][2][3]; // velocity RGB,XY,123
  int[][][] cWaves = new int[1300][3][2]; // pos,RGB,XY
  int[] pixels;
  long frameCount = 1;

  public DLPlasma() {
    super();
  }

  DLPlasma(DLPlasma src) {
    this();
  }

  public DLPlasma(float x, float y) {
    super(x, y);
  }

  DLPlasma copy() {
    return new DLPlasma(this);
  }

  void setup() {
    
    // randomize positions
    for (int ix = 0; ix < 3; ix++) {
      for (int iy = 0; iy < 2; iy++) {
        for (int iz = 0; iz < 3; iz++) {
          pos[iz][iy][ix] = DLUtil.RangeRandom(0, 512);
          velocity[iz][iy][ix] = DLUtil.RangeRandom(-3f, 3f);
        }
      }
    }

    // make sine waves
    for (int ix = 0; ix < 2000; ix++) {
      waves[ix][0] = (int) (100 + (Math.sin(ix * DLUtil.TWO_PI / iwidth) * 100));
      waves[ix][1] = (int) (50 + (Math.sin(ix * 2 * DLUtil.TWO_PI / iwidth) * 50));
      waves[ix][2] = (int) (25 + (Math.sin(ix * 3 * DLUtil.TWO_PI / iwidth) * 25));
    }

    // make luma wave
    for (int ix = 0; ix < 1024; ix++) {
      int iy = ix;
      while ((iy > 255) || (iy < 0)) {
        if (iy > 255)
          iy = 511 - iy;
        if (iy < 0)
          iy = -iy;
      }
      if (iy > 201)
        iy = ((iy * 4) - (201 * 3));
      iy = (int) ((iy * 255) / ((255 * 4) - (201 * 3)));
      luma[ix] = iy;
    }

    pixels = new int[iwidth * iheight];
  }

  void clearImage() {
    if (backgroundColor == null)
      super.clearImage();
    else {
      Graphics2D g = image.createGraphics();
      g.setColor(backgroundColor);
      g.fillRect(0, 0, iwidth, iheight);
    }
  }

  public void f(Graphics2D g, DLThread t) {

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
    // update velocity
    for (int ix = 0; ix < 3; ix++) {
      for (int iy = 0; iy < 2; iy++) {
        for (int iz = 0; iz < 3; iz++) {
          velocity[iz][iy][ix] += DLUtil.RangeRandom(-0.2, 0.2);
          if (velocity[iz][iy][ix] > (6 - ix))
            velocity[iz][iy][ix] = (6 - ix);
          if (velocity[iz][iy][ix] < (-6 + ix))
            velocity[iz][iy][ix] = (-6 + ix);
        }
      }
    }

    // update positions
    for (int ix = 0; ix < 3; ix++) {
      for (int iy = 0; iy < 2; iy++) {
        for (int iz = 0; iz < 3; iz++) {
          pos[iz][iy][ix] += (int) (velocity[iz][iy][ix]);
          if (pos[iz][iy][ix] >= iwidth)
            pos[iz][iy][ix] -= iwidth;
          if (pos[iz][iy][ix] < 0)
            pos[iz][iy][ix] += iwidth;
        }
      }
    }

    // make composite waves
    for (int ix = 0; ix < iwidth; ix++) {
      for (int iy = 0; iy < 3; iy++) {
        for (int iz = 0; iz < 2; iz++) {
          cWaves[ix][iy][iz] = waves[ix + pos[iy][iz][0]][0] + waves[ix + pos[iy][iz][1]][1]
              + waves[ix + pos[iy][iz][2]][2];
        }
      }
    }

    for (int iy = 0; iy < iheight; iy++) {
      int xOff = iy * iwidth;
      for (int ix = 0; ix < iwidth; ix++) {
        int p = (luma[cWaves[ix][0][0] + cWaves[iy][0][1]] << 16) + (luma[cWaves[ix][1][0] + cWaves[iy][1][1]] << 8)
            + luma[cWaves[ix][2][0] + cWaves[iy][2][1]];
        pixels[xOff + ix] = 0xff000000 | p;
      }
    }

    image.setRGB(0, 0, iwidth, iheight, pixels, 0, iwidth);

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
