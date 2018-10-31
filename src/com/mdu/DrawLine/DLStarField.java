package com.mdu.DrawLine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class DLStarField extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;
  Star[] stars;

  int numStars = 1500;
  float velocity = 5f;
  float growFactor = 100f;
  float hOffset = 0;
  float vOffset = 0;
  float small = 1f;
  float verySmall = 0;
  boolean sort = false;
  int tailLength = 0;
  float gaussX = 500f;
  float gaussY = 500f;
  float color = 5f;
  float cent = 50f;
  float far = 1f;

  public float getCent() {
    return cent;
  }

  public void setCent(float c) {
    cent = c;
  }

  public float[] rangeCent() {
    return new float[] {
        0.1f, 100f
    };
  }

  public float getFar() {
    return far;
  }

  public void setFar(float c) {
    far = c;
  }

  public float[] rangeFar() {
    return new float[] {
        0.1f, 10f
    };
  }

  public float getColor() {
    return color;
  }

  public void setColor(float color) {
    this.color = color;
  }

  public float[] rangeColor() {
    float[] rangeColor = {
        0, 256
    };
    return rangeColor;
  }

  public float getGaussX() {
    return gaussX;
  }

  public void setGaussX(float gaussX) {
    this.gaussX = gaussX;
  }

  public float[] rangeGaussX() {
    float[] rangeGaussX = {
        1, 1000
    };
    return rangeGaussX;
  }

  public float getGaussY() {
    return gaussY;
  }

  public void setGaussY(float gaussY) {
    this.gaussY = gaussY;
  }

  public float[] rangeGaussY() {
    float[] rangeGaussY = {
        1, 1000
    };
    return rangeGaussY;
  }

  public int getTailLength() {
    return tailLength;
  }

  public void setTailLength(int tailLength) {
    this.tailLength = tailLength;
  }

  public int[] rangeTailLength() {
    int[] ret = {
        0, 10
    };
    return ret;
  }

  public boolean getSort() {
    return sort;
  }

  public void setSort(boolean sort) {
    this.sort = sort;
  }

  public float getSmall() {
    return small;
  }

  public void setSmall(float small) {
    this.small = small;
  }

  public float[] rangeSmall() {
    float[] rangeSmall = {
        1f, 100f
    };
    return rangeSmall;
  }

  public float getVerySmall() {
    return verySmall;
  }

  public void setVerySmall(float verySmall) {
    this.verySmall = verySmall;
  }

  public float[] rangeVerySmall() {
    float[] rangeVerySmall = {
        0, 10f
    };
    return rangeVerySmall;
  }

  public float gethOffset() {
    return hOffset;
  }

  public void sethOffset(float hOffset) {
    this.hOffset = hOffset;
  }

  public float[] rangehOffset() {
    return new float[] {
        -200, 200
    };
  }

  public float getvOffset() {
    return vOffset;
  }

  public void setvOffset(float vOffset) {
    this.vOffset = vOffset;
  }

  public float[] rangevOffset() {
    return new float[] {
        -200, 200
    };
  }

  public DLStarField() {
    super();
  }

  DLStarField(DLStarField src) {
    this();
  }

  public DLStarField(float x, float y) {
    super(x, y);
  }

  DLStarField copy() {
    return new DLStarField(this);
  }

  synchronized void setup() {
    stars = new Star[numStars];
    for (int i = 0; i < numStars; i++) {
      Star s = new Star();
      s.velocity = velocity;
      stars[i] = s;
    }

  }

  synchronized void sortStars() {
    Arrays.sort(stars, new Comparator<Star>() {
      public int compare(Star s1, Star s2) {
        int ret = 0;
        if (s1.pos.displaySize > s2.pos.displaySize)
          ret = 1;
        if (s1.pos.displaySize < s2.pos.displaySize)
          ret = -1;
        return ret;
      }
    });
  }

  synchronized void draw(Graphics2D g) {
    if (sort)
      sortStars();
    for (Star s : stars)
      s.display(g);
  }

  public void f(Graphics2D g, DLThread t) {
    backgroundColor = new Color(0, 0, 25);

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

  synchronized void update() {
    for (Star s : stars)
      s.update();
  }

  void step(Graphics2D g) {
    update();
    clearImage();
    draw(g);
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 500);
    iheight = iwidth;
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public int getNumStars() {
    return numStars;
  }

  synchronized void updateStars() {
    Star[] ns = new Star[numStars];

    if (numStars > stars.length) {
      for (int i = 0; i < stars.length; i++)
        ns[i] = stars[i];
      for (int i = stars.length; i < numStars; i++) {
        Star s = new Star();
        s.velocity = velocity;
        ns[i] = s;
      }
    } else {
      for (int i = 0; i < ns.length; i++)
        ns[i] = stars[i];
    }

    stars = ns;
  }

  public void setNumStars(int numStars) {
    this.numStars = numStars;
    updateStars();
  }

  public int[] rangeNumStars() {
    return new int[] {
        1, 30000
    };
  }

  public float getVelocity() {
    return velocity;
  }

  synchronized public void setVelocity(float velocity) {
    this.velocity = velocity;
    for (Star s : stars)
      s.velocity = velocity;
  }

  public float[] rangeVelocity() {
    float ret[] = {
        0f, 50f
    };
    return ret;
  }

  public float getGrowFactor() {
    return growFactor;
  }

  synchronized public void setGrowFactor(float growFactor) {
    this.growFactor = growFactor;
  }

  public float[] rangeGrowFactor() {
    return new float[] {
        1f, 1000f
    };
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] {
        0, 10000
    };
  }

  public static void main(String[] a) {
    int w = 500;
    int h = 300;
    int w2 = w / 2;
    int h2 = h / 2;

    Object[][] params = {
        {
            "width", w
        }, {
            "height", h
        }, {
            "iwidth", w
        }, {
            "iheight", h
        }, {
            "x", w2
        }, {
            "y", h2
        }, {
            "threadSleep", 5
        }
    };

    DLMain.Main(DLStarField.class, params);
  }

  class StarPos {
    float cx;
    float cy;
    float z;
    float displaySize;

    StarPos() {
      cx = cy = Float.NaN;
    }

    StarPos(float cx, float cy, float z, float ds) {
      this.cx = cx;
      this.cy = cy;
      this.z = z;
      this.displaySize = ds;
    }

    StarPos(StarPos sp) {
      cx = sp.cx;
      cy = sp.cy;
      z = sp.z;
      displaySize = sp.displaySize;
    }

    boolean out() {
      return cx < 0 || cx > iwidth || cy < 0 || cy > iheight || z < 1 || displaySize <= verySmall;
    }

    public String toString() {
      return "cx " + cx + " cy " + cy + " z " + z + " displaySize " + displaySize;
    }
  }

  class Star {
    float x;
    float y;
    float velocity;
    float size;
    StarPos pos = new StarPos();

    boolean useLine = true;

    ArrayList<StarPos> tail = new ArrayList<StarPos>(0);

    Star() {
      randomizePosition(true);
    }

    void randomizePosition(boolean randomizeZ) {
      x = DLUtil.RandomGauss(0, gaussX);
      y = DLUtil.RandomGauss(0, gaussY);

      if (randomizeZ)
        pos.z = DLUtil.RangeRandom(100f, 1000f);
      else
        pos.z = 1000f;

      size = DLUtil.RangeRandom(1f, 5f);

      pos.cx = pos.cy = Float.NaN;
    }

    public float getVelocity() {
      return velocity;
    }

    public void setVelocity(float velocity) {
      this.velocity = velocity;
    }

    void updatePos() {
      pos.z -= velocity;
      pos.cx = cent * x / pos.z + iwidth / 2f + hOffset;
      pos.cy = cent * y / pos.z + iheight / 2f + vOffset;
      pos.displaySize = growFactor * size / (far * pos.z);
      if (pos.out())
        randomizePosition(true);
    }

    void updateTail() {
      if (tailLength > 0) {
        if (!pos.out()) {
          StarPos p = new StarPos(pos);
          tail.add(p);
        }
        while (tail.size() > tailLength)
          tail.remove(0);
      }
    }

    void update() {
      updatePos();
      updateTail();
    }

    Shape makeTailShape(StarPos p1, StarPos p2) {
      if (useLine) {
        return DLUtil.Line(p1.cx, p1.cy, p2.cx, p2.cy);
      } else {
        Point2D.Float[] r = DLUtil.orthopoints(p1.cx, p1.cy, p1.cx, p2.cy, p2.displaySize, 0);
        DLPath p = new DLPath();
        p.moveTo(r[0].x, r[0].y);
        p.lineTo(p2.cx, p2.cy);
        p.lineTo(r[1].x, r[1].y);
        p.closePath();
        return p;
      }
    }

    void displayTail(Graphics2D gr, Color c) {
      for (int i = 0; i < tail.size() - 1; i++) {
        StarPos p1 = tail.get(i);
        StarPos p2 = tail.get(i + 1);
        float d = DLUtil.SquareDist(p1.cx, p1.cy, p2.cx, p2.cy);
        if (d < 250f) { // TODO Remove that
          Shape l = makeTailShape(p1, p2);
          BasicStroke bs = new BasicStroke(1f);
          gr.setStroke(bs);
          gr.setColor(c);
          gr.draw(l);
        }
      }
    }

    void display(Graphics2D gr) {
      if (pos.displaySize <= verySmall)
        return;

      float i = 1f - pos.z / 1000f;
      int r = DLUtil.IntAbs(i * 255f + DLUtil.RangeRandom(color)) % 255;
      int g = DLUtil.IntAbs(i * 255f + DLUtil.RangeRandom(color)) % 255;
      int b = DLUtil.IntAbs(i * 255f + DLUtil.RangeRandom(color)) % 255;
      Color c1 = new Color(r, g, b);

      displayTail(gr, c1);

      if (pos.displaySize <= small) {
        if (useLine) {
          BasicStroke bs = new BasicStroke(pos.displaySize);
          gr.setStroke(bs);
          Shape l = DLUtil.Line(pos.cx, pos.cy, pos.cx, pos.cy);
          gr.setColor(c1);
          gr.draw(l);
        } else {
          gr.setColor(c1);
          float r2 = pos.displaySize / 2f;
          float r1 = r2 / 2f;
          Shape l = DLUtil.Star(pos.cx, pos.cy, r1, r2, 5);
          gr.fill(l);
        }
      } else {
        Color c2 = new Color(r, g, b, 0);
        Color colors[] = {
            c1, c2
        };
        float[] dist = {
            DLUtil.RangeRandom(0, 0.5f), 1f
        };
        Shape s = DLUtil.Rectangle(pos.cx, pos.cy, pos.displaySize, pos.displaySize);
        gr.setPaint(new RadialGradientPaint(pos.cx, pos.cy, pos.displaySize / 2, dist, colors));
        gr.fill(s);
      }
    }
  }
}
