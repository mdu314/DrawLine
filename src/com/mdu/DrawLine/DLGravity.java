package com.mdu.DrawLine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.jhlabs.image.EdgeFilter;

public class DLGravity extends DLImage {
  private int threadSleep = 50;
  private int frameCount = 0;
  private BufferedImage filterImage;
  private ArrayList<GParticle> particles;

  private float ATT_FORCE = 15.0f;
  private float attForce = ATT_FORCE;

  public float getAttForce() {
    return attForce;
  }

  public void setAttForce(float attForce) {
    this.attForce = attForce;
  }

  public float[] rangeAttForce() {
    return new float[] {
        1f, 20f
    };
  }

  private int TAIL_LENGTH = 133;
  private int tailLength = TAIL_LENGTH;

  public int getTailLength() {
    return tailLength;
  }

  public void setTailLength(int tailLength) {
    this.tailLength = tailLength;
  }

  public int[] rangeTailLength() {
    return new int[] {
        0, 200
    };
  }

  private float FRICTION = 0.006f;
  private float friction = FRICTION;

  public float getFriction() {
    return friction;
  }

  public void setFriction(float friction) {
    this.friction = friction;
  }

  public float[] rangeFriction() {
    return new float[] {
        0, 0.01f
    };
  }

  private float FOLLOW_SPEED = 30.0f;
  private float followSpeed = FOLLOW_SPEED;

  public float getFollowSpeed() {
    return followSpeed;
  }

  public void setFollowSpeed(float followSpeed) {
    this.followSpeed = followSpeed;
  }

  public float[] rangeFollowSpeed() {
    return new float[] {
        1f, 50f
    };
  }

  private float ZERO_QUATRE_VINGT_QUINZE = 0.95f;
  private float zeroQuatreVingtQuinze = ZERO_QUATRE_VINGT_QUINZE;

  public float getZeroQuatreVingtQuinze() {
    return zeroQuatreVingtQuinze;
  }

  public void setZeroQuatreVingtQuinze(float zeroQuatreVingtQuinze) {
    this.zeroQuatreVingtQuinze = zeroQuatreVingtQuinze;
  }

  public float[] rangeZeroQuatreVingtQuinze() {
    return new float[] {
        0, 1
    };
  }

  private int C_TRAIL_LENGTH = 500;
  private int cTailLength = C_TRAIL_LENGTH;

  public int getcTailLength() {
    return cTailLength;
  }

  public void setcTailLength(int cTailLength) {
    this.cTailLength = cTailLength;
  }

  public int[] rangecTailLength() {
    return new int[] {
        0, 1000
    };
  }

  private Point lastMousePoint = new Point();
  private Point mousePoint = new Point();
  private float sx, sy;
  private PVector center, scenter;
  private ArrayList<PVector> centrail;
  private float l, r, t, b;

  public DLGravity() {
    super();
  }

  DLGravity(DLGravity src) {
    this();
  }

  public DLGravity(float x, float y) {
    super(x, y);
  }

  DLGravity copy() {
    return new DLGravity(this);
  }

  public void f(Graphics2D g, DLThread t) {
    setup();

    DLUtil.SetHints(g);

    while (frameCount++ >= 0) {

      if (t != null && t.isStopped())
        break;

      try {
        step(g);
      } catch (Throwable b) {
        System.err.println(b);
      }

      g = filter(g);

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

  boolean mouse(MouseEvent e) {
    if (e instanceof MouseWheelEvent) {
      return true;
    }
    switch (e.getID()) {
    case MouseEvent.MOUSE_DRAGGED:
      lastMousePoint = mousePoint;
      mousePoint = e.getPoint();
      return true;
    case MouseEvent.MOUSE_PRESSED:
      lastMousePoint = mousePoint;
      mousePoint = e.getPoint();
      return true;
    case MouseEvent.MOUSE_RELEASED:
      lastMousePoint = mousePoint;
      mousePoint = e.getPoint();

      float mx = e.getX() - this.x + scenter.x;
      float my = e.getY() - this.y + scenter.y;

      particles.add(new GParticle(mx, my));

      return true;
    default:
      return false;
    }
  }

  void step(Graphics2D g) {
    clearImage();
    draw(g);
    paintBorder(g);
  }

  void paintBorder(Graphics2D g) {
    float cx = iwidth / 2f;
    float cy = iheight / 2f;
    float w = iwidth - 1f;
    float h = iheight - 1f;
    Shape s = DLUtil.Rectangle(cx, cy, w, h);
    g.draw(s);
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

  public void randomize() {
    iwidth = DLUtil.RangeRandom(400, 500);
    iheight = DLUtil.RangeRandom(400, 500);
    threadSleep = 5;
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

  Graphics2D filter(Graphics2D g) {
    if (filterStrength > 0) {
      EdgeFilter ef = new EdgeFilter();
      /* BufferedImage filterImage = */ef.filter(image, filterImage);
      image = DLUtil.Merge(image, filterImage, filterStrength, null);
      return image.createGraphics();
    }
    return g;
  }

  void setup() {

    center = new PVector(0, 0);
    scenter = new PVector(0, 0);
    centrail = new ArrayList<PVector>();
    particles = new ArrayList<GParticle>();

    for (int i = 0; i < 3; i++)
      particles.add(new GParticle(DLUtil.RangeRandom(0f, iwidth), DLUtil.RangeRandom(0f, iheight)));
  }

  void draw(Graphics2D g) {

    scenter.x += (center.x - scenter.x) / followSpeed;
    scenter.y += (center.y - scenter.y) / followSpeed;

    AffineTransform tr = g.getTransform();
    AffineTransform nt = AffineTransform.getTranslateInstance(-scenter.x + iwidth / 2, -scenter.y + iheight / 2);

    g.setTransform(nt);

    int red = 0xf1;
    int green = 0x00;
    int blue = 0x26;

    for (int i = 2; i < centrail.size(); i++) {
      PVector tmp1 = centrail.get(i - 1);
      PVector tmp2 = centrail.get(i);

      int transparency = (int) DLUtil.Normalize(5, 90, 0, centrail.size(), i);
      Line2D.Float l = new Line2D.Float(tmp1.x, tmp1.y, tmp2.x, tmp2.y);
      g.setPaint(new Color(red, green, blue, transparency));
      g.draw(l);
    }

    red = 0xa6;
    green = 0x5a;
    blue = 0x34;
    Shape shpa[] = DLUtil.Target((l + sx / 2.0f), (t + sy / 2.0f), 10);
    // Shape shp = DLUtil.Circle((l + sx / 2.0f), (t + sy / 2.0f), 10);
    g.setPaint(new Color(red, green, blue));
    // g.fill(shp);
    // shp = DLUtil.Circle(center.x, center.y, 15);
    for (Shape s : shpa)
      g.draw(s);

    red = 0xf1;
    green = 0x00;
    blue = 0x26;
    Shape shp = DLUtil.Circle(center.x, center.y, 5);
    g.setPaint(new Color(red, green, blue));
    g.fill(shp);

    l = t = 10000.0f;
    r = b = -10000.0f;

    for (int i = 0; i < particles.size(); i++) {
      GParticle tmp = particles.get(i);
      tmp.move();
    }

    for (int i = 0; i < particles.size(); i++) {
      GParticle tmp = particles.get(i);
      tmp.drawTail(g);
    }

    for (int i = 0; i < particles.size(); i++) {
      GParticle tmp = particles.get(i);
      tmp.draw(g);
    }

    sx = (r - l);
    sy = (b - t);

    centrail.add(new PVector((center.x), (center.y)));

    while (centrail.size() > cTailLength)
      centrail.remove(0);

    g.setTransform(tr);
  }

  class Field {
    ArrayList<PVector> pnts;
    ArrayList<PVector> orig;

    PVector pos;

    Field() {

      pnts = new ArrayList<PVector>();
      orig = new ArrayList<PVector>();

      for (int i = 0; i < iwidth * iheight; i += 4) {
        pnts.add(new PVector(i % iwidth, i / iwidth));
        orig.add(new PVector(i % iwidth, i / iwidth));
      }
    }

    void draw(Graphics2D g) {

      for (int i = 0; i < pnts.size(); i++) {
        PVector pos = (PVector) pnts.get(i);
        PVector org = (PVector) orig.get(i);
        pos.x = org.x;// (org.x-pos.x)/(3.0);
        pos.y = org.y;// (org.y-pos.y)/(3.0);
      }

      g.setStroke(new BasicStroke(0.5f));
      DLUtil.SetHints(g);

      for (int z = 0; z < particles.size(); z++) {
        GParticle tmp = particles.get(z);

        float X = tmp.rpos.x;
        float Y = tmp.rpos.y;

        for (int i = 0; i < pnts.size(); i++) {
          PVector pos = (PVector) pnts.get(i);

          float d = DLUtil.EuclideanDist(X, Y, pos.x, pos.y) + 1.0f;

          pos.x += (X - pos.x) / (particles.size() * d / 200.0);
          pos.y += (Y - pos.y) / (particles.size() * d / 200.0);

          if (d < 100)
            point(g, pos.x, pos.y);
        }
      }
    }

    void point(Graphics2D g, float x, float y) {
      float dim = 5;
      Shape ell = DLUtil.Circle(x, y, dim);
      g.draw(ell);
    }
  }

  class GParticle {
    ArrayList<PVector> tail;
    PVector rpos, pos, acc, vel;
    float R = 10;

    GParticle() {
      pos = new PVector(mousePoint.x + scenter.x - iwidth / 2, mousePoint.y + scenter.y - iheight / 2);
      initialize();
    }

    GParticle(float x, float y) {
      pos = new PVector(x, y);
      initialize();
    }

    void initialize() {
      acc = new PVector(0, 0);
      rpos = new PVector(0, 0);
      vel = new PVector(mousePoint.x - lastMousePoint.x, mousePoint.y - lastMousePoint.y);
      tail = new ArrayList<PVector>();
    }

    void move() {
      pos.add(vel);
      vel.add(acc);
      vel.mult(1.0f / (friction + 1.0f));

      acc = new PVector();

      for (int i = 0; i < particles.size(); i++) {
        if (i != particles.indexOf(this)) {
          GParticle other = particles.get(i);
          float d = 1.0f + DLUtil.EuclideanDist(pos.x, pos.y, other.pos.x, other.pos.y);
          PVector dir = new PVector(other.pos.x - pos.x, other.pos.y - pos.y);
          dir.normalize();
          dir.mult(attForce / DLUtil.Pow(d, zeroQuatreVingtQuinze));
          acc.add(dir);
        }
      }

      center.x += (pos.x - center.x) / (particles.size() + 0.0);
      center.y += (pos.y - center.y) / (particles.size() + 0.0);

      tail.add(new PVector(pos.x, pos.y));

      if (tail.size() > tailLength)
        tail.remove(0);

      getDimm();
    }

    void getDimm() {
      l = DLUtil.Min(pos.x, l);
      r = DLUtil.Max(pos.x, r);
      t = DLUtil.Min(pos.y, t);
      b = DLUtil.Max(pos.y, b);
    }

    int red = 0x52;
    int green = 0x02;
    int blue = 0x6e;

    void draw(Graphics2D g) {
      Shape shp = DLUtil.Circle(pos.x, pos.y, R);
      g.setPaint(new Color(red, green, blue));
      g.fill(shp);
      rpos = new PVector(pos.x, pos.y);
    }

    void drawTail(Graphics2D g) {
      PVector p = null;
      for (int i = 0; i < tail.size(); i++) {
        PVector tmp = (PVector) tail.get(i);
        if (p != null) {
          int transparency = (int) DLUtil.Normalize(0, 255, 0, tail.size(), i);
          Line2D.Float line = new Line2D.Float(p.x, p.y, tmp.x, tmp.y);
          g.setPaint(new Color(red, green, blue, transparency));
          g.draw(line);
        }
        p = tmp;
      }
    }
  }

  class PVector {
    float x;
    float y;

    PVector() {
      this(0, 0);
    }

    PVector(float x, float y) {
      this.x = x;
      this.y = y;
    }

    void normalize() {
      float d = DLUtil.FastSqrt(x * x + y * y);
      x = x / d;
      y = y / d;
    }

    void mult(float m) {
      x = x * m;
      y = y * m;
    }

    void add(PVector v) {
      x += v.x;
      y += v.y;
    }
  }

  public void reset() {
    super.reset(true);
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
    DLMain.Main(DLGravity.class, params);
  }
}
