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
 * A filter which produces a rubber-stamp type of effect by performing a
 * thresholded blur.
 */
public class StampFilter extends PointFilter {

  private int black = 0xff000000;
  private float lowerThreshold3;
  private float radius = 5;
  private float softness = 0;
  private float threshold;
  private float upperThreshold3;
  private int white = 0xffffffff;

  /**
   * Construct a StampFilter.
   */
  public StampFilter() {
    this(0.5f);
  }

  /**
   * Construct a StampFilter.
   *
   * @param threshold
   *          the threshold value
   */
  public StampFilter(float threshold) {
    setThreshold(threshold);
  }

  @Override
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    dst = new GaussianFilter((int) radius).filter(src, null);
    lowerThreshold3 = 255 * 3 * (threshold - softness * 0.5f);
    upperThreshold3 = 255 * 3 * (threshold + softness * 0.5f);
    return super.filter(dst, dst);
  }

  @Override
  public int filterRGB(int x, int y, int rgb) {
    final int r = rgb >> 16 & 0xff;
    final int g = rgb >> 8 & 0xff;
    final int b = rgb & 0xff;
    final int l = r + g + b;
    final float f = ImageMath.smoothStep(lowerThreshold3, upperThreshold3, l);
    return ImageMath.mixColors(f, black, white);
  }

  /**
   * Set the color to be used for pixels below the lower threshold.
   *
   * @return the color
   * @see #setBlack
   */
  public int getBlack() {
    return black;
  }

  /**
   * Get the radius of the effect.
   *
   * @return the radius
   * @see #setRadius
   */
  public float getRadius() {
    return radius;
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

  /**
   * Get the threshold value.
   *
   * @return the threshold value
   * @see #setThreshold
   */
  public float getThreshold() {
    return threshold;
  }

  /**
   * Get the color to be used for pixels above the upper threshold.
   *
   * @return the color
   * @see #setWhite
   */
  public int getWhite() {
    return white;
  }

  /**
   * Set the color to be used for pixels below the lower threshold.
   *
   * @param black
   *          the color
   * @see #getBlack
   */
  public void setBlack(int black) {
    this.black = black;
  }

  /**
   * Set the radius of the effect.
   *
   * @param radius
   *          the radius
   * @min-value 0
   * @see #getRadius
   */
  public void setRadius(float radius) {
    this.radius = radius;
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

  /**
   * Set the threshold value.
   *
   * @param threshold
   *          the threshold value
   * @see #getThreshold
   */
  public void setThreshold(float threshold) {
    this.threshold = threshold;
  }

  /**
   * Set the color to be used for pixels above the upper threshold.
   *
   * @param white
   *          the color
   * @see #getWhite
   */
  public void setWhite(int white) {
    this.white = white;
  }

  @Override
  public String toString() {
    return "Stylize/Stamp...";
  }
}
