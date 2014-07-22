package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RandomColor;
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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.omg.CosNaming.IstringHelper;

@SuppressWarnings("serial")
class DrawLine extends JFrame {
  DLComponentList components = new DLComponentList();
  DrawLineMenuBar menu;
  DLMouse mouse;
  JPanel canvas;
  JLabel messages;
  JLabel count;
  DLComponentList selection;
  Cursor cursor = null;
  PaintControl paintControl = new PaintControl(this);
  static final String ZERO_PATTERN = "000000";

  static Graphics2D GetGraphics() {
    return (Graphics2D) new JFrame().getGraphics();
  }

  void makeARandomCurve() {
    int w = getPanel().getWidth();
    int h = getPanel().getHeight();
    int m = Math.min(w, h);
    double r = DLUtil.RandomGauss(10 * m);
    double t = DLUtil.RangeRandom(0, DLUtil.TWO_PI);
    int x = (int) Math.round(r * Math.cos(t)) + w / 2;
    int y = (int) Math.round(r * Math.sin(t)) + h / 2;
    makeARandomCurve(x, y);
  }

  void makeARandomCurve(int x, int y) {
    int k = RangeRandom(0, curveList.size());
    makeACurve(k, x, y);
  }

  void makeACurve(int k, int x, int y) {
    Class<? extends DLComponent> c = curveList.get(k);
    makeACurve(c, x, y);
  }

  void makeSegmentedCurve(Class<DLSegmented> cls, int x, int y) {
    Class<?> params[] = { int.class, int.class };
    try {
      Constructor<?> con = cls.getConstructor(params);
      con.setAccessible(true);
      final DLSegmented comp = (DLSegmented) con.newInstance(x, y);
      comp.randomize();
      DLMouse dlm = new DLMouse(components) {
        public void mouseDragged(MouseEvent e) {
          comp.addSegment(e.getX(), e.getY(), e.getWhen());
          Graphics g = getPanel().getGraphics();
          DLUtil.SetHints(g);
          comp.drawLastSegment(g);
        }

        public void mouseReleased(MouseEvent e) {
          stoplisten();
          addComponent(comp);
        }
      };
      dlm.listen(getPanel());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void makeACurve(Class<?> cls, int x, int y) {
    Class<?> params[] = { int.class, int.class };
    try {
      if (cls != null) {
        message("Make a " + cls.getName());
        Constructor<?> con = cls.getConstructor(params);
        con.setAccessible(true);
        DLComponent comp = (DLComponent) con.newInstance(x, y);
        comp.randomize();
        addComponent(comp);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  JPanel content() {
    JPanel panel = new JPanel() {
      protected void paintComponent(java.awt.Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        DLUtil.SetHints(g2d);
        super.paintComponent(g);
      }

    };
    panel.setFocusable(true);
    panel.setSize(800, 600);
    return panel;
  }

  Component getPanel() {
    return canvas; // getContentPane();
  }

  String lines[] = new String[] { "0", "1", "2", "3" };

  void count() {
    int s = components.size();
    DecimalFormat df = new DecimalFormat(ZERO_PATTERN);

    int size = 0;
    Iterator<DLComponent> i = components.iterator();
    while (i.hasNext())
      size += DLUtil.GetObjectSize(i.next());
    String f = "<html> " + df.format(s) + " objects<br> <hr>size <nbsp/><nbsp/><nbsp/><nbsp/>" + df.format(size) + "</html>";
    count.setText(f);
  }

  void message(String s) {
    int nlines = lines.length;
    StringBuffer sb = new StringBuffer();

    if (messages == null)
      return;

    if (s == null || s == "")
      return;

    if (s.equals(lines[0]))
      return;

    for (int i = nlines - 1; i > 0; i--)
      lines[i] = lines[i - 1];
    lines[0] = s;

    sb.setLength(0);
    sb.append("<html>");
    for (int i = 0; i < nlines; i++) {
      if (i != 0)
        sb.append(" <br> ");
      sb.append(lines[i]);
    }
    sb.append("</html>");
    messages.setText(sb.toString());
  }

  JPanel deco() {

    JPanel panel = new JPanel(new BorderLayout(3, 1));

    JLabel memory = new JLabel("memory");
    /* DLMemory dlm = */new DLMemory(memory);

    memory.setBorder(new LineBorder(Color.black, 1, true));
    panel.add(memory, BorderLayout.LINE_START);

    messages = new JLabel();
    // "<html>The white zone is for loading and unloading only." + "<br>"
    // + "If you want to load or unload, go to the white zone.</html>");
    messages.setVerticalAlignment(SwingConstants.BOTTOM);
    Font f = new Font(Font.SERIF, Font.PLAIN, 8);
    messages.setFont(f);
    messages.setBorder(new LineBorder(Color.black, 1, true));

    panel.add(messages, BorderLayout.CENTER);

    f = new Font(Font.SERIF, Font.PLAIN, 8);
    count = new JLabel(ZERO_PATTERN);
    count.setBorder(new LineBorder(Color.black, 1, true));
    count.setFont(f);
    count.setVerticalAlignment(SwingConstants.BOTTOM);
    count.setHorizontalAlignment(SwingConstants.RIGHT);
    panel.add(count, BorderLayout.LINE_END);

    add(panel, BorderLayout.SOUTH);

    return panel;
  }

  boolean selectionEmpty() {
    if (selection == null)
      return true;
    if (selection.size() == 0)
      return true;
    return false;
  }

  void setSelection(DLComponentList sel) {
    paintControl.setPainting(false);

    if (selection != null) {
      Iterator<DLComponent> i = selection.iterator();
      while (i.hasNext()) {
        DLComponent c = i.next();
        if (c instanceof DLCurve) {
          DLCurve cu = (DLCurve) c;
          Rectangle r = cu.selected(false);
          paintControl.addRectangle(r);
        }
      }
    }

    selection = sel;

    if (selection != null) {
      Iterator<DLComponent> i = selection.iterator();
      while (i.hasNext()) {
        DLComponent c = i.next();
        if (c instanceof DLCurve) {
          DLCurve cu = (DLCurve) c;
          Rectangle r = cu.selected(true);
          paintControl.addRectangle(r);
        }
      }
    }
    paintControl.setPainting(true);
  }

  DLComponentList getSelection() {
    return selection;
  }

  public DrawLine() {
    super("DrawLine");
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");

    canvas = content(); // new JPanel();
    getContentPane().add(canvas, BorderLayout.CENTER);

    setFocusable(true);

    JPanel deco = deco();
    getContentPane().add(deco, BorderLayout.SOUTH);

    setSize(800, 600);
    getPanel().setBackground(new Color(0xc0c0c0));

    getPanel().addComponentListener(new ComponentListener() {

      public void componentShown(ComponentEvent e) {
      }

      public void componentResized(ComponentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            paint();
          }
        });
      }

      public void componentMoved(ComponentEvent e) {
      }

      public void componentHidden(ComponentEvent e) {
      }
    });

    menu = new DrawLineMenuBar(this);
    setJMenuBar(menu);

    KeyListener keyListener = new KeyListener() {

      public void keyTyped(KeyEvent arg0) {
      }

      @Override
      public void keyReleased(KeyEvent arg0) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        DLComponent c;
        int x;
        int y;
        switch (e.getKeyCode()) {
        case KeyEvent.VK_2:
          Color b1 = RandomColor(0, 1, 0f, 0.5f, 0.5f, 1);
          getPanel().setBackground(b1);

          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              paint();
            }
          });
          break;
        case KeyEvent.VK_1:
          Color b2 = RandomColor(0, 1, 0.5f, 0.8f, 0, 0.5f);
          getPanel().setBackground(b2);

          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              paint();
            }
          });
          break;
        case KeyEvent.VK_3:
          Color b3 = RandomColor(0, 1, 0.6f, 1f, 0.8f, 1f);
          getPanel().setBackground(b3);

          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              paint();
            }
          });
          break;
        case KeyEvent.VK_X: {
          Component p = getPanel();
          int w = p.getWidth();
          int min = w / 4;
          int max = 3 * w / 4;
          x = DLUtil.RangeRandom(min, max);
          int h = p.getHeight();
          min = h / 4;
          max = 3 * h / 4;
          y = DLUtil.RangeRandom(min, max);
          DLPolyline pl = new DLPolyline(x, y);

          int k = RangeRandom(10, 30);
          for (int i = 0; i < k; i++) {
            int dx = RangeRandom(-30, 30);
            int dy = RangeRandom(-30, 30);
            long now = System.currentTimeMillis() + i * 500;
            x += dx;
            y += dy;
            pl.addSegment(x, y, now);
            pl.drawLastSegment(p.getGraphics());
          }
          pl.color = RandomColor(0, 1, 0.6f, 1f, 0.8f, 1f);
          pl.mode = DLPolyline.CIRCLE_SHAPE;
          addComponent((DLComponent) pl);
        }
          break;
        case KeyEvent.VK_Z: {
          Component p = getPanel();
          int w = p.getWidth();
          int min = w / 4;
          int max = 3 * w / 4;
          x = DLUtil.RangeRandom(min, max);
          int h = p.getHeight();
          min = h / 4;
          max = 3 * h / 4;
          y = DLUtil.RangeRandom(min, max);
          DLRuban pl = new DLRuban(x, y);
          pl.randomize();
          int k = RangeRandom(10, 30);
          boolean horiz = DLUtil.BooleanRandom();
          int dx = 0;
          int dy = 0;
          int dd = 3;
          for (int i = 0; i < k; i++) {
            if (horiz) {
              dx = RangeRandom(0, 15);
              dy += RangeRandom(-dd, dd);
            } else {
              dx += RangeRandom(-dd, dd);
              dy = RangeRandom(0, 15);
            }
            long now = System.currentTimeMillis() + i * 100;
            x += dx;
            y += dy;
            pl.addSegment(x, y, now);
            pl.drawLastSegment(p.getGraphics());
          }
          addComponent((DLComponent) pl);
        }
          break;
        case KeyEvent.VK_Q:
          paintControl.setPainting(false);
          for (int i = 0; i < 10; i++)
            makeARandomCurve();
          paintControl.setPainting(true);
          break;
        case KeyEvent.VK_R:
          makeARandomCurve();
          break;
        case KeyEvent.VK_D:
          count();
          long time = System.currentTimeMillis();
          paint();
          time = System.currentTimeMillis() - time;
          message("Redisplay time " + time + " ms");
          break;
        case KeyEvent.VK_C:
          message("Clear ");
          clear();
          count();
          paint();
          break;
        case KeyEvent.VK_PLUS:
        case KeyEvent.VK_P:
          message("Add ");
          Rectangle r = getPanel().getBounds();
          double margin = 20;
          x = (int) (Math.floor(DLUtil.Normalize(margin, r.width - margin, 0., 1., Math.random())));
          y = (int) (Math.floor(DLUtil.Normalize(margin, r.height - margin, 0., 1., Math.random())));
          makeARandomCurve(x, y);
          break;
        case KeyEvent.VK_MINUS:
        case KeyEvent.VK_M:
          if (components.size() <= 0) {
            message("Empty list");
            return;
          }
          message("Delete last ");
          c = components.get(components.size() - 1);
          removeComponent(c);
          paint(c.getBounds());
          break;
        case KeyEvent.VK_S:
          if (selection != null) {
            Iterator<DLComponent> i = selection.iterator();
            while (i.hasNext()) {
              c = i.next();
              if (c instanceof DLCurve) {
                DLCurve cu = (DLCurve) c;
                cu.setShadow(!cu.getShadow());
              }
            }
          }
          break;
        default:
          message("KeyCode: " + e.getKeyCode());
        }
      }
    };

    addKeyListener(keyListener);

    mouse = new DLMouse(components) {
      public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        int x = p.x;
        int y = p.y;
        switch (e.getButton()) {
        case BUTTON1:
          setSelection(hitTest(p));
          if (true) {
            int k;
            if ((k = getKey()) != 0) {
              makeACurve(k, x, y);
            } else if (e.isShiftDown()) {
              makeARandomCurve(x, y);
            } else if (menu.selectedClass != null) {
              makeACurve(menu.selectedClass, x, y);
            } else if (e.isAltDown()) {

            } else {
              makeARandomCurve(x, y);
            }
          }
          break;
        case BUTTON3:
          setSelection(hitTest(p));
          if (getSelection() != null) {
            if (selection.iterator().hasNext()) {
              DLComponent c = selection.iterator().next();
              /* DLPropertySheet ps = */new DLPropertySheet(c);
            }
          }
          break;
        default:
          break;
        }
      }

      void cursor(Point p) {
        DLComponentList h = hitTest(p);
        if (h == null)
          setCursor(null);

        if (cursor == null) {
          File f = new File("eldorado_bundle_mini/eldorado_stroke/mobile/086.png");
          Image image = Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath());
          Point hs = new Point(20, 20);
          Cursor cu = Toolkit.getDefaultToolkit().createCustomCursor(image, hs, "Sym");
          cursor = cu;
        }
        setCursor(cursor);

      }

      void componentEnter(MouseEvent e, DLComponent c) {
        cursor(e.getPoint());
      }

      void componentLeave(MouseEvent e, DLComponent c) {
        setCursor(null);
      }

      public void mousePressed(MouseEvent e) {
        setSelection(hitTest(e.getPoint()));
        if (selection != null) {
          DLComponent c = selection.get(selection.size() - 1);
          message("Select " + c.getClass().getName());
          components.raise(c);
          Rectangle bounds = c.getBounds();
          paint(bounds);
          startMoveComponent(e.getPoint());
        } else if (menu.selectedClass != null) {

          Class<?> selected = menu.selectedClass;
          if (DLSegmented.class.isAssignableFrom(selected))
            makeSegmentedCurve((Class<DLSegmented>) selected, e.getX(), e.getY());
        }
      }

      public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        int x = p.x;
        int y = p.y;
        if (menu.selectedClass != null)
          makeACurve(menu.selectedClass, x, y);
        else
          makeARandomCurve(x, y);
      }

    };
    mouse.listen(getPanel());

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int w = this.getSize().width;
    int h = this.getSize().height;
    int x = (dim.width - w) / 2;
    int y = (dim.height - h) / 2;

    // Move the window
    this.setLocation(x, y);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);

  }

  DLComponentList hitTest(Point p) {
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

  void startMoveComponent(Point p) {
    startMoveComponent(selection, p);
  }

  void startMoveComponent(final DLComponentList dlc, Point p) {
    final Point lp = p;
    mouse.stoplisten();

    DLMouse dlm = new DLMouse(components, 2) {
      public void mouseReleased(MouseEvent e) {
        Point ep = e.getPoint();
        int dx = ep.x - lp.x;
        int dy = ep.y - lp.y;
        Iterator<DLComponent> i = dlc.iterator();
        paintControl.setPainting(false);
        while (i.hasNext()) {
          DLComponent c = i.next();
          Rectangle r = moveComponent(c, dx, dy);
          paintControl.addRectangle(r);
        }
        paintControl.setPainting(true);

        stoplisten();
        mouse.restartListen();
      }

      public void mouseDragged(MouseEvent e) {
        Point ep = e.getPoint();
        int dx = ep.x - lp.x;
        int dy = ep.y - lp.y;
        Iterator<DLComponent> i = dlc.iterator();
        paintControl.setPainting(false);
        while (i.hasNext()) {
          DLComponent c = i.next();
          Rectangle r = moveComponent(c, dx, dy);
          paintControl.addRectangle(r);
        }
        paintControl.setPainting(true);
        lp.x = ep.x;
        lp.y = ep.y;
      }

    };
    dlm.listen(getPanel());
  }

  void clear() {
    components.clear();
  }

  void paint() {
    Component c = getPanel();
    Dimension size = c.getSize();
    Graphics g = c.getGraphics();
    DLUtil.SetHints(g);
    paint(new Rectangle(size), g);
  }

  void paint(Rectangle r) {
    Graphics g = getPanel().getGraphics();
    DLUtil.SetHints(g);
    paint(r, g);
  }

  public void repaint(long t, int x, int y, int w, int h) {
    Graphics g = getPanel().getGraphics();
    DLUtil.SetHints(g);
    paint(new Rectangle(x, y, w, h), g);
  }

  void paint(Rectangle r, Graphics g) {
    if (r.width <= 0 || r.height <= 0) {
      // new Error(r.width + " " + r.height).printStackTrace();
      return;
    }
    if (r.width > 100000 || r.height > 100000) {
      return;
    }
    if (paintControl.isPainting()) {
      BufferedImage image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_RGB);
      Color c = getPanel().getBackground();
      Graphics2D gi = image.createGraphics();
      gi.setColor(c);
      gi.fillRect(0, 0, r.width, r.height);

      AffineTransform tr = AffineTransform.getTranslateInstance(-r.x, -r.y);
      gi.setTransform(tr);

      DLUtil.SetHints(gi);

      Iterator<DLComponent> i = components.iterator();
      while (i.hasNext()) {
        DLComponent dlc = i.next();
        if (r.intersects(dlc.getBounds())) {
          dlc.paint(gi);
        }
      }
      g.drawImage(image, r.x, r.y, null);
    } else {
      paintControl.addRectangle(r);
    }
  }

  Rectangle moveComponent(DLComponent c, int dx, int dy) {
    Rectangle r1 = c.getBounds();
    c.move(dx, dy);
    Rectangle r2 = c.getBounds();
    Rectangle r = new Rectangle();
    Rectangle2D.union(r1, r2, r);
    if (paintControl.painting) {
      Graphics g = getPanel().getGraphics();
      paint(r, g);
    }
    return r;
  }

  void removeComponent(DLComponent o) {
    if (selection != null)
      selection.remove(o);
    Rectangle r = o.getBounds();
    components.remove(o);
    count();
    o.parent = null;
    Graphics g = getPanel().getGraphics();
    DLUtil.SetHints(g);
    paint(r, g);
  }

  void addComponent(DLSegmented s) {
    if (s instanceof DLComponent)
      addComponent((DLComponent) s);
  }

  void addComponent(DLComponent o) {
    components.add(o);
    count();
    o.parent = this;
    Rectangle r = o.getBounds();
    Graphics g = getPanel().getGraphics();
    DLUtil.SetHints(g);
    paint(r, g);
  }

  public static void main(String[] args) {
    /* DrawLine dl = */new DrawLine();
  }

}

class PaintControl {
  boolean painting = true;
  Rectangle rectangle = null;
  DrawLine drawLine;

  PaintControl(DrawLine dl) {
    drawLine = dl;
  }

  public boolean isPainting() {
    return painting;
  }

  public void setPainting(boolean painting) {
    this.painting = painting;
    if (painting && (rectangle != null)) {
      drawLine.paint(rectangle);
      rectangle = null;
    }
  }

  public Rectangle getRectangle() {
    return rectangle;
  }

  public void setRectangle(Rectangle rectangle) {
    this.rectangle = rectangle;
  }

  void addRectangle(Rectangle r) {
    if (painting)
      System.err.println("Adding rectangle while painting!");
    if (rectangle == null)
      rectangle = (Rectangle) r.clone();
    else
      Rectangle2D.union(rectangle, r, rectangle);
  }

}