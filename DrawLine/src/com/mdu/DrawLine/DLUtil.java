package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.POLY_PRECISION;
import static java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static java.awt.geom.PathIterator.SEG_CLOSE;
import static java.awt.geom.PathIterator.SEG_CUBICTO;
import static java.awt.geom.PathIterator.SEG_LINETO;
import static java.awt.geom.PathIterator.SEG_MOVETO;
import static java.awt.geom.PathIterator.SEG_QUADTO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class DLUtil {
  static double TWO_PI = 6.2831853071795864769252866;
  static boolean hasSpare = false;
  static double rand1;
  static double rand2;

  @SuppressWarnings("unchecked")
  static ArrayList<Class<? extends DLComponent>> curveList = new ArrayList<Class<? extends DLComponent>>(Arrays.asList(
      DLPolygon.class, DLEgg.class, DLSpiral.class, DLHeart.class, DLSplineCircle.class, DLStar.class, DLArrow.class,
      DLFish.class, DLLemniscate.class, DLLeaf.class, DLRose.class, DLGear.class, DLAstroid.class, DLEpitrochoid.class,
      DLSuperEllipse.class, DLRosace.class, DLVonKoch.class, DLPapillon.class, DLTruc.class, DLTruc2.class,
      DLBug.class, DLPshit.class, DLSpirograph.class, DLTrefle.class, DLMoebius.class, DLChar.class, DLRuban.class,
      DLPolyline.class, DLLorem.class, DLLorenz.class, DLKnot.class, DLCross.class, null));

  static DLPoint Rotate(double px, double py, double a) {
    double x = px * Math.cos(a) - py * Math.sin(a);
    double y = px * Math.sin(a) + py * Math.cos(a);
    return new DLPoint(x, y);
  }
  
  static DLPoint Rotate(DLPoint p, double a) {
    return Rotate(p.x, p.y, a);
  }
  
  static float Normalize(float min, float max, float minVal, float maxVal, float val) {
    float v = (val - minVal) / (maxVal - minVal);
    return (max - min) * v + min;
  }

  static double Normalize(double min, double max, double minVal, double maxVal, double val) {
    double v = (val - minVal) / (maxVal - minVal);
    return (max - min) * v + min;
  }

  static boolean BooleanRandom() {
    return BooleanRandom(0.5);
  }

  static boolean BooleanRandom(double med) {
    return Math.random() > med;
  }

  static int RangeRandom(int min, int max) {
    return (int) (Math.floor((max - min) * Math.random()) + min);
  }

  static float RangeRandom(float min, float max) {
    return (float) ((max - min) * Math.random() + min);
  }

  static double RangeRandom(double min, double max) {
    return (max - min) * Math.random() + min;
  }

  static float FloatRandom(float min, float max) {
    return (float) RangeRandom(min, max);
  }

  static Point RandomPoint(int x, int y, int w, int h) {
    int i = RangeRandom(x, x + w);
    int j = RangeRandom(y, y + h);
    return new Point(i, j);
  }

  static Color RandomColor(float mh, float Mh, float ms, float Ms, float mb, float Mb) {
    float h = (float) ((Mh - mh) * Math.random() + mh);
    float s = (float) ((Ms - ms) * Math.random() + ms);
    float b = (float) ((Mb - mb) * Math.random() + mb);
    int rgb = Color.HSBtoRGB(h, s, b);
    Color c = new Color(rgb);
    return c;
  }

  static void SetHints(Graphics g) {
    SetHints((Graphics2D) g);
  }

  static void SetHints(Graphics2D g2d) {
    g2d.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
    g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
  }

  static double RandomGauss() {
    return RandomGauss(0, 0.5);
  }
  
  static double RandomGauss(double m, double s) {    
    double x = Math.random();
    double y = Math.exp(-0.5 * Math.pow(((x - m) / s), 2.) / (s * Math.sqrt(2. * Math.PI)));
    return y;
  }

  static double RandomGauss(double variance) {
    if (hasSpare) {
      hasSpare = false;
      return Math.sqrt(variance * rand1) * Math.sin(rand2);
    }

    hasSpare = true;

    rand1 = Math.random();
    if (rand1 < 1e-10)
      rand1 = 1e-10;
    rand1 = -2. * Math.log(rand1);
    rand2 = Math.random() * TWO_PI;

    return Math.sqrt(variance * rand1) * Math.cos(rand2);
  }

  static double ExpRandom(double l) {
    double x = Math.random();
    return Math.log(x) / l;
  }
  
  static double Gauss(double m, double s) {
    double u = Math.random();
    double v = Math.random();
    double n = m + s * Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * Math.PI * v);
    return n;
  }

  static double RandomSin(double min, double max) {
    double a = RangeRandom(0, Math.PI * 2);
    double v = Math.sin(a);
    double n = Normalize(min, max, -1., 1., v);
    return n;
  }
  
  static Color DarkerColor(Color c, float factor) {
    float res[] = new float[3];
    Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), res);
    float h = res[0];
    float s = res[1];
    float b = res[2];
    b = b / factor;
    return Color.getHSBColor(h, s, b);
  }
  
  static Color DarkerColor(Color c, double factor) {
    return new Color(Math.max((int) (c.getRed() * factor), 0), Math.max((int) (c.getGreen() * factor), 0), Math.max(
        (int) (c.getBlue() * factor), 0));
  }

  static Color BrighterColor(Color c, double factor) {
    return BrighterColor(c, (float) factor);
  }

  static Color BrighterColor(Color c, float factor) {
    float res[] = new float[3];
    Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), res);
    float h = res[0];
    float s = res[1];
    float b = res[2];
    b = b * factor;
    return Color.getHSBColor(h, s, b);
  }

  static Point2D orthopoint(Point p, int x, int y, double d) {
    return orthopoint(p, new java.awt.Point(x, y), d);
  }

  static Point2D orthopoint(Point p1, Point p2, double d) {
    Point2D.Double src = new Point2D.Double();
    Point2D.Double dst = new Point2D.Double();

    double dx = p2.x - p1.x;
    double dy = p2.y - p1.y;
    double D = Math.sqrt(dx * dx + dy * dy);
    double a = Math.asin(dy / D);
    AffineTransform tr = AffineTransform.getRotateInstance(-a, p1.x, p1.y);
    src.setLocation(p2.x, p2.y);
    tr.transform(src, dst);
    if (Math.abs(dx) > Math.abs(dy)) {
      if (dx < 0)
        d = -d;
    } else {
      if (dy > 0)
        d = -d;
    }
    dst.setLocation(dst.x, dst.y + d);
    tr = AffineTransform.getRotateInstance(a, p1.x, p1.y);
    tr.transform(dst, dst);
    return dst;
  }

  static Point2D.Float p = new Point2D.Float();
  static Point2D.Float[] res = new Point2D.Float[2];

  static Point2D.Float[] orthopoints(DLPoint p1, DLPoint p2, double d) {
    return orthopoints(new Point2D.Float(p1.x, p1.y), new Point2D.Float(p2.x, p2.y), d);
  }

  static Point2D.Float[] orthopoints(Point2D.Float p1, Point2D.Float p2, double d) {
    AffineTransform tr;

    double dx = p2.x - p1.x;
    double dy = p2.y - p1.y;
    double D = Math.sqrt(dx * dx + dy * dy);
    double a = Math.asin(dy / D);
    tr = AffineTransform.getRotateInstance(a, p1.x, p1.y);

    p.setLocation(p1.x + D, p2.y - d);
    res[0] = (Point2D.Float) tr.transform(p, null);
    p.setLocation(p1.x + D, p2.y + d);
    res[1] = (Point2D.Float) tr.transform(p, null);
    return res;
  }

  static Point2D[] controlPoint(Point2D p0, Point2D p1, Point2D p2, Point2D p3, float smooth_value) {
    double x0 = p0.getX();
    double y0 = p0.getY();
    double x1 = p1.getX();
    double y1 = p1.getY();
    double x2 = p2.getX();
    double y2 = p2.getY();
    double x3 = p3.getX();
    double y3 = p3.getY();

    double xc1 = (x0 + x1) / 2.0;
    double yc1 = (y0 + y1) / 2.0;
    double xc2 = (x1 + x2) / 2.0;
    double yc2 = (y1 + y2) / 2.0;
    double xc3 = (x2 + x3) / 2.0;
    double yc3 = (y2 + y3) / 2.0;

    double len1 = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
    double len2 = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    double len3 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));

    double k1 = len1 / (len1 + len2);
    double k2 = len2 / (len2 + len3);

    double xm1 = xc1 + (xc2 - xc1) * k1;
    double ym1 = yc1 + (yc2 - yc1) * k1;

    double xm2 = xc2 + (xc3 - xc2) * k2;
    double ym2 = yc2 + (yc3 - yc2) * k2;

    // Resulting control points. Here smooth_value is mentioned
    // above coefficient K whose value should be in range [0...1].

    double ctrl1_x = xm1 + (xc2 - xm1) * smooth_value + x1 - xm1;
    double ctrl1_y = ym1 + (yc2 - ym1) * smooth_value + y1 - ym1;

    double ctrl2_x = xm2 + (xc2 - xm2) * smooth_value + x2 - xm2;
    double ctrl2_y = ym2 + (yc2 - ym2) * smooth_value + y2 - ym2;
    Point2D[] ret = new Point2D[2];
    ret[0] = new Point2D.Double(ctrl1_x, ctrl1_y);
    ret[1] = new Point2D.Double(ctrl2_x, ctrl2_y);
    return ret;
  }

  // Compute the dot product AB ⋅ BC
  static double dot(Point2D.Float A, Point2D.Float B, Point2D.Float C) {
    Point2D.Float AB = new Point2D.Float();
    Point2D.Float BC = new Point2D.Float();
    AB.x = B.x - A.x;
    AB.y = B.y - A.y;
    BC.x = C.x - B.x;
    BC.y = C.y - B.y;
    double dot = AB.x * BC.x + AB.y * BC.y;
    return dot;
  }

  // Compute the cross product AB x AC
  double cross(Point2D.Float A, Point2D.Float B, Point2D.Float C) {
    Point2D.Float AB = new Point2D.Float();
    Point2D.Float AC = new Point2D.Float();
    AB.x = B.x - A.x;
    AB.y = B.y - A.y;
    AC.x = C.x - A.x;
    AC.y = C.y - A.y;
    double cross = AB.x * AC.y - AB.y * AC.x;
    return cross;
  }

  // Compute the distance from A to B
  double distance(Point2D.Float A, Point2D.Float B) {
    double d1 = A.x - B.x;
    double d2 = A.y - B.y;
    return Math.sqrt(d1 * d1 + d2 * d2);
  }

  // Compute the distance from AB to C
  // if isSegment is true, AB is a segment, not a line.
  double linePointDist(Point2D.Float A, Point2D.Float B, Point2D.Float C, boolean isSegment) {
    double dist = cross(A, B, C) / distance(A, B);
    if (isSegment) {
      double dot1 = dot(A, B, C);
      if (dot1 > 0)
        return distance(B, C);
      double dot2 = dot(B, A, C);
      if (dot2 > 0)
        return distance(A, C);
    }
    return Math.abs(dist);
  }

  /**
   * Reduce the number of points in a shape using the Douglas-Peucker algorithm
   * 
   * @param shape
   *          The shape to reduce
   * @param tolerance
   *          The tolerance to decide whether or not to keep a point, in the
   *          coordinate system of the points (micro-degrees here)
   * @return the reduced shape
   */
  public static ArrayList<Point2D.Float> reduceWithTolerance(ArrayList<Point2D.Float> shape, double tolerance) {
    int n = shape.size();
    // if a shape has 2 or less points it cannot be reduced
    if (tolerance <= 0 || n < 3) {
      return shape;
    }

    boolean[] marked = new boolean[n]; // vertex indexes to keep will be marked
                                       // as "true"
    for (int i = 1; i < n - 1; i++)
      marked[i] = false;
    // automatically add the first and last point to the returned shape
    marked[0] = marked[n - 1] = true;

    // the first and last points in the original shape are
    // used as the entry point to the algorithm.
    douglasPeuckerReduction(shape, // original shape
        marked, // reduced shape
        tolerance, // tolerance
        0, // index of first point
        n - 1 // index of last point
    );

    // all done, return the reduced shape
    ArrayList<Point2D.Float> newShape = new ArrayList<Point2D.Float>(n);
    for (int i = 0; i < n; i++) {
      if (marked[i])
        newShape.add(shape.get(i));
    }
    return newShape;
  }

  /**
   * Reduce the points in shape between the specified first and last index. Mark
   * the points to keep in marked[]
   * 
   * @param shape
   *          The original shape
   * @param marked
   *          The points to keep (marked as true)
   * @param tolerance
   *          The tolerance to determine if a point is kept
   * @param firstIdx
   *          The index in original shape's point of the starting point for this
   *          line segment
   * @param lastIdx
   *          The index in original shape's point of the ending point for this
   *          line segment
   */
  static void douglasPeuckerReduction(ArrayList<Point2D.Float> shape, boolean[] marked, double tolerance, int firstIdx,
      int lastIdx) {
    if (lastIdx <= firstIdx + 1) {
      // overlapping indexes, just return
      return;
    }

    // loop over the points between the first and last points
    // and find the point that is the farthest away

    double maxDistance = 0.0;
    int indexFarthest = 0;

    Point2D.Float firstPoint = shape.get(firstIdx);
    Point2D.Float lastPoint = shape.get(lastIdx);

    for (int idx = firstIdx + 1; idx < lastIdx; idx++) {
      Point.Float point = shape.get(idx);

      double distance = orthogonalDistance(point, firstPoint, lastPoint);

      // keep the point with the greatest distance
      if (distance > maxDistance) {
        maxDistance = distance;
        indexFarthest = idx;
      }
    }

    if (maxDistance > tolerance) {
      // The farthest point is outside the tolerance: it is marked and the
      // algorithm continues.
      marked[indexFarthest] = true;

      // reduce the shape between the starting point to newly found point
      douglasPeuckerReduction(shape, marked, tolerance, firstIdx, indexFarthest);

      // reduce the shape between the newly found point and the finishing point
      douglasPeuckerReduction(shape, marked, tolerance, indexFarthest, lastIdx);
    }
    // else: the farthest point is within the tolerance, the whole segment is
    // discarded.
  }

  /**
   * Calculate the orthogonal distance from the line joining the lineStart and
   * lineEnd points to point
   * 
   * @param point
   *          The point the distance is being calculated for
   * @param lineStart
   *          The point that starts the line
   * @param lineEnd
   *          The point that ends the line
   * @return The distance in points coordinate system
   */
  public static double orthogonalDistance(Point2D.Float point, Point2D.Float lineStart, Point2D.Float lineEnd) {
    double area = Math.abs((lineStart.y * lineEnd.x + lineEnd.y * point.x + point.y * lineStart.x - lineEnd.y
        * lineStart.x - point.y * lineEnd.x - lineStart.y * point.x) / 2.0);

    double bottom = Math.hypot(lineStart.y - lineEnd.y, lineStart.x - lineEnd.x);

    return (area / bottom * 2.0);
  }

  static Rectangle2D PolylineBounds2D(ArrayList<DLPoint> pts, float margin) {
    float xMin = Float.MAX_VALUE;
    float yMin = Float.MAX_VALUE;
    float xMax = Float.MIN_VALUE;
    float yMax = Float.MIN_VALUE;
    Iterator<DLPoint> i = pts.iterator();
    while (i.hasNext()) {
      DLPoint p = i.next();
      if (p.x > xMax)
        xMax = p.x;
      if (p.y > yMax)
        yMax = p.y;
      if (p.x < xMin)
        xMin = p.x;
      if (p.y < yMin)
        yMin = p.y;
    }
    return new Rectangle2D.Float(xMin - margin, yMin - margin, xMax - xMin + 2 * margin, yMax - yMin + 2 * margin);
  }

  static Rectangle PolylineBounds(ArrayList<DLPoint> pts, float margin) {
      
    float xMin = Float.MAX_VALUE;
    float yMin = Float.MAX_VALUE;
    float xMax = Float.MIN_VALUE;
    float yMax = Float.MIN_VALUE;
    Iterator<DLPoint> i = pts.iterator();
    while (i.hasNext()) {
      DLPoint p = i.next();
      if (p.x > xMax)
        xMax = p.x;
      if (p.y > yMax)
        yMax = p.y;
      if (p.x < xMin)
        xMin = p.x;
      if (p.y < yMin)
        yMin = p.y;
    }
    return new Rectangle((int) Math.floor(xMin - margin), (int) Math.floor(yMin - margin),
        (int) Math.ceil(xMax - xMin + 2 * margin), (int) Math.ceil(yMax - yMin + 2 * margin));
  }

  static boolean contains(ArrayList<DLPoint> p, int x, int y) {
    boolean oddTransitions = false;
    int sz = p.size();
    for (int i = 0, j = sz - 1; i < sz; j = i++) {
      if ((p.get(i).y < y && p.get(j).y >= y) || (p.get(j).y < y && p.get(i).y >= y)) {
        if (p.get(i).x + (y - p.get(i).y) / (p.get(j).y - p.get(i).y) * (p.get(j).x - p.get(i).y) < x) {
          oddTransitions = !oddTransitions;
        }
      }
    }
    return oddTransitions;
  }

  static float sqr(float x) {
    return x * x;
  }

  static float dist2(DLPoint v, DLPoint w) {
    return sqr(v.x - w.x) + sqr(v.y - w.y);
  }

  static float distToSegmentSquared(DLPoint p, DLPoint v, DLPoint w) {
    float l2 = dist2(v, w);
    if (l2 == 0)
      return dist2(p, v);
    float t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2;
    if (t < 0)
      return dist2(p, v);
    if (t > 1)
      return dist2(p, w);
    return dist2(p, new DLPoint(v.x + t * (w.x - v.x), v.y + t * (w.y - v.y)));
  }

  static float distToSegment(DLPoint p, DLPoint v, DLPoint w) {
    return (float) Math.sqrt(distToSegmentSquared(p, v, w));
  }

  static float MinDistance(ArrayList<DLPoint> pts, int x, int y) {
    return MinDistance(pts, new DLPoint(x, y));
  }

  static float MinDistance(ArrayList<DLPoint> pts, DLPoint pt) {

    Iterator<DLPoint> i = pts.iterator();
    float dist = Float.MAX_VALUE;
    DLPoint prev = null;
    while (i.hasNext()) {
      DLPoint p = i.next();
      if (prev != null) {
        float d = distToSegmentSquared(pt, prev, p);
        if (d < dist)
          dist = d;
      }
      prev = p;
    }
    return (float) Math.sqrt(dist);
  }

  static TreeMap<RGB, String> colors = null;

  static String ColorName(int rgb) {
    int r = (rgb & 0x00ff0000) >> 16;
    int g = (rgb & 0x0000ff00) >> 8;
    int b = (rgb & 0x000000ff) >> 0;
    return ColorName(r, g, b);
  }

  static String ColorName(int r, int g, int b) {
    if (colors == null) {
      colors = new TreeMap<RGB, String>();

      File file = new File("colors");

      try {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String s;
        while ((s = in.readLine()) != null) {
          String[] res = s.split(" ");
          String name = res[0];
          String rgb = res[1];
          String c[] = rgb.split(";");
          RGB ergebe = new RGB(Integer.parseInt(c[0]), Integer.parseInt(c[1]), Integer.parseInt(c[2]));
          colors.put(ergebe, name);
        }
        in.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    RGB rgb = new RGB(r, g, b);
    String name = colors.get(rgb);
    if (name == null) {
      Map.Entry<RGB, String> low = colors.floorEntry(rgb);
      Map.Entry<RGB, String> high = colors.ceilingEntry(rgb);
      int d1 = Math.abs(rgb.compareTo(low.getKey()));
      int d2 = Math.abs(rgb.compareTo(high.getKey()));
      if (d1 < d2)
        name = low.getValue();
      else
        name = high.getValue();
    }

    return null;
  }

  static public AffineTransform computeTransform(Rectangle2D rect, Rectangle2D trect, boolean keepAspect) {
    double sx = 1;
    double sy = 1;
    double tw = trect.getWidth();
    double w = rect.getWidth();
    if (tw != w)
      sx = tw / w;
    double th = trect.getHeight();
    double h = rect.getHeight();
    if (th != h)
      sy = th / h;
    if (keepAspect) {
      double s = Math.min(sx, sy);
      sx = s;
      sy = s;
    }
    AffineTransform t = new AffineTransform(sx, 0, 0, sy, trect.getX() - sx * rect.getX(), trect.getY() - sy
        * rect.getY());
    return t;
  }

  static Path2D AddPoint(double x, double y, Path2D p) {
    if (p == null)
      p = new Path2D.Float();
    if (p.getCurrentPoint() == null) {
      p.moveTo(x, y);
    } else {
      Point2D.Float p2 = (Point2D.Float) p.getCurrentPoint();
      double dx = (x - p2.x);
      double dy = (y - p2.y);
      if ((dx * dx + dy * dy) > POLY_PRECISION / 2)
        p.lineTo(x, y);
    }
    return p;
  }

  static String lorem = ReadFile("Lorem.txt");

  static String ReadFile(String name) {
    try {
      FileInputStream f = new FileInputStream(name);
      StringBuffer buff = new StringBuffer();
      byte b[] = new byte[1024];
      int n;
      while ((n = f.read(b)) > 0)
        buff.append(new String(b, 0, n));
      f.close();
      return buff.toString();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  static String RandomChar() {
    int i = RangeRandom(0, lorem.length() - 1);
    char c = lorem.charAt(i);
    while ((c != ' ') && (i > 0)) {
      i--;
      c = lorem.charAt(i);
    }
    String ret = lorem.substring(i + 1, i + 2);
    return ret;
  }

  static String RandomWord() {
    int i = RangeRandom(0, lorem.length() - 1);

    char c = lorem.charAt(i);
    while ((c != ' ') && (i > 0)) {
      i--;
      c = lorem.charAt(i);
    }

    while ((c == ' ') && (i < lorem.length())) {
      i++;
      c = lorem.charAt(i);
    }
    int start = i;

    while ((c != ' ') && (i < lorem.length())) {
      i++;
      c = lorem.charAt(i);
    }
    int end = i;
    return lorem.substring(start, end);
  }

  static GeneralPath ELetter() {
    GeneralPath s = new GeneralPath();
    s.moveTo(23.453125, -1.171875);
    s.quadTo(18.953125, 0.59375, 15.234375, 0.59375);
    s.quadTo(9.515625, 0.59375, 5.9453125, -3.2578125);
    s.quadTo(2.375, -7.109375, 2.375, -13.25);
    s.quadTo(2.375, -19.109375, 5.578125, -22.835938);
    s.quadTo(8.78125, -26.5625, 13.8125, -26.5625);
    s.quadTo(18.46875, -26.5625, 20.960938, -23.429688);
    s.quadTo(23.453125, -20.296875, 23.453125, -14.46875);
    s.lineTo(23.453125, -14.015625);
    s.lineTo(7.578125, -14.015625);
    s.quadTo(7.578125, -1.796875, 15.984375, -1.796875);
    s.quadTo(20.359375, -1.796875, 23.453125, -3.59375);
    s.lineTo(23.453125, -1.171875);
    s.closePath();
    s.moveTo(7.6875, -15.5);
    s.lineTo(18.453125, -15.5);
    s.lineTo(18.46875, -16.5625);
    s.quadTo(18.46875, -25.09375, 13.5, -25.09375);
    s.quadTo(10.8125, -25.09375, 9.25, -22.484375);
    s.quadTo(7.6875, -19.875, 7.6875, -15.5);
    s.closePath();
    return s;
  }

  void DumpGeneralPath(GeneralPath p) {
    PathIterator pi = p.getPathIterator(null);
    float[] c = new float[6];
    while (!pi.isDone()) {
      int ret = pi.currentSegment(c);
      switch (ret) {
      case SEG_MOVETO:
        System.err.println("moveTo( " + c[0] + ", " + c[1] + ");");
        break;
      case SEG_LINETO:
        System.err.println("lineTo( " + c[0] + ", " + c[1] + ");");
        break;
      case SEG_QUADTO:
        System.err.println("quadTo( " + c[0] + ", " + c[1] + ", " + c[2] + ", " + c[3] + ");");
        break;
      case SEG_CUBICTO:
        System.err.println("cubicTo( " + c[0] + ", " + c[1] + ", " + c[2] + ", " + c[3] + ", " + c[4] + ", " + c[5]
            + ");");
        break;
      case SEG_CLOSE:
        System.err.println("closePath();");
        break;
      default:
        break;
      }
      pi.next();
    }
  }

  static long GetObjectSize(Object o) {
    try {
      return ObjectSizeCalculator.sizeOf(o);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return -1;
  }
}

class RGB implements Comparable<RGB> {
  int r, g, b;
  static float min = 2f;

  RGB(int r, int g, int b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public int hashCode() {
    int c = Integer.valueOf(r).hashCode() | Integer.valueOf(g).hashCode() | Integer.valueOf(b).hashCode();
    return c;
  }

  public boolean equals(Object op) {
    RGB p = (RGB) op;
    return Math.abs(p.r - r) < min && Math.abs(p.g - g) < min && Math.abs(p.b - b) < min;
  }

  public String toString() {
    return r + " " + g + " " + b;
  }

  public int compareTo(RGB o) {
    float[] hsbvals = new float[3];
    Color.RGBtoHSB(r, g, b, hsbvals);
    float[] ohsbvals = new float[3];
    Color.RGBtoHSB(o.r, o.g, o.b, ohsbvals);

    return (int) ((11 * hsbvals[0] + 7 * hsbvals[1] + hsbvals[0]) - (11 * ohsbvals[0] + 7 * ohsbvals[1] + ohsbvals[0]));
  }

}

class PolyUtils {
  public static void GetCurveControlPoints(Point2D[] knots, Point2D[] firstControlPoints, Point2D[] secondControlPoints) {

    int n = knots.length - 1;
    if (n < 1)
      return;

    // Calculate first Bezier control points
    // Right hand side vector
    double[] rhs = new double[n];

    // Set right hand side X values
    for (int i = 1; i < n - 1; ++i)
      rhs[i] = 4 * knots[i].getX() + 2 * knots[i + 1].getX();
    rhs[0] = knots[0].getX() + 2 * knots[1].getX();
    rhs[n - 1] = 3 * knots[n - 1].getX();
    // Get first control points X-values
    double[] x = GetFirstControlPoints(rhs);

    // Set right hand side Y values
    for (int i = 1; i < n - 1; ++i)
      rhs[i] = 4 * knots[i].getY() + 2 * knots[i + 1].getY();
    rhs[0] = knots[0].getY() + 2 * knots[1].getY();
    rhs[n - 1] = 3 * knots[n - 1].getY();
    // Get first control points Y-values
    double[] y = GetFirstControlPoints(rhs);

    // Fill output arrays.
    // firstControlPoints = new Point[n];
    // secondControlPoints = new Point[n];
    for (int i = 0; i < n; ++i) {
      // First control point
      firstControlPoints[i] = new Point2D.Double(x[i], y[i]);
      // Second control point
      if (i < n - 1)
        secondControlPoints[i] = new Point2D.Double(2 * knots[i + 1].getX() - x[i + 1], 2 * knots[i + 1].getY()
            - y[i + 1]);
      else
        secondControlPoints[i] = new Point2D.Double((knots[n].getX() + x[n - 1]) / 2, (knots[n].getY() + y[n - 1]) / 2);
    }
  }

  public static void GetCurveControlPoints(ArrayList<DLPoint> knots, Point2D[] firstControlPoints,
      Point2D[] secondControlPoints) {

    int n = knots.size() - 1;
    if (n < 1)
      return;

    // Calculate first Bezier control points
    // Right hand side vector
    double[] rhs = new double[n];

    // Set right hand side X values
    for (int i = 1; i < n - 1; ++i)
      rhs[i] = 4 * knots.get(i).x + 2 * knots.get(i + 1).x;
    rhs[0] = knots.get(0).x + 2 * knots.get(1).x;
    rhs[n - 1] = 3 * knots.get(n - 1).x;
    // Get first control points X-values
    double[] x = GetFirstControlPoints(rhs);

    // Set right hand side Y values
    for (int i = 1; i < n - 1; ++i)
      rhs[i] = 4 * knots.get(i).y + 2 * knots.get(i + 1).y;
    rhs[0] = knots.get(0).y + 2 * knots.get(1).y;
    rhs[n - 1] = 3 * knots.get(n - 1).y;
    // Get first control points Y-values
    double[] y = GetFirstControlPoints(rhs);

    // Fill output arrays.
    // firstControlPoints = new Point[n];
    // secondControlPoints = new Point[n];
    for (int i = 0; i < n; ++i) {
      // First control point
      firstControlPoints[i] = new Point2D.Double(x[i], y[i]);
      // Second control point
      if (i < n - 1)
        secondControlPoints[i] = new Point2D.Double(2 * knots.get(i + 1).x - x[i + 1], 2 * knots.get(i + 1).y
            - y[i + 1]);
      else
        secondControlPoints[i] = new Point2D.Double((knots.get(n).x + x[n - 1]) / 2, (knots.get(n).y + y[n - 1]) / 2);
    }
  }

  // / <summary>
  // / Solves a tridiagonal system for one of coordinates (x or y) of first
  // Bezier control points.
  // / </summary>
  // / <param name="rhs">Right hand side vector.</param>
  // / <returns>Solution vector.</returns>
  private static double[] GetFirstControlPoints(double[] rhs) {
    int n = rhs.length;
    double[] x = new double[n]; // Solution vector.
    double[] tmp = new double[n]; // Temp workspace.

    double b = 2.0;
    x[0] = rhs[0] / b;
    for (int i = 1; i < n; i++) {// Decomposition and forward substitution.
      tmp[i] = 1 / b;
      b = (i < n - 1 ? 4.0 : 2.0) - tmp[i];
      x[i] = (rhs[i] - x[i - 1]) / b;
    }
    for (int i = 1; i < n; i++)
      x[n - i - 1] -= tmp[n - i] * x[n - i]; // Backsubstitution.
    return x;
  }

}