package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.Cos;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.Sin;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLRose extends DLCurve {
  float sx = 60;
  float sy = 60;
  int k = 15;

  DLRose(DLRose r) {
    super(r);
    sx = r.sx;
    sy = r.sy;
    k = r.k;
  }

  public DLRose(float x, float y) {
    super(x, y);
  }

  DLRose copy() {
    return new DLRose(this);
  }

  @Override
  DLPath path() {
    DLPointList points = new DLPointList();

    for (float t = 0; t < DLUtil.TWO_PI; t += SAMPLE_PRECISION / 5) {

      float x = sx * Cos(k * t) * Sin(t);
      float y = sy * Cos(k * t) * Cos(t);
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
  public void randomize() {
    super.randomize();
    this.sx = RangeRandom(20f, 40f);
    this.sy = sx; //RangeRandom(20f, 40f);
    this.k = RangeRandom(2, 10);
  }

  public float getSx() {
    return sx;
  }

  public void setSx(float sx) {
    Rectangle r = redisplayStart();
    this.sx = sx;
    clear();
    redisplay(r);
  }

  float[] rangeSx() {
    return new float[] { 20, 100 };
  }

  public float getSy() {
    return sy;
  }

  public void setSy(float sy) {
    Rectangle r = redisplayStart();
    this.sy = sy;
    clear();
    redisplay(r);
  }

  float[] rangeSy() {
    return new float[] { 20, 100 };
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
    return new int[] { 1, 20 };
  }

}
