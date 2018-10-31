package com.mdu.DrawLine;

import java.awt.Rectangle;

class DLStar extends DLCurve {
  int branches;
  float r1;
  float r2;
  float startAngle;

  DLStar(DLStar s) {
    super(s);
    r1 = s.r1;
    r2 = s.r2;
    branches = s.branches;
    startAngle = s.startAngle;
  }

  public DLStar(float x, float y) {
    super(x, y);
  }

  @Override
  DLStar copy() {
    return new DLStar(this);
  }

  @Override
  DLPath path() {
    DLPath p = DLUtil.Star(0, 0, r1, r2, branches, startAngle);
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    r1 = DLUtil.RangeRandom(5, 50);
    r2 = DLUtil.RangeRandom(3, 20);
    branches = DLUtil.RangeRandom(2, 15);
    startAngle = DLUtil.RangeRandom(0f, (float) Math.PI);
  }

  public int getBranches() {
    return branches;
  }

  public void setBranches(int b) {
    Rectangle r = redisplayStart();
    this.branches = b;
    clear();
    redisplay(r);
  }

  public int[] rangeBranches() {
    return new int[] { 2, 30 };
  }

  public float getR1() {
    return r1;
  }

  public void setR1(float r1) {
    Rectangle r = redisplayStart();
    this.r1 = r1;
    clear();
    redisplay(r);
  }

  public float[] rangeR2() {
    return new float[] { 5, 50 };
  }

  public float getR2() {
    return r2;
  }

  public void setR2(float r2) {
    Rectangle r = redisplayStart();
    this.r2 = r2;
    clear();
    redisplay(r);
  }

  public float[] rangeR1() {
    return new float[] { 5, 50 };
  }

  public float getStartAngle() {
    return startAngle;
  }

  public void setStartAngle(float startAngle) {
    Rectangle r = redisplayStart();
    this.startAngle = startAngle;
    clear();
    redisplay(r);
  }

  public float[] rangeStartAngle() {
    return new float[] { 0, (float) Math.PI };
  }
}
