package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLParams.*;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

class DLLorenz extends DLCurve {
  double a = 10.; // or 28
  double b = 28.; // or 46.92
  double c = 8. / 3.; // or 4
  int steps = 10000;
  double scale = 5;
  double dt = 0.001;
  double x0 = 1.;
  double y0 = 1.;
  double z0 = 2.;
  int c1 = 0;
  int c2 = 1;

  public DLLorenz(DLLorenz l) {
    super(l);
    a = l.a;
    b = l.b;
    c = l.c;
    steps = l.steps;
    scale = l.scale;
    dt = l.dt;
    x0 = l.x0;
    y0 = l.y0;
    z0 = l.z0;
    c1 = l.c1;
    c2 = l.c2;
  }

  public DLLorenz(int x, int y) {
    super(x, y);
  }

  DLLorenz(int x, int y, double a, double b, double c, int steps) {
    super(x, y);
    this.a = a;
    this.b = b;
    this.c = c;
    this.steps = steps;
  }

  DLLorenz copy() {
    return new DLLorenz(this);
  }

  double getC1(double x, double y, double z) {
    switch (c1) {
    case 0:
      return x;
    case 1:
      return y;
    case 2:
      return z;
    }
    new Error().printStackTrace();
    return Double.NaN;
  }

  double getC2(double x, double y, double z) {
    switch (c2) {
    case 0:
      return x;
    case 1:
      return y;
    case 2:
      return z;
    }
    new Error().printStackTrace();
    return Double.NaN;
  }

  Path2D p2() {
    Path2D p = new Path2D.Float();

    double x = x0;
    double y = y0;
    double z = z0;
    for (int i = 0; i < steps; i++) {
      double dx = (a * (y - x)) * dt;
      double dy = (x * (b - z) - y) * dt;
      double dz = (x * y - c * z) * dt;
      x += dx;
      y += dy;
      z += dz;
      p = DLUtil.AddPoint(x, y, p);
    }
    return p;
  }

  Path2D p1() {
    Path2D p = new Path2D.Float();
    boolean started = false;
    double x = x0;
    double y = y0;
    double z = z0;
    for (int i = 0; i < steps; i++) {
      double dx = (a * (y - x)) * dt;
      double dy = (x * (b - z) - y) * dt;
      double dz = (x * y - c * z) * dt;
      double c1;
      double c2;
      x += dx;
      y += dy;
      z += dz;
      if (!started) {
        started = true;
        c1 = getC1(x, y, z) * scale;
        c2 = getC2(x, y, z) * scale;
        p.moveTo(c1, c2);
      } else {
        Point2D p2 = p.getCurrentPoint();
        c1 = getC1(x, y, z) * scale;
        c2 = getC2(z, y, z) * scale;
        dx = (c1 - p2.getX());
        dy = (c2 - p2.getY());
        if ((dx * dx + dy * dy) > DRAW_PRECISION)
          p.lineTo(c1, c2);
      }
    }
    p.closePath();
    return p;
  }

  Path2D path() {
    Path2D p = p2();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    steps = RangeRandom(1000, 100000);
    scale = RangeRandom(1., 3.);
    a = RangeRandom(9., 11.);
    b = RangeRandom(27., 29.);
    c = RangeRandom(7. / 3., 9. / 3.);
    x0 = RangeRandom(-1., -1.);
    y0 = RangeRandom(-1., -1.);
    z0 = RangeRandom(-1., -1.);
    ArrayList<Integer> list = new ArrayList<Integer>(Arrays.asList(0, 1, 2));
    int i = RangeRandom(0, 3);
    c1 = list.get(i);
    list.remove(i);
    i = RangeRandom(0, 2);
    c2 = list.get(i);
  }

}
