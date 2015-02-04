package com.mdu.DrawLine;

import java.awt.Font;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;

class DLMemory {
  StringBuilder sb = new StringBuilder();
  JLabel text;
  private final Timer timer;

  DLMemory(JLabel t) {
    text = t;
    final Font f = new Font(Font.SERIF, Font.PLAIN, 8);
    t.setFont(f);
    timer = new Timer();
    final TimerTask task = new TimerTask() {

      @Override
      public void run() {
        final Runtime runtime = Runtime.getRuntime();

        final NumberFormat format = NumberFormat.getInstance();

        final long maxMemory = runtime.maxMemory();
        final long allocatedMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();
        sb.setLength(0);
        sb.append("<html>");
        sb.append("free memory: " + format.format(freeMemory / 1024) + "<br>");
        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "<br> ");
        sb.append("max memory: " + format.format(maxMemory / 1024) + "<br>");
        sb.append("total free memory: " + format.format((freeMemory + maxMemory - allocatedMemory) / 1024) + " ");
        sb.append("</html>");
        text.setText(sb.toString());
      }
    };
    timer.schedule(task, new Date(), 2000);
  }

}

/**
 *
 * @author Kyrylo Holodnov
 */
class ObjectSizeCalculator {

  static final class ObjectWrapper {

    private final Object object;

    public ObjectWrapper(Object object) {
      this.object = object;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this)
        return true;
      if (obj == null || obj.getClass() != ObjectWrapper.class)
        return false;
      return object == ((ObjectWrapper) obj).object;
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 47 * hash + System.identityHashCode(object);
      return hash;
    }
  }

  private static final int ALIGNMENT = 8;
  private static final int BOOLEAN_SIZE = 1;
  private static final int BYTE_SIZE = 1;
  private static final int CHAR_SIZE = 2;
  private static final int DOUBLE_SIZE = 8;
  private static final int FLOAT_SIZE = 4;
  private static final int HEADER_SIZE;
  private static final int INT_SIZE = 4;
  private static final int LONG_SIZE = 8;
  private static final int REFERENCE_SIZE;

  private static final int SHORT_SIZE = 2;

  static {
    try {
      if (System.getProperties().get("java.vm.name").toString().contains("64")) {
        // java.vm.name is something like â€œJava HotSpot(TM) 64-Bit Server
        // VMâ€�
        REFERENCE_SIZE = 8;
        HEADER_SIZE = 16;
      } else {
        REFERENCE_SIZE = 4;
        HEADER_SIZE = 8;
      }
    } catch (final Exception ex) {
      ex.printStackTrace();
      throw new AssertionError(ex);
    }
  }

  public static long sizeOf(Object o) throws IllegalAccessException {
    return sizeOf(o, new HashSet<ObjectWrapper>());
  }

  private static long sizeOf(Object o, Set<ObjectWrapper> visited) throws IllegalAccessException {
    if (o == null)
      return 0;
    final ObjectWrapper objectWrapper = new ObjectWrapper(o);
    if (visited.contains(objectWrapper))
      // We have reference graph with cycles.
      return 0;
    visited.add(objectWrapper);
    long size = HEADER_SIZE;
    final Class<?> clazz = o.getClass();
    if (clazz.isArray()) {
      if (clazz == long[].class) {
        final long[] objs = (long[]) o;
        size += objs.length * LONG_SIZE;
      } else if (clazz == int[].class) {
        final int[] objs = (int[]) o;
        size += objs.length * INT_SIZE;
      } else if (clazz == byte[].class) {
        final byte[] objs = (byte[]) o;
        size += objs.length * BYTE_SIZE;
      } else if (clazz == boolean[].class) {
        final boolean[] objs = (boolean[]) o;
        size += objs.length * BOOLEAN_SIZE;
      } else if (clazz == char[].class) {
        final char[] objs = (char[]) o;
        size += objs.length * CHAR_SIZE;
      } else if (clazz == short[].class) {
        final short[] objs = (short[]) o;
        size += objs.length * SHORT_SIZE;
      } else if (clazz == float[].class) {
        final float[] objs = (float[]) o;
        size += objs.length * FLOAT_SIZE;
      } else if (clazz == double[].class) {
        final double[] objs = (double[]) o;
        size += objs.length * DOUBLE_SIZE;
      } else {
        final Object[] objs = (Object[]) o;
        for (final Object obj : objs)
          size += sizeOf(obj, visited) + REFERENCE_SIZE;
      }
      size += INT_SIZE;
    } else {
      final Field[] fields = o.getClass().getDeclaredFields();
      for (final Field field : fields) {
        if (Modifier.isStatic(field.getModifiers()))
          continue;
        field.setAccessible(true);
        final String fieldType = field.getGenericType().toString();
        if (fieldType.equals("long"))
          size += LONG_SIZE;
        else if (fieldType.equals("int"))
          size += INT_SIZE;
        else if (fieldType.equals("byte"))
          size += BYTE_SIZE;
        else if (fieldType.equals("boolean"))
          size += BOOLEAN_SIZE;
        else if (fieldType.equals("char"))
          size += CHAR_SIZE;
        else if (fieldType.equals("short"))
          size += SHORT_SIZE;
        else if (fieldType.equals("float"))
          size += FLOAT_SIZE;
        else if (fieldType.equals("double"))
          size += DOUBLE_SIZE;
        else
          size += sizeOf(field.get(o), visited) + REFERENCE_SIZE;
      }
    }
    if (size % ALIGNMENT != 0)
      size = ALIGNMENT * (size / ALIGNMENT) + ALIGNMENT;
    return size;
  }
}