package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class DLPolyline extends DLSegmentedComponent {

  static final String CIRCLE_SHAPE = "circles";
  static final String MIXED_SHAPE = "mixed";
  static final String NO_SHAPE = "noshape";
  static final String SQUARE_SHAPE = "square";

  Color color = Color.black;

  float handleSize = DLParams.HANDLE_SIZE;
  String mode = CIRCLE_SHAPE;
  Path2D path;
  Point2D.Float[] fcp;
  Point2D.Float[] scp;

  DLPolyline(DLPolyline src) {
    super();
    this.color = src.color;
    this.handleSize = src.handleSize;
    points = (DLPointList) src.points.clone();
  }

  public DLPolyline(float x, float y) {
    super(x, y);
  }

  @Override
  DLPolyline copy() {
    return new DLPolyline(this);
  }

  Path2D curve() {
    return curve(true);
  }

  Path2D curve(boolean deco) {
    final int sz = points.size();
    final DLPath p = new DLPath();
    if (sz > 0) {
      fcp = new Point2D.Float[sz - 1];
      scp = new Point2D.Float[sz - 1];
      PolyUtils.GetCurveControlPoints(points, fcp, scp);

      p.moveTo(points.get(0).x, points.get(0).y);

      for (int i = 0; i < sz - 1; i++) {
        final Point2D cp1 = fcp[i];
        final Point2D cp2 = scp[i];
        final DLPoint pt = points.get(i + 1);
        p.curveTo(cp1.getX(), cp1.getY(), cp2.getX(), cp2.getY(), pt.x, pt.y);
        if (deco)
          deco(pt);
      }
    } else
      p.moveTo(x, y);
    return p;
  }

  void deco(DLPoint s) {
    if (mode == null)
      return;
    final float h = handleSize + DLUtil.RangeRandom(1f, 10f);

    if (mode.equals(CIRCLE_SHAPE)) {
      if (DLUtil.BooleanRandom()) {
        final Ellipse2D e = new Ellipse2D.Float(s.x - h / 2, s.y - h / 2, h, h);
        s.shape = e;
      } else {
        s.shape = null;
      }
    } else if (mode.equals(SQUARE_SHAPE)) {
      if (DLUtil.BooleanRandom()) {
        final Path2D r = new Path2D.Float();
        r.moveTo(s.x - h / 2, s.y - h / 2);
        r.lineTo(s.x - h / 2, s.y - h / 2 + h);
        r.lineTo(s.x - h / 2 + h, s.y - h / 2 + h);
        r.lineTo(s.x - h / 2 + h, s.y - h / 2);
        r.closePath();

        final AffineTransform tr = AffineTransform.getRotateInstance(DLUtil.RangeRandom(0, 2 * Math.PI), s.x, s.y);
        r.transform(tr);

        s.shape = r;
      } else {
        s.shape = null;
      }
    } else if (mode.equals(MIXED_SHAPE)) {
      if (DLUtil.BooleanRandom())
        if (DLUtil.BooleanRandom()) {
          final Ellipse2D e = new Ellipse2D.Float(s.x - h / 2, s.y - h / 2, h, h);
          s.shape = e;
        } else {
          final Path2D r = new Path2D.Float();
          r.moveTo(s.x - h / 2, s.y - h / 2);
          r.lineTo(s.x - h / 2, s.y - h / 2 + h);
          r.lineTo(s.x - h / 2 + h, s.y - h / 2 + h);
          r.lineTo(s.x - h / 2 + h, s.y - h / 2);
          r.closePath();

          final AffineTransform tr = AffineTransform.getRotateInstance(DLUtil.RangeRandom(0, 2 * Math.PI), s.x, s.y);
          r.transform(tr);

          s.shape = r;
        }
    } else if (mode.equals(NO_SHAPE)) {
      s.shape = null;
    } else {
      s.shape = null;
    }

  }

  private void drawCurve(Graphics2D g) {
    if (color != null)
      g.setColor(color);
    g.draw(path);
    if (mode != NO_SHAPE)
      drawPoints(g);
  }

  void drawPoint(Graphics2D g, int i) {
    final DLPoint p = points.get(i);
    if (p.shape != null)
      g.fill(p.shape);
  }

  void drawPoints(Graphics2D g) {
    for (int i = 0; i < points.size(); i++)
      drawPoint(g, i);
  }

  void drawSegment(Graphics2D g, int i) {

    if (i > points.size() || i < 1)
      return;

    final DLPoint ls = points.get(i - 1);
    final DLPoint s = points.get(i);
    final float x = s.x;
    final float y = s.y;
    final float dx = x - ls.x;
    final float dy = y - ls.y;
    final float factor = DLUtil.Abs(dy) < 0.1f ? 0f : DLUtil.Abs(dx / dy);
    if (color != null)
      g.setColor(DLUtil.BrighterColor(color, factor));
    else
      g.setColor(null);
    final Line2D line = new Line2D.Float(ls.x, ls.y, x, y);
    g.draw(line);

//    final Rectangle2D rect = new Rectangle2D.Float(x - handleSize / 2, y - handleSize / 2, handleSize, handleSize);
//    g.fill(rect);
  }

  Rectangle getBounds(boolean deco) {
    if (path == null)
      path = curve();
    Rectangle r = path.getBounds();
    for (int i = 0; i < points.size(); i++) {
      final DLPoint p = points.get(i);
      if (p.shape != null) {
        final Rectangle rs = p.shape.getBounds();
        Rectangle2D.union(r, rs, r);
      }
    }
    if (deco)
      r = addShadowBounds(r);
    return r;
  }

  Rectangle getBounds() {
    return getBounds(true);
  }

  boolean hitTest(Point p) {
    if (!super.hitTest(p))
      return false;
    final float d = DLUtil.MinDistance(points, p.x, p.y);
    if (d > DLParams.SELECT_PRECISION)
      return false;
    return true;
  }

  public void paint(Graphics gr) {
    paint(gr, true);
  }

  public void paint(Graphics gr, boolean deco) {
    final Graphics2D g = (Graphics2D) gr;
    if (path == null)
      path = curve();
    if (deco) {
      shadow(g);
      if (DLParams.DEBUG) {
        final Rectangle r = getBounds();
        g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
      }
    }
//    for (int i = 1; i < points.size(); i++)
//      drawSegment(g, i);
    drawCurve(g);
  }

  public void randomize() {
    setShadow(true);
    mode = DLUtil.Random(new String[] { NO_SHAPE, CIRCLE_SHAPE, SQUARE_SHAPE, MIXED_SHAPE });
    color = DLUtil.RandomColor(0.0f, 1.0f, 0.6f, 0.9f, 0.8f, 1f);
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
    path = curve(true);
    clearShadow();
    if (parent != null)
      parent.paint(getBounds());
  }

  public String[] enumMode() {
    return new String[] { NO_SHAPE, CIRCLE_SHAPE, SQUARE_SHAPE, MIXED_SHAPE };
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color c) {
    color = c;
    if (parent != null)
      parent.paint(getBounds());
  }

  @Override
  void transform(AffineTransform tr) {
    super.transform(tr);
    path = curve(false);
  }

}
