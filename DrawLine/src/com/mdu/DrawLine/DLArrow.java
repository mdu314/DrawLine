package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;

import java.awt.geom.Path2D;

class DLArrow extends DLCurve {

  double w1 = 11;
  double l1 = 4;
  double l2 = -10;
  double l3 = 20;
  double w2 = 4;
  double l4 = 8;
  double w3 = 9;

  public DLArrow(int x, int y) {
    super(x, y);
  }

  public DLArrow(DLArrow a) {
    super(a);
    w1 = a.w1;
    l1 = a.l1;
    l2 = a.l2;
    l3 = a.l3;
    w2 = a.w2;
    l4 = a.l4;
    w3 = a.w3;
  }

  DLArrow copy() {
    return new DLArrow(this);
  }

  Path2D path() {
    Path2D p = new Path2D.Float();
    double x1 = l1;
    double y1 = 0;
    p.moveTo(x1, y1);
    double x2 = l2;
    double y2 = -w1;
    p.lineTo(x2, y2);
    double x3 = l3;
    double y3 = -w2;
    p.lineTo(x3, y3);
    double x4 = l1 + l3 - l4;
    double y4 = -w2 - w3;
    p.lineTo(x4, y4);
    double x5 = l1 + l3 + l4;
    double y5 = 0;
    p.lineTo(x5, y5);

    double x6 = l1 + l3 - l4;
    double y6 = w2 + w3;
    p.lineTo(x6, y6);
    double x7 = l3;
    double y7 = w2;
    p.lineTo(x7, y7);
    double x8 = l2;
    double y8 = w1;
    p.lineTo(x8, y8);
    double x9 = l1;
    double y9 = 0;
    p.lineTo(x9, y9);

    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    w1 = RangeRandom(9, 15);
    l1 = RangeRandom(2, 6);
    l2 = RangeRandom(-6, -15);
    l3 = RangeRandom(15, 35);
    w2 = RangeRandom(2, 6);
    l4 = RangeRandom(4, 10);
    w3 = RangeRandom(5, 15);
  }

}
