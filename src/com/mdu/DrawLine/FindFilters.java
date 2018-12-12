package com.mdu.DrawLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindFilters {
  String path;
  ArrayList<String> paths = new ArrayList<String>(); 
  ArrayList<_Filter> filters = new ArrayList<_Filter>(); 
  
  public FindFilters(String path) {
    this.path = path;
    File fpath = new File(path); 
    
    if(!fpath.exists()) 
      throw new Error("Path " + path + " Does not exists");
    
    if(!fpath.isDirectory())
      throw new Error("Path " + path + " Should be a directory");
    
  }
  
  void _findFilters(File f) {
    if(f.isDirectory()) {
      File[] list = f.listFiles();
      for(File l:list) {
        _findFilters(l);
      }
    } else {
      String name = f.getName();
      if(name.endsWith("Filter.java")) {
        String p;
        try {
          p = f.getCanonicalPath();
          paths.add(p);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }
  
  void findFilters() {
    File f = new File(path);
    System.out.println("Search filters from " + f.getAbsolutePath());
    if(!f.exists()) 
      throw new Error("Path " + path + " Does not exists");
    
    if(!f.isDirectory())
      throw new Error("Path " + path + " Should be a directory");    
    
    _findFilters(f) ;
  }
  
  void instanciate(String path) {
    String pattern = "^.*/src/(.*)/(.*)\\.java$" ;
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(path);
    String clazz = null;
    String paquet = null;
    
    if (m.find()) {
      int c = m.groupCount( );
      if(c >= 2) {
        paquet = m.group(1);
        clazz = m.group(2);
      }
    }    
    paquet = paquet.replaceAll("/",  ".");
    String s = paquet + "." + clazz;
    System.out.print("instantiate " + s);
    try {
      Class cls = Class.forName(s);
      Object o = cls.newInstance();
      // 
      _Filter fil = new _Filter(path, clazz, paquet);
      System.err.println("\nAdd \n" + fil);
      filters.add(fil);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      System.out.println(" KO");
    }
  }

  String paquet(String p) {
    Pattern r = Pattern.compile("^.*/src/(.*)/(.*)\\.java$");
    Matcher m = r.matcher(p);
    if (m.find()) {
      int c = m.groupCount();
      if (c >= 1)
        return m.group(1);
    } else {
      return null;
    }
    return null;
  }

  void instantiateFilters() {
    for(String s:paths) {
      instanciate(s);
    }
  }
  
  void p (String s) {
    System.out.println(s);
  }
  
  void output() {
    for(_Filter f:filters) 
      p("import " + f.paquet + "." + f.clazz);
    p(null);
    
    p("ArrayList<Filter>");
    for(_Filter f:filters) 
      p("import " + f.paquet + "." + f.clazz);
    
    p("public void setFilter(String s) {");
    p("for(Filter f:filters"); 
      p("if(f.toString().equals(s)) {");
  }
  
  void printFilters() {
    for(String s:paths) {
      System.out.println(s);
    }
  }
  
  public static void main(String[] a) {
    FindFilters f = new FindFilters(".");
    f.findFilters();
    f.printFilters();
    f.instantiateFilters();
    f.output();
  }
  
  class _Filter {
    String path;
    String clazz;
    String paquet;
    
    _Filter(String p, String c, String pq) {
      path = p;
      clazz = c;
      paquet = pq;
    }
    
    public String toString() {
      return "Path " + path + "\nClasse " + clazz + "\nPackage " + paquet;
    }
  }
}
