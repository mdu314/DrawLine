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

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.jhlabs.math.Noise;

public class SkyFilter extends PointFilter {

  private final static float r255 = 1.0f / 255.0f;
  private float amount = 1.0f;
  private float angle = 0.0f;
  private float bias = 0.6f;
  private float cameraAzimuth = 0.0f;
  private float cameraElevation = 0.0f;
  private float cloudCover = 0.5f;
  private float cloudSharpness = 0.5f;
  private float[] exponents;
  private float fov = 1.0f;
  private float gain = 1.0f;
  private float glow = 0.5f;
  private float glowFalloff = 0.5f;
  private float H = 1.0f;
  private float haziness = 0.96f;
  private float lacunarity = 2.0f;
  float mn, mx;
  private float octaves = 8.0f;
  private int operation;
  protected Random random = new Random();
  private float scale = 0.1f;
  private BufferedImage skyColors;
  private float stretch = 1.0f;

  private float sunAzimuth = 0.5f;
  private int sunColor = 0xffffffff;
  private float sunElevation = 0.5f;

  private float sunR, sunG, sunB;
  private float t = 0.0f;
  private float[] tan;
  private float time = 0.3f;

  private float width, height;

  private float windSpeed = 0.0f;

  public SkyFilter() {
    if (skyColors == null)
      skyColors = ImageUtils.createImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("SkyColors.png"))
          .getSource());
  }

  public float evaluate(float x, float y) {
    float value = 0.0f;
    float remainder;
    int i;

    // to prevent "cascading" effects
    x += 371;
    y += 529;

    for (i = 0; i < (int) octaves; i++) {
      value += Noise.noise3(x, y, t) * exponents[i];
      x *= lacunarity;
      y *= lacunarity;
    }

    remainder = octaves - (int) octaves;
    if (remainder != 0)
      value += remainder * Noise.noise3(x, y, t) * exponents[i];

    return value;
  }

  @Override
  public BufferedImage filter(BufferedImage src, BufferedImage dst) {
    final long start = System.currentTimeMillis();
    sunR = (sunColor >> 16 & 0xff) * r255;
    sunG = (sunColor >> 8 & 0xff) * r255;
    sunB = (sunColor & 0xff) * r255;

    mn = 10000;
    mx = -10000;
    exponents = new float[(int) octaves + 1];
    for (int i = 0; i <= (int) octaves; i++)
      exponents[i] = (float) Math.pow(2, -i);

    // min = -1.2f;
    // max = 1.2f;

    width = src.getWidth();
    height = src.getHeight();

    final int h = src.getHeight();
    tan = new float[h];
    for (int i = 0; i < h; i++)
      tan[i] = (float) Math.tan(fov * i / h * Math.PI * 0.5);

    if (dst == null)
      dst = createCompatibleDestImage(src, null);
    final int t = (int) (63 * time);
    // skyPixels = getRGB( skyColors, t, 0, 1, 64, skyPixels );
    final Graphics2D g = dst.createGraphics();
    g.drawImage(skyColors, 0, 0, dst.getWidth(), dst.getHeight(), t, 0, t + 1, 64, null);
    g.dispose();
    super.filter(dst, dst);
    // g.drawRenderedImage( clouds, null );
    // g.dispose();
    final long finish = System.currentTimeMillis();
    System.out.println(mn + " " + mx + " " + (finish - start) * 0.001f);
    exponents = null;
    tan = null;
    return dst;
  }

  @Override
  public int filterRGB(int x, int y, int rgb) {

    // Curvature
    final float fx = x / width;
    // y += 20*Math.sin( fx*Math.PI*0.5 );
    final float fy = y / height;
    final float haze = (float) Math.pow(haziness, 100 * fy * fy);
    // int argb = skyPixels[(int)fy];
    float r = (rgb >> 16 & 0xff) * r255;
    float g = (rgb >> 8 & 0xff) * r255;
    float b = (rgb & 0xff) * r255;

    final float cx = width * 0.5f;
    float nx = x - cx;
    float ny = y;
    // FOV
    // ny = (float)Math.tan( fov * fy * Math.PI * 0.5 );
    ny = tan[y];
    nx = (fx - 0.5f) * (1 + ny);
    ny += t * windSpeed;// Wind towards the camera

    // float xscale = scale/(1+y*bias*0.1f);
    nx /= scale;
    ny /= scale * stretch;
    float f = evaluate(nx, ny);
    f = (f + 1.23f) / 2.46f;

    int v;

    // Work out cloud cover
    float c = f - cloudCover;
    if (c < 0)
      c = 0;

    float cloudAlpha = 1 - (float) Math.pow(cloudSharpness, c);
    // cloudAlpha *= amount;
    // if ( cloudAlpha > 1 )
    // cloudAlpha = 1;
    mn = Math.min(mn, cloudAlpha);
    mx = Math.max(mx, cloudAlpha);

    // Sun glow
    final float centreX = width * sunAzimuth;
    final float centreY = height * sunElevation;
    final float dx = x - centreX;
    final float dy = y - centreY;
    float distance2 = dx * dx + dy * dy;
    // float sun = 0;
    // distance2 = (float)Math.sqrt(distance2);
    distance2 = (float) Math.pow(distance2, glowFalloff);
    final float sun = /* amount* */10 * (float) Math.exp(-distance2 * glow * 0.1f);
    // sun = glow*10*(float)Math.exp(-distance2);

    // Sun glow onto sky
    r += sun * sunR;
    g += sun * sunG;
    b += sun * sunB;

    // float cloudColor = cloudAlpha *sun;
    // Bump map
    /*
     * float nnx = x-cx; float nny = y-1; nnx /= xscale; nny /= xscale *
     * stretch; float gf = evaluate(nnx, nny); float gradient = fg-gf; if (y ==
     * 100)System.out.println(fg+" "+gf+gradient); cloudColor += amount *
     * gradient;
     */
    // ...
    /*
     * r += (cloudColor-r) * cloudAlpha; g += (cloudColor-g) * cloudAlpha; b +=
     * (cloudColor-b) * cloudAlpha;
     */
    // Clouds get darker as they get thicker
    final float ca = (1 - cloudAlpha * cloudAlpha * cloudAlpha * cloudAlpha)/**
     *
     * 
     * (1 + sun)
     */
    * amount;
    final float cloudR = sunR * ca;
    final float cloudG = sunG * ca;
    final float cloudB = sunB * ca;

    // Apply the haziness as we move further away
    cloudAlpha *= haze;

    // Composite the clouds on the sky
    final float iCloudAlpha = 1 - cloudAlpha;
    r = iCloudAlpha * r + cloudAlpha * cloudR;
    g = iCloudAlpha * g + cloudAlpha * cloudG;
    b = iCloudAlpha * b + cloudAlpha * cloudB;

    // Exposure
    final float exposure = gain;
    r = 1 - (float) Math.exp(-r * exposure);
    g = 1 - (float) Math.exp(-g * exposure);
    b = 1 - (float) Math.exp(-b * exposure);

    final int ir = (int) (255 * r) << 16;
    final int ig = (int) (255 * g) << 8;
    final int ib = (int) (255 * b);
    v = 0xff000000 | ir | ig | ib;
    return v;
  }

  public float getAmount() {
    return amount;
  }

  public float getAngle() {
    return angle;
  }

  public float getBias() {
    return bias;
  }

  public float getCameraAzimuth() {
    return cameraAzimuth;
  }

  public float getCameraElevation() {
    return cameraElevation;
  }

  public float getCloudCover() {
    return cloudCover;
  }

  public float getCloudSharpness() {
    return cloudSharpness;
  }

  public float getFOV() {
    return fov;
  }

  public float getGain() {
    return gain;
  }

  public float getGlow() {
    return glow;
  }

  public float getGlowFalloff() {
    return glowFalloff;
  }

  public float getH() {
    return H;
  }

  public float getHaziness() {
    return haziness;
  }

  public float getLacunarity() {
    return lacunarity;
  }

  public float getOctaves() {
    return octaves;
  }

  public int getOperation() {
    return operation;
  }

  public float getScale() {
    return scale;
  }

  public float getStretch() {
    return stretch;
  }

  public float getSunAzimuth() {
    return sunAzimuth;
  }

  public int getSunColor() {
    return sunColor;
  }

  public float getSunElevation() {
    return sunElevation;
  }

  public float getT() {
    return t;
  }

  public float getTime() {
    return time;
  }

  public float getWindSpeed() {
    return windSpeed;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  public void setAngle(float angle) {
    this.angle = angle;
  }

  public void setBias(float bias) {
    this.bias = bias;
  }

  public void setCameraAzimuth(float cameraAzimuth) {
    this.cameraAzimuth = cameraAzimuth;
  }

  public void setCameraElevation(float cameraElevation) {
    this.cameraElevation = cameraElevation;
  }

  public void setCloudCover(float cloudCover) {
    this.cloudCover = cloudCover;
  }

  public void setCloudSharpness(float cloudSharpness) {
    this.cloudSharpness = cloudSharpness;
  }

  public void setFOV(float fov) {
    this.fov = fov;
  }

  public void setGain(float gain) {
    this.gain = gain;
  }

  public void setGlow(float glow) {
    this.glow = glow;
  }

  public void setGlowFalloff(float glowFalloff) {
    this.glowFalloff = glowFalloff;
  }

  public void setH(float H) {
    this.H = H;
  }

  public void setHaziness(float haziness) {
    this.haziness = haziness;
  }

  public void setLacunarity(float lacunarity) {
    this.lacunarity = lacunarity;
  }

  public void setOctaves(float octaves) {
    this.octaves = octaves;
  }

  public void setOperation(int operation) {
    this.operation = operation;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }

  public void setStretch(float stretch) {
    this.stretch = stretch;
  }

  public void setSunAzimuth(float sunAzimuth) {
    this.sunAzimuth = sunAzimuth;
  }

  public void setSunColor(int sunColor) {
    this.sunColor = sunColor;
  }

  public void setSunElevation(float sunElevation) {
    this.sunElevation = sunElevation;
  }

  public void setT(float t) {
    this.t = t;
  }

  public void setTime(float time) {
    this.time = time;
  }

  public void setWindSpeed(float windSpeed) {
    this.windSpeed = windSpeed;
  }

  @Override
  public String toString() {
    return "Texture/Sky...";
  }

}
