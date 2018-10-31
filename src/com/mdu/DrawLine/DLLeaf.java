package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;

class DLLeaf extends DLCurve {
  float a = 0.01f;
  float b = 0.01f;
  float c = 0.9f;
  float d = 0.9f;
  float scale = 10;
  float sp = 0.05f;

  DLLeaf(DLLeaf l) {
    super(l);
    a = l.a;
  }

  public DLLeaf(float x, float y) {
    super(x, y);
  }

  DLLeaf copy() {
    return new DLLeaf(this);
  }

  DLPath path() {
    DLPath p = null;
    for (float t = 0; t < 2 * PI; t += sp) {
      float sint = DLUtil.Sin(t);
      float cost = DLUtil.Cos(t);
      float r = (1f + c * DLUtil.Cos(8 * t)) * (1f + b * DLUtil.Cos(24 * t)) * (d + a * DLUtil.Cos(200 * t)) * (1f + sint);
      float x = scale * r * cost;
      float y = scale * r * sint;
      p = DLUtil.AddPoint(x, y, p);
    }
    p.closePath();
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();
    scale = RangeRandom(15, 30);
    a = RangeRandom(0.01f, 0.6f);
  }

}
