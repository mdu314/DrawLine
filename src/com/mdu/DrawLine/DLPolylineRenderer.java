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
  Color color;
  GeneralPath curve;
  DLPolyline polyline;

  /**
   * http://ovpwp.wordpress.com/2008/12/17/how-to-draw-a-smooth-curve-through-a-
   * set-of-2d-points-with-bezier-methods/
   * 
   * @param g
   */
  GeneralPath curve() {
    final int sz = polyline.points.size();
    final Point2D[] pts = new Point2D[sz];
    for (int i = 0; i < sz; i++) {
      final DLPoint s = polyline.points.get(i);
      pts[i] = new Point2D.Float(s.x, s.y);
    }
    final Point2D.Float[] fcp = new Point2D.Float[sz - 1];
    final Point2D.Float[] scp = new Point2D.Float[sz - 1];
    PolyUtils.GetCurveControlPoints(pts, fcp, scp);

    final GeneralPath p = new GeneralPath();
    p.moveTo(pts[0].getX(), pts[0].getY());

    for (int i = 0; i < pts.length - 1; i++) {
      final Point2D cp1 = fcp[i];
      final Point2D cp2 = scp[i];
      final Point2D pt = pts[i + 1];
      p.curveTo(cp1.getX(), cp1.getY(), cp2.getX(), cp2.getY(), pt.getX(), pt.getY());
    }
    return p;
  }

  void drawCurve(Graphics2D g) {
    DLUtil.SetHints(g);
    g.setColor(color);
    g.draw(curve);
  }

  @Override
  Shape makeGraphics() {
    if (curve == null)
      curve = curve();
    return curve;
  }

  @Override
  void render(Graphics2D g) {
    drawCurve(g);
  }

  @Override
  void render(Graphics2D g, int i) {
    DLUtil.SetHints(g);
    final DLPoint ls = polyline.points.get(i - 1);
    final DLPoint s = polyline.points.get(i);
    final float x = s.x;
    final float y = s.y;
    final float dx = x - ls.x;
    final float dy = y - ls.y;

    final float size = 5;
    final Rectangle2D rect = new Rectangle2D.Float(x - size / 2, y - size / 2, size, size);

    final float factor = Math.abs(dy) < 0.1 ? 0 : Math.abs(dx / dy);
    g.setColor(DLUtil.BrighterColor(color, factor));
    final Line2D line = new Line2D.Float(ls.x, ls.y, x, y);
    g.draw(line);
    g.fill(rect);
  }

}
