package com.mdu.DrawLine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class DLSub extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;

  public DLSub() {
    super();
  }

  DLSub(DLSub src) {
    this();
  }

  public DLSub(float x, float y) {
    super(x, y);
  }

  DLSub copy() {
    return new DLSub(this);
  }

  public void f(Graphics2D g, DLThread t) {
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      synchronized (this) {
        clearImage();
        step(g);
      }

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

  Graphics2D g;

  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  void step(Graphics2D g) {
    draw();
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 500);
    iheight = iwidth;
    threadSleep = DLUtil.RangeRandom(1000, 10000);
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] { 0, 10000 };
  }

  boolean fillSwitch = true; // change this to 1 for color instead of line drawing

  public boolean isFillSwitch() {
    return fillSwitch;
  }

  public void setFillSwitch(boolean fillSwitch) {
    this.fillSwitch = fillSwitch;
  }

  int subCut = 2;

  public int getSubCut() {
    return subCut;
  }

  public void setSubCut(int subCut) {
    this.subCut = subCut;
  }

  public int[] rangeSubCut() {
    return new int[] { 1, 50 };
  }

  int Int(float f) {
    return (int) f;
  }

  float random(float a, float b) {
    return DLUtil.RangeRandom(a, b);
  }

  float random(double a, double b) {
    return (float) DLUtil.RangeRandom(a, b);
  }

  float min(float a, float b) {
    return a < b ? a : b;
  }

  float min(float a, float b, float c) {
    return min(min(a, b), min(b, c));
  }

  float max(float a, float b) {
    return a > b ? a : b;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public int[] rangeLevel() {
    return new int[] { 1, 24 };
  }

  int level = 12;

  void draw() {
    rectangle1(iwidth * 0.05f, iheight * 0.05f, iwidth * 0.9f, iheight * 0.9f, level);
  }

  float aspect(float ww, float hh) { // return the aspect ration of the rectangle
    return (max(hh / ww, ww / hh));
  }

  float random(float f) {
    return DLUtil.RangeRandom(0, f);
  }

  void rect(float x, float y, float w, float h) {
    Rectangle2D r = new Rectangle2D.Float(x, y, w, h);
    if (fillColor != null) {
      g.setColor(fillColor);
      g.fill(r);
    }
    if (strokeColor != null) {
      g.setColor(strokeColor);
      g.draw(r);
    }
  }

  Color fillColor;

  void setFill(Color fill) {
    fillColor = fill;
  }

  Color strokeColor;

  void setStroke(Color stroke) {
    strokeColor = stroke;
  }

  void rectangle1(float x1, float y1, float ww, float hh, int level) {
    if ((level < 1) || (min(ww, hh) < 10)) {
      setStroke(Color.black);
      Color fill = null;
      Color stroke = null;
      if (fillSwitch) {
        fill = randColor();
      }
      setStroke(stroke);
      setFill(fill);
      rect(x1, y1, ww, hh);
      return;
    }

    if (aspect(ww, hh) < 3) { // if the rectangle is not too skinny.
      if (random(1) < 0.2) { // cut the rectangle into two vertically
        rectangle1(x1, y1, ww, hh / 2, level - 1);
        rectangle1(x1, y1 + hh / 2, ww, hh / 2, level - 1);
        return;
      }
      if (random(1) < 0.2) { // cut the rectangle in two horizontally
        rectangle1(x1, y1, ww / 2, hh, level - 1);
        rectangle1(x1 + ww / 2, y1, ww / 2, hh, level - 1);
        return;
      }

      if (random(1) < 0.4) { // put three circles in the rectangle
        rectangle1(x1, y1, ww, hh, 0);
        circle1(x1 + ww / 2, y1 + hh / 2, 0.5f * min(ww, hh), level - 1);
        if (ww < hh) {
          float rad = (hh - min(ww, hh)) * 0.25f;
          circle1(x1 + ww / 2, y1 + hh / 2 - 0.5f * min(ww, hh) - rad, rad, level - 1);
          circle1(x1 + ww / 2, y1 + hh / 2 + 0.5f * min(ww, hh) + rad, rad, level - 1);
        } else {
          float rad = (ww - min(ww, hh)) * 0.25f;
          circle1(x1 + ww / 2 + 0.5f * min(ww, hh) + rad, y1 + hh / 2, rad, level - 1);
          circle1(x1 + ww / 2 - 0.5f * min(ww, hh) - rad, y1 + hh / 2, rad, level - 1);
        }
        return;
      }

      if (random(1) < 0.5) { // make a "sub" rectangle
        rectangle1(x1, y1, ww, hh, 0);
        if ((ww > 4 + 2 * subCut) && (hh > 4 + 2 * subCut)) {
          rectangle1(x1 + subCut, y1 + subCut, ww - 2 * subCut, hh - 2 * subCut, level - 1);
        }
        return;
      }

      else { // cut the rectangle into 6 triangles
        rectangle1(x1, y1, ww, hh, 0);
        float rat1 = random(0.2, 0.8);
        float rat2 = random(0.2, 0.8);
        float rat3 = random(0.2, 0.8);
        float rat4 = random(0.2, 0.8);
        triangle1(x1 + rat2 * ww, y1, x1, y1 + rat1 * hh, x1 + ww, y1 + rat3 * hh, level - 1);
        triangle1(x1, y1, x1, y1 + rat1 * hh, x1 + rat2 * ww, y1, level - 1);
        triangle1(x1 + rat2 * ww, y1, x1 + ww, y1, x1 + ww, y1 + rat3 * hh, level - 1);
        triangle1(x1, y1 + rat1 * hh, x1 + ww, y1 + rat3 * hh, x1 + rat4 * ww, y1 + hh, level - 1);
        triangle1(x1, y1 + rat1 * hh, x1, y1 + hh, x1 + rat4 * ww, y1 + hh, level - 1);
        triangle1(x1 + rat4 * ww, y1 + hh, x1 + ww, y1 + hh, x1 + ww, y1 + rat3 * hh, level - 1);
        return;
      }
    } // if the rectangle is too skinny...
    if (hh > ww) { // cut the rectangle into 2 rectangles horizontally
      rectangle1(x1, y1, ww, hh / 2, level - 1);
      rectangle1(x1, y1 + hh / 2, ww, hh / 2, level - 1);
    } else if (hh < ww) { // cut the rectangle into 2 rectangles vertically
      rectangle1(x1, y1, ww / 2, hh, level - 1);
      rectangle1(x1 + ww / 2, y1, ww / 2, hh, level - 1);
    } else {
      rectangle1(x1, y1, ww, hh, 0);
    }

  }

  float sqrt(float v) {
    return DLUtil.FastSqrt(v);
  }

  float dist(float x1, float y1, float x2, float y2) {
    float dx = x2 - x1;
    float dy = y2 - y1;
    return sqrt(dx * dx + dy * dy);
  }

  float triangleSize(float x1, float y1, float x2, float y2, float x3, float y3) {
    // returns the length of the shortest side of the trianlge
    return (min(dist(x1, y1, x2, y2), dist(x1, y1, x3, y3), dist(x2, y2, x3, y3)));
  }

  void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
    Path2D p = new DLPath();
    p.moveTo(x1, y1);
    p.lineTo(x2, y2);
    p.lineTo(x3, y3);
    p.closePath();
    if (fillColor != null) {
      g.setColor(fillColor);
      g.fill(p);
    }
    if (strokeColor != null) {
      g.setColor(strokeColor);
      g.draw(p);
    }
  }

  void triangle1(float x1, float y1, float x2, float y2, float x3, float y3, int level) {
    if ((level < 1) || (triangleSize(x1, y1, x2, y2, x3, y3) < 15)) {
      setStroke(Color.black);
      fillColor = randColor();

      if (fillSwitch) {
        fillColor = randColor();
      }
      if (triangleSize(x1, y1, x2, y2, x3, y3) > 6) {
        triangle(x1, y1, x2, y2, x3, y3);
      }
      return;
    }

    float xx1 = (x1 + x2) / 2;
    float yy1 = (y1 + y2) / 2;
    float xx2 = (x1 + x3) / 2;
    float yy2 = (y1 + y3) / 2;
    float xx3 = (x2 + x3) / 2;
    float yy3 = (y2 + y3) / 2;

    if ((random(1) < 0.2) && (triangleSize(x1, y1, x2, y2, x3, y3) > 15)) { // cut the triangle into four smaller ones
      triangle1(x1, y1, xx1, yy1, xx2, yy2, level - 1);
      triangle1(x2, y2, xx1, yy1, xx3, yy3, level - 1);
      triangle1(xx1, yy1, xx2, yy2, xx3, yy3, level - 1);
      triangle1(xx2, yy2, xx3, yy3, x3, y3, level - 1);
    } else { // inscribe a circle
      triangle1(x1, y1, x2, y2, x3, y3, 0);
      float len1 = dist(x3, y3, x2, y2);
      float len2 = dist(x1, y1, x3, y3);
      float len3 = dist(x2, y2, x1, y1);
      float s = (len1 + len2 + len3) / 2.f;
      float r1 = sqrt((s - len1) * (s - len2) * (s - len3) / s); // inscribed circle radius
      float xCen = (len1 * x1 + len2 * x2 + len3 * x3) / (len1 + len2 + len3); // inscribed circle center x
      float yCen = (len1 * y1 + len2 * y2 + len3 * y3) / (len1 + len2 + len3); // inscribed circle center y

      circle1(xCen, yCen, r1, level - 1); // the inscribed circle

      float d1 = dist(x1, y1, xCen, yCen);
      float r2 = (r1 * (d1 - r1)) / (d1 + r1);
      float weight = (d1 - r1 - r2) / d1;
      float xCen2 = xCen * weight + x1 * (1 - weight);
      float yCen2 = yCen * weight + y1 * (1 - weight);

      circle1(xCen2, yCen2, r2, level - 1);

      d1 = dist(x2, y2, xCen, yCen);
      r2 = (r1 * (d1 - r1)) / (d1 + r1);
      weight = (d1 - r1 - r2) / d1;
      xCen2 = xCen * weight + x2 * (1 - weight);
      yCen2 = yCen * weight + y2 * (1 - weight);

      circle1(xCen2, yCen2, r2, level - 1);

      d1 = dist(x3, y3, xCen, yCen);
      r2 = (r1 * (d1 - r1)) / (d1 + r1);
      weight = (d1 - r1 - r2) / d1;
      xCen2 = xCen * weight + x3 * (1 - weight);
      yCen2 = yCen * weight + y3 * (1 - weight);

      circle1(xCen2, yCen2, r2, level - 1);
    }
  }

  float sin(float v) {
    return (float) Math.sin(v); //DLUtil.Sin(v);
  }

  float cos(float v) {
    return (float) Math.cos(v); //DLUtil.Cos(v);
  }

  void ellipse(float x, float y, float w, float h) {
    Ellipse2D e = new Ellipse2D.Float(x - w / 2, y - h / 2, w, h);
    if (fillColor != null) {
      g.setColor(fillColor);
      g.fill(e);
    }
    if (strokeColor != null) {
      g.setColor(strokeColor);
      g.draw(e);
    }
  }

  float tan(float f) {
    return (float) Math.tan(f);
  }

  void circle1(float xc, float yc, float circRad, int level) {
    float choiceMaker = random(1);
    if ((level < 1) || (circRad < 5)) {
      g.setStroke(new BasicStroke(0));
      if (fillSwitch) {
        fillColor = randColor();
        strokeColor = randColor();
      }
      if (circRad > 3) {
        ellipse(xc, yc, circRad * 2, circRad * 2);
      }
      return;
    }
    if (choiceMaker < 0.15) { // add a "sub" circle concentrically
      circle1(xc, yc, circRad, 0);
      circle1(xc, yc, circRad - subCut, level - 1);
      return;
    }
    if ((choiceMaker < 0.3) && (circRad > 25)) { // create two tangential inscribed circles
      circle1(xc, yc, circRad, 0);
      float ang = random(DLUtil.TWO_PI);
      float ratio = random(0.3, 0.7);
      circle1(xc + (circRad - ratio * circRad) * cos(ang), yc + (circRad - ratio * circRad) * sin(ang),
          circRad * ratio, level - 1);
      circle1(xc + (circRad - (1 - ratio) * circRad) * cos(ang + DLUtil.PI), yc + (circRad - (1 - ratio) * circRad)
          * sin(ang - DLUtil.PI), circRad * (1 - ratio), level - 1);
      return;
    }

    if (choiceMaker < 0.4) { // inscribe a triangle
      float spread = 0.3f;
      float rot = random(0, DLUtil.TWO_PI);
      float ang1 = random(-spread, spread) + rot;
      float ang2 = random(DLUtil.TWO_PI / 3 - spread, DLUtil.TWO_PI / 3 + spread) + rot;
      float ang3 = random(2 * DLUtil.TWO_PI / 3 - spread, 2 * DLUtil.TWO_PI / 3 + spread) + rot;
      //stroke(255,0,0);
      //triangle(xc+circRad*cos(ang1),yc+circRad*sin(ang1),xc+circRad*cos(ang2),yc+circRad*sin(ang2),xc+circRad*cos(ang3),yc+circRad*sin(ang3));
      circle1(xc, yc, circRad, 0);
      triangle1(xc + circRad * cos(ang1), yc + circRad * sin(ang1), xc + circRad * cos(ang2), yc + circRad * sin(ang2),
          xc + circRad * cos(ang3), yc + circRad * sin(ang3), level - 1);
      return;
    }
    if (choiceMaker < 0.6) { // put a rectangle in the circle, with two smaller circles
      float ang1 = random(DLUtil.PI / 4 - 0.3, DLUtil.PI / 3 + 0.3);
      circle1(xc, yc, circRad, 0);
      rectangle1(xc - circRad * cos(ang1), yc - circRad * sin(ang1), 2 * circRad * cos(ang1), 2 * circRad * sin(ang1),
          level - 1);
      float rad2 = 0.5f * (circRad - circRad * cos(ang1));
      circle1(xc + circRad * cos(ang1) + rad2, yc, rad2, level - 1);
      circle1(xc - circRad * cos(ang1) - rad2, yc, rad2, level - 1);
      float rad3 = 0.5f * (circRad - circRad * sin(ang1));
      circle1(xc, yc + circRad * sin(ang1) + rad3, rad3, level - 1);
      circle1(xc, yc - circRad * sin(ang1) - rad3, rad3, level - 1);
      return;
    }
    if (choiceMaker < 0.8) { // annular steiner chain
      circle1(xc, yc, circRad, 0);
      float randtemp = random(0, 1);
      int n = Int(3 + 12 * randtemp * randtemp);
      float theta = DLUtil.PI / (1.0f * n);
      float temp = (1 / cos(theta) + tan(theta));
      float littleR = circRad / (temp * temp);
      float rho = littleR * sin(theta) / (1 - sin(theta));
      for (int i = 0; i < n; ++i) {
        //stroke(255, 0, 255);

        circle1(xc + (rho + littleR) * cos(theta * i * 2), yc + (rho + littleR) * sin(theta * i * 2), rho, level - 1);
      }
      circle1(xc, yc, littleR, level - 1);
      return;
    }
    if (choiceMaker < 0.9) { //Pappus chain
      circle1(xc, yc, circRad, 0);
      float rf = random(0.2, 0.8);
      int n = Int(random(4, 10));
      float rot = random(DLUtil.TWO_PI);
      for (int i = 0; i < n; ++i) {
        float xx = rf * (1 + rf) / (2 * (i * i * (1 - rf) * (1 - rf) + rf));
        float yy = i * rf * (1 - rf) / (i * i * (1 - rf) * (1 - rf) + rf);
        float rad = ((1 - rf) * rf / (1.0f * (i * i * (1 - rf) * (1 - rf) + rf)));
        xx = 2 * (xx - 0.5f);
        yy = 2 * yy;
        float xt = xx * cos(rot) - yy * sin(rot);
        float yt = xx * sin(rot) + yy * cos(rot);
        circle1(xc + xt * circRad, yc + yt * circRad, rad * circRad, level - 1);
        if (i > 0) {
          yy = -yy;
          xt = xx * cos(rot) - yy * sin(rot);
          yt = xx * sin(rot) + yy * cos(rot);
          circle1(xc + xt * circRad, yc + yt * circRad, rad * circRad, level - 1);
        }
        //println(xx+" "+yy+" "+rad);
      }

      float xx = rf - 1;
      float yy = 0;
      float xt = xx * cos(rot) - yy * sin(rot);
      float yt = xx * sin(rot) + yy * cos(rot);
      circle1(xc + xt * circRad, yc + yt * circRad, rf * circRad, level - 1);
      return;
    }

    else { // "octagon"
      circle1(xc, yc, circRad, 0);
      float shift = 2 * circRad / sqrt(10.f);
      rectangle1(xc - shift * 0.5f, yc - shift * 0.5f, shift, shift, level - 1);
      rectangle1(xc - 1.5f * shift, yc - shift * 0.5f, shift, shift, level - 1);
      rectangle1(xc - shift * 0.5f, yc - 1.5f * shift, shift, shift, level - 1);
      rectangle1(xc + shift * 0.5f, yc - shift * 0.5f, shift, shift, level - 1);
      rectangle1(xc - shift * 0.5f, yc + shift * 0.5f, shift, shift, level - 1);
      triangle1(xc - shift * 0.5f, yc - shift * 0.5f, xc - shift * 1.5f, yc - shift * 0.5f, xc - shift * 0.5f, yc
          - shift * 1.5f, level - 1);
      triangle1(xc - shift * 0.5f, yc + shift * 0.5f, xc - shift * 1.5f, yc + shift * 0.5f, xc - shift * 0.5f, yc
          + shift * 1.5f, level - 1);
      triangle1(xc + shift * 0.5f, yc - shift * 0.5f, xc + shift * 0.5f, yc - shift * 1.5f, xc + shift * 1.5f, yc
          - shift * 0.5f, level - 1);
      triangle1(xc + shift * 0.5f, yc + shift * 0.5f, xc + shift * 0.5f, yc + shift * 1.5f, xc + shift * 1.5f, yc
          + shift * 0.5f, level - 1);
      return;
    }

  }

  public void setRandColor(float f) {
    randColor = f;
  }

  public float getRandColor() {
    return randColor;
  }

  public float[] rangeRandColor() {
    return new float[] { 0, 1 };
  }

  public float getColorVar() {
    return colorVar;
  }

  public void setColorVar(float cv) {
    colorVar = cv;
  }

  public float[] rangeColorVar() {
    return new float[] { 0f, 1f };
  }

  public float getRandSat() {
    return randSat;
  }

  public void setRandSat(float rSat) {
    this.randSat = rSat;
  }

  public float[] rangeRandSat() {
    return new float[] { 0, 1 };
  }

  public float getSatVar() {
    return satVar;
  }

  public void setSatVar(float sVar) {
    this.satVar = sVar;
  }

  public float[] rangeSatVar() {
    return new float[] { 0, 1 };
  }

  public float getRandBright() {
    return randBright;
  }

  public void setRandBright(float rBright) {
    this.randBright = rBright;
  }

  public float[] rangeRandBright() {
    return new float[] { 0, 1 };
  }

  public float getBrightVar() {
    return brightVar;
  }

  public void setBrightVar(float brightVar) {
    this.brightVar = brightVar;
  }

  public float[] rangeBrightVar() {
    return new float[] { 0, 1 };
  }

  float randColor = 0.5f;
  float colorVar = 0.5f;
  float randSat = 0.5f;
  float satVar = 0.5f;
  float randBright = 0.5f;
  float brightVar = 0.5f;

  float s = DLUtil.RangeRandom(randBright - brightVar, randBright + brightVar);

  Color randColor() {
    float h = DLUtil.RangeRandom(randColor - colorVar, randColor + colorVar);
    float s = DLUtil.RangeRandom(randSat - satVar, randSat + satVar);
    float b = DLUtil.RangeRandom(randBright - brightVar, randBright + brightVar);
    int i = Color.HSBtoRGB(h, s, b);
    return new Color(i);
  }

  Color _randColor() {
    //return DLUtil.RandomColor(0, 1, 0, 1, 0, 1); //color(Int(random(0, 255)), Int(random(0, 255)), Int(random(0, 255)));
    float h = DLUtil.RangeRandom(0f, 1f);
    float H = DLUtil.RangeRandom(h, 1f);
    float s = DLUtil.RangeRandom(0f, 1f);
    float S = DLUtil.RangeRandom(s, 1f);
    float b = DLUtil.RangeRandom(0f, 1f);
    float B = DLUtil.RangeRandom(b, 1f);
    return DLUtil.RandomColor(h, H, s, S, b, B);
  }
}
