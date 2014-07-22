package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DRAWING_STEP;
import static java.lang.Integer.MAX_VALUE;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;

 class DLMouse extends MouseAdapter implements MouseMotionListener {
  int lastX = MAX_VALUE;
  int lastY = MAX_VALUE;
  int e2 = DRAWING_STEP * DRAWING_STEP;
  int key;
  Point p;

  Component listenComponent;
  KeyListener keyListener;
  MouseListener mouseListener;
  MouseMotionListener mouseMotionListener;
  DLComponentList components;

  DLMouse() {
    this(null);
  }

  DLMouse(DLComponentList dl) {
    super();
    this.components = dl;
  }

  DLMouse(DLComponentList dl, int e) {
    this(dl);
    e2 = e * e;
  }
  
  int getKey() {
    return key;
  }

  void componentEnter(MouseEvent e, DLComponent c) {

  }

  void componentLeave(MouseEvent e, DLComponent c) {

  }

  DLComponentList hitTest(Point p) {
    if (components == null)
      return null;
    DLComponentList hit = null;
    Iterator<DLComponent> i = components.iterator();
    while (i.hasNext()) {
      DLComponent dlc = i.next();
      if (dlc.hitTest(p)) {
        if (hit == null)
          hit = new DLComponentList();
        hit.add(dlc);
      }
    }
    if (hit == null)
      return null;

    return hit;
  }

  void stoplisten() {
    if (keyListener != null)
      listenComponent.removeKeyListener(keyListener);

    if (mouseListener != null)
      listenComponent.removeMouseListener(mouseListener);

    if (mouseMotionListener != null)
      listenComponent.removeMouseMotionListener(mouseMotionListener);
  }

  void restartListen() {
    if (keyListener != null)
      listenComponent.addKeyListener(keyListener);

    if (mouseListener != null)
      listenComponent.addMouseListener(mouseListener);

    if (mouseMotionListener != null)
      listenComponent.addMouseMotionListener(mouseMotionListener);
  }

  void listen(Component c) {
    listenComponent = c;

    keyListener = new KeyListener() {

      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyReleased(KeyEvent e) {
        int kc = e.getKeyCode();
        key &= ~kc;
      }

      @Override
      public void keyPressed(KeyEvent e) {
        int kc = e.getKeyCode();
        key |= kc;
      }
    };
    c.addKeyListener(keyListener);

    mouseListener = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        DLMouse.this.mouseClicked(e);
        lastX = MAX_VALUE;
        lastY = MAX_VALUE;
        p = e.getPoint();
      }

      public void mousePressed(MouseEvent e) {
        DLMouse.this.mousePressed(e);
        lastX = MAX_VALUE;
        lastY = MAX_VALUE;
        p = e.getPoint();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        DLMouse.this.mouseReleased(e);
        lastX = MAX_VALUE;
        lastY = MAX_VALUE;
        p = e.getPoint();
      }

    };
    c.addMouseListener(mouseListener);

    mouseMotionListener = new MouseMotionAdapter() {

      DLComponent current = null;

      public void mouseMoved(MouseEvent e) {
        DLMouse.this.mouseMoved(e);

        DLComponentList dlc = hitTest(e.getPoint());
        if (dlc != null) {
          Iterator<DLComponent> i = dlc.iterator();
          if (i.hasNext()) {
            DLComponent c = i.next();
            if (c != current) {
              if (current != null)
                componentLeave(e, current);
              current = c;
              componentEnter(e, c);
            }
          }
        } else {
          if (current != null)
            componentLeave(e, current);
          current = null;
        }
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int dx = lastX - x;
        int dy = lastY - y;
        double d2 = dx * dx + dy * dy;
        if (d2 < e2)
          return;
        lastX = x;
        lastY = y;
        p = e.getPoint();
        DLMouse.this.mouseDragged(e);
      }
    };
    c.addMouseMotionListener(mouseMotionListener);
  }

  Point getPoint() {
    return p;
  }
}
