package com.mdu.DrawLine;

public class Gauss {
  public static void main(String[] args) throws Exception {
    final double sigma = 1;
    final double mu = 1;
    for (int i = 0; i < 20; i++) {
      final double x = Math.random();
      Math.exp(-05 * Math.pow((x - mu) / sigma, 2) / (sigma * Math.sqrt(2 * Math.PI)));
    }
  }
}
