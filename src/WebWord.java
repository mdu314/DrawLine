import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebWord {
  String url = "http://www.ibm.fr/";

  //  String url = "http://www.lemonde.fr/";
  //String url = "https://www.google.fr/#q=Lucy";

  String words() {
    try {
      Connection soup = Jsoup.connect(url);
      Document doc = soup.get();
      String title = doc.title();
      System.out.println("title : " + title);
      String s = doc.body().text();
      return s;
      // Elements links = doc.select("a[href]");
      // if(false)
      //   for (Element link : links) {
      // get the value from href attribute
      //        System.out.println("\nlink : " + link.attr("href"));
      //        System.out.println("text : " + link.text());
      //      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  static String[] ToArray(String s) {
    return s.split("\\s|!\\w");
  }

  static ArrayList<String> ToArrayList(String s) {
    String[] a = ToArray(s);
    ArrayList<String> list = new ArrayList<String>(Arrays.asList(a));
//    Collections.sort(list, new Comparator<String>() {
//      public int compare(String o1, String o2) {
//        return ((String) o1).length() - ((String) o2).length();
//      }
//    });
    ArrayList<String> toRemove = new ArrayList<String>();
    for (String l : list) {
      if (l.length() < 3)
        toRemove.add(l);
    }
    for(String r:toRemove) {
      list.remove(r);
    }
    return list;
  }

  public static void main(String[] a) {
    String res = new WebWord().words();
    System.err.println(res);
    List<String> list = ToArrayList(res);
    for (String l : list) {
      System.err.println(l);
    }
  }
}