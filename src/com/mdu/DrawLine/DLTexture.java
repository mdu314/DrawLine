package com.mdu.DrawLine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jhlabs.image.ContourFilter;
import com.jhlabs.image.PlasmaFilter;
import com.jhlabs.image.RaysFilter;
import com.jhlabs.image.ScratchFilter;

class DLTexture extends DLImage {
  String imageResource;

  DLTexture(DLTexture r) {
    super(r);
  }

  public DLTexture(float x, float y) {
    super(x, y);
  }

  public DLTexture(float x, float y, int w, int h) {
    super(x, y, w, h);
  }

  @Override
  DLTexture copy() {
    return new DLTexture(this);
  }

  @Override
  void dump() {
  }

  public void f(Graphics2D g, DLThread t) {
  }

  @Override
  BufferedImage image() {
    BufferedImage img = null;
    if (imageResource != null) {
      try {
        img = ImageIO.read(getClass().getResource(imageResource));
        double iw = img.getWidth();
        double ih = img.getHeight();
        double r = Math.min(iwidth / iw, iheight / ih);
        int siw = (int) (r * iw + 0.5);
        int sih = (int) (r * ih + 0.5);
        img = DLUtil.GetScaledInstance(img, siw, sih);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
    }
    // CellularFilter cf = new CellularFilter();
    // img = cf.filter(img, img);

    // TextureFilter tf = new TextureFilter();
    // tf.setAmount(1.2f);
    // img = tf.filter(img, img);

    // WoodFilter wf = new WoodFilter();
    // img = wf.filter(img, img);

    final PlasmaFilter pf = new PlasmaFilter();
    pf.setSeed(DLUtil.RangeRandom(0, Integer.MAX_VALUE));
    img = pf.filter(img, img);

    // CausticsFilter cf = new CausticsFilter();
    // img = cf.filter(img, img);

    // ChromeFilter crf = new ChromeFilter();
    // img = crf.filter(img, img);

    // NoiseFilter nf = new NoiseFilter();
    // nf.setDistribution(NoiseFilter.GAUSSIAN);
    // img = nf.filter(img, img);

    // WaterFilter wf = new WaterFilter();
    // img = wf.filter(img, img);

    // SkyFilter sf = new SkyFilter();
    // img = sf.filter(img, img);

    // KaleidoscopeFilter kf = new KaleidoscopeFilter();
    // kf.setSides(5);
    // img = kf.filter(img, img);

    // StampFilter sf = new StampFilter();
    // img = sf.filter(img, img);
    final ScratchFilter sf = new ScratchFilter();
    sf.setSeed((int) System.currentTimeMillis());
    img = sf.filter(img, img);

    final ContourFilter cf = new ContourFilter();
    final Color color = DLUtil.RandomColor(0, 1, 0.4f, 0.8f, 0, 0.3f);
    cf.setContourColor(color.getRGB());

    img = cf.filter(img, img);

    // RippleFilter rf = new RippleFilter();
    // rf.setYAmplitude(5);
    // rf.setXAmplitude(5);
    // rf.setWaveType(RippleFilter.SINE);
    // img = rf.filter(img, img);

    // BrushedMetalFilter bmf = new BrushedMetalFilter();
    // img = bmf.filter(img, img);

    // BumpFilter bf = new BumpFilter();
    // img = bf.filter(img, img);

    RaysFilter rf = new RaysFilter();
    img = rf.filter(img, img);

    return img;
  }

  @Override
  public void randomize() {
    final int i = DLUtil.RangeRandom(100, 200);
    iwidth = i;
    iheight = i;
    setShadow(true);
  }

}
