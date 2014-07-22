package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import static java.lang.Math.*;
import static com.mdu.DrawLine.DLUtil.*;

class DLLemniscate extends DLCurve {
  float a = 2;
  float b = 3;
  float p = 1;

  public DLLemniscate(DLLemniscate l) {
    super(l);
    a = l.a;
    b = l.b;
    p = l.p;
  }

  public DLLemniscate(int x, int y) {
    super(x, y);
  }

  DLLemniscate(int x, int y, float a, float b, float p) {
    super(x, y);
    this.a = a;
    this.b = b;
    this.p = p;
  }

  DLLemniscate copy() {
    return new DLLemniscate(this);
  }

  Path2D path() {
    Path2D c = null;
    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION) {
      double cost = cos(t);
      double sint = sin(t);
      double x = a * (p * cost / (1 + sint * sint));
      double y = b * (p * cost * sint / (1 + sint * sint));
      c = DLUtil.AddPoint(x, y, c);
    }
    c.closePath();
    transform(c);
    return c;
  }

  public void randomize() {
    super.randomize();
    a = RangeRandom(3, 10);
    b = RangeRandom(3, 10);
    p = RangeRandom(3, 10);
  }

}
