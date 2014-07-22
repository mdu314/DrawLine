package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DEBUG;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.jhlabs.image.ShadowFilter;

class Selection {
  DLCurve comp;
  Color color = Color.black;
  BufferedImage selectionImage;
  int radius = 4;

  Selection(DLCurve comp) {
    super();
    this.comp = comp;
  }

  Rectangle boundingBox() {
    Rectangle r = comp.path.getBounds();
    int x = r.x - radius;
    int y = r.y - radius;
    int w = r.width + 2 * radius;
    int h = r.height + 2 * radius;
    return new Rectangle(x, y, w, h);
  }

  void draw(Graphics2D g) {
    Rectangle r = comp.path.getBounds();
    int ix = r.x - radius;
    int iy = r.y - radius;
    BufferedImage image = getSelectionImage();
    g.drawImage(image, ix, iy, null);
    if (DEBUG) {
      g.setColor(Color.red);
      int w = image.getWidth() - 1;
      int h = image.getHeight() - 1;
      g.drawRect(ix, iy, w, h);
      g.drawLine(ix, iy, ix + w, iy + h);
      g.drawLine(ix + w, iy, ix, iy + h);
    }
  }

  BufferedImage getSelectionImage() {
    if (selectionImage == null)
      selectionImage = makeSelectionImage();
    return selectionImage;
  }

  BufferedImage makeSelectionImage() {
    Rectangle r = comp.path.getBounds();
    int iw = r.width + 2 * radius;
    int ih = r.height + 2 * radius;

    BufferedImage image = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gi = (Graphics2D) image.getGraphics();
    // DLUtil.SetHints(gi);
    Color oColor = gi.getColor();
    if (color != null)
      gi.setColor(color);
    int w = r.width;
    int h = r.height;
    gi.setStroke(new BasicStroke(2f));
    gi.drawRoundRect(radius, radius, w, h, w / 4, h / 4);
    if (oColor != null)
      gi.setColor(oColor);
    ShadowFilter f = new ShadowFilter(radius, 0f, 0f, 1f);
    f.setShadowOnly(true);
    f.setAddMargins(false);
    f.setShadowColor(0xff0000);
    BufferedImage bi = f.filter(image, null);
    if (DEBUG) {
      gi.setColor(Color.red);
      gi.drawRect(0, 0, iw, ih);
    }
    return bi;
  }

}