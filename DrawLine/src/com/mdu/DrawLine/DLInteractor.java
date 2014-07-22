package com.mdu.DrawLine;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;

public class DLInteractor {
  DrawLine drawLine;
  MouseMotionListener motionListener;
  
  DLComponent hitTest(Point p) {
    Iterator<DLComponent> i = drawLine.components.iterator();
    while (i.hasNext()) {
      DLComponent dlc = i.next();
      if (dlc.hitTest(p)) {
        return dlc;
      }
    }
    return null;
  }

  public DLInteractor(DrawLine c) {
    c.addMouseListener(new MouseListener() {

      public void mouseReleased(MouseEvent e) {
      }

      public void mousePressed(MouseEvent e) {
        DLComponent dlc = hitTest(e.getPoint());
        if(dlc == null)
          return;
        
      }

      public void mouseExited(MouseEvent e) {
      }

      public void mouseEntered(MouseEvent e) {
      }

      public void mouseClicked(MouseEvent e) {
      }
    });
  }

  void stop(DLComponent c) {
    if (motionListener != null)
      drawLine.removeMouseMotionListener(motionListener);
  }

  void start(DLComponent c) {
    motionListener = new MouseMotionListener() {

      public void mouseMoved(MouseEvent e) {
      }

      public void mouseDragged(MouseEvent e) {
      }
    };
    drawLine.addMouseMotionListener(motionListener);
  }
}
