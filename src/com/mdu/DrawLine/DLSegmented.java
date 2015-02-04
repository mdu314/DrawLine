package com.mdu.DrawLine;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public interface DLSegmented {
  void addSegment(float x, float y, long l);

  void addSegment(MouseEvent e);

  void addSomeSegments(int n);

  void drawLastSegment(Graphics g);

  void randomize();
}
