package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.Cos;
import static com.mdu.DrawLine.DLUtil.Sin;
import static com.mdu.DrawLine.DLUtil.TWO_PI;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLMoebius extends DLCurve {
  float a = 15f;
  int k = 3;

  DLMoebius(DLMoebius e) {
    super(e);
  }

  public DLMoebius(float x, float y) {
    super(x, y);
  }

  public DLMoebius copy() {
    return new DLMoebius(this);
  }

  int f(boolean b) {
    return b ? 1 : 0;
  }

  DLPointList p1() {
    DLPointList points = new DLPointList();

    for (float t = -1; t < 1; t += SAMPLE_PRECISION / 10) {
      for (float v = 0; v < TWO_PI; v += SAMPLE_PRECISION) {

        float x = a * (1 + t * Cos(v / 2) / 2) * Cos(v);
        float y = a * (1 + t * Cos(v / 2) / 2) * Sin(v);
        float z = t * Sin(v / 2) / 2;

        x *= z;
        y *= z;
        points.add(x, y);
      }
    }
    return points;
  }

  DLPointList p2() {

    DLPointList points = new DLPointList();

    for (float t = -1; t < 1; t += SAMPLE_PRECISION / 5) {
      for (float v = 0; v < TWO_PI; v += SAMPLE_PRECISION / 5) {

        float x = a * (2 + t * Cos(k * v)) * Cos(2 * v);
        float y = a * (2 + t * Cos(k * v)) * Sin(2 * v);
        float z = t * Sin(k * v);

        x *= z;
        y *= z;
        points.add(x, y);
      }
    }
    return points;
  }

  @Override
  Path2D path() {
    DLPointList points = p2();
    Path2D.Float p = null;
    if (smooth)
      p = toSpline(points);
    else {
      p = toPath(points);
    }
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    int count = 10;
    while ((k = DLUtil.RangeRandom(3, 20)) % 2 == 0)
      if (count-- < 0)
        break;
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
    return new float[] { 5, 50 };
  }

  public int getK() {
    return k;
  }

  public void setK(int k) {
    Rectangle r = redisplayStart();
    this.k = k;
    clear();
    redisplay(r);
  }

  int[] rangeK() {

    return new int[] { 1, 30 };
  }
}
