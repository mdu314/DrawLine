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
import java.awt.image.ColorModel;

/**
 * A filter which flips images or rotates by multiples of 90 degrees.
 */
public class FlipFilter extends AbstractBufferedImageOp {

  /**
   * Rotate the image 180 degrees.
   */
  public static final int FLIP_180 = 6;

  /**
   * Rotate the image 90 degrees counter-clockwise.
   */
  public static final int FLIP_90CCW = 5;

  /**
   * Rotate the image 90 degrees clockwise.
   */
  public static final int FLIP_90CW = 4;

  /**
   * Flip the image horizontally.
   */
  public static final int FLIP_H = 1;

  /**
   * Flip the image horizontally and vertically.
   */
  public static final int FLIP_HV = 3;

  /**
   * Flip the image vertically.
   */
  public static final int FLIP_V = 2;

  private int operation;

  /**
   * Construct a FlipFilter which flips horizontally and vertically.
   */
  public FlipFilter() {
    this(FLIP_HV);
  }

  /**
   * Construct a FlipFilter.
   *
   * @param operation
   *          the filter operation
   */
  public FlipFilter(int operation) {
    this.operation = operation;
  }

  @Override
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    final int width = src.getWidth();
    final int height = src.getHeight();
    src.getType();
    src.getRaster();

    final int[] inPixels = getRGB(src, 0, 0, width, height, null);

    final int w = width;
    final int h = height;

    int newW = w;
    int newH = h;
    switch (operation) {
    case FLIP_H:
      break;
    case FLIP_V:
      break;
    case FLIP_HV:
      newW = h;
      newH = w;
      break;
    case FLIP_90CW:
      newW = h;
      newH = w;
      break;
    case FLIP_90CCW:
      newW = h;
      newH = w;
      break;
    case FLIP_180:
      break;
    }

    final int[] newPixels = new int[newW * newH];

    for (int row = 0; row < h; row++)
      for (int col = 0; col < w; col++) {
        final int index = row * width + col;
        int newRow = row;
        int newCol = col;
        switch (operation) {
        case FLIP_H:
          newCol = w - col - 1;
          break;
        case FLIP_V:
          newRow = h - row - 1;
          break;
        case FLIP_HV:
          newRow = col;
          newCol = row;
          break;
        case FLIP_90CW:
          newRow = col;
          newCol = h - row - 1;
          ;
          break;
        case FLIP_90CCW:
          newRow = w - col - 1;
          newCol = row;
          break;
        case FLIP_180:
          newRow = h - row - 1;
          newCol = w - col - 1;
          break;
        }
        final int newIndex = newRow * newW + newCol;
        newPixels[newIndex] = inPixels[index];
      }

    if (dst == null) {
      final ColorModel dstCM = src.getColorModel();
      dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(newW, newH), dstCM.isAlphaPremultiplied(),
          null);
    }
    dst.getRaster();
    setRGB(dst, 0, 0, newW, newH, newPixels);

    return dst;
  }

  /**
   * Get the filter operation.
   *
   * @return the filter operation
   * @see #setOperation
   */
  public int getOperation() {
    return operation;
  }

  /**
   * Set the filter operation.
   *
   * @param operation
   *          the filter operation
   * @see #getOperation
   */
  public void setOperation(int operation) {
    this.operation = operation;
  }

  @Override
  public String toString() {
    switch (operation) {
    case FLIP_H:
      return "Flip Horizontal";
    case FLIP_V:
      return "Flip Vertical";
    case FLIP_HV:
      return "Flip Diagonal";
    case FLIP_90CW:
      return "Rotate 90";
    case FLIP_90CCW:
      return "Rotate -90";
    case FLIP_180:
      return "Rotate 180";
    }
    return "Flip";
  }
}