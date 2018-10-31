package com.mdu.DrawLine;

public class DLError {
  public static void report(Exception e) {
    e.printStackTrace();
  }
  public static void report(Exception e, String message) {
    System.err.println(message);
    e.printStackTrace();
  }
}
