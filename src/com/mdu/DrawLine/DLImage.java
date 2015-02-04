package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.jhlabs.image.CausticsFilter;
import com.jhlabs.image.PlasmaFilter;

abstract class DLImage extends DLComponent implements Threaded, JPG {
  ArrayList<DLThread> threads = new ArrayList<DLThread>();
  boolean threaded = true;
  BufferedImage image = null;
  int iheight = 200;
  int iwidth = 200;
  float pointSize = 0f;
  boolean selectCheckTransparentPixel = false;
  public static final String EllipsePoint = "ellipsePoint";
  public static final String PolyPoint = "polyPoint";
  public static final String RectanglePoint = "rectPoint";
  public static final String StarPoint = "starPoint";
  public static final String LinePoint = "linePoint";
  public static final String HeartPoint = "heartPoint";
  public static final String[] pointShapes = { EllipsePoint, PolyPoint, RectanglePoint, StarPoint, LinePoint,
      HeartPoint };
  String pointShape = EllipsePoint;
  Paint pointFill;
  Paint pointStroke;
  Color backgroundColor;

  void reportException(Throwable e) {
    System.err.println(e);
  }

  public String getPointShape() {
    return pointShape;
  }

  public void setPointShape(String pointShape) {
    this.pointShape = pointShape;
    stopAll();
    clear();
    run();
  }

  public String[] enumPointShape() {
    return new String[] { EllipsePoint, PolyPoint, RectanglePoint, LinePoint, StarPoint, HeartPoint };
  }

  public float getPointSize() {
    return pointSize;
  }

  public void setPointSize(float s) {
    pointSize = s;
    stopAll();
    clear();
    run();
  }

  public float[] rangePointSize() {
    return new float[] { 0, 20 };
  }

  DLImage() {
    super(0, 0);
  }

  DLImage(DLImage c) {
    super(c);
    iwidth = c.iwidth;
    iheight = c.iheight;
    image = new BufferedImage(c.iwidth, c.iheight, BufferedImage.TYPE_INT_ARGB);
    threaded = c.threaded;
  }

  DLImage(float x, float y) {
    super(x, y);
  }

  DLImage(float x, float y, int iw, int ih) {
    super(x, y);
    iwidth = iw;
    iheight = ih;
  }

  void clear() {
    clearImage();
    clearShadow();
  }

  void clearImage() {
    if (image == null) {
      image = image();
    } else {
      if (backgroundColor == null) {
        final Graphics2D g = image.createGraphics();
        Composite c = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        final Rectangle rect = new Rectangle(0, 0, iwidth, iheight);
        g.fill(rect);
        g.setComposite(c);
      } else {
        Graphics2D g = image.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, iwidth, iheight);
      }
    }

    if (DLParams.DEBUG) {
      final Graphics2D g = image.createGraphics();
      g.setColor(Color.darkGray);
      g.drawRect(0, 0, iwidth - 1, iheight - 1);
    }
  }

  abstract DLImage copy();

  void drawPoint(double x, double y) {
    drawPoint((float) x, (float) y);
  }

  void drawPoint(float x, float y) {
    if (image != null)
      drawPoint(image.createGraphics(), x, y);
  }

  void paintAsCircle(Graphics2D g, float x, float y, float s) {
    Shape sh = DLUtil.Circle(x, y, s);
    if (pointFill != null) {
      g.setPaint(pointFill);
      g.fill(sh);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(sh);
    }
  }

  void paintAsPolygon(Graphics2D g, float x, float y, float s) {
    Shape sh = DLUtil.Polygon(x, y, 5, s / 2);
    if (pointFill != null) {
      g.setPaint(pointFill);
      g.fill(sh);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(sh);
    }
  }

  void paintAsRectangle(Graphics2D g, float x, float y, float s) {
    Shape sh = DLUtil.Square(x, y, s);
    if (pointFill != null) {
      g.setPaint(pointFill);
      g.fill(sh);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(sh);
    }
  }

  void paintAsIntLine(Graphics2D g, float x, float y) {
    int ix = (int) (x + 0.5f);
    int iy = (int) (y + 0.5f);
    Line2D.Float l = new Line2D.Float(ix, iy, ix, iy);
    if (pointFill != null) {
      g.setPaint(pointFill);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
    }
    g.draw(l);
  }

  void paintAsLine(Graphics2D g, float x, float y) {
    Line2D.Float l = new Line2D.Float(x, y, x, y);
    g.draw(l);
  }

  void paintAsLine(Graphics2D g, float x, float y, float size) {
    //    Line2D.Float l = new Line2D.Float(x - size / 2, y - size / 2, x + size / 2, y + size / 2);
    float s2 = size / 2;
    Path2D.Float p = new Path2D.Float();
    p.moveTo(x - s2, y - s2);
    p.lineTo(x + s2, y + s2);
    p.moveTo(x - s2, y + s2);
    p.lineTo(x + s2, y - s2);
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(p);
    }
  }

  void paintAsStar(Graphics2D g, float x, float y, float size) {
    size = size / 2f;
    Shape s = DLUtil.Star(x, y, size / 2, size, 7, 0);
    if (pointFill != null) {
      g.setPaint(pointFill);
      g.fill(s);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(s);
    }
  }

  void paintAsHeart(Graphics2D g, float x, float y, float size) {
    Shape s = DLUtil.Heart(x - size / 2, y - size / 2, size, size, true);
    if (pointFill != null) {
      g.setPaint(pointFill);
      g.fill(s);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(s);
    }
  }

  Shape getPointForme(String pointShape, float size) {
    Shape s = null;
    switch (pointShape) {
    case EllipsePoint:
      s = DLUtil.Circle(x, y, size);
      break;
    case PolyPoint:
      s = DLUtil.Polygon(x, y, 5, size / 2);
      break;
    case RectanglePoint:
      s = DLUtil.Square(x, y, size);
      break;
    case StarPoint:
      size = size / 2f;
      s = DLUtil.Star(x, y, size / 2, size, 7, 0);
      break;
    case LinePoint:
      float s2 = size / 2;
      Path2D.Float p = new Path2D.Float();
      p.moveTo(x - s2, y - s2);
      p.lineTo(x + s2, y + s2);
      p.moveTo(x - s2, y + s2);
      p.lineTo(x + s2, y - s2);
      s = p;
      break;
    default:
      break;
    }
    return s;
  }

  void drawPoint(Graphics2D g, double x, double y) {
    drawPoint(g, (float) x, (float) y);
  }

  void drawPoint(Graphics2D g, String shape, float ps, float x, float y) {
    if (ps == 0) {
      paintAsIntLine(g, x, y);
    } else {
      switch (shape) {
      case EllipsePoint:
        paintAsCircle(g, x, y, ps);
        break;
      case PolyPoint:
        paintAsPolygon(g, x, y, ps);
        break;
      case RectanglePoint:
        paintAsRectangle(g, x, y, ps);
        break;
      case StarPoint:
        paintAsStar(g, x, y, ps);
        break;
      case LinePoint:
        paintAsLine(g, x, y, ps);
        break;
      case HeartPoint:
        paintAsHeart(g, x, y, ps);
      default:
        break;
      }
    }
  }

  void drawPoint(Graphics2D g, float x, float y) {
    drawPoint(g, pointShape, pointSize, x, y);
  }

  Rectangle getBounds() {
    return getBounds(true);
  }

  Rectangle getBounds(boolean deco) {
    if (image == null)
      image = image();
    Rectangle bounds = new Rectangle((int) (x - iwidth / 2), (int) (y - iheight / 2), iwidth, iheight);
    if (deco)
      bounds = addShadowBounds(bounds);

    return bounds;
  }

  @Override
  boolean hitTest(Point p) {
    if (!super.hitTest(p))
      return false;
    if (!selectCheckTransparentPixel)
      return true;
    if (image == null)
      image = image();
    final double tx = this.x - iwidth / 2.;
    final double ty = this.y - iheight / 2.;
    float px = (float) (p.x - tx + 0.5f);
    if (px < 0)
      px = 0;
    if (px >= iwidth)
      px = iwidth - 1;
    float py = (float) (p.y - ty + 0.5);
    if (py < 0)
      py = 0;
    if (py >= iheight)
      py = iheight - 1;
    final int pix = image.getRGB((int) px, (int) py);
    if ((pix & 0xff000000) == 0)
      return false;
    return true;
  }

  abstract BufferedImage image();

  boolean mouse(MouseEvent e) {
    return false;
  }

  @Override
  public void move(float dx, float dy) {
    final AffineTransform tr = AffineTransform.getTranslateInstance(dx, dy);
    transform(tr);
  }

  @Override
  public void paint(Graphics gr) {
    paint(gr, true);
  }

  @Override
  public void paint(Graphics gr, boolean deco) {
    final Graphics2D g = (Graphics2D) gr;

    if (image == null)
      image = image();

    if (deco)
      shadow(g);

    g.drawImage(image, (int) (x - iwidth / 2), (int) (y - iheight / 2), null);

    if (deco && DLParams.DEBUG) {
      final Rectangle b = getBounds();
      g.setColor(Color.darkGray);
      g.drawRect(b.x, b.y, b.width - 1, b.height - 1);
    }
  }

  @Override
  public void randomize() {
    super.randomize();
    iwidth = RangeRandom(20, 60);
    iheight = RangeRandom(20, 60);
    setShadow(true);
  };

  @Override
  void transform(AffineTransform tr) {
    final Point2D src = new Point2D.Float(x, y);
    final Point2D dst = tr.transform(src, null);
    x = (float) dst.getX();
    y = (float) dst.getY();
  }

  public int[] rangeSteps() {
    return new int[] { 10, 100000 };
  }

  public boolean isThreaded() {
    return threaded;
  }

  public void setThreaded(boolean threaded) {
    this.threaded = threaded;
    stopAll();
    clear();
    run();
  }

  public Color getBackground() {
    return backgroundColor;
  }

  public void setBackground(Color c) {
    this.backgroundColor = c;
  }

  public void stopAll() {
    synchronized (threads) {
      DLThread[] tr = threads.toArray(new DLThread[threads.size()]);
      for (DLThread t : tr) {
        t.setStopped(true);
      }
    }
  }

  public void f() {
    if (image == null)
      image = image();
    f(image.createGraphics());
  }

  public void f(Graphics2D g) {
    f(g, null);
  }

  public abstract void f(Graphics2D g, DLThread t);

  public void run() {
    if (threaded)
      runThreaded();
  }

  public void runThreaded() {
    if (image == null)
      image = image();
    runThreaded(image.createGraphics());
  }

  public void runThreaded(final Graphics2D g) {
    DLRunnable run = new DLRunnable() {
      DLThread t;

      public void run() {
        f(g, t);
        synchronized (threads) {
          threads.remove(t);
        }
      }

      public DLThread getThread() {
        return t;
      }

      public void setThread(DLThread t) {
        this.t = t;
      }
    };
    DLThread t = new DLThread(run);
    run.setThread(t);
    stopAll();
    synchronized (threads) {
      threads.add(t);
    }
    t.start();
  }

  public Paint getPointFill() {
    return pointFill;
  }

  public void setPointFill(Paint pointFill) {
    this.pointFill = pointFill;
  }

  public Paint getPointStroke() {
    return pointStroke;
  }

  public void setPointStroke(Paint pointStroke) {
    this.pointStroke = pointStroke;
  }

  
  public void save(File f) {
    DLUtil.Save(image, f);
  }
}
