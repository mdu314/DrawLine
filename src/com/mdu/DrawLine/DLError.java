package com.mdu.DrawLine;

import java.awt.Graphics2D;

public class DLError {
  Graphics2D graphics;
  
  public static void report(Exception e) {
    e.printStackTrace();
  }
  
  public static void report(Exception e, String message) {
    System.err.println(message);
    e.printStackTrace();
  }
  
}
