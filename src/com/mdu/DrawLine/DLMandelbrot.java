package com.mdu.DrawLine;

import static java.awt.event.MouseEvent.BUTTON1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DLMandelbrot extends DLPointImage {
  boolean paintOutside = true;
  boolean paintInside = true;
  Point lastMousePoint = null;
  int maxIter = 1000;
  DLPoint poi;
  DLColorModel model1 = DLUtil.ColorModel1;
  DLColorModel model2 = DLUtil.ColorModel2;
  DLColorModel model3 = DLUtil.ColorModel3;
  DLColorModel model = model3;

  static double XMax = 0.6;
  static double XMin = -2.1;
  static double YMax = 1.2;
  static double YMin = -1.2;

  double xMax = XMax;
  double xMin = XMin;
  double yMax = YMax;
  double yMin = YMin;

  BufferedImage translateImage = null;
  int currentDx, currentDy;
  int[][] coordsArray = null;

  DLMandelbrot() {
    super();
  }

  DLMandelbrot(DLMandelbrot src) {
    super();
  }

  public DLMandelbrot(float x, float y) {
    super(x, y);
  }

  DLMandelbrot copy() {
    return new DLMandelbrot(this);
  }

  public void paint(Graphics gr, boolean deco) {
    super.paint(gr, deco);
    if (translateImage != null) {
      Rectangle r = getBounds(false);
//      Shape c = gr.getClip();
      gr.setClip(r);
      gr.drawImage(translateImage, (int) x - iwidth / 2 + currentDx, (int) y - iheight / 2 + currentDy, null);
//      gr.setClip(c);
      gr.setClip(null);
    }
  }

  public void f(Graphics2D g, DLThread t) {

    final double x1 = xMin;
    final double x2 = xMax;
    final double y1 = yMin;
    final double y2 = yMax;

    if (coordsArray == null)
      coordsArray = DLUtil.ShuffleCoords(iwidth, iheight);
    int[][] al = coordsArray;

    for (int i = 0; i < al.length; i++) {

      if ((t != null) && t.isStopped())
        return;

      int[] ii = al[i];
      int x = ii[0];
      int y = ii[1];

      final double nx = DLUtil.Normalize(x1, x2, 0, iwidth, x);
      final double ny = DLUtil.Normalize(y1, y2, 0, iheight, y);
      double zr = 0;
      double zi = 0;
      final double cr = nx;
      final double ci = ny;
      int k = 0;
      double stop = 0;
      do {
        if ((t != null) && t.isStopped()) {
          return;
        }
        final double tmp = zr;
        zr = zr * zr - zi * zi + cr;
        zi = 2 * zi * tmp + ci;
        stop = zr * zr + zi * zi;
        k++;
        if ((t != null) && t.isStopped()) {
          return;
        }
      } while (stop < 4 && k < maxIter);
      if (k == maxIter) {
        if (paintInside) {
          stop = DLUtil.Normalize(0, 1, 0.01f, 0.99f, stop);
          float s = (float) stop;
          int color = model.getColor(s);
          Color c = new Color(color);
          // g.setColor(c);
          setPointFill(c);
          drawPoint(g, x, y);
        }
      } else if (paintOutside) {
        stop = DLUtil.Normalize(0, 1, 0, maxIter, k);
        final float s = (float) stop;
        final int color = model.getColor(s);
        Color c = new Color(color);
        // g.setColor(c);
        setPointFill(c);
        drawPoint(g, x, y);
      }
      if (parent != null) {
        if ((y + iwidth * x) % maxIter == 0) {
          final Rectangle r = getBounds();
          parent.paint(r);
        }
      }
      if ((t != null) && t.isStopped()) {
        return;
      }
    } // for

    if (parent != null) {
      final Rectangle r = getBounds();
      parent.paint(r);
    }

  }

  public DLColorModel getModel() {
    return model;
  }

  public void setModel(DLColorModel model) {
    this.model = model;
    stopAll();
    clearImage();
    runThreaded();
  }

  public DLColorModel[] enumModel() {
    return new DLColorModel[] { model1, model2, model3 };
  }

  public boolean getPaintInside() {
    return paintInside;
  }

  public void setPaintInside(boolean paintInside) {
    this.paintInside = paintInside;
    stopAll();
    clearImage();
    runThreaded();
  }

  @Override
  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);
    else
      f(g, null);
    return img;
  }

  boolean mouse(MouseEvent e) {
    if (e instanceof MouseWheelEvent) {
      final MouseWheelEvent mwe = (MouseWheelEvent) e;
      float x = e.getX() - (this.x - iwidth / 2);
      float y = e.getY() - (this.y - iheight / 2);
      final int d = mwe.getWheelRotation();
      zoom(x, y, d);
      return true;
    }
    switch (e.getID()) {
    case MouseEvent.MOUSE_DRAGGED:
      if (lastMousePoint == null)
        lastMousePoint = e.getPoint();
      else {
        final Point p = e.getPoint();
        final int dx = lastMousePoint.x - p.x;
        final int dy = lastMousePoint.y - p.y;
        currentDx -= dx;
        currentDy -= dy;
        translate(dx, dy);
        lastMousePoint = p;
      }
      return true;
    case MouseEvent.MOUSE_PRESSED:
      lastMousePoint = e.getPoint();
      translateImage = DLUtil.copy(image, null);
      currentDx = 0;
      currentDy = 0;
      return true;
    case MouseEvent.MOUSE_RELEASED:
      lastMousePoint = null;
      translateImage = null;
      return true;
    default:
      return false;
    }
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 400);
    iheight = iwidth; // DLUtil.RangeRandom(200, 300);
    // model.invert();
    // setShadow(true);
  }

  void translate(int dx, int dy) {
    double vx = DLUtil.Normalize(xMin, xMax, 0, iwidth, dx) - DLUtil.Normalize(xMin, xMax, 0, iwidth, 0);
    double vy = DLUtil.Normalize(yMin, yMax, 0, iheight, dy) - DLUtil.Normalize(yMin, yMax, 0, iheight, 0);

    xMin += vx;
    xMax += vx;
    yMin += vy;
    yMax += vy;

    stopAll();
    clearImage();
    runThreaded();
  }

  public boolean getPaintOutside() {
    return paintOutside;
  }

  public void setPaintOutside(boolean paintOutside) {
    this.paintOutside = paintOutside;
    stopAll();
    clearImage();
    runThreaded();
  }

  public int getMaxIter() {
    return maxIter;
  }

  public void setMaxIter(int maxIter) {
    this.maxIter = maxIter;
    stopAll();
    clearImage();
    runThreaded();
  }

  public int[] rangeMaxIter() {
    return new int[] { 50, 10000 };
  }

  void zoom(float x, float y, int d) {
    final double vx = DLUtil.Normalize(xMin, xMax, 0, iwidth, x);
    final double vy = DLUtil.Normalize(yMin, yMax, 0, iheight, y);

    double zoom;

    if (d > 0)
      zoom = 0.7;
    else
      zoom = 1. / 0.7;

    final double sx = zoom;
    final double sy = zoom;
    final double x0 = vx - sx * vx;
    final double y0 = vy - sy * vy;
    final AffineTransform tr = new AffineTransform(sx, 0, 0, sy, x0, y0);
    final Point2D.Double p = new Point2D.Double();

    stopAll();

    p.x = xMin;
    p.y = yMin;
    tr.transform(p, p);
    xMin = p.x;
    yMin = p.y;

    p.x = xMax;
    p.y = yMax;
    tr.transform(p, p);
    xMax = p.x;
    yMax = p.y;

    clearImage();
    runThreaded();
  }

  public static void main(String[] a) {
    final JFrame frame = new JFrame();
    final DLContainer panel = new DLContainer();
    panel.setFocusable(true);
    panel.setBackground(new Color(0x0c0c0c));
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setFocusable(true);
    int width = 400;
    int height = 300;
    frame.setSize(width, height);
    final DLMandelbrot dlg = new DLMandelbrot(width / 2, height / 2);
    dlg.iwidth = width;
    dlg.iheight = height;
    panel.addComponent(dlg);

    DLMouse mouse = new DLMouse(panel) {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);
        final MouseWheelEvent mwe = (MouseWheelEvent) e;
        float x = e.getX() - (dlg.x - dlg.iwidth / 2);
        float y = e.getY() - (dlg.y - dlg.iheight / 2);
        final int d = mwe.getWheelRotation();
        dlg.zoom(x, y, d);
      }

      public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
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
        // Move the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(x, y);
        frame.setVisible(true);
      }
    });
  }
}
