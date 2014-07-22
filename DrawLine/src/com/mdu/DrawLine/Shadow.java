package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DEBUG;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.jhlabs.image.ShadowFilter;

class Shadow {

  int xOff = 5;
  int yOff = 7;
  Color color = Color.black;
  DLComponent curve;
  BufferedImage shadowImage;
  int radius = 10;
  float opacity = 0.9f;

  public String toString() {
    String s = "Shadow" ;
    s += " xOff " + xOff + " yOff " + yOff;
    s += " color " + color;
    s += " radius " + radius;
    s += " opacity " + opacity;
    return s;
  }
  
  public int getxOff() {
    return xOff;
  }

  public void setxOff(int xOff) {
    this.xOff = xOff;
  }

  public int getyOff() {
    return yOff;
  }

  public void setyOff(int yOff) {
    this.yOff = yOff;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  Shadow(DLComponent curve) {
    this.curve = curve;
  }

  void draw(Graphics2D g) {
    Rectangle r = curve.getBounds(); // curve.path.getBounds2D();
    int ix = (int) Math.floor(r.x - radius + xOff);
    int iy = (int) Math.floor(r.y - radius + yOff);
    BufferedImage image = getShadow();
    g.drawImage(image, ix, iy, null);

    if (DEBUG) {
      g.setColor(Color.red);
      g.drawRect(ix, iy, image.getWidth() - 1, image.getHeight() - 1);
      g.drawLine(ix, iy, ix + image.getWidth(), iy + image.getHeight());
      g.drawLine(ix + image.getWidth(), iy, ix, iy + image.getHeight());
    }
  }

  BufferedImage getShadow() {
    if (shadowImage == null)
      shadowImage = makeShadow();
    return shadowImage;
  }

  BufferedImage makeShadow() {
    Rectangle r = curve.getBounds(); // curve.path.getBounds();
    int iw = (int) Math.ceil(r.width + 2 * radius);
    int ih = (int) Math.ceil(r.height + 2 * radius);

    BufferedImage image = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gi = (Graphics2D) image.getGraphics();
    DLUtil.SetHints(gi);
    double dx = -r.x + radius;
    double dy = -r.y + radius;

    AffineTransform tr = AffineTransform.getTranslateInstance(dx, dy);
    AffineTransform save = gi.getTransform();
    gi.transform(tr);
    Color oColor = gi.getColor();
    if (color != null)
      gi.setColor(color);

    curve.paint(gi, false);

    gi.setTransform(save);
    if (oColor != null)
      gi.setColor(oColor);
    ShadowFilter f = new ShadowFilter(radius, 0, 0, opacity);
    f.setShadowOnly(true);
    f.setAddMargins(false);
    BufferedImage bi = f.filter(image, null);
    return bi;
  }

}
