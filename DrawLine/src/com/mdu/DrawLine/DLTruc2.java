package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

import javax.swing.RepaintManager;

class DLTruc2 extends DLCurve {

  float a = 10f;
  int mode = 0; // 0, 1, 2, 3, 4
  int tours = 1;

  public float getA() {
    return a;
  }

  public void setA(float a) {
    this.a = a;
  }

  public int getMode() {
    return mode;
  }

  public void setMode(int mode) {
    Rectangle r = redisplayStart();
    this.mode = mode;    
    clearPath();
    clearShadow();
    redisplay(r);
  }

  public int getTours() {
    return tours;
  }

  public void setTours(int tours) {
    this.tours = tours;
  }

  DLTruc2(DLTruc2 e) {
    super(e);
  }

  public DLTruc2(int x, int y) {
    super(x, y);
  }

  DLTruc2(int x, int y, float a, float b, float d) {
    super(x, y);
  }

  public DLTruc2 copy() {
    return new DLTruc2(this);
  }

  int f(boolean b) {
    return b ? 1 : 0;
  }

  Path2D p1() {
    Path2D p = null;

    double r = 0;
    for (double t = 0; t < tours * DLUtil.TWO_PI; t += SAMPLE_PRECISION / 5) {
      switch (mode) {
      case 0:
        r = a * (1 - abs(-sin(6 * (t - 1))) + 2 * (cos(2 * (t - 1))));
        break;
      case 1:
        r = a * (1 - abs(5 * sin(6 * (t - 1))) + 2 * (cos(2 * (t - 1))));
        break;
      case 2:
        r = a * (1 - abs(2 * sin(4 * (2 * t - 3))) + 1.5 * (cos(4 * (3 * t - 3))));
        break;
      case 3:
        r = a * 2 * pow(sin(4 * t - 2), 2) + 4 * pow(cos(2 * t - 5), 2);
        break;
      case 4:
        r = a * (0.5 * pow(sin(t - 2), 2) + 4 * cos(2 * t - 5));
        break;
      }
      double x = r * sin(t);
      double y = r * cos(t);
      p = DLUtil.AddPoint(x, y, p);
    }
    return p;
  }

  Path2D path() {
    Path2D p = p1();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    mode = DLUtil.RangeRandom(0, 5);
    tours = 1;
  }

}
