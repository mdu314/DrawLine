package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

class PaintControl {
  DLContainer dl;
  boolean painting = true;
  Rectangle rectangle = null;

  PaintControl(DLContainer dl) {
    this.dl = dl;
  }

  void addRectangle(Rectangle r) {
    if (painting) {
      final String m = "Adding rectangle while painting!";
      System.err.println(m);
    }
    if (rectangle == null)
      rectangle = (Rectangle) r.clone();
    else
      Rectangle2D.union(rectangle, r, rectangle);
  }

  public Rectangle getRectangle() {
    return rectangle;
  }

  public boolean isPainting() {
    return painting;
  }

  public void setPainting(boolean painting) {
    this.painting = painting;
    if (painting && rectangle != null) {
      paint(rectangle, dl.getGraphics()); // .getPanel().getGraphics());
      rectangle = null;
    }
  }

  public void setRectangle(Rectangle rectangle) {
    this.rectangle = rectangle;
  }

  void paint() {
    Component c = dl; // .getPanel();
    Dimension size = c.getSize();
    Graphics g = c.getGraphics();
    paint(new Rectangle(size), g);
  }

  void paint(DLComponent c, Graphics g) {
    DLUtil.SetHints(g);
    Rectangle r = c.getBounds();

    if (g != null && isPainting()) {
      BufferedImage image = null;
      image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_RGB);

      Graphics2D gi = image.createGraphics();
      Color col = dl.getBackground(); // .getPanel().getBackground();
      gi.setColor(col);
      gi.fillRect(0, 0, r.width, r.height);
      AffineTransform tr = AffineTransform.getTranslateInstance(-r.x, -r.y);
      gi.setTransform(tr);
      DLUtil.SetHints(gi);

      final DLComponentList copy = dl.components.copy();
      final Iterator<DLComponent> i = copy.iterator();
      while (i.hasNext()) {
        final DLComponent dlc = i.next();
        if (r.intersects(dlc.getBounds()))
          dlc.paint(gi);
      }
      g.drawImage(image, r.x, r.y, null);
    } else
      addRectangle(r);
  }

  void paint(Rectangle r, Graphics g) {
    if(r == null)
      return;
    if (r.width <= 0 || r.height <= 0)
      return;
    if (r.width > 100000 || r.height > 100000)
      return;
    if (g == null)
      return;
    DLUtil.SetHints(g);
    if (isPainting()) {
      BufferedImage image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_RGB);
      Color c = dl.getBackground(); // getPanel().getBackground();
      Graphics2D gi = image.createGraphics();
      gi.setColor(c);
      gi.fillRect(0, 0, r.width, r.height);

      final AffineTransform tr = AffineTransform.getTranslateInstance(-r.x, -r.y);
      gi.setTransform(tr);

      DLUtil.SetHints(gi);

      final DLComponentList copy = dl.components.copy();
      final Iterator<DLComponent> i = copy.iterator();
      while (i.hasNext()) {
        final DLComponent dlc = i.next();
        if (r.intersects(dlc.getBounds()))
          dlc.paint(gi);
      }
      g.drawImage(image, r.x, r.y, null);
      image = null;
    } else
      addRectangle(r);
  }

}
