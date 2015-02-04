package com.mdu.DrawLine;

import java.awt.Graphics2D;

public interface Threaded {
  boolean isThreaded();

  void setThreaded(boolean threaded);

  void stopAll();

  void runThreaded(final Graphics2D g);

  void runThreaded();

  void f(Graphics2D g, DLThread t);
}
