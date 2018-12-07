package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.curveList;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON2;
import static java.awt.event.MouseEvent.BUTTON3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

@SuppressWarnings("serial")
class DrawLine extends JFrame {
  DrawLineMenuBar menu;
  DLMouse mouse;
  DLContainer canvas;

  public static void setBestLookAndFeelAvailable() {
    String system_lf = UIManager.getSystemLookAndFeelClassName().toLowerCase();
    if (system_lf.contains("metal")) {
      try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
      } catch (Exception e) {
      }
    } else {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
      }
    }
  }

  static DLComponent makeACurve(Class<?> cls, float x, float y) {
    final Class<?> params[] = {
        float.class, float.class
    };
    try {
      if (cls != null) {
        final Constructor<?> con = cls.getConstructor(params);
        con.setAccessible(true);
        final DLComponent comp = (DLComponent) con.newInstance(x, y);
        comp.randomize();
        return comp;
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  static DLComponent makeARandomCurve(float x, float y) {
    int k = RangeRandom(0, curveList.size());
    return makeACurve(k, x, y);
  }

  static DLComponent makeACurve(int k, float x, float y) {
    final Class<? extends DLComponent> c = curveList.get(k);
    return makeACurve(c, x, y);
  }

  static DLComponent makeARandomCurve(DLContainer canvas) {
    final int w = canvas.getWidth();
    final int h = canvas.getHeight();
    final int m = Math.min(w, h);
    final double r = DLUtil.RandomGauss(0, m / 10);
    final double t = DLUtil.RangeRandom(0, DLUtil.TWO_PI);
    final float x = (float) Math.round(r * Math.cos(t)) + w / 2;
    final float y = (float) Math.round(r * Math.sin(t)) + h / 2;
    return makeARandomCurve(x, y);
  }

  static void fill(DLContainer canvas) {
    final int w = canvas.getWidth();
    final int h = canvas.getHeight();
    int count = 100;
    while (count-- > 0) {
      float x = DLUtil.RangeRandom(0, w);
      float y = DLUtil.RangeRandom(0, h);
      DLComponent c = makeARandomCurve(x, y);
      canvas.addComponent(c);
    }
  }

  static DLComponent makeSegmentedCurve(final DLMouse mouse, final DLContainer canvas, Class<DLSegmentedComponent> cls,
      int x, int y) {
    final Class<?> params[] = {
        float.class, float.class
    };
    try {
      final Constructor<?> con = cls.getConstructor(params);
      con.setAccessible(true);
      final DLSegmentedComponent comp = (DLSegmentedComponent) con.newInstance(x, y);
      comp.randomize();
      mouse.stoplisten();
      final DLMouse dlm = new DLMouse(canvas) {
        @Override
        public void mouseDragged(MouseEvent e) {
          comp.addSegment(e.getX(), e.getY(), e.getWhen());
          final Graphics g = canvas.getGraphics();
          DLUtil.SetHints(g);
          comp.drawLastSegment(g);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          canvas.addComponent(comp);
          stoplisten();
          mouse.listen();
        }
      };
      dlm.listen(canvas);
      return comp;
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    // setBestLookAndFeelAvailable();
    /* DrawLine dl = */new DrawLine();
  }

  public DrawLine() {
    super("DrawLine");
    // System.setProperty("awt.useSystemAAFontSettings", "on");
    // System.setProperty("swing.aatext", "true");

    canvas = content();
    getContentPane().add(canvas, BorderLayout.CENTER);

    setFocusable(true);

    setSize(800, 600);
    getPanel().setBackground(new Color(0xc0c0c0));

    menu = new DrawLineMenuBar(this);
    setJMenuBar(menu);

    addKeyListener(new DLKeyListener(canvas));

    mouse = new DLMouse(canvas) {
      void componentEnter(MouseEvent e, DLComponent c) {
        if (e.isAltDown())
          cursor(e.getPoint(), "delete");
        else
          cursor(e.getPoint(), "select");
      }

      void componentLeave(MouseEvent e, DLComponent c) {
        setCursor(null);
      }

      void cursor(Point p, String type) {
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      }

      private boolean isEmpty(DLComponentList list) {
        if(list == null)
          return true;
        if(list.isEmpty())
          return true;
        return false;
      }
      
      private void button1AndShift(MouseEvent e) {        
          setSelection(hitTest(p, true));
          if (canvas.getSelection() != null)
            if (canvas.selection.iterator().hasNext()) {
              final DLComponent c = canvas.selection.iterator().next();
              if (canvas.ps != null)
                canvas.ps.close();
              canvas.ps = new DLPropertySheet(c);
            }
      }
      
      void button1(MouseEvent e) {
        
        final Point p = e.getPoint();
        final float x = p.x;
        final float y = p.y;        
        final DLComponentList old = canvas.getSelection();
          final DLComponentList hit = hitTest(p);
          setSelection(hit);
          if (!isEmpty(hit))
              return;
          else if(!isEmpty(old))
            return;
          int k;
              DLComponent dlc = null;
          if ((k = getKey()) != 0)
            dlc = makeACurve(k, x, y);
          else if (e.isShiftDown())
            dlc = makeARandomCurve(x, y);
          else if (menu.selectedClass != null)
            dlc = makeACurve(menu.selectedClass, x, y);
          else if (e.isAltDown()) {
            final ArrayList<DLComponent> c = hitTest(p);
            if (c != null) {
              final Iterator<DLComponent> i = c.iterator();
              while (i.hasNext()) {
                canvas.removeComponent(i.next());
              }
            }
          } else if (hit == null && old == null) {
            dlc = makeARandomCurve(x, y);
          }
          if (dlc != null)
            canvas.addComponent(dlc);
      }
      
      public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
        case BUTTON1:
          if(e.isShiftDown()) 
            button1AndShift(e);
          else 
            button1(e);          
          break;
        case BUTTON2:
        case BUTTON3:
          button1AndShift(e);
          break;
        default:
          break;
        }
      }

      public void mouseDragged(MouseEvent e) {
        final Point p = e.getPoint();
        final int x = p.x;
        final int y = p.y;
        DLComponent dlc = null;
        if (menu.selectedClass != null)
          dlc = makeACurve(menu.selectedClass, x, y);
        else
          dlc = makeARandomCurve(x, y);
        canvas.addComponent(dlc);
      }

      public void mousePressed(MouseEvent e) {
        final DLComponentList hit = hitTest(e.getPoint());
        if (hit != null)
          setSelection(hit);
        if (hit != null) {
          final DLComponent c = canvas.selection.get(canvas.selection.size() - 1);
          canvas.components.raise(c);
          final Rectangle bounds = c.getBounds();
          canvas.paint(bounds);
          startMoveComponent(e.getPoint());
        } else if (menu.selectedClass != null) {
          final Class<?> selected = menu.selectedClass;
          if (DLSegmentedComponent.class.isAssignableFrom(selected)) {
            DLComponent dlc = DrawLine.makeSegmentedCurve(this, canvas, (Class<DLSegmentedComponent>) selected,
                e.getX(), e.getY());
            canvas.addComponent(dlc);
          }
        }
      }

      public void mouseWheelMoved(MouseWheelEvent e) {
        final DLComponentList hit = hitTest(e.getPoint(), true);
        if (hit != null) {
          final Iterator<DLComponent> it = hit.iterator();
          while (it.hasNext()) {
            final DLComponent h = it.next();
            h.mouse(e);
          }
        }
      }
    };

    mouse.listen(canvas);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = DrawLine.this.getSize().width;
        final int h = DrawLine.this.getSize().height;
        final int x = (dim.width - w) / 2;
        final int y = (dim.height - h) / 2;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(x, y);
        setVisible(true);
      }
    });
  }

  DLContainer content() {
    final DLContainer panel = new DLContainer();
    panel.setFocusable(true);
    panel.setSize(800, 600);
    panel.setBackground(new Color(0x0c0c0c));
    panel.addComponentListener(new ComponentListener() {
      public void componentShown(ComponentEvent e) {
      }

      public void componentResized(ComponentEvent e) {
        panel.paintControl.paint();
      }

      public void componentMoved(ComponentEvent e) {
      }

      public void componentHidden(ComponentEvent e) {
      }
    });
    return panel;
  }

  Component getPanel() {
    return canvas;
  }

  Rectangle moveComponent(DLComponent c, int dx, int dy) {
    final Rectangle r1 = c.getBounds();

    if (c.getMovableProxy() != null)
      c.getMovableProxy().move(dx, dy);
    else
      c.move(dx, dy);
    final Rectangle r2 = c.getBounds();
    final Rectangle r = new Rectangle();
    Rectangle2D.union(r1, r2, r);
    if (canvas.paintControl.painting) {
      final Graphics g = getPanel().getGraphics();
      canvas.paintControl.paint(r, g);
    }
    return r;
  }

  void setSelection(DLComponentList sel) {
    canvas.paintControl.setPainting(false);

    if (canvas.selection != null) {
      final Iterator<DLComponent> i = canvas.selection.iterator();
      while (i.hasNext()) {
        final DLComponent c = i.next();
        if (c instanceof DLCurve) {
          final DLCurve cu = (DLCurve) c;

          final Rectangle r = cu.setSelectedGetRect(false);
          if (r != null)
            canvas.paintControl.addRectangle(r);
        }
      }
    }

    canvas.selection = sel;

    if (canvas.selection != null) {
      final Iterator<DLComponent> i = canvas.selection.iterator();
      while (i.hasNext()) {
        final DLComponent c = i.next();
        if (c instanceof DLCurve) {
          final DLCurve cu = (DLCurve) c;
          final Rectangle r = cu.setSelectedGetRect(true);
          if (r != null)
            canvas.paintControl.addRectangle(r);
        }
      }
    }

    canvas.paintControl.setPainting(true);
  }

  void startMoveComponent(final DLComponentList dlc, Point p) {
    final Point lp = p;
    mouse.stoplisten();

    final DLMouse dlm = new DLMouse(canvas, 2) {
      @Override
      public void mouseDragged(MouseEvent e) {
        final Point ep = e.getPoint();
        final int dx = ep.x - lp.x;
        final int dy = ep.y - lp.y;
        final Iterator<DLComponent> i = dlc.iterator();
        canvas.paintControl.setPainting(false);
        while (i.hasNext()) {
          final DLComponent c = i.next();
          final Rectangle r = moveComponent(c, dx, dy);
          canvas.paintControl.addRectangle(r);
        }
        canvas.paintControl.setPainting(true);
        lp.x = ep.x;
        lp.y = ep.y;
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        final Point ep = e.getPoint();
        final int dx = ep.x - lp.x;
        final int dy = ep.y - lp.y;
        final Iterator<DLComponent> i = dlc.iterator();
        canvas.paintControl.setPainting(false);
        while (i.hasNext()) {
          final DLComponent c = i.next();
          final Rectangle r = moveComponent(c, dx, dy);
          canvas.paintControl.addRectangle(r);
        }
        canvas.paintControl.setPainting(true);
        stoplisten();
        mouse.listen();
      }

    };
    dlm.listen(canvas);
  }

  void startMoveComponent(Point p) {
    startMoveComponent(canvas.selection, p);
  }

}
