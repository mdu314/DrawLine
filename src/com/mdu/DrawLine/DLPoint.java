package com.mdu.DrawLine;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;

@SuppressWarnings("serial")
class DLPoint extends Point2D.Float {
  DLLineBrush brush;
  DLComponent dlc;
  Shape shape;
  long when;
  Paint paint;
  float vx;
  float vy;

  DLPoint(double x, double y) {
    super((float) x, (float) y);
  }

  DLPoint(float x, float y, long w) {
    super(x, y);
    this.when = w;
  }

  DLPoint(float x, float y, long w, Shape s) {
    super(x, y);
    this.when = w;
    this.shape = s;
  }

  DLPoint(float x, float y, Shape s) {
    super(x, y);
    this.shape = s;
  }

  public String toString() {
    return x + " " + y + " " + when;
  }

  public boolean isContained(DLPoint[] points) {
    int i;
    int j;
    boolean result = false;
    for (i = 0, j = points.length - 1; i < points.length; j = i++) {
      DLPoint pi = points[i];
      DLPoint pj = points[j];
      if ((pi.y > this.y) != (pj.y > this.y) && (this.x < (pj.x - pi.x) * (this.y - pi.y) / (pj.y - pi.y) + pi.x)) {
        result = !result;
      }
    }
    return result;
  }
}
