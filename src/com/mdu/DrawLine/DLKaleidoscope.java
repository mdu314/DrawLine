package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class DLKaleidoscope extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;
  String textures = "images/kaleidoscope/";
  String defaultTexture = firstImage(textures); // "mantsun-intro-2.jpg";
  String imageResource = textures + defaultTexture;
  BufferedImage baseTexture;
  BufferedImage texture;
  BufferedImage invert;
  int tx = 0;
  int ty = 0;
  int radius = 200;
  int angleDiv = 7;
  float gangle = DLUtil.PI / angleDiv;
  float rotation = 0;
  boolean clear = false;
  ArrayList<DLPath> shapes = new ArrayList<DLPath>();
  float imageScale = 1f;

  public String toString() {
    String s = "imageResource " + imageResource + "\n";
    s += "baseTexture " + baseTexture + "\n";
    s += "texture " + texture + "\n";
    s += "invert " + invert + "\n";
    s += "tx " + tx + "\n";
    s += "ty " + ty;
    return s;
  }

  public DLKaleidoscope() {
    super();
  }

  DLKaleidoscope(DLKaleidoscope src) {
    this();
  }

  public DLKaleidoscope(float x, float y) {
    super(x, y);
  }

  DLKaleidoscope copy() {
    return new DLKaleidoscope(this);
  }

  void setup() {
    loadImage();
  }

  public void f(Graphics2D g, DLThread t) {
    setup();
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

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

  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  public boolean getClear() {
    return clear;
  }

  public void setClear(boolean c) {
    clear = c;
  }

  public void setAngleDiv(int ad) {
    angleDiv = ad;
    gangle = DLUtil.PI / angleDiv;
  }

  public int getAngleDiv() {
    return angleDiv;
  }

  public int[] rangeAngleDiv() {
    return new int[] {
      2, 20
    };
  }

  public void setRadius(int r) {
    radius = r;
    clearImage();
  }

  public int getRadius() {
    return radius;
  }

  public int[] rangeRadius() {
    return new int[] {
      1, 500
    };
  }

  public void setRotation(float r) {
    rotation = r;
  }

  public float getRotation() {
    return rotation;
  }

  public float[] rangeRotation() {
    return new float[] {
      0.001f, 0.5f
    };
  }

  void step(Graphics2D g) {
    if (clear)
      clearImage();
    frame(g);
    try {
      updateTexture();
    } catch (Exception e) {
      DLError.report(e, "Caught " + e);
    }
    draw(g);
  }

  public void setTx(int t) {
    if (t + radius >= baseTexture.getWidth())
      return;
    tx = t;
    texture = null;
    invert = null;
  }

  public int getTx() {
    return tx;
  }

  public int[] rangeTx() {
    return new int[] {
      0, radius
    };
  }

  public void setTy(int t) {
    if (t + radius >= baseTexture.getHeight())
      return;
    ty = t;
    texture = null;
    invert = null;
  }

  public int getTy() {
    return ty;
  }

  public int[] rangeTy() {
    return new int[] {
      0, radius
    };
  }

  public void randomize() {
    iwidth = 500; // DLUtil.RangeRandom(500, 500);
    iheight = iwidth;
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] {
      0, 1000
    };
  }

  public String getImageResource() {
    return imageResource;
  }

  public void setImageResource(String ir) {
    imageResource = textures + ir;
    loadImage();
  }

  public String[] enumImageResource() {
    URL url = DrawLine.class.getResource(textures);
    if (url == null)
      return new String[] {};
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

  public void setImageScale(float s) {
    imageScale = s;
    loadImage();
  }

  public float getImageScale() {
    return imageScale;
  }

  public float[] rangeImageScale() {
    return new float[] {
      0.01f, 2f
    };
  }

  void scaleBaseTexture() {
    if (imageScale != 1f) {
      AffineTransform t = AffineTransform.getScaleInstance(imageScale, imageScale);
      AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      baseTexture = op.filter(baseTexture, null);
    }
  }

  HashMap<String, BufferedImage> imageCache = new HashMap<String, BufferedImage>();
  
  void loadImage() {
    
    baseTexture = imageCache.get(imageResource);
    if(baseTexture == null) {
      System.err.print("Load base texture " + imageResource);
      baseTexture = DLUtil.LoadImage(imageResource, null);
      imageCache.put(imageResource,  baseTexture);
    }

    try {
      scaleBaseTexture();
    } catch (Exception e) {
      DLError.report(e, "Caught " + e);
    }
    texture = null;
    invert = null;
    try {
      updateTexture();
    } catch (Exception e) {
      DLError.report(e, "Caught " + e);
    }
  }

  void updateTexture() {
    int bw = baseTexture.getWidth();
    int bh = baseTexture.getHeight();
    if (radius + tx >= bw || radius + ty >= bh)
      return;
    texture = baseTexture.getSubimage(tx, ty, radius, radius);
    AffineTransform t = AffineTransform.getScaleInstance(1, -1);
    t.translate(0, -texture.getHeight());
    AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    invert = op.filter(texture, null);
  }

  String firstImage(String t) {
    URL url = DLKaleidoscope.class.getResource(t);
    File f;
    try {
      f = new File(url.toURI());
    } catch (URISyntaxException e) {
      f = new File(url.getPath());
    }
    String[] list = f.list();
    return list[0];
  }

  String randomImage() {
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

  float currentAngle;

  void frame(Graphics2D g) {

    float cx = iwidth / 2f;
    float cy = iheight / 2f;
    shapes.clear();
    float da = gangle / 2f;

    for (float a = currentAngle; a < DLUtil.TWO_PI + currentAngle; a = a + gangle) {
      float ax;
      float ay;

      DLPath p = DLUtil.AddPoint(cx, cy, null);

      ax = cx + DLUtil.cos(a + da) * radius;
      ay = cy + DLUtil.sin(a + da) * radius;
      p = DLUtil.AddPoint(ax, ay, p);

      ax = cx + DLUtil.cos(a - da) * radius;
      ay = cy + DLUtil.sin(a - da) * radius;
      p = DLUtil.AddPoint(ax, ay, p);

      p.setAngle(a);

      shapes.add(p);
    }
    currentAngle += rotation;
  }

  void drawFrame(Graphics2D g) {
    g.setColor(Color.lightGray);
    for (Shape s : shapes)
      g.draw(s);
  }

  void draw(Graphics2D g) {
    float cx = iwidth / 2f;
    float cy = iheight / 2f - radius / 2f;
    BufferedImage img = invert;

    for (DLPath s : shapes) {

      Shape clip = g.getClip();
      g.setClip(s);

      AffineTransform tr = AffineTransform.getRotateInstance(s.getAngle(), iwidth / 2, iheight / 2);
      AffineTransform str = g.getTransform();
      g.setTransform(tr);
      try {
        if (img != null)
          g.drawImage(img,
              (int) cx,
              (int) cy,
              (int) (cx + radius),
              (int) (cy + radius),
              0, 0, img.getWidth(), img.getHeight(), null);
      } catch (NullPointerException e) {
        DLError.report(e, "Caught " + e + " img " + img + " texture " + texture + " invert " + invert);
      }
      g.setTransform(str);
      g.setClip(clip);
      if (img == texture)
        img = invert;
      else
        img = texture;
    }
  }

  public void reset() {
    super.reset();
    clearImage();
  }

  public static void main(String[] a) {
    int w = 500;
    int h = 500;

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

    DLMain.Main(DLKaleidoscope.class, params);
  }

}
