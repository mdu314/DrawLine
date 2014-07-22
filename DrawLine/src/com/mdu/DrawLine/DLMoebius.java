package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

import javax.swing.RepaintManager;

class DLMoebius extends DLCurve {
  int k = 3;
  float a = 15f;

  public float getA() {
    return a;
  }

  public void setA(float a) {
    this.a = a;
  }

  DLMoebius(DLMoebius e) {
    super(e);
  }

  public DLMoebius(int x, int y) {
    super(x, y);
  }

  DLMoebius(int x, int y, float a, float b, float d) {
    super(x, y);
  }

  public DLMoebius copy() {
    return new DLMoebius(this);
  }

  int f(boolean b) {
    return b ? 1 : 0;
  }

  Path2D p1() {
    Path2D p = null;

    for (double t = -1; t < 1; t += 0.4) {
      for (double v = 0; v < DLUtil.TWO_PI; v += SAMPLE_PRECISION) {

        double x = a * (1 + t * cos(v / 2) / 2) * cos(v);
        double y = a * (1 + t * cos(v / 2) / 2) * sin(v);
        double z = t * sin(v / 2) / 2;

        x *= z;
        y *= z;
        if (p != null && v == 0)
          p.moveTo(x, y);
        else
          p = DLUtil.AddPoint(x, y, p);
      }
    }
    return p;
  }

  Path2D p2() {
    Path2D p = null;

    for (double t = -1; t < 1; t += 0.2) {
      for (double v = 0; v < DLUtil.TWO_PI; v += SAMPLE_PRECISION / 5) {

        double x = a * (2 + t * cos(k * v)) * cos(2 * v);
        double y = a * (2 + t * cos(k * v)) * sin(2 * v);
        double z = t * sin(k * v);

        x *= z;
        y *= z;
        if (p != null && v == 0)
          p.moveTo(x, y);
        else
          p = DLUtil.AddPoint(x, y, p);
      }
    }
    return p;
  }

  Path2D path() {
    Path2D p = p2();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    int count = 10;
    while(((k = DLUtil.RangeRandom(3, 20)) % 2) == 0) {
      if(count -- < 0)
        break;
    }
  }

}
