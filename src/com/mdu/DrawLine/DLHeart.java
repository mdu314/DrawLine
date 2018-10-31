package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLHeart extends DLCurve {
  float scale = 5;

  DLHeart(DLHeart h) {
    super(h);
    scale = h.scale;
  }

  public DLHeart(float x, float y) {
    super(x, y);
  }

  DLHeart copy() {
    return new DLHeart(this);
  }

  DLPath p1() {
    DLPath p = DLUtil.Heart(smooth, scale);
    transform(p);
    return p;
  }

  @Override
  DLPath path() {
    return p1();
  }

  @Override
  public void randomize() {
    super.randomize();
    this.scale = RangeRandom(3, 5);
    smooth = true;
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
    return new float[] { 0.5f, 10 };
  }

}
