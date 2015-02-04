package com.mdu.DrawLine;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

class DLArrow extends DLCurve {
  Point2D.Float p1 = new Point2D.Float(4, 0);
  Point2D.Float p2 = new Point2D.Float(-7, 7);
  Point2D.Float p3 = new Point2D.Float(20, 3);
  Point2D.Float p4 = new Point2D.Float(15, 10);
  Point2D.Float p5 = new Point2D.Float(30, 0);
  float off = 10;

  public Point2D.Float getP1() {
    return p1;
  }

  public void setP1(Point2D.Float p1) {
    Rectangle r = redisplayStart();
    this.p1 = p1;
    clear();
    redisplay(r);
  }

  public Point2D.Float[] rangeP1() {
    return new Point2D.Float[] { 
        new Point2D.Float(p1.x - off, p1.y - off), 
        new Point2D.Float(p1.x + off, p1.y + off) 
    };
  }

  public Point2D.Float getP2() {
    return p2;
  }

  public void setP2(Point2D.Float p2) {
    Rectangle r = redisplayStart();
    this.p2 = p2;
    clear();
    redisplay(r);
  }

  public Point2D.Float[] rangeP2() {
    return new Point2D.Float[] { 
        new Point2D.Float(p2.x - off, p2.y - off), 
        new Point2D.Float(p2.x + off, p2.y + off)
        };
  }

  public Point2D.Float getP3() {
    return p3;
  }

  public void setP3(Point2D.Float p3) {
    Rectangle r = redisplayStart();
    this.p3 = p3;
    clear();
    redisplay(r);
  }

  public Point2D.Float[] rangeP3() {
    return new Point2D.Float[] { 
        new Point2D.Float(p3.x - off, p3.y - off), 
        new Point2D.Float(p3.x + off, p3.y + off) };
  }

  public Point2D.Float getP4() {
    return p4;
  }

  public Point2D.Float[] rangeP4() {
    return new Point2D.Float[] {
        new Point2D.Float(p4.x - off, p4.y - off), 
        new Point2D.Float(p4.x + off, p4.y + off) };
  }

  public void setP4(Point2D.Float p4) {
    Rectangle r = redisplayStart();
    this.p4 = p4;
    clear();
    redisplay(r);
  }

  public Point2D.Float getP5() {
    return p5;
  }

  public void setP5(Point2D.Float p5) {
    Rectangle r = redisplayStart();
    this.p5 = p5;
    clear();
    redisplay(r);
  }

  public Point2D.Float[] rangeP5() {
    return new Point2D.Float[] { 
        new Point2D.Float(p5.x - off, p5.y - off), 
        new Point2D.Float(p5.x + off, p5.y + off) };
  }

  public DLArrow(DLArrow a) {
    super(a);
    p1 = new Point2D.Float(a.p1.x, a.p1.y);
    p2 = new Point2D.Float(a.p2.x, a.p2.y);
    p3 = new Point2D.Float(a.p3.x, a.p3.y);
    p4 = new Point2D.Float(a.p4.x, a.p4.y);
    p5 = new Point2D.Float(a.p5.x, a.p5.y);
  }

  public DLArrow(float x, float y) {
    super(x, y);
  }

  @Override
  DLArrow copy() {
    return new DLArrow(this);
  }

  @Override
  Path2D.Float path() {
    Path2D.Float p = new Path2D.Float();

    p = DLUtil.AddPoint(p1, p);
    p = DLUtil.AddPoint(p2, p);
    p = DLUtil.AddPoint(p3, p);
    p = DLUtil.AddPoint(p4, p);
    p = DLUtil.AddPoint(p5, p);

    p = DLUtil.AddPoint(p5.x, -p5.y, p);
    p = DLUtil.AddPoint(p4.x, -p4.y, p);
    p = DLUtil.AddPoint(p3.x, -p3.y, p);
    p = DLUtil.AddPoint(p2.x, -p2.y, p);
    p = DLUtil.AddPoint(p1.x, -p1.y, p);

    p.closePath();

    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    stroke = DLUtil.RandomColor(0,  1, 0, 1, 0, 1);
    p1 = DLUtil.RangeRandom(2, 6, 0, 2); // new Point2D.Float(4, 0);
    p2 = DLUtil.RangeRandom(-10, 10, 4, 10); // new Point2D.Float(-7, 7);
    p3 = DLUtil.RangeRandom(15, 25, 0, 3); // new Point2D.Float(20, 3);
    p4 = DLUtil.RangeRandom(10, 20, 5, 15); // new Point2D.Float(15, 10);
    p5 = DLUtil.RangeRandom(25, 35, 0, 3); // new Point2D.Float(30, 0);
  }

}
