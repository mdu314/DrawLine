package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLTruc2 extends DLCurve {

  float a = 10f;
  int mode = 0; // 0, 1, 2, 3, 4
  int tours = 1;

  DLTruc2(DLTruc2 e) {
    super(e);
  }

  public DLTruc2(float x, float y) {
    super(x, y);
  }

  public DLTruc2 copy() {
    return new DLTruc2(this);
  }

  int f(boolean b) {
    return b ? 1 : 0;
  }

  public float getA() {
    return a;
  }

  public int getMode() {
    return mode;
  }

  public int getTours() {
    return tours;
  }

  DLPath p1() {
    DLPath p = null;

    double r = 0;
    for (double t = 0; t < tours * DLUtil.TWO_PI; t += SAMPLE_PRECISION / 5) {
      switch (mode) {
      case 0:
        r = a * (1 - abs(-sin(6 * (t - 1))) + 2 * cos(2 * (t - 1)));
        break;
      case 1:
        r = a * (1 - abs(5 * sin(6 * (t - 1))) + 2 * cos(2 * (t - 1)));
        break;
      case 2:
        r = a * (1 - abs(2 * sin(4 * (2 * t - 3))) + 1.5 * cos(4 * (3 * t - 3)));
        break;
      case 3:
        r = a * 2 * pow(sin(4 * t - 2), 2) + 4 * pow(cos(2 * t - 5), 2);
        break;
      case 4:
        r = a * (0.5 * pow(sin(t - 2), 2) + 4 * cos(2 * t - 5));
        break;
      }
      final double x = r * sin(t);
      final double y = r * cos(t);
      p = DLUtil.AddPoint(x, y, p);
    }
    return p;
  }

  @Override
  DLPath path() {
    final DLPath p = p1();
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    mode = DLUtil.RangeRandom(0, 5);
    tours = 1;
  }

  public void setA(float a) {
    this.a = a;
  }

  public void setMode(int mode) {
    final Rectangle r = redisplayStart();
    this.mode = mode;
    clearPath();
    clearShadow();
    redisplay(r);
  }

  public void setTours(int tours) {
    this.tours = tours;
  }

}
