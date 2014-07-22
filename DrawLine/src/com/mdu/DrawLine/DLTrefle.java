package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLTrefle extends DLCurve {
  float scale = 1f;
int n = 3;

  DLTrefle(DLTrefle e) {
    super(e);
  }

  public DLTrefle(int x, int y) {
    super(x, y);
  }

  DLTrefle(int x, int y, float a, float b, float d) {
    super(x, y);
  }

  public DLTrefle copy() {
    return new DLTrefle(this);
  }

  Path2D path() {
    Path2D p = null;
    for (double t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 10) {
      double sint = sin(t);
      double cost = cos(t);
      double cosnt = cos(n * t);
      double sinnt = sin(n * t);
      
      double r = 1 + cosnt + sinnt * sinnt;
      double x = scale * r * sint;
      double y = scale * r * cost;
      p = DLUtil.AddPoint(x * scale, y * scale, p);
    }

    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    scale = RangeRandom(3f, 4f);
    n = RangeRandom(3,  15);
  }

}
