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
 * A filter which allows the red, green and blue channels of an image to be
 * mixed into each other.
 */
public class ChannelMixFilter extends PointFilter {

  private int blueGreen, redBlue, greenRed;
  private int intoR, intoG, intoB;

  public ChannelMixFilter() {
    canFilterIndexColorModel = true;
  }

  @Override
  public int filterRGB(int x, int y, int rgb) {
    final int a = rgb & 0xff000000;
    final int r = rgb >> 16 & 0xff;
    final int g = rgb >> 8 & 0xff;
    final int b = rgb & 0xff;
    final int nr = PixelUtils.clamp((intoR * (blueGreen * g + (255 - blueGreen) * b) / 255 + (255 - intoR) * r) / 255);
    final int ng = PixelUtils.clamp((intoG * (redBlue * b + (255 - redBlue) * r) / 255 + (255 - intoG) * g) / 255);
    final int nb = PixelUtils.clamp((intoB * (greenRed * r + (255 - greenRed) * g) / 255 + (255 - intoB) * b) / 255);
    return a | nr << 16 | ng << 8 | nb;
  }

  public int getBlueGreen() {
    return blueGreen;
  }

  public int getGreenRed() {
    return greenRed;
  }

  public int getIntoB() {
    return intoB;
  }

  public int getIntoG() {
    return intoG;
  }

  public int getIntoR() {
    return intoR;
  }

  public int getRedBlue() {
    return redBlue;
  }

  public void setBlueGreen(int blueGreen) {
    this.blueGreen = blueGreen;
  }

  public void setGreenRed(int greenRed) {
    this.greenRed = greenRed;
  }

  public void setIntoB(int intoB) {
    this.intoB = intoB;
  }

  public void setIntoG(int intoG) {
    this.intoG = intoG;
  }

  public void setIntoR(int intoR) {
    this.intoR = intoR;
  }

  public void setRedBlue(int redBlue) {
    this.redBlue = redBlue;
  }

  @Override
  public String toString() {
    return "Colors/Mix Channels...";
  }
}
