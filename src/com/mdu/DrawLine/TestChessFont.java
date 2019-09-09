package com.mdu.DrawLine;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


public class TestChessFont {
  Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);  
  String blackPawn = "\u265F";  
  
   void test(Font font) {
     
     BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
     Graphics2D g = img.createGraphics();
     FontRenderContext frc = g.getFontMetrics(font).getFontRenderContext();
     GlyphVector v = font.createGlyphVector(frc, blackPawn);
//     Rectangle2D lb = v.getLogicalBounds();
//     System.err.println("logicalBounds " + lb);
//     Rectangle2D vb = v.getVisualBounds();
//     System.err.println("visualBounds " + vb);
     Shape shape = v.getOutline();
     Rectangle2D r = shape.getBounds2D();
     if(!DLUtil.isNullRect(r)) {
       System.err.println(font);
       System.err.println(blackPawn + " " + r);
       if(shape instanceof GeneralPath) {
         GeneralPath gp = (GeneralPath)shape;
         int wr = gp.getWindingRule();
         System.err.println("wr " + (wr == GeneralPath.WIND_EVEN_ODD ? "WIND_EVEN_ODD" : "WIND_NON_ZERO"));
       }
     }
   }
   
   public static void main(String[] a) {
     String fonts[] = 
         GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
for(String font:fonts)
     new TestChessFont().test(new Font(font, Font.PLAIN, 10));
   }
}
