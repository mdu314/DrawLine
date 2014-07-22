package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLEgg extends DLCurve {
  float a = 60;
  float b = 40;
  float d = 10;

  public float getA() {
    return a;
  }

  public void setA(float a) {
    setProp("a", a);
  }

  public float getB() {
    return b;
  }

  public void setB(float b) {
    setProp("b", b);
  }

  public float getD() {
    return d;
  }

  public void setD(float d) {
    setProp("d", d);
  }

  DLEgg(DLEgg e) {
    super(e);
    a = e.a;
    b = e.b;
    d = e.d;
  }

  public DLEgg(int x, int y) {
    super(x, y);
  }

  DLEgg(int x, int y, float a, float b, float d) {
    super(x, y);
    this.a = a;
    this.b = b;
    this.d = d;
  }

  public DLEgg copy() {
    return new DLEgg(this);
  }

  int in(double d) {
    return (int)Math.floor(d);
  }

  Path2D path3() {
    double pi = PI;

    Path2D p = null;
    for (double t = 0; t < 2 * PI; t += SAMPLE_PRECISION ) {
      double cost = Math.cos(t);
      double sint = Math.sin(t);
      double x = cost * (1 - in(t / pi)) + (1 + 2 * cost) * in(t / pi) * (1 - in(4 * t / 5 / pi)) + (2 - Math.sqrt(2))
          * cost * in(4 * t / 5 / pi) * (1 - in(4 * t / 7 / pi)) + (-1 + 2 * cost) * in(4 * t / 7 / pi)
          * (1 - in(t / 2 / pi));

      double y = sint * (1 - in(t / pi)) + 2 * sint * in(t / pi) * (1 - in(4 * t / 5 / pi))
          + (-1 + (2 - Math.sqrt(2)) * sint) * in(4 * t / 5 / pi) * (1 - in(4 * t / 7 / pi)) + 2 * sint
          * in(4 * t / 7 / pi) * (1 - in(t / 2 / pi));
      
      p = DLUtil.AddPoint(a * x, a * y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  Path2D path2() {
    Path2D p = null;
    for (double t = 0; t < 2 * PI; t += SAMPLE_PRECISION / 5) {
      double cost = Math.cos(t);
      double sint = Math.sin(t);
      double x = cost * (t <= PI ? 1 : 0) + (1 + 2 * cost) * (t > PI ? 1 : 0) * (t <= 5 * PI / 4 ? 1 : 0)
          + (2 - Math.sqrt(2)) * cost * (t > 5 * PI / 4 ? 1 : 0) * (t <= 7 * PI / 4 ? 1 : 0) + (-1 + 2 * cost)
          * (t > 7 * PI / 4 ? 1 : 0);

      double y = sint * (t <= PI ? 1 : 0) + 2 * sint * (t > PI ? 1 : 0) * (t <= 5 * PI / 4 ? 1 : 0)
          + (-1 + (2 - Math.sqrt(2)) * sint) * (t > 5 * PI / 4 ? 1 : 0) * (t <= 7 * PI / 4 ? 1 : 0) + 2 * sint
          * (t > 7 * PI / 4 ? 1 : 0);

      p = DLUtil.AddPoint(a * x, a * y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  Path2D path1() {
    Path2D p = new Path2D.Float();
    for (float i = 0; i < 2 * PI; i += SAMPLE_PRECISION) {
      double sint = sin(i);
      double cost = cos(i);
      double x = cost * (sqrt(a * a - d * d * sint * sint) + d * cost);
      double y = b * sint;
      p = DLUtil.AddPoint(x, y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  Path2D path() {
    return path3();
  }

  public void randomize() {
    super.randomize();
    this.a = (float) RangeRandom(30, 50);
    this.b = (float) RangeRandom(20, 40);
    this.d = (float) RangeRandom(5, 20);
  }

}
