package com.mdu.DrawLine;

import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLPolygon extends DLCurve {
  float radius;
  int sides;

  DLPolygon(DLPolygon p) {
    super(p);
    radius = p.radius;
    sides = p.sides;
  }

  public DLPolygon(float x, float y) {
    super(x, y);
  }

  @Override
  DLPolygon copy() {
    return new DLPolygon(this);
  }

  public float getRadius() {
    return radius;
  }

  public int getSides() {
    return sides;
  }

  @Override
  DLPath path() {
    DLPath path = null;
    final double a = 2 * Math.PI / sides;

    for (int i = 0; i < sides; i++) {
      final float x = radius * (float) Math.cos(i * a);
      final float y = radius * (float) Math.sin(i * a);
      path = DLUtil.AddPoint(x, y, path);
    }
    path.closePath();
    transform(path);
    return path;
  }

  @Override
  public void randomize() {
    super.randomize();
    sides = DLUtil.RangeRandom(3, 10);
    radius = DLUtil.RangeRandom(5, 50);
  }

  public float[] rangeRadius() {
    return new float[] { 5, 100 };
  }

  public int[] rangeSides() {
    return new int[] { 3, 20 };
  }

  public void setRadius(float radius) {
    Rectangle r = redisplayStart();
    this.radius = radius;
    clear();
    redisplay(r);
  }

  public void setSides(int sides) {
    Rectangle r = redisplayStart();
    this.sides = sides;
    clear();
    redisplay(r);
  }
}
