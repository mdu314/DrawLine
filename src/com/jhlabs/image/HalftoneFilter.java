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
 * A filter which uses a another image as a ask to produce a halftoning effect.
 */
public class HalftoneFilter extends AbstractBufferedImageOp {

  private boolean invert;
  private BufferedImage mask;
  private boolean monochrome;
  private float softness = 0.1f;

  public HalftoneFilter() {
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

    final float s = 255 * softness;

    final int[] inPixels = new int[width];
    final int[] maskPixels = new int[maskWidth];

    for (int y = 0; y < height; y++) {
      getRGB(src, 0, y, width, 1, inPixels);
      getRGB(mask, 0, y % maskHeight, maskWidth, 1, maskPixels);

      for (int x = 0; x < width; x++) {
        int maskRGB = maskPixels[x % maskWidth];
        final int inRGB = inPixels[x];
        if (invert)
          maskRGB ^= 0xffffff;
        if (monochrome) {
          final int v = PixelUtils.brightness(maskRGB);
          final int iv = PixelUtils.brightness(inRGB);
          final float f = 1 - ImageMath.smoothStep(iv - s, iv + s, v);
          final int a = (int) (255 * f);
          inPixels[x] = inRGB & 0xff000000 | a << 16 | a << 8 | a;
        } else {
          final int ir = inRGB >> 16 & 0xff;
          final int ig = inRGB >> 8 & 0xff;
          final int ib = inRGB & 0xff;
          final int mr = maskRGB >> 16 & 0xff;
          final int mg = maskRGB >> 8 & 0xff;
          final int mb = maskRGB & 0xff;
          final int r = (int) (255 * (1 - ImageMath.smoothStep(ir - s, ir + s, mr)));
          final int g = (int) (255 * (1 - ImageMath.smoothStep(ig - s, ig + s, mg)));
          final int b = (int) (255 * (1 - ImageMath.smoothStep(ib - s, ib + s, mb)));
          inPixels[x] = inRGB & 0xff000000 | r << 16 | g << 8 | b;
        }
      }

      setRGB(dst, 0, y, width, 1, inPixels);
    }

    return dst;
  }

  public boolean getInvert() {
    return invert;
  }

  /**
   * Get the halftone mask.
   *
   * @return the mask
   * @see #setMask
   */
  public BufferedImage getMask() {
    return mask;
  }

  /**
   * Get whether to do monochrome halftoning.
   *
   * @return true for monochrome halftoning
   * @see #setMonochrome
   */
  public boolean getMonochrome() {
    return monochrome;
  }

  /**
   * Get the softness of the effect.
   *
   * @return the softness
   * @see #setSoftness
   */
  public float getSoftness() {
    return softness;
  }

  public void setInvert(boolean invert) {
    this.invert = invert;
  }

  /**
   * Set the halftone mask.
   *
   * @param mask
   *          the mask
   * @see #getMask
   */
  public void setMask(BufferedImage mask) {
    this.mask = mask;
  }

  /**
   * Set whether to do monochrome halftoning.
   *
   * @param monochrome
   *          true for monochrome halftoning
   * @see #getMonochrome
   */
  public void setMonochrome(boolean monochrome) {
    this.monochrome = monochrome;
  }

  /**
   * Set the softness of the effect in the range 0..1.
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
    return "Stylize/Halftone...";
  }
}
