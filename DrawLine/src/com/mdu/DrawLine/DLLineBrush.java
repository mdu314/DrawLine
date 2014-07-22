package com.mdu.DrawLine;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class DLLineBrush {
  float size = 20;
  float angle = 0;
  Point2D.Float[] brush = null;

  static HashMap<Brush, DLLineBrush> brushList = new HashMap<Brush, DLLineBrush>();

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

  static DLLineBrush getBrush(float s, DLPoint p1, DLPoint p2) {
    Point2D.Float[] p = DLUtil.orthopoints(p1, p2, s / 2);
    double dx = p[1].x - p[0].x;
    double dy = p[1].y - p[0].y;
    double D = Math.sqrt(dx * dx + dy * dy);
    float a = (float) (Math.asin(dy / D));

    return getBrush(s, a);
  }

  static DLLineBrush getBrush(float s, float a) {

    Brush pair = new Brush(s, a);
    DLLineBrush b = brushList.get(pair);

    if (b != null)
      return b;

    b = new DLLineBrush();
    b.size = s;
    b.angle = a;
    Point2D.Float p = new Point2D.Float();
    b.brush = new Point2D.Float[2];

    AffineTransform tr = AffineTransform.getRotateInstance(-a);

    p.setLocation(-s / 2, 0);
    tr.transform(p, p);
    b.brush[0] = new Point2D.Float(p.x, p.y);

    p.setLocation(s / 2, 0);
    tr.transform(p, p);
    b.brush[1] = new Point2D.Float(p.x, p.y);

    brushList.put(pair, b);
    return b;
  }

  public String toString() {
    return "size " + size + " angle " + angle + " brush " + brush[0] + " " + brush[1];
  }

  public boolean equals(Object ob) {
    DLLineBrush b = (DLLineBrush) ob;
    return new Brush(size, angle).equals(new Brush(b.size, b.angle));
  }
}

class Brush {
  float a, b;
  static float min = 0.1f;

  Brush(float a, float b) {
    this.a = a;
    this.b = b;
  }

  public boolean equals(Object op) {
    Brush p = (Brush) op;
    return Math.abs(p.a - a) < min && Math.abs(p.b - b) < min;
  }

  public String toString() {
    return a + " " + b;
  }

}
