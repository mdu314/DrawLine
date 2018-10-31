package com.mdu.DrawLine;

public interface Movable {

  Movable getMovableProxy();
  void setMovableProxy(Movable m);
  
  void move(float x, float y);

}
