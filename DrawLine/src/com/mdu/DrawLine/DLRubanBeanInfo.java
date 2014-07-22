package com.mdu.DrawLine;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;

public class DLRubanBeanInfo extends DLComponentBeanInfo {

  public PropertyDescriptor[] getPropertyDescriptors() {
    PropertyDescriptor[] pa = super.getPropertyDescriptors();
    try {
      PropertyDescriptor[] pd = { 
          new PropertyDescriptor("brushAngle", DLRuban.class),
          new PropertyDescriptor("brushSize", DLRuban.class), 
          new PropertyDescriptor("color", DLRuban.class),
          new PropertyDescriptor("maxSpeed", DLRuban.class)};

      PropertyDescriptor[] result = Arrays.copyOf(pa, pa.length + pd.length);
      System.arraycopy(pd, 0, result, pa.length, pd.length);

      return result;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }
    return null;
  }
}
