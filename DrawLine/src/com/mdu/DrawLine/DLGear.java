package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tanh;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLGear extends DLCurve {
  float a = 60;
  float b = 40;
  float s = 10;
  float n = 4;
  float k = 10;
  float q = 2;

  DLGear(DLGear g) {
    super(g);
    a = g.a;
    b = g.b;
    s = g.s;
    n = g.n;
  }

  public DLGear(int x, int y) {
    super(x, y);
  }

  DLGear(int x, int y, float a, float b, float s, int n) {
    super(x, y);
    this.a = a;
    this.b = b;
    this.s = s;
  }

  DLGear copy() {
    return new DLGear(this);
  }

  Path2D p1() {
    Path2D p = null;
    for (float t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 5) {
      double sint = sin(t);
      double cost = cos(t);

      double r = a + tanh(b * sin(n * t));
      double x = s * r * cost;
      double y = s * r * sint;

      p = DLUtil.AddPoint(x, y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  double fh(double x) {
    return x / (1 + Math.abs(x));
  }

  double fH(double t) {
    return fh(k * sin(n * t)) / q;
  }

  // http://www.mathcurve.com/courbes2d/dentelee/dentelee.shtml
  Path2D p2() {
    Path2D p = null;
    for (float t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 5) {

      double r = a * (1 + fH(t));
      double x = s * r * cos(t);
      double y = s * r * sin(t);

      p = DLUtil.AddPoint(x, y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  Path2D path() {
    return p2();
  }

  public void randomize() {
    super.randomize();
    a = RangeRandom(5, 10);
    b = RangeRandom(20, 40);
    s = RangeRandom(2f, 7f);
    n = RangeRandom(2, 20);
    k = RangeRandom(5, 15);
    q = RangeRandom(1, 4);
  }

}
