package com.mdu.DrawLine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class DLNeuralNet extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;

  public static void main(String[] a) {
    int w = 800;
    int h = 600;
    int w2 = w / 2;
    int h2 = h / 2;

    Object[][] params = {
        {
            "width", w
        }, {
            "height", h
        }, {
            "iwidth", w
        }, {
            "iheight", h
        }, {
            "x", w2
        }, {
            "y", h2
        }, {
            "threadSleep", 5
        }
    };

    DLMain.Main(DLNeuralNet.class, params);
  }
  public DLNeuralNet() {
    super();
  }

  DLNeuralNet(DLNeuralNet src) {
    this();
  }

  public DLNeuralNet(float x, float y) {
    super(x, y);
  }

  DLNeuralNet copy() {
    return new DLNeuralNet(this);
  }

  public void f(Graphics2D g, DLThread t) {
    setup();
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      clearImage();
      draw(g);

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

  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  public void randomize() {
    iwidth = 600;
    iheight = 400;
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

  void setup() {

    n = new Neuron[400];

    for (int i = 0; i < n.length; i++)
      n[i] = new Neuron(i, random(iwidth), random(iheight));

    for (int i = 0; i < n.length; i++)
      n[i].makeSynapse();

    for (int i = 0; i < n[0].s.length; i++)
      n[0].makeSignal(i);

  }

  void mousePressed() {

    for (int i = 0; i < n.length; i++) {
      n[i].x = random(iwidth);
      n[i].y = random(iheight);
    }

    for (int i = 0; i < n.length; i++)
      n[i].makeSynapse();

    for (int i = 0; i < n[0].s.length; i++)
      n[0].makeSignal(i);

  }

  void draw(Graphics2D g) {

    for (int i = 0; i < n.length; i++)
      n[i].draw(g);

  }

  float noise(float x) {
    float v = DLUtil.RandomGauss(0, 0.1f);
    return v;
  }

  float norm(float v, float min, float max) {
    return DLUtil.Normalize(0, 1, min, max, v);
  }

  float random(int v) {
    return DLUtil.RangeRandom(0, v);
  }

  float random(float v1, float v2) {
    return DLUtil.RangeRandom(v1, v2);
  }

  float dist(float x1, float y1, float x2, float y2) {
    return DLUtil.EuclideanDist(x1, y1, x2, y2);
  }

  int lerpColor(int c1, int c2, float v) {
    int r1 = (c1 >> 16) & 0xff;
    int r2 = (c2 >> 16) & 0xff;
    int g1 = (c1 >> 8) & 0xff;
    int g2 = (c2 >> 8) & 0xff;
    int b1 = c1 & 0xff;
    int b2 = c2 & 0xff;

    int r = (int) DLUtil.Normalize(r1, r2, 0f, 1f, v) & 0xff;
    int g = (int) DLUtil.Normalize(g1, g2, 0f, 1f, v) & 0xff;
    int b = (int) DLUtil.Normalize(b1, b2, 0f, 1f, v) & 0xff;

    return r << 16 | g << 8 | b;
  }

  Color lerpColor(Color c1, Color c2, float v) {
    int r1 = c1.getRed();
    int r2 = c2.getRed();
    int g1 = c1.getGreen();
    int g2 = c2.getGreen();
    int b1 = c1.getBlue();
    int b2 = c2.getBlue();
    int r = (int) DLUtil.Normalize(r1, r2, 0f, 1f, v);
    int g = (int) DLUtil.Normalize(g1, g2, 0f, 1f, v);
    int b = (int) DLUtil.Normalize(b1, b2, 0f, 1f, v);
    return new Color(r, g, b);
  }

  Neuron n[];
  Signal s[];

  class Neuron {
    int id;
    float x, y, val, xx, yy;
    float radius = 60.0f;

    Synapse s[];
    Signal sig[];

    Neuron(int _id, float _x, float _y) {
      val = DLUtil.RangeRandom(255f);
      id = _id;
      xx = x = _x;
      yy = y = _y;
    }

    Object[] expand(Object[] a, int len) {
      return Arrays.copyOf(a, len);
    }

    void makeSynapse() {
      s = new Synapse[0];
      sig = new Signal[0];

      for (int i = 0; i < n.length; i++) {
        if (i != id && dist(x, y, n[i].x, n[i].y) <= radius && noise(i / 100f) < 0.8f) {
          s = (Synapse[]) expand(s, s.length + 1);
          s[s.length - 1] = new Synapse(id, i);

          sig = (Signal[]) expand(sig, sig.length + 1);
          sig[sig.length - 1] = new Signal(s[s.length - 1]);

        }
      }
    }

    void makeSignal(int which) {
      int i = which;
      sig[i].x = xx;
      sig[i].y = yy;
      sig[i].running = true;
    }

    void drawSynapse(Graphics2D g) {
      if (sig.length > 0) {
        for (int i = 0; i < sig.length; i += 1) {
          if (sig[i].running) {
            g.setStroke(new BasicStroke(0.5f));
            g.setPaint(Color.blue);
            Line2D.Float l = new Line2D.Float(sig[i].x, sig[i].y, sig[i].lx, sig[i].ly);
            g.draw(l);
            sig[i].step(g);
          }
        }
      }
      int lerp = lerpColor(0xffcc11, 0xffffff, norm(val, 0, 255));
      Color c = new Color(lerp);
      g.setPaint(c);
      for (int i = 0; i < s.length; i += 1) {
        Line2D.Float l = new Line2D.Float(n[s[i].B].xx, n[s[i].B].yy, xx, yy);
        g.draw(l);
      }
    }

    float dix = 2;
    
    void draw(Graphics2D g) {
      drawSynapse(g);
      xx += (x - xx) / dix;
      yy += (y - yy) / dix;
    }
  
  }

  class Synapse {

    float weight = 1.5f;
    int A, B;

    Synapse(int _A, int _B) {

      A = _A;
      B = _B;

      weight = random(101, 1100) / 300.9f;
    }

  }

  class Signal {

    Synapse base;
    int cyc = 0;
    float x, y, lx, ly;
    float speed = 7.1f;

    boolean running = false;
    boolean visible = true;

    int deadnum = 300;
    int deadcount = 0;

    Signal(Synapse _base) {
      deadnum = (int) random(2, 400);
      base = _base;
      lx = x = n[base.A].x;
      ly = y = n[base.A].y;
      speed *= base.weight;
    }

    void step(Graphics2D g) {
      running = true;

      lx = x;
      ly = y;

      x += (n[base.B].xx - x) / speed;// (speed+(dist(n[base.A].x,n[base.A].y,n[base.B].x,n[base.B].y)+1)/100.0);
      y += (n[base.B].yy - y) / speed;// (speed+(dist(n[base.A].x,n[base.A].y,n[base.B].x,n[base.B].y)+1)/100.0);

      if (dist(x, y, n[base.B].x, n[base.B].y) < 1.0) {

        if (deadcount < 0) {
          deadcount = deadnum;
          for (int i = 1; i < 10; i++) {
            float is = i / 2f;

            float cx = x;
            float cy = y;
            float radius = i / 2f;
            float[] fractions = new float[]{0, 1};
            Color[] colors = new Color[]{Color.black, new Color(0x00CCAC00, true)};
            RadialGradientPaint rgp = new RadialGradientPaint(cx, cy, radius, fractions, colors);
            g.setPaint(rgp);
            
            Ellipse2D.Float e = new Ellipse2D.Float(x - is, y - is, i, i);
            g.fill(e);
          }

          running = false;
          for (int i = 0; i < n[base.B].s.length; i++) {
            if (!n[base.B].sig[i].running && base.A != n[base.B].sig[i].base.B) {
              n[base.B].makeSignal(i);
              n[base.B].sig[i].base.weight += (base.weight - n[base.B].sig[i].base.weight)
                  / ((dist(x, y, n[base.A].xx, n[base.A].yy) + 1.0) / 200.0);
            }

          }

          // base.weight = random(1001,3000) / 1000.0;

          n[base.A].xx += ((n[base.B].x - n[base.A].x) / 1.1) * noise((frameCount + n[base.A].id) / 11.0f);

          n[base.A].yy += ((n[base.B].y - n[base.A].y) / 1.1) * noise((frameCount + n[base.A].id) / 10.0f);

          n[base.A].xx -= ((n[base.B].x - n[base.A].x) / 1.1) * noise((frameCount + n[base.B].id) / 10.0f);

          n[base.A].yy -= ((n[base.B].y - n[base.A].y) / 1.1) * noise((frameCount + n[base.B].id) / 11.0f);

          lx = n[base.A].xx;
          ly = n[base.A].yy;

          n[base.A].val += (n[base.B].val - n[base.A].val) / 5.0;
        } else {
          deadcount--;
        }
      }
    }
  }
}
