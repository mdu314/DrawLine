package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RandomColor;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DLDragon extends DLCurve {
  int iter = 10;
  int sideSize = 100;

  DLDragon(DLDragon r) {
    super(r);
    iter = r.iter;
  }

  public DLDragon(float x, float y) {
    super(x, y);
  }

  @Override
  public DLDragon copy() {
    return new DLDragon(this);
  }

  public int getIter() {
    return iter;
  }

  public List<Integer> getSequence() {
    final List<Integer> turnSequence = new ArrayList<Integer>();
    for (int i = 0; i < iter; i++) {
      final List<Integer> copy = new ArrayList<Integer>(turnSequence);
      Collections.reverse(copy);
      turnSequence.add(1);
      for (final Integer turn : copy)
        turnSequence.add(-turn);
    }
    return turnSequence;
  }

  public int getSideSize() {
    return sideSize;
  }

  @Override
  DLPath path() {
    DLPath p = null;

    final List<Integer> turns = getSequence();
    final double startingAngle = -iter * (Math.PI / 4);
    final double side = sideSize / Math.pow(2, iter / 2.);

    double angle = startingAngle;
    double x1 = 0;
    double y1 = 0;
    double x2 = x1 + Math.cos(angle) * side;
    double y2 = y1 + Math.sin(angle) * side;
    p = DLUtil.AddPoint(x1, y1, p);
    p = DLUtil.AddPoint(x2, y2, p);

    x1 = x2;
    y1 = y2;
    for (final Integer turn : turns) {
      angle += turn * (Math.PI / 2);
      x2 = x1 + Math.cos(angle) * side;
      y2 = y1 + Math.sin(angle) * side;
      p = DLUtil.AddPoint(x2, y2, p);
      x1 = x2;
      y1 = y2;
    }
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    fill = null;
    if (stroke == null)
      stroke = RandomColor(0.0f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f);
    iter = DLUtil.RangeRandom(5, 15);
    sideSize = DLUtil.RangeRandom(50, 150);
  }

  public int[] rangeIter() {
    return new int[] { 5, 15 };
  }

  public int[] rangeSideSize() {
    return new int[] { 50, 150 };
  }

  public void setIter(int iter) {
    Rectangle r = redisplayStart();
    this.iter = iter;
    clear();
    redisplay(r);
  }

  public void setSideSize(int sideSize) {
    Rectangle r = redisplayStart();
    this.sideSize = sideSize;
    clear();
    redisplay(r);
  }
}
