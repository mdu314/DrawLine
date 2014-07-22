package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.AddPoint;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.ceil;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLVonKoch extends DLCurve {
  int steps = 5;
  int sides = 10;
  double size = 50;
  double a = 1.;
  double b = 1.;

  public DLVonKoch(int x, int y) {
    super(x, y);
  }

  public DLVonKoch(DLVonKoch vk) {
    super(vk);
    steps = vk.steps;
    sides = vk.sides;
    size = vk.size;
  }

  DLVonKoch copy() {
    return new DLVonKoch(this);
  }

  int side(double xs0, double ys0, double xs1, double ys1, double[] xs, double[] ys) {
    double co = .5;
    double si = sqrt(3.) / 2.;
    int n = 1;
    xs[0] = xs0;
    ys[0] = ys0;
    xs[1] = xs1;
    ys[1] = ys1;
    int s = steps;
    while (s-- > 0) {
      for (int i = n; i >= 0; i--) {
        xs[4 * i] = xs[i];
        ys[4 * i] = ys[i];
      }
      n = 4 * n;
      for (int i = 0; i <= n - 4; i += 4) {
        double dx = (xs[i + 4] - xs[i]) / 3;
        double dy = (ys[i + 4] - ys[i]) / 3;
        xs[i + 1] = xs[i] + dx;
        xs[i + 3] = xs[i] + 2 * dx;
        ys[i + 1] = ys[i] + dy;
        ys[i + 3] = ys[i] + 2 * dy;
        xs[i + 2] = a * (co * dx - si * dy + xs[i + 1]);
        ys[i + 2] = b * (si * dx + co * dy + ys[i + 1]);
      }
    }
    return n;
  }

  Path2D path() {

    int sz = (int) ceil(pow(2, 2 * steps) + 1);
    double[] xs = new double[sz];
    double[] ys = new double[sz];

    Path2D p = null;

    double a = (2 * PI) / sides;

    for (int i = 0; i < sides; i++) {
      double x1 = size * sin(i * a);
      double y1 = size * cos(i * a);
      double x2 = size * sin((i + 1) * a);
      double y2 = size * cos((i + 1) * a);

      int n = side(x1, y1, x2, y2, xs, ys);

      for (int j = 0; j <= n; j++) {
        double x = xs[j];
        double y = ys[j];
        p = AddPoint(x, y, p);
      }
    }
    p.closePath();
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();
    steps = RangeRandom(2, 6);
    size = RangeRandom(10., 50.);
    sides = RangeRandom(3, 7);
//    alpha = RangeRandom(0., 1.);
//    beta = RangeRandom(0., 1.);
  }

}
