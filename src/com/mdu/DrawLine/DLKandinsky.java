package com.mdu.DrawLine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class DLKandinsky extends DLImage {
  int threadSleep = 50;
  int frameCount = 1;
  Graphics g = new Graphics();

  public DLKandinsky() {
    super();
  }

  DLKandinsky(DLKandinsky src) {
    this();
  }

  public DLKandinsky(float x, float y) {
    super(x, y);
  }

  DLKandinsky copy() {
    return new DLKandinsky(this);
  }

  public void f(Graphics2D g, DLThread t) {
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      // synchronized (this) {
      step(g);
      // }

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

  void draw() {
    g.background(0xFEFFDB);
    g.smooth();

    g.noStroke();
    g.fill(200, 50, 74, 50);
    g.ellipse(400, 200, 400, 400);
    g.ellipse(330, 260, 300, 300);
    g.rect(200, 400, 100, 100);

    g.fill(50, 200, 74, 50);
    g.ellipse(100, 500, 500, 500);
    g.rect(300, 0, 200, 200);

    g.noFill();
    g.stroke(0xFF8EA8);
    g.ellipse(130, 130, 205, 205);
    g.ellipse(130, 130, 210, 210);
    g.ellipse(130, 130, 220, 220);
    g.ellipse(130, 130, 240, 240);
    g.fill(0xFF8EA8);
    g.ellipse(130, 130, 200, 200);

    g.noFill();
    g.stroke(0);
    g.ellipse(130, 130, 155, 155);
    g.ellipse(130, 130, 160, 160);

    g.noStroke();
    g.fill(0x000000);
    g.ellipse(130, 130, 150, 150);

    g.fill(0x6E2879);
    g.ellipse(130, 130, 60, 60);

    g.stroke(0x94BAED);
    g.noFill();
    g.ellipse(200, 470, 100, 100);
    g.ellipse(200, 470, 105, 105);

    g.noStroke();
    g.fill(0x94BAED);
    g.ellipse(200, 470, 95, 95);
    g.fill(0xFFF262);
    g.stroke(0);
    g.ellipse(200, 470, 60, 60);

    g.noFill();
    g.stroke(0xFFF262);
    g.ellipse(500, 270, 140, 140);
    g.ellipse(500, 270, 150, 150);
    g.noStroke();
    g.fill(0xFFF262);
    g.ellipse(500, 270, 120, 120);
    g.fill(0x94BAED);
    g.ellipse(500, 270, 80, 80);

    g.fill(0);
    g.ellipse(360, 420, 40, 40);
    g.ellipse(240, 360, 20, 20);

    g.fill(255);
    g.ellipse(420, 360, 25, 25);
    g.ellipse(140, 500, 30, 30);

    g.fill(200, 50, 74);
    g.ellipse(420, 100, 40, 40);

    g.noFill();
    g.stroke(0);
    g.strokeWeight(2);
    g.line(100, 420, 500, 400);
    g.line(420, 100, 400, 500);

    g.strokeWeight(1);
    g.line(0, 200, 600, 400);
    g.line(0, 280, 600, 420);
    g.line(0, 360, 600, 440);
    g.line(450, 160, 550, 100);
    g.line(450, 140, 550, 80);
    g.line(450, 120, 550, 60);
    g.line(100, 450, 200, 550);
    g.line(85, 450, 185, 550);
    g.line(70, 450, 170, 550);
    
    g.line(450, 500, 550, 500);
    g.line(450, 525, 550, 525);
    g.line(450, 550, 550, 550);
    g.line(475, 475, 475, 575);
    g.line(500, 475, 500, 575);
    g.line(525, 475, 525, 575);

    g.stroke(0x6E2879);
    g.strokeWeight(4);
    g.line(130, 130, 390, 520);

    g.noStroke();
    g.fill(0);
    g.rect(475, 500, 25, 25);
    g.fill(0x6E2879);
    g.rect(500, 500, 25, 25);

    g.fill(0);
    g.rect(150, 200, 20, 20);
    g.rect(190, 200, 20, 20);
    g.rect(170, 220, 20, 20);
    g.rect(210, 220, 20, 20);
    g.rect(150, 240, 20, 20);
    g.rect(190, 240, 20, 20);
    g.rect(170, 260, 20, 20);
    g.rect(210, 260, 20, 20);

    g.fill(255);
    g.rect(350, 250, 20, 20);
    g.rect(390, 250, 20, 20);
    g.rect(370, 270, 20, 20);
    g.rect(410, 270, 20, 20);
    g.rect(350, 290, 20, 20);
    g.rect(390, 290, 20, 20);
    g.rect(370, 310, 20, 20);
    g.rect(310, 310, 20, 20);

    g.fill(47, 63, 155);
    g.rect(100, 500, 60, 60);

    g.fill(255);
    g.triangle(75, 275, 275, 325, 95, 325);
    g.triangle(50, 350, 300, 350, 105, 375);

    g.noFill();
    g.stroke(0);
    g.arc(500, 300, 80, 80, g.radians(0), g.radians(180));
    g.arc(420, 300, 80, 80, g.radians(0), g.radians(180));
    g.arc(340, 300, 80, 80, g.radians(0), g.radians(180));
    g.arc(300, 300, 300, 300, g.radians(-90), g.radians(90));
    g.arc(300, 0, 300, 300, g.radians(-90), g.radians(90));
    g.arc(300, 600, 300, 300, g.radians(-90), g.radians(90));
    // quad(38, 31, 86, 20, 69, 63, 30, 76);
  }

  void step(Graphics2D g) {
    clearImage();
    this.g.set(g, this);
    draw();
  }

  public void randomize() {
    iwidth = 600;
    iheight = 600;
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

  public static void main(String[] a) {
    int w = 800;
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
        }
    };

    DLMain.Main(DLKandinsky.class, params);
  }

}
