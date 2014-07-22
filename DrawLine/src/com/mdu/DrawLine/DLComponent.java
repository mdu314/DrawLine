package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public abstract class DLComponent implements DLRandom , DLShadow{
  DrawLine parent;
  AffineTransform transformation = new AffineTransform();
  float scaleX = 1f;
  float scaleY = 1f;
  float angle = 1f;
  float shearX = 0;
  float shearY = 0;
  Shadow shadow = null;

  int x = 0;
  int y = 0;

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  DLComponent() {
    super();
  }

  DLComponent(DLComponent dlc) {
    super();
    x = dlc.x;
    y = dlc.y;
  }

  DLComponent(int x, int y) {
    super();
    this.x = x;
    this.y = y;
  }

  abstract DLComponent copy();

  abstract void paint(Graphics g);
  
  abstract void paint(Graphics g, boolean deco);

  abstract Rectangle getBounds();

  abstract void transform(AffineTransform tr);

  long getSize() {
    return DLUtil.GetObjectSize(this);
  }

  boolean hitTest(Point p) {
    Rectangle b = getBounds();
    return b.contains(p);
  }

  void move(int dx, int dy) {
    AffineTransform tr = AffineTransform.getTranslateInstance(dx, dy);
    transform(tr);
    tr.concatenate(transformation);
    transformation = tr;
  }

  float getRandomAngle() {
    return RangeRandom(0f, (float) Math.PI * 2);
  }

  public void randomize() {
    setShadow(true);
    angle = getRandomAngle();
    scaleX = RangeRandom(0.8f, 1.2f);
    scaleY = RangeRandom(0.8f, 1.2f);
    double scale = Math.min(scaleX, scaleY);
    // shearX = RangeRandom(0f, 0.1f);
    // shearY = RangeRandom(0f, 0.1f);
    transformation.concatenate(AffineTransform.getRotateInstance(angle));
    // transformation.concatenate(AffineTransform.getShearInstance(shearX,
    // shearY));
    // transformation.concatenate(AffineTransform.getScaleInstance(scaleX,
    // scaleY));
    transformation.concatenate(AffineTransform.getScaleInstance(scale, scale));
  }

  public boolean getShadow() {
    return shadow != null;
  }

  public void setShadow(boolean s) {
    if (s && (shadow != null))
      return;
    if (!s && (shadow == null))
      return;
    Rectangle o = redisplayStart();
    if (s) {
      shadow = new Shadow(this);
    } else {
      shadow = null;
    }
    redisplay(o);
  }

  public void clearShadow() {
    shadow = null;
  }

  public void shadow(Graphics2D ga) {
    if (shadow != null)
      shadow.draw(ga);
  }

  public Rectangle addShadowBounds(Rectangle bounds) {
    if (shadow != null) {
      int r = shadow.radius;
      int x = bounds.x + shadow.xOff - r;
      int y = bounds.y + shadow.yOff - r;
      int w = bounds.width + 2 * r;
      int h = bounds.height + 2 * r;
      Rectangle s = new Rectangle(x, y, w, h);
      Rectangle2D.union(bounds, s, s);
      return s;
    }
    return bounds;
  }

  Rectangle redisplayStart() {
    if (parent != null)
      return getBounds();
    return null;
  }

  Rectangle redisplay() {
    return redisplay(null);
  }

  Rectangle redisplay(Rectangle o) {
    if (parent != null) {
      Rectangle r = getBounds();
      if (o != null) {
        Rectangle d = new Rectangle();
        Rectangle2D.union(r, o, d);
        parent.paint(d);
        return d;
      } else {
        parent.paint(r);
        return r;
      }
    }
    return null;
  }

}
