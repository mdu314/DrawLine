package com.mdu.DrawLine;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class Graphics {

  Graphics2D graphics;
  Paint fillPaint;
  Paint strokePaint;
  DLImage image;

  float map(float v, float minV, float maxV, float newMin, float newMax) {
    return DLUtil.Normalize2(newMin, newMax, minV, maxV, v);
  }

  void ellipseMode(int mode) {

  }

  void translate(float tx, float ty) {
    AffineTransform at = AffineTransform.getTranslateInstance(tx, ty);
    graphics.setTransform(at);
  }

  public void set(Graphics2D g, DLImage image) {
    this.graphics = g;
    this.image = image;
  }

  public Graphics2D getGraphics() {
    return graphics;
  }

  public void setGraphics(Graphics2D graphics) {
    this.graphics = graphics;
  }

  void background(int c) {
    image.setBackground(new Color(c));
  }

  void smooth() {
    graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
  }

  void strokeWeight(float w) {
    BasicStroke bs = new BasicStroke(w);
    graphics.setStroke(bs);
  }

  void stroke(int c, float w) {
    Color col = new Color(c);
    strokePaint = col;
    BasicStroke bs = new BasicStroke(w);
    graphics.setStroke(bs);
  }

  void stroke(int c) {
    Color col = new Color(c);
    strokePaint = col;
  }

  void noStroke() {
    strokePaint = null;
  }

  void fill(int rgb) {
    Color col = new Color(rgb);
    fillPaint = col;
  }

  void fill(int r, int g, int b, int a) {
    Color col = new Color(r, g, b, a);
    fillPaint = col;
  }

  void fill(int rgb, int alpha) {
    Color col = new Color(rgb | alpha << 24, true);
    fillPaint = col;
  }

  void fill(int r, int g, int b) {
    Color col = new Color(r, g, b);
    fillPaint = col;
  }

  void noFill() {
    fillPaint = null;
  }

  void draw(Shape s) {
    if (fillPaint != null) {
      graphics.setPaint(fillPaint);
      graphics.fill(s);
    }

    if (strokePaint != null) {
      graphics.setPaint(strokePaint);
      graphics.draw(s);
    }
  }

  void ellipse(float x, float y, float w, float h) {
    // Ellipse2D.Float e = new Ellipse2D.Float(x, y, w, h);
    Ellipse2D.Float e = new Ellipse2D.Float(x - w / 2, y - h / 2, w, h);
    draw(e);
  }

  void rect(float x, float y, float w, float h) {
    // Rectangle2D.Float e = new Rectangle2D.Float(x, y, w, h);
    Rectangle2D.Float e = new Rectangle2D.Float(x - w / 2, y - h / 2, w, h);
    draw(e);
  }

  void line(float x1, float y1, float x2, float y2) {
    Line2D.Float e = new Line2D.Float(x1, y1, x2, y2);
    draw(e);
  }

  void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
    Path2D.Float p = new Path2D.Float();
    p.moveTo(x1, y1);
    p.lineTo(x2, y2);
    p.lineTo(x3, y3);
    p.lineTo(x1, y1);
    draw(p);
  }

  void arc(float x, float y, float w, float h, float a1, float a2) {
    Arc2D.Float a = new Arc2D.Float(x, y, w, h, a1, a2 - a1, Arc2D.OPEN);
    draw(a);
  }

  float radians(float deg) {
    return DLUtil.Radian(deg);
  }

  public static void main(String[] args) {
    
  }
}
