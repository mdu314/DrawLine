package com.mdu.DrawLine;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;

class Brush {
  static float min = 0.1f;
  float a, b;

  Brush(float a, float b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public boolean equals(Object op) {
    final Brush p = (Brush) op;
    return Math.abs(p.a - a) < min && Math.abs(p.b - b) < min;
  }

  @Override
  public String toString() {
    return a + " " + b;
  }

}

public class DLLineBrush {
  static HashMap<Brush, DLLineBrush> brushList = new HashMap<Brush, DLLineBrush>();

  static DLLineBrush getBrush(float s, DLPoint p1, DLPoint p2) {
    final Point2D.Float[] p = DLUtil.orthopoints(p1, p2, s / 2);
    final float dx = p[1].x - p[0].x;
    final float dy = p[1].y - p[0].y;
    final float D = DLUtil.FastSqrt(dx * dx + dy * dy);
    final float a = (float) Math.asin(dy / D);

    return getBrush(s, a);
  }

  static DLLineBrush getBrush(float s, float a) {

    final Brush pair = new Brush(s, a);
    DLLineBrush b = brushList.get(pair);

    if (b != null)
      return b;

    b = new DLLineBrush();
    b.size = s;
    b.angle = a;
    final Point2D.Float p = new Point2D.Float();
    b.brush = new Point2D.Float[2];

    final AffineTransform tr = AffineTransform.getRotateInstance(-a);

    p.setLocation(-s / 2, 0);
    tr.transform(p, p);
    b.brush[0] = new Point2D.Float(p.x, p.y);

    p.setLocation(s / 2, 0);
    tr.transform(p, p);
    b.brush[1] = new Point2D.Float(p.x, p.y);

    brushList.put(pair, b);
    return b;
  }

  float angle = 0;

  Point2D.Float[] brush = null;

  float size = 20;

  DLLineBrush() {
    super();
  }

  DLLineBrush(DLLineBrush src) {
    super();
    size = src.size;
    angle = src.angle;
    if (src.brush != null) {
      brush = new Point2D.Float[2];
      Point2D.Float b;
      b = src.brush[0];
      brush[0] = new Point2D.Float(b.x, b.y);
      b = src.brush[1];
      brush[1] = new Point2D.Float(b.x, b.y);
    }
  }

  DLLineBrush copy() {
    return new DLLineBrush(this);
  }

  @Override
  public boolean equals(Object ob) {
    final DLLineBrush b = (DLLineBrush) ob;
    return new Brush(size, angle).equals(new Brush(b.size, b.angle));
  }

  @Override
  public String toString() {
    return "size " + size + " angle " + angle + " brush " + brush[0] + " " + brush[1];
  }
}
