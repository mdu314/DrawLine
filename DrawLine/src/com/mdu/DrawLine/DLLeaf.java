package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLLeaf extends DLCurve {
  double a = 0.01;
  double scale = 10;

  DLLeaf(DLLeaf l) {
    super(l);
    a = l.a;
  }

  public DLLeaf(int x, int y) {
    super(x, y);
  }

  DLLeaf(int x, int y, float a) {
    super(x, y);
    this.a = a;
  }

  DLLeaf copy() {
    return new DLLeaf(this);
  }

  Path2D path() {
    Path2D p = null;
    for (float t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 10) {
      double sint = sin(t);
      double cost = cos(t);

      double r = (1 + 0.9 * cos(8 * t)) * (1 + 0.01 * cos(24 * t)) * (0.9 + a * cos(200 * t)) * (1 + sint);
      double x = scale * r * cost;
      double y = scale * r * sint;

      p = DLUtil.AddPoint(x, y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    scale = RangeRandom(15, 30);
    a = RangeRandom(0.01, 0.6);
  }

}
