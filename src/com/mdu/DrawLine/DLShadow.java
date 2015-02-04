package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface DLShadow {

  Rectangle addShadowBounds(Rectangle r);

  void clearShadow();

  public boolean getShadow();

  public void setShadow(boolean s);

  void shadow(Graphics2D ga);
}
