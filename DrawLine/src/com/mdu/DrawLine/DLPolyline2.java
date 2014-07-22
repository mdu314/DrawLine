package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DLPolyline2 extends DLShape implements DLCollectableObject {
  ArrayList<DLPoint> points = new ArrayList<DLPoint>();
  Color color;
  DLLineBrush brush;
  Path2D curve;
  Point2D.Double[] fcp;
  Point2D.Double[] scp;

  public void paint(Graphics gr) {
    Graphics2D g = (Graphics2D) gr;
    if (curve == null)
      curve = curve();
    drawCurve(g);
  }

  void drawCurve(Graphics2D g) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(color);
    g.draw(curve);

    g.setColor(Color.red);

    PathIterator i = curve.getPathIterator(null, 10000);
    double c[] = new double[2];
    int j = 0;
    Point2D last = null;
    while (!i.isDone()) {
      i.currentSegment(c);
      i.next();
      if (last != null) {
        g.draw(new Line2D.Double(last.getX(), last.getY(), c[0], c[1]));
      }
      g.draw(new Rectangle2D.Double(c[0] - 1, c[1] - 1, 2, 2));

      if (j < fcp.length) {
        Point2D cp1 = fcp[j];
        Point2D cp2 = scp[j];
        last = cp2;
        g.draw(new Line2D.Double(c[0], c[1], cp1.getX(), cp1.getY()));
      }
      //      , cp2.getX(), cp2.getY(), pt.getX(), pt.getY());
      g.draw(new Rectangle2D.Double(c[0] - 2, c[1] - 2, 2, 2));
      j++;
    }
  }

  void addSegment(int x, int y, long when) {
    DLPoint s = new DLPoint(x, y, when);
    points.add(s);
  }

  void drawSegment(Graphics2D g, int i) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    DLPoint ls = points.get(i - 1);
    DLPoint s = points.get(i);
    double x = s.x;
    double y = s.y;
    double dx = x - ls.x;
    double dy = y - ls.y;

    double size = 5;
    Rectangle2D rect = new Rectangle2D.Double(x - size / 2, y - size / 2, size, size);

    double factor = Math.abs(dy) < 0.1 ? 0 : Math.abs(dx / dy);
    g.setColor(DLUtil.BrighterColor(color, factor));
    Line2D line = new Line2D.Double(ls.x, ls.y, x, y);
    g.draw(line);
    g.fill(rect);
  }

  void drawLastSegment(Graphics2D g) {
    int sz = points.size();
    if (sz > 1)
      drawSegment(g, sz - 1);
  }

  /**
  * http://ovpwp.wordpress.com/2008/12/17/how-to-draw-a-smooth-curve-through-a-set-of-2d-points-with-bezier-methods/
  * @param g
  */
  Path2D curve() {
    int sz = points.size();
    Point2D[] pts = new Point2D[sz];
    for (int i = 0; i < sz; i++) {
      DLPoint s = points.get(i);
      pts[i] = new Point2D.Double(s.x, s.y);
    }
    fcp = new Point2D.Double[sz - 1];
    scp = new Point2D.Double[sz - 1];
    PolyUtils.GetCurveControlPoints(pts, fcp, scp);

    Path2D p = new Path2D.Double();
    p.moveTo(pts[0].getX(), pts[0].getY());

    for (int i = 0; i < pts.length - 1; i++) {
      Point2D cp1 = fcp[i];
      Point2D cp2 = scp[i];
      Point2D pt = pts[i + 1];
      p.curveTo(cp1.getX(), cp1.getY(), cp2.getX(), cp2.getY(), pt.getX(), pt.getY());
    }
    return p;
  }
}
