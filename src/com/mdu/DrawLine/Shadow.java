package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DEBUG;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.jhlabs.image.ShadowFilter;

class Shadow {

  Color color = Color.black;
  DLComponent curve;
  float opacity = 0.9f;
  int radius = 10;
  BufferedImage shadowImage;
  int xOff = 5;
  int yOff = 7;

  Shadow(DLComponent curve) {
    this.curve = curve;
  }

  void draw(Graphics2D g) {
    final Rectangle r = curve.getBounds(); // curve.path.getBounds2D();
    final int ix = (int) Math.floor(r.x - radius + xOff);
    final int iy = (int) Math.floor(r.y - radius + yOff);
    final BufferedImage image = getShadow();
    g.drawImage(image, ix, iy, null);

    if (DEBUG) {
      g.setColor(Color.red);
      g.drawRect(ix, iy, image.getWidth() - 1, image.getHeight() - 1);
      g.drawLine(ix, iy, ix + image.getWidth(), iy + image.getHeight());
      g.drawLine(ix + image.getWidth(), iy, ix, iy + image.getHeight());
    }
  }

  public Rectangle getBounds(Rectangle bounds) {
    final int r = radius;
    final int x = bounds.x + xOff - r;
    final int y = bounds.y + yOff - r;
    final int w = bounds.width + 2 * r;
    final int h = bounds.height + 2 * r;
    return new Rectangle(x, y, w, h);
  }
  
  public Color getColor() {
    return color;
  }

  BufferedImage getShadow() {
    if (shadowImage == null)
      shadowImage = makeShadow();
    return shadowImage;
  }

  public int getxOff() {
    return xOff;
  }

  public int getyOff() {
    return yOff;
  }

  BufferedImage makeShadow() {
    final Rectangle r = curve.getBounds(); // curve.path.getBounds();
    final int iw = (int) Math.ceil(r.width + 2 * radius);
    final int ih = (int) Math.ceil(r.height + 2 * radius);

    final BufferedImage image = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D gi = (Graphics2D) image.getGraphics();
    DLUtil.SetHints(gi);
    final double dx = -r.x + radius;
    final double dy = -r.y + radius;

    final AffineTransform tr = AffineTransform.getTranslateInstance(dx, dy);
    final AffineTransform save = gi.getTransform();
    gi.transform(tr);
    final Color oColor = gi.getColor();
    if (color != null)
      gi.setColor(color);

    curve.paint(gi, false);

    gi.setTransform(save);
    if (oColor != null)
      gi.setColor(oColor);
    final ShadowFilter f = new ShadowFilter(radius, 0, 0, opacity);
    f.setShadowOnly(true);
    f.setAddMargins(false);
    final BufferedImage bi = f.filter(image, null);
    return bi;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public void setxOff(int xOff) {
    this.xOff = xOff;
  }

  public void setyOff(int yOff) {
    this.yOff = yOff;
  }

  @Override
  public String toString() {
    String s = "Shadow";
    s += " xOff " + xOff + " yOff " + yOff;
    s += " color " + color;
    s += " radius " + radius;
    s += " opacity " + opacity;
    return s;
  }

}
