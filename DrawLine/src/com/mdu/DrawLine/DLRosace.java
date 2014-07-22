package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

class DLRosace extends DLCurve {
  float a;
  int n;

  public DLRosace(int x, int y) {
    super(x, y);
  }

  public DLRosace(DLRosace r) {
    super(r);
    a = r.a;
    n = r.n;
  }

  DLRosace copy() {
    return new DLRosace(this);
  }

  Path2D path() {
    Path2D p = null;
    for (double t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 10) {
      double r = a * sin(n * t);
      double x = r * sin(t);
      double y = r * cos(t);
      p = DLUtil.AddPoint(x, y, p);      
    }
    p.closePath();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    a = DLUtil.RangeRandom(20f, 50f);
    n = DLUtil.RangeRandom(2, 10);
  }

}
