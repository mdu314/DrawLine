package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLEpitrochoid extends DLCurve {
  float a = 10;
  float b = 10;
  float c = 10;

  public DLEpitrochoid(DLEpitrochoid e) {
    super(e);
    a = e.a;
    b = e.b;
    c = e.c;
  }

  public DLEpitrochoid(int x, int y) {
    super(x, y);
  }

  DLEpitrochoid(int x, int y, float a, float b, float c) {
    super(x, y);
    this.a = a;
    this.b = b;
    this.c = c;
  }

  DLEpitrochoid copy() {
    return new DLEpitrochoid(this);
  }

  Path2D path() {
    Path2D p = new Path2D.Float();

    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION / 10) {
      double cost = cos(t);
      double sint = sin(t);
      double x = (a + b) * cost - c * cos((a / b + 1) * t);
      double y = (a + b) * sint - c * sin((a / b + 1) * t);
      if (t == 0) {
        p.moveTo(x, y);
      } else {
        p.lineTo(x, y);
      }
    }
    p.closePath();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    a = RangeRandom(10, 30);
    b = RangeRandom(1, 3);
    c = RangeRandom(1, 30);
  }

}
