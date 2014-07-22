package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DEBUG;
import static com.mdu.DrawLine.DLUtil.BooleanRandom;
import static com.mdu.DrawLine.DLUtil.RandomColor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;

public abstract class DLCurve extends DLComponent {
  Path2D path = null;
  Color fill = null;
  Color stroke = null;
  Selection selection = null;

  public boolean isSelected() {
    return selection != null;
  }

  Rectangle selected(boolean s) {
    if (s && (selection != null))
      return null;
    if (!s && (selection == null))
      return null;
    Rectangle o = redisplayStart();
    if (s) {
      selection = new Selection(this);
    } else {
      selection = null;
    }
    return o;
  }

  public void setSelected(boolean s) {
    Rectangle o = selected(s);
    if (o != null)
      redisplay(o);
  }

  public Color getFill() {
    return fill;
  }

  public void setFill(Color fill) {
    this.fill = fill;
    redisplay();
  }

  public Color getStroke() {
    return stroke;
  }

  public void setStroke(Color stroke) {
    this.stroke = stroke;
    redisplay();
  }

  DLCurve() {
    super();
  }

  DLCurve(DLCurve c) {
    super(c);
    fill = c.fill;
    stroke = c.stroke;
    if (c.path != null)
      path = (Path2D) c.path.clone();
  }

  DLCurve(int x, int y) {
    super(x, y);
  }

  abstract Path2D path();

  void clearPath() {
    path = null;
  }

  void clear() {
    clearShadow();
    clearPath();
    // clearSelection();
  }

  public void randomize() {
    super.randomize();
    if (BooleanRandom())
      fill = RandomColor(0.0f, 1.0f, 0.3f, 0.6f, 0.8f, 1f);
    else
      fill = null;
    if (BooleanRandom())
      stroke = RandomColor(0.0f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f);
    else
      stroke = null;

    if (stroke == null)
      stroke = RandomColor(0.0f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f);

    setShadow(true);
  }

  Rectangle getBounds() {
    return getBounds(0);
  }

  Rectangle getBounds(int grow) {
    if (path == null)
      path = path();

    Rectangle bounds = new Rectangle(path.getBounds());

    bounds = addShadowBounds(bounds);
    
    if (selection != null) {
      Rectangle rs = selection.boundingBox();
      Rectangle2D.union(bounds, rs, bounds);
    }
    
    if (grow != 0)
      bounds.grow(grow, grow);
    
    return bounds;
  }

  void setProp(String prop, Object val) {
    Class<? extends DLCurve> c = getClass();
    try {
      Rectangle r = redisplayStart();
      Field f = c.getDeclaredField(prop);
      f.set(this, val);
      clear();
      redisplay(r);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  boolean hitTest(Point p) {
    if (!super.hitTest(p))
      return false;
    if (path == null)
      path = path();
    if (!path.contains(p))
      return false;
    return true;
  }

  void selection(Graphics2D g) {
    if (selection != null)
      selection.draw(g);
  }

  public void paint(Graphics gr) {
    Graphics2D g = (Graphics2D) gr;
    paint(g, true);
    if (DEBUG) {
      Rectangle b = getBounds();
      g.drawRect(b.x, b.y, b.width, b.height);
    }
  }

  public void paint(Graphics gr, boolean deco) {
    Graphics2D g = (Graphics2D) gr;
    if (path == null)
      path = path();

    if (deco)
      shadow(g);

    if (fill != null) {
      g.setColor(fill);
      g.fill(path);
    }

    if (stroke != null) {
      g.setColor(stroke);
      g.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
      g.draw(path);
    }

    if (deco)
      selection(g);

    if (DEBUG) {
      PathIterator pi = path.getPathIterator(null);
      float[] coords = new float[6];
      g.setColor(Color.red);
      while (!pi.isDone()) {
        int i = pi.currentSegment(coords);
        g.drawRect((int) Math.floor(coords[0] - 2), (int) Math.floor(coords[1] - 2), 4, 4);
        if (i > 2)
          g.drawRect((int) Math.floor(coords[2] - 2), (int) Math.floor(coords[3] - 2), 4, 4);
        if (i > 4)
          g.drawRect((int) Math.floor(coords[4] - 2), (int) Math.floor(coords[5] - 2), 4, 4);
        pi.next();
      }

      Rectangle r2 = getBounds();
      g.draw(r2);
    }

  }

  void transform(Path2D p) {
    AffineTransform tr = new AffineTransform();
    tr.translate(x, y);
    tr.concatenate(transformation);
    p.transform(tr);
  }

  void transform(AffineTransform tr) {
    if (path == null)
      path = path();
    path.transform(tr);
  }

}
