package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLSuperEllipse extends DLCurve {
  float a = 1;
  float b = 1;
  float n = 2;

  public DLSuperEllipse(DLSuperEllipse se) {
    super(se);
    a = se.a;
    b = se.b;
    n = se.n;
  }

  public DLSuperEllipse(int x, int y) {
    super(x, y);
  }

  DLSuperEllipse(int x, int y, float a, float b, float n) {
    super(x, y);
    this.a = a;
    this.b = b;
    this.n = n;
  }

  DLSuperEllipse copy() {
    return new DLSuperEllipse(this);
  }

  int sign(double a) {
    return a < 0 ? -1 : 1;
  }

  Path2D path() {
    Path2D c = new Path2D.Float();
    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION) {
      double cost = cos(t);
      double sint = sin(t);
      double x = Math.pow(Math.abs(cost), 2. / n) * a * sign(cost);
      double y = Math.pow(Math.abs(sint), 2. / n) * b * sign(sint);
      // double x = a * Math.pow(cost, 2. / n);
      // double y = b * Math.pow(sint, 2. / n);
      c = DLUtil.AddPoint(x, y, c);
    }
    c.closePath();
    transform(c);
    return c;
  }

  public void randomize() {
    super.randomize();
    a = RangeRandom(10f, 30f);
    b = RangeRandom(10f, 30f);
    n = RangeRandom(0.3f, 2f);
  }

}
