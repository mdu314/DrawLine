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

  DLComponentList(DLComponentList src) {
    super();
    addAll(src);
  }

  void circulate(DLComponentList selection) {
    final DLComponent[] compArray = toArray(new DLComponent[0]);
    final int[] selArray = new int[selection.size()];

    final Iterator<DLComponent> it = selection.iterator();
    int k = 0;
    while (it.hasNext()) {
      final DLComponent o = it.next();
      final int index = indexOf(o);
      selArray[k++] = index;
    }

    final DLComponent tmp = compArray[selArray[0]];
    int i;
    for (i = 1; i < selArray.length - 1; i++)
      compArray[selArray[i - 1]] = compArray[selArray[i]];
    compArray[selArray[i]] = tmp;
    clear();
    addAll(Arrays.asList(compArray));
  }

  DLComponentList copy() {
    return new DLComponentList(this);
  }

  void raise(DLComponent c) {
    if (remove(c))
      add(size(), c);
  }

  @Override
  public String toString() {
    return "DLComponentList " + size();
  }

}
