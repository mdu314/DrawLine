package com.mdu.DrawLine;

import java.awt.geom.Path2D;

 class DLPolygon extends DLCurve{
  float radius;
  int sides;

  DLPolygon(DLPolygon p) {
    super(p);
    radius = p.radius;
    sides = p.sides;
  }
  
  public DLPolygon(int x, int y) {
    super(x, y);
  }

  DLPolygon(int x, int y, float radius, int sides, float alpha) {
    super(x, y);
    this.radius = radius;
    this.sides = sides;
  }

  DLPolygon copy() {
    return new DLPolygon(this);
  }
  
  Path2D path() {
    Path2D path = null;
    double a = 2 * Math.PI / sides;

    for (int i = 0; i < sides; i++) {
      float x = radius * (float) Math.cos(i * a);
      float y = radius * (float) Math.sin(i * a);
      path = DLUtil.AddPoint(x, y, path);
    }
    path.closePath();
    transform(path);
    return path;
  }

  public void randomize() {
    super.randomize();
    sides = DLUtil.RangeRandom(3, 10);
    radius = DLUtil.RangeRandom(5, 50);
  }

}
