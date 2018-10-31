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

  public DLBug(float x, float y) {
    super(x, y);
  }

  @Override
  public DLBug copy() {
    return new DLBug(this);
  }

  int f(boolean b) {
    return b ? 1 : 0;
  }

  @Override
  DLPath path() {
    DLPath p = null;
    for (double t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 10) {
      final double r = sin(cos(tan(t)));
      final double x = scale * r * cos(t);
      final double y = scale * r * sin(t);
      p = DLUtil.AddPoint(x, y, p);
    }
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    scale = RangeRandom(20, 40);
  }

}
