package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.AddPoint;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.pow;
import static java.lang.Math.E;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLPapillon extends DLCurve {

  private float scale = 2f;
  int tours = 1;

  DLPapillon(DLPapillon e) {
    super(e);
    scale = e.scale;
  }

  public DLPapillon(int x, int y) {
    super(x, y);
  }

  public DLPapillon(int x, int y, float s) {
    super(x, y);
    scale = s;
  }

  public DLPapillon copy() {
    return new DLPapillon(this);
  }

  Path2D path() {
    Path2D p = null;

    for (double t = 0; t < tours * 2 * PI; t += DLParams.SAMPLE_PRECISION / 10) {
      // double r = pow(E, cos(t)) - 2 * cos(4 * t) + pow(sin(t / 12), 5);
      double r = -3 * cos(2 * t) + sin(7 * t) - 1;
      double x = scale * r * cos(t);
      double y = scale * r * sin(t);
      p = AddPoint(x, y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    scale = RangeRandom(7f, 15f);
    // tours = RangeRandom(1, 5);
  }

}
