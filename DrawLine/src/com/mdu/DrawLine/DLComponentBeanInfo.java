package com.mdu.DrawLine;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class DLComponentBeanInfo extends SimpleBeanInfo {
 
  public DLComponentBeanInfo() {

  }

  public PropertyDescriptor[] getPropertyDescriptors() {
    PropertyDescriptor[] result = new PropertyDescriptor[2];
    try {
      PropertyDescriptor xDescriptor = new PropertyDescriptor("x", DLComponent.class);
      result[0] = xDescriptor;
    } catch (IntrospectionException exc) {
    }
    try {
      PropertyDescriptor xDescriptor = new PropertyDescriptor("y", DLComponent.class);
      result[1] = xDescriptor;
    } catch (IntrospectionException exc) {
    }
    return result;
  }
}
