package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.jhlabs.image.EdgeFilter;

public class DLLife extends DLImage {

  int threadSleep = 50;
  int lwidth = 30;
  int lheight = 30;
  boolean life[][];
  boolean newLife[][];
  float initLifeRatio = 0.8f;

  static final String RULE_ZERO = "ruleZero";
  static final String RULE_ONE = "ruleOne";
  static final String RULE_TWO = "ruleThwo";
  static final String RULE_THREE = "ruleThree";
  String rules = RULE_THREE;

  static final String RANDOM = "random";
  static final String STARS = "stars";
  static final String RECTANGLE = "rectangle";
  static final String CIRCLE = "circle";
  static final String POLYGON = "poly";
  static final String HEART = "heart";
  String cellRenderer = RECTANGLE;

  static final String TORIC = "toric";
  static final String NORMAL = "normal";
  String mode = NORMAL;

  Color cellColor = DLUtil.DarkerColor(Color.orange, 0.5f);
  Color cellStroke = Color.darkGray;

  Color backgroundColor = null;
  boolean edges = false;

  static final String INIT_RANDOM = "initRandom";
  static final String INIT_BLINKER = "initBlinker";

  String initPattern = INIT_RANDOM;

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public Color getCellColor() {
    return cellColor;
  }

  public void setCellColor(Color cellColor) {
    this.cellColor = cellColor;
  }

  public Color getCellStroke() {
    return cellStroke;
  }

  public void setCellStroke(Color s) {
    cellStroke = s;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String[] enumMode() {
    return new String[] { TORIC, NORMAL };
  }

  public String getCellRenderer() {
    return cellRenderer;
  }

  public void setCellRenderer(String mode) {
    this.cellRenderer = mode;
  }

  public String[] enumCellRenderer() {
    return new String[] { RANDOM, STARS, RECTANGLE, CIRCLE, POLYGON, HEART };
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
    return new float[] { 0, 1 };
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
    return new int[] { 1, 200 };
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
    return new int[] { 1, 200 };
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

  void edges() {
    EdgeFilter ef = new EdgeFilter();
    image = ef.filter(image, image);
  }

  public void f(Graphics2D g, DLThread t) {
    long start = System.currentTimeMillis();
    long dt = 0;
    while (1 > 0) {
      start = System.currentTimeMillis();
      if (t != null && t.isStopped())
        break;

      synchronized (life) {
        step();

        clearImage();

        paint(g, dt);
        if (edges)
          edges();
      }
      if (parent != null)
        parent.paint(this);
      dt = System.currentTimeMillis() - start;
      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          System.err.println(e);
        }
      }
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
    step();
  }

  int neighbours1(int i, int j) {
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
    boolean[][] t = life;

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

  int neighbours3(int i, int j) {
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
    boolean[][] t = life;

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

  int neighbours0(int i, int j) {

    int mi = i - 1;
    int pi = i + 1;
    int mj = j - 1;
    int pj = j + 1;

    int n = 0;
    boolean[][] t = life;

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

  void fillRandom() {
    for (int i = 0; i < lwidth; i++) {
      for (int j = 0; j < lheight; j++) {
        life[i][j] = DLUtil.BooleanRandom(initLifeRatio);
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
    return new String[]{INIT_RANDOM, INIT_BLINKER};
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
    life = new boolean[lwidth][lheight];
    newLife = new boolean[lwidth][lheight];
    int i =lwidth / 2;
    int j = lheight / 2;
    
    life[i][j] = true;
    life[i - 1][j] = true;
    life[i + 1][j] = true;
  }

  void initRandom() {
    life = new boolean[lwidth][lheight];
    newLife = new boolean[lwidth][lheight];
    fillRandom();
  }

  void r(int i, int j) {
    switch (rules) {
    case RULE_ZERO:
      r0(i, j);
      break;
    case RULE_ONE:
      r1(i, j);
      break;
    case RULE_TWO:
      r2(i, j);
      break;
    case RULE_THREE:
      r3(i, j);
      break;
    default:
      break;
    }
  }

  void r2(int i, int j) {
    int n = neighbours1(i, j);

    newLife[i][j] = life[i][j];

    if (life[i][j] == false && n == 3) {
      newLife[i][j] = true;
    }

    if (life[i][j] == true && (n == 1 || n > 3)) {
      newLife[i][j] = false;
    }
  }

  void r3(int i, int j) {
    int n = neighbours3(i, j);

    newLife[i][j] = life[i][j];

    if (n == 3)
      newLife[i][j] = true;

    if (n == 2)
      newLife[i][j] = life[i][j];

    if (n < 2 || n > 3) {
      newLife[i][j] = false;
    }
  }

  void r0(int i, int j) {
    int n = neighbours0(i, j);

    newLife[i][j] = life[i][j];

    if (life[i][j]) {
      if (n == 0 || n == 1 || n >= 4)
        newLife[i][j] = false;
      if (n == 2 || n == 3)
        newLife[i][j] = true;
    } else {
      newLife[i][j] = (n == 3);
    }
  }

  void r1(int i, int j) {
    int n = neighbours1(i, j);

    newLife[i][j] = life[i][j];

    if (life[i][j]) {
      if (n == 0 || n == 1 || n >= 4)
        newLife[i][j] = false;
      if (n == 2 || n == 3)
        newLife[i][j] = true;
    } else {
      newLife[i][j] = (n == 3);
    }
  }

  void sToric() {
    try {
      synchronized (life) {
        for (int i = 0; i < lwidth; i++) {
          for (int j = 0; j < lheight; j++) {
            r(i, j);
          }
        }
        System.arraycopy(newLife, 0, life, 0, life.length);
      }
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  void sNormal() {
    try {
      synchronized (life) {
        for (int i = 1; i < lwidth - 1; i++) {
          for (int j = 1; j < lheight - 1; j++) {
            r(i, j);
          }
        }
        System.arraycopy(newLife, 0, life, 0, life.length);
      }
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  boolean step() {
    switch (mode) {
    case TORIC:
      sToric();
      break;
    case NORMAL:
      sNormal();
      break;
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
      synchronized (life) {
        float w = (float) iwidth / lwidth;
        float h = (float) iheight / lheight;
        for (int i = 0; i < lwidth; i++) {
          for (int j = 0; j < lheight; j++) {
            if (life[i][j]) {
              float x = i * w;
              float y = j * h;
              Shape r = createCellRenderer(x, y, w, h);
              if (cellColor != null) {
                g.setColor(cellColor);
                g.fill(r);
              }
              if (cellStroke != null) {
                g.setColor(cellStroke);
                g.draw(r);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  Shape createCellRenderer(float x, float y, float w, float h) {
    float rd = (w + h) / 2;
    Shape s = null;
    switch (cellRenderer) {
    case RANDOM:
      int r = DLUtil.RangeRandom(0, 6);
      switch (r) {
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
    }
    return s;
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

  public void setRules(String r) {
    rules = r;
  }

  public String getRules() {
    return rules;
  }

  public String[] enumRules() {
    return new String[] { RULE_ZERO, RULE_ONE, RULE_TWO, RULE_THREE };
  }

  public void setEdges(boolean b) {
    this.edges = b;
  }

  public boolean getEdges() {
    return edges;
  }
}
