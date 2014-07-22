package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLHeart extends DLCurve {
  float s = 50;

  DLHeart(DLHeart h) {
    super(h);
    s = h.s;
  }

  public DLHeart(int x, int y) {
    super(x, y);
  }

  DLHeart(int x, int y, float s) {
    super(x, y);
    this.s = s;
  }

  DLHeart copy() {
    return new DLHeart(this);
  }

  Path2D path() {
    Path2D p = null;
    for (float t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 10) {
      double sint = sin(t);
      double cost = cos(t);
      double x = s * 16 * sint * sint * sint;
      double y = s * (13 * cost - 5 * cos(2 * t) - 2 * cos(3 * t) - cos(4 * t));
      //      double x = s * (12 * sint - 4 * sin(3 * t));
      //      double y = s * (13 * cost - 5 * cos(2 * t) - 2 * cos(3 * t) - cos(4 * t));
      p = DLUtil.AddPoint(x, y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    this.s = (float) RangeRandom(2, 4);
  }

}
