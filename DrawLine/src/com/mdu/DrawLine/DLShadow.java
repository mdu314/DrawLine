package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface DLShadow {

  public boolean getShadow();
  public void setShadow(boolean s);
  void clearShadow();
  Rectangle addShadowBounds(Rectangle r);
  void shadow(Graphics2D ga);
}
