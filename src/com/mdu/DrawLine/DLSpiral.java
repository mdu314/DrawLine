package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.Cos;
import static com.mdu.DrawLine.DLUtil.E;
import static com.mdu.DrawLine.DLUtil.Pow;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.Sin;
import static com.mdu.DrawLine.DLUtil.TWO_PI;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLSpiral extends DLCurve {
  float a = 1;
  float b = 1;
  float k = 1;
  float p = 0.5f;
  float tours = 1;
  static final String ONE = "one";
  static final String TWO = "two";
  static final String THREE = "three";
  String mode = ONE;

  DLSpiral(DLSpiral s) {
    super(s);
    a = s.a;
    b = s.b;
    tours = s.tours;
    p = s.p;
    k = s.k;
    mode = s.mode;
  }

  public DLSpiral(float x, float y) {
    super(x, y);
  }

  @Override
  DLSpiral copy() {
    return new DLSpiral(this);
  }

  // http://www.mathcurve.com/courbes2d/doppler/doppler.htm
  Path2D p1() {
    DLPointList points = new DLPointList();

    for (float t = 0; t < tours * TWO_PI; t += SAMPLE_PRECISION) {
      float i = a * Pow(t, p) * Cos(t);
      float j = a * Pow(t, p) * Sin(t);
      points.add(i, j);
    }
    Path2D.Float p = null;

    if (smooth)
      p = toSpline(points);
    else {
      p = toPath(points);
    }
    return p;
  }

  Path2D p2() {

    DLPointList points = new DLPointList();

    for (float t = 0; t < tours * TWO_PI; t += SAMPLE_PRECISION) {
      float i = a * (t * Cos(t) + k * t);
      float j = a * t * Sin(t);
      points.add(i, j);
    }

    Path2D.Float p = null;
    if (smooth)
      p = toSpline(points);
    else {
      p = toPath(points);
    }
    return p;
  }

  Path2D p3() {
    DLPointList points = new DLPointList();
    
    float r = tours; // < 2 ? tours : 2;
    
    for (float t = 0; t < r * TWO_PI; t += SAMPLE_PRECISION) {
      float i = a * Pow(E, t * p / 10) * Cos(t);
      float j = a * Pow(E, t * p / 10) * Sin(t);
      points.add(i, j);
    }

    Path2D.Float p = null;
    if (smooth)
      p = toSpline(points);
    else {
      p = toPath(points);
    }
    return p;
  }

  @Override
  Path2D path() {
    final Path2D c;
    switch (mode) {
    default:
    case ONE:
      c = p1();
      break;
    case TWO:
      c = p2();
      break;
    case THREE:
      c = p3();
      break;
    }
    transform(c);
    return c;
  }

  @Override
  public void randomize() {
    super.randomize();
    a = RangeRandom(1.5f, 3f);
    b = RangeRandom(1.5f, 3f);
    tours = RangeRandom(2f, 5f);
    p = RangeRandom(0.3f, 1.1f);
    k = RangeRandom(0f, 3f);
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

  public float[] rangeA() {
    return new float[] { 1, 5 };
  }

  public float getB() {
    return b;
  }

  public void setB(float b) {
    Rectangle r = redisplayStart();
    this.b = b;
    clear();
    redisplay(r);
  }

  public float[] rangeB() {
    return new float[] { 1, 5 };
  }

  public float getK() {
    return k;
  }

  public void setK(float k) {
    Rectangle r = redisplayStart();
    this.k = k;
    clear();
    redisplay(r);
  }

  public float[] rangeK() {
    return new float[] { 0, 5 };
  }

  public float getP() {
    return p;
  }

  public void setP(float p) {
    Rectangle r = redisplayStart();
    this.p = p;
    clear();
    redisplay(r);
  }

  public float[] rangeP() {
    return new float[] { 0f, 2f };
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
    return new float[] { 0.1f, 20 };
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    Rectangle r = redisplayStart();
    this.mode = mode;
    clear();
    redisplay(r);
  }

  public String[] enumMode() {
    return new String[] { ONE, TWO, THREE };
  }
}
