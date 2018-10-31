package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class DLWave extends DLImage {
  Color backgroundColor;
  int threadSleep = 10;

  public DLWave() {
    super();
  }

  DLWave(DLWave src) {
    this();
  }

  public DLWave(float x, float y) {
    super(x, y);
  }

  DLWave copy() {
    return new DLWave(this);
  }

  void clearImage() {
    if (backgroundColor == null)
      super.clearImage();
    else {
      Graphics2D g = image.createGraphics();
      g.setColor(backgroundColor);
      g.fillRect(0, 0, iwidth, iheight);
    }
  }

  void paintFrame(Graphics2D g) {
    g.setColor(Color.black);
    g.drawRect(0, 0, iwidth - 1, iheight - 1);
  }

  public void f(Graphics2D g, DLThread t) {
    
    while (1 > 0) {
      
      if (t != null && t.isStopped())
        break;

      synchronized (this) {
        clearImage();
        step(g);
        paintFrame(g);
      }
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

  float a;

  static int Int(double a) {
    return (int) Math.round(a);
  }

  static int Int(float a) {
    return (int) a;
  }

  void step(Graphics2D g) {
    /*
    a is decreased by 0.08. It represents the amount of radians the height of our boxes changes, and their speed.
    If we did nothing to a, then none of our shapes will move, so a is a key component in our formulas.
    */
    a -= 0.08f;

    /*
    These are our loops.
    We loop through 14 rows (-7 through 7) for the x axis, and within each row, we loop through 14 collumns for the z axis
    (x,z) is the ground, while y is verticle)
    */
    int sept = 7;
    for (int x = -sept; x < sept; x++) {
      for (int z = -sept; z < sept; z++) {
        /*
        The y variable is set to determine the height of the box.
        We use formula radius * cos(angle) to determine this.
        Since cosine, when graphed, creates a wave, we can use this to have the boxes transition from small to big smoothly.
         
        The radius pretty much stands for our range. cosine alone will return values between -1 and 1, so we multiply this by
        24 to increase this value. The formula will return something in between -24 and 24.
         
        The angle is in radians. An entire loop (circle) is 2pi radians, or roughly 6.283185.
        Distance is used to create the circular effect. It makes the boxes of the same radius around the center similar.
        The distance ranges from 0 to 7, so 0.55 * distance will be between 0 and 3.85. This will make the highest and lowest
        box a little more than half a loop's difference. a is added on, (subtracted if you want to be technical, since a is negative), to
        provide some sort of change for each frame. If we don't include '+ a' in the algorithm, the boxes would be still.
        */
        float angle = 0.55f * distance(x, z, 0, 0) + a;
        int vingtquatre = 20;
        int y = Int(vingtquatre * Math.cos(angle));

        /*
        These are 2 coordinate variations for each quadrilateral.
        Since they can be found in 4 different quadrants (+ and - for x, and + and - for z),
        we'll only need 2 coordinates for each quadrilateral (but we'll need to pair them up differently
        for this to work fully).
         
        Multiplying the x and z variables by 17 will space them 17 pixels apart.
        The 8.5 will determine half the width of the box ()
        8.5 is used because it is half of 17. Since 8.5 is added one way, and 8.5 is subtracted the other way, the total
        width of each box is 17. This will eliminate any sort of spacing in between each box.
         
        If you enable noStroke(), then the whole thing will appear as one 3d shape. Try it.
        */
        int dixsept = 10;
        float xm = x * dixsept - dixsept / 2;
        float xt = x * dixsept + dixsept / 2;
        float zm = z * dixsept - dixsept / 2;
        float zt = z * dixsept + dixsept / 2;

        /* We use an integer to define the width and height of the window. This is used to save resources on further calculating */
        int halfw = iwidth / 2;
        int halfh = iheight / 2;

        /*
        Here is where all the isometric calculating is done.
        We take our 4 coordinates for each quadrilateral, and find their (x,y) coordinates using an isometric formula.
        You'll probably find a similar formula used in some of my other isometric animations. However, I normally use
        these in a function. To avoid using repetitive calculation (for each coordinate of each quadrilateral, which
        would be 3 quads * 4 coords * 3 dimensions = 36 calculations).
         
        Formerly, the isometric formula was ((x - z) * cos(radians(30)) + width/2, (x + z) * sin(radians(30)) - y + height/2).
        however, the cosine and sine are constant, so they could be precalculated. Cosine of 30 degrees returns roughly 0.866, which can round to 1,
        Leaving it out would have little artifacts (unless placed side-by-side to accurate versions, where everything would appear wider in this version)
        Sine of 30 returns 0.5.
         
        We left out subtracting the y value, since this changes for each quadrilateral coordinate. (-40 for the base, and our y variable)
        These are later subtracted in the actual quad().
        */
        int isox1 = Int(xm - zm + halfw);
        int isoy1 = Int((xm + zm) * 0.5f + halfh);
        int isox2 = Int(xm - zt + halfw);
        int isoy2 = Int((xm + zt) * 0.5f + halfh);
        int isox3 = Int(xt - zt + halfw);
        int isoy3 = Int((xt + zt) * 0.5f + halfh);
        int isox4 = Int(xt - zm + halfw);
        int isoy4 = Int((xt + zm) * 0.5f + halfh);

        /* The side quads. 2 and 4 is used for the coloring of each of these quads */
        float darkFactor = 1.4f;
        g.setColor(DLUtil.GetGrey(2));
        DLPath f = new DLPath();
        f.moveTo(isox2, isoy2 - y);
        f.lineTo(isox3, isoy3 - y);
        f.lineTo(isox3, isoy3 + 40);
        f.lineTo(isox2, isoy2 + 40);
        f.closePath();
        g.fill(f);
        g.setColor(DLUtil.DarkerColor(g.getColor(), darkFactor));
        g.draw(f);

        g.setColor(DLUtil.GetGrey(4));
        f.reset();
        f.moveTo(isox3, isoy3 - y);
        f.lineTo(isox4, isoy4 - y);
        f.lineTo(isox4, isoy4 + 40);
        f.lineTo(isox3, isoy3 + 40);
        f.closePath();
        g.fill(f);
        g.setColor(DLUtil.DarkerColor(g.getColor(), darkFactor));
        g.draw(f);

        /*
        The top quadrilateral.
        y, which ranges between -24 and 24, multiplied by 0.05 ranges between -1.2 and 1.2
        We add 4 to get the values up to between 2.8 and 5.2.
        This is a very fair shade of grays, since it doesn't become one extreme or the other.
        */
        g.setColor(DLUtil.GetGrey(4 + y * 0.05f));
        f.reset();
        f.moveTo(isox1, isoy1 - y);
        f.lineTo(isox2, isoy2 - y);
        f.lineTo(isox3, isoy3 - y);
        f.lineTo(isox4, isoy4 - y);
        f.closePath();
        g.fill(f);
        g.setColor(DLUtil.DarkerColor(g.getColor(), darkFactor));
        g.draw(f);
      }
    }
  }

  float sq(float x) {
    return x * x;
  }

  float distance(float x, float y, float cx, float cy) {
    return (float) Math.sqrt(sq(cx - x) + sq(cy - y));
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 500);
    iheight = iwidth;
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

}
