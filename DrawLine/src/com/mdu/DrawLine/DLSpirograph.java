package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.Path2D;

class DLSpirograph extends DLCurve {
  float tours = 3f;
  float r1 = 100f;
  float r2 = 2f;
  float p = 80f;

  DLSpirograph(DLSpirograph r) {
    super(r);
    this.r1 = r.r1;
    this.r2 = r.r2;
    this.p = r.p;
    this.tours = r.tours;
  }

  public DLSpirograph(int x, int y) {
    super(x, y);
  }

  DLSpirograph(int x, int y, float r1, float r2, float p, float tours) {
    super(x, y);
    this.r1 = r1;
    this.r2 = r2;
    this.p = p;
    this.tours = tours;
  }

  DLSpirograph copy() {
    return new DLSpirograph(this);
  }

  Path2D path() {
    Path2D pa = null;

    for (float t = 0; t < 2 * PI * tours; t += SAMPLE_PRECISION / 5) {

      double x = (r1 - r2) * cos(t) + p * cos((r1 - r2) * t / r2);
      double y = (r1 - r2) * sin(t) - p * sin((r1 - r2) * t / r2);

      pa = DLUtil.AddPoint(x, y, pa);
    }
    transform(pa);
    return pa;
  }

  public void randomize() {
    super.randomize();
    r1 = RangeRandom(20, 40);
    r2 = RangeRandom(2, 20);
    p = RangeRandom(2, 20);
    tours = RangeRandom(1, 20);
  }

  public float getTours() {
    return tours;
  }

  public void setTours(float tours) {
    setProp("tours", tours);
  }

  public float getR1() {
    return r1;
  }

  public void setR1(float r) {
    setProp("r1", r);
  }

  public float getR2() {
    return r2;
  }

  public void setR2(float r) {
    setProp("r2", r);
  }

  public float getP() {
    return p;
  }

  public void setP(float p) {
    setProp("p", p);
  }

}
