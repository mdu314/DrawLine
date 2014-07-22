package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class DLPolylineRenderer extends DLRenderer {
  GeneralPath curve;
  Color color;
  DLPolyline polyline;

  @Override
  void render(Graphics2D g) {
    drawCurve(g);
  }

  @Override
  Shape makeGraphics() {
    if (curve == null)
      curve = curve();
    return curve;
  }

  void drawCurve(Graphics2D g) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(color);
    g.draw(curve);
  }
  
  void render(Graphics2D g, int i) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    DLPoint ls = polyline.points.get(i - 1);
    DLPoint s = polyline.points.get(i);
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

  /**
  * http://ovpwp.wordpress.com/2008/12/17/how-to-draw-a-smooth-curve-through-a-set-of-2d-points-with-bezier-methods/
  * @param g
  */
  GeneralPath curve() {
    int sz = polyline.points.size();
    Point2D[] pts = new Point2D[sz];
    for (int i = 0; i < sz; i++) {
      DLPoint s = polyline.points.get(i);
      pts[i] = new Point2D.Double(s.x, s.y);
    }
    Point2D.Double[] fcp = new Point2D.Double[sz - 1];
    Point2D.Double[] scp = new Point2D.Double[sz - 1];
    PolyUtils.GetCurveControlPoints(pts, fcp, scp);

    GeneralPath p = new GeneralPath();
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
