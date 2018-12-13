package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.Timer;

public class DLError {
  
  public static void report(Exception e) {
    e.printStackTrace();
  }
  
  public static void report(Exception e, String message) {
    System.err.println(message);
    e.printStackTrace();
  }
  
  public static ImageMessage report(DLImage image, String s) {
    BufferedImage im = image.image;
    int w = im.getWidth();
    int h = im.getHeight();
    int t = im.getType();
    BufferedImage i = new BufferedImage(w, h, t);
    Graphics2D g = i.createGraphics();
    
    g.dispose();
    g.setColor(Color.black);
    g.drawString(s,  10,  50);    
    
    JComponent p = image.parent;
    ImageMessage l = new DLError().new ImageMessage(i); 
    p.add(l);
    float x = image.getX();
    float y = image.getY();
    l.setLocation((int)(x + 0.5f), (int)(y + 0.5f));
    l.setSize(w, h);
    l.setVisible(true);
    
    return l;
  }
  
  public class ImageMessage extends JLabel {
    String message;
    int delay =  1000;
    
    public ImageMessage(BufferedImage i) {
      super(new ImageIcon(i));
    }
   
     protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
    }
     
     public void setVisible(boolean v) {
       super.setVisible(v);
       if(v) {
         Timer t = new Timer(delay, (ActionEvent e) -> {          
          setVisible(false);           
          });
         t.start();
       }
     }
  }
}
