package com.mdu.DrawLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

@SuppressWarnings("serial")
public class DLComponentList extends ArrayList<DLComponent> {
  
  DLComponentList() {
    super();
  }
  
  DLComponentList(DLComponent c) {
    super();
    add(c);
  }
  
  void circulate(DLComponentList selection) {
    DLComponent[] compArray = toArray(new DLComponent[0]);
    int[] selArray = new int[selection.size()];

    Iterator<DLComponent> it = selection.iterator();
    int k = 0;
    while (it.hasNext()) {
      DLComponent o = it.next();
      int index = indexOf(o);
      selArray[k++] = index;
    }

    DLComponent tmp = (DLComponent)compArray[selArray[0]];
    int i;
    for (i = 1; i < selArray.length - 1; i++)
      compArray[selArray[i - 1]] = compArray[selArray[i]];
    compArray[selArray[i]] = tmp;
    clear();
    addAll(Arrays.asList(compArray));
  }

  void raise(DLComponent c) {
    if(remove(c))
      add(size(), c);
  }
  
}
