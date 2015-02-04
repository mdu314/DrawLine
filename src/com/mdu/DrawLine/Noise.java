package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class Noise {

  static float[][] TheNoise;

  static void InitTheNoise(int width, int height, Hashtable<String, Object> hints) {
    float[][] noise = Noise.GenerateWhiteNoise(width, height);
    int octaves;
    float persistence;
    if (hints != null) {
      octaves = (int) hints.get("octaves");
      persistence = (float) hints.get("persistence");
    } else {
      octaves = 3;
      persistence = 0.5f;
    }
    TheNoise = Noise.GeneratePerlinNoise(noise, octaves, persistence);
  }

  static float GetNoise(int x, int y) {
    if (TheNoise == null) {
      throw new RuntimeException("Noise not inited");
    }
    return TheNoise[x][y];
  }

  static float GetNoise(int x) {
    if (TheNoise == null) {
      throw new RuntimeException("Noise not inited");
    }
    return TheNoise[x][0];
  }

  static float[][] GenerateGaussNoise(int width, int height) {
    final float[][] noise = new float[width][height];
    final float cx = width / 2;
    final float cy = height / 2;
    final double dmax = (cx + cy) / 2;
    for (int x = 0; x < width; x++)
      for (int y = 0; y < height; y++) {
        final double dx = x - cx;
        final double dy = y - cy;
        final float d = (float) (dmax - Math.sqrt(dx * dx + dy * dy));
        final float n = DLUtil.RandomGauss(0, d);
        noise[x][y] = n;
      }
    return noise;
  }

  static float[][] GeneratePerlinNoise(float[][] baseNoise, int octaveCount, float persistance) {
    final int width = baseNoise.length;
    final int height = baseNoise[0].length;

    final float[][][] smoothNoise = new float[octaveCount][width][height]; // an
                                                                           // array
    // of 2D
    // arrays containing

    // generate smooth noise
    for (int i = 0; i < octaveCount; i++)
      smoothNoise[i] = GenerateSmoothNoise(baseNoise, i);

    final float[][] perlinNoise = new float[width][height];
    float amplitude = 1.0f;
    float totalAmplitude = 0.0f;

    // blend noise together
    for (int octave = octaveCount - 1; octave >= 0; octave--) {
      amplitude *= persistance;
      totalAmplitude += amplitude;

      for (int i = 0; i < width; i++)
        for (int j = 0; j < height; j++)
          perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
    }

    // normalisation
    for (int i = 0; i < width; i++)
      for (int j = 0; j < height; j++)
        perlinNoise[i][j] /= totalAmplitude;

    return perlinNoise;
  }

  static float[][] GenerateSmoothNoise(float[][] baseNoise, int octave) {
    final int width = baseNoise.length;
    final int height = baseNoise[0].length;

    final float[][] smoothNoise = new float[width][height];

    final int samplePeriod = 1 << octave; // calculates 2 ^ k
    final float sampleFrequency = 1.0f / samplePeriod;

    for (int i = 0; i < width; i++) {
      // calculate the horizontal sampling indices
      final int sample_i0 = i / samplePeriod * samplePeriod;
      final int sample_i1 = (sample_i0 + samplePeriod) % width; // wrap around
      final float horizontal_blend = (i - sample_i0) * sampleFrequency;

      for (int j = 0; j < height; j++) {
        // calculate the vertical sampling indices
        final int sample_j0 = j / samplePeriod * samplePeriod;
        final int sample_j1 = (sample_j0 + samplePeriod) % height; // wrap
                                                                   // around
        final float vertical_blend = (j - sample_j0) * sampleFrequency;

        // blend the top two corners
        final float top = Interpolate(baseNoise[sample_i0][sample_j0], baseNoise[sample_i1][sample_j0],
            horizontal_blend);

        // blend the bottom two corners
        final float bottom = Interpolate(baseNoise[sample_i0][sample_j1], baseNoise[sample_i1][sample_j1],
            horizontal_blend);

        // final blend
        smoothNoise[i][j] = Interpolate(top, bottom, vertical_blend);
      }
    }
    return smoothNoise;
  }

  static float[][] GenerateWhiteNoise(int width, int height) {
    final float[][] noise = new float[width][height];
    for (int x = 0; x < width; x++)
      for (int y = 0; y < height; y++)
        noise[x][y] = (float) Math.random();
    return noise;
  }

  static Color GetColor(Color gradientStart, Color gradientEnd, float t) {
    final float u = 1 - t;

    final Color color = new Color((int) (gradientStart.getRed() * u + gradientEnd.getRed() * t),
        (int) (gradientStart.getGreen() * u + gradientEnd.getGreen() * t),
        (int) (gradientStart.getBlue() * u + gradientEnd.getBlue() * t),
        (int) (gradientStart.getAlpha() * u + gradientEnd.getAlpha() * t));
    return color;
  }

  static Color[][] GetEmptyColorArray(int width, int height) {
    return new Color[width][height];
  }

  static float Interpolate(float x0, float x1, float alpha) {
    return x0 * (1 - alpha) + alpha * x1;
  }

  static Color[][] MapGradient(Color gradientStart, Color gradientEnd, float[][] perlinNoise) {
    final int width = perlinNoise.length;
    final int height = perlinNoise[0].length;

    final Color[][] image = GetEmptyColorArray(width, height);

    for (int i = 0; i < width; i++)
      for (int j = 0; j < height; j++)
        image[i][j] = GetColor(gradientStart, gradientEnd, perlinNoise[i][j]);

    return image;
  }

  static float[][] Normalize(float[][] in, float min, float max) {
    final int width = in.length;
    final int height = in[0].length;
    float m = Float.MAX_VALUE;
    float M = Float.MIN_VALUE;
    for (int x = 0; x < width; x++)
      for (int y = 0; y < height; y++) {
        final float v = in[x][y];
        if (v < m)
          m = v;
        if (v > M)
          M = v;
      }

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        final float v = in[x][y];
        in[x][y] = DLUtil.Normalize(0, 1, m, M, v);
      }
    }
    return in;
  }

  static BufferedImage Rescale(BufferedImage image, float scale, float offset) {
    final int w = image.getWidth();
    final int h = image.getHeight();
    for (int i = 0; i < w; i++)
      for (int j = 0; j < h; j++) {
        final int p = image.getRGB(i, j);
        final int alpha = (p & 0xff000000) >> 24 & 0xff;
        int red = (p & 0xff0000) >> 16 & 0xff;
        int green = (p & 0xff00) >> 8 & 0xff;
        int blue = p & 0xff;
        red = (int) (red * scale + offset + 0.5);
        if (red > 255)
          red = 255;
        green = (int) (green * scale + offset + 0.5);
        if (green > 255)
          green = 255;
        blue = (int) (blue * scale + offset + 0.5);
        if (blue > 255)
          blue = 255;
        final int rgb = alpha << 24 | red << 16 | green << 8 | blue;
        image.setRGB(i, j, rgb);
      }
    return image;
  }

  static float[][] Rescale(float[][] in, float scale, float offset) {
    final int width = in.length;
    final int height = in[0].length;
    for (int i = 0; i < width; i++)
      for (int j = 0; j < height; j++) {
        float v = in[i][j];
        v = v * scale + offset;
        in[i][j] = v;
      }
    return in;
  }
}
