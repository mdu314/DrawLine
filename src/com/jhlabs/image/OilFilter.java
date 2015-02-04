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
 * A filter which produces a "oil-painting" effect.
 */
public class OilFilter extends WholeImageFilter {

  private int levels = 256;
  private int range = 3;

  public OilFilter() {
  }

  @Override
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int index = 0;
    final int[] rHistogram = new int[levels];
    final int[] gHistogram = new int[levels];
    final int[] bHistogram = new int[levels];
    final int[] rTotal = new int[levels];
    final int[] gTotal = new int[levels];
    final int[] bTotal = new int[levels];
    final int[] outPixels = new int[width * height];

    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++) {
        for (int i = 0; i < levels; i++)
          rHistogram[i] = gHistogram[i] = bHistogram[i] = rTotal[i] = gTotal[i] = bTotal[i] = 0;

        for (int row = -range; row <= range; row++) {
          final int iy = y + row;
          int ioffset;
          if (0 <= iy && iy < height) {
            ioffset = iy * width;
            for (int col = -range; col <= range; col++) {
              final int ix = x + col;
              if (0 <= ix && ix < width) {
                final int rgb = inPixels[ioffset + ix];
                final int r = rgb >> 16 & 0xff;
                final int g = rgb >> 8 & 0xff;
                final int b = rgb & 0xff;
                final int ri = r * levels / 256;
                final int gi = g * levels / 256;
                final int bi = b * levels / 256;
                rTotal[ri] += r;
                gTotal[gi] += g;
                bTotal[bi] += b;
                rHistogram[ri]++;
                gHistogram[gi]++;
                bHistogram[bi]++;
              }
            }
          }
        }

        int r = 0, g = 0, b = 0;
        for (int i = 1; i < levels; i++) {
          if (rHistogram[i] > rHistogram[r])
            r = i;
          if (gHistogram[i] > gHistogram[g])
            g = i;
          if (bHistogram[i] > bHistogram[b])
            b = i;
        }
        r = rTotal[r] / rHistogram[r];
        g = gTotal[g] / gHistogram[g];
        b = bTotal[b] / bHistogram[b];
        outPixels[index] = inPixels[index] & 0xff000000 | r << 16 | g << 8 | b;
        index++;
      }
    return outPixels;
  }

  /**
   * Get the number of levels for the effect.
   *
   * @return the number of levels
   * @see #setLevels
   */
  public int getLevels() {
    return levels;
  }

  /**
   * Get the range of the effect in pixels.
   *
   * @return the range
   * @see #setRange
   */
  public int getRange() {
    return range;
  }

  /**
   * Set the number of levels for the effect.
   *
   * @param levels
   *          the number of levels
   * @see #getLevels
   */
  public void setLevels(int levels) {
    this.levels = levels;
  }

  /**
   * Set the range of the effect in pixels.
   *
   * @param range
   *          the range
   * @see #getRange
   */
  public void setRange(int range) {
    this.range = range;
  }

  @Override
  public String toString() {
    return "Stylize/Oil...";
  }

}
