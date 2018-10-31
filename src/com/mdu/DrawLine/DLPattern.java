package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DLPattern extends DLImage {
  int pattern = 2;
  boolean border, invert;
  int[] pat;

  int threadSleep = 50;
  int frameCount = 1;

  int scl = 4;
  int dirs = 12;
  int lim = 128;

  BufferedImage unzoomedImage;
  BufferedImage zoomedImage;

  public int getLim() {
    return lim;
  }

  public void setLim(int lim) {
    this.lim = lim;
  }

  public int[] rangeLim() {
    return new int[] {
        1, 256
    };
  }

  static DLColorModel cm = new DLColorModel("model1", new int[] {
      0xff0000, 0x00ff00, 0x0000ff
  }, new float[] {
      1,
      0.5f, 0f
  });

  public void reset() {
    int w = DLUtil.Int(iwidth / res);
    int h = DLUtil.Int(iheight / res);

    unzoomedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    zoomedImage = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);

    int s = w * h;
    pat = new int[s];
    synchronized (pat) {
      // random init
      for (int i = 0; i < s; i++) {
        int k = DLUtil.RangeRandom(0, 0xff);
        pat[i] = DLUtil.GetIntGrey(k);
        // pat[i] = cm.getColor(k / 255f);
      }
    }
  }

  private void tzoom() {
//    if (res > 1) {
//      zoom(unzoomedImage, zoomedImage);
//      image = zoomedImage;
//    } else {
      image = unzoomedImage;
//    }
  }

  public DLPattern() {
    super();
  }

  DLPattern(DLPattern src) {
    this();
  }

  public DLPattern(float x, float y) {
    super(x, y);
  }

  DLPattern copy() {
    return new DLPattern(this);
  }

  public void f(Graphics2D g, DLThread t) {
    reset();

    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      synchronized (pat) {
        try {
          step(g);
        } catch (Exception e) {
          DLError.report(e);
        }
      }

      if (parent != null)
        parent.paint(this);

      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          DLError.report(e);
        }
      }
    }
  }

  BufferedImage image() {
    reset();

    Graphics2D g = unzoomedImage.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);
    image = zoomedImage;
    return zoomedImage;
  }

  void step(Graphics2D g) {
    pattern();
    int w = iwidth / res;
    int h = iheight / res;
    unzoomedImage.setRGB(0, 0, w, h, pat, 0, w);
    tzoom();
    applyFilter();
  }

  public void randomize() {
    iwidth = 400;
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

  void pattern() {
    float tp = DLUtil.TWO_PI;

    // random angular offset
    float R = DLUtil.RangeRandom(0, tp);
    int w = iwidth / res;
    int h = iheight / res;
    int s = w * h;
    // copy chemicals
    float[] pnew = new float[s];
    for (int i = 0; i < s; i++)
      pnew[i] = pat[i];

    // create matrices
    float[][] pmedian = new float[s][scl];
    float[][] prange = new float[s][scl];
    float[][] pvar = new float[s][scl];

    // iterate over increasing distances
    for (int i = 0; i < scl; i++) {
      float d = (2 << i);

      // update median matrix
      for (int j = 0; j < dirs; j++) {
        float dir = j * tp / dirs + R;
        int dx = DLUtil.Int(d * DLUtil.Cos(dir));
        int dy = DLUtil.Int(d * DLUtil.Sin(dir));
        for (int l = 0; l < s; l++) {
          // coordinates of the connected cell
          int x1 = l % w + dx;
          int y1 = l / w + dy;
          // skip if the cell is beyond the border or wrap around
          if (x1 < 0)
            if (border)
              continue;
            else
              x1 = w - 1 - (-x1 - 1) % w;
          else if (x1 >= w)
            if (border)
              continue;
            else
              x1 = x1 % w;
          if (y1 < 0)
            if (border)
              continue;
            else
              y1 = h - 1 - (-y1 - 1) % h;
          else if (y1 >= h)
            if (border)
              continue;
            else
              y1 = y1 % h;
          // update median
          int index = (x1 + y1 * w);
          if (index >= pat.length)
            index = pat.length - 1;
          if (index < 0)
            index = 0;
          pmedian[l][i] += pat[index] / dirs;
        }
      }

      // update range and variance matrix
      for (int j = 0; j < dirs; j++) {
        float dir = j * tp / dirs + R;
        int dx = DLUtil.Int(d * DLUtil.Cos(dir));
        int dy = DLUtil.Int(d * DLUtil.Sin(dir));
        for (int l = 0; l < s; l++) {
          // coordinates of the connected cell
          int x1 = l % w + dx;
          int y1 = l / w + dy;
          // skip if the cell is beyond the border or wrap around
          if (x1 < 0)
            if (border)
              continue;
            else
              x1 = w - 1 - (-x1 - 1) % w;
          else if (x1 >= w)
            if (border)
              continue;
            else
              x1 = x1 % w;
          if (y1 < 0)
            if (border)
              continue;
            else
              y1 = h - 1 - (-y1 - 1) % h;
          else if (y1 >= h)
            if (border)
              continue;
            else
              y1 = y1 % h;
          // update variance
          synchronized (pat) {
            int index = (x1 + y1 * w);
            if (index >= pat.length)
              index = pat.length - 1;
            if (index < 0)
              index = 0;

            pvar[l][i] += DLUtil.Abs(pat[index] - pmedian[l][i]) / dirs;
            // update range

            prange[l][i] += pat[index] > (lim + i * 10) ? +1 : -1;
          }
        }
      }
    }
    float mf = Float.MAX_VALUE;

    for (int l = 0; l < s; l++) {

      // find min and max variation
      int imin = 0, imax = scl;
      float vmin = mf;
      float vmax = -mf;
      for (int i = 0; i < scl; i += 1) {
        if (pvar[l][i] <= vmin) {
          vmin = pvar[l][i];
          imin = i;
        }
        if (pvar[l][i] >= vmax) {
          vmax = pvar[l][i];
          imax = i;
        }
      }

      // turing pattern variants
      switch (pattern) {
      case 0:
        for (int i = 0; i <= imax; i++)
          pnew[l] += pvar[l][i] / 2;
        break;
      case 1:
        for (int i = imin; i <= imax; i++)
          pnew[l] += (prange[l][i] + pvar[l][i]) / 2; // prange[l][i];
        break;
      case 2:
        for (int i = imin; i <= imax; i++)
          pnew[l] += prange[l][i] + pvar[l][i] / 2;
        break;
      }

    }

    // rescale values
    float vmin = mf;
    float vmax = -mf;
    for (int i = 0; i < s; i++) {
      vmin = DLUtil.Min(vmin, pnew[i]);
      vmax = DLUtil.Max(vmax, pnew[i]);
    }
    float dv = vmax - vmin;
    synchronized (pat) {
      for (int i = 0; i < s; i++) {
        int k = DLUtil.Floor((pnew[i] - vmin) * 255 / dv);
        if (i < pat.length) {
          try {
            pat[i] = DLUtil.GetIntGrey(k);
          } catch (Exception e) {
            DLError.report(e);
          }
        }
      }
    }
  }

  public int getScl() {
    return scl;
  }

  public void setScl(int scl) {
    this.scl = scl;
  }

  public int[] rangeScl() {
    return new int[] {
        2, 50
    };
  }

  public int getDirs() {
    return dirs;
  }

  public void setDirs(int dirs) {
    this.dirs = dirs;
  }

  public int[] rangeDirs() {
    return new int[] {
        2, 50
    };
  }

  public int getPattern() {
    return pattern;
  }

  public void setPattern(int pattern) {
    this.pattern = pattern;
  }

  public int[] rangePattern() {
    return new int[] {
        0, 2
    };
  }

  public boolean isInvert() {
    return invert;
  }

  public void setInvert(boolean invert) {
    this.invert = invert;
  }

  public static void main(String[] a) {
    Object[][] params = {
        {
            "iwidth", 500
        }, {
            "iheight", 600
        }, {
            "x", 400 / 2
        }, {
            "y", 400 / 2
        },
        {
            "threadSleep", 5
        }
    };

    Class<? extends DLComponent> cls = DLPattern.class;
    DLMain.Main(cls, params);
  }
}
