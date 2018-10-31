package com.mdu.DrawLine;

import java.util.ArrayList;

import shapeFile.files.shp.shapeTypes.ShpPolygon;
import shapeFile.shapeFile.ShapeFile;

public class DLMap extends DLCurve {

  float scale = 1.5f;

  public DLMap() {
    super();
  }

  DLMap(DLMap src) {
    this(src.x, src.y);
    scale = src.scale;
  }

  public DLMap(float x, float y) {
    super(x, y);
  }

  DLMap copy() {
    return new DLMap(this);
  }

  DLPath path() {
    String resources = "resources";
    String basename = "countries";
    ShapeFile s = null;
    
    DLPath path = new DLPath();
    
    try {     
      s = new ShapeFile(resources, basename);
      s.read();
      ArrayList<ShpPolygon> shapes = s.getSHP_shape();

      for (ShpPolygon sp : shapes) {
        double[][] pts = sp.getPoints();
        double d2 = 0;
        double max = 360. * scale;
        double lx = pts[0][0] * scale;
        double ly = -pts[0][1] * scale;
        for (int i = 0; i < sp.getNumberOfPoints(); i++) {
          double x = pts[i][0] * scale;
          double y = -pts[i][1] * scale;
          d2 = (x - lx) * (x - lx) + (y - ly) * (y - ly);
          if(i == 0 || d2 > max)
            path.moveTo(x, y);
          else
            path.lineTo(x, y);
          lx = x;
          ly = y;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return path;
  }
}
