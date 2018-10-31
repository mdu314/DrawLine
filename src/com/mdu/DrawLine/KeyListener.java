package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RandomColor;
import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.awt.event.KeyEvent.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

class DLKeyListener implements KeyListener {
  DLContainer dl;

  DLKeyListener(DLContainer dl) {
    this.dl = dl;
  }

  private void vk_j() {
    final DLComponentList s = dl.hitTest(dl.getMousePosition());
    if (s != null && s.size() == 1) {
      DLComponent d = s.get(0);
      if (d instanceof JPG) {
        JFileChooser fs = new JFileChooser();
        int rv = fs.showOpenDialog(dl);
        if (rv == JFileChooser.APPROVE_OPTION) {
          ((JPG) d).save(fs.getSelectedFile());
        }
      }
    }
  }
  
  @Override
  public void keyPressed(KeyEvent e) {
    // System.err.println(e);
    DLComponent c;
    float x;
    float y;
    
    switch (e.getKeyCode()) {
    case KeyEvent.VK_J: {
      vk_j(); 
      break;
    }
    case KeyEvent.VK_I:
      final Point pt = dl.getMousePosition();
      final DLComponentList s = dl.hitTest(pt);
      if (s != null && s.size() == 1)
        s.get(0).dump();
      break;
    case KeyEvent.VK_2:
      final Color b1 = RandomColor(0, 1, 0f, 0.5f, 0.5f, 1);
      dl.setBackground(b1);

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          dl.paintControl.paint();
        }
      });
      break;
    case KeyEvent.VK_1:
      final Color b2 = RandomColor(0, 1, 0.5f, 0.8f, 0, 0.5f);
      dl.setBackground(b2);

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          dl.paintControl.paint();
        }
      });
      break;
    case KeyEvent.VK_3:
      final Color b3 = RandomColor(0, 1, 0.6f, 1f, 0.8f, 1f);
      dl.setBackground(b3);

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          dl.paintControl.paint();
        }
      });
      break;
    case KeyEvent.VK_X: {
      Component p = dl;
      int w = p.getWidth();
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
      dl.addComponent((DLComponent) pl);
    }
      break;
    case KeyEvent.VK_Z: {
      final Component p = dl;
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
      final int dd = 5;
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
      dl.addComponent((DLComponent) pl);
    }
      break;
    case KeyEvent.VK_Q:
      dl.paintControl.setPainting(false);
      for (int i = 0; i < 10; i++) {
        DLComponent dlc = DrawLine.makeARandomCurve(dl);
        dl.addComponent(dlc);
      }
      dl.paintControl.setPainting(true);
      break;
    case KeyEvent.VK_R:
      DLComponent dlc = DrawLine.makeARandomCurve(dl);
      dl.addComponent(dlc);
      break;
    case KeyEvent.VK_T:
      paintTexture();
      paintImageTexture();
      break;
    case KeyEvent.VK_E:
      paintTexture();
      paintImageTexture();
      texturePreview(3);
      break;
    case KeyEvent.VK_F:
      DrawLine.fill(dl);
      break;
    case KeyEvent.VK_D:
      long time = System.currentTimeMillis();
      dl.paintControl.paint();
      time = System.currentTimeMillis() - time;
      break;
    case KeyEvent.VK_C:
      dl.clear();
      dl.paintControl.paint();
      break;
    case KeyEvent.VK_PLUS:
    case KeyEvent.VK_P:
      final Rectangle r = dl.getBounds();
      final double margin = 20;
      x = (float) Math.floor(DLUtil.Normalize(margin, r.width - margin, 0., 1., Math.random()));
      y = (float) Math.floor(DLUtil.Normalize(margin, r.height - margin, 0., 1., Math.random()));
      dlc = DrawLine.makeARandomCurve(x, y);
      dl.addComponent(dlc);
      break;
    case KeyEvent.VK_DELETE:
    case KeyEvent.VK_MINUS:
      if (dl.components.size() <= 0)
        return;
      c = dl.components.get(dl.components.size() - 1);
      if (dl.selection != null)
        dl.selection.remove(c);
      dl.removeComponent(c);
      dl.paint(c.getBounds());
      break;
    case KeyEvent.VK_S:
      if (dl.selection != null) {
        final Iterator<DLComponent> i = dl.selection.iterator();
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
      break;
    }
  }

  @Override
  public void keyReleased(KeyEvent a) {
    // System.err.println(a);
  }

  @Override
  public void keyTyped(KeyEvent a) {
    // System.err.println(a);
  }

  void paintTexture() {
    Component c = dl;
    Dimension size = c.getSize();
    Graphics g = c.getGraphics();
    paintTexture(new Rectangle(size), g);
  }

  private void paintTexture(Rectangle r, Graphics g) {
    if (r.width <= 0 || r.height <= 0)
      return;
    if (r.width > 100000 || r.height > 100000)
      return;
    DLUtil.SetHints(g);
    if (dl.paintControl.isPainting()) {
      final BufferedImage image = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_RGB);
      final Color c = dl.getBackground();
      final Graphics2D gi = image.createGraphics();
      gi.setColor(c);
      gi.fillRect(0, 0, r.width, r.height);

      final AffineTransform tr = AffineTransform.getTranslateInstance(-r.x, -r.y);
      gi.setTransform(tr);

      DLUtil.SetHints(gi);

      final DLComponentList copy = dl.components.copy();
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
      dl.paintControl.addRectangle(r);
  }

  void paintImageTexture() {
    JFileChooser fs = new JFileChooser();

    fs.setFileFilter(new FileFilter() {

      public String getDescription() {
        return "Portable Network Graphics files";
      }

      public boolean accept(File f) {
        System.err.println(f.getName() + " " + f.getName().endsWith(".png"));
        if (f.getName().toLowerCase().endsWith(".png"))
          return true;
        return false;
      }
    });

    int rv = fs.showSaveDialog(dl);
    if (rv == JFileChooser.APPROVE_OPTION) {
      Component c = dl;
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
    Component c = dl;
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

};
