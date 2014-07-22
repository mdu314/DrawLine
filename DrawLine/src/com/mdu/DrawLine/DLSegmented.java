package com.mdu.DrawLine;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public interface DLSegmented {
  void addSomeSegments(int n);
  void addSegment(int x, int y, long l);  
  void addSegment(MouseEvent e);  
  void drawLastSegment(Graphics g);
  void randomize();
}
