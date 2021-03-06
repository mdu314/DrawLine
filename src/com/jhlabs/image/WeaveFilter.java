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

public class WeaveFilter extends PointFilter {

  private final int cols = 4;
  public int[][] matrix = { { 0, 1, 0, 1 }, { 1, 0, 1, 0 }, { 0, 1, 0, 1 }, { 1, 0, 1, 0 }, };
  private final int rgbX = 0xffff8080;
  private final int rgbY = 0xff8080ff;
  private boolean roundThreads = false;
  private final int rows = 4;
  private boolean shadeCrossings = true;
  private boolean useImageColors = true;
  private float xGap = 6;
  private float xWidth = 16;
  private float yGap = 6;

  private float yWidth = 16;

  public WeaveFilter() {
  }

  @Override
  public int filterRGB(int x, int y, int rgb) {
    x += xWidth + xGap / 2;
    y += yWidth + yGap / 2;
    final float nx = ImageMath.mod(x, xWidth + xGap);
    final float ny = ImageMath.mod(y, yWidth + yGap);
    final int ix = (int) (x / (xWidth + xGap));
    final int iy = (int) (y / (yWidth + yGap));
    final boolean inX = nx < xWidth;
    final boolean inY = ny < yWidth;
    float dX, dY;
    float cX, cY;
    int lrgbX, lrgbY;

    if (roundThreads) {
      dX = Math.abs(xWidth / 2 - nx) / xWidth / 2;
      dY = Math.abs(yWidth / 2 - ny) / yWidth / 2;
    } else
      dX = dY = 0;

    if (shadeCrossings) {
      cX = ImageMath.smoothStep(xWidth / 2, xWidth / 2 + xGap, Math.abs(xWidth / 2 - nx));
      cY = ImageMath.smoothStep(yWidth / 2, yWidth / 2 + yGap, Math.abs(yWidth / 2 - ny));
    } else
      cX = cY = 0;

    if (useImageColors)
      lrgbX = lrgbY = rgb;
    else {
      lrgbX = rgbX;
      lrgbY = rgbY;
    }
    int v;
    final int ixc = ix % cols;
    final int iyr = iy % rows;
    final int m = matrix[iyr][ixc];
    if (inX) {
      if (inY) {
        v = m == 1 ? lrgbX : lrgbY;
        v = ImageMath.mixColors(2 * (m == 1 ? dX : dY), v, 0xff000000);
      } else {
        if (shadeCrossings)
          if (m != matrix[(iy + 1) % rows][ixc]) {
            if (m == 0)
              cY = 1 - cY;
            cY *= 0.5f;
            lrgbX = ImageMath.mixColors(cY, lrgbX, 0xff000000);
          } else if (m == 0)
            lrgbX = ImageMath.mixColors(0.5f, lrgbX, 0xff000000);
        v = ImageMath.mixColors(2 * dX, lrgbX, 0xff000000);
      }
    } else if (inY) {
      if (shadeCrossings)
        if (m != matrix[iyr][(ix + 1) % cols]) {
          if (m == 1)
            cX = 1 - cX;
          cX *= 0.5f;
          lrgbY = ImageMath.mixColors(cX, lrgbY, 0xff000000);
        } else if (m == 1)
          lrgbY = ImageMath.mixColors(0.5f, lrgbY, 0xff000000);
      v = ImageMath.mixColors(2 * dY, lrgbY, 0xff000000);
    } else
      v = 0x00000000;
    return v;
  }

  public int[][] getCrossings() {
    return matrix;
  }

  public boolean getRoundThreads() {
    return roundThreads;
  }

  public boolean getShadeCrossings() {
    return shadeCrossings;
  }

  public boolean getUseImageColors() {
    return useImageColors;
  }

  public float getXGap() {
    return xGap;
  }

  public float getXWidth() {
    return xWidth;
  }

  public float getYGap() {
    return yGap;
  }

  public float getYWidth() {
    return yWidth;
  }

  public void setCrossings(int[][] matrix) {
    this.matrix = matrix;
  }

  public void setRoundThreads(boolean roundThreads) {
    this.roundThreads = roundThreads;
  }

  public void setShadeCrossings(boolean shadeCrossings) {
    this.shadeCrossings = shadeCrossings;
  }

  public void setUseImageColors(boolean useImageColors) {
    this.useImageColors = useImageColors;
  }

  public void setXGap(float xGap) {
    this.xGap = xGap;
  }

  public void setXWidth(float xWidth) {
    this.xWidth = xWidth;
  }

  public void setYGap(float yGap) {
    this.yGap = yGap;
  }

  public void setYWidth(float yWidth) {
    this.yWidth = yWidth;
  }

  @Override
  public String toString() {
    return "Texture/Weave...";
  }

}
