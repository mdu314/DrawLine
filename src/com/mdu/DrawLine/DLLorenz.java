package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DEBUG;
import static com.mdu.DrawLine.DLUtil.Cos;
import static com.mdu.DrawLine.DLUtil.RandomColor;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.Sin;
import static com.mdu.DrawLine.DLUtil.TWO_PI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;

class DLLorenz extends DLCurve {
  float a = 10.f; // or 28
  float a1 = (float) (Math.PI / 4);
  float a2 = (float) (Math.PI / 4);
  float a3 = (float) (Math.PI / 4);
  float b = 28.f; // or 46.92
  float c = 8.f / 3.f; // or 4
  int c1 = 0;
  int c2 = 1;
  float d = 50;
  float dt = 0.005f;
  float scale = 5;
  int steps = 10000;
  float x0 = 1.f;
  float y0 = 1.f;
  float z0 = 2.f;

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

  public DLLorenz(float x, float y) {
    super(x, y);
  }

  DLLorenz copy() {
    return new DLLorenz(this);
  }

  float getC1(float x, float y, float z, float d) {
    switch (c1) {
    case 0:
      return x / d;
    case 1:
      return y / d;
    case 2:
      return z / d;
    }
    new Error().printStackTrace();
    return Float.NaN;
  }

  float getC2(float x, float y, float z, float d) {
    switch (c2) {
    case 0:
      return x / d;
    case 1:
      return y / d;
    case 2:
      return z / d;
    }
    new Error().printStackTrace();
    return Float.NaN;
  }

  DLPointList p2() {
    DLPointList points = new DLPointList();

    float x = x0;
    float y = y0;
    float z = z0;
    float nx;
    float ny;
    float nz;
    float c1;
    float c2;
    for (int i = 0; i < steps; i++) {
      final float dx = a * (y - x) * dt;
      final float dy = (x * (b - z) - y) * dt;
      final float dz = (x * y - c * z) * dt;
      
      x += dx;
      y += dy;
      z += dz;

      nx = x * Cos(a1) - y * Sin(a1);
      ny = x * Sin(a1) + y * Cos(a1);

      nx = nx * Cos(a2) + z * Sin(a2);
      nz = -nx * Sin(a2) + z * Cos(a2);

      ny = ny * Cos(a3) - nz * Sin(a3);
      nz = ny * Sin(a3) + nz * Cos(a3);

      c1 = getC1(nx, ny, nz, d) * scale;
      c2 = getC2(nx, ny, nz, d) * scale;

      points.add(c1, c2);
    }
    return points;
  }

  @Override
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

  @Override
  DLPath path() {
    DLPointList points = p2();
    DLPath p = null;
    if (smooth)
      p = toSpline(points);
    else {
      p = toPath(points);
    }
    transform(p);
    return p;
  }

  @Override
  public void randomize() {
    super.randomize();

    fill = null;
    if (stroke == null)
      stroke = RandomColor(0.0f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f);

    steps = RangeRandom(100, 500);
    scale = RangeRandom(50.f, 100.f);
    dt = RangeRandom(0.001f, 0.025f);
    a = RangeRandom(9.f, 11.f);
    b = RangeRandom(27.f, 29.f);
    c = RangeRandom(7.f / 3.f, 9.f / 3.f);
    a1 = RangeRandom(0, TWO_PI);
    a2 = RangeRandom(0, TWO_PI);
    a3 = RangeRandom(0, TWO_PI);
    x0 = RangeRandom(-1.f, -1.f);
    y0 = RangeRandom(-1.f, -1.f);
    z0 = RangeRandom(-1.f, -1.f);
    final ArrayList<Integer> list = new ArrayList<Integer>(Arrays.asList(0, 1, 2));
    int i = RangeRandom(0, 3);
    c1 = list.get(i);
    list.remove(i);
    i = RangeRandom(0, 2);
    c2 = list.get(i);
  }

  public float getA1() {
    return a1;
  }

  public void setA1(float a1) {
    Rectangle r = redisplayStart();
    this.a1 = a1;
    clear();
    redisplay(r);
  }

  public float[] rangeA1() {
    return new float[] { 0, (float) (2 * Math.PI) };
  }

  public float getA2() {
    return a2;
  }

  public void setA2(float a2) {
    Rectangle r = redisplayStart();
    this.a2 = a2;
    clear();
    redisplay(r);
  }

  public float[] rangeA2() {
    return new float[] { 0, (float) (2 * Math.PI) };
  }

  public float getA3() {
    return a3;
  }

  public void setA3(float a3) {
    Rectangle r = redisplayStart();
    this.a3 = a3;
    clear();
    redisplay(r);
  }

  public float[] rangeA3() {
    return new float[] { 0, (float) (2 * Math.PI) };
  }

  public int getSteps() {
    return steps;
  }

  public void setSteps(int steps) {
    Rectangle r = redisplayStart();
    this.steps = steps;
    clear();
    redisplay(r);
  }

  public int[] rangeSteps() {
    return new int[] { 1, 50000 };
  }

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    Rectangle r = redisplayStart();
    this.scale = scale;
    clear();
    redisplay(r);
  }

  public float[] rangeScale() {
    return new float[] { 1, 1000 };
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

  public float[] rangeDt() {
    return new float[] { 0.001f, 0.25f };
  }
}
