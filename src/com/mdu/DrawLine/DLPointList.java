package com.mdu.DrawLine;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("serial")
public class DLPointList extends ArrayList<DLPoint> {

  static Point2D.Float p1 = new Point2D.Float();

  DLPointList() {
    super();
  }

  void add(float x, float y) {
    add(new DLPoint(x, y));
  }
  
  DLPointList(DLPointList src) {
    addAll(src);
  }

  DLPointList copy() {
    return new DLPointList(this);
  }

  void transform(AffineTransform tr) {
    final Iterator<DLPoint> i = iterator();
    while (i.hasNext()) {
      final DLPoint p = i.next();
      p1.x = p.x;
      p1.y = p.y;
      tr.transform(p1, p1);
      p.x = p1.x;
      p.y = p1.y;
      if (p.shape != null)
        p.shape = tr.createTransformedShape(p.shape);
      if (p.dlc != null)
        p.dlc.transform(tr);
    }
  }
}
