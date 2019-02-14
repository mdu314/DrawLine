package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;

public class DLQuasiCristal extends DLImage {
  float dimPix = 0.5f;                       
  int levels = 7;
  float tFactor = 0.3f;
  int[] pixels;

  String colorModelName = "null";
  DLColorModel colorModel = null;
  
  public void setColorModel(String cm) {
    colorModelName = cm;
    if(cm.equals("null")) {
      colorModel = null;
      return;
    }
    
    for(DLColorModel com : DLUtil.ColorModels) {
      if(com.getName().equals(cm)) {
        colorModelName = cm;
        colorModel = com;
        break;
      }
    }
  }
  
  public String getColorModel() {
    return colorModelName;
  }
  public String[] enumColorModel() {
    return new String[] {
        "null",
        DLUtil.ColorModel1.getName(),
        DLUtil.ColorModel2.getName(),
        DLUtil.ColorModel3.getName(),
        DLUtil.ColorModel4.getName()
        };
  }
  
  public DLQuasiCristal() {
    super();
  }

  DLQuasiCristal(DLQuasiCristal src) {
    this();
  }

  public DLQuasiCristal(float x, float y) {
    super(x, y);
  }

  @Override
  DLQuasiCristal copy() {
    return new DLQuasiCristal(this);
  }

  int fcr = DLUtil.RangeRandom(5, 15);
  int dpr = DLUtil.RangeRandom(10, 20);

  void random(int fc) {
    if (fc % dpr == 0) {
      float d = DLUtil.RangeRandom(-0.1f, 0.1f);
      dimPix += d;
      if (dimPix < 0.1f)
        dimPix = 0.1f;
      if (dimPix > 1f)
        dimPix = 1f;
      dpr = DLUtil.RangeRandom(10, 20);
    }
    if ((fc % fcr) == 0) {
      int l = DLUtil.RangeRandom(-2, 2);
      levels += l;
      if (levels < 1)
        levels = 1;
      if (levels > 10)
        levels = 10;
      fcr = DLUtil.RangeRandom(5, 15);
    }
  }
  
  @Override
  void step(Graphics2D g) {
    draw(g);
  }
  
  @Override
  void setup() {
    pixels = new int[iwidth * iheight];
  }
  
  void draw(Graphics2D g) {
    
    float t = frameCount * tFactor;
    int iw2 = iwidth / 2;
    int ih2 = iheight / 2;
    int ip = 0;

    for (int yp = -ih2; yp < ih2; yp++) {
      float y = yp * dimPix;

      for (int xp = -iw2; xp < iw2; xp++) {
        float x = xp * dimPix;

        float o = 0;
        float s = 0;

        for (int i = 0; i < levels; i++) {
          float sin = DLUtil.sin(o);
          float cos = DLUtil.cos(o);
          s += (DLUtil.cos(cos * x + sin * y + t) + 1f) / 2f;
          o += DLUtil.PI / levels;
        }

        int is = (int) s;
        float ds = s - is;
        s = (is % 2) == 0 ? ds : 1f - ds;

        pixels[ip++] = color(s);
      }
    }
    image.setRGB(0, 0, iwidth, iheight, pixels, 0, iwidth);
  }

  int color(float c) {    
    if(colorModel != null) {
      int color = colorModel.getColor(c);
      return color | 0xff000000;
    }
    c = c * 256;
    int icf = (int) c & 0xff;
    if (backgroundColor != null) {
      int bg = backgroundColor.getRGB();
      int ret = 0xff000000 | ((icf << 16 | icf << 8 | icf) & bg);
      return ret;
    } else {
      return 0xff000000 | icf << 16 | icf << 8 | icf;
    }
  }

  public void reset() {
    super.reset();
    setup();
  }
  
  public void setTFactor(float tf) {
    tFactor = tf;
  }

  public float getTFactor() {
    return tFactor;
  }

  public float[] rangeTFactor() {
    return new float[] {
        0.1f, 1
    };
  }

  public void setLevels(int l) {
    levels = l;
  }

  public int getLevels() {
    return levels;
  }

  public int[] rangeLevels() {
    return new int[] {
        1, 15
    };
  }

  public void setDimPix(float dp) {
    dimPix = dp;
  }

  public float getDimPix() {
    return dimPix;
  }

  public float[] rangeDimPix() {
    return new float[] {
        0.00001f, 0.5f
    };
  }

  public static void main(String[] a) throws ClassNotFoundException {
    String cls = new Throwable().getStackTrace()[0].getClassName();
    Class<?> classe = Class.forName(cls);

    int w = 400;
    int h = 400;
    Object[][] params = {
        { "iwidth", w }, 
        {  "iheight", h }, 
        {  "x", w / 2 }, 
        {  "y", h / 2 }, 
        { "clear", false },
        { "colorModel", DLUtil.ColorModel2},
        { "dimPix", 0.25f},
        { "backgroundColor", new Color(53, 53, 20).brighter().brighter().brighter() }
    };
    DLMain.Main(classe, params);
  }
}
