package com.mdu.DrawLine;

import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON2;
import static java.awt.event.MouseEvent.BUTTON3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jhlabs.image.BoxBlurFilter;

public class DLParticles extends DLPointImage {
  List<P> particles = Collections.synchronizedList(new ArrayList<P>());
  List<G> gravitons = Collections.synchronizedList(new ArrayList<G>());
  List<E> emiters = Collections.synchronizedList(new ArrayList<E>());
  boolean fps = true;
  float deltaT = 0.5f;
  int threadSleep = 10;
  long frameCount = 0;
  boolean emit = false;
  boolean paintGraviton = true;
  boolean paintParticles = true;
  boolean paintEmiters = true;
  boolean paintTrajectories = true;
  String mode = NONE;
  float partColor = DLUtil.RangeRandom(0, 1f);
  float colVariance = 0.3f;
  int margin = 15;
  boolean paintMargin = false;
  float blurRadius = 0f;

  float trajectoryPointSize = 1f;

  static final String SEGMENT = "segments";
  static final String POINTS = "points";

  String trajectories = SEGMENT;

  static final String BOUNCE = "bounce";
  static final String TORIC = "toric";
  static final String NONE = "none";

  float minMass = 100;
  float maxMass = 1000;
  float minSpeed = -500;
  float maxSpeed = 500;
  float minGraviton = -500;
  float maxGraviton = 500;

  float minPosX = -100000;
  float maxPosX = 100000;
  float minPosY = -100000;
  float maxPosY = 100000;

  float minIntensity = -5000;
  float maxIntensity = 5000;

  int numInitParticles = 1000;

  int numInitGravitons = 3;
  int numInitEmiters = 3;
  int maxTrajectoryPoints = 5;
  float TrajectableProba = 0.95f;
  float trajectoryStep = 50f * DLUtil.FastSqrt((maxPosX - minPosX) * (maxPosX - minPosX) + (maxPosY - minPosY)
      * (maxPosY - minPosY));
  int salve = 3;
  int emitDelay = 50;

  public int getSalve() {
    return salve;
  }

  public void setSalve(int salve) {
    this.salve = salve;
    for (E e : emiters)
      e.salve = salve;
  }

  public int[] rangeSalve() {
    return new int[] { 2, 10 };
  }

  public int getEmitDelay() {
    return emitDelay;
  }

  public void setEmitDelay(int emitDelay) {
    this.emitDelay = emitDelay;
    for (E e : emiters)
      e.emitDelay = emitDelay;
  }

  public int[] rangeEmiteDelay() {
    return new int[] { 5, 200 };
  }

  public float getTrajectoryPointSize() {
    return trajectoryPointSize;
  }

  public void setTrajectoryPointSize(float trajectoryPointSize) {
    this.trajectoryPointSize = trajectoryPointSize;
  }

  public float[] rangeTrajectoryPointSize() {
    return new float[] { 1, 20 };
  }

  public float getBlurRadius() {
    return blurRadius;
  }

  public void setBlurRadius(float blurRadius) {
    this.blurRadius = blurRadius;
  }

  public float[] rangeBlurRadius() {
    return new float[] { 0, 10 };
  }

  public float getTrajectoryStep() {
    return trajectoryStep;
  }

  public void setTrajectoryStep(float trajectoryStep) {
    this.trajectoryStep = trajectoryStep;
  }

  public float[] rangeTrajectoryStep() {
    float d = DLUtil.FastSqrt((maxPosX - minPosX) * (maxPosX - minPosX) + (maxPosY - minPosY) * (maxPosY - minPosY));
    return new float[] { d / 100f, d * 100f };
  }

  public float getTrajectableProba() {
    return TrajectableProba;
  }

  public void setTrajectableProba(float trajectableProba) {
    TrajectableProba = trajectableProba;
  }

  public float[] rangeTrajectableProba() {
    return new float[] { 0, 1 };
  }

  public float getminIntensity() {
    return minIntensity;
  }

  public void setminIntensity(float minIntensity) {
    if (minIntensity == minIntensity)
      return;
    this.minIntensity = minIntensity;
    redoGravitons();
  }

  public float[] rangemaxIntensity() {
    return new float[] { 0, 10000 };
  }

  public int getnumInitGravitons() {
    return numInitGravitons;
  }

  public void setnumInitGravitons(int numInitGravitons) {
    this.numInitGravitons = numInitGravitons;
    synchronized (gravitons) {
      while (gravitons.size() > numInitGravitons)
        gravitons.remove(0);
      while (gravitons.size() < numInitGravitons)
        gravitons.add(createGraviton());
    }
  }

  public int getnumInitParticles() {
    return numInitParticles;
  }

  public void setnumInitParticles(int numInitParticles) {
    this.numInitParticles = numInitParticles;
    synchronized (particles) {
      while (particles.size() > numInitParticles)
        particles.remove(0);
      while (particles.size() < numInitParticles)
        particles.add(createParticle());
    }
  }

  public int[] rangeInitParticle() {
    return new int[] { 1, 10000 };
  }

  public int getnumInitEmiters() {
    return numInitEmiters;
  }

  public void setnumInitEmiters(int numInitEmiters) {
    this.numInitEmiters = numInitEmiters;
    synchronized (emiters) {
      while (emiters.size() > numInitEmiters)
        emiters.remove(0);
      while (emiters.size() < numInitEmiters)
        emiters.add(createEmiter());
    }
  }

  G createGraviton() {
    float x = DLUtil.RangeRandom(minPosX, maxPosX);
    float y = DLUtil.RangeRandom(minPosY, maxPosY);
    float intensity = DLUtil.RangeRandom(minIntensity, maxIntensity);
    G f = new G(x, y, intensity);
    return f;
  }

  void initGravitons() {
    synchronized (gravitons) {
      for (int i = 0; i < numInitGravitons; i++) {
        gravitons.add(createGraviton());
      }
    }
  }

  void redoGravitons() {
    synchronized (gravitons) {
      for (G g : gravitons) {
        float intensity = DLUtil.RangeRandom(minIntensity, maxIntensity);
        g.intensity = intensity;
      }
    }
  }

  public float getmaxIntensity() {
    return maxIntensity;
  }

  public void setmaxIntensity(float maxIntensity) {
    this.maxIntensity = maxIntensity;
    redoGravitons();
  }

  public float[] rangeminIntensity() {
    return new float[] { -10000, 0 };
  }

  public DLParticles() {
    super();
    initParticles();
    initGravitons();
  }

  DLParticles(DLParticles src) {
    this();
  }

  public DLParticles(float x, float y) {
    super(x, y);
    initParticles();
    initGravitons();
    initEmiters();
  }

  DLParticles copy() {
    return new DLParticles(this);
  }

  public float getDeltaT() {
    return deltaT;
  }

  public void setDeltaT(float dt) {
    deltaT = dt;
  }

  public float[] rangeDeltaT() {
    return new float[] { 0.01f, 5 };
  }

  public String getTrajectories() {
    return trajectories;
  }

  public void setTrajectories(String trajectories) {
    this.trajectories = trajectories;
  }

  public String[] enumTrajectories() {
    return new String[] { SEGMENT, POINTS };
  }

  void removeOldParticles() {
    ArrayList<P> tmp = new ArrayList<P>();

    synchronized (particles) {
      for (P p : particles)
        if (p.age-- <= 0)
          tmp.add(p);

      for (P p : tmp)
        particles.remove(p);
    }
  }

  public void f(Graphics2D g, DLThread t) {
    long start = System.currentTimeMillis();
    long dt = 0;
    while (frameCount++ >= 0) {
      start = System.currentTimeMillis();
      if (t != null && t.isStopped())
        break;

      step(g, t);

      clearImage();

      paintBefore(g);

      if (blurRadius > 0) {
        BoxBlurFilter bf = new BoxBlurFilter();
        bf.setRadius(blurRadius);
        bf.filter(image, image);
      }

      paintAfter(g, dt);

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

  @Override
  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  PosObject getPosObject(float x, float y) {
    G g = getGraviton(x, y);
    if (g != null)
      return g;
    E e = getEmiter(x, y);
    if (e != null)
      return e;
    return null;
  }

  float selectMargin = 4;

  G getGraviton(float x, float y) {
    synchronized (gravitons) {
      for (G g : gravitons) {
        Shape s = g.shape;
        if (s != null) {
          Rectangle2D r = DLUtil.getBounds(s, selectMargin);
          if (r.contains(x, y)) {
            return g;
          }
        }
      }
    }
    return null;
  }

  E getEmiter(float x, float y) {
    synchronized (emiters) {
      for (E e : emiters) {
        Shape s = e.shape;
        if (s != null) {
          Rectangle2D r = DLUtil.getBounds(s, selectMargin);
          if (r.contains(x, y))
            return e;
        }
      }
    }
    return null;
  }

  PosObject posObject;

  void delete(PosObject o) {
    if (o instanceof E) {
      E e = (E) o;
      e.setEmit(false);
      synchronized (emiters) {
        emiters.remove(e);
      }
    }
    if (o instanceof G) {
      G g = (G) o;
      synchronized (gravitons) {
        gravitons.remove(g);
      }
    }
  }

  boolean mouse(MouseEvent e) {
    Point p = e.getPoint();
    float ix = p.x - (this.x - iwidth / 2);
    float iy = p.y - (this.y - iheight / 2);
    float x = DLUtil.Normalize(minPosX, maxPosX, 0, iwidth, ix);
    float y = DLUtil.Normalize(minPosY, maxPosY, 0, iheight, iy);
    if (e instanceof MouseWheelEvent) {
      return false;
    }
    switch (e.getID()) {
    case MouseEvent.MOUSE_MOVED: {
      PosObject o = getPosObject(ix, iy);
      if (o != null)
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      else
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      return true;
    }
    case MouseEvent.MOUSE_DRAGGED: {
      if (posObject != null) {
        posObject.moveTo(x, y);
        boolean delete = ix < margin || ix > (iwidth - margin) || iy < margin || iy > (iheight - margin);
        posObject.setMarkDelete(delete);
        setPaintMargin(delete);
      }
      return true;
    }
    case MouseEvent.MOUSE_PRESSED: {
      PosObject g = getPosObject(ix, iy);
      if (g != null) {
        posObject = g;
      } else {
        if (e.isControlDown()) {
          E emiter = new E(this, x, y);
          synchronized (emiters) {
            emiters.add(emiter);
            emiter.setEmit(true);
          }
        } else if (e.isShiftDown()) {
          float i = DLUtil.RangeRandom(1, 5 * maxIntensity);
          G gr = new G(x, y, i);
          posObject = gr;

          synchronized (gravitons) {
            gravitons.add(gr);
          }

        } else if (e.isAltDown() || e.isMetaDown()) {
          float i = DLUtil.RangeRandom(5 * minIntensity, 1);
          G gr = new G(x, y, i);
          posObject = gr;

          synchronized (gravitons) {
            gravitons.add(gr);
          }

        }
      }
      return true;
    }
    case MouseEvent.MOUSE_RELEASED:
      if (posObject != null) {
        if (ix < margin || ix > (iwidth - margin) || iy < margin || iy > (iheight - margin)) {
          delete(posObject);
          posObject = null;
        }
      }
      setPaintMargin(false);
      return true;
    default:
      return true;
    }

  }

  void step(Graphics2D g) {
    step(g, null);
  }

  boolean step(Graphics2D g, DLThread t) {

    try {

      ArrayList<P> toRemove = null;

      synchronized (particles) {
        for (P p : particles) {
          float fx = 0;
          float fy = 0;
          float nvx = 0;
          float nvy = 0;

          synchronized (gravitons) {
            for (G fo : gravitons) {
              float dx = p.x - fo.x;
              float dy = p.y - fo.y;
              float d2 = dx * dx + dy * dy;
              float d = DLUtil.FastSqrt(d2);
              float f = fo.intensity;

              fx += f * dx / d;
              fy += f * dy / d;

              float dvx = fx * deltaT / p.mass;
              float dvy = fy * deltaT / p.mass;

              nvx += dvx;
              nvy += dvy;
            }
          }
          if (t != null && t.isStopped())
            return true;

          nvx = nvx + p.vx;
          nvy = nvy + p.vy;
          float nx = p.x + nvx * deltaT;
          float ny = p.y + nvy * deltaT;

          if (t != null && t.isStopped())
            return true;

          if (BOUNCE.equals(mode)) {
            if (nx < minPosX) {
              nx = 2 * minPosX - nx;
              nvx = -nvx;
            } else if (nx > maxPosX) {
              nx = 2 * maxPosX - nx;
              nvx = -nvx;
            }

            if (ny < minPosY) {
              ny = 2 * minPosY - ny;
              nvy = -nvy;
            } else if (ny > maxPosY) {
              ny = 2 * maxPosY - ny;
              nvy = -nvy;
            }
          } else if (TORIC.equals(mode)) {
            if (nx < minPosX) {
              nx = maxPosX - (minPosX - nx);
            }
            if (nx > maxPosX) {
              nx = minPosX + nx - maxPosX;
            }
            if (ny < minPosY) {
              ny = maxPosY - (minPosY - ny);
            }
            if (ny > maxPosY) {
              ny = minPosY + ny - maxPosY;
            }
          } else if (NONE.equals(mode)) {
            if (nx < minPosX || nx > maxPosX || ny < minPosY || ny > maxPosY) {
              p.age = 0; // will be deleted
            }
          } else {
            System.err.println("Unkown mode");
          }
          p.moveTo(nx, ny);
          p.vx = nvx;
          p.vy = nvy;

          if (p.trajectable)
            p.addTrajectoryPoint();

          if (--p.age <= 0) {
            if (toRemove == null)
              toRemove = new ArrayList<P>();
            toRemove.add(p);
          }

        } // for particles

        if (toRemove != null)
          for (P p : toRemove)
            particles.remove(p);

      }
    } catch (Exception e) {
      System.err.println(e);
    }

    return false;
  }

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
    iwidth = DLUtil.RangeRandom(400, 600);
    iheight = iwidth;
  }

  public int getnumParticles() {
    synchronized (particles) {
      return particles.size();
    }
  }

  public void setnumParticles(int numParticles) {
    synchronized (particles) {
      while (particles.size() > numParticles)
        particles.remove(particles.size() - 1);
      while (particles.size() < numParticles)
        particles.add(createParticle());
    }
  }

  int[] rangenumParticles() {
    return new int[] { 10, numInitParticles * 10 };
  }

  E createEmiter() {
    float x = DLUtil.RangeRandom(minPosX, maxPosX);
    float y = DLUtil.RangeRandom(minPosY, maxPosY);
    E e = new E(this, x, y);
    e.setEmit(true);
    return e;
  }

  void initEmiters() {
    synchronized (emiters) {
      for (int i = 0; i < numInitEmiters; i++) {
        emiters.add(createEmiter());
      }
    }
  }

  P createParticle() {
    float x = DLUtil.RangeRandom(minPosX, maxPosX);
    float y = DLUtil.RangeRandom(minPosY, maxPosY);
    return createParticle(x, y);
  }

  P createParticle(float x, float y, Color color) {
    float mass = DLUtil.RangeRandom(minMass, maxMass);
    float v = DLUtil.RangeRandom(minSpeed, maxSpeed);
    float t = DLUtil.RangeRandom(0, DLUtil.TWO_PI);
    float vx = v * DLUtil.Cos(t);
    float vy = v * DLUtil.Sin(t);
    P p = new P(this, x, y, mass, vx, vy, color);
    return p;
  }

  P createParticle(float x, float y) {
    Color color = particleColor();
    return createParticle(x, y, color);
  }

  void colorizeParticles() {
    synchronized (particles) {
      for (P p : particles) {
        Color color = particleColor();
        p.paint = color;
      }
    }
  }

  Color particleColor() {
    float dc = DLUtil.RangeRandom(-colVariance, colVariance);
    float c1 = partColor - dc;
    if (c1 < 0)
      c1 = 0;
    float c2 = partColor + dc;
    if (c2 > 1)
      c2 = 1;
    Color color = DLUtil.RandomColor(c1, c2, 0.5f, 1f, 0.6f, 1f);
    return color;
  }

  void initParticles() {
    synchronized (particles) {
      for (int i = 0; i < numInitParticles; i++) {
        particles.add(createParticle());
      }
    }
  }

  /* painters */

  void paintBefore(Graphics2D g) {
    if (paintTrajectories)
      paintTrajectories(g);
    if (paintParticles)
      paintParticles(g);
  }

  void paintAfter(Graphics2D g, long dt) {
    if (paintMargin)
      paintMargin(g);
    if (paintGraviton)
      paintGravitons(g);
    if (paintEmiters)
      paintEmiters(g);
    if (fps)
      paintFps(g, dt);
  }

  void paintMargin(Graphics2D g) {
    Path2D p = new DLPath();

    int w = iwidth - 1;
    int h = iheight - 1;
    int m = margin;

    p.moveTo(0, 0);
    p.lineTo(w, 0);
    p.lineTo(w, h);
    p.lineTo(0, h);
    p.lineTo(0, 0);

    p.moveTo(m, m);
    p.lineTo(w - m, m);
    p.lineTo(w - m, h - m);
    p.lineTo(m, h - m);
    p.lineTo(m, m);

    Color r = new Color(0xBB0B0B);
    LinearGradientPaint lgp = new LinearGradientPaint(0, 0, w, h, new float[] { 0, 0.5f, 1 }, new Color[] { r,
        Color.darkGray, r });
    // g.setColor(new Color(0xBB0B0B));
    g.setPaint(lgp);
    g.draw(p);
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

    String s = "F#: " + ff.format(frameCount) + " P#: " + particles.size() + " Fps: " + nf.format(1000. / frameTime)
        + " Ft: " + tf.format(frameTime) + " ms" + " M: " + getMode() + " E#: " + emiters.size() + " G#: "
        + gravitons.size();
    g.setColor(Color.darkGray);
    g.drawString(s, 5, iheight - descent);
  }

  void paintEmiter(Graphics2D g, E e) {
    float px = e.x;
    float x = DLUtil.Normalize(0, iwidth, minPosX, maxPosX, px);
    float py = e.y;
    float y = DLUtil.Normalize(0, iheight, minPosY, maxPosY, py);
//    if (e.shape == null) {
      e.shape = DLUtil.Star(x, y, 3, 9, 8, DLUtil.RangeRandom(0, DLUtil.TWO_PI));
//    }
//    if (e.paint == null) {
      e.paint = DLUtil.RandomColor(0.5f, 0.7f, 0.6f, 1f, 0.7f, 1f);
//    }
    g.setPaint(e.paint);
    try {
      g.fill(e.shape);
    } catch (Exception ex) {
      System.err.println(ex);
    }
  }

  void paintEmiters(Graphics2D g) {
    synchronized (emiters) {
      for (E e : emiters)
        paintEmiter(g, e);
    }
  }

  void paintGraviton(Graphics2D g, G f) {
    float i = f.intensity;
    float ni = i < 0 ? -i : i;
    ni = DLUtil.Normalize(1, 30, 0, maxIntensity, ni);
    
    float x = DLUtil.Normalize(0, iwidth, minPosX, maxPosX, f.x);
    float y = DLUtil.Normalize(0, iheight, minPosY, maxPosY, f.y);
    float fl = 2 * DLUtil.FastLog(ni);

    if (f.shape == null) {
      f.shape = DLUtil.Star(x, y, fl, 3 * fl, 12, DLUtil.RandomAngle());
    }
    if (f.paint == null) {
      float ni2 = ni / 20;
      if (ni2 <= 0.1f)
        ni2 = 0.1f;
      if (ni2 >= 0.95f)
        ni2 = 0.95f;

      if (i < 0) {
        RadialGradientPaint rgp = new RadialGradientPaint(x, y, 3 * fl, new float[] { 0, 1 }, new Color[] { Color.red,
            Color.blue });
        f.paint = rgp; // DLUtil.TransparenterColor(Color.yellow, ni2); //0.3f);
      } else {
        RadialGradientPaint rgp = new RadialGradientPaint(x, y, 3 * fl, new float[] { 0, 1 }, new Color[] { Color.blue,
            Color.red });
        f.paint = rgp; // DLUtil.TransparenterColor(Color.cyan, ni2); //0.3f);
      }
    }

    g.setPaint(f.paint);
    try {
      Shape s = f.shape;
      if (s != null) {
        g.fill(s);
        if (f.isMarkDelete()) {
          g.setPaint(Color.red);
        } else {
          g.setPaint(DLUtil.TransparenterColor(DLUtil.BrighterColor(Color.lightGray, 1.7f), 0.3f));
        }
        g.draw(s);
      }
    } catch (Exception e) {
      System.err.println("paintGraviton " + e);
    }
  }

  void paintParticle(Graphics2D g, P p) {
    float px = p.x;
    float x = DLUtil.Normalize(0, iwidth, minPosX, maxPosX, px);
    float py = p.y;
    float y = DLUtil.Normalize(0, iheight, minPosY, maxPosY, py);
    // g.setPaint(p.paint);
    setPointFill(p.paint);
    drawPoint(g, x, y);
  }

  void paintTrajectories(Graphics2D g) {
    try {
      synchronized (particles) {
        for (P p : particles)
          p.paintTrajectory(g);
      }
    } catch (ConcurrentModificationException e) {
      e.printStackTrace();
    }
  }

  void paintParticles(Graphics2D g) {
    try {
      synchronized (particles) {
        for (P p : particles)
          paintParticle(g, p);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void paintGravitons(Graphics2D g) {
    synchronized (gravitons) {
      for (G f : gravitons)
        paintGraviton(g, f);
    }
  }

  /** getters/setters */
  public void setPaintMargin(boolean b) {
    paintMargin = b;
  }

  public boolean getPaintMargin() {
    return paintMargin;
  }

  public boolean getPaintGraviton() {
    return paintGraviton;
  }

  public void setPaintGraviton(boolean paintGraviton) {
    this.paintGraviton = paintGraviton;
  }

  public boolean getPaintParticles() {
    return paintParticles;
  }

  public void setPaintParticles(boolean paintParticles) {
    this.paintParticles = paintParticles;
  }

  public boolean getPaintTrajectories() {
    return paintTrajectories;
  }

  public void setPaintTrajectories(boolean paintTrajectories) {
    this.paintTrajectories = paintTrajectories;
  }

  public boolean getPaintEmiters() {
    return paintEmiters;
  }

  public void setPaintEmiters(boolean paintEmiters) {
    this.paintEmiters = paintEmiters;
  }

  public boolean getEmit() {
    return emit;
  }

  public void setEmit(boolean emit) {
    this.emit = emit;
    synchronized (emiters) {
      for (E e : emiters)
        e.setEmit(emit);
    }
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

  public boolean getDisplayFps() {
    return fps;
  }

  public void setDisplayFps(boolean fps) {
    this.fps = fps;
  }

  public int getmaxTrajectoryPoints() {
    return maxTrajectoryPoints;
  }

  public void setmaxTrajectoryPoints(int maxTrajectoryPoints) {
    this.maxTrajectoryPoints = maxTrajectoryPoints;
  }

  public float getPartColor() {
    return partColor;
  }

  public void setPartColor(float partColor) {
    this.partColor = partColor;
    colorizeParticles();
  }

  public float[] rangePartColor() {
    return new float[] { 0, 1f };
  }

  public float getPartColVariance() {
    return colVariance;
  }

  public void setPartColVariance(float colVariance) {
    this.colVariance = colVariance;
    colorizeParticles();
  }

  public float[] rangePartColVariance() {
    return new float[] { 0, 1f };
  }
  

  public static void main(String[] a) {

    final JFrame frame = new JFrame();
    final DLContainer panel = new DLContainer();
    panel.setFocusable(true);
    panel.setBackground(new Color(0x0c0c0c));
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setFocusable(true);
    frame.setSize(800, 600);
    panel.setBackground(new Color(0xc0c0c0));
    final DLParticles dlg = new DLParticles(400, 300);
    dlg.iwidth = 800;
    dlg.iheight = 600;
    dlg.threadSleep = 5;
    
    panel.addComponent(dlg);

    DLMouse mouse = new DLMouse(panel) {
      public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
        case BUTTON2:
        case BUTTON3:
        case BUTTON1:
          if (panel.ps != null)
            panel.ps.close();
          panel.ps = new DLPropertySheet(dlg);
          break;
        }
      }
    };

    mouse.listen(panel);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = frame.getSize().width;
        final int h = frame.getSize().height;
        final int x = (dim.width - w) / 2;
        final int y = (dim.height - h) / 2;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(x, y);
        frame.setVisible(true);
      }
    });
  }
}

class P {
  float x;
  float y;
  float mass;
  float vx;
  float vy;
  long age;
  DLParticles system;
  Paint paint;
  boolean trajectable;

  List<DLPoint> trajectory;

  P(DLParticles system, float x, float y, float mass, float vx, float vy, Paint c) {
    this.system = system;
    this.x = x;
    this.y = y;
    this.mass = mass;
    this.vx = vx;
    this.vy = vy;
    this.paint = c;
    this.age = DLUtil.RangeRandom(50, 500);
    this.trajectable = DLUtil.BooleanRandom(system.TrajectableProba);
  }

  P(P p) {
    this.system = p.system;
    this.x = p.x;
    this.y = p.y;
    this.mass = p.mass;
    this.vx = p.vx;
    this.vy = p.vy;
    this.paint = p.paint;
    this.age = p.age;
    this.trajectable = p.trajectable;
  }

  P copy() {
    return new P(this);
  }

  P split() {
    P p = copy();

    p.mass = DLUtil.RangeRandom(1, mass);
    mass -= p.mass;

    p.vx = DLUtil.RangeRandom(0, vx);
    vx -= p.vx;

    p.vy = DLUtil.RangeRandom(0, vy);
    vy -= p.vy;

    this.age = DLUtil.RangeRandom(50, 500);
    this.trajectable = DLUtil.BooleanRandom(0.95);

    return p;
  }

  void addTrajectoryPoint() {
    if (trajectory == null)
      trajectory = Collections.synchronizedList(new ArrayList<DLPoint>());

    DLPoint dlp = new DLPoint(this.x, this.y, System.currentTimeMillis());

    int s = trajectory.size();
    if (s > 1) {
      DLPoint lp = trajectory.get(s - 1);
      double dx = x - lp.x;
      double dy = y - lp.y;
      double d2 = dx * dx + dy * dy;
      if (d2 < system.trajectoryStep)
        return;
    }
    trajectory.add(dlp);
    while (trajectory.size() > system.maxTrajectoryPoints)
      trajectory.remove(0);
    trajectory.add(dlp);
  }

  void paintTrajectory(Graphics2D g) {

    if (trajectory == null)
      return;

    DLParticles s = system;
    Color c = (Color) paint;

    if (s.trajectories == DLParticles.SEGMENT) {
      float lastX = 0;
      float lastY = 0;
      for (int i = trajectory.size() - 1; i >= 0; i--) {
        DLPoint pt = trajectory.get(i);
        float x = DLUtil.Normalize(0, s.iwidth, system.minPosX, system.maxPosX, pt.x);
        float y = DLUtil.Normalize(0, s.iheight, system.minPosY, system.maxPosY, pt.y);
        if (i < trajectory.size() - 1) {
          float dx = lastX - x;
          float dy = lastY - y;
          if (dx < 0)
            dx = -dx;
          if (dy < 0)
            dy = -dy;
          if (dx < 100 && dy < 100) {
            Line2D.Float l = new Line2D.Float(lastX, lastY, x, y);
            int tr = (int) DLUtil.Normalize(0, 255, 0, trajectory.size(), i);
            Color col = new Color(c.getRed(), c.getGreen(), c.getBlue(), tr);
            g.setColor(col);
            g.draw(l);
          }
          /*
           * int x1 = (int) lastX; int y1 = (int) lastY; int x2 = (int) x; int
           * y2 = (int) y; g.setColor(c); g.drawLine(x1, y1, x2, y2); c =
           * DLUtil.TransparenterColor(c, 0.85f);
           */
        }
        lastX = x;
        lastY = y;
      }
    }

    if (s.trajectories == DLParticles.POINTS) {
      float sz = DLUtil.Normalize(0, 1, 0, trajectory.size(), s.trajectoryPointSize);
      for (int i = trajectory.size() - 1; i >= 0; i--) {
        DLPoint pt = trajectory.get(i);
        float x = DLUtil.Normalize(0, s.iwidth, system.minPosX, system.maxPosX, pt.x);
        float y = DLUtil.Normalize(0, s.iheight, system.minPosY, system.maxPosY, pt.y);
        RadialGradientPaint rdp = new RadialGradientPaint(x, y, sz, new float[] { 0, 1 }, new Color[] { c,
            DLUtil.TransparentColor(c) });
        g.setPaint(rdp);
        Shape shp = DLUtil.Polygon(x, y, 7, sz, 0);
        g.fill(shp);
        c = DLUtil.TransparenterColor(c, 0.85f);
      }
    }
  }

  void moveTo(float x, float y) {
    this.x = x;
    this.y = y;
  }

  void avance(float dt) {
    x = x + vx * dt;
    y = y + vy * dt;
  }

  public boolean equals(Object o) {
    P p = (P) o;
    int c;
    c = Float.compare(mass, p.mass);
    if (c != 0)
      return false;
    if (!paint.equals(p.paint))
      return false;
    return true;
  }

  public int hashCode() {
    int ret = super.hashCode();
    ret += Float.valueOf(mass).hashCode();
    ret += paint.hashCode();
    return ret;
  }
}

class PosObject {
  float x;
  float y;
  Shape shape;
  Paint paint;

  boolean markDelete = false;

  PosObject(float x, float y) {
    this.x = x;
    this.y = y;
  }

  void moveTo(float x, float y) {
    this.x = x;
    this.y = y;
    shape = null;
    paint = null;
  }

  public boolean isMarkDelete() {
    return markDelete;
  }

  public void setMarkDelete(boolean markDelete) {
    this.markDelete = markDelete;
  }

}

class G extends PosObject {
  float intensity;

  G(float x, float y, float i) {
    super(x, y);
    this.intensity = i;
  }

}

class E extends PosObject {
  int emitDelay = 50;
  int salve = 3;
  boolean emit;
  DLParticles system;
  boolean useTimer;
  Timer timer;
  Thread thread;
  boolean stopped;

  E(DLParticles system, float x, float y) {
    super(x, y);
    this.system = system;
    salve = system.salve;
    emitDelay = system.emitDelay;

  }

  void emit() {
    int i = salve;
    while (i-- > 0) {
      float ex = E.this.x;
      float ey = E.this.y;
      P p = system.createParticle(ex, ey);
      system.particles.add(p);
    }
  }

  void emitThread() {
    Runnable run = new Runnable() {
      public void run() {
        while (!stopped) {
          int i = salve;
          while (i-- >= 0) {
            emit();
          }
          if (emitDelay > 0) {
            try {
              Thread.sleep(emitDelay);
            } catch (InterruptedException e) {
            }
          }
        }
      }
    };
    thread = new Thread(run);
    thread.start();
  }

  TimerTask newTask() {
    return new TimerTask() {
      public void run() {
        emit();
        long delay = DLUtil.RangeRandom(1, emitDelay);
        timer.schedule(newTask(), delay);
      }
    };
  }

  void setEmit(boolean e) {
    if (e == emit)
      return;
    emit = e;
    if (e) {
      if (useTimer) {
        timer = new Timer();
        TimerTask task = newTask();
        long delay = DLUtil.RangeRandom(1, emitDelay);
        timer.schedule(task, delay);
      } else {
        stopped = false;
        emitThread();
      }
    } else {
      if (useTimer) {
        if (timer != null) {
          timer.cancel();
          timer = null;
        }
      } else {
        stopped = true;
        thread = null;
      }
    }
  }
}

class Renderer {
  Shape shape;
  Color color;
}
