package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import static java.lang.Math.*;
import static com.mdu.DrawLine.DLUtil.*;

class DLFish extends DLCurve {
  float a = 2;
  float b = 3;
  float p = 1;

  public DLFish(DLFish f) {
    super(f);
    a = f.a;
    b = f.b;
    p = f.p;
  }

  public DLFish(int x, int y) {
    super(x, y);
  }

  DLFish(int x, int y, float a, float b, float p) {
    super(x, y);
    this.a = a;
    this.b = b;
    this.p = p;
  }

  DLFish copy() {
    return new DLFish(this);
  }

  Path2D path() {
    Path2D c = null;
    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION) {
      double cost = cos(t);
      double sint = sin(t);
      double x = a * (p * cost - p * sint * sint / sqrt(2));
      double y = b * (p * cost * sint);
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
