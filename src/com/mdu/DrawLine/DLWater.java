package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import com.jhlabs.image.CausticsFilter;

public class DLWater extends DLImage {
  String imageResource = null; // "images/Terre.jpg";
  MemoryImageSource source;
  Image iimage;
  BufferedImage offImage;
  Graphics2D offGraphics;
  short ripplemap[];
  int texture[];
  int ripple[];
  int oldind, newind, mapind;

  int rippleIncrement = 512;
  int threadSleep = 10;
  int riprad = 3;
  boolean rain = false;
  int dataShift = 5;
  int dataAttenuation = 1024;

  public int getDataAttenuation() {
    return dataAttenuation;
  }

  public void setDataAttenuation(int dataAttenuation) {
    this.dataAttenuation = dataAttenuation;
  }

  public int[] rangeDataAttenuation() {
    return new int[] {
        1, 2048
    };
  }

  public int getDataShift() {
    return dataShift;
  }

  public void setDataShift(int dataShift) {
    this.dataShift = dataShift;
  }

  public int[] rangeDataShift() {
    return new int[] {
        1, 10
    };
  }

  public boolean getRain() {
    return rain;
  }

  TimerTask newTask() {
    return new TimerTask() {
      public void run() {
        int x = DLUtil.RangeRandom(0, iwidth);
        int y = DLUtil.RangeRandom(0, iheight);
        disturb(x, y);
        long delay = DLUtil.RangeRandom(1, rainDelay);
        timer.schedule(newTask(), delay);
      }
    };
  }

  Timer timer;

  public void setRain(boolean rain) {
    if (this.rain == rain)
      return;
    this.rain = rain;
    if (rain) {
      timer = new Timer();
      TimerTask task = newTask();
      long delay = DLUtil.RangeRandom(1, rainDelay);
      timer.schedule(task, delay);
    } else {
      if (timer != null) {
        timer.cancel();
        timer = null;
      }
    }
  }

  int rainDelay = 300;

  public int getRainDelay() {
    return rainDelay;
  }

  public void setRainDelay(int timerDelay) {
    this.rainDelay = timerDelay;
  }

  public int[] rangeRainDelay() {
    return new int[] {
        1, 1000
    };
  }

  public String getImageResource() {
    return imageResource;
  }

  public void setImageResource(String imageResource) {
    this.imageResource = imageResource;
    init();
  }

  public String[] enumImageResource() {
    URL url = getClass().getResource("images");
    File f;
    try {
      f = new File(url.toURI());
    } catch (URISyntaxException e) {
      f = new File(url.getPath());
    }
    String[] list = f.list();
    String[] items = new String[list.length + 1];
    for (int i = 0; i < list.length; i++)
      items[i] = "images/" + list[i];
    items[list.length] = null;
    return items;
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] {
        0, 100
    };
  }

  public int getRiprad() {
    return riprad;
  }

  public void setRiprad(int riprad) {
    this.riprad = riprad;
  }

  public int[] rangeRiprad() {
    return new int[] {
        1, 10
    };
  }

  public int getRippleIncrement() {
    return rippleIncrement;
  }

  public void setRippleIncrement(int rippleIncrement) {
    this.rippleIncrement = rippleIncrement;
  }

  public int[] rangeRippleIncrement() {
    return new int[] {
        1, 2048
    };
  }

  public void init() {
    BufferedImage im;
    im = loadBackgroundImage();

    int size = iwidth * (iheight + 2) * 2;
    ripplemap = new short[size];
    ripple = new int[iwidth * iheight];
    texture = new int[iwidth * iheight];
    oldind = iwidth;
    newind = iwidth * (iheight + 3);

    PixelGrabber pg = new PixelGrabber(im, 0, 0, iwidth, iheight, texture, 0, iwidth);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
    }

    source = new MemoryImageSource(iwidth, iheight, ripple, 0, iwidth);
    source.setAnimated(true);
    source.setFullBufferUpdates(true);

    iimage = Toolkit.getDefaultToolkit().createImage(source);
    offImage = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    offGraphics = offImage.createGraphics();
  }

  public void disturb(int x, int y) {
    for (int j = y - riprad; j < y + riprad; j++) {
      for (int k = x - riprad; k < x + riprad; k++) {
        if (j >= 0 && j < iheight && k >= 0 && k < iwidth) {
          ripplemap[oldind + (j * iwidth) + k] += rippleIncrement;
        }
      }
    }
  }

  boolean mouse(MouseEvent e) {

    float x = e.getX() - (this.x - iwidth / 2f);
    float y = e.getY() - (this.y - iheight / 2f);

    int mx = (int) x;
    int my = (int) y;
    if (e instanceof MouseWheelEvent) {
      disturb(mx, my);
      return true;
    }
    switch (e.getID()) {
    case MouseEvent.MOUSE_DRAGGED:
      disturb(mx, my);
      return true;
    case MouseEvent.MOUSE_PRESSED:
      disturb(mx, my);
      return true;
    case MouseEvent.MOUSE_RELEASED:
      disturb(mx, my);
      return true;
    default:
      return true;
    }
  }

  public void newframe() {
    int i = oldind;
    oldind = newind;
    newind = i;

    i = 0;
    mapind = oldind;
    for (int y = 0; y < iheight; y++) {
      for (int x = 0; x < iwidth; x++) {
        short data = (short) ((ripplemap[mapind - iwidth] + ripplemap[mapind + iwidth] + ripplemap[mapind - 1]
            + ripplemap[mapind + 1]) / 2);
        data -= ripplemap[newind + i];
        data -= data >> dataShift;
        ripplemap[newind + i] = data;

        // where data=0 then still, where data>0 then wave
        data = (short) (dataAttenuation - data);

        // offsets
        int a = ((x - iwidth / 2) * data / dataAttenuation) + iwidth / 2;
        int b = ((y - iheight / 2) * data / dataAttenuation) + iheight / 2;

        // bounds check
        if (a >= iwidth)
          a = iwidth - 1;
        if (a < 0)
          a = 0;
        if (b >= iheight)
          b = iheight - 1;
        if (b < 0)
          b = 0;

        ripple[i] = texture[a + (b * iwidth)];
        mapind++;
        i++;
      }
    }
  }

  public DLWater() {
    super();
    init();
  }

  DLWater(DLWater src) {
    this();
  }

  public DLWater(float x, float y) {
    super(x, y);
    init();
  }

  DLWater copy() {
    return new DLWater(this);
  }

  public void reset() {
    super.reset();
    init();
  }
  
  long frameCount = 0;

  public void f(Graphics2D g, DLThread t) {
    while (frameCount++ >= 0) {
      if (t != null && t.isStopped())
        break;
      try {
      newframe();
      } catch (Exception e) {
        // ignore
      }
      if (t != null && t.isStopped())
        break;
      source.newPixels();
      if (t != null && t.isStopped())
        break;
      offGraphics.drawImage(iimage, 0, 0, iwidth, iheight, null);
      if (t != null && t.isStopped())
        break;
      if (parent != null) {
        parent.paint(this);
      }
      if (threadSleep > 0) {
        // System.err.println("threadSleep " + threadSleep);
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void paint(Graphics g) {
    g.drawImage(offImage, (int) (x - iwidth / 2), (int) (y - iheight / 2), iwidth, iheight, null); 
  }

  BufferedImage loadBackgroundImage() {
    BufferedImage img = null;

    if (imageResource != null) {
      try {
        img = ImageIO.read(getClass().getResource(imageResource));
        double iw = img.getWidth();
        double ih = img.getHeight();
        double r = Math.min(iwidth / iw, iheight / ih);
        int siw = (int) (r * iw + 0.5);
        int sih = (int) (r * ih + 0.5);
        img = DLUtil.GetScaledInstance(img, siw, sih);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      if (iwidth == 0 || iheight == 0)
        randomize();
      img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
      CausticsFilter cf = new CausticsFilter();
      cf.setBrightness(20);
      img = cf.filter(img, img);
    }
    return img;
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
    super.randomize();
    final int i = DLUtil.RangeRandom(200, 400);
    iwidth = i;
    iheight = i;
    init();
    setShadow(true);
  }
  

  public static void main(String[] a) {
    int w = 600;
    int h = 600;
    Object[][] params = {
      {
        "iwidth", w
      }, {
        "iheight", h
      }, {
        "x", w / 2
      }, {
        "y", h / 2
      }, {
        "threadSleep", 5
      }, {
        "backgroundColor", new Color(53, 53, 20).brighter().brighter().brighter()
      }
    };
    Class<? extends DLComponent> cls = DLWater.class;
    DLMain.Main(cls, params);
  }
}
