package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLTruc extends DLCurve {
  float scale = 1f;

  DLTruc(DLTruc e) {
    super(e);
  }

  public DLTruc(int x, int y) {
    super(x, y);
  }

  DLTruc(int x, int y, float a, float b, float d) {
    super(x, y);
  }

  public DLTruc copy() {
    return new DLTruc(this);
  }

  int f(boolean b) {
    return b ? 1 : 0;
  }

  Path2D path() {
    Path2D p = null;
    for (double t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 10) {
      double r = sin(cos(tan(sin(cos(tan(sin(cos(tan(t)))))))));
      double x = scale * r * sin(t);
      double y = scale * r * cos(t);
      p = DLUtil.AddPoint(x * scale, y * scale, p);
    }

    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    scale = RangeRandom(4f, 6f);
  }

}
