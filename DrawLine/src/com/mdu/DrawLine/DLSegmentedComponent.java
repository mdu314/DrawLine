package com.mdu.DrawLine;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

abstract class DLSegmentedComponent extends DLComponent {
  ArrayList<DLPoint> points = new ArrayList<DLPoint>();
  
  DLSegmentedComponent(int x, int y) {
    super(x, y);
  }

  DLSegmentedComponent() {
    super();
  }

  public void addSomeSegments(int n) {
    while (n-- >= 0) {
      int x = DLUtil.RangeRandom(10, 100);
      int y = DLUtil.RangeRandom(10, 100);
      long when = new Date().getTime();
      addSegment(x, y, when);
    }
  }

  public void addSegment(MouseEvent e) {
    addSegment(e.getX(), e.getY(), e.getWhen());
  }

  public void addSegment(int x, int y, long when) {
    DLPoint s = new DLPoint(x, y, when);
    points.add(s);
  }

  abstract void drawSegment(Graphics2D g, int i);
  
  public void drawLastSegment(Graphics g) {
    int sz = points.size();
    if (sz > 1)
      drawSegment((Graphics2D) g, sz - 1);
  }

  static Point2D.Float p1 = new Point2D.Float();

  void transformPointList(AffineTransform tr) {
    Iterator<DLPoint> i = points.iterator();
    while (i.hasNext()) {
      DLPoint p = i.next();
      p1.x = p.x;
      p1.y = p.y;
      tr.transform(p1, p1);
      p.x = p1.x;
      p.y = p1.y;
      if(p.shape != null)
        p.shape = tr.createTransformedShape(p.shape);
    }
  }

}
