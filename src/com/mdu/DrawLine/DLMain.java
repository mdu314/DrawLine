package com.mdu.DrawLine;

import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON2;
import static java.awt.event.MouseEvent.BUTTON3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DLMain {

  static final int NAN = Integer.MIN_VALUE;

  static Object[][] params;

  static int get(String what) {
    for (Object[] pr : params) {
      Object pn = pr[0];
      if (what.equals(pn)) {
        int pv = (int) pr[1];
        return pv;
      }
    }
    return NAN;
  }

  static int getWidth() {
    int i = get("width");
    if (i == NAN)
      i = get("iwidth");
    if (i == NAN)
      i = 0;
    return i;
  }

  static int getHeight() {
    int i = get("height");
    if (i == NAN)
      i = get("iheight");
    if (i == NAN)
      i = 0;
    return i;
  }

  static DLComponent Main(Class<?> cls, Object[][] params) {
    DLMain.params = params;
    int width = getWidth();
    int height = getHeight();
    final JFrame frame = new JFrame();
    final DLContainer panel = new DLContainer();
    panel.setFocusable(true);
    panel.setBackground(new Color(0x0c0c0c));
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setFocusable(true);
    frame.pack();
    int dh = frame.getSize().height - frame.getContentPane().getSize().height;
    frame.setSize(width, height + dh);
    panel.setBackground(new Color(0xc0c0c0));
    DLComponent dlc = null;
    try {
      Constructor<?> ctr = cls.getConstructor();
      dlc = (DLComponent) ctr.newInstance();
      init(dlc);
      panel.addComponent(dlc);
    } catch (Exception e) {
      DLError.report(e);
    }
    final DLComponent fdlc = dlc;
    DLMouse mouse = new DLMouse(panel) {
      public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
        case BUTTON2:
        case BUTTON3:
        case BUTTON1:
          if (panel.ps != null)
            panel.ps.close();
          panel.ps = new DLPropertySheet(fdlc);
          break;
        }
      }
    };

    mouse.listen(panel);

    ComponentListener cl = new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        Component p = e.getComponent();
        if (p != null) {
          Rectangle r = p.getBounds();
          int w = r.width;
          int h = r.height;

          setAttribute(fdlc, "x", w / 2);
          setAttribute(fdlc, "y", h / 2);

          setAttribute(fdlc, "iwidth", w);
          setAttribute(fdlc, "iheight", h);

          fdlc.reset();
        }
      }
    };

    panel.addComponentListener(cl);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(x, y);
        frame.setVisible(true);
        init(fdlc);
      }
    });

    return dlc;
  }

  static ArrayList<Field> getFields(Class<?> cls) {
    ArrayList<Field> ret = new ArrayList<Field>();
    while (cls != null) {
      Field[] fld = cls.getDeclaredFields();
      ret.addAll(java.util.Arrays.asList(fld));
      cls = cls.getSuperclass();
    }
    return ret;
  }

  static Field getField(Class<?> cls, String name) {
    while (cls != null) {
      Field[] fld = cls.getDeclaredFields();
      for (Field f : fld)
        if (name.equals(f.getName()))
          return f;
      cls = cls.getSuperclass();
    }
    return null;
  }

  static void setAttribute(DLComponent c, String att, Object val) {
    try {
      Class<?> cls = c.getClass();
      Field f = getField(cls, att);
      f.setAccessible(true);
      f.set(c, val);
    } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
      DLError.report(e);
    }
  }

  static Object getAttribute(DLComponent c, String att) {

    try {
      Class<?> cls = c.getClass();
      Field f = getField(cls, att);
      f.setAccessible(true);
      Object val = f.get(c);
      return val;
    } catch (IllegalArgumentException | IllegalAccessException e) {
      DLError.report(e);
    }
    return null;
  }

  static void init(DLComponent pat) {
    for (Object[] pr : params) {
      Object pn = pr[0];
      Object pv = pr[1];
      try {
        Class<?> cls = pat.getClass();
        Field f = getField(cls, (String) pn);
        if (f != null) {
          f.setAccessible(true);
          f.set(pat, pv);
        }
      } catch (SecurityException | IllegalArgumentException | IllegalAccessException e1) {
        DLError.report(e1);
      }
    }
  }
}
