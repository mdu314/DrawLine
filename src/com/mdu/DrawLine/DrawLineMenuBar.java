package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.curveList;
import static com.mdu.DrawLine.DLUtil.imageList;
import static com.mdu.DrawLine.DLUtil.otherList;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

@SuppressWarnings("serial")
class DLMenu extends JMenu {
  DLComponent component;

  public DLMenu(String t) {
    super(t);
    getPopupMenu().setLayout(new GridLayout(0, 4));
    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    getPopupMenu().addPopupMenuListener(new PopupMenuListener() {

      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {        
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
    this((String) null);
  }

  DLMenuItem(String s) {
    super(s);
  }

  static String shortName(Class<?> c) {
    String s = c.getName();
    int i = s.lastIndexOf('.');
    if (i > 0)
      s = s.substring(i + 1, s.length());
    if (s.startsWith("DL"))
      s = s.substring("DL".length(), s.length());
    return s;
  }

  public DLMenuItem(Class<?> cls) {
    this(shortName(cls));
    this.cls = cls;
  }

}

@SuppressWarnings("serial")
class DrawLineMenuBar extends JMenuBar {
  DrawLine drawLine;
  DLMenu menu1;
  DLMenu menu2;
  DLMenu menu3;
  Class<?> selectedClass;

  DrawLineMenuBar(DrawLine dl) {
    super();
    drawLine = dl;
    menu1 = new DLMenu("Curves");
    add(menu1);
    menu2 = new DLMenu("Images");
    add(menu2);
    menu3 = new DLMenu("Maps");
    add(menu3);
    makeMenu(menu1, curveList);
    makeMenu(menu2, imageList);
    makeMenu(menu3, otherList);

    add(Box.createHorizontalGlue());

    JButton b = new JButton("Clear");
    b.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        drawLine.canvas.clear();
        drawLine.canvas.paintControl.paint();
      }
    });
    add(b);
    b = new JButton("Delete");
    final ActionListener deleteLastActionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DLComponent c = drawLine.canvas.deleteLastComponent();
        if (c != null)
          drawLine.canvas.paint(c.getBounds());
      }
    };
    b.addActionListener(deleteLastActionListener);

    final JButton but = b;
    b.addMouseListener(new MouseAdapter() {
      final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
      ScheduledFuture<?> future;
      int delay = 150;

      public void mousePressed(MouseEvent e) {
        final Runnable runnable = new Runnable() {
          public void run() {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                but.doClick();
                if (future != null) {
                  future = executor.schedule(this, delay--, TimeUnit.MILLISECONDS);
                  delay -= 10;
                  if(delay < 1)
                    delay = 1;
                }
              }
            });
          }
        };
        future = executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
      }

      public void mouseReleased(MouseEvent e) {
        if (future != null) {
          future.cancel(true);
        }
        future = null;
      }
    });
    add(b);
  }

  void makeMenu(final DLMenu menu, ArrayList<Class<? extends DLComponent>> list) {

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
              menu.component = menuItem.dlc.menuComponent;
            } else {
              menu.component = menuItem.dlc.copy();
            }
            menu.component.prepareForDisplay();
            if (menu.component instanceof Threaded) {
              Threaded t = (Threaded) menu.component;
              t.stopAll();
              t.setThreaded(false);
            }
          }
        }
      });
    }
  }
}
