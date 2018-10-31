package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class DLCircusFluid extends DLImage {
  int threadSleep = 10;
  int frameCount = 1;
  int cellSize = 4;
  // Use 2 dimensional arrays to store velocity and density for each pixel.
  // To access, use this: grid[x/cellSize][y/cellSize]
  float[][] velocity;
  float[][] density;
  float[][] oldVelocity;
  float[][] oldDensity;
  long previousTime = System.currentTimeMillis();
  long currentTime;

  float timeScale = 0.5f; // Play with this to slow down or speed up the fluid
                          // (the higher, the faster)
  int fixedDeltaTime = (int) (10 / timeScale);
  float fixedDeltaTimeSeconds = (float) fixedDeltaTime / 1000;
  float leftOverDeltaTime = 0;
  float friction = 0.58f;
  float speed = 20f;

  public int getCellSize() {
    return cellSize;
  }

  public void setCellSize(int cs) {
    cellSize = cs;
    setup();
  }

  public int[] rangeCellSize() {
    return new int[] { 1, 10 };
  }

  public float getTimeScale() {
    return timeScale;
  }

  public void setTimeScale(float timeScale) {
    this.timeScale = timeScale;
    setup();
  }

  public float[] rangeTimeScale() {
    return new float[] { 0.01f, 2f };
  }

  public float getFriction() {
    return friction;
  }

  public void setFriction(float friction) {
    this.friction = friction;
    setup();
  }

  public float[] rangeFriction() {
    return new float[] { 0.01f, 1 };
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
    setup();
  }

  public float[] rangeSpeed() {
    return new float[] { 10, 30 };
  }

  public DLCircusFluid() {
    super();
  }

  DLCircusFluid(DLCircusFluid src) {
    this();
  }

  public DLCircusFluid(float x, float y) {
    super(x, y);
  }

  DLCircusFluid copy() {
    return new DLCircusFluid(this);
  }

  void setup() {
    fixedDeltaTime = (int) (10 / timeScale);
    fixedDeltaTimeSeconds = (float) fixedDeltaTime / 1000;
    leftOverDeltaTime = 0;
    velocity = new float[DLUtil.Floor(iwidth / cellSize)][DLUtil.Floor(iheight / cellSize)];
    density = new float[DLUtil.Floor(iwidth / cellSize)][DLUtil.Floor(iheight / cellSize)];
  }

  public void f(Graphics2D g, DLThread t) {
    setup();
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      // clearImage();
      step(g);

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

  void step(Graphics2D g) { // Calculate amount of time since last frame (Delta
                            // means "change in")
    currentTime = System.currentTimeMillis();
    long deltaTimeMS = (long) ((currentTime - previousTime));
    previousTime = currentTime; // reset previousTime

    // timeStepAmt will be how many of our fixedDeltaTimes we need to make up
    // for the passed time since last frame.
    int timeStepAmt = (int) (((float) deltaTimeMS + leftOverDeltaTime) / (float) (fixedDeltaTime));

    // If we have any left over time left, add it to the leftOverDeltaTime.
    leftOverDeltaTime += deltaTimeMS - (timeStepAmt * (float) fixedDeltaTime);

    if (timeStepAmt > 15) {
      timeStepAmt = 15; // too much accumulation can freeze the program!
      System.err.println("Time step amount too high");
    }

    for (int iteration = 1; iteration <= timeStepAmt; iteration++) {
      solve(fixedDeltaTimeSeconds * timeScale);
    }
    draw(g);
  }

  public void randomize() {
    iwidth = 300; // DLUtil.RangeRandom(500, 500);
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

  void draw(Graphics2D g) {
    try {
      for (int x = 0; x < velocity.length; x++) {
        for (int y = 0; y < velocity[x].length; y++) {

          float a = density[x][y] * 0.0004f;
          a = a % DLUtil.TWO_PI;
          int red = DLUtil.Floor(127 + 127 * DLUtil.sin(a));
          int green = 127;
          a = velocity[x][y] * 0.1f;
          a = a % DLUtil.TWO_PI;
          int blue = DLUtil.Floor(127 + 127 * DLUtil.sin(a));
          if (red > 255) {
            System.err.println("red " + red);
            red = 255;
          }
          if (red < 0) {
            System.err.println("red " + red);
            red = 0;
          }
          if (green > 255) {
            System.err.println("green " + green);
            green = 255;
          }
          if (green < 0) {
            System.err.println("green " + green);
            green = 0;
          }
          if (blue > 255) {
            System.err.println("blue " + blue);
            blue = 255;
          }
          if (blue < 0) {
            System.err.println("blue " + blue);
            blue = 0;
          }
          // System.err.println(red + " " + green + " " + blue);
          Color c = new Color(red, green, blue);
          Rectangle2D.Float r = new Rectangle2D.Float(x * cellSize, y * cellSize, cellSize, cellSize);
          g.setPaint(c);
          g.fill(r);

        }
      }
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  void solve(float timeStep) {
    // Reset oldDensity and oldVelocity
    oldDensity = (float[][]) density.clone();
    oldVelocity = (float[][]) velocity.clone();

    synchronized (velocity) {
  
      for (int x = 0; x < velocity.length; x++) {
        for (int y = 0; y < velocity[x].length; y++) {
          /*
           * Equation for each cell: Velocity = oldVelocity +
           * (sum_Of_Adjacent_Old_Densities - oldDensity_Of_Cell * 4) * timeStep *
           * speed) Density = oldDensity + Velocity Scientists and engineers:
           * Please don't use this to model tsunamis, I'm pretty sure it's not
           * *that* accurate
           */
          velocity[x][y] = friction * oldVelocity[x][y]
              + ((getAdjacentDensitySum(x, y) - density[x][y] * 4) * timeStep * speed);
          density[x][y] = oldDensity[x][y] + velocity[x][y];
        }
      }
    }
  }

  float getAdjacentDensitySum(int x, int y) {
    // If the x or y is at the boundary, use the closest available cell
    float sum = 0;
    if (x - 1 > 0)
      sum += oldDensity[x - 1][y];
    else
      sum += oldDensity[0][y];

    if (x + 1 <= oldDensity.length - 1)
      sum += (oldDensity[x + 1][y]);
    else
      sum += (oldDensity[oldDensity.length - 1][y]);

    if (y - 1 > 0)
      sum += (oldDensity[x][y - 1]);
    else
      sum += (oldDensity[x][0]);

    if (y + 1 <= oldDensity[x].length - 1)
      sum += (oldDensity[x][y + 1]);
    else
      sum += (oldDensity[x][oldDensity[x].length - 1]);

    return sum;
  }

  boolean mouse(MouseEvent e) {
    if (e instanceof MouseWheelEvent) {
      mouseDragged(e);
      return true;
    }
    switch (e.getID()) {
    case MouseEvent.MOUSE_DRAGGED:
      mouseDragged(e);
      return true;
    case MouseEvent.MOUSE_PRESSED:
      pmouseX = Float.NaN;
      pmouseY = Float.NaN;
      mouseClicked(e);
      return true;
    case MouseEvent.MOUSE_RELEASED:
      mouseClicked(e);
      pmouseX = Float.NaN;
      pmouseY = Float.NaN;
      return true;
    default:
      return true;
    }
  }

  float pmouseX = Float.NaN;
  float pmouseY = Float.NaN;

  void mouseDragged(MouseEvent e) {

    float mouseX = e.getX() - x + iwidth / 2;
    float mouseY = e.getY() - y + iheight / 2;
    if (Float.isNaN(pmouseX) || Float.isNaN(pmouseY)) {
      pmouseX = mouseX;
      pmouseY = mouseY;
      return;
    }
    // The ripple size will be determined by mouse speed
    float force = DLUtil.SquareDist(mouseX, mouseY, pmouseX, pmouseY) * 55;

    float dx = DLUtil.Abs(mouseX - pmouseX);
    float dy = DLUtil.Abs(mouseY - pmouseY);
    float sx;
    float sy;
    if (pmouseX < mouseX)
      sx = 1;
    else
      sx = -1;
    if (pmouseY < mouseY)
      sy = 1;
    else
      sy = -1;
    float err = dx - dy;
    float x0 = pmouseX;
    float x1 = mouseX;
    float y0 = pmouseY;
    float y1 = mouseY;
    while ((x0 != x1) || (y0 != y1)) {
      // Make sure the coordinate is within the window
      if (((int) (x0 / cellSize) < density.length) && ((int) (y0 / cellSize) < density[0].length)
          && ((int) (x0 / cellSize) > 0) && ((int) (y0 / cellSize) > 0))
        velocity[(int) (x0 / cellSize)][(int) (y0 / cellSize)] += force;
      float e2 = 2 * err;
      if (e2 > -dy) {
        err -= dy;
        x0 = x0 + sx;
      }
      if (e2 < dx) {
        err = err + dx;
        y0 = y0 + sy;
      }
    }
    pmouseX = mouseX;
    pmouseY = mouseY;
  }

  // If the user clicks instead of drags the mouse, we create a ripple at one
  // spot.
  void mouseClicked(MouseEvent e) {
    float mouseX = e.getX() - x + iwidth / 2;
    float mouseY = e.getY() - y + iheight / 2;
    float force = 250000;
    if (((int) (mouseX / cellSize) < density.length) && ((int) (mouseY / cellSize) < density[0].length)
        && ((int) (mouseX / cellSize) > 0) && ((int) (mouseY / cellSize) > 0)) {
      velocity[(int) (mouseX / cellSize)][(int) (mouseY / cellSize)] += force;
    }
  }

  public static void main(String[] a) {
    int w = 600;
    int h = 400;
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
    DLMain.Main(DLCircusFluid.class, params);
  }
}
