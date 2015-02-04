package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import com.jhlabs.image.BoxBlurFilter;

public class DLParticle extends DLImage {

  public DLParticle() {
    super();
    initMenuComponent();
    initParticles();
  }

  DLParticle(DLParticle src) {
    this();
  }

  public DLParticle(float x, float y) {
    super(x, y);
    initMenuComponent();
    initParticles();
  }

  DLParticle copy() {
    return new DLParticle(this);
  }

  void initMenuComponent() {
    float s = DLParams.MENU_ITEM_SIZE;
    int is = (int) (s + 0.5);
    DLTexture dlt = new DLTexture(s / 2, s / 2, is, is);
    dlt.imageResource = "images/30x30.png";
    dlt.image = dlt.image();
    menuComponent = dlt;
}

  boolean useD2 = false;

  public boolean getUseD2() {
    return useD2;
  }

  public void setUseD2(boolean useD2) {
    this.useD2 = useD2;
  }

  int collisionFactor = 2;

  int threadSleep = 10;

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] { 0, 1000 };
  }

  int linkFactor = 200000;

  public int getLinkFactor() {
    return linkFactor;
  }

  public void setLinkFactor(int linkFactor) {
    this.linkFactor = linkFactor;
  }

  public int[] rangeLinkFactor() {
    return new int[] { 1000, 500000 };
  }

  boolean fps = false;

  public boolean getFps() {
    return fps;
  }

  public void setFps(boolean fps) {
    this.fps = fps;
  }

  boolean displayParticles = true;

  public boolean getDisplayParticles() {
    return displayParticles;
  }

  public void setDisplayParticles(boolean displayParticles) {
    this.displayParticles = displayParticles;
  }

  float referenceMass = 500;

  public float getReferenceMass() {
    return referenceMass;
  }

  public void setReferenceMass(float referenceMass) {
    this.referenceMass = referenceMass;
  }

  public float[] rangeReferenceMass() {
    return new float[] { 1, 1000 };
  }

  boolean blurFilter = false;

  public boolean getBlurFilter() {
    return blurFilter;
  }

  public void setBlurFilter(boolean blurFilter) {
    this.blurFilter = blurFilter;
  }

  int pixelSize = 2;
  int iIncr = pixelSize;
  int jIncr = pixelSize;

  public int getPixelSize() {
    return pixelSize;
  }

  public void setPixelSize(int pixelSize) {
    this.pixelSize = pixelSize;
    iIncr = pixelSize;
    jIncr = pixelSize;
  }

  public int[] rangePixelSize() {
    return new int[] { 1, 10 };
  }

  float forceRange = 1000;

  public float getForceRange() {
    return forceRange;
  }

  public void setForceRange(float forceRange) {
    this.forceRange = forceRange;
  }

  public float[] rangeForceRange() {
    return new float[] { 1, 2000 };
  }

  boolean field = false;

  public boolean getField() {
    return field;
  }

  public void setField(boolean field) {
    this.field = field;
  }

  float G = 9.81f;

  public float getG() {
    return G;
  }

  public void setG(float g) {
    G = g;
  }

  public float[] rangeG() {
    return new float[] { 0.1f, 100 };
  }

  float deltaT = 0.2f;
  ArrayList<Boum> boums = new ArrayList<Boum>();
  ArrayList<Particle> particles = new ArrayList<Particle>();
  float boumIncrement = 3;
  boolean trajectories = false;
  int numParticles = 20;

  float minRadius = 10;
  float maxRadius = 100;
  float minDisplaySpeed = -40;
  float maxDisplaySpeed = 40;
  float minDisplayForce = -30;
  float maxDisplayForce = 30;
  int maxTrajectoryPoints = 10;

  public int getMaxTrajectoryPoints() {
    return maxTrajectoryPoints;
  }

  public void setMaxTrajectoryPoints(int maxTrajectoryPoints) {
    this.maxTrajectoryPoints = maxTrajectoryPoints;
  }

  public int[] rangeTrajectoryPoints() {
    return new int[] { 2, 100 };
  }

  public float getBoumIncrement() {
    return boumIncrement;
  }

  public void setBoumIncrement(float boumIncrement) {
    this.boumIncrement = boumIncrement;
  }

  public float[] rangeBoumIncrement() {
    return new float[] { 1, 10 };
  }

  public int getNumParticles() {
    return numParticles;
  }

  public void setNumParticles(int numParticles) {
    if (this.numParticles == numParticles)
      return;
    this.numParticles = numParticles;
    synchronized (particles) {
      boums = new ArrayList<Boum>();
      particles = new ArrayList<Particle>();
      initParticles();
    }
  }

  public int[] rangeNumParticles() {
    return new int[] { 1, 100 };
  }

  public boolean getPaintTrajectories() {
    return trajectories;
  }

  public void setPaintTrajectories(boolean paintTrajectories) {
    this.trajectories = paintTrajectories;
    cleanTrajectories();
  }

  void cleanTrajectories() {
    if (trajectories) {
      synchronized (particles) {
        for (Particle p : particles) {
          p.trajectories = true;
        }
      }
    } else {
      synchronized (particles) {
        for (Particle p : particles) {
          p.trajectory = null;
          p.trajectories = false;
        }
      }
    }
  }

  public float getDeltaT() {
    return deltaT;
  }

  public void setDeltaT(float dt) {
    deltaT = dt;
  }

  public float[] rangeDeltaT() {
    return new float[] { 0.01f, 50 };
  }

  public void paint(Graphics gr, boolean deco) {
    super.paint(gr, deco);
    paintTrajectories();
    paintParticles();
    paintBoums();
    cleanBoums();
  }

  public void f(Graphics2D g) {
    f(g, null);
  }

  long frameCount = 0;

  public void f(Graphics2D g, DLThread t) {
    long start = System.currentTimeMillis();
    long dt = 0;
    while (frameCount++ >= 0) {
      start = System.currentTimeMillis();
      if (t != null && t.isStopped())
        break;
      clearImage();
      if (step(g, t))
        break;
      clearShadow();
      paintParticles(g);
      paintBoums(g);
      paintTrajectories(g);
      cleanBoums();

      if (fps)
        paintFps(g, dt);
      if (parent != null)
        parent.paint(this);

      dt = System.currentTimeMillis() - start;
      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  float cmf = 50;
  DLColorModel colorModel1 = new DLColorModel("model1", new int[] { 0xff0000, 0xffff00, 0xffffff }, new float[] { 2000,
      1000, 0 });
  DLColorModel colorModel2 = new DLColorModel("model2", new int[] { (int) (0xfff000 * cmf), (int) (0x0ffff0 * cmf),
      (int) (0x00ffff * cmf) }, new float[] { 2000, 1000, 0 });
  DLColorModel colorModel3 = new DLColorModel("model3", new int[] { 0x000fff, 0x0ffff0, 0xfff000 }, new float[] { 2000,
      1000, 0 });
  DLColorModel colorModel4 = new DLColorModel("model4", new int[] { 0xfff000, 0x0ffff0, 0x000fff }, new float[] { 2000,
      1000, 0 });

  DLColorModel colorModel = colorModel3;

  public DLColorModel getFieldColorModel() {
    return colorModel;
  }

  public void setFieldColorModel(DLColorModel model) {
    this.colorModel = model;
  }

  public DLColorModel[] enumFieldColorModel() {
    return new DLColorModel[] { colorModel1, colorModel2, colorModel3, colorModel4 };
  }

  void field(Graphics2D g, DLThread t) {
    try {
      for (int i = 0; i < iwidth; i += iIncr) {
        for (int j = 0; j < iheight; j += jIncr) {
          if (t != null && t.isStopped())
            return;
          float fx = 0;
          float fy = 0;
          float x = DLUtil.Normalize(minPosX, maxPosX, 0, iwidth, i);
          float y = DLUtil.Normalize(minPosY, maxPosY, 0, iheight, j);
          Particle p1 = new Particle(this, x, y, 10 * referenceMass / G, 0, 0, 1, null);
          for (Particle p : particles) {
            if (t != null && t.isStopped())
              return;
            float dx = p.getX() - p1.getX();
            float dy = p.getY() - p1.getY();
            float d2 = dx * dx + dy * dy;
            // float d = (float) Math.sqrt(d2);
            float f;
            float d = DLUtil.FastSqrt(d2);
            if (this.useD2) {
              f = G * p1.mass * p.mass / d2; 
            } else {
              f = G * p1.mass * p.mass / d; // Should be d2
            }
            fx += f * dx / d;
            fy += f * dy / d;
            if (t != null && t.isStopped())
              return;
          }
          float f2 = fx * fx + fy * fy;
          float f = DLUtil.FastSqrt(f2);

          float nv = DLUtil.Normalize(0, 2000, 0, forceRange, f);
          int iv = colorModel.getColor(nv);

          Color c = new Color(iv); // , iv / 10, iv / 20);
          g.setColor(c);
          g.fillRect(i, j, iIncr, jIncr);
          if (t != null && t.isStopped())
            return;
        }
      }
      if (blurFilter) {
        BoxBlurFilter bf = new BoxBlurFilter();
        bf.setHRadius(iIncr);
        bf.setVRadius(jIncr);
        bf.setIterations(1);
        image = bf.filter(image, image);
      }
    } catch (ConcurrentModificationException e) {
      System.err.println(e);
    }
  }

  @Override
  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  @Override
  boolean mouse(MouseEvent e) {
    if (e instanceof MouseWheelEvent) {
      return false;
    }
    switch (e.getID()) {
    case MouseEvent.MOUSE_DRAGGED:
      return true;
    case MouseEvent.MOUSE_PRESSED:
      return true;
    case MouseEvent.MOUSE_RELEASED:
      return true;
    default:
      return true;
    }
  }

  void step(Graphics2D g) {
    step(g, null);
  }

  boolean step(Graphics2D g, DLThread t) {
    synchronized (particles) {
      try {
        //        for(int i = 0; i < particles.size(); i++) {
        //          Particle p1 = particles.get(i);
        //        for(int j = i + 1; j < particles.size(); j++) {
        //          Particle p2 = particles.get(j);

        for (Particle p1 : particles) {
          p1.fx = 0;
          p1.fy = 0;
          p1.linked = null;
          p1.linkedDistance = Float.MAX_VALUE;

          for (Particle p2 : particles) {
            if (p1 == p2)
              continue;
            if (t != null && t.isStopped())
              return true;
            float dx = p2.getX() - p1.getX();
            float dy = p2.getY() - p1.getY();
            //            float adx = dx > 0 ? dx : -dx;
            //            float ady = dy > 0 ? dy : -dy;

            float d2 = dx * dx + dy * dy;

            float d = DLUtil.FastSqrt(d2);
            float r = collisionFactor * (p1.radius + p2.radius);
            if (d < r) {
              Boum b = boum(p1, p2);
              //System.err.println("Boum");
              synchronized (boums) {
                boums.add(b);
              }
            }

            if (d < (linkFactor)) {
              // System.err.println(linkFactor * r);              
              link(p1, p2, d);
            }

            float f = G * p1.mass * p2.mass / d; // Should be d2
            float fx = f * dx / d;
            float fy = f * dy / d;

            p1.fx += fx;
            p1.fy += fy;

            float nvx = fx * deltaT / p1.mass;
            float nvy = fy * deltaT / p1.mass;

            p1.nvx += nvx;
            p1.nvy += nvy;

            if (t != null && t.isStopped())
              return true;
          }
        }

        for (Particle p : particles) {
          if (t != null && t.isStopped())
            return true;

          float nvx = p.nvx + p.vx;
          float nvy = p.nvy + p.vy;
          float nx = p.getX() + nvx * deltaT;
          float ny = p.getY() + nvy * deltaT;
          float x = nx;
          float y = ny;

          float vx = nvx;
          float vy = nvy;

          if (t != null && t.isStopped())
            return true;

          if (mode == BOUNCE) {
            if (x < minPosX) {
              x = 2 * minPosX - x;
              vx = -vx;
            } else if (x > maxPosX) {
              x = 2 * maxPosX - x;
              vx = -vx;
            }

            if (y < minPosY) {
              y = 2 * minPosY - y;
              vy = -vy;
            } else if (y > maxPosY) {
              y = 2 * maxPosY - y;
              vy = -vy;
            }
          } else if (mode == TORIC) {
            if (x < minPosX) {
              x = maxPosX - (minPosX - x);
            }
            if (x > maxPosX) {
              x = minPosX + x - maxPosX;
            }
            if (y < minPosY) {
              y = maxPosY - (minPosY - y);
            }
            if (y > maxPosY) {
              y = minPosY + y - maxPosY;
            }
          } else if (mode == NONE) {
            // bye
          }
          p.moveTo(x, y);
          p.vx = vx;
          p.vy = vy;
          p.nvx = 0;
          p.nvy = 0;

          if (t != null && t.isStopped())
            return true;
        }

      } catch (ConcurrentModificationException e) {
        System.err.print(e);
      }
      if (field)
        field(g, t);
    }
    return false;
  }

  static final String BOUNCE = "bounce";
  static final String TORIC = "toric";
  static final String NONE = "none";
  String mode = BOUNCE;

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String[] enumMode() {
    return new String[] { BOUNCE, TORIC, NONE };
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 400);
    iheight = iwidth;
  }

  float minMass = 100;
  float maxMass = 1000;
  float minSpeed = -500;
  float maxSpeed = 500;
  float minForce = -500;
  float maxForce = 500;

  float minPosX = -100000;
  float maxPosX = 100000;
  float minPosY = -100000;
  float maxPosY = 100000;

  public float getMinPosX() {
    return minPosX;
  }

  public void setMinPosX(float minPosX) {
    this.minPosX = minPosX;
  }

  public float[] rangeMinPosX() {
    return new float[] { -1000000, 0 };
  }

  public float getMaxPosX() {
    return maxPosX;
  }

  public void setMaxPosX(float maxPosX) {
    this.maxPosX = maxPosX;
  }

  public float[] rangeMaxPosX() {
    return new float[] { 0, 1000000 };
  }

  public float getMinPosY() {
    return minPosY;
  }

  public void setMinPosY(float minPosY) {
    this.minPosY = minPosY;
  }

  public float[] rangeMinPosY() {
    return new float[] { -1000000, 0 };
  }

  public float getMaxPosY() {
    return maxPosY;
  }

  public void setMaxPosY(float maxPosY) {
    this.maxPosY = maxPosY;
  }

  public float[] rangeMaxPosY() {
    return new float[] { 0, 1000000 };
  }

  void initParticles() {
    for (int i = 0; i < numParticles; i++) {
      float mass = DLUtil.RangeRandom(minMass, maxMass);
      float vx = DLUtil.RangeRandom(minSpeed, maxSpeed);
      float vy = DLUtil.RangeRandom(minSpeed, maxSpeed);
      float radius = DLUtil.RangeRandom(minRadius, maxRadius);
      float x = DLUtil.RangeRandom(minPosX, maxPosX);
      float y = DLUtil.RangeRandom(minPosY, maxPosY);
      Color color = DLUtil.RandomColor(0f, 1f, 0.5f, 1f, 0.6f, 1f);
      Particle p = new Particle(this, x, y, mass, vx, vy, radius, color);
      particles.add(p);
    }
  }

  void paintFps(Graphics2D g, long frameTime) {
    if (frameTime == 0)
      return;
    NumberFormat nf = new DecimalFormat("####.##");
    String s = nf.format(1000. / frameTime);
    Font f = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    FontMetrics metrics = g.getFontMetrics(f);

    int w = metrics.stringWidth(s);
    int descent = metrics.getDescent();
    g.setColor(Color.darkGray);
    g.drawString(s, iwidth - w - 5, iheight - descent);

    s = "" + frameCount;
    g.drawString(s, 5, iheight - descent);
  }

  void paintTrajectories() {
    if (image == null)
      image = image();
    paintTrajectories(image.createGraphics());
  }

  void paintTrajectories(Graphics2D g) {
    if (trajectories)
      for (Particle p : particles)
        paintTrajectory(g, p);
  }

  void paintTrajectory(Graphics2D g, Particle part) {
    Color c = part.color;
    c = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 3);
    g.setColor(c);
    Path2D.Float path = new Path2D.Float();
    if (part.trajectory != null)
      synchronized (part.trajectory) {
        for (DLPoint p : part.trajectory) {
          float x = DLUtil.Normalize(0, iwidth, minPosX, maxPosX, p.x);
          float y = DLUtil.Normalize(0, iheight, minPosY, maxPosY, p.y);
          float dx2 = iwidth;
          float dy2 = iheight;
          float d = dx2 * dx2 + dy2 * dy2;
          path = DLUtil.AddPoint(x, y, path, 25, d / 4);
        }
      }
    g.draw(path);
  }

  void paintP(Graphics2D g, Particle p, float x, float y) {
    float w = DLUtil.fastLog(p.mass) / DLUtil.Log3;
    Shape pe = DLUtil.Polygon(x, y, 5, w, DLUtil.RangeRandom(0, DLUtil.TWO_PI));
    g.setColor(DLUtil.BrighterColor(p.color, 0.8));
    g.fill(pe);
    g.setColor(DLUtil.BrighterColor(p.color, 1.2));
    g.draw(pe);
  }

  void paintV(Graphics2D g, Particle p, float x, float y) {
    float vx = DLUtil.Normalize(minDisplaySpeed, maxDisplaySpeed, minSpeed, maxSpeed, p.vx);
    float nx = x + vx;
    float vy = DLUtil.Normalize(minDisplaySpeed, maxDisplaySpeed, minSpeed, maxSpeed, p.vy);
    float ny = y + vy;

    Line2D.Float lv = new Line2D.Float(x, y, nx, ny);
    Point2D.Float p1 = new Point2D.Float(x, y);
    Point2D.Float p2 = new Point2D.Float(nx, ny);
    Point2D.Float[] r = DLUtil.orthopoints(p1, p2, 8, 3);
    Path2D.Float pe1 = new Path2D.Float();
    pe1.moveTo(r[0].x, r[0].y);
    pe1.lineTo(nx, ny);
    pe1.lineTo(r[1].x, r[1].y);
    pe1.closePath();

    g.setColor(p.color);
    g.draw(lv);
    g.fill(pe1);
  }

  void paintF(Graphics2D g, Particle p, float x, float y) {
    float fx = DLUtil.Normalize(minDisplayForce, maxDisplayForce, minForce, maxForce, p.fx);
    float nx = x + (10 * fx) / G;
    float fy = DLUtil.Normalize(minDisplayForce, maxDisplayForce, minForce, maxForce, p.fy);
    float ny = y + (10 * fy) / G;

    Line2D.Float lf = new Line2D.Float(x, y, nx, ny);

    float dx = nx - x;
    float dy = ny - y;
    float d2 = dx * dx + dy * dy;
    float d = DLUtil.FastSqrt(d2);
    float s = d != 0 ? DLUtil.fastLog(d) / DLUtil.Log2 : 1;

    Shape fe = DLUtil.Polygon(nx, ny, 3, s, DLUtil.RangeRandom(0, DLUtil.TWO_PI));

    Point2D.Float p1 = new Point2D.Float(x, y);
    Point2D.Float p2 = new Point2D.Float(nx, ny);
    Point2D.Float[] r = DLUtil.orthopoints(p1, p2, 5, 5);

    s = s / 2f;
    Shape ne3 = DLUtil.Polygon(r[0], 5, s, 0);
    Shape ne4 = DLUtil.Polygon(r[1], 5, s, 0);

    g.setColor(DLUtil.BrighterColor(p.color, 1.2));
    g.draw(lf);
    g.fill(ne3);
    g.fill(ne4);
    g.setColor(p.color);
    g.fill(fe);
  }

  static final String LinkDoubleCurve = "double curve";
  static final String LinkDoublePolyline = "double poly";
  static final String LinkPolyline = "electric";
  static final String LinkLine = "line";

  String linkType = LinkPolyline;

  public String getLinkType() {
    return linkType;
  }

  public void setLinkType(String linkType) {
    this.linkType = linkType;
  }

  public String[] enumLinkType() {
    return new String[] { LinkDoubleCurve, LinkDoublePolyline, LinkPolyline, LinkLine };
  }

  public int getElectricCount() {
    return electricCount;
  }

  public void setElectricCount(int electricCount) {
    this.electricCount = electricCount;
  }

  public int[] rangeElectricCount() {
    return new int[] { 1, 5 };
  }

  public float getElectricMove() {
    return electricMove;
  }

  public void setElectricMove(float electricMove) {
    this.electricMove = electricMove;
  }

  public float[] rangeElectricMove() {
    return new float[] { 0, 50 };
  }

  public int getElectricPoints() {
    return electricPoints;
  }

  public void setElectricPoints(int electricPoints) {
    this.electricPoints = electricPoints;
  }

  public int[] rangeElectricPoints() {
    return new int[] { 2, 50 };
  }

  public String getElectricStyle() {
    return electricStyle;
  }

  public void setElectricStyle(String electricStyle) {
    this.electricStyle = electricStyle;
  }

  public String[] enumElectricStyle() {
    return new String[] { ElectricStyleOne, ElectricStyleTwo, ElectricStyleThree, ElectricStyleFour };
  }

  int electricCount = 1;
  float electricMove = 7;
  int electricPoints = 10;
  String electricStyle = ElectricStyleOne;

  static final String ElectricStyleOne = "electric 1";
  static final String ElectricStyleTwo = "electric 2";
  static final String ElectricStyleThree = "electric 3";
  static final String ElectricStyleFour = "electric 4";

  void paintL(Graphics2D g, Particle p, float x, float y) {
    Particle l = p.linked;
    if (l == null)
      return;

    float px = l.getX();
    float lx = DLUtil.Normalize(0, iwidth, minPosX, maxPosX, px);
    float py = l.getY();
    float ly = DLUtil.Normalize(0, iheight, minPosY, maxPosY, py);

    float rp = DLUtil.fastLog(p.mass) / DLUtil.Log3;
    float rl = DLUtil.fastLog(l.mass) / DLUtil.Log3;

    float dx = lx - x;
    float dy = ly - y;
    float ld = DLUtil.FastSqrt((float) (dx * dx + dy * dy));

    //    path1.moveTo(start[0].x, start[0].y);
    if (LinkDoubleCurve.equals(linkType)) {
      Path2D.Float path1 = new Path2D.Float();
      Path2D.Float path2 = new Path2D.Float();

      Point2D.Float start[] = DLUtil.orthopoints(lx, ly, x, y, 0, rp);
      Point2D.Float mid[] = DLUtil.orthopoints(lx, ly, x, y, ld / 2, 2 /*-5*/); //(r1 + r2) / 4);
      Point2D.Float end[] = DLUtil.orthopoints(lx, ly, x, y, ld, rl);

      path1.moveTo(start[1].x, start[1].y);
      path1.lineTo(mid[1].x, mid[1].y);
      path1.lineTo(end[1].x, end[1].y);

      path2.moveTo(end[0].x, end[0].y);
      path2.lineTo(mid[0].x, mid[0].y);
      path2.lineTo(start[0].x, start[0].y);

      Color pc = p.color;
      Color c = new Color(255 - pc.getRed(), 255 - pc.getGreen(), 255 - pc.getBlue());
      g.setColor(c);
      g.draw(path1);
      g.draw(path2);

    } else if (LinkDoublePolyline.equals(linkType)) {
      Path2D.Float path1 = new Path2D.Float();
      Path2D.Float path2 = new Path2D.Float();

      Point2D.Float start[] = DLUtil.orthopoints(lx, ly, x, y, 0, rp);
      Point2D.Float mid[] = DLUtil.orthopoints(lx, ly, x, y, ld / 2, 2 /*-5*/); //(r1 + r2) / 4);
      Point2D.Float end[] = DLUtil.orthopoints(lx, ly, x, y, ld, rl);

      path1.moveTo(start[1].x, start[1].y);
      path1.curveTo(start[1].x, start[1].y, mid[1].x, mid[1].y, end[1].x, end[1].y);

      path2.moveTo(end[0].x, end[0].y);
      path2.curveTo(end[0].x, end[0].y, mid[0].x, mid[0].y, start[0].x, start[0].y);

      Color pc = p.color;
      Color c = new Color(255 - pc.getRed(), 255 - pc.getGreen(), 255 - pc.getBlue());
      g.setColor(c);
      g.draw(path1);
      g.draw(path2);
    } else if (LinkLine.equals(linkType)) {
      Path2D.Float path = new Path2D.Float();
      path.moveTo(lx, ly);
      path.lineTo(x, y);
      Color pc = p.color;
      Color c = new Color(255 - pc.getRed(), 255 - pc.getGreen(), 255 - pc.getBlue());
      g.setColor(c);
      g.draw(path);
    } else if (LinkPolyline.equals(linkType)) {

      Path2D.Float path = new Path2D.Float();
      int count = electricCount;

      while (count-- > 0) {
        int np = electricPoints;

        float xs[] = new float[np + 2];
        float ys[] = new float[np + 2];
        int xyIndex = 0;
        xs[xyIndex] = x;
        ys[xyIndex] = y;
        xyIndex++;

        float ix = x;
        float iy = y;

        while (true) {
          ix += dx / np;
          iy += dy / np;
          if (dx > 0) {
            if (dy > 0) {
              if (!((ix < x + dx) && (iy < y + dy)))
                break;
            } else {
              if (!((ix < x + dx) && (iy > y + dy)))
                break;
            }
          } else {
            if (dy > 0) {
              if (!((ix > x + dx) && (iy < y + dy)))
                break;
            } else {
              if (!((ix > x + dx) && (iy > y + dy)))
                break;
            }
          }
          xs[xyIndex] = ix;
          ys[xyIndex] = iy;
          xyIndex++;
        }

        xs[xyIndex] = lx;
        ys[xyIndex] = ly;

        for (int i = 0; i < np + 1; i++) {
          float k = (i - np / 2);
          k = k > 0 ? k : -k;
          float f = 2 * (np - k) / np - 1;
          //          System.err.println(i + " " + f);
          float ef = electricMove * f;
          float r = DLUtil.RangeRandom(-ef, ef);
          xs[i] = xs[i] + r;
          r = DLUtil.RangeRandom(-ef, ef);
          ys[i] = ys[i] + r;

          if (i == 0) {
            path.moveTo(xs[i], ys[i]);
          } else {
            path.lineTo(xs[i], ys[i]);
          }
        }

      }
      Point2D.Float op[] = DLUtil.orthopoints(lx, ly, x, y, ld / 2, electricMove / 2);
      Color wc = new Color(0xcc, 0xcc, 0xcc); //0xff, 0xf0, 0xf0, 0xff);

      LinearGradientPaint gp = null;
      Color b1 = new Color(0x2C75FF);
      Color b2 = new Color(0x0131B4);
      if (ElectricStyleOne.equals(electricStyle)) {
        gp = new LinearGradientPaint(op[0], op[1], new float[] { 0, 0.5f, 1 }, new Color[] { wc, b1, wc });
      } else if (ElectricStyleTwo.equals(electricStyle)) {
        gp = new LinearGradientPaint(op[0], op[1], new float[] { 0, 0.5f, 1 }, new Color[] { b1, wc, b1 });
      } else if (ElectricStyleThree.equals(electricStyle)) {
        gp = new LinearGradientPaint(new Point2D.Float(x, y), new Point2D.Float(lx, ly), new float[] { 0, 0.5f, 1 },
            new Color[] { wc, b2, wc }); // 2C75FF
      } else if (ElectricStyleFour.equals(electricStyle)) {
        gp = new LinearGradientPaint(new Point2D.Float(x, y), new Point2D.Float(lx, ly), new float[] { 0, 0.5f, 1 },
            new Color[] { b2, wc, b2 }); // 2C75FF
      }
      if (gp != null)
        g.setPaint(gp);
      else
        g.setColor(Color.black);
      g.draw(path);

    }

  }

  boolean paintLinks = true;
  boolean paintVelocity = true;
  boolean paintForce = true;
  boolean paintParticles = true;

  public boolean getPaintLinks() {
    return paintLinks;
  }

  public void setPaintLinks(boolean paintLinks) {
    this.paintLinks = paintLinks;
  }

  public boolean getPaintVelocity() {
    return paintVelocity;
  }

  public void setPaintVelocity(boolean paintVelocity) {
    this.paintVelocity = paintVelocity;
  }

  public boolean getPaintForce() {
    return paintForce;
  }

  public void setPaintForce(boolean paintForce) {
    this.paintForce = paintForce;
  }

  public boolean getPaintParticles() {
    return paintParticles;
  }

  public void setPaintParticles(boolean paintParticles) {
    this.paintParticles = paintParticles;
  }

  void paintParticle(Graphics2D g, Particle p) {
    float px = p.getX();
    float x = DLUtil.Normalize(0, iwidth, minPosX, maxPosX, px);
    float py = p.getY();
    float y = DLUtil.Normalize(0, iheight, minPosY, maxPosY, py);

    if (paintLinks)
      paintL(g, p, x, y);
    if (paintVelocity)
      paintV(g, p, x, y);
    if (paintForce)
      paintF(g, p, x, y);
    if (paintParticles)
      paintP(g, p, x, y);
  }

  void paintParticles() {
    if (image == null)
      image = image();
    Graphics2D g = image.createGraphics();
    DLUtil.SetHints(g);
    paintParticles(g);
  }

  void paintParticles(Graphics2D g) {
    synchronized (particles) {
      if (displayParticles)
        try {
          for (Particle p : particles)
            paintParticle(g, p);
        } catch (ConcurrentModificationException e) {
          System.err.println(e);
        }
    }
  }

  void paintBoums() {
    if (image == null)
      image = image();
    paintBoums(image.createGraphics());
  }

  void paintBoums(Graphics2D g) {
    synchronized (boums) {
      for (Boum b : boums) {
        paintBoum(g, b);
      }
    }
  }

  void cleanBoums() {
    ArrayList<Boum> toRemove = new ArrayList<Boum>();
    synchronized (boums) {
      for (Boum b : boums) {
        if (b.radius > b.maxRadius) {
          toRemove.add(b);
        }
      }
      for (Boum b : toRemove) {
        if (!boums.remove(b))
          System.err.println("Did not remove " + b);
      }
    }
  }

  void paintBoum(Graphics2D g, Boum b) {
    Shape e = DLUtil.Circle(b.x, b.y, b.radius);
    g.setColor(b.color);
    g.draw(e);
    b.radius += boumIncrement;
    if (b.radius > b.maxRadius)
      return;
    float f = 1 - b.radius / b.maxRadius;
    int k = (int) (255 * f);
    if (k > 255)
      k = 255;
    if (k < 0)
      k = 0;
    Color c = b.color;
    b.color = new Color(c.getRed(), c.getBlue(), c.getGreen(), k);
  }

  void link(Particle p1, Particle p2, float d) {
    Particle l = p1.linked;
    if (l != null) {
      if (l == p2) {
        System.err.println("link Error 1");
        return;
      }
    }
    l = p2.linked;
    if (l == p1)
      return;
    if (d < p1.linkedDistance) {
      p1.linked = p2;
      p1.linkedDistance = d;
    }
  }

  Boum boum(Particle p1, Particle p2) {
    float mass = p1.mass + p2.mass;
    float vx = p1.vx + p2.vx;
    float vy = p1.vy + p2.vy;
    float m1 = DLUtil.RangeRandom(10, mass);
    float m2 = mass - m1;
    float v1x = DLUtil.RangeRandom(-vx, vx);
    float v2x = vx - v1x;
    float v1y = DLUtil.RangeRandom(-vy, vy);
    float v2y = vy - v1y;

    p1.mass = m1;
    p1.vx = v1x;
    p1.vy = v1y;

    p2.mass = m2;
    p2.vx = v2x;
    p2.vy = v2y;

    float x = (p1.getX() + p2.getX()) / 2;
    float y = (p1.getY() + p2.getY()) / 2;

    float d = 0;
    do {
      p1.avance(deltaT);
      p2.avance(deltaT);
      float dx = p2.getX() - p1.getX();
      float dy = p2.getY() - p1.getY();
      float d2 = dx * dx + dy * dy;
      // d = (float) Math.sqrt(d2);
      d = DLUtil.FastSqrt(d2);
    } while (d < collisionFactor * (p1.radius + p2.radius));

    x = DLUtil.Normalize(0, iwidth, minPosX, maxPosX, x);
    y = DLUtil.Normalize(0, iheight, minPosY, maxPosY, y);
    return GetBoum(p1, p2, x, y);
  }

  static HashMap<BoumKey, Boum> boumList = new HashMap<BoumKey, Boum>();

  static Boum GetBoum(Particle p1, Particle p2, float x, float y) {
    BoumKey b = new BoumKey(p1, p2);
    Boum boum = boumList.get(b);
    if (boum == null) {
      boum = new Boum(x, y);
      boumList.put(b, boum);
    } else {
      // System.err.println("retrieved " + boum);
    }
    return boum;
  }

  static Boum RemoveBoum(Particle p1, Particle p2) {
    BoumKey b = new BoumKey(p1, p2);
    Boum boum = boumList.remove(b);
    if (boum == null) {
      System.err.println("Can't remove boum from key " + b);
    }
    return boum;
  }

}

class BoumKey {
  Particle p1;
  Particle p2;

  BoumKey(Particle p1, Particle p2) {
    this.p1 = p1;
    this.p2 = p2;
  }

  public boolean equals(Object op) {
    final BoumKey p = (BoumKey) op;
    return p1.equals(p.p1) && p1.equals(p.p1);
  }

  public int hashCode() {
    return p1.hashCode() + p2.hashCode();
  }
}

class Particle {
  private float x;
  private float y;
  float mass;
  float vx;
  float vy;
  float fx;
  float fy;
  float radius;
  float nvx;
  float nvy;
  Color color;
  boolean trajectories = false;
  ArrayList<DLPoint> trajectory = null;
  DLParticle particleSystem;
  Particle linked;
  float linkedDistance;

  Particle(DLParticle particleSystem, float x, float y, float mass, float vx, float vy, float r, Color c) {
    this.particleSystem = particleSystem;
    this.x = x;
    this.y = y;
    this.mass = mass;
    this.vx = vx;
    this.vy = vy;
    this.radius = r;
    this.color = c;
  }

  float getX() {
    return x;
  }

  float getY() {
    return y;
  }

  void moveTo(float x, float y) {
    this.x = x;
    this.y = y;
    if (trajectories)
      addTrajectoryPoint(x, y);
  }

  void avance(float dt) {
    x = x + vx * dt;
    y = y + vy * dt;
    if (trajectories)
      addTrajectoryPoint(x, y);
  }

  void addTrajectoryPoint(float x, float y) {
    if (!trajectories)
      return;
    if (trajectory == null)
      trajectory = new ArrayList<DLPoint>();
    DLPoint dlp = new DLPoint(x, y, System.currentTimeMillis());

    int s = trajectory.size();
    if (s > 1) {
      DLPoint lp = trajectory.get(s - 1);
      double dx = x - lp.x;
      double dy = y - lp.y;
      double d = dx * dx + dy * dy;
      if (d < 1000 * 1000 * 10)
        return;
    }
    trajectory.add(dlp);
    while (trajectory.size() >= particleSystem.maxTrajectoryPoints)
      trajectory.remove(0);
  }

  public boolean equals(Object o) {
    Particle p = (Particle) o;
    int c;
    c = Float.compare(mass, p.mass);
    if (c != 0)
      return false;
    c = Float.compare(radius, p.radius);
    if (c != 0)
      return false;
    if (!color.equals(p.color))
      return false;
    return true;
  }

  public int hashCode() {
    int ret = 0;
    ret += Float.valueOf(mass).hashCode();
    ret += Float.valueOf(radius).hashCode();
    ret += color.hashCode();
    return ret;
  }
}

class Boum {
  float x;
  float y;
  float radius = 10;
  float maxRadius = 100;
  Color color = new Color(0xA91101); // Color.red;

  Boum(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public boolean equals(Object a) {
    Boum b = (Boum) a;
    int c;
    c = Float.compare(this.radius, b.radius);
    if (c != 0)
      return false;
    c = Float.compare(this.maxRadius, b.maxRadius);
    if (c != 0)
      return false;
    if (!this.color.equals(b.color))
      return false;
    return true;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public int hashCode() {
    int ret = 0;
    ret += Float.valueOf(radius).hashCode();
    ret += Float.valueOf(maxRadius).hashCode();
    ret += color.hashCode();
    return ret;
  }
}
