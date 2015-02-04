package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.Cos;
import static com.mdu.DrawLine.DLUtil.Pow;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.Sin;
import static com.mdu.DrawLine.DLUtil.TWO_PI;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLKnot extends DLCurve {
  float a = 2f;
  int mode = 0;
  float s = 15f;
  float tours = 3f;

  DLKnot(DLKnot r) {
    super(r);
  }

  public DLKnot(float x, float y) {
    super(x, y);
  }

  @Override
  DLKnot copy() {
    return new DLKnot(this);
  }

  DLPointList p1() {
    DLPointList points = new DLPointList();
    for (float t = 0; t < TWO_PI * tours; t += SAMPLE_PRECISION) {
      float r = s * (a + Pow(2, Cos(4 * t / 3)));
      float x = r * Cos(t);
      float y = r * Sin(t);
      points.add(x, y);
    }
    return points;
  }

  DLPointList p2() {
    DLPointList points = new DLPointList();
    for (float t = 0; t < TWO_PI * tours; t += SAMPLE_PRECISION) {
      float r = s * (2 + Cos(8 * t / 5));
      float x = r * Cos(t);
      float y = r * Sin(t);
      points.add(x, y);
    }
    return points;
  }

  @Override
  Path2D path() {
    DLPointList points;
    switch (mode) {
    case 0:
    default:
      points = p1();
      break;
    case 1:
      points = p2();
      break;
    }

    Path2D.Float p = null;
    if (smooth)
      p = toSpline(points);
    else {
      p = toPath(points);
    }
    transform(p);
    return p;
  }

  public float[] rangeA() {
    return new float[] { 1, 5 };
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

  public void setMode(int mode) {
    Rectangle r = redisplayStart();
    this.mode = mode;
    clear();
    redisplay(r);
  }

  public float getS() {
    return s;
  }

  public float[] rangeS() {
    return new float[] { 10, 15 };
  }

  public void setS(float s) {
    Rectangle r = redisplayStart();
    this.s = s;
    clear();
    redisplay(r);
  }

  public float getTours() {
    return tours;
  }

  public void setTours(float tours) {
    Rectangle r = redisplayStart();
    this.tours = tours;
    clear();
    redisplay(r);
  }

  public float[] rangeTours() {
    return new float[] { 0, 10 };
  }

  @Override
  public void randomize() {
    super.randomize();
    a = RangeRandom(1, 5);
    s = RangeRandom(10f, 15f);
    mode = DLUtil.RangeRandom(0, 2);
  }

}
