package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.BrighterColor;
import static com.mdu.DrawLine.DLUtil.Normalize;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

class DLRuban extends DLSegmentedComponent {
  DLLineBrush brush = DLLineBrush.getBrush(10, 0);
  Color color = Color.black;
  float maxSpeed = 3;
  boolean useSpeed = false;
  boolean light = true;

  public boolean getLight() {
    return light;
  }

  public void setLight(boolean light) {
    this.light = light;
    Rectangle dirt = getBounds();
    dirt = addShadowBounds(dirt);
    setShadow(shadow, true);
    repaint(dirt);
  }

  public boolean getUseSpeed() {
    return useSpeed;
  }

  public void setUseSpeed(boolean useSpeed) {
    this.useSpeed = useSpeed;
    Rectangle dirt = getBounds();
    dirt = addShadowBounds(dirt);
    setShadow(shadow, true);
    repaint(dirt);
  }

  public DLRuban() {
    super();
  }

  public DLRuban(DLRuban src) {
    super();
    brush = src.brush.copy();
    color = src.color;
    maxSpeed = src.maxSpeed;
    final Object o = src.points.clone();
    points = (DLPointList) o;
  }

  public DLRuban(float x, float y) {
    super(x, y);
  }

  DLRuban copy() {
    return new DLRuban(this);
  }

  void generateShape(int i) {

    if (i > points.size() || i < 1)
      return;
    final DLPoint lp = points.get(i - 1);
    final DLPoint p = points.get(i);

    final float x = p.x;
    final float y = p.y;
    final float dx = x - lp.x;
    final float dy = y - lp.y;

    if (p.shape == null) {
      final GeneralPath gp = new GeneralPath();

      float r = 1;
      if (useSpeed) {
        final float t = (p.when - lp.when) / 1000.0f;
        final float speed = DLUtil.FastSqrt(dx * dx + dy * dy) / t;
        r = Normalize(1f, maxSpeed, 0f, 1500f, speed);
      }

      final float bs = brush.size / r;
      final float ba = brush.angle;
      DLLineBrush b = null;
      if (Float.isNaN(ba))
        b = DLLineBrush.getBrush(bs, lp, p);
      else
        b = DLLineBrush.getBrush(bs, ba);

      p.brush = b;

      final double b0x = b.brush[0].getX();
      final double b0y = b.brush[0].getY();
      final double b1x = b.brush[1].getX();
      final double b1y = b.brush[1].getY();

      final DLLineBrush lb = lp.brush != null ? lp.brush : b;
      final double lb0x = lb.brush[0].getX();
      final double lb0y = lb.brush[0].getY();
      final double lb1x = lb.brush[1].getX();
      final double lb1y = lb.brush[1].getY();

      gp.moveTo(x + b0x, y + b0y);
      gp.lineTo(x + b1x, y + b1y);
      gp.lineTo(lp.x + lb1x, lp.y + lb1y);
      gp.lineTo(lp.x + lb0x, lp.y + lb0y);
      gp.lineTo(x + b0x, y + b0y);

      p.shape = gp;
    }

  }

  void drawSegment(Graphics2D g, int i) {
    if (i > points.size() || i < 1)
      return;
    generateShape(i);

    final DLPoint lp = points.get(i - 1);
    final DLPoint p = points.get(i);

    final float x = p.x;
    final float y = p.y;
    final float dx = x - lp.x;
    final float dy = y - lp.y;

    Color c = color;
    if (light && c != null) {
      final float factor = (float) DLUtil.Abs(dx / DLUtil.FastSqrt(dx * dx + dy * dy));
      c = BrighterColor(color, factor);
    }

    g.setColor(c);
    g.fill(p.shape);
    g.draw(p.shape);
  }

  @Override
  Rectangle getBounds() {
    Rectangle r = null;

    for (int i = 0; i < points.size(); i++) {
      final DLPoint pt = points.get(i);
      final Shape s = pt.shape;
      if (s != null) {
        final Rectangle rs = s.getBounds();
        if (r == null)
          r = new Rectangle(rs);
        else
          Rectangle2D.union(rs, r, r);
      }
    }

    Rectangle bounds = r;
    if (bounds == null)
      bounds = new Rectangle((int) x, (int) y, 1, 1);
    bounds = addShadowBounds(bounds);
    return bounds;
  }

  public Color getColor() {
    return color;
  }

  public float getMaxSpeed() {
    return maxSpeed;
  }

  @Override
  boolean hitTest(Point p) {
    if (!super.hitTest(p))
      return false;
    for (int i = 1; i < points.size(); i++) {
      final DLPoint pt = points.get(i);
      final Shape s = pt.shape;
      if (s != null && s.contains(p))
        return true;
    }
    return false;
  }

  public void paint(Graphics gr) {
    paint(gr, true);
  }

  public void paint(Graphics gr, boolean deco) {
    final Graphics2D g = (Graphics2D) gr;
    if (deco) {
      shadow(g);
      if (DLParams.DEBUG) {
        final Rectangle r = getBounds();
        g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
      }
    }
    for (int i = 1; i < points.size(); i++)
      drawSegment(g, i);
  }

  public void randomize() {
    setShadow(true);
    color = DLUtil.RandomColor(0.0f, 1.0f, 0.6f, 0.9f, 0.8f, 1f);
    final float angle = DLUtil.FloatRandom(0, (float) Math.PI);
    if (useSpeed)
      brush = DLLineBrush.getBrush(DLUtil.FloatRandom(10f, 50f), angle);
    else
      brush = DLLineBrush.getBrush(DLUtil.FloatRandom(10, 30), angle);
    maxSpeed = DLUtil.RangeRandom(1f, 5f);
  }

  private void repaint(Rectangle dirt) {
    if (parent == null)
      return;

    Rectangle r = getBounds();
    if (shadow)
      r = addShadowBounds(r);
    for (int i = 1; i < points.size(); i++) {
      final DLPoint p = points.get(i);
      p.shape = null;
    }

    for (int i = 1; i < points.size(); i++)
      generateShape(i);

    Rectangle.union(getBounds(), r, r);
    if (shadow)
      r = addShadowBounds(r);
    if (dirt != null)
      Rectangle.union(dirt, r, r);

    parent.repaint(r.x, r.y, r.width, r.height);
  }

  public void setBrushAngle(float brushAngle) {
    Rectangle dirt = getBounds();
    brush = DLLineBrush.getBrush(brush.size, brushAngle);
    dirt = addShadowBounds(dirt);
    // setShadow(shadow, true);
    // clearShadow();
    // repaint(dirt);
    repaint(null);
  }

  public void setBrushSize(float brushSize) {
    Rectangle dirt = getBounds();
    brush = DLLineBrush.getBrush(brushSize, brush.angle);
    dirt = addShadowBounds(dirt);
    // setShadow(shadow, true);
    // clearShadow();
    // repaint(dirt);
    repaint(null);
  }

  public float[] rangeBrushSize() {
    return new float[] { 1, 50 };
  }

  public float getBrushAngle() {
    return brush.angle;
  }

  public float getBrushSize() {
    return brush.size;
  }

  public float[] rangeBrushAngle() {
    return new float[] { 0, (float) Math.PI * 2f };
  }

  public void setColor(Color color) {
    this.color = color;
    repaint(null);
  }

  public void setMaxSpeed(float maxSpeed) {
    this.maxSpeed = maxSpeed;
    repaint(null);
  }

}
