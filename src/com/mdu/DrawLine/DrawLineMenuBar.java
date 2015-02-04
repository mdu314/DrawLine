package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.curveList;
import static com.mdu.DrawLine.DLUtil.imageList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

@SuppressWarnings("serial")
class DLMenu extends JMenu {
  DLComponent comp;
  int margin = DLParams.MENU_MARGIN;
  JWindow window;

  public DLMenu(ArrayList<Class<? extends DLComponent>> c) {
    super();
    final int f = (int) Math.floor(Math.sqrt(c.size()));
    getPopupMenu().setLayout(new GridLayout(f, 0));
    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    addMenuListener(new MenuListener() {
      public void menuSelected(MenuEvent e) {
      }

      public void menuDeselected(MenuEvent e) {
        if (window != null)
          window.setVisible(false);
      }

      public void menuCanceled(MenuEvent e) {
      }
    });
  }

  public Dimension getPreferredSize() {
    final Dimension d = super.getPreferredSize();
    d.width = Math.max(d.width, DLParams.MENU_ITEM_SIZE);
    d.height = Math.max(d.height, DLParams.MENU_ITEM_SIZE);
    return d;
  }

  public void paint(Graphics g) {

    final Graphics2D g2 = (Graphics2D) g;
    DLUtil.SetHints(g);
    super.paint(g);

    final DLComponent c = comp;
    if (c != null) {
      final Rectangle b = getBounds();
      final Rectangle2D.Float b2 = new Rectangle2D.Float(margin, margin, b.width - 2 * margin, b.height - 2 * margin);
      final Rectangle2D r = c.getBounds();
      final AffineTransform tr = DLUtil.computeTransform(r, b2, true);
      g2.setTransform(tr);
      c.paint(g);
    }
  }

  boolean processing = false;

  void window(final boolean show, final DLMenuItem item) {
    if (window == null) {
      window = new JWindow();
      window.addMouseListener(new MouseListener() {
        public void mouseClicked(MouseEvent e) {
          if (processing)
            return;
          processing = true;
          item.processMouseEvent(e);
          processing = false;
        }

        public void mousePressed(MouseEvent e) {
          if (processing)
            return;
          processing = true;
          item.processMouseEvent(e);
          processing = false;
        }

        public void mouseReleased(MouseEvent e) {
          if (processing)
            return;
          processing = true;
          item.processMouseEvent(e);
          processing = false;
        }

        public void mouseEntered(MouseEvent e) {
          if (processing)
            return;
          processing = true;
          //          item.processMouseEvent(e);
          processing = false;
        }

        public void mouseExited(MouseEvent e) {
          if (processing)
            return;
          processing = true;
          item.processMouseEvent(e);
          processing = false;
        }
      });
      window.addComponentListener(new ComponentListener() {
        public void componentResized(ComponentEvent e) {
        }

        public void componentMoved(ComponentEvent e) {
          Graphics2D g = (Graphics2D) window.getGraphics();
          g.setColor(Color.black);
          g.drawLine(0, 0, window.getWidth(), window.getHeight());
        }

        public void componentShown(ComponentEvent e) {
          Graphics2D g = (Graphics2D) window.getGraphics();
          g.setColor(Color.black);
          g.drawLine(0, 0, window.getWidth(), window.getHeight());
        }

        public void componentHidden(ComponentEvent e) {
        }
      });
    }
    final Point pt = new Point(item.getLocation());
    pt.x = pt.x + item.getWidth() / 2;
    pt.y = pt.y + item.getHeight() / 2;
    SwingUtilities.convertPointToScreen(pt, item.getParent());
    final int size = 50;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        window.setLocation(pt.x - size / 2, pt.y - size / 2);
        window.setSize(size, size);
        window.toFront();
        window.setVisible(show);
      }
    });
  }
}

@SuppressWarnings("serial")
class DLMenuItem extends JMenuItem {
  Class<?> cls;
  DLComponent dlc;
  int margin = DLParams.MENU_MARGIN;

  DLMenuItem() {
    super();
  }

  public void paint(Graphics g) {
    DLUtil.SetHints(g);
    super.paint(g);

    if (dlc == null)
      dlc = getDLComponent();

    if (dlc != null) {
      final Rectangle b = getBounds();
      final Rectangle2D.Float b2 = new Rectangle2D.Float(0, 0, b.width - 2 * 0, b.height - 2 * 0);
      final Rectangle2D r = dlc.getBounds();
      final AffineTransform tr = DLUtil.computeTransform(r, b2, true);
      ((Graphics2D) g).setTransform(tr);
      dlc.paint(g);
    }

  }

  public DLMenuItem(Class<?> cls) {
    this();
    this.cls = cls;

    addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        final DLMenu m = (DLMenu) ((JPopupMenu) getParent()).getInvoker();
        final DrawLineMenuBar bar = (DrawLineMenuBar) m.getParent();
        if (DLMenuItem.this.cls != null) {
          bar.drawLine.message(DLMenuItem.this.cls.getName());
        }
      }
    });
  }

  public void processMouseEvent(MouseEvent e) {
    super.processMouseEvent(e);
  }

  DLComponent makeAComponent(Class<?> cls, float x, float y) {
    try {
      if (cls != null) {
        final Class<?> params[] = { float.class, float.class };
        final Constructor<?> con = cls.getConstructor(params);
        con.setAccessible(true);
        final DLComponent comp = (DLComponent) con.newInstance(x, y);
        comp.randomize();
        final Rectangle b = getBounds();
        final Rectangle2D.Float b2 = new Rectangle2D.Float(0, 0, b.width, b.height);
        final Rectangle2D r = comp.getBounds();
        final AffineTransform tr = DLUtil.computeTransform(r, b2, true);
        comp.transform(tr);
        if (comp instanceof Threaded) {
          Threaded t = (Threaded) comp;
          t.stopAll();
          t.setThreaded(false);
        }
        return comp;
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /*
    private DLComponent _getDLChar(String s) {
      DLChar dls = new DLChar(0, 0);
      dls.randomize();
      dls.setText(s);
      final Rectangle b = getBounds();
      final Rectangle2D.Float b2 = new Rectangle2D.Float(margin, margin, b.width - 2 * margin, b.height - 2 * margin);
      final Rectangle2D r = dls.getBounds();
      final AffineTransform tr = DLUtil.computeTransform(r, b2, true);
      dls.transform(tr);
      return dls;
    }
  */

  private DLComponent getDLComponent() {
    return getDLComponent(null);
  }

  private DLComponent getDLComponent(String s) {
    DLComponent dls = makeAComponent(cls, 0, 0);
    if (dls != null)
      dls.prepareForDisplay();
    return dls;
  }

  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  @Override
  public Dimension getPreferredSize() {
    final Dimension d = super.getPreferredSize();
    d.width = Math.max(d.width, margin + DLParams.MENU_ITEM_SIZE);
    d.height = Math.max(d.height, margin + DLParams.MENU_ITEM_SIZE);
    return d;
  }

}

@SuppressWarnings("serial")
class DrawLineMenuBar extends JMenuBar {

  DrawLine drawLine;
  DLMenu menu1;
  DLMenu menu2;
  Class<?> selectedClass;

  DrawLineMenuBar(DrawLine dl) {
    super();
    drawLine = dl;
    menu1 = new DLMenu(curveList);
    add(menu1);
    menu2 = new DLMenu(imageList);
    add(menu2);

    MenuListener ml = new MenuListener() {

      @Override
      public void menuCanceled(MenuEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            drawLine.repaint();
          }
        });
      }

      @Override
      public void menuDeselected(MenuEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            drawLine.repaint();
          }
        });
      }

      @Override
      public void menuSelected(MenuEvent e) {
      }

    };

    makeMenu(menu1, curveList, ml);
    makeMenu(menu2, imageList, ml);
  }

  void makeMenu(final DLMenu menu, ArrayList<Class<? extends DLComponent>> list, MenuListener ml) {

    final Iterator<Class<? extends DLComponent>> i = list.iterator();
    while (i.hasNext()) {
      final Class<?> dlc = i.next();
      final DLMenuItem menuItem = new DLMenuItem(dlc);
      menuItem.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
      menu.add(menuItem);
      menuItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          selectedClass = dlc;
          if (dlc != null && menuItem.dlc != null) {
            if (menuItem.dlc.menuComponent != null) {
              menu.comp = menuItem.dlc.menuComponent;
            } else {
              menu.comp = menuItem.dlc.copy();
            }
            menu.comp.prepareForDisplay();
            if (menu.comp instanceof Threaded) {
              Threaded t = (Threaded) menu.comp;
              t.stopAll();
              t.setThreaded(false);
            }
          }
        }
      });
    }
    //    menu.addMenuListener(ml);
  }
}
