package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

class DLPolyline extends DLSegmentedComponent implements DLSegmented {

  static final int CIRCLE_SHAPE = 1;
  static final int SQUARE_SHAPE = 2;
  static final int MIXED_SHAPE = 3;
  static final int NO_SHAPE = 0;

  int mode = CIRCLE_SHAPE;

  Color color = Color.black;
  Path2D path;
  Point2D.Double[] fcp;
  Point2D.Double[] scp;
  double handleSize = DLParams.HANDLE_SIZE;

  public DLPolyline(int x, int y) {
    super(x, y);
  }

  private void drawCurve(Graphics2D g) {
    if (color != null)
      g.setColor(color);
    g.draw(path);
    if (mode != NO_SHAPE)
      drawPoints(g);
  }

  public void paint(Graphics gr, boolean deco) {
    Graphics2D g = (Graphics2D) gr;
    if (path == null)
      path = curve();
    if (deco) {
      shadow(g);
      if (DLParams.DEBUG) {
        Rectangle r = getBounds();
        g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
      }
    }
    drawCurve(g);
  }

  public void paint(Graphics gr) {
    paint(gr, true);
  }

  DLPolyline copy() {
    return new DLPolyline(this);
  }

  DLPolyline(DLPolyline src) {
    super();
    this.color = src.color;
    this.handleSize = src.handleSize;
    points = (ArrayList<DLPoint>) src.points.clone();
  }

  Rectangle getBounds() {
    if (path == null)
      path = curve();
    Rectangle r = path.getBounds();
    for (int i = 0; i < points.size(); i++) {
      DLPoint p = points.get(i);
      if (p.shape != null) {
        Rectangle rs = p.shape.getBounds();
        Rectangle2D.union(r, rs, r);
      }
    }
    r = addShadowBounds(r);
    return r;
  }

  boolean hitTest(Point p) {
    if (!super.hitTest(p))
      return false;
    float d = DLUtil.MinDistance(points, p.x, p.y);
    if (d > DLParams.SELECT_PRECISION)
      return false;
    return true;
  }

  public void randomize() {
    setShadow(true);
    mode = DLUtil.RangeRandom(0, 4);
    color = DLUtil.RandomColor(0.0f, 1.0f, 0.6f, 0.9f, 0.8f, 1f);
  }

  public int getMode() {
    return mode;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  void drawPoints(Graphics2D g) {
    for (int i = 0; i < points.size(); i++)
      drawPoint(g, i);
  }

  void drawPoint(Graphics2D g, int i) {
    DLPoint p = points.get(i);
    if (p.shape != null)
      g.fill(p.shape);
  }

  void drawSegment(Graphics2D g, int i) {

    DLPoint ls = points.get(i - 1);
    DLPoint s = points.get(i);
    double x = s.x;
    double y = s.y;
    double dx = x - ls.x;
    double dy = y - ls.y;

    Rectangle2D rect = new Rectangle2D.Double(x - handleSize / 2, y - handleSize / 2, handleSize, handleSize);
    float factor = Math.abs(dy) < 0.1 ? 0f : (float) Math.abs(dx / dy);
    g.setColor(DLUtil.BrighterColor(color, factor));
    Line2D line = new Line2D.Double(ls.x, ls.y, x, y);
    g.draw(line);
    g.fill(rect);
  }

  void deco(DLPoint s) {

    double h = handleSize + DLUtil.RangeRandom(1., 10.);

    switch (mode) {
    case CIRCLE_SHAPE:
      if (DLUtil.BooleanRandom()) {
        Ellipse2D e = new Ellipse2D.Double(s.x - h / 2, s.y - h / 2, h, h);
        s.shape = e;
      }
      break;
    case SQUARE_SHAPE:
      if (DLUtil.BooleanRandom()) {
        Path2D r = new Path2D.Double();
        r.moveTo(s.x - h / 2, s.y - h / 2);
        r.lineTo(s.x - h / 2, s.y - h / 2 + h);
        r.lineTo(s.x - h / 2 + h, s.y - h / 2 + h);
        r.lineTo(s.x - h / 2 + h, s.y - h / 2);
        r.closePath();

        AffineTransform tr = AffineTransform.getRotateInstance(DLUtil.RangeRandom(0, 2 * Math.PI), s.x, s.y);
        r.transform(tr);

        s.shape = r;
      }
      break;
    case MIXED_SHAPE:
      if (DLUtil.BooleanRandom()) {
        if (DLUtil.BooleanRandom()) {
          Ellipse2D e = new Ellipse2D.Double(s.x - h / 2, s.y - h / 2, h, h);
          s.shape = e;
        } else {
          Path2D r = new Path2D.Double();
          r.moveTo(s.x - h / 2, s.y - h / 2);
          r.lineTo(s.x - h / 2, s.y - h / 2 + h);
          r.lineTo(s.x - h / 2 + h, s.y - h / 2 + h);
          r.lineTo(s.x - h / 2 + h, s.y - h / 2);
          r.closePath();

          AffineTransform tr = AffineTransform.getRotateInstance(DLUtil.RangeRandom(0, 2 * Math.PI), s.x, s.y);
          r.transform(tr);

          s.shape = r;
        }
      }
      break;
    case NO_SHAPE:
      break;
    }

  }

  Path2D curve() {
    return curve(true);
  }

  Path2D curve(boolean deco) {
    int sz = points.size();
    Path2D p = new Path2D.Double();
    if (sz > 0) {
      fcp = new Point2D.Double[sz - 1];
      scp = new Point2D.Double[sz - 1];
      PolyUtils.GetCurveControlPoints(points, fcp, scp);

      p.moveTo(points.get(0).x, points.get(0).y);

      for (int i = 0; i < sz - 1; i++) {
        Point2D cp1 = fcp[i];
        Point2D cp2 = scp[i];
        DLPoint pt = points.get(i + 1);
        p.curveTo(cp1.getX(), cp1.getY(), cp2.getX(), cp2.getY(), pt.x, pt.y);
        if (deco)
          deco(pt);
      }
    } else {
      p.moveTo(x,  y);
    }
    return p;
  }

  void transform(AffineTransform tr) {
    transformPointList(tr);
    path = curve(false);
  }

}
