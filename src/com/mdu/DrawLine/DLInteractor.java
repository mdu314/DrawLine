package com.mdu.DrawLine;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;

public class DLInteractor {
  DrawLine drawLine;
  MouseMotionListener motionListener;

  public DLInteractor(DrawLine c) {
    c.addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
        final DLComponent dlc = hitTest(e.getPoint());
        if (dlc == null)
          return;

      }

      @Override
      public void mouseReleased(MouseEvent e) {
      }
    });
  }

  DLComponent hitTest(Point p) {
    final Iterator<DLComponent> i = drawLine.canvas.components.iterator();
    while (i.hasNext()) {
      final DLComponent dlc = i.next();
      if (dlc.hitTest(p))
        return dlc;
    }
    return null;
  }

  void start(DLComponent c) {
    motionListener = new MouseMotionListener() {

      @Override
      public void mouseDragged(MouseEvent e) {
      }

      @Override
      public void mouseMoved(MouseEvent e) {
      }
    };
    drawLine.addMouseMotionListener(motionListener);
  }

  void stop(DLComponent c) {
    if (motionListener != null)
      drawLine.removeMouseMotionListener(motionListener);
  }
}
