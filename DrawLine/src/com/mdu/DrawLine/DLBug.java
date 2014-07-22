package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

import java.awt.geom.Path2D;

class DLBug extends DLCurve {
  float scale = 30f;

  DLBug(DLBug e) {
    super(e);
  }

  public DLBug(int x, int y) {
    super(x, y);
  }

  DLBug(int x, int y, float a, float b, float d) {
    super(x, y);
  }

  public DLBug copy() {
    return new DLBug(this);
  }

  int f(boolean b) {
    return b ? 1 : 0;
  }

  Path2D path() {
    Path2D p = null;
    for (double t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 10) {
      double r = (sin(cos(tan(t))));
      double x = scale * r * cos(t);
      double y = scale * r * sin(t);
      p = DLUtil.AddPoint(x, y, p);
    }
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    scale = (float) RangeRandom(20, 40);
  }

}
