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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ScratchFilter extends AbstractBufferedImageOp {
  private float angle;
  private float angleVariation = 1.0f;
  private int color = 0xffffffff;
  private float density = 0.1f;
  private float length = 0.5f;
  private int seed = 0;
  private float width = 0.5f;

  public ScratchFilter() {
  }

  @Override
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    if (dst == null)
      dst = createCompatibleDestImage(src, null);

    final int width = src.getWidth();
    final int height = src.getHeight();
    final int numScratches = (int) (density * width * height / 100);
    final float l = length * width;
    final Random random = new Random(seed);
    final Graphics2D g = dst.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(new Color(color));
    g.setStroke(new BasicStroke(this.width));
    for (int i = 0; i < numScratches; i++) {
      final float x = width * random.nextFloat();
      final float y = height * random.nextFloat();
      final float a = angle + ImageMath.TWO_PI * (angleVariation * (random.nextFloat() - 0.5f));
      final float s = (float) Math.sin(a) * l;
      final float c = (float) Math.cos(a) * l;
      final float x1 = x - c;
      final float y1 = y - s;
      final float x2 = x + c;
      final float y2 = y + s;
      g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
    }
    g.dispose();

    return dst;
  }

  public float getAngle() {
    return angle;
  }

  public float getAngleVariation() {
    return angleVariation;
  }

  public int getColor() {
    return color;
  }

  public float getDensity() {
    return density;
  }

  public float getLength() {
    return length;
  }

  public int getSeed() {
    return seed;
  }

  public float getWidth() {
    return width;
  }

  public void setAngle(float angle) {
    this.angle = angle;
  }

  public void setAngleVariation(float angleVariation) {
    this.angleVariation = angleVariation;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public void setDensity(float density) {
    this.density = density;
  }

  public void setLength(float length) {
    this.length = length;
  }

  public void setSeed(int seed) {
    this.seed = seed;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  @Override
  public String toString() {
    return "Render/Scratches...";
  }
}
