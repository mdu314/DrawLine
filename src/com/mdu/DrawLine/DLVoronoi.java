package com.mdu.DrawLine;

import static java.awt.Font.DIALOG;
import static java.awt.Font.DIALOG_INPUT;
import static java.awt.Font.MONOSPACED;
import static java.awt.Font.SANS_SERIF;
import static java.awt.Font.SERIF;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.EdgeFilter;

public class DLVoronoi extends DLPointImage {
  Color background;
  int threadSleep = 10;
  DLPointList sites = new DLPointList();
  int numInitSites = 30;
  float deltaT = 0.8f;
  boolean inited = false;
  boolean clear = false;
  boolean voronoi = true;
  boolean paintSites = true;
  boolean paintFps = true;
  float vmax = 10f;
  int pixelSize = 5;
  final static String EUCLIDEAN = "euclidean";
  final static String MANHATTAN = "manhattan";
  final static String ALIEN = "alien";
  String distance = MANHATTAN;
  boolean blur = false;
  boolean edges = true;
  long frameCount = 1;
  final static String AVANCE = "avance";
  final static String TELEPORT = "teleport";
  final static String SHAKE = "shake";
  final static String NOISE = "noise";
  String avanceMode = AVANCE;
  float shakeRange = 10f;
  float teleportRange = 100f;
  float distFactor = 20;
  boolean flatVoronoi = false;

  public DLVoronoi() {
    super();
  }

  DLVoronoi(DLVoronoi src) {
    this();
  }

  public DLVoronoi(float x, float y) {
    super(x, y);
  }

  DLVoronoi copy() {
    return new DLVoronoi(this);
  }

  void blur() {
    BoxBlurFilter f = new BoxBlurFilter();
    f.setRadius(pixelSize);
    image = f.filter(image, image);
  }

  void edges() {
    EdgeFilter ef = new EdgeFilter();
    image = ef.filter(image, image);
  }

  void avance() {
    for (DLPoint p : sites) {
      float sx = p.vx;
      float sy = p.vy;
      float x = p.x + sx * deltaT;
      float y = p.y + sy * deltaT;

      if (x < 0) {
        x = -x;
        sx = -sx;
      } else if (x > iwidth) {
        x = 2 * iwidth - x;
        sx = -sx;
      }

      if (y < 0) {
        y = -y;
        sy = -sy;
      } else if (y > iheight) {
        y = 2 * iheight - y;
        sy = -sy;
      }

      p.vx = sx;
      p.vy = sy;
      p.x = x;
      p.y = y;
    }
  }

  float[][] theNoise = null;

  void noise() {
    int octaves = 3;
    float persistence = 0.5f;
    if (theNoise == null) {
      float[][] noise = Noise.GenerateWhiteNoise(iwidth, iheight);
      theNoise = Noise.GeneratePerlinNoise(noise, octaves, persistence);
    }
    for (DLPoint p : sites) {
      float x = p.x;
      float y = p.y;

      float n = 10 * theNoise[(int) x][(int) y];

      if (DLUtil.BooleanRandom())
        x += n;
      else
        x -= n;
      if (DLUtil.BooleanRandom())
        y += n;
      else
        y -= n;

      if (x < 0) {
        x = -x;
      } else if (x > iwidth) {
        x = 2 * iwidth - x;
      }

      if (y < 0) {
        y = -y;
      } else if (y > iheight) {
        y = 2 * iheight - y;
      }

      p.x = x;
      p.y = y;
      //      System.err.println(x + " " + y + " " + n);

    }
  }

  void shake() {
    float d = shakeRange;
    for (DLPoint p : sites) {
      float x = p.x + DLUtil.RangeRandom(-d, d);
      float y = p.y + DLUtil.RangeRandom(-d, d);

      if (x < 0) {
        x = -x;
      } else if (x > iwidth) {
        x = 2 * iwidth - x;
      }

      if (y < 0) {
        y = -y;
      } else if (y > iheight) {
        y = 2 * iheight - y;
      }

      p.x = x;
      p.y = y;
    }
  }

  void teleport() {
    float d = teleportRange;
    for (DLPoint p : sites) {
      float x = p.x + DLUtil.RangeRandom(-d, d);
      float y = p.y + DLUtil.RangeRandom(-d, d);

      if (x < 0) {
        x = -x;
      } else if (x > iwidth) {
        x = 2 * iwidth - x;
      }

      if (y < 0) {
        y = -y;
      } else if (y > iheight) {
        y = 2 * iheight - y;
      }

      p.x = x;
      p.y = y;
    }
  }

  DLPoint createSite() {
    float x = DLUtil.RangeRandom(0, iwidth);
    float y = DLUtil.RangeRandom(0, iheight);
    Color c = DLUtil.RandomColor(0, 1, 0.5f, 0.7f, 0.4f, 0.7f);
    DLPoint p = new DLPoint(x, y);
    p.paint = c;
    p.vx = DLUtil.RangeRandom(-vmax, vmax);
    p.vy = DLUtil.RangeRandom(-vmax, vmax);
    return p;
  }

  void init() {
    synchronized (sites) {
      while (sites.size() > numInitSites)
        sites.remove(0);
      while (sites.size() < numInitSites) {
        DLPoint p = createSite();
        sites.add(p);
      }
    }
    pointSize = pixelSize;
  }

  void paintSites(Graphics2D g) {
    synchronized (sites) {
      boolean star = true;
      for (DLPoint p : sites) {
        Shape s = null;
        if (star) {
          float r1 = DLUtil.RangeRandom(1f, 2f);
          float r2 = DLUtil.RangeRandom(3f, 5f);
          int b = DLUtil.RangeRandom(4, 8);
          s = DLUtil.Star(p.x, p.y, r1, r2, b);
        } else {
          final int f = DLUtil.RangeRandom(0, 5);
          final String[] fa = { DIALOG, DIALOG_INPUT, SANS_SERIF, SERIF, MONOSPACED };
          String family = fa[f];
          int style = DLUtil.RangeRandom(0, 4);
          int size = DLUtil.RangeRandom(7, 10);
          s = DLUtil.Char(sites.indexOf(p), family, style, size);
          Rectangle bb = s.getBounds();
          AffineTransform tr = AffineTransform.getTranslateInstance(p.x - bb.getWidth() / 2, p.y - bb.getHeight() / 2);
          s = tr.createTransformedShape(s);
        }
        g.setColor(Color.DARK_GRAY);
        g.draw(s);
        g.setColor(DLUtil.DarkerPaint(p.paint, 0.4f));
        g.fill(s);
      }
    }
  }

  void paintFps(Graphics2D g, long frameTime) {
    if (frameTime == 0)
      return;

    NumberFormat nf = new DecimalFormat("000.00");
    NumberFormat tf = new DecimalFormat("00.00");
    NumberFormat ff = new DecimalFormat("00000");

    Font f = new Font(Font.MONOSPACED, Font.PLAIN, 10);
    FontMetrics metrics = g.getFontMetrics(f);

    int descent = metrics.getDescent();

    String s = "F#: " + ff.format(frameCount) + " S#: " + sites.size() + " Fps: " + nf.format(1000. / frameTime)
        + " Ft: " + tf.format(frameTime) + " ms" + " M: " + getDistance();
    g.setColor(Color.darkGray);
    g.drawString(s, 5, iheight - descent);
  }

  void clearImage() {
    if (image == null) {
      image = image();
    } else {
      final Graphics2D g = image.createGraphics();
      Composite c = null;
      if (background != null) {
        g.setColor(background);
      } else {
        c = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
      }
      final Rectangle rect = new Rectangle(0, 0, iwidth, iheight);
      g.fill(rect);
      if (background == null)
        g.setComposite(c);
    }
  }

  public void f(Graphics2D g, DLThread t) {
    if (!inited) {
      init();
      inited = true;
    }
    long start = System.currentTimeMillis();
    long dt = 0;
    while (frameCount++ > 0) {
      start = System.currentTimeMillis();
      if (t != null && t.isStopped())
        break;

      synchronized (this) {
        switch (avanceMode) {
        case AVANCE:
          avance();
          break;
        case SHAKE:
          shake();
          break;
        case TELEPORT:
          teleport();
          break;
        case NOISE:
          noise();
          break;
        }
        if (clear)
          clear();
        if (voronoi)
          voronoi(g);
        if (blur)
          blur();
        if (edges)
          edges();
        if (paintSites)
          paintSites(g);
        if (paintFps)
          paintFps(g, dt);
      }

      if (parent != null)
        parent.paint(this);
      dt = System.currentTimeMillis() - start;
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

  float maxDist() {
    float w = iwidth / distFactor;
    float h = iheight / distFactor;
    float d = 0;
    switch (distance) {
    case EUCLIDEAN:
      d = w * w + h * h;
      break;
    case MANHATTAN:
      d = w + h;
      break;
    case ALIEN:
      d = w * w * w + h * h * h;
      break;
    }
    return d;
  }

  float dist(int i, int j, DLPoint p) {
    float d = Float.NaN;
    float dx = p.x - i;
    float dy = p.y - j;
    switch (distance) {
    case EUCLIDEAN:
      d = dx * dx + dy * dy;
      break;
    case MANHATTAN:
      dx = dx < 0 ? -dx : dx;
      dy = dy < 0 ? -dy : dy;
      d = dx + dy;
      break;
    case ALIEN:
      dx = dx < 0 ? -dx : dx;
      dy = dy < 0 ? -dy : dy;
      d = dx * dx * dx + dy * dy * dy;
      break;
    default:
      throw new Error("Invalid case " + distance);
      // break;
    }
    return d;
  }

  void voronoi(Graphics2D g) {
    synchronized (sites) {
      int ps = pixelSize;
      if (ps == 0)
        ps = 1;
      for (int i = 0; i < iwidth; i += ps) {
        for (int j = 0; j < iheight; j += ps) {
          float dMin = Float.MAX_VALUE;
          DLPoint ni = null;
          for (int k = 0; k < sites.size(); k++) {
            DLPoint p = sites.get(k);
            float d = dist(i, j, p);
            if (d < dMin) {
              ni = p;
              dMin = d;
            }
          }
          if (flatVoronoi) {
            setPointFill(ni.paint);
            g.setPaint(ni.paint);
          } else {
            float d = dist(i, j, ni);
            d = DLUtil.Normalize(0, 1, 0, maxDist(), d);
            Paint c = DLUtil.DarkerPaint(ni.paint, d);
            setPointFill(c);
            g.setPaint(c);
          }
          drawPoint(g, i + pixelSize / 2f, j + pixelSize / 2f);
        }
      }
    }
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 500);
    iheight = iwidth;
    pointShape = RectanglePoint;
  }

  void paint(Graphics2D g, long dt) {
    DLUtil.SetHints(g);
    super.paint(g);
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

  public int getNumInitSites() {
    return numInitSites;
  }

  public void setNumInitSites(int numInitSites) {
    if (this.numInitSites == numInitSites)
      return;
    this.numInitSites = numInitSites;
    init();
  }

  public int[] rangeNumInitSites() {
    return new int[] { 0, 100 };
  }

  public float getDeltaT() {
    return deltaT;
  }

  public void setDeltaT(float deltaT) {
    this.deltaT = deltaT;
  }

  public float[] rangeDeltaT() {
    return new float[] { 0.1f, 2f };
  }

  public float getVmax() {
    return vmax;
  }

  public void setVmax(float vmax) {
    if (vmax <= 0)
      vmax = 0.01f;
    float r = this.vmax / vmax;
    this.vmax = vmax;
    for (DLPoint p : sites) {
      p.vx /= r;
      p.vy /= r;
    }
  }

  public float[] rangeVmax() {
    return new float[] { 0.1f, 20f };
  }

  public void setPointSize(float sz) {
    super.setPointSize(sz);
    pixelSize = (int) (sz + 0.5f);
    if (sheet != null)
      sheet.update("PixelSize", pixelSize);
  }

  public float[] rangePointSize() {
    return new float[] { 0, 50 };
  }

  public int getPixelSize() {
    return pixelSize;
  }

  public void setPixelSize(int pixelSize) {
    this.pixelSize = pixelSize;
    this.pointSize = pixelSize;
    if (sheet != null)
      sheet.update("PointSize", pointSize);
  }

  public int[] rangePixelSize() {
    return new int[] { 0, 50 };
  }

  public float getDistFactor() {
    return distFactor;
  }

  public void setDistFactor(float distFactor) {
    this.distFactor = distFactor;
  }

  public float[] rangeDistFactor() {
    return new float[] { 1, 100 };
  }

  public String getDistance() {
    return distance;
  }

  public void setDistance(String distance) {
    this.distance = distance;
  }

  public String[] enumDistance() {
    return new String[] { MANHATTAN, EUCLIDEAN, ALIEN };
  }

  public boolean getBlur() {
    return blur;
  }

  public void setBlur(boolean blur) {
    this.blur = blur;
  }

  public boolean getEdges() {
    return edges;
  }

  public void setEdges(boolean edges) {
    this.edges = edges;
  }

  public boolean getClear() {
    return clear;
  }

  public void setClear(boolean clear) {
    this.clear = clear;
  }

  public boolean getPaintSites() {
    return paintSites;
  }

  public void setPaintSites(boolean paintSites) {
    this.paintSites = paintSites;
  }

  public boolean getPaintFps() {
    return paintFps;
  }

  public void setPaintFps(boolean paintFps) {
    this.paintFps = paintFps;
  }

  public Color getBackground() {
    return background;
  }

  public void setBackground(Color color) {
    this.background = color;
  }

  public String getAvanceMode() {
    return avanceMode;
  }

  public void setAvanceMode(String mode) {
    avanceMode = mode;
  }

  public String[] enumAvanceMode() {
    return new String[] { AVANCE, SHAKE, NOISE, TELEPORT };
  }

  public boolean getVoronoi() {
    return voronoi;
  }

  public void setVoronoi(boolean voronoi) {
    this.voronoi = voronoi;
  }

  public boolean getFlatVoronoi() {
    return flatVoronoi;
  }

  public void setFlatVoronoi(boolean v) {
    this.flatVoronoi = v;
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
    Class<? extends DLComponent> cls = DLVoronoi.class;
    DLMain.Main(cls, params);
  }
}
