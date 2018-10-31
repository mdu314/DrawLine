package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

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

  public DLSuperEllipse(float x, float y) {
    super(x, y);
  }

  DLSuperEllipse copy() {
    return new DLSuperEllipse(this);
  }

  @Override
  DLPath path() {
    DLPath c = new DLPath();
    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION) {
      final double cost = cos(t);
      final double sint = sin(t);
      final double x = Math.pow(Math.abs(cost), 2. / n) * a * sign(cost);
      final double y = Math.pow(Math.abs(sint), 2. / n) * b * sign(sint);
      // double x = a * Math.pow(cost, 2. / n);
      // double y = b * Math.pow(sint, 2. / n);
      c = DLUtil.AddPoint(x, y, c);
    }
    c.closePath();
    transform(c);
    return c;
  }

  @Override
  public void randomize() {
    super.randomize();
    a = RangeRandom(10f, 30f);
    b = RangeRandom(10f, 30f);
    n = RangeRandom(0.3f, 2f);
  }

  int sign(double a) {
    return a < 0 ? -1 : 1;
  }

}
