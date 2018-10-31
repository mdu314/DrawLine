package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

abstract class DLPointImage extends DLImage {

  float pointSize = 0f;
  public static final String EllipsePoint = "ellipsePoint";
  public static final String PolyPoint = "polyPoint";
  public static final String RectanglePoint = "rectPoint";
  public static final String StarPoint = "starPoint";
  public static final String LinePoint = "linePoint";
  public static final String HeartPoint = "heartPoint";
  public static final String[] pointShapes = { EllipsePoint, PolyPoint, RectanglePoint, StarPoint, LinePoint,
      HeartPoint };
  String pointShape = EllipsePoint;
  Paint pointFill;
  Paint pointStroke;

  DLPointImage() {
    super();
  }

  DLPointImage(float x, float y) {
    super(x, y);
  }

  DLPointImage(DLPointImage i) {
    super(i);
  }
  
  public String getPointShape() {
    return pointShape;
  }

  public void setPointShape(String pointShape) {
    this.pointShape = pointShape;
    stopAll();
    clear();
    run();
  }

  public String[] enumPointShape() {
    return new String[] { EllipsePoint, PolyPoint, RectanglePoint, LinePoint, StarPoint, HeartPoint };
  }

  public float getPointSize() {
    return pointSize;
  }

  public void setPointSize(float s) {
    pointSize = s;
    stopAll();
    clear();
    run();
  }

  public float[] rangePointSize() {
    return new float[] { 0, 20 };
  }

  void drawPoint(double x, double y) {
    drawPoint((float) x, (float) y);
  }

  void drawPoint(float x, float y) {
    if (image != null)
      drawPoint(image.createGraphics(), x, y);
  }

  void paintAsCircle(Graphics2D g, float x, float y, float s) {
    Shape sh = DLUtil.Circle(x, y, s);
    if (pointFill != null) {
      g.setPaint(pointFill);
      g.fill(sh);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(sh);
    }
  }

  void paintAsPolygon(Graphics2D g, float x, float y, float s) {
    Shape sh = DLUtil.Polygon(x, y, 5, s / 2);
    if (pointFill != null) {
      g.setPaint(pointFill);
      g.fill(sh);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(sh);
    }
  }

  void paintAsRectangle(Graphics2D g, float x, float y, float s) {
    Shape sh = DLUtil.Square(x, y, s);
    if (pointFill != null) {
      g.setPaint(pointFill);
      g.fill(sh);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(sh);
    }
  }

  void paintAsIntLine(Graphics2D g, float x, float y) {
    int ix = DLUtil.Int(x);
    int iy = DLUtil.Int(y);
    Line2D.Float l = new Line2D.Float(ix, iy, ix, iy);
    if (pointFill != null) {
      g.setPaint(pointFill);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
    }
    g.draw(l);
  }

  void paintAsLine(Graphics2D g, float x, float y) {
    Line2D.Float l = new Line2D.Float(x, y, x, y);
    g.draw(l);
  }

  void paintAsLine(Graphics2D g, float x, float y, float size) {
    float s2 = size / 2;
    DLPath p = new DLPath();
    p.moveTo(x - s2, y - s2);
    p.lineTo(x + s2, y + s2);
    p.moveTo(x - s2, y + s2);
    p.lineTo(x + s2, y - s2);
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(p);
    }
  }

  void paintAsStar(Graphics2D g, float x, float y, float size) {
    size = size / 2f;
    Shape s = DLUtil.Star(x, y, size / 2, size, 7, 0);
    if (pointFill != null) {
      g.setPaint(pointFill);
      g.fill(s);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(s);
    }
  }

  void paintAsHeart(Graphics2D g, float x, float y, float size) {
    Shape s = DLUtil.Heart(x - size / 2, y - size / 2, size, size, true);
    if (pointFill != null) {
      g.setPaint(pointFill);
      g.fill(s);
    }
    if (pointStroke != null) {
      g.setPaint(pointStroke);
      g.draw(s);
    }
  }

  Shape getShapePoint(String pointShape, float size) {
    Shape s = null;
    switch (pointShape) {
    case EllipsePoint:
      s = DLUtil.Circle(x, y, size);
      break;
    case PolyPoint:
      s = DLUtil.Polygon(x, y, 5, size / 2);
      break;
    case RectanglePoint:
      s = DLUtil.Square(x, y, size);
      break;
    case StarPoint:
      size = size / 2f;
      s = DLUtil.Star(x, y, size / 2, size, 7, 0);
      break;
    case LinePoint:
      float s2 = size / 2;
      DLPath p = new DLPath();
      p.moveTo(x - s2, y - s2);
      p.lineTo(x + s2, y + s2);
      p.moveTo(x - s2, y + s2);
      p.lineTo(x + s2, y - s2);
      s = p;
      break;
    default:
      break;
    }
    return s;
  }

  void drawPoint(Graphics2D g, double x, double y) {
    drawPoint(g, (float) x, (float) y);
  }

  void drawPoint(Graphics2D g, String shape, float ps, float x, float y) {
    if (ps == 0) {
      paintAsIntLine(g, x, y);
    } else {
      switch (shape) {
      case EllipsePoint:
        paintAsCircle(g, x, y, ps);
        break;
      case PolyPoint:
        paintAsPolygon(g, x, y, ps);
        break;
      case RectanglePoint:
        paintAsRectangle(g, x, y, ps);
        break;
      case StarPoint:
        paintAsStar(g, x, y, ps);
        break;
      case LinePoint:
        paintAsLine(g, x, y, ps);
        break;
      case HeartPoint:
        paintAsHeart(g, x, y, ps);
      default:
        break;
      }
    }
  }

  void drawPoint(Graphics2D g, float x, float y) {
    drawPoint(g, pointShape, pointSize, x, y);
  }

  public Paint getPointFill() {
    return pointFill;
  }

  public void setPointFill(Paint pointFill) {
    this.pointFill = pointFill;
  }

  public Paint getPointStroke() {
    return pointStroke;
  }

  public void setPointStroke(Paint pointStroke) {
    this.pointStroke = pointStroke;
  }
}
