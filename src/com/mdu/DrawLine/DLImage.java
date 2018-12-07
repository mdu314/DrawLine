package com.mdu.DrawLine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.util.ArrayList;

import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.BumpFilter;
import com.jhlabs.image.CausticsFilter;
import com.jhlabs.image.ChromeFilter;
import com.jhlabs.image.ContourFilter;
import com.jhlabs.image.CrystallizeFilter;
import com.jhlabs.image.EdgeFilter;
import com.jhlabs.image.EmbossFilter;
import com.jhlabs.image.EqualizeFilter;
import com.jhlabs.image.FlareFilter;
import com.jhlabs.image.GlowFilter;
import com.jhlabs.image.KaleidoscopeFilter;
import com.jhlabs.image.OilFilter;

abstract class DLImage extends DLComponent implements Threaded, JPG {
  ArrayList<DLThread> threads = new ArrayList<DLThread>();
  boolean threaded = true;
  BufferedImage image = null;
  int iheight;
  int iwidth;
  boolean selectCheckTransparentPixel = false;
  Color backgroundColor;
  
  static final String NullFilter = "null";
  static final String BlurFilter = "blur";
  static final String EdgeFilter = "edge";
  static final String EmbossFilter = "emboss";
  static final String EqualizeFilter = "equalize";
  static final String KaleidoscopeFilter = "kaleidoscope";
  static final String BumpFilter = "bump";
  static final String CausticFilter = "caustic";
  static final String ChromeFilter = "chrome";
  static final String OilFilter = "oil";
  static final String FlareFilter = "flare";
  static final String ContourFilter = "contour";
  static final String GlowFilter = "glow";
  static final String CrystalizeFilter = "crystallize";
  
  int frameCount = 1;
  int threadSleep = 50;

  String filterName = NullFilter;
  BufferedImageOp filter = getFilterFromString(filterName);
  int res = 1;
  boolean clear = true;
  float filterStrength = 0f;
  
  DLImage() {
    super(0, 0);
  }

  DLImage(DLImage c) {
    super(c);
    iwidth = c.iwidth;
    iheight = c.iheight;
    threaded = c.threaded;
    reset(true);
  }

  DLImage(float x, float y) {
    super(x, y);
  }

  DLImage(float x, float y, int iw, int ih) {
    super(x, y);
    iwidth = iw;
    iheight = ih;
  }

  public void reset() {
    image = image();
  }

  void clear() {
    clearImage();
    clearShadow();
  }

  void clearImage(BufferedImage img) {
    if (backgroundColor == null) {
      final Graphics2D g = img.createGraphics();
      Composite c = g.getComposite();
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
      final Rectangle rect = new Rectangle(0, 0, iwidth, iheight);
      g.fill(rect);
      g.setComposite(c);
    } else {
      Graphics2D g = img.createGraphics();
      g.setColor(backgroundColor);
      g.fillRect(0, 0, iwidth, iheight);
    }
  }

  void clearImage() {
    if (image == null) {
      image = image();
    } else {
      clearImage(image);
    }

    if (DLParams.DEBUG) {
      final Graphics2D g = image.createGraphics();
      g.setColor(Color.darkGray);
      g.drawRect(1, 1, iwidth - 2, iheight - 2);
    }
  }

  abstract DLImage copy();

  Rectangle getBounds() {
    return getBounds(true);
  }

  Rectangle getBounds(boolean deco) {
    if (image == null)
      image = image();
    float fx = x - iwidth / 2f;
    float fy = y - iheight / 2f;
    int ifx = (int) fx; // Math.floor(fx);
    int ify = (int) fy; // Math.floor(fy);
    Rectangle bounds = new Rectangle(ifx - 1, ify - 1, iwidth + 2, iheight + 2);

    if (deco)
      bounds = addShadowBounds(bounds);

    return bounds;
  }

  @Override
  boolean hitTest(Point p) {
    if (!super.hitTest(p))
      return false;
    if (!selectCheckTransparentPixel)
      return true;
    if (image == null)
      image = image();
    final double tx = this.x - iwidth / 2.;
    final double ty = this.y - iheight / 2.;
    float px = (float) (p.x - tx + 0.5f);
    if (px < 0)
      px = 0;
    if (px >= iwidth)
      px = iwidth - 1;
    float py = (float) (p.y - ty + 0.5);
    if (py < 0)
      py = 0;
    if (py >= iheight)
      py = iheight - 1;
    final int pix = image.getRGB((int) px, (int) py);
    if ((pix & 0xff000000) == 0)
      return false;
    return true;
  }

  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  boolean mouse(MouseEvent e) {
    return false;
  }

  @Override
  public void move(float dx, float dy) {
    final AffineTransform tr = AffineTransform.getTranslateInstance(dx, dy);
    transform(tr);
  }

  void paint() {
    paint(image.createGraphics());
  }

  @Override
  public void paint(Graphics gr) {
    paint(gr, true);
  }

  @Override
  public void paint(Graphics gr, boolean deco) {
    final Graphics2D g = (Graphics2D) gr;

    if (image == null)
      image = image();

    applyFilter();
    
    if (deco)
      shadow(g);

    g.drawImage(image, (int) (x - iwidth / 2f), (int) (y - iheight / 2f), null);

    if (deco && DLParams.DEBUG) {
      final Rectangle b = getBounds();
      g.setColor(Color.darkGray);
      g.drawRect(b.x, b.y, b.width - 1, b.height - 1);
    }
  }

  @Override
  public void randomize() {
    super.randomize();
  };

  @Override
  void transform(AffineTransform tr) {
    final Point2D src = new Point2D.Float(x, y);
    final Point2D dst = tr.transform(src, null);
    x = (float) dst.getX();
    y = (float) dst.getY();
  }

  public boolean isThreaded() {
    return threaded;
  }

  public void setThreaded(boolean threaded) {
    this.threaded = threaded;
    stopAll();
    clear();
    run();
  }

  public Color getBackground() {
    return backgroundColor;
  }

  public void setBackground(Color c) {
    this.backgroundColor = c;
  }

  public void stopAll() {
    synchronized (threads) {
      DLThread[] tr = threads.toArray(new DLThread[threads.size()]);
      for (DLThread t : tr) {
        t.setStopped(true);
      }
    }
  }

  public void f() {
    if (image == null)
      image = image();
    f(image.createGraphics());
  }

  public void f(Graphics2D g) {
    f(g, null);
  }

  // public abstract void f(Graphics2D g, DLThread t);

  public void run() {
    if (threaded)
      runThreaded();
  }

  public void runThreaded() {
    if (image == null)
      image = image();
    runThreaded(image.createGraphics());
  }

  void setup() {

  }

  void step(Graphics2D g) {

  }

  public void f(Graphics2D g, DLThread t) {
    setup();
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      if (clear)
        clearImage();

      step(g);

      if (parent != null)
        parent.paint(this);

      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          DLError.report(e);
        }
      }
    }
  }

  public void runThreaded(final Graphics2D g) {
    DLRunnable run = new DLRunnable() {
      DLThread t;

      public void run() {
        f(g, t);
        synchronized (threads) {
          threads.remove(t);
        }
      }

      public DLThread getThread() {
        return t;
      }

      public void setThread(DLThread t) {
        this.t = t;
      }
    };
    DLThread t = new DLThread(run);
    // run.setThread(t);
    stopAll();
    synchronized (threads) {
      threads.add(t);
    }
    t.start();
  }

  public void saveAsJPG(File f) {
    DLUtil.Save(image, f);
  }

 void prepareForDisplay() {

  }

  public String getFilter() {
    return filterName;
  }

  public void setFilter(String f) {
    filterName = f;
    filter = getFilterFromString(f);
  }

  public String[] enumFilter() {
    return new String[] { 
        NullFilter, 
        EdgeFilter, 
        BlurFilter, 
        EmbossFilter, 
        EqualizeFilter, 
        OilFilter , 
        KaleidoscopeFilter,
        BumpFilter,
        CausticFilter,
        ChromeFilter,
        FlareFilter,
        ContourFilter,
        GlowFilter,
        CrystalizeFilter
        };
  }

  public void setFilterStrength(float br) {
    filterStrength = br;
  }

  public float getFilterStrength() {
    return filterStrength;
  }

  public float[] rangeFilterStrength() {
    return new float[] { 0f, 1f };
  }

  private BufferedImageOp getFilterFromString(String s) {
    switch (s) {

    case NullFilter:
      return null;

    case BlurFilter:
      BoxBlurFilter bf = new BoxBlurFilter();
      bf.setHRadius(5);
      bf.setVRadius(5);
      bf.setIterations(5);
      return bf;

    case EdgeFilter:
      EdgeFilter ef = new EdgeFilter();
      return ef;

    case EmbossFilter:
      EmbossFilter cf = new EmbossFilter();
      return cf;

    case EqualizeFilter:
      EqualizeFilter eqf = new EqualizeFilter();
      return eqf;

    case OilFilter:
      OilFilter of = new OilFilter();
      return of;

    case KaleidoscopeFilter:
      KaleidoscopeFilter k = new KaleidoscopeFilter();
      k.setAngle(DLUtil.PI / 5f);
      k.setAngle2(DLUtil.PI / 3f);
      k.setRadius((iwidth + iheight) / 4f);
      k.setSides(5);
      return k;
      
    case BumpFilter:
      BumpFilter bft = new BumpFilter();
      return bft;
      
    case CausticFilter:
      CausticsFilter cft = new CausticsFilter();
      return cft;
      
    case ChromeFilter:
      ChromeFilter crf = new ChromeFilter();
      return crf;
      
    case FlareFilter:
      FlareFilter wf = new FlareFilter();
      return wf;
      
    case ContourFilter:
      ContourFilter cof = new ContourFilter();
      return cof;
    
    case CrystalizeFilter:
      CrystallizeFilter crft = new CrystallizeFilter();
      return crft;
      
    case GlowFilter:
      GlowFilter gft = new GlowFilter();
      return gft;
      
    default:
      return null;
    }
  }

  void applyFilter() {
    if (filter == null)
      return;
    if (filterStrength <= 0.001f)
      return;
    BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    filter.filter(image, bi);
    DLUtil.Merge(image, bi, filterStrength, image);
  }

  void zoom() {
    BufferedImage i = new BufferedImage(iwidth, iheight, image.getType());
    zoom(image, i);
    image = i;
  }

  BufferedImage zoom(BufferedImage src, BufferedImage dst) {
    if (src == null || dst == null)
      return null;

    AffineTransform tx = new AffineTransform();
    float sx = dst.getWidth() / src.getWidth();
    float sy = dst.getHeight() / src.getHeight();
    tx.scale(sx, sy);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);
    op.filter(src, dst);
    return dst;
  }

  // public int getRes() {
  // return res;
  // }

  // public void setRes(int res) {
  // this.res = res;
  // reset();
  // }

  void reset(boolean img) {
    if (img)
      image = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    stopAll();
    clear();
    run();
  }

  // public int[] rangeRes() {
  // return new int[] { 1, 16 };
  // }

  public boolean getClear() {
    return clear;
  }

  public void setClear(boolean c) {
    clear = c;
  }

}
