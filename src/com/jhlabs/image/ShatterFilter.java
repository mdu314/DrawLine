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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ShatterFilter extends AbstractBufferedImageOp {
  static class Tile {
    float rotation;
    Shape shape;
    float x, y, vx, vy, w, h;
  }

  private float centreX = 0.5f, centreY = 0.5f;
  private float distance;
  private float endAlpha = 1;
  private int iterations = 5;
  private float rotation;
  private float startAlpha = 1;
  private int tile;
  private float transition;

  private float zoom;

  public ShatterFilter() {
  }

  @Override
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    if (dst == null)
      dst = createCompatibleDestImage(src, null);
    final float width = src.getWidth();
    final float height = src.getHeight();
    final float cx = src.getWidth() * centreX;
    final float cy = src.getHeight() * centreY;
    Math.sqrt(cx * cx + cy * cy);

    // BufferedImage[] tiles = new BufferedImage[iterations];
    final int numTiles = iterations * iterations;
    final Tile[] shapes = new Tile[numTiles];
    final float[] rx = new float[numTiles];
    final float[] ry = new float[numTiles];
    final float[] rz = new float[numTiles];

    final Graphics2D g = dst.createGraphics();
    // g.drawImage( src, null, null );

    final Random random = new Random(0);
    /*
     * for ( int i = 0; i <= numTiles; i++ ) { double angle = (double)i *
     * 2*Math.PI / numTiles; float x = cx + width*(float)Math.cos(angle); float
     * y = cy + height*(float)Math.sin(angle); g.setColor( Color.black );
     * g.setColor( Color.getHSBColor( (float)angle, 1, 1 ) ); if ( i != 0 ) {
     * rz[i-1] = tile*(2*random.nextFloat()-1); ry[i-1] =
     * tile*random.nextFloat(); rz[i-1] = tile*random.nextFloat(); GeneralPath p
     * = new GeneralPath(); p.moveTo( cx, cy ); p.lineTo( lastx, lasty );
     * p.lineTo( x, y ); p.closePath(); shapes[i-1] = p; // Rectangle r =
     * p.getBounds(); // r.intersect( r, new Rectangle( (int)width, (int)height
     * ), r ); } lastx = x; lasty = y; }
     */
    for (int y = 0; y < iterations; y++) {
      final int y1 = (int) height * y / iterations;
      final int y2 = (int) height * (y + 1) / iterations;
      for (int x = 0; x < iterations; x++) {
        final int i = y * iterations + x;
        final int x1 = (int) width * x / iterations;
        final int x2 = (int) width * (x + 1) / iterations;
        rx[i] = tile * random.nextFloat();
        ry[i] = tile * random.nextFloat();
        rx[i] = 0;
        ry[i] = 0;
        rz[i] = tile * (2 * random.nextFloat() - 1);
        final Shape p = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        shapes[i] = new Tile();
        shapes[i].shape = p;
        shapes[i].x = (x1 + x2) * 0.5f;
        shapes[i].y = (y1 + y2) * 0.5f;
        shapes[i].vx = width - (cx - x);
        shapes[i].vy = height - (cy - y);
        shapes[i].w = x2 - x1;
        shapes[i].h = y2 - y1;
      }
    }

    for (int i = 0; i < numTiles; i++) {
      final float h = (float) i / numTiles;
      final double angle = h * 2 * Math.PI;
      float x = transition * width * (float) Math.cos(angle);
      float y = transition * height * (float) Math.sin(angle);

      final Tile tile = shapes[i];
      tile.shape.getBounds();
      final AffineTransform t = g.getTransform();
      x = tile.x + transition * tile.vx;
      y = tile.y + transition * tile.vy;
      g.translate(x, y);
      // g.translate( tile.w*0.5f, tile.h*0.5f );
      g.rotate(transition * rz[i]);
      // g.scale( (float)Math.cos( transition * rx[i] ), (float)Math.cos(
      // transition * ry[i] ) );
      // g.translate( -tile.w*0.5f, -tile.h*0.5f );
      g.setColor(Color.getHSBColor(h, 1, 1));
      final Shape clip = g.getClip();
      g.clip(tile.shape);
      g.drawImage(src, 0, 0, null);
      g.setClip(clip);
      g.setTransform(t);
    }

    g.dispose();
    return dst;
  }

  public Point2D getCentre() {
    return new Point2D.Float(centreX, centreY);
  }

  public float getCentreX() {
    return centreX;
  }

  public float getCentreY() {
    return centreY;
  }

  public float getDistance() {
    return distance;
  }

  public float getEndAlpha() {
    return endAlpha;
  }

  public int getIterations() {
    return iterations;
  }

  public float getRotation() {
    return rotation;
  }

  public float getStartAlpha() {
    return startAlpha;
  }

  public int getTile() {
    return tile;
  }

  public float getTransition() {
    return transition;
  }

  public float getZoom() {
    return zoom;
  }

  public void setCentre(Point2D centre) {
    this.centreX = (float) centre.getX();
    this.centreY = (float) centre.getY();
  }

  public void setCentreX(float centreX) {
    this.centreX = centreX;
  }

  public void setCentreY(float centreY) {
    this.centreY = centreY;
  }

  public void setDistance(float distance) {
    this.distance = distance;
  }

  public void setEndAlpha(float endAlpha) {
    this.endAlpha = endAlpha;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public void setStartAlpha(float startAlpha) {
    this.startAlpha = startAlpha;
  }

  public void setTile(int tile) {
    this.tile = tile;
  }

  public void setTransition(float transition) {
    this.transition = transition;
  }

  public void setZoom(float zoom) {
    this.zoom = zoom;
  }

  @Override
  public String toString() {
    return "Transition/Shatter...";
  }
}
