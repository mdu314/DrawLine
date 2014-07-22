package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

class DLLorem extends DLSegmentedComponent implements DLSegmented {

  static final int CIRCLE_SHAPE = 1;
  static final int SQUARE_SHAPE = 2;
  static final int MIXED_SHAPE = 3;
  static final int NO_SHAPE = 0;
  int loremIndex;
  Font font;
  int fontSize = 20;

  Color color = Color.black;

  public DLLorem(int x, int y) {
    super(x, y);
  }

  private void drawCurve(Graphics2D g) {
    if (color != null)
      g.setColor(color);
    drawPoints(g);
  }

  public void paint(Graphics gr, boolean deco) {
    Graphics2D g = (Graphics2D) gr;
    lorem(g);
    if (deco)
      shadow(g);
    if (DLParams.DEBUG) {
      Rectangle r = getBounds();
      g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
    }
    drawCurve(g);
  }

  public void paint(Graphics gr) {
    paint(gr, true);
  }

  DLLorem copy() {
    return new DLLorem(this);
  }

  DLLorem(DLLorem src) {
    super();
    this.color = src.color;
    points = (ArrayList<DLPoint>) src.points.clone();
  }

  Rectangle getBounds() {
    Rectangle r;
    if (points.size() > 0)
      r = DLUtil.PolylineBounds(points, 0);
    else
      r = new Rectangle(x, y, 1, 1);
    for (int i = 0; i < points.size(); i++) {
      DLPoint p = points.get(i);
      if (p.dlc != null) {
        Rectangle rs = p.dlc.getBounds();
        Rectangle2D.union(r, rs, r);
      }
    }
    r = addShadowBounds(r);
    return r;
  }

  boolean hitTest(Point p) {
    if (!super.hitTest(p))
      return false;
    for (int i = 0; i < points.size(); i++) {
      DLPoint pt = points.get(i);
      if (pt.dlc != null && pt.dlc.hitTest(p))
        return true;
    }
    float d = DLUtil.MinDistance(points, p.x, p.y);
    if (d > DLParams.SELECT_PRECISION)
      return false;
    return true;
  }

  public void randomize() {
    // setShadow(true);
    color = DLUtil.RandomColor(0.0f, 1.0f, 0.6f, 0.9f, 0.8f, 1f);
  }

  void drawPoints(Graphics2D g) {
    for (int i = 0; i < points.size(); i++)
      drawPoint(g, i);
  }

  void drawPoint(Graphics2D g, int i) {
    DLPoint p = points.get(i);
    if (p.dlc == null) {
      String s = DLUtil.lorem.substring(loremIndex, loremIndex + 1);
      loremIndex++;
      if (loremIndex >= DLUtil.lorem.length())
        loremIndex = 0;
      DLChar dlc = new DLChar((int) Math.rint(p.x), (int) Math.rint(p.y));
      dlc.randomize();
      dlc.text = s;
      p.dlc = dlc;
    }
    p.dlc.paint(g);
  }

  void drawSegment(Graphics2D g, int i) {
    drawPoint(g, i);
  }

  Font getFont() {
    if (font == null)
      font = new Font(Font.SERIF, Font.PLAIN, fontSize);
    return font;
  }

  void lorem(Graphics2D g) {
    for (int i = 0; i < points.size(); i++) {
      DLPoint p = points.get(i);
      if (p.dlc == null) {
        String s = DLUtil.lorem.substring(loremIndex, loremIndex + 1);
        loremIndex++;
        if (loremIndex >= DLUtil.lorem.length())
          loremIndex = 0;
        DLChar dlc = new DLChar((int) Math.floor(p.x), (int) Math.floor(p.y));
        dlc.randomize();
        dlc.text = s;
        p.dlc = dlc;
      }
    }
  }

  void transform(AffineTransform tr) {
    transformPointList(tr);

    for (int i = 0; i < points.size(); i++) {
      DLPoint p = points.get(i);
      if (p.dlc != null)
        ((DLChar) p.dlc).transform(tr);
    }
  }

}
