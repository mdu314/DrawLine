package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.AddPoint;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.pow;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLVonKoch extends DLCurve {
  float a = 1;
  float b = 1;
  int sides = 10;
  float sz = 10;
  int steps = 5;

  public DLVonKoch(DLVonKoch vk) {
    super(vk);
    steps = vk.steps;
    sides = vk.sides;
    sz = vk.sz;
  }

  public DLVonKoch(float x, float y) {
    super(x, y);
  }

  @Override
  DLVonKoch copy() {
    return new DLVonKoch(this);
  }

  @Override
  DLPath path() {

    final int s = (int) (pow(2, 2 * steps) + 1);
    final float[] xs = new float[s];
    final float[] ys = new float[s];

    DLPath p = null;

    final float a = DLUtil.TWO_PI / sides;

    for (int i = 0; i < sides; i++) {
      final float x1 = sz * DLUtil.Sin(i * a);
      final float y1 = sz * DLUtil.Cos(i * a);
      final float x2 = sz * DLUtil.Sin((i + 1) * a);
      final float y2 = sz * DLUtil.Cos((i + 1) * a);

      final int n = side(x1, y1, x2, y2, xs, ys);

      for (int j = 0; j <= n; j++) {
        final float x = xs[j];
        final float y = ys[j];
        p = AddPoint(x, y, p);
      }
    }
    p.closePath();
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    steps = RangeRandom(2, 6);
    sz = RangeRandom(10f, 50f);
    sides = RangeRandom(3, 7);
//    a = RangeRandom(0.9f, 1.1f);
//    b = RangeRandom(0.9f, 1.1f);
  }

  int side(float xs0, float ys0, float xs1, float ys1, float[] xs, float[] ys) {
    final float co = .5f;
    final float si = DLUtil.FastSqrt(3f) / 2f;
    int n = 1;
    xs[0] = xs0;
    ys[0] = ys0;
    xs[1] = xs1;
    ys[1] = ys1;
    int s = steps;
    while (s-- > 0) {
      for (int i = n; i >= 0; i--) {
        xs[4 * i] = xs[i];
        ys[4 * i] = ys[i];
      }
      n = 4 * n;
      for (int i = 0; i <= n - 4; i += 4) {
        final float dx = (xs[i + 4] - xs[i]) / 3;
        final float dy = (ys[i + 4] - ys[i]) / 3;
        xs[i + 1] = xs[i] + dx;
        xs[i + 3] = xs[i] + 2 * dx;
        ys[i + 1] = ys[i] + dy;
        ys[i + 3] = ys[i] + 2 * dy;
        xs[i + 2] = a * (co * dx - si * dy + xs[i + 1]);
        ys[i + 2] = b * (si * dx + co * dy + ys[i + 1]);
      }
    }
    return n;
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
    return new float[] { 0.8f, 1.2f };
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
    return new float[] { 0.8f, 1.2f };
  }

  public int getSides() {
    return sides;
  }

  public void setSides(int sides) {
    Rectangle r = redisplayStart();
    this.sides = sides;
    clear();
    redisplay(r);
  }

  public int[] rangeSides() {
    return new int[] { 2, 12 };
  }

  public float getSz() {
    return sz;
  }

  public void setSz(float size) {
    Rectangle r = redisplayStart();
    this.sz = size;
    clear();
    redisplay(r);
  }

  public float[] rangeSz() {
    return new float[] { 10f, 200f };
  }

  public int getSteps() {
    return steps;
  }

  public void setSteps(int steps) {
    Rectangle r = redisplayStart();
    this.steps = steps;
    clear();
    redisplay(r);
  }

  public int[] rangeSteps() {
    return new int[] { 2, 10 };
  }

}
