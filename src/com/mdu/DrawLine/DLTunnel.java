package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.jhlabs.image.EdgeFilter;

public class DLTunnel extends DLImage {
  int threadSleep = 20;
  String imageResource = null;
  String textures = "images/textures/";

  long frameCount = 1;
  BufferedImage texture;
  BufferedImage mergedTexture;
  int[][] distances;
  int[][] angles;
  int[] pixels;
  boolean clearImage = true;

  float movement = 0.1f;
  float animation = 0;
  float animIncr = 3;
  float movIncr = 1;
  float filterIncr = 0;

  float trente = 30;
  float ratioIncr = 0.01f;
  float maxRatio = 1f;

  float currentRatio = maxRatio;
  BufferedImage currentTexture = null;
  String currentTextureName = null;
  boolean changeImage;
  float filterStrength = 0f;
  boolean move = false;
  boolean imageCycle;
  boolean paintFPS;

  boolean decorateTexture = false;
  int rabouteMarginWidth = 0;
  int rabouteMarginHeight = 0;
  static final String RABOUTE_VERTICAL = "raboute vertical";
  static final String RABOUTE_HORIZONTAL = "raboute horizontal";
  String rabouteDirection = RABOUTE_HORIZONTAL + " " + RABOUTE_VERTICAL;
  BufferedImage filterImage;

  public DLTunnel() {
    super();
  }

  DLTunnel(DLTunnel src) {
    this();
  }

  public DLTunnel(float x, float y) {
    super(x, y);
  }

  DLTunnel copy() {
    return new DLTunnel(this);
  }

  void decorateTexture(BufferedImage tex) {

    Graphics2D g = tex.createGraphics();
    if (DLUtil.BooleanRandom()) {
      for (int i = 0; i < 1000; i++) {
        float x = DLUtil.RangeRandom(0, tex.getWidth());
        float y = DLUtil.RangeRandom(0, tex.getHeight());
        int r = DLUtil.RangeRandom(0, pointShapes.length);
        String s = pointShapes[r];
        float sz = DLUtil.RangeRandom(0, 10);
        if (DLUtil.BooleanRandom())
          pointStroke = DLUtil.RandomColor(0, 1, 0.7f, 1, 0.8f, 1);
        if (DLUtil.BooleanRandom())
          pointFill = DLUtil.RandomColor(0, 1, 0.7f, 1, 0.8f, 1);
        drawPoint(g, s, sz, x, y);
      }
    } else {
      for (int i = 0; i < 10; i++) {
        float x = DLUtil.RangeRandom(0, tex.getWidth());
        float y = DLUtil.RangeRandom(0, tex.getHeight());
        DLLorenz d = new DLLorenz(x, y);
        d.randomize();
        d.stroke = DLUtil.RandomColor(0, 1, 0.7f, 1, 0.8f, 1);
        d.fill = null;
        d.paint(g, true);
      }
    }
  }

  BufferedImage loadImage(String resource, Dimension d) {
    BufferedImage i = DLUtil.LoadImage(resource, d);
    if (decorateTexture)
      decorateTexture(i);
    if (rabouteMarginWidth > 0 || rabouteMarginHeight > 0)
      raboute(i);
    return i;
  }

  void nextImage() {
    if (imageCycle) {
      if (currentTexture == null || changeImage) {
        boolean r = DLUtil.BooleanRandom(0.99f);
        if (r || changeImage) {
          String is = randomImage();
          currentRatio = 0;
          currentTextureName = is;
          changeImage = false;
          imageResource = is;
          currentTexture = loadImage(textures + is, new Dimension(iwidth, iheight));
          if (sheet != null) {
            sheet.update("ImageResource", imageResource);
            sheet.update("ChangeImage", false);
          }
        }
      }
    }
    if (currentTexture != null) {
      currentRatio += ratioIncr;
      if (currentRatio >= maxRatio || (currentTexture == texture)) {
        currentRatio = 0;
        texture = currentTexture;
        currentTexture = null;
        currentTextureName = null;
        mergedTexture = null;
      } else {
        //        System.err.println("texture " + texture.hashCode() + " currentTexture " + currentTexture.hashCode() + " " + currentRatio);
        mergedTexture = DLUtil.Merge(texture, currentTexture, currentRatio);
      }
    }
  }

  void setup(boolean t, boolean a, boolean d) {
    if (t)
      texture = loadImage(imageResource, new Dimension(iwidth, iheight));
    if (a || d) {
      if (d)
        distances = new int[iwidth][iheight];
      if (a)
        angles = new int[iwidth][iheight];

      int textureWidth = texture.getWidth();
      int textureHeight = texture.getHeight();
      pixels = new int[iwidth * iheight];
      float cx = iwidth / 2f;
      float cy = iheight / 2f;
      for (int x = 0; x < iwidth; x++) {
        float dx = x - cx;
        for (int y = 0; y < iheight; y++) {
          float dy = y - cy;
          if (d)
            distances[x][y] = (int) ((trente * textureWidth / Math.sqrt(dx * dx + dy * dy)) % textureHeight);
          if (a)
            angles[x][y] = (int) (0.5f * textureWidth * Math.atan2(dy, dx / 2.0f) / DLUtil.PI);
        }
      }
    }
  }

  static int average(int p1, int p2, float r) {
    
    int red1 = (p1 >> 16) & 0xff;
    int green1 = (p1 >> 8) & 0xff;
    int blue1 = (p1 >> 0) & 0xff;

    int red2 = (p2 >> 16) & 0xff;
    int green2 = (p2 >> 8) & 0xff;
    int blue2 = (p2 >> 0) & 0xff;

    int red = (int) ((1f - r) * red1 + r * red2 + 0.5f);
    int green = (int) ((1f - r) * green1 + r * green2 + 0.5f);
    int blue = (int) ((1f - r) * blue1 + r * blue2 + 0.5f);

    int p = 0xff << 24 | red << 16 | green << 8 | blue;
    return p;
  }

  void raboute(BufferedImage img) {
    int iw = img.getWidth();
    int ih = img.getHeight();
    int mw = rabouteMarginWidth;

    for (int j = 0; j < mw; j++) {
      float r = DLUtil.Normalize(0.5f, 0, 0, mw, j);
      for (int i = 0; i < iw; i++) {
        int p1 = img.getRGB(i, j);
        int p2 = img.getRGB(i, ih - j - 1);

        int p = average(p1, p2, r);
        img.setRGB(i, j, p);
        p = average(p1, p2, 1 - r);
        img.setRGB(i, ih - j - 1, p);

      }
    }

    int mh = rabouteMarginHeight;
    for (int i = 0; i < mh; i++) {
      float r = DLUtil.Normalize(0.5f, 0, 0, mh, i);
      for (int j = 0; j < ih; j++) {
        int p1 = img.getRGB(i, j);
        int p2 = img.getRGB(iw - i - 1, j);

        int p = average(p1, p2, r);
        img.setRGB(i, j, p);
        p = average(p1, p2, 1 - r);
        img.setRGB(iw - i - 1, j, p);

      }
    }

  }

  void filter() {
    filterStrength += filterIncr;
    if (filterStrength < 0)
      filterStrength = 0;
    if (filterStrength > 1)
      filterStrength = 1;
    if (sheet != null)
      sheet.update("Filter", filterStrength);
    if (filterStrength > 0) {
      EdgeFilter ef = new EdgeFilter();
      /* BufferedImage filterImage = */ef.filter(image, filterImage);
      image = DLUtil.Merge(image, filterImage, filterStrength);
    }
  }

  synchronized void tunnel() {
    animation += animIncr;
    movement += movIncr;

    BufferedImage t = (mergedTexture != null) ? mergedTexture : texture;

    int tw = t.getWidth(null);
    int th = t.getHeight(null);

    int shiftX = (int) (tw + animation);
    int shiftY = (int) (th + movement);

    for (int y = 0, cursor = 0; y < iheight; y++) {
      for (int x = 0; x < iwidth; x++, cursor++) {
        int c_x = (distances[x][y] + shiftX) % tw;
        int c_y = (angles[x][y] + shiftY) % th;
        try {
          pixels[cursor] = t.getRGB(c_x, c_y);
        } catch (Throwable e) {
          reportException(e);
        }
      }
    }
    image.setRGB(0, 0, iwidth, iheight, pixels, 0, iwidth);
  }

  void move() {
    if (!move)
      return;
    if (DLUtil.BooleanRandom(0.7f)) {
      animIncr += DLUtil.RangeRandom(-2f, 2f);
      if (sheet != null)
        sheet.update("AnimIncr", animIncr);
    }
    if (DLUtil.BooleanRandom(0.8f)) {
      movIncr += DLUtil.RangeRandom(-1f, 1f);
      if (sheet != null)
        sheet.update("MovIncr", movIncr);
    }
    if (DLUtil.BooleanRandom(0.6f)) {
      filterIncr += DLUtil.RangeRandom(-0.1f, 0.1f);
      if (filterStrength + filterIncr < 0)
        filterIncr = -filterStrength;
      if (filterStrength + filterIncr > 1)
        filterIncr = 1 - filterStrength;
      if (sheet != null)
        sheet.update("Filter", filterIncr);
    }
  }

  public void f(Graphics2D g, DLThread t) {
    setup(true, true, true);
    long start = System.currentTimeMillis();
    long dt = 0;
    while (frameCount++ > 0) {
      start = System.currentTimeMillis();
      if (t != null && t.isStopped())
        break;
      try {
        synchronized (this) {
          nextImage();
          move();
          tunnel();
          filter();
          paintFps(g, dt);
        }
      } catch (Exception e) {
        reportException(e);
      }
      if (parent != null)
        parent.paint(this);
      dt = System.currentTimeMillis() - start;
      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          reportException(e);
        }
      }
    }
  }

  void paintFps(Graphics2D g, long frameTime) {
    if (!paintFPS)
      return;
    if (frameTime == 0)
      return;
    DLUtil.SetHints(g);

    String s = null;
    Font f = new Font(Font.MONOSPACED, Font.PLAIN, 10);
    FontMetrics metrics = g.getFontMetrics(f);
    int descent = metrics.getDescent();

    if (currentTexture != null && currentRatio != 0) {
      NumberFormat ft = new DecimalFormat("0.00");
      s = "Loading " + currentTextureName + " " + ft.format(currentRatio);
    } else {
      NumberFormat nf = new DecimalFormat("000.00");
      NumberFormat tf = new DecimalFormat("00.00");
      NumberFormat ff = new DecimalFormat("00000");

      s = " F#: " + ff.format(frameCount) + " Fps: " + nf.format(1000. / frameTime) + " Ft: " + tf.format(frameTime)
          + " ms " + "Mi: " + tf.format(movIncr) + " Ai: " + tf.format(animIncr);
    }
    FontMetrics m = g.getFontMetrics();
    float w = m.stringWidth(s);
    float h = m.getMaxAscent() + m.getMaxDescent();
    float x = 5;
    float y = iheight - h;
    Rectangle2D.Float r2d = new Rectangle2D.Float(x, y, w, h);
    Color c = new Color(0x66, 0, 0x33);
    g.setColor(DLUtil.TransparenterColor(DLUtil.Invert(c), 0.65f));
    g.fill(r2d);
    g.setColor(c);
    g.draw(r2d);
    g.setFont(f);
    g.drawString(s, 5, iheight - descent);
  }

  BufferedImage image() {
    filterImage = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  void step(Graphics2D g) {
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(100, 500);
    iheight = iwidth; // DLUtil.RangeRandom(300, 500);
    setImageResource(randomImage());
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] { 0, 100 };
  }

  public String getImageResource() {
    return imageResource;
  }

  public void setImageResource(String ir) {
    imageResource = textures + ir;
    currentTexture = loadImage(imageResource, new Dimension(iwidth, iheight));
    currentTextureName = ir;
  }

  public String[] enumImageResource() {
    URL url = getClass().getResource(textures);
    File f;
    try {
      f = new File(url.toURI());
    } catch (URISyntaxException e) {
      f = new File(url.getPath());
    }
    String[] list = f.list();
    String[] items = new String[list.length + 1];
    for (int i = 0; i < list.length; i++)
      items[i] = list[i];
    items[list.length] = null;
    return items;
  }

  public String randomImage() {
    URL url = getClass().getResource(textures);
    File f;
    try {
      f = new File(url.toURI());
    } catch (URISyntaxException e) {
      f = new File(url.getPath());
    }
    String[] list = f.list();
    int r = DLUtil.RangeRandom(0, list.length);
    return list[r];
  }

  public float getAnimIncr() {
    return animIncr;
  }

  public void setAnimIncr(float animIncr) {
    this.animIncr = animIncr;
  }

  public float[] rangeAnimIncr() {
    return new float[] { -10f, 10f };
  }

  public float getMovIncr() {
    return movIncr;
  }

  public void setMovIncr(float movIncr) {
    this.movIncr = movIncr;
  }

  public float[] rangeMovIncr() {
    return new float[] { -10f, 10f };
  }

  public boolean getClearImage() {
    return clearImage;
  }

  public void setClearImage(boolean clearImage) {
    this.clearImage = clearImage;
  }

  public boolean getChangeImage() {
    return changeImage;
  }

  public void setChangeImage(boolean changeImage) {
    this.changeImage = changeImage;

  }

  public float getTrente() {
    return trente;
  }

  public void setTrente(float trente) {
    this.trente = trente;
    setup(false, false, true);
  }

  public float[] rangeTrente() {
    return new float[] { 1, 100 };
  }

  public void setFilter(float br) {
    filterStrength = br;
  }

  public float getFilter() {
    return filterStrength;
  }

  public float[] rangeFilter() {
    return new float[] { 0f, 1 };
  }

  public boolean getMove() {
    return move;
  }

  public void setMove(boolean m) {
    move = m;
    if (!m) {
      movIncr = 1;
      animIncr = 3;
      filterIncr = 0;
    }
  }

  public boolean getImageCycle() {
    return imageCycle;
  }

  public void setImageCycle(boolean m) {
    imageCycle = m;
  }

  public boolean getPaintFPS() {
    return paintFPS;
  }

  public void setPaintFPS(boolean m) {
    paintFPS = m;
  }

  public boolean getDecorateTexture() {
    return decorateTexture;
  }

  public void setDecorateTexture(boolean decorateTexture) {
    this.decorateTexture = decorateTexture;
    setup(true, false, false);
  }

  public int getRabouteMarginWidth() {
    return rabouteMarginWidth;
  }

  public void setRabouteMarginWidth(int rabouteMargin) {
    this.rabouteMarginWidth = rabouteMargin;
    setup(true, false, false);
  }

  public int[] rangeRabouteMarginWidth() {
    return new int[] { 0, 100 };
  }

  public int getRabouteMarginHeight() {
    return rabouteMarginHeight;
  }

  public void setRabouteMarginHeight(int rabouteMargin) {
    this.rabouteMarginHeight = rabouteMargin;
    setup(true, false, false);
  }

  public int[] rangeRabouteMarginHeight() {
    return new int[] { 0, 100 };
  }
}
