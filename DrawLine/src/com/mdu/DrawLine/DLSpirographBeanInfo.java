package com.mdu.DrawLine;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class DLSpirographBeanInfo extends DLCurveBeanInfo {
  public PropertyDescriptor[] getPropertyDescriptors() {
    PropertyDescriptor[] sup = super.getPropertyDescriptors();

    PropertyDescriptor[] result = new PropertyDescriptor[sup.length + 4];
    int i;
    for (i = 0; i < sup.length; i++)
      result[i] = sup[i];

    try {
      PropertyDescriptor pd = new PropertyDescriptor("r1", DLSpirograph.class);
      result[i++] = pd;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }

    try {
      PropertyDescriptor pd = new PropertyDescriptor("r2", DLSpirograph.class);
      result[i++] = pd;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }

    try {
      PropertyDescriptor pd = new PropertyDescriptor("p", DLSpirograph.class);
      result[i++] = pd;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }

    try {
      PropertyDescriptor pd = new PropertyDescriptor("tours", DLSpirograph.class);
      result[i++] = pd;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }
    
    return result;
  }
}
