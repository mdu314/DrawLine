package com.mdu.DrawLine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class MoveHandler implements InvocationHandler {
  private DLComponent component;

  MoveHandler(DLComponent c) {
    component = c;
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object o = method.invoke(component, args);
    DLPropertySheet ps = component.parent.ps;
    if (ps != null && method.getName().equals("move")) {
      ps.update("X", new Float(component.x + (Float) args[0]));
      ps.update("Y", new Float(component.y + (Float) args[1]));
    }
    return o;
  }
}
