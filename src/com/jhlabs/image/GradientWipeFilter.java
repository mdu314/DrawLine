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

import java.awt.image.BufferedImage;

/**
 * A filter which can be used to produce wipes by transferring the luma of a
 * mask image into the alpha channel of the source.
 */
public class GradientWipeFilter extends AbstractBufferedImageOp {

  private float density = 0;
  private boolean invert;
  private BufferedImage mask;
  private float softness = 0;

  public GradientWipeFilter() {
  }

  @Override
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    final int width = src.getWidth();
    final int height = src.getHeight();

    if (dst == null)
      dst = createCompatibleDestImage(src, null);
    if (mask == null)
      return dst;

    final int maskWidth = mask.getWidth();
    final int maskHeight = mask.getHeight();

    final float d = density * (1 + softness);
    final float lower = 255 * (d - softness);
    final float upper = 255 * d;

    final int[] inPixels = new int[width];
    final int[] maskPixels = new int[maskWidth];

    for (int y = 0; y < height; y++) {
      getRGB(src, 0, y, width, 1, inPixels);
      getRGB(mask, 0, y % maskHeight, maskWidth, 1, maskPixels);

      for (int x = 0; x < width; x++) {
        final int maskRGB = maskPixels[x % maskWidth];
        final int inRGB = inPixels[x];
        final int v = PixelUtils.brightness(maskRGB);
        final float f = ImageMath.smoothStep(lower, upper, v);
        int a = (int) (255 * f);

        if (invert)
          a = 255 - a;
        inPixels[x] = a << 24 | inRGB & 0x00ffffff;
      }

      setRGB(dst, 0, y, width, 1, inPixels);
    }

    return dst;
  }

  public float getDensity() {
    return density;
  }

  public boolean getInvert() {
    return invert;
  }

  public BufferedImage getMask() {
    return mask;
  }

  /**
   * Get the softness of the dissolve.
   *
   * @return the softness
   * @see #setSoftness
   */
  public float getSoftness() {
    return softness;
  }

  /**
   * Set the density of the image in the range 0..1. *arg density The density
   */
  public void setDensity(float density) {
    this.density = density;
  }

  public void setInvert(boolean invert) {
    this.invert = invert;
  }

  public void setMask(BufferedImage mask) {
    this.mask = mask;
  }

  /**
   * Set the softness of the dissolve in the range 0..1.
   *
   * @param softness
   *          the softness
   * @min-value 0
   * @max-value 1
   * @see #getSoftness
   */
  public void setSoftness(float softness) {
    this.softness = softness;
  }

  @Override
  public String toString() {
    return "Transitions/Gradient Wipe...";
  }
}
