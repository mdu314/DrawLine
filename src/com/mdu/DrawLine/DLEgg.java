package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.Cos;
import static com.mdu.DrawLine.DLUtil.PI;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.SQRT2;
import static com.mdu.DrawLine.DLUtil.Sin;
import static com.mdu.DrawLine.DLUtil.TWO_PI;
import static com.mdu.DrawLine.DLUtil.FastSqrt;

import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

class DLEgg extends DLCurve {
  float a = 60;
  float b = 40;
  float d = 10;
  float sx = 50;
  float sy = 50;

  static final String ZERO = "zero";
  static final String ONE = "one";
  static final String TWO = "two";
  static final String THREE = "three";

  String mode = ONE;

  DLEgg(DLEgg e) {
    super(e);
    a = e.a;
    b = e.b;
    d = e.d;
  }

  public DLEgg(float x, float y) {
    super(x, y);
  }

  @Override
  public DLEgg copy() {
    return new DLEgg(this);
  }

  public float getA() {
    return a;
  }

  public float getB() {
    return b;
  }

  public float getD() {
    return d;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
    Rectangle r = getBounds();
    clear();
    redisplay(r);
  }

  public String[] enumMode() {
    return new String[] { ZERO, ONE, TWO, THREE };
  }

  public float getSx() {
    return sx;
  }

  public void setSx(float sx) {
    Rectangle r = getBounds();
    this.sx = sx;
    clear();
    redisplay(r);
  }

  public float[] rangeSx() {
    return new float[] { 1, 100 };
  }

  public float getSy() {
    return sy;
  }

  public void setSy(float sy) {
    Rectangle r = getBounds();
    this.sy = sy;
    clear();
    redisplay(r);
  }

  public float[] rangeSy() {
    return new float[] { 1, 100 };
  }

  int in(float d) {
    return (int) d;
  }

  @Override
  DLPath path() {
    DLPointList points = null;
    switch (mode) {
    case ZERO:
      points = path0();
      break;
    case ONE:
      points = path1();
      break;
    case TWO:
      points = path2();
      break;
    case THREE:
      points = path3();
      break;
    default:
      break;
    }
    DLPath p = new DLPath();
    if (smooth) {
      final int sz = points.size();
      if (sz > 0) {
        Point2D.Float[] fcp = new Point2D.Float[sz - 1];
        Point2D.Float[] scp = new Point2D.Float[sz - 1];
        PolyUtils.GetCurveControlPoints(points, fcp, scp);

        p.moveTo(points.get(0).x, points.get(0).y);

        for (int i = 0; i < sz - 1; i++) {
          final Point2D cp1 = fcp[i];
          final Point2D cp2 = scp[i];
          final DLPoint pt = points.get(i + 1);
          p.curveTo(cp1.getX(), cp1.getY(), cp2.getX(), cp2.getY(), pt.x, pt.y);

        }
      } else {
        p.moveTo(x, y);
      }
    } else {
      int sz = points.size();
      for (int i = 0; i < sz - 1; i++) {
        DLPoint pt = points.get(i);
        p = DLUtil.AddPoint(pt.x, pt.y, p);
      }
    }
    p.closePath();
    transform(p);
    return p;
  }

  DLPointList path0() {

    DLPointList points = new DLPointList();
    for (float i = 0; i < 2 * PI; i += SAMPLE_PRECISION) {
      float sint = Sin(i);
      float cost = Cos(i);
      float x = cost * (FastSqrt(a * a - d * d * sint * sint) + d * cost);
      float y = b * sint;
      points.add(x, y);
    }
    return points;

  }

  DLPointList path1() {
    DLPointList points = new DLPointList();
    for (float i = 0; i < 2 * PI; i += SAMPLE_PRECISION) {
      float sint = Sin(i);
      float cost = Cos(i);
      float x = cost * (FastSqrt(a * a - d * d * sint * sint) + d * cost);
      float y = b * sint;
      points.add(x, y);
    }
    return points;
  }

  DLPointList path2() {
    DLPointList points = new DLPointList();
    for (float t = 0; t < TWO_PI; t += SAMPLE_PRECISION) {
      float cost = Cos(t);
      float sint = Sin(t);
      float x = cost * (t <= PI ? 1 : 0) + (1 + 2 * cost) * (t > PI ? 1 : 0) * (t <= 5 * PI / 4 ? 1 : 0) + (2 - SQRT2)
          * cost * (t > 5 * PI / 4 ? 1 : 0) * (t <= 7 * PI / 4 ? 1 : 0) + (-1 + 2 * cost) * (t > 7 * PI / 4 ? 1 : 0);

      float y = sint * (t <= PI ? 1 : 0) + 2 * sint * (t > PI ? 1 : 0) * (t <= 5 * PI / 4 ? 1 : 0)
          + (-1 + (2 - SQRT2) * sint) * (t > 5 * PI / 4 ? 1 : 0) * (t <= 7 * PI / 4 ? 1 : 0) + 2 * sint
          * (t > 7 * PI / 4 ? 1 : 0);

      points.add(sx * x, sy * y);
    }
    return points;
  }

  DLPointList path3() {
    DLPointList points = new DLPointList();
    for (float t = 0; t < 2 * PI; t += SAMPLE_PRECISION) {
      float cost = Cos(t);
      float sint = Sin(t);
      float x = cost * (1 - in(t / PI)) + (1 + 2 * cost) * in(t / PI) * (1 - in(4 * t / 5 / PI)) + (2 - SQRT2) * cost
          * in(4 * t / 5 / PI) * (1 - in(4 * t / 7 / PI)) + (-1 + 2 * cost) * in(4 * t / 7 / PI) * (1 - in(t / 2 / PI));

      float y = sint * (1 - in(t / PI)) + 2 * sint * in(t / PI) * (1 - in(4 * t / 5 / PI)) + (-1 + (2 - SQRT2) * sint)
          * in(4 * t / 5 / PI) * (1 - in(4 * t / 7 / PI)) + 2 * sint * in(4 * t / 7 / PI) * (1 - in(t / 2 / PI));

      points.add(sx * x, sy * y);
    }
    return points;
  }

  @Override
  public void randomize() {
    super.randomize();
    this.a = RangeRandom(30, 50);
    this.b = RangeRandom(20, 40);
    this.d = RangeRandom(5, 20);
  }

  public float[] rangeA() {
    return new float[] { 30, 100 };
  }

  float[] rangeB() {
    return new float[] { 20, 100 };
  }

  float[] rangeD() {
    return new float[] { 5, 100 };
  }

  public int[] rangeMode() {
    return new int[] { 0, 1, 2 };
  }

  public void setA(float a) {
    Rectangle r = redisplayStart();
    this.a = a;
    clear();
    redisplay(r);
  }

  public void setB(float b) {
    Rectangle r = redisplayStart();
    this.b = b;
    clear();
    redisplay(r);
  }

  public void setD(float d) {
    Rectangle r = redisplayStart();
    this.d = d;
    clear();
    redisplay(r);
  }

}
