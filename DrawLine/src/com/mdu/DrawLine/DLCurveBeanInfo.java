package com.mdu.DrawLine;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DLCurveBeanInfo extends DLComponentBeanInfo {

  public DLCurveBeanInfo() {

  }

  public PropertyDescriptor[] getPropertyDescriptors() {
    PropertyDescriptor[] sup = super.getPropertyDescriptors();

    PropertyDescriptor[] result = new PropertyDescriptor[sup.length + 3];
    int i;
    for (i = 0; i < sup.length; i++)
      result[i] = sup[i];

    try {
      PropertyDescriptor pd = new PropertyDescriptor("fill", DLCurve.class);
      result[i++] = pd;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }
    try {
      PropertyDescriptor pd = new PropertyDescriptor("stroke", DLCurve.class);
      result[i++] = pd;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }   
    try {
      PropertyDescriptor pd = new PropertyDescriptor("shadow", DLCurve.class);
      result[i++] = pd;
    } catch (IntrospectionException e) {
      e.printStackTrace();
    }
    return result;
  }
}
