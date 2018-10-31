package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.AddPoint;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

class DLPapillon extends DLCurve {

  float scale = 2f;

  DLPapillon(DLPapillon e) {
    super(e);
    scale = e.scale;
  }

  public DLPapillon(float x, float y) {
    super(x, y);
  }

  public DLPapillon copy() {
    return new DLPapillon(this);
  }

  @Override
  DLPath path() {
    DLPath p = null;

    for (double t = 0; t < 2 * PI; t += DLParams.SAMPLE_PRECISION / 10) {
      // double r = pow(E, cos(t)) - 2 * cos(4 * t) + pow(sin(t / 12), 5);
      final double r = -3 * cos(2 * t) + sin(7 * t) - 1;
      final double x = scale * r * cos(t);
      final double y = scale * r * sin(t);
      p = AddPoint(x, y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    scale = RangeRandom(7f, 15f);
  }

}
