package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

class DLPshit extends DLImage {
  int points = 1000;
  int mode = 1;

  DLPshit(DLPshit r) {
    super(r);
  }

  public DLPshit(int x, int y) {
    super(x, y);
  }

  DLPshit(int x, int y, int iw, int ih) {
    super(x, y, iw, ih);
  }

  Image image() {
    Image img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) img.getGraphics();
    DLUtil.SetHints(g);

    double var = 2 * Math.min(iwidth, iheight);

    for (int i = 0; i < points; i++) {
      double k = DLUtil.Gauss(0, var / 15);
      //double k = DLUtil.ExpRandom(var / 200);
      double t = DLUtil.RangeRandom(0, Math.PI * 2);
      double dx = k * Math.sin(t) + iwidth / 2;
      double dy = k * Math.cos(t) + iheight / 2;
      g.setColor(DLUtil.RandomColor(0f, 1f, 0f, 0.5f, 0.5f, 1f));
      if (mode == 1) {
        float x = (float) dx;
        float y = (float) dy;
        float s = DLUtil.RangeRandom(0f, 5f);
        Shape sh = new Ellipse2D.Float(x - s / 2f, y - s / 2f, s, s);
        g.fill(sh);
      } else {
        int ix = (int) Math.floor(dx);
        int iy = (int) Math.floor(dy);
        g.drawLine(ix, iy, ix, iy);
        // g.fillRect(x, y, 1, 1);
      }
    }
    return img;
  }

  DLPshit copy() {
    return new DLPshit(this);
  }

  public void randomize() {
    int i = DLUtil.RangeRandom(4 * DLParams.DRAWING_STEP, 10 * DLParams.DRAWING_STEP);
    iwidth = i;
    iheight = i;
    points = DLUtil.RangeRandom(500, 1000);
  }

}
