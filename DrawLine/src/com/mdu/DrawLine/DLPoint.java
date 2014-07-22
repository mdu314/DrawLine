package com.mdu.DrawLine;

import java.awt.Shape;

class DLPoint {
  float x, y;
  long when;
  DLLineBrush brush;
  Shape shape;
  DLComponent dlc;

  DLPoint(float x, float y) {
    this.x = x;
    this.y = y;
  }

  DLPoint(double x, double y) {
    this.x = (float) x;
    this.y = (float) y;
  }

  DLPoint(float x, float y, long w, Shape s) {
    this.x = x;
    this.y = y;
    this.when = w;
    this.shape = s;
  }

  DLPoint(float x, float y, long w) {
    this.x = x;
    this.y = y;
    this.when = w;
  }

  DLPoint(float x, float y, Shape s) {
    this.x = x;
    this.y = y;
    this.shape = s;
  }

  public String toString() {
    return x + " " + y + " " + when;
  }
}
