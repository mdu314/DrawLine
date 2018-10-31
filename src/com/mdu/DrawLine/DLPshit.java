package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

class DLPshit extends DLPointImage {
  int points = 1000;
  boolean keepPoints = true;
  int[] xs = null;
  int[] ys = null;
  Color colors[] = null;

  Rectangle getBounds() {
    return getBounds(true);
  }

  Rectangle getBounds(boolean deco) {
    return super.getBounds(deco);
  }

  public boolean getKeepPoints() {
    return keepPoints;
  }

  public void setKeepPoints(boolean keepPoints) {
    this.keepPoints = keepPoints;
  }

  DLPshit(DLPshit r) {
    super(r);
  }

  public DLPshit(float x, float y) {
    super(x, y);
  }

  @Override
  DLPshit copy() {
    return new DLPshit(this);
  }

  public void f(Graphics2D g, DLThread t) {
    DLUtil.SetHints(g);
    populateImage(g);
    if (parent != null)
      parent.paint(this);
  }

  public void paint(Graphics gr) {
    paint(gr, true);
  }

  public void paint(Graphics gr, boolean deco) {
    super.paint(gr, deco);
  }

  void prepareForDisplay() {
    BufferedImage i = image();
    populateImage(i.createGraphics());
  }

  void populateImage(Graphics2D g) {
    DLUtil.SetHints(g);
    if (keepPoints && xs != null && ys != null) {

      for (int i = 0; i < points; i++) {
        int ix = xs[i];
        int iy = ys[i];
        Color c = colors[i];
        g.setColor(c);
        setPointFill(c);
        drawPoint(g, ix, iy);
      }
    } else {
      float var = Math.min(iwidth, iheight);

      for (int i = 0; i < points; i++) {
        float k = DLUtil.RandomGauss(0, var / 7);
        float t = DLUtil.RangeRandom(0, DLUtil.TWO_PI);
        float dx = k * DLUtil.Sin(t) + iwidth / 2;
        float dy = k * DLUtil.Cos(t) + iheight / 2;
        Color c = DLUtil.RandomColor(0f, 1f, 0f, 0.5f, 0.5f, 1f);
        g.setColor(c);
        int ix = (int) (dx + 0.5f);
        int iy = (int) (dy + 0.5f);
        setPointFill(c);

        drawPoint(g, ix, iy);
        if (keepPoints) {
          if (xs == null)
            xs = new int[points];
          if (ys == null)
            ys = new int[points];
          if (colors == null)
            colors = new Color[points];
          xs[i] = ix;
          ys[i] = iy;
          colors[i] = c;
        }
      }
    }
  }

  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = (Graphics2D) img.getGraphics();
    DLUtil.SetHints(g);
    populateImage(g);
    return img;
  }

  public void randomize() {
    final int i = DLUtil.RangeRandom(4 * DLParams.DRAWING_STEP, 10 * DLParams.DRAWING_STEP);
    iwidth = i;
    iheight = i;
    points = DLUtil.RangeRandom(500, 1000);
    setShadow(true);
  }

}
