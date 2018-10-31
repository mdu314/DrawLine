package com.mdu.DrawLine;

import java.awt.Font;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComboBox;
import javax.swing.JLabel;

interface DLRunnable extends Runnable {
  DLThread getThread();

  void setThread(DLThread t);
}

class DLThread extends Thread {
  boolean stopped = false;

  DLThread() {
    super();
  }

  DLThread(DLRunnable b) {
    super(b);
    b.setThread(this);
  }

  boolean isStopped() {
    return stopped;
  }

  void setStopped(boolean s) {
    stopped = s;
  }

  static ThreadGroup rootThreadGroup;

  static ThreadGroup getRootThreadGroup() {
    if (rootThreadGroup != null)
      return rootThreadGroup;
    ThreadGroup tg = Thread.currentThread().getThreadGroup();
    ThreadGroup ptg;
    while ((ptg = tg.getParent()) != null)
      tg = ptg;
    return tg;
  }

  static Thread[] getAllThreads() {
    final ThreadGroup root = getRootThreadGroup();
    final ThreadMXBean thbean = ManagementFactory.getThreadMXBean();
    int nAlloc = thbean.getThreadCount();
    int n = 0;
    Thread[] threads;
    do {
      nAlloc *= 2;
      threads = new Thread[nAlloc];
      n = root.enumerate(threads, true);
    } while (n == nAlloc);
    return java.util.Arrays.copyOf(threads, n);
  }

  static void DLThreads(final JLabel text) {
    final StringBuilder sb = new StringBuilder();
    final Timer timer;

    final Font f = new Font(Font.SERIF, Font.PLAIN, 8);
    text.setFont(f);
    timer = new Timer();
    final TimerTask task = new TimerTask() {

      public void run() {
        Thread[] ts = getAllThreads();
        sb.setLength(0);
        sb.append("<html>");
        for (Thread t : ts)
          sb.append(t.getName() + "<br>");
        sb.append("</html>");
        text.setText(sb.toString());
      }
    };
    timer.schedule(task, new Date(), 5000);
  }

  static void DLThreads(final JComboBox box) {
    final Font f = new Font(Font.SERIF, Font.PLAIN, 8);
    box.setFont(f);
    Thread[] ts = getAllThreads();
    box.removeAllItems();
    for (Thread l : ts)
      box.addItem(l);

    Timer timer = new Timer();
    final TimerTask task = new TimerTask() {
      public void run() {
        Thread[] ts = getAllThreads();
        box.removeAllItems();
        for (Thread l : ts)
          box.addItem(l);
      }
    };
    timer.schedule(task, new Date(), 5000);
  }

}
