package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLLemniscate extends DLCurve {
  float a = 2;
  float b = 3;
  float p = 1;

  public DLLemniscate(DLLemniscate l) {
    super(l);
    a = l.a;
    b = l.b;
    p = l.p;
  }

  public DLLemniscate(float x, float y) {
    super(x, y);
  }

  @Override
  DLLemniscate copy() {
    return new DLLemniscate(this);
  }

  @Override
  DLPath path() {
    DLPath c = null;
    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION) {
      final double cost = cos(t);
      final double sint = sin(t);
      final double x = a * (p * cost / (1 + sint * sint));
      final double y = b * (p * cost * sint / (1 + sint * sint));
      c = DLUtil.AddPoint(x, y, c);
    }
    c.closePath();
    transform(c);
    return c;
  }

  @Override
  public void randomize() {
    super.randomize();
    a = RangeRandom(3, 10);
    b = RangeRandom(3, 10);
    p = RangeRandom(3, 10);
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
    return new float[] { 3, 13 };
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
    return new float[] { 3, 13 };
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
    return new float[] { 1, 13 };
  }

}
