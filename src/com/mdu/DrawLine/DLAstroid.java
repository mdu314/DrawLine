package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

class DLAstroid extends DLCurve {
  float p = 100;

  public DLAstroid() {
    super();
  }
  
  public DLAstroid(DLAstroid a) {
    super(a);
    p = a.p;
  }

  public DLAstroid(float x, float y) {
    super(x, y);
  }

  public DLAstroid copy() {
    return new DLAstroid(this);
  }

  DLPath path() {
    final DLPath c = new DLPath();
    for (float t = 0; t < 2 * Math.PI; t += SAMPLE_PRECISION) {
      final double cost = cos(t);
      final double sint = sin(t);
      final double x = p * cost * cost * cost;
      final double y = p * sint * sint * sint;
      if (t == 0)
        c.moveTo(x, y);
      else
        c.lineTo(x, y);
    }
    c.closePath();
    final AffineTransform tr = new AffineTransform();
    tr.translate(x, y);
    c.transform(tr);
    return c;
  }

  public void randomize() {
    super.randomize();
    p = RangeRandom(10f, 30f);
  }

  public void setP(float p) {
    Rectangle r = redisplayStart();    
    this.p = p;
    clear();
    redisplay(r);
  }
  
  public float getP() {
    return p;
  }
  
  public float[] rangeP() {
    return new float[] {1, 200};
  }
  
  public static void main(String[] a) {
    int w = 800;
    int h = 600;

    Object[][] params = {
        {
            "iwidth", w
        }, {
            "iheight", h
        }, {
            "x", w / 2
        }, {
            "y", h / 2
        }, {
            "threadSleep", 5
        }
    };

    DLAstroid dl = (DLAstroid)DLMain.Main(DLAstroid.class, params);
//    Rectangle r = dl.redisplayStart();
//    dl.clear();
//    dl.redisplay(r);
  }

}
