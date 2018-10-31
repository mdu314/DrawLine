package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.SwingWorker;

public class DLFlowers extends DLImage {
  int threadSleep = 50;
  long frameCount = 1;
  int W = 2;
  int H = 2;
  Flower[][] flowers; // = new Flower[W][H];
  ImageFader fader;
  float cent = 100f;

  public int getW() {
    return W;
  }

  synchronized public void setW(int w) {
    W = w;
    setup();
    clearImage();
  }

  public int[] rangeW() {
    return new int[] {
        1,
        10
    };
  }

  public int getH() {
    return H;
  }

  synchronized public void setH(int h) {
    H = h;
    setup();
    clearImage();
  }

  public int[] rangeH() {
    return new int[] {
        1,
        10
    };
  }

  public float getCent() {
    return cent;
  }

  public void setCent(float cent) {
    this.cent = cent;
  }

  public float[] rangeCent() {
    return new float[] {
        0f,
        1000f
    };
  }

  static float smallFloat = 0.0000001f; // (float) Math.pow(2, -126);

  public DLFlowers() {
    super();
  }

  DLFlowers(DLFlowers src) {
    this();
  }

  public DLFlowers(float x, float y) {
    super(x, y);
  }

  DLFlowers copy() {
    return new DLFlowers(this);
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
          DLError.report(e);
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
    randomizeFlowers2();
    update();
    draw(g);
  }

  synchronized public void randomizeFlowers2() {
    for (int j = 0; j < H; j++) {
      for (int i = 0; i < W; i++) {
        if (DLUtil.BooleanRandom())
          flowers[i][j].randomize();
        else
          flowers[i][j].randomizeABit();
      }
    }
  }

  synchronized public void randomizeFlowers() {
    for (int j = 0; j < H; j++) {
      for (int i = 0; i < W; i++) {
        if (DLUtil.BooleanRandom(0.9f))
          flowers[i][j].randomize();
      }
    }
  }

  synchronized public void randomizeFlowersABit() {
    for (int j = 0; j < H; j++) {
      for (int i = 0; i < W; i++) {
        if (DLUtil.BooleanRandom(0.9f))
          flowers[i][j].randomizeABit();
      }
    }
  }

  synchronized public void mutateFlowers() {
    for (int j = 0; j < H; j++) {
      for (int i = 0; i < W; i++) {

        if (DLUtil.BooleanRandom(0.2f))
          continue;

        Flower f = flowers[i][j];
        Flower nf = null;
        int ni = (i + 1) % (W - 1);
        int nj = (j + 1) % (H - 1);

        nf = flowers[ni][nj];
        f.mutateWith(nf);
      }
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
        0,
        10000
    };
  }

  synchronized void setup() {
    int fw = iwidth / W;
    int fh = iheight / H;
    int fs = DLUtil.Min(fw, fh);

    if (flowers != null) {
      synchronized (flowers) {
        for (int i = 0; i < flowers.length; i++) {
          for (int j = 0; j < flowers[i].length; j++) {
            Flower f = flowers[i][j];
            if (f != null)
              f.stop();
          }
        }
      }
    }

    flowers = new Flower[W][H];

    synchronized (flowers) {
      for (int j = 0; j < H; j++) {
        for (int i = 0; i < W; i++) {
          flowers[i][j] = new Flower(this, i * fs, j * fs, fs / 2);
        }
      }
    }
  }

  synchronized void update() {
    for (int j = 0; j < H; j++) {
      for (int i = 0; i < W; i++) {
        flowers[i][j].updateImg();
      }
    }
  }

  synchronized void draw(Graphics2D g) {
    for (int j = 0; j < H; j++) {
      for (int i = 0; i < W; i++) {
        flowers[i][j].draw(g);
      }
    }
  }

  int sign(float n) {
    if (n < 0)
      return -1;
    if (n > 0)
      return 1;
    return 0;
  }

  class Flower {

    private float x;
    private float y;
    private float s;

    private float rFlakes;
    private float gFlakes;
    private float bFlakes;

    private float rWaves;
    private float gWaves;
    private float bWaves;

    private BufferedImage img;
    private BufferedImage newimg;

    private boolean fade = false;
    DLFlowers flowers;

    Flower(DLFlowers flowers, float x, float y, float s) {
      this.flowers = flowers;
      this.x = x;
      this.y = y;
      this.s = s;

      int is = (int) (2 * s);
      img = new BufferedImage(is, is, BufferedImage.TYPE_INT_RGB);

      randomize();
    }

    Flower(DLFlowers flowers, int x, int y, int s) {
      this(flowers, (float) x, (float) y, (float) s);
    }

    void stop() {
      fade = false;
    }

    void draw(Graphics2D g) {
      if (g == null)
        g = flowers.image.createGraphics();
      g.drawImage(img, null, DLUtil.Floor(x), DLUtil.Floor(y));
    }

    void setParams(int rF, int gF, int bF, int rW, int gW, int bW) {
      this.rFlakes = rF;
      this.gFlakes = gF;
      this.bFlakes = bF;
      this.rWaves = rW;
      this.gWaves = gW;
      this.bWaves = bW;
      updateImg();
    }

    void duplicateFrom(Flower f) {
      this.rFlakes = f.rFlakes;
      this.gFlakes = f.gFlakes;
      this.bFlakes = f.bFlakes;
      this.rWaves = f.rWaves;
      this.gWaves = f.gWaves;
      this.bWaves = f.bWaves;
      updateImg();
    }

    void randomizeABit() {
      float v = 0.5f;
      if (DLUtil.BooleanRandom())
        this.rFlakes += DLUtil.RangeRandom(v);
      if (DLUtil.BooleanRandom())
        this.gFlakes += DLUtil.RangeRandom(v);
      if (DLUtil.BooleanRandom())
        this.bFlakes += DLUtil.RangeRandom(v);
      if (DLUtil.BooleanRandom())
        this.rWaves += DLUtil.RangeRandom(v);
      if (DLUtil.BooleanRandom())
        this.gWaves += DLUtil.RangeRandom(v);
      if (DLUtil.BooleanRandom())
        this.bWaves += DLUtil.RangeRandom(v);
      updateImg();
    }

    void mutateWith(Flower f) {
      this.rFlakes = DLUtil.BooleanRandom() ? f.rFlakes : this.rFlakes;
      this.gFlakes = DLUtil.BooleanRandom() ? f.gFlakes : this.gFlakes;
      this.bFlakes = DLUtil.BooleanRandom() ? f.bFlakes : this.bFlakes;
      this.rWaves = DLUtil.BooleanRandom() ? f.rWaves : this.rWaves;
      this.gWaves = DLUtil.BooleanRandom() ? f.gWaves : this.gWaves;
      this.bWaves = DLUtil.BooleanRandom() ? f.bWaves : this.bWaves;
      updateImg();
    }

    int checkColor(int i) {
      if (i < 0)
        i = 0;
      if (i > 255)
        i = 255;
      return i;
    }

    float checkColor(float i) {
      if (i < 0f)
        i = 0f;
      if (i > 255f)
        i = 255f;
      return i;
    }

    float ff1one = 1f;

    float ff1(float a) {
      if (DLUtil.BooleanRandom(ff1one))
        return (float) Math.sin(a);
      else
        return (float) Math.cos(a);
    }

    float ff2one = 1f;

    float ff2(float a) {
      if (DLUtil.BooleanRandom(ff2one))
        return (float) Math.cos(a);
      else
        return (float) Math.sin(a);
    }

    float f1(float a) {
      return ff1(a);
    }

    float f2(float a) {
      return ff2(a);
    }

    float f3(float a) {
      return ff1(a);
    }

    float f4(float a) {
      return ff2(a);
    }

    float f5(float a) {
      return ff1(a);
    }

    float f6(float a) {
      return ff2(a);
    }

    float dist(float i, float j) {
      // if (true)
      return DLUtil.EuclideanDist(0, 0, i, j);
      // else
      // return DLUtil.SquareDist(0, 0, i / 10f, j / 10f);
    }

    void updateImg() {
      synchronized (img) {

        int w = img.getWidth();
        int[] pixels = img.getRGB(0, 0, w, w, null, 0, w);
        synchronized (pixels) {
          if (pixels != null) {
            for (float i = -s; i < s; i++) {
              int jR = (int) DLUtil.FastSqrt(s * s - i * i) - 1;
              for (int j = -jR; j < jR; j++) {

                float p = s + i;
                float q = s + j;

                float k;
                float ai = i < 0 ? -i : i;

                if (ai < smallFloat)
                  k = Float.MAX_VALUE;
                else
                  k = (float) j / i;

                float a = -DLUtil.Atan(k);

                float r = dist(i, j);

                float cR = (128 + (127 * f1(a * rFlakes))) * f2(cent * r / s / rWaves) * 0.5f * s / r;
                float cG = (128 + (127 * f3(a * gFlakes))) * f4(cent * r / s / gWaves) * 0.5f * s / r;
                float cB = (128 + (127 * f5(a * bFlakes))) * f6(cent * r / s / bWaves) * 0.5f * s / r;

                cR = checkColor(cR);
                cG = checkColor(cG);
                cB = checkColor(cB);

                int px = DLUtil.IntColor(cR, cG, cB);

                int index = DLUtil.Floor(p + 2 * s * q);
                if (index < pixels.length)
                  pixels[index] = px;
                else
                  System.err.println("index " + index + "pixels.length " + pixels.length);
              }
            }
          } else {
            System.err.println("pixels is null");
          }
        }

        if (fade) {
          if (newimg == null)
            newimg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
          newimg.setRGB(0, 0, w, w, pixels, 0, w);
          fader = new ImageFader(this, img, newimg, 20);
          fader.fade();
        } else {
          img.setRGB(0, 0, w, w, pixels, 0, w);
        }
      }
    }

    void randomize() {
      rFlakes = DLUtil.RangeRandom(0f, 30);
      gFlakes = DLUtil.RangeRandom(0f, 30);
      bFlakes = DLUtil.RangeRandom(0f, 30);

      rWaves = DLUtil.RangeRandom(0f, 10);
      gWaves = DLUtil.RangeRandom(0f, 10);
      bWaves = DLUtil.RangeRandom(0f, 10);
      updateImg();
    }

    int sign(float n) {
      int s = 0;
      if (n < 0)
        s = -1;
      if (n > 0)
        s = 1;
      return (s);
    }

  }

  class ImageFader {
    BufferedImage from;
    BufferedImage to;
    BufferedImage faded;
    float ratio = 0f;
    SwingWorker<BufferedImage, Object> worker;
    Flower flower;
    int steps = 50;

    ImageFader(Flower f, BufferedImage from, BufferedImage to, int steps) {
      flower = f;
      this.from = from;
      this.to = to;
      faded = DLUtil.copy(to, null);
    }

    void stop() {
      if (worker != null)
        worker.cancel(true);
    }

    void fade() {
      if (worker != null)
        worker.cancel(true);

      worker = new SwingWorker<BufferedImage, Object>() {

        protected BufferedImage doInBackground() throws Exception {
          while (ratio < 1f) {
            ratio += 1f / steps;
            if (ratio < 1)
              faded = DLUtil.Merge(from, to, ratio, faded);
            flower.img = faded;
            flower.draw(null);
            flower.flowers.draw(null);
          }
          return faded;
        }

        protected void done() {
          try {
            flower.img = faded;
            worker.cancel(true);
            worker = null;
          } catch (Exception ignore) {
          }
        }

      };
      worker.execute();
    }
  }

  public void reset() {
    System.err.println("reset");
    image = image();
    setup();
  }

  public static void main(String[] a) {
    int w = 600;
    int h = 400;
    Object[][] params = {
        {
            "iwidth",
            w
        },
        {
            "iheight",
            h
        },
        {
            "width",
            w
        },
        {
            "height",
            h
        },
        {
            "x",
            w / 2
        },
        {
            "y",
            h / 2
        },
        {
            "threadSleep",
            5
        }
    };

    DLMain.Main(DLFlowers.class, params);
  }

}
