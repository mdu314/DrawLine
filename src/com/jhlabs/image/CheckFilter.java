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

/**
 * A Filter to draw grids and check patterns.
 */
public class CheckFilter extends PointFilter {

  private float angle = 0.0f;
  private int background = 0xff000000;
  private int foreground = 0xffffffff;
  private int fuzziness = 0;
  private float m00 = 1.0f;
  private float m01 = 0.0f;
  private float m10 = 0.0f;
  private float m11 = 1.0f;
  private int xScale = 8;
  private int yScale = 8;

  public CheckFilter() {
  }

  @Override
  public int filterRGB(int x, int y, int rgb) {
    final float nx = (m00 * x + m01 * y) / xScale;
    final float ny = (m10 * x + m11 * y) / yScale;
    float f = (int) (nx + 100000) % 2 != (int) (ny + 100000) % 2 ? 1.0f : 0.0f;
    if (fuzziness != 0) {
      final float fuzz = fuzziness / 100.0f;
      final float fx = ImageMath.smoothPulse(0, fuzz, 1 - fuzz, 1, ImageMath.mod(nx, 1));
      final float fy = ImageMath.smoothPulse(0, fuzz, 1 - fuzz, 1, ImageMath.mod(ny, 1));
      f *= fx * fy;
    }
    return ImageMath.mixColors(f, foreground, background);
  }

  /**
   * Get the angle of the texture.
   *
   * @return the angle of the texture.
   * @see #setAngle
   */
  public float getAngle() {
    return angle;
  }

  /**
   * Get the background color.
   *
   * @return the color.
   * @see #setBackground
   */
  public int getBackground() {
    return background;
  }

  /**
   * Get the foreground color.
   *
   * @return the color.
   * @see #setForeground
   */
  public int getForeground() {
    return foreground;
  }

  /**
   * Get the fuzziness of the texture.
   *
   * @return the fuzziness.
   * @see #setFuzziness
   */
  public int getFuzziness() {
    return fuzziness;
  }

  /**
   * Get the X scale of the texture.
   *
   * @return the scale.
   * @see #setXScale
   */
  public int getXScale() {
    return xScale;
  }

  /**
   * Get the Y scale of the texture.
   *
   * @return the scale.
   * @see #setYScale
   */
  public int getYScale() {
    return yScale;
  }

  /**
   * Set the angle of the texture.
   *
   * @param angle
   *          the angle of the texture.
   * @angle
   * @see #getAngle
   */
  public void setAngle(float angle) {
    this.angle = angle;
    final float cos = (float) Math.cos(angle);
    final float sin = (float) Math.sin(angle);
    m00 = cos;
    m01 = sin;
    m10 = -sin;
    m11 = cos;
  }

  /**
   * Set the background color.
   *
   * @param background
   *          the color.
   * @see #getBackground
   */
  public void setBackground(int background) {
    this.background = background;
  }

  /**
   * Set the foreground color.
   *
   * @param foreground
   *          the color.
   * @see #getForeground
   */
  public void setForeground(int foreground) {
    this.foreground = foreground;
  }

  /**
   * Set the fuzziness of the texture.
   *
   * @param fuzziness
   *          the fuzziness.
   * @see #getFuzziness
   */
  public void setFuzziness(int fuzziness) {
    this.fuzziness = fuzziness;
  }

  /**
   * Set the X scale of the texture.
   *
   * @param xScale
   *          the scale.
   * @see #getXScale
   */
  public void setXScale(int xScale) {
    this.xScale = xScale;
  }

  /**
   * Set the Y scale of the texture.
   *
   * @param yScale
   *          the scale.
   * @see #getYScale
   */
  public void setYScale(int yScale) {
    this.yScale = yScale;
  }

  @Override
  public String toString() {
    return "Texture/Checkerboard...";
  }
}
