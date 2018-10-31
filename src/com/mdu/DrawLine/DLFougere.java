package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.random;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DLFougere extends DLPointImage {
  boolean cleanIsolatedPoints = false;
  Color color = Color.black;
  float fangle = (float) Math.PI / 2;
  float scale = 15;
  int steps = 1000;

  DLFougere() {
    super();
  }

  DLFougere(DLFougere src) {
    super();
  }

  public DLFougere(float x, float y) {
    super(x, y);
  }

  BufferedImage cleanIsolatedPoints(BufferedImage image) {
    for (int i = 0; i < iwidth; i++)
      for (int j = 0; j < iheight; j++) {
        int p = image.getRGB(i, j);
        if ((p & 0xff000000) != 0) {
          if (i > 0) {
            p = image.getRGB(i - 1, j);
            if ((p & 0xff000000) != 0)
              continue;
            if (j > 0) {
              p = image.getRGB(i - 1, j - 1);
              if ((p & 0xff000000) != 0)
                continue;
              p = image.getRGB(i, j - 1);
              if ((p & 0xff000000) != 0)
                continue;
            }
            if (j < iheight - 1) {
              p = image.getRGB(i - 1, j + 1);
              if ((p & 0xff000000) != 0)
                continue;
              p = image.getRGB(i, j + 1);
              if ((p & 0xff000000) != 0)
                continue;
            }
          }
          if (i < iwidth - 1) {
            p = image.getRGB(i + 1, j);
            if ((p & 0xff000000) != 0)
              continue;
            if (j > 0) {
              p = image.getRGB(i + 1, j - 1);
              if ((p & 0xff000000) != 0)
                continue;
            }
            if (j < iheight - 1) {
              p = image.getRGB(i + 1, j + 1);
              if ((p & 0xff000000) != 0)
                continue;
            }
          }

          image.setRGB(i, j, 0x00000000);
        }
      }
    return image;
  }

  @Override
  DLFougere copy() {
    return new DLFougere(this);
  }

  public void f(Graphics2D g, DLThread t) {
    setPointFill(color);
    
    double x = iwidth / 2;
    double y = iheight / 2;

    for (int n = 0; n < steps; n++) {
      double x1 = 0;
      double y1 = 0;

      final int c = 2 * (n % 2) - 1;
      final double r = sqrt(x * x + y * y);
      final double w = atan2(y, x);
      x = r * cos(w - c * PI / 12.0);
      y = r * sin(w - c * PI / 12.0);

      final double ran = random();

      if (ran <= 0.02) {
        x1 = 0;
        y1 = 0.16 * y;
      } else if (ran < 0.09) {
        x1 = 0.2 * x - 0.26 * y;
        y1 = 0.23 * x + 0.22 * y + 1.6;
      } else if (ran < 0.16) {
        x1 = -0.15 * x + 0.28 * y;
        y1 = 0.26 * x + 0.24 * y + 0.44;
      } else {
        x1 = 0.85 * x + 0.04 * y;
        y1 = -0.04 * x + 0.85 * y + 1.6;
      }

      x = x1;
      y = y1;
      // if (n < 10)
      // continue;
      final double dpx = x * scale + iwidth / 2;
      final double dpy = iheight - (int) (y * scale);

      final int px = (int) dpx;
      final int py = (int) dpy;

      drawPoint(g, px, py);

      final int q = steps / 10;
      if (n % q == 0)
        if (DLFougere.this.image != null)
          if (parent != null) {
            parent.paint(getBounds());
          }
    }
    if (DLFougere.this.image != null)
      if (parent != null) {
        parent.paint(getBounds());
      }
  }

  @Override
  BufferedImage image() {
    BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = (Graphics2D) img.getGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);
    else
      f(g, null);
    if (cleanIsolatedPoints)
      img = cleanIsolatedPoints(img);
    return img;
  }

  @Override
  public void randomize() {
    super.randomize();
    color = DLUtil.RandomColor(0, 1, 0.6f, 1, 0.4f, 0.6f);
    iwidth = RangeRandom(100, 200);
    iheight = RangeRandom(100, 200);
    scale = Math.min(iwidth, iheight) / 10;
    steps = DLUtil.RangeRandom(1000, 10000);
    setShadow(true);
  }

  public boolean getCleanIsolatedPoints() {
    return cleanIsolatedPoints;
  }

  public void setCleanIsolatedPoints(boolean cleanIsolatedPoints) {
    this.cleanIsolatedPoints = cleanIsolatedPoints;
    stopAll();
    clear();
    runThreaded();
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
    stopAll();
    clear();
    runThreaded();
  }

  public float getFangle() {
    return fangle;
  }

  public void setFangle(float fangle) {
    this.fangle = fangle;
    stopAll();
    clear();
    runThreaded();
  }

  public float[] rangeFangle() {
    return new float[] { 0, (float) Math.PI / 2 };
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
    stopAll();
    clear();
    runThreaded();
  }

  float[] rangeScale() {
    return new float[] { 1, 30 };
  }

  public int getSteps() {
    return steps;
  }

  public void setSteps(int steps) {
    this.steps = steps;
    stopAll();
    clear();
    runThreaded();
  }

}
