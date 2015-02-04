package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RandomColor;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static com.mdu.DrawLine.DLUtil.curveList;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.nitido.utils.toaster.Toaster;

@SuppressWarnings("serial")
class DrawLine extends JFrame {
  static final String ZERO_PATTERN = "000000";

  //  static Graphics2D GetGraphics() {
  //    return (Graphics2D) new JFrame().getGraphics();
  //  }

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

  public static void main(String[] args) {
    //setBestLookAndFeelAvailable();
    /* DrawLine dl = */new DrawLine();
  }

  JPanel canvas;
  DLComponentList components = new DLComponentList();
  JLabel count;
  HashMap<String, Cursor> cursors = new HashMap<String, Cursor>();
  String lines[] = new String[] { "0", "1", "2", "3" };

  DrawLineMenuBar menu;
  JLabel messages;
  DLMouse mouse;

  PaintControl paintControl = new PaintControl(this);

  DLComponentList selection;

  Toaster toasterManager = new Toaster();
  DLPropertySheet ps;

  public DrawLine() {
    super("DrawLine");
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");

    canvas = content(); // new JPanel();
    getContentPane().add(canvas, BorderLayout.CENTER);

    setFocusable(true);

    final JPanel deco = deco();
    getContentPane().add(deco, BorderLayout.SOUTH);

    setSize(800, 600);
    getPanel().setBackground(new Color(0xc0c0c0));

    getPanel().addComponentListener(new ComponentListener() {
      public void componentHidden(ComponentEvent e) {
      }
      public void componentMoved(ComponentEvent e) {
      }
      public void componentResized(ComponentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            paint();
          }
        });
      }
      public void componentShown(ComponentEvent e) {
      }
    });

    menu = new DrawLineMenuBar(this);
    setJMenuBar(menu);

    final KeyListener keyListener = new KeyListener() {

      @Override
      public void keyPressed(KeyEvent e) {
        DLComponent c;
        float x;
        float y;
        switch (e.getKeyCode()) {
        case KeyEvent.VK_J: {
          final DLComponentList s = hitTest(getPanel().getMousePosition());
          if (s != null && s.size() == 1) {
            DLComponent d = s.get(0);
            if (d instanceof JPG) {
              JFileChooser fs = new JFileChooser();
              int rv = fs.showOpenDialog(DrawLine.this);
              if (rv == JFileChooser.APPROVE_OPTION) {
                ((JPG) d).save(fs.getSelectedFile());
              }
            }
          }
          break;
        }
        case KeyEvent.VK_I:
          final Point pt = getPanel().getMousePosition();
          final DLComponentList s = hitTest(pt);
          if (s != null && s.size() == 1)
            s.get(0).dump();
          break;
        case KeyEvent.VK_2:
          final Color b1 = RandomColor(0, 1, 0f, 0.5f, 0.5f, 1);
          getPanel().setBackground(b1);

          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              paint();
            }
          });
          break;
        case KeyEvent.VK_1:
          final Color b2 = RandomColor(0, 1, 0.5f, 0.8f, 0, 0.5f);
          getPanel().setBackground(b2);

          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              paint();
            }
          });
          break;
        case KeyEvent.VK_3:
          final Color b3 = RandomColor(0, 1, 0.6f, 1f, 0.8f, 1f);
          getPanel().setBackground(b3);

          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              paint();
            }
          });
          break;
        case KeyEvent.VK_X: {
          final Component p = getPanel();
          final int w = p.getWidth();
          int min = w / 4;
          int max = 3 * w / 4;
          x = DLUtil.RangeRandom(min, max);
          final int h = p.getHeight();
          min = h / 4;
          max = 3 * h / 4;
          y = DLUtil.RangeRandom(min, max);
          final DLPolyline pl = new DLPolyline(x, y);

          final int k = RangeRandom(10, 30);
          for (int i = 0; i < k; i++) {
            final int dx = RangeRandom(-30, 30);
            final int dy = RangeRandom(-30, 30);
            final long now = System.currentTimeMillis() + i * 500;
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
          final Component p = getPanel();
          final int w = p.getWidth();
          int min = w / 4;
          int max = 3 * w / 4;
          x = DLUtil.RangeRandom(min, max);
          final int h = p.getHeight();
          min = h / 4;
          max = 3 * h / 4;
          y = DLUtil.RangeRandom(min, max);
          final DLRuban pl = new DLRuban(x, y);
          pl.randomize();
          final int k = RangeRandom(10, 30);
          final boolean horiz = DLUtil.BooleanRandom();
          int dx = 0;
          int dy = 0;
          final int dd = 3;
          for (int i = 0; i < k; i++) {
            if (horiz) {
              dx = RangeRandom(0, 15);
              dy += RangeRandom(-dd, dd);
            } else {
              dx += RangeRandom(-dd, dd);
              dy = RangeRandom(0, 15);
            }
            final long now = System.currentTimeMillis() + i * 100;
            x += dx;
            y += dy;
            pl.addSegment(x, y, now);
            pl.drawLastSegment(p.getGraphics());
          }
          addComponent((DLComponent) pl);
        }
          break;
        case KeyEvent.VK_Q:
          message("10 random objects");
          paintControl.setPainting(false);
          for (int i = 0; i < 10; i++)
            makeARandomCurve();
          paintControl.setPainting(true);
          break;
        case KeyEvent.VK_R:
          message("1 random object");
          makeARandomCurve();
          break;
        case KeyEvent.VK_T:
          message("Paint texture");
          paintTexture();
          paintImageTexture();
          break;
        case KeyEvent.VK_E:
          paintTexture();
          paintImageTexture();
          texturePreview(3);
          break;
        case KeyEvent.VK_D:
          long time = System.currentTimeMillis();
          paint();
          time = System.currentTimeMillis() - time;
          message("Redisplay time " + time + " ms");
          break;
        case KeyEvent.VK_C:
          message("Clear ");
          clear();
          paint();
          break;
        case KeyEvent.VK_PLUS:
        case KeyEvent.VK_P:
          message("Add ");
          final Rectangle r = getPanel().getBounds();
          final double margin = 20;
          x = (float) Math.floor(DLUtil.Normalize(margin, r.width - margin, 0., 1., Math.random()));
          y = (float) Math.floor(DLUtil.Normalize(margin, r.height - margin, 0., 1., Math.random()));
          makeARandomCurve(x, y);
          break;
        case KeyEvent.VK_DELETE:
        case KeyEvent.VK_MINUS:
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
            final Iterator<DLComponent> i = selection.iterator();
            while (i.hasNext()) {
              c = i.next();
              if (c instanceof DLCurve) {
                final DLCurve cu = (DLCurve) c;
                cu.setShadow(!cu.getShadow());
              }
            }
          }
          break;
        default:
          message("KeyCode: " + e.getKeyCode());
        }
      }

      @Override
      public void keyReleased(KeyEvent arg0) {
      }

      @Override
      public void keyTyped(KeyEvent arg0) {
      }
    };

    addKeyListener(keyListener);

    mouse = new DLMouse(components) {
      @Override
      void componentEnter(MouseEvent e, DLComponent c) {
        if (e.isAltDown())
          cursor(e.getPoint(), "delete");
        else
          cursor(e.getPoint(), "select");
      }

      @Override
      void componentLeave(MouseEvent e, DLComponent c) {
        setCursor(null);
      }

      void cursor(Point p, String type) {
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        final Point p = e.getPoint();
        final float x = p.x;
        final float y = p.y;
        switch (e.getButton()) {
        case BUTTON1:
          final DLComponentList old = getSelection();
          final DLComponentList hit = hitTest(p);
          setSelection(hit);

          int k;
          if (!selectionEmpty()) {

          }
          if ((k = getKey()) != 0)
            makeACurve(k, x, y);
          else if (e.isShiftDown())
            makeARandomCurve(x, y);
          else if (menu.selectedClass != null)
            makeACurve(menu.selectedClass, x, y);
          else if (e.isAltDown()) {
            final ArrayList<DLComponent> c = hitTest(p);
            if (c != null) {
              final Iterator<DLComponent> i = c.iterator();
              while (i.hasNext())
                removeComponent(i.next());
            }
          } else if (hit == null && old == null)
            makeARandomCurve(x, y);

          break;
        case BUTTON2:
          setSelection(hitTest(p, true));
          if (getSelection() != null)
            if (selection.iterator().hasNext()) {
              final DLComponent c = selection.iterator().next();
              if (ps != null)
                ps.close();
              ps = new DLPropertySheet(c);
            }
          break;
        default:
          break;
        }
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        final Point p = e.getPoint();
        final int x = p.x;
        final int y = p.y;
        if (menu.selectedClass != null)
          makeACurve(menu.selectedClass, x, y);
        else
          makeARandomCurve(x, y);
      }

      @Override
      public void mousePressed(MouseEvent e) {
        final DLComponentList hit = hitTest(e.getPoint());
        if (hit != null)
          setSelection(hit);
        if (hit != null) {
          final DLComponent c = selection.get(selection.size() - 1);
          message("Select " + c.getClass().getName());
          components.raise(c);
          final Rectangle bounds = c.getBounds();
          paint(bounds);
          startMoveComponent(e.getPoint());
        } else if (menu.selectedClass != null) {
          final Class<?> selected = menu.selectedClass;
          if (DLSegmentedComponent.class.isAssignableFrom(selected))
            makeSegmentedCurve((Class<DLSegmentedComponent>) selected, e.getX(), e.getY());
        }
      }

      @Override
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
    mouse.listen(getPanel());

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = DrawLine.this.getSize().width;
        final int h = DrawLine.this.getSize().height;
        final int x = (dim.width - w) / 2;
        final int y = (dim.height - h) / 2;
        // Move the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(x, y);
        setVisible(true);
      }
    });
  }

  void addComponent(DLComponent o) {
    // new Error().printStackTrace();
    if (o instanceof DLSegmentedComponent) {
      final DLSegmentedComponent sc = (DLSegmentedComponent) o;
      if (sc.points.size() <= 1)
        return;
    }

    Movable proxy = (Movable) Proxy.newProxyInstance(Movable.class.getClassLoader(), new Class[] { Movable.class },
        new MoveHandler(o));
    o.movableProxy = proxy;
    components.add(o);
    o.parent = this;
    final Rectangle r = o.getBounds();
    final Graphics g = getPanel().getGraphics();
    DLUtil.SetHints(g);
    paint(r, g);
  }

  void addComponent(DLSegmentedComponent s) {
    if (s instanceof DLComponent)
      addComponent((DLComponent) s);
  }

  void clear() {
    components.clear();
  }

  JPanel content() {

    final JPanel panel = new JPanel(true) {
      @Override
      public void paintComponent(Graphics g) {
        DLUtil.SetHints(g);
        super.paintComponent(g);
      }
    };
    panel.setFocusable(true);
    panel.setSize(800, 600);
    return panel;
  }

  JPanel deco() {

    final JPanel panel = new JPanel(new BorderLayout(3, 1));

    final JLabel memory = new JLabel("memory");
    /* DLMemory dlm = */new DLMemory(memory);

    memory.setBorder(new LineBorder(Color.black, 1, true));

    messages = new JLabel();
    // "<html>The white zone is for loading and unloading only." + "<br>"
    // + "If you want to load or unload, go to the white zone.</html>");
    messages.setVerticalAlignment(SwingConstants.BOTTOM);
    Font f = new Font(Font.SERIF, Font.PLAIN, 8);
    messages.setFont(f);
    messages.setBorder(new LineBorder(Color.black, 1, true));

    f = new Font(Font.SERIF, Font.PLAIN, 8);
    count = new JLabel(ZERO_PATTERN);
    count.setBorder(new LineBorder(Color.black, 1, true));
    count.setFont(f);
    count.setVerticalAlignment(SwingConstants.BOTTOM);
    count.setHorizontalAlignment(SwingConstants.RIGHT);

    JComboBox<?> box = new JComboBox();
    DLThread.DLThreads(box);

    panel.add(memory, BorderLayout.LINE_START);
    JPanel p = new JPanel(new BorderLayout());
    p.add(messages, BorderLayout.CENTER);
    p.add(box, BorderLayout.LINE_END);
    panel.add(p, BorderLayout.CENTER);
    panel.add(count, BorderLayout.LINE_END);

    add(panel, BorderLayout.SOUTH);

    return panel;
  }

  Component getPanel() {
    return canvas; // getContentPane();
  }

  DLComponentList getSelection() {
    return selection;
  }

  DLComponentList hitTest(Point p) {
    DLComponentList hit = null;
    final Iterator<DLComponent> i = components.iterator();
    while (i.hasNext()) {
      final DLComponent dlc = i.next();
      if (dlc.hitTest(p)) {
        if (hit == null)
          hit = new DLComponentList();
        hit.add(dlc);
      }
    }
    return hit;
  }

  void makeACurve(Class<?> cls, float x, float y) {
    final Class<?> params[] = { float.class, float.class };
    try {
      if (cls != null) {
        message("Make a " + cls.getName());
        final Constructor<?> con = cls.getConstructor(params);
        con.setAccessible(true);
        final DLComponent comp = (DLComponent) con.newInstance(x, y);
        comp.randomize();
        addComponent(comp);
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  void makeACurve(int k, float x, float y) {
    final Class<? extends DLComponent> c = curveList.get(k);
    makeACurve(c, x, y);
  }

  void makeARandomCurve() {
    final int w = getPanel().getWidth();
    final int h = getPanel().getHeight();
    final int m = Math.min(w, h);
    final double r = DLUtil.RandomGauss(0, m / 10);
    final double t = DLUtil.RangeRandom(0, DLUtil.TWO_PI);
    final float x = (float) Math.round(r * Math.cos(t)) + w / 2;
    final float y = (float) Math.round(r * Math.sin(t)) + h / 2;
    makeARandomCurve(x, y);
  }

  void makeARandomCurve(float x, float y) {
    int k = RangeRandom(0, curveList.size());
    makeACurve(k, x, y);
  }

  void makeSegmentedCurve(Class<DLSegmentedComponent> cls, int x, int y) {
    final Class<?> params[] = { float.class, float.class };
    try {
      final Constructor<?> con = cls.getConstructor(params);
      con.setAccessible(true);
      final DLSegmentedComponent comp = (DLSegmentedComponent) con.newInstance(x, y);
      comp.randomize();
      mouse.stoplisten();
      final DLMouse dlm = new DLMouse(components) {
        @Override
        public void mouseDragged(MouseEvent e) {
          comp.addSegment(e.getX(), e.getY(), e.getWhen());
          final Graphics g = getPanel().getGraphics();
          DLUtil.SetHints(g);
          comp.drawLastSegment(g);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          stoplisten();
          if (comp.points.size() > 1)
            addComponent(comp);
          mouse.listen();
        }
      };
      dlm.listen(getPanel());
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  void message(String s) {
    final int nlines = lines.length;
    final StringBuffer sb = new StringBuffer();

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

  Rectangle moveComponent(DLComponent c, int dx, int dy) {
    final Rectangle r1 = c.getBounds();

    if (c.movableProxy != null)
      c.movableProxy.move(dx, dy);
    else
      c.move(dx, dy);
    final Rectangle r2 = c.getBounds();
    final Rectangle r = new Rectangle();
    Rectangle2D.union(r1, r2, r);
    if (paintControl.painting) {
      final Graphics g = getPanel().getGraphics();
      paint(r, g);
    }
    return r;
  }

  void paintTexture() {
    Component c = getPanel();
    Dimension size = c.getSize();
    Graphics g = c.getGraphics();
    DLUtil.SetHints(g);
    paintTexture(new Rectangle(size), g);
  }

  void paintTexture(Rectangle r, Graphics g) {
    if (r.width <= 0 || r.height <= 0)
      // new Error(r.width + " " + r.height).printStackTrace();
      return;
    if (r.width > 100000 || r.height > 100000)
      return;
    DLUtil.SetHints(g);
    if (paintControl.isPainting()) {
      final BufferedImage image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_RGB);
      final Color c = getPanel().getBackground();
      final Graphics2D gi = image.createGraphics();
      gi.setColor(c);
      gi.fillRect(0, 0, r.width, r.height);

      final AffineTransform tr = AffineTransform.getTranslateInstance(-r.x, -r.y);
      gi.setTransform(tr);

      DLUtil.SetHints(gi);

      final DLComponentList copy = components.copy();
      final Iterator<DLComponent> i = copy.iterator();
      while (i.hasNext()) {
        final DLComponent dlc = i.next();
        Rectangle db = dlc.getBounds();
        if (r.intersects(db)) {
          dlc.paint(gi);
          float tx = 0;
          float ty = 0;
          if (db.x < 0)
            tx = r.width;
          if (db.y < 0)
            ty = r.height;
          if (db.x + db.width > r.width)
            tx = -r.width;
          if (db.y + db.height > r.height)
            ty = -r.height;
          if (tx != 0)
            dlc.paint(gi, tx, 0);
          if (ty != 0)
            dlc.paint(gi, 0, ty);
          if (tx != 0 && ty != 0)
            dlc.paint(gi, tx, ty);
        }
      }
      g.drawImage(image, r.x, r.y, null);
    } else
      paintControl.addRectangle(r);
  }

  void paintImageTexture() {
    JFileChooser fs = new JFileChooser();
    int rv = fs.showOpenDialog(DrawLine.this);
    if (rv == JFileChooser.APPROVE_OPTION) {
      Component c = getPanel();
      Dimension size = c.getSize();
      BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.createGraphics();
      DLUtil.SetHints(g);
      paintTexture(new Rectangle(size), g);
      DLUtil.Save(image, fs.getSelectedFile());
    }
  }

  void texturePreview(int tile) {
    JDialog dialog = new JDialog();
    Component c = getPanel();
    Dimension size = c.getSize();
    dialog.setSize(size);
    BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    DLUtil.SetHints(g);
    double dx = 0;
    double dy = 0;
    for (int i = 0; i < tile; i++) {
      for (int j = 0; j < tile; j++) {
        dx = (double) i * size.width / tile;
        dy = (double) j * size.height / tile;
        AffineTransform tr = AffineTransform.getTranslateInstance(dx, dy);
        tr.concatenate(AffineTransform.getScaleInstance(1. / tile, 1. / tile));
        g.setTransform(tr);
        paintTexture(new Rectangle(size), g);
      }
    }
    JLabel label = new JLabel(new ImageIcon(image));
    dialog.add(label);
    dialog.pack();
    dialog.setLocationByPlatform(true);
    dialog.setVisible(true);
  }

  void paint() {
    Component c = getPanel();
    Dimension size = c.getSize();
    Graphics g = c.getGraphics();
    DLUtil.SetHints(g);
    paint(new Rectangle(size), g);
  }

  void paint(DLComponent c) {
    final Graphics g = getPanel().getGraphics();
    DLUtil.SetHints(g);
    paint(c, g);
  }

  void paint(Rectangle r) {
    final Graphics g = getPanel().getGraphics();
    DLUtil.SetHints(g);
    paint(r, g);
  }

  void paint(DLComponent c, Graphics g) {
    DLUtil.SetHints(g);
    Rectangle r = c.getBounds();
    if (paintControl.isPainting()) {
      final BufferedImage image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_RGB);
      final Color col = getPanel().getBackground();
      final Graphics2D gi = image.createGraphics();
      gi.setColor(col);
      gi.fillRect(0, 0, r.width, r.height);

      final AffineTransform tr = AffineTransform.getTranslateInstance(-r.x, -r.y);
      gi.setTransform(tr);

      DLUtil.SetHints(gi);

      final DLComponentList copy = components.copy();
      final Iterator<DLComponent> i = copy.iterator();
      while (i.hasNext()) {
        final DLComponent dlc = i.next();
        if (r.intersects(dlc.getBounds()))
          dlc.paint(gi);
      }
      g.drawImage(image, r.x, r.y, null);
    } else
      paintControl.addRectangle(r);
  }

  void paint(Rectangle r, Graphics g) {
    if (r.width <= 0 || r.height <= 0)
      // new Error(r.width + " " + r.height).printStackTrace();
      return;
    if (r.width > 100000 || r.height > 100000)
      return;
    DLUtil.SetHints(g);
    if (paintControl.isPainting()) {
      final BufferedImage image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_RGB);
      final Color c = getPanel().getBackground();
      final Graphics2D gi = image.createGraphics();
      gi.setColor(c);
      gi.fillRect(0, 0, r.width, r.height);

      final AffineTransform tr = AffineTransform.getTranslateInstance(-r.x, -r.y);
      gi.setTransform(tr);

      DLUtil.SetHints(gi);

      final DLComponentList copy = components.copy();
      final Iterator<DLComponent> i = copy.iterator();
      while (i.hasNext()) {
        final DLComponent dlc = i.next();
        if (r.intersects(dlc.getBounds()))
          dlc.paint(gi);
      }
      g.drawImage(image, r.x, r.y, null);
    } else
      paintControl.addRectangle(r);
  }

  void removeComponent(DLComponent o) {
    if (selection != null)
      selection.remove(o);
    final Rectangle r = o.getBounds();
    components.remove(o);
    o.parent = null;
    final Graphics g = getPanel().getGraphics();
    DLUtil.SetHints(g);
    paint(r, g);
  }

  @Override
  public void repaint(long t, int x, int y, int w, int h) {
    final Graphics g = getPanel().getGraphics();
    DLUtil.SetHints(g);
    paint(new Rectangle(x, y, w, h), g);
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
      final Iterator<DLComponent> i = selection.iterator();
      while (i.hasNext()) {
        final DLComponent c = i.next();
        if (c instanceof DLCurve) {
          final DLCurve cu = (DLCurve) c;

          final Rectangle r = cu.setSelectedGetRect(false);
          if (r != null)
            paintControl.addRectangle(r);
        }
      }
    }

    selection = sel;

    if (selection != null) {
      final Iterator<DLComponent> i = selection.iterator();
      while (i.hasNext()) {
        final DLComponent c = i.next();
        if (c instanceof DLCurve) {
          final DLCurve cu = (DLCurve) c;
          final Rectangle r = cu.setSelectedGetRect(true);
          if (r != null)
            paintControl.addRectangle(r);
        }
      }
    }
    paintControl.setPainting(true);
  }

  void startMoveComponent(final DLComponentList dlc, Point p) {
    final Point lp = p;
    mouse.stoplisten();

    final DLMouse dlm = new DLMouse(components, 2) {
      @Override
      public void mouseDragged(MouseEvent e) {
        final Point ep = e.getPoint();
        final int dx = ep.x - lp.x;
        final int dy = ep.y - lp.y;
        final Iterator<DLComponent> i = dlc.iterator();
        paintControl.setPainting(false);
        while (i.hasNext()) {
          final DLComponent c = i.next();
          final Rectangle r = moveComponent(c, dx, dy);
          paintControl.addRectangle(r);
        }
        paintControl.setPainting(true);
        lp.x = ep.x;
        lp.y = ep.y;
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        final Point ep = e.getPoint();
        final int dx = ep.x - lp.x;
        final int dy = ep.y - lp.y;
        final Iterator<DLComponent> i = dlc.iterator();
        paintControl.setPainting(false);
        while (i.hasNext()) {
          final DLComponent c = i.next();
          final Rectangle r = moveComponent(c, dx, dy);
          paintControl.addRectangle(r);
        }
        paintControl.setPainting(true);
        stoplisten();
        mouse.listen();
      }

    };
    dlm.listen(getPanel());
  }

  void startMoveComponent(Point p) {
    startMoveComponent(selection, p);
  }

}

class PaintControl {
  DrawLine drawLine;
  boolean painting = true;
  Rectangle rectangle = null;

  PaintControl(DrawLine dl) {
    drawLine = dl;
  }

  void addRectangle(Rectangle r) {
    if (painting) {
      final String m = "Adding rectangle while painting!";
      drawLine.toasterManager.showToaster(m);
      System.err.println(m);
    }
    if (rectangle == null)
      rectangle = (Rectangle) r.clone();
    else
      Rectangle2D.union(rectangle, r, rectangle);
  }

  public Rectangle getRectangle() {
    return rectangle;
  }

  public boolean isPainting() {
    return painting;
  }

  public void setPainting(boolean painting) {
    this.painting = painting;
    if (painting && rectangle != null) {
      drawLine.paint(rectangle);
      rectangle = null;
    }
  }

  public void setRectangle(Rectangle rectangle) {
    this.rectangle = rectangle;
  }
}

class MoveHandler implements InvocationHandler {
  private DLComponent component;

  MoveHandler(DLComponent c) {
    component = c;
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object o = method.invoke(component, args);
    DLPropertySheet ps = component.parent.ps;
    if (ps != null && method.getName().equals("move")) {
      ps.update("X", new Float(component.x + (Float) args[0]));
      ps.update("Y", new Float(component.y + (Float) args[1]));
    }
    return o;
  }
}
