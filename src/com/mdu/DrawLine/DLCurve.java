package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DEBUG;
import static com.mdu.DrawLine.DLUtil.BooleanRandom;
import static com.mdu.DrawLine.DLUtil.RandomColor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public abstract class DLCurve extends DLComponent {
  Color fill = null;
  DLPath path = null;
  Color stroke = Color.black;
  boolean smooth;

  DLCurve() {
    super();
  }

  DLCurve(DLCurve c) {
    super(c);
    fill = c.fill;
    stroke = c.stroke;
    if (c.path != null)
      path = (DLPath) c.path.clone();
  }

  DLCurve(float x, float y) {
    super(x, y);
  }

  DLPath toPath(DLPointList points) {
    return DLUtil.toPath(points);
  }

  DLPath toSpline(DLPointList points) {
    return DLUtil.toSpline(points);
  }

  public void reset() {
    
  }
  
  public boolean getSmooth() {
    return smooth;
  }

  public void setSmooth(boolean smooth) {
    if(this.smooth == smooth)
      return;
    Rectangle r = redisplayStart();
    this.smooth = smooth;
    clear();
    redisplay(r);
  }

  void clear() {
    clearShadow();
    clearPath();
    clearSelection();
  }

  void clearPath() {
    path = null;
  }

  Rectangle getBounds(boolean deco) {

    if (path == null)
      path = path();

    Rectangle bounds = new Rectangle(path.getBounds());
    if (deco) {
      bounds = addShadowBounds(bounds);
      bounds = addSelectionBounds(bounds);
    }
    return bounds;
  }

  Rectangle getBounds() {
    return getBounds(true);
  }

  public Color getFill() {
    return fill;
  }

  public Color getStroke() {
    return stroke;
  }

  @Override
  boolean mouse(MouseEvent e) {
    return false;
  }

  @Override
  public void paint(Graphics gr) {
    final Graphics2D g = (Graphics2D) gr;
    paint(g, true);
    if (DEBUG) {
      final Rectangle b = getBounds();
      g.drawRect(b.x, b.y, b.width, b.height);
    }
  }

  @Override
  public void paint(Graphics gr, boolean deco) {
    final Graphics2D g = (Graphics2D) gr;

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
      //      g.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
      g.draw(path);
    }

    if (deco)
      selection(g);

    if (DEBUG) {
      final PathIterator pi = path.getPathIterator(null);
      final float[] coords = new float[6];
      g.setColor(Color.red);
      while (!pi.isDone()) {
        final int i = pi.currentSegment(coords);
        g.drawRect((int) Math.floor(coords[0] - 2), (int) Math.floor(coords[1] - 2), 4, 4);
        if (i > 2)
          g.drawRect((int) Math.floor(coords[2] - 2), (int) Math.floor(coords[3] - 2), 4, 4);
        if (i > 4)
          g.drawRect((int) Math.floor(coords[4] - 2), (int) Math.floor(coords[5] - 2), 4, 4);
        pi.next();
      }

//      final Rectangle r2 = getBounds();
//      g.draw(r2);
    }

  }

  abstract DLPath path();

  @Override
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

  public void setFill(Color fill) {
    Rectangle r = redisplayStart();
    clearShadow();
    this.fill = fill;
    redisplay(r);
  }

  public void setStroke(Color stroke) {
    Rectangle r = redisplayStart();
    clearShadow();
    this.stroke = stroke;
    redisplay(r);
  }

  @Override
  void transform(AffineTransform tr) {
    if (path == null)
      path = path();
    path.transform(tr);
  }

  void transform(Path2D p) {
    if(p == null)
      return;
    final AffineTransform tr = new AffineTransform();
    tr.translate(x, y);
    tr.concatenate(transformation);
    p.transform(tr);
  };

  void transform() {
    if(path == null)
      path = path();
    final AffineTransform tr = new AffineTransform();
    tr.translate(x, y);
    tr.concatenate(transformation);
    path.transform(tr);
  };

  void prepareForDisplay() {

  }

}
