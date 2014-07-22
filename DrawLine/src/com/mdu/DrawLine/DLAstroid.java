package com.mdu.DrawLine;
import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

class DLAstroid extends DLCurve {
  float p = 10;

  public DLAstroid(DLAstroid a) {
    super(a);
    p = a.p;
  }

  public DLAstroid(int x, int y) {
    super(x, y);
  }

  DLAstroid(int x, int y, float p) {
    super(x, y);
    this.p = p;
  }

  DLAstroid copy() {
    return new DLAstroid(this);
  }

  Path2D path() {
    Path2D c = new Path2D.Float();
    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION) {
      double cost = cos(t);
      double sint = sin(t);
      double x = p * cost * cost * cost;
      double y = p * sint * sint * sint;
      if (t == 0) {
        c.moveTo(x, y);
      } else {
        c.lineTo(x, y);
      }
    }
    c.closePath();
    AffineTransform tr = new AffineTransform();
    tr.translate(x, y);
    c.transform(tr);
    return c;
  }

  public void randomize() {
    super.randomize();
    p = RangeRandom(10f, 30f);
  }

}
