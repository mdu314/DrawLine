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

public class DLKaleidoscope extends DLImage {
  private static final String FlipHorizontal = "flip horizontal";
  private static final String FlipVertical = "flip vertical";
  private static final String DoNotFlip = "do not flip";
  String flip = DoNotFlip;
  String textures = "images/kaleidoscope/";
  String defaultTexture = randomImage(textures);
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
  // boolean clear = true;
  ArrayList<DLPath> shapes = new ArrayList<DLPath>();
  float imageScale = 1f;
  float currentAngle;
  HashMap<String, BufferedImage> imageCache = new HashMap<String, BufferedImage>();
  boolean clip = true;

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

  public void setAngleDiv(int ad) {
    angleDiv = ad;
    gangle = DLUtil.PI / angleDiv;
  }

  public int getAngleDiv() {
    return angleDiv;
  }

  public int[] rangeAngleDiv() {
    return new int[] { 2, 20 };
  }

  public void setRadius(int r) {
    radius = r;
    if (sheet != null)
      sheet.update("Tx", getTx());
    clearImage();
  }

  public int getRadius() {
    return radius;
  }

  public int[] rangeRadius() {
    return new int[] { 1, 500 };
  }

  public void setRotation(float r) {
    rotation = r;
  }

  public float getRotation() {
    return rotation;
  }

  public float[] rangeRotation() {
    return new float[] { 0.001f, 0.5f };
  }

  public boolean getClip() {
    return clip;
  }

  public void setClip(boolean c) {
    clip = c;
  }

  void step(Graphics2D g) {
    frame(g);
    updateTexture();
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
    return new int[] { 0, radius };
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
    return new int[] { 0, radius };
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
    return new int[] { 0, 1000 };
  }

  public String getImageResource() {
    return imageResource;
  }

  public void setImageResource(String ir) {
    boolean b = ir.indexOf(File.separator) != -1;
    if (b)
      imageResource = ir;
    else
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
    for (int i = 0; i < list.length; i++) {
      String s = list[i];
      items[i] = s;
      String path = new String(textures + File.separator + s);
      items[i] = path;
    }
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
    return new float[] { 0.01f, 2f };
  }

  public String getFlip() {
    return flip;
  }

  public void setFlip(String f) {
    flip = f;
    loadImage();
  }

  public String[] enumFlip() {
    return new String[] { FlipHorizontal, FlipVertical, DoNotFlip };
  }

  void scaleBaseTexture() {
    if (imageScale != 1f) {
      AffineTransform t = AffineTransform.getScaleInstance(imageScale, imageScale);
      AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      baseTexture = op.filter(baseTexture, null);
    }
  }

  void flipBaseTexture() {
    switch (flip) {
    case FlipHorizontal: {
      AffineTransform t = AffineTransform.getScaleInstance(1, -1);
      t.translate(0, -baseTexture.getHeight());
      AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      baseTexture = op.filter(baseTexture, null);
      break;
    }
    case FlipVertical: {
      AffineTransform t = AffineTransform.getScaleInstance(-1, 1);
      t.translate(-baseTexture.getWidth(), 0);
      AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      baseTexture = op.filter(baseTexture, null);
      break;
    }
    case DoNotFlip:
      break;
    }
  }

  BufferedImage getImage(String t) {
    BufferedImage i = imageCache.get(imageResource);
    if (i == null) {
      System.err.println("Load base texture " + imageResource);
      i = DLUtil.LoadImage(imageResource, null);
      if (i != null)
        imageCache.put(imageResource, i);
    }
    return i;
  }

  void loadImage() {

    baseTexture = getImage(imageResource);

    try {
      flipBaseTexture();
    } catch (Exception e) {
      DLError.report(e, "Caught " + e);
    }

    try {
      scaleBaseTexture();
    } catch (Exception e) {
      DLError.report(e, "Caught " + e);
    }

    // texture = null;
    // invert = null;

    try {
      updateTexture();
    } catch (Exception e) {
      DLError.report(e, "Caught " + e);
    }
  }

  void updateTexture() {
    if (baseTexture == null)
      baseTexture = getImage(imageResource);
    if (baseTexture == null)
      throw new Error("Cannot load " + imageResource);
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

  String randomImage(String t) {
    URL url = getClass().getResource(t);
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

      Shape oclip = g.getClip();
      if (clip)
        g.setClip(s);

      AffineTransform tr = AffineTransform.getRotateInstance(s.getAngle(), iwidth / 2, iheight / 2);
      AffineTransform str = g.getTransform();
      g.setTransform(tr);
      try {
        if (img != null)
          g.drawImage(img, (int) cx, (int) cy, (int) (cx + radius), (int) (cy + radius), 0, 0, img.getWidth(),
              img.getHeight(), null);
      } catch (NullPointerException e) {
        DLError.report(e, "Caught " + e + " img " + img + " texture " + texture + " invert " + invert);
      }
      g.setTransform(str);
      g.setClip(oclip);
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

    Object[][] params = { { "iwidth", w }, { "iheight", h }, { "x", w / 2 }, { "y", h / 2 }, { "threadSleep", 5 } };

    DLMain.Main(DLKaleidoscope.class, params);
  }

}
