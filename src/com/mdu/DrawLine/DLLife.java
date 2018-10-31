package com.mdu.DrawLine;

import static java.awt.Font.PLAIN;
import static java.awt.Font.SERIF;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;

import com.jhlabs.image.EdgeFilter;

public class DLLife extends DLImage {

  int threadSleep = 100;
  int lwidth = 50;
  int lheight = 50;
  boolean lifeBoard[][] = new boolean[lwidth][lheight];
  boolean newLife[][] = new boolean[lwidth][lheight];
  float initLifeRatio = 0.5f;
  boolean run = true;

  static final String RANDOM = "random";
  static final String STARS = "stars";
  static final String RECTANGLE = "rectangle";
  static final String CIRCLE = "circle";
  static final String POLYGON = "poly";
  static final String HEART = "heart";
  static final String CHAR = "char";
  String liveCellRenderer = RECTANGLE;
  String deadCellRenderer = RECTANGLE;

  static final String TORIC = "toric";
  static final String NORMAL = "normal";
  String mode = TORIC;

  Color liveColor = DLUtil.DarkerColor(Color.orange, 0.5f);
  Color liveStroke = Color.darkGray;
  Color deadColor = Color.darkGray;
  Color deadStroke = DLUtil.DarkerColor(Color.red, 0.5f);

  boolean edges = false;

  static final String INIT_RANDOM = "initRandom";
  static final String INIT_BLINKER = "initBlinker";

  String initPattern = INIT_RANDOM;

  public Color getLiveColor() {
    return liveColor;
  }

  public void setLiveColor(Color c) {
    this.liveColor = c;
  }

  public Color getLiveStroke() {
    return liveStroke;
  }

  public void setLiveStroke(Color s) {
    liveStroke = s;
  }

  public Color getDeadColor() {
    return deadColor;
  }

  public void setDeadColor(Color c) {
    this.deadColor = c;
  }

  public Color getDeadStroke() {
    return deadStroke;
  }

  public void setDeadStroke(Color s) {
    deadStroke = s;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String[] enumMode() {
    return new String[] {
        TORIC, NORMAL
    };
  }

  public String getLiveCellRenderer() {
    return liveCellRenderer;
  }

  public void setLiveCellRenderer(String mode) {
    this.liveCellRenderer = mode;
  }

  public String[] enumLiveCellRenderer() {
    return new String[] {
        RANDOM, STARS, RECTANGLE, CIRCLE, POLYGON, HEART, CHAR
    };
  }

  public String getDeadCellRenderer() {
    return deadCellRenderer;
  }

  public void setDeadCellRenderer(String mode) {
    this.deadCellRenderer = mode;
  }

  public String[] enumDeadCellRenderer() {
    return new String[] {
        RANDOM, STARS, RECTANGLE, CIRCLE, POLYGON, HEART, CHAR
    };
  }

  public DLLife() {
    super();
    initRandom();
  }

  DLLife(DLLife src) {
    this();
  }

  public DLLife(float x, float y) {
    super(x, y);
    initRandom();
  }

  DLLife copy() {
    return new DLLife(this);
  }

  public float getInitLifeRatio() {
    return initLifeRatio;
  }

  public void setInitLifeRatio(float initLifeRatio) {
    if (this.initLifeRatio == initLifeRatio)
      return;
    this.initLifeRatio = initLifeRatio;
    initRandom();
  }

  public float[] rangeInitLifeRatio() {
    return new float[] {
        0, 1
    };
  }

  public int getLwidth() {
    return lwidth;
  }

  public void setLwidth(int lwidth) {
    if (this.lwidth == lwidth)
      return;
    this.lwidth = lwidth;
    initRandom();
  }

  public int[] rangeLwidth() {
    return new int[] {
        1, 200
    };
  }

  public int getLheight() {
    return lheight;
  }

  public void setLheight(int lheight) {
    if (this.lheight == lheight)
      return;
    this.lheight = lheight;
    initRandom();
  }

  public int[] rangeLheight() {
    return new int[] {
        1, 200
    };
  }

  void edges() {
    EdgeFilter ef = new EdgeFilter();
    image = ef.filter(image, image);
  }

  void step() {
    step(image.createGraphics(), 0);
  }

  void step(Graphics2D g, long dt) {

    synchronized (lifeBoard) {
      s();

      clearImage();

      paint(g, dt);
      if (edges)
        edges();
      if (parent != null)
        parent.paint(this);
    }
  }

  public void f(Graphics2D g, DLThread t) {
    long start = System.currentTimeMillis();
    long dt = 0;
    while (true) {
      if (t != null && t.isStopped())
        break;
      if (run) {
        start = System.currentTimeMillis();
        step(g, dt);
        dt = System.currentTimeMillis() - start;
      }
      if (t != null && t.isStopped())
        break;
      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          System.err.println(e);
        }
      }
      if (t != null && t.isStopped())
        break;
    }
  }

  @Override
  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  void step(Graphics2D g) {
    s();
  }

  int neighbours(int i, int j) {
    switch (mode) {
    case TORIC:
      return neighboursToric(i, j);
    case NORMAL:
      return neighboursNormal(i, j);
    }
    throw new IllegalArgumentException();
  }

  int neighboursToric(int i, int j) {
    // toric mode

    int mi = i - 1;
    if (mi < 0)
      mi = lwidth - 1;

    int pi = i + 1;
    if (pi >= lwidth)
      pi = 0;

    int mj = j - 1;
    if (mj < 0)
      mj = lheight - 1;

    int pj = j + 1;
    if (pj >= lheight)
      pj = 0;

    int n = 0;
    boolean[][] t = lifeBoard;

    if (t[mi][j])
      n++;
    if (t[mi][mj])
      n++;
    if (t[mi][pj])
      n++;

    if (t[i][mj])
      n++;
    if (t[i][pj])
      n++;

    if (t[pi][j])
      n++;
    if (t[pi][mj])
      n++;
    if (t[pi][pj])
      n++;

    return n;
  }

  int neighboursNormal(int i, int j) {

    int mi = i - 1;
    int pi = i + 1;
    int mj = j - 1;
    int pj = j + 1;

    int n = 0;
    boolean[][] t = lifeBoard;

    if ((mi >= 0) && t[mi][j])
      n++;
    if ((mi >= 0) && (mj >= 0) && t[mi][mj])
      n++;
    if ((mi >= 0) && (pj < lheight) && t[mi][pj])
      n++;

    if ((mj >= 0) && t[i][mj])
      n++;
    if ((pj < lheight) && t[i][pj])
      n++;

    if ((pi < lwidth) && t[pi][j])
      n++;
    if ((pi < lwidth) && (mj >= 0) && t[pi][mj])
      n++;
    if ((pi < lwidth) && (pj < lheight) && t[pi][pj])
      n++;

    return n;
  }

  void fillRandom() {
    for (int i = 0; i < lwidth; i++) {
      for (int j = 0; j < lheight; j++) {
        lifeBoard[i][j] = DLUtil.BooleanRandom(initLifeRatio);
      }
    }
  }

  public void setInitPattern(String ip) {
    initPattern = ip;
    init();
  }

  public String getInitPattern() {
    return initPattern;
  }

  public String[] enumInitPattern() {
    return new String[] {
        INIT_RANDOM, INIT_BLINKER
    };
  }

  void init() {
    switch (initPattern) {
    case INIT_RANDOM:
      initRandom();
      break;
    case INIT_BLINKER:
      initBlinker();
      break;
    }
  }

  void initBlinker() {
    clear();

    int i = lwidth / 2;
    int j = lheight / 2;

    lifeBoard[i][j] = true;
    lifeBoard[i - 1][j] = true;
    lifeBoard[i + 1][j] = true;
  }

  void initRandom() {
    fillRandom();
  }

  void rule(int i, int j) {
    int n = neighbours(i, j);
    boolean v = lifeBoard[i][j];
    newLife[i][j] = false;
    if (!v && n == 3)
      newLife[i][j] = true;
    if (v && (n == 2 || n == 3))
      newLife[i][j] = true;
  }

  void clear() {
    for (int i = 0; i < lifeBoard.length; i++) {
      boolean[] b = lifeBoard[i];
      for (int j = 0; j < b.length; j++)
        b[j] = false;
    }
  }

  boolean s() {
    try {
      synchronized (lifeBoard) {
        for (int i = 0; i < lwidth; i++) {
          for (int j = 0; j < lheight; j++) {
            rule(i, j);
          }
        }
        for (int i = 0; i < lwidth; i++)
          System.arraycopy(newLife[i], 0, lifeBoard[i], 0, newLife[i].length);
      }
    } catch (Exception e) {
      System.err.println(e);
    }
    return false;
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(300, 500);
    iheight = iwidth;
  }

  /* painter */

  void paint(Graphics2D g, long dt) {
    DLUtil.SetHints(g);
    try {
      synchronized (lifeBoard) {
        float w = (float) iwidth / lwidth;
        float h = (float) iheight / lheight;
        for (int i = 0; i < lwidth; i++) {
          for (int j = 0; j < lheight; j++) {
            float x = i * w;
            float y = j * h;
            Shape r = null;
            if (lifeBoard[i][j]) {
              if (liveColor != null || liveStroke != null)
                r = createCellRenderer(liveCellRenderer, i, j, x, y, w, h);
              if (liveColor != null) {
                g.setColor(liveColor);
                g.fill(r);
              }
              if (liveStroke != null) {
                g.setColor(liveStroke);
                g.draw(r);
              }
            } else {
              if (deadColor != null || deadStroke != null)
                r = createCellRenderer(deadCellRenderer, i, j, x, y, w, h);
              if (deadColor != null) {
                g.setColor(deadColor);
                g.fill(r);
              }
              if (deadStroke != null) {
                g.setColor(deadStroke);
                g.draw(r);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      System.err.println(e);
    }
    g.drawRect(0, 0, iwidth - 1, iheight - 1);
  }

  Shape createCellRenderer(String r, int i, int j, float x, float y, float w, float h) {
    Shape s = null;
    float rd = (w + h) / 2;

    switch (r) {
    case RANDOM:
      int rnd = DLUtil.RangeRandom(0, 6);
      switch (rnd) {
      case 0:
        s = DLUtil.Square(x + w / 2f, y + w / 2f, rd);
        break;
      case 1:
        s = DLUtil.Rectangle(x + w / 2f, y + w / 2f, w, h);
        break;
      case 2:
        s = DLUtil.Star(x + w / 2f, y + h / 2f, rd / 5f, rd / 2f, 7, DLUtil.RandomAngle());
        break;
      case 3:
        s = DLUtil.Polygon(x + w / 2f, y + h / 2f, 7, rd / 2, 0);
        break;
      case 4:
        s = DLUtil.Circle(x + w / 2f, y + h / 2f, rd);
        break;
      case 5:
        s = DLUtil.Heart(x, y, w, h, false);
        break;
      case 6:
        String c;
        int n = neighbours(i, j);
        c = new Integer(n).toString();
        s = DLUtil.stringToShape(c, SERIF, 20, PLAIN);
      default:
        s = null;
        break;
      }
      break;
    case STARS:
      s = DLUtil.Star(x + w / 2f, y + h / 2f, rd / 5f, rd / 2, 7, DLUtil.RandomAngle());
      break;
    case RECTANGLE:
      s = DLUtil.Rectangle(x + w / 2f, y + w / 2f, w, h);
      break;
    case POLYGON:
      s = DLUtil.Polygon(x + w / 2f, y + h / 2f, 7, rd / 2, 0);
      break;
    case CIRCLE:
      s = DLUtil.Circle(x + w / 2f, y + h / 2f, rd);
      break;
    case HEART:
      s = DLUtil.Heart(x, y, w, h, false);
      break;
    case CHAR:
      String c;
      int n = neighbours(i, j);
      c = new Integer(n).toString();
      s = DLUtil.stringToShape(c, SERIF, 20, PLAIN);
      float m = 0.5f;
      s = DLUtil.fitShapeIn(s, x + m, y + m, w - 2 * m, h - 2 * m, true);
      break;
    }
    return s;
  }

  Shape createCellRenderer(int i, int j, float x, float y, float w, float h) {
    Shape s;
    if (lifeBoard[i][j])
      s = createCellRenderer(liveCellRenderer, i, j, x, y, w, h);
    else
      s = createCellRenderer(deadCellRenderer, i, j, x, y, w, h);
    return s;
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] {
        0, 1000
    };
  }

  public void setEdges(boolean b) {
    this.edges = b;
  }

  public boolean getEdges() {
    return edges;
  }

  public void setRun(boolean r) {
    run = r;
  }

  public boolean getRun() {
    return run;
  }

  Push push = new Push("Push for one step");

  public Push getStep() {
    return push;
  }

  public void setStep(Push p) {
    if (getRun())
      return;
    step((Graphics2D) image.getGraphics(), 0);
  }

  boolean mouse(MouseEvent e) {

    float x = e.getX() - (this.x - iwidth / 2f);
    float y = e.getY() - (this.y - iheight / 2f);

    float w = (float) iwidth / lwidth;
    float h = (float) iheight / lheight;

    int i = DLUtil.Floor(x / w);
    int j = DLUtil.Floor(y / h);

    switch (e.getID()) {
    case MouseEvent.MOUSE_DRAGGED:
      lifeBoard[i][j] = true;
      return true;
    case MouseEvent.MOUSE_PRESSED:
      lifeBoard[i][j] = true;
      return true;
    case MouseEvent.MOUSE_RELEASED:
      lifeBoard[i][j] = true;
      return true;
    }
    return false;
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
    DLMain.Main(DLLife.class, params);
  }
}
