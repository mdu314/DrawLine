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
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.util.Vector;

import com.jhlabs.math.Function2D;
import com.jhlabs.math.ImageFunction2D;
import com.jhlabs.vecmath.Color4f;
import com.jhlabs.vecmath.Vector3f;

/**
 * A filter which produces lighting and embossing effects.
 */
public class LightFilter extends WholeImageFilter {

  public class AmbientLight extends Light {
    @Override
    public String toString() {
      return "Ambient Light";
    }
  }

  public class DistantLight extends Light {
    public DistantLight() {
      type = DISTANT;
    }

    @Override
    public String toString() {
      return "Distant Light";
    }
  }

  /**
   * A class representing a light.
   */
  public static class Light implements Cloneable {

    float azimuth;
    float centreX = 0.5f, centreY = 0.5f;
    int color = 0xffffffff;
    float coneAngle = ImageMath.PI / 6;
    float cosConeAngle;
    Vector3f direction;
    float distance = 100.0f;
    float elevation;
    float focus = 0.5f;
    float intensity;
    Vector3f position;
    Color4f realColor = new Color4f();
    int type = AMBIENT;

    public Light() {
      this(270 * ImageMath.PI / 180.0f, 0.5235987755982988f, 1.0f);
    }

    public Light(float azimuth, float elevation, float intensity) {
      this.azimuth = azimuth;
      this.elevation = elevation;
      this.intensity = intensity;
    }

    @Override
    public Object clone() {
      try {
        final Light copy = (Light) super.clone();
        return copy;
      } catch (final CloneNotSupportedException e) {
        return null;
      }
    }

    public float getAzimuth() {
      return azimuth;
    }

    /**
     * Get the centre of the light in the X direction as a proportion of the
     * image size.
     *
     * @return the center
     * @see #setCentreX
     */
    public float getCentreX() {
      return centreX;
    }

    /**
     * Get the centre of the light in the Y direction as a proportion of the
     * image size.
     *
     * @return the center
     * @see #setCentreY
     */
    public float getCentreY() {
      return centreY;
    }

    public int getColor() {
      return color;
    }

    public float getConeAngle() {
      return coneAngle;
    }

    public float getDistance() {
      return distance;
    }

    public float getElevation() {
      return elevation;
    }

    public float getFocus() {
      return focus;
    }

    public float getIntensity() {
      return intensity;
    }

    /**
     * Prepare the light for rendering.
     *
     * @param width
     *          the output image width
     * @param height
     *          the output image height
     */
    public void prepare(int width, int height) {
      float lx = (float) (Math.cos(azimuth) * Math.cos(elevation));
      float ly = (float) (Math.sin(azimuth) * Math.cos(elevation));
      float lz = (float) Math.sin(elevation);
      direction = new Vector3f(lx, ly, lz);
      direction.normalize();
      if (type != DISTANT) {
        lx *= distance;
        ly *= distance;
        lz *= distance;
        lx += width * centreX;
        ly += height * centreY;
      }
      position = new Vector3f(lx, ly, lz);
      realColor.set(new Color(color));
      realColor.scale(intensity);
      cosConeAngle = (float) Math.cos(coneAngle);
    }

    public void setAzimuth(float azimuth) {
      this.azimuth = azimuth;
    }

    /**
     * Set the centre of the light in the X direction as a proportion of the
     * image size.
     *
     * @param centreX
     *          the center
     * @see #getCentreX
     */
    public void setCentreX(float x) {
      centreX = x;
    }

    /**
     * Set the centre of the light in the Y direction as a proportion of the
     * image size.
     *
     * @param centreY
     *          the center
     * @see #getCentreY
     */
    public void setCentreY(float y) {
      centreY = y;
    }

    public void setColor(int color) {
      this.color = color;
    }

    public void setConeAngle(float coneAngle) {
      this.coneAngle = coneAngle;
    }

    public void setDistance(float distance) {
      this.distance = distance;
    }

    public void setElevation(float elevation) {
      this.elevation = elevation;
    }

    public void setFocus(float focus) {
      this.focus = focus;
    }

    public void setIntensity(float intensity) {
      this.intensity = intensity;
    }

    @Override
    public String toString() {
      return "Light";
    }

  }

  /**
   * A class representing material properties.
   */
  public static class Material {
    float ambientIntensity;
    int diffuseColor;
    float diffuseReflectivity;
    float highlight;
    float opacity = 1;
    float reflectivity;
    int specularColor;
    float specularReflectivity;

    public Material() {
      ambientIntensity = 0.5f;
      diffuseReflectivity = 1.0f;
      specularReflectivity = 1.0f;
      highlight = 3.0f;
      reflectivity = 0.0f;
      diffuseColor = 0xff888888;
      specularColor = 0xffffffff;
    }

    public int getDiffuseColor() {
      return diffuseColor;
    }

    public float getOpacity() {
      return opacity;
    }

    public void setDiffuseColor(int diffuseColor) {
      this.diffuseColor = diffuseColor;
    }

    public void setOpacity(float opacity) {
      this.opacity = opacity;
    }

  }

  public class PointLight extends Light {
    public PointLight() {
      type = POINT;
    }

    @Override
    public String toString() {
      return "Point Light";
    }
  }

  public class SpotLight extends Light {
    public SpotLight() {
      type = SPOT;
    }

    @Override
    public String toString() {
      return "Spotlight";
    }
  }

  public final static int AMBIENT = 0;
  /**
   * Use a custom function as the bump map.
   */
  public final static int BUMPS_FROM_BEVEL = 3;
  /**
   * Use the input image brightness as the bump map.
   */
  public final static int BUMPS_FROM_IMAGE = 0;
  /**
   * Use the input image alpha as the bump map.
   */
  public final static int BUMPS_FROM_IMAGE_ALPHA = 1;
  /**
   * Use a separate image alpha channel as the bump map.
   */
  public final static int BUMPS_FROM_MAP = 2;
  /**
   * Use constant material color.
   */
  public final static int COLORS_CONSTANT = 1;
  /**
   * Take the output colors from the input image.
   */
  public final static int COLORS_FROM_IMAGE = 0;
  public final static int DISTANT = 1;
  public final static int POINT = 2;
  protected final static float r255 = 1.0f / 255.0f;
  public final static int SPOT = 3;
  private Function2D bumpFunction;

  private float bumpHeight;
  private int bumpShape;
  private float bumpSoftness;
  private int bumpSource = BUMPS_FROM_IMAGE;
  private int colorSource = COLORS_FROM_IMAGE;
  private final Color4f diffuse_color;
  private Image environmentMap;

  private int[] envPixels;

  private int envWidth = 1, envHeight = 1;

  // Temporary variables used to avoid per-pixel memory allocation while
  // filtering
  private final Vector3f l;

  private final Vector<Light> lights;

  Material material;

  private final Vector3f n;

  private final Color4f shadedColor;

  private final Color4f specular_color;

  private final Vector3f tmpv, tmpv2;

  private final Vector3f v;

  private float viewDistance = 10000.0f;

  public LightFilter() {
    lights = new Vector<Light>();
    addLight(new DistantLight());
    bumpHeight = 1.0f;
    bumpSoftness = 5.0f;
    bumpShape = 0;
    material = new Material();
    l = new Vector3f();
    v = new Vector3f();
    n = new Vector3f();
    shadedColor = new Color4f();
    diffuse_color = new Color4f();
    specular_color = new Color4f();
    tmpv = new Vector3f();
    tmpv2 = new Vector3f();
  }

  public void addLight(Light light) {
    lights.addElement(light);
  }

  @Override
  protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
    int index = 0;
    final int[] outPixels = new int[width * height];
    final float width45 = Math.abs(6.0f * bumpHeight);
    final boolean invertBumps = bumpHeight < 0;
    final Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
    final Vector3f viewpoint = new Vector3f(width / 2.0f, height / 2.0f, viewDistance);
    final Vector3f normal = new Vector3f();
    final Color4f envColor = new Color4f();
    final Color4f diffuseColor = new Color4f(new Color(material.diffuseColor));
    final Color4f specularColor = new Color4f(new Color(material.specularColor));
    Function2D bump = bumpFunction;

    // Apply the bump softness
    if (bumpSource == BUMPS_FROM_IMAGE || bumpSource == BUMPS_FROM_IMAGE_ALPHA || bumpSource == BUMPS_FROM_MAP
        || bump == null)
      if (bumpSoftness != 0) {
        int bumpWidth = width;
        int bumpHeight = height;
        int[] bumpPixels = inPixels;
        if (bumpSource == BUMPS_FROM_MAP && bumpFunction instanceof ImageFunction2D) {
          final ImageFunction2D if2d = (ImageFunction2D) bumpFunction;
          bumpWidth = if2d.getWidth();
          bumpHeight = if2d.getHeight();
          bumpPixels = if2d.getPixels();
        }
        final int[] tmpPixels = new int[bumpWidth * bumpHeight];
        final int[] softPixels = new int[bumpWidth * bumpHeight];
        /*
         * for (int i = 0; i < 3; i++ ) { BoxBlurFilter.blur( bumpPixels,
         * tmpPixels, bumpWidth, bumpHeight, (int)bumpSoftness );
         * BoxBlurFilter.blur( tmpPixels, softPixels, bumpHeight, bumpWidth,
         * (int)bumpSoftness ); }
         */
        final Kernel kernel = GaussianFilter.makeKernel(bumpSoftness);
        GaussianFilter.convolveAndTranspose(kernel, bumpPixels, tmpPixels, bumpWidth, bumpHeight, true, false, false,
            ConvolveFilter.WRAP_EDGES);
        GaussianFilter.convolveAndTranspose(kernel, tmpPixels, softPixels, bumpHeight, bumpWidth, true, false, false,
            ConvolveFilter.WRAP_EDGES);
        bump = new ImageFunction2D(softPixels, bumpWidth, bumpHeight, ImageFunction2D.CLAMP,
            bumpSource == BUMPS_FROM_IMAGE_ALPHA);
        final Function2D bbump = bump;
        if (bumpShape != 0)
          bump = new Function2D() {
            private final Function2D original = bbump;

            @Override
            public float evaluate(float x, float y) {
              float v = original.evaluate(x, y);
              switch (bumpShape) {
              case 1:
                // v = v > 0.5f ? 0.5f : v;
                v *= ImageMath.smoothStep(0.45f, 0.55f, v);
                break;
              case 2:
                v = v < 0.5f ? 0.5f : v;
                break;
              case 3:
                v = ImageMath.triangle(v);
                break;
              case 4:
                v = ImageMath.circleDown(v);
                break;
              case 5:
                v = ImageMath.gain(v, 0.75f);
                break;
              }
              return v;
            }
          };
      } else if (bumpSource != BUMPS_FROM_MAP)
        bump = new ImageFunction2D(inPixels, width, height, ImageFunction2D.CLAMP, bumpSource == BUMPS_FROM_IMAGE_ALPHA);

    final float reflectivity = material.reflectivity;
    final float areflectivity = 1 - reflectivity;
    final Vector3f v1 = new Vector3f();
    final Vector3f v2 = new Vector3f();
    final Vector3f n = new Vector3f();
    final Light[] lightsArray = new Light[lights.size()];
    lights.copyInto(lightsArray);
    for (final Light element : lightsArray)
      element.prepare(width, height);

    final float[][] heightWindow = new float[3][width];
    for (int x = 0; x < width; x++)
      heightWindow[1][x] = width45 * bump.evaluate(x, 0);

    // Loop through each source pixel
    for (int y = 0; y < height; y++) {
      final boolean y0 = y > 0;
      final boolean y1 = y < height - 1;
      position.y = y;
      for (int x = 0; x < width; x++)
        heightWindow[2][x] = width45 * bump.evaluate(x, y + 1);
      for (int x = 0; x < width; x++) {
        final boolean x0 = x > 0;
        final boolean x1 = x < width - 1;

        // Calculate the normal at this point
        if (bumpSource != BUMPS_FROM_BEVEL) {
          // Complicated and slower method
          // Calculate four normals using the gradients in +/- X/Y directions
          int count = 0;
          normal.x = normal.y = normal.z = 0;
          final float m0 = heightWindow[1][x];
          final float m1 = x0 ? heightWindow[1][x - 1] - m0 : 0;
          final float m2 = y0 ? heightWindow[0][x] - m0 : 0;
          final float m3 = x1 ? heightWindow[1][x + 1] - m0 : 0;
          final float m4 = y1 ? heightWindow[2][x] - m0 : 0;

          if (x0 && y1) {
            v1.x = -1.0f;
            v1.y = 0.0f;
            v1.z = m1;
            v2.x = 0.0f;
            v2.y = 1.0f;
            v2.z = m4;
            n.cross(v1, v2);
            n.normalize();
            if (n.z < 0.0)
              n.z = -n.z;
            normal.add(n);
            count++;
          }

          if (x0 && y0) {
            v1.x = -1.0f;
            v1.y = 0.0f;
            v1.z = m1;
            v2.x = 0.0f;
            v2.y = -1.0f;
            v2.z = m2;
            n.cross(v1, v2);
            n.normalize();
            if (n.z < 0.0)
              n.z = -n.z;
            normal.add(n);
            count++;
          }

          if (y0 && x1) {
            v1.x = 0.0f;
            v1.y = -1.0f;
            v1.z = m2;
            v2.x = 1.0f;
            v2.y = 0.0f;
            v2.z = m3;
            n.cross(v1, v2);
            n.normalize();
            if (n.z < 0.0)
              n.z = -n.z;
            normal.add(n);
            count++;
          }

          if (x1 && y1) {
            v1.x = 1.0f;
            v1.y = 0.0f;
            v1.z = m3;
            v2.x = 0.0f;
            v2.y = 1.0f;
            v2.z = m4;
            n.cross(v1, v2);
            n.normalize();
            if (n.z < 0.0)
              n.z = -n.z;
            normal.add(n);
            count++;
          }

          // Average the four normals
          normal.x /= count;
          normal.y /= count;
          normal.z /= count;
        }
        if (invertBumps) {
          normal.x = -normal.x;
          normal.y = -normal.y;
        }
        position.x = x;

        if (normal.z >= 0) {
          // Get the material colour at this point
          if (colorSource == COLORS_FROM_IMAGE)
            setFromRGB(diffuseColor, inPixels[index]);
          else
            setFromRGB(diffuseColor, material.diffuseColor);
          if (reflectivity != 0 && environmentMap != null) {
            // FIXME-too much normalizing going on here
            tmpv2.set(viewpoint);
            tmpv2.sub(position);
            tmpv2.normalize();
            tmpv.set(normal);
            tmpv.normalize();

            // Reflect
            tmpv.scale(2.0f * tmpv.dot(tmpv2));
            tmpv.sub(v);

            tmpv.normalize();
            setFromRGB(envColor, getEnvironmentMap(tmpv, inPixels, width, height));// FIXME-interpolate()
            diffuseColor.x = reflectivity * envColor.x + areflectivity * diffuseColor.x;
            diffuseColor.y = reflectivity * envColor.y + areflectivity * diffuseColor.y;
            diffuseColor.z = reflectivity * envColor.z + areflectivity * diffuseColor.z;
          }
          // Shade the pixel
          final Color4f c = phongShade(position, viewpoint, normal, diffuseColor, specularColor, material, lightsArray);
          final int alpha = inPixels[index] & 0xff000000;
          final int rgb = (int) (c.x * 255) << 16 | (int) (c.y * 255) << 8 | (int) (c.z * 255);
          outPixels[index++] = alpha | rgb;
        } else
          outPixels[index++] = 0;
      }
      final float[] t = heightWindow[0];
      heightWindow[0] = heightWindow[1];
      heightWindow[1] = heightWindow[2];
      heightWindow[2] = t;
    }
    return outPixels;
  }

  public Function2D getBumpFunction() {
    return bumpFunction;
  }

  public float getBumpHeight() {
    return bumpHeight;
  }

  public int getBumpShape() {
    return bumpShape;
  }

  public float getBumpSoftness() {
    return bumpSoftness;
  }

  public int getBumpSource() {
    return bumpSource;
  }

  public int getColorSource() {
    return colorSource;
  }

  public int getDiffuseColor() {
    return material.diffuseColor;
  }

  public Image getEnvironmentMap() {
    return environmentMap;
  }

  private int getEnvironmentMap(Vector3f normal, int[] inPixels, int width, int height) {
    if (environmentMap != null) {
      final float angle = (float) Math.acos(-normal.y);

      float x, y;
      y = angle / ImageMath.PI;

      if (y == 0.0f || y == 1.0f)
        x = 0.0f;
      else {
        float f = normal.x / (float) Math.sin(angle);

        if (f > 1.0f)
          f = 1.0f;
        else if (f < -1.0f)
          f = -1.0f;

        x = (float) Math.acos(f) / ImageMath.PI;
      }
      // A bit of empirical scaling....
      x = ImageMath.clamp(x * envWidth, 0, envWidth - 1);
      y = ImageMath.clamp(y * envHeight, 0, envHeight - 1);
      final int ix = (int) x;
      final int iy = (int) y;

      final float xWeight = x - ix;
      final float yWeight = y - iy;
      final int i = envWidth * iy + ix;
      final int dx = ix == envWidth - 1 ? 0 : 1;
      final int dy = iy == envHeight - 1 ? 0 : envWidth;
      return ImageMath.bilinearInterpolate(xWeight, yWeight, envPixels[i], envPixels[i + dx], envPixels[i + dy],
          envPixels[i + dx + dy]);
    }
    return 0;
  }

  public Vector<Light> getLights() {
    return lights;
  }

  public Material getMaterial() {
    return material;
  }

  public float getViewDistance() {
    return viewDistance;
  }

  protected Color4f phongShade(Vector3f position, Vector3f viewpoint, Vector3f normal, Color4f diffuseColor,
      Color4f specularColor, Material material, Light[] lightsArray) {
    shadedColor.set(diffuseColor);
    shadedColor.scale(material.ambientIntensity);

    for (final Light light : lightsArray) {
      n.set(normal);
      l.set(light.position);
      if (light.type != DISTANT)
        l.sub(position);
      l.normalize();
      float nDotL = n.dot(l);
      if (nDotL >= 0.0) {
        float dDotL = 0;

        v.set(viewpoint);
        v.sub(position);
        v.normalize();

        // Spotlight
        if (light.type == SPOT) {
          dDotL = light.direction.dot(l);
          if (dDotL < light.cosConeAngle)
            continue;
        }

        n.scale(2.0f * nDotL);
        n.sub(l);
        final float rDotV = n.dot(v);

        float rv;
        if (rDotV < 0.0)
          rv = 0.0f;
        else
          // rv = (float)Math.pow(rDotV, material.highlight);
          rv = rDotV / (material.highlight - material.highlight * rDotV + rDotV); // Fast
        // approximation
        // to
        // pow

        // Spotlight
        if (light.type == SPOT) {
          dDotL = light.cosConeAngle / dDotL;
          float e = dDotL;
          e *= e;
          e *= e;
          e *= e;
          e = (float) Math.pow(dDotL, light.focus * 10) * (1 - e);
          rv *= e;
          nDotL *= e;
        }

        diffuse_color.set(diffuseColor);
        diffuse_color.scale(material.diffuseReflectivity);
        diffuse_color.x *= light.realColor.x * nDotL;
        diffuse_color.y *= light.realColor.y * nDotL;
        diffuse_color.z *= light.realColor.z * nDotL;
        specular_color.set(specularColor);
        specular_color.scale(material.specularReflectivity);
        specular_color.x *= light.realColor.x * rv;
        specular_color.y *= light.realColor.y * rv;
        specular_color.z *= light.realColor.z * rv;
        diffuse_color.add(specular_color);
        diffuse_color.clamp(0, 1);
        shadedColor.add(diffuse_color);
      }
    }
    shadedColor.clamp(0, 1);
    return shadedColor;
  }

  public void removeLight(Light light) {
    lights.removeElement(light);
  }

  public void setBumpFunction(Function2D bumpFunction) {
    this.bumpFunction = bumpFunction;
  }

  public void setBumpHeight(float bumpHeight) {
    this.bumpHeight = bumpHeight;
  }

  public void setBumpShape(int bumpShape) {
    this.bumpShape = bumpShape;
  }

  public void setBumpSoftness(float bumpSoftness) {
    this.bumpSoftness = bumpSoftness;
  }

  public void setBumpSource(int bumpSource) {
    this.bumpSource = bumpSource;
  }

  public void setColorSource(int colorSource) {
    this.colorSource = colorSource;
  }

  public void setDiffuseColor(int diffuseColor) {
    material.diffuseColor = diffuseColor;
  }

  public void setEnvironmentMap(BufferedImage environmentMap) {
    this.environmentMap = environmentMap;
    if (environmentMap != null) {
      envWidth = environmentMap.getWidth();
      envHeight = environmentMap.getHeight();
      envPixels = getRGB(environmentMap, 0, 0, envWidth, envHeight, null);
    } else {
      envWidth = envHeight = 1;
      envPixels = null;
    }
  }

  protected void setFromRGB(Color4f c, int argb) {
    c.set((argb >> 16 & 0xff) * r255, (argb >> 8 & 0xff) * r255, (argb & 0xff) * r255, (argb >> 24 & 0xff) * r255);
  }

  public void setMaterial(Material material) {
    this.material = material;
  }

  public void setViewDistance(float viewDistance) {
    this.viewDistance = viewDistance;
  }

  @Override
  public String toString() {
    return "Stylize/Light Effects...";
  }
}