package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.Cos;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.Sin;
import static com.mdu.DrawLine.DLUtil.TWO_PI;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLGear extends DLCurve {
  private float a = 60;
  private float k = 10;
  private int n = 4;
  private float q = 7;

  DLGear(DLGear g) {
    super(g);
    a = g.a;
    n = g.n;
    k = g.k;
    q = g.q;
  }

  public DLGear(float x, float y) {
    super(x, y);
  }

  DLGear copy() {
    return new DLGear(this);
  }

  private float fh(float x) {
    return x / (1 + (x < 0 ? -x : x));
  }

  private float fH(float t) {
    return fh(k * Sin(n * t)) / q;
  }

  // http://www.mathcurve.com/courbes2d/dentelee/dentelee.shtml
  DLPath p() {
    DLPointList points = new DLPointList();
    for (float t = 0; t < TWO_PI; t += SAMPLE_PRECISION / 5) {

      float r = a * (1 + fH(t));
      float x = r * Cos(t);
      float y = r * Sin(t);

      points.add(x, y);
    }
    DLPath p = null;

    if (smooth)
      p = toSpline(points);
    else {
      p = toPath(points);
    }
    p.closePath();
    transform(p);
    return p;
  }

  @Override
  DLPath path() {
    return p();
  }

  @Override
  public void randomize() {
    super.randomize();
    a = RangeRandom(50f, 100f);
    n = RangeRandom(2, 20);
    k = RangeRandom(5f, 15f);
    q = RangeRandom(1f, 15f);
  }

  public float getA() {
    return a;
  }

  public void setA(float a) {
    this.a = a;
    Rectangle r = redisplayStart();
    clearPath();
    clearShadow();
    clearSelection();
    redisplay(r);
  }

  public float[] rangeA() {
    return new float[] { 50, 100 };
  }

  public float getK() {
    return k;
  }

  public void setK(float k) {
    this.k = k;
    Rectangle r = redisplayStart();
    clearPath();
    clearShadow();
    clearSelection();
    redisplay(r);
  }

  public float[] rangeK() {
    return new float[] { 5, 15 };
  }

  public int getN() {
    return n;
  }

  public void setN(int n) {
    this.n = n;
    Rectangle r = redisplayStart();
    clearPath();
    clearShadow();
    clearSelection();
    redisplay(r);
  }

  public int[] rangeN() {
    return new int[] { 2, 20 };
  }

  public float getQ() {
    return q;
  }

  public void setQ(float q) {
    this.q = q;
    Rectangle r = redisplayStart();
    clearPath();
    clearShadow();
    clearSelection();
    redisplay(r);
  }

  public float[] rangeQ() {
    return new float[] { 1, 20 };
  }

}
