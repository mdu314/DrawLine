package com.mdu.DrawLine;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Proxy;
import java.util.Iterator;

import javax.swing.JPanel;

@SuppressWarnings("serial")
class DLContainer extends JPanel { // Container {
  DLComponentList components = new DLComponentList();
  PaintControl paintControl = new PaintControl(this);
  DLComponentList selection = new DLComponentList();
  DLPropertySheet ps;

  DLComponentList hitTest(Point p) {
    DLComponentList hit = null;
    final Iterator<DLComponent> i = components.iterator();
    while (i.hasNext()) {
      final DLComponent dlc = i.next();
      if (dlc.hitTest(p)) {
        if (hit == null)
          hit = new DLComponentList();
        hit.add(dlc);
      }
    }
    return hit;
  }

  DLComponent deleteLastComponent() {
    if (components.size() <= 0) {
      return null;
    }
    DLComponent c = components.get(components.size() - 1);
    if (!selectionEmpty())
      selection.remove(c);
    removeComponent(c);
    return c;
  }

  DLComponent getLastComponent() {
    if(components.size() == 0)
      return null;
    DLComponent c = components.get(components.size() - 1);
    return c;
  }
  
  void addComponent(Object o) {
    addComponent((DLComponent) o);
  }

  void addComponent(DLComponent o) {
    if (o instanceof DLSegmentedComponent) {
      final DLSegmentedComponent sc = (DLSegmentedComponent) o;
      if (sc.points.size() <= 1)
        return;
    }

    Movable proxy = (Movable) Proxy.newProxyInstance(Movable.class.getClassLoader(), new Class[] { Movable.class },
        new MoveHandler(o));
    o.setMovableProxy(proxy);
    components.add(o);
    o.parent = this;
    final Rectangle r = o.getBounds();
    final Graphics g = getGraphics();
    DLUtil.SetHints(g);
    paintControl.paint(r, g);
  }

  void addComponent(DLSegmentedComponent s) {
    if (s instanceof DLComponent)
      addComponent((DLComponent) s);
  }

  void clear() {
    components.clear();
  }

  void paint(DLComponent c) {
    final Graphics g = getGraphics();
    paintControl.paint(c, g);
  }

  void paint(Rectangle r) {
    final Graphics g = getGraphics();
    paintControl.paint(r, g);
  }

  @Override
  public void repaint(long t, int x, int y, int w, int h) {
    final Graphics g = getGraphics();
    if (paintControl != null)
      paintControl.paint(new Rectangle(x, y, w, h), g);
  }

  void removeComponent(DLComponent o) {
    if (!selectionEmpty())
      selection.remove(o);
    final Rectangle r = o.getBounds();
    components.remove(o);
    o.parent = null;
    final Graphics g = getGraphics();
    paintControl.paint(r, g);
  }

  DLComponentList getSelection() {
    return selection;
  }

  boolean selectionEmpty() {
    if (selection == null)
      return true;
    if (selection.size() == 0)
      return true;
    return false;
  }

}