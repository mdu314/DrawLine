package com.mdu.DrawLine;

import static com.mdu.DrawLine.DLUtil.RangeRandom;
import static java.awt.Font.BOLD;
import static java.awt.Font.DIALOG;
import static java.awt.Font.DIALOG_INPUT;
import static java.awt.Font.ITALIC;
import static java.awt.Font.MONOSPACED;
import static java.awt.Font.PLAIN;
import static java.awt.Font.SANS_SERIF;
import static java.awt.Font.SERIF;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

class DLChar extends DLCurve {
  String family = SERIF; // DIALOG, DIALOG_INPUT SANS_SERIF SERIF MONOSPACED
  int size = 20;
  int style = PLAIN; // PLAIN, BOLD, ITALIC, or BOLD+ITALIC.
  String text = "@";

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    Rectangle r = redisplayStart();
    this.family = family;
    clear();
    redisplay(r);
  }

  public String[] enumFamily() {
    return new String[] { DIALOG, DIALOG_INPUT, MONOSPACED, SANS_SERIF, SERIF };
  }

  public int getSaize() {
    return size;
  }

  public void setSaize(int size) {
    Rectangle r = redisplayStart();
    this.size = size;
    clear();
    redisplay(r);
  }

  public int getStyle() {
    return style;
  }

  public void setStyle(int style) {
    Rectangle r = redisplayStart();
    this.style = style;
    clear();
    redisplay(r);
  }

  public Integer[] enumStyle() {
    return new Integer[] { PLAIN, BOLD, ITALIC, BOLD + ITALIC };
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    Rectangle r = redisplayStart();
    this.text = text;
    clear();
    redisplay(r);
  }

  DLChar(DLChar e) {
    super(e);
  }

  public DLChar(float x, float y) {
    super(x, y);
  }

  public GeneralPath convert(String s) {
    final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);
    final Font f = new Font(family, style, size);
    final FontRenderContext frc = g.getFontMetrics(f).getFontRenderContext();
    final GlyphVector v = f.createGlyphVector(frc, s);
    final Shape shape = v.getOutline();
    if (!(shape instanceof GeneralPath))
      throw new Error("Character outline is not a GeneralPath but a " + shape);
    return (GeneralPath) shape;
  }

  @Override
  public DLChar copy() {
    return new DLChar(this);
  }

  @Override
  float getRandomAngle() {
    return RangeRandom(-(float) Math.PI / 10, (float) Math.PI / 10);
  }

  @Override
  DLPath path() {
    final GeneralPath s = convert(text);
    transform(s);
    return new DLPath(s);
  }

  @Override
  public void randomize() {
    super.randomize();
    final int f = RangeRandom(0, 5);
    final String[] fa = { DIALOG, DIALOG_INPUT, SANS_SERIF, SERIF, MONOSPACED };
    family = fa[f];
    style = RangeRandom(0, 4);
    size = RangeRandom(30, 50);
    text = DLUtil.RandomChar();
  }

}
