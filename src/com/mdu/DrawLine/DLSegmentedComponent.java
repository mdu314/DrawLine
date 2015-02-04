package com.mdu.DrawLine;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Date;

abstract class DLSegmentedComponent extends DLComponent implements DLSegmented {
  static Point2D.Float p1 = new Point2D.Float();

  DLPointList points = new DLPointList();

  DLSegmentedComponent() {
    super();
  }

  DLSegmentedComponent(float x, float y) {
    super(x, y);
  }

  @Override
  public void addSegment(float x, float y, long when) {
    final DLPoint s = new DLPoint(x, y, when);
    points.add(s);
  }

  @Override
  public void addSegment(MouseEvent e) {
    addSegment(e.getX(), e.getY(), e.getWhen());
  }

  @Override
  public void addSomeSegments(int n) {
    while (n-- >= 0) {
      final int x = DLUtil.RangeRandom(10, 100);
      final int y = DLUtil.RangeRandom(10, 100);
      final long when = new Date().getTime();
      addSegment(x, y, when);
    }
  }

  @Override
  public void drawLastSegment(Graphics g) {
    final int sz = points.size();
    if (sz >= 1)
      drawSegment((Graphics2D) g, sz - 1);
  }

  abstract void drawSegment(Graphics2D g, int i);

  @Override
  boolean mouse(MouseEvent e) {
    return false;
  }

  @Override
  void transform(AffineTransform tr) {
    transformPointList(tr);
  }

  void transformPointList(AffineTransform tr) {
    points.transform(tr);
  };

  Rectangle getBounds(boolean deco) {
    return getBounds();
  }

  void prepareForDisplay() {
    final DLSegmented seg = (DLSegmented) this;
    int x = 0;
    int y = 0;
    final int k = DLUtil.RangeRandom(10, 30);
    for (int ii = 0; ii < k; ii++) {
      final int dx = DLUtil.RangeRandom(-30, 30);
      final int dy = DLUtil.RangeRandom(-30, 30);
      final long now = System.currentTimeMillis() + ii * 1000;
      x += dx;
      y += dy;
      seg.addSegment(x, y, now);
    }
  }

}
