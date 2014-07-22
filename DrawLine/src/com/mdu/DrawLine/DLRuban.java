package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.BrighterColor;
import static com.mdu.DrawLine.DLUtil.Normalize;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

class DLRuban extends DLSegmentedComponent implements DLSegmented {
  Color color = Color.black;
  float maxSpeed = 3;
  DLLineBrush brush = DLLineBrush.getBrush(10, 0);

  public DLRuban() {
    super();
  }

  public DLRuban(int x, int y) {
    super(x, y);
  }

  public DLRuban(DLRuban src) {
    super();
    brush = src.brush.copy();
    color = src.color;
    maxSpeed = src.maxSpeed;
    Object o = src.points.clone();
    points = (ArrayList<DLPoint>) o;
  }

  public void paint(Graphics gr, boolean deco) {
    Graphics2D g = (Graphics2D) gr;
    if (deco) {
      shadow(g);
      if (DLParams.DEBUG) {
        Rectangle r = getBounds();
        g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
      }
    }
    for (int i = 1; i < points.size(); i++)
      drawSegment(g, i);
  }

  public void paint(Graphics gr) {
    paint(gr, true);
  }

  DLRuban copy() {
    return new DLRuban(this);
  }

  Rectangle getBounds() {
    Rectangle r = null;

    for (int i = 1; i < points.size(); i++) {
      DLPoint pt = points.get(i);
      Shape s = pt.shape;
      if (s != null) {
        Rectangle rs = s.getBounds();
        if (r == null)
          r = new Rectangle(rs);
        else
          Rectangle2D.union(rs, r, r);
      }
    }

    Rectangle bounds = r;
    if (r == null)
      bounds = new Rectangle(x, y, 1, 1);
    bounds = addShadowBounds(bounds);
    return bounds;
  }

  boolean hitTest(Point p) {
    if (!super.hitTest(p))
      return false;
    for (int i = 1; i < points.size(); i++) {
      DLPoint pt = points.get(i);
      Shape s = pt.shape;
      if ((s != null) && s.contains(p))
        return true;
    }
    return false;
  }

  public void randomize() {
    // setShadow(true);
    color = DLUtil.RandomColor(0.0f, 1.0f, 0.6f, 0.9f, 0.8f, 1f);
    float angle = (float) DLUtil.FloatRandom(0, (float) (Math.PI));
    brush = DLLineBrush.getBrush(DLUtil.FloatRandom(10f, 50f), angle);
    maxSpeed = DLUtil.RangeRandom(1f, 5f);
  }

  private void repaint(Rectangle dirt) {

    for (int i = 1; i < points.size(); i++) {
      DLPoint p = points.get(i);
      p.shape = null;
    }
    if (dirt != null)
      parent.repaint(dirt.x, dirt.y, dirt.width, dirt.height);

    if (parent != null) {
      Rectangle r = getBounds();
      if (r != null)
        parent.repaint(r.x, r.y, r.width, r.height);
    }
  }

  void drawSegment(Graphics2D g, int i) {

    DLUtil.SetHints(g);

    DLPoint lp = points.get(i - 1);
    DLPoint p = points.get(i);

    float x = p.x;
    float y = p.y;
    float dx = x - lp.x;
    float dy = y - lp.y;

    if (p.shape == null) {
      GeneralPath gp = new GeneralPath();

      float t = (p.when - lp.when) / 1000.0f;
      float speed = (float) Math.sqrt(dx * dx + dy * dy) / t;
      float r = Normalize(1f, maxSpeed, 0f, 1500f, speed);
      float bs = brush.size / r;
      float ba = brush.angle;
      DLLineBrush b = null;
      if (Float.isNaN(ba))
        b = DLLineBrush.getBrush(bs, lp, p);
      else
        b = DLLineBrush.getBrush(bs, ba);

      p.brush = b;

      double b0x = b.brush[0].getX();
      double b0y = b.brush[0].getY();
      double b1x = b.brush[1].getX();
      double b1y = b.brush[1].getY();

      DLLineBrush lb = (lp.brush != null) ? lp.brush : b;
      double lb0x = lb.brush[0].getX();
      double lb0y = lb.brush[0].getY();
      double lb1x = lb.brush[1].getX();
      double lb1y = lb.brush[1].getY();

      gp.moveTo(x + b0x, y + b0y);
      gp.lineTo(x + b1x, y + b1y);
      gp.lineTo(lp.x + lb1x, lp.y + lb1y);
      gp.lineTo(lp.x + lb0x, lp.y + lb0y);
      gp.lineTo(x + b0x, y + b0y);

      p.shape = gp;
    }

    float factor = (float) (Math.abs(dx / Math.sqrt(dx * dx + dy * dy)));
    Color c = BrighterColor(color, factor);

    g.setColor(c);
    g.fill(p.shape);
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
    repaint(null);
  }

  public float getMaxSpeed() {
    return maxSpeed;
  }

  public void setMaxSpeed(float maxSpeed) {
    this.maxSpeed = maxSpeed;
    repaint(null);
  }

  public float getBrushAngle() {
    return brush.angle;
  }

  public void setBrushAngle(float brushAngle) {
    Rectangle dirt = getBounds();
    brush = DLLineBrush.getBrush(brush.size, brushAngle);
    repaint(dirt);
  }

  public float getBrushSize() {
    return brush.size;
  }

  public void setBrushSize(float brushSize) {
    Rectangle dirt = getBounds();
    brush = DLLineBrush.getBrush(brushSize, brush.angle);
    repaint(dirt);
  }

  void transform(AffineTransform tr) {
    transformPointList(tr);
  }

}
