package com.mdu.DrawLine;

import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON2;
import static java.awt.event.MouseEvent.BUTTON3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jhlabs.image.EdgeFilter;

public class DLRgbDaze extends DLImage {
  int[] pixels;
  private int threadSleep = 50;
  private int frameCount = 0;
  private BufferedImage filterImage;
  float range = 1000;
  float incre = 0.1f;
  float baseDegree = 0;

  public float getBaseDegree() {
    return baseDegree;
  }

  public void setBaseDegree(float baseDegree) {
    this.baseDegree = baseDegree;
  }

  public float[] rangeBaseDegree() {
    return new float[] { 0f, 360f };
  }

  public float getRange() {
    return range;
  }

  public void setRange(float range) {
    this.range = range;
  }

  public float[] rangeRange() {
    return new float[] { 1, 5000 };
  }

  public float getIncre() {
    return incre;
  }

  public void setIncre(float incre) {
    this.incre = incre;
  }

  public float[] rangeIncre() {
    return new float[] { 0.0001f, 1f };
  }

  public DLRgbDaze() {
    super();
  }

  DLRgbDaze(DLRgbDaze src) {
    this();
  }

  public DLRgbDaze(float x, float y) {
    super(x, y);
  }

  DLRgbDaze copy() {
    return new DLRgbDaze(this);
  }

  public void f(Graphics2D g, DLThread t) {
    setup();

    DLUtil.SetHints(g);

    while (frameCount++ >= 0) {

      if (t != null && t.isStopped())
        break;

      try {
        step(g);
      } catch (Throwable b) {
        System.err.println(b);
      }

      g = filter(g);

      if (parent != null)
        parent.paint(this);

      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          System.err.println(e);
        }
      }
    }
  }

  void step(Graphics2D g) {
    // clearImage();
    draw(g);
  }

  BufferedImage image() {
    filterImage = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();

    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(400, 500);
    iheight = DLUtil.RangeRandom(400, 500);
    threadSleep = 10;
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] { 0, 100 };
  }

  Graphics2D filter(Graphics2D g) {
    if (filterStrength > 0) {
      EdgeFilter ef = new EdgeFilter();
      /* BufferedImage filterImage = */ef.filter(image, filterImage);
      image = DLUtil.Merge(image, filterImage, filterStrength, null);
      return image.createGraphics();
    }
    return g;
  }

  void setup() {
    pixels = new int[iwidth * iheight];
  }

  float dist(float x1, float y1, float x2, float y2) {
    // return DLUtil.SquareDist(x1, y1, x2, y2);
    return DLUtil.EuclideanDist(x1, y1, x2, y2);
    // return DLUtil.ManhattanDist(x1, y1, x2, y2);
    // return DLUtil.Tchebychev(x1, y1, x2, y2);
    // return DLUtil.Cubic(x1, y1, x2, y2);
  }

  float sin(float f) {
    return DLUtil.Sin(f);
    // return (float)Math.sin(f);
  }

  float cos(float f) {
    return DLUtil.Cos(f);
    // return (float)Math.sin(f);
  }

  void draw(Graphics2D gr) {
    float base = DLUtil.Sin(DLUtil.Radian(baseDegree)) * range;

    for (int i = 0; i <= iwidth / 2; i++) {
      for (int j = 0; j <= iheight / 2; j++) {
        int index1 = i + j * iwidth;
        int index2 = iwidth - 1 - i + j * iwidth;
        int index3 = i + (iheight - 1 - j) * iwidth;
        int index4 = iwidth - 1 - i + (iheight - 1 - j) * iwidth;
        float r = 255f * sin(DLUtil.Radian(base * dist(i, j, iwidth / 2, iheight / 2)));
        float g = 255f * cos(DLUtil.Radian(base * dist(i, j, 0, 0)));
        float b = 255f * sin(DLUtil.Radian(base * dist(i, j, iwidth, iheight)));        
        pixels[index1] = color(r, g, b);
        pixels[index2] = color(r, g, b);
        pixels[index3] = color(r, g, b);
        pixels[index4] = color(r, g, b);
      }
    }
    baseDegree += incre;
    updatePixels(gr);
  }

  int color(float r, float g, float b) {
    int ir = DLUtil.Floor((r + 255f) / 2f);
    int ig = DLUtil.Floor((g + 255f) / 2f);
    int ib = DLUtil.Floor((b + 255f) / 2f);
    return 0xff << 24 | ir << 16 | ig << 8 | ib;
  }

  void updatePixels(Graphics2D gr) {
    image.setRGB(0, 0, iwidth, iheight, pixels, 0, iwidth);
  }

  public static void main(String[] a) {
    final JFrame frame = new JFrame();
    final DLContainer panel = new DLContainer();
    panel.setFocusable(true);
    panel.setBackground(new Color(0x0c0c0c));
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setFocusable(true);
    frame.setSize(800, 600);
    panel.setBackground(new Color(0xc0c0c0));
    final DLRgbDaze dlg = new DLRgbDaze(400, 300);
    dlg.iwidth = 800;
    dlg.iheight = 600;
    dlg.threadSleep = 5;
    panel.addComponent(dlg);

    DLMouse mouse = new DLMouse(panel) {
      public void mouseClicked(MouseEvent e) {
        switch (e.getButton()) {
        case BUTTON2:
        case BUTTON3:
        case BUTTON1:
          if (panel.ps != null)
            panel.ps.close();
          panel.ps = new DLPropertySheet(dlg);
          break;
        }
      }
    };

    mouse.listen(panel);

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = frame.getSize().width;
        final int h = frame.getSize().height;
        final int x = (dim.width - w) / 2;
        final int y = (dim.height - h) / 2;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(x, y);
        frame.setVisible(true);
      }
    });
  }
}
