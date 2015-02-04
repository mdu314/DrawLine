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

import java.awt.geom.Point2D;

import com.jhlabs.math.Noise;

/**
 * An experimental filter for rendering lens flares.
 */
public class FlareFilter extends PointFilter {

  private float baseAmount = 1.0f;
  private float centreX = 0.5f, centreY = 0.5f;
  private int color = 0xffffffff;
  private final float falloff = 6.0f;
  private final float gauss = 0.006f;
  private float icentreX, icentreY;
  private final float linear = 0.03f;

  private final float mix = 0.50f;
  private float radius;
  private float rayAmount = 0.1f;
  private float ringAmount = 0.2f;
  private float ringWidth = 1.6f;

  public FlareFilter() {
    setRadius(50.0f);
  }

  @Override
  public int filterRGB(int x, int y, int rgb) {
    final float dx = x - icentreX;
    final float dy = y - icentreY;
    final float distance = (float) Math.sqrt(dx * dx + dy * dy);
    float a = (float) Math.exp(-distance * distance * gauss) * mix + (float) Math.exp(-distance * linear) * (1 - mix);
    float ring;

    a *= baseAmount;

    if (distance > radius + ringWidth)
      a = ImageMath.lerp((distance - (radius + ringWidth)) / falloff, a, 0);

    if (distance < radius - ringWidth || distance > radius + ringWidth)
      ring = 0;
    else {
      ring = Math.abs(distance - radius) / ringWidth;
      ring = 1 - ring * ring * (3 - 2 * ring);
      ring *= ringAmount;
    }

    a += ring;

    float angle = (float) Math.atan2(dx, dy) + ImageMath.PI;
    angle = (ImageMath.mod(angle / ImageMath.PI * 17 + 1.0f + Noise.noise1(angle * 10), 1.0f) - 0.5f) * 2;
    angle = Math.abs(angle);
    angle = (float) Math.pow(angle, 5.0);

    final float b = rayAmount * angle / (1 + distance * 0.1f);
    a += b;

    a = ImageMath.clamp(a, 0, 1);
    return ImageMath.mixColors(a, rgb, color);
  }

  public float getBaseAmount() {
    return baseAmount;
  }

  public Point2D getCentre() {
    return new Point2D.Float(centreX, centreY);
  }

  public int getColor() {
    return color;
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

  public float getRayAmount() {
    return rayAmount;
  }

  public float getRingAmount() {
    return ringAmount;
  }

  public float getRingWidth() {
    return ringWidth;
  }

  public void setBaseAmount(float baseAmount) {
    this.baseAmount = baseAmount;
  }

  public void setCentre(Point2D centre) {
    this.centreX = (float) centre.getX();
    this.centreY = (float) centre.getY();
  }

  public void setColor(int color) {
    this.color = color;
  }

  @Override
  public void setDimensions(int width, int height) {
    icentreX = centreX * width;
    icentreY = centreY * height;
    super.setDimensions(width, height);
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

  public void setRayAmount(float rayAmount) {
    this.rayAmount = rayAmount;
  }

  public void setRingAmount(float ringAmount) {
    this.ringAmount = ringAmount;
  }

  public void setRingWidth(float ringWidth) {
    this.ringWidth = ringWidth;
  }

  @Override
  public String toString() {
    return "Stylize/Flare...";
  }
}
