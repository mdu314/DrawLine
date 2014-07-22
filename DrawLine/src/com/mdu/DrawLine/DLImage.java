package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

abstract class DLImage extends DLComponent {
  private Image image = null;
  int iwidth = 50;
  int iheight = 50;

  DLImage() {
    super(0, 0);
  }

  DLImage(DLImage c) {
    super(c);
    iwidth = c.iwidth;
    iheight = c.iheight;
    if (c.image == null)
      c.image = c.image();
    image = (Image) new BufferedImage(c.iwidth, c.iheight,
        BufferedImage.TYPE_INT_ARGB);
  }

  DLImage(int x, int y) {
    super(x, y);
  }

  DLImage(int x, int y, int iw, int ih) {
    super(x, y);
    iwidth = iw;
    iheight = ih;
  }

  abstract DLImage copy();

  abstract Image image();

  public void randomize() {
    iwidth = RangeRandom(20, 60);
    iheight = RangeRandom(20, 60);  
  }

  void clearBounds() {
//    bounds = null;
  }

  Rectangle getBounds() {
    if (image == null)
      image = image();
//    if (bounds == null)
      Rectangle bounds = new Rectangle(x - iwidth / 2, y - iheight / 2, iwidth, iheight);

    return bounds;
  }

  boolean hitTest(Point p) {
    if (!super.hitTest(p))
      return false;
    return true;
  }

  public void paint(Graphics gr, boolean deco) {

  }
  
  public void paint(Graphics gr) {
    Graphics2D g = (Graphics2D) gr;
    if (image == null)
      image = image();
    g.drawImage(image, x - iwidth / 2, y - iheight / 2, null);
    if(DLParams.DEBUG) {
      Rectangle b  = getBounds();
      g.setColor(Color.darkGray);
      g.drawRect(b.x,  b.y,  b.width - 1, b.height - 1);
    }
  }

  void transform(AffineTransform tr) {
    if (image == null)
      image = image();
    Point2D src = new Point2D.Float(x, y);    
    Point2D dst = tr.transform(src, null);
    x = (int)(Math.floor(dst.getX()));
    y = (int)(Math.floor(dst.getY()));
    clearBounds();
  }

  void move(int dx, int dy) {
    AffineTransform tr = AffineTransform.getTranslateInstance(dx, dy);
    transform(tr);
  }
}
