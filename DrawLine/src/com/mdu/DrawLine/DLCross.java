package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.geom.Path2D;
import static com.mdu.DrawLine.DLUtil.*;

class DLCross extends DLCurve {
  DLPoint p1 = new DLPoint(10, 10);
  DLPoint p2 = new DLPoint(50, 15);
  DLPoint p3 = new DLPoint(40, 0);

  DLCross(DLCross p) {
    super(p);
  }

  public DLCross(int x, int y) {
    super(x, y);
  }

  DLCross copy() {
    return new DLCross(this);
  }

  Path2D path() {
    Path2D path = null;

    for (double t = 2 * Math.PI; t > 0; t -= Math.PI / 2) {
      DLPoint rp1 = DLUtil.Rotate(p1.x, p1.y, t);
      DLPoint rp2 = DLUtil.Rotate(p2.x, p2.y, t);
      DLPoint rp3 = DLUtil.Rotate(p3.x, p3.y, t);

      path = DLUtil.AddPoint(rp1.x, rp1.y, path);
      path = DLUtil.AddPoint(rp2.x, rp2.y, path);
      path = DLUtil.AddPoint(rp3.x, rp3.y, path);

      rp1 = DLUtil.Rotate(p1.x, -p1.y, t);
      rp2 = DLUtil.Rotate(p2.x, -p2.y, t);
      rp3 = DLUtil.Rotate(p3.x, -p3.y, t);

      path = DLUtil.AddPoint(rp3.x, rp3.y, path);
      path = DLUtil.AddPoint(rp2.x, rp2.y, path);
      path = DLUtil.AddPoint(rp1.x, rp1.y, path);
    }    
    path.closePath();
    transform(path);
    return path;
  }

  public void randomize() {
    setShadow(true);
    p1 = new DLPoint(RangeRandom(5, 15), RangeRandom(5, 15));
    p2 = new DLPoint(RangeRandom(40, 60), RangeRandom(10,  20));
    p3 = new DLPoint(RangeRandom(30,  50), RangeRandom(0, 5));
    super.randomize();
  }

}
