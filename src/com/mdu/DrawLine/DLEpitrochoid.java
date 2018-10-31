package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

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

  public DLEpitrochoid(float x, float y) {
    super(x, y);
  }

  @Override
  DLEpitrochoid copy() {
    return new DLEpitrochoid(this);
  }

  @Override
  DLPath path() {
    final DLPath p = new DLPath();

    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION / 10) {
      final double cost = cos(t);
      final double sint = sin(t);
      final double x = (a + b) * cost - c * cos((a / b + 1) * t);
      final double y = (a + b) * sint - c * sin((a / b + 1) * t);
      if (t == 0)
        p.moveTo(x, y);
      else
        p.lineTo(x, y);
    }
    p.closePath();
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    a = RangeRandom(10, 30);
    b = RangeRandom(1, 3);
    c = RangeRandom(1, 30);
  }

}
