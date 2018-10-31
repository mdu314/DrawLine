package com.mdu.DrawLine;

import java.awt.geom.Path2D;

class DLSplineCircle extends DLCurve {
  static double magic = 0.5522847;
  float max;
  float min;
  // static double magic = 4 * (Math.sqrt(2) - 1) / 3;
  float radius;

  public DLSplineCircle(DLSplineCircle c) {
    super(c);
    radius = c.radius;
    min = c.min;
    max = c.max;
  }

  public DLSplineCircle(float x, float y) {
    super(x, y);
  }

  Path2D circle() {
    final Path2D path = new DLPath();
    path.moveTo(x, y + radius);
    path.curveTo(x + magic * radius, y + radius, x + radius, y + magic * radius, x + radius, y);
    path.curveTo(x + radius, y - magic * radius, x + magic * radius, y - radius, x, y - radius);
    path.curveTo(x - magic * radius, y - radius, x - radius, y - magic * radius, x - radius, y);
    path.curveTo(x - radius, y + magic * radius, x - magic * radius, y + radius, x, y + radius);
    return path;
  }

  Path2D circle(float min, float max) {
    double x1, y1, x2, y2, x3, y3;
    double dx, dy;
    final Path2D p = new DLPath();

    dx = DLUtil.RangeRandom(min, max);
    dy = DLUtil.RangeRandom(min, max);

    final double startx = x + dx;
    final double starty = y + radius + dy;

    p.moveTo(startx, starty);

    dx = DLUtil.RangeRandom(min, max);
    dy = DLUtil.RangeRandom(min, max);
    x1 = x + magic * radius + dx;
    y1 = y + radius + dy;
    x2 = x + radius + dx;
    y2 = y + magic * radius + dy;
    x3 = x + radius + dx;
    y3 = y + dy;
    p.curveTo(x1, y1, x2, y2, x3, y3);

    dx = DLUtil.RangeRandom(min, max);
    dy = DLUtil.RangeRandom(min, max);
    x1 = x + radius + dx;
    y1 = y - magic * radius + dy;
    x2 = x + magic * radius + dx;
    y2 = y - radius + dy;
    x3 = x + dx;
    y3 = y - radius + dy;
    p.curveTo(x1, y1, x2, y2, x3, y3);

    dx = DLUtil.RangeRandom(min, max);
    dy = DLUtil.RangeRandom(min, max);
    x1 = x - magic * radius + dx;
    y1 = y - radius + dy;
    x2 = x - radius + dx;
    y2 = y - magic * radius + dy;
    x3 = x - radius + dx;
    y3 = y + dy;
    p.curveTo(x1, y1, x2, y2, x3, y3);

    dx = DLUtil.RangeRandom(min, max);
    dy = DLUtil.RangeRandom(min, max);
    x1 = x - radius + dx;
    y1 = y + magic * radius + dy;
    x2 = x - magic * radius + dx;
    y2 = y + radius + dy;
    x3 = startx;
    y3 = starty;
    p.curveTo(x1, y1, x2, y2, x3, y3);
    return p;
  }

  DLPath circle0() {
    final DLPath path = new DLPath();
    path.moveTo(0, radius);
    path.curveTo(magic * radius, radius, radius, magic * radius, radius, 0);
    path.curveTo(radius, -magic * radius, magic * radius, -radius, 0, -radius);
    path.curveTo(-magic * radius, -radius, -radius, -magic * radius, -radius, 0);
    path.curveTo(-radius, magic * radius, -magic * radius, radius, 0, radius);
    return path;
  }

  DLPath circle0(float min, float max) {
    double x1, y1, x2, y2, x3, y3;
    final DLPath p = new DLPath();

    final double startx = 0;
    final double starty = radius;

    p.moveTo(startx, starty);

    x1 = magic * radius;
    y1 = radius;
    x2 = radius;
    y2 = magic * radius;
    x3 = radius;
    y3 = 0;
    p.curveTo(x1, y1, x2, y2, x3, y3);

    x1 = radius;
    y1 = -magic * radius;
    x2 = magic * radius;
    y2 = -radius;
    x3 = 0;
    y3 = -radius;
    p.curveTo(x1, y1, x2, y2, x3, y3);

    x1 = -magic * radius;
    y1 = -radius;
    x2 = -radius;
    y2 = -magic * radius;
    x3 = -radius;
    y3 = 0;
    p.curveTo(x1, y1, x2, y2, x3, y3);

    x1 = -radius;
    y1 = magic * radius;
    x2 = -magic * radius;
    y2 = radius;
    x3 = startx;
    y3 = starty;
    p.curveTo(x1, y1, x2, y2, x3, y3);
    return p;
  }

  @Override
  DLSplineCircle copy() {
    return new DLSplineCircle(this);
  }

  @Override
  DLPath path() {
    DLPath p;
    if (min != 0 || max != 0)
      p = circle0(min, max);
    else
      p = circle0();
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    radius = DLUtil.RangeRandom(10, 40);
    if (DLUtil.BooleanRandom()) {
      min = 5;
      max = 10;
    }
  }

}
