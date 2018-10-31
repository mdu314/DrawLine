package com.mdu.DrawLine;

import static java.awt.event.MouseEvent.BUTTON1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jhlabs.image.EdgeFilter;

public class DLMultiscaleTuringPatterns extends DLImage {
  int threadSleep = 50;
  int frameCount = 1000;
  BufferedImage filterImage;
  int[] pixels;

  int n, levels;
  float[] grid;
  float[] diffusionLeft, diffusionRight, blurBuffer, variation;
  float[] bestVariation;
  int[] bestLevel;
  boolean[] direction;
  float[] stepSizes;
  int[] radii;

  private static float MIN_BASE = 1.3f;
  private static float MAX_BASE = 3f;
  float base = DLUtil.RangeRandom(MIN_BASE, MAX_BASE);

  private static float MIN_STEP_SCALE = 0.006f;
  private static float MAX_STEP_SCALE = 0.11f;
  float stepScale = DLUtil.RangeRandom(MIN_STEP_SCALE, MAX_STEP_SCALE);

  private static float MIN_STEP_OFFSET = 0.006f;
  private static float MAX_STEP_OFFSET = 0.11f;
  float stepOffset = DLUtil.RangeRandom(MIN_STEP_OFFSET, MAX_STEP_OFFSET);

  public DLMultiscaleTuringPatterns() {
    super();
  }

  DLMultiscaleTuringPatterns(DLMultiscaleTuringPatterns src) {
    this();
  }

  public DLMultiscaleTuringPatterns(float x, float y) {
    super(x, y);
  }

  DLMultiscaleTuringPatterns copy() {
    return new DLMultiscaleTuringPatterns(this);
  }

  public float getBase() {
    return base;
  }

  public void setBase(float v) {
    base = v;
    applyValues();
  }

  public float[] rangeBase() {
    return new float[] { MIN_BASE, MAX_BASE };
  }

  public float getStepScale() {
    return stepScale;
  }

  public void setStepScale(float ss) {
    stepScale = ss;
    applyValues();
  }

  public float[] rangeStepScale() {
    return new float[] { MIN_STEP_SCALE, MAX_STEP_SCALE };
  }

  public float getStepOffset() {
    return stepOffset;
  }

  public void setStepOffset(float v) {
    stepOffset = v;
    applyValues();
  }

  public float[] rangeStepOffset() {
    return new float[] { MIN_STEP_OFFSET, MAX_STEP_OFFSET };
  }

  void applyValues() {
    levels = (int) (Math.log(iwidth) / Math.log(base));
    radii = new int[levels];
    stepSizes = new float[levels];
    // determines the shape of the patterns
    for (int i = 0; i < levels; i++) {
      int radius = (int) Math.pow(base, i);
      radii[i] = radius;
      stepSizes[i] = (float) (DLUtil.FastLog(radius) * stepScale + stepOffset);
    }

  }

  void setup() {

    pixels = new int[iwidth * iheight];

    applyValues();

    // allocate space
    n = iwidth * iheight;
    grid = new float[n];
    diffusionLeft = new float[n];
    diffusionRight = new float[n];
    blurBuffer = new float[n];
    variation = new float[n];
    bestVariation = new float[n];
    bestLevel = new int[n];
    direction = new boolean[n];

    // initialize the grid with noise
    for (int i = 0; i < n; i++) {
      grid[i] = DLUtil.RangeRandom(-1f, +1f);
    }
  }

  public void f(Graphics2D g, DLThread t) {
    setup();
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      try {
        step(g);
      } catch (Throwable b) {
        System.err.println(b);
      }
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
    float[] activator = grid;
    float[] inhibitor = diffusionRight;

    for (int level = 0; level < levels - 1; level++) {
      // blur activator into inhibitor
      int radius = radii[level];
      blur(activator, inhibitor, blurBuffer, iwidth, iheight, radius);

      // absdiff between activator and inhibitor
      for (int i = 0; i < n; i++) {
        variation[i] = activator[i] - inhibitor[i];
        if (variation[i] < 0) {
          variation[i] = -variation[i];
        }
      }

      if (level == 0) {
        // save bestLevel and bestVariation
        for (int i = 0; i < n; i++) {
          bestVariation[i] = variation[i];
          bestLevel[i] = level;
          direction[i] = activator[i] > inhibitor[i];
        }
        activator = diffusionRight;
        inhibitor = diffusionLeft;
      } else {
        // check/save bestLevel and bestVariation
        for (int i = 0; i < n; i++) {
          if (variation[i] < bestVariation[i]) {
            bestVariation[i] = variation[i];
            bestLevel[i] = level;
            direction[i] = activator[i] > inhibitor[i];
          }
        }
        float[] swap = activator;
        activator = inhibitor;
        inhibitor = swap;
      }
    }

    // update grid from bestLevel
    float smallest = Float.MAX_VALUE;
    float largest = Float.MIN_VALUE;
    for (int i = 0; i < n; i++) {
      float curStep = stepSizes[bestLevel[i]];
      if (direction[i]) {
        grid[i] += curStep;
      } else {
        grid[i] -= curStep;
      }
      smallest = Math.min(smallest, grid[i]);
      largest = Math.max(largest, grid[i]);
    }

    // normalize to [-1, +1]
    float range = (largest - smallest) / 2;
    for (int i = 0; i < n; i++) {
      grid[i] = ((grid[i] - smallest) / range) - 1;
    }
    draw(g);
  }

  void blur(float[] from, float[] to, float[] buffer, int w, int h, int radius) {
    // build integral image
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int i = y * w + x;
        if (y == 0 && x == 0) {
          buffer[i] = from[i];
        } else if (y == 0) {
          buffer[i] = buffer[i - 1] + from[i];
        } else if (x == 0) {
          buffer[i] = buffer[i - w] + from[i];
        } else {
          buffer[i] = buffer[i - 1] + buffer[i - w] - buffer[i - w - 1] + from[i];
        }
      }
    }
    // do lookups
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int minx = Math.max(0, x - radius);
        int maxx = Math.min(x + radius, w - 1);
        int miny = Math.max(0, y - radius);
        int maxy = Math.min(y + radius, h - 1);
        int area = (maxx - minx) * (maxy - miny);

        int nw = miny * w + minx;
        int ne = miny * w + maxx;
        int sw = maxy * w + minx;
        int se = maxy * w + maxx;

        int i = y * w + x;
        to[i] = (buffer[se] - buffer[sw] - buffer[ne] + buffer[nw]) / area;
      }
    }
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(500, 500);
    iheight = DLUtil.RangeRandom(500, 500);
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

  void draw(Graphics2D g) {
    for (int i = 0; i < n; i++) {
      pixels[i] = color(128 + 128 * grid[i]);
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
    if (backgroundColor != null) {
      int icf = (int) c & 0xff;
      int bg = backgroundColor.getRGB();
      int ret = 0xff000000 | ((icf << 16 | icf << 8 | icf) & bg);
      return ret;
    } else {
      int icf = (int) c & 0xff;
      return 0xff000000 | icf << 16 | icf << 8 | icf;
    }
  }

  void filter() {
    if (filterStrength > 0) {
      EdgeFilter ef = new EdgeFilter();
      // BoxBlurFilter ef = new BoxBlurFilter();
      filterImage = ef.filter(image, filterImage);
      image = DLUtil.Merge(image, filterImage, filterStrength, null);
    }
  }

  public static void main(String[] a) {
    int w = 600;
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
      }, {
        "backgroundColor", new Color(53, 53, 20).brighter().brighter().brighter()
      }
    };
    DLMain.Main(DLMultiscaleTuringPatterns.class, params);
  }

}
