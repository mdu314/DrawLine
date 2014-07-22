package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLRose extends DLCurve {
  float a = 60;
  float b = 40;
  float k = 40;

  DLRose(DLRose r) {
    super(r);
    a = r.a;
    b = r.b;
    k = r.k;
  }

  public DLRose(int x, int y) {
    super(x, y);
  }

  DLRose(int x, int y, float a, float b) {
    super(x, y);
    this.a = a;
    this.b = b;
  }

  DLRose copy() {
    return new DLRose(this);
  }

  Path2D path() {
    Path2D p = new Path2D.Float();

    for (float t = 0; t < 2 * PI; t += SAMPLE_PRECISION) {

      double x = a * cos(k * t) * sin(t);
      double y = b * cos(k * t) * cos(t);
      p = DLUtil.AddPoint(x, y, p);

    }
    p.closePath();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    this.a = RangeRandom(20f, 40f);
    this.b = RangeRandom(20f, 40f);
    this.k = RangeRandom(2, 10);
  }

}
