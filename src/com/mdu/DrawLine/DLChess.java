package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

public class DLChess extends DLImage {
  int threadSleep = 1000;
  int frameCount = 1;
  float imargin = 50f;
  float margx = 10; // percent of a square side
  float margy = 10; // percent of a square side
  Board board = new Board();
  Paint black = Color.gray;
  Paint white = Color.lightGray;
  /* Apple Symbols, Arial Unicode MS, Menlo, Monospaced */
  Font font = new Font("Arial Unicode MS", Font.PLAIN, 20);
  static final int WHITE_SQUARE = 0;
  static final int BLACK_SQUARE = 1;
  boolean debug = false;
  String position = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
  // String position =
  // "pppppppp/pppppppp/pppppppp/pppppppp/pppppppp/pppppppp/pppppppp/pppppppp";
  boolean normalFont = false;

  public DLChess() {
    super();
    setPosition(position);
    if (!normalFont)
      try {
        URL u = DrawLine.class.getResource("fonts/case.ttf");
        File f = new File(u.toURI());
        font = Font.createFont(Font.TRUETYPE_FONT, f);

      } catch (Exception e) {
        DLError.report(e);
      }
  }

  DLChess(DLChess src) {
    this();
  }

  public DLChess(float x, float y) {
    super(x, y);
  }

  DLChess copy() {
    return new DLChess(this);
  }

  public float getMargin() {
    return imargin;
  }

  public float[] rangeMargin() {
    return new float[] {
        0, 200
    };
  }

  public void setMargin(float m) {
    clearImage();
    imargin = m;
  }

  public float getMargy() {
    return margy;
  }

  public float[] rangeMargy() {
    return new float[] {
        0, 100
    };
  }

  public void setMargy(float m) {
    clearImage();
    margy = m;
  }

  public float getMargx() {
    return margx;
  }

  public float[] rangeMargx() {
    return new float[] {
        0, 100
    };
  }

  public void setMargx(float m) {
    clearImage();
    margx = m;
  }

  float iwidth() {
    return iwidth - imargin;
  }

  float iheight() {
    return iheight - imargin;
  }

  public void f(Graphics2D g, DLThread t) {
    while (frameCount++ > 0) {

      if (t != null && t.isStopped())
        break;

      step(g);

      if (parent != null)
        parent.paint(this);

      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          DLError.report(e);
        }
      }
    }
  }

  public void setPosition(String p) {
    position = p;
    try {
      parse(position);
    } catch (Exception e) {
      DLError.report(e);
    }
  }

  public String getPosition() {
    return position;
  }

  public String[] enumPosition() {
    return new String[] {
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/", 
        "8/8/8/8/8/8/8/8",
        "2kr3r/1pp2ppp/1n2b3/p3P3/PnP2B2/1K2P3/1P4PP/R4BNR", 
        "r1bq1rk1/2ppbppp/p1n2n2/1p2p3/4P3/1B3N2/PPPPQPPP/RNB2RK1"
    };
  }

  void board(Graphics2D g) {
    board.paint(g);
  }

  void pieces(Graphics2D g) {
    board.pieces(g);
  }

  void step(Graphics2D g) {
    DLUtil.setIdentity(g);
    DLUtil.SetHints(g);
    board(g);
    pieces(g);
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
    return new int[] {
        0, 100
    };
  }

  public Font getFont() {
    return font;
  }

  public void setFont(Font f) {
    if(f == null)
      return;
    font = f;
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
            "threadSleep", 500
        }, {
            "backgroundColor", null
        }
    };
    DLMain.Main(DLChess.class, params);
  }

  class Piece {
    String piece;

    Piece(String s) {
      piece = s;

    }
    
    String getPiece() {
      return piece;
    }
  }

  class Square {
    Paint paint;
    int type;
    Piece piece;

    void setType(int c) {
      type = c;
    }

    int getType() {
      return type;
    }

    void setPaint(Paint p) {
      paint = p;
    }

    Paint getPaint() {
      return paint;
    }
  }

  class Board {
    Square[][] board = new Square[8][8];
    HashMap<String, String> charMap = new HashMap<String, String>();

    Board() {
      int k = 0;
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          Square s = new Square();
          board[i][j] = s;
          if (k % 2 == 0) {
            s.setType(WHITE_SQUARE);
          } else {
            s.setType(BLACK_SQUARE);
          }
          k++;
        }
        k++;
      }

      if (normalFont) {
        charMap.put("K", "\u2654");
        charMap.put("Q", "\u2655");
        charMap.put("R", "\u2656");
        charMap.put("B", "\u2657");
        charMap.put("N", "\u2658");
        charMap.put("P", "\u2659");
        charMap.put("k", "\u265A");
        charMap.put("q", "\u265B");
        charMap.put("r", "\u265C");
        charMap.put("b", "\u265D");
        charMap.put("n", "\u265E");
        charMap.put("p", "\u265F");
      } else {
        charMap.put("K", "k");
        charMap.put("Q", "q");
        charMap.put("R", "r");
        charMap.put("B", "b");
        charMap.put("N", "n");
        charMap.put("P", "p");
        charMap.put("k", "l");
        charMap.put("q", "w");
        charMap.put("r", "t");
        charMap.put("b", "v");
        charMap.put("n", "m");
        charMap.put("p", "o");
      }
    }

    void paint(Graphics2D g) {

      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          renderSquare(i, j, g);
        }
      }

      g.setPaint(Color.gray);

      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          Shape s = getRect(i, j, 0, 0);
          g.draw(s);
        }
      }
    }

    void pieces(Graphics2D g) {

      float dim = DLUtil.Min(iwidth(), iheight());
      float d = dim / 8f; // dimension d une case
      Font f = getFont().deriveFont(d);

      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
          Piece p = board[i][j].piece;
          if (p == null)
            continue;
          String s = p.getPiece();
          String uc = charMap.get(s);
          if (uc == null)
            throw new IllegalArgumentException("Illegal character " + s);
          Rectangle2D r = getRect(i, j, margx, margy);

          g.setFont(getFont());
          Shape shp = DLUtil.Char(f, uc);
          Rectangle2D sr = shp.getBounds2D();

          AffineTransform otr = g.getTransform();

          AffineTransform tr = DLUtil.computeTransform(sr, r, true);

          g.setTransform(tr);

          g.setColor(Color.black);
          g.fill(shp);

          g.setTransform(otr);

        }
      }
    }
  }

  Rectangle2D getRect(int i, int j, float mx, float my) {
    float dim = DLUtil.Min(iwidth(), iheight());
    float d = dim / 8f;
    mx = d * mx / 100f;
    my = d * my / 100f;
    Rectangle2D shp = new Rectangle2D.Float(x - dim / 2 + j * d + mx, 
                                            y - dim / 2 + i * d + my,
                                            d - 2f * mx, d - 2f * my);
    return shp;
  }

  void renderSquare(int i, int j, Graphics2D g) {
    Square s = board.board[i][j];

    Paint paint = null;
    Shape shp = getRect(i, j, 0, 0);

    switch (s.getType()) {
      case WHITE_SQUARE:
        paint = white;
        break;
      case BLACK_SQUARE:
        paint = black;
        break;
    }
    g.setPaint(paint);
    g.fill(shp);
  }

  // Notation Forsyth-Edwards
  // from 8th row to 1st:
  // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR/
  void parse(String str) {
    Square[][] b = board.board;
    int x = 0;
    int y = 0;

    String[] parts = str.split("/");
    for (int i = 0; i < parts.length; i++) {
      char[] row = parts[i].toCharArray();
      y = i;
      for (int r = 0; r < row.length; r++) {
        char c = row[r];
        if (Character.isDigit(c)) {
          if (c >= '1' && c <= '8') {
            int k = Character.getNumericValue(c);
            for (int j = 0; j < k; j++) {
              b[y][x++].piece = null;
              x %= 8;
            }
          } else {
            throw new IllegalArgumentException("Illegal digit " + c);
          }
          continue;
        } else {
          String sc = new String(new char[] {
              c
          });
          Piece p = new Piece(sc);
          b[y][x].piece = p;
          x++;
          x %= 8;
        }
      }
    }
  }
}
