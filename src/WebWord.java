import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebWord {

  private static void print(String msg, Object... args) {
    System.out.println(String.format(msg, args));
  }

  private static String trim(String s, int width) {
      if (s.length() > width)
          return s.substring(0, width-1) + ".";
      else
          return s;
  }

  Elements links(String url) throws IOException {

    Document doc = Jsoup.connect(url).get();
    Elements links = doc.select("a[href]");
    Elements media = doc.select("[src]");
    Elements imports = doc.select("link[href]");
    Elements ret = new Elements();

    print("\nMedia: (%d)", media.size());
    for (Element src : media) {
      if (src.tagName().equals("img"))
        print(" * %s: <%s> %sx%s (%s)", src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
            trim(src.attr("alt"), 20));
      else
        print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
    }

    print("\nImports: (%d)", imports.size());
    for (Element link : imports) {
      print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
    }

    print("\nLinks: (%d)", links.size());
    for (Element link : links) {
      ret.add(link);
      print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
    }
    return ret;
  }
  
  String words(String url) {
    try {
      Connection soup = Jsoup.connect(url);
      Document doc = soup.get();
      String title = doc.title();
      System.out.println("title : " + title);
      String s = doc.body().text();
      return s;
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

  public static void main(String[] a) throws IOException {
  //String url = "http://www.ibm.fr/";
  //String url = "http://www.lemonde.fr/";
  //String url = "https://www.google.fr/#q=Lucy";
  String url = "https://www.monde-diplomatique.fr";
  
    WebWord ww = new WebWord();
    String res = ww.words(url);
    System.err.println(res);
    List<String> list = ToArrayList(res);
    for (String l : list) {
      System.err.println(l);
    }
    ww.links(url);
  }
  
}