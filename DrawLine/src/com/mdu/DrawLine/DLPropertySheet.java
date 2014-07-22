package com.mdu.DrawLine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;

import javax.swing.JFrame;

import com.l2fprod.common.model.DefaultBeanInfoResolver;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

public class DLPropertySheet {
  DLComponent comp;
  JFrame frame;

  public DLPropertySheet(DLComponent c) {
    comp = c;
    frame = new JFrame("PropertySheet");
    frame.getContentPane().setLayout(new BorderLayout());
    // frame.getContentPane().add("Center", new PropertySheetMain());
    //    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    page();
    frame.pack();
//    frame.setLocation(100, 100); Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    // Determine the new location of the window
    int w = frame.getSize().width;
    int h = frame.getSize().height;
    int x = (dim.width-w)/2;
    int y = (dim.height-h)/2;

    // Move the window
    frame.setLocation(x, y);
    frame.setVisible(true);
  }

  private void page() {

    DefaultBeanInfoResolver resolver = new DefaultBeanInfoResolver();
    BeanInfo beanInfo = resolver.getBeanInfo(comp);

    PropertySheetPanel sheet = new PropertySheetPanel();
    sheet.setMode(PropertySheet.VIEW_AS_CATEGORIES);
    PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();
    if (pd != null)
      sheet.setProperties(pd);
    sheet.readFromObject(comp);
    sheet.setDescriptionVisible(true);
    sheet.setSortingCategories(true);
    sheet.setSortingProperties(true);
    frame.getContentPane().add(sheet, BorderLayout.CENTER);

    // everytime a property change, update the button with it
    PropertyChangeListener listener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        Property prop = (Property) evt.getSource();
        prop.writeToObject(comp);
//        System.out.println("Updated object to " + comp);
      }
    };
    sheet.addPropertySheetChangeListener(listener);
  }
}
