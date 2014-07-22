package com.mdu.DrawLine;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class DLEggBeanInfo extends DLCurveBeanInfo {
  public PropertyDescriptor[] getPropertyDescriptors() {
    PropertyDescriptor[] sup = super.getPropertyDescriptors();

    PropertyDescriptor[] result = new PropertyDescriptor[sup.length + 3];
    int i;
    for (i = 0; i < sup.length; i++)
      result[i] = sup[i];

    try {
      PropertyDescriptor pd = new PropertyDescriptor("a", DLEgg.class);
      result[i++] = pd;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }

    try {
      PropertyDescriptor pd = new PropertyDescriptor("b", DLEgg.class);
      result[i++] = pd;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }

    try {
      PropertyDescriptor pd = new PropertyDescriptor("d", DLEgg.class);
      result[i++] = pd;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }
    
    return result;
  }
}
