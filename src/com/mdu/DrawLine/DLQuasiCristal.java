package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.jhlabs.image.KaleidoscopeFilter;

public class DLQuasiCristal extends DLImage {
  int threadSleep = 50;
  int frameCount = 1000;
  float dimPix = 0.5f;
  int levels = 8;
  float tFactor = 0.3f;
  int[] pixels;

  float filterStrength = 0f;
  BufferedImage filterImage;
  boolean randomize = false;

  static DLColorModel ColorModel1 = new DLColorModel("model1",
      new int[] {
          0xff001f3f, 0xff39cccc, 0xff3d9970, 0xff2ecc40, 0xffffdc00
      },
      new float[] {
          1f, 3f / 4f, 2f / 4f, 1f / 4f, 0f
      });

  public DLQuasiCristal() {
    super();
  }

  DLQuasiCristal(DLQuasiCristal src) {
    this();
  }

  public DLQuasiCristal(float x, float y) {
    super(x, y);
  }

  DLQuasiCristal copy() {
    return new DLQuasiCristal(this);
  }

  int fcr = DLUtil.RangeRandom(5, 15);
  int dpr = DLUtil.RangeRandom(10, 20);

  void random(int fc) {
    if (fc % dpr == 0) {
      float d = DLUtil.RangeRandom(-0.1f, 0.1f);
      dimPix += d;
      if (dimPix < 0.1f)
        dimPix = 0.1f;
      if (dimPix > 1f)
        dimPix = 1f;
      dpr = DLUtil.RangeRandom(10, 20);
    }
    if ((fc % fcr) == 0) {
      int l = DLUtil.RangeRandom(-2, 2);
      levels += l;
      if (levels < 1)
        levels = 1;
      if (levels > 10)
        levels = 10;
      fcr = DLUtil.RangeRandom(5, 15);
    }
  }

  public void f(Graphics2D g, DLThread t) {
    setup();
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      step(g);
      if (randomize)
        random(frameCount);
      filter();

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
    filterImage = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
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
    iwidth = DLUtil.RangeRandom(500, 500);
    iheight = iwidth;
  }

  void setup() {
    pixels = new int[iwidth * iheight];
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

  void draw(Graphics2D g) {
    float t = frameCount * tFactor;
    int iw2 = iwidth / 2;
    int ih2 = iheight / 2;
    int ip = 0;

    for (int yp = -ih2; yp < ih2; yp++) {
      float y = yp * dimPix;

      for (int xp = -iw2; xp < iw2; xp++) {
        float x = xp * dimPix;

        float o = 0;
        float s = 0;

        for (int i = 0; i < levels; i++) {
          float sin = DLUtil.Sin(o);
          float cos = DLUtil.Cos(o);
          s += (DLUtil.Cos(cos * x + sin * y + t) + 1f) / 2f;
          o += DLUtil.PI / levels;
        }

        int is = (int) s;
        float ds = s - is;
        s = (is % 2) == 0 ? ds : 1f - ds;

        pixels[ip++] = color(s);
      }
    }
    image.setRGB(0, 0, iwidth, iheight, pixels, 0, iwidth);
  }

  float min = Float.MAX_VALUE;
  float max = Float.MIN_VALUE;

  void check(float c) {
    if (c < min)
      min = c;
    if (c > max)
      max = c;
    System.err.println(min + " " + max);
  }

  int color(float c) {
    c = c * 256;
    int icf = (int) c & 0xff;
    if (backgroundColor != null) {
      int bg = backgroundColor.getRGB();
      int ret = 0xff000000 | ((icf << 16 | icf << 8 | icf) & bg);
      return ret;
    } else {
      return 0xff000000 | icf << 16 | icf << 8 | icf;
    }
  }

  void filter() {
    if (filterStrength > 0) {
      KaleidoscopeFilter k = new KaleidoscopeFilter();
      // k.setCentreX(iwidth / 2);
      // k.setCentreY(iheight / 2);
      k.setAngle(DLUtil.PI / 5f);
      k.setAngle2(DLUtil.PI / 3f);
      k.setRadius((iwidth + iheight) / 4f);
      k.setSides(5);
      // EdgeFilter ef = new EdgeFilter();
      /* BufferedImage filterImage = */k.filter(image, filterImage);
      image = DLUtil.Merge(image, filterImage, filterStrength, null);
    }
  }

  public void setTFactor(float tf) {
    tFactor = tf;
  }

  public float getTFactor() {
    return tFactor;
  }

  public float[] rangeTFactor() {
    return new float[] {
        0.1f, 1
    };
  }

  public void setLevels(int l) {
    levels = l;
  }

  public int getLevels() {
    return levels;
  }

  public int[] rangeLevels() {
    return new int[] {
        1, 15
    };
  }

  public void setDimPix(float dp) {
    dimPix = dp;
  }

  public float getDimPix() {
    return dimPix;
  }

  public float[] rangeDimPix() {
    return new float[] {
        0, 1f
    };
  }

  public void setRandomize(boolean b) {
    randomize = b;
  }

  public boolean getRandomize() {
    return randomize;
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
            "threadSleep", 5
        }, {
          "backgroundColor", new Color(53, 53, 20).brighter().brighter().brighter()
        }
    };
    Class<? extends DLComponent> cls = DLQuasiCristal.class;
    DLMain.Main(cls, params);
  }
}
