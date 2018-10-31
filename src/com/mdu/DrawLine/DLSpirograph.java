package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.Cos;
import static com.mdu.DrawLine.DLUtil.Random;
import static com.mdu.DrawLine.DLUtil.RandomColor;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.Sin;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLSpirograph extends DLCurve {
  float l = 0.5f;
  float k = 0.5f;
  float S = 100f;
  float tours = 3f;
  float dt = SAMPLE_PRECISION;

  DLSpirograph(DLSpirograph r) {
    super(r);
    this.k = r.k;
    this.l = r.l;
    this.S = r.S;
    this.tours = r.tours;
  }

  public DLSpirograph(float x, float y) {
    super(x, y);
  }

  DLSpirograph copy() {
    return new DLSpirograph(this);
  }

  @Override
  DLPath path() {
    DLPointList points = new DLPointList();
    for (float t = 0; t < DLUtil.TWO_PI * tours; t += dt) {
      float x = S * ((1 - k) * Cos(t) + l * k * Cos((1 - k) * t / k));
      float y = S * ((1 - k) * Sin(t) - l * k * Sin((1 - k) * t / k));
      points.add(x, y);
    }
    DLPath p = null;
    if (smooth)
      p = toSpline(points);
    else
      p = toPath(points);
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    l = Random();
    k = Random();
    S = RangeRandom(50f, 100f);
    tours = RangeRandom(1, 20);
    fill = null;
    if (stroke == null)
      stroke = RandomColor(0.0f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f);
  }

  public float getS() {
    return S;
  }

  public void setS(float S) {
    Rectangle r = redisplayStart();
    this.S = S;
    clear();
    redisplay(r);
  }

  float[] rangeS() {
    return new float[] { 30f, 200f };
  }

  public float getL() {
    return l;
  }

  public void setL(float l) {
    Rectangle r = redisplayStart();
    this.l = l;
    clear();
    redisplay(r);
  }

  float[] rangeL() {
    return new float[] { -5f, 5f };
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

  float[] rangeK() {
    return new float[] { 0f, 1f };
  }

  public void setTours(float tours) {
    Rectangle r = redisplayStart();
    this.tours = tours;
    clear();
    redisplay(r);
  }

  public float getTours() {
    return tours;
  }

  float[] rangeTours() {
    return new float[] { 0f, 500f };
  }

  public float getDt() {
    return dt;
  }

  public void setDt(float dt) {
    Rectangle r = redisplayStart();
    this.dt = dt;
    clear();
    redisplay(r);
  }

  public float[] rangeDt() {
    return new float[] { 0.1f, 10 };
  }
}
