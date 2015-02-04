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

  public DLAstroid(float x, float y) {
    super(x, y);
  }

  @Override
  DLAstroid copy() {
    return new DLAstroid(this);
  }

  @Override
  Path2D path() {
    final Path2D c = new Path2D.Float();
    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION) {
      final double cost = cos(t);
      final double sint = sin(t);
      final double x = p * cost * cost * cost;
      final double y = p * sint * sint * sint;
      if (t == 0)
        c.moveTo(x, y);
      else
        c.lineTo(x, y);
    }
    c.closePath();
    final AffineTransform tr = new AffineTransform();
    tr.translate(x, y);
    c.transform(tr);
    return c;
  }

  @Override
  public void randomize() {
    super.randomize();
    p = RangeRandom(10f, 30f);
  }

}
