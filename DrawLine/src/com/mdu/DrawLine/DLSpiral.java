package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLSpiral extends DLCurve {
  float a = 1;
  float b = 1;
  float tours = 1;
  float p = 0.5f;
  float k = 1;

  DLSpiral(DLSpiral s) {
    super(s);
    a = s.a;
    b = s.b;
    tours = s.tours;
    p = s.p;
    k = s.k;
  }

  public DLSpiral(int x, int y) {
    super(x, y);
  }

  DLSpiral(int x, int y, float a, float b, int t, float p) {
    super(x, y);
    this.a = a;
    this.b = b;
    this.tours = t;
    this.p = p;
  }

  DLSpiral copy() {
    return new DLSpiral(this);
  }

  double func(double t, float p) {
    return Math.pow(t, p);
  }

  Path2D p2() {
    Path2D p = null;

    for (double t = 0; t < tours * 2 * PI; t += SAMPLE_PRECISION) {
      double i = a * (t * cos(t) + k * t);
      double j = a * t * sin(t);
      p = DLUtil.AddPoint(i, j, p);
    }

    return p;
  }

  // http://www.mathcurve.com/courbes2d/doppler/doppler.htm
  Path2D p1() {
    Path2D c = new Path2D.Float();

    for (double t = 0; t < tours * 2 * PI; t += SAMPLE_PRECISION) {
      double i = a * func(t, p) * cos(t);
      double j = a * func(t, p) * sin(t);
      c = DLUtil.AddPoint(i, j, c);
    }

    return c;
  }

  Path2D path() {
    Path2D c = p2();
    transform(c);
    return c;
  }

  public void randomize() {
    super.randomize();
    a = RangeRandom(1.5f, 3f);
    b = RangeRandom(1.5f, 3f);
    tours = RangeRandom(2f, 5f);
    p = RangeRandom(0.3f, 1.1f);
    k = RangeRandom(0f, 3f);
  }

}
