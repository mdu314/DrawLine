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

import java.awt.Rectangle;

/**
 * A filter which allows levels adjustment on an image.
 */
public class LevelsFilter extends WholeImageFilter {

  private float highLevel = 1;
  private float highOutputLevel = 1;
  private float lowLevel = 0;
  private float lowOutputLevel = 0;
  private int[][] lut;

  public LevelsFilter() {
  }

  @Override
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    final Histogram histogram = new Histogram(inPixels, width, height, 0, width);

    int i, j;

    if (histogram.getNumSamples() > 0) {
      histogram.getNumSamples();
      lut = new int[3][256];

      final float low = lowLevel * 255;
      float high = highLevel * 255;
      if (low == high)
        high++;
      for (i = 0; i < 3; i++)
        for (j = 0; j < 256; j++)
          lut[i][j] = PixelUtils.clamp((int) (255 * (lowOutputLevel + (highOutputLevel - lowOutputLevel) * (j - low)
              / (high - low))));
    } else
      lut = null;

    i = 0;
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++) {
        inPixels[i] = filterRGB(x, y, inPixels[i]);
        i++;
      }
    lut = null;

    return inPixels;
  }

  public int filterRGB(int x, int y, int rgb) {
    if (lut != null) {
      final int a = rgb & 0xff000000;
      final int r = lut[Histogram.RED][rgb >> 16 & 0xff];
      final int g = lut[Histogram.GREEN][rgb >> 8 & 0xff];
      final int b = lut[Histogram.BLUE][rgb & 0xff];

      return a | r << 16 | g << 8 | b;
    }
    return rgb;
  }

  public float getHighLevel() {
    return highLevel;
  }

  public float getHighOutputLevel() {
    return highOutputLevel;
  }

  public float getLowLevel() {
    return lowLevel;
  }

  public float getLowOutputLevel() {
    return lowOutputLevel;
  }

  public void setHighLevel(float highLevel) {
    this.highLevel = highLevel;
  }

  public void setHighOutputLevel(float highOutputLevel) {
    this.highOutputLevel = highOutputLevel;
  }

  public void setLowLevel(float lowLevel) {
    this.lowLevel = lowLevel;
  }

  public void setLowOutputLevel(float lowOutputLevel) {
    this.lowOutputLevel = lowOutputLevel;
  }

  @Override
  public String toString() {
    return "Colors/Levels...";
  }
}
