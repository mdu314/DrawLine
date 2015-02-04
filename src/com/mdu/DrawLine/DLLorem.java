package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.BooleanRandom;
import static com.mdu.DrawLine.DLUtil.RandomColor;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.awt.Font.DIALOG;
import static java.awt.Font.DIALOG_INPUT;
import static java.awt.Font.MONOSPACED;
import static java.awt.Font.SANS_SERIF;
import static java.awt.Font.SERIF;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

class DLLorem extends DLSegmentedComponent {

  Color color = Color.black;
  Font font;
  int fontSize = 20;

  int loremIndex;

  DLLorem(DLLorem src) {
    super();
    this.color = src.color;
    points = src.points.copy();
  }

  public DLLorem(float x, float y) {
    super(x, y);
  }

  @Override
  DLLorem copy() {
    return new DLLorem(this);
  }

  private void drawCurve(Graphics2D g) {
    if (color != null)
      g.setColor(color);
    drawPoints(g);
  }

  void drawPoint(Graphics2D g, int i) {
    final DLPoint p = points.get(i);
    if (p.dlc == null) {
      final String s = DLUtil.lorem.substring(loremIndex, loremIndex + 1);
      loremIndex++;
      if (loremIndex >= DLUtil.lorem.length())
        loremIndex = 0;
      final DLChar dlc = new DLChar((int) Math.rint(p.x), (int) Math.rint(p.y));
      randomize(dlc);
      dlc.text = s;
      p.dlc = dlc;
    }
    p.dlc.paint(g);
  }

  void drawPoints(Graphics2D g) {
    for (int i = 0; i < points.size(); i++)
      drawPoint(g, i);
  }

  @Override
  void drawSegment(Graphics2D g, int i) {
    drawPoint(g, i);
  }

  @Override
  Rectangle getBounds() {
    Rectangle r = null;
    for (int i = 0; i < points.size(); i++) {
      final DLPoint p = points.get(i);
      if (p.dlc != null) {
        final Rectangle rs = p.dlc.getBounds();
        if (r == null)
          r = rs;
        else
          Rectangle2D.union(r, rs, r);
      }
    }
    if (r == null)
      r = new Rectangle((int)x, (int)y, 1, 1);
    r = addShadowBounds(r);
    return r;
  }

  Font getFont() {
    if (font == null)
      font = new Font(Font.SERIF, Font.PLAIN, fontSize);
    return font;
  }

  @Override
  boolean hitTest(Point p) {
    if (!super.hitTest(p))
      return false;
    for (int i = 0; i < points.size(); i++) {
      final DLPoint pt = points.get(i);
      if (pt.dlc != null && pt.dlc.hitTest(p))
        return true;
    }
    final float d = DLUtil.MinDistance(points, p.x, p.y);
    if (d > DLParams.SELECT_PRECISION)
      return false;
    return true;
  }

  void lorem(Graphics2D g) {
    for (int i = 0; i < points.size(); i++) {
      final DLPoint p = points.get(i);
      if (p.dlc == null) {
        final String s = DLUtil.lorem.substring(loremIndex, loremIndex + 1);
        loremIndex++;
        if (loremIndex >= DLUtil.lorem.length())
          loremIndex = 0;
        final DLChar dlc = new DLChar((int) Math.rint(p.x), (int) Math.rint(p.y));
        randomize(dlc);
        dlc.text = s;
        p.dlc = dlc;
      }
    }
  }

  @Override
  public void paint(Graphics gr) {
    paint(gr, true);
  }

  @Override
  public void paint(Graphics gr, boolean deco) {
    final Graphics2D g = (Graphics2D) gr;
    lorem(g);

    if (deco) {
      shadow(g);
      if (DLParams.DEBUG) {
        final Rectangle r = getBounds();
        g.setColor(Color.black);
        g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
      }
    }
    drawCurve(g);
  }

  @Override
  public void randomize() {
    super.randomize();
    // setShadow(true);
    color = DLUtil.RandomColor(0.0f, 1.0f, 0.6f, 0.9f, 0.8f, 1f);
  }

  void randomize(DLChar c) {
    c.fill = null;
    if (BooleanRandom())
      c.fill = RandomColor(0.0f, 1.0f, 0.3f, 0.6f, 0.8f, 1f);
    
    c.stroke = null;
    if (BooleanRandom())
      c.stroke = RandomColor(0.0f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f);

    if (c.fill == null && c.stroke == null)
      c.stroke = RandomColor(0.0f, 1.0f, 0.4f, 1.0f, 0.6f, 1.0f);

    setShadow(false);

    final String[] fa = { DIALOG, DIALOG_INPUT, SANS_SERIF, SERIF, MONOSPACED };
    c.family = fa[RangeRandom(0, 5)];
    c.style = RangeRandom(0, 4);
    c.size = RangeRandom(15, 20);
    final int huit = 16;
    c.angle = (float) RangeRandom(-Math.PI / huit, Math.PI / huit);
    c.transformation.concatenate(AffineTransform.getRotateInstance(c.angle));
  }

}
