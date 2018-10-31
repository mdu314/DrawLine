package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLParams.DRAWING_STEP;
import static java.lang.Integer.MAX_VALUE;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.SwingUtilities;

class DLMouse extends MouseAdapter implements MouseMotionListener {
  DLContainer con;
  int e2 = DRAWING_STEP * DRAWING_STEP;
  int key;
  KeyListener keyListener;
  int lastX = MAX_VALUE;

  int lastY = MAX_VALUE;
  DLContainer listenComponent;
  boolean listening = false;
  MouseListener mouseListener;
  MouseMotionListener mouseMotionListener;

  MouseWheelListener mouseWheelListener;
  Point p;
  boolean singleSelection = true;

  DLMouse() {
    this(null);
  }

  DLMouse(DLContainer dl) {
    super();
    this.con = dl;
  }

  DLMouse(DLContainer dl, int e) {
    this(dl);
    e2 = e * e;
  }

  void componentEnter(MouseEvent e, DLComponent c) {

  }

  void componentLeave(MouseEvent e, DLComponent c) {

  }

  int getKey() {
    return key;
  }

  Point getPoint() {
    return p;
  }

  DLComponent hit(MouseEvent e) {
    final DLComponentList dlc = hitTest(e.getPoint(), true);
    if (dlc != null) {
      final Iterator<DLComponent> i = dlc.iterator();
      if (i.hasNext()) {
        final DLComponent c = i.next();
        c.mouse(e);
        return c;
      }
    }
    return null;
  }

  DLComponentList hitTest(Point p) {
    return hitTest(p, false);
  }

  DLComponentList hitTest(Point p, boolean boxOnly) {
    if (con.components == null)
      return null;
    DLComponentList hit = null;
    final ListIterator<DLComponent> i = con.components.listIterator(con.components.size());

    while (i.hasPrevious()) {
      final DLComponent dlc = i.previous();
      boolean add = false;
      if (boxOnly) {
        final Rectangle b = dlc.getBounds();
        add = b.contains(p);
      } else
        add = dlc.hitTest(p);
      if (add)
        if (hit == null) {
          hit = new DLComponentList();
          hit.add(dlc);
          if (singleSelection)
            return hit;
        } else
          hit.add(dlc);
    }
    return hit;
  }

  void listen() {
    if (keyListener != null)
      listenComponent.addKeyListener(keyListener);

    if (mouseListener != null)
      listenComponent.addMouseListener(mouseListener);

    if (mouseMotionListener != null)
      listenComponent.addMouseMotionListener(mouseMotionListener);

    if (mouseWheelListener != null)
      listenComponent.addMouseWheelListener(mouseWheelListener);

    listening = true;
  }

  void listen(DLContainer c) {
    listenComponent = c;

    listening = true;
    keyListener = new KeyListener() {

      @Override
      public void keyPressed(KeyEvent e) {
        final int kc = e.getKeyCode();
        key |= kc;
      }

      @Override
      public void keyReleased(KeyEvent e) {
        final int kc = e.getKeyCode();
        key &= ~kc;
      }

      @Override
      public void keyTyped(KeyEvent e) {
      }
    };
    c.addKeyListener(keyListener);

    mouseListener = new MouseAdapter() {
      DLComponent grabComponent;

      @Override
      public void mouseClicked(MouseEvent e) {
        if (!listening) {
          System.err.println("MouseClicked while not listening");
          return;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
          grabComponent = hit(e);
          return;
        }
        DLMouse.this.mouseClicked(e);
        lastX = MAX_VALUE;
        lastY = MAX_VALUE;
        p = e.getPoint();
      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          grabComponent = hit(e);
          if (grabComponent != null)
            grabComponent.mouse(e);
          else
            hit(e);
          return;
        }
        DLMouse.this.mousePressed(e);
        lastX = MAX_VALUE;
        lastY = MAX_VALUE;
        p = e.getPoint();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (!listening) {
          System.err.println("MouseReleased while no listener");
          return;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
          if (grabComponent != null)
            grabComponent.mouse(e);
          else
            hit(e);
        } else {
          DLMouse.this.mouseReleased(e);
          lastX = MAX_VALUE;
          lastY = MAX_VALUE;
          p = e.getPoint();
          grabComponent = null;
        }
      }

    };
    c.addMouseListener(mouseListener);

    mouseMotionListener = new MouseMotionAdapter() {

      DLComponent current = null;

      @Override
      public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          hit(e);
          return;
        }
        final int x = e.getX();
        final int y = e.getY();
        final int dx = lastX - x;
        final int dy = lastY - y;
        final double d2 = dx * dx + dy * dy;
        if (d2 < e2)
          return;
        lastX = x;
        lastY = y;
        p = e.getPoint();
        DLMouse.this.mouseDragged(e);
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        hit(e);
        DLMouse.this.mouseMoved(e);

        final DLComponentList dlc = hitTest(e.getPoint(), true);
        if (dlc != null) {
          final Iterator<DLComponent> i = dlc.iterator();
          if (i.hasNext()) {
            final DLComponent c = i.next();
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
    };
    c.addMouseMotionListener(mouseMotionListener);

    mouseWheelListener = new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          hit(e);
          return;
        }
        DLMouse.this.mouseWheelMoved(e);
      }
    };
    c.addMouseWheelListener(mouseWheelListener);
  }

  void stoplisten() {
    if (keyListener != null)
      listenComponent.removeKeyListener(keyListener);

    if (mouseListener != null)
      listenComponent.removeMouseListener(mouseListener);

    if (mouseMotionListener != null)
      listenComponent.removeMouseMotionListener(mouseMotionListener);

    if (mouseWheelListener != null)
      listenComponent.removeMouseWheelListener(mouseWheelListener);

    listening = false;
  }
}
