package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.Cos;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.Sin;
import static com.mdu.DrawLine.DLUtil.TWO_PI;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLTrefle extends DLCurve {
  int n = 3;
  float scale = 1f;

  DLTrefle(DLTrefle e) {
    super(e);
  }

  public DLTrefle(float x, float y) {
    super(x, y);
  }

  public DLTrefle copy() {
    return new DLTrefle(this);
  }

  @Override
  DLPath path() {
    DLPointList points = new DLPointList();
    
    for (float t = 0; t < TWO_PI; t += SAMPLE_PRECISION / 5) {
      float sint = Sin(t);
      float cost = Cos(t);
      float cosnt = Cos(n * t);
      float sinnt = Sin(n * t);

      float r = 1 + cosnt + sinnt * sinnt;
      float x = scale * r * sint;
      float y = scale * r * cost;
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
    scale = RangeRandom(10f, 30f);
    n = RangeRandom(3, 15);
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

  public int[] rangeN() {
    return new int[] { 1, 20 };
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    Rectangle r = redisplayStart();
    this.scale = scale;
    clear();
    redisplay(r);
  }

  public float[] rangeScale() {
    return new float[] { 10,  100};
  }
}
