package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class DLKaleidoscope extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;
  String textures = "images/textures/";

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

  public void f(Graphics2D g, DLThread t) {
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      clearImage();
      step(g);

      if (parent != null)
        parent.paint(this);

      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          System.err.println(e);
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

  void step(Graphics2D g) {
    draw(g);
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(200, 200);
    iheight = iwidth;
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

  Shape shape;
  BufferedImage texture;

  Shape makeShape() {
    Path2D.Float path = new Path2D.Float();
    int d = 30;
    path.moveTo(d, d);
    path.lineTo(d, 100);
    path.lineTo(100, 100);
    path.lineTo(100, d);
    path.closePath();
    return path;
  }

  float angle = 0f;
  
  void draw(Graphics2D g) {
    if (texture == null)
      texture = loadImage("attractive_article.jpg", null);
    
    if (shape == null)
      shape = makeShape();

    Rectangle r = shape.getBounds();
//    System.err.println(r);
    BufferedImage subImage = texture.getSubimage(r.x, r.y, r.width, r.height);
    AffineTransform tr = new AffineTransform();
    
    tr.rotate(angle, r.width / 2., r.height / 2.);
    tr.translate(r.width / 2., r.height / 2.);
    
    AffineTransformOp ato = new AffineTransformOp(tr, AffineTransformOp.TYPE_BICUBIC);
    BufferedImage si = ato.filter(subImage, null);
    Graphics2D gi = si.createGraphics();
    gi.setColor(Color.red);
    gi.drawRect(0, 0, si.getWidth() - 1, si.getHeight() - 1);
    g.drawImage(si, null, 10, 10);
    
    angle += 0.1f;
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

  BufferedImage loadImage(String resource, Dimension d) {
    BufferedImage i = DLUtil.LoadImage(textures + resource, d);
    return i;
  }
}
