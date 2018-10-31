package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLRosace extends DLCurve {
  float a;
  int n;

  public DLRosace(DLRosace r) {
    super(r);
    a = r.a;
    n = r.n;
  }

  public float getA() {
    return a;
  }

  public void setA(float a) {
    Rectangle r = redisplayStart();
    this.a = a;
    clear();
    redisplay(r);
  }

  float[] rangeA() {
    return new float[] { 20, 50 };
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    Rectangle r = redisplayStart();
    this.n = n;
    clear();
    redisplay(r);
  }

  int[] rangeN() {
    return new int[] { 1, 20 };
  }

  public DLRosace(float x, float y) {
    super(x, y);
  }

  @Override
  DLRosace copy() {
    return new DLRosace(this);
  }

  @Override
  DLPath path() {
    DLPath p = null;
    for (double t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 10) {
      final double r = a * sin(n * t);
      final double x = r * sin(t);
      final double y = r * cos(t);
      p = DLUtil.AddPoint(x, y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    a = DLUtil.RangeRandom(20f, 50f);
    n = DLUtil.RangeRandom(2, 10);
  }

}
