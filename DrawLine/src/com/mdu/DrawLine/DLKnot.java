package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.Path2D;

class DLKnot extends DLCurve {
  float a = 2f;
  float s = 15f;
  float tours = 3f;
boolean kind = false;

  DLKnot(DLKnot r) {
    super(r);
  }

  public DLKnot(int x, int y) {
    super(x, y);
  }

  DLKnot copy() {
    return new DLKnot(this);
  }

  Path2D p2() {

    Path2D pa = null;
    for (float t = 0; t < 2 * PI * 5; t += SAMPLE_PRECISION) {
      double r = s * (2 + cos(8 * t / 5));
      double x = r * cos(t);
      double y = r * sin(t);
      pa = DLUtil.AddPoint(x, y, pa);
    }
    pa.closePath();
    return pa;
  }

  Path2D p1() {

    Path2D pa = null;

    for (float t = 0; t < 2 * PI * tours; t += SAMPLE_PRECISION) {
      double r = s * (a + Math.pow(2, cos(4 * t / 3)));
      double x = r * cos(t);
      double y = r * sin(t);
      pa = DLUtil.AddPoint(x, y, pa);
    }
    pa.closePath();
    return pa;
  }

  Path2D path() {
    Path2D pa;
    if (kind)
      pa = p2();
    else
      pa = p1();
    transform(pa);
    return pa;
  }

  public void randomize() {
    super.randomize();
    a = RangeRandom(1, 5);
    s = RangeRandom(10f, 15f);
    kind = DLUtil.BooleanRandom();
  }

}
