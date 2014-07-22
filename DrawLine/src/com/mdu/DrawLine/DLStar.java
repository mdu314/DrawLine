package com.mdu.DrawLine;

import java.awt.geom.Path2D;

class DLStar extends DLCurve {
  double r1;
  double r2;
  int branches;
  double startAngle;

  DLStar(DLStar s) {
    super(s);
    r1 = s.r1;
    r2 = s.r2;
    branches = s.branches;
    startAngle = s.startAngle;
  }

  public DLStar(int x, int y) {
    super(x, y);
  }

  DLStar(double r1, double r2, int b, double s, int x, int y) {
    super(x, y);
    this.r1 = r1;
    this.r2 = r2;
    this.branches = b;
    this.startAngle = s;
  }

  DLStar copy() {
    return new DLStar(this);
  }

  Path2D path() {
    double TPI = Math.PI * 2;
    double di = TPI / branches;
    double start = Math.PI + startAngle;
    double end = start + TPI;
    Path2D p = null;
    for (double i = start; i < end; i += di) {
      double c1 = Math.cos(i);
      double s1 = Math.sin(i);
      double i2 = i + di / 2;
      double c2 = Math.cos(i2);
      double s2 = Math.sin(i2);

      double x = s1 * r1;
      double y = c1 * r1;
      p = DLUtil.AddPoint(x, y, p);

      x = s2 * r2;
      y = c2 * r2;
      p.lineTo(x, y);
    }
    p.closePath();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    r1 = DLUtil.RangeRandom(5, 50);
    r2 = DLUtil.RangeRandom(3, 20);
    branches = DLUtil.RangeRandom(2, 15);
    startAngle = Math.random() * Math.PI;
  }

}
