package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.*;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

class DLCross extends DLCurve {
  DLPoint.Float p1 = new DLPoint.Float(10, 10);
  DLPoint.Float p2 = new DLPoint.Float(50, 15);
  DLPoint.Float p3 = new DLPoint.Float(40, 0);
  int off = 10;

  DLCross(DLCross p) {
    super(p);
  }

  public DLCross(float x, float y) {
    super(x, y);
  }

  @Override
  DLCross copy() {
    return new DLCross(this);
  }

  @Override
  DLPath path() {
    DLPath path = new DLPath(Path2D.WIND_EVEN_ODD);

    for (float t = TWO_PI; t > 0; t -= PI / 2) {
      DLPoint rp1 = DLUtil.Rotate(p1.x, p1.y, t);
      DLPoint rp2 = DLUtil.Rotate(p2.x, p2.y, t);
      DLPoint rp3 = DLUtil.Rotate(p3.x, p3.y, t);

      path = DLUtil.AddPoint(rp1.x, rp1.y, path);
      path = DLUtil.AddPoint(rp2.x, rp2.y, path);
      path = DLUtil.AddPoint(rp3.x, rp3.y, path);

      rp1 = DLUtil.Rotate(p1.x, -p1.y, t);
      rp2 = DLUtil.Rotate(p2.x, -p2.y, t);
      rp3 = DLUtil.Rotate(p3.x, -p3.y, t);

      path = DLUtil.AddPoint(rp3.x, rp3.y, path);
      path = DLUtil.AddPoint(rp2.x, rp2.y, path);
      path = DLUtil.AddPoint(rp1.x, rp1.y, path);
    }
    path.closePath();
    transform(path);
    return path;
  }

  @Override
  public void randomize() {
    setShadow(true);
    p1 = new DLPoint(RangeRandom(5, 15), RangeRandom(5, 15));
    p2 = new DLPoint(RangeRandom(30, 50), RangeRandom(10, 20));
    p3 = new DLPoint(RangeRandom(30, 50), RangeRandom(0, 5));
    super.randomize();
  }

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
    return new Point2D.Float[] { new Point2D.Float(p1.x - off, p1.y - off), new Point2D.Float(p1.x + off, p1.y + off) };
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
    return new Point2D.Float[] { new Point2D.Float(p2.x - off, p2.y - off), new Point2D.Float(p2.x + off, p2.y + off) };
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
    return new Point2D.Float[] { new Point2D.Float(p3.x - off, p3.y - off), new Point2D.Float(p3.x + off, p3.y + off) };
  }
}
