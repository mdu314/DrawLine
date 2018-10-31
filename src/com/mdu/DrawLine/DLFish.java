package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.awt.Rectangle;

class DLFish extends DLCurve {
  float a = 2;
  float b = 3;
  float p = 1;

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

  public DLFish(DLFish f) {
    super(f);
    a = f.a;
    b = f.b;
    p = f.p;
  }

  public DLFish(float x, float y) {
    super(x, y);
  }

  @Override
  DLFish copy() {
    return new DLFish(this);
  }

  @Override
  DLPath path() {
    DLPath c = null;
    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION) {
      final double cost = cos(t);
      final double sint = sin(t);
      final double x = a * (p * cost - p * sint * sint / sqrt(2));
      final double y = b * (p * cost * sint);
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

}
