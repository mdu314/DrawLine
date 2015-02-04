package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DEBUG;
import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.Exp;
import static com.mdu.DrawLine.DLUtil.PI;
import static com.mdu.DrawLine.DLUtil.RandomColor;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.Sin;
import static com.mdu.DrawLine.DLUtil.TWO_PI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;

class DLHarmonograph extends DLCurve {
  float tours = 3f;
  float dt = SAMPLE_PRECISION;
  float A1 = 3.08f;
  float A2 = 4.73f;
  float A3 = 4.63f;
  float A4 = 3.37f;
  float f1 = 1.72f;
  float f2 = 3.31f;
  float f3 = 2.3f;
  float f4 = 5.13f;
  float d1 = 0.0172f;
  float d2 = 0.0331f;
  float d3 = 0.023f;
  float d4 = 0.0513f;
  float p1 = 0f;
  float p2 = 1f;
  float p3 = 0.15f;
  float p4 = 0.3f;
  float s = 20;

  public DLHarmonograph(DLHarmonograph l) {
    super(l);
  }

  public DLHarmonograph(float x, float y) {
    super(x, y);
  }

  DLHarmonograph copy() {
    return new DLHarmonograph(this);
  }

  public void paint(Graphics gr) {
    super.paint(gr);
    if (DEBUG) {
      final Graphics2D g = (Graphics2D) gr;
      gr.setColor(Color.red);
      g.fillOval((int) (x - 5), (int) (y - 5), 5, 5);
      final Rectangle b = getBounds();
      g.drawRect(b.x, b.y, b.width - 1, b.height - 1);
    }
  }

  DLPointList p() {
    DLPointList points = new DLPointList();
    for (float t = 0; t < tours * TWO_PI; t += dt) {
      float x = A1 * Sin(t * f1 + p1 * PI) * Exp(-d1 * t) + A2 * Sin(t * f2 + p2 * PI) * Exp(-d2 * t);
      float y = A3 * Sin(t * f3 + p3 * PI) * Exp(-d3 * t) + A4 * Sin(t * f4 + p4 * PI) * Exp(-d4 * t);
      points.add(s * x, s * y);
    }
    return points;
  }

  Path2D path() {
    DLPointList points = p();
    Path2D.Float p = null;
    if (smooth)
      p = toSpline(points);
    else {
      p = toPath(points);
    }
    transform(p);
    return p;
  }

  public void randomize() {
    super.randomize();

    shadow = false;
    smooth = true;

    A1 = RangeRandom(1f, 10f);
    A2 = RangeRandom(1f, 10f);
    A3 = RangeRandom(1f, 10f);
    A4 = RangeRandom(1f, 10f);

    float f = RangeRandom(0, TWO_PI);
    float var = 0.1f;
    f1 = f + RangeRandom(var);
    f2 = f + RangeRandom(var);
    f3 = f + RangeRandom(var);
    f4 = f + RangeRandom(var);

    p1 = RangeRandom(0, TWO_PI);
    p2 = RangeRandom(0, TWO_PI);
    p3 = RangeRandom(0, TWO_PI);
    p4 = RangeRandom(0, TWO_PI);

    d1 = RangeRandom(0, 0.1f);
    d2 = RangeRandom(0, 0.1f);
    d3 = RangeRandom(0, 0.1f);
    d4 = RangeRandom(0, 0.1f);

    s = RangeRandom(7, 12f);

    fill = null;
    if (stroke == null)
      stroke = RandomColor(0.0f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f);
  }

  public float getA1() {
    return A1;
  }

  public void setA1(float a1) {
    Rectangle r = redisplayStart();
    A1 = a1;
    clear();
    redisplay(r);
  }

  public float[] rangeA1() {
    return new float[] { 1, 10 };
  }

  public float getA2() {
    return A2;
  }

  public void setA2(float a2) {
    Rectangle r = redisplayStart();
    A2 = a2;
    clear();
    redisplay(r);
  }

  public float[] rangeA2() {
    return new float[] { 1, 10 };
  }

  public float getA3() {
    return A3;
  }

  public void setA3(float a3) {
    Rectangle r = redisplayStart();
    A3 = a3;
    clear();
    redisplay(r);
  }

  public float[] rangeA3() {
    return new float[] { 1, 10 };
  }

  public float getA4() {
    return A4;
  }

  public void setA4(float a4) {
    Rectangle r = redisplayStart();
    A4 = a4;
    clear();
    redisplay(r);
  }

  public float[] rangeA4() {
    return new float[] { 1, 10 };
  }

  public float getF1() {
    return f1;
  }

  public void setF1(float f1) {
    Rectangle r = redisplayStart();
    this.f1 = f1;
    clear();
    redisplay(r);
  }

  public float[] rangeF1() {
    return new float[] { -0.1f, TWO_PI };
  }

  public float getF2() {
    return f2;
  }

  public void setF2(float f2) {
    Rectangle r = redisplayStart();
    this.f2 = f2;
    clear();
    redisplay(r);
  }

  public float[] rangeF2() {
    return new float[] { -0.1f, TWO_PI };
  }

  public float getF3() {
    return f3;
  }

  public void setF3(float f3) {
    Rectangle r = redisplayStart();
    this.f3 = f3;
    clear();
    redisplay(r);
  }

  public float[] rangeF3() {
    return new float[] { -0.1f, TWO_PI };
  }

  public float getF4() {
    return f4;
  }

  public void setF4(float f4) {
    Rectangle r = redisplayStart();
    this.f4 = f4;
    clear();
    redisplay(r);
  }

  public float[] rangeF4() {
    return new float[] { -0.1f, TWO_PI };
  }

  public float getP1() {
    return p1;
  }

  public void setP1(float p1) {
    Rectangle r = redisplayStart();
    this.p1 = p1;
    clear();
    redisplay(r);
  }

  public float[] rangeP1() {
    return new float[] { 0, 1 };
  }

  public float getP2() {
    return p2;
  }

  public void setP2(float p2) {
    Rectangle r = redisplayStart();
    this.p2 = p2;
    clear();
    redisplay(r);
  }

  public float[] rangeP2() {
    return new float[] { 0, 1 };
  }

  public float getP3() {
    return p3;
  }

  public void setP3(float p3) {
    Rectangle r = redisplayStart();
    this.p3 = p3;
    clear();
    redisplay(r);
  }

  public float[] rangeP3() {
    return new float[] { 0, 1 };
  }

  public float getP4() {
    return p4;
  }

  public void setP4(float p4) {
    Rectangle r = redisplayStart();
    this.p4 = p4;
    clear();
    redisplay(r);
  }

  public float[] rangeP4() {
    return new float[] { 0, 1 };
  }

  public float getS() {
    return s;
  }

  public void setS(float s) {
    Rectangle r = redisplayStart();
    this.s = s;
    clear();
    redisplay(r);
  }

  public float[] rangeS() {
    return new float[] { 2f, 100f };
  }

  public float getTours() {
    return tours;
  }

  public void setTours(float tours) {
    Rectangle r = redisplayStart();
    this.tours = tours;
    clear();
    redisplay(r);
  }

  public float[] rangeTours() {
    return new float[] { 0f, 200f };
  }

  public float getDt() {
    return dt;
  }

  public void setDt(float dt) {
    Rectangle r = redisplayStart();
    this.dt = dt;
    clear();
    redisplay(r);
  }

  float[] rangeDt() {
    return new float[] { 0.1f, 1 };
  }

  public float getD1() {
    return d1;
  }

  public void setD1(float d1) {
    Rectangle r = redisplayStart();
    this.d1 = d1;
    clear();
    redisplay(r);
  }

  float[] rangeD1() {
    return new float[] { -0.1f, 0.5f };
  }

  public float getD2() {
    return d2;
  }

  public void setD2(float d2) {
    Rectangle r = redisplayStart();
    this.d2 = d2;
    clear();
    redisplay(r);
  }

  float[] rangeD2() {
    return new float[] { -0.1f, 0.5f };
  }

  public float getD3() {
    return d3;
  }

  public void setD3(float d3) {
    Rectangle r = redisplayStart();
    this.d3 = d3;
    clear();
    redisplay(r);
  }

  float[] rangeD3() {
    return new float[] { -0.1f, 0.5f };
  }

  public float getD4() {
    return d4;
  }

  public void setD4(float d4) {
    Rectangle r = redisplayStart();
    this.d4 = d4;
    clear();
    redisplay(r);
  }

  float[] rangeD4() {
    return new float[] { -0.1f, 0.5f };
  }

}
