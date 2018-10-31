package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DLApollon extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;
  Graphics g = new Graphics();

  public DLApollon() {
    super();
  }

  DLApollon(DLApollon src) {
    this();
  }

  public DLApollon(float x, float y) {
    super(x, y);
  }

  DLApollon copy() {
    return new DLApollon(this);
  }

  public void f(Graphics2D g, DLThread t) {
    setup();
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      clear();
      step(g);

      if (parent != null)
        parent.paint(this);

      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          System.err.println(e);
        }
      }
    }
  }

  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  void step(Graphics2D g) {
    this.g.set(g, this);
    draw();
  }

  public void randomize() {
    iwidth = 500;
    iheight = 300;
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] { 0, 100 };
  }

  // From a tutorial here: http://lsandig.org/blog/2014/08/apollon-python/en/
  // Apollonian Gasket, complex number, curvature, recursion, sin()

  Circle[] circs;
  Circle outer;
  ApollonianGasket AG;
  int level, maxLevel;
  float incr, adj;
  boolean clear;

  void setup() {
    circs = new Circle[3];
    AG = new ApollonianGasket();
    level = 0;
    maxLevel = 5; // 5 or 6 in Java
    clear = true;
  }

  void draw() {
    g.fill(0, 20);
    g.noStroke();
    g.rect(0, 0, iwidth, iheight);
    if (clear)
      clearImage();
    g.translate(iwidth * .42f, iheight * .35f);
    firstFour();
    AG.build(outer, circs[0], circs[1], circs[2], level, maxLevel);
  }

  void mousePressed() {
    clear = !clear;
  }

  class ApollonianGasket {

    void build(Circle c1, Circle c2, Circle c3, Circle c4, int current, int max) {
      if (current == max)
        return;

      if (current == 0) {
        Circle result = secSol(c1, c2, c3, c4);
        build(result, c2, c3, c4, 1, max);
      }

      Circle result2 = secSol(c2, c1, c3, c4);
      Circle result3 = secSol(c3, c1, c2, c4);
      Circle result4 = secSol(c4, c1, c2, c3);

      build(result2, c1, c3, c4, current + 1, max);
      build(result3, c1, c2, c4, current + 1, max);
      build(result4, c1, c2, c3, current + 1, max);
    }
  }

  class Circle {
    Complex pos;
    float rad, sze;

    Circle(float xin, float yin, float rin) {
      pos = new Complex(xin, yin);
      rad = rin;
      display();
    }

    float curvature() {
      return 1.0f / rad;
    }

    void display() {
      sze = DLUtil.Abs(rad);
      g.ellipseMode(1);
      float s = g.map(sze, 0, 400, 255, 50);
      if (s < 0.01f)
        s = 0.01f;
      g.stroke(0xE48BFF, s);
      s = g.map(sze, 0, 200, .5f, 15f);
      if (s < 0.01f)
        s = 0.01f;
      g.strokeWeight(s);
      g.noFill();
      g.ellipse(pos.realFloat(), pos.imagFloat(), sze, sze);

      g.strokeWeight(1);
      g.stroke(255);
      if (sze < 5) {
        g.fill(255);
        g.strokeWeight(.5f);
        g.stroke(0);
      }

      g.ellipse(pos.realFloat(), pos.imagFloat(), sze, sze);
    }
  }

  class Complex {
    double x, y;

    Complex(double xin, double yin) {
      x = xin;
      y = yin;
    }

    double real() {
      return x;
    }

    float realFloat() {
      return (float) x;
    }

    double imag() {
      return y;
    }

    float imagFloat() {
      return (float) y;
    }

    double mod() {
      if (x != 0 || y != 0) {
        return Math.sqrt(x * x + y * y);
      } else {
        return 0;
      }
    }

    double arg() {
      return Math.atan2(y, x);
    }

    Complex plus(Complex w) {
      return new Complex(x + w.real(), y + w.imag());
    }

    Complex minus(Complex w) {
      return new Complex(x - w.real(), y - w.imag());
    }

    Complex timesC(Complex w) {
      return new Complex(x * w.real() - y * w.imag(), x * w.imag() + y * w.real());
    }

    // scalar multiplication
    Complex timesS(double alpha) {
      return new Complex(alpha * x, alpha * y);
    }

    Complex divC(Complex w) {
      double den = Math.pow(w.mod(), 2);
      return new Complex((x * w.real() + y * w.imag()) / den, (y * w.real() - x * w.imag()) / den);
    }

    // scalar div
    Complex divS(double alpha) {
      if (alpha == 0) {
        DLUtil.Log("Division by zero attempted in divS");
        return new Complex(x, y);
      } else
        return new Complex(x / alpha, y / alpha);
    }

    Complex sqrtC() {
      double r = Math.sqrt(this.mod());
      double theta = this.arg() / 2;
      return new Complex(r * Math.cos(theta), r * Math.sin(theta));
    }
  }

  void firstFour() {
    incr += -.01;
    adj = 70 * DLUtil.Sin(incr);
    circs = calcThreeCircles(80, 80, 80 + adj);
    outer = calcOuterSoddy(circs[0], circs[1], circs[2]);
  }

  // given 3 radii, calcs 3 externally tangent circles
  Circle[] calcThreeCircles(float r0, float r1, float r2) {
    Circle[] circs = new Circle[3];
    Circle c0, c1, c2;
    float pos2x, pos2y;

    c0 = new Circle(0, 0, r0);
    circs[0] = c0;

    c1 = new Circle(r0 + r1, 0, r1);
    circs[1] = c1;

    pos2x = (r0 * r0 + r0 * r2 + r0 * r1 - r1 * r2) / (r0 + r1);
    pos2y = DLUtil.Sqrt((r0 + r2) * (r0 + r2) - pos2x * pos2x);

    c2 = new Circle(pos2x, pos2y, r2);
    circs[2] = c2;

    return circs;
  }

  // calc enclosing circle (the outer Soddy circle) for 3 externally tangent
  // circles
  Circle calcOuterSoddy(Circle c1in, Circle c2in, Circle c3in) {
    float c1C, c2C, c3C, c4C, c4Rad;
    Complex z1, z2, z3, z4, z5, z6, z7, z8, z9, z10, z11;

    c1C = 1 / c1in.rad;
    c2C = 1 / c2in.rad;
    c3C = 1 / c3in.rad;

    c4C = c1C + c2C + c3C - 2 * (DLUtil.Sqrt((c1C * c2C) + (c2C * c3C) + (c1C * c3C)));
    c4Rad = 1.0f / c4C; // needs making positive when actually drawing circle

    z1 = new Complex(c1in.pos.real(), c1in.pos.imag());
    z2 = new Complex(c2in.pos.real(), c2in.pos.imag());
    z3 = new Complex(c3in.pos.real(), c3in.pos.imag());

    z4 = z1.timesS(c1C).plus(z2.timesS(c2C)).plus(z3.timesS(c3C));
    z5 = z1.timesC(z2).timesS(c1C * c2C);

    z6 = z2.timesC(z3).timesS(c2C * c3C);
    z7 = z1.timesC(z3).timesS(c1C * c3C);

    z8 = z5.plus(z6).plus(z7);
    z9 = z8.sqrtC().timesS(-2);

    z10 = z4.plus(z9);
    z11 = z10.divS(c4C);

    return new Circle(z11.realFloat(), z11.imagFloat(), c4Rad);
  }

  // given four tangent circles, calculate the other (new) one
  // that is tangent to the last three. The firstSol circle touches
  // the other three, but doesn't touch the one to be calculated
  // (the second solution). c1, c2, c3 are the three circles for
  // which the other (new) tangent circle is to be calculated.
  Circle secSol(Circle firstSol, Circle c1, Circle c2, Circle c3) {
    float c1C, c2C, c3C, cFirst, cNew;
    Complex z1, z2, z3, z4, z5, z6, z7, zFirst, zNew;

    cFirst = firstSol.curvature();
    c1C = c1.curvature();
    c2C = c2.curvature();
    c3C = c3.curvature();

    z1 = new Complex(c1.pos.real(), c1.pos.imag());
    z2 = new Complex(c2.pos.real(), c2.pos.imag());
    z3 = new Complex(c3.pos.real(), c3.pos.imag());
    zFirst = new Complex(firstSol.pos.real(), firstSol.pos.imag());

    cNew = (2.0f * (c1C + c2C + c3C)) - cFirst;

    z4 = z1.timesS(c1C).plus(z2.timesS(c2C)).plus(z3.timesS(c3C));
    z5 = z4.timesS(2);
    z6 = zFirst.timesS(cFirst);
    z7 = z5.minus(z6);
    zNew = z7.divS(cNew);

    return new Circle(zNew.realFloat(), zNew.imagFloat(), 1 / cNew);
  }
  
  public static void main(String[] a) {
    Object[][] params = {
        { "iwidth", 800 },
        { "iheight", 400 },
        { "x", 800 / 2 }, 
        { "y", 400 / 2 },
        { "threadSleep", 5 } 
        };

    DLMain.Main(DLApollon.class, params);
  }
}
