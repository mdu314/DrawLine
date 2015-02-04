package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DEBUG;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

class Selection {
  Color color = Color.darkGray;
  DLComponent comp;
  BufferedImage selectionImage;

  Selection(DLComponent comp) {
    super();
    this.comp = comp;
  }

  Rectangle boundingBox() {
    Rectangle r = comp.getBounds(false);
    //    r = comp.addShadowBounds(r);
    return new Rectangle(r.x, r.y, r.width, r.height);
  }

  void draw(Graphics2D g) {
    final Rectangle r = boundingBox();
    final int ix = r.x;
    final int iy = r.y;
    final BufferedImage image = getSelectionImage();
    g.drawImage(image, ix, iy, null);
    if (DEBUG) {
      g.setColor(Color.red);
      final int w = image.getWidth() - 1;
      final int h = image.getHeight() - 1;
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
    Rectangle r = boundingBox();
    int iw = r.width;
    int ih = r.height;
    if (iw <= 0)
      iw = 1;
    if (ih <= 0)
      ih = 1;
    BufferedImage image = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) image.getGraphics();
    DLUtil.SetHints(g);
    Color oColor = g.getColor();
    if (color != null)
      g.setColor(color);
    int w = r.width;
    int h = r.height;

    RoundRectangle2D.Float dr = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 10, 10);
    g.draw(dr);

    AffineTransform tr = AffineTransform.getTranslateInstance(-r.x, -r.y);
    g.setTransform(tr);
    comp.paint(g, false);

    if (oColor != null)
      g.setColor(oColor);

    if (DEBUG) {
      g.setColor(Color.red);
      g.drawRect(0, 0, iw - 1, ih - 1);
    }

    return image;
  }

}