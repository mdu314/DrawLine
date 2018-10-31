package com.mdu.DrawLine;

import static com.google.zxing.BarcodeFormat.AZTEC;
import static com.google.zxing.BarcodeFormat.CODABAR;
import static com.google.zxing.BarcodeFormat.CODE_128;
import static com.google.zxing.BarcodeFormat.CODE_39;
import static com.google.zxing.BarcodeFormat.CODE_93;
import static com.google.zxing.BarcodeFormat.DATA_MATRIX;
import static com.google.zxing.BarcodeFormat.EAN_13;
import static com.google.zxing.BarcodeFormat.EAN_8;
import static com.google.zxing.BarcodeFormat.ITF;
import static com.google.zxing.BarcodeFormat.MAXICODE;
import static com.google.zxing.BarcodeFormat.PDF_417;
import static com.google.zxing.BarcodeFormat.QR_CODE;
import static com.google.zxing.BarcodeFormat.RSS_14;
import static com.google.zxing.BarcodeFormat.RSS_EXPANDED;
import static com.google.zxing.BarcodeFormat.UPC_A;
import static com.google.zxing.BarcodeFormat.UPC_E;
import static com.google.zxing.BarcodeFormat.UPC_EAN_EXTENSION;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.CodaBarWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.oned.EAN13Reader;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.EAN8Writer;
import com.google.zxing.oned.ITFWriter;
import com.google.zxing.oned.UPCAWriter;
import com.google.zxing.oned.UPCEANWriter;
import com.google.zxing.pdf417.PDF417Writer;
import com.google.zxing.qrcode.QRCodeWriter;

public class DLQRCode extends DLPointImage {
  Color backgroundColor = null;
  int threadSleep = 50;
  String data = DLUtil.RandomWord();
  BarcodeFormat barCodeFormat = QR_CODE;
  String currentData;

  public DLQRCode() {
    super();
  }

  DLQRCode(DLQRCode src) {
    this();
  }

  public DLQRCode(float x, float y) {
    super(x, y);
  }

  DLQRCode copy() {
    return new DLQRCode(this);
  }

  BitMatrix generateMatrix() {
    try {
      Map<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
      hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
      //      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
      String d = data;
      if (d == null || d.isEmpty()) {
        int c = DLUtil.RangeRandom(1, 20);
        d = DLUtil.RandomWord();
        while (c-- > 0) {
          d += " " + DLUtil.RandomWord();
          if (d.length() >= 3577) {
            d = d.substring(0, 3576);
            break;
          }
        }
      }
      currentData = d;
      switch (barCodeFormat) {
      case AZTEC: {
        BitMatrix m = new AztecWriter().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;
      }
      case CODABAR: {
        d = DLUtil.RandomDigits(DLUtil.RangeRandom(1, 80));
        currentData = d;
        BitMatrix m = new CodaBarWriter().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;
      }
      case CODE_39: {
        // (A through Z), numeric digits (0 through 9) and a number of special characters (-, ., $, /, +, %, and space
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-.$/+% ";
        d = "";
        int n = DLUtil.RangeRandom(1,  20);
        while (n-- > 0) {
          int i = DLUtil.RangeRandom(0, letters.length() - 1);
          d += letters.substring(i, i + 1);
        }
        currentData = d;
        hints.put(EncodeHintType.MARGIN, 20);
        BitMatrix m = new Code39Writer().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;
      }
      case CODE_128: {
        hints.put(EncodeHintType.MARGIN, 20);
        BitMatrix m = new Code128Writer().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;
      }
      case DATA_MATRIX: {
        BitMatrix m = new DataMatrixWriter().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;
      }
      case EAN_8: {
        d = DLUtil.RandomDigits(DLUtil.RangeRandom(8, 9));
        currentData = d;
        BitMatrix m = new EAN8Writer().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;
      }
      case EAN_13: {
        BitMatrix m = new EAN13Writer().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;
      }
      case ITF: {
        BitMatrix m = new ITFWriter().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;
      }
      case PDF_417: {
        BitMatrix m = new PDF417Writer().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;
      }
      case QR_CODE: {
        BitMatrix m = new QRCodeWriter().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;
      }
      case UPC_A: {
        BitMatrix m = new UPCAWriter().encode(d, barCodeFormat, iwidth, iheight, hints);
        return m;      
      }
      default:
        break;
      }
    } catch (Exception e) {
      System.err.println(e);
      return null;
    }
    return null;
  }

  void clearImage() {
    if (backgroundColor == null)
      super.clearImage();
    else {
      Graphics2D g = image.createGraphics();
      g.setColor(backgroundColor);
      g.fillRect(0, 0, iwidth, iheight);
    }
  }

  public void f(Graphics2D g, DLThread t) {
    long start = System.currentTimeMillis();
    long dt = 0;
    while (1 > 0) {
      start = System.currentTimeMillis();
      if (t != null && t.isStopped())
        break;

      synchronized (this) {
        clearImage();

        step(g, dt);

        paintFps(g);
      }
      if (parent != null)
        parent.paint(this);
      dt = System.currentTimeMillis() - start;
      if (threadSleep > 0) {
        try {
          Thread.sleep(threadSleep);
        } catch (InterruptedException e) {
          System.err.println(e);
        }
      }
    }
  }

  void paintFps(Graphics2D g) {

    Font f = new Font(Font.MONOSPACED, Font.PLAIN, 10);
    FontMetrics metrics = g.getFontMetrics(f);

    int descent = metrics.getDescent();

    String s = currentData;

    g.setColor(Color.darkGray);
    g.drawString(s, 5, iheight - descent);
  }

  BufferedImage image() {
    final BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    DLUtil.SetHints(g);

    if (threaded)
      runThreaded(g);

    return img;
  }

  void step(Graphics2D g, long dt) {
    BitMatrix m = generateMatrix();
    if (m != null) {
      for (int i = 0; i < m.getWidth(); i++) {
        for (int j = 0; j < m.getHeight(); j++) {
          Color c;
          if (m.get(i, j)) {
            c = Color.black;
          } else {
            c = DLUtil.TransparentColor(Color.white);
          }
          setPointFill(c);
          drawPoint(g, i, j);
        }
      }
    }
  }

  public void randomize() {
    iwidth = DLUtil.RangeRandom(200, 300);
    iheight = DLUtil.RangeRandom(100, 200);
  }

  void paint(Graphics2D g, long dt) {
    DLUtil.SetHints(g);
    super.paint(g);
  }

  public int getThreadSleep() {
    return threadSleep;
  }

  public void setThreadSleep(int threadSleep) {
    this.threadSleep = threadSleep;
  }

  public int[] rangeThreadSleep() {
    return new int[] { 0, 100 };
  }

  public String getData() {
    return data;
  }

  public void setData(String s) {
    data = s;
  }

  static Hashtable<String, BarcodeFormat> format = createMap();

  private static Hashtable<String, BarcodeFormat> createMap() {
    Hashtable<String, BarcodeFormat> map = new Hashtable<String, BarcodeFormat>();
    map.put("AZTEC", AZTEC);
    map.put("CODABAR", CODABAR);
    map.put("CODE_39", CODE_39);
    map.put("CODE_93", CODE_93);
    map.put("CODE_128", CODE_128);
    map.put("DATA_MATRIX", DATA_MATRIX);
    map.put("EAN_8", EAN_8);
    map.put("EAN_13", EAN_13);
    map.put("ITF", ITF);
    map.put("MAXICODE", MAXICODE);
    map.put("PDF_417", PDF_417);
    map.put("QR_CODE", QR_CODE);
    map.put("RSS_14", RSS_14);
    map.put("RSS_EXPANDED", RSS_EXPANDED);
    map.put("UPC_A", UPC_A);
    map.put("UPC_E", UPC_E);
    map.put("UPC_EAN_EXTENSION", UPC_EAN_EXTENSION);
    return map;
  }

  private static String[] formats = { "AZTEC", "CODABAR", "CODE_39", "CODE_93", "CODE_128", "DATA_MATRIX", "EAN_8",
      "EAN_13", "ITF", "MAXICODE", "PDF_417", "QR_CODE", "RSS_14", "RSS_EXPANDED", "UPC_A", "UPC_E",
      "UPC_EAN_EXTENSION" };

  //  private static String[] formats = { "AZTEC", "CODABAR", "CODE_39", "CODE_128", "QR_CODE" };

  public String getBarcodeFormat() {
    for (String s : format.keySet()) {
      if (format.get(s) == barCodeFormat)
        return s;
    }
    return null;
  }

  public void setBarcodeFormat(String s) {
    BarcodeFormat f = format.get(s);
    barCodeFormat = f;
  }

  public String[] enumBarcodeFormat() {
    return formats;
  }

}
