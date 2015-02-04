package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DEBUG;
import static com.mdu.DrawLine.DLUtil.BooleanRandom;
import static com.mdu.DrawLine.DLUtil.RandomColor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public abstract class DLCurve extends DLComponent {
  Color fill = null;
  Path2D path = null;
  Color stroke = null;
  boolean smooth;

  DLCurve() {
    super();
  }

  DLCurve(DLCurve c) {
    super(c);
    fill = c.fill;
    stroke = c.stroke;
    if (c.path != null)
      path = (Path2D) c.path.clone();
  }

  DLCurve(float x, float y) {
    super(x, y);
  }

  Path2D.Float toPath(DLPointList points) {
    return DLUtil.toPath(points);
  }

  Path2D.Float toSpline(DLPointList points) {
    return DLUtil.toSpline(points);
  }

  /*
    Path2D.Float toPath(DLPointList points) {
      Path2D.Float p = null;
      for (int i = 0; i < points.size(); i++)
        p = DLUtil.AddPoint(points.get(i), p);
      return p;
    }

    Path2D.Float toSpline(DLPointList points) {

      final int sz = points.size();
      final Path2D.Float p = new Path2D.Float();
      if (sz > 0) {
        Point2D.Float[] fcp = new Point2D.Float[sz - 1];
        Point2D.Float[] scp = new Point2D.Float[sz - 1];
        PolyUtils.GetCurveControlPoints(points, fcp, scp);

        p.moveTo(points.get(0).x, points.get(0).y);

        for (int i = 0; i < sz - 1; i++) {
          final Point2D cp1 = fcp[i];
          final Point2D cp2 = scp[i];
          final DLPoint pt = points.get(i + 1);
          p.curveTo(cp1.getX(), cp1.getY(), cp2.getX(), cp2.getY(), pt.x, pt.y);

        }
      } else {
        p.moveTo(x, y);
      }
      return p;
    }
  */
  public boolean getSmooth() {
    return smooth;
  }

  public void setSmooth(boolean smooth) {
    Rectangle r = redisplayStart();
    this.smooth = smooth;
    clear();
    redisplay(r);
  }

  void clear() {
    clearShadow();
    clearPath();
    clearSelection();
  }

  void clearPath() {
    path = null;
  }

  Rectangle getBounds(boolean deco) {

    if (path == null)
      path = path();

    Rectangle bounds = new Rectangle(path.getBounds());
    if (deco) {
      bounds = addShadowBounds(bounds);

      bounds = addSelectionBounds(bounds);
    }
    return bounds;
  }

  Rectangle getBounds() {
    return getBounds(true);
  }

  public Color getFill() {
    return fill;
  }

  public Color getStroke() {
    return stroke;
  }

  @Override
  boolean mouse(MouseEvent e) {
    return false;
  }

  @Override
  public void paint(Graphics gr) {
    final Graphics2D g = (Graphics2D) gr;
    paint(g, true);
    if (DEBUG) {
      final Rectangle b = getBounds();
      g.drawRect(b.x, b.y, b.width, b.height);
    }
  }

  
  @Override
  public void paint(Graphics gr, boolean deco) {
    final Graphics2D g = (Graphics2D) gr;

    if (path == null)
      path = path();

    if (deco)
      shadow(g);

    if (fill != null) {
      g.setColor(fill);
      g.fill(path);
    }

    if (stroke != null) {
      g.setColor(stroke);
      //      g.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
      g.draw(path);
    }

    if (deco)
      selection(g);

    if (DEBUG) {
      final PathIterator pi = path.getPathIterator(null);
      final float[] coords = new float[6];
      g.setColor(Color.red);
      while (!pi.isDone()) {
        final int i = pi.currentSegment(coords);
        g.drawRect((int) Math.floor(coords[0] - 2), (int) Math.floor(coords[1] - 2), 4, 4);
        if (i > 2)
          g.drawRect((int) Math.floor(coords[2] - 2), (int) Math.floor(coords[3] - 2), 4, 4);
        if (i > 4)
          g.drawRect((int) Math.floor(coords[4] - 2), (int) Math.floor(coords[5] - 2), 4, 4);
        pi.next();
      }

      final Rectangle r2 = getBounds();
      g.draw(r2);
    }

  }

  abstract Path2D path();

  @Override
  public void randomize() {
    super.randomize();
    if (BooleanRandom())
      fill = RandomColor(0.0f, 1.0f, 0.3f, 0.6f, 0.8f, 1f);
    else
      fill = null;
    if (BooleanRandom())
      stroke = RandomColor(0.0f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f);
    else
      stroke = null;

    if (stroke == null)
      stroke = RandomColor(0.0f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f);

    setShadow(true);
  }

  public void setFill(Color fill) {
    Rectangle r = redisplayStart();
    clearShadow();
    this.fill = fill;
    redisplay(r);
  }

  public void setStroke(Color stroke) {
    Rectangle r = redisplayStart();
    clearShadow();
    this.stroke = stroke;
    redisplay(r);
  }

  @Override
  void transform(AffineTransform tr) {
    if (path == null)
      path = path();
    path.transform(tr);
  }

  void transform(Path2D p) {
    final AffineTransform tr = new AffineTransform();
    tr.translate(x, y);
    tr.concatenate(transformation);
    p.transform(tr);
  };
}
