package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.jhlabs.image.EdgeFilter;
import com.pc.delaunay.Pnt;
import com.pc.delaunay.Triangle;
import com.pc.delaunay.Triangulation;

public class DLDelaunay extends DLImage {
  int threadSleep = 50;
  boolean inited = false;
  private Triangulation triangulation;
  private Triangle initialTriangle;
  private static int initialSize = 50000;
  private int initialPoints = 100;
  ArrayList<DLPoint> sites = new ArrayList<DLPoint>();
  float deltaT = 0.25f;
  float vmax = 10f;
  long frameCount = 1;
  float radius = 0.5f;

  Color voronoiStroke = Color.yellow;
  Color voronoiFill;
  Color delaunayStroke = Color.orange;
  Color delaunayFill;
  Color circlesFill;
  Color circlesStroke = Color.pink;

  boolean clearImage = true;
  boolean paintDelaunay = true;
  boolean paintVoronoi = true;
  boolean paintSites = true;
  boolean paintFrame = true;
  boolean paintCircle = false;
  boolean paintFPS = true;
  boolean gradient = false;
  boolean edges = false;
  ShowException exception;

  public DLDelaunay() {
    super();
  }

  DLDelaunay(DLDelaunay src) {
    this();
  }

  public DLDelaunay(float x, float y) {
    super(x, y);
  }

  DLDelaunay copy() {
    return new DLDelaunay(this);
  }

  void clearImage() {
    if (backgroundColor == null)
      super.clearImage();
    else {
      Graphics2D g = image.createGraphics();
      g.setColor(backgroundColor);
      g.fillRect(0, 0, iwidth, iheight);
    }
  }

  public void f(Graphics2D g, DLThread t) {
    init();

    long start = System.currentTimeMillis();
    long dt = 0;
    while (frameCount++ > 0) {
      start = System.currentTimeMillis();
      if (t != null && t.isStopped())
        break;
      try {
        DLUtil.SetHints(g);

        step(g);

        if (clearImage)
          clearImage();
        if (paintDelaunay)
          paintDelaunay(g);
        if (paintVoronoi)
          paintVoronoi(g);
        if (paintCircle)
          paintCircles(g);
        if (edges)
          edges();
        if (paintSites)
          paintSites(g);
        if (paintFPS)
          paintFPS(g, dt);
        if (paintFrame)
          paintFrame(g);
      } catch (Exception e) {
        exception = new ShowException(this, e);
      }
      if (parent != null)
        parent.paint(this);
      dt = System.currentTimeMillis() - start;
      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          exception = new ShowException(this, e);
        }
      }
    }
  }

  void paintFPS(Graphics2D g, long frameTime) {
    if (frameTime == 0)
      return;
    DLUtil.SetHints(g);

    NumberFormat nf = new DecimalFormat("000.00");
    NumberFormat tf = new DecimalFormat("00.00");
    NumberFormat ff = new DecimalFormat("00000");

    Font f = new Font(Font.MONOSPACED, Font.PLAIN, 10);
    FontMetrics metrics = g.getFontMetrics(f);

    int descent = metrics.getDescent();

    String s = null;
    if (exception != null) {
      s = " " + exception.toString() + " ";
      FontMetrics m = g.getFontMetrics();
      float w = m.stringWidth(s);
      float h = m.getMaxAscent() + m.getMaxDescent();
      float x = 5;
      float y = iheight - h;
      Rectangle2D.Float r2d = new Rectangle2D.Float(x, y, w, h);
      g.setColor(DLUtil.Invert(exception.color));
      g.fill(r2d);

      //      exception.color = DLUtil.TransparenterColor(exception.color, 0.9f);
      g.setColor(exception.color);
      if (exception.count-- <= 0)
        exception = null;
    } else {
      s = " F#: " + ff.format(frameCount) + " Fps: " + nf.format(1000. / frameTime) + " Ft: " + tf.format(frameTime)
          + " ms ";
      FontMetrics m = g.getFontMetrics();
      float w = m.stringWidth(s);
      float h = m.getMaxAscent() + m.getMaxDescent();
      float x = 5;
      float y = iheight - h;
      Rectangle2D.Float r2d = new Rectangle2D.Float(x, y, w, h);
      //      g.setStroke(new BasicStroke());
      Color c = new Color(0x66, 0, 0x33);
      g.setColor(DLUtil.TransparenterColor(DLUtil.Invert(c), 0.65f));
      g.fill(r2d);
      g.setColor(c);
      g.draw(r2d);
    }
    g.drawString(s, 5, iheight - descent);
  }

  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  synchronized void step(Graphics2D g) {
    avance();
    initialTriangle = new Triangle(new Pnt(-initialSize, -initialSize), new Pnt(initialSize, -initialSize), new Pnt(0,
        initialSize));
    triangulation = new Triangulation(initialTriangle);
    for (DLPoint dlp : sites) {
      Pnt p = new Pnt(dlp.x, dlp.y);
      triangulation.delaunayPlace(p);
    }
  }

  void edges() {
    EdgeFilter ef = new EdgeFilter();
    image = ef.filter(image, image);
  }

  synchronized void avance() {

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

  DLPoint createSite() {
    int margin = 20;
    float x = DLUtil.RangeRandom(-margin, iwidth + margin);
    float y = DLUtil.RangeRandom(-margin, iheight + margin);
    Color c = DLUtil.RandomColor(0, 1, 0.5f, 0.7f, 0.4f, 0.7f);
    DLPoint p = new DLPoint(x, y);
    p.paint = c;
    p.vx = DLUtil.RangeRandom(-vmax, vmax);
    p.vy = DLUtil.RangeRandom(-vmax, vmax);
    return p;
  }

  synchronized void reinit() {

    while (sites.size() > initialPoints)
      sites.remove(0);
    while (sites.size() < initialPoints)      
      sites.add(createSite());

  }

  synchronized void init() {
    if (inited)
      return;
    sites.clear();
    initialTriangle = new Triangle(new Pnt(-initialSize, -initialSize), new Pnt(initialSize, -initialSize), new Pnt(0,
        initialSize));
    triangulation = new Triangulation(initialTriangle);
    for (int i = 0; i < initialPoints; i++) {
      DLPoint dlp = createSite();
      Pnt p = new Pnt(dlp.x, dlp.y);
      sites.add(dlp);
      try {
        triangulation.delaunayPlace(p);
      } catch (Exception e) {
        exception = new ShowException(this, e);
      }
    }
    inited = true;
  }

  void paintFrame(Graphics2D g) {
    g.setColor(Color.black);
    g.drawRect(0, 0, iwidth - 1, iheight - 1);
  }

  float radius(Rectangle2D.Float rect) {
    return (rect.width + rect.height) * radius;
  }

  synchronized void paintVoronoi(Graphics2D g) {
    if ((!gradient) && (voronoiStroke == null) && (voronoiFill == null))
      return;
    // Keep track of sites done; no drawing for initial triangles sites
    HashSet<Pnt> done = new HashSet<Pnt>(initialTriangle);
    for (Triangle triangle : triangulation) {
      for (Pnt site : triangle) {
        if (done.contains(site))
          continue;
        done.add(site);
        List<Triangle> list = triangulation.surroundingTriangles(site, triangle);
        Pnt[] vertices = new Pnt[list.size()];
        int i = 0;
        for (Triangle tri : list)
          vertices[i++] = tri.getCircumcenter();
        Path2D.Float pa = null;
        for (Pnt p : vertices)
          pa = DLUtil.AddPoint(p.coord(0), p.coord(1), pa);
        pa.closePath();
        if (gradient) {
          Rectangle2D.Float rect = (Rectangle2D.Float) pa.getBounds2D();
          float radius = radius(rect);
          RadialGradientPaint r = new RadialGradientPaint((float) site.coord(0), (float) site.coord(1), radius,
              new float[] { 0, 1 }, new Color[] { Color.yellow, Color.blue });
          g.setPaint(r);
          g.fill(pa);
        } else {
          if (voronoiFill != null) {
            g.setColor(voronoiFill);
            g.fill(pa);
          }
          if (voronoiStroke != null) {
            g.setColor(voronoiStroke);
            g.draw(pa);
          }
        }
      }
    }
  }

  synchronized void paintSites(Graphics2D g) {
    for (DLPoint p : sites) {
      drawPoint(g, p.x, p.y);
    }
  }

  synchronized void paintDelaunay(Graphics2D g) {
    if (delaunayFill == null && delaunayStroke == null)
      return;
    for (Triangle triangle : triangulation) {
      Path2D.Float pa = null;
      for (Pnt p : triangle)
        pa = DLUtil.AddPoint(p.coord(0), p.coord(1), pa);
      pa.closePath();
      if (delaunayFill != null) {
        g.setColor(delaunayFill);
        g.fill(pa);
      }
      if (delaunayStroke != null) {
        g.setColor(delaunayStroke);
        g.draw(pa);
      }
    }
  }

  synchronized void paintCircles(Graphics2D g) {
    if (circlesFill == null && circlesStroke == null)
      return;
    for (Triangle triangle : triangulation) {
      if (triangle.containsAny(initialTriangle))
        continue;
      Pnt c = triangle.getCircumcenter();
      double radius = c.subtract(triangle.get(0)).magnitude();
      Ellipse2D el = new Ellipse2D.Double(c.coord(0) - radius, c.coord(1) - radius, 2 * radius, 2 * radius);
      if (circlesFill != null) {
        g.setColor(circlesFill);
        g.fill(el);
      }
      if (circlesStroke != null) {
        g.setColor(circlesStroke);
        g.draw(el);
      }
    }
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 500);
    iheight = DLUtil.RangeRandom(300, 500);
    pointSize = 15;
    setPointFill(Color.cyan);
    setPointStroke(Color.darkGray);
    setPointShape(StarPoint);
  }

  void paint(Graphics2D g, long dt) {
    DLUtil.SetHints(g);
    super.paint(g);
  }

  synchronized boolean mouse(MouseEvent e) {
    Point p = e.getPoint();
    if (e instanceof MouseWheelEvent) {
      return false;
    }
    switch (e.getID()) {
    case MouseEvent.MOUSE_MOVED: {
      return true;
    }
    case MouseEvent.MOUSE_DRAGGED: {
      DLPoint dlp = new DLPoint(p.x, p.y);
      sites.add(dlp);
      Pnt pn = new Pnt(dlp.x, dlp.y);
      triangulation.delaunayPlace(pn);
      return true;
    }
    case MouseEvent.MOUSE_PRESSED: {
      DLPoint dlp = new DLPoint(p.x, p.y);
      sites.add(dlp);
      Pnt pn = new Pnt(dlp.x, dlp.y);
      triangulation.delaunayPlace(pn);
      return true;
    }
    case MouseEvent.MOUSE_RELEASED: {
      return true;
    }
    default: {
      return true;
    }
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

  public float getDeltaT() {
    return deltaT;
  }

  public void setDeltaT(float deltaT) {
    this.deltaT = deltaT;
  }

  public float[] rangeDeltaT() {
    return new float[] { 0.1f, 2f };
  }

  public boolean getClearImage() {
    return clearImage;
  }

  public void setClearImage(boolean clearImage) {
    this.clearImage = clearImage;
  }

  public boolean getPaintDelaunay() {
    return paintDelaunay;
  }

  public void setPaintDelaunay(boolean paintDelaunay) {
    this.paintDelaunay = paintDelaunay;
  }

  public boolean getPaintVoronoi() {
    return paintVoronoi;
  }

  public void setPaintVoronoi(boolean paintVoronoi) {
    this.paintVoronoi = paintVoronoi;
  }

  public boolean getPaintSites() {
    return paintSites;
  }

  public void setPaintSites(boolean paintSites) {
    this.paintSites = paintSites;
  }

  public boolean getPaintFrame() {
    return paintFrame;
  }

  public void setPaintFrame(boolean paintFrame) {
    this.paintFrame = paintFrame;
  }

  public boolean getPaintCircle() {
    return paintCircle;
  }

  public void setPaintCircle(boolean paintCircle) {
    this.paintCircle = paintCircle;
  }

  public boolean getPaintFPS() {
    return paintFPS;
  }

  public void setPaintFPS(boolean paintFPS) {
    this.paintFPS = paintFPS;
  }

  public boolean getEdges() {
    return edges;
  }

  public void setEdges(boolean e) {
    this.edges = e;
  }

  public void setGradient(boolean g) {
    gradient = g;
  }

  public boolean getGradient() {
    return gradient;
  }

  public float getVmax() {
    return vmax;
  }

  synchronized public void setVmax(float vmax) {
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

  public int getNumSites() {
    return initialPoints;
  }

  public void setNumSites(int initialPoints) {
    if (this.initialPoints == initialPoints)
      return;
    this.initialPoints = initialPoints;
    reinit();
  }

  public int[] rangeNumSites() {
    return new int[] { 0, 1000 };
  }

  public float getRadius() {
    return radius;
  }

  public void setRadius(float r) {
    this.radius = r;
  }

  public float[] rangeRadius() {
    return new float[] { 0, 1 };
  }

  public Color getVoronoiStroke() {
    return voronoiStroke;
  }

  public void setVoronoiStroke(Color voronoiStroke) {
    this.voronoiStroke = voronoiStroke;
  }

  public Color getVoronoiFill() {
    return voronoiFill;
  }

  public void setVoronoiFill(Color voronoiFill) {
    this.voronoiFill = voronoiFill;
  }

  public Color getDelaunayStroke() {
    return delaunayStroke;
  }

  public void setDelaunayStroke(Color delaunayStroke) {
    this.delaunayStroke = delaunayStroke;
  }

  public Color getDelaunayFill() {
    return delaunayFill;
  }

  public void setDelaunayFill(Color delaunayFill) {
    this.delaunayFill = delaunayFill;
  }

  public Color getCirclesFill() {
    return circlesFill;
  }

  public void setCirclesFill(Color c) {
    this.circlesFill = c;
  }

  public Color getCirclesStroke() {
    return circlesStroke;
  }

  public void setCirclesStroke(Color c) {
    this.circlesStroke = c;
  }

}

class ShowException {
  Exception e;
  int count;
  Color color;
  DLDelaunay vld;

  ShowException(DLDelaunay vld, Exception e) {
    this(vld, e, 20, Color.red);
  }

  ShowException(DLDelaunay vld, Exception e, int count, Color color) {
    this.vld = vld;
    this.e = e;
    this.count = count;
    this.color = color;
    vld.exception = this;
    e.printStackTrace();
  }

  public String toString() {
    String m = e.getMessage();
    return e + (m != null ? (" " + m) : "");
  }
}