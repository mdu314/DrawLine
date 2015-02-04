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
import java.awt.image.BufferedImage;

/**
 * A filter which produces a water ripple distortion.
 */
public class WaterFilter extends TransformFilter {

  private float amplitude = 10;
  private float centreX = 0.5f;
  private float centreY = 0.5f;
  private float icentreX;
  private float icentreY;
  private float phase = 0;

  private float radius = 50;
  private float radius2 = 0;
  private float wavelength = 16;

  public WaterFilter() {
    setEdgeAction(CLAMP);
  }

  @Override
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    icentreX = src.getWidth() * centreX;
    icentreY = src.getHeight() * centreY;
    if (radius == 0)
      radius = Math.min(icentreX, icentreY);
    radius2 = radius * radius;
    return super.filter(src, dst);
  }

  /**
   * Get the amplitude of the ripples.
   *
   * @return the amplitude
   * @see #setAmplitude
   */
  public float getAmplitude() {
    return amplitude;
  }

  /**
   * Get the centre of the effect as a proportion of the image size.
   *
   * @return the center
   * @see #setCentre
   */
  public Point2D getCentre() {
    return new Point2D.Float(centreX, centreY);
  }

  /**
   * Get the centre of the effect in the X direction as a proportion of the
   * image size.
   *
   * @return the center
   * @see #setCentreX
   */
  public float getCentreX() {
    return centreX;
  }

  /**
   * Get the centre of the effect in the Y direction as a proportion of the
   * image size.
   *
   * @return the center
   * @see #setCentreY
   */
  public float getCentreY() {
    return centreY;
  }

  /**
   * Get the phase of the ripples.
   *
   * @return the phase
   * @see #setPhase
   */
  public float getPhase() {
    return phase;
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
   * Get the wavelength of the ripples.
   *
   * @return the wavelength
   * @see #setWavelength
   */
  public float getWavelength() {
    return wavelength;
  }

  boolean inside(int v, int a, int b) {
    return a <= v && v <= b;
  }

  /**
   * Set the amplitude of the ripples.
   *
   * @param amplitude
   *          the amplitude
   * @see #getAmplitude
   */
  public void setAmplitude(float amplitude) {
    this.amplitude = amplitude;
  }

  /**
   * Set the centre of the effect as a proportion of the image size.
   *
   * @param centre
   *          the center
   * @see #getCentre
   */
  public void setCentre(Point2D centre) {
    this.centreX = (float) centre.getX();
    this.centreY = (float) centre.getY();
  }

  /**
   * Set the centre of the effect in the X direction as a proportion of the
   * image size.
   *
   * @param centreX
   *          the center
   * @see #getCentreX
   */
  public void setCentreX(float centreX) {
    this.centreX = centreX;
  }

  /**
   * Set the centre of the effect in the Y direction as a proportion of the
   * image size.
   *
   * @param centreY
   *          the center
   * @see #getCentreY
   */
  public void setCentreY(float centreY) {
    this.centreY = centreY;
  }

  /**
   * Set the phase of the ripples.
   *
   * @param phase
   *          the phase
   * @see #getPhase
   */
  public void setPhase(float phase) {
    this.phase = phase;
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
   * Set the wavelength of the ripples.
   *
   * @param wavelength
   *          the wavelength
   * @see #getWavelength
   */
  public void setWavelength(float wavelength) {
    this.wavelength = wavelength;
  }

  @Override
  public String toString() {
    return "Distort/Water Ripples...";
  }

  @Override
  protected void transformInverse(int x, int y, float[] out) {
    final float dx = x - icentreX;
    final float dy = y - icentreY;
    final float distance2 = dx * dx + dy * dy;
    if (distance2 > radius2) {
      out[0] = x;
      out[1] = y;
    } else {
      final float distance = (float) Math.sqrt(distance2);
      float amount = amplitude * (float) Math.sin(distance / wavelength * ImageMath.TWO_PI - phase);
      amount *= (radius - distance) / radius;
      if (distance != 0)
        amount *= wavelength / distance;
      out[0] = x + dx * amount;
      out[1] = y + dy * amount;
    }
  }

}
