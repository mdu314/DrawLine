package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class DLPlante extends DLImage {
  double atAngleLeft = 22.5; // Angle of the left branch.
  double atAngleRight = 22.5; // Angle of the right branch.
  double atLengthLeft = 1.0; // Length of the left branch.
  double atLengthRight = 1.0; // Length of the right branch.
  int atSize = 200;
  int atDepth = 3;
  Color atForeground = Color.green;

  public double getAtAngleLeft() {
    return atAngleLeft;
  }

  public void setAtAngleLeft(double atAngleLeft) {
    this.atAngleLeft = atAngleLeft;
    stopAll();
    clearImage();
    runThreaded();
  }

  double[] rangeAtAngleLeft() {
    return new double[] { 0, 360 };
  }

  public double getAtAngleRight() {
    return atAngleRight;
  }

  public void setAtAngleRight(double atAngleRight) {
    this.atAngleRight = atAngleRight;
    stopAll();
    clearImage();
    runThreaded();
  }

  double[] rangeAtAngleRight() {
    return new double[] { 0, 360 };
  }

  public double getAtLengthLeft() {
    return atLengthLeft;
  }

  public double[] rangeAtLengthLeft() {
    return new double[]{0.5, 1.5};
  }

  public void setAtLengthLeft(double atLengthLeft) {
    this.atLengthLeft = atLengthLeft;
    stopAll();
    clearImage();
    runThreaded();
  }

  public double getAtLengthRight() {
    return atLengthRight;
  }

  public double[] rangeAtLengthRight() {
    return new double[]{0.5, 1.5};
  }

  public void setAtLengthRight(double atLengthRight) {
    this.atLengthRight = atLengthRight;
    stopAll();
    clearImage();
    runThreaded();
  }

  public int getAtSize() {
    return atSize;
  }

  public void setAtSize(int atSize) {
    this.atSize = atSize;
    stopAll();
    clearImage();
    runThreaded();
  }

  public int getAtDepth() {
    return atDepth;
  }

  public void setAtDepth(int atDepth) {
    this.atDepth = atDepth;
    stopAll();
    clearImage();
    runThreaded();
  }

  public Color getAtForeground() {
    return atForeground;
  }

  public void setAtForeground(Color atForeground) {
    this.atForeground = atForeground;
    stopAll();
    clearImage();
    runThreaded();
  }

  DLPlante() {
    super();
  }

  DLPlante(DLPlante src) {
    super();
  }

  public DLPlante(float x, float y) {
    super(x, y);
  }

  DLPlante copy() {
    return new DLPlante(this);
  }

  protected void recursive(Graphics g, int agDepth, double agPoint1X, double agPoint1Y, double agPoint2X,
      double agPoint2Y, DLThread t) {
    if ((t != null) && t.isStopped())
      return;
    if (agDepth <= 0) {
      Line2D.Double l = new Line2D.Double(agPoint1X, agPoint1Y, agPoint2X, agPoint2Y);
      ((Graphics2D)g).draw(l);
      if (parent != null) {
        final Rectangle r = getBounds();
        parent.paint(r);
      }
    } else {
      double lcAngleLeft = Math.toRadians(atAngleLeft);
      double lcAngleRight = Math.toRadians(atAngleRight);

      double lcAxis1X = (agPoint2X - agPoint1X) / 2.0;
      double lcAxis1Y = (agPoint2Y - agPoint1Y) / 2.0;

      double lcAxis2X = (Math.cos(2 * lcAngleRight) * lcAxis1X - Math.sin(2 * lcAngleRight) * lcAxis1Y) * atLengthRight;

      double lcAxis2Y = (Math.cos(2 * lcAngleRight) * lcAxis1Y + Math.sin(2 * lcAngleRight) * lcAxis1X) * atLengthRight;

      double lcAxis3X = (Math.cos(lcAngleRight) * lcAxis1X - Math.sin(lcAngleRight) * lcAxis1Y) * atLengthRight;

      double lcAxis3Y = (Math.cos(lcAngleRight) * lcAxis1Y + Math.sin(lcAngleRight) * lcAxis1X) * atLengthRight;

      double lcAxis4X = (Math.cos(lcAngleLeft) * lcAxis1X + Math.sin(lcAngleLeft) * lcAxis1Y) * atLengthLeft;

      double lcAxis4Y = (Math.cos(lcAngleLeft) * lcAxis1Y - Math.sin(lcAngleLeft) * lcAxis1X) * atLengthLeft;

      double lcPoint3X = agPoint1X + lcAxis1X;
      double lcPoint3Y = agPoint1Y + lcAxis1Y;
      double lcPoint4X = agPoint2X + lcAxis2X;
      double lcPoint4Y = agPoint2Y + lcAxis2Y;
      double lcPoint5X = lcPoint4X + lcAxis3X;
      double lcPoint5Y = lcPoint4Y + lcAxis3Y;
      double lcPoint6X = lcPoint5X + lcAxis1X;
      double lcPoint6Y = lcPoint5Y + lcAxis1Y;
      double lcPoint7X = agPoint2X + lcAxis4X;
      double lcPoint7Y = agPoint2Y + lcAxis4Y;
      double lcPoint8X = lcPoint7X + lcAxis1X;
      double lcPoint8Y = lcPoint7Y + lcAxis1Y;
      double lcPoint9X = lcPoint8X + lcAxis3X;
      double lcPoint9Y = lcPoint8Y + lcAxis3Y;

      if ((t != null) && t.isStopped())
        return;
      recursive(g, agDepth - 1, agPoint1X, agPoint1Y, lcPoint3X, lcPoint3Y, t);

      if ((t != null) && t.isStopped())
        return;
      recursive(g, agDepth - 1, lcPoint3X, lcPoint3Y, agPoint2X, agPoint2Y, t);

      if ((t != null) && t.isStopped())
        return;
      recursive(g, agDepth - 1, agPoint2X, agPoint2Y, lcPoint4X, lcPoint4Y, t);

      if ((t != null) && t.isStopped())
        return;
      recursive(g, agDepth - 1, lcPoint4X, lcPoint4Y, lcPoint5X, lcPoint5Y, t);

      if ((t != null) && t.isStopped())
        return;
      recursive(g, agDepth - 1, lcPoint5X, lcPoint5Y, lcPoint6X, lcPoint6Y, t);

      if ((t != null) && t.isStopped())
        return;
      recursive(g, agDepth - 1, agPoint2X, agPoint2Y, lcPoint7X, lcPoint7Y, t);

      if ((t != null) && t.isStopped())
        return;
      recursive(g, agDepth - 1, lcPoint7X, lcPoint7Y, lcPoint8X, lcPoint8Y, t);

      if ((t != null) && t.isStopped())
        return;
      recursive(g, agDepth - 1, lcPoint8X, lcPoint8Y, lcPoint9X, lcPoint9Y, t);
    }
  }

  public void f(Graphics2D g, DLThread t) {
    g.setColor(atForeground);
    //recursive(g, Math.abs(atDepth), iwidth / 2, iheight, iwidth / 2, 0, t); //atSize * 0.5, atSize , atSize * 0.5, atSize * 0.7, t);
    recursive(g, Math.abs(atDepth), iwidth * 0.5, atSize , atSize * 0.5, atSize * 0.5, t); //atSize * 0.7, t);
    if (parent != null) {
      final Rectangle r = getBounds();
      parent.paint(r);
    }
  }

  @Override
  BufferedImage image() {
    BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = (Graphics2D) img.getGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);
    else
      f(g, null);
    return img;
  }

  @Override
  public void randomize() {
    super.randomize();
    iwidth = RangeRandom(100, 200);
    iheight = RangeRandom(100, 200);
  }

}
