package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.curveList;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

@SuppressWarnings("serial")
class DrawLineMenuBar extends JMenuBar {

  DLMenu menu;
  Class<?> selectedClass;
  DrawLine drawLine;

  DrawLineMenuBar(DrawLine dl) {
    super();
    drawLine = dl;
    menu = new DLMenu();
    add(menu);

    menu.addMenuListener(new MenuListener() {

      @Override
      public void menuSelected(MenuEvent e) {
      }

      @Override
      public void menuDeselected(MenuEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            drawLine.repaint();
          }
        });
      }

      @Override
      public void menuCanceled(MenuEvent e) {
        System.err.println(drawLine.getPanel().getGraphics().getClip());
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            drawLine.repaint();
          }
        });
      }
    });
    Iterator<Class<? extends DLComponent>> i = curveList.iterator();
    while (i.hasNext()) {
      final Class<?> dlc = i.next();
      final DLMenuItem menuItem = new DLMenuItem(dlc);
      menuItem.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
      menu.add(menuItem);
      menuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          selectedClass = dlc;
          if (dlc != null) {
            menu.comp = menuItem.dlc.copy();
            if (menu.comp instanceof DLSegmented) {
              DLSegmented seg = (DLSegmented) menu.comp;
              int x = 0;
              int y = 0;
              int k = RangeRandom(10, 30);
              for (int i = 0; i < k; i++) {
                int dx = RangeRandom(-30, 30);
                int dy = RangeRandom(-30, 30);
                long now = System.currentTimeMillis() + i * 1000;
                x += dx;
                y += dy;
                seg.addSegment(x, y, now);
              }
            }
          }
        }
      });
    }
  }

  Component top(Component c) {
    if (c.getParent() == null)
      return c;
    return top(c.getParent());
  }

}

@SuppressWarnings("serial")
class DLMenu extends JMenu {
  private double margin = DLParams.MENU_MARGIN;
  DLComponent comp;

  public DLMenu() {
    super();
    int f = (int) Math.floor(Math.sqrt(curveList.size()));
    getPopupMenu().setLayout(new GridLayout(f, 0));
  }

  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    d.width = Math.max(d.width, DLParams.MENU_ITEM_SIZE);
    d.height = Math.max(d.height, DLParams.MENU_ITEM_SIZE);
    return d;
  }

  public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    DLComponent c = comp;
    if (c != null) {
      Rectangle b = getBounds();
      Rectangle2D b2 = new Rectangle2D.Double(margin, margin, b.width - 2 * margin, b.height - 2 * margin);
      Rectangle2D r = c.getBounds();
      AffineTransform tr = DLUtil.computeTransform(r, b2, true);
      g2.setTransform(tr);
      c.paint(g);
    }
  }
}

@SuppressWarnings("serial")
class DLMenuItem extends JMenuItem {
  private double margin = DLParams.MENU_MARGIN;
  DLComponent dlc;
  Class<?> cls;

  DLMenuItem() {
    super();
  }

  public DLMenuItem(Class<?> cls) {
    this();
    this.cls = cls;
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        DLMenu m = (DLMenu) (((JPopupMenu) getParent()).getInvoker());
        DrawLineMenuBar bar = (DrawLineMenuBar) m.getParent();
        if (DLMenuItem.this.cls != null)
          bar.drawLine.message(DLMenuItem.this.cls.getName());
      }
    });
  }

  private DLComponent getDLComponent() {
    if (dlc != null)
      return dlc;

    if (cls == null)
      return null;

    try {
      Class<?> params[] = { int.class, int.class };
      Constructor<?> con = cls.getConstructor(params);
      con.setAccessible(true);
      dlc = (DLComponent) con.newInstance(0, 0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    dlc.randomize();
    if (dlc instanceof DLSegmented) {
      DLSegmented seg = (DLSegmented) dlc;
      seg.addSomeSegments(10);
    }
    Rectangle b = getBounds();
    Rectangle2D b2 = new Rectangle2D.Double(margin, margin, b.width - 2 * margin, b.height - 2 * margin);
    Rectangle2D r = dlc.getBounds();
    AffineTransform tr = DLUtil.computeTransform(r, b2, true);
    dlc.transform(tr);
    return dlc;
  }

  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    d.width = Math.max(d.width, DLParams.MENU_ITEM_SIZE);
    d.height = Math.max(d.height, DLParams.MENU_ITEM_SIZE);
    return d;
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public void paint(Graphics g) {
    super.paint(g);
    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    DLComponent c = getDLComponent();
    if (c != null)
      c.paint(g);
  }
}
