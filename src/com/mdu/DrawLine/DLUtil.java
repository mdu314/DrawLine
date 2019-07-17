package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.POLY_PRECISION;
import static com.mdu.DrawLine.DLParams.SAMPLE_PRECISION;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static java.awt.geom.PathIterator.SEG_CLOSE;
import static java.awt.geom.PathIterator.SEG_CUBICTO;
import static java.awt.geom.PathIterator.SEG_LINETO;
import static java.awt.geom.PathIterator.SEG_MOVETO;
import static java.awt.geom.PathIterator.SEG_QUADTO;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;

import com.jhlabs.image.CausticsFilter;
import com.jhlabs.image.PlasmaFilter;

public class DLUtil {

  static ArrayList<Class<? extends DLComponent>> curveList = new ArrayList<Class<? extends DLComponent>>(Arrays.asList(
      DLPolygon.class, DLRuban.class, DLPolyline.class, DLEgg.class, DLSpiral.class, DLHeart.class,
      DLSplineCircle.class, DLStar.class, DLArrow.class, DLFish.class, DLLemniscate.class, DLLeaf.class, DLRose.class,
      DLGear.class, DLAstroid.class, DLEpitrochoid.class, DLSuperEllipse.class, DLRosace.class, DLVonKoch.class,
      DLPapillon.class, DLTruc.class, DLTruc2.class, DLBug.class, DLSpirograph.class, DLTrefle.class, DLMoebius.class,
      DLChar.class, DLLorenz.class, DLKnot.class, DLCross.class, DLDragon.class, DLPlante.class, DLHarmonograph.class,
      DLCross.class, DLLorem.class, DLDragon.class/* , DLMap.class */));

  static ArrayList<Class<? extends DLComponent>> imageList = new ArrayList<Class<? extends DLComponent>>(Arrays.asList(
      DLPshit.class, DLNoise.class, DLTexture.class, DLFougere.class, DLMandelbrot.class, DLPlante.class,
      DLParticle.class, DLWater.class, DLParticles.class, DLLife.class, DLVoronoi.class, DLQRCode.class,
      DLDelaunay.class, DLWave.class, DLTunnel.class, DLPlasma.class, DLSub.class, DLWords.class, DLMetaball.class,
      DLKaleidoscope.class, DLFougere.class, DLKaleidoscope.class, DLQuasiCristal.class,
      DLMultiscaleTuringPatterns.class, DLGravity.class, DLRgbDaze.class, DLCircusFluid.class, DLNeuralNet.class, DL3D.class,
      DLApollonian.class, DLRotators.class));

  static ArrayList<Class<? extends DLComponent>> otherList = new ArrayList<Class<? extends DLComponent>>(Arrays.asList(
      DLMap.class, DLKandinsky.class, DLApollon.class, DLRings.class, DLPattern.class, DLFlowers.class,
      DLStarField.class, DLMagicalTree.class, DLSinCosLines.class, DLChess.class, DLKaleidoscope.class));

  private static String lorem; // ReadFile("resources/Lorem.txt");
  static final float PI = 3.1415926535897932384626433f;
  static final float TWO_PI = 6.2831853071795864769252866f;
  static final float Log3 = (float) Math.log(3);
  static final float Log2 = (float) Math.log(2);
  static final float SQRT2 = (float) Math.sqrt(2);
  static final float E = (float) Math.E;

  static DLColorModel ColorModel1 = new DLColorModel("model1", new int[] {
    0xff0000, 0x00ff00, 0x0000ff
  }, new float[] {
    1, 0.5f, 0f
  });

  static DLColorModel ColorModel2 = new DLColorModel("model2", new int[] {
    66 << 16 | 30 << 8 | 15, 25 << 16 | 7 << 8 | 26, 9 << 16 | 1 << 8 | 47, 4 << 16 | 4 << 8 | 73, 0 << 16 | 7 << 8 | 100, 12 << 16 | 44 << 8 | 138, 24 << 16 | 82 << 8 | 177, 57 << 16 | 125 << 8 | 209, 134 << 16
        | 181 << 8 | 229, 211 << 16 | 236 << 8 | 248, 241 << 16 | 233 << 8 | 191, 248 << 16 | 201 << 8 | 95, 255 << 16 | 170 << 8 | 0, 204 << 16 | 128 << 8 | 0, 153 << 16 | 87 << 8 | 0, 106 << 16 | 52 << 8 | 3
  }, new float[] {
    16f / 16f, 15f / 16f, 14f / 16f, 13f / 16f, 12f / 16f, 11f / 16f, 10f / 16f, 9f / 16f, 8f / 16f, 7f / 16f, 6f / 16f, 5f / 16f, 4f / 16f, 3f / 16f, 2f / 16f, 1f / 16f
  });

  static DLColorModel ColorModel3 = new DLColorModel("model3", new int[] {
    0x6B0000, 0xE80000, 0xCD9859, 0xFF0400, 0xFFDD90
  }, new float[] {
    1f, 3f / 4f, 2f / 4f, 1f / 4f, 0f
  });

 static DLColorModel ColorModel4 = new DLColorModel("model4",
      new int[] {
          0xff001f3f, 0xff39cccc, 0xff3d9970, 0xff2ecc40, 0xffffdc00
      },
      new float[] {
          1f, 3f / 4f, 2f / 4f, 1f / 4f, 0f
      });

 static DLColorModel[] ColorModels = {
     ColorModel1, 
     ColorModel2, 
     ColorModel3, 
     ColorModel4 
 };
 
  static long millis() {
    return System.currentTimeMillis();
  }

  static long nanos() {
    return System.nanoTime();
  }

  static int Floor(float f) {
    if (f < 0)
      return (int) (f - 0.5f);
    return (int) f;
  }

  static synchronized float Abs(float a) {
    return a < 0 ? -a : a;
  }

  static synchronized int IntAbs(float a) {
    a = a < 0 ? -a : a;
    return Floor(a);
  }

  static synchronized float Sqrt(float s) {
    return FastSqrt(s);
  }

  static synchronized float FastSqrt(float x) {
    return Float.intBitsToFloat(532483686 + (Float.floatToRawIntBits(x) >> 1));
  }

  static synchronized float SlowSqrt(float x) {
    return (float) Math.sqrt(x);
  }

  static synchronized float FastSqrt(double x) {
    float fx = (float) x;
    return Float.intBitsToFloat(532483686 + (Float.floatToRawIntBits(fx) >> 1));
  }

  static float FastLog(float x) {
    return 6 * (x - 1) / (x + 1 + 4 * FastSqrt(x));
  }

  public static float Exp(float t) {
    return (float) Math.exp(t);
  }

  public static float Pow(float a, float b) {
    return (float) Math.pow(a, b);
  }

  public static float FastPow(float a, float b) {
    return (float) FastPow((double) a, (double) b);
  }

  public static double FastPow(final double a, final double b) {
    final long tmp = Double.doubleToLongBits(a);
    final long tmp2 = (long) (b * (tmp - 4606921280493453312L)) + 4606921280493453312L;
    return Double.longBitsToDouble(tmp2);
  }

  public static int Min(int a, int b) {
    return a < b ? a : b;
  }

  public static float Min(float a, float b) {
    return a < b ? a : b;
  }

  public static float Max(float a, float b) {
    return a > b ? a : b;
  }

  private static final int ATAN2_BITS = 7;

  private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
  private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
  private static final int ATAN2_COUNT = ATAN2_MASK + 1;
  private static final int ATAN2_DIM = (int) FastSqrt(ATAN2_COUNT);

  private static final float ATAN2_DIM_MINUS_1 = (ATAN2_DIM - 1);
  private static final float[] atan2 = new float[ATAN2_COUNT];

  static {
    for (int i = 0; i < ATAN2_DIM; i++) {
      for (int j = 0; j < ATAN2_DIM; j++) {
        float x0 = (float) i / ATAN2_DIM;
        float y0 = (float) j / ATAN2_DIM;

        atan2[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
      }
    }
  }

  static float Radian(float deg) {
    return deg / 180.0f * PI;
  }

  static float Atan(float x) {
    return (float) Math.atan(x);
  }

  static float fastAtan2(double y, double x) {
    return fastAtan2((float) y, (float) x);
  }

  static float fastAtan2(float y, float x) {
    float add, mul;

    if (x < 0.0f) {
      if (y < 0.0f) {
        x = -x;
        y = -y;

        mul = 1.0f;
      } else {
        x = -x;
        mul = -1.0f;
      }

      add = -3.141592653f;
    } else {
      if (y < 0.0f) {
        y = -y;
        mul = -1.0f;
      } else {
        mul = 1.0f;
      }

      add = 0.0f;
    }

    float invDiv = ATAN2_DIM_MINUS_1 / ((x < y) ? y : x);

    int xi = (int) (x * invDiv);
    int yi = (int) (y * invDiv);

    return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
  }

  static float[] sintable = null;
  static float[] costable = null;
  static float step;
  static float invStep;
  static int size = 0;

  static {

    size = 1000;
    sintable = new float[size];
    costable = new float[size];
    step = TWO_PI / size;
    invStep = 1.0f / step;
    for (int i = 0; i < size; ++i) {
      sintable[i] = (float) Math.sin(step * i);
      costable[i] = (float) Math.cos(step * i);
    }
  }

  public static float abs(float f) {
    return f < 0 ? -f : f;
  }
  
  public static float sin(float a) {
    return (float) Math.sin(a);
  }

  public static float cos(float a) {
    return (float) Math.cos(a);
  }

  /**
   * Find a linear interpolation from the table
   * 
   * @param ang
   *          angle in radians
   * @return sin of angle a
   */
  final public static float Sin(float ang) {
    float t = ang % TWO_PI;
    float indexA = t / step;
    float indexB = indexA + 1;
    while (indexA < 0)
      indexA += size;
    indexA %= size;
    if (indexB >= size)
      return sintable[(int)indexA];
    while (indexB < 0)
      indexB += size;
    indexB %= size;

    float a = sintable[(int)indexA];
    return a + (sintable[(int)indexB] - a) * (t - (indexA * step)) * invStep;

  }

  final public static float Atan2(float y, float x) {
    return (float) Math.atan2(y, x);
  }

  /**
   * Find a linear interpolation from the table
   * 
   * @param ang
   *          angle in radians
   * @return cos of angle a
   */
  final public static float Cos(float ang) {
    float t = ang % TWO_PI;
    float indexA = (int) (t / step);
    float indexB = indexA + 1;
    while (indexA < 0)
      indexA += size;
    indexA %= size;
    if (indexB >= size)
      return costable[(int)indexA];
    while (indexB < 0)
      indexB += size;
    indexB %= size;

    float a = costable[(int)indexA];

    return a + (costable[(int)indexB] - a) * (t - (indexA * step)) * invStep;
  }

  static synchronized void shuffleArray(int[] ar) {
    Random rnd = new Random();
    for (int i = ar.length - 1; i > 0; i--) {
      int index = rnd.nextInt(i + 1);
      // Simple swap
      int a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }
  }

  static synchronized void shuffleArray(int[][] ar) {
    Random rnd = new Random();
    for (int i = ar.length - 1; i > 0; i--) {
      int index = rnd.nextInt(i + 1);
      // Simple swap
      int[] a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }
  }

  static synchronized void Log(String message) {
    System.err.println(message);
  }

  static synchronized void debug(String message) {
    int deux = 3;
    String fullClassName = Thread.currentThread().getStackTrace()[deux].getClassName();
    String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
    String methodName = Thread.currentThread().getStackTrace()[deux].getMethodName();
    int lineNumber = Thread.currentThread().getStackTrace()[deux].getLineNumber();
    Log(message + " " + className + "." + methodName + "():" + lineNumber);
  }

  static HashMap<Object, BufferedImage> imageMap = new HashMap<>();

  static BufferedImage getImage(Object value, int size) {

    BufferedImage image = imageMap.get(value);

    String s = value.toString();
    File f;
    URL url;

    if (image == null) {
      if (new File(s).exists()) {
        Log("loading " + s);
        image = DLUtil.LoadImage(s, size);
      }
      if (image == null) {
        url = DrawLine.class.getResource(s);
        if (url != null) {
          try {
            f = new File(url.toURI());
          } catch (URISyntaxException e) {
            f = new File(url.getPath());
          }
          if (f.exists()) {
            Log("loading " + f);
            image = DLUtil.LoadImage(url, size);
          }
        }
      }
      if (image == null) {
        url = DrawLine.class.getResource("images/" + s);
        if (url != null) {
          try {
            f = new File(url.toURI());
          } catch (URISyntaxException e) {
            f = new File(url.getPath());
          }
          if (f.exists()) {
            Log("loading " + f);
            image = DLUtil.LoadImage(url, size);
          }
        }
      }
      if (image == null) {
        url = DrawLine.class.getResource("images/textures/" + s);
        if (url != null) {
          try {
            f = new File(url.toURI());
          } catch (URISyntaxException e) {
            f = new File(url.getPath());
          }
          if (f.exists()) {
            Log("loading " + f);
            image = DLUtil.LoadImage(url, size);
          }
        }
      }
      imageMap.put(value, image);
    }
    return image;
  }

  static synchronized BufferedImage copy(BufferedImage src, BufferedImage dst) {
    if (dst == null)
      dst = new BufferedImage(src.getWidth(null), src.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = dst.createGraphics();
    DLUtil.SetHints(g);
    g.drawImage(src, 0, 0, null);
    g.dispose();
    return dst;
  }

  static synchronized DLPath AddPoint(Point2D pt, DLPath p) {
    return AddPoint(pt.getX(), pt.getY(), p);
  }

  static synchronized DLPath AddPoint(Point2D.Float pt, float scale, DLPath p) {
    return AddPoint(pt.x, pt.y, scale, p);
  }

  static synchronized DLPath AddPoint(float x, float y, float scale, DLPath p) {
    return AddPoint(x * scale, y * scale, p, DLParams.DRAW_PRECISION);
  }

  static synchronized DLPath AddPoint(float x, float y, DLPath p, float tol) {
    return AddPoint(x, y, p, tol, Float.MAX_VALUE);
  }

  static synchronized DLPath AddPoint(DLPoint pt, DLPath p, float tol, float cutDistance) {
    return AddPoint(pt.x, pt.y, p, tol, cutDistance);
  }

  static synchronized DLPath AddPoint(float x, float y, DLPath p, float skipDistance, float cutDistance) {
    if (p == null)
      p = new DLPath();
    Point2D.Float cur = (Point2D.Float) p.getCurrentPoint();
    if (cur == null) {
      p.moveTo(x, y);
    } else {
      float dx = x - cur.x;
      float dy = y - cur.y;
      float d = dx * dx + dy * dy;
      if (d < skipDistance)
        return p;
      if (d > cutDistance)
        p.moveTo(x, y);
      else
        p.lineTo(x, y);
    }
    return p;
  }

  static synchronized DLPath AddPoint(double x, double y, DLPath p) {
    if (p == null)
      p = new DLPath();
    if (p.getCurrentPoint() == null)
      p.moveTo(x, y);
    else {
      final Point2D.Float p2 = (Point2D.Float) p.getCurrentPoint();
      float dx = (float) x - p2.x;
      float dy = (float) y - p2.y;
      if (dx * dx + dy * dy > POLY_PRECISION / 2)
        p.lineTo(x, y);
    }
    return p;
  }

  static GeneralPath stringToShape(String s, String family, int size, int style) {

    final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);
    final Font f = new Font(family, style, size);
    final FontRenderContext frc = g.getFontMetrics(f).getFontRenderContext();
    final GlyphVector v = f.createGlyphVector(frc, s);
    final Shape shape = v.getOutline();
    if (!(shape instanceof GeneralPath))
      throw new Error("Character outline is not a GeneralPath but a " + shape);
    return (GeneralPath) shape;
  }

  static String digits = "0123456789";

  static String RandomDigits(int n) {
    String s = "";
    while (n-- > 0) {
      int i = RangeRandom(0, digits.length() - 1);
      s += digits.substring(i, i + 1);
    }
    return s;
  }

  static synchronized boolean BooleanRandom() {
    return BooleanRandom(0.5);
  }

  static synchronized boolean BooleanRandom(double med) {
    return Math.random() > med;
  }

  static float Random() {
    return (float) Math.random();
  }

  static Color GetGrey(float i) {
    return GetGrey((int) (i + 0.5f));
  }

  static Color GetGrey(int i) {
    i = 25 * i;
    return new Color(255 - i, 255 - i, 255 - i);
  }

  static int GetIntGrey(int i) {
    int value = ((0xFF) << 24) | ((i & 0xFF) << 16) | ((i & 0xFF) << 8) | ((i & 0xFF) << 0);
    return value;
  }

  static synchronized Color BrighterColor(Paint p, double factor) {
    if (!(p instanceof Color))
      return null;
    return BrighterColor((Color) p, factor);
  }

  static synchronized Color BrighterColor(Color c, double factor) {
    return BrighterColor(c, (float) factor);
  }

  static synchronized Color BrighterColor(Color c, float factor) {
    float res[] = new float[3];
    Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), res);
    float h = res[0];
    float s = res[1];
    float b = res[2];
    b = b * factor;
    return Color.getHSBColor(h, s, b);
  }

  static public AffineTransform computeTransform2(Rectangle2D r, Rectangle2D tr, boolean keepAspect) {
    double rxc = r.getX() + r.getWidth() / 2.;
    double ryc = r.getY() + r.getHeight() / 2.;

    double trxc = tr.getX() + tr.getWidth() / 2.;
    double tryc = tr.getY() + tr.getHeight() / 2.;

    double tx = trxc - rxc;
    double ty = tryc - ryc;

    double sx = tr.getWidth() / r.getWidth();
    double sy = tr.getHeight() / r.getHeight();

    AffineTransform at = new AffineTransform();
    at.translate(tx, ty);
    at.scale(sx, sy);

    return at;
  }

  static synchronized public AffineTransform computeTransform(Rectangle2D r, Rectangle2D tr, boolean keepAspect) {
    return _computeTransform((float) tr.getX(), (float) r.getX(), (float) tr.getY(), (float) r.getY(),
        (float) tr.getWidth(), (float) r.getWidth(), (float) tr.getHeight(), (float) r.getHeight(), keepAspect);
  }

  static synchronized public AffineTransform computeTransform(Rectangle2D.Float r, Rectangle2D.Float tr,
      boolean keepAspect) {
    return _computeTransform(tr.x, r.x, tr.y, r.y, tr.width, r.width, tr.height, r.height, keepAspect);
  }

  static synchronized public AffineTransform _computeTransform(float tx, float x, float ty, float y, float tw, float w,
      float th, float h, boolean keepAspect) {
    float sx = 1;
    float sy = 1;
    if (tw != w)
      sx = tw / w;
    if (th != h)
      sy = th / h;
    if (keepAspect) {
      float s = sx < sy ? sx : sy;
      sx = s;
      sy = s;
    }
    return new AffineTransform(sx, 0, 0, sy, tx - sx * x, ty - sy * y);
  }

  static synchronized boolean contains(ArrayList<DLPoint> p, int x, int y) {
    boolean oddTransitions = false;
    final int sz = p.size();
    for (int i = 0, j = sz - 1; i < sz; j = i++)
      if (p.get(i).y < y && p.get(j).y >= y || p.get(j).y < y && p.get(i).y >= y)
        if (p.get(i).x + (y - p.get(i).y) / (p.get(j).y - p.get(i).y) * (p.get(j).x - p.get(i).y) < x)
          oddTransitions = !oddTransitions;
    return oddTransitions;
  }

  static synchronized Point2D[] controlPoint(Point2D p0, Point2D p1, Point2D p2, Point2D p3, float smooth_value) {
    final double x0 = p0.getX();
    final double y0 = p0.getY();
    final double x1 = p1.getX();
    final double y1 = p1.getY();
    final double x2 = p2.getX();
    final double y2 = p2.getY();
    final double x3 = p3.getX();
    final double y3 = p3.getY();

    final double xc1 = (x0 + x1) / 2.0;
    final double yc1 = (y0 + y1) / 2.0;
    final double xc2 = (x1 + x2) / 2.0;
    final double yc2 = (y1 + y2) / 2.0;
    final double xc3 = (x2 + x3) / 2.0;
    final double yc3 = (y2 + y3) / 2.0;

    final double len1 = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
    final double len2 = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    final double len3 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));

    final double k1 = len1 / (len1 + len2);
    final double k2 = len2 / (len2 + len3);

    final double xm1 = xc1 + (xc2 - xc1) * k1;
    final double ym1 = yc1 + (yc2 - yc1) * k1;

    final double xm2 = xc2 + (xc3 - xc2) * k2;
    final double ym2 = yc2 + (yc3 - yc2) * k2;

    // Resulting control points. Here smooth_value is mentioned
    // above coefficient K whose value should be in range [0...1].

    final double ctrl1_x = xm1 + (xc2 - xm1) * smooth_value + x1 - xm1;
    final double ctrl1_y = ym1 + (yc2 - ym1) * smooth_value + y1 - ym1;

    final double ctrl2_x = xm2 + (xc2 - xm2) * smooth_value + x2 - xm2;
    final double ctrl2_y = ym2 + (yc2 - ym2) * smooth_value + y2 - ym2;
    final Point2D[] ret = new Point2D[2];
    ret[0] = new Point2D.Double(ctrl1_x, ctrl1_y);
    ret[1] = new Point2D.Double(ctrl2_x, ctrl2_y);
    return ret;
  }

  static Color DarkerPaint(Paint p, float factor) {
    if (!(p instanceof Color))
      return null;
    Color c = (Color) p;
    return DarkerColor(c, factor);
  }

  // static synchronized Color DarkerColor(Color c, double factor) {
  // return new Color(Math.max((int) (c.getRed() * factor), 0), Math.max((int)
  // (c.getGreen() * factor), 0), Math.max(
  // (int) (c.getBlue() * factor), 0));
  // }

  static synchronized Color DarkerColor(Color c, float factor) {
    final float res[] = new float[3];
    Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), res);
    final float h = res[0];
    final float s = res[1];
    float b = res[2];
    b = b / factor;
    if (b > 1)
      b = 1;
    if (b < 0)
      b = 0;
    return Color.getHSBColor(h, s, b);
  }

  static Color TransparentColor() {
    return new Color(0, 0, 0, 0);
  }

  static Color TransparentColor(Color c) {
    int r = c.getRed();
    int g = c.getGreen();
    int b = c.getBlue();
    return new Color(r, g, b, 0);
  }

  static Color Invert(Color c) {
    int r = 255 - c.getRed();
    int g = 255 - c.getGreen();
    int b = 255 - c.getBlue();
    return new Color(r, g, b, 255);
  }

  static Color TransparenterColor(Color c, float f) {
    int r = c.getRed();
    int g = c.getGreen();
    int b = c.getBlue();
    int a = c.getAlpha();
    a *= f;
    return new Color(r, g, b, a);
  }

  static synchronized float dist2(DLPoint v, DLPoint w) {
    float dx = v.x - w.x;
    float dy = v.y - w.y;
    return dx * dx + dy * dy;
  }

  static synchronized float distToSegment(DLPoint p, DLPoint v, DLPoint w) {
    return (float) Math.sqrt(distToSegmentSquared(p, v, w));
  }

  static synchronized float distToSegmentSquared(DLPoint p, DLPoint v, DLPoint w) {
    final float l2 = dist2(v, w);
    if (l2 == 0)
      return dist2(p, v);
    final float t = ((p.x - v.x) * (w.x - v.x) + (p.y - v.y) * (w.y - v.y)) / l2;
    if (t < 0)
      return dist2(p, v);
    if (t > 1)
      return dist2(p, w);
    return dist2(p, new DLPoint(v.x + t * (w.x - v.x), v.y + t * (w.y - v.y)));
  }

  // Compute the dot product AB ⋅ BC
  static synchronized double dot(Point2D.Float A, Point2D.Float B, Point2D.Float C) {
    final Point2D.Float AB = new Point2D.Float();
    final Point2D.Float BC = new Point2D.Float();
    AB.x = B.x - A.x;
    AB.y = B.y - A.y;
    BC.x = C.x - B.x;
    BC.y = C.y - B.y;
    final double dot = AB.x * BC.x + AB.y * BC.y;
    return dot;
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
  static synchronized void douglasPeuckerReduction(ArrayList<Point2D.Float> shape, boolean[] marked, double tolerance,
      int firstIdx, int lastIdx) {
    if (lastIdx <= firstIdx + 1)
      // overlapping indexes, just return
      return;

    // loop over the points between the first and last points
    // and find the point that is the farthest away

    double maxDistance = 0.0;
    int indexFarthest = 0;

    final Point2D.Float firstPoint = shape.get(firstIdx);
    final Point2D.Float lastPoint = shape.get(lastIdx);

    for (int idx = firstIdx + 1; idx < lastIdx; idx++) {
      final Point.Float point = shape.get(idx);

      final double distance = orthogonalDistance(point, firstPoint, lastPoint);

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

  static synchronized GeneralPath ELetter() {
    final GeneralPath s = new GeneralPath();
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

  static synchronized double ExpRandom(double l) {
    final double x = Math.random();
    return Math.log(x) / l;
  }

  static synchronized float FloatRandom(float min, float max) {
    return RangeRandom(min, max);
  }

  static synchronized Point2D.Float RangeRandom(double x1, double x2, double y1, double y2) {
    float x = (float) RangeRandom(x1, x2);
    float y = (float) RangeRandom(y1, y2);
    return new Point2D.Float(x, y);
  }

  static synchronized Point2D.Float RangeRandom(float x1, float x2, float y1, float y2) {
    float x = RangeRandom(x1, x2);
    float y = RangeRandom(y1, y2);
    return new Point2D.Float(x, y);
  }

  static synchronized String Random(String[] strings) {
    int i = RangeRandom(0, strings.length);
    return strings[i];
  }

  static synchronized Point2D.Float RangeRandom(Point2D p1, Point2D p2) {
    return RangeRandom(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  static synchronized double Gauss(double x, double m, double s) {
    final double ret = Math.exp(-Math.pow((x - m) / s, 2) / 2) / s * Math.sqrt(TWO_PI);
    return ret;
  }

  static synchronized float MinDistance(ArrayList<DLPoint> pts, DLPoint pt) {
    final Iterator<DLPoint> i = pts.iterator();
    float dist = Float.MAX_VALUE;
    DLPoint prev = null;
    while (i.hasNext()) {
      final DLPoint p = i.next();
      if (prev != null) {
        final float d = distToSegmentSquared(pt, prev, p);
        if (d < dist)
          dist = d;
      }
      prev = p;
    }
    return (float) Math.sqrt(dist);
  }

  static synchronized float MinDistance(ArrayList<DLPoint> pts, int x, int y) {
    return MinDistance(pts, new DLPoint(x, y));
  }

  private static double rand1;
  private static double rand2;
  private static boolean hasSpare = false;

  static synchronized double NextRandomGauss(double variance) {
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

  static synchronized float map(float val, float minVal, float maxVal, float min, float max) {
    final float v = (val - minVal) / (maxVal - minVal);
    return (float) ((max - min) * v + min);
  }

  static synchronized float Normalize(float v, float start, float stop) {
    return (v - start) / (stop - start);
  }

  static synchronized int Normalize(int min, int max, int minVal, int maxVal, int val) {
    final float v = ((float) val - (float) minVal) / ((float) maxVal - (float) minVal);
    return (int) (((float) max - (float) min) * v + (float) min);
  }

  static synchronized double Normalize(double min, double max, double minVal, double maxVal, double val) {
    final double v = (val - minVal) / (maxVal - minVal);
    return (max - min) * v + min;
  }

  static synchronized float Normalize(float min, float max, float minVal, float maxVal, float val) {
    float v = (val - minVal) / (maxVal - minVal);
    return (max - min) * v + min;
  }

  static synchronized float Normalize2(float min, float max, float minVal, float maxVal, float val) {
    final float v = (val - minVal) / (maxVal - minVal);
    return (max - min) * v + min;
  }

  public static synchronized double orthogonalDistance(Point2D.Float point, Point2D.Float lineStart,
      Point2D.Float lineEnd) {
    final double area = Math.abs((lineStart.y * lineEnd.x + lineEnd.y * point.x + point.y * lineStart.x - lineEnd.y
        * lineStart.x - point.y * lineEnd.x - lineStart.y * point.x) / 2.0);

    final double bottom = Math.hypot(lineStart.y - lineEnd.y, lineStart.x - lineEnd.x);

    return area / bottom * 2.0;
  }

  static synchronized Point2D orthopoint(Point p, int x, int y, double d) {
    return orthopoint(p, new java.awt.Point(x, y), d);
  }

  static synchronized Point2D.Float orthopoint(Point2D.Float p1, Point2D.Float p2, float d) {
    return orthopoint(p1.x, p1.y, p2.x, p2.y, d);
  }

  static synchronized Point2D.Float orthopoint(double x1, double y1, double x2, double y2, double d) {
    return orthopoint((float) x1, (float) y1, (float) x2, (float) y2, (float) d);
  }

  static synchronized Point2D.Float orthopoint(float x1, float y1, float x2, float y2, float d) {
    final Point2D.Float src = new Point2D.Float();
    final Point2D.Float dst = new Point2D.Float();

    float dx = x2 - x1;
    float dy = y2 - y1;
    float D = FastSqrt(dx * dx + dy * dy);
    float a = (float) Math.asin(dy / D);
    AffineTransform tr = AffineTransform.getRotateInstance(-a, x1, y1);
    src.setLocation(x2, y2);
    tr.transform(src, dst);
    if (Abs(dx) > Math.abs(dy)) {
      if (dx < 0)
        d = -d;
    } else if (dy > 0) {
      d = -d;
    }
    dst.setLocation(dst.x, dst.y + d);
    tr = AffineTransform.getRotateInstance(a, x1, y1);
    tr.transform(dst, dst);
    return dst;
  }

  static synchronized Point2D orthopoint(Point p1, Point p2, double d) {
    return orthopoint(p1.getX(), p1.getY(), p2.getX(), p2.getY(), d);
  }

  static synchronized Point2D.Float[] orthopoints(DLPoint p1, DLPoint p2, float D, float d) {
    return orthopoints(new Point2D.Float(p1.x, p1.y), new Point2D.Float(p2.x, p2.y), D, d);
  }

  static synchronized Point2D.Float[] orthopoints(Point2D.Float p1, Point2D.Float p2, float d) {
    return orthopoints(p1, p2, Float.NaN, d);
  }

  static synchronized Point2D.Float[] orthopoints(float x1, float y1, float x2, float y2, float D, float d) {
    AffineTransform tr, tri;
    Point2D.Float tmp;

    float dx = x2 - x1;
    float dy = y2 - y1;

    if (Float.isNaN(D))
      D = 0;

    // float a = (float)Math.atan(dy / dx);
    float a = fastAtan2(dy, dx);
    tr = AffineTransform.getRotateInstance(a, x1, y1);
    // int s = dx < 0 ? -1 : 1;
    int s = 1;
    Point2D.Float[] res = new Point2D.Float[2];
    try {
      tri = tr.createInverse();
      Point2D.Float p = new Point2D.Float(x2, y2);
      tmp = (Point2D.Float) tri.transform(p, null);

      p.setLocation(tmp.x - s * D, tmp.y - d);
      res[0] = (Point2D.Float) tr.transform(p, null);

      p.setLocation(tmp.x - s * D, tmp.y + d);
      res[1] = (Point2D.Float) tr.transform(p, null);
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }

    return res;
  }

  static synchronized Point2D.Float[] orthopoints(Point2D.Float p1, Point2D.Float p2, float D, float d) {
    return orthopoints(p1.x, p1.y, p2.x, p2.y, D, d);
  }

  static Rectangle2D getBounds(Shape s, float margin) {
    Rectangle2D r = s.getBounds2D();
    r.setFrame(r.getX() - margin, r.getY() - margin, r.getWidth() + 2 * margin, r.getHeight() + 2 * margin);
    return r;
  }

  static synchronized void expand(Rectangle r, float a) {
    float aa = 2f * a;
    r.setRect(r.getX() - a, r.getY() - a, r.getWidth() + aa, r.getHeight() + aa);
  }

  static synchronized void expand(Rectangle2D.Float r, float howmuch) {
    float a = howmuch;
    float aa = 2f * a;
    r.setRect(r.getX() - a, r.getY() - a, r.getWidth() + aa, r.getHeight() + aa);
  }

  static synchronized Rectangle PolylineBounds(ArrayList<DLPoint> pts, float margin) {
    float xMin = Float.MAX_VALUE;
    float yMin = Float.MAX_VALUE;
    float xMax = Float.MIN_VALUE;
    float yMax = Float.MIN_VALUE;
    final Iterator<DLPoint> i = pts.iterator();
    while (i.hasNext()) {
      final DLPoint p = i.next();
      if (p.x > xMax)
        xMax = p.x;
      if (p.y > yMax)
        yMax = p.y;
      if (p.x < xMin)
        xMin = p.x;
      if (p.y < yMin)
        yMin = p.y;
    }
    return new Rectangle((int) Math.floor(xMin - margin), (int) Math.floor(yMin - margin), (int) Math.ceil(xMax - xMin
        + 2 * margin), (int) Math.ceil(yMax - yMin + 2 * margin));
  }

  static synchronized Rectangle2D.Float PolylineBounds2D(ArrayList<DLPoint> pts, float margin) {
    float xMin = Float.MAX_VALUE;
    float yMin = Float.MAX_VALUE;
    float xMax = Float.MIN_VALUE;
    float yMax = Float.MIN_VALUE;
    final Iterator<DLPoint> i = pts.iterator();
    while (i.hasNext()) {
      final DLPoint p = i.next();
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

  static String getLorem() {
    if (lorem == null)
      lorem = ReadFile("resources/Lorem.txt");
    return lorem;
  }

  static synchronized String RandomChar() {
    String lo = getLorem();
    int i = RangeRandom(0, lo.length() - 1);
    char c = lo.charAt(i);
    while (c != ' ' && i > 0) {
      i--;
      c = lo.charAt(i);
    }
    final String ret = lo.substring(i + 1, i + 2);
    return ret;
  }

  static synchronized Color RandomColor() {
    return RandomColor(1f, 0f, 1f, 0f, 1f, 0f);
  }

  static synchronized Color RandomColor(float mh, float Mh, float ms, float Ms, float mb, float Mb) {
    final float h = (float) ((Mh - mh) * Math.random() + mh);
    final float s = (float) ((Ms - ms) * Math.random() + ms);
    final float b = (float) ((Mb - mb) * Math.random() + mb);
    final int rgb = Color.HSBtoRGB(h, s, b);
    final Color c = new Color(rgb);
    return c;
  }

  static synchronized double RandomGauss() {
    return RandomGauss(0, 0.5);
  }

  static synchronized double RandomGauss(double m, double s) {
    final float u = (float) Math.random();
    final float v = (float) Math.random();
    final double n = m + s * Math.sqrt(-2 * Math.log(u)) * Math.cos(TWO_PI * v);
    return n;
  }

  static synchronized float RandomGauss(float m, float s) {
    final float u = (float) Math.random();
    final float v = (float) Math.random();
    final float n = m + s * FastSqrt(-2 * FastLog(u)) * Cos(TWO_PI * v);
    return (float) n;
  }

  static synchronized Point RandomPoint(int x, int y, int w, int h) {
    final int i = RangeRandom(x, x + w);
    final int j = RangeRandom(y, y + h);
    return new Point(i, j);
  }

  static synchronized double RandomSin(double min, double max) {
    final double a = RangeRandom(0, TWO_PI);
    final double v = Math.sin(a);
    final double n = Normalize(min, max, -1., 1., v);
    return n;
  }

  static synchronized String RandomWord() {
    String lo = getLorem();
    int i = RangeRandom(0, lo.length() - 1);

    char c = lo.charAt(i);
    while (c != ' ' && i > 0) {
      i--;
      c = lo.charAt(i);
    }

    while (c == ' ' && i < lo.length() - 1) {
      i++;
      c = lo.charAt(i);
    }
    final int start = i;

    while (c != ' ' && i < lo.length() - 1) {
      i++;
      c = lo.charAt(i);
    }
    final int end = i;
    return lo.substring(start, end);
  }

  static synchronized double RangeRandom(double min, double max) {
    return (max - min) * Math.random() + min;
  }

  static synchronized float RandomAngle() {
    return RangeRandom(0, TWO_PI);
  }

  static synchronized float RangeRandom(float min, float max) {
    return (float) ((max - min) * Math.random() + min);
  }

  static synchronized int RangeRandom(int m) {
    return RangeRandom(-m, m);
  }

  static synchronized float RangeRandom(float m) {
    return RangeRandom(-m, m);
  }

  static synchronized int RangeRandom(int min, int max) {
    return (int) (((max - min) * Math.random()) + min);
  }

  static synchronized String ReadFile(String name) {
    try {
      final FileInputStream f = new FileInputStream(name);
      final StringBuffer buff = new StringBuffer();
      final byte b[] = new byte[1024];
      int n;
      while ((n = f.read(b)) > 0)
        buff.append(new String(b, 0, n));
      f.close();
      return buff.toString();

    } catch (final Exception e) {
      e.printStackTrace();
    }
    return null;
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
  public static synchronized ArrayList<Point2D.Float> reduceWithTolerance(ArrayList<Point2D.Float> shape,
      double tolerance) {
    final int n = shape.size();
    // if a shape has 2 or less points it cannot be reduced
    if (tolerance <= 0 || n < 3)
      return shape;

    final boolean[] marked = new boolean[n]; // vertex indexes to keep will be
                                             // marked
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
    final ArrayList<Point2D.Float> newShape = new ArrayList<Point2D.Float>(n);
    for (int i = 0; i < n; i++)
      if (marked[i])
        newShape.add(shape.get(i));
    return newShape;
  }

  static synchronized DLPoint Rotate(DLPoint p, double a) {
    return Rotate(p.x, p.y, a);
  }

  static synchronized DLPoint Rotate(DLPoint src, double a, DLPoint p) {
    return Rotate(src.x, src.y, a, p);
  }

  static synchronized DLPoint Rotate(double px, double py, double a) {
    return Rotate(px, py, a, null);
  }

  static synchronized DLPoint Rotate(double px, double py, double a, DLPoint p) {
    final double sa = Math.sin(a);
    final double ca = Math.cos(a);
    final double x = px * ca - py * sa;
    final double y = px * sa + py * ca;
    if (p == null) {
      p = new DLPoint(x, y);
    } else {
      p.x = (float) x;
      p.y = (float) y;
    }
    return p;
  }

  static synchronized void SetDefaults(Graphics g) {
    SetDefaults((Graphics2D) g);
  }

  static Stroke defaultStroke = new BasicStroke(0);

  static synchronized void SetDefaults(Graphics2D g) {
    g.setStroke(defaultStroke);
  }

  static synchronized void SetHints(Graphics g) {
    SetHints((Graphics2D) g);
  }

  static boolean DraftMode = false;

  static synchronized void setIdentity(Graphics2D g) {
    AffineTransform af = g.getTransform();
    af.setToIdentity();
    g.setTransform(af);
  }

  static synchronized void SetHints(Graphics2D g) {
    if (g == null)
      return;
    if (DraftMode) {
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
      g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
      g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
    } else {
      g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
      g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
      g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    }
  }

  public static synchronized BufferedImage ToBufferedImage(Image img) {
    if (img instanceof BufferedImage)
      return (BufferedImage) img;

    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    final Graphics2D g = bimage.createGraphics();
    DLUtil.SetHints(g);
    g.drawImage(img, 0, 0, null);
    g.dispose();

    return bimage;
  }

  static float EuclideanDist(float x1, float y1, float x2, float y2) {
    float dx = x2 - x1;
    float dy = y2 - y1;
    return FastSqrt(dx * dx + dy * dy);
  }

  static float SquareDist(float x1, float y1, float x2, float y2) {
    float dx = x2 - x1;
    float dy = y2 - y1;
    return (dx * dx + dy * dy);
  }

  static float Tchebychev(float x1, float y1, float x2, float y2) {
    float dx = x2 - x1;
    if (dx < 0)
      dx = -dx;
    float dy = y2 - y1;
    if (dy < 0)
      dy = -dy;
    return dx > dy ? dx : dy;
  }

  static float Cubic(float x1, float y1, float x2, float y2) {
    float dx = x2 - x1;
    if (dx < 0)
      dx = -dx;
    float dy = y2 - y1;
    if (dy < 0)
      dy = -dy;
    return dx * dx * dx + dy * dy * dy;
  }

  static float Minkowski(float x1, float y1, float x2, float y2, float p) {
    float dx = x2 - x1;
    if (dx < 0)
      dx = -dx;
    float dy = y2 - y1;
    if (dy < 0)
      dy = -dy;
    double a = Math.pow(dx, p);
    double b = Math.pow(dy, p);
    double ret = (float) Math.pow(a + b, 1. / p);
    return (float) ret;
  }

  static float AlienDist1(float x1, float y1, float x2, float y2) {
    float dx = x2 - x1;
    if (dx < 0)
      dx = -dx;
    float dy = y2 - y1;
    if (dy < 0)
      dy = -dy;
    return dx * dx * dy + dy * dy * dx;
  }

  static float AlienDist2(float x1, float y1, float x2, float y2) {
    float dx = x2 - x1;
    if (dx < 0)
      dx = -dx;
    float dy = y2 - y1;
    if (dy < 0)
      dy = -dy;
    return dx * dy * dy + dy * dx * dx;
  }

  static float ManhattanDist(float x1, float y1, float x2, float y2) {
    float dx = x2 - x1;
    if (dx < 0)
      dx = -dx;
    float dy = y2 - y1;
    if (dy < 0)
      dy = -dy;
    return dx + dy;
  }

  // Compute the cross product AB x AC
  static double cross(Point2D.Float A, Point2D.Float B, Point2D.Float C) {
    final Point2D.Float AB = new Point2D.Float();
    final Point2D.Float AC = new Point2D.Float();
    AB.x = B.x - A.x;
    AB.y = B.y - A.y;
    AC.x = C.x - A.x;
    AC.y = C.y - A.y;
    final double cross = AB.x * AC.y - AB.y * AC.x;
    return cross;
  }

  // Compute the distance from A to B
  static double distance(Point2D.Float A, Point2D.Float B) {
    final double d1 = A.x - B.x;
    final double d2 = A.y - B.y;
    return Math.sqrt(d1 * d1 + d2 * d2);
  }

  static String DumpGeneralPath(GeneralPath p) {
    return DumpGeneralPath(p, null);
  }
  
  static String DumpGeneralPath(Shape p) {
    if(p instanceof GeneralPath)
      return DumpGeneralPath((GeneralPath)p, null);
    return "Not a generalPath but a " + p.getClass();
  }
  
  static String DumpGeneralPath(GeneralPath p, AffineTransform af) {
    final PathIterator pi = p.getPathIterator(af);
    final float[] c = new float[6];
    StringBuffer sb = new StringBuffer();
    System.err.print("Dump GeneralPath ");
    while (!pi.isDone()) {
      final int ret = pi.currentSegment(c);
      switch (ret) {
      case SEG_MOVETO:
        sb.append("moveTo( " + c[0] + ", " + c[1] + ");");
        break;
      case SEG_LINETO:
        sb.append("lineTo( " + c[0] + ", " + c[1] + ");");
        break;
      case SEG_QUADTO:
        sb.append("quadTo( " + c[0] + ", " + c[1] + ", " + c[2] + ", " + c[3] + ");");
        break;
      case SEG_CUBICTO:
        sb.append("cubicTo( " + c[0] + ", " + c[1] + ", " + c[2] + ", " + c[3] + ", " + c[4] + ", " + c[5] + ");");
        break;
      case SEG_CLOSE:
        sb.append("closePath();");
        break;
      default:
        throw new RuntimeException("Illegal switch " + ret);
//        break;
      }
      pi.next();
    }
    System.err.println("Done");
    return sb.toString();
  }

  static Rectangle2D GeneralPathBB(GeneralPath p) {
    final PathIterator pi = p.getPathIterator(null);
    final float[] c = new float[6];
    float xmin = Float.MAX_VALUE;
    float ymin = Float.MAX_VALUE;
    float xmax = Float.MIN_VALUE;
    float ymax = Float.MIN_VALUE;
    while (!pi.isDone()) {
      final int ret = pi.currentSegment(c);
      switch (ret) {
        case SEG_MOVETO:
          if (c[0] < xmin)
            xmin = c[0];
          if (c[1] < ymin)
            ymin = c[1];
          if (c[0] > xmax)
            xmax = c[0];
          if (c[1] > ymax)
            ymax = c[1];
          // System.err.println("moveTo( " + c[0] + ", " + c[1] + ");");
          break;
        case SEG_LINETO:
          if (c[0] < xmin)
            xmin = c[0];
          if (c[1] < ymin)
            ymin = c[1];
          if (c[0] > xmax)
            xmax = c[0];
          if (c[1] > ymax)
            ymax = c[1];
          // System.err.println("lineTo( " + c[0] + ", " + c[1] + ");");
          break;
        case SEG_QUADTO:
          if (c[0] < xmin)
            xmin = c[0];
          if (c[1] < ymin)
            ymin = c[1];
          if (c[0] > xmax)
            xmax = c[0];
          if (c[1] > ymax)
            ymax = c[1];
          // System.err.println("quadTo( " + c[0] + ", " + c[1] + ", " + c[2] + ", " +
          // c[3] + ");");
          break;
        case SEG_CUBICTO:
          // System.err.println("cubicTo( " + c[0] + ", " + c[1] + ", " + c[2] + ", " +
          // c[3] + ", " + c[4] + ", " + c[5] + ");");
          break;
        case SEG_CLOSE:
          System.err.println("closePath();");
          break;
        default:
          break;
      }
      pi.next();
    }
    return new Rectangle2D.Float(xmin, ymin, xmax - xmin, ymax - ymin);
  }
  
  
  // Compute the distance from AB to C
  // if isSegment is true, AB is a segment, not a line.
  static double linePointDist(Point2D.Float A, Point2D.Float B, Point2D.Float C, boolean isSegment) {
    final double dist = cross(A, B, C) / distance(A, B);
    if (isSegment) {
      final double dot1 = dot(A, B, C);
      if (dot1 > 0)
        return distance(B, C);
      final double dot2 = dot(B, A, C);
      if (dot2 > 0)
        return distance(A, C);
    }
    return Math.abs(dist);
  }

  static synchronized int[][] ShuffleCoords(int iwidth, int iheight) {

    int[] xa = new int[iwidth];
    int[] ya = new int[iheight];
    for (int i = 0; i < xa.length; i++)
      xa[i] = i;
    for (int i = 0; i < ya.length; i++)
      ya[i] = i;

    DLUtil.shuffleArray(xa);
    DLUtil.shuffleArray(ya);
    int[][] al = new int[iwidth * iheight][];

    int l = 0;
    for (int i = 0; i < iwidth; i++) {
      for (int j = 0; j < iheight; j++) {
        al[l++] = new int[] {
          xa[i], ya[j]
        };
      }
    }
    DLUtil.shuffleArray(al);
    return al;
  }

  static Shape fitShapeIn(Shape s, float x, float y, float w, float h, boolean kar) {
    Rectangle2D r = s.getBounds2D();
    AffineTransform tr = _computeTransform(x, (float) r.getX(),
        y, (float) r.getY(),
        w, (float) r.getWidth(),
        h, (float) r.getHeight(), kar);
    s = tr.createTransformedShape(s);
    return s;
  }

  static Shape Heart(float x, float y, float w, float h, boolean smooth, float angle) {
    DLPath p = Heart(smooth, 1);
    Rectangle2D.Float b = (Rectangle2D.Float) p.getBounds2D();
    Rectangle2D.Float tb = new Rectangle2D.Float(x, y, w, h);

    Shape s;
    AffineTransform tr;

    if (angle != 0) {
      tr = AffineTransform.getRotateInstance(angle);
      s = tr.createTransformedShape(p);
    } else {
      s = p;
    }
    tr = computeTransform(b, tb, false);
    s = tr.createTransformedShape(s);
    return s;
  }

  static Shape Heart(float x, float y, float w, float h, boolean smooth) {
    return Heart(x, y, w, h, smooth, RangeRandom(-PI / 8, PI / 8));
  }

  static DLPath Star(float cx, float cy, float r1, float r2, int branches) {
    float a = RandomAngle();
    return Star(cx, cy, r1, r2, branches, a);
  }

  static DLPath Star(float cx, float cy, float r1, float r2, int branches, float startAngle) {

    final float di = TWO_PI / branches;
    final float start = PI + startAngle;
    final float end = start + TWO_PI;
    DLPath p = null;
    for (float i = start; i < end; i += di) {
      final float c1 = Cos(i);
      final float s1 = Sin(i);
      final float i2 = i + di / 2f;
      final float c2 = Cos(i2);
      final float s2 = Sin(i2);

      float sx = s1 * r1;
      float sy = c1 * r1;
      p = DLUtil.AddPoint(cx + sx, cy + sy, p);

      sx = s2 * r2;
      sy = c2 * r2;
      p = DLUtil.AddPoint(cx + sx, cy + sy, p);
    }
    p.closePath();
    return p;
  }

  static DLPath Polygon(Point2D.Float p, int sides, float radius, float startAngle) {
    return Polygon(p.x, p.y, sides, radius, startAngle);
  }

  static DLPath Polygon(float cx, float cy, int sides, float radius) {
    return Polygon(cx, cy, sides, radius, DLUtil.RandomAngle());
  }

  static DLPath Polygon(float cx, float cy, int sides, float radius, float startAngle) {
    DLPath path = null;
    float a = TWO_PI / sides;
    for (int i = 0; i < sides; i++) {
      float ia = i * a + startAngle;
      float px = cx + radius * Cos(ia);
      float py = cy + radius * Sin(ia);
      path = DLUtil.AddPoint(px, py, path);
    }
    path.closePath();
    return path;
  }

  static Arc2D.Float Arc(float x, float y, float w, float h, float start, float extent) {
    return new Arc2D.Float(x - w / 2f, y - h / 2f, w, h, start, extent, Arc2D.OPEN);
  }

  static Line2D.Float Line(float x1, float y1, float x2, float y2) {
    return new Line2D.Float(x1, y1, x2, y2);
  }

  static Rectangle2D.Float Square(float cx, float cy, float s) {
    return new Rectangle2D.Float(cx - s / 2, cy - s / 2, s, s);
  }

  static Rectangle2D.Float Rectangle(float cx, float cy, float w, float h) {
    return new Rectangle2D.Float(cx - w / 2, cy - h / 2, w, h);
  }

  static Rectangle2D.Float Rectangle(Point2D.Float p, float s) {
    return Square(p.x, p.y, s);
  }

  static Ellipse2D.Float Circle(Point2D.Float p, float s) {
    return Circle(p.x, p.y, s);
  }

  static Ellipse2D.Float Circle(float x, float y, float s) {
    return new Ellipse2D.Float(x - s / 2, y - s / 2, s, s);
  }

  static Ellipse2D.Float Ellipse(float x, float y, float s1, float s2) {
    return new Ellipse2D.Float(x - s1 / 2, y - s2 / 2, s1, s2);
  }

  static int intConversion = 3;

  public static int Int(float f) {
    switch (intConversion) {
    case 0:
      return (int) f;
    case 1:
      return (int) Math.floor(f);
    case 2:
      return Floor(f);
    default:
      return (int) (f < 0 ? f - 0.5f : f + 0.5f);
    }
  }

  static Rectangle2D GetBounds(Font font, String u) { 
    Image image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics graphics = image.getGraphics();
    FontMetrics metrics = graphics.getFontMetrics(font);
    int hgt = metrics.getHeight();
    float h = hgt;
    int adv = metrics.stringWidth(u);
    float w = adv;
    return new Rectangle2D.Float(0, 0, w, h);
  }
  
  static Shape Char(int s, String family, int style, int size) {
    return Char("" + s, family, style, size);
  } 
  
  static Shape Char(int s, Font font) {
    return Char("" + s, font.getFamily(), font.getStyle(), font.getSize2D());
  }

  static Shape Char(String s, Font font) {
    return Char(s, font.getFamily(), font.getStyle(), font.getSize2D());
  }

  static Shape Char(String s, String family, int style, float size) {
    final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    final Font font = new Font(family, style, (int) (size + 0.5f)).deriveFont(size);
    g.setFont(font);
    final FontRenderContext frc = g.getFontMetrics(font).getFontRenderContext();
    final GlyphVector v = font.createGlyphVector(frc, s);
    final Shape shape = v.getOutline();
    return shape;
  }

  static Shape[] Target(float x, float y, float r) {
    float m = 3;
    Shape s1 = new Ellipse2D.Float(x - r, y - r, 2 * r, 2 * r);
    Shape l1 = new Line2D.Float(x - r - m, y, x - r / 2f + m, y);
    Shape l2 = new Line2D.Float(x, y - r - m, x, y - r / 2f + m);
    Shape l3 = new Line2D.Float(x + r + m, y, x + r / 2f - m, y);
    Shape l4 = new Line2D.Float(x, y + r + m, x, y + r / 2f - m);
    s1.getPathIterator(null);
    r = r / 2;
    Shape s2 = new Ellipse2D.Float(x - r, y - r, 2 * r, 2 * r);
    return new Shape[] {
      s1, s2, l1, l2, l3, l4
    };
  }

  static DLPath toPath(DLPointList points) {
    DLPath p = null;
    for (int i = 0; i < points.size(); i++)
      p = DLUtil.AddPoint(points.get(i), p);
    return p;
  }

  static DLPath toSpline(DLPointList points) {

    final int sz = points.size();
    final DLPath p = new DLPath();
    if (sz > 0) {
      Point2D.Float[] fcp = new Point2D.Float[sz - 1];
      Point2D.Float[] scp = new Point2D.Float[sz - 1];
      PolyUtils.GetCurveControlPoints(points, fcp, scp);

      p.moveTo(points.get(0).x, points.get(0).y);

      for (int i = 0; i < sz - 1; i++) {
        final Point2D cp1 = fcp[i];
        final Point2D cp2 = scp[i];
        final DLPoint pt = points.get(i + 1);
        p.curveTo(cp1.getX(), cp1.getY(), cp2.getX(), cp2.getY(), pt.x, pt.y);
      }
    }
    return p;
  }

  static DLPath Heart(boolean smooth) {
    return Heart(smooth, 1);
  }

  static DLPath Heart(boolean smooth, float scale) {

    DLPointList points = new DLPointList();
    for (float t = 0; t < TWO_PI; t += SAMPLE_PRECISION) {
      float sint = Sin(t);
      float cost = Cos(t);
      float x = scale * 16 * sint * sint * sint;
      float y = -scale * (13 * cost - 5 * Cos(2 * t) - 2 * Cos(3 * t) - Cos(4 * t));
      points.add(new DLPoint(x, y));
    }

    DLPath p = null;
    if (smooth)
      p = toSpline(points);
    else {
      p = toPath(points);
    }
    p.closePath();
    return p;
  }

  static BufferedImage LoadImage(String imageResource, int size) {
    return LoadImage(DLUtil.class.getResource(imageResource), size);
  }

  static BufferedImage LoadImage(URL imageResource, int size) {
    BufferedImage img = null;

    if (imageResource != null) {
      try {
        img = ImageIO.read(imageResource);
        if (img == null)
          return null;
        float r = (float) img.getWidth() / (float) img.getHeight();
        int w = (int) (size * r + 0.5f);
        img = GetScaledInstance(img, w, size);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
      PlasmaFilter pf = new PlasmaFilter();
      img = pf.filter(img, img);
    }
    return img;
  }

  static BufferedImage LoadImage(URL imageResource, Dimension size) {
    BufferedImage img = null;

    if (imageResource != null) {
      try {
        img = ImageIO.read(imageResource);
        if (size != null)
          img = GetScaledInstance(img, size);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      if (size != null) {
        img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        CausticsFilter cf = new CausticsFilter();
        img = cf.filter(img, img);
      }
    }
    return img;
  }

  static BufferedImage LoadImage(String imageResource, Dimension size) {
    if (imageResource == null)
      return null;
    return LoadImage(DLUtil.class.getResource(imageResource), size);
  }

  static BufferedImage GetScaledInstance(BufferedImage image, Dimension size) {
    if (image == null)
      return null;
    return GetScaledInstance(image, size.width, size.height);
  }

  static int IntColor(int r, int g, int b) {
    return 0xff << 24 | r << 16 | g << 8 | b;
  }

  static int IntColor(float c) {
    return IntColor(c, c, c);
  }

  static int IntColor(float r, float g, float b) {
    int col = 0xff << 24 | (int) r << 16 | (int) g << 8 | (int) b;
    return col;
  }

  static BufferedImage GetScaledInstance(BufferedImage image, int width, int height) {
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = bi.createGraphics();
    double sx = (double) width / image.getWidth();
    double sy = (double) height / image.getHeight();
    AffineTransform at = AffineTransform.getScaleInstance(sx, sy);
    SetHints(g);
    g.drawImage(image, at, null);
    return bi;
  }

  static BufferedImage Merge(BufferedImage i1, BufferedImage i2, float r, BufferedImage result) {

    if (i1 == null && i2 == null) {
      return null;
    }
    if (i1 != null && i2 == null) {
      return i1;
    }
    if (i2 != null && i1 == null) {
      return i2;
    }

    int iwidth = i1.getWidth();
    int iheight = i1.getHeight();
    if (i1.getWidth() != i2.getWidth() || i1.getHeight() != i2.getHeight()) {
      Log("images should have same size " + i1.getWidth() + " X " + i1.getHeight() + " != "
          + i2.getWidth() + " X " + i2.getHeight());
      i1 = GetScaledInstance(i1, iwidth, iheight);
      i2 = GetScaledInstance(i2, iwidth, iheight);
    }
    int[] rgb1 = i1.getRGB(0, 0, iwidth, iheight, null, 0, iwidth);
    int[] rgb2 = i2.getRGB(0, 0, iwidth, iheight, null, 0, iwidth);
    int[] rgb = new int[iwidth * iheight];
    for (int i = 0; i < iwidth * iheight; i++) {
      int rgb1i = rgb1[i];
      int rgb2i = rgb2[i];

      int red1 = (rgb1i >> 16) & 0xff;
      int green1 = (rgb1i >> 8) & 0xff;
      int blue1 = (rgb1i >> 0) & 0xff;

      int red2 = (rgb2i >> 16) & 0xff;
      int green2 = (rgb2i >> 8) & 0xff;
      int blue2 = (rgb2i >> 0) & 0xff;

      int red = (int) (((1f - r) * red1 + r * red2) + 0.5f);
      int green = (int) (((1f - r) * green1 + r * green2) + 0.5f);
      int blue = (int) (((1f - r) * blue1 + r * blue2) + 0.5f);

      rgb[i] = 0xff << 24 | red << 16 | green << 8 | blue;
    }

    if (result == null)
      result = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    result.setRGB(0, 0, iwidth, iheight, rgb, 0, iwidth);
    return result;
  }

  static public void Save(RenderedImage image, File f) {
    try {
      String name = f.getName();
      if (name.endsWith(".png"))
        ImageIO.write(image, "png", f);
      else if (name.endsWith(".jpg"))
        ImageIO.write(image, "jpg", f);
      else {
        int i = name.lastIndexOf('.');
        String s = name.substring(i + 1);
        ImageIO.write(image, s, f);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class PolyUtils {
  public static synchronized void GetCurveControlPoints(ArrayList<DLPoint> knots, Point2D[] firstControlPoints,
      Point2D[] secondControlPoints) {

    final int n = knots.size() - 1;
    if (n < 1)
      return;

    // Calculate first Bezier control points
    // Right hand side vector
    final double[] rhs = new double[n];

    // Set right hand side X values
    for (int i = 1; i < n - 1; ++i)
      rhs[i] = 4 * knots.get(i).x + 2 * knots.get(i + 1).x;
    rhs[0] = knots.get(0).x + 2 * knots.get(1).x;
    rhs[n - 1] = 3 * knots.get(n - 1).x;
    // Get first control points X-values
    final double[] x = GetFirstControlPoints(rhs);

    // Set right hand side Y values
    for (int i = 1; i < n - 1; ++i)
      rhs[i] = 4 * knots.get(i).y + 2 * knots.get(i + 1).y;
    rhs[0] = knots.get(0).y + 2 * knots.get(1).y;
    rhs[n - 1] = 3 * knots.get(n - 1).y;
    // Get first control points Y-values
    final double[] y = GetFirstControlPoints(rhs);

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

  public static synchronized void GetCurveControlPoints(ArrayList<DLPoint> knots, Point2D.Float[] firstControlPoints,
      Point2D.Float[] secondControlPoints) {

    int n = knots.size() - 1;
    if (n < 1)
      return;

    // Calculate first Bezier control points
    // Right hand side vector
    float[] rhs = new float[n];

    // Set right hand side X values
    for (int i = 1; i < n - 1; ++i)
      rhs[i] = 4 * knots.get(i).x + 2 * knots.get(i + 1).x;
    rhs[0] = knots.get(0).x + 2 * knots.get(1).x;
    rhs[n - 1] = 3 * knots.get(n - 1).x;
    // Get first control points X-values
    float[] x = GetFirstControlPoints(rhs);

    // Set right hand side Y values
    for (int i = 1; i < n - 1; ++i)
      rhs[i] = 4 * knots.get(i).y + 2 * knots.get(i + 1).y;
    rhs[0] = knots.get(0).y + 2 * knots.get(1).y;
    rhs[n - 1] = 3 * knots.get(n - 1).y;
    // Get first control points Y-values
    float[] y = GetFirstControlPoints(rhs);

    // Fill output arrays.
    // firstControlPoints = new Point[n];
    // secondControlPoints = new Point[n];
    for (int i = 0; i < n; ++i) {
      // First control point
      firstControlPoints[i] = new Point2D.Float(x[i], y[i]);
      // Second control point
      if (i < n - 1)
        secondControlPoints[i] = new Point2D.Float(2 * knots.get(i + 1).x - x[i + 1],
            2 * knots.get(i + 1).y - y[i + 1]);
      else
        secondControlPoints[i] = new Point2D.Float((knots.get(n).x + x[n - 1]) / 2, (knots.get(n).y + y[n - 1]) / 2);
    }
  }

  public static synchronized void GetCurveControlPoints(Point2D[] knots, Point2D[] firstControlPoints,
      Point2D[] secondControlPoints) {

    final int n = knots.length - 1;
    if (n < 1)
      return;

    // Calculate first Bezier control points
    // Right hand side vector
    final double[] rhs = new double[n];

    // Set right hand side X values
    for (int i = 1; i < n - 1; ++i)
      rhs[i] = 4 * knots[i].getX() + 2 * knots[i + 1].getX();
    rhs[0] = knots[0].getX() + 2 * knots[1].getX();
    rhs[n - 1] = 3 * knots[n - 1].getX();
    // Get first control points X-values
    final double[] x = GetFirstControlPoints(rhs);

    // Set right hand side Y values
    for (int i = 1; i < n - 1; ++i)
      rhs[i] = 4 * knots[i].getY() + 2 * knots[i + 1].getY();
    rhs[0] = knots[0].getY() + 2 * knots[1].getY();
    rhs[n - 1] = 3 * knots[n - 1].getY();
    // Get first control points Y-values
    final double[] y = GetFirstControlPoints(rhs);

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

  // / <summary>
  // / Solves a tridiagonal system for one of coordinates (x or y) of first
  // Bezier control points.
  // / </summary>
  // / <param name="rhs">Right hand side vector.</param>
  // / <returns>Solution vector.</returns>
  private static synchronized double[] GetFirstControlPoints(double[] rhs) {
    final int n = rhs.length;
    final double[] x = new double[n]; // Solution vector.
    final double[] tmp = new double[n]; // Temp workspace.

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

  private static synchronized float[] GetFirstControlPoints(float[] rhs) {
    final int n = rhs.length;
    final float[] x = new float[n]; // Solution vector.
    final float[] tmp = new float[n]; // Temp workspace.

    float b = 2.f;
    x[0] = rhs[0] / b;
    for (int i = 1; i < n; i++) {// Decomposition and forward substitution.
      tmp[i] = 1 / b;
      b = (i < n - 1 ? 4.0f : 2.0f) - tmp[i];
      x[i] = (rhs[i] - x[i - 1]) / b;
    }
    for (int i = 1; i < n; i++)
      x[n - i - 1] -= tmp[n - i] * x[n - i]; // Backsubstitution.
    return x;
  }

}
