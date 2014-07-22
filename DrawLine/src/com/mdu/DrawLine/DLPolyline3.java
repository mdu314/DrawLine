package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DLPolyline3 extends DLShape implements DLCollectableObject {
  ArrayList<DLPoint> points = new ArrayList<DLPoint>();
  private DLRenderer renderer = null;
  
  public void paint(Graphics gr) {
    if(renderer == null)
      return;
    Graphics2D g = (Graphics2D) gr;
    renderer.render(g);
  }
  
  void addSegment(int x, int y, long when) {
    DLPoint s = new DLPoint(x, y, when);
    points.add(s);
  }
  
  void drawLastSegment(Graphics2D g) {
    int sz = points.size();
    if (sz > 1)
      renderer.render(g, sz - 1);
  }

  DLRenderer getRenderer() {
    return renderer;
  }

  void setRenderer(DLRenderer renderer) {
    this.renderer = renderer;
  }
}
