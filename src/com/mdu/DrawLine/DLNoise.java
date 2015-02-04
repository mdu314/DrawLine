package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

class DLNoise extends DLImage {
  Color color1 = new Color(255, 0, 0);
  Color color2 = new Color(0, 0, 0);
  String noiseType = "perlin";
  int octaves = 3;
  float persistence = 0.5f;
  float rescaleOffset = 0;
  float rescaleScale = 1f;
  int[][] coordsArray;

  DLNoise(DLNoise r) {
    super(r);
    threaded = true;
  }

  public DLNoise(float x, float y) {
    super(x, y);
    threaded = true;
  }

  @Override
  DLNoise copy() {
    return new DLNoise(this);
  }

  @Override
  void dump() {
    System.err.println("octaves " + octaves);
    System.err.println("persistence " + persistence);
    System.err.println("rescaleScale " + rescaleScale);
    System.err.println("rescaleOffset " + rescaleOffset);
    System.err.println("color1 " + color1);
    System.err.println("color2 " + color2);
  }

  public void f(Graphics2D g, DLThread t) {
    float[][] noise = null;
    if (noiseType.indexOf("gauss") != -1)
      noise = Noise.GenerateGaussNoise(iwidth, iheight);

    if ((t != null) && t.isStopped())
      return;

    if (noiseType.indexOf("white") != -1)
      noise = Noise.GenerateWhiteNoise(iwidth, iheight);

    if ((t != null) && t.isStopped())
      return;

    if (noiseType.indexOf("smooth") != -1) {
      if (noise == null)
        noise = Noise.GenerateWhiteNoise(iwidth, iheight);
      noise = Noise.GenerateSmoothNoise(noise, getOctaves());
    }
    if ((t != null) && t.isStopped())
      return;
    if (noiseType.indexOf("perlin") != -1) {
      if (noise == null)
        noise = Noise.GenerateWhiteNoise(iwidth, iheight);
      noise = Noise.GeneratePerlinNoise(noise, octaves, persistence);
    }
    if ((t != null) && t.isStopped())
      return;
    if (noise != null) {
      noise = Noise.Normalize(noise, 0f, 1f);
      if (coordsArray == null)
        coordsArray = DLUtil.ShuffleCoords(iwidth, iheight);
      int[][] al = coordsArray;

      for (int i = 0; i < al.length; i++) {
        if ((t != null) && t.isStopped()) {
          return;
        }
        int[] ii = al[i];
        int x = ii[0];
        int y = ii[1];

        final Color c = Noise.GetColor(color1, color2, noise[x][y]);
        g.setColor(c);
        drawPoint(g, x, y);

        if (parent != null) {
          if ((y + iwidth * x) % 100 == 0) {
            final Rectangle r = getBounds();
            parent.paint(r);
            // parent.repaint(r.x, r.y, r.width, r.height);
          }
        }
        if ((t != null) && t.isStopped())
          return;
      }
      if (image != null)
        Noise.Rescale(image, rescaleScale, rescaleOffset);
    } else {
      System.err.println("No noise type but " + noiseType);
    }
    if (parent != null) {
      final Rectangle r = getBounds();
      parent.paint(r);
      // parent.repaint(r.x, r.y, r.width, r.height);
    }
  }

  @Override
  BufferedImage image() {
    BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = (Graphics2D) img.getGraphics();
    DLUtil.SetHints(g);
    if (threaded)
      runThreaded(g);
    else
      f(g, null);
    return img;
  }

  @Override
  public void randomize() {
    final int i = DLUtil.RangeRandom(4 * DLParams.DRAWING_STEP, 10 * DLParams.DRAWING_STEP);
    iwidth = i;
    iheight = i;
    octaves = DLUtil.RangeRandom(7, 15);
    persistence = DLUtil.RangeRandom(0.5f, 1f);
    rescaleScale = DLUtil.RangeRandom(1f, 2f);
    rescaleOffset = DLUtil.RangeRandom(0, 50);
    color1 = DLUtil.RandomColor(0, 360, 1, 1, 1, 1);
    color2 = DLUtil.RandomColor(0, 360, 1, 1, 0.2f, 0.4f);
    noiseType = "";
    // if (DLUtil.BooleanRandom())
    // noiseType += "white";
    // else
    // noiseType += "gauss";
    noiseType += "perlin";
    setShadow(true);
    threaded = true;
  }

  public Color getColor1() {
    return color1;
  }

  public void setColor1(Color color1) {
    this.color1 = color1;
    stopAll();
    clearImage();
    runThreaded();
  }

  public Color getColor2() {
    return color2;
  }

  public void setColor2(Color color2) {
    this.color2 = color2;
    stopAll();
    clearImage();
    runThreaded();
  }

  public String[] enumNoiseType() {
    return new String[] { "white", "gauss", "smooth", "perlin" };
  }

  public String getNoiseType() {
    return noiseType;
  }

  public void setNoiseType(String noiseType) {
    this.noiseType = noiseType;
    stopAll();
    clearImage();
    runThreaded();
  }

  public int getOctaves() {
    return octaves;
  }

  public void setOctaves(int octaves) {
    this.octaves = octaves;
    stopAll();
    clearImage();
    runThreaded();
  }

  public int[] rangeOctaves() {
    return new int[] { 7, 15 };
  }

  public float getPersistence() {
    return persistence;
  }

  public void setPersistence(float persistence) {
    this.persistence = persistence;
    stopAll();
    clearImage();
    runThreaded();
  }

  public float[] rangePersistence() {
    return new float[] { 0.01f, 1 };
  }

  public float getRescaleOffset() {
    return rescaleOffset;
  }

  public void setRescaleOffset(float rescaleOffset) {
    this.rescaleOffset = rescaleOffset;
    stopAll();
    clearImage();
    runThreaded();
  }

  public float[] rangeRescaleOffset() {
    return new float[] { 0, 50 };
  }

  public float getRescaleScale() {
    return rescaleScale;
  }

  public void setRescaleScale(float rescaleScale) {
    this.rescaleScale = rescaleScale;
    stopAll();
    clearImage();
    runThreaded();
  }

  float[] rangeRescaleScale() {
    return new float[] { 1, 2 };
  }
}
