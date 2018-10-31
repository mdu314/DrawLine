package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class DLComponent implements DLRandom, DLShadow, Movable {
  DLComponent menuComponent;
  float angle = 0;
  DLContainer parent;
  float scaleX = 1;
  float scaleY = 1;
  Shadow shadowImage = null;
  boolean shadow;
  float shearX = 0;
  float shearY = 0;
  AffineTransform transformation = new AffineTransform();
  Selection selection = null;
  boolean selected = false;
  private Movable movableProxy;
  DLPropertySheet sheet;

  float x = 0;
  float y = 0;

  abstract void reset();
  
  abstract DLComponent copy();

  abstract void paint(Graphics g);

  abstract void paint(Graphics g, boolean deco);

  abstract boolean mouse(MouseEvent e);

  abstract Rectangle getBounds();

  abstract Rectangle getBounds(boolean deco);

  abstract void transform(AffineTransform tr);

  abstract void prepareForDisplay();

  public void paint(Graphics2D g, float tx, float ty) {
    AffineTransform t = AffineTransform.getTranslateInstance(tx, ty);
    AffineTransform ot = g.getTransform();
    t.concatenate(ot);
    g.setTransform(t);
    paint(g);
    g.setTransform(ot);
  }

  DLComponent() {
    super();
  }

  DLComponent(DLComponent dlc) {
    super();
    x = dlc.x;
    y = dlc.y;
  }

  DLComponent(float x, float y) {
    super();
    this.x = x;
    this.y = y;
  }

  public Rectangle addShadowBounds(Rectangle bounds) {
    if (shadow)
      if (shadowImage == null)
        shadowImage = new Shadow(this);
    if (shadowImage != null) {
      final Rectangle s = shadowImage.getBounds(bounds);
      Rectangle2D.union(bounds, s, s);
      return s;
    }
    return bounds;
  }

  @Override
  public void clearShadow() {
    shadowImage = null;
  }

  void clearSelection() {
    selection = null;
  }

  Rectangle addSelectionBounds(Rectangle r) {
    if (selection != null) {
      Rectangle s = selection.boundingBox();
      // To Be fixed
      DLUtil.expand(s,  0.1f);
      
      Rectangle2D.union(r, s, s);
      return s;
    }
    return r;
  }

  void selection(Graphics2D g) {
    if (selected) {
      if (selection == null)
        selection = new Selection(this);
      selection.draw(g);
    }
  }

  Rectangle setSelectedGetRect(boolean s) {
    if (s == selected)
      return null;
    setSelected(s);
    final Rectangle o = redisplayStart();
    if (s)
      selection = new Selection(this);
    else
      selection = null;
    return o;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean s) {
    if (s == selected)
      return;
    Rectangle o = redisplayStart();
    selected = s;
    if (s) {
      if (selection == null)
        selection = new Selection(this);
    } else {
      selection = null;
    }
    redisplay(o);
  }

  void dump() {
  }

  float getRandomAngle() {
    return RangeRandom(0f, (float) Math.PI * 2);
  }

  @Override
  public boolean getShadow() {
    return shadow;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  protected void _setXY(float x, float y) {
    if (this.x == x && this.y == y)
      return;
    this.x = x;
    this.y = y;
  }

  boolean hitTest(Point p) {
    final Rectangle b = getBounds(false);
    return b.contains(p);
  }

  public void move(float dx, float dy) {
    if (dx == 0 && dy == 0)
      return;
    final AffineTransform tr = AffineTransform.getTranslateInstance(dx, dy);
    transform(tr);
    Point2D p = tr.transform(new Point2D.Float(getX(), getY()), null);
    _setXY((float) p.getX(), (float) p.getY());
    tr.concatenate(transformation);
    transformation = tr;
  }

  @Override
  public void randomize() {
    setShadow(true);
    angle = getRandomAngle();
    scaleX = RangeRandom(0.8f, 1.2f);
    scaleY = RangeRandom(0.8f, 1.2f);
    final double scale = Math.min(scaleX, scaleY);
    // shearX = RangeRandom(0f, 0.1f);
    // shearY = RangeRandom(0f, 0.1f);
    transformation.concatenate(AffineTransform.getRotateInstance(angle));
    // transformation.concatenate(AffineTransform.getShearInstance(shearX,
    // shearY));
    // transformation.concatenate(AffineTransform.getScaleInstance(scaleX,
    // scaleY));
    transformation.concatenate(AffineTransform.getScaleInstance(scale, scale));
  }

  float[] rangeX() {
    return new float[] { 0, parent != null ? parent.getWidth() : DLParams.MAX_X };
  }

  float[] rangeY() {
    return new float[] { 0, parent != null ? parent.getWidth() : DLParams.MAX_X };
  }

  Rectangle redisplay() {
    return redisplay(null);
  }

  Rectangle redisplay(Rectangle o) {
    if (parent != null) {
      final Rectangle r = getBounds();
      if (o != null) {
        final Rectangle d = new Rectangle();
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

  Rectangle redisplayStart() {
    if (parent != null)
      return getBounds();
    return null;
  }

  public void setShadow(boolean s, boolean force) {
    if (!force)
      if (s == shadow)
        return;
    final Rectangle o = redisplayStart();
    shadow = s;
    clearShadow();
    redisplay(o);
  }

  public void setShadow(boolean s) {
    if (s == shadow)
      return;
    final Rectangle o = redisplayStart();
    shadow = s;
    clearShadow();
    redisplay(o);
  }

  public void setX(float x) {
    if (x == this.x)
      return;
    Rectangle r = redisplayStart();
    float dx = x - this.x;
    this.x = x;
    AffineTransform tr = AffineTransform.getTranslateInstance(dx, 0);
    transform(tr);
    redisplay(r);
  }

  public void setY(float y) {
    if (y == this.y)
      return;
    Rectangle r = redisplayStart();
    float dy = y - this.y;
    this.y = y;
    final AffineTransform tr = AffineTransform.getTranslateInstance(0, dy);
    transform(tr);
    redisplay(r);
  }

  @Override
  public void shadow(Graphics2D ga) {
    if (shadow) {
      if (shadowImage == null)
        shadowImage = new Shadow(this);
      shadowImage.draw(ga);
    }
  }

  void clear() {
  }

  public Movable getMovableProxy() {
    return movableProxy;
  }
  
  public void setMovableProxy(Movable m) {
    movableProxy = m;
  }
  
  /*
   * public float getAngle() { return angle; }
   * 
   * public void setAngle(float angle) { Rectangle r = redisplayStart();
   * this.angle = angle; clear(); AffineTransform tr =
   * AffineTransform.getRotateInstance(angle, x, y); transform(tr);
   * redisplay(r); }
   * 
   * public float[] rangeAngle() { return new float[] { 0, DLUtil.TWO_PI }; }
   * 
   * public float getScaleX() { return scaleX; }
   * 
   * public void setScaleX(float scaleX) { Rectangle r = redisplayStart();
   * this.scaleX = scaleX; clear(); AffineTransform tr =
   * AffineTransform.getScaleInstance(scaleX, scaleY); transform(tr);
   * redisplay(r); }
   * 
   * public float getScaleY() { return scaleY; }
   * 
   * public void setScaleY(float scaleY) { Rectangle r = redisplayStart();
   * this.scaleY = scaleY; clear(); AffineTransform tr =
   * AffineTransform.getScaleInstance(scaleX, scaleY); transform(tr);
   * redisplay(r); }
   */
}
