package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.awt.Font.DIALOG;
import static java.awt.Font.DIALOG_INPUT;
import static java.awt.Font.MONOSPACED;
import static java.awt.Font.PLAIN;
import static java.awt.Font.SANS_SERIF;
import static java.awt.Font.SERIF;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

class DLChar extends DLCurve {
  float scale = 1f;
  int fontSize = 20;
  Font font;
  String text = "A";
  String family = SERIF; // DIALOG, DIALOG_INPUT SANS_SERIF SERIF MONOSPACED
  int style = PLAIN; // PLAIN, BOLD, ITALIC, or BOLD+ITALIC.

  DLChar(DLChar e) {
    super(e);
  }

  public DLChar(int x, int y) {
    super(x, y);
  }

  DLChar(int x, int y, String s) {
    super(x, y);
    text = s;
  }

  public DLChar copy() {
    return new DLChar(this);
  }

  Font getFont() {
    if (font == null)
      font = new Font(Font.SERIF, Font.PLAIN, fontSize);
    return font;
  }

  public GeneralPath convert(Graphics2D g, String s) {
    Font f = getFont();
    FontRenderContext frc = g.getFontMetrics(f).getFontRenderContext();
    GlyphVector v = f.createGlyphVector(frc, s);
    Shape shape = v.getOutline();
    if (!(shape instanceof GeneralPath))
      throw new Error("Character outline is not a GeneralPath but a " + shape);
    return (GeneralPath) shape;
  }

  Path2D path() {
    GeneralPath s = null;
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);
    s = convert(g, text);
    transform(s);
    return s;
  }

  float getRandomAngle() {
    return RangeRandom(-(float) Math.PI / 10, (float) Math.PI / 10);
  }

  public void randomize() {
    super.randomize();
    scale = RangeRandom(4f, 6f);
    int f = RangeRandom(0, 5);
    String[] fa = { DIALOG, DIALOG_INPUT, SANS_SERIF, SERIF, MONOSPACED };
    family = fa[f];
    style = RangeRandom(0, 4);
    fontSize = RangeRandom(10, 50);
    text = DLUtil.RandomChar();
  }

}
