/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.jhlabs.image;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * A filter which can be used to produce wipes by transferring the luma of a
 * Destination image into the alpha channel of the source.
 */
public class ChromaKeyFilter extends AbstractBufferedImageOp {

  private float bTolerance = 0;
  private int color;
  private float hTolerance = 0;
  private float sTolerance = 0;

  public ChromaKeyFilter() {
  }

  @Override
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    final int width = src.getWidth();
    final int height = src.getHeight();
    src.getType();
    src.getRaster();

    if (dst == null)
      dst = createCompatibleDestImage(src, null);
    dst.getRaster();

    float[] hsb1 = null;
    float[] hsb2 = null;
    final int rgb2 = color;
    final int r2 = rgb2 >> 16 & 0xff;
    final int g2 = rgb2 >> 8 & 0xff;
    final int b2 = rgb2 & 0xff;
    hsb2 = Color.RGBtoHSB(r2, b2, g2, hsb2);
    int[] inPixels = null;
    for (int y = 0; y < height; y++) {
      inPixels = getRGB(src, 0, y, width, 1, inPixels);
      for (int x = 0; x < width; x++) {
        final int rgb1 = inPixels[x];

        final int r1 = rgb1 >> 16 & 0xff;
        final int g1 = rgb1 >> 8 & 0xff;
        final int b1 = rgb1 & 0xff;
        hsb1 = Color.RGBtoHSB(r1, b1, g1, hsb1);
        // int tolerance = (int)(255*tolerance);
        // return Math.abs(r1-r2) <= tolerance && Math.abs(g1-g2) <= tolerance
        // && Math.abs(b1-b2) <= tolerance;

        // if ( PixelUtils.nearColors( in, clean, (int)(255*tolerance) ) )
        if (Math.abs(hsb1[0] - hsb2[0]) < hTolerance && Math.abs(hsb1[1] - hsb2[1]) < sTolerance
            && Math.abs(hsb1[2] - hsb2[2]) < bTolerance)
          inPixels[x] = rgb1 & 0xffffff;
        else
          inPixels[x] = rgb1;
      }
      setRGB(dst, 0, y, width, 1, inPixels);
    }

    return dst;
  }

  public float getBTolerance() {
    return bTolerance;
  }

  public int getColor() {
    return color;
  }

  public float getHTolerance() {
    return hTolerance;
  }

  public float getSTolerance() {
    return sTolerance;
  }

  public void setBTolerance(float bTolerance) {
    this.bTolerance = bTolerance;
  }

  public void setColor(int color) {
    this.color = color;
  }

  /**
   * Set the tolerance of the image in the range 0..1. *arg tolerance The
   * tolerance
   */
  public void setHTolerance(float hTolerance) {
    this.hTolerance = hTolerance;
  }

  public void setSTolerance(float sTolerance) {
    this.sTolerance = sTolerance;
  }

  @Override
  public String toString() {
    return "Keying/Chroma Key...";
  }
}
